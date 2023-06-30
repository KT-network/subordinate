package com.kt.whose.subordinate.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SocketDataBase {

    private static final String TAG = "SocketDataBase";

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
            JSONObject data = new JSONObject();

            JSONArray ips = new JSONArray();
            String[] ip_split = ip.split("\\.");

            ips.put(Integer.parseInt(ip_split[0]));
            ips.put(Integer.parseInt(ip_split[1]));
            ips.put(Integer.parseInt(ip_split[2]));
            ips.put(Integer.parseInt(ip_split[3]));

            JSONArray maskCodes = new JSONArray();
            String[] mask_split = maskCode.split("\\.");
            maskCodes.put(Integer.parseInt(mask_split[0]));
            maskCodes.put(Integer.parseInt(mask_split[1]));
            maskCodes.put(Integer.parseInt(mask_split[2]));
            maskCodes.put(Integer.parseInt(mask_split[3]));

            JSONArray gateways = new JSONArray();
            String[] gateways_split = gateway.split("\\.");
            gateways.put(Integer.parseInt(gateways_split[0]));
            gateways.put(Integer.parseInt(gateways_split[1]));
            gateways.put(Integer.parseInt(gateways_split[2]));
            gateways.put(Integer.parseInt(gateways_split[3]));


//            JSONObject jsonObject1 = new JSONObject();
            data.put("dhcp",dhcp);
            data.put("ssid",ssid);
            data.put("pwd",pwd);
            data.put("ip",ips);
            data.put("maskCode",maskCodes);
            data.put("gateway",gateways);
            data.put("dns",gateways);
            jsonObject.put("data",data);


//            jsonObject.put("data",jsonObject1);

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

            JSONArray ips = new JSONArray();
            String[] ip_split = ip.split("\\.");

            ips.put(Integer.parseInt(ip_split[0]));
            ips.put(Integer.parseInt(ip_split[1]));
            ips.put(Integer.parseInt(ip_split[2]));
            ips.put(Integer.parseInt(ip_split[3]));

            JSONArray maskCodes = new JSONArray();
            String[] mask_split = maskCode.split("\\.");
            maskCodes.put(Integer.parseInt(mask_split[0]));
            maskCodes.put(Integer.parseInt(mask_split[1]));
            maskCodes.put(Integer.parseInt(mask_split[2]));
            maskCodes.put(Integer.parseInt(mask_split[3]));

            JSONArray gateways = new JSONArray();
            String[] gateways_split = gateway.split("\\.");
            gateways.put(Integer.parseInt(gateways_split[0]));
            gateways.put(Integer.parseInt(gateways_split[1]));
            gateways.put(Integer.parseInt(gateways_split[2]));
            gateways.put(Integer.parseInt(gateways_split[3]));


            jsonObjectWifi.put("dhcp",dhcp);
            jsonObjectWifi.put("ssid",ssid);
            jsonObjectWifi.put("pwd",pwd);
            jsonObjectWifi.put("ip",ips);
            jsonObjectWifi.put("maskCode",maskCodes);
            jsonObjectWifi.put("gateway",gateways);
            jsonObjectWifi.put("dns",gateways);

            JSONObject jsonObjectMqtt = new JSONObject();

            JSONArray hosts = new JSONArray();
            String[] hosts_split = host.split("\\.");
            hosts.put(Integer.parseInt(hosts_split[0]));
            hosts.put(Integer.parseInt(hosts_split[1]));
            hosts.put(Integer.parseInt(hosts_split[2]));
            hosts.put(Integer.parseInt(hosts_split[3]));

            jsonObjectMqtt.put("host",hosts);
            jsonObjectMqtt.put("port",Integer.parseInt(port));
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


    public static String getRestart(){
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("type","restart");
            /*JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("handshake","hello subordinate");
            jsonObject1.put("id","65535-ks");
            jsonObject.put("data",jsonObject1);*/

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject.toString();
    }


}
