package com.sellsapp.models;

import com.sellsapp.Public.BaseConfig;

public class CodePacket extends BasePacket {

	public CodePacket() {
		getBody().put("cmd", BaseConfig.CMD_GET_CODE);
	}
	
	public void setPhone(String phone) {
		getBody().put("phone", phone);
	}
	
}
