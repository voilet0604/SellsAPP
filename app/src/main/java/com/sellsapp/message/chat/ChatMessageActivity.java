package com.sellsapp.message.chat;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sellsapp.R;
import com.sellsapp.basic.MainActivity;
import com.sellsapp.basic.widget.TopBarView;
import com.sellsapp.message.MessageFragment;
import com.sellsapp.message.contentProvider.ChatCotnentProvider;
import com.sellsapp.message.contentProvider.ChatProviderManager;
import com.sellsapp.message.contentProvider.MessageProvider;
import com.sellsapp.message.contentProvider.MessageProviderManger;
import com.sellsapp.message.db.ChatDao;
import com.sellsapp.message.db.entity.ChatUser;
import com.sellsapp.tcp.client.ConnectManager;
import com.sellsapp.tcp.entity.InvitationRequest;
import com.sellsapp.tcp.entity.TextRequest;
import com.sellsapp.utils.AppManager;
import com.sellsapp.utils.Tools;

public class ChatMessageActivity extends Activity implements TextWatcher {

	@ViewInject(R.id.bar)
	private TopBarView bar;

	@ViewInject(R.id.message_list_view)
	private static ListView listView;

	@ViewInject(R.id.message_et_content)
	private EditText etContent;

	@ViewInject(R.id.message_btn_send)
	private Button btnSend;

	private SharedPreferences sp;

	private static String title = "与RECEIVER在聊天_STATUS";

	private String receiver;

	private DbUtils dbUtils;

	private ChatUserObserver chatObserver;

	private MessageObserver messageObserver;

	private ChatProviderManager manager;

	private MessageProviderManger mm;

	private static MsgChatAdapter adapter;

	private static ChatDao chatDao;

	private String sender;

	public static final int CHAT_USER_STATUS_CHANGE = 1;

	public static final int CHAT_MESSAGE_STATUS_CHANGE = 3;

