package com.sellsapp.tcp.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sellsapp.message.contentProvider.ChatProviderManager;
import com.sellsapp.message.contentProvider.MessageProviderManger;
import com.sellsapp.message.db.ChatDao;
import com.sellsapp.tcp.entity.InvitationRequest;
import com.sellsapp.utils.Tools;

public class TCPPushReceiver extends BroadcastReceiver {

	public static final String ACTION_TEXT = "com.sellsapp.tcp.receiver.text";

	public static final String DATA_KEY = "data";

	public static final String ACTION_AUTH_OK = "auth_ok";

	public static final String ACTION_AUTH_FAILED = "auth_failed";

	public static final String ACTION_SEND_OK = "send_ok";

	public static final String ACTION_SEND_FAILED = "send_failed";

	public static final String ACTION_RECEIVER = "receive";

	private ChatProviderManager manager;

	private MessageProviderManger mm;

	private ChatDao dao;

	@Override
	public void onReceive(Context context, Intent intent) {
		manager = ChatProviderManager.getInstance();
		mm = MessageProviderManger.getInstance();
		String data = intent.getStringExtra(DATA_KEY);
		try {
			JSONObject jObj = new JSONObject(data);
			String action = jObj.getString("action");
			if (InvitationRequest.ACTION_KEY.equals(action)) {
				String status = jObj.getString("content");
				String sender = jObj.getString("sender");
				String receiver = jObj.getString("receiver");
				manager.updateStatus(status, sender, receiver, context);
			} else if (ACTION_AUTH_OK.equals(action)) {
				System.out.println("连接成功");
			} else if (ACTION_AUTH_FAILED.equals(action)) {
				Toast.makeText(context, "连接服务器失败！！", Toast.LENGTH_SHORT).show();
			} else if (ACTION_SEND_OK.equals(action)) { // 发送成功
				String msg_id = jObj.getString("token");
				String status = "发送成功";
				System.out.println("sg = " +msg_id + "status = " + status);
				mm.updateMsgStatus(status, msg_id, context);
			} else if (ACTION_SEND_FAILED.equals(action)) { // 发送失败
				String msg_id = jObj.getString("token");
				String status = "发送失败";
				mm.updateMsgStatus(status, msg_id, context);
			} else if (ACTION_RECEIVER.equals(action)) { // 接收
//				String sender = jObj.getString("receiver");
//				String receiver = jObj.getString("sender");
//				dao = ChatDao.getInstance(context);
//				long chatUserId = dao.getChatUserId(sender, receiver);
//				if (chatUserId <= 0) {
//					ContentValues values = new ContentValues();
//					values.put("sender", sender);
//					values.put("receiver", receiver);
//					values.put("status", "在线");
//					values.put("lastAccess", System.currentTimeMillis());
//					dao.insertOfUser(values);
//					chatUserId = dao.getChatUserId(sender, receiver);
//				}
//				
//				ContentValues values = new ContentValues();
//				values.put("content", jObj.getString("content"));
//				values.put("chat_user_id", (int) chatUserId);
//				values.put("status", "接收");
//				values.put("type", "receive");
//				values.put("lastAccess",Tools.convert2long(jObj.getString("timestamp")));
//				mm.insertReceive(values, context);
//				Toast.makeText(context, sender + "，给你发来了消息", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
