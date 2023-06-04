package com.kt.whose.subordinate;

import android.app.Application;
import org.litepal.LitePal;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);


        SQLiteStudioService.instance().start(this);


    }
}
