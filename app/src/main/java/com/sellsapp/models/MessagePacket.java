package com.sellsapp.models;

import java.util.List;

import com.google.gson.reflect.TypeToken;

public class MessagePacket extends BasePacket {

	public MessagePacket() {
		// TODO Auto-generated constructor stub
	}
	public void setUserId(String userId) {
		getBody().put("userId", userId);
	}
	public void setSize(int size) {
		getBody().put("size", size);
	}
	public void setPage(int page) {
		getBody().put("page", page);
	}
	public void setMsgId(String id){
		getBody().put("id", id);
	}
	public String getPagesum(){
		if(getBody().containsKey("pagesum")) {
			return  getBody().get("pagesum").toString();
		}
		return null;
	}
	public List<Message> getMessages() {
		if (getBody().containsKey("datas")) {
			return gson.fromJson(gson.toJson(getBody().get("datas")),	new TypeToken<List<Message>> () {}.getType());
		}
		return null;
	}
} 
