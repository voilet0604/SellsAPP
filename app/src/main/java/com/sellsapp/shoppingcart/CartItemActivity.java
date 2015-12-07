package com.sellsapp.shoppingcart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
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
import com.sellsapp.personal.LoginActivity;
import com.sellsapp.utils.AppManager;

/**
 * 购物车详情页面
 *
 */
public class CartItemActivity extends Activity {

	private ShoppingInfo info;
	
	@ViewInject(R.id.btn_buy)
	private Button btnBuy;
	
	@ViewInject(R.id.btn_delete)
	private Button btnDe;
	
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
	
	@ViewInject(R.id.bar)
	private TopBarView bar;
	
	@ViewInject(R.id.tv_chat)
	private TextView tvChat;
	
	private SharedPreferences sp;
	
	private String id;
	private HttpClient httpClient = HttpClient.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cart_deit);
		//购物车列表页面传递过来的订单信息
		info = (ShoppingInfo) getIntent().getSerializableExtra("info");
		ViewUtils.inject(this);
		sp = getSharedPreferences("SellApp", MODE_PRIVATE);
		AppManager.getAppManager().addActivity(this);
		bar.setTitle("订单");
		init();
	}
	
	private void init() {
		if(null != info) {
			tvName.setText(info.getBookname());
			tvPh.setText(info.getPublish());
			tvPic.setText(info.getPrice()+"元");
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
				BitmapManager.getBitmapUtils(this).display(ivImg, imgUrl, new BitmapLoadCallBack<ImageView>() {

					@Override
					public void onLoadCompleted(ImageView arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
						arg0.setImageBitmap(arg2);
					}

					@Override
					public void onLoadFailed(ImageView arg0, String arg1, Drawable arg2) {
						arg0.setImageResource(R.drawable.no_photo);
					}
				});
			}
		}
		
	}

	@OnClick({R.id.btn_buy, R.id.btn_delete,R.id.tv_chat})
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.btn_buy:
			upadate("2");
			break;
		case R.id.btn_delete:
			upadate("1");
			break;
		case R.id.tv_chat:
			String sellphone = info.getSellphone();
			String username = sp.getString("username", null);
			System.out.println(""+sellphone +", " + username);
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
		}
	}
	
	//更改订单状态
	private void upadate(final String state) {
		AlertDialog.Builder builder = new Builder(this,3);
		if("1".equals(state)) {
			builder.setTitle("确定从购物车中移除吗？");
		}else if("2".equals(state)) {
				builder.setTitle("确认下单？");
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
