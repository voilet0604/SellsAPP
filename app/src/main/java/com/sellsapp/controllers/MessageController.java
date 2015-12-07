package com.sellsapp.controllers;

import com.sellsapp.models.BasePacket;

public class MessageController extends BasicController {

	private MessageController() {}
	
	private static MessageController listBookController;
	
	public synchronized static MessageController getInstance() {
		if(null == listBookController) {
			listBookController = new MessageController();
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
