package com.sellsapp.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

import com.sellsapp.R;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.models.AppUpdateInfo;
import com.sellsapp.service.UpAppService;
import com.sellsapp.utils.Tools;

/**
 * 用来弹出下载更新对话框
 * 
 */
public class UpAppActivity extends Activity {

	private AppUpdateInfo appUpdateInfo;

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_up_app);
		
		//准备开启服务下载apk
		final Intent service = new Intent(this, UpAppService.class);
		
		appUpdateInfo = (AppUpdateInfo) getIntent().getSerializableExtra(
				"appUpdateInfo");
		if (null == appUpdateInfo) { 
			Toast.makeText(this,
					getResources().getString(R.string.update_app_lose),
					Toast.LENGTH_SHORT).show();
			service.putExtra("action", BaseConfig.UPDATE_APP);
			startService(service);
			finish();
			return;
		}
		//弹出对话框，用户选择是否下载
		AlertDialog.Builder builder = new Builder(this,
				AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setTitle(getResources().getString(R.string.update_app_notice));
		builder.setCancelable(false);
		builder.setMessage(Html.fromHtml(appUpdateInfo.getDescription()));
		builder.setPositiveButton(
				getResources().getString(R.string.update_app_backstage),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						String netConnect = Tools
								.checkNetWorkConnect(getApplicationContext());
						if (TextUtils.isEmpty(netConnect)
								|| Tools.UNKNOW_CONNECT.equals(netConnect)) {
							netConnect = getResources().getString(
									R.string.update_app_error);
							Toast.makeText(getApplicationContext(), netConnect,
									Toast.LENGTH_SHORT).show();
							finish();
							return;
						} else if (Tools.MOBILE_CONNECT.equals(netConnect)) {
							netConnect = netConnect
									+ getResources().getString(
											R.string.update_app_mobile);
						} else if (Tools.WIFI_CONNECT.equals(netConnect)) {
							netConnect = netConnect
									+ getResources().getString(
											R.string.update_app_wifi);
						}
						Toast.makeText(getApplicationContext(), netConnect,
								Toast.LENGTH_SHORT).show();
						service.putExtra("action", BaseConfig.DOWN_APP);
						service.putExtra("appUpdateInfo", appUpdateInfo);
						startService(service);
						finish();

					}
				});
		builder.setNeutralButton(
				getResources().getString(R.string.update_app_canncel),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
		builder.create().show();
	}

}