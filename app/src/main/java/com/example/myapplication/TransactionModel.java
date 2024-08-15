package com.example.myapplication;

public class TransactionModel {
    private String transactionId;
    private String transactionType;
    private Double amount;
    private String status;

    public String getAccountNumber() {
        return toAccountNumber;
    }

    public void setAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    private String toAccountNumber;
    private String createdAt;
    public TransactionModel(){

    }
    public TransactionModel(String transactionId, String transactionType, Double amount, String status, String createdAt, String toAccountNumber) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.toAccountNumber=toAccountNumber;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getcreatedAt() {
        return createdAt;
    }

    public void setcreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

