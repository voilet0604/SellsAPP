package com.sellsapp.personal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sellsapp.R;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.Public.BitmapManager;
import com.sellsapp.basic.widget.TopBarView;
import com.sellsapp.controllers.ShoppingCartController;
import com.sellsapp.message.chat.ChatMessageActivity;
import com.sellsapp.models.BasePacket;
import com.sellsapp.models.ShoppingCartPacket;
import com.sellsapp.models.ShoppingInfo;
import com.sellsapp.net.HttpClient;
import com.sellsapp.utils.AppManager;
import com.sellsapp.utils.Tools;

/**
 * 我是卖家的详情页面
 */
public class SellDeitActivity extends Activity {

	private ShoppingInfo info;
	
	@ViewInject(R.id.btn_send)
	private Button btnBuy;
	
	@ViewInject(R.id.btn_can)
	private Button btnCan;
	
	@ViewInject(R.id.iv_img)
	private ImageView ivImg;
	
	@ViewInject(R.id.tv_bookname)
	private TextView tvName;
	
	@ViewInject(R.id.tv_push)
	private TextView tvPh;
	
	@ViewInject(R.id.tv_pic)
	private TextView tvPic;
	
	@ViewInject(R.id.tv_sendway)
	private TextView tvSendway;
	
	@ViewInject(R.id.tv_version)
	private TextView tvVersion;
	
	@ViewInject(R.id.tv_beizhu)
	private TextView tvBeizhu;

	@ViewInject(R.id.tv_phone)
	private TextView tvPhone;
	
	@ViewInject(R.id.bar)
	private TopBarView bar;
	
	@ViewInject(R.id.tv_state)
	private TextView tvSate;
	
	@ViewInject(R.id.tv_chat)
	private TextView tvChat;
	
	private BitmapUtils bUtlis ;
	
	private SharedPreferences sp;
	
	private String id;
	private HttpClient httpClient = HttpClient.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sell_deit);
		info = (ShoppingInfo) getIntent().getSerializableExtra("info");
		bUtlis = BitmapManager.getBitmapUtils(this);
		sp = getSharedPreferences("SellApp", MODE_PRIVATE);
		ViewUtils.inject(this);
		AppManager.getAppManager().addActivity(this);
		init();
	}
	
	private void init() {
		if(null != info) {
			bar.setTitle(info.getBookname());
			btnBuy.setVisibility(View.GONE);
			btnCan.setVisibility(View.GONE);
			if("2".equals(info.getState())) {
				btnBuy.setVisibility(View.VISIBLE);
				btnCan.setVisibility(View.VISIBLE);
			}
			String state = null;
			
			//根据state的不同页面显示不同的内容
			if("2".equals(info.getState())){
				state = "等待发货";
			}else if("3".equals(info.getState())) {
				state = "交易完成";
			}else if("4".equals(info.getState())) {
				state= "卖家取消";
			}else if("5".equals(info.getState())) {
				state = "买家取消";
			}else if("-1".equals(info.getState())) {
				state = "审核未通过";
			}else if("1".equals(info.getState())) {
				state = "审核通过";
			}else  {
				state = "审核中";
			}
			tvPhone.setText(info.getBuyphone());
			tvName.setText(info.getBookname());
			tvPh.setText(info.getPublish());
			tvPic.setText(info.getPrice()+"元");
			tvSate.setText(state);
			tvSendway.setText(info.getSendway());
			tvBeizhu.setText(info.getBeizhu());
			tvVersion.setText(info.getVersion());
			id = info.getId();
			String imgUrl  = info.getImg();
			if(TextUtils.isEmpty(imgUrl)) {
				ivImg.setImageResource(R.drawable.no_photo);
			}else {
				if(!imgUrl.contains("http")) {
					imgUrl = BaseConfig.BASE_SERVICE_URL+imgUrl;
				}
				//XUtils框架中BitmapUtils 提供的方法网络图片的方法
				BitmapManager.getBitmapUtils(this).display(ivImg, imgUrl, new BitmapLoadCallBack<ImageView>() {

					//加载成功，获取的bitmap对象显示imageview上面
					@Override
					public void onLoadCompleted(ImageView arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
						arg0.setImageBitmap(arg2);
					}

					//加载失败显示默认图片
					@Override
					public void onLoadFailed(ImageView arg0, String arg1, Drawable arg2) {
						arg0.setImageResource(R.drawable.no_photo);
					}
				});
			}
		}
		
	}

	@OnClick({R.id.btn_send, R.id.btn_can, R.id.tv_chat, R.id.tv_phone})
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.btn_ok:
			upadate("3");
			break;
		case R.id.btn_can:
			upadate("4");
			break;
		case R.id.tv_chat:
			String sellphone = info.getSellphone();
			String username = sp.getString("username", null);
			if(sellphone.equals(username)) {
				Toast.makeText(getApplicationContext(), "无法发起和自己的聊天！！", Toast.LENGTH_SHORT).show();
				return;
			}
			boolean isLogin = sp.getBoolean("isLogin", false);
			if(isLogin) {
				//跳转到聊天页面
				Intent intent = new Intent(this, ChatMessageActivity.class);
				intent.putExtra("receiver", info.getSellphone());
				startActivity(intent);
			}else {
				Toast.makeText(getApplicationContext(), "请先登录！！", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(this, LoginActivity.class);
				intent.putExtra("from", "BookInfoDetiActivity");
				startActivity(intent);
			}
			break;
		case R.id.tv_phone:
			Tools.callPhone(this, info.getBuyphone());
			break;
		}
	}
	
	
	private void upadate(final String state) {
		AlertDialog.Builder builder = new Builder(this,3);
		if("5".equals(builder)) {
			builder.setTitle("您确定要取消订单吗？");
		}else {
			builder.setTitle("请确定书籍已发出并且收到货款");
		}
		builder.setNegativeButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new upadteCartAsync().execute(state);
			}
		});
		builder.setPositiveButton("取消", null);
		
		builder.create().show();
	}
	
	class upadteCartAsync extends AsyncTask<String, Void, String> {
		
		ShoppingCartController shoppingCartController = ShoppingCartController.getInstance();
		ShoppingCartPacket mPacket = new ShoppingCartPacket();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			mPacket.setState(params[0]);
			mPacket.setId(id);
			shoppingCartController.execute(mPacket);
			return httpClient.postRequest(BaseConfig.CAR_URL, mPacket);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			BasePacket basePacket = shoppingCartController.execute(result);
			if(basePacket.isActionState()) {
				setResult(RESULT_OK);
				finish();
			}
		}
		
	}
	
}
