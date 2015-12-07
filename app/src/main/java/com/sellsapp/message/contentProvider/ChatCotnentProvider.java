package com.sellsapp.message.contentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.sellsapp.message.db.ChatDao;

public class ChatCotnentProvider extends ContentProvider {

	private static final int INSERT = 10;

	private static final int UPDATE = 20;
	
	private static final int DELETE = 30;
	
	private static final String STR_INSERT = "insert";
	
	private static final String STR_UPDATE = "update";
	
	private static final String STR_DELETE = "delete";
	
	private ChatDao chatUserDB;
	
	public static Uri USER_URI = Uri.parse("content://com.sellsapp.message.contentProvider.ChatCotnentProvider");
	
	private static final String USER_AUTHORITY = "com.sellsapp.message.contentProvider.ChatCotnentProvider";
	private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		matcher.addURI(USER_AUTHORITY, STR_INSERT, INSERT);
		matcher.addURI(USER_AUTHORITY, STR_UPDATE, UPDATE);
		matcher.addURI(USER_AUTHORITY, STR_DELETE, DELETE);
	}
	
	private Context context;
	public ChatCotnentProvider(Context context) {
		this.context = context;
	}
	
	@Override
	public boolean onCreate() {
		chatUserDB = ChatDao.getInstance(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int result = matcher.match(uri);
		if(result == INSERT) {
			
			getContext().getContentResolver().notifyChange(USER_URI, null);
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int result = matcher.match(uri);
		int count = 0;
		if(result == DELETE) {
			count = chatUserDB.deleteChatUser(selection, selectionArgs);
			context.getContentResolver().notifyChange(USER_URI, null);
		}
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,String[] selectionArgs) {
		chatUserDB = ChatDao.getInstance(getContext());
		int upadte = chatUserDB.updateUserStatus(values, selection, selectionArgs);
		context.getContentResolver().notifyChange(USER_URI, null);
		return upadte;
	}

}
