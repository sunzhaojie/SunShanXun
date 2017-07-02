package com.example.sunshanxun.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.sunshanxun.constant.NetConstant;
import com.example.sunshanxun.constant.DataConstant;
import com.example.sunshanxun.main.MainAty;
import com.example.sunshanxun.network.statistics.ServerReportHelper;
import com.example.sunshanxun.shanxun.SXHttp;
import com.example.sunshanxun.utils.Fileutil;
import com.example.sunshanxun.utils.data.SPUtil;
import com.example.sunshanxun.utils.sms.SMSUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SunZJ on 2016/10/18.
 */

public class DialService extends Service {
    private static final String TAG = "DialService";
    public static volatile boolean sIsAutoDial = false;

    private volatile int mCode = NetConstant.NULL;

    private String mRouterName = "";
    private String mRouterPwd = "";
    private String mSxName = "";
    private String mSxPwd = "";
    private SMSObserver mSmsObserver;
    private String dialState;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mSmsObserver = new SMSObserver(null);
        getContentResolver().registerContentObserver(SMSUtil.SMS_INBOX, true, mSmsObserver);

        if (intent != null) {
            mRouterName = intent.getStringExtra("mRouterName");
            mRouterPwd = intent.getStringExtra("mRouterPwd");
            mSxName = intent.getStringExtra("mSxName");
            mSxPwd = intent.getStringExtra("mSxPwd");
        } else {
            mRouterName = (String) SPUtil.getParam(DataConstant.ROUTER_NAME, "");
            mRouterPwd = (String) SPUtil.getParam(DataConstant.ROUTER_PWD, "");
            mSxName = (String) SPUtil.getParam(DataConstant.SX_NAME, "");
            mSxPwd = (String) SPUtil.getParam(DataConstant.SX_PWD, "");
        }
        boolean flage = (boolean) SPUtil.getParam(DataConstant.SX_GET_PWD, false);
        dialState = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("router_name", mRouterName);
            jsonObject.put("router_pwd", mRouterPwd);
            jsonObject.put("rsx_name", mSxName);
            jsonObject.put("rsx_pwd", mSxPwd);
            jsonObject.put("is_get_sx_pwd", flage);
            jsonObject.put("dial_type", "自动拨号");
            dialState = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sIsAutoDial = true;
        if (flage == false) {
            dial(mRouterName, mRouterPwd, mSxName, mSxPwd);
        } else {
            SMSUtil.sendMessage(DataConstant.SX_GET_PWD_PHONE, "mm");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DialBinder();
    }

    /**
     * 拨号
     *
     * @param mRouterName
     * @param mRouterPwd
     * @param mSxName
     * @param mSxPwd
     */
    private void dial(String mRouterName, String mRouterPwd, String mSxName, String mSxPwd) {
        mCode = NetConstant.NULL;
        sIsAutoDial = false;
        DialThread dialThread = new DialThread(mRouterName, mRouterPwd, mSxName, mSxPwd);
        dialThread.start();
    }


    /**
     * 拨号线程
     */
    private class DialThread extends Thread {

        private String mRouterName, mRouterPwd, mSxName, mSxPwd;

        public DialThread(String mRouterName, String mRouterPwd, String mSxName, String mSxPwd) {
            this.mRouterName = mRouterName;
            this.mRouterPwd = mRouterPwd;
            this.mSxName = mSxName;
            this.mSxPwd = mSxPwd;
        }

