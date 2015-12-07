package com.sellsapp.models;

import com.sellsapp.Public.BaseConfig;

public class RegisterPacket extends BasePacket {

	public RegisterPacket() {
		getBody().put("cmd", BaseConfig.CMD_REGISTER);
	}
	
	public void setPhone(String phone) {
		getBody().put("phone", phone);
	}
	
	public void setCode(String code) {
		getBody().put("code", code);
	}
	
	public void setPwd(String pwd) {
		getBody().put("password", pwd);
	}
}
