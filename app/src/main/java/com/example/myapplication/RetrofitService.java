package com.example.myapplication;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;
    public RetrofitService(){
        intialize();
    }
    private void intialize(){
        retrofit=new Retrofit.Builder().baseUrl("http://192.168.5.5:8080").
                addConverterFactory(GsonConverterFactory.create(new Gson())).build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
