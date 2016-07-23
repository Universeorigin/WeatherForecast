package com.fangyu.weather;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherforecast.R;
import com.fangyu.weather.bean.FutureWeatherBean;
import com.fangyu.weather.bean.HoursWeatherBean;
import com.fangyu.weather.bean.PMBean;
import com.fangyu.weather.bean.WeatherBean;
import com.fangyu.weather.service.WeatherService;
import com.fangyu.weather.service.WeatherService.OnParseCallBack;
import com.fangyu.weather.service.WeatherService.WeatherServiceBinder;
import com.fangyu.weather.swiperefresh.PullToRefreshBase;
import com.fangyu.weather.swiperefresh.PullToRefreshBase.OnRefreshListener;
import com.fangyu.weather.swiperefresh.PullToRefreshScrollView;

public class WeatherActivity extends Activity {
	
	private Context mContext;
	private PullToRefreshScrollView mPullToRefreshScrollView;
	private ScrollView mScrollView;
	
	private WeatherService mService;
	
    private TextView tv_city,// ����
            tv_release,// ����ʱ��
            tv_now_weather,// ����
            tv_today_temp,// �¶�
            tv_now_temp,// ��ǰ�¶�
            tv_aqi,// ��������ָ��
            tv_quality,// ��������
            tv_next_three,// 3Сʱ
            tv_next_six,// 6Сʱ
            tv_next_nine,// 9Сʱ
            tv_next_twelve,// 12Сʱ
            tv_next_fifteen,// 15Сʱ
            tv_next_three_temp,// 3Сʱ�¶�
            tv_next_six_temp,// 6Сʱ�¶�
            tv_next_nine_temp,// 9Сʱ�¶�
            tv_next_twelve_temp,// 12Сʱ�¶�
            tv_next_fifteen_temp,// 15Сʱ�¶�
            tv_today_tempMax,// �����¶�a
            tv_today_tempMin,// �����¶�b
            tv_tommorrow,// ����
            tv_tommorrow_tempMax,// �����¶�a
            tv_tommorrow_tempMin,// �����¶�b
            tv_thirdday,// ������
            tv_thirdday_tempMax,// �������¶�a
            tv_thirdday_tempMin,// �������¶�b
            tv_fourthday,// ������
            tv_fourthday_tempMax,// �������¶�a
            tv_fourthday_tempMin,// �������¶�b
            tv_humidity,// ʪ��
            tv_wind, tv_uv_index,// ������ָ��
            tv_dressing_index;// ����ָ��

    private ImageView iv_now_weather,// ����
            iv_next_three,// 3Сʱ
            iv_next_six,// 6Сʱ
            iv_next_nine,// 9Сʱ
            iv_next_twelve,// 12Сʱ
            iv_next_fifteen,// 15Сʱ
            iv_today_weather,// ����
            iv_tommorrow_weather,// ����
            iv_thirdday_weather,// ������
            iv_fourthday_weather;// ������

    private RelativeLayout rl_city;
    
    private static String city;
    
