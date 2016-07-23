package com.fangyu.weather;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherforecast.R;
import com.fangyu.weather.adapter.CityListAdapter;
import com.fangyu.weather.db.UWeatherDB;
import com.fangyu.weather.model.City;
import com.fangyu.weather.model.County;
import com.fangyu.weather.model.Province;
import com.fangyu.weather.util.HttpCallbackListener;
import com.fangyu.weather.util.HttpUtil;
import com.fangyu.weather.util.Utility;

public class CityActivity extends Activity{
	
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;
	
	private Button dingweiButton,beijingButton,tainjinButton,shanghaiButton,chongqingButton,
	shenyangButton,dalianButton,changchunButton,haerbinButton,zhengzhouButton,wuhanButton,
	changshaButton,guangzhouButton,shenzhenButton,nanjingButton,gugongbButton,dongfangButton,
	huangguoshuButton,huangshanButton,lushanButton,qingmingButton,budalagongButton,
	qinshihuangButton,yungangButton,jingpohuButton,taohuayuanButton,huanghelouButton,
	lijiangButton,leshanButton,nanjingfuzimiaoButton;
	
	private List<Button> buttons=new ArrayList<Button>();
	
	private CityListAdapter adapter;
	private UWeatherDB uWeatherDB;
	
	private ScrollView popScrollView;
	private int count=0;
	
	private ListView lv_city;
	private TextView titleText;
	private List<String> dataList=new ArrayList<String>();
	
