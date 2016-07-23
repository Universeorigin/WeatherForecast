package com.fangyu.weather.util;

import android.text.TextUtils;

import com.fangyu.weather.db.UWeatherDB;
import com.fangyu.weather.model.City;
import com.fangyu.weather.model.County;
import com.fangyu.weather.model.Province;


public class Utility {
	
	public static boolean isNull=false;
	
	public synchronized static boolean handleProvincesResponse(UWeatherDB uWeatherDB, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces=response.split(",");
			if(allProvinces!=null && allProvinces.length>0){
				for(String p:allProvinces){
					String[] array=p.split("\\|");
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					uWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleCitiesResponse(UWeatherDB uWeatherDB, String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities=response.split(",");
			if(allCities!=null && allCities.length>0){
				for(String c:allCities){
					String[] array=c.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					uWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleCountiesResponse(UWeatherDB uWeatherDB, String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties=response.split(",");
			if(allCounties!=null && allCounties.length>0){
				for(String c:allCounties){
					String[] array=c.split("\\|");
					County county=new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					uWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	//去掉集合中重复的元素
	/*private static List<String> getNewList(List<String> li){
		List<String> list = new ArrayList<String>();
		for(int i=0; i<li.size(); i++){
			String str = li.get(i); //获取传入集合对象的每一个元素
			if(!list.contains(str)){ //查看新集合中是否有指定的元素，如果没有则加入
				list.add(str);
			}
		}
		return list; //返回集合
	}*/
	
	//一开始就从服务器获取到了城市数据，因此可以直接解析出城市数据并将其放到数据库里面，这样从数据库获取数据就不存在数据库为空的情况
	//解析出省级数据
	/*public static boolean handleProvinces(UWeatherDB uWeatherDB, JSONObject jsonObject){
		if(!TextUtils.isEmpty(jsonObject.toString())){
			List<String> tempList=new ArrayList<String>();
			List<String> provinceList=new ArrayList<String>();
			try {
				JSONArray resultArray = jsonObject.getJSONArray("result");
				for (int i = 0; i < resultArray.length(); i++) {
					tempList.add(resultArray.getJSONObject(i).getString("province"));
				}
				provinceList=Utility.getNewList(tempList);
				for (int i = 0; i < provinceList.size(); i++) {
					Province province=new Province();
					province.setProvinceName(provinceList.get(i));
					uWeatherDB.saveProvince(province);
				}
				return true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}*/
 	
	//解析出市级数据
	/*public static void handleCities(UWeatherDB uWeatherDB, JSONObject jsonObject, String cityInProvince){
		if(!TextUtils.isEmpty(jsonObject.toString())){
			List<String> tempList=new ArrayList<String>();
			List<String> cityList=new ArrayList<String>();
			try {
				JSONArray resultArray = jsonObject.getJSONArray("result");
				for (int i = 0; i < resultArray.length(); i++) {
					tempList.add(resultArray.getJSONObject(i).getString("city"));
				}
				cityList=Utility.getNewList(tempList);
				for (int i = 0; i < cityList.size(); i++) {
					City city=new City();
					city.setCityName(cityList.get(i));
					city.setCityInProvince(cityInProvince);
					uWeatherDB.saveCity(city);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
	
	//解析出城区数据
	/*public static void handleDistricts(UWeatherDB uWeatherDB, JSONObject jsonObject, String districtInCity){
		
		if(!TextUtils.isEmpty(jsonObject.toString())){
			List<String> tempList=new ArrayList<String>();
			List<String> districtList=new ArrayList<String>();
			try {
				JSONArray resultArray = jsonObject.getJSONArray("result");
				for (int i = 0; i < resultArray.length(); i++) {
					tempList.add(resultArray.getJSONObject(i).getString("district"));
				}
				districtList=Utility.getNewList(tempList);
				for (int i = 0; i < districtList.size(); i++) {
					County district=new County();
					district.setDistrictName(districtList.get(i));
					district.setDistrictInCity(districtInCity);
					uWeatherDB.saveDistrict(district);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}*/

}
