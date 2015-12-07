package com.sellsapp.welcome;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * 导航页面的适配器
 * 
 * @author juanjuan
 * 
 */
public class GuideAdapter extends PagerAdapter {

	private List<View> views;

	public GuideAdapter(List<View> views) {
		this.views = views;
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		((ViewPager) container).addView(views.get(position), 0);
		return views.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}
