package com.sellsapp.message.db.entity;

import java.io.Serializable;

public class ChatMessage implements Serializable {

	private static final long serialVersionUID = 4019607502539829783L;

	
	private int _id;
	
	private String content;
	
	private long chat_user_id;
	
	//消息类型，接收还是发送 0-接收 1-发送
	private String type;
	
	private long lastAcces;

	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getChat_user_id() {
		return chat_user_id;
	}

	public void setChat_user_id(long chat_user_id) {
		this.chat_user_id = chat_user_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getLastAcces() {
		return lastAcces;
	}

	public void setLastAcces(long lastAcces) {
		this.lastAcces = lastAcces;
	}
	
	
}
