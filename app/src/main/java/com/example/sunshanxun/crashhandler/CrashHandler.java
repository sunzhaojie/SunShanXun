package com.example.sunshanxun.crashhandler;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.sunshanxun.constant.DataConstant;
import com.example.sunshanxun.constant.SXGlobals;
import com.example.sunshanxun.network.LogApi;
import com.example.sunshanxun.network.RequestClient;
import com.example.sunshanxun.network.bean.BaseResultBean;
import com.example.sunshanxun.network.bean.CrashLogBean;
import com.example.sunshanxun.utils.Fileutil;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by SunZJ on 2016/11/6.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    private static volatile CrashHandler sCrashHandler;
    private static final Thread.UncaughtExceptionHandler sDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

    public static CrashHandler getInstance() {
        if (sCrashHandler == null) {
            synchronized (CrashHandler.class) {
                if (sCrashHandler == null) {
                    sCrashHandler = new CrashHandler();
                }
            }
        }
        return sCrashHandler;
    }

    private CrashHandler() {

    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        writeCrashLog(DataConstant.CRASH_LOG_FILE, getThrowableMsg(ex));
        reportCrashLog(getThrowableMsg(ex));
        if (sDefaultHandler != null) {
            sDefaultHandler.uncaughtException(thread, ex);
        }
    }

    private String getThrowableMsg(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        if (ex != null) {
            StackTraceElement[] stes = ex.getStackTrace();
            if (stes != null) {
                for (StackTraceElement ste : stes) {
                    sb.append(ste.toString()).append("\r\n");
                }
            }
        }
        return ex.toString() + "\r\n" + sb.toString();
    }

    public String buildBody(Context context) {
        StringBuilder sb = new StringBuilder();

        sb.append("APPLICATION INFORMATION").append('\n');
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = context.getApplicationInfo();
        sb.append("Application : ").append(pm.getApplicationLabel(ai)).append('\n');

        try {
            PackageInfo pi = pm.getPackageInfo(ai.packageName, 0);
            sb.append("Version Code: ").append(pi.versionCode).append('\n');
            sb.append("Version Name: ").append(pi.versionName).append('\n');
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        sb.append("DEVICE INFORMATION").append('\n');
        sb.append("Board: ").append(Build.BOARD).append('\n');
        sb.append("BOOTLOADER: ").append(Build.BOOTLOADER).append('\n');
        sb.append("BRAND: ").append(Build.BRAND).append('\n');
        sb.append("CPU_ABI: ").append(Build.CPU_ABI).append('\n');
        sb.append("CPU_ABI2: ").append(Build.CPU_ABI2).append('\n');
        sb.append("DEVICE: ").append(Build.DEVICE).append('\n');
        sb.append("DISPLAY: ").append(Build.DISPLAY).append('\n');
        sb.append("FINGERPRINT: ").append(Build.FINGERPRINT).append('\n');
        sb.append("HARDWARE: ").append(Build.HARDWARE).append('\n');
        sb.append("HOST: ").append(Build.HOST).append('\n');
        sb.append("ID: ").append(Build.ID).append('\n');
        sb.append("MANUFACTURER: ").append(Build.MANUFACTURER).append('\n');
        sb.append("PRODUCT: ").append(Build.PRODUCT).append('\n');
        sb.append("TAGS: ").append(Build.TAGS).append('\n');
        sb.append("TYPE: ").append(Build.TYPE).append('\n');
        sb.append("USER: ").append(Build.USER);

        return sb.toString();
    }

    private void writeCrashLog(String filePath, String msg) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(new Date());
        StringBuffer sb = new StringBuffer();
        sb.append("\r\n").append(time).append("\r\n").append(buildBody(SXGlobals.getApplication())).append("\r\n").append(msg).append("\r\n");
        Fileutil.writeStringAppend(filePath, sb.toString());
    }

    private void reportCrashLog(String msg) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(new Date());
        StringBuffer sb = new StringBuffer();
        sb.append("\r\n").append(time).append("\r\n").append(buildBody(SXGlobals.getApplication())).append("\r\n").append(msg).append("\r\n");

        TelephonyManager mTelephonyMgr = (TelephonyManager) SXGlobals.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId(); //获取IMSI号
        String imei = mTelephonyMgr.getDeviceId(); //获取IMEI号
        Log.d(TAG, "onCreate: " + imsi + "sun" + imei);

        CrashLogBean crashLogBean = new CrashLogBean();
        crashLogBean.setImsi(imsi);
        crashLogBean.setImei(imei);
        crashLogBean.setTimeStamp(System.currentTimeMillis());
        crashLogBean.setDeviceType(CrashLogBean.DEVICE_ANDROID);
        crashLogBean.setMsg(sb.toString());
        Gson gson = new Gson();
        String json = gson.toJson(crashLogBean);
        RequestClient.getApi(LogApi.class).reportCrashLog(json).enqueue(new Callback<BaseResultBean>() {
            @Override
            public void onResponse(Response<BaseResultBean> response, Retrofit retrofit) {
                BaseResultBean bean = response.body();
                if (bean != null) {
                    Log.d(TAG, "onResponse: " + bean.toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }
}
