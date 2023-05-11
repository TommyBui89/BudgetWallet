package com.example.budgetapp.Model;

public class Transaction {
    private String category;
    private double amount;
    private String notes;
    private String date;
    private long dateinSeconds;


    public Transaction() {}
    public Transaction(String category, double amount, String notes, String date, long dateinSeconds) {
        this.category = category;
        this.amount = amount;
        this.notes = notes;
        this.date = date;
        this.dateinSeconds = dateinSeconds;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getNotes() {
        return notes;
    }

    public String getDate() {
        return date;
    }

    public long getDateinSeconds() {
        return dateinSeconds;
    }

}
