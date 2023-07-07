package com.kt.whose.subordinate.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kt.whose.subordinate.Utils.mqtt.KsMqttService;

public abstract class BaseActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    KsMqttService mqttService;
    private boolean servicesState = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayoutId());
        WindowInsetsControllerCompat wic = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (wic != null) {
            // true表示Light Mode，状态栏字体呈黑色，反之呈白色
            wic.setAppearanceLightStatusBars(true);
        }

        initView();
        initEvent();

    }

    protected abstract void initEvent();
    public abstract int initLayoutId();
    public abstract void initView();


    /*public void broadcastService(){
        registerBroadcast();

    }*/
    private void registerBroadcast(){
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    public void broadcastFilter(){
        registerBroadcast();
    }




    public void bindService(){
        servicesState = true;
        Intent intent = new Intent(this,KsMqttService.class);
        bindService(intent,mqttServiceConnection,BIND_AUTO_CREATE);
    }


    private ServiceConnection mqttServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttService = ((KsMqttService.LocalBinder) iBinder).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //mqttService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (servicesState){
            unbindService(mqttServiceConnection);
        }


    }
}
