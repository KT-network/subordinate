package com.kt.whose.subordinate.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.BottomDialog;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;
import com.kt.whose.subordinate.Broadcast.BroadcastTag;
import com.kt.whose.subordinate.HttpEntity.Devices;
import com.kt.whose.subordinate.HttpEntity.DevicesType;
import com.kt.whose.subordinate.HttpEntity.Msg;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.Utils.Preferences;
import com.kt.whose.subordinate.Utils.SocketDataBase;
import com.kt.whose.subordinate.Utils.Tool;
import com.kt.whose.subordinate.Utils.mqtt.KsMqttService;
import com.kt.whose.subordinate.Utils.sqlModel.DevicesInfoSql;
import com.rxjava.rxlife.RxLife;

import org.litepal.LitePal;

import rxhttp.wrapper.param.RxHttp;


public class RgbDevicesControlActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "DevicesControlActivity";
    Toolbar toolbar;
    RecyclerView pixel_screen;

    GridLayoutManager gridLayoutManager;

    ImageView connectStateImage, editName;
    TextView devicesNameText, devicesIdText;

    RelativeLayout editNetwork;

    private Devices devices;

    private String topic;

    @Override
    public int initLayoutId() {
        return R.layout.activity_devices_control;
    }

    @Override
    public void initView() {
        toolbar = findViewById(R.id.devices_control_toolbar);
        toolbar.setOnClickListener(toolBarOnClickListener);

        editName = findViewById(R.id.devices_control_edit_name);
        editName.setOnClickListener(this);
        connectStateImage = findViewById(R.id.devices_control_connect_state);
        devicesNameText = findViewById(R.id.devices_control_name);
        devicesIdText = findViewById(R.id.devices_control_id);

        editNetwork = findViewById(R.id.devices_control_edit_network);
        editNetwork.setOnClickListener(this);
    }

    @Override
    protected void initEvent() {
        Intent intent = getIntent();
        Devices s = (Devices) intent.getSerializableExtra("info");
        if (s != null) {
            devices = s;
            devicesIdText.setText(devices.getDevicesId());
            devicesNameText.setText(devices.getName());
            topic = "ks/subordinate/" + Preferences.getValue("userId", "") + "/" + devices.getDevicesId() + "/action";
        }


        bindService();
        broadcastFilter();

    }

    // toolbar 退出
    private View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    @Override
    public void broadcastFilter() {
        super.broadcastFilter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastTag.ACTION_MQTT_DISCONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_DEVICES_CONNECTED);
        intentFilter.addAction(BroadcastTag.ACTION_DEVICES_DISCONNECTED);

        intentFilter.addAction(BroadcastTag.EXTRA_DATA_MESSAGE);
        intentFilter.addAction(BroadcastTag.EXTRA_DATA_TOPIC);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);


    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BroadcastTag.ACTION_MQTT_DISCONNECTED.equals(action)) {

                connectStateImage.setSelected(false);
            } else if (BroadcastTag.ACTION_DEVICES_DISCONNECTED.equals(action)) {

                if (intent.getStringExtra(BroadcastTag.ACTION_DEVICES_DISCONNECTED).equals(devices.getDevicesId())) {
                    connectStateImage.setSelected(false);
                }
            } else if (BroadcastTag.ACTION_DEVICES_CONNECTED.equals(action)) {

                if (intent.getStringExtra(BroadcastTag.ACTION_DEVICES_CONNECTED).equals(devices.getDevicesId())) {
                    connectStateImage.setSelected(true);
                }
            }
        }
    };


    private void editNameHttp(String s) {

        RxHttp.postJson("/devices/edit/name")
                .add("id", devices.getId())
                .add("name", s)
                .toObservableResponse(String.class)
                .to(RxLife.toMain(this))
                .subscribe(r -> {
                    PopTip.show(r).iconSuccess();
                    devicesNameText.setText(s);

                }, e -> {
                    int code = Tool.ErrorInfo(e);
                    if (code == 6) {
                        broadcastUpdateLoginState(false);
                    }

                });
    }

    @Override
    public void onClick(View view) {

        if (view == editName) {
            new InputDialog("修改设备名称", null, "修改", "取消", devices.getName())
                    .setCancelable(false)
                    .setOkButton(new OnInputDialogButtonClickListener<InputDialog>() {
                        @Override
                        public boolean onClick(InputDialog dialog, View v, String inputStr) {
                            editNameHttp(inputStr);
                            return false;
                        }
                    }).show();
        } else if (view == editNetwork) {

            clickEditNetwork();

        }

    }


    private void clickEditNetwork() {
        BottomDialog.show(new OnBindView<BottomDialog>(R.layout.layout_devices_edit_network) {
            @Override
            public void onBind(BottomDialog dialogBottom, View v) {

                TextView commit = v.findViewById(R.id.edit_network_commit);
                EditText ssid = v.findViewById(R.id.edit_network_ssid);
                EditText pwd = v.findViewById(R.id.edit_network_pwd);
                EditText ip = v.findViewById(R.id.edit_network_ip);
                EditText gateway = v.findViewById(R.id.edit_network_gateway);
                CheckBox staticIp = v.findViewById(R.id.edit_network_static);
                CheckBox restart = v.findViewById(R.id.edit_network_restart);

                staticIp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            ip.setVisibility(View.VISIBLE);
                            gateway.setVisibility(View.VISIBLE);
                        } else {
                            ip.setVisibility(View.GONE);
                            gateway.setVisibility(View.GONE);
                        }

                    }
                });

                commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String msg = "";
                        if (staticIp.isChecked()) {
                            String ssids = ssid.getText().toString();
                            String pwds = pwd.getText().toString();
                            String ips = ip.getText().toString();
                            String g = gateway.getText().toString();

                            if (ssids.length() == 0 || pwds.length() == 0 || ips.length() == 0 || g.length() == 0) {
                                PopTip.show("请输入完整").iconWarning();
                                return;
                            }
                            msg = ssids + "\n" + pwds + "\n" + ips + "\n" + g;

                        } else {
                            String ssids = ssid.getText().toString();
                            String pwds = pwd.getText().toString();
                            if (ssids.length() == 0 || pwds.length() == 0) {
                                PopTip.show("请输入完整").iconWarning();
                                return;
                            }
                            msg = ssids + "\n" + pwds;
                        }
                        MessageDialog.show("配置网络", "你确定要提交新的网络信息吗？\n" + msg, "提交", "取消")
                                .setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                                    @Override
                                    public boolean onClick(MessageDialog dialog, View v) {

                                        if (mqttService.isConnected()) {
                                            String s = "";
                                            if (staticIp.isChecked()){
                                                s = SocketDataBase.editNetwork(
                                                        1,
                                                        ssid.getText().toString(),
                                                        pwd.getText().toString(),
                                                        ip.getText().toString(),
                                                        "255.255.255.0",
                                                        gateway.getText().toString(),
                                                        restart.isChecked()?1:0);
                                            }else {
                                                s = SocketDataBase.editNetwork(
                                                        1,
                                                        ssid.getText().toString(),
                                                        pwd.getText().toString(),
                                                        "0.0.0.0",
                                                        "255.255.255.0",
                                                        "0.0.0.0",
                                                        restart.isChecked()?1:0);
                                            }

                                            mqttService.publish(topic,s);
                                            PopTip.show("提交成功").iconSuccess();
                                            dialogBottom.dismiss();
                                        }

                                        return false;
                                    }
                                });


                    }
                });


            }
        }).setAllowInterceptTouch(false);
    }


    /*
     * service
     * *//*
    private final ServiceConnection mqttServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttService = ((KsMqttService.LocalBinder) iBinder).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "onServiceDisconnected: componentName");
        }
    };*/


    void broadcastUpdateLoginState(boolean is) {
        Intent intent = new Intent(BroadcastTag.ACTION_LOGIN_STATE);
        intent.putExtra(BroadcastTag.ACTION_LOGIN_STATE, is);
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);

    }


}
