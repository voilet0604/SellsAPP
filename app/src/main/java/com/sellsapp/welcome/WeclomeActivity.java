package com.sellsapp.welcome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

import com.sellsapp.R;
import com.sellsapp.basic.MainActivity;

public class WeclomeActivity extends Activity {

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weclome);
		init();
	}

	private void init() {
		RelativeLayout rlWelcome = (RelativeLayout) findViewById(R.id.rl_welcome);
		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(2000);
		rlWelcome.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@SuppressWarnings("rawtypes")
			@Override
			public void onAnimationEnd(Animation animation) {// 判断当前应该是否第一次打开，是跳转到导航页面
				SharedPreferences preferences = getSharedPreferences("SellApp",
						MODE_PRIVATE);
				Intent intent = null;
				Class clazz = null;
				if (preferences.getBoolean("isFirst", true)) { // 第一次打开应用
					clazz = GuideActivity.class;
				} else {
					clazz = MainActivity.class;
				}
				intent = new Intent(WeclomeActivity.this, clazz);
				startActivity(intent);
				finish();
			}
		});
		
	}

}
