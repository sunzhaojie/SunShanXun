package com.example.sunshanxun.shanxun;


import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Calendar;

/**
 * Created by SunZJ on 16/9/30.
 */
public class SXRealName {

    /**
     * 上次成功的时间处理
     */
    private long mLastTime;
    /**
     * 原始用户名
     */
    private String mUserName;
    /**
     * 真正的用户名
     */
    private String mReallyUserName;

    /**
     * 秘钥
     */
    private final String RADIUS = "singlenet01";
    /**
     * 回车换行
     */
    private final String LR = "\r\n";

    private volatile static SXRealName sSXRealName = null;

    public static SXRealName getInstance() {
        if (sSXRealName == null) {
            synchronized (SXRealName.class) {
                if (sSXRealName == null) {
                    sSXRealName = new SXRealName();
                }
            }
        }
        return sSXRealName;
    }

    private SXRealName() {

    }


    /**
     * 获取加密后的闪讯账号
     *
     * @return
     */
    public String getRealName(String username, long lasttime) {
        if (TextUtils.isEmpty(username)) {
            return null;
        }

        mUserName = username;
        mLastTime = lasttime;

        Calendar calendar = Calendar.getInstance();
        long mTime1c;
        long mTime1convert;
        byte[] ss = new byte[]{0, 0, 0, 0};// unsigned char byte

        byte[] ss2 = new byte[]{0, 0, 0, 0};
        String strS1 = "";
        String mFormatsring = "";
        String mMd5 = "";
        String mMd5use = "";


        {
            long t;
            t = calendar.getTimeInMillis() / 1000;// 得到系统时间
            t *= 0x66666667;
            t >>= 0x20;
            t >>= 0x01;
            mTime1c = (long) t;

        }


        if (mTime1c <= mLastTime) {
            mTime1c = mLastTime + 1;
        }
        mLastTime = mTime1c;


        {
            long t;
            t = mTime1c;
            ss2[3] = (byte) (t & 0xFF);
            ss2[2] = (byte) ((t & 0xFF00) / 0x100);
            ss2[1] = (byte) ((t & 0xFF0000) / 0x10000);
            ss2[0] = (byte) ((t & 0xFF000000) / 0x1000000);
            {

                try {
                    strS1 = new String(ss2, "ISO-8859-1");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }


        {
            int t, t1, t2, t3;
            t = (int) mTime1c;
            t1 = t;
            t2 = t;
            t3 = t;
            t3 = t3 << 0x10;
            t1 = t1 & 0x0FF00;
            t1 = t1 | t3;
            t3 = t;
            t3 = t3 & 0x0FF0000;
            t2 = t2 >> 0x10;
            t3 = t3 | t2;
            t1 = t1 << 0x08;
            t3 = t3 >> 0x08;
            t1 = t1 | t3;
            mTime1convert = t1;
        }


        {
            long t;
            t = mTime1convert;
            ss[3] = (byte) (t & 0xFF);
            ss[2] = (byte) ((t & 0xFF00) / 0x100);
            ss[1] = (byte) ((t & 0xFF0000) / 0x10000);
            ss[0] = (byte) ((t & 0xFF000000) / 0x1000000);
        }


        /**
         * sun ss byte ！负数
         */
        int ssInt[] = new int[]{0, 0, 0, 0};
        {
            ssInt[3] = (int) (ss[3] & 0xff);
            ssInt[2] = (int) (ss[2] & 0xff);
            ssInt[1] = (int) (ss[1] & 0xff);
            ssInt[0] = (int) (ss[0] & 0xff);
        }


        byte[] pp = new byte[]{0, 0, 0, 0};
        {
            int i = 0, j = 0, k = 0;
            for (i = 0; i < 0x20; i++) {
                j = i / 0x8;
                k = 3 - (i % 0x4);
                pp[k] *= 0x2;
                if (ssInt[j] % 2 == 1) {
                    pp[k]++;
                }
                ssInt[j] /= 2;
            }
        }

        /**
         * sun pp byte ！负数
         */
        int ppInt[] = new int[]{0, 0, 0, 0};
        {
            ppInt[3] = (int) (pp[3] & 0xff);
            ppInt[2] = (int) (pp[2] & 0xff);
            ppInt[1] = (int) (pp[1] & 0xff);
            ppInt[0] = (int) (pp[0] & 0xff);
        }


        /**
         * 格式符计算,mFormatsring为结果
         */
        byte[] pf = new byte[]{0, 0, 0, 0, 0, 0};
        {
            short t1, t2;
            t1 = (short) ppInt[3];
            t1 /= 0x4;
            pf[0] = (byte) t1;
            t1 = (short) ppInt[3];
            t1 = (short) (t1 & 0x3);
            t1 *= 0x10;
            pf[1] = (byte) t1;
            t2 = (short) ppInt[2];
            t2 /= 0x10;
            t2 = (short) (t2 | t1);
            pf[1] = (byte) t2;
            t1 = (short) ppInt[2];
            t1 = (short) (t1 & 0x0F);
            t1 *= 0x04;
            pf[2] = (byte) t1;
            t2 = (short) ppInt[1];
            t2 /= 0x40;
            t2 = (short) (t2 | t1);
            pf[2] = (byte) t2;
            t1 = (short) ppInt[1];
            t1 = (short) (t1 & 0x3F);
            pf[3] = (byte) t1;
            t2 = (short) ppInt[0];
            t2 /= 0x04;
            pf[4] = (byte) t2;
            t1 = (short) ppInt[0];
            t1 = (short) (t1 & 0x03);
            t1 *= 0x10;
            pf[5] = (byte) t1;
        }
        {
            int i;
            for (i = 0; i < 6; i++) {
                pf[i] += 0x20;
                if ((pf[i]) >= 0x40) {
                    pf[i]++;
                }
            }
        }
        {
            for (int i = 0; i < 6; i++) {
                mFormatsring += (char) ((int) (pf[i] & 0xff));
            }
        }


        String strInput;
        strInput = strS1 + mUserName.split("@")[0] + RADIUS;

        try {
            mMd5 = getMD5(strInput, "ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMd5use = mMd5.substring(0, 2);
        mReallyUserName = mFormatsring + mMd5use.toLowerCase() + mUserName;

        return mReallyUserName;
    }


    public static String getMD5(String str, String encoding) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(encoding));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val < 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toUpperCase();
    }
}
