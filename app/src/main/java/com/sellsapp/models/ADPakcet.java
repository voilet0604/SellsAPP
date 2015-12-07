package com.sellsapp.models;

import java.util.List;

import com.google.gson.reflect.TypeToken;

//ad
public class ADPakcet extends BasePacket {
	
	public ADPakcet(String cmd) {
		getBody().put("cmd", cmd);
	}
	
	public int getSize() {
		if(getBody().containsKey("size")) {
			return (Integer) getBody().get("size");
		}
		return 0;
	}
	public List<ADImage> getADs() {
		if (getBody().containsKey("datas")) {
			return gson.fromJson(gson.toJson(getBody().get("datas")),	new TypeToken<List<ADImage>> () {}.getType());
		}
		return null;
	}

}
