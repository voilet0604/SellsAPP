package com.sellsapp.controllers;

import com.sellsapp.models.BasePacket;

public class RegisterController extends BasicController {

	private RegisterController() {}
	
	private static RegisterController listBookController;
	
	public synchronized static RegisterController getInstance() {
		if(null == listBookController) {
			listBookController = new RegisterController();
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
