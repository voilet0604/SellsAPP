package com.sellsapp.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sellsapp.R;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.basic.widget.RefreshDialog;
import com.sellsapp.basic.widget.TopBarView;
import com.sellsapp.basic.widget.XListView;
import com.sellsapp.basic.widget.XListView.IXListViewListener;
import com.sellsapp.controllers.MessageController;
import com.sellsapp.message.adapter.MessageAdapter;
import com.sellsapp.models.BasePacket;
import com.sellsapp.models.Message;
import com.sellsapp.models.MessagePacket;
import com.sellsapp.net.HttpClient;
import com.sellsapp.utils.AppManager;

public class MessageActivity extends Activity implements IXListViewListener, OnItemLongClickListener {
	
	private String userId;

	@ViewInject(R.id.messagelist)
	private XListView msgList;

	private int size = 20;

	private int page = 1;

	private int pagesum;

	private List<Message> messages;

	private MessageAdapter adapter;

	private RefreshDialog refreshDialog;

	private HttpClient httpClient = HttpClient.getInstance();

	@ ViewInject(R.id.bar)
	private TopBarView bar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_message);
		userId = getIntent().getStringExtra("userId");
		ViewUtils.inject(this);
		AppManager.getAppManager().addActivity(this);
		bar.setTitle("系统消息");
		msgList.setDividerHeight(0);
		msgList.setPullLoadEnable(true);// 控制下拉刷新
		msgList.setXListViewListener(this);
		msgList.setOnItemLongClickListener(this);
		refreshDialog = new RefreshDialog(this);
		messages = new ArrayList<Message>();
		adapter = new MessageAdapter(this, messages);
		msgList.setAdapter(adapter);
		page = 1;
		new GetMessageAsyncTask().execute((Void)null);
	}

	
	/**
	 * 异步访问网络获取消息
	 */
	class GetMessageAsyncTask extends AsyncTask<Void, Void, String> {

		MessageController messageController = MessageController.getInstance();
		MessagePacket mPacket = new MessagePacket();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPacket.setPage(page);
			mPacket.setSize(size);
			mPacket.setUserId(userId);
			messageController.execute(mPacket);
		}

		@Override
		protected String doInBackground(Void... params) {
			return httpClient.postRequest(BaseConfig.MESSAGE_URL, mPacket);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			System.out.println("result = " + result);
			onLoad();
			BasePacket basePacket = messageController.execute(result);
			if (!basePacket.isActionState()) {
				return;
			}
			mPacket.setBody(basePacket.getBody());
			pagesum = Integer.valueOf(mPacket.getPagesum());
			System.out.println("sum = " + pagesum);
			messages = mPacket.getMessages();
			System.out.println("messages = " + messages);
			if(null == messages || messages.isEmpty()) {
				pagesum  = 0;
				return;
			}
			adapter.refresh(messages);
		}

	}

	
	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		page = 1;
		new GetMessageAsyncTask().execute((Void) null);
	}

	/**
	 * 上拉加载更多
	 */
	@Override
	public void onLoadMore() {
		if (page < pagesum) {
			page++;
			refreshDialog.cancel();
			new GetMessageAsyncTask().execute((Void) null);
		} else {
			onLoad();
			Toast.makeText(this, getResources().getString(R.string.end_page),
					Toast.LENGTH_SHORT).show();
		}

	}
	
	//加载中
	private void onLoad() {
		msgList.stopRefresh();
		msgList.stopLoadMore();
		msgList.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd hh:mm",
				Locale.CHINESE).format(new Date()));
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View arg1, int position,
			long arg3) {
		Message message = (Message) parent.getItemAtPosition(position);
		if(null != message ) {
			showDeleteDialog(message);
		}
		return false;
	}
	
	//弹出删除对话框
		private void showDeleteDialog(final Message message) {
			
			AlertDialog.Builder builder = new Builder(this,3);
			builder.setTitle("确认删除？");
			builder.setNegativeButton("取消",null);
			builder.setPositiveButton("确定",  new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
						deleteMsg(message);
						messages.remove(message);
				}

			});
			builder.create().show();
		}

		private void deleteMsg(Message message) {
			new DeleteAsync().execute(message);
		}
		
		//开启异步加载删除消息
		class DeleteAsync extends AsyncTask<Message, Void, String>{

			MessageController messageController = MessageController.getInstance();
			MessagePacket mPacket = new MessagePacket();
			Message message = null;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
			@Override
			protected String doInBackground(Message... params) {
				message = params[0];
				mPacket.setMsgId(message.getId());
				messageController.execute(mPacket);
				return httpClient.postRequest(BaseConfig.DETELE_MESSAGE_URL, mPacket);
			}
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				BasePacket basePacket = messageController.execute(result);
				if(basePacket.isActionState()) {
					messages.remove(message);
					Toast.makeText(MessageActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
					adapter.refresh(messages);
				}
			}
		}


}
