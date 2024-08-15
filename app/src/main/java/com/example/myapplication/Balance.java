package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityBalanceBinding;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Balance extends AppCompatActivity {
ActivityBalanceBinding binding;
FirebaseDatabase database;
FirebaseAuth auth;
String uid;
String accountNumber;
Apirequest request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBalanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        database = FirebaseDatabase.getInstance();
        RetrofitService service = new RetrofitService();
        request = service.getRetrofit().create(Apirequest.class);
        database.getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                payment pay = snapshot.getValue(payment.class);
                accountNumber = pay.getAccountNumber();
                addElements();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error","Failed to retrieve", error.toException());
            }
        });
    }
    public void addElements(){
        request.getAccountByaccountNumber(accountNumber).enqueue(new Callback<payment>() {
            @Override
            public void onResponse(Call<payment> call, Response<payment> response) {
                payment pay = response.body();
                binding.balance.setText(String.format("₹ %s", pay.getBalance().toString()));
                binding.id.setText(pay.getAccountNumber());
                binding.name.setText(pay.getUsername());
            }

            @Override
            public void onFailure(Call<payment> call, Throwable t) {
                Toast.makeText(Balance.this,"Failed to connnect to server",Toast.LENGTH_SHORT).show();
            }
        });
    }
}