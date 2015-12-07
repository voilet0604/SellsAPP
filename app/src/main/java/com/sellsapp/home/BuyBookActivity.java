package com.sellsapp.home;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sellsapp.R;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.basic.widget.RefreshDialog;
import com.sellsapp.basic.widget.TopBarView;
import com.sellsapp.basic.widget.TopBarView.Callback;
import com.sellsapp.basic.widget.XListView;
import com.sellsapp.basic.widget.XListView.IXListViewListener;
import com.sellsapp.controllers.ListBookController;
import com.sellsapp.models.BasePacket;
import com.sellsapp.models.BookInfo;
import com.sellsapp.models.ListBookPacket;
import com.sellsapp.net.HttpClient;
import com.sellsapp.utils.AppManager;

//列出书籍列表
public class BuyBookActivity extends Activity implements OnItemClickListener, IXListViewListener {

	private XListView lvBook;
	private BookInfoAdapter adapter;
	private List<BookInfo> books;
	private RefreshDialog refreshDialog;
	private int size = 20;
	
	private int page =1;
	
	private int pagesum;
	
	@ViewInject(R.id.bar)
	private TopBarView bar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_buy_book);
		initView();
	}
	
	private void initView() {
		lvBook = (XListView) findViewById(R.id.lv_buy_list);
		ViewUtils.inject(this);
		AppManager.getAppManager().addActivity(this);

		bar.setTitle("书籍列表");
		bar.mTvRight.setText("搜索");
		bar.mTvRight.setVisibility(View.VISIBLE);
		
		lvBook.setDividerHeight(0);
		lvBook.setPullLoadEnable(true);// 控制下拉刷新
		lvBook.setOnItemClickListener(this);
		lvBook.setXListViewListener(this);
		refreshDialog = new RefreshDialog(this);
		
		books = new ArrayList<BookInfo>();
		adapter = new BookInfoAdapter(this, books);
		lvBook.setAdapter(adapter);
		
		page = 1;
		
		//监听搜索按钮，点击跳转到搜索页面
		bar.setCallBack(new Callback() {
			
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case R.id.top_right_tv:
					Intent intent = new Intent(BuyBookActivity.this, SearchActivity.class);
					startActivity(intent);
					break;
				}
			}
		});
		//按理说应该先提前判断下网络是否可用
		new GetBookInfoTask().execute((Void)null);
	}
	
	/**
	 * 异步请求，获取书籍的数据集合
	 *
	 */
	class GetBookInfoTask extends AsyncTask<Void, Void, String>{
		ListBookController bookController = ListBookController.getInstance();
		ListBookPacket mPacket = new ListBookPacket();
		HttpClient httpClient = HttpClient.getInstance();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			refreshDialog.show();
			mPacket.setCmd(BaseConfig.CMD_GET_NEW_BOOK);
			mPacket.setSize(size);
			mPacket.setPage(page);
			bookController.execute(mPacket);
		}
		@Override
		protected String doInBackground(Void... params) {
			return httpClient.postRequest(BaseConfig.BOOKINF_URL, mPacket);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			refreshDialog.cancel();
			onLoad();
			BasePacket basePacket = bookController.execute(result);
			if(basePacket.isActionState()) {
				mPacket.setBody(basePacket.getBody());
				pagesum = Integer.valueOf(mPacket.getPageSum());
				books = mPacket.getBookInfos();
				if(null == books || books.size() <= 0) {
					pagesum  = 0;
					return;
				}
				adapter.refresh(books);
			}
		}
		
	}

	

	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		page = 1;
		new GetBookInfoTask().execute((Void)null);
	}

	/**
	 * 上拉加载更多
	 */
	@Override
	public void onLoadMore() {
		if (page < pagesum) {
			page++;
			refreshDialog.cancel();
			new GetBookInfoTask().execute((Void) null);
		} else {
			onLoad();
			Toast.makeText(this, getResources().getString(R.string.end_page), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * XListView的item的监听
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BookInfo bookInfo = (BookInfo) parent.getItemAtPosition(position);
		Intent intent = new Intent(this, BookInfoDetiActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("bookinfo", bookInfo);
		intent.putExtras(bundle);
		//打开新的activity并且返回结果
		startActivityForResult(intent, 0);
	}
	
	private void onLoad() {
		lvBook.stopRefresh();
		lvBook.stopLoadMore();
		lvBook.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd hh:mm",
				Locale.CHINESE).format(new Date()));
	}
	
	/**
	 * 返回页面重新刷新页面
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != Activity.RESULT_OK) {
			return;
		}
		if(requestCode == 0) {
			new GetBookInfoTask().execute((Void)null);
		}
	}
	
}