	private List<Province> provinceList;//省列表
	private List<City> cityList;//市列表
	private List<County> countyList;//县区列表
	private Province selectedProvince;//当前选中的省份
	private City selectedCity;//当前选中的城市
	private int currentLevel;//当前选中的级别
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_city);
		
		initView();
	}
	
	@Override
	public void onBackPressed() {
		
		if (currentLevel==LEVEL_COUNTY) {
			queryCities();
		}else if (currentLevel==LEVEL_CITY) {
			queryProvinces();
		}else {
			finish();
		}
	}
	
	private void initView(){
		
		dingweiButton=(Button) findViewById(R.id.gps);
		buttons.add(dingweiButton);
		beijingButton=(Button) findViewById(R.id.beijing);
		buttons.add(beijingButton);
		tainjinButton=(Button) findViewById(R.id.tianjin);
		buttons.add(tainjinButton);
		shanghaiButton=(Button) findViewById(R.id.shangahi);
		buttons.add(shanghaiButton);
		chongqingButton=(Button) findViewById(R.id.chongqing);
		buttons.add(chongqingButton);
		shenyangButton=(Button) findViewById(R.id.shenyang);
		buttons.add(shenyangButton);
		dalianButton=(Button) findViewById(R.id.dalian);
		buttons.add(dalianButton);
		changchunButton=(Button) findViewById(R.id.changchun);
		buttons.add(changchunButton);
		haerbinButton=(Button) findViewById(R.id.haerbin);
		buttons.add(haerbinButton);
		zhengzhouButton=(Button) findViewById(R.id.zhengzhou);
		buttons.add(zhengzhouButton);
		wuhanButton=(Button) findViewById(R.id.wuhan);
		buttons.add(wuhanButton);
		changshaButton=(Button) findViewById(R.id.changsha);
		buttons.add(changshaButton);
		guangzhouButton=(Button) findViewById(R.id.guangzhou);
		buttons.add(guangzhouButton);
		shenzhenButton=(Button) findViewById(R.id.shenzhen);
		buttons.add(shenzhenButton);
		nanjingButton=(Button) findViewById(R.id.nanjing);
		buttons.add(nanjingButton);
		
		gugongbButton=(Button) findViewById(R.id.gugongbowuyuan);
		buttons.add(gugongbButton);
		dongfangButton=(Button) findViewById(R.id.dongfangmingzhuta);
		buttons.add(dongfangButton);
		huangguoshuButton=(Button) findViewById(R.id.huangguoshupubu);
		buttons.add(huangguoshuButton);
		huangshanButton=(Button) findViewById(R.id.huangshanfengjingqu);
		buttons.add(huangshanButton);
		lushanButton=(Button) findViewById(R.id.lushanfengjingqu);
		buttons.add(lushanButton);
		qingmingButton=(Button) findViewById(R.id.qingmingshanghetu);
		buttons.add(qingmingButton);
		budalagongButton=(Button) findViewById(R.id.budalagong);
		buttons.add(budalagongButton);
		qinshihuangButton=(Button) findViewById(R.id.qinshihuangling);
		buttons.add(qinshihuangButton);
		yungangButton=(Button) findViewById(R.id.yungangshiku);
		buttons.add(yungangButton);
		jingpohuButton=(Button) findViewById(R.id.jingpohu);
		buttons.add(jingpohuButton);
		taohuayuanButton=(Button) findViewById(R.id.taohuayuan);
		buttons.add(taohuayuanButton);
		huanghelouButton=(Button) findViewById(R.id.huanghelou);
		buttons.add(huanghelouButton);
		lijiangButton=(Button) findViewById(R.id.lijianggucheng);
		buttons.add(lijiangButton);
		leshanButton=(Button) findViewById(R.id.leshandafo);
		buttons.add(leshanButton);
		nanjingfuzimiaoButton=(Button) findViewById(R.id.nanjingfuzimiao);
		buttons.add(nanjingfuzimiaoButton);
		
		for (int i = 0; i < buttons.size(); i++) {
			final Button tempButton=buttons.get(i);
			final String tempString=tempButton.getText().toString();
			final Intent intent = new Intent();
			
			tempButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (tempString.contains("定位")) {
						//定位所在城市，并查询所定位城市的天气
						intent.putExtra("gps", tempString.replace("♦", ""));
						setResult(1, intent);
						finish();
					}else if (tempString.contains("市")) {
						intent.putExtra("county", tempString.replace("市", ""));
						setResult(1, intent);
						finish();
					}else {
						Intent intent=new Intent();
						intent.putExtra("spotName", tempString);
						
						if (tempString==getResources().getString(R.string.gugongbowuyuan)) {
							intent.putExtra("county", "北京");
						}
						if (tempString==getResources().getString(R.string.dongfangmingzhuta)) {
							intent.putExtra("county", "上海");
						}
						if (tempString==getResources().getString(R.string.huangguoshupubu)) {
							intent.putExtra("county", "安顺");
						}
						if (tempString==getResources().getString(R.string.huangshanfengjingqu)) {
							intent.putExtra("county", "黄山");
						}
						if (tempString==getResources().getString(R.string.lushanfengjingqu)) {
							intent.putExtra("county", "九江");
						}
						if (tempString==getResources().getString(R.string.qingmingshanghetu)) {
							intent.putExtra("county", "金华");
						}
						if (tempString==getResources().getString(R.string.budalagong)) {
							intent.putExtra("county", "拉萨");
						}
						if (tempString==getResources().getString(R.string.qinshihuangling)) {
							intent.putExtra("county", "西安");
						}
						if (tempString==getResources().getString(R.string.yungangshiku)) {
							intent.putExtra("county", "大同");
						}
						if (tempString==getResources().getString(R.string.jingpohu)) {
							intent.putExtra("county", "宁安");
						}
						if (tempString==getResources().getString(R.string.taohuayuan)) {
							intent.putExtra("county", "常德");
						}
						if (tempString==getResources().getString(R.string.huanghelou)) {
							intent.putExtra("county", "武汉");
						}
						if (tempString==getResources().getString(R.string.lijianggucheng)) {
							intent.putExtra("county", "丽江");
						}
						if (tempString==getResources().getString(R.string.leshandafo)) {
							intent.putExtra("county", "乐山");
						}
						if (tempString==getResources().getString(R.string.nanjingfuzimiao)) {
							intent.putExtra("county", "南京");
						}
						setResult(1, intent);
						finish();
					}
				}
			});
		}
		
		findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentLevel==LEVEL_COUNTY) {
					queryCities();
				}else if (currentLevel==LEVEL_CITY) {
					queryProvinces();
				}else {
					finish();
				}
			}
		});
		popScrollView=(ScrollView) findViewById(R.id.popScrollView);
		findViewById(R.id.iv_addOtherCity).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (count%2==0) {
					popScrollView.setVisibility(View.GONE);
					lv_city.setVisibility(View.VISIBLE);
					count++;
				}else {
					popScrollView.setVisibility(View.VISIBLE);
					lv_city.setVisibility(View.GONE);
					titleText.setText("城市选择");
					count++;
				}
			}
		});
		
		lv_city=(ListView) findViewById(R.id.lv_city);
		titleText=(TextView) findViewById(R.id.titleText);
		adapter=new CityListAdapter(CityActivity.this, dataList);
		lv_city.setAdapter(adapter);
		uWeatherDB=UWeatherDB.getInstance(CityActivity.this);
		lv_city.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel==LEVEL_PROVINCE) {
					selectedProvince=provinceList.get(position);
					queryCities();
				}else if (currentLevel==LEVEL_CITY) {
					selectedCity=cityList.get(position);
					queryCounties();
				}else if (currentLevel==LEVEL_COUNTY) {
					
					Intent intent = new Intent();
					intent.putExtra("county", dataList.get(position));
					intent.putExtra("city", dataList.get(0));
					
					setResult(1, intent);
					finish();
				}
			}
		});
		queryProvinces();
