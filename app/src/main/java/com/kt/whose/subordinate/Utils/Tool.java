package com.kt.whose.subordinate.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class Tool {


    public static void Toast(Context context,String msg){
        Toast.makeText(context, msg+"", Toast.LENGTH_SHORT).show();

    }

    public static void Toast(Context context,int msg){
        Toast.makeText(context, msg+"", Toast.LENGTH_SHORT).show();
    }


    /*
    * 判断wifi是否连接
    * */
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
    }


    /**
     * 获取wifi ip
     *
     * @param context
     * @return
     */
    public static String getWifiIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return "0";
        }
    }

    /**
     * 获取wifi dhcp
     *
     * @param context
     * @return
     * */
    public static String getWifiDhcpAddress(Context context){
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            int i = dhcpInfo.gateway;
            return int2ip(i);
        } catch (Exception ex) {
            return "0";
        }
    }


    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

}
