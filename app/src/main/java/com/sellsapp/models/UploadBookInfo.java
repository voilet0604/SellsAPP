package com.sellsapp.models;

import java.io.Serializable;


public class UploadBookInfo implements Serializable {


	private static final long serialVersionUID = -3680269554566033879L;

		// 要上传的书信息
		private String grade; //适用范围
		
		private String degree; //新旧程度
		
		private String mobile; //手机号
		
		private String booknum; //出售数量
		
		private String price; //出售价格
		
		private String author; //作者
		
		private String id; //id，取豆瓣id
		
		private String bookname; //书名
		
		private String version; //版本
		
		private String publisher; //出版商
		
		private String address; //库存地址
		
		private String remark; //备注
		
		private String userId;

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getGrade() {
			return grade;
		}

		public void setGrade(String grade) {
			this.grade = grade;
		}

		public String getDegree() {
			return degree;
		}

		public void setDegree(String degree) {
			this.degree = degree;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getBooknum() {
			return booknum;
		}

		public void setBooknum(String booknum) {
			this.booknum = booknum;
		}

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

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

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getPublisher() {
			return publisher;
		}

		public void setPublisher(String publisher) {
			this.publisher = publisher;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

	
		
}
