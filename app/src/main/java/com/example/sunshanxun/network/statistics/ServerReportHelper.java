package com.example.sunshanxun.network.statistics;

/**
 * Created by SunZJ on 2017/5/8.
 */

public class ServerReportHelper {
    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String KEY_ARGS = "args";

    private static final String KEY1_START = "start";
    private static final String KEY1_SHOW = "show";
    private static final String KEY1_CLICK = "click";
    private static final String KEY1_DIAL_RESULT = "dial_result";
    private static final String KEY1_SET_AUTO_DIAL_TIME = "set_auto_dial_time";


    private static final String KEY2_PAGE_MAIN = "main";
    private static final String KEY2_BTN_DIAL = "btn_dial";


    /**
     * report start app
     */
    public static void reportStart() {
        ServerStatistics.report(KEY1_START, "", "");
    }

    /**
     * 主页显示
     */
    public static void reportMainShow() {
        ServerStatistics.report(KEY1_SHOW, KEY2_PAGE_MAIN, "");
    }

    /**
     * 点击拨号
     */
    public static void reportClickDial() {
        ServerStatistics.report(KEY1_CLICK, KEY2_BTN_DIAL, "");
    }

    /**
     * 拨号结果
     */
    public static void reportDialResult(String result, String args) {
        ServerStatistics.report(KEY1_DIAL_RESULT, result, args);
    }

    /**
     * 设置自动拨号时间
     */
    public static void reportSetAutoDialTime(String time) {
        ServerStatistics.report(KEY1_SET_AUTO_DIAL_TIME, time, "");
    }
}
