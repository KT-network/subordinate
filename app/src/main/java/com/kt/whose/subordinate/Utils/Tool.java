package com.kt.whose.subordinate.Utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kt.whose.subordinate.BaseApplication;
import com.kt.whose.subordinate.HttpEntity.Error.ExceptionHelper;
import com.kt.whose.subordinate.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import rxhttp.wrapper.exception.HttpStatusCodeException;
import rxhttp.wrapper.exception.ParseException;

public class Tool {


    public static void Toast(Context context, String msg) {
        Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();

    }

    public static void Toast(Context context, int msg) {
        Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
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
     */
    public static String getWifiDhcpAddress(Context context) {
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


    /**
     * 获取屏幕宽度
     */
    public static int getWidth(Context context) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        return width;
    }


    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static int ErrorInfo(Throwable throwable) {
        int errorCode = -1;
        String errorMsg = null;
        if (throwable instanceof UnknownHostException) {
            if (!ExceptionHelper.isNetworkConnected(BaseApplication.getInstance())) {
                errorMsg = BaseApplication.getInstance().getString(R.string.network_error);

            } else {
                errorMsg = BaseApplication.getInstance().getString(R.string.notify_no_network);

            }
            errorCode = 0;
        } else if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {
            //前者是通过OkHttpClient设置的超时引发的异常，后者是对单个请求调用timeout方法引发的超时异常
            errorMsg = BaseApplication.getInstance().getString(R.string.time_out_please_try_again_later);

            errorCode = 1;
        } else if (throwable instanceof ConnectException) {
            errorMsg = BaseApplication.getInstance().getString(R.string.esky_service_exception);

            errorCode = 2;
        } else if (throwable instanceof HttpStatusCodeException) {
            //请求失败异常
            String code = throwable.getLocalizedMessage();
            if ("416".equals(code)) {
                errorMsg = "请求范围不符合要求";
            } else {
                errorMsg = throwable.getMessage();
            }
            errorCode = 3;
        } else if (throwable instanceof JsonSyntaxException) {
            //请求成功，但Json语法异常,导致解析失败
            errorMsg = "数据解析失败,请稍后再试";
            errorCode = 4;
        } else if (throwable instanceof ParseException) {
            // ParseException异常表明请求成功，但是数据不正确
            if (((ParseException) throwable).getErrorCode().equals("400")){
                errorCode = 5;
            }else {
                errorCode = 6;
            }

            errorMsg = throwable.getMessage();

        } else {
            errorMsg = "未知错误";
            errorCode = 7;
        }
        if (errorCode != 6){
            PopTip.show(errorMsg).iconError();
        }

        return errorCode;
    }


}
