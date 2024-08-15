package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityNumberBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NumberActivity extends AppCompatActivity {
    ActivityNumberBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String mobileNo;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RetrofitService service = new RetrofitService();
        Apirequest request = service.getRetrofit().create(Apirequest.class);
        auth= FirebaseAuth.getInstance();
        uid=auth.getUid();
        database=FirebaseDatabase.getInstance();
        database.getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                payment pay = snapshot.getValue(payment.class);
                mobileNo = pay.getPhoneNumber();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = binding.phone.getText().toString();
                String money = binding.amount.getText().toString();
                Double amount = Double.parseDouble(money);
                if (str.isEmpty()) {
                    binding.phone.setError("This field cannot be empty");
                }
                else if(amount<1.00){
                    binding.amount.setError("Enter a valid ammount");
                }
                else{
                    request.transferMoneyByPhone(mobileNo,str,amount).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                try {
                                    // Read the plain text response
                                    String result = response.body().string();
                                    if(result.equals("Insufficient balance")) {
                                        Intent intent = new Intent(NumberActivity.this,TransferFailed.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Intent intent = new Intent(NumberActivity.this,TransferSuccessfull.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(NumberActivity.this, "Error reading response", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(NumberActivity.this, "Transfer failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(NumberActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Profile", "Request failed", t);
                        }
                    });
                }
            }
        });
    }
}