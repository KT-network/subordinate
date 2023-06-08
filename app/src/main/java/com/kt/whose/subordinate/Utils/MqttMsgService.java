package com.kt.whose.subordinate.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kt.whose.subordinate.MainActivity;
import com.kt.whose.subordinate.Utils.mqtt.MqttClientManager;

public class MqttMsgService extends Service {

    private static final String TAG = "MqttMsgService";


    private MyBinder binder = new MyBinder();
    /*private OnMsgListener onMsgListener;

    public void setOnMsgListener(OnMsgListener onMsgListener) {
        this.onMsgListener = onMsgListener;
    }


    public interface OnMsgListener{
        void onMsg(String topic,String msg);
    }*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: 只执行一次");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }


    public class MyBinder extends Binder{
        public MyBinder getService(){
            return MyBinder.this;
        }
    }

}
