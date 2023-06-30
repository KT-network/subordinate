package com.kt.whose.subordinate.Utils.mqtt;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kt.whose.subordinate.Broadcast.BroadcastTag;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.net.DatagramSocket;

public class KsMqttService extends Service {

    private final static String TAG = "MqttService";

    /*public final static String ACTION_MQTT_CONNECTED = "com.kt.whose.subordinate.mqtt.ACTION_MQTT_CONNECTED";
    public final static String ACTION_MQTT_DISCONNECTED = "com.kt.whose.subordinate.mqtt.ACTION_MQTT_DISCONNECTED";
    public final static String ACTION_DATA_AVAILABLE = "com.kt.whose.subordinate.mqtt.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA_TOPIC = "com.kt.whose.subordinate.mqtt.EXTRA_DATA_TOPIC";
    public final static String EXTRA_DATA_MESSAGE = "com.kt.whose.subordinate.mqtt.EXTRA_DATA_MESSAGE";
    public final static String EXTRA_ERROR_CODE = "com.kt.whose.subordinate.mqtt.EXTRA_ERROR_CODE";
    public final static String EXTRA_ERROR_MESSAGE = "com.kt.whose.subordinate.mqtt.EXTRA_ERROR_MESSAGE";*/


    private String HOST;
    private int PORT;
    private String USERNAME;
    private String PASSWORD;
    private String devicesId;
    private boolean isConnect = false;
    private int connectionTimeout = 120;
    private int keepAliveInterval = 20;
    private boolean autoReconnect = false;

    public boolean loginDisconnected = false;

    MqttAsyncClient mqttClient = null;
    DatagramSocket datagramSocket = null;
    private LocalBroadcastManager localBroadcastManager;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public KsMqttService getService() {
            return KsMqttService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastTag.ACTION_LOGIN_STATE);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BroadcastTag.ACTION_LOGIN_STATE.equals(action)) {
                loginDisconnected = intent.getBooleanExtra(BroadcastTag.ACTION_LOGIN_STATE, false);
            }
        }
    };

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public void setDevicesId(String devicesId) {
        this.devicesId = devicesId;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public boolean getAutoReconnect() {
        return this.autoReconnect;
    }

    public void connect() {

        MemoryPersistence persistence = new MemoryPersistence();

        MqttConnectOptions options = new MqttConnectOptions();
        if (this.USERNAME != null && this.PASSWORD != null) {
            options.setUserName(this.USERNAME);
            options.setPassword(this.PASSWORD.toCharArray());
        }

        options.setConnectionTimeout(this.connectionTimeout);
        options.setKeepAliveInterval(this.keepAliveInterval);
        options.setAutomaticReconnect(false);

        try {

            if (mqttClient == null) {
                mqttClient = new MqttAsyncClient("tcp://" + this.HOST + ":" + this.PORT, this.devicesId, persistence);
            }

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    String message = null;
                    int error_code = -1;
                    if (cause != null && cause instanceof MqttException) {
                        error_code = ((MqttException) cause).getReasonCode();
                    }
                    if (cause != null && cause.getCause() != null)
                        message = cause.getCause().getMessage();
                    else message = cause.getMessage();

                    Log.i(TAG, "onFailure1消息回调监听: " + error_code + message);

                    if (message == null && error_code == 32109) {
                        broadcastUpdateLoginState(false);
                    } else {
                        broadcastUpdate(BroadcastTag.ACTION_MQTT_DISCONNECTED, error_code, message);
                    }

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

                    broadcastUpdate(BroadcastTag.ACTION_DATA_AVAILABLE, s, mqttMessage);

                    //Log.i(TAG, "messageArrived: " + s + "\n" + mqttMessage.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });

            mqttClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {

                    broadcastUpdate(BroadcastTag.ACTION_MQTT_CONNECTED);
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable exception) {
                    String message = null;
                    int error_code = -1;
                    if (exception != null && exception instanceof MqttException) {
                        error_code = ((MqttException) exception).getReasonCode();
                    }

                    if (exception != null && exception.getCause() != null)
                        message = exception.getCause().getMessage();
                    else message = exception.getMessage();
                    Log.i(TAG, "onFailure连接回调: " + error_code + message);
                    if (message == null && error_code == 32109) {
                        broadcastUpdateLoginState(false);
                    } else {
                        broadcastUpdate(BroadcastTag.ACTION_MQTT_DISCONNECTED, error_code, message); //连接失败
                    }

                }
            });


        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    public boolean isConnected() {
        if (mqttClient == null) return false;
        return mqttClient.isConnected();
    }


    public void disconnect() {
        try {
            if (mqttClient != null)
                mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void subscribe(String topic, int qos) {
        try {
            mqttClient.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    public boolean subscribe(String[] topic, int[] qos) {
        try {
            mqttClient.subscribe(topic, qos);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            disconnect();
            return false;
        }
    }

    public void unsubscribe(String topic) {
        try {
            mqttClient.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
            disconnect();
        }
    }


    public void unsubscribe(String[] topic) {
        try {
            mqttClient.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    public void publish(String topic, String str) {
        publish(topic, str, 0);
    }

    public void publish(String topic, String str, int qos) {
        try {
            MqttMessage message = new MqttMessage(str.getBytes());
            message.setQos(qos);
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    void broadcastUpdate(String action, String topic, MqttMessage mqttMessage) {
        Intent intent = new Intent(action);
        intent.putExtra(BroadcastTag.EXTRA_DATA_TOPIC, topic);
        intent.putExtra(BroadcastTag.EXTRA_DATA_MESSAGE, mqttMessage.toString());
        localBroadcastManager.sendBroadcast(intent);
    }

    void broadcastUpdate(String action, int error_code, String message) {
        final Intent intent = new Intent(action);
        intent.putExtra(BroadcastTag.EXTRA_ERROR_CODE, error_code);
        if (message != null && !message.isEmpty())
            intent.putExtra(BroadcastTag.EXTRA_ERROR_MESSAGE, message);
        localBroadcastManager.sendBroadcast(intent);
    }

    void broadcastUpdate(String action) {
        final Intent intent = new Intent(action);
        localBroadcastManager.sendBroadcast(intent);
    }

    void broadcastUpdateLoginState(boolean is) {

        Intent intent = new Intent(BroadcastTag.ACTION_LOGIN_STATE);
        intent.putExtra(BroadcastTag.ACTION_LOGIN_STATE, is);
        localBroadcastManager.sendBroadcast(intent);
    }

}
