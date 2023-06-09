package com.example.budgetapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class wallet extends Fragment {

    String url, firstName, lastName, email, phone, password, id, budget;
    Button nextMonthBTN, previousMonthBTN;
    TextView monthTextView, displayTV, shoBudgetTextView, availableBudgetTextView;

    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("MM-yyyy");
    String formattedDate = formatter.format(date);
    int currentMonth = Integer.parseInt(formattedDate.substring(0, 2));
    int currentYear = Integer.parseInt(formattedDate.substring(3, 7));

    int queryMonth = currentMonth;
    int queryYear = currentYear;
    String queryMonthString;

    float totalExpense = 0;


    private FirebaseFirestore db;
    private TransactionAdapter adapter;


    public wallet() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("url");
            firstName = bundle.getString("firstName");
            lastName = bundle.getString("lastName");
            password = bundle.getString("password");
            phone = bundle.getString("phone");
            email = bundle.getString("email");
            id = bundle.getString("id");
            budget = bundle.getString("budget");
        }

        // Initialize
        shoBudgetTextView = view.findViewById(R.id.ShowBudget);
        availableBudgetTextView = view.findViewById(R.id.AvailableBudget);
        monthTextView = view.findViewById(R.id.CurrentMonth);
        previousMonthBTN = view.findViewById(R.id.PreviousMonth);
        nextMonthBTN = view.findViewById(R.id.NextMonth);
        displayTV = view.findViewById(R.id.DisplayStatus);

        queryMonthString = queryMonth + "-" + queryYear;

        monthTextView.setText(queryMonthString);

        RecyclerView recyclerView = view.findViewById(R.id.history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        updateHistory(recyclerView, queryMonthString, displayTV);


        previousMonthBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthBTN.setEnabled(true);
                if (queryMonth == 0) {
                    queryMonth = 12;
                    queryYear--;
                } else {
                    queryMonth--;
                }
                String queryMonthString = queryMonth + "-" + queryYear;
                monthTextView.setText(queryMonthString);
                updateHistory(recyclerView, queryMonthString, displayTV);
            }
        });

        nextMonthBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (queryMonth == 12) {
                    queryMonth = 1;
                    queryYear++;
                } else {
                    queryMonth++;
                }

                if (currentMonth == queryMonth && currentYear == queryYear) {
                    nextMonthBTN.setEnabled(false);
                }
                String queryMonthString = queryMonth + "-" + queryYear;
                monthTextView.setText(queryMonthString);
                updateHistory(recyclerView, queryMonthString, displayTV);
            }
        });

        return view;
    }

    void updateHistory(RecyclerView recyclerView, String documentName, TextView displayTV) {

        DocumentReference transactionsCollectionRef = db.collection("transactions").document(id);
        // Query the collection and update the adapter
        Query transactionsQuery = transactionsCollectionRef.collection(documentName).orderBy("dateinSeconds", Query.Direction.DESCENDING);
        adapter = new TransactionAdapter(transactionsQuery, displayTV);
        recyclerView.setAdapter(adapter);

        getTotal(documentName, new TotalCallback() {
            @Override
            public void onTotal(float[] values) {
                float total = values[0];
                float income = values[1];
                totalExpense = total;
                shoBudgetTextView.setText(String.valueOf(Float.parseFloat(budget) + income));
                availableBudgetTextView.setText(String.valueOf(Float.parseFloat(budget)+ income + totalExpense)); // Expense stored in DB is negative
            }
        });
    }

    public void getTotal(String documentName, TotalCallback callback) {
        DocumentReference transactionsCollectionRef = db.collection("transactions").document(id);
        // Query the collection
        Query transactionsQuery = transactionsCollectionRef.collection(documentName);
        transactionsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore error", error.getMessage());
                    callback.onTotal(new float[]{0, 0}); // Notify the callback with the default value in case of an error
                    return;
                }
                float total = 0;
                float income = 0;
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Log.d("query", "onEvent: "+doc.get("category").toString());
                    if (doc.get("category").toString().equals("Income")) {
                        Log.d("query", "onEvent: "+doc.get("category").toString());
                        income += Float.parseFloat(doc.get("amount").toString());
                    } else if (doc.get("amount") != null) {
                        total += Float.parseFloat(doc.get("amount").toString());
                    }
                }
                callback.onTotal(new float[]{total, income});
            }
        });
    }

    public interface TotalCallback {
        void onTotal(float[] values);
    }


}
