package com.kt.whose.subordinate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.AsyncQueryHandler;
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
import android.os.Message;
import android.util.Log;
import android.widget.FrameLayout;

import com.kt.whose.subordinate.Broadcast.BroadcastTag;
import com.kt.whose.subordinate.Fragment.Main.DevicesFragment;
import com.kt.whose.subordinate.Fragment.Main.DiscoverFragment;
import com.kt.whose.subordinate.Fragment.Main.MeFragment;
import com.kt.whose.subordinate.Utils.MqttMsgService;
import com.kt.whose.subordinate.Utils.mqtt.KsMqttService;
import com.kt.whose.subordinate.Utils.mqtt.MqttCallBackHandler;
import com.kt.whose.subordinate.Utils.mqtt.MqttClientManager;


import org.eclipse.paho.client.mqttv3.IMqttActionListener;

import nl.joery.animatedbottombar.AnimatedBottomBar;

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
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String devicesId = "65535-ks";
    Toolbar toolbar;
    FrameLayout frameLayout;
    AnimatedBottomBar bottom_bar;

    DevicesFragment mDevicesFragment;
    DiscoverFragment mDiscoverFragment;
    MeFragment mMeFragment;

    private KsMqttService mqttService;

    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    private Handler handler;


    final FragmentManager supportFragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();
        initData();

        Intent intent = new Intent(this, KsMqttService.class);

        startService(intent);
        bindService(intent, mqttServiceConnection, BIND_AUTO_CREATE);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastTag.ACTION_MQTT_CONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_MQTT_DISCONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BroadcastTag.EXTRA_DATA_MESSAGE);
        intentFilter.addAction(BroadcastTag.EXTRA_DATA_TOPIC);
        intentFilter.addAction(BroadcastTag.EXTRA_ERROR_CODE);
        intentFilter.addAction(BroadcastTag.EXTRA_ERROR_MESSAGE);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        handler = new Handler(Looper.getMainLooper());


    }

    private void initEvent() {


    }

    private void initData() {
        /*mqttClientManager = MqttClientManager.getInstance(this);
        mqttCallBackHandler = new MqttCallBackHandler(this);
        mqttClientManager
                .setBroker("124.70.108.79",1883)
                .setAutoReconnect(true)
                .setConnectTimeout(10)
                .setDevicesId(devicesId)
                .setUserNamePass("public","123456")
                .setCallBackHandler(mqttCallBackHandler)
                .connect();


//        mqttClientManager.addSubscribeTopic("/test");
        mqttCallBackHandler.setOnConnectedStateListener(onConnectedStateListener);*/

    }

    // mqtt 连接状态监听回调
    private MqttCallBackHandler.OnConnectedStateListener onConnectedStateListener = new MqttCallBackHandler.OnConnectedStateListener() {
        @Override
        public void onState(boolean b, String s) {
            Log.i(TAG, "onState: " + b + s);


        }
    };

    private void initView() {
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


    private final ServiceConnection mqttServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttService = ((KsMqttService.LocalBinder) iBinder).getService();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    connectMqtt();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //mqttService = null;
        }
    };

    private void connectMqtt() {

        mqttService.setHOST("124.70.108.79");
        mqttService.setPORT(1883);
        mqttService.setAutoReconnect(true);
        mqttService.setDevicesId(devicesId);
        mqttService.setUSERNAME("public");
        mqttService.setPASSWORD("123456");
        mqttService.connect();

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BroadcastTag.ACTION_MQTT_DISCONNECTED.equals(action)) {
                Log.i(TAG, "onReceive: mqtt 断开连接,执行重新连接策略");

                if (!mqttService.getAutoReconnect()) {
                    if (mqttService != null) {
                        if (mqttService.isConnected()) {
                            mqttService.disconnect();
                        }
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connectMqtt();
                        }
                    }, 5000);
                }
            }

        }
    };


}