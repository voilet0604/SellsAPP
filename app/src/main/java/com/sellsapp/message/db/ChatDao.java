package com.sellsapp.message.db;

import com.sellsapp.message.db.entity.ChatUser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ChatDao {

	private static ChatDao instance;

	private static ChatDBHelper helper;

	private ChatDao() {
	}

	public static ChatDao getInstance(Context context) {
		if (null == instance) {
			instance = new ChatDao();
		}
		if (helper == null) {
			helper = new ChatDBHelper(context);
		}
		return instance;
	}

	public void insertOfUser(ContentValues values) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.insert(ChatDBHelper.TABLE_NAME_CHAT_USER, null, values);
		db.close();
	}

	public void updateOfUser(ContentValues values, String sender,
			String receiver) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String whereClause = "sender = ? and receiver = ?";
		String[] whereArgs = new String[] { sender, receiver };
		db.update(ChatDBHelper.TABLE_NAME_CHAT_USER, values, whereClause,
				whereArgs);
		db.close();
	}

	public void deleteOfUser(ContentValues values) {
		// SQLiteDatabase db = helper.getWritableDatabase();
		// db.delete(table, whereClause, whereArgs)
	}

	public long insertOfMessage(ContentValues values) {
		SQLiteDatabase db = helper.getWritableDatabase();
		System.out.println("afsf "+ values.toString());
		long insert = db.insert(ChatDBHelper.TABLE_NAME_CHAT_MESSAGER, null,values);
		System.out.println("插入行号 ; " + insert);
		return insert;
	}

	/**
	 * 
	 * @param sender
	 * @param offset
	 * @param size
	 * @return
	 */
	public Cursor getChatUser(String sender, int offset, int size) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select * from chat_user where sender = ? order by lastAccess ASC ";
		String[] selectionArgs = new String[]{sender};
		return db.rawQuery(sql, selectionArgs);
	}

	public long getChatUserId(String sender, String recevier) {
		System.out.println("sender = " + sender + ", reciever = " + recevier);
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] columns = new String[] { "_id" };
		String selection = "sender = ? and receiver = ?";
		String[] selectionArgs = new String[] { sender, recevier };
		Cursor cursor = db.query(ChatDBHelper.TABLE_NAME_CHAT_USER, columns,
				selection, selectionArgs, null, null, null);
		int id = 0;
		if (null != cursor && cursor.moveToFirst()) {
			id = cursor.getInt(cursor.getColumnIndex("_id"));
			cursor.close();
		}
		db.close();
		return id;
	}

	public String getChatUserStatus(String sender, String receiver) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] columns = new String[] { "status" };
		String selection = "sender = ? and receiver = ?";
		String[] selectionArgs = new String[] { sender, receiver };
		Cursor cursor = db.query(ChatDBHelper.TABLE_NAME_CHAT_USER, columns,
				selection, selectionArgs, null, null, null);
		String status = null;
		if (null != cursor && cursor.moveToFirst()) {
			status = cursor.getString(cursor.getColumnIndex("status"));
			cursor.close();
		}
		db.close();
		return status;
	}

	/**
	 * 数据总数
	 * 
	 * @param sender
	 * @return
	 */
	public int getChatUserCount(String sender) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] selectionArgs = new String[] { sender };
		Cursor cursor = db.rawQuery(
				"select count(*) from CHAT_USER where sender = ?",
				selectionArgs);
		if (null == cursor || !cursor.moveToFirst()) {
			return 0;
		}

		int count = cursor.getInt(0);
		cursor.close();
		db.close();
		return count;
	}

	public int deleteChatUser(String whereClause, String[] whereArgs) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int count = db.delete(ChatDBHelper.TABLE_NAME_CHAT_USER, whereClause,
				whereArgs);
		db.close();
		return count;
	}

	public Cursor getChatMessage(long chat_user_id, int offset, int size) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String selection = "chat_user_id = ?";
		String[] selectionArgs = new String[] { String.valueOf(chat_user_id) };
		String limit = size + "," + offset ;
		Cursor cursor = db.query(ChatDBHelper.TABLE_NAME_CHAT_MESSAGER, null,
				selection, selectionArgs, null, null, "lastAccess ASC", limit);
		return cursor;
	}
	public Cursor getChatMessageAll(long chat_user_id) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String selection = "chat_user_id = ?";
		String[] selectionArgs = new String[] { String.valueOf(chat_user_id) };
		Cursor cursor = db.query(ChatDBHelper.TABLE_NAME_CHAT_MESSAGER, null,selection, selectionArgs, null, null, "lastAccess ASC", null);
		return cursor;
	}

	public void updateOfMessage(ContentValues values) {
	}

	public void deleteOfMessage() {

	}

	public int updateUserStatus(ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int update = db.update(ChatDBHelper.TABLE_NAME_CHAT_USER, values,
				selection, selectionArgs);
		db.close();
		return update;
	}

	/**
	 * 
	 * @param chatUser
	 */
	public long saveOrUpdateUser(ChatUser chatUser) {
		System.out.println("cahdfa = " + chatUser.toString());
		SQLiteDatabase db = helper.getWritableDatabase();
		String[] columns = new String[] { "_id" };
		String selection = "sender = ? and receiver = ?";
		String[] selectionArgs = new String[] { chatUser.getSender(),
				chatUser.getReceiver() };
		Cursor cursor = db.query(ChatDBHelper.TABLE_NAME_CHAT_USER, columns,
				selection, selectionArgs, null, null, null);
		long resultCode = 0;
		if (null != cursor && cursor.moveToFirst()) {
			int _id = cursor.getInt(cursor.getColumnIndex("_id"));
			if (_id > 0) {
				ContentValues values = new ContentValues();
				values.put("lastAccess", chatUser.getLastAccess());
				values.put("status", chatUser.getStatus());
				String whereClause = "_id = ?";
				String[] whereArgs = new String[] { String.valueOf(_id) };
				resultCode = db.update(ChatDBHelper.TABLE_NAME_CHAT_USER,
						values, whereClause, whereArgs);
			} else {
				resultCode = SaveChat(chatUser, db);
			}
			cursor.close();
			cursor = null;
			db.close();
		} else {
			resultCode = SaveChat(chatUser, db);
			db.close();
		}
		return resultCode;
	}

	private long SaveChat(ChatUser chatUser, SQLiteDatabase db) {
		long resultCode;
		ContentValues values = new ContentValues();
		values.put("sender", chatUser.getSender());
		values.put("receiver", chatUser.getReceiver());
		values.put("status", chatUser.getStatus());
		values.put("lastAccess", chatUser.getLastAccess());
		resultCode = db.insert(ChatDBHelper.TABLE_NAME_CHAT_USER, null,
				values);
		return resultCode;
	}

	public Cursor getChatMessage(int chat_user_id) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String selection = "chat_user_id = ?";
		String[] selectionArgs = new String[]{String.valueOf(chat_user_id)};
		return db.query(ChatDBHelper.TABLE_NAME_CHAT_MESSAGER, null, selection, selectionArgs, null, null, "lastAccess ASC");
	}

	public Cursor queryMessage(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(ChatDBHelper.TABLE_NAME_CHAT_MESSAGER, projection, selection, selectionArgs, null, null, sortOrder);
		return cursor;
	}

	public int updateMessageStatus(ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = helper.getWritableDatabase();
		return db.update(ChatDBHelper.TABLE_NAME_CHAT_MESSAGER, values, selection, selectionArgs);
	}

	public Cursor queryMessageById(int msgId) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String selection = "_id = ?";
		String[] selectionArgs = new String[]{String.valueOf(msgId)};
		Cursor cursor = db.query(ChatDBHelper.TABLE_NAME_CHAT_MESSAGER, null, selection, selectionArgs, null, null, null);
		return cursor;
	}

}
