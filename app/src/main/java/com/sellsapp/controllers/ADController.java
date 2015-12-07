package com.sellsapp.controllers;

import com.sellsapp.models.BasePacket;

/**
 * 作废
 */
@Deprecated
public class ADController extends BasicController {

	private ADController() {
	}
	private static ADController adController;
	
	public static synchronized ADController getInstance() {
		
		if(null == adController) {
			adController = new ADController();
		}
		return adController;
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
