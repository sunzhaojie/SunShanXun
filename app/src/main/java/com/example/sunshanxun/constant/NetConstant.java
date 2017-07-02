package com.example.sunshanxun.constant;

/**
 * Created by SunZJ on 16/10/4.
 */
public class NetConstant {

    /**
     * code
     */
    public static final int NULL = -3, ERR = -2, ROUTER_LOGIN_FAIL = -1, NOT_CONNECTED = 0, CONNECTED = 1, LINKING = 2, NAME_PWD_WRONG = 3,
            NO_RESPONSE = 4, UNKNOW_REASON = 5, WAN_NO_CONNECTED = 6;


    /**
     * wan type
     */
    public static final String[] WAN_TYPE = new String[]{"未连接", "已连接", "正在连接...",
            "用户名或密码验证失败", "服务器无响应", "未知原因失败", "WAN口未连接！"};
}
