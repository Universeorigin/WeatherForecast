package com.fangyu.weather;

import com.thinkland.juheapi.common.CommonFun;

import android.app.Application;

public class WeatherApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		//��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		//ע��÷���Ҫ��setContentView����֮ǰʵ��
		//ע�⣺��SDK���������ʹ��֮ǰ����Ҫ����CommonFun.initialize(getApplicationContext());
		//��˽���÷�������Application�ĳ�ʼ��������
		CommonFun.initialize(getApplicationContext());
	}

}
