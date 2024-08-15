package com.example.myapplication;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Apirequest {
    @GET("api/account/{username}")
    Call<payment> getAccountByUsername(@Path("username") String username);
    @GET("/api/account/get-all")
    Call<List<payment>> getAll();
    @POST("/api/account/save")
    Call<payment> save(@Body payment pay);
    @GET("/api/account/ping") // Example endpoint
    Call<Void> checkServerStatus();
    @POST("api/account/transfer")
    Call<String> transferMoney(
            @Query("fromUsername") String fromUsername,
            @Query("toUsername") String toUsername,
            @Query("amount") Double amount
    );
    @POST("api/account/number/transfer")
    Call<ResponseBody> transferMoneyByAccount(
            @Query("fromAccountNumber") String fromAccountNumber,
            @Query("toAccountNumber") String toAccountNumber,
            @Query("amount") Double amount
    );
    @POST("api/account/phone/transfer")
    Call<ResponseBody> transferMoneyByPhone(
            @Query("fromPhone") String fromAccountNumber,
            @Query("toPhone") String toAccountNumber,
            @Query("amount") Double amount
    );
    @GET("/api/account/phone/{phoneNumber}")
    Call<payment> getAccountByPhoneNumber(@Path("phoneNumber") String phoneNumber);
    @POST("/api/account/saveByAccount")
    Call<payment> saveByAccountNumber(@Body payment pay);
    @GET("/api/account/number/{accountNumber}")
    Call<payment> getAccountByaccountNumber(@Path("accountNumber") String accountNumber);
    @GET("/transactions/user/{accountNumber}")
    Call<List<TransactionModel>> getTransactionByaccountNumber(@Path("accountNumber") String accountNumber);
}
