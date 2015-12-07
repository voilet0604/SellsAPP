package com.sellsapp.message.contentProvider;

import com.sellsapp.message.db.ChatDao;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class MessageProvider extends ContentProvider {

	private static final int INSERT = 10;

	private static final int UPDATE = 20;
	
	private static final int DELETE = 30;
	
	private static final String STR_INSERT = "insert";
	
	private static final String STR_UPDATE = "update";
	
	private static final String STR_DELETE = "delete";

	private ChatDao chatUserDB;

	public static Uri MESSAGE_URI = Uri.parse("content://com.sellsapp.message.contentProvider.MessageProvider");

	private static final String USER_AUTHORITY = "com.sellsapp.message.contentProvider.MessageProvider";
	private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		matcher.addURI(USER_AUTHORITY, STR_INSERT, INSERT);
		matcher.addURI(USER_AUTHORITY, STR_UPDATE, UPDATE);
		matcher.addURI(USER_AUTHORITY, STR_DELETE, DELETE);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		chatUserDB = ChatDao.getInstance(getContext());
		long count = chatUserDB.insertOfMessage(values);
		if(count > 0) {
			return uri;
		}
		return null;
	}

	@Override
	public boolean onCreate() {
		chatUserDB = ChatDao.getInstance(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return chatUserDB.queryMessage(projection, selection,selectionArgs, sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return chatUserDB.updateMessageStatus(values, selection, selectionArgs);
	}

}
