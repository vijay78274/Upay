package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {
ActivityProfileBinding binding;
String name;
String phone;
FirebaseAuth auth;
String uid;
FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RetrofitService service = new RetrofitService();
        auth=FirebaseAuth.getInstance();
        uid=auth.getUid();
        database=FirebaseDatabase.getInstance();
        Apirequest request = service.getRetrofit().create(Apirequest.class);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = binding.name.getText().toString();
                phone = binding.phone.getText().toString();
                payment pay = new payment();
                pay.setUsername(name);
                pay.setPhoneNumber(phone);
                request.checkServerStatus().enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(Profile.this, "Connected to server", Toast.LENGTH_SHORT).show();
                            Log.d("Profile", "Server connection successful: " + response.code());
                        } else {
                            Toast.makeText(Profile.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                            Log.e("Profile", "Server connection failed with response code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(Profile.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                        Log.e("Profile", "Connection failed", t);
                    }
                });
//
                request.saveByAccountNumber(pay).enqueue(new Callback<payment>() {
                    @Override
                    public void onResponse(@NonNull Call<payment> call, @NonNull Response<payment> response) {
                        payment model = response.body();
                        String accoutNo = model.getAccountNumber();
                        saveToDatabase(name,phone,accoutNo);
                    }

                    @Override
                    public void onFailure(@NonNull Call<payment> call, @NonNull Throwable t) {
                        Toast.makeText(Profile.this, "Failed", Toast.LENGTH_SHORT).show();
                        Log.e("Profile", "not saved", t);
                    }
                });
            }
        });
    }
    public void saveToDatabase(String name, String phone, String accounNo){
        payment pay = new payment(name,phone,accounNo);
        database.getReference("users").child(uid).setValue(pay).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Profile.this,"Saved",Toast.LENGTH_SHORT).show();
                Log.d("Profile", "Sending data: " + new Gson().toJson(pay));
                Intent intent = new Intent(Profile.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}