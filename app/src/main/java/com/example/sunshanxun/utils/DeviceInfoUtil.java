package com.example.sunshanxun.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.example.sunshanxun.constant.SXGlobals;

/**
 * Created by SunZJ on 16/10/4.
 */
public class DeviceInfoUtil {

    public static int getPixelFromDip(float f) {
        return getPixelFromDip(SXGlobals.getApplication().getResources().getDisplayMetrics(), f);
    }

    /**
     * Dip转换为实际屏幕的像素值
     *
     * @param dm  设备显示对象描述
     * @param dip dip值
     * @return 匹配当前屏幕的像素值
     */
    public static int getPixelFromDip(DisplayMetrics dm, float dip) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, dm) + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        Display display = getDisplay(SXGlobals.getApplication());
        if (display != null) {
            return display.getWidth();
        }
        return 0;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        Display display = getDisplay(SXGlobals.getApplication());
        if (display != null) {
            return display.getHeight();
        }
        return 0;
    }

    public static Display getDisplay(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return null;
        }
        return windowManager.getDefaultDisplay();
    }

    public static boolean isInteractive(Context context) {
        PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        if (mgr != null) {
            return mgr.isScreenOn();
        }

        return false;
    }


    /**
     * 获取Text的宽度
     *
     * @param str
     * @param paint
     * @return
     */
    public static float getTextWidth(String str, Paint paint) {
        Rect rect = new Rect();
        if (str == null) {
            str = "";
        }
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.width();
    }

    /**
     * 获取Text的高度
     *
     * @param str
     * @param paint
     * @return
     */
    public static float getTextHeight(String str, Paint paint) {
        Rect rect = new Rect();
        if (str == null) {
            str = "";
        }
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.height();
    }
}
