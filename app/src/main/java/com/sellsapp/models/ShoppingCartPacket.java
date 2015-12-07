package com.sellsapp.models;

import java.util.List;

import com.google.gson.reflect.TypeToken;

public class ShoppingCartPacket extends BasePacket {

	public void setUserId(String userId) {
		getBody().put("buyer_id", userId);
	}

	public void setSize(int size) {
		getBody().put("size", size);
	}

	public void setPage(int page) {
		getBody().put("page", page);
	}
	
	public void setState(String state){
		getBody().put("state", state);
	}
	
	public void setId(String id) {
		getBody().put("id", id);
	}

	public String getPageSum() {
		if (getBody().containsKey("pagesum")) {
			return getBody().get("pagesum").toString();
		}
		return null;
	}
	
	public List<ShoppingInfo> getShoppingCart() {
		if (getBody().containsKey("datas")) {
			return gson.fromJson(gson.toJson(getBody().get("datas")),	new TypeToken<List<ShoppingInfo>> () {}.getType());
		}
		return null;
	}
}
