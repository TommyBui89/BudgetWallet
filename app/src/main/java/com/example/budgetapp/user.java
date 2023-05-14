package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link user#newInstance} factory method to
 * create an instance of this fragment.
 */
public class user extends Fragment {

    // Declare the sign out button
    private Button btnSignOut;
    private FirebaseAuth mAuth; // Firebase Authentication instance
    // ...

    String url, email, password, firstName, lastName, phone, budget, id, balance;
    TextInputEditText usernamefield, passwordfield, firstNamefield, lastNamefield, budgetfield, balancefield;

    public static user newInstance() {
        user fragment = new user();
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Find the sign out button by its ID
        btnSignOut = view.findViewById(R.id.btnSignOut);
        usernamefield = view.findViewById(R.id.userNameTextbox);
        passwordfield = view.findViewById(R.id.PasswordTextbox);
        firstNamefield = view.findViewById(R.id.FirstNameTextbox);
        lastNamefield = view.findViewById(R.id.LastNameTextbox);
        budgetfield = view.findViewById(R.id.BudgetTextbox);
        balancefield = view.findViewById(R.id.BalanceTextBox);


        // Set a click listener for the sign out button
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the signOut() method to sign out the user
                mAuth.signOut();
                // Optional: You can also navigate to your sign in activity or clear any user data here
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });


        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("url");
            firstName = bundle.getString("firstName");
            lastName = bundle.getString("lastName");
            password = bundle.getString("password");
            email = bundle.getString("email");
            budget = bundle.getString("budget");
            balance = bundle.getString("balance");
            phone = bundle.getString("phone");
            id = bundle.getString("id");
        }

        Log.d("TAG", "onCreateView: " + url + " " + firstName + " " + lastName + " " + password + " " + email + " " + budget + " " + balance + " " + phone + " " + id);

        usernamefield.setText(email);
        passwordfield.setText(password);
        firstNamefield.setText(firstName);
        lastNamefield.setText(lastName);
        budgetfield.setText(budget);
        balancefield.setText(balance);


        return view;
    }
}

