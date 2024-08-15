package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityTransactionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Transaction extends AppCompatActivity {
ActivityTransactionBinding binding;
TransactionAdapter adapter;
List<TransactionModel> list;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String accountNumber;
    String uid;
    Apirequest request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        list = new ArrayList<>();
        binding.recyler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(this, list);
        binding.recyler.setAdapter(adapter);
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
        request.getTransactionByaccountNumber(accountNumber).enqueue(new Callback<List<TransactionModel>>() {
            @Override
            public void onResponse(Call<List<TransactionModel>> call, Response<List<TransactionModel>> response) {
                list.clear();
                List<TransactionModel> transactions = response.body();
                list.addAll(transactions);
                adapter.notifyDataSetChanged();
                if (response.isSuccessful() && response.body() != null) {
                    // Account found, stop scanning
                    Log.d("Transaction", "Account found: " + accountNumber);
                }
            }

            @Override
            public void onFailure(Call<List<TransactionModel>> call, Throwable t) {
                Log.e("Transaction", "Failed to connect to server", t);
            }
        });
    }
}