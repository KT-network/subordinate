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
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kt.whose.subordinate.Activity.AddDevicesActivity;
import com.kt.whose.subordinate.Activity.DevicesControlActivity;
import com.kt.whose.subordinate.Adapter.DevicesMainAdapter;
import com.kt.whose.subordinate.Broadcast.BroadcastTag;
import com.kt.whose.subordinate.Fragment.NewLazyFragment;
import com.kt.whose.subordinate.Interface.ClickListener;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.Utils.sqlModel.DevicesInfoSql;
import com.kt.whose.subordinate.Utils.mqtt.KsMqttService;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;

public class DevicesFragment extends NewLazyFragment {

    private static final String TAG = "DevicesFragment";
    //    private Context mContext;
    FloatingActionButton mDevicesFloating;
    RecyclerView mDevicesRecycler;
    DevicesMainAdapter mDevicesMainAdapter;

    //List<DevicesInfoSql> devicesInfoSqlList;

    List<DevicesInfoSql> devicesInfoSqlList;

    private KsMqttService mqttService;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    private Handler handler;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_main_devices;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        handler = new Handler(Looper.getMainLooper());

        getActivity().bindService(new Intent(getActivity(), KsMqttService.class), mqttServiceConnection, Context.BIND_AUTO_CREATE);


        mDevicesFloating = view.findViewById(R.id.devices_fab);
        mDevicesRecycler = view.findViewById(R.id.devices_recycler);

        mDevicesMainAdapter = new DevicesMainAdapter(getContext());
        /*mDevicesMainAdapter.setOnClickListener(onClickAdapterListener);*/
        mDevicesMainAdapter.setClickDevicesInfoListener(devicesInfoListener);
        mDevicesMainAdapter.setOnLongClickListener(onLongClickAdapterListener);
        mDevicesMainAdapter.setDevicesDisConnectedListener(devicesDisConnectedListener);

        mDevicesRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mDevicesRecycler.setAdapter(mDevicesMainAdapter);

        mDevicesFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddDevicesActivity.class);
                intentActivityResultLauncher.launch(intent);

//                startActivity(new Intent(getContext(), DevicesControlActivity.class));

            }
        });


    }


    @Override
    protected void initData() {
        super.initData();
        //setItemConnectState();
        mDevicesMainAdapter.setItemConnectState();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        devicesInfoSqlList = LitePal.findAll(DevicesInfoSql.class);

        mDevicesMainAdapter.setData(devicesInfoSqlList);
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
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    /*
    * 设备item点击
    * */
    private DevicesMainAdapter.ClickDevicesInfoListener devicesInfoListener = new DevicesMainAdapter.ClickDevicesInfoListener() {
        @Override
        public void onDevicesInfo(DevicesInfoSql devicesInfoSql) {

            long id = devicesInfoSql.getId();

            Intent intent = new Intent(getContext(),DevicesControlActivity.class);
            intent.putExtra("info", id);
            startActivity(intent);

        }
    };

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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mqttService.subscribe("/test", 0);
                        initSubscribe();
                    }
                });

            } else if (BroadcastTag.ACTION_DATA_AVAILABLE.equals(action)) {
                String topic = intent.getStringExtra(BroadcastTag.EXTRA_DATA_TOPIC);
                String msg = intent.getStringExtra(BroadcastTag.EXTRA_DATA_MESSAGE);
                Log.i(TAG, "onReceive: topic: " + topic);
                Log.i(TAG, "onReceive: msg: " + msg);
                receiveMsgDispose(topic, msg);

            }

        }
    };


    // 初始化订阅设备
    private void initSubscribe() {

        if (devicesInfoSqlList.size() != 0) {

            String[] topics = new String[devicesInfoSqlList.size()];
            int[] qos = new int[devicesInfoSqlList.size()];

            // /ks/subordinate/65535-ks/c8f09e9bee48

            for (int i = 0; i < devicesInfoSqlList.size(); i++) {
                String devicesId = devicesInfoSqlList.get(i).getDevicesId();
                qos[i] = 0;
                String topic = "ks/subordinate/65535-ks/" + devicesId;
                topics[i] = topic;

            }
            mqttService.subscribe(topics, qos);

        }
    }


    // msg 处理（msg 信息更新到ui）
    private void receiveMsgDispose(String topic, String msg) {
        String[] split = topic.split("/");
        String id = split[split.length - 1];
        int index = 0;
        for (int i = 0; i < devicesInfoSqlList.size(); i++) {
            if (id.equals(devicesInfoSqlList.get(i).getDevicesId())) {
                index = i;
            }
        }

        try {
            JSONObject jsonMsg = new JSONObject(msg);
            JSONArray time = jsonMsg.getJSONArray("time");
            int anInt = time.getInt(6);
            DevicesInfoSql devicesItem = devicesInfoSqlList.get(index);

            devicesItem.setLastTime(devicesItem.getNowTime());

            devicesItem.setState(true);
            devicesItem.setNowTime(anInt);
            mDevicesMainAdapter.notifyItem(devicesItem, index);
            broadcastUpdate(BroadcastTag.ACTION_DEVICES_CONNECTED, devicesItem.getDevicesId());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

    void broadcastUpdate(String action, String topic) {
        Intent intent = new Intent(action);
        intent.putExtra(action, topic);
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
