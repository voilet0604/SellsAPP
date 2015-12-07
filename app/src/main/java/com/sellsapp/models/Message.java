package com.sellsapp.models;

import java.io.Serializable;

public class Message implements Serializable{


	private static final long serialVersionUID = 3944456718446124326L;

	private String id;
	private String bookId;
	
	private String userId;
	
	private String status;
	
	private String content;
	
	private String time;

	public String getBookId() {
		return bookId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	
}
