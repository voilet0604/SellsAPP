package com.sellsapp.tcp.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/15.
 */
public class Request implements Serializable{

	private static final long serialVersionUID = -3309456938404313197L;

	private String sequence;

    private String type;

    private String flag;
    
    private long lastAccess;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

	public long getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}
    
    
}


