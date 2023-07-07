package com.kt.whose.subordinate.Fragment.Main;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kt.whose.subordinate.Activity.AddDevicesActivity;
import com.kt.whose.subordinate.Activity.LoginActivity;
import com.kt.whose.subordinate.Activity.RgbDevicesControlActivity;
import com.kt.whose.subordinate.Adapter.DevicesMainAdapter;
import com.kt.whose.subordinate.BaseApplication;
import com.kt.whose.subordinate.Broadcast.BroadcastTag;
import com.kt.whose.subordinate.Fragment.NewLazyFragment;
import com.kt.whose.subordinate.HttpEntity.Devices;
import com.kt.whose.subordinate.HttpEntity.DevicesList;
import com.kt.whose.subordinate.HttpEntity.Login;
import com.kt.whose.subordinate.Interface.ClickListener;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.ExtendView.RefreshLottieHeader;
import com.kt.whose.subordinate.Utils.Preferences;
import com.kt.whose.subordinate.Utils.Tool;
import com.kt.whose.subordinate.Utils.sqlModel.DevicesInfoSql;
import com.kt.whose.subordinate.Utils.mqtt.KsMqttService;
import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import rxhttp.wrapper.param.RxHttp;

public class DevicesFragment extends NewLazyFragment {

    private static final String TAG = "DevicesFragment";
    //    private Context mContext;
    FloatingActionButton mDevicesFloating;
    RecyclerView mDevicesRecycler;
    DevicesMainAdapter mDevicesMainAdapter;
    SmartRefreshLayout smartRefreshLayout;

    RefreshLottieHeader refreshLottieHeader;

    //List<DevicesInfoSql> devicesInfoSqlList;

    List<DevicesInfoSql> devicesInfoSqlList;

    public static final List<Devices> devicesList = new ArrayList<>();


    private KsMqttService mqttService;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    private Handler handler;
    private String userId;

    private boolean loginIs = false;


    // -1打开软件后，第一次刷新，0
    private int refreshTick = -1;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_main_devices;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        handler = new Handler(Looper.getMainLooper());

        getActivity().bindService(new Intent(getActivity(), KsMqttService.class), mqttServiceConnection, Context.BIND_AUTO_CREATE);

        // 初始化刷新 Lottie 动画
        refreshLottieHeader = new RefreshLottieHeader(getContext());
        refreshLottieHeader.setAnimationViewJson("001.json");


        // smartRefreshLayout初始化
        smartRefreshLayout = view.findViewById(R.id.devices_smartRefreshLayout);
        // smartRefreshLayout设置头刷新
        smartRefreshLayout.setRefreshHeader(refreshLottieHeader);
        // smartRefreshLayout 监听
        smartRefreshLayout.setOnRefreshListener(onRefreshListener);

        mDevicesFloating = view.findViewById(R.id.devices_fab);
        mDevicesRecycler = view.findViewById(R.id.devices_recycler);

        mDevicesMainAdapter = new DevicesMainAdapter(getContext());
        mDevicesMainAdapter.setOnClickListener(onClickAdapterListener);
        mDevicesMainAdapter.setOnLongClickListener(onLongClickAdapterListener);
        mDevicesMainAdapter.setDevicesDisConnectedListener(devicesDisConnectedListener);

        mDevicesRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mDevicesRecycler.setAdapter(mDevicesMainAdapter);

        mDevicesFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Preferences.getValue("account", null) == null || Preferences.getValue("account_pwd", null) == null) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    return;
                }
                if (BaseApplication.getToken() == null) {
                    finishRefresh(500);
                    PopTip.show("未登录").iconWarning();
                    return;
                }
                Intent intent = new Intent(getContext(), AddDevicesActivity.class);
                startActivity(intent);
