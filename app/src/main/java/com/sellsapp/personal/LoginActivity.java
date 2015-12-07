package com.sellsapp.personal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sellsapp.R;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.basic.MainActivity;
import com.sellsapp.basic.widget.ClearEditText;
import com.sellsapp.basic.widget.RefreshDialog;
import com.sellsapp.basic.widget.TopBarView;
import com.sellsapp.basic.widget.TopBarView.Callback;
import com.sellsapp.controllers.LoginController;
import com.sellsapp.models.BasePacket;
import com.sellsapp.models.LoginPacket;
import com.sellsapp.net.HttpClient;
import com.sellsapp.tcp.service.TCPService;
import com.sellsapp.utils.AppManager;
import com.sellsapp.utils.Tools;

/**
 * 登录页面
 */
public class LoginActivity extends Activity {
	
	@ViewInject(R.id.bar)
	private TopBarView bar;
	
	@ViewInject(R.id.login_account_et)
	private ClearEditText etAccount;
	
	@ViewInject(R.id.login_password_cet)
	private ClearEditText etPwd;
	
	@ViewInject(R.id.login_btn)
	private Button loginBtn;
	
	@ViewInject(R.id.register_btn)
	private Button registerBtn;
	
	@ViewInject(R.id.tv_findpassword) 
	private TextView tvfindPwd; //忘记密码
	
	private RefreshDialog refreshDialog;
	
	private SharedPreferences msp;

	private boolean isFirst;
	
	private String pswd;
	
	private String from = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		ViewUtils.inject(this);
		bar.setTitle(getResources().getString(R.string.login));
		refreshDialog = new RefreshDialog(this);
		AppManager.getAppManager().addActivity(this);
		msp = getSharedPreferences("SellApp", MODE_PRIVATE);
		isFirst = getIntent().getBooleanExtra("isFirst", false);
		from = getIntent().getStringExtra("from");
		//判断是否是第一次登录
		if(isFirst){
			bar.mTvRight.setVisibility(View.VISIBLE);
			bar.setCallBack(new Callback() {
				@Override
				public void onClick(View view) {
					switch (view.getId()) {
					case R.id.top_right_tv:
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(intent);
						finish();
						break;
					}
				}
			});
		}
	}
	
	@OnClick({R.id.login_btn, R.id.register_btn, R.id.tv_findpassword})
	public void onlick(View view) {

		if(Tools.checkNetWorkConnect(getApplicationContext()).equals(Tools.UNKNOW_CONNECT)) {
			Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
			return;
		}
		switch (view.getId()) {
		case R.id.login_btn:
			String account = etAccount.getText().toString().trim();
			String pwd = etPwd.getText().toString().trim();

			pswd = pwd;
			if(TextUtils.isEmpty(account) || !Tools.isPhone(account)) {
				etAccount.setError("请正确输入手机号");
				etAccount.setShakeAnimation(); //默认5秒晃动的动画
				//重新获取焦点
				etAccount.setFocusable(true);
				etAccount.requestFocus();
				return;
			}
			
			if(TextUtils.isEmpty(pwd)) {
				etPwd.setError("请正确输入密码");
				etPwd.setShakeAnimation();
				etPwd.setFocusable(true);
				etPwd.requestFocus();
				return;
			}
			String md5str = Tools.encodeMD5(pwd);
			new LoginAsyncTask().execute(account, md5str);
			break;
		case R.id.register_btn:
			Intent intent = new Intent(this, RegisterActivity.class);
			intent.putExtra("type", "register");
			startActivity(intent);
			break;
		case R.id.tv_findpassword:
			intent = new Intent(this, RegisterActivity.class);
			intent.putExtra("type", "reset");
			startActivity(intent);
			break;

		}
	}
	
	/**
	 * 发送请求到服务端 申请登录
	 */
	class LoginAsyncTask extends AsyncTask<String, Void, String> {
		HttpClient httpClient = HttpClient.getInstance();
		LoginController loginController = LoginController.getInstance();
		LoginPacket mPacket = new LoginPacket();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			refreshDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			mPacket.setUsername(params[0]);
			mPacket.setPwd(params[1]);
			loginController.execute(mPacket);
			return httpClient.postRequest(BaseConfig.LOGIN_URL, mPacket);
		}
		
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			refreshDialog.cancel();
			BasePacket basePacket = loginController.execute(result);
			System.out.println("basePacket = " + basePacket.getBody());
			if(!basePacket.isActionState()){
				Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
				return ;
			}
			mPacket.setBody(basePacket.getBody());
			String userId = mPacket.getUserId();
			String username = mPacket.getUsername();
			if(TextUtils.isEmpty(userId) || TextUtils.isEmpty(username)) {
				Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
				return ;
			}
			Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
			
			//登录成功缓存数据到本地
			msp.edit()
			.putBoolean("isLogin", true)
			.putString("userId", userId)
			.putString("username", username)
			.putString("phone", username)
			.putString("password", pswd)
			.commit();
			if(!Tools.serviceIsRunning(getApplicationContext(), TCPService.SRVICE_NAME)){
				startService(new Intent(LoginActivity.this, TCPService.class));
			}
			if(isFirst) {
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}else {
				if(TextUtils.isEmpty(from)) {
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					AppManager.getAppManager().finishActivity(LoginActivity.this);
					finish();
				}else if("BookInfoDetiActivity".equals(from)){
					AppManager.getAppManager().finishActivity(LoginActivity.this);
					finish();
				}
			}
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		String username = msp.getString("username", null);
		String password = msp.getString("password", null);
		etAccount.setText(username);
		etPwd.setText(password);
	}
}
