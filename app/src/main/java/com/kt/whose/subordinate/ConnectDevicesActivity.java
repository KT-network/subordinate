package com.kt.whose.subordinate;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.flod.loadingbutton.LoadingButton;

public class ConnectDevicesActivity extends AppCompatActivity {

    Toolbar toolbar;
    LoadingButton loadingButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_devices);
        initView();
    }

    private void initView() {

        toolbar = findViewById(R.id.connect_devices_toolbar);
        loadingButton = findViewById(R.id.connect_devices_but);
        loadingButton.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            loadingButton.start();

        }
    };

}
