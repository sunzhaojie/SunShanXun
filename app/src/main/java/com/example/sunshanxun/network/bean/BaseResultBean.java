package com.example.sunshanxun.network.bean;

/**
 * Created by SunZJ on 2017/5/8.
 */

public class BaseResultBean {
    public static final int SUCCESS = 1;
    public static final int FAILURE = -1;

    int code;
    String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "BaseResultBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
