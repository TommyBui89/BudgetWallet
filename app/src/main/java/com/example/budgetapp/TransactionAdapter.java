package com.example.budgetapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetapp.Model.Transaction;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;
    private Query mQuery;

    public TransactionAdapter(Query query,TextView displayTV) {
        mQuery = query;
        transactions = new ArrayList<>();

        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    displayTV.setText("No transactions found");
                    return;
                }

                // Handle success case
                if (!queryDocumentSnapshots.isEmpty()) {
                    // Query was successful and documents exist
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Transaction transaction = documentSnapshot.toObject(Transaction.class);
                        displayTV.setText("");
                        transactions.add(transaction);
                    }
                    notifyDataSetChanged();
                } else {
                    displayTV.setText("No transactions found");

                }
            }
        });

    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_card_view, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.categoryTextView.setText(transaction.getCategory());
        holder.amountTextView.setText("$ "+String.valueOf(transaction.getAmount()));
        holder.dateTextView.setText(transaction.getDate());
        holder.noteTextView.setText(transaction.getNotes());

        switch (transaction.getCategory()) {
            case "Income":
                holder.itemView.setBackgroundResource(R.color.incomeColor);
                break;
            case "Food":
                holder.itemView.setBackgroundResource(R.color.foodColor);
                break;
            case "Entertainment":
                holder.itemView.setBackgroundResource(R.color.entertainmentColor);
                break;
            case "Shopping":
                holder.itemView.setBackgroundResource(R.color.shoppingColor);
                break;
            case "Rent":
                holder.itemView.setBackgroundResource(R.color.rentColor);
                break;
            case "Other":
                holder.itemView.setBackgroundResource(R.color.expenseColor);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView categoryTextView, amountTextView, dateTextView, noteTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.Category);
            dateTextView = itemView.findViewById(R.id.Date);
            noteTextView = itemView.findViewById(R.id.Notes);
            amountTextView = itemView.findViewById(R.id.Amount);
        }
    }
}
