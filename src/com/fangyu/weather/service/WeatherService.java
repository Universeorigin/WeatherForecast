package com.fangyu.weather.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.fangyu.weather.bean.FutureWeatherBean;
import com.fangyu.weather.bean.HoursWeatherBean;
import com.fangyu.weather.bean.PMBean;
import com.fangyu.weather.bean.WeatherBean;
import com.thinkland.juheapi.common.JsonCallBack;
import com.thinkland.juheapi.data.air.AirData;
import com.thinkland.juheapi.data.weather.WeatherData;

public class WeatherService extends Service{
	
	private LocationManager locationManager;
	private Location location;
	private String provider;
	
	private String city;
	private WeatherServiceBinder binder=new WeatherServiceBinder();
	//这两个参数的作用是判断回调函数的执行
    private boolean isRunning=false;
    private int count=0;
    
    private List<HoursWeatherBean> hourList;
    private HoursWeatherBean hoursWeatherBean;
    private PMBean pmBean;
    private WeatherBean weatherBean;
    
    private OnParseCallBack callBack;
    
    private static final int REFRESH_INFO=0x01;
    
    public interface OnParseCallBack {
		public void OnParseComplete(List<HoursWeatherBean> hourList, PMBean pmBean, WeatherBean weatherBean);
	}
    
    public void setCallBack(OnParseCallBack callback) {
		this.callBack = callback;
	}

	public void removeCallBack() {
		callBack = null;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}

