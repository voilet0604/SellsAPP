package com.sellsapp.models;

public class GetPicPacket extends BasePacket {

	public void setUserId(String userId){
		getBody().put("userId", userId);
	}
}
