package com.fangyu.weather;

import com.thinkland.juheapi.common.CommonFun;

import android.app.Application;

public class WeatherApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext
		//注意该方法要在setContentView方法之前实现
		//注意：在SDK各功能组件使用之前都需要调用CommonFun.initialize(getApplicationContext());
		//因此建议该方法放在Application的初始化方法中
		CommonFun.initialize(getApplicationContext());
	}

}
