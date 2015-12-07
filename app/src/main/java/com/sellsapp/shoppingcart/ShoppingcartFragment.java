package com.sellsapp.shoppingcart;

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
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
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
import com.sellsapp.utils.Tools;

//购物车的fragment
public class ShoppingcartFragment extends Fragment implements IXListViewListener, OnItemLongClickListener, OnItemClickListener {
	private List<ShoppingInfo> infos = null;
	private ShopAdapter adapter;
	private XListView listview;
	
	private LinearLayout ll;
	
	private ImageView ivno;

	private int size = 20;

	private int page = 1;

	private int pagesum;

	private RefreshDialog refreshDialog;

	private HttpClient httpClient = HttpClient.getInstance();
	
	private SharedPreferences sp;

	private String userId;
	@ ViewInject(R.id.bar)
	private TopBarView bar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.shoppingcart_fragment, null);
		init(view);
		return view;
	}

	// 需要实现功能 的代码全部写在这里面
	private void init(View view) {
		ViewUtils.inject(this, view);
		bar.mTvBack.setVisibility(View.GONE);
		bar.setTitle("购物车");
		ll = (LinearLayout) view.findViewById(R.id.ll);
		ivno = (ImageView) view.findViewById(R.id.no);
		listview = (XListView) view.findViewById(R.id.shop_listView);
		initData();
	}

	private void initData() {
		listview.setDividerHeight(0);
		listview.setPullLoadEnable(true);// 控制下拉刷新
		listview.setXListViewListener(this);
		listview.setOnItemLongClickListener(this);
		listview.setOnItemClickListener(this);
		refreshDialog = new RefreshDialog(getActivity());
		
		infos = new ArrayList<ShoppingInfo>();
		adapter = new ShopAdapter(getActivity(), infos);
		listview.setAdapter(adapter);
		page = 1;
		sp = getActivity()
				.getSharedPreferences("SellApp", Context.MODE_PRIVATE);
		userId = sp.getString("userId", null);
		if(Tools.checkNetWorkConnect(getActivity()).equals(Tools.UNKNOW_CONNECT)) {
			ivno.setVisibility(View.VISIBLE);
			ivno.setImageResource(R.drawable.no_param);
			listview.setVisibility(View.GONE);
			ll.setVisibility(View.GONE);
			return;
		}
		if (TextUtils.isEmpty(userId)) {
			ll.setVisibility(View.GONE);
			ivno.setVisibility(View.VISIBLE);
			ivno.setImageResource(R.drawable.no_login);
			listview.setVisibility(View.GONE);
		}else {
			ivno.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
			ll.setVisibility(View.VISIBLE);
			new GetCartAsyncTask().execute(userId);
		}
	}
	

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		return false;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), CartItemActivity.class);
		ShoppingInfo shoppingInfo = (ShoppingInfo) parent.getItemAtPosition(position);
		intent.putExtra("info", shoppingInfo);
		startActivityForResult(intent, 0);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("re = " + requestCode + ", res = " + resultCode);
		if(resultCode != Activity.RESULT_OK) {
			return;
		}
		if(requestCode == 0) {
			new GetCartAsyncTask().execute(userId);
		}
	}

	private void onLoad() {
		listview.stopRefresh();
		listview.stopLoadMore();
		listview.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd hh:mm",
				Locale.CHINESE).format(new Date()));
	}
	@Override
	public void onRefresh() {
		page = 1;
		new GetCartAsyncTask().execute(userId);
	}

	@Override
	public void onLoadMore() {
		if (page < pagesum) {
			page++;
			refreshDialog.cancel();
			new GetCartAsyncTask().execute(userId);
		} else {
			onLoad();
			Toast.makeText(getActivity(), getResources().getString(R.string.end_page),
					Toast.LENGTH_SHORT).show();
		}
	}
	
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
			return httpClient.postRequest(BaseConfig.SHOPPING_CART_URL, mPacket);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			onLoad();
			BasePacket basePacket = shoppingCartController.execute(result);
			if(!basePacket.isActionState()) {
				ll.setVisibility(View.GONE);
				ivno.setImageResource(R.drawable.no_param);
				ivno.setVisibility(View.VISIBLE);
				pagesum = 0;
				infos.removeAll(infos);
				adapter.refresh(infos);
				return;
			}
			mPacket.setBody(basePacket.getBody());
			pagesum = Integer.valueOf(mPacket.getPageSum());
			infos = mPacket.getShoppingCart();
			if(null == infos || infos.isEmpty()) {
				ll.setVisibility(View.GONE);
				ivno.setImageResource(R.drawable.no_param);
				ivno.setVisibility(View.VISIBLE);
				pagesum  = 0;
				return;
			}
			ll.setVisibility(View.VISIBLE);
			ivno.setVisibility(View.GONE);
			adapter.refresh(infos);
		}
	}

}
