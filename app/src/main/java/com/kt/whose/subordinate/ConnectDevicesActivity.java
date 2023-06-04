package com.kt.whose.subordinate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.flod.loadingbutton.LoadingButton;
import com.kt.whose.subordinate.Utils.Tool;

public class ConnectDevicesActivity extends AppCompatActivity {

    private static final String TAG = "ConnectDevicesActivity";
    Toolbar toolbar;
    LoadingButton loadingButton;
    Handler handler;
    RelativeLayout btn;
    TextView btn_txt;

    private boolean examine_connect = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_devices);
        initView();
        handler = new Handler();
    }

    private void initView() {

        toolbar = findViewById(R.id.connect_devices_toolbar);
        btn = findViewById(R.id.connect_devices_btn);
        btn_txt = findViewById(R.id.connect_devices_btn_txt);

        btn.setOnClickListener(onClickListener);

        /*loadingButton = findViewById(R.id.connect_devices_but);
        loadingButton.setOnClickListener(onClickListener);
        loadingButton.getLoadingDrawable().setStrokeWidth(6);*/

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (Tool.isWifiConnect(getApplicationContext()) && Tool.getWifiDhcpAddress(getApplicationContext()).equals("192.168.4.1")) {

                Intent intent = new Intent(getApplicationContext(),ConfigActivity.class);

                intent.putExtra("code",0);
                startActivity(intent);

//                toast("检查成功");

            } else {

//                toast("检查失败");
            }


            /*if (!examine_connect){

                if (Tool.isWifiConnect(getApplicationContext()) && Tool.getWifiDhcpAddress(getApplicationContext()).equals("192.168.0.254")) {
                    examine_connect = true;

                    btn_txt.setText(getResources().getText(R.string.connect_devices_state_config));
                    toast("检查成功");

                } else {
                    btn_txt.setText(getResources().getText(R.string.connect_devices_examine_connect));
                    toast("检查失败");
                }

            }*/


            /*if (!examine_connect) {
                loadingButton.start();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Tool.isWifiConnect(getApplicationContext()) && Tool.getWifiDhcpAddress(getApplicationContext()).equals("192.168.0.254")) {
                                    examine_connect = true;
                                    loadingButton.complete(true);
                                    loadingButton.setText(getResources().getText(R.string.connect_devices_state_config));
                                } else {

                                    loadingButton.complete(false);

                                    loadingButton.setText(getResources().getText(R.string.connect_devices_examine_connect));
                                }


                            }
                        });


                    }
                }, 3000);
            }*/


        }
    };


    private void toast(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
    private void toast(int i){

        Toast.makeText(getApplicationContext(), ""+i, Toast.LENGTH_SHORT).show();
    }

}
