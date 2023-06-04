package com.kt.whose.subordinate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.kt.whose.subordinate.Fragment.Main.DevicesFragment;
import com.kt.whose.subordinate.Fragment.Main.DiscoverFragment;
import com.kt.whose.subordinate.Fragment.Main.MeFragment;
import com.kt.whose.subordinate.Utils.model.DevicesInfoSql;

import org.litepal.LitePal;

import java.util.List;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    FrameLayout frameLayout;
    AnimatedBottomBar bottom_bar;

    DevicesFragment mDevicesFragment;
    DiscoverFragment mDiscoverFragment;
    MeFragment mMeFragment;


    final FragmentManager supportFragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();


        List<DevicesInfoSql> devicesInfoSqlList = LitePal.findAll(DevicesInfoSql.class);

        Log.i(TAG, "onCreate: "+devicesInfoSqlList.size());





    }

    private void initView() {
        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle(R.string.toolbar_title_devices);
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.main_fragment);
        bottom_bar = findViewById(R.id.main_bottom_bar);
        bottom_bar.setSelectedTabType(AnimatedBottomBar.TabType.ICON);
        bottom_bar.setTabColor(getResources().getColor(R.color.purple_200));
        bottom_bar.setOnTabSelectListener(onTabSelectListener);


        mDevicesFragment = new DevicesFragment();
        mDiscoverFragment = new DiscoverFragment();
        mMeFragment = new MeFragment();

        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        fragmentTransaction.add(R.id.main_fragment, mDevicesFragment);
        fragmentTransaction.add(R.id.main_fragment, mDiscoverFragment);
        fragmentTransaction.add(R.id.main_fragment, mMeFragment);


        fragmentTransaction.show(mDevicesFragment);
        fragmentTransaction.commit();

    }


    private void TabToFragment(int index) {
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:
                toolbar.setTitle(R.string.toolbar_title_devices);
                fragmentTransaction.show(mDevicesFragment);
                break;
            case 1:
                toolbar.setTitle(R.string.toolbar_title_discover);
                fragmentTransaction.show(mDiscoverFragment);
                break;
            case 2:
                toolbar.setTitle(R.string.toolbar_title_me);
                fragmentTransaction.show(mMeFragment);
                break;
        }

        fragmentTransaction.commit();

    }


    //将四个的Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (mDevicesFragment != null) {
            transaction.hide(mDevicesFragment);
        }
        if (mDiscoverFragment != null) {
            transaction.hide(mDiscoverFragment);
        }
        if (mMeFragment != null) {
            transaction.hide(mMeFragment);
        }
    }


    private AnimatedBottomBar.OnTabSelectListener onTabSelectListener = new AnimatedBottomBar.OnTabSelectListener() {
        @Override
        public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {

            TabToFragment(i1);
        }

        @Override
        public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

        }
    };


}