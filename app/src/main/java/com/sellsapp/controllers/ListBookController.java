package com.sellsapp.controllers;

import com.sellsapp.models.BasePacket;

/**
 * 书籍信息相关
 */
public class ListBookController extends BasicController {

	private ListBookController() {}
	
	private static ListBookController listBookController;
	
	public synchronized static ListBookController getInstance() {
		if(null == listBookController) {
			listBookController = new ListBookController();
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
