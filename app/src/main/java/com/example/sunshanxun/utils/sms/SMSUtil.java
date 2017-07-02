package com.example.sunshanxun.utils.sms;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.example.sunshanxun.constant.SXGlobals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SunZJ on 16/10/8.
 */
public class SMSUtil {

    public static final Uri SMS_INBOX = Uri.parse("content://sms/");

    public static void sendMessage(String number, String msg) {
        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(msg)) {
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number.trim(), null, msg.trim(), null, null);
    }

    public static String getMessage(String number) {
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        ContentResolver cr = SXGlobals.getApplication().getContentResolver();
        String[] projection = new String[]{"body"};
        String where = "address = '" + number + "' and date > " + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cursor = cr.query(SMS_INBOX, projection, where, null, "date desc");
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToNext()) {
            String body = cursor.getString(cursor.getColumnIndex("body"));
            return body;
        }
        return null;
    }

    /**
     * 获取短信验证码/密码
     *
     * @param msg    短信内容
     * @param length 验证码长度
     * @return
     */
    public static String getSMSCaptcha(String msg, int length) {
        if (TextUtils.isEmpty(msg) || length <= 0) {
            return null;
        }
        Pattern pattern = Pattern.compile("(?<![0-9])([0-9]){" + length + "}(?![0-9])");
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

}
