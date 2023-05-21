package com.kt.whose.subordinate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kt.whose.subordinate.Adapter.AddDevicesAdapter;
import com.kt.whose.subordinate.Interface.ClickListener;
import com.kt.whose.subordinate.Utils.Tool;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import androidx.appcompat.widget.Toolbar;

public class AddDevicesActivity extends AppCompatActivity {

    private static final String TAG = "AddDevicesActivity";

    Toolbar toolbar;
    RecyclerView recyclerView;
    AddDevicesAdapter addDevicesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_add_devices);
        initView();

        initEvent();

    }


    private void initView(){
        toolbar = findViewById(R.id.add_devices_toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.add_devices_recycler);
    }

    private void initEvent(){

        addDevicesAdapter = new AddDevicesAdapter(getResources().getStringArray(R.array.test_add_devices_item_data));
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        recyclerView.setAdapter(addDevicesAdapter);
        addDevicesAdapter.setOnClickListener(onClickListener);

    }

    private ClickListener.OnClickListener onClickListener = new ClickListener.OnClickListener() {
        @Override
        public void onClick(int i) {
            Intent intent = new Intent(getApplicationContext(),ConnectDevicesActivity.class);
            startActivity(intent);
        }
    };


}
