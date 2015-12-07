package com.sellsapp.models;

import java.io.Serializable;

public class BaseBean implements Serializable {

	private static final long serialVersionUID = -3304031796303888922L;

	private String userid;

	private String cmd;
	
	
	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
}
