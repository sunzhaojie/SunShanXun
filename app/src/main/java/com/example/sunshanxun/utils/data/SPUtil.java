package com.example.sunshanxun.utils.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.sunshanxun.constant.SXGlobals;

/**
 * Created by SunZJ on 16/10/6.
 */
public class SPUtil {

    private static final String FILE_NAME = "sun_data";

    public static void setParam(String key, Object value) {
        Context context = SXGlobals.getApplication();
        if (TextUtils.isEmpty(key) || value == null) {
            return;
        }
        String type = value.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) value);
        } else if ("String".equals(type)) {
            editor.putString(key, (String) value);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) value);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) value);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) value);
        }

        editor.commit();
    }

    public static Object getParam(String key, Object defaultValue) {
        Context context = SXGlobals.getApplication();
        if (TextUtils.isEmpty(key) || defaultValue == null) {
            return null;
        }
        String type = defaultValue.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultValue);
        } else if ("String".equals(type)) {
            return sp.getString(key, (String) defaultValue);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultValue);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultValue);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultValue);
        }

        return null;
    }
}
