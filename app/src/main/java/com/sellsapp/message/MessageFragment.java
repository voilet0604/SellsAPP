package com.sellsapp.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.sellsapp.message.chat.ChatMessageActivity;
import com.sellsapp.message.contentProvider.ChatProviderManager;
import com.sellsapp.message.db.ChatDao;
import com.sellsapp.models.MessagePacket;
import com.sellsapp.net.HttpClient;
import com.sellsapp.utils.Tools;

//消息的fragment
public class MessageFragment extends Fragment implements IXListViewListener,
		OnItemLongClickListener, android.view.View.OnClickListener,
		OnItemClickListener {

	private SharedPreferences sp;

	@ViewInject(R.id.no)
	private ImageView ivno;

	private static XListView msgList;

	private int size = 10;

	private static int pagesum;

	// 偏移量
	private int offset = 1;

	private RefreshDialog refreshDialog;

	private HttpClient httpClient = HttpClient.getInstance();

	private String userId;
	@ViewInject(R.id.bar)
	private TopBarView bar;

	private View headerView;

	private static ChatDao chatDao;

	private ChatProviderManager manager;

	private static ChatAdapter chatAdapter;

	private static String username;

	private static int chat_id = -2;

	private LinearLayout ll;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,

	Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.message_fragment, null);
		init(view);
		return view;

	}

	// 需要实现功能 的代码全部写在这里面
	private void init(View view) {
		// activity和fragment XUtils的初始化有点区别的
		ViewUtils.inject(this, view);
		bar.mTvBack.setVisibility(View.GONE);
		bar.setTitle("消息中心");
		chat_id = -2;
		headerView = View.inflate(getActivity(), R.layout.message_header, null);
		headerView.setOnClickListener(this);
		msgList = (XListView) view.findViewById(R.id.messagelist);
		ll = (LinearLayout) view.findViewById(R.id.msg_fragment);
		msgList.setDividerHeight(0);
		msgList.setPullLoadEnable(true);// 控制下拉刷新
		msgList.setXListViewListener(this);
		msgList.setOnItemLongClickListener(this);
		msgList.setOnItemClickListener(this);
		refreshDialog = new RefreshDialog(getActivity());
		initData();
	}

	private void initData() {
		// page = 1;
		sp = getActivity()
				.getSharedPreferences("SellApp", Context.MODE_PRIVATE);
		userId = sp.getString("userId", null);
		username = sp.getString("username", null);
		ivno.setOnClickListener(this);
		chatDao = ChatDao.getInstance(getActivity());

		if (TextUtils.isEmpty(userId)) {
			ivno.setVisibility(View.VISIBLE);
			ivno.setImageResource(R.drawable.no_login);
			msgList.setVisibility(View.GONE);
		} else { // 已登录
			ivno.setVisibility(View.GONE);
			msgList.setVisibility(View.VISIBLE);
			offset = 1;
			// 查询数据库
			// 临时 之后换成dao
			// 每次查询20条
			int chatUserCount = chatDao.getChatUserCount(username);
			if (chatUserCount > 0) {
				pagesum = chatUserCount;
				Cursor cursor = chatDao.getChatUser(username, offset, size);
				if (null != cursor && cursor.moveToNext()) {
					ll.removeView(headerView);
					msgList.addHeaderView(headerView);
					chatAdapter = new ChatAdapter(getActivity(), cursor);
					msgList.setAdapter(chatAdapter);
					System.out.println("youshuji ");
				} else {
					ivno.setVisibility(View.GONE);
					msgList.setVisibility(View.GONE);
					ll.addView(headerView);
					System.out.println("keiou ");
				}
			} else {
				ivno.setVisibility(View.GONE);
				msgList.setVisibility(View.GONE);
				ll.addView(headerView);
				System.out.println("keiou ");
			}

		}
	}

	public class ChatAdapter extends CursorAdapter {

		public ChatAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ImageView headerIcon = (ImageView) view
					.findViewById(R.id.iv_message_chat_header_icon);

			FrameLayout flCount = (FrameLayout) view
					.findViewById(R.id.fl_msg_chat_count);

			TextView tvReceiver = (TextView) view
					.findViewById(R.id.tv_message_chat_name);

			TextView tvTime = (TextView) view
					.findViewById(R.id.tv_message_chat_time);

			String receiver = cursor.getString(cursor
					.getColumnIndex("receiver"));
			int chatId = cursor.getInt(cursor.getColumnIndex("_id"));
			System.out.println("chatId = " + chatId + " chat_id = " + chat_id);
			if (chat_id == chatId) {
				flCount.setVisibility(View.VISIBLE);
			} else {
				flCount.setVisibility(View.GONE);
			}
			long longTime = cursor.getLong(cursor.getColumnIndex("lastAccess"));
			String time = Tools.formatDateTime(longTime);
			tvTime.setText(time);
			tvReceiver.setText(receiver);
			flCount.setVisibility(View.GONE);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return View.inflate(getActivity(), R.layout.message_chat_item, null);
		}

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
			// mPacket.setPage(page);
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
		}

	}

	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		refreshDialog.show();
		offset = 1;
		// 重新访问数据库
		Cursor cursor = chatDao.getChatUser(username, offset, size);
		if (null != cursor) {
			chatAdapter.changeCursor(cursor);
			refreshDialog.cancel();
			onLoad();
		}

	}

	/**
	 * 上拉加载更多
	 */
	@Override
	public void onLoadMore() {
		if (offset <= pagesum) {
			offset += size;
			refreshDialog.cancel();
			Cursor cursor = chatDao.getChatUser(username, offset, size);
			if (null != cursor) {
				chatAdapter.changeCursor(cursor);
			}
		} else {
			onLoad();
			Toast.makeText(getActivity(),
					getResources().getString(R.string.end_page),
					Toast.LENGTH_SHORT).show();
		}

	}

	// 加载中
	private void onLoad() {
		msgList.stopRefresh();
		msgList.stopLoadMore();
		msgList.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd hh:mm",
				Locale.CHINESE).format(new Date()));
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// 不行就换成adapter.getItem(position);
		Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		showDeleteDialog(cursor);
		cursor.close();
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		chat_id = -2;
		Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
		String receiver = cursor.getString(cursor.getColumnIndex("receiver"));
		Intent intent = new Intent(getActivity(), ChatMessageActivity.class);
		intent.putExtra("receiver", receiver);
		startActivity(intent);
	}

	// 弹出删除对话框
	private void showDeleteDialog(final Cursor cursor) {

		AlertDialog.Builder builder = new Builder(getActivity(), 3);
		builder.setTitle("确认删除？");
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteChat(cursor);
			}

		});
		builder.create().show();
	}

	private void deleteChat(Cursor cursor) {
		// 修改数据库
		manager = ChatProviderManager.getInstance();
		manager.deleteChatUser(cursor, getActivity());
	}

	// 图片的点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.message_header:
			Intent intent = new Intent(getActivity(), MessageActivity.class);
			intent.putExtra("userId", userId);
			startActivity(intent);
			break;

		default:
			initData();
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public static void changeUI(long chatUserId) {
		if (msgList == null || chatAdapter == null) {
			return;
		}
		// 查询数据库
		// 临时 之后换成dao
		// 每次查询20条
		int chatUserCount = chatDao.getChatUserCount(username);
		chat_id = (int) chatUserId;
		if (chatUserCount > 0) {
			pagesum = chatUserCount;
			Cursor cursor = chatDao.getChatUser(username, 1, 10);
			if (null != cursor) {
				chatAdapter.changeCursor(cursor);
				msgList.post(new Runnable() {
					@Override
					public void run() {
						try {
							msgList.smoothScrollToPosition(chatAdapter.getCount(),0);
						} catch (Exception e) {
						}
					}
				});
			}
		}
	}
}