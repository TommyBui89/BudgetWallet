package com.example.budgetapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.budgetapp.Model.Transaction;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link wallet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class wallet extends Fragment {

    String url, firstName, lastName, email, phone, password, id, balance;
    Button nextMonthBTN, previousMonthBTN;
    TextView monthTextView,displayTV,shoBalanceTextView,availableBalanceTextView;

    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("MM-yyyy");
    String formattedDate = formatter.format(date);
    int currentMonth = Integer.parseInt(formattedDate.substring(0,2));
    int currentYear = Integer.parseInt(formattedDate.substring(3,7));

    int queryMonth = currentMonth;
    int queryYear = currentYear;
    String queryMonthString;

    float totalExpense = 0;


    private FirebaseFirestore db;
    private List<Transaction> transactionList;
    private TransactionAdapter adapter;



    public wallet() {
        // Required empty public constructor

    }

    public static wallet newInstance() {
        wallet fragment = new wallet();
        return fragment;
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
            balance = bundle.getString("balance");
        }

        // Initialize
        shoBalanceTextView = view.findViewById(R.id.ShoBalance);
        availableBalanceTextView = view.findViewById(R.id.textView9);
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
                if (currentMonth > queryMonth && currentYear >= queryYear) {
                    nextMonthBTN.setEnabled(true);
                }
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
        Query transactionsQuery = db.collection("transactions").document(id).collection(documentName).orderBy("dateinSeconds", Query.Direction.DESCENDING);
        adapter = new TransactionAdapter(transactionsQuery, displayTV);
        recyclerView.setAdapter(adapter);

        getTotal(documentName, new Budget.TotalCallback() {
            @Override
            public void onTotal(float total) {
                totalExpense = total;
                shoBalanceTextView.setText(String.valueOf(balance));
                availableBalanceTextView.setText(String.valueOf(Float.parseFloat(balance)-totalExpense));
            }
        });
    }

    public void getTotal(String documentName, Budget.TotalCallback callback) {
        DocumentReference transactionsCollectionRef = db.collection("transactions").document(id);
        // Query the collection
        Query transactionsQuery = transactionsCollectionRef.collection(documentName);
        transactionsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore error", error.getMessage());
                    callback.onTotal(0); // Notify the callback with the default value in case of an error
                    return;
                }
                float total = 0;
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    if (doc.get("amount") != null) {
                        total += Float.parseFloat(doc.get("amount").toString());
                    }
                }
                callback.onTotal(total); // Notify the callback with the calculated total value
            }
        });
    }

    public interface TotalCallback {
        void onTotal(float total);
    }




}
