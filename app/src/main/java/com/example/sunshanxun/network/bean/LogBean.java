package com.example.sunshanxun.network.bean;

/**
 * Created by SunZJ on 2017/5/8.
 */

public class LogBean {
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
    private String key1;
    private String key2;
    private String args;

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "LogBean{" +
                "id=" + id +
                ", imsi='" + imsi + '\'' +
                ", imei='" + imei + '\'' +
                ", timeStamp=" + timeStamp +
                ", deviceType=" + deviceType +
                ", key1='" + key1 + '\'' +
                ", key2='" + key2 + '\'' +
                ", args='" + args + '\'' +
                '}';
    }
}

