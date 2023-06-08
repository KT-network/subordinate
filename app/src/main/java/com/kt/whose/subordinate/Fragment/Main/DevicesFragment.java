package com.kt.whose.subordinate.Fragment.Main;

import static android.content.Context.BIND_AUTO_CREATE;

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

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kt.whose.subordinate.Adapter.DevicesMainAdapter;
import com.kt.whose.subordinate.AddDevicesActivity;
import com.kt.whose.subordinate.Broadcast.BroadcastTag;
import com.kt.whose.subordinate.Fragment.NewLazyFragment;
import com.kt.whose.subordinate.Interface.ClickListener;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.Utils.MqttMsgService;
import com.kt.whose.subordinate.Utils.model.DevicesInfoSql;
import com.kt.whose.subordinate.Utils.mqtt.KsMqttService;

import org.litepal.LitePal;

import java.util.List;

public class DevicesFragment extends NewLazyFragment {

    private static final String TAG = "DevicesFragment";
//    private Context mContext;
    FloatingActionButton mDevicesFloating;
    RecyclerView mDevicesRecycler;
    DevicesMainAdapter mDevicesMainAdapter;

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
        mDevicesFloating = view.findViewById(R.id.devices_fab);
        mDevicesRecycler = view.findViewById(R.id.devices_recycler);

        mDevicesMainAdapter = new DevicesMainAdapter(getContext());
        mDevicesMainAdapter.setOnClickListener(onClickAdapterListener);
        mDevicesMainAdapter.setOnLongClickListener(onLongClickAdapterListener);

        mDevicesRecycler.setLayoutManager(new GridLayoutManager(getContext(),2));
        mDevicesRecycler.setAdapter(mDevicesMainAdapter);

        mDevicesFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getContext(), AddDevicesActivity.class);
                //getContext().startActivity(intent);

//                getActivity().unbindService(connection);


            }
        });

    }


    @Override
    protected void initData() {
        super.initData();

        devicesInfoSqlList = LitePal.findAll(DevicesInfoSql.class);
        Log.i(TAG, "initData: "+devicesInfoSqlList.size());

    }

    @Override
    protected void initEvent() {
        super.initEvent();

        mDevicesMainAdapter.setData(devicesInfoSqlList);

        getActivity().bindService(new Intent(getActivity(), KsMqttService.class),mqttServiceConnection,BIND_AUTO_CREATE);

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext().getApplicationContext());
        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastTag.ACTION_MQTT_CONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_MQTT_DISCONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BroadcastTag.EXTRA_DATA_MESSAGE);
        intentFilter.addAction(BroadcastTag.EXTRA_DATA_TOPIC);
        intentFilter.addAction(BroadcastTag.EXTRA_ERROR_CODE);
        intentFilter.addAction(BroadcastTag.EXTRA_ERROR_MESSAGE);
        localBroadcastManager.registerReceiver(broadcastReceiver,intentFilter);

        handler = new Handler(Looper.getMainLooper());

    }

    private ClickListener.OnClickListener onClickAdapterListener = new ClickListener.OnClickListener() {
        @Override
        public void onClick(int i) {

        }
    };


    private ClickListener.OnLongClickListener onLongClickAdapterListener = new ClickListener.OnLongClickListener() {
        @Override
        public void onLongClick(int position) {

        }
    };


    private ServiceConnection mqttServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttService =((KsMqttService.LocalBinder) iBinder).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BroadcastTag.ACTION_MQTT_CONNECTED.equals(action)){

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mqttService.subscribe("/test",0);
                    }
                });

            } else if (BroadcastTag.ACTION_DATA_AVAILABLE.equals(action)) {
                String topic = intent.getStringExtra(BroadcastTag.EXTRA_DATA_TOPIC);
                String msg = intent.getStringExtra(BroadcastTag.EXTRA_DATA_MESSAGE);
                Log.i(TAG, "onReceive: topic: "+topic);
                Log.i(TAG, "onReceive: msg: "+msg);
            }


        }
    };



}
