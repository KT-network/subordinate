package com.kt.whose.subordinate.Broadcast;

public class BroadcastTag {

    /*
    * mqtt 连接成功
    * */
    public final static String ACTION_MQTT_CONNECTED = "com.kt.whose.subordinate.mqtt.ACTION_MQTT_CONNECTED";

    /*
    * mqtt 断开连接
    * */
    public final static String ACTION_MQTT_DISCONNECTED = "com.kt.whose.subordinate.mqtt.ACTION_MQTT_DISCONNECTED";

    /*
    * 数据可用
    * */
    public final static String ACTION_DATA_AVAILABLE = "com.kt.whose.subordinate.mqtt.ACTION_DATA_AVAILABLE";


    /*
    * 设备连接成功
    * */
    public final static String ACTION_DEVICES_CONNECTED = "com.kt.whose.subordinate.devices.ACTION_DEVICES_CONNECTED";
    /*
    * 设备连接断开连接
    * */
    public final static String ACTION_DEVICES_DISCONNECTED = "com.kt.whose.subordinate.devices.ACTION_DEVICES_DISCONNECTED";

    /*
    * mqtt topic
    * */
    public final static String EXTRA_DATA_TOPIC = "com.kt.whose.subordinate.mqtt.EXTRA_DATA_TOPIC";

    /*
    * mqtt msg
    * */
    public final static String EXTRA_DATA_MESSAGE = "com.kt.whose.subordinate.mqtt.EXTRA_DATA_MESSAGE";
    public final static String EXTRA_ERROR_CODE = "com.kt.whose.subordinate.mqtt.EXTRA_ERROR_CODE";
    public final static String EXTRA_ERROR_MESSAGE = "com.kt.whose.subordinate.mqtt.EXTRA_ERROR_MESSAGE";




}
