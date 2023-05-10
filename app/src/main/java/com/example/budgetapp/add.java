package com.example.budgetapp;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetapp.Model.Transaction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class add extends Fragment {
    String url, firstName, lastName, email, phone, password, id;
    TextInputEditText amount, notes, date;
    Button submitBTN;


    //drop down list
    List<String> item = new ArrayList<>();
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference transactionsRef = db.collection("category");


    public add() {
        // Required empty public constructor
    }

    public static add newInstance(String param1, String param2) {
        add fragment = new add();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("url");
            firstName = bundle.getString("firstName");
            lastName = bundle.getString("lastName");
            password = bundle.getString("password");
            phone = bundle.getString("phone");
            email = bundle.getString("email");
            id = bundle.getString("id");
        }


        amount = view.findViewById(R.id.AmountTextbox);
        notes = view.findViewById(R.id.NotesTextbox);
        date = view.findViewById(R.id.TransactionDateTextbox);
        submitBTN = view.findViewById(R.id.SubmitButton);


        populateCategoriesList();

        //drop down list
        autoCompleteTextView = view.findViewById(R.id.auto_complete_textview);
        adapterItems = new ArrayAdapter<String>(requireContext(), R.layout.list_item, item);
        autoCompleteTextView.setAdapter(adapterItems);

        //set default date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        date.setText(currentDate);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getContext(), "item " + item, Toast.LENGTH_SHORT).show();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the current date as default
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a new DatePickerDialog instance and show it
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Update the date text view with the selected date
                        calendar.set(year, month, day);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                        String selectedDate = dateFormat.format(calendar.getTime());
                        date.setText(selectedDate);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category = autoCompleteTextView.getText().toString();
                String amountStr = amount.getText().toString().trim();
                String notesStr = notes.getText().toString().trim();
                String dateStr = date.getText().toString().trim();

                if (category.isEmpty() || amountStr.isEmpty() || dateStr.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                double amount = Double.parseDouble(amountStr);

                // Create a new transaction object

                Transaction transaction = new Transaction(category, amount, notesStr, dateStr);

                // Add the transaction to Firestore
                db.collection("transactions").document(id).collection("userTransactions").add(transaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Transaction added successfully.", Toast.LENGTH_SHORT).show();
                        clearInputs();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to add transaction.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document", e);
                    }
                });
                // Get the current focused view
                View focus = requireActivity().getCurrentFocus();
                if (focus != null) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        return view;
    }

    private void populateCategoriesList() {
        transactionsRef.whereEqualTo("userId", id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> categories = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String category = document.getString("category");
                    if (category != null && !categories.contains(category)) {
                        categories.add(category);
                    }
                }
                if (categories.isEmpty()) {
                    // Add default categories
                    categories.add("Food");
                    categories.add("Entertainment");
                    categories.add("Shopping");
                    categories.add("Rent");
                    categories.add("Other");

                    Map<String, Object> defaultData = new HashMap<>();
                    defaultData.put("userID", id);
                    defaultData.put("category", categories);

                    db.collection("category").document(id).set(defaultData);
                }
                item.addAll(categories);
                adapterItems.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Warning", "Error getting documents.", e);
            }
        });
    }


    private void clearInputs() {
        autoCompleteTextView.setText("");
        amount.setText("");
        notes.setText("");
    }

}
