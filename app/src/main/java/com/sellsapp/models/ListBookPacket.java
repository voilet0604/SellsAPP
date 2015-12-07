package com.sellsapp.models;

import java.util.List;

import com.google.gson.reflect.TypeToken;

public class ListBookPacket extends BasePacket {
	
	
	public ListBookPacket() {
	}
	
	public void setCmd(String cmd){
		getBody().put("cmd", cmd);
	}

	public void setSize(int size) {
		
		getBody().put("size", size);
	}
	
	public void setPage(int page) {
		getBody().put("page", page);
	}
	public String getCmd() {
		if(getBody().containsKey("cmd")) {
			return (String) getBody().get("cmd");
		}
		return null;
	}
	public void setBookName(String bookname) {
		getBody().put("bookname", bookname);
	}
	
	public String getPageSum() {
		if(getBody().containsKey("pagesum")) {
			return getBody().get("pagesum").toString();
		}
		return null;
	}
	

	public List<BookInfo> getBookInfos() {
		if (getBody().containsKey("datas")) {
			return gson.fromJson(gson.toJson(getBody().get("datas")),	new TypeToken<List<BookInfo>> () {}.getType());
		}
		return null;
	}
}

