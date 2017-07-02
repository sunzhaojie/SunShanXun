package com.example.sunshanxun.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sunshanxun.R;

/**
 * Created by SunZJ on 2016/10/13.
 */

public class SXDialogFragment extends DialogFragment {

    private TextView mTextViewMsg;
    private Button mButtonLeft, mButtonRight;
    private String mMsg = null;
    private OnRightClickListener onRightClickListener;
    private OnLeftClickListener onLeftClickListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_layout, null);
        mTextViewMsg = (TextView) view.findViewById(R.id.id_tv_msg);
        mButtonLeft = (Button) view.findViewById(R.id.id_btn_left);
        mButtonRight = (Button) view.findViewById(R.id.id_btn_right);
        mButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onLeftClickListener!=null){
                    onLeftClickListener.onClick();
                }
                dismiss();
            }
        });
        mButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onRightClickListener!=null){
                    onRightClickListener.onClick();
                }
                dismiss();
            }
        });
        if (!TextUtils.isEmpty(mMsg)) {
            mTextViewMsg.setText(mMsg);
        } else {
            mTextViewMsg.setText("");
        }
        builder.setView(view).setCancelable(false);
        return builder.create();

    }

    public void setDialogText(String msg) {
        mMsg = msg;
        if (mTextViewMsg != null) {
            if (!TextUtils.isEmpty(msg)) {
                mTextViewMsg.setText(msg);
            } else {
                mTextViewMsg.setText("");
            }
        }
    }


    public interface OnRightClickListener {
        void onClick();
    }

    public interface OnLeftClickListener {
        void onClick();
    }

    public void setOnRightClickListener(OnRightClickListener onClickListener) {
        this.onRightClickListener = onClickListener;
    }

    public void setOnLeftClickListener(OnLeftClickListener onLeftClickListener) {
        this.onLeftClickListener = onLeftClickListener;
    }

}
