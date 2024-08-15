package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityTranferToContactBinding;
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

public class TranferToContact extends AppCompatActivity {
ActivityTranferToContactBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String mobileNo;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTranferToContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String name = getIntent().getStringExtra("name");
        String phoneNo = getIntent().getStringExtra("phoneNo");
        binding.phone.setText(String.format("%s : %s", name, phoneNo));
        RetrofitService service = new RetrofitService();
        Apirequest request = service.getRetrofit().create(Apirequest.class);
        auth= FirebaseAuth.getInstance();
        uid=auth.getUid();
        database= FirebaseDatabase.getInstance();
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
                String money = binding.amount.getText().toString();
                Double amount = Double.parseDouble(money);
                if(amount<1.00){
                    binding.amount.setError("Enter a valid amount");
                }
                else{
                    request.transferMoneyByPhone(mobileNo,phoneNo,amount).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                try {
                                    // Read the plain text response
                                    String result = response.body().string();
                                    if(result.equals("Insufficient balance")) {
                                        Intent intent = new Intent(TranferToContact.this,TransferFailed.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Intent intent = new Intent(TranferToContact.this,TransferSuccessfull.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(TranferToContact.this, "Error reading response", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(TranferToContact.this, "Transfer failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(TranferToContact.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Profile", "Request failed", t);
                        }
                    });
                }
            }
        });
    }
}