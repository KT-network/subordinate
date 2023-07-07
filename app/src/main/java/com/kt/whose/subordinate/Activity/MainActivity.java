package com.kt.whose.subordinate.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.FrameLayout;

import com.kt.whose.subordinate.BaseApplication;
import com.kt.whose.subordinate.Broadcast.BroadcastTag;
import com.kt.whose.subordinate.Fragment.Main.DevicesFragment;
import com.kt.whose.subordinate.Fragment.Main.DiscoverFragment;
import com.kt.whose.subordinate.Fragment.Main.MeFragment;
import com.kt.whose.subordinate.HttpEntity.Login;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.Utils.Preferences;
import com.kt.whose.subordinate.Utils.mqtt.KsMqttService;
import com.kt.whose.subordinate.Utils.mqtt.MqttCallBackHandler;
import com.rxjava.rxlife.RxLife;

import nl.joery.animatedbottombar.AnimatedBottomBar;
import rxhttp.wrapper.param.RxHttp;


/*
 *
 * 策略说明：
 *
 * mqtt登录需要账号密码
 * mqtt的账号密码生成软件唯一的设备id
 * 在配置硬件时需要传入软件设备id（硬件设备绑定软件的设备id），在设备wifi连接失败的情况下，本地重新配置时的识别标识
 * --传入mqtt的连接配置信息（包括mqtt host,post,user,passwd），软硬件设备的通信基于mqtt
 *
 * */
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final String devicesId = "65535-ks";
    Toolbar toolbar;
    FrameLayout frameLayout;
    AnimatedBottomBar bottom_bar;

    DevicesFragment mDevicesFragment;
    DiscoverFragment mDiscoverFragment;
    MeFragment mMeFragment;

//    private KsMqttService mqttService;

    //    private LocalBroadcastManager localBroadcastManager;
