package com.sellsapp.home;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterViewFlipper;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sellsapp.R;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.controllers.ADController;
import com.sellsapp.controllers.ListBookController;
import com.sellsapp.models.ADPakcet;
import com.sellsapp.models.BasePacket;
import com.sellsapp.models.BookInfo;
import com.sellsapp.models.ListBookPacket;
import com.sellsapp.net.HttpClient;
import com.sellsapp.personal.PersonalSettingActivity;
import com.sellsapp.service.UpAppService;
import com.sellsapp.utils.Tools;

//首页的fragment
public class HomeFragment extends Fragment implements OnClickListener, OnItemClickListener{

	private ImageView ivSetting;
	private TextView tvTitle;
	private ImageView ivSearch;
	
	private ImageView ivBuy;
	private ImageView ivSell;
	
	private ImageView ivForward1;
	private ListView lvNewbook;
	
	private AdapterViewFlipper adapterViewFlipper;
	
	private SamllAdapter bookInfoAdapter;
	
	private List<BookInfo> bookInfos;
	
	//书籍数量
	private int bookinfocount = 4;
	
	private List<String> adUrls; //广告地址
	
	private SharedPreferences sp;
	
	//自定义的httpClient
	private HttpClient httpClient = HttpClient.getInstance();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.home_fragment,null);
		init(view);
		return view;
	}

	//需要实现的代码写在这里面 
	private void init(View view) {
		
		sp = getActivity().getSharedPreferences("SellApp", Context.MODE_PRIVATE);
		initView(view);
		setOnClickListener();
		//判断是否有网络
		//之后可以考虑使用广播的方式提示用户是否有网络
		if(Tools.checkNetWorkConnect(getActivity()).equals(Tools.UNKNOW_CONNECT)) {
			Toast.makeText(getActivity(), "世界上最遥远的距离是，我在你面前，你看不到我，请检查网络连接!!!", Toast.LENGTH_LONG).show();
			return;
		}
		//开启异步请求获取书籍信息
		new GetBookInfoTask().execute(BaseConfig.CMD_GET_NEW_BOOK);
}
	
	private void initView(View view) {
		ivSetting = (ImageView) view.findViewById(R.id.iv_setting);
		tvTitle = (TextView) view.findViewById(R.id.tv_home_title);
		ivSearch = (ImageView) view.findViewById(R.id.iv_search);
		
		
		ivBuy = (ImageView) view.findViewById(R.id.iv_home_buy);
		ivSell = (ImageView) view.findViewById(R.id.iv_home_sell);
		
		ivForward1 = (ImageView) view.findViewById(R.id.iv_home_forward1);
		lvNewbook = (ListView) view.findViewById(R.id.lv_home_newbook);
		lvNewbook.setOnItemClickListener(this);
		adapterViewFlipper = (AdapterViewFlipper) view.findViewById(R.id.avf_ad);
		
		List<Integer> list = new ArrayList<Integer>();
		list.add(R.drawable.ad01);
		list.add(R.drawable.ad02);
		list.add(R.drawable.ad03);
		adapterViewFlipper.setAdapter(new ADAdapter(getActivity(), list, null));
//		adapterViewFlipper.setInAnimation(getActivity(), R.anim.ob);
//		adapterViewFlipper.setOutAnimation(getActivity(), R.anim.oc);
		adapterViewFlipper.startFlipping();
		
		bookInfos = new ArrayList<BookInfo>();
		bookInfoAdapter = new SamllAdapter(getActivity(), bookInfos);
		lvNewbook.setAdapter(bookInfoAdapter);
		updateAppVersion();
	}
	


	private void setOnClickListener(){
		ivSetting.setOnClickListener(this);
		ivSearch.setOnClickListener(this);
		ivBuy.setOnClickListener(this);
		ivSell.setOnClickListener(this);
		ivForward1.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.iv_setting: //设置按钮，预留跳转到设置页面
			boolean isLogin= sp.getBoolean("isLogin", false);
		
			if(isLogin){
				Intent intent = new Intent(getActivity(),PersonalSettingActivity.class);	
				startActivity(intent);
			}else {
				Toast.makeText(getActivity(), "设置，请先登录用户",  Toast.LENGTH_SHORT).show();
			}
			
			break;
		case R.id.iv_search: //搜索功能
			Intent intent = new Intent(getActivity(), SearchActivity.class);
			startActivity(intent);
			break;

		case R.id.iv_home_buy: //我要买书
			Intent buy = new Intent(getActivity(), BuyBookActivity.class);
			startActivity(buy);
			break;
		case R.id.iv_home_sell: //我要卖书
			Intent sell = new Intent(getActivity(), SellBookActivity.class);
			startActivity(sell);
			break;
		case R.id.iv_home_forward1: //跳转到新书列表
			Intent new_search = new Intent(getActivity(), SearchActivity.class);
			new_search.putExtra("type", "newbook");
			startActivity(new_search);
			break;
		}
		
	}
	
	//获取书籍信息
	class GetBookInfoTask extends AsyncTask<String, Void, String> {
		ListBookController listBookController = ListBookController.getInstance();
		ListBookPacket mPacket = new ListBookPacket();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			mPacket.setCmd(params[0]);
			mPacket.setSize(bookinfocount);
			mPacket.setPage(1);
			listBookController.execute(mPacket);
			return httpClient.postRequest(BaseConfig.BOOKINF_URL, mPacket);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			BasePacket basePacket = listBookController.execute(result);
			if(basePacket.isActionState()) {
				mPacket.setBody(basePacket.getBody());
				
				List<BookInfo> bookInfos = mPacket.getBookInfos();
				if(null != bookInfos && bookInfos.size() > 0) {
					bookInfoAdapter.refresh(bookInfos);
				}
				
			}
		}
	}
	
	//获取广告图片
	class GetADTask extends AsyncTask<Void, Void, String> {
		private ADController adController = ADController.getInstance();
		private ADPakcet mPacket = new ADPakcet(BaseConfig.CMD_GET_AD_IMAGE);
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			adController.execute(mPacket);
		}
		@Override
		protected String doInBackground(Void... params) {
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
		
	}

	//点击跳转到详情页面
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), BuyBookActivity.class);
		startActivity(intent);
	}

	//开始更新app的service
	private void updateAppVersion() {
		String state = Tools.checkNetWorkConnect(getActivity());
		if (TextUtils.isEmpty(state)
				|| Tools.UNKNOW_CONNECT.equals(state)) {
		}else {
			Intent intent = new Intent(getActivity(), UpAppService.class);
			intent.putExtra("action", BaseConfig.UPDATE_APP);
			intent.addFlags(Service.START_STICKY_COMPATIBILITY);
			getActivity().startService(intent);
		}
		
	}

}
