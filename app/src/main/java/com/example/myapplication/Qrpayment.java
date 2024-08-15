package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityQrpaymentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Qrpayment extends AppCompatActivity {
ActivityQrpaymentBinding binding;
FirebaseAuth auth;
FirebaseDatabase database;
String accountNo;
String uid;
String userAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrpaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RetrofitService service = new RetrofitService();
        Apirequest request = service.getRetrofit().create(Apirequest.class);
        accountNo=getIntent().getStringExtra("accountNo");
        binding.phone.setText(accountNo);
        auth= FirebaseAuth.getInstance();
        uid=auth.getUid();
        database=FirebaseDatabase.getInstance();
        database.getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                payment pay = snapshot.getValue(payment.class);
                userAccount = pay.getAccountNumber();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double amount = Double.parseDouble(binding.amount.getText().toString());
                if(amount<1.00){
                    binding.amount.setError("Enter a valid amount");
                }
                else{
                    request.transferMoneyByAccount(userAccount,accountNo,amount).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                try {
                                    // Read the plain text response
                                    String result = response.body().string();
                                    if(result.equals("Insufficient balance")) {
                                        Intent intent = new Intent(Qrpayment.this,TransferFailed.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Intent intent = new Intent(Qrpayment.this,TransferSuccessfull.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(Qrpayment.this, "Error reading response", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Qrpayment.this, "Transfer failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(Qrpayment.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Profile", "Request failed", t);
                        }
                    });
                }
            }
        });
    }
}