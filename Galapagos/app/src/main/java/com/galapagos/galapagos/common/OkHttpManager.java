package com.galapagos.galapagos.common;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by yeji on 2016. 11. 25..
 */

public class OkHttpManager {//서버랑 안드로이드랑 통신을 위한 아이를 만들어주고 그 아이의 속성들을 설정해주는 부분
    private static OkHttpClient okHttpClient;
    private static final int OKHTTP_INIT_VALUE = 12;
    static{
        okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(OKHTTP_INIT_VALUE, TimeUnit.SECONDS)
                .readTimeout(OKHTTP_INIT_VALUE, TimeUnit.SECONDS)
                .build();
    }
    public static OkHttpClient getOkHttpClient(){
        if( okHttpClient != null){
            return okHttpClient;
        }else{
            okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(OKHTTP_INIT_VALUE, TimeUnit.SECONDS)
                    .readTimeout(OKHTTP_INIT_VALUE, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }

}
