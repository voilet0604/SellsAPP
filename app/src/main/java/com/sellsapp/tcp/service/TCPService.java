package com.sellsapp.tcp.service;

import java.util.UUID;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.TextUtils;

import com.sellsapp.message.chat.ChatMessageActivity.MsgReceiver;
import com.sellsapp.tcp.client.ConnectManager;
import com.sellsapp.tcp.client.ConnectManager.ConnectListener;
import com.sellsapp.tcp.entity.OAuthRequst;
import com.sellsapp.tcp.receiver.TCPPushReceiver;

public class TCPService extends Service implements ConnectListener{

	public static final String SRVICE_NAME = "com.sellsapp.tcp.service.TCPService";
	private ConnectManager manager;
	private SharedPreferences sp;
	
	private TCPPushReceiver receiver;
	
	private MsgReceiver msgReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("SellApp", MODE_PRIVATE);
		final String username = sp.getString("username", null);
		if(!TextUtils.isEmpty(username)) {
			receiver = new TCPPushReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(TCPPushReceiver.ACTION_TEXT);
			registerReceiver(receiver, filter);

			msgReceiver = new MsgReceiver();
			IntentFilter filter1 = new IntentFilter();
			filter1.addAction(TCPPushReceiver.ACTION_TEXT);
			registerReceiver(msgReceiver, filter1);
			
			manager = ConnectManager.getInstance();

			System.out.println("进入");
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					manager.setListener(TCPService.this);
					OAuthRequst requst = new OAuthRequst();
					requst.setAction("auth");
					requst.setType("request");
					requst.setSender(username);
					requst.setSequence(UUID.randomUUID().toString());
					requst.setLastAccess(System.currentTimeMillis());
					manager.connect(requst);
					System.out.println("成功");
				}
			}){}.start();
		}
	}

	@Override
	public void postData(String data) {
		System.out.println("服务接收到消息 = " + data);
		Intent intent = new Intent();
		intent.setAction(TCPPushReceiver.ACTION_TEXT);
		intent.putExtra(TCPPushReceiver.DATA_KEY, data);
		sendBroadcast(intent);
		Intent intent1 = new Intent();
		intent.setAction(MsgReceiver.ACTION_TEXT);
		intent.putExtra(MsgReceiver.DATA_KEY, data);
		sendBroadcast(intent1);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("service 销毁");
		if(manager != null) {
			manager.desConnect();
		}
		if(receiver != null) {
			unregisterReceiver(receiver);
		}
		if(msgReceiver != null) {
			unregisterReceiver(msgReceiver);
		}
		System.out.println("service 销毁");
	}

}
