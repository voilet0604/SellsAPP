package com.sellsapp.models;

import com.sellsapp.Public.BaseConfig;

public class AppUpdatePacket extends BasePacket {

	public AppUpdatePacket() {
		getBody().put("cmd", BaseConfig.CMD_UPDATE_APP);
	}
	
	public String getVersion(){
		if(getBody().containsKey("version")) {
			return (String) getBody().get("version");
		}
		return null;
	}
	
	public String getDescription() {
		if(getBody().containsKey("description")) {
			return (String) getBody().get("description");
		}
		return null;
	}
	
	public String getApkUrl() {
		if(getBody().containsKey("url")) {
			return (String) getBody().get("url");
		}
		return null;
	}
}
