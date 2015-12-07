package com.sellsapp.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
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
import com.sellsapp.models.BookInfo;
import com.sellsapp.models.ShoppingCartPacket;
import com.sellsapp.net.HttpClient;
import com.sellsapp.personal.LoginActivity;
import com.sellsapp.utils.AppManager;
import com.sellsapp.utils.Tools;

/**
 * 书籍页面详细activity
 */
@SuppressLint("NewApi") public class BookInfoDetiActivity extends Activity {

	/**
	 * 这里的注解xutils提供，方便开发 ，可以不用findviewbyid
	 */
	@ViewInject(R.id.btn_add)
	private Button btnBuy;

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
	
	private BookInfo bookInfo;
	

	private HttpClient httpClient = HttpClient.getInstance();
	
	private SharedPreferences sp;

	private String id;
	
	private String userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_book_info_deti);
		//使用xutils框架的注解必须先调用这个方法，初始化
		ViewUtils.inject(this);
		bar.setTitle(getResources().getString(R.string.buy_book));
		//结合TopViewBar 使用,把当前activity添加到栈中，退出从栈中销毁
		AppManager.getAppManager().addActivity(this);
		
		Bundle bundle = getIntent().getExtras();
		//获取上一个页面传过来的bookinfo
		bookInfo = (BookInfo) bundle.getSerializable("bookinfo");
		//获取缓存文件对象
		sp = getSharedPreferences("SellApp", MODE_PRIVATE);
		if(null != bookInfo) {
			bar.setTitle(bookInfo.getBookname());
			id = bookInfo.getId();
			tvName.setText(bookInfo.getBookname());
			tvPh.setText(bookInfo.getPublish());
			tvPic.setText(bookInfo.getPrice());
			tvSendway.setText(bookInfo.getSendway());
			tvVersion.setText(bookInfo.getVersion());
			tvBeizhu.setText(bookInfo.getBeizhu());
			String imgUrl = bookInfo.getImg();
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

	//添加购物车的监听  xutils框架提供的注释监听
	@OnClick({R.id.btn_add, R.id.tv_chat})
	public void addShoppingcart(View view) {
		
		switch (view.getId()) {
		case R.id.btn_add:
			upadate("-2");
			break;
		case R.id.tv_chat:
			String sellphone = bookInfo.getSellphone();
			String username = sp.getString("username", null);
			if(sellphone.equals(username)) {
				Toast.makeText(getApplicationContext(), "无法发起和自己的聊天！！", Toast.LENGTH_SHORT).show();
				return;
			}
			boolean isLogin = sp.getBoolean("isLogin", false);
			if(isLogin) {
				//跳转到聊天页面
				Intent intent = new Intent(this, ChatMessageActivity.class);
				intent.putExtra("receiver", bookInfo.getSellphone());
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

	/**
	 * 修改购物车信息的状态 
	 * @param state -2 代表下单
	 */
	private void upadate(final String state) {
		AlertDialog.Builder builder = new Builder(this,3);
		builder.setTitle("您确定要添加到购物车？");
		builder.setNegativeButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(Tools.checkNetWorkConnect(getApplicationContext()).equals(Tools.UNKNOW_CONNECT)) {
					Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
					return;
				}
				String userId = sp.getString("userId", null);
				if(TextUtils.isEmpty(userId)) {
					Toast.makeText(getApplicationContext(), "请先登录", Toast.LENGTH_SHORT).show();
					return;
				}
				new upadteCartAsync().execute(state);
			}
		});
		builder.setPositiveButton("取消", null);
		
		builder.create().show();
	}
	
	//开启异步请求访问网络
	class upadteCartAsync extends AsyncTask<String, Void, String> {
		
		ShoppingCartController shoppingCartController = ShoppingCartController.getInstance();
		ShoppingCartPacket mPacket = new ShoppingCartPacket();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			userId = sp.getString("userId", null);
			mPacket.setUserId(userId);
			mPacket.setState(params[0]);
			mPacket.setId(id);
			shoppingCartController.execute(mPacket);
			return httpClient.postRequest(BaseConfig.ADD_CART_URL, mPacket);
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
