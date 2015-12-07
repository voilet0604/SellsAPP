package com.sellsapp.models;

import com.sellsapp.Public.BaseConfig;

public class LoginPacket extends BasePacket {

	public LoginPacket() {
		getBody().put("cmd", BaseConfig.CMD_LOGIN);
	}
	
	public void setUsername(String username) {
		getBody().put("username", username);
	}
	public void setPwd(String pwd) {
		getBody().put("password", pwd);
	}
	
	public String getUserId(){
		if(getBody().containsKey("userId")) {
			return getBody().get("userId").toString();
		}
		return null;
	}
	public String getUsername() {
		if(getBody().containsKey("username")) {
			return getBody().get("username").toString();
		}
		return null;
	}
}
