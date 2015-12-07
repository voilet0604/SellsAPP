package com.sellsapp.tcp.client;

import com.google.gson.Gson;
import com.sellsapp.tcp.client.Connector.ConnectorListener;
import com.sellsapp.tcp.entity.InvitationRequest;
import com.sellsapp.tcp.entity.OAuthRequst;
import com.sellsapp.tcp.entity.TextRequest;

/**
 * Created by Administrator on 2015/10/15.
 */
public class ConnectManager implements ConnectorListener {

    private static ConnectManager instance;
    
    private static Connector connector;
    
    private ConnectListener listener;
    
    private Gson gson  = new Gson();

    public static ConnectManager getInstance() {
        if(null == instance) {
            instance = new ConnectManager();
        }
        return instance;
    }

    private ConnectManager() {
    }
    
    public void connect(OAuthRequst oAuthRequst){
    	if(null == connector) {
            connector = new Connector();
    	}
    	connector.connect();
    	connector.setListener(this);
    	connector.auth(gson.toJson(oAuthRequst));
    }
    
    public void send(TextRequest textRequest) {
    	connector.send(gson.toJson(textRequest));
    }
    
    public void Invitaion(InvitationRequest request) {
    	connector.send(gson.toJson(request));
    }

	@Override
	public void pushData(String data) {
		if(null != listener) {
			listener.postData(data);
		}
	}
    
	public void desConnect(){
		connector.desconnect();
	}
	public void setListener(ConnectListener listener){
		this.listener = listener;
	}

	public interface ConnectListener{
		void postData(String data);
	}

}
