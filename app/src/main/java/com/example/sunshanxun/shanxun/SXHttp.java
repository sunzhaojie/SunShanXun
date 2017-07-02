package com.example.sunshanxun.shanxun;

import android.text.TextUtils;
import android.util.Log;


import com.example.sunshanxun.constant.NetConstant;
import com.example.sunshanxun.utils.Base64;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SunZJ on 16/10/2.
 */
public class SXHttp {

    private static final String TAG = "SXHttp";

    public static int sErrCode = -1;

    private static volatile SXHttp sSXHttp;

    public static SXHttp getInstance() {
        if (sSXHttp == null) {
            synchronized (SXHttp.class) {
                if (sSXHttp == null) {
                    sSXHttp = new SXHttp();
                }
            }
        }
        return sSXHttp;
    }

    private SXHttp() {
    }


    /**
     * 拨号
     *
     * @param routerName
     * @param routerPwd
     * @param shanXunName
     * @param shanXunPwd
     * @return
     */
    public int dial(String routerName, String routerPwd, String shanXunName, String shanXunPwd) {
        if (TextUtils.isEmpty(routerName) || TextUtils.isEmpty(routerPwd) || TextUtils.isEmpty(shanXunName) || TextUtils.isEmpty(shanXunPwd)) {
            sErrCode = -1;
            return NetConstant.ERR;
        }
        int r;
        shanXunName = "\r\n" + SXRealName.getInstance().getRealName(shanXunName, 0);
        String acc = str2HexStr(shanXunName);
        String str = routerName + ":" + routerPwd;
        String authorization = new Base64().encode(str.getBytes());
        String path = "http://192.168.1.1/userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&acc=" + acc +
                "&psw=" + shanXunPwd + "&confirm=" + shanXunPwd + "&specialDial=100&SecType=1&sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=4&waittime2=0&Connect=%C1%AC+%BD%D3";
        try {
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Authorization", "Basic " + authorization);
            con.setRequestMethod("GET");
            con.setRequestProperty("Host", "192.168.1.1");
            con.setRequestProperty("Referer", "http://192.168.1.1/userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&acc=" + acc + "&psw=" + shanXunPwd +
                    "&confirm=" + shanXunPwd + "&SecType=1&sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=4&waittime2=0&Disconnect=%B6%CF+%CF%DF");
            con.setRequestProperty("Cookie", "Authorization=Basic " + authorization);
            String result = getHttpResponse(con);
            if (result == null) {
                sErrCode = -2;
                return NetConstant.ERR;
            } else if ("401".equals(result)) {
                return NetConstant.ROUTER_LOGIN_FAIL;
            }
            String[] strs = result.split("Hello123World");
            if (strs.length >= 2) {
                strs = strs[1].split(",");
                if (strs.length >= 19) {
                    r = Integer.valueOf(strs[18].trim());
                    Log.d(TAG, "dial: " + strs[18].trim());
                } else {
                    sErrCode = -3;
                    return NetConstant.ERR;
                }
            } else {
                sErrCode = -4;
                return NetConstant.ERR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sErrCode = -5;
            r = NetConstant.ERR;
        }

        return r;
    }

    /**
     * 获取无线状态
     *
     * @param routerName
     * @param routerPwd
     * @return -1:路由器登录失败
     * "未连接",
     * "已连接",
     * "正在连接...",
     * "用户名或密码验证失败",
     * "服务器无响应",
     * "未知原因失败",
     * "WAN口未连接！"
     */
    public int getWanType(String routerName, String routerPwd) {
        if (TextUtils.isEmpty(routerName) || TextUtils.isEmpty(routerPwd)) {
            sErrCode = -6;
            return NetConstant.ERR;
        }
        int r;
        String str = routerName + ":" + routerPwd;
        String authorization = new Base64().encode(str.getBytes());
        String path = "http://192.168.1.1/userRpm/PPPoECfgRpm.htm";
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authorization);
            connection.setRequestProperty("Referer",
                    "http://192.168.1.1/userRpm/WanCfgRpm.htm");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", "Authorization=Basic " + authorization);
            String result = getHttpResponse(connection);
            if (result == null) {
                sErrCode = -7;
                return NetConstant.ERR;
            } else if ("401".equals(result)) {
                return NetConstant.ROUTER_LOGIN_FAIL;
            }
            String[] strs = result.split("Hello123World");
            if (strs.length >= 2) {
                strs = strs[1].split(",");
                if (strs.length >= 19) {
                    r = Integer.valueOf(strs[18].trim());
                    Log.d(TAG, "dial: " + strs[18].trim());
                } else {
                    sErrCode = -8;
                    return NetConstant.ERR;
                }
            } else {
                sErrCode = -9;
                return NetConstant.ERR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sErrCode = -10;
            r = NetConstant.ERR;
        }
        return r;
    }

    private String getHttpResponse(HttpURLConnection connection) {
        if (connection == null) {
            return null;
        }
        String result = null;
        try {
            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);
            int code = connection.getResponseCode();
            switch (code) {
                case 200:
                    InputStream in = connection.getInputStream();
                    result = readStream(in);
                    break;
                case 401:
                    result = "401";
                    break;
                default:
                    Log.d(TAG, "getHttpResponse: " + code);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getHttpResponse: " + e.getMessage());
        }

        return result;
    }

    /**
     * 把输入流的内容转换成字符串
     *
     * @param is
     * @return null解析失败， string读取成功
     */
    public static String readStream(InputStream is) {
        if (is == null) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            is.close();
            String tempText = new String(baos.toByteArray());
            if (tempText.contains("charset=gb2312")) {//解析meta标签
                return new String(baos.toByteArray(), "gb2312");
            } else {
                return new String(baos.toByteArray(), "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String str2HexStr(String paramString) {
        char[] arrayOfChar = "0123456789ABCDEF".toCharArray();
        StringBuffer localStringBuffer = new StringBuffer("");
        byte[] arrayOfByte = paramString.getBytes();
        for (int i = 0; ; i++) {
            if (i >= arrayOfByte.length)
                return localStringBuffer.toString();
            localStringBuffer.append('%');
            localStringBuffer
                    .append(arrayOfChar[((0xF0 & arrayOfByte[i]) >> 4)]);
            localStringBuffer.append(arrayOfChar[(0xF & arrayOfByte[i])]);
        }
    }

}
