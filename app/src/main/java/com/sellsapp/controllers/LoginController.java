package com.sellsapp.controllers;

import com.sellsapp.models.BasePacket;

public class LoginController extends BasicController {

	private LoginController() {}
	
	private static LoginController listBookController;
	
	public synchronized static LoginController getInstance() {
		if(null == listBookController) {
			listBookController = new LoginController();
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
