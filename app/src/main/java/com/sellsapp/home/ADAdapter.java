package com.sellsapp.home;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.sellsapp.R;

/**
 * 首页广告滚动adapter
 */
public class ADAdapter extends BaseAdapter {

	private Context context;
	private List<Integer> urls;
	private OnClickListener onClickListener;
	private Animation animation;

	public ADAdapter(Context context, List<Integer> urls, OnClickListener onClickListener) {
		this.context = context;
		this.urls = urls;
		this.onClickListener = onClickListener;
	}
	@Override
	public int getCount() {
		return urls.size();
	}

	@Override
	public Object getItem(int position) {
		return urls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ImageView imageView = new ImageView(context);
		imageView.setScaleType(ScaleType.FIT_XY);
	
		imageView.setImageResource(urls.get(position));
		
		// 设置显示的动画效果
		animation = AnimationUtils.loadAnimation(context, R.anim.ad);
		imageView.setAnimation(animation);
		imageView.setOnClickListener(onClickListener);
		return imageView;
	}

}
