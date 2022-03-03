package com.trickyworld.simpleloginapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String URL_LOGIN = "http://192.168.49.100/simple_login_app/";
    private static Retrofit retrofit = null;
    public static Retrofit getRetrofitInstance(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
            .baseUrl(URL_LOGIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        }
        return retrofit;
    }
}
