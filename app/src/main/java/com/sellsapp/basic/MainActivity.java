package com.sellsapp.basic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sellsapp.R;
import com.sellsapp.tcp.service.TCPService;
import com.sellsapp.utils.Tools;

//基本的activity
public class MainActivity extends FragmentActivity implements OnClickListener,
		OnPageChangeListener {

	private ViewPager viewPager; // 切换

	// 底部的四个按钮
	private ImageView ivHome;
	private ImageView ivShopping;
	private FrameLayout flMessage;
	private ImageView ivPersonal;
	
	private static ImageView iv_point;

	private MainViewPagerAdapter viewPagerAdapter;
	
	private SharedPreferences sp;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		sp = getSharedPreferences("SellApp", MODE_PRIVATE);
		boolean isLogin = sp.getBoolean("isLogin", false);
		Intent intent = new Intent(this, TCPService.class);
		if(isLogin) {
			if(!Tools.serviceIsRunning(getApplicationContext(), TCPService.SRVICE_NAME)){
				startService(intent);
			}
		}else {
			if(Tools.serviceIsRunning(getApplicationContext(), TCPService.SRVICE_NAME)){
				stopService(intent);
			}
		}
	
		initView();
		setOnClickListener();
	}

	// 初始化 view
	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.vp_content);
		ivHome = (ImageView) findViewById(R.id.iv_home);
		ivShopping = (ImageView) findViewById(R.id.iv_shopping);
		flMessage = (FrameLayout) findViewById(R.id.fl_message);
		ivPersonal = (ImageView) findViewById(R.id.iv_personal);
		iv_point = (ImageView) findViewById(R.id.iv_point);
		iv_point.setVisibility(View.GONE);
		viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(viewPagerAdapter);
	}
	public static void setPoint(boolean isVisibitity){
		if(isVisibitity) {
			iv_point.setVisibility(View.VISIBLE);
		}else {
			iv_point.setVisibility(View.GONE);
		}
	}

	// 设置监听
	private void setOnClickListener() {
		ivHome.setOnClickListener(this);
		ivShopping.setOnClickListener(this);
		flMessage.setOnClickListener(this);
		ivPersonal.setOnClickListener(this);

		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(0);
	}

	// 对应监听的业务操作
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_home:
			viewPager.setCurrentItem(0);
			break;

		case R.id.iv_shopping:
			viewPager.setCurrentItem(1);
			break;
			
		case R.id.fl_message:
			iv_point.setVisibility(View.GONE);
			viewPager.setCurrentItem(2);
			break;
			
		case R.id.iv_personal:
			viewPager.setCurrentItem(3);
			break;

		}
	}

	// viewpager的 监听业务操作
	@Override
	public void onPageScrollStateChanged(int position) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
	
	}

}