//		closeProgressDialog();
	}
	//从数据库中获取省份数据，显示在列表中  
	private void queryProvinces(){
		provinceList=uWeatherDB.loadProvinces();
		if(provinceList.size()>0){
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			lv_city.setSelection(0);
			titleText.setText("城市选择");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null, "province");
		}
	}
	//从数据库中获取城市数据，显示在列表中
	private void queryCities(){
		cityList=uWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			lv_city.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	//从数据库中查询县区数据，显示到列表中
	private void queryCounties(){
		countyList=uWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			lv_city.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	//根据代号和类型从服务器上查询省市县数据
	private void queryFromServer(final String code, final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address="http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				if("province".equals(type)){
					result=Utility.handleProvincesResponse(uWeatherDB, response);
				}else if("city".equals(type)){
					result=Utility.handleCitiesResponse(uWeatherDB, response, selectedProvince.getId());
				}else if("county".equals(type)){
					result=Utility.handleCountiesResponse(uWeatherDB, response, selectedCity.getId());
				}
				if(result){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(CityActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		
	}
	
	private void showProgressDialog(){
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	
	/*private void getCitiesToDB(){
		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
				WeatherData data=WeatherData.getInstance();
				data.getCities(new JsonCallBack() {
					
					@Override
					public void jsonLoaded(final JSONObject json) {
						System.err.println(json.toString());
//						System.out.println(json.toString());
//						showProgressDialog();
//						new Thread(new Runnable() {
//							@Override
//							public void run() {
								try {
									int code = json.getInt("resultcode");
									int error_code = json.getInt("error_code");
									if (error_code == 0 && code == 200) {
										List<Province> tempProvinces=new ArrayList<Province>();
										List<City> tempCities=new ArrayList<City>();
										if(Utility.handleProvinces(uWeatherDB, json)){//将获取到的省份数据放到数据库
											tempProvinces=uWeatherDB.loadProvinces();//将数据库中的省份数据取出来，用于查询省份下面的城市
											for (int i = 0; i < tempProvinces.size(); i++) {
												Utility.handleCities(uWeatherDB, json, tempProvinces.get(i).getProvinceName());//将查询到的城市数据放到数据库里面
											}
											for (int j = 0; j < tempProvinces.size(); j++) {
												tempCities=uWeatherDB.loadCities(tempProvinces.get(j).getProvinceName());//将数据库中的城市数据取出来，用于查询城市下面的县区
											}
											for (int k = 0; k < tempCities.size(); k++) {
												Utility.handleDistricts(uWeatherDB, json, tempCities.get(k).getCityName());//将县区数据放到数据库中
											}
										}
										

										list = new ArrayList<String>();
										JSONArray resultArray = json.getJSONArray("result");
										//使用set管理城市名称
										//Set<String> set=new LinkedHashSet<String>();有顺序，无重复
										Set<String> citySet = new HashSet<String>();
										for (int i = 0; i < resultArray.length(); i++) {
											String city = resultArray.getJSONObject(i).getString("city");
											citySet.add(city);
										}
										list.addAll(citySet);
										CityListAdapter adatper = new CityListAdapter(CityActivity.this, list);
										lv_city.setAdapter(adatper);
										
										lv_city.setOnItemClickListener(new OnItemClickListener() {
											@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
												// TODO Auto-generated method stub
												Intent intent = new Intent();
												intent.putExtra("city", list.get(position));
												setResult(1, intent);
												finish();
											}
										});
				
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
//							}
//						}).start();
						
					}
				});
			}*/
			
//		}).start();
//	}
}
//}
		
		

