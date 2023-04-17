package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Find the sign out button by its ID
        btnSignOut = view.findViewById(R.id.btnSignOut);

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

        return view;
    }
}