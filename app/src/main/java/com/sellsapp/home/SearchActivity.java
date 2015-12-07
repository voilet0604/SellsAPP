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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sellsapp.R;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.basic.widget.ClearEditText;
import com.sellsapp.basic.widget.RefreshDialog;
import com.sellsapp.basic.widget.TopBarView;
import com.sellsapp.basic.widget.XListView;
import com.sellsapp.basic.widget.XListView.IXListViewListener;
import com.sellsapp.controllers.ListBookController;
import com.sellsapp.models.BasePacket;
import com.sellsapp.models.BookInfo;
import com.sellsapp.models.ListBookPacket;
import com.sellsapp.net.HttpClient;
import com.sellsapp.utils.AppManager;

/**
 * 搜索书籍页面
 */
public class SearchActivity extends Activity implements IXListViewListener,
		OnItemClickListener, OnClickListener {

	private ClearEditText etSearch; //自带删除按钮的EditText
	private Button btnSearch;
	private LinearLayout llListBookInfo;

	private XListView xListView;

	private int size = 20; // 每次加载多少页
	private int page = 1; // 当前页
	private int pageSum; // 总页数
	private String bookname; // 书名

	private BookInfoAdapter adapter;
	private List<BookInfo> hotSearchBooks;

	private RefreshDialog dialog;

	@ViewInject(R.id.bar)
	private TopBarView bar;

	HttpClient httpClient = HttpClient.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_activity);
		initView();
		hotSearchBooks = new ArrayList<BookInfo>();
		adapter = new BookInfoAdapter(this, hotSearchBooks);
		xListView.setAdapter(adapter);
		xListView.setDividerHeight(0);
		xListView.setPullLoadEnable(true);
		xListView.setXListViewListener(this);
		xListView.setOnItemClickListener(this);
		page = 1;
		new BookInfoAsyncTask().execute(BaseConfig.CMD_GET_NEW_BOOK);
	}

	private void initView() {
		ViewUtils.inject(this);
		AppManager.getAppManager().addActivity(this);
		bar.setTitle("搜索");
		dialog = new RefreshDialog(this);
		llListBookInfo = (LinearLayout) findViewById(R.id.ll_search_listbookinfo);
		etSearch = (ClearEditText) findViewById(R.id.et_search);
		btnSearch = (Button) findViewById(R.id.btn_search);
		xListView = (XListView) findViewById(R.id.xlv_serach_book);
		btnSearch.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			bookname = etSearch.getText().toString().trim();
			if (TextUtils.isEmpty(bookname)) {
				etSearch.setError("请输入书名");
				return;
			}
			etSearch.setText(""); // 清空输入框
			etSearch.setHint(bookname);
			new BookInfoAsyncTask().execute(BaseConfig.CMD_SEARCH_BOOK,
					bookname);
			break;

		}
	}

	/**
	 * 开启异步请求获取书籍信息
	 * @author Administrator
	 *
	 */
	class BookInfoAsyncTask extends AsyncTask<String, Void, String> {
		ListBookController listBookController = ListBookController.getInstance();
		ListBookPacket mPacket = new ListBookPacket();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			mPacket.setCmd(params[0]);
			mPacket.setSize(size);
			mPacket.setPage(page);
			if (params.length > 1) {
				mPacket.setBookName(params[1]);
			}
			listBookController.execute(mPacket);
			return httpClient.postRequest(BaseConfig.BOOKINF_URL, mPacket);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.cancel();
			onLoad();
			BasePacket basePacket = listBookController.execute(result);
			if(!basePacket.isActionState()) {
				pageSum = 0;
				hotSearchBooks.removeAll(hotSearchBooks);
				adapter.refresh(hotSearchBooks);
				Toast.makeText(getApplicationContext(), basePacket.getActionMessage(), Toast.LENGTH_SHORT).show();
				return;
			}
			mPacket.setBody(basePacket.getBody());
			pageSum = Integer.valueOf(mPacket.getPageSum());
			hotSearchBooks = mPacket.getBookInfos();
			if(null == hotSearchBooks || hotSearchBooks.isEmpty()) {
				pageSum  = 0;
				return;
			}
			//获取到书籍信息后，刷新adapter
			adapter.refresh(hotSearchBooks);
		}
	}

	// xlistview item的监听
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BookInfo bookInfo = (BookInfo) parent.getItemAtPosition(position);
		Intent intent = new Intent(this, BookInfoDetiActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("bookinfo", bookInfo);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		page = 1;
		new BookInfoAsyncTask().execute(BaseConfig.CMD_SEARCH_BOOK, bookname);
	}

	// 加载更多
	@Override
	public void onLoadMore() {
		if (page < pageSum) {
			page++;
			dialog.cancel();
			new BookInfoAsyncTask().execute(BaseConfig.CMD_SEARCH_BOOK,
					bookname);
		} else {
			onLoad();
			Toast.makeText(this, getResources().getString(R.string.end_page),
					Toast.LENGTH_SHORT).show();
		}
	}

	//加载中...
	private void onLoad() {
		xListView.stopRefresh();
		xListView.stopLoadMore();
		xListView.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd hh:mm",
				Locale.CHINESE).format(new Date()));
	}
	
	//防止冲突在,系统调用onDestroy的同时把当前的activity从栈中移除出去
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().finishActivity(SearchActivity.this);
	}
}
