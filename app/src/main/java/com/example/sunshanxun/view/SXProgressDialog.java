package com.example.sunshanxun.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.example.sunshanxun.R;


/**
 * Created by SunZJ on 16/10/2.
 */
public class SXProgressDialog extends Dialog {

    public SXProgressDialog(Context context, String strMessage) {
        this(context, R.style.SXProgressDialog, strMessage);
    }

    public SXProgressDialog(Context context, int theme, String msg) {
        super(context, theme);
        this.setContentView(R.layout.progress_dialog_layout);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        TextView tvMsg = (TextView) this.findViewById(R.id.id_tv_msg);
        if (tvMsg != null) {
            tvMsg.setText(msg);
        }
        this.setCancelable(false);
    }
}
