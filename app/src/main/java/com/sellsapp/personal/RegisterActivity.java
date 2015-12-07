package com.sellsapp.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sellsapp.R;
import com.sellsapp.basic.widget.ClearEditText;
import com.sellsapp.basic.widget.RefreshDialog;
import com.sellsapp.basic.widget.SendValidateButton;
import com.sellsapp.basic.widget.TopBarView;
import com.sellsapp.controllers.CodeController;
import com.sellsapp.controllers.RegisterController;
import com.sellsapp.models.BasePacket;
import com.sellsapp.models.CodePacket;
import com.sellsapp.models.RegisterPacket;
import com.sellsapp.net.HttpClient;
import com.sellsapp.net.HttpRequest;
import com.sellsapp.utils.AppManager;
import com.sellsapp.utils.Tools;

/**
 * 注册页面
 */
public class RegisterActivity extends Activity {
  
	@ViewInject(R.id.bar)
	private TopBarView bar;
	
	@ViewInject(R.id.username_cet)
	private ClearEditText etPhone;
	
	@ViewInject(R.id.password_cet)
	private ClearEditText etPwd;
	
	@ViewInject(R.id.confirmpassword_cet)
	private ClearEditText etConfirm;
	
	@ViewInject(R.id.register_code)
	private ClearEditText etCode;
	
	@ViewInject(R.id.getcode_btn)
	private SendValidateButton btnCode;

	@ViewInject(R.id.btn_register)
	private Button btnRegister;

	private String type;
	private RefreshDialog refreshDialog;
	
	private HttpClient httpClient = HttpClient.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ViewUtils.inject(this);
		
		refreshDialog  = new RefreshDialog(this);
		AppManager.getAppManager().addActivity(this);
		etPhone.addTextChangedListener(new MyTextWatacher());
		
		type = getIntent().getStringExtra("type");
		if("register".equals(type)) {
			bar.setTitle(getResources().getString(R.string.register));
			btnRegister.setText("注册");
		}else {
			bar.setTitle("重置密码");
			btnRegister.setText("重置密码");
		}
	}
	
	class MyTextWatacher implements TextWatcher{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			//手机号满足要求的时候，按钮为可点击
			if(Tools.isPhone(s.toString())) {
				btnCode.setEnabled(true);
			}else {
				btnCode.setEnabled(false);
			}
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			
			
		}
		
	}
	
	@OnClick({R.id.getcode_btn, R.id.btn_register})
	public void onclick(View view) {

		if(Tools.checkNetWorkConnect(getApplicationContext()).equals(Tools.UNKNOW_CONNECT)) {
			Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
			return;
		}
		switch (view.getId()) {
		case R.id.getcode_btn:
			String phone = etPhone.getText().toString().trim();
			if(TextUtils.isEmpty(phone) || !Tools.isPhone(phone)) {
				etPhone.setError("请正确输入手机号");
				etPhone.setShakeAnimation(); //默认5秒晃动的动画
				//重新获取焦点
				etPhone.setFocusable(true);
				etPhone.requestFocus();
				return;
			}
			new GetCodeAsyncTask().execute(phone);
			break;

		case R.id.btn_register:
			phone = etPhone.getText().toString().trim();
			String password1 = etPwd.getText().toString().trim();
			String confrim = etConfirm.getText().toString().trim();
			String code = etCode.getText().toString().trim();
			if(TextUtils.isEmpty(phone) || !Tools.isPhone(phone)) {
				etPhone.setError("请正确输入手机号");
				etPhone.setShakeAnimation(); //默认5秒晃动的动画
				//重新获取焦点
				etPhone.setFocusable(true);
				etPhone.requestFocus();
				return;
			}
			if(TextUtils.isEmpty(password1) || password1.length() < 6) {
				etPwd.setError("密码必须填写正确");
				etPwd.setShakeAnimation();
				etPwd.setFocusable(true);
				etPhone.requestFocus();
				return;
			}
			if(!password1.equals(confrim)) {
				etConfirm.setError("两次密码不一致");
				etConfirm.setShakeAnimation();
				etConfirm.setFocusable(true);
				etConfirm.requestFocus();
				return;
			}
			if(TextUtils.isEmpty(code)) {
				etCode.setError("正确填写验证码");
				etCode.setShakeAnimation();
				etCode.setFocusable(true);
				etConfirm.requestFocus();
				return;
			}
			String md5Str = Tools.encodeMD5(password1);
			new RegisterAsyncTask().execute(phone, md5Str, code);
			break;
		}
	}
	
	
	class RegisterAsyncTask extends AsyncTask<String, Void, String> {
		RegisterController registerController = RegisterController.getInstance();
		RegisterPacket mPacket = new RegisterPacket();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			refreshDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			mPacket.setPhone(params[0]); //手机号
			mPacket.setPwd(params[1]); //密码
			mPacket.setCode(params[2]); //验证码
			registerController.execute(mPacket);
			return HttpRequest.sendSinginData(params[0], params[1], params[2], type);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			refreshDialog.dismiss();
			BasePacket basePacket = registerController.execute(result);
			if(!basePacket.isActionState()) {
				Toast.makeText(getApplicationContext(),basePacket.getActionMessage(), Toast.LENGTH_SHORT).show();
				return;
			}
			Toast.makeText(getApplicationContext(), "注册成功,请登录", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
			AppManager.getAppManager().finishActivity();
		}
		
	}
	
	//获取验证码
	class GetCodeAsyncTask extends AsyncTask<String, Void, String> {

		CodeController codeController = CodeController.getInstance();
		
		CodePacket mPacket = new CodePacket();
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
//			mPacket.setPhone(params[0]);
//			codeController.execute(mPacket);
			HttpRequest.getVerycode(params[0]);
			return null;
		}
		
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
//			BasePacket basePacket = codeController.execute(result);
//			if(!basePacket.isActionState()) {
//				Toast.makeText(getApplicationContext(), "获取验证码失败", Toast.LENGTH_SHORT).show();
//				return;
//			}
			btnCode.setEnabled(false);
		}
	}

}
