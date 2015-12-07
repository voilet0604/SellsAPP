package com.sellsapp.net;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.sellsapp.Public.BaseConfig;


/**
 * 自带session的http请求
 * */
public class HttpRequest {
  
	public static String sessionId=null;
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			URLConnection connection = realUrl.openConnection();
			  if(sessionId!=null) {
				  connection.setRequestProperty("Cookie", sessionId);
			  }
			  //设置请求头
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.connect();
			sessionId=connection.getHeaderField("Set-Cookie");
//			System.out.println("cookie value:"+connection.getHeaderField("Set-Cookie"));
			if(sessionId!=null){
			sessionId=sessionId.substring(0,sessionId.indexOf(";"));  
			}
			Map<String, List<String>> map = connection.getHeaderFields();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
//	获取验证码请求
	public static void getVerycode(String phone) {
//		请求不需要携带sessionId
		sessionId=null;
		/*sendGet("http://10.1.132.35:8080/sells/android_sendcode", "phone="+phone);*/
		sendGet(BaseConfig.sendcode_url, "phone="+phone);
//		执行后，会获取并且设置sessionId，第二次请求的时候会携带sessionId
	}
	
	/*String url = BaseConfig.action_url;
	sendGet(BaseConfig.sendcode_url, "phone="+phone);*/
//	提交
	public static String sendSinginData(String username, String pass,String code, String type) {
		/*String url = "http://10.1.132.35:8080/sells/ACTION";*/
		String url = BaseConfig.action_url;
		if("register".equals(type)) {
			url = url.replace("ACTION", "android_signin");
		}else if("reset".equals(type)){
			url = url.replace("ACTION", "android_repassword");
		}
		String s=sendGet(url, "username="+username+"&password="+pass+"&code="+code);
		JSONObject json =  null;
		try {
			json = new JSONObject(s);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
}