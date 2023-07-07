package com.kt.whose.subordinate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.flod.loadingbutton.LoadingButton;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kt.whose.subordinate.HttpEntity.DevicesType;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.Utils.Tool;

import java.io.Serializable;

public class ConnectDevicesActivity extends BaseActivity {

    private static final String TAG = "ConnectDevicesActivity";
    Toolbar toolbar;
    Handler handler;
    RelativeLayout btn;
    TextView btn_txt;
    ImageView connect_devices_img;
    private DevicesType devicesType;

    private boolean firstIs = false;


    @Override
    public int initLayoutId() {
        return R.layout.activity_connect_devices;
    }

    @Override
    public void initView() {

        toolbar = findViewById(R.id.connect_devices_toolbar);
        toolbar.setOnClickListener(toolBarOnClickListener);

        btn = findViewById(R.id.connect_devices_btn);
        btn_txt = findViewById(R.id.connect_devices_btn_txt);
        connect_devices_img = findViewById(R.id.connect_devices_img);
        btn.setOnClickListener(onClickListener);


    }

    @Override
    protected void initEvent() {
        handler = new Handler();

        Intent intent = getIntent();
        DevicesType s = (DevicesType) intent.getSerializableExtra("devicesType");

        Log.i(TAG, "initEvent: " + s.getType());
        if (s != null) {
            devicesType = s;
            if (!devicesType.getPicUrl().equals("")) {
                Glide.with(this).load(devicesType.getPicUrl()).into(connect_devices_img);
            }
        }


    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (Tool.isWifiConnect(getApplicationContext()) && Tool.getWifiDhcpAddress(getApplicationContext()).equals("192.168.5.1")) {
                Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
                intent.putExtra("devicesType", devicesType);
                startActivity(intent);
//                intentActivityResultLauncher.launch(intent);
                finish();
            } else {
                PopTip.show("请确保已连接到设备的wifi").iconWarning();
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

    /*
     * activity 回调
     * */
    private ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();
                    //returnResultCode = resultCode;
                }
            });


    private void toast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void toast(int i) {

        Toast.makeText(getApplicationContext(), "" + i, Toast.LENGTH_SHORT).show();
    }

}
