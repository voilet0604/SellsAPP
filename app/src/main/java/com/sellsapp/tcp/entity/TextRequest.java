package com.sellsapp.tcp.entity;

/**
 * Created by Administrator on 2015/10/15.
 */
public class TextRequest extends Request{

	private static final long serialVersionUID = 3033807661619401182L;

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
