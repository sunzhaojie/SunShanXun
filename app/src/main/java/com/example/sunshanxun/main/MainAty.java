package com.example.sunshanxun.main;


import android.Manifest;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.example.sunshanxun.R;
import com.example.sunshanxun.constant.NetConstant;
import com.example.sunshanxun.constant.DataConstant;
import com.example.sunshanxun.constant.SXGlobals;
import com.example.sunshanxun.network.statistics.ServerReportHelper;
import com.example.sunshanxun.service.DialService;
import com.example.sunshanxun.shanxun.SXHttp;
import com.example.sunshanxun.shanxun.SXRealName;
import com.example.sunshanxun.utils.Fileutil;
import com.example.sunshanxun.utils.ToastUtil;
import com.example.sunshanxun.utils.Utils;
import com.example.sunshanxun.utils.data.SPUtil;
import com.example.sunshanxun.utils.sms.SMSUtil;
import com.example.sunshanxun.view.SXDialogFragment;
import com.example.sunshanxun.view.SXTimeView;
import com.example.sunshanxun.view.SXTitleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by SunZJ on 16/7/3.
 */
public class MainAty extends BaseAty {

    private static final String TAG = "MainAty";
    private static final int H_ERR = NetConstant.ERR, H_ROUTER_LOGIN_FAIL = NetConstant.ROUTER_LOGIN_FAIL, H_NOT_CONNECTED = NetConstant.NOT_CONNECTED, H_CONNECTED = NetConstant.CONNECTED, H_LINKING = NetConstant.LINKING, H_NAME_PWD_WRONG = NetConstant.NAME_PWD_WRONG,
            H_NO_RESPONSE = NetConstant.NO_RESPONSE, H_UNKNOW_REASON = NetConstant.UNKNOW_REASON, H_WAN_NO_CONNECTED = NetConstant.WAN_NO_CONNECTED, H_SX_GET_PWD = H_WAN_NO_CONNECTED + 1;

    private SXTitleView mTitle;
    private EditText mEdtRouterName, mEdtRouterPwd, mEdtSxName, mEdtSxPwd;
    private CheckBox mCheckBoxRemember, mCheckBoxShowLogs, mCheckBoxGetSXPwd, mCheckBoxReconnect, mCheckBoxShowAutoLogs;
    private Button mBtnDial;
    private TextView mTextViewLogs, mTextViewAutoLogs, mTextViewAbout;

