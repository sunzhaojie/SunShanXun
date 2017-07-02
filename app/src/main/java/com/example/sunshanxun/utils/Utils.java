package com.example.sunshanxun.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.sunshanxun.constant.SXGlobals;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by SunZJ on 2017/6/11.
 */

public class Utils {

    //一个整形常量
    public static final int MY_PERMISSIONS_REQUEST = 3000;
    //定义一个list，用于存储需要申请的权限
    private static ArrayList<String> permissionList=new ArrayList<>();

    static {
        /**
         * 自动获取闪讯密码功能需要的权限
         */
        permissionList.add(Manifest.permission.READ_SMS);

    }


    //调用封装好的申请权限的方法
    public static void checkAndRequestPermissions(Activity activity) {

        ArrayList<String> list = new ArrayList<>(permissionList);
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String permission = it.next();
            //检查权限是否已经申请
            int hasPermission = ContextCompat.checkSelfPermission(SXGlobals.getApplication(), permission);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                it.remove();
            }
        }

        if (list.size() == 0) {
            return;
        }
        String[] permissions = list.toArray(new String[0]);
        //正式请求权限
        ActivityCompat.requestPermissions(activity, permissions, MY_PERMISSIONS_REQUEST);

    }
}
