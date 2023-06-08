package com.kt.whose.subordinate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kt.whose.subordinate.Interface.SocketClientListener;
import com.kt.whose.subordinate.Utils.LoadDialog;
import com.kt.whose.subordinate.Utils.SocketClient;
import com.kt.whose.subordinate.Utils.SocketDataBase;
import com.kt.whose.subordinate.Utils.Tool;
import com.kt.whose.subordinate.Utils.model.DevicesInfoSql;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigActivity extends AppCompatActivity {

    private static final String TAG = "ConfigActivity";

    private int stateActivity = 1;

    private SocketClient socketClient;
    private LoadDialog loadDialog;

    // socket 连接的ip与端口
    private int PORT = 1006;
    private String IP = "192.168.4.1";

    // 记录测试连接是否成功
    private boolean isTest = false;

    // 记录默认为不开启静态ip
    private int autoWifiIpState = 0;

    RelativeLayout btn;
    TextView btn_txt, devices_id, connect_state;
    EditText editName, editWifiSsid, editWifiPwd, editWifiIp, editWifiGateway, editMqttHost, editMqttPort, editMqttUser, editMqttPwd;
    CheckBox autoWifiIp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Intent intent = getIntent();
        stateActivity = intent.getIntExtra("code", 1);

        initView();

        socketClient = new SocketClient(IP, PORT);

        socketClient.setConnectedListener(tcpConnectClientStateListener);
        socketClient.setDisconnectedListener(tcpDisConnectClientStateListener);
        socketClient.setDataReceiveListener(tcpClientDataReceiveListener);


        socketClient.connect();


    }

    private void initView() {
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

        loadDialog = new LoadDialog(getApplicationContext());

    }

    // 测试连接与保存配置的点击监听
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isTest) {
                String data = SocketDataBase.verifyDevicesInfoData(autoWifiIpState,
                        getEditWifiSsid(),
                        getEditWifiPwd(),
                        getEditWifiIp(),
                        "255.255.255.0",
                        getEditWifiGateway());
                send(data);
            } else {
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

                send(data);

            }

        }
    };

    // socket 连接成功的监听
    private SocketClientListener.TcpClientStateListener tcpConnectClientStateListener = new SocketClientListener.TcpClientStateListener() {
        @Override
        public void onHandler() {

            Log.i(TAG, "onHandler: 连接成功");
            setConnectState(true);
            socketClient.send(SocketDataBase.getHandshakeData().getBytes());


        }
    };

    // socket 断开连接监听
    private SocketClientListener.TcpClientStateListener tcpDisConnectClientStateListener = new SocketClientListener.TcpClientStateListener() {
        @Override
        public void onHandler() {
            setConnectState(false);
            Log.i(TAG, "onHandler: 断开连接");

        }
    };

    // socket的数据接收监听
    private SocketClientListener.TcpClientDataReceiveListener tcpClientDataReceiveListener = new SocketClientListener.TcpClientDataReceiveListener() {
        @Override
        public void onDataReceive(byte[] var1) {
            String msg = new String(var1);
            Log.i(TAG, "onDataReceive: " + msg);

            try {
                JSONObject jsonObject = new JSONObject(msg);
                String type = jsonObject.getString("type");
                if (type.equals("handshake")) {
                    socketClient.send(SocketDataBase.getDevicesIdData().getBytes());
                } else if (type.equals("getDevicesId")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String id = data.getString("id");
                    setDevicesId(id);

                } else if (type.equals("verifyDevicesInfo")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    Log.i(TAG, "onDataReceive: " + data.getBoolean("state"));
                    if (data.getBoolean("state")) {
                        isTest = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_txt.setText(getResources().getText(R.string.config_devices_save_btn));
                            }
                        });
                    }

                } else if (type.equals("setDevicesInfo")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data.getBoolean("state")) {
                        saveInfo();
                    }

                    String m = data.getBoolean("state") == true ? "配置成功" : "配置失败";

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ConfigActivity.this, m, Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }
    };

    // 封装socket的send 函数
    private void send(String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                socketClient.send(s.getBytes());
            }
        }).start();
    }


    // 设置 设备id
    private void setDevicesId(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                devices_id.setText(s + "");
            }
        });
    }

    // 设置socket提示的状态
    private void setConnectState(boolean state) {
        runOnUiThread(new Runnable() {
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
            }
        });


    }


}
