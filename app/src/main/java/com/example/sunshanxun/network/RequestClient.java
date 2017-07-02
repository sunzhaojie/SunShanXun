package com.example.sunshanxun.network;

import java.util.HashMap;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by SunZJ on 2017/5/7.
 */

public class RequestClient {
    public static String IP="192.168.1.103";
    public static String BASE_URL = "http://"+IP+":8080/";

    private static RequestClient mInstance;
    private Retrofit mRetrofit;

    private static RequestClient getInstance() {
        if (mInstance == null) {
            mInstance = new RequestClient();
        }
        return mInstance;
    }

    private RequestClient() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static final HashMap<Class, Object> cache = new HashMap<>();

    public static synchronized <T> T getApi(Class<T> t) {
        T api = (T) cache.get(t);
        if (api == null) {
            api = getInstance().mRetrofit.create(t);
            cache.put(t, api);
        }
        return api;
    }
}
