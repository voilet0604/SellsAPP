package com.sellsapp.message.contentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class MessageProviderManger {

	private MessageProvider provider;

	private static MessageProviderManger instance;

	public static MessageProviderManger getInstance() {
		if (null == instance) {
			instance = new MessageProviderManger();
		}
		return instance;
	}

	private MessageProviderManger() {
	}

	public Cursor query(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return provider.query(MessageProvider.MESSAGE_URI, projection,
				selection, selectionArgs, sortOrder);
	}

	public int insert(int chatUserId, String content,  String type, String status, Context context) {
		if (null == provider) {
			provider = new MessageProvider();
		}
		ContentValues values = new ContentValues();
		values.put("chat_user_id", chatUserId);
		values.put("content", content);
		values.put("type", type);
		values.put("status", status);
		values.put("lastAccess", System.currentTimeMillis());
		Uri insert = provider.insert(MessageProvider.MESSAGE_URI, values);
		int id = 0;
		if(insert != null) {
			String[] projection = new String[]{"_id"};
			String selection = "chat_user_id = ?";
			String[] selectionArgs = new String[]{String.valueOf(chatUserId)};
			String sortOrder = "lastAccess DESC";
			Cursor cursor = provider.query(insert, projection, selection, selectionArgs, sortOrder );
			if(null != cursor && cursor.moveToFirst()) {
				 id = cursor.getInt(0);
			}
			context.getContentResolver().notifyChange(MessageProvider.MESSAGE_URI, null);
		}
		return id;
	}

	public void insertReceive(ContentValues values, Context context) {
		if (null == provider) {
			provider = new MessageProvider();
		}
		provider.insert(MessageProvider.MESSAGE_URI, values);
		context.getContentResolver().notifyChange(MessageProvider.MESSAGE_URI, null);
	}


	public int updateMsgStatus(String status, String msg_id, Context context) {
		if (null == provider) {
			provider = new MessageProvider();
		}
		ContentValues values = new ContentValues();
		values.put("status", status);
		String selection = "_id = ?";
		String[] selectionArgs = new String[]{msg_id};
		int update = provider.update(MessageProvider.MESSAGE_URI, values, selection, selectionArgs);
		System.out.println("修改改数据库  = " + update);
		if(update > 0) {
			context.getContentResolver().notifyChange(MessageProvider.MESSAGE_URI, null);
		}
		return update;
	}
}
