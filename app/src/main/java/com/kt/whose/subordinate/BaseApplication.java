package com.kt.whose.subordinate;

import android.app.Application;

import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.style.MaterialStyle;
import com.kongzue.dialogx.util.InputInfo;
import com.kongzue.dialogx.util.TextInfo;
import com.kt.whose.subordinate.Utils.Preferences;

import org.litepal.LitePal;

import okhttp3.OkHttpClient;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;
import rxhttp.RxHttpPlugins;
import rxhttp.wrapper.callback.Consumer;
import rxhttp.wrapper.callback.Function;
import rxhttp.wrapper.param.Param;
import rxhttp.wrapper.param.RxHttp;

public class BaseApplication extends Application {
    private static BaseApplication instance;
    private static String Token;
    private static String UserId;

    public static BaseApplication getInstance() {
        return instance;
    }

    public static String getToken() {
        return Token;
    }

    public static void setToken(String token) {
        Token = token;
    }

    public static String getUserId() {
        return UserId;
    }

    public static void setUserId(String userId) {
        UserId = userId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;


        if (!Preferences.getValue("defaultConfig", false)) {
//
            Preferences.setValue("mqtt-host", "124.70.108.79");
//            Preferences.setValue("mqtt-host", "192.168.0.7");
            Preferences.setValue("mqtt-port", 1883);
            Preferences.setValue("defaultConfig", true);

        }

        RxHttpPlugins.init(new OkHttpClient.Builder().build()).setOnParamAssembly(new Consumer<Param<?>>() {
            @Override
            public void accept(Param<?> param) {
                param.addHeader("UserToken",Token);
            }
        });

        LitePal.initialize(this);


        SQLiteStudioService.instance().start(this);

        DialogX.init(this);
        DialogX.globalStyle = MaterialStyle.style();

        DialogX.globalTheme = DialogX.THEME.AUTO;
        DialogX.titleTextInfo = new TextInfo().setFontSize(18);
        DialogX.okButtonTextInfo = new TextInfo().setFontColor(getResources().getColor(R.color.purple_500));
        DialogX.buttonTextInfo = new TextInfo().setFontColor(getResources().getColor(R.color.purple_200));
        DialogX.inputInfo =
                new InputInfo()
                        .setBottomLineColor(getResources().getColor(R.color.purple_100))
                        .setCursorColor(getResources().getColor(R.color.purple_200));


    }
}
