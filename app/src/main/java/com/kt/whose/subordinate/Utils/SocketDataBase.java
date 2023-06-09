package com.kt.whose.subordinate.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class SocketDataBase {

    public static String getHandshakeData(){
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("type","handshake");
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("handshake","hello subordinate");
            jsonObject1.put("id","65535-ks");
            jsonObject.put("data",jsonObject1);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject.toString();
    }

    public static String getDevicesIdData(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type","getDevicesId");

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id","65535-ks");
            jsonObject.put("data",jsonObject1);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject.toString();
    }

    public static String verifyDevicesInfoData(int dhcp,String ssid,String pwd,String ip,String maskCode,String gateway){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type","verifyDevicesInfo");

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("dhcp",dhcp);
            jsonObject1.put("ssid",ssid);
            jsonObject1.put("pwd",pwd);
            jsonObject1.put("ip",ip);
            jsonObject1.put("maskCode",maskCode);
            jsonObject1.put("gateway",gateway);
            jsonObject1.put("dns",gateway);


            jsonObject.put("data",jsonObject1);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject.toString();

    }

    public static String setDevicesInfoData(int dhcp,String ssid,String pwd,String ip,String maskCode,String gateway,String host,String port,String user,String mqttPwd){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type","setDevicesInfo");
            JSONObject jsonObjectWifi = new JSONObject();
            jsonObjectWifi.put("dhcp",dhcp);
            jsonObjectWifi.put("ssid",ssid);
            jsonObjectWifi.put("pwd",pwd);
            jsonObjectWifi.put("ip",ip);
            jsonObjectWifi.put("maskCode",maskCode);
            jsonObjectWifi.put("gateway",gateway);
            jsonObjectWifi.put("dns",gateway);

            JSONObject jsonObjectMqtt = new JSONObject();
            jsonObjectMqtt.put("host",host);
            jsonObjectMqtt.put("port",port);
            jsonObjectMqtt.put("user",user);
            jsonObjectMqtt.put("pwd",mqttPwd);



            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("wifi",jsonObjectWifi);
            jsonObject1.put("mqtt",jsonObjectMqtt);
            jsonObject1.put("id","65535-ks");

            jsonObject.put("data",jsonObject1);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject.toString();
    }


}
