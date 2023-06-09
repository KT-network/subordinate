package com.kt.whose.subordinate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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

import com.flod.loadingbutton.LoadingButton;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.Utils.Tool;

public class ConnectDevicesActivity extends BaseActivity {

    private static final String TAG = "ConnectDevicesActivity";
    Toolbar toolbar;
    LoadingButton loadingButton;
    Handler handler;
    RelativeLayout btn;
    TextView btn_txt;

    private boolean examine_connect = false;


    @Override
    public int initLayoutId() {
        return R.layout.activity_connect_devices;
    }
    @Override
    public void initView() {

        toolbar = findViewById(R.id.connect_devices_toolbar);
        btn = findViewById(R.id.connect_devices_btn);
        btn_txt = findViewById(R.id.connect_devices_btn_txt);

        btn.setOnClickListener(onClickListener);

        /*loadingButton = findViewById(R.id.connect_devices_but);
        loadingButton.setOnClickListener(onClickListener);
        loadingButton.getLoadingDrawable().setStrokeWidth(6);*/

    }

    @Override
    protected void initEvent() {
        handler = new Handler();

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


    private void toast(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
    private void toast(int i){

        Toast.makeText(getApplicationContext(), ""+i, Toast.LENGTH_SHORT).show();
    }

}