//                intentActivityResultLauncher.launch(intent);


            }
        });


    }


    @Override
    protected void initData() {
        super.initData();
//        setItemConnectState();
//        mDevicesMainAdapter.setItemConnectState();
    }

    @Override
    protected void initEvent() {
        super.initEvent();

//        devicesList = new ArrayList<>();

        devicesInfoSqlList = LitePal.findAll(DevicesInfoSql.class);
        mDevicesMainAdapter.setHandler(handler);

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext().getApplicationContext());
        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastTag.ACTION_MQTT_CONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_MQTT_DISCONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BroadcastTag.EXTRA_DATA_MESSAGE);
        intentFilter.addAction(BroadcastTag.EXTRA_DATA_TOPIC);
        intentFilter.addAction(BroadcastTag.EXTRA_ERROR_CODE);
        intentFilter.addAction(BroadcastTag.EXTRA_ERROR_MESSAGE);
        intentFilter.addAction(BroadcastTag.ACTION_LOGIN_SUCCEED_DEVICES_LIST);
        intentFilter.addAction(BroadcastTag.ACTION_LOGIN_STATE);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);


        if (Preferences.getValue("account", null) == null || Preferences.getValue("account_pwd", null) == null) {
            return;
        }
        smartRefreshLayout.autoRefresh();

    }


    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            if (refreshTick == -1) {
                login();
                return;
            }
            refreshData();
        }
    };


    private void login() {

        if (Preferences.getValue("account", null) == null || Preferences.getValue("account_pwd", null) == null) {
            finishRefresh(500);
            PopTip.show("未登录").iconWarning();
            return;
        }
        String account = Preferences.getValue("account", "");
        String pwd = Preferences.getValue("account_pwd", "");
        RxHttp.postJson("/user/login")
                .setAssemblyEnabled(false)
                .add("user", account)
                .add("pwd", pwd)
                .toObservableResponse(Login.class)
                .to(RxLife.toMain(this))
                .subscribe(s -> {

                    BaseApplication.setToken(s.getToken());
                    BaseApplication.setUserId(Tool.md5(account+"-ks"));
                    finishRefresh(500);
                    loginDataDispose(s.getDevices());
                    broadcastUpdateLoginState(true);
                }, throwable -> {
                    int code = Tool.ErrorInfo(throwable);
                    refreshTick = -1;
                    finishRefresh(500);
                });

    }


    private void loginDataDispose(List<Devices> devices) {
        refreshTick = 0;
        setHeader("002.json");
//        userId = Preferences.getValue("userId", "");
        if (devices != null || devices.size() != 0) {
            devicesList.addAll(devices);
            mDevicesMainAdapter.notifyDataSetChanged();
        }

    }


    private void refreshData() {


        if (BaseApplication.getToken() == null) {
            finishRefresh(500);
            PopTip.show("登录已过期").iconWarning();
            return;
        }

        RxHttp.postJson("/devices/list")
//                .addHeader("UserToken", BaseApplication.getToken())
                .toObservableResponse(DevicesList.class)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    finishRefresh(500);
                    refreshDataDispose(s.getDevices());
                }, e -> {

                    int code = Tool.ErrorInfo(e);
                    if (code == 6) {
                        broadcastUpdateLoginState(false);
                    }
                    finishRefresh(500);

                });

    }

    private void refreshDataDispose(List<Devices> devices) {
        if (devicesList.size() == 0) {
//            subscribeDevicesTopic(devices);
            devicesList.addAll(devices);
            mDevicesMainAdapter.notifyDataSetChanged();
        } else {
//            mDevicesMainAdapter.filterData(devices);

            filterData(devices);

        }

    }


    private void filterData(List<Devices> newDevicesList) {

        HashMap<String, Devices> devicesOldHashMap = new HashMap<>();
        HashMap<String, Devices> devicesNewHashMap = new HashMap<>();
        HashMap<String, Devices> devicesAlterHashMap = new HashMap<>();

        for (Devices devices : devicesList) {
            devicesOldHashMap.put(devices.getDevicesId(), devices);
        }
        for (Devices devices : newDevicesList) {
            devicesNewHashMap.put(devices.getDevicesId(), devices);
        }

        Set<String> oldSet = devicesOldHashMap.keySet();
        Set<String> newSet = devicesNewHashMap.keySet();

        Set<String> retainOld = new HashSet<>(devicesOldHashMap.keySet());
        Set<String> retainNow = new HashSet<>(devicesNewHashMap.keySet());
        retainNow.retainAll(retainOld);

        for (String key : retainNow) {
            Devices devices = devicesOldHashMap.get(key);
            Devices devices1 = devicesNewHashMap.get(key);
            if (!devices.getName().equals(devices1.getName()) || !devices.getPicUrl().equals(devices1.getPicUrl())) {
                devices.setName(devices1.getName());
                devices.setPicUrl(devices1.getPicUrl());
                int i = devicesList.indexOf(devices);
                mDevicesMainAdapter.notifyItemChanged(i);
            }
        }

        Set<String> andOld_o = new HashSet<>(devicesOldHashMap.keySet());
        Set<String> andNow_o = new HashSet<>(devicesNewHashMap.keySet());
        andOld_o.removeAll(andNow_o);
        Log.i(TAG, "filterData: " + andOld_o.toString());
        for (String key : andOld_o) {
            Devices devices = devicesOldHashMap.get(key);
            //String topic = "ks/subordinate/" + userId + "/" + devices.getDevicesId() + "/state";
            int i = devicesList.indexOf(devices);
            //mqttService.unsubscribe(topic);
            devicesList.remove(i);
            mDevicesMainAdapter.notifyItemRemoved(i);
            mDevicesMainAdapter.notifyItemRangeRemoved(i, devicesList.size() - i);
        }

        Set<String> andOld_n = new HashSet<>(devicesOldHashMap.keySet());
        Set<String> andNow_n = new HashSet<>(devicesNewHashMap.keySet());
        andNow_n.removeAll(andOld_n);
        Log.i(TAG, "filterData: " + andNow_n.toString());
        for (String key : andNow_n) {
            Devices devices = devicesNewHashMap.get(key);
            //String topic = "ks/subordinate/" + userId + "/" + devices.getDevicesId() + "/state";
            //mqttService.subscribe(topic, 0);
            devicesList.add(devices);

            mDevicesMainAdapter.notifyItemInserted(devicesList.size() - 1);
        }

        mDevicesMainAdapter.notifyDataSetChanged();

    }


    /**
     * 设置刷新header风格
     *
     * @param name json文件名称
     */
    private void setHeader(String name) {
        refreshLottieHeader.setAnimationViewJson(name);
        if (smartRefreshLayout.isRefreshing()) {
            smartRefreshLayout.finishRefresh();
        }
        smartRefreshLayout.setRefreshHeader(refreshLottieHeader);
    }


    /**
     * 完成刷新
     *
     * @param s 延迟时间
     */
    private void finishRefresh(int s) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                smartRefreshLayout.finishRefresh();
            }
        }, s);

    }

    /*
     * 设备item点击
     * */
    /*private DevicesMainAdapter.ClickDevicesInfoListener devicesInfoListener = new DevicesMainAdapter.ClickDevicesInfoListener() {
        @Override
        public void onDevicesInfo(Devices devices) {


            Intent intent = new Intent(getContext(), RgbDevicesControlActivity.class);

            startActivity(intent);

        }
    };*/

    /*
     * 设备掉线
     * */
    private DevicesMainAdapter.DevicesDisConnectedListener devicesDisConnectedListener = new DevicesMainAdapter.DevicesDisConnectedListener() {
        @Override
        public void onDevicesId(String s) {
            broadcastUpdate(BroadcastTag.ACTION_DEVICES_DISCONNECTED, s);
        }
    };


    /*
     * 设备item点击
     * */
    private ClickListener.OnClickListener onClickAdapterListener = new ClickListener.OnClickListener() {
        @Override
        public void onClick(int i) {
            Devices devices = devicesList.get(i);
            Intent intent = new Intent(getContext(), RgbDevicesControlActivity.class);
            intent.putExtra("info", devices);
            startActivity(intent);

        }
    };


    /*
     * 设备item长按
     * */
    private ClickListener.OnLongClickListener onLongClickAdapterListener = new ClickListener.OnLongClickListener() {
        @Override
        public void onLongClick(int position) {

        }
    };


    /*
     * service
     * */
    private final ServiceConnection mqttServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttService = ((KsMqttService.LocalBinder) iBinder).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "onServiceDisconnected: componentName");
        }
    };


    /*
     * 广播监听
     * */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BroadcastTag.ACTION_MQTT_CONNECTED.equals(action)) {
                // mqtt连接成功后订阅话题
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run: 连接成功");
                        mqttService.subscribe("ks/subordinate/" + BaseApplication.getUserId() + "/devices/state", 0);

                        if (devicesList != null && devicesList.size() != 0) {
//                            subscribeDevicesTopic(devicesList);
                        }
                    }
                });

            } else if (BroadcastTag.ACTION_DATA_AVAILABLE.equals(action)) {
                String topic = intent.getStringExtra(BroadcastTag.EXTRA_DATA_TOPIC);
                String msg = intent.getStringExtra(BroadcastTag.EXTRA_DATA_MESSAGE);
//                Log.i(TAG, "onReceive: "+msg);
                receiveMsgDispose(topic, msg);
            } else if (BroadcastTag.ACTION_LOGIN_STATE.equals(action)) {
                Log.i(TAG, "onReceive: " + intent.getBooleanExtra(BroadcastTag.ACTION_LOGIN_STATE, false));
                if (intent.getBooleanExtra(BroadcastTag.ACTION_LOGIN_STATE, false)) {
                    // 登录成功后开始连接mqtt
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            refreshTick = 0;
                            setHeader("002.json");
//                            userId = Preferences.getValue("userId", "");
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            PopTip.show("登录已过期").iconWarning();
                        }
                    });
                }

            } else if (BroadcastTag.ACTION_LOGIN_SUCCEED_DEVICES_LIST.equals(action)) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<Devices> devices = (List<Devices>) intent.getSerializableExtra(BroadcastTag.ACTION_LOGIN_SUCCEED_DEVICES_LIST);
                        loginDataDispose(devices);
                    }
                });
            }

        }
    };


    // 初始化订阅设备
    private void subscribeDevicesTopic(List<Devices> devices) {
        if (devices == null || devices.size() == 0) {
            return;
        }
        mqttService.subscribe("/test", 0);
        String[] topics = new String[devices.size()];
        int[] qos = new int[devices.size()];
        // /ks/subordinate/65535-ks/c8f09e9bee48
        for (int i = 0; i < devices.size(); i++) {
            String devicesId = devices.get(i).getDevicesId();
            qos[i] = 0;
            String topic = "ks/subordinate/" + userId + "/" + devicesId + "/state";
            topics[i] = topic;
        }
        mqttService.subscribe(topics, qos);

    }


    // msg 处理（设备状态更新到ui）
    private void receiveMsgDispose(String topic, String msg) {
        Log.i(TAG, "receiveMsgDispose: "+topic);
        String[] split = topic.split("/");
        if (!split[split.length - 1].equals("state") || !split[split.length-2].equals("devices")) {
            return;
        }

        try {
            JSONObject jo = new JSONObject(msg);
            Iterator<String> keys = jo.keys();

            while (keys.hasNext()){
                String key = keys.next();
                for (Devices devices:devicesList){
                    int i = devicesList.indexOf(devices);
                    if (key.equals(devices.getDevicesId())) {
                        devices.setState(jo.getBoolean(key));
                        mDevicesMainAdapter.notifyItemChanged(i);
                    }
                }
            }
           mDevicesMainAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }





        /*String id = split[split.length - 2];
        int index = -1;
        for (int i = 0; i < devicesList.size(); i++) {
            if (id.equals(devicesList.get(i).getDevicesId())) {
                index = i;
                break;
            }
        }
        if (index == -1) return;
        try {
            JSONObject jsonMsg = new JSONObject(msg);
//            JSONArray time = jsonMsg.getJSONArray("time");
            int anInt = jsonMsg.getInt("time");
            Devices devicesItem = devicesList.get(index);

            devicesItem.setLastTime(devicesItem.getNowTime());

            devicesItem.setState(true);
            devicesItem.setNowTime(anInt);
            mDevicesMainAdapter.notifyItem(devicesItem, index);
            // 广播设备在线信息
            broadcastUpdate(BroadcastTag.ACTION_DEVICES_CONNECTED, devicesItem.getDevicesId());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }*/

    }

    void broadcastUpdate(String action, String topic) {
        Intent intent = new Intent(action);
        intent.putExtra(action, topic);
        localBroadcastManager.sendBroadcast(intent);
    }

    void broadcastUpdateLoginState(boolean is) {
        Intent intent = new Intent(BroadcastTag.ACTION_LOGIN_STATE);
        intent.putExtra(BroadcastTag.ACTION_LOGIN_STATE, is);
        localBroadcastManager.sendBroadcast(intent);
    }


    /*
     * activity 回调
     * */
    private ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    int resultCode = result.getResultCode();
                    Log.i(TAG, "onActivityResult: " + resultCode);
                    //Intent data = result.getData();
                }
            });


    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        getActivity().unbindService(mqttServiceConnection);
        super.onDestroy();
    }
}
