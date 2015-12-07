package com.sellsapp.controllers;

import com.sellsapp.models.BasePacket;

public class ShoppingCartController extends BasicController {

	private ShoppingCartController() {}
	
	private static ShoppingCartController listBookController;
	
	public synchronized static ShoppingCartController getInstance() {
		if(null == listBookController) {
			listBookController = new ShoppingCartController();
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
