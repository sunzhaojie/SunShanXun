package com.example.sunshanxun.network.bean;

/**
 * Created by SunZJ on 2017/5/8.
 */

public class CrashLogBean {
    public static final int DEVICE_ANDROID = 1, DEVICE_IOS = 2;
    private int id;
    private String imsi;
    private String imei;
    private long timeStamp;
    /**
     * 1 android
     * 2 ios
     */
    private int deviceType;
    private String msg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "CrashLogBean{" +
                "id=" + id +
                ", imsi='" + imsi + '\'' +
                ", imei='" + imei + '\'' +
                ", timeStamp=" + timeStamp +
                ", deviceType=" + deviceType +
                ", msg='" + msg + '\'' +
                '}';
    }
}
