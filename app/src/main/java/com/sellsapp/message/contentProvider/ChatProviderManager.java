package com.sellsapp.message.contentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;


public class ChatProviderManager {
	
	private ChatCotnentProvider provider;
	
	private static ChatProviderManager instance;
	
	
	private ChatProviderManager() {
	}
	
	public static ChatProviderManager getInstance(){
		if(null == instance) {
			instance = new ChatProviderManager();
		}

		return instance;
	}
	
	public int deleteChatUser(Cursor cursor, Context context){
		if(null == provider) {
			provider = new ChatCotnentProvider(context);
		}
		long id = cursor.getLong(cursor.getColumnIndex("id"));
		String selection = "id = ?";
		String[] selectionArgs = new String[]{String.valueOf(id)};
		return provider.delete(ChatCotnentProvider.USER_URI, selection, selectionArgs);
	}

	public int updateStatus(String status, String sender, String receiver, Context context) {
		if(null == provider) {
			provider = new ChatCotnentProvider(context);
		}
		if(TextUtils.isEmpty(status) || TextUtils.isEmpty(receiver) || TextUtils.isEmpty(sender)) {
			return 0;
		}
		ContentValues values = new ContentValues();
		values.put("status", status);
		String selection = "sender = ? and receiver = ?";
		String[] selectionArgs = new String[]{sender, receiver};
		return provider.update(ChatCotnentProvider.USER_URI, values, selection, selectionArgs);
	}	

}
