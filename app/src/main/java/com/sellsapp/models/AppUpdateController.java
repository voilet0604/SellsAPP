package com.sellsapp.models;

import com.sellsapp.controllers.BasicController;

public class AppUpdateController extends BasicController {

	private static AppUpdateController instance;
	public static synchronized AppUpdateController getInstanc(){
		if(null == instance) {
			instance = new AppUpdateController();
		}
		return instance;
	}
	private AppUpdateController() {
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
