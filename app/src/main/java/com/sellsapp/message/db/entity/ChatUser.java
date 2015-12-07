package com.sellsapp.message.db.entity;

import java.io.Serializable;

public class ChatUser implements Serializable {


	private static final long serialVersionUID = 5918042837918986104L;
	
	private int _id;
	
	private String sender;
	
	private String receiver;
	
	private String status;
	
	private long lastAccess;


	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}

	@Override
	public String toString() {
		return "ChatUser [_id=" + _id + ", sender=" + sender + ", receiver="
				+ receiver + ", status=" + status + ", lastAccess="
				+ lastAccess + "]";
	}

}
