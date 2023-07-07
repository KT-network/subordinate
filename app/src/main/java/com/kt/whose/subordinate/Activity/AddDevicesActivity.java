package com.kt.whose.subordinate.Activity;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kongzue.dialogx.dialogs.PopTip;
import com.kt.whose.subordinate.Adapter.AddDevicesAdapter;
import com.kt.whose.subordinate.BaseApplication;
import com.kt.whose.subordinate.Broadcast.BroadcastTag;
import com.kt.whose.subordinate.HttpEntity.DevicesType;
import com.kt.whose.subordinate.HttpEntity.DevicesTypeList;
import com.kt.whose.subordinate.Interface.ClickListener;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.ExtendView.RefreshLottieHeader;
import com.kt.whose.subordinate.Utils.Tool;
import com.rxjava.rxlife.RxLife;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import androidx.appcompat.widget.Toolbar;

import java.util.List;
import rxhttp.wrapper.param.RxHttp;


public class AddDevicesActivity extends BaseActivity {

    private static final String TAG = "AddDevicesActivity";

    // 返回的状态码（0 表示为添加任何设备，1表示添了设备）
    private int returnResultCode = 0;
    Toolbar toolbar;
    RecyclerView recyclerView;
    AddDevicesAdapter addDevicesAdapter;
    SmartRefreshLayout smartRefreshLayout;
    RefreshLottieHeader refreshLottieHeader;

    List<DevicesType> devicesTypeList;
    Handler handler;

    @Override
    public int initLayoutId() {
        return R.layout.acitvity_add_devices;
    }

    @Override
    public void initView() {

        toolbar = findViewById(R.id.add_devices_toolbar);
        toolbar.setOnClickListener(toolBarOnClickListener);
        smartRefreshLayout = findViewById(R.id.add_devices_smartRefreshLayout);
        recyclerView = findViewById(R.id.add_devices_recycler);

    }

    @Override
    public void initEvent() {
        handler = new Handler();
        broadcastFilter();

        refreshLottieHeader = new RefreshLottieHeader(this);
        refreshLottieHeader.setAnimationViewJson("002.json");
        // smartRefreshLayout设置头刷新
        smartRefreshLayout.setRefreshHeader(refreshLottieHeader);
        // smartRefreshLayout 监听
        smartRefreshLayout.setOnRefreshListener(onRefreshListener);
        smartRefreshLayout.autoRefresh();

        addDevicesAdapter = new AddDevicesAdapter(getApplicationContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recyclerView.setAdapter(addDevicesAdapter);
        addDevicesAdapter.setOnClickListener(onClickListener);
    }

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            getDevicesType();
        }
    };


    private void getDevicesType() {
        Log.i(TAG, "getDevicesType: "+BaseApplication.getToken());

        if (BaseApplication.getToken() == null){
            finishRefresh(500);
            PopTip.show("登录已过期").iconWarning();
            return;
        }

        RxHttp.postJson("/devices/type/list")
                .toObservableResponse(DevicesTypeList.class)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    refreshDataDispose(s.getDevicesType());
                    finishRefresh(500);
                }, e -> {
                    int code = Tool.ErrorInfo(e);
                    if (code == 6) {
                        broadcastUpdateLoginState(false);
                    }
                    finishRefresh(500);
                });

    }

    private void refreshDataDispose(List<DevicesType> devices) {

        if (devices == null || devices.size() == 0){
            return;
        }else {
            devicesTypeList = devices;
            addDevicesAdapter.setData(devices);
        }

    }

    void broadcastUpdateLoginState(boolean is) {
        Intent intent = new Intent(BroadcastTag.ACTION_LOGIN_STATE);
        intent.putExtra(BroadcastTag.ACTION_LOGIN_STATE, is);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 完成刷新
     *
     * @param s 延迟时间
     */
    private void finishRefresh(int s) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                smartRefreshLayout.finishRefresh();
            }
        }, s);

    }


    private ClickListener.OnClickListener onClickListener = new ClickListener.OnClickListener() {
        @Override
        public void onClick(int i) {
            DevicesType devicesType = devicesTypeList.get(i);
            Log.i(TAG, "onClick: "+devicesType.getType());
            Intent intent = new Intent(getApplicationContext(), ConnectDevicesActivity.class);
            intent.putExtra("devicesType",devicesType);
            startActivity(intent);

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
