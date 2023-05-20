package com.kt.whose.subordinate.Fragment.Main;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kt.whose.subordinate.Fragment.NewLazyFragment;
import com.kt.whose.subordinate.R;

public class DevicesFragment extends NewLazyFragment {

    FloatingActionButton mDevicesFloating;
    RecyclerView mDevicesRecycler;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_main_devices;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mDevicesFloating = view.findViewById(R.id.devices_fab);
        mDevicesRecycler = view.findViewById(R.id.devices_recycler);
    }



}
