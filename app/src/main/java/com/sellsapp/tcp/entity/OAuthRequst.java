package com.sellsapp.tcp.entity;

/**
 * Created by Administrator on 2015/10/15.
 */
public class OAuthRequst extends Request{


	private static final long serialVersionUID = 5145593182793202952L;

	private String action;

    private String sender;

    private String token;
    
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
