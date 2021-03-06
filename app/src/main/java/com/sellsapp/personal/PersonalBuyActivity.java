package com.sellsapp.personal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sellsapp.R;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.basic.widget.RefreshDialog;
import com.sellsapp.basic.widget.TopBarView;
import com.sellsapp.basic.widget.XListView;
import com.sellsapp.basic.widget.XListView.IXListViewListener;
import com.sellsapp.controllers.ShoppingCartController;
import com.sellsapp.models.BasePacket;
import com.sellsapp.models.ShoppingCartPacket;
import com.sellsapp.models.ShoppingInfo;
import com.sellsapp.net.HttpClient;
import com.sellsapp.shoppingcart.ShopAdapter;
import com.sellsapp.utils.AppManager;
import com.sellsapp.utils.Tools;

/**
 * 我是买家页面
 */
public class PersonalBuyActivity extends Activity implements IXListViewListener, OnItemLongClickListener, OnItemClickListener {

	private List<ShoppingInfo> infos = null; //订单信息集合
	private ShopAdapter adapter; //适配器
	private XListView listview; 

	private int size = 20; //每页20条数据

	private int page = 1; //当前的页码

	private int pagesum; //总页数

	private RefreshDialog refreshDialog; //加载提示框

	private HttpClient httpClient = HttpClient.getInstance();
	
	private SharedPreferences sp;
	
	private LinearLayout ll;

	private String userId;
	@ ViewInject(R.id.bar)
	private TopBarView bar; //titlebar
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_personal_buy);
		initView();
	}

	private void initView() {
		ViewUtils.inject(this);
		AppManager.getAppManager().addActivity(this);
		
		bar.setTitle("我买的书");
		ll = (LinearLayout) findViewById(R.id.ll);
		listview = (XListView) findViewById(R.id.shop_listView);
		listview.setDividerHeight(0);
		listview.setPullLoadEnable(true);// 控制下拉刷新
		listview.setXListViewListener(this);
		listview.setOnItemLongClickListener(this);
		listview.setOnItemClickListener(this);
		refreshDialog = new RefreshDialog(this);
		
		//预先加载adapter,之后获取到数据后刷新adapter
		infos = new ArrayList<ShoppingInfo>();
		adapter = new ShopAdapter(this, infos);
		listview.setAdapter(adapter);
		
		page = 1;
		sp = getSharedPreferences("SellApp", Context.MODE_PRIVATE);
		userId = sp.getString("userId", null);
		//根据网络和是否登陆判断显示哪些布局
		ll.setVisibility(View.GONE);
		if(Tools.checkNetWorkConnect(this).equals(Tools.UNKNOW_CONNECT)) {
			ll.setVisibility(View.GONE);
			return;
		}
		if (TextUtils.isEmpty(userId)) {
			ll.setVisibility(View.GONE);
		}else if (!TextUtils.isEmpty(userId)) { //满足条件获取订单信息
			ll.setVisibility(View.VISIBLE);
		}	new GetCartAsyncTask().execute(userId);
		
	}
	
	//加载中
	private void onLoad() {
		listview.stopRefresh();
		listview.stopLoadMore();
		listview.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd hh:mm",
				Locale.CHINESE).format(new Date()));
	}
	//下拉刷新
	@Override
	public void onRefresh() {
		page = 1;
		new GetCartAsyncTask().execute(userId);
	}

	
	//上拉加载更多
	@Override
	public void onLoadMore() {
		if (page < pagesum) {
			page++;
			refreshDialog.cancel();
			new GetCartAsyncTask().execute(userId);
		} else {
			onLoad();
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.end_page),
					Toast.LENGTH_SHORT).show();
		}
	}
	
	//获取订单
	public class GetCartAsyncTask extends AsyncTask<String, Void, String>{
		ShoppingCartController shoppingCartController = ShoppingCartController.getInstance();
		ShoppingCartPacket mPacket = new ShoppingCartPacket();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			mPacket.setPage(page);
			mPacket.setSize(size);
			mPacket.setUserId(params[0]);
			shoppingCartController.execute(mPacket);
			return httpClient.postRequest(BaseConfig.BUYERINFO_URL, mPacket);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			onLoad();
			BasePacket basePacket = shoppingCartController.execute(result);
			if(!basePacket.isActionState()) {
				ll.setVisibility(View.GONE);
				pagesum = 0;
				infos.removeAll(infos);
				adapter.refresh(infos);
				Toast.makeText(getApplicationContext(), basePacket.getActionMessage(), Toast.LENGTH_SHORT).show();
				return;
			}
			mPacket.setBody(basePacket.getBody());
			pagesum = Integer.valueOf(mPacket.getPageSum());
			infos = mPacket.getShoppingCart();
			if(null == infos || infos.isEmpty()) {
				ll.setVisibility(View.GONE);
				pagesum  = 0;
				return;
			}
			ll.setVisibility(View.VISIBLE);
			adapter.refresh(infos);
		}
	}
	
	//更新界面
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != Activity.RESULT_OK) {
			return;
		}
		if(requestCode == 0) {
			new GetCartAsyncTask().execute(userId);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, PDeitActivity.class);
		ShoppingInfo shoppingInfo = (ShoppingInfo) parent.getItemAtPosition(position);
		intent.putExtra("info", shoppingInfo);
		startActivityForResult(intent, 0);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		return false;
	}
}
