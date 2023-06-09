package com.kt.whose.subordinate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kt.whose.subordinate.Activity.ConnectDevicesActivity;
import com.kt.whose.subordinate.Adapter.AddDevicesAdapter;
import com.kt.whose.subordinate.Interface.ClickListener;
import com.kt.whose.subordinate.R;

import androidx.appcompat.widget.Toolbar;

public class AddDevicesActivity extends BaseActivity {

    private static final String TAG = "AddDevicesActivity";


    // 返回的状态码（0 表示为添加任何设备，1表示添了设备）
    private int returnResultCode = 0;
    Toolbar toolbar;
    RecyclerView recyclerView;
    AddDevicesAdapter addDevicesAdapter;


    @Override
    public int initLayoutId() {
        return R.layout.acitvity_add_devices;
    }


    @Override
    public void initView(){
        toolbar = findViewById(R.id.add_devices_toolbar);
        toolbar.setOnClickListener(toolBarOnClickListener);

        recyclerView = findViewById(R.id.add_devices_recycler);
    }
    @Override
    public void initEvent(){
        addDevicesAdapter = new AddDevicesAdapter(getResources().getStringArray(R.array.test_add_devices_item_data));
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        recyclerView.setAdapter(addDevicesAdapter);
        addDevicesAdapter.setOnClickListener(onClickListener);
    }

    private ClickListener.OnClickListener onClickListener = new ClickListener.OnClickListener() {
        @Override
        public void onClick(int i) {
            Intent intent = new Intent(getApplicationContext(), ConnectDevicesActivity.class);
            intentActivityResultLauncher.launch(intent);
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
                    returnResultCode = resultCode;
                }
            });


    @Override
    protected void onDestroy() {
        super.onDestroy();

        setResult(returnResultCode);

    }
}
