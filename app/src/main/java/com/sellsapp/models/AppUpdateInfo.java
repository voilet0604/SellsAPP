package com.sellsapp.models;

import java.io.Serializable;

public class AppUpdateInfo implements Serializable{


	private static final long serialVersionUID = 3108273606785178123L;
	private String version;
	private String description;
	private String apkurl;
	
	

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getApkurl() {
		return apkurl;
	}
	public void setApkurl(String apkurl) {
		this.apkurl = apkurl;
	}

	public AppUpdateInfo(String version, String description, String apkurl) {
		super();
		this.version = version;
		this.description = description;
		this.apkurl = apkurl;
	}
	public AppUpdateInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "AppUpdateInfo [version=" + version + ", description="
				+ description + ", apkurl=" + apkurl + "]";
	}

	
}
