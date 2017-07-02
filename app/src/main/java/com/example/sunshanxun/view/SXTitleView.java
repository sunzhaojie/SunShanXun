package com.example.sunshanxun.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sunshanxun.R;
import com.example.sunshanxun.utils.DeviceInfoUtil;


/**
 * Created by SunZJ on 16/7/4.
 */
public class SXTitleView extends RelativeLayout implements View.OnClickListener {

    private static final int ID_LEFT_IMAGE = 0x1001;
    private static final int ID_TITLE = 0x1002;
    private static final int ID_RIGHT_BUTTON = 0x1003;

    private int mLeftImageRes;
    private CharSequence mTitleText;
    private CharSequence mRightText;
    TextView titleView, rightView;
    private OnTitleClickListener mOnTitleClickListener;

    public interface OnTitleClickListener {
        void onLogoClick(View v);

        void onTitleClick(View v);

        void onButtonClick(View v);
    }

    public SXTitleView(Context context) {
        this(context, null);
    }

    public SXTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SXTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromAttributes(context, attrs);
        initChildViews(context);
        setBgColor(getResources().getColor(R.color.colorPrimary));
    }

    private void initFromAttributes(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IKTitleView);
        mLeftImageRes = a.getResourceId(R.styleable.IKTitleView_leftImage, -1);
        mTitleText = a.getText(R.styleable.IKTitleView_titleText);
        mRightText = a.getText(R.styleable.IKTitleView_rightText);
    }

    private void initChildViews(Context context) {
        LayoutParams params;
        // add left imageview
        if (mLeftImageRes != -1) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(mLeftImageRes);
            params = new LayoutParams(DeviceInfoUtil.getPixelFromDip(26), DeviceInfoUtil.getPixelFromDip(26));
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.leftMargin = DeviceInfoUtil.getPixelFromDip(15);
            imageView.setLayoutParams(params);
            imageView.setId(ID_LEFT_IMAGE);
            imageView.setOnClickListener(this);
            addView(imageView, params);
        }

        // add title view
        titleView = new TextView(context);
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(18);
        titleView.setText(mTitleText);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        titleView.setLayoutParams(params);
        titleView.setId(ID_TITLE);
        titleView.setOnClickListener(this);
        addView(titleView, params);

        // add right view
        if (!TextUtils.isEmpty(mRightText)) {
            rightView = new TextView(context);
            rightView.setTextColor(Color.WHITE);
            rightView.setTextSize(18);
            rightView.setText(mRightText);
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            params.rightMargin = DeviceInfoUtil.getPixelFromDip(15);
            rightView.setLayoutParams(params);
            rightView.setId(ID_RIGHT_BUTTON);
            rightView.setOnClickListener(this);
            addView(rightView, params);
        }

    }

    /**
     * 设置标题栏点击监听
     *
     * @param listener
     */
    public void setOnTitleClickListener(OnTitleClickListener listener) {
        mOnTitleClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case ID_LEFT_IMAGE:
                if (mOnTitleClickListener != null) {
                    mOnTitleClickListener.onLogoClick(v);
                }
                sendKeyBackEvent();
                break;
            case ID_TITLE:
                if (mOnTitleClickListener != null) {
                    mOnTitleClickListener.onTitleClick(v);
                }
                break;
            case ID_RIGHT_BUTTON:
                if (mOnTitleClickListener != null) {
                    mOnTitleClickListener.onButtonClick(v);
                }
                break;
        }
    }

    private void sendKeyBackEvent() {
        final Context context = getContext();
        if (context instanceof Activity) {
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
            (((Activity) context)).getWindow().getDecorView().dispatchKeyEvent(keyEvent);
        }
    }

    public void setTitleText(CharSequence title) {
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    public void setBgColor(int color) {
        setBackgroundColor(color);
    }

    public void setRightText(CharSequence text) {
        if (rightView != null) {
            rightView.setText(text);
        }
    }

    public void hideButton() {
        if (rightView != null) {
            rightView.setVisibility(View.GONE);
        }
    }

    public void showButton() {
        if (rightView != null) {
            rightView.setVisibility(View.VISIBLE);
        }
    }

}
