package com.sellsapp.message.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChatDBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "CHAT";
	
	private static final int DB_VERSION = 1;
	
	public static final String TABLE_NAME_CHAT_USER = "CHAT_USER";
	
	public static final String TABLE_NAME_CHAT_MESSAGER = "CHAT_MESSAGE";
	
	public ChatDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建CHAT_USER这张表
		db.execSQL("create table " + TABLE_NAME_CHAT_USER + 
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"sender varchar(20) NOT NULL, receiver varchar(20) NOT NULL," +
				" status varchar(20) NOT NULL, lastAccess BIGINT NOT NULL);");
		
		//创建CHAT_MESSAGE
		db.execSQL("create table "+TABLE_NAME_CHAT_MESSAGER+"" +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, "+"" +
				"content varchar(1024), chat_user_id INTEGER NOT NULL, type varchar(20), status varchar(20) NOT NULL, lastAccess BIGINT);");

	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
