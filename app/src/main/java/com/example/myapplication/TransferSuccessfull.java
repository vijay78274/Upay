package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.myapplication.databinding.ActivityTransferSuccessfullBinding;

public class TransferSuccessfull extends AppCompatActivity {
ActivityTransferSuccessfullBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferSuccessfullBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.success.setImageResource(R.drawable.success);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(TransferSuccessfull.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}