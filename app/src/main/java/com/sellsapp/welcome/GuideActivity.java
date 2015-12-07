package com.sellsapp.welcome;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.sellsapp.R;
import com.sellsapp.basic.MainActivity;
import com.sellsapp.personal.LoginActivity;

/**
 * 用于切换引导图片的页面
 * @author Administrator
 *
 */
public class GuideActivity extends Activity implements OnClickListener {

	private ViewPager viewPager;
	private List<View> views;
	private ImageView iv01, iv02, iv03;
	
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);
		sp = getSharedPreferences("SellApp", MODE_PRIVATE);
		viewPager = (ViewPager) findViewById(R.id.guide_viewpager);
		initViews();
		viewPager.setAdapter(new GuideAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					iv01.setImageResource(R.drawable.point01);
					iv02.setImageResource(R.drawable.point02);
					iv03.setImageResource(R.drawable.point02);
					break;
				case 1:
					iv01.setImageResource(R.drawable.point02);
					iv02.setImageResource(R.drawable.point01);
					iv03.setImageResource(R.drawable.point02);

					break;
				case 2:
					iv01.setImageResource(R.drawable.point02);
					iv02.setImageResource(R.drawable.point02);
					iv03.setImageResource(R.drawable.point01);

					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 每个view都用布局文件，主要是为了每个item都有不同的表现形式 每个item中可以加入多个控件，可以实现不同的动画效果
	 */
	private void initViews() {
		// 手机屏幕的高度
//		int height = getWindowManager().getDefaultDisplay().getHeight();
//		FrameLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	//	layoutParams.topMargin = height / 3;

		iv01 = (ImageView) findViewById(R.id.iv_01);
		iv02 = (ImageView) findViewById(R.id.iv_02);
		iv03 = (ImageView) findViewById(R.id.iv_03);
		views = new ArrayList<View>();

		View view01 = View.inflate(this, R.layout.guide_first, null);
		views.add(view01);
		
		View view02 = View.inflate(this, R.layout.guide_second, null);
		views.add(view02);

		View view03 = View.inflate(this, R.layout.guide_third, null);
		Button button = (Button) view03.findViewById(R.id.btn_tiyan);
		button.setOnClickListener(this);
		views.add(view03);
	}

	

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_tiyan:
			Intent intent = new Intent(this, MainActivity.class);
			boolean is = sp.getBoolean("isFirst", true);
			if(is) {
				intent = new Intent(this, LoginActivity.class);
				intent.putExtra("isFirst", true);
			}
			sp.edit()
			.putBoolean("isFirst", false)
			.commit();
			startActivity(intent);
			finish();
			
			break;
		}
	}

}
