package com.sellsapp.basic;

import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

/**
 * 封装的adpter
 * @author juan
 *	方便adapter的创建，不用重复的重写所有的方法
 * @param <T> 传进参数的集合
 * @param <Q> 传进的控件
 */
public abstract class MyBaseAdapter<T, Q> extends BaseAdapter {

	public Context context;
	public List<T> list;
	public Q view;
	
	public MyBaseAdapter(Context context, List<T> list, Q view) {
		this.context = context;
		this.list = list;
		this.view = view;
	}
	
	public MyBaseAdapter(Context context, List<T> list) {
		this.context = context;
		this.list = list;
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void refresh(List<T> list) {
		this.list = list;
		System.out.println("list " + list.toString());
		notifyDataSetChanged();
	}
}
