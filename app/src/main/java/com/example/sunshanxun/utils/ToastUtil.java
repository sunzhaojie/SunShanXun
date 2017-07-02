package com.example.sunshanxun.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.sunshanxun.constant.SXGlobals;

/**
 * Created by SunZJ on 16/10/4.
 */
public class ToastUtil {

    private static Toast sToast;

    public static void toast(String msg) {
        Context context = SXGlobals.getApplication();
        if (sToast != null) {
            sToast.cancel();
            sToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            sToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }

        sToast.show();
    }
}
