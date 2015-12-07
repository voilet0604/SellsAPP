package com.sellsapp.controllers;

import com.sellsapp.models.BasePacket;

/**
 * 验证码相关
 */
public class CodeController extends BasicController {

	private CodeController() {}
	
	private static CodeController listBookController;
	
	public synchronized static CodeController getInstance() {
		if(null == listBookController) {
			listBookController = new CodeController();
		}
		return listBookController;
	}
	
	@Override
	protected void execBefore(BasePacket basePacket) {
	}

	@Override
	protected void execAfter(BasePacket basePacket) {
		if("false".equals(basePacket.getResult())){
			basePacket.setActionState(false);
			basePacket.setActionMessage(basePacket.getErrorstr());
			return;
		}

	}

}
