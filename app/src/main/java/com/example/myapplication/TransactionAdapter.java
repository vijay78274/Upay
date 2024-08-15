package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private Context context;
    private List<TransactionModel> trasactionList;

    public TransactionAdapter(Context context, List<TransactionModel> transactionList) {
        this.context = context;
        this.trasactionList = transactionList;
    }
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent,false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionModel transaction = trasactionList.get(position);
        holder.amount.setText(String.format("Amount: %s", transaction.getAmount().toString()));
        holder.id.setText(String.format("ID: %s", transaction.getTransactionId()));
        if(transaction.getTransactionType().equals("CREDIT")){
            holder.account.setText(String.format("From account: %s", transaction.getAccountNumber()));
        }
        else{
            holder.account.setText(String.format("To account: %s", transaction.getAccountNumber()));
        }
        holder.type.setText(transaction.getTransactionType());
        holder.status.setText(transaction.getStatus());
        if(transaction.getStatus().equals("COMPLETED")) {
            holder.status.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
        }
        else{
            holder.status.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.red));
        }
        holder.time.setText(transaction.getcreatedAt());

    }

    @Override
    public int getItemCount() {
        return trasactionList.size();
    }
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView amount;
        TextView time;
        TextView status;
        TextView type;
        TextView account;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            id=itemView.findViewById(R.id.id);
            amount=itemView.findViewById(R.id.amount);
            time=itemView.findViewById(R.id.time);
            status=itemView.findViewById(R.id.status);
            type=itemView.findViewById(R.id.type);
            account=itemView.findViewById(R.id.accountNumber);
        }
    }
}

