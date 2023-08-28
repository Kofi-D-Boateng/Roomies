package com.roomies.api.util.httpClient;

import okhttp3.OkHttpClient;

public class HttpClientSingleton {

    private static OkHttpClient CLIENT;

    private HttpClientSingleton(){
        CLIENT = new OkHttpClient();
    }

    public static OkHttpClient getClient(){
        if(CLIENT == null){
            CLIENT = new OkHttpClient();
        }
        return CLIENT;
    }

}
