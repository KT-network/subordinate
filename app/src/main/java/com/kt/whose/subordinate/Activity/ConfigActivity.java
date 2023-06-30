package com.kt.whose.subordinate.Activity;

import android.animation.TimeAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.kt.whose.subordinate.Broadcast.BroadcastTag;
import com.kt.whose.subordinate.Interface.SocketClientListener;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.Utils.LoadDialog;
import com.kt.whose.subordinate.Utils.SocketClient;
import com.kt.whose.subordinate.Utils.SocketDataBase;
import com.kt.whose.subordinate.Utils.Tool;
import com.kt.whose.subordinate.Utils.sqlModel.DevicesInfoSql;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConfigActivity extends BaseActivity {

    private static final String TAG = "ConfigActivity";

    private int stateActivity = 1;

    private LoadDialog loadDialog;

    // socket 连接的ip与端口
    //private int PORT = 1006;
    private String IP = "192.168.5.1";
    private String URL = "http://192.168.5.1/";

    // 记录测试连接是否成功 (-1 断开连接，0测试连接，1保存配置)
    private int isTest = -1;

    // 记录默认为不开启静态ip
    private int autoWifiIpState = 0;

    private int returnResultCode = 0;

    private Handler handler;


    Toolbar toolbar;
    RelativeLayout btn;
    TextView btn_txt, devices_id, connect_state;
    EditText editName, editWifiSsid, editWifiPwd, editWifiIp, editWifiGateway, editMqttHost, editMqttPort, editMqttUser, editMqttPwd;
    CheckBox autoWifiIp;


    @Override
    public int initLayoutId() {
        return R.layout.activity_config;
    }

    @Override
    public void initView() {
        toolbar = findViewById(R.id.config_devices_toolbar);
        toolbar.setOnClickListener(toolBarOnClickListener);

        btn = findViewById(R.id.config_devices_test_connect_btn);
        btn_txt = findViewById(R.id.config_devices_test_connect_txt);
        btn.setOnClickListener(onClickListener);

        devices_id = findViewById(R.id.config_devices_id);
        connect_state = findViewById(R.id.config_devices_connect_state);

        editName = findViewById(R.id.config_devices_name_edit);
        editWifiSsid = findViewById(R.id.config_devices_wifi_ssid_edit);
        editWifiPwd = findViewById(R.id.config_devices_wifi_pwd_edit);
        editWifiIp = findViewById(R.id.config_devices_wifi_ip_edit);
        editWifiGateway = findViewById(R.id.config_devices_wifi_gateway_edit);

        editMqttHost = findViewById(R.id.config_devices_mqtt_host_edit);
        editMqttPort = findViewById(R.id.config_devices_mqtt_port_edit);
        editMqttUser = findViewById(R.id.config_devices_mqtt_user_edit);
        editMqttPwd = findViewById(R.id.config_devices_mqtt_pwd_edit);

        autoWifiIp = findViewById(R.id.config_devices_wifi_auto_ip);
        autoWifiIp.setOnCheckedChangeListener(autoWifiIpListener);

        loadDialog = new LoadDialog(ConfigActivity.this);

    }

    @Override
    protected void initEvent() {
        Intent intent = getIntent();
        stateActivity = intent.getIntExtra("code", 1);
        handler = new Handler();
        WaitDialog.show("设备连接中...");

        if (Tool.isWifiConnect(getApplicationContext()) && Tool.getWifiDhcpAddress(getApplicationContext()).equals(IP)) {
            httpPost(null, SocketDataBase.getHandshakeData());
        } else {
            setConnectState(false);
            WaitDialog.dismiss();
            TipDialog.show("设备连接失败！", WaitDialog.TYPE.ERROR);
        }


    }

    // 测试连接与保存配置的点击监听
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isTest == -1) {
                WaitDialog.show("设备连接中...");
                if (Tool.isWifiConnect(getApplicationContext()) && Tool.getWifiDhcpAddress(getApplicationContext()).equals(IP)) {

                    httpPost(null, SocketDataBase.getHandshakeData());
                } else {
                    WaitDialog.dismiss();
                    TipDialog.show("设备连接失败！", WaitDialog.TYPE.ERROR);
                    setConnectState(false);
                }
            } else if (isTest == 0) {

                WaitDialog.show("wifi连接测试中...");
                String data = SocketDataBase.verifyDevicesInfoData(autoWifiIpState,
                        getEditWifiSsid(),
                        getEditWifiPwd(),
                        getEditWifiIp(),
                        "255.255.255.0",
                        getEditWifiGateway());

                httpPost(null, data);
            } else if (isTest == 1) {
                WaitDialog.show("配置信息保存中...");
                String data = SocketDataBase.setDevicesInfoData(autoWifiIpState,
                        getEditWifiSsid(),
                        getEditWifiPwd(),
                        getEditWifiIp(),
                        "255.255.255.0",
                        getEditWifiGateway(),
                        getEditMqttHost(),
                        getEditMqttPort(),
                        getEditMqttUser(),
                        getEditMqttPwd());
                Log.i(TAG, "onClick: wifi 保存");

                httpPost(null, data);
            } else if (isTest == 2) {
                httpPost(null,SocketDataBase.getRestart());
            }


        }
    };


    // toolbar 退出
    private View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };


    // 设置 设备id
    private void setDevicesId(String s) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                devices_id.setText(s + "");
            }
        });
    }

    // 设置socket提示的状态
    private void setConnectState(boolean state) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (state) {
                    connect_state.setText(getResources().getText(R.string.config_devices_connect_state_true));
                    connect_state.setTextColor(getResources().getColor(R.color.green));
                } else {
                    connect_state.setText(getResources().getText(R.string.config_devices_connect_state_false));
                    connect_state.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });
    }

    private void setBtnTxt(int s) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                btn_txt.setText(getResources().getText(s));
            }
        });
    }


    /*
     * 获取配置信息
     * */
    private String getEditName() {
        return editName.getText().toString();
    }

    private String getEditWifiSsid() {
        return editWifiSsid.getText().toString();
    }

    private String getEditWifiPwd() {
        return editWifiPwd.getText().toString();
    }

    private String getEditWifiIp() {
        return editWifiIp.getText().toString();
    }

    private String getEditWifiGateway() {
        return editWifiGateway.getText().toString();
    }

    private String getEditMqttHost() {
        return editMqttHost.getText().toString();
    }

    private String getEditMqttPort() {
        return editMqttPort.getText().toString();
    }

    private String getEditMqttUser() {
        return editMqttUser.getText().toString();
    }

    private String getEditMqttPwd() {
        return editMqttPwd.getText().toString();
    }

    private String getDevicesId() {
        return devices_id.getText().toString();
    }


    // 单选框的监听事件（是否为静态ip）
    private CompoundButton.OnCheckedChangeListener autoWifiIpListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            autoWifiIpState = b ? 1 : 0;

        }
    };


    // 保存配置信息到sqlite
    private void saveInfo() {
        DevicesInfoSql devicesInfoSql = new DevicesInfoSql();
        devicesInfoSql.setName(getEditName());
        devicesInfoSql.setDevicesId(getDevicesId());
        devicesInfoSql.setDevicesType("");

        devicesInfoSql.setDevicesWifiSsid(getEditWifiSsid());
        devicesInfoSql.setDevicesWifiPwd(getEditWifiPwd());
        devicesInfoSql.setDevicesWifiIp(getEditWifiIp());
        devicesInfoSql.setDevicesWifiGateway(getEditWifiGateway());

        devicesInfoSql.setDevicesMqttHost(getEditMqttHost());
        devicesInfoSql.setDevicesMqttPort(getEditMqttPort());
        devicesInfoSql.setDevicesMqttUser(getEditMqttUser());
        devicesInfoSql.setDevicesMqttPwd(getEditMqttPwd());
        devicesInfoSql.save();
        returnResultCode = 1;
    }


    /*
     * http 请求
     * */
    private void httpPost(String mode, String date) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("GeneralId", "TOKEN");
                    connection.setConnectTimeout(17000);
                    connection.setReadTimeout(17000);

                    // 提交数据（获取输出流，提交数据到输出流）
                    DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                    dataOutputStream.writeBytes(date);
                    dataOutputStream.close();

                    if (connection.getResponseCode() == 200) {

                        String res = is2String(connection.getInputStream());

                        Log.i(TAG, "run: " + res);
                        JSONObject jo = new JSONObject(res);
                        JSONObject jsonObject = new JSONObject(res);
                        String type = jsonObject.getString("type");

                        if (type.equals("handshake")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String id = data.getString("id");
                            setDevicesId(id);
                            setConnectState(true);
                            isTest = 0;

                            setBtnTxt(R.string.config_devices_wifi_connect_btn);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    TipDialog.show("设备连接成功！", WaitDialog.TYPE.SUCCESS);
                                }
                            }, 1000);

                        } else if (type.equals("verifyDevicesInfo")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            Log.i(TAG, "onDataReceive: " + data.getBoolean("state"));
                            if (data.getBoolean("state")) {
                                isTest = 1;
                                setBtnTxt(R.string.config_devices_save_btn);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        WaitDialog.dismiss();
                                        TipDialog.show("wifi连接成功！", WaitDialog.TYPE.SUCCESS);
                                    }
                                }, 1000);
                                return;
                            }

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    WaitDialog.dismiss();
                                    TipDialog.show("wifi连接失败！", WaitDialog.TYPE.ERROR);
                                }
                            }, 1000);


                        } else if (type.equals("setDevicesInfo")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (data.getBoolean("state")) {
                                Log.i(TAG, "onDataReceive: state true");
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setBtnTxt(R.string.config_devices_restart);
                                        saveInfo();
                                        WaitDialog.dismiss();
                                        TipDialog.show("配置信息保存成功！", WaitDialog.TYPE.SUCCESS);
                                    }
                                },1000);
                                isTest = 2;

                                return;
                            }


                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    WaitDialog.dismiss();
                                    TipDialog.show("配置信息保存失败！", WaitDialog.TYPE.ERROR);
                                }
                            },1000);

                        }


                    }

                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isTest == 0){
                                WaitDialog.dismiss();
                                TipDialog.show("wifi连接失败！", WaitDialog.TYPE.ERROR);
                            } else if (isTest == 1) {
                                WaitDialog.dismiss();
                                TipDialog.show("配置信息保存失败！", WaitDialog.TYPE.ERROR);
                            } else if (isTest == -1) {

                                WaitDialog.dismiss();
                                TipDialog.show("设备连接失败！", WaitDialog.TYPE.ERROR);
                            }
                            setConnectState(false);
                            isTest = -1;

                        }
                    });

                }

            }
        }).start();


    }




    public String is2String(InputStream is) throws IOException {

        //连接后，创建一个输入流来读取response
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "utf-8"));
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        String response = "";
        //每次读取一行，若非空则添加至 stringBuilder
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        //读取所有的数据后，赋值给 response
        response = stringBuilder.toString().trim();
        return response;
    }


    /*
     * 广播监听
     * */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(1);
    }
}
