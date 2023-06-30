package com.kt.whose.subordinate;

import android.app.Application;

import com.kongzue.dialogx.DialogX;
import com.kt.whose.subordinate.Utils.Preferences;

import org.litepal.LitePal;

import okhttp3.OkHttpClient;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;
import rxhttp.RxHttpPlugins;

public class BaseApplication extends Application {
    private static BaseApplication instance;
    private static String Token;

    public static BaseApplication getInstance() {
        return instance;
    }

    public static String getToken() {
        return Token;
    }

    public static void setToken(String token) {
        Token = token;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;


        if (!Preferences.getValue("defaultConfig",false)){

            Preferences.setValue("mqtt-host","124.70.108.79");
            Preferences.setValue("mqtt-port",1883);
            Preferences.setValue("defaultConfig",true);

        }

        RxHttpPlugins.init(new OkHttpClient.Builder().build());

        LitePal.initialize(this);


        SQLiteStudioService.instance().start(this);

        DialogX.init(this);


    }
}
