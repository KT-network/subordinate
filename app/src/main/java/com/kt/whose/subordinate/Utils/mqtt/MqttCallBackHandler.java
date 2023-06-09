package com.kt.whose.subordinate.Utils.mqtt;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttCallBackHandler implements MqttCallbackExtended, IMqttActionListener {
    private String TAG = "MqttCallBackHandler";

    @Override
    public void onSuccess(IMqttToken iMqttToken) {

    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {

    }


    // 创建 mqtt连接状态监听接口
    public interface OnConnectedStateListener{
        void onState(boolean b,String s);
    }

    // 实现连接状态接口
    private OnConnectedStateListener onConnectedStateListener;
    public void setOnConnectedStateListener(OnConnectedStateListener listener){
        this.onConnectedStateListener = listener;
    }

    @Override
    public void connectComplete(boolean b, String s) {

        if (this.onConnectedStateListener != null){
            onConnectedStateListener.onState(b,s);
        }

    }

    @Override
    public void connectionLost(Throwable throwable) {
        Log.i(TAG, "connectionLost: "+throwable.toString());
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        Log.i(TAG, "messageArrived: "+s);
        Log.i(TAG, "messageArrived: "+mqttMessage.toString());


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
