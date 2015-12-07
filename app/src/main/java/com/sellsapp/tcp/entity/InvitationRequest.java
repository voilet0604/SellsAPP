package com.sellsapp.tcp.entity;

/**
 * 也是第一次用户发送信息的
 * 邀请，用于添加用户
 */
public class InvitationRequest extends Request{


	private static final long serialVersionUID = -7521618007243204799L;
	
	public static final String ACTION_KEY = "invitation";

	private String action;

    private String token;

    private String sender;

    private String receiver;

    private String content;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
