package com.kt.whose.subordinate.Utils.mqtt;

import android.content.Context;
import android.util.Log;


import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;


public class MqttClientManager {

    private static final String TAG = "MqttClientManager";
//    private MqttClient client;
    private MqttClient client;
    private static MqttClientManager manager;
    private Context mContext;

    private String HOST;
    private int PORT;
    private String USERNAME;
    private String PASSWORD;
    private String devicesId;
    private boolean isConnect = false;
    private int connectionTimeout = 120;
    private int keepAliveInterval = 20;
    private boolean autoReconnect = false;
    private MqttCallBackHandler callBackHandler;
    private IMqttActionListener mqttActionListener;


    private MqttClientManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static MqttClientManager getInstance(Context context){
        if (manager == null) {
            synchronized (MqttClientManager.class) {
                if (manager == null) {
                    manager = new MqttClientManager(context);
                }
            }
        }
        return manager;
    }


    public MqttClientManager setBroker(String host, int port) {
        if (!manager.isConnect) {
            manager.HOST = host;
            manager.PORT = port;
        }
        return manager;

    }

    public MqttClientManager setUserNamePass(String user, String passwd) {
        if (!manager.isConnect) {
            manager.USERNAME = user;
            manager.PASSWORD = passwd;
        }
        return manager;
    }

    public MqttClientManager setConnectTimeout(int timeOut) {

        if (!manager.isConnect) {
            manager.connectionTimeout = timeOut;
        }

        return manager;
    }


    public MqttClientManager setKeepAliveInterval(int interval) {
        if (!manager.isConnect) {
            manager.keepAliveInterval = interval;
        }
        return manager;
    }


    public MqttClientManager setAutoReconnect(boolean auto) {
        if (!manager.isConnect) {
            manager.autoReconnect = auto;
        }
        return manager;
    }

    public MqttClientManager setDevicesId(String id) {
        if (!manager.isConnect) {
            manager.devicesId = id;
        }
        return manager;
    }

    public MqttClientManager setCallBackHandler(MqttCallBackHandler callBackHandler){
        if (!manager.isConnect) {
            manager.callBackHandler = callBackHandler;
        }
        return manager;

    }

    public MqttClientManager setMqttActionListener(IMqttActionListener listener){
        if (!manager.isConnect) {
            manager.mqttActionListener = listener;
        }
        return manager;

    }



    public void connect() throws MqttException {
        manager.client = new MqttClient("tcp://"+manager.HOST+":"+manager.PORT,devicesId, (MqttClientPersistence) manager.mqttActionListener);
        manager.isConnect = true;
        MqttConnectOptions options = new MqttConnectOptions();
        if (manager.USERNAME != null && manager.PASSWORD != null) {
            options.setUserName(manager.USERNAME);
            options.setPassword(manager.PASSWORD.toCharArray());
        }
        options.setConnectionTimeout(manager.connectionTimeout);
        options.setKeepAliveInterval(manager.keepAliveInterval);
        options.setAutomaticReconnect(manager.autoReconnect);
        manager.client.setCallback(manager.callBackHandler);
        try {
            manager.client.connect(options);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

    }


    public void disconnect() {
        try {
            if (manager.client == null || !manager.isConnect) {
                onTrace("disconnect status invalid");
                return;
            }
            manager.client.disconnect();
            manager.isConnect = false;
        }catch (MqttException e){
            onTrace("disconnect:",e);
        }
    }

    public void addSubscribeTopic(String topic) {

        try {
            if (manager.client == null || !manager.isConnect) {
                onTrace("Connection status invalid. \n manager: " + manager + "\n connected: " + manager.isConnect);
                return;
            }
            manager.client.subscribe(topic,0);
        }catch (MqttException e){
            onTrace("addSubscribeTopic" + topic, e);
        }


    }


    public void removeSubscribeTopic(String topic) {
        try {
            if (manager.client == null || !manager.isConnect) {
                onTrace("Connection status invalid. \n manager: " + manager + "\n connected: " + manager.isConnect);
                return;
            }
            manager.client.unsubscribe(topic);
        } catch (Exception e) {
            onTrace("removeSubscribeTopic" + topic, e);
        }


    }



    private void onTrace(String s) {
        Log.d(TAG, "onTrace: " + s);
    }

    private void onTrace(String s, Exception e) {
        Log.e(TAG, "onTrace: " + s, e);
    }


}