//    private IntentFilter intentFilter;
    private Handler handler;

    final FragmentManager supportFragmentManager = getSupportFragmentManager();


    @Override
    public int initLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle(R.string.toolbar_title_devices);
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.main_fragment);
        bottom_bar = findViewById(R.id.main_bottom_bar);
        bottom_bar.setSelectedTabType(AnimatedBottomBar.TabType.ICON);
        bottom_bar.setTabColor(getResources().getColor(R.color.purple_200));
        bottom_bar.setOnTabSelectListener(onTabSelectListener);


        mDevicesFragment = new DevicesFragment();
        mDiscoverFragment = new DiscoverFragment();
        mMeFragment = new MeFragment();

        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        fragmentTransaction.add(R.id.main_fragment, mDevicesFragment);
        fragmentTransaction.add(R.id.main_fragment, mDiscoverFragment);
        fragmentTransaction.add(R.id.main_fragment, mMeFragment);


        fragmentTransaction.show(mDevicesFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void initEvent() {
        Intent intent = new Intent(this, KsMqttService.class);

        startService(intent);

//        bindService(intent, mqttServiceConnection, BIND_AUTO_CREATE);

        /*localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastTag.ACTION_MQTT_CONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_MQTT_DISCONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BroadcastTag.EXTRA_DATA_MESSAGE);
        intentFilter.addAction(BroadcastTag.EXTRA_DATA_TOPIC);
        intentFilter.addAction(BroadcastTag.EXTRA_ERROR_CODE);
        intentFilter.addAction(BroadcastTag.EXTRA_ERROR_MESSAGE);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);*/

        handler = new Handler(Looper.getMainLooper());
        broadcastFilter();
        bindService();


    }

    @Override
    public void broadcastFilter() {
        super.broadcastFilter();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastTag.ACTION_MQTT_CONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_MQTT_DISCONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BroadcastTag.EXTRA_DATA_MESSAGE);
        intentFilter.addAction(BroadcastTag.EXTRA_DATA_TOPIC);
        intentFilter.addAction(BroadcastTag.EXTRA_ERROR_CODE);
        intentFilter.addAction(BroadcastTag.EXTRA_ERROR_MESSAGE);
        intentFilter.addAction(BroadcastTag.ACTION_LOGIN_STATE);
//        intentFilter.addAction(BroadcastTag.ACTION_LOGIN_DISCONNECTED);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    // mqtt 连接状态监听回调
    private MqttCallBackHandler.OnConnectedStateListener onConnectedStateListener = new MqttCallBackHandler.OnConnectedStateListener() {
        @Override
        public void onState(boolean b, String s) {
            Log.i(TAG, "onState: " + b + s);


        }
    };


    private void TabToFragment(int index) {
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:
                toolbar.setTitle(R.string.toolbar_title_devices);
                fragmentTransaction.show(mDevicesFragment);
                break;
            case 1:
                toolbar.setTitle(R.string.toolbar_title_discover);
                fragmentTransaction.show(mDiscoverFragment);
                break;
            case 2:
                toolbar.setTitle(R.string.toolbar_title_me);
                fragmentTransaction.show(mMeFragment);
                break;
        }

        fragmentTransaction.commit();

    }


    //将四个的Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (mDevicesFragment != null) {
            transaction.hide(mDevicesFragment);
        }
        if (mDiscoverFragment != null) {
            transaction.hide(mDiscoverFragment);
        }
        if (mMeFragment != null) {
            transaction.hide(mMeFragment);
        }
    }


    private AnimatedBottomBar.OnTabSelectListener onTabSelectListener = new AnimatedBottomBar.OnTabSelectListener() {
        @Override
        public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {

            TabToFragment(i1);
        }

        @Override
        public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

        }
    };


    /*private final ServiceConnection mqttServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttService = ((KsMqttService.LocalBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //mqttService = null;
        }
    };*/

    private void connectMqtt() {


        mqttService.setHOST(Preferences.getValue("mqtt-host","")/*"192.168.0.7"*/);
        mqttService.setPORT(Preferences.getValue("mqtt-port", 1883));
        mqttService.setDevicesId(BaseApplication.getUserId() + "-app");
        mqttService.setUSERNAME(Preferences.getValue("account", ""));
        mqttService.setPASSWORD(Preferences.getValue("account_pwd", ""));
        mqttService.connect();

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BroadcastTag.ACTION_MQTT_DISCONNECTED.equals(action)) {
                // mqtt 因网络断开连接执行重新连接
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mqttService == null) {
                            connectMqtt();
                            return;
                        }
                        if (!mqttService.isConnected()) {
                            connectMqtt();
                        }

                    }
                }, 1000);
                /*if (!mqttService.getAutoReconnect()) {
                    if (mqttService != null) {
                        if (mqttService.isConnected()) {
                            mqttService.disconnect();
                        }
                    }

                }*/
            } else if (BroadcastTag.ACTION_LOGIN_STATE.equals(action)) {
                if (intent.getBooleanExtra(BroadcastTag.ACTION_LOGIN_STATE, false)) {
                    // 登录成功后开始连接mqtt
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            connectMqtt();
                        }
                    });
                } else {
                    // token过期，mqtt断开连接的msg为null
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            BaseApplication.setToken(null);
                            if (mqttService != null) {
                                if (mqttService.isConnected()) {
                                    mqttService.disconnect();
                                }
                            }
                        }
                    });
                }
            }

        }
    };

    /*private void login() {

        if (Preferences.getValue("account", null) == null) {
            return;
        }
        String account = Preferences.getValue("account", "");
        String pwd = Preferences.getValue("account_pwd", "");
        RxHttp.postJson("/user/login").add("user", account).add("pwd", pwd).toObservableResponse(Login.class).to(RxLife.toMain(this)).subscribe(s -> {

            Preferences.setValue("token", s.getToken());
        }, throwable -> {

        });

    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        Intent intent = new Intent(MainActivity.this, KsMqttService.class);
        stopService(intent);

    }
}