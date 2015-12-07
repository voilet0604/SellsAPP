package com.sellsapp.basic;

import com.sellsapp.home.HomeFragment;
import com.sellsapp.message.MessageFragment;
import com.sellsapp.personal.PersonalFragment;
import com.sellsapp.shoppingcart.ShoppingcartFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

//用于切换四个模块的适配器
public class MainViewPagerAdapter extends FragmentPagerAdapter {

	public MainViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	//不同position显示对应不同的fragment
	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;

		case 1:
			fragment = new ShoppingcartFragment();
			break;
		case 2:
			fragment = new MessageFragment();
			break;
		case 3:
			fragment = new PersonalFragment();
			break;
		}
		return fragment;
	}

	//4个fragment
	@Override
	public int getCount() {
		return 4;
	}

}
