package com.sellsapp.controllers;

import com.sellsapp.models.BasePacket;

/**
 * 图片相关
 */
public class GetImageController extends BasicController {

	private GetImageController() {}
	
	private static GetImageController listBookController;
	
	public synchronized static GetImageController getInstance() {
		if(null == listBookController) {
			listBookController = new GetImageController();
		}
		return listBookController;
	}
	
	@Override
	protected void execBefore(BasePacket basePacket) {
		// TODO Auto-generated method stub

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
