package com.sellsapp.personal;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sellsapp.R;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.basic.widget.TopBarView;
import com.sellsapp.service.UpAppService;
import com.sellsapp.utils.AppManager;
import com.sellsapp.utils.Tools;

//设置中心
public class PersonalSettingActivity extends Activity {

	private Button message_btn;
	private Button pwd_change_btn;
	private Button update_check_btn;
	private Button about_us;

	@ViewInject(R.id.bar)
	private TopBarView bar;
	
	private SharedPreferences sp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_personal_setting);
		
		ViewUtils.inject(this);
		AppManager.getAppManager().addActivity(this);
		
		sp = getSharedPreferences("SellApp", MODE_PRIVATE);
		bar.setTitle("设置");
		message_btn = (Button) findViewById(R.id.message_btn);
		pwd_change_btn = (Button) findViewById(R.id.pwd_change_btn);
		update_check_btn = (Button) findViewById(R.id.update_check_btn);
		about_us = (Button) findViewById(R.id.about_us_btn);

		pwd_change_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(Tools.checkNetWorkConnect(getApplicationContext()).equals(Tools.UNKNOW_CONNECT)) {
					Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(PersonalSettingActivity.this,
						RegisterActivity.class);
				intent.putExtra("type", "reset");
				startActivity(intent);
			}

		});
		message_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PersonalSettingActivity.this, WebViewActivity.class);
				intent.putExtra("type", "1");
				intent.putExtra("url", "file:///android_asset/html/OBTP_t.html");
				startActivity(intent);
			}

		});

		update_check_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateAppVersion();
			}

		});
		about_us.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(PersonalSettingActivity.this, WebViewActivity.class);
				intent.putExtra("type", "2");
				intent.putExtra("url", "file:///android_asset/html/OBTP_y.html");
				startActivity(intent);
			}

		});
	}
	//开启service 更新应用
	private void updateAppVersion() {
		String state = Tools.checkNetWorkConnect(this);
		if (TextUtils.isEmpty(state)
				|| Tools.UNKNOW_CONNECT.equals(state)) {
		}else {
			Intent intent = new Intent(this, UpAppService.class);
			intent.putExtra("action", BaseConfig.UPDATE_APP);
			intent.addFlags(Service.START_STICKY_COMPATIBILITY);
			startService(intent);
			Toast.makeText(getApplicationContext(), "正在获取更新信息", Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AppManager.getAppManager().finishActivity(PersonalSettingActivity.this);
	}
}