    private TextWatcher mTextWatcher;
    private int mEdtSxPwdTextWatcherCount = 0;//mEdtSxPwd TextWatcher Count
    private int mEditTextHaveInputCount = 0, EDITTEXT_AMOUNT = 4;
    private SMSObserver mSmsObserver;
    private DialService mDialService;
    private List<String> mHourStrs;
    private List<String> mMinuteStrs;
    private String dialState;
    private static long[] mHints = new long[5];


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDialService = ((DialService.DialBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
            String date = format.format(new Date());
            switch (msg.what) {
                case H_SX_GET_PWD:
                    mEdtSxPwd.setText(msg.obj + "");
                    showProgress("正在拨号");
                    dial(mEdtRouterName.getText().toString().trim(), mEdtRouterPwd.getText().toString().trim(), mEdtSxName.getText().toString().trim(), mEdtSxPwd.getText().toString().trim());
                    break;
                case H_ROUTER_LOGIN_FAIL:
                    ServerReportHelper.reportDialResult(mEdtSxName.getText().toString().trim() + ":" + "路由器登录失败", dialState);
                    ToastUtil.toast("路由器登录失败");
                    mTextViewLogs.append(date + ": " + "路由器登录失败" + "\r\n");
                    break;
                case H_NOT_CONNECTED:
                case H_CONNECTED:
                case H_LINKING:
                case H_NAME_PWD_WRONG:
                case H_NO_RESPONSE:
                case H_UNKNOW_REASON:
                case H_WAN_NO_CONNECTED:
                    if (msg.what != H_LINKING) {
                        ServerReportHelper.reportDialResult(mEdtSxName.getText().toString().trim() + ":" + NetConstant.WAN_TYPE[msg.arg1], dialState);
                    }
                    ToastUtil.toast(NetConstant.WAN_TYPE[msg.arg1]);
                    mTextViewLogs.append(date + ": " + NetConstant.WAN_TYPE[msg.arg1] + "\r\n");
                    break;
                case H_ERR:
                    ServerReportHelper.reportDialResult(mEdtSxName.getText().toString().trim() + ":" + "连接超时", dialState);
                    ToastUtil.toast("连接超时");
                    mTextViewLogs.append(date + ": " + "连接超时  error code：" + SXHttp.sErrCode + "\r\n");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(SXGlobals.getApplication(), DialService.class);
//        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        init();

        Utils.checkAndRequestPermissions(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        ServerReportHelper.reportMainShow();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void init() {
        initView();
        initData();
        initListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                int length = grantResults.length;
                boolean re_request = false;//标记位：如果需要重新授予权限，true；反之，false。
                for (int i = 0; i < length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "权限授予成功:" + permissions[i]);
                    } else {
                        Log.e(TAG, "权限授予失败:" + permissions[i]);
                        re_request = true;
                    }
                }
                if (re_request) {
                    //弹出对话框，提示用户重新授予权限
                    final SXDialogFragment permissionDialog = new SXDialogFragment();
                    permissionDialog.setDialogText("请授予相关权限，否则自动获取闪讯密码功能无效。\n\n点击确定，重新授予权限。\n点击取消，取消授权。\n");
                    permissionDialog.setOnLeftClickListener(new SXDialogFragment.OnLeftClickListener() {
                        @Override
                        public void onClick() {

                        }
                    });
                    permissionDialog.setOnRightClickListener(new SXDialogFragment.OnRightClickListener() {
                        @Override
                        public void onClick() {
                            Utils.checkAndRequestPermissions(MainAty.this);
                        }
                    });
                    permissionDialog.show(getFragmentManager(), "permissionDialog");
                }
                break;
            }
            default:
                break;
        }
    }

    private void initView() {
        mTitle = (SXTitleView) findViewById(R.id.id_title_view);
        mEdtRouterName = (EditText) findViewById(R.id.id_edit_router_name);
        mEdtRouterPwd = (EditText) findViewById(R.id.id_edit_router_password);
        mEdtSxName = (EditText) findViewById(R.id.id_edit_shanxun_name);
        mEdtSxPwd = (EditText) findViewById(R.id.id_edit_shanxun_password);
        mCheckBoxRemember = (CheckBox) findViewById(R.id.id_cb_remember);
        mCheckBoxShowLogs = (CheckBox) findViewById(R.id.id_cb_log);
        mCheckBoxReconnect = (CheckBox) findViewById(R.id.id_cb_reconnect);
        mCheckBoxGetSXPwd = (CheckBox) findViewById(R.id.id_cb_get_sx_pwd);
        mCheckBoxShowAutoLogs = (CheckBox) findViewById(R.id.id_cb_auto_log);
        mBtnDial = (Button) findViewById(R.id.id_btn_dial);
        mTextViewLogs = (TextView) findViewById(R.id.id_tv_logs);
        mTextViewAutoLogs = (TextView) findViewById(R.id.id_tv_auto_logs);
        mTextViewAbout = (TextView) findViewById(R.id.id_tv_about);
    }