    //�������������������жϻص�������ִ��
//    private boolean isRunning=false;
//    private int count=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		mContext=this;
//		getCityWeather();
		init();
		initService();
	}
	
	private void initService(){
		Intent intent=new Intent(mContext, WeatherService.class);
		startService(intent);
		//BIND_AUTO_CREATE��ʾ�ڻ�ͷ�����а�֮���Զ���������
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private ServiceConnection serviceConnection=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService.removeCallBack();
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService=((WeatherServiceBinder)service).getService();
			mService.setCallBack(new OnParseCallBack() {
				
				@Override
				public void OnParseComplete(List<HoursWeatherBean> hourList, PMBean pmBean,
						WeatherBean weatherBean) {
					
					mPullToRefreshScrollView.onRefreshComplete();
					
					if (hourList!=null && hourList.size()<=5) {
						setHourViews(hourList);
					}
					
					if (pmBean!=null) {
						setPMViews(pmBean);
					}
					if (weatherBean!=null) {
						setWeatherViews(weatherBean);
					}
				}
			});
//			mService.getCityWeather();
			Log.e("GPS��λ��������������", "GPS��λ�ص���������");
			mService.getCityWeatherByGEO();
		}
	};
	
	
	private void setPMViews(PMBean pmBean){
		tv_aqi.setText(pmBean.getAqi());
		tv_quality.setText(pmBean.getQuality());
	}
	
	//�������
	private void setWeatherViews(WeatherBean weatherBean){
		
		tv_city.setText(weatherBean.getCity());
        tv_release.setText(weatherBean.getRelease());
        tv_now_weather.setText(weatherBean.getWeather_str());
        String[] tempArray=weatherBean.getTemp().split("~");
        String tempMax = tempArray[1].substring(0, tempArray[1].indexOf("��"));
        String tempMin = tempArray[0].substring(0, tempArray[0].indexOf("��"));
     // �¶� 8��~16��" �� �� ��
        tv_today_temp.setText("�� " + tempMax + "��   ��" + tempMin + "��");
        tv_now_temp.setText(weatherBean.getNow_temp()+"��");
        iv_today_weather.setImageResource(getResources().getIdentifier("d"+weatherBean.getWeather_id(), "drawable", "com.example.weatherforecast"));
        
        tv_today_tempMax.setText(tempMax + "��");
        tv_today_tempMin.setText(tempMin + "��");
        List<FutureWeatherBean> futureList = weatherBean.getFutureList();
        if (futureList.size()==3) {
        	setFutureData(tv_tommorrow, iv_tommorrow_weather, tv_tommorrow_tempMax, tv_tommorrow_tempMin, futureList.get(0));
        	setFutureData(tv_thirdday, iv_thirdday_weather, tv_thirdday_tempMax, tv_thirdday_tempMin, futureList.get(1));
        	setFutureData(tv_fourthday, iv_fourthday_weather, tv_fourthday_tempMax, tv_fourthday_tempMin, futureList.get(2));
		}
        Calendar calendar=Calendar.getInstance();
        int time=calendar.get(Calendar.HOUR_OF_DAY);
        String prefixString=null;
        if (time>=6 && time<18) {
			prefixString="d";
		}else {
			prefixString="n";
		}
        iv_now_weather.setImageResource(getResources().getIdentifier(prefixString+weatherBean.getWeather_id(), "drawable", "com.example.weatherforecast"));
        
        tv_humidity.setText(weatherBean.getHumidity());
        tv_dressing_index.setText(weatherBean.getDressing_index());
        tv_uv_index.setText(weatherBean.getUv_index());
        tv_wind.setText(weatherBean.getWind());
	}
	
	private void setHourViews(List<HoursWeatherBean> hourList) {
		
		setHourData(tv_next_three, iv_next_three, tv_next_three_temp, hourList.get(0));
        setHourData(tv_next_six, iv_next_six, tv_next_six_temp, hourList.get(1));
        setHourData(tv_next_nine, iv_next_nine, tv_next_nine_temp, hourList.get(2));
        setHourData(tv_next_twelve, iv_next_twelve, tv_next_twelve_temp, hourList.get(3));
        setHourData(tv_next_fifteen, iv_next_fifteen, tv_next_fifteen_temp, hourList.get(4));
		
	}
	
	private void setHourData(TextView tv_hour, ImageView iv_weather, TextView tv_temp, HoursWeatherBean bean){
		
		String prefixString=null;
		int time=Integer.valueOf(bean.getTime());
		if (time>=6 && time<18) {
			prefixString="d";
		}else {
			prefixString="n";
		}
		tv_hour.setText(bean.getTime()+"��");
		iv_weather.setImageResource(getResources().getIdentifier(prefixString+bean.getWeather_id(), "drawable", "com.example.weatherforecast"));
		tv_temp.setText(bean.getTemp()+"��");
		
	}
	
	private void setFutureData(TextView tv_week, ImageView iv_weather, TextView tv_tempMax, TextView tv_tempMin, FutureWeatherBean bean){
		
		tv_week.setText(bean.getWeek());
		//����ͼƬ���ú�weather_id���Ӧ��d��ʾ����day��n��ʾҹ��night
		//�����ﶼ��ʾ���������ͼƬ
        iv_weather.setImageResource(getResources().getIdentifier("d" + bean.getWeather_id(), "drawable", "com.example.weatherforecast"));
        String[] tempArray = bean.getTemp().split("~");
        String tempMax = tempArray[1].substring(0, tempArray[1].indexOf("��"));
        String tempMin = tempArray[0].substring(0, tempArray[0].indexOf("��"));
        tv_tempMax.setText(tempMax + "��");
        tv_tempMin.setText(tempMin + "��");
		
	}
	
	private void init(){
		mPullToRefreshScrollView=(PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		mPullToRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				mService.getCityWeather();
			}
		});
		mScrollView=mPullToRefreshScrollView.getRefreshableView();
		
		rl_city = (RelativeLayout) findViewById(R.id.rl_city);
		//�����ת������ѡ�����
        rl_city.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivityForResult(new Intent(mContext, CityActivity.class), 1);

            }
        });

        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_release = (TextView) findViewById(R.id.tv_release);
        tv_now_weather = (TextView) findViewById(R.id.tv_now_weather);
        tv_today_temp = (TextView) findViewById(R.id.tv_today_temp);
        tv_now_temp = (TextView) findViewById(R.id.tv_now_temp);
        tv_aqi = (TextView) findViewById(R.id.tv_aqi);
        tv_quality = (TextView) findViewById(R.id.tv_quality);
        tv_next_three = (TextView) findViewById(R.id.tv_next_three);
        tv_next_six = (TextView) findViewById(R.id.tv_next_six);
        tv_next_nine = (TextView) findViewById(R.id.tv_next_nine);
        tv_next_twelve = (TextView) findViewById(R.id.tv_next_twelve);
        tv_next_fifteen = (TextView) findViewById(R.id.tv_next_fifteen);
        tv_next_three_temp = (TextView) findViewById(R.id.tv_next_three_temp);
        tv_next_six_temp = (TextView) findViewById(R.id.tv_next_six_temp);
        tv_next_nine_temp = (TextView) findViewById(R.id.tv_next_nine_temp);
        tv_next_twelve_temp = (TextView) findViewById(R.id.tv_next_twelve_temp);
        tv_next_fifteen_temp = (TextView) findViewById(R.id.tv_next_fifteen_temp);
        tv_today_tempMax = (TextView) findViewById(R.id.tv_today_temp_a);
        tv_today_tempMin = (TextView) findViewById(R.id.tv_today_temp_b);
        tv_tommorrow = (TextView) findViewById(R.id.tv_tommorrow);
        tv_tommorrow_tempMax = (TextView) findViewById(R.id.tv_tommorrow_temp_a);
        tv_tommorrow_tempMin = (TextView) findViewById(R.id.tv_tommorrow_temp_b);
        tv_thirdday = (TextView) findViewById(R.id.tv_thirdday);
        tv_thirdday_tempMax = (TextView) findViewById(R.id.tv_thirdday_temp_a);
        tv_thirdday_tempMin = (TextView) findViewById(R.id.tv_thirdday_temp_b);
        tv_fourthday = (TextView) findViewById(R.id.tv_fourthday);
        tv_fourthday_tempMax = (TextView) findViewById(R.id.tv_fourthday_temp_a);
        tv_fourthday_tempMin = (TextView) findViewById(R.id.tv_fourthday_temp_b);
        tv_humidity = (TextView) findViewById(R.id.tv_humidity);
        tv_wind = (TextView) findViewById(R.id.tv_wind);
        tv_uv_index = (TextView) findViewById(R.id.tv_uv_index);
        tv_dressing_index = (TextView) findViewById(R.id.tv_dressing_index);

        iv_now_weather = (ImageView) findViewById(R.id.iv_now_weather);
        iv_next_three = (ImageView) findViewById(R.id.iv_next_three);
        iv_next_six = (ImageView) findViewById(R.id.iv_next_six);
        iv_next_nine = (ImageView) findViewById(R.id.iv_next_nine);
        iv_next_twelve = (ImageView) findViewById(R.id.iv_next_twelve);
        iv_next_fifteen = (ImageView) findViewById(R.id.iv_next_fifteen);
        iv_today_weather = (ImageView) findViewById(R.id.iv_today_weather);
        iv_tommorrow_weather = (ImageView) findViewById(R.id.iv_tommorrow_weather);
        iv_thirdday_weather = (ImageView) findViewById(R.id.iv_thirdday_weather);
        iv_fourthday_weather = (ImageView) findViewById(R.id.iv_fourthday_weather);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == 1 && resultCode == 1) {
            String county = data.getStringExtra("county");
            if (county!=null) {
            	Log.e("COUNTY>>>>>>>>>>>>>>>>>>>>>>", county);
			}else {
				Log.e("COUNTY>>>>>>>>>>>>>>>>>>>>>>", "county Ϊ�գ�����");
			}
            mService.getCityWeather(county);
            
            String gps=data.getStringExtra("gps");
            if (gps!=null) {
            	Log.e("GPS>>>>>>>>>>>>>>>>>>>>>>", gps);
            	mService.getCityWeatherByGEO();
			}else {
				Log.e("GPS>>>>>>>>>>>>>>>>>>>>>>", "gps is null");
			}
            
            String spotName = data.getStringExtra("spotName");
            
            if (spotName!=null) {
            	Log.e("spotName>>>>>>>>>>>>>>>>>>>>>>", spotName);
            	Toast.makeText(WeatherActivity.this, spotName+"λ��"+county, Toast.LENGTH_LONG).show();
            	tv_city.setText(spotName);
			}else {
				Log.e("spotName>>>>>>>>>>>>>>>>>>>>>>", "spotName Ϊ�գ�����");
			}
            
            city=data.getStringExtra("city");
			if (city!=null) {
				Log.e("CITY>>>>>>>>>>>>>>>>>>>>>>", city);
			}else {
				Log.e("CITY>>>>>>>>>>>>>>>>>>>>>>", "city Ϊ�գ�����");
			}
//            if (Utility.isNull) {
//            	Utility.isNull=false;
//            	String city=data.getStringExtra("city");
//				if (city!=null) {
//					Log.e("CITY>>>>>>>>>>>>>>>>>>>>>>", city);
//					mService.getCityWeather(city);
//				}else {
//					Log.e("CITY>>>>>>>>>>>>>>>>>>>>>>", "city Ϊ�գ�����");
//				}
//			}
            /*if (getSharedPreferences("nullOrNot", MODE_PRIVATE).getBoolean("isNull", false)) {
				Utility.isNull=false;
				SharedPreferences.Editor editor=getSharedPreferences("nullOrNot", MODE_PRIVATE).edit();
				editor.putBoolean("isNull", Utility.isNull);
				editor.commit();
				
				String city=data.getStringExtra("city");
				if (city!=null) {
					Log.e("CITY>>>>>>>>>>>>>>>>>>>>>>", city);
					mService.getCityWeather(city);
				}else {
					Log.e("CITY>>>>>>>>>>>>>>>>>>>>>>", "city Ϊ�գ�����");
				}
			}*/
		}
	}
	
	@Override
	protected void onDestroy() {
		unbindService(serviceConnection);
		super.onDestroy();
	}
	
}
