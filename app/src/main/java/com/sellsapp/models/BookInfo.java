package com.sellsapp.models;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Table;

//存到本地数据库表名
@Table(name="SHOPPING_BOOKINFO")
public class BookInfo  implements Serializable{

	private static final long serialVersionUID = 7041680380521361404L;
	
	private String id;
	
	private String bookname;
	
	private String sendway;
	
	private String price;
	
	private String state;
	
	private String img;
	
	private String publish;
	
	private String beizhu;
	
	private String version;
	
	private String sellphone;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBookname() {
		return bookname;
	}

	public void setBookname(String bookname) {
		this.bookname = bookname;
	}

	public String getSendway() {
		return sendway;
	}

	public void setSendway(String sendway) {
		this.sendway = sendway;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getPublish() {
		return publish;
	}

	public void setPublish(String publish) {
		this.publish = publish;
	}

	public String getBeizhu() {
		return beizhu;
	}

	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSellphone() {
		return sellphone;
	}

	public void setSellphone(String sellphone) {
		this.sellphone = sellphone;
	}
	
	
	
}