	@SuppressWarnings("static-access")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providerList=locationManager.getProviders(true);
		if (providerList.contains(locationManager.GPS_PROVIDER)) {
			provider=locationManager.GPS_PROVIDER;
		} else if (providerList.contains(locationManager.NETWORK_PROVIDER)) {
			provider=locationManager.NETWORK_PROVIDER;
		}else {
			Toast.makeText(getApplicationContext(), "No location provider to use", Toast.LENGTH_SHORT).show();
			return;
		}
		location=locationManager.getLastKnownLocation(provider);
		
//		city="合肥";
		mHandler.sendEmptyMessage(REFRESH_INFO);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what==REFRESH_INFO) {
				getCityWeather();
				//每半小时刷新一次天气
				sendEmptyMessageDelayed(REFRESH_INFO, 30*60*1000);
			}
		}
	};
	
	public void getCityWeather(String city) {
		this.city = city;
		getCityWeather();
	}
	
	public void getCityWeatherByGEO(){
		
		WeatherData data=WeatherData.getInstance();
		data.getByGEO(location.getLatitude(), location.getLongitude(), 2, new JsonCallBack() {
			
			@Override
			public void jsonLoaded(JSONObject json) {
				Log.e("GPS>>>>>>>>>>>>>>>>>", json.toString());
				try {
					JSONObject resultJsonObject=json.getJSONObject("result");
					JSONObject todayJsonObject=resultJsonObject.getJSONObject("today");
					city=todayJsonObject.getString("city");
					getCityWeather(city);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
	}
	
	public void getCityWeather(){
		
		if (isRunning) {
			return;
		}
		isRunning=true;
		count=0;
		WeatherData data=WeatherData.getInstance();
		
		data.getByCitys(city, 2, new JsonCallBack() {
			
			@Override
			public void jsonLoaded(JSONObject arg0) {
				System.out.println(arg0.toString());
				synchronized (this) {
					count++;
				}
				weatherBean=parseWeather(arg0);
//				if (weatherBean!=null) {
//					setWeatherViews(weatherBean);
//				}
				if (count==3) {
//					mPullToRefreshScrollView.onRefreshComplete();
					if (callBack!=null) {
						callBack.OnParseComplete(hourList, pmBean, weatherBean);
					}
					isRunning=false;
				}
			}
		});
		
		data.getForecast3h(city, new JsonCallBack() {
			
			@Override
			public void jsonLoaded(JSONObject arg0) {
				System.out.println(arg0.toString());
				synchronized (this) {
					count++;
				}
				hourList=parseForecast3h(arg0);
//				if (hourList!=null && hourList.size()==5) {
//					setHourViews(hourList);
//				}
				if (count==3) {
//					mPullToRefreshScrollView.onRefreshComplete();
					if (callBack!=null) {
						callBack.OnParseComplete(hourList, pmBean, weatherBean);
					}
					isRunning=false;
				}
			}

		});
		
		AirData airData=AirData.getInstance();
		airData.cityAir(city, new JsonCallBack() {
			
			@Override
			public void jsonLoaded(JSONObject arg0) {
				System.err.println(arg0.toString());
				synchronized (this) {
					count++;
				}
				pmBean=parsePM(arg0);
//				if (pmBean!=null) {
//					setPMViews(pmBean);
//				}
				if (count==3) {
//					mPullToRefreshScrollView.onRefreshComplete();
					if (callBack!=null) {
						callBack.OnParseComplete(hourList, pmBean, weatherBean);
					}
					isRunning=false;
				}
			}
		});
	}
	
	//解析空气质量PM2.5数据
	private PMBean parsePM(JSONObject json){
		PMBean pmBean=null;
		try {
			int code =json.getInt("resultcode");
			int error_code=json.getInt("error_code");
			if (error_code==0 && code==200) {
				pmBean=new PMBean();
				JSONObject pmJson=json.getJSONArray("result").getJSONObject(0).getJSONObject("citynow");
				pmBean.setAqi(pmJson.getString("AQI"));
				pmBean.setQuality(pmJson.getString("quality"));
			}else {
				Toast.makeText(getApplicationContext(), "PM2.5:"+json.getString("reason"), Toast.LENGTH_SHORT).show();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pmBean;
	}
	
	//解析城市天气数据
	private WeatherBean parseWeather(JSONObject json){
		
		WeatherBean weatherBean=null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		
		try {
			int code =json.getInt("resultcode");
			int error_code=json.getInt("error_code");
			if (error_code==0 && code==200) {
				JSONObject resultJson=json.getJSONObject("result");
				weatherBean=new WeatherBean();
				
				//解析today
				JSONObject todayJson=resultJson.getJSONObject("today");
				weatherBean.setCity(todayJson.getString("city"));
				weatherBean.setUv_index(todayJson.getString("uv_index"));
				weatherBean.setTemp(todayJson.getString("temperature"));
				weatherBean.setWeather_str(todayJson.getString("weather"));
				weatherBean.setWeather_id(todayJson.getJSONObject("weather_id").getString("fa"));
				weatherBean.setDressing_index(todayJson.getString("dressing_index"));
				
				//解析sk
				JSONObject skJson=resultJson.getJSONObject("sk");
				weatherBean.setWind(skJson.getString("wind_direction")+skJson.getString("wind_strength"));
				weatherBean.setNow_temp(skJson.getString("temp"));
				weatherBean.setRelease(skJson.getString("time"));
				weatherBean.setHumidity(skJson.getString("humidity"));
				
				//解析future
				Date currentDate=new Date(System.currentTimeMillis());
				JSONArray futureArray=resultJson.getJSONArray("future");
				List<FutureWeatherBean> futureList=new ArrayList<FutureWeatherBean>();
				for (int i = 0; i < futureArray.length(); i++) {
					JSONObject futureJson=futureArray.getJSONObject(i);
					FutureWeatherBean futureWeatherBean=new FutureWeatherBean();
					Date weatherDate = sdf.parse(futureJson.getString("date"));
					if (!weatherDate.after(currentDate)) {
						//如果所查询天气的时间早于当前时间，则跳出本次循环，进入下一个循环
						continue;
					}
					futureWeatherBean.setTemp(futureJson.getString("temperature"));
					futureWeatherBean.setWeek(futureJson.getString("week"));
					futureWeatherBean.setWeather_id(futureJson.getJSONObject("weather_id").getString("fa"));
					futureList.add(futureWeatherBean);
					if (futureList.size()==3) {
						break;
					}
				}
				weatherBean.setFutureList(futureList);
				
			}else {
//					Utility.isNull=true;
//					SharedPreferences.Editor editor=getSharedPreferences("nullOrNot", MODE_PRIVATE).edit();
//					editor.putBoolean("isNull", Utility.isNull);
//					editor.commit();
					Toast.makeText(getApplicationContext(), "<"+json.getString("reason")+">", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return weatherBean;
	}
	
	//解析未来每隔3小时天气预报数据
	private List<HoursWeatherBean> parseForecast3h(JSONObject json){
		
		List<HoursWeatherBean> hourList=null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
		Date currentDate=new Date(System.currentTimeMillis());
		
		int code;
		try {
			code = json.getInt("resultcode");
			int error_code=json.getInt("error_code");
			if (error_code==0 && code==200) {
				hourList=new ArrayList<HoursWeatherBean>();
				JSONArray resultArray=json.getJSONArray("result");
				for (int i = 0; i < resultArray.length(); i++) {
					JSONObject hourJson=resultArray.getJSONObject(i);
					Date hourDate=sdf.parse(hourJson.getString("sfdate"));
					if (!hourDate.after(currentDate)) {
						continue;
					}
					HoursWeatherBean hoursWeatherBean=new HoursWeatherBean();
					hoursWeatherBean.setWeather_id(hourJson.getString("weatherid"));
					hoursWeatherBean.setTemp(hourJson.getString("temp1"));
					Calendar calendar=Calendar.getInstance();
					calendar.setTime(hourDate);
					hoursWeatherBean.setTime(calendar.get(Calendar.HOUR_OF_DAY)+"");
					hourList.add(hoursWeatherBean);
					if (hourList.size()==5) {
						break;
					}
				}
			}else {
				Toast.makeText(getApplicationContext(), "<<"+json.getString("reason")+">>", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hourList;
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}
	//Activity和Service通信需要Binder
	public class WeatherServiceBinder extends Binder{
		
		public WeatherService getService(){
			return WeatherService.this;
		}
	}
	
} 
