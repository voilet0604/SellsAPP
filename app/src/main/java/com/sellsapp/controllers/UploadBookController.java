package com.sellsapp.controllers;

import com.sellsapp.models.BasePacket;

public class UploadBookController extends BasicController {

	private UploadBookController() {}
	
	private static UploadBookController listBookController;
	
	public synchronized static UploadBookController getInstance() {
		if(null == listBookController) {
			listBookController = new UploadBookController();
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
