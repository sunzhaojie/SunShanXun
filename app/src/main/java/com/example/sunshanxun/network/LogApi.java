package com.example.sunshanxun.network;

import com.example.sunshanxun.network.bean.BaseResultBean;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by SunZJ on 2017/5/8.
 */

public interface LogApi {
    @POST("log/reportLog")
    @FormUrlEncoded
    Call<BaseResultBean> reportLog(@Field("logBean") String logBean);

    @POST("log/reportCrashLog")
    @FormUrlEncoded
    Call<BaseResultBean> reportCrashLog(@Field("crashLogBean") String crashLogBean);
}
