package com.sellsapp.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.sellsapp.models.BasePacket;

public class HttpClient {
	private static HttpClient httpClient = null;

	private HttpClient() {
	}

	public synchronized static HttpClient getInstance() {
		if (httpClient == null) {
			httpClient = new HttpClient();
		}

		return httpClient;
	}

	/**
	 * 发送数据包到服务端，并且获取响应
	 * @param url
	 * @param basePacket
	 * @return
	 */
	public String postRequest(String url, BasePacket basePacket) {
		InputStream is = null;
		BufferedReader br = null;
		try {
			HttpParams httpParams = new BasicHttpParams();

			ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
					new ConnPerRouteBean(100));
			ConnManagerParams.setMaxTotalConnections(httpParams, 100);

			// 是否考虑加上超时时间设置，遇到的问题是地销数据从服务器获取的时候，花了非常长的时间超过2分钟才能获取到。是否考虑超过15秒就应该断开链接
			/* 从连接池获取连接超时 */
			// ConnManagerParams.setTimeout(httpParams, 3 * 1000);
			/* 连接服务器超时 */
			// HttpConnectionParams.setConnectionTimeout(httpParams, 60 * 1000);
			/* 等待服务器响应超时 */
			// HttpConnectionParams.setSoTimeout(httpParams, 60 * 1000);

			httpParams.setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

			HttpPost request = new HttpPost(url);
			request.setEntity(new UrlEncodedFormEntity(
					basePacket.getPostData(), HTTP.UTF_8));
			request.setParams(httpParams);

			HttpResponse response = new DefaultHttpClient().execute(request);
			// 页面请求的状态值，分别有：200请求成功、303重定向、400请求错误、
			// 401未授权、403禁止访问、404文件未找到、500服务器错误
			if (response.getStatusLine().getStatusCode() != 200) {
				return null;
			}

			is = response.getEntity().getContent();// 得到输入流
			br = new BufferedReader(new InputStreamReader(is), 8192);
			StringBuffer buffer = new StringBuffer();
			String str;

			while ((str = br.readLine()) != null) {
				buffer.append(str);
			}
			return buffer.toString();
		} catch (ConnectTimeoutException e) { /* 捕获这个两个异常，发送广播连接超时，预留 */
			// Log.i(TAG, "连接服务器超时");
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			// Log.i(TAG, "获取数据超时");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param url
	 * @param basePacket
	 * @param timeout
	 *            从连接池获取连接超时
	 * @param connectionTimeout
	 *            连接服务器超时
	 * @param socketTimeout
	 *            等待服务器响应超时
	 * @return
	 */
	public String postRequest(String url, BasePacket basePacket, long timeout,
			int connectionTimeout, int socketTimeout) {
		InputStream is = null;
		BufferedReader br = null;
		try {
			HttpParams httpParams = new BasicHttpParams();

			ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
					new ConnPerRouteBean(100));
			ConnManagerParams.setMaxTotalConnections(httpParams, 100);

			// 是否考虑加上超时时间设置，遇到的问题是地销数据从服务器获取的时候，花了非常长的时间超过2分钟才能获取到。是否考虑超过15秒就应该断开链接
			/* 从连接池获取连接超时 */
			ConnManagerParams.setTimeout(httpParams, timeout);
			/* 连接服务器超时 */
			HttpConnectionParams.setConnectionTimeout(httpParams,
					connectionTimeout);
			/* 等待服务器响应超时 */
			HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);

			httpParams.setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

			HttpPost request = new HttpPost(url);
			request.setEntity(new UrlEncodedFormEntity(
					basePacket.getPostData(), HTTP.UTF_8));
			request.setParams(httpParams);

			HttpResponse response = new DefaultHttpClient().execute(request);
			// 页面请求的状态值，分别有：200请求成功、303重定向、400请求错误、
			// 401未授权、403禁止访问、404文件未找到、500服务器错误
			if (response.getStatusLine().getStatusCode() != 200) {
				return null;
			}

			is = response.getEntity().getContent();// 得到输入流
			br = new BufferedReader(new InputStreamReader(is), 8192);
			StringBuffer buffer = new StringBuffer();
			String str;

			while ((str = br.readLine()) != null) {
				buffer.append(str);
			}
			return buffer.toString();
		} catch (ConnectTimeoutException e) { /* 捕获这个两个异常，发送广播连接超时，预留 */
			// Log.i(TAG, "连接服务器超时");
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			// Log.i(TAG, "获取数据超时");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 对url的地址发送get方式的Http请求 主要针对https请求使用，HttpClient 无法访问https
	 * 
	 * @param String
	 *            url
	 * @return String
	 */
	public String getHttpRequest(String url) {
		String content = "";
		try {
			URL getUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) getUrl
					.openConnection();
			connection.connect();
			// 取得输入流，并使用Reader读取
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String lines = "";
			while ((lines = reader.readLine()) != null) {
				content += lines;
			}
			reader.close();
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public String upload(String urlstr, String filename, byte[] data,
			Map<String, String> params) throws UnsupportedEncodingException {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成 String
														// PREFIX
		StringBuffer actionUrl = new StringBuffer(urlstr);
		// System.out.println(actionUrl.toString());
		HttpURLConnection con = null;
		URL url;
		try {
			url = new URL(actionUrl.toString());
			con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设定传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			con.setConnectTimeout(1200);
			con.setReadTimeout(1200);

			/* 设定DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());

			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"file1\";filename=\"" + filename + "\";" + end);

			ds.write(data, 0, data.length);
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			ds.flush();

			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}

			/* 关闭DataOutputStream */
			ds.close();
			return b.toString();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (con != null)
				con.disconnect();
		}

		return null;
	}

	/**
	 * 上传图片和参数
	 * @param actionUrl
	 * @param newName
	 * @param uploadFile
	 * @return
	 */
	public String up(String actionUrl, String newName, File uploadFile) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			DataOutputStream ds = null;
				/* 设置DataOutputStream */
				ds = new DataOutputStream(con.getOutputStream());
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; "
						+ "name=\"file1\";filename=\"" + newName + "\"" + end);
				ds.writeBytes(end);
				/* 取得文件的FileInputStream */
				FileInputStream fStream = new FileInputStream(uploadFile);
				/* 设置每次写入1024bytes */
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				int length = -1;
				/* 从文件读取数据至缓冲区 */
				while ((length = fStream.read(buffer)) != -1) {
					/* 将资料写入DataOutputStream中 */
					ds.write(buffer, 0, length);
				}
				ds.writeBytes(end);
				ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
				/* close streams */
				fStream.close();
				ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			ds.close();
			return b.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