        @Override
        public void run() {
            if (TextUtils.isEmpty(mRouterName) || TextUtils.isEmpty(mRouterPwd) || TextUtils.isEmpty(mSxName) || TextUtils.isEmpty(mSxPwd)) {
                return;
            }
            int r = SXHttp.getInstance().dial(mRouterName, mRouterPwd, mSxName, mSxPwd);
            Log.d(TAG, "run code: " + r);
            Fileutil.writeString(DataConstant.AUTO_DIAL_LOG_FILE, "", "");
            boolean flage = true;
            while (flage == true) {
                flage = false;
                switch (r) {
                    case NetConstant.ROUTER_LOGIN_FAIL:
                        mCode = NetConstant.ROUTER_LOGIN_FAIL;
                        break;
                    case NetConstant.NOT_CONNECTED:
                        mCode = NetConstant.NOT_CONNECTED;
                        break;
                    case NetConstant.CONNECTED:
                        mCode = NetConstant.CONNECTED;
                        break;
                    case NetConstant.LINKING:
                        mCode = NetConstant.LINKING;
                        flage = true;
                        break;
                    case NetConstant.NAME_PWD_WRONG:
                        mCode = NetConstant.NAME_PWD_WRONG;
                        break;
                    case NetConstant.NO_RESPONSE:
                        mCode = NetConstant.NO_RESPONSE;
                        break;
                    case NetConstant.UNKNOW_REASON:
                        mCode = NetConstant.UNKNOW_REASON;
                        break;
                    case NetConstant.WAN_NO_CONNECTED:
                        mCode = NetConstant.WAN_NO_CONNECTED;
                        break;
                    case NetConstant.ERR:
                        mCode = NetConstant.ERR;
                        break;
                    default:
                        break;
                }
                writeLog(mCode);
                try {
                    if (flage == true) {
                        r = SXHttp.getInstance().getWanType(mRouterName, mRouterPwd);
                        Thread.sleep(1500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeLog(int code) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        String date = format.format(new Date());
        switch (code) {
            case NetConstant.ROUTER_LOGIN_FAIL:
                ServerReportHelper.reportDialResult(mSxName + ":" + "路由器登录失败", dialState);
                Fileutil.writeStringAppend(DataConstant.AUTO_DIAL_LOG_FILE, date + ": " + "路由器登录失败" + "\r\n");
                break;
            case NetConstant.NOT_CONNECTED:
            case NetConstant.CONNECTED:
            case NetConstant.LINKING:
            case NetConstant.NAME_PWD_WRONG:
            case NetConstant.NO_RESPONSE:
            case NetConstant.UNKNOW_REASON:
            case NetConstant.WAN_NO_CONNECTED:
                if (code != NetConstant.LINKING) {
                    ServerReportHelper.reportDialResult(mSxName + ":" + NetConstant.WAN_TYPE[code], dialState);
                }
                Fileutil.writeStringAppend(DataConstant.AUTO_DIAL_LOG_FILE, date + ": " + NetConstant.WAN_TYPE[code] + "\r\n");
                break;
            case NetConstant.ERR:
                ServerReportHelper.reportDialResult(mSxName + ":" + "连接超时  error code：" + SXHttp.sErrCode + "\r\n", dialState);
                Fileutil.writeStringAppend(DataConstant.AUTO_DIAL_LOG_FILE, date + ": " + "连接超时  error code：" + SXHttp.sErrCode + "\r\n");
                break;
            default:
                break;
        }
    }

    public class DialBinder extends Binder {
        public DialService getService() {
            return DialService.this;
        }
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        synchronized (this) {
            this.mCode = code;
        }
    }

    /**
     * 短信监听
     */
    private class SMSObserver extends ContentObserver {

        private Handler handler;
        private boolean flag = true;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public SMSObserver(Handler handler) {
            super(handler);
            this.handler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            if (sIsAutoDial == false) {
                return;
            }

            ContentResolver resolver = getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            Cursor cursor = resolver.query(uri, null, null, null, "date");
            cursor.moveToLast();
            if (!cursor.getString(cursor.getColumnIndex("address")).equals(
                    DataConstant.SX_GET_PWD_PHONE)) {
                return;
            }

            if (flag == false) {
                flag = !flag;
                return;
            } else {
                flag = !flag;
            }
            String body = SMSUtil.getMessage(DataConstant.SX_GET_PWD_PHONE);
            String sxPwd = SMSUtil.getSMSCaptcha(body, 6);
            if (sxPwd == null) {
                return;
            }
            dial(mRouterName, mRouterPwd, mSxName, sxPwd);

        }

    }
}
