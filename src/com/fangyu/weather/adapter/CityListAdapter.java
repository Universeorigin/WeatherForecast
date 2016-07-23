package com.fangyu.weather.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.weatherforecast.R;

public class CityListAdapter extends BaseAdapter {
	
	private List<String> list;
	private LayoutInflater mLayoutInflater;
	
	public CityListAdapter(Context context, List<String> list) {
		this.list=list;
		//动态加载布局
		mLayoutInflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view=null;
		if (convertView==null) {
			view=mLayoutInflater.inflate(R.layout.list_city_item, null);
		}else {
			view=convertView;
		}
		TextView tv_city=(TextView) view.findViewById(R.id.tv_city);
		tv_city.setText(getItem(position));
		
		return view;
	}

}
