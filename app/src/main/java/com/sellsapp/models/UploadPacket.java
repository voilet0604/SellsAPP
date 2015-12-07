package com.sellsapp.models;

import com.sellsapp.Public.BaseConfig;

public class UploadPacket extends BasePacket {

	public UploadPacket() {
		getBody().put("cmd", BaseConfig.CMD_UPLOAD_BOOKINFO);
	}
	
	public void setPrice(String price) {
		getBody().put("price", price);
	}
	
	public void setBookName(String bookname) {
		getBody().put("bookname", bookname);
	}
	public void setAddress(String address) {
		getBody().put("address", address);
	}
	
	public void setDegree(String degree) {
		getBody().put("degree", degree);
	}
	
	public void setVersion(String grade) {
		getBody().put("version", grade);
	}
	
	public void setRemark(String remark) {
		getBody().put("remark", remark);
	}
	
	public void setBookNum(String num){
		getBody().put("num", num);
	}
	
	public void setUserId(String userId){
		getBody().put("userId", userId);
		
	}
	public void setImgUrl(String url){
		getBody().put("img", url);
	}
	public void setPublish(String publish) {
		getBody().put("publish", publish);
	}
	
	public void setMobile(String mobile) {
		getBody().put("mobile", mobile);
	}
	public void setAuthor(String author) {
		getBody().put("zuozhe", author);
	}
}
