package com.sellsapp.models;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.util.Base64;

import com.google.gson.Gson;

/**
 * 请求数据包基类
 * 
 * @author jefe 2014.3.5
 */
public class BasePacket {
	private String cmd; // 命令字
	private Map<String, Object> body; // 协议内容
	// private Map<String, Object> content;
	private List<NameValuePair> postData = null; // 发送URL
	protected Gson gson = new Gson();
	private boolean actionState = true; // 状态，用来判断是否有异常
	private String actionMessage; // 要吐司的提示

	public BasePacket() {
		// 初始化将协议头写入字典
		// content=new HashMap<String, Object>();
		setBody(new HashMap<String, Object>());

//		getBody().put("timestamp", System.currentTimeMillis() + ""); // 时间戳
//		getBody().put("ostype", BaseConfig.ostype); // 设备系统类型
	}

	// 将发送数据转换成JSON
	public String toJson() {
		// content.put("cmd", cmd);
		// content.put("body", gson.toJson(getBody()));
		return gson.toJson(getBody()); // 把HashMap集合的数据转成String字符串
	}

	public List<NameValuePair> getPostData() {
		return postData;
	}

	/**
	 * Post请求，要发送给服务器的数据 NameValuePair 是键值对信息
	 */
	public void setPostData() {
		this.postData = new ArrayList<NameValuePair>();
		// Set<Entry<String, Object>> entrySet=getBody().entrySet();
		// for(Entry<String, Object> entry:entrySet){
		// postData.add(new
		// BasicNameValuePair(entry.getKey(),entry.getValue().toString()));
		// }
		// "req"是key 对应的是一个json字符串
		postData.add(new BasicNameValuePair("req", toJson()));
		// postData.add(new BasicNameValuePair("cellphone",
		// BaseConfig.cellPhone));
		// postData.add(new BasicNameValuePair("verification",
		// Base64code(toJson(), BaseConfig.password)));
	}

	/**
	 * 获取请求id，根据请求id来判断这次请求是要进行什么操作，获取什么数据
	 * @return
	 */
	public String getRequestid() {
		return cmd;
	}

	/**
	 * 设置请求的id 根据请求id来判断这次请求是要进行什么操作，获取什么数据
	 * @param requestid
	 */
	public void setRequestid(String requestid) {
		this.cmd = requestid;
	}

	/**
	 * 获取协议的内容
	 * @return
	 */
	public Map<String, Object> getBody() {
		return body;
	}

	/**
	 * 设置协议的内容
	 * 包括发出和接收的
	 * @param body
	 */
	public void setBody(Map<String, Object> body) {
		this.body = body;
	}

	/**
	 * 获取时间戳
	 * @return
	 */
	public String getTimestamp() {
		if (getBody().containsKey("timestamp")) {
			return getBody().get("timestamp").toString();
		}

		return "";
	}
	/**
	 * 从返回的body中取出服务器返回的结果
	 * @return
	 */
	public String getResult() {
//		if (getBody().containsKey("result")) {
//			return getBody().get("result").toString();
//		}

		return "";
	}

	/**
	 * 返回服务器的提示的error错误信息
	 * @return
	 */
	public String getErrorstr() {
		if (getBody().containsKey("errorstr")) {
			return getBody().get("errorstr").toString();
		}

		return "";
	}

	
	@SuppressLint("NewApi")
	private String Base64code(String request, String password) {
		String md5Str = request + password;
		MessageDigest md5 = null;

		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			return "";
		}

		md5.reset();

		try {
			md5.update(md5Str.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			return "";
		}

		byte[] b = md5.digest();
		String s = makeString(b);
		String b64result = Base64.encodeToString(s.getBytes(), Base64.DEFAULT);
		return b64result;
	}

	private String makeString(byte[] b) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < b.length; i++) {
			sb.append(String.format("%02x", b[i]));// 以十六进制输出,不足俩位,补0输出
		}

		return sb.toString();
	}

	public boolean isActionState() {
		return actionState;
	}

	public void setActionState(boolean actionState) {
		this.actionState = actionState;
	}

	public String getActionMessage() {
		return actionMessage;
	}

	public void setActionMessage(String actionMessage) {
		this.actionMessage = actionMessage;
	}
}
