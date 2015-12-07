package com.sellsapp.controllers;

import java.util.Map;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.models.BasePacket;

/**
 * 主方法，界面逻辑执行的入口
 */
public abstract class BasicController {
	private Gson gson = null;

	/**
	 * 派生类验证逻辑执行方法，发送前调用
	 * 
	 * @param basePacket
	 *            数据协议包
	 */
	protected abstract void execBefore(BasePacket basePacket);

	/**
	 * 派生类验证逻辑执行方法，用于接受成功后操作
	 * 
	 * @param basePacket
	 */
	protected abstract void execAfter(BasePacket basePacket);

	/**
	 * 主方法，用于发送前操作 主要是为了检查数据
	 * 
	 * @param basePacket
	 *            数据协议包
	 */
	public void execute(BasePacket basePacket) {
		try {
			if (basePacket == null) {
				return;
			}

			// 逻辑处理
			execBefore(basePacket);

			// 生成发送URL
			basePacket.setPostData();
		} catch (Exception ex) {
			basePacket.setActionState(false);
			basePacket.setActionMessage(BaseConfig.action_error_other);
		}
	}

	/**
	 * 主方法，用于接受成功后的操作
	 * 
	 * @param responseStr
	 *            接受的JSON数据
	 * @return basePacket数据协议包
	 */
	public BasePacket execute(String responseStr) {
		BasePacket basePacket = new BasePacket();
		// try
		// {
		if (TextUtils.isEmpty(responseStr)) {
			basePacket.setActionState(false);
			basePacket.setActionMessage(BaseConfig.action_error_null);
			return basePacket;
		}

		// 将JSON字符串转换为接受包对象
		gson = new Gson();
		/**
		 * 服务器返回的数据已键值对的形式返回
		 */
		Map<String, Object> retMap = gson.fromJson(responseStr,new TypeToken<Map<String, Object>>() {}.getType());
		String code = retMap.get("resultcode").toString();
		if (code.equals("false")) {
			basePacket.setActionState(false);
			basePacket.setActionMessage(retMap.get("errorstr").toString());
			return basePacket;
		}
		Map<String, Object> retMap1 = gson.fromJson(
				gson.toJson(retMap.get("body")),
				new TypeToken<Map<String, Object>>() {
				}.getType());
		basePacket.setBody(retMap1);
		basePacket.setActionState(true);

		execAfter(basePacket);

		return basePacket;
	}
}
