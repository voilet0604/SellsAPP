package com.sellsapp.tcp.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import com.sellsapp.Public.BaseConfig;

/**
 * Created by Administrator on 2015/10/15.
 */
public class Connector {

    private Socket client;

    private ConnectorListener listener;

    private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);

    public Connector() {
    }


    public void connect() {
        try {
            if (client == null || client.isClosed()) {
                client = new Socket(BaseConfig.CHAT_SOCKET_NAME, BaseConfig.CHAT_SOCKET_PORT);
            }

            /**
             * 发送消息
             */
            new Thread(new RequestWorker()).start();

            /**
             * 得到消息
             */
            new Thread(new Runnable() {

                @Override
                public void run() {
                	 InputStream is = null;
                    try {
                    	is = client.getInputStream();
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = is.read(buffer)) != -1) {
                            final String text = new String(buffer, 0, len);

                            System.out.println("text : " + text);

                            if (listener != null) {
                                listener.pushData(text);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                        	if(null != is) {
    							is.close();
                        	}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class RequestWorker implements Runnable{

        @Override
        public void run() {

        	try {
				OutputStream os = client.getOutputStream();
				while(true) {
					String content = queue.take();
					os.write(content.getBytes());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }
    
    public void send(String content){
    	putRequest(content);
    }
    
    public void auth(String auth) {
    	putRequest(auth);
    }
    
    private void putRequest(String content) {
    	try {
			queue.put(content);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    
    public void desconnect(){
    	if(client != null && !client.isClosed()) {
    			try {
					client.close();
					client = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
    	}
    }
    public void setListener(ConnectorListener listener){
        this.listener = listener;
    }

    /**
     * 连接监听
     */
    public interface ConnectorListener {
        void pushData(String data);
    }
}