	// 监听数据库
	@SuppressLint("HandlerLeak")
	private Handler chatHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 这里更新ui
			int what = msg.what;
			switch (what) {
			case CHAT_USER_STATUS_CHANGE:
				bar.setTitle(title.replace("RECEIVER", receiver).replace(
						"STATUS", msg.obj.toString()));
				break;
			case 2:
				// 来新消息了
				if (null != adapter) {
					Cursor cursor = (Cursor) msg.obj;
					adapter.changeCursor(cursor);
				}
			case CHAT_MESSAGE_STATUS_CHANGE:
				loadData();
				break;
			}
		}
	};

	private long chatUserId;

	private int msgId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat_message);
		ViewUtils.inject(this);
		AppManager.getAppManager().addActivity(this);
		receiver = getIntent().getStringExtra("receiver");
		sp = getSharedPreferences("SellApp", MODE_PRIVATE);
		String username = sp.getString("username", null);
		sender = username;
		btnSend.setEnabled(false);
		chatDao = ChatDao.getInstance(getApplicationContext());
		ChatUser chatUser = new ChatUser();
		chatUser.setLastAccess(System.currentTimeMillis());
		chatUser.setReceiver(receiver);
		chatUser.setSender(sender);
		chatUser.setStatus("未知");
		chatDao.saveOrUpdateUser(chatUser);

		mm = MessageProviderManger.getInstance();
		manager = ChatProviderManager.getInstance();

		chatObserver = new ChatUserObserver(getApplicationContext(),
				chatHandler);
		getContentResolver().registerContentObserver(
				ChatCotnentProvider.USER_URI, false, chatObserver);

		messageObserver = new MessageObserver(chatHandler);
		getContentResolver().registerContentObserver(
				MessageProvider.MESSAGE_URI, false, messageObserver);

		chatUserId = chatDao.getChatUserId(username, receiver);
		if (chatUserId > 0) {
			final Cursor cursor = chatDao.getChatMessageAll(chatUserId);
			if (null != cursor) {
				adapter = new MsgChatAdapter(getApplicationContext(), cursor);
				listView.setAdapter(adapter);
				listView.post(new Runnable() {
					@Override
					public void run() {
						listView.smoothScrollToPositionFromTop(cursor.getCount(), 0);
					}
				});
			}
		}

		// 发出一段请求，得到对方是否在线
		InvitationRequest request = new InvitationRequest();
		request.setAction(InvitationRequest.ACTION_KEY);
		request.setContent(InvitationRequest.ACTION_KEY);
		request.setReceiver(receiver);
		request.setSender(username);
		request.setType("request");
		request.setLastAccess(System.currentTimeMillis());
		ConnectManager.getInstance().Invitaion(request);
		bar.setTitle(title.replace("RECEIVER", receiver).replace("STATUS", ""));
		etContent.addTextChangedListener(this);

	}

	public class MsgChatAdapter extends CursorAdapter {

		public MsgChatAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tvTime = (TextView) view
					.findViewById(R.id.item_message_tv_time);
			// sender
			RelativeLayout rlSender = (RelativeLayout) view
					.findViewById(R.id.item_message_sender);
			ImageView sernIcon = (ImageView) view
					.findViewById(R.id.item_message_sender_icon);
			ImageView faildIcon = (ImageView) view
					.findViewById(R.id.item_message_sender_iv_faild);
			ProgressBar loadBar = (ProgressBar) view
					.findViewById(R.id.item_message_sender_pb_state);
			TextView tvSendcontent = (TextView) view
					.findViewById(R.id.item_message_sender_tv_content);

			RelativeLayout rlReceiver = (RelativeLayout) view
					.findViewById(R.id.item_message_receiver);
			ImageView receiverIcon = (ImageView) view
					.findViewById(R.id.item_message_receiver_icon);
			TextView tvReceiverContent = (TextView) view
					.findViewById(R.id.item_message_receiver_tv_content);

			String type = cursor.getString(cursor.getColumnIndex("type"));
			String status = cursor.getString(cursor.getColumnIndex("status"));
			String content = cursor.getString(cursor.getColumnIndex("content"));
			long lastAccess  = cursor.getLong(cursor.getColumnIndex("lastAccess"));
			tvTime.setText(Tools.formatDateTime(lastAccess));
			System.out.println("status = " + status);
			if ("send".equals(type)) {
				rlReceiver.setVisibility(View.GONE);
				rlSender.setVisibility(View.VISIBLE);
				if ("发送中".equals(status)) {
					faildIcon.setVisibility(View.GONE);
					loadBar.setVisibility(View.VISIBLE);
				} else if ("发送失败".equals(status)) {
					faildIcon.setVisibility(View.VISIBLE);
				} else if ("发送成功".equals(status)) {
					faildIcon.setVisibility(View.GONE);
					loadBar.setVisibility(View.GONE);
				}
				tvSendcontent.setText(content);
			} else if ("receive".equals(type)) {
				rlSender.setVisibility(View.GONE);
				rlReceiver.setVisibility(View.VISIBLE);
				tvReceiverContent.setText(content);
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			return View.inflate(context, R.layout.chat_item, null);
		}

	}

	@OnClick({ R.id.message_btn_send })
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.message_btn_send:
			System.out.println("被电价");
			sendMessage();
			break;
		}
	}

	private void sendMessage() {

		System.out.println("准备发送消息");
		final String content = etContent.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			return;
		}
		String status = "发送中";
		String type = "send";
		msgId = mm.insert((int) chatUserId, content, type, status, this);
		System.out.println("是否成功 " + msgId);
		if (msgId > 0) {
			// 更新ui
			loadData();
			etContent.setText("");
			new Thread() {
				public void run() {
					TextRequest textRequest = new TextRequest();
					textRequest.setAction("send");
					textRequest.setContent(content);
					textRequest.setReceiver(receiver);
					textRequest.setSender(sender);
					textRequest.setSequence(UUID.randomUUID().toString());
					textRequest.setType("request");
					textRequest.setToken(String.valueOf(msgId));
					textRequest.setLastAccess(System.currentTimeMillis());
					ConnectManager.getInstance().send(textRequest);
				};
			}.start();
		}

	}

	public static void receiveMsgChange(Cursor cursor) {
		if(null == adapter && listView == null) {
			return;
		}
		adapter.changeCursor(cursor);
		listView.post(new Runnable() {

			@Override
			public void run() {
				try {
					listView.smoothScrollToPosition(adapter.getCount(), 0);
				} catch (Exception e) {
				}
			}
		});
	}

	private void loadData() {
		final Cursor cursor = chatDao.getChatMessage((int) chatUserId);
		if (null != cursor) {
			adapter.changeCursor(cursor);
			listView.post(new Runnable() {

				@Override
				public void run() {
					listView.smoothScrollToPositionFromTop(cursor.getCount(), 0);
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getContentResolver().unregisterContentObserver(chatObserver);
	}

	class ChatUserObserver extends ContentObserver {
		private Context context;
		private Handler handler;

		public ChatUserObserver(Context context, Handler handler) {
			super(handler);
			this.context = context;
			this.handler = handler;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			String chatUserStatus = chatDao.getChatUserStatus(sender, receiver);
			if (TextUtils.isEmpty(chatUserStatus)
					|| chatUserStatus.equals("未知")) {
				return;
			}
			handler.obtainMessage(CHAT_USER_STATUS_CHANGE, chatUserStatus)
					.sendToTarget();
		}
	}

	class MessageObserver extends ContentObserver {

		private Handler handler;

		public MessageObserver(Handler handler) {
			super(handler);
			this.handler = handler;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Cursor cursor = chatDao.queryMessageById(msgId);
			System.out.println("消息改变状态 ");
			if (null != cursor && cursor.moveToFirst()) {
				handler.obtainMessage(CHAT_MESSAGE_STATUS_CHANGE)
						.sendToTarget();
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (TextUtils.isEmpty(s)) {
			btnSend.setEnabled(false);
			return;
		}
		btnSend.setEnabled(true);
	}

	public static class MsgReceiver extends BroadcastReceiver {

		public static final String ACTION_TEXT = "com.sellsapp.message.chat.ChatMessageActivity.MsgReceiver";

		public static final String DATA_KEY = "data";

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String stringExtra = intent.getStringExtra(DATA_KEY);
				System.out.println("hfjkahsfkhskahfk = " + stringExtra);
				JSONObject jsonObject = new JSONObject(stringExtra);
				String action = jsonObject.getString("action");
				if("receive".equals(action)) {
					String sender = jsonObject.getString("receiver");
					String receiver = jsonObject.getString("sender");
					chatDao = ChatDao.getInstance(context);
					long chatUserId = chatDao.getChatUserId(sender, receiver);
					if (chatUserId <= 0) {
						ContentValues values = new ContentValues();
						values.put("sender", sender);
						values.put("receiver", receiver);
						values.put("status", "在线");
						values.put("lastAccess", System.currentTimeMillis());
						chatDao.insertOfUser(values);
						chatUserId = chatDao.getChatUserId(sender, receiver);
					}
					
					ContentValues values = new ContentValues();
					values.put("content", jsonObject.getString("content"));
					values.put("chat_user_id", (int) chatUserId);
					values.put("status", "接收");
					values.put("type", "receive");
					values.put("lastAccess", System.currentTimeMillis());
					long insertOfMessage = chatDao.insertOfMessage(values);
					String selection = "chat_user_id = ?";
					String[] selectionArgs = new String[]{String.valueOf(chatUserId)};
					String sortOrder = "lastAccess ASC";
					Cursor cursor = chatDao.queryMessage(null, selection, selectionArgs, sortOrder );
					if(null != cursor) {
						receiveMsgChange(cursor);
						MainActivity.setPoint(true);
						MessageFragment.changeUI(chatUserId);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