    private void initData() {
        String routerName = (String) SPUtil.getParam(DataConstant.ROUTER_NAME, "");
        String routerPwd = (String) SPUtil.getParam(DataConstant.ROUTER_PWD, "");
        String sxName = (String) SPUtil.getParam(DataConstant.SX_NAME, "");
        String sxPwd = (String) SPUtil.getParam(DataConstant.SX_PWD, "");
        boolean flage = (boolean) SPUtil.getParam(DataConstant.SX_GET_PWD, false);
        mCheckBoxGetSXPwd.setChecked(flage);
        if (!TextUtils.isEmpty(routerName)) {
            mEdtRouterName.setText(routerName);
        }
        if (!TextUtils.isEmpty(routerPwd)) {
            mEdtRouterPwd.setText(routerPwd);
        }
        if (!TextUtils.isEmpty(sxName)) {
            mEdtSxName.setText(sxName);
        }
        if (!TextUtils.isEmpty(sxPwd)) {
            mEdtSxPwd.setText(sxPwd);
        }

        initEditTextChange();

        mSmsObserver = new SMSObserver(handler);
        getContentResolver().registerContentObserver(SMSUtil.SMS_INBOX, true, mSmsObserver);

        mHourStrs = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                mHourStrs.add("0" + i);
            } else {
                mHourStrs.add(i + "");
            }
        }
        mMinuteStrs = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                mMinuteStrs.add("0" + i);
            } else {
                mMinuteStrs.add(i + "");
            }
        }

    }


    private void initListener() {
        mTitle.setOnTitleClickListener(new SXTitleView.OnTitleClickListener() {
            @Override
            public void onLogoClick(View v) {

            }

            @Override
            public void onTitleClick(View v) {
                System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);//把从第二位至最后一位之间的数字复制到第一位至倒数第一位
                mHints[mHints.length - 1] = SystemClock.uptimeMillis();//从开机到现在的时间毫秒数
                if (SystemClock.uptimeMillis() - mHints[0] <= 1000) {//连续点击之间间隔小于一秒，有效
                    Intent intent = new Intent(SXGlobals.getApplication(), TestAty.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onButtonClick(View v) {

            }
        });
        mTextWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mEditTextHaveInputCount++;
                    if (mEditTextHaveInputCount == EDITTEXT_AMOUNT)
                        mBtnDial.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mEditTextHaveInputCount--;
                    mBtnDial.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };


        mEdtRouterName.addTextChangedListener(mTextWatcher);
        mEdtRouterPwd.addTextChangedListener(mTextWatcher);
        mEdtSxName.addTextChangedListener(mTextWatcher);
        mEdtSxPwd.addTextChangedListener(mTextWatcher);
        mEdtSxPwdTextWatcherCount++;

        mCheckBoxShowLogs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == false) {
                    mTextViewLogs.setVisibility(View.GONE);
                } else {
                    mCheckBoxShowAutoLogs.setChecked(false);
                    mTextViewLogs.setVisibility(View.VISIBLE);
                }
            }
        });
        mCheckBoxShowAutoLogs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == false) {
                    mTextViewAutoLogs.setVisibility(View.GONE);
                } else {
                    mCheckBoxShowLogs.setChecked(false);
                    String str = Fileutil.readString(DataConstant.AUTO_DIAL_LOG_FILE, "");
                    mTextViewAutoLogs.setText("自动拨号log\r\n" + str);
                    mTextViewAutoLogs.setVisibility(View.VISIBLE);
                }
            }
        });

        mCheckBoxGetSXPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    SXDialogFragment SXDialogFragment = new SXDialogFragment();
                    SXDialogFragment.show(getFragmentManager(), "fragment_dialog");
                    SXDialogFragment.setDialogText("注意：1.原先是固定密码的不要勾选，此功能会将固定密码变为动态密码 2.手机卡要是闪讯卡");
                    SXDialogFragment.setOnRightClickListener(new SXDialogFragment.OnRightClickListener() {
                        @Override
                        public void onClick() {
                            mCheckBoxGetSXPwd.setChecked(true);
                            if (mEditTextHaveInputCount == 3 && TextUtils.isEmpty(mEdtSxPwd.getText().toString().trim())) {
                                mBtnDial.setEnabled(true);
                            }
                            if (!TextUtils.isEmpty(mEdtSxPwd.getText().toString().trim())) {
                                mEditTextHaveInputCount--;
                            }
                            EDITTEXT_AMOUNT = 3;
                            if (mEdtSxPwdTextWatcherCount == 1) {
                                mEdtSxPwd.removeTextChangedListener(mTextWatcher);
                                mEdtSxPwdTextWatcherCount--;
                            }
                        }
                    });
                    SXDialogFragment.setOnLeftClickListener(new SXDialogFragment.OnLeftClickListener() {
                        @Override
                        public void onClick() {
                            mCheckBoxGetSXPwd.setChecked(false);
                        }
                    });
                } else {
                    if (mEdtSxPwdTextWatcherCount == 0) {
                        mEdtSxPwd.addTextChangedListener(mTextWatcher);
                        mEdtSxPwdTextWatcherCount++;
                    }
                    initEditTextChange();

                }
            }
        });

        mCheckBoxReconnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showTimePopup();
                }
            }
        });

        mBtnDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerReportHelper.reportClickDial();

                dialState = "";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("router_name", mEdtRouterName.getText().toString().trim());
                    jsonObject.put("router_pwd", mEdtRouterPwd.getText().toString().trim());
                    jsonObject.put("rsx_name", mEdtSxName.getText().toString().trim());
                    jsonObject.put("rsx_pwd", mEdtSxPwd.getText().toString().trim());
                    jsonObject.put("is_remember", mCheckBoxRemember.isChecked());
                    jsonObject.put("is_show_logs", mCheckBoxShowLogs.isChecked());
                    jsonObject.put("is_get_sx_pwd", mCheckBoxGetSXPwd.isChecked());
                    jsonObject.put("is_reconnect", mCheckBoxReconnect.isChecked());
                    jsonObject.put("is_show_auto_logs", mCheckBoxShowAutoLogs.isChecked());
                    jsonObject.put("dial_type", "手动拨号");
                    dialState = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mCheckBoxRemember.isChecked() == true) {
                    SPUtil.setParam(DataConstant.ROUTER_NAME, mEdtRouterName.getText().toString().trim());
                    SPUtil.setParam(DataConstant.ROUTER_PWD, mEdtRouterPwd.getText().toString().trim());
                    SPUtil.setParam(DataConstant.SX_NAME, mEdtSxName.getText().toString().trim());
                    SPUtil.setParam(DataConstant.SX_PWD, mEdtSxPwd.getText().toString().trim());
                }

                if (mCheckBoxGetSXPwd.isChecked() == false) {
                    showProgress("正在拨号");
                    dial(mEdtRouterName.getText().toString().trim(), mEdtRouterPwd.getText().toString().trim(), mEdtSxName.getText().toString().trim(), mEdtSxPwd.getText().toString().trim());
//                    DialThread thread = new DialThread();
//                    thread.start();
                } else {
                    showProgress("正在获取");
                    SMSUtil.sendMessage(DataConstant.SX_GET_PWD_PHONE, "mm");

                }
            }
        });

        findViewById(R.id.id_iv_float).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.toast("您的支持是我更新的动力");
                try {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("market://details?id="
                            + "com.example.sunshanxun");
                    intent.setData(content_url);
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });


        mTextViewAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SXGlobals.getApplication(), AboutAty.class);
                startActivity(intent);
            }
        });
    }


    private void initEditTextChange() {

        mEditTextHaveInputCount = 0;
        EDITTEXT_AMOUNT = 4;
        if (!TextUtils.isEmpty(mEdtRouterName.getText().toString())) {
            mEditTextHaveInputCount++;
        }
        if (!TextUtils.isEmpty(mEdtRouterPwd.getText().toString())) {
            mEditTextHaveInputCount++;
        }
        if (!TextUtils.isEmpty(mEdtSxName.getText().toString())) {
            mEditTextHaveInputCount++;
        }
        if (!TextUtils.isEmpty(mEdtSxPwd.getText().toString())) {
            mEditTextHaveInputCount++;
        }
        if (mEditTextHaveInputCount == EDITTEXT_AMOUNT) {
            mBtnDial.setEnabled(true);
        } else {
            mBtnDial.setEnabled(false);
        }
    }

    /**
     * 拨号
     *
     * @param mRouterName
     * @param mRouterPwd
     * @param mSxName
     * @param mSxPwd
     */
    public void dial(String mRouterName, String mRouterPwd, String mSxName, String mSxPwd) {
        if (TextUtils.isEmpty(mRouterName) || TextUtils.isEmpty(mRouterPwd) || TextUtils.isEmpty(mSxName) || TextUtils.isEmpty(mSxPwd)) {
            cancelProgress();
            return;
        }


        SPUtil.setParam(DataConstant.SX_GET_PWD, mCheckBoxGetSXPwd.isChecked());

        DialThread thread = new DialThread();
        thread.start();

    }

    /**
     * 自动重连
     */
    private void reconnect(int hour, int minute) {
        int interval = 1000 * 60 * 60 * 24;// 24h

        Intent intent = new Intent(SXGlobals.getApplication(), DialService.class);
        intent.putExtra("mRouterName", mEdtRouterName.getText().toString().trim());
        intent.putExtra("mRouterPwd", mEdtRouterPwd.getText().toString().trim());
        intent.putExtra("mSxName", mEdtSxName.getText().toString().trim());
        intent.putExtra("mSxPwd", mEdtSxPwd.getText().toString().trim());
        PendingIntent pendingIntent = PendingIntent.getService(SXGlobals.getApplication(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);

    }

    /**
     * 显示时间选择器
     */
    private void showTimePopup() {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        View menuView = LayoutInflater.from(this).inflate(R.layout.popup_windown_time_layout, null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int) (width * 0.8),
                ActionBar.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setAnimationStyle(R.style.pop_win_anim_style);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
//       mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1f);
            }
        });
        setBackgroundAlpha(0.6f);
        final SXTimeView mSXTimeViewHour = (SXTimeView) menuView.findViewById(R.id.id_time_view_hour);
        final SXTimeView mSXTimeViewMinute = (SXTimeView) menuView.findViewById(R.id.id_time_view_minute);
        mSXTimeViewHour.setData(mHourStrs, 7);
        mSXTimeViewMinute.setData(mMinuteStrs, 30);
        Button mButtonLeft = (Button) menuView.findViewById(R.id.id_btn_left);
        Button mButtonRight = (Button) menuView.findViewById(R.id.id_btn_right);
        mButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                setBackgroundAlpha(1.0f);
                mCheckBoxReconnect.setChecked(false);
            }
        });
        mButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                setBackgroundAlpha(1.0f);
                int hour = Integer.valueOf(mSXTimeViewHour.getSelectValue());
                int minute = Integer.valueOf(mSXTimeViewMinute.getSelectValue());
                reconnect(hour, minute);
                ServerReportHelper.reportSetAutoDialTime(hour + "时" + minute + "分");
                ToastUtil.toast(hour + "时" + minute + "分");
            }
        });


    }

    private void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }


    /**
     * 拨号线程
     */
    private class DialThread extends Thread {
        @Override
        public void run() {
            int r = SXHttp.getInstance().dial(mEdtRouterName.getText().toString().trim(),
                    mEdtRouterPwd.getText().toString().trim(), mEdtSxName.getText().toString().trim(),
                    mEdtSxPwd.getText().toString().trim());
            boolean flage = true;
            while (flage == true) {
                flage = false;
                Message msg = Message.obtain();
                msg.arg1 = r;
                switch (r) {
                    case NetConstant.ROUTER_LOGIN_FAIL:
                        msg.what = NetConstant.ROUTER_LOGIN_FAIL;
                        break;
                    case NetConstant.NOT_CONNECTED:
                        msg.what = NetConstant.NOT_CONNECTED;
                        break;
                    case NetConstant.CONNECTED:
                        msg.what = NetConstant.CONNECTED;
                        break;
                    case NetConstant.LINKING:
                        msg.what = NetConstant.LINKING;
                        flage = true;
                        break;
                    case NetConstant.NAME_PWD_WRONG:
                    case NetConstant.NO_RESPONSE:
                    case NetConstant.UNKNOW_REASON:
                    case NetConstant.WAN_NO_CONNECTED:
                        msg.what = r;
                        break;
                    case NetConstant.ERR:
                        msg.what = NetConstant.ERR;
                        break;
                    default:
                        break;
                }
                handler.sendMessage(msg);
                try {
                    if (flage == true) {
                        r = SXHttp.getInstance().getWanType(mEdtRouterName.getText().toString().trim(),
                                mEdtRouterPwd.getText().toString().trim());
                        Thread.sleep(1500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cancelProgress();
        }
    }

    /**
     * 短信 自动获取闪讯密码功能
     */
    private class SMSObserver extends ContentObserver {

        private Handler handler;
        private boolean flag = true;


        public SMSObserver(Handler handler) {
            super(handler);
            this.handler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            if (DialService.sIsAutoDial == true) {
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
            cancelProgress();
            Message msg = Message.obtain();
            msg.what = H_SX_GET_PWD;
            msg.obj = sxPwd;
            handler.sendMessage(msg);

        }

    }


}
