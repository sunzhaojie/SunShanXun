package com.example.sunshanxun.main;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunshanxun.R;
import com.example.sunshanxun.constant.DataConstant;
import com.example.sunshanxun.constant.NetConstant;
import com.example.sunshanxun.network.RequestClient;
import com.example.sunshanxun.shanxun.SXHttp;
import com.example.sunshanxun.utils.Fileutil;
import com.example.sunshanxun.utils.ToastUtil;
import com.example.sunshanxun.utils.data.SPUtil;
import com.example.sunshanxun.view.SXTitleView;

public class TestAty extends BaseAty {
    private static final int H_NET_TYPE = 1;
    private SXTitleView mTitle;
    private EditText mEdtChangeIP;
    private Button mBtnShowCrashLogs, mBtnNetType, mBtnChangeIP;
    private TextView mTvLogs;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case H_NET_TYPE:
                    int r = msg.arg1;
                    if (r >= 0 && r < NetConstant.WAN_TYPE.length) {
                        ToastUtil.toast(NetConstant.WAN_TYPE[r]);
                    } else {
                        ToastUtil.toast("error type: " + r);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_aty);

        mTvLogs = (TextView) findViewById(R.id.id_tv_logs);
        mTitle = (SXTitleView) findViewById(R.id.id_title_view);
        mTitle.setOnTitleClickListener(new SXTitleView.OnTitleClickListener() {
            @Override
            public void onLogoClick(View v) {
                TestAty.this.finish();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onButtonClick(View v) {

            }
        });


        mBtnShowCrashLogs = (Button) findViewById(R.id.id_btn_show_crash_logs);
        mBtnShowCrashLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = Fileutil.readString(DataConstant.CRASH_LOG_FILE, "") + "";
                mTvLogs.setText(str);
            }
        });

        mBtnNetType = (Button) findViewById(R.id.id_btn_net_type);
        mBtnNetType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        String routerName = (String) SPUtil.getParam(DataConstant.ROUTER_NAME, "admin");
                        String routerPwd = (String) SPUtil.getParam(DataConstant.ROUTER_PWD, "admin");
                        int r = SXHttp.getInstance().getWanType(routerName, routerPwd);
                        Message message = new Message();
                        message.what = H_NET_TYPE;
                        message.arg1 = r;
                        mHandler.sendMessage(message);
                    }
                }.start();
            }
        });

        mEdtChangeIP = (EditText) findViewById(R.id.id_edit_report_logs_ip);
        mBtnChangeIP = (Button) findViewById(R.id.id_btn_change_report_logs_ip);
        mBtnChangeIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEdtChangeIP.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(mEdtChangeIP.getText())) {
                    RequestClient.IP = mEdtChangeIP.getText().toString();
                    mEdtChangeIP.setVisibility(View.GONE);
                    ToastUtil.toast("修改成功");
                } else {
                    ToastUtil.toast("请输入IP");
                }
            }
        });
    }
}
