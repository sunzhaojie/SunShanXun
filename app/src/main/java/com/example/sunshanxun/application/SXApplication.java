package com.example.sunshanxun.application;

import android.app.Application;

import com.example.sunshanxun.constant.SXGlobals;
import com.example.sunshanxun.crashhandler.CrashHandler;
import com.example.sunshanxun.network.statistics.ServerReportHelper;

/**
 * Created by SunZJ on 2016/10/14.
 */

public class SXApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SXGlobals.sApplication = this;
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance());
        ServerReportHelper.reportStart();
    }
}



