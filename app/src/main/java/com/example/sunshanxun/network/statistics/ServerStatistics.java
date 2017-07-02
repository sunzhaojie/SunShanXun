package com.example.sunshanxun.network.statistics;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.sunshanxun.BuildConfig;
import com.example.sunshanxun.constant.SXGlobals;
import com.example.sunshanxun.network.LogApi;
import com.example.sunshanxun.network.RequestClient;
import com.example.sunshanxun.network.bean.BaseResultBean;
import com.example.sunshanxun.network.bean.CrashLogBean;
import com.example.sunshanxun.network.bean.LogBean;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by SunZJ on 2017/5/8.
 */

public class ServerStatistics {
    private static final String TAG = "ServerStatistics";
    private static JSONObject mCommonJson = null;
    //不上报
    private static boolean CAN_REPORT = false;

    private static JSONObject commontParams() {
        if (mCommonJson != null) {
            return mCommonJson;
        }
        Context context = SXGlobals.getApplication();
        JSONObject object = new JSONObject();
        try {
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

            object.put("commont_params", sb.toString());

        } catch (JSONException e) {
        }
        mCommonJson = object;
        return mCommonJson;
    }

    public static void report(String key1, String key2, String args) {
        if (CAN_REPORT == false) {
            return;
        }
        try {
            //检查权限是否已经申请
            int hasPermission = ContextCompat.checkSelfPermission(SXGlobals.getApplication(), Manifest.permission.READ_PHONE_STATE);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            JSONObject commonContent = commontParams();
            commonContent.put("args", args);
            TelephonyManager mTelephonyMgr = (TelephonyManager) SXGlobals.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = mTelephonyMgr.getSubscriberId(); //获取IMSI号
            String imei = mTelephonyMgr.getDeviceId(); //获取IMEI号
            LogBean logBean = new LogBean();
            logBean.setDeviceType(LogBean.DEVICE_ANDROID);
            logBean.setImsi(imsi);
            logBean.setImei(imei);
            logBean.setTimeStamp(System.currentTimeMillis());
            logBean.setKey1(key1);
            logBean.setKey2(key2);
            logBean.setArgs(commonContent.toString());
            Gson gson = new Gson();
            sendToServer(gson.toJson(logBean), true);
        } catch (JSONException e) {
        }
    }

    private static void sendToServer(final String content, final boolean resend) {
        if (content == null) {
            if (BuildConfig.DEBUG_LOG) {
                Log.d(TAG, "illegal content");
            }
            return;
        }

        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "send: " + content);
        }

        RequestClient.getApi(LogApi.class)
                .reportLog(content)
                .enqueue(new Callback<BaseResultBean>() {

                    @Override
                    public void onResponse(Response<BaseResultBean> response, Retrofit retrofit) {
                        if (BuildConfig.DEBUG_LOG) {
                            if (response.body() != null) {
                                Log.d(TAG, "response: " + response.body().toString());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (BuildConfig.DEBUG_LOG) {
                            Log.d(TAG, "onFail: " + t.getMessage());
                        }
                    }
                });
    }
}
