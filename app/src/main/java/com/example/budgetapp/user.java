package com.example.budgetapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.budgetapp.Model.Users;
import com.firebase.ui.auth.data.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link user#newInstance} factory method to
 * create an instance of this fragment.
 */
public class user extends Fragment {


    //Declare Variables
    private Button btnSignOut, changeButton;
    private FirebaseAuth mAuth;

    String url, email, password, firstName, lastName, phone, budget, id;
    TextInputEditText usernamefield, passwordfield, firstNamefield, lastNamefield, budgetfield;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        changeButton = view.findViewById(R.id.ChangeBtn);


        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("url");
            firstName = bundle.getString("firstName");
            lastName = bundle.getString("lastName");
            password = bundle.getString("password");
            email = bundle.getString("email");
            budget = bundle.getString("budget");
            phone = bundle.getString("phone");
            id = bundle.getString("id");
            url = bundle.getString("url");
        }

        if (email == null) {
            email = "Please Use Google To Sign In";
            password = "Please Use Google To Sign In";
            //disable text fields
            usernamefield.setEnabled(false);
            passwordfield.setEnabled(false);

        }

        usernamefield.setText(email);
        passwordfield.setText(password);
        firstNamefield.setText(firstName);
        lastNamefield.setText(lastName);
        budgetfield.setText(budget);


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


        //Set a click listener for the change button
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the text from the text fields
                String newEmail = usernamefield.getText().toString();
                String newPassword = passwordfield.getText().toString();
                String newFirstName = firstNamefield.getText().toString();
                String newLastName = lastNamefield.getText().toString();
                String newBudget = budgetfield.getText().toString();

                //create a new user object
                Users user = new Users();
                user.setEmail(newEmail);
                user.setPassword(newPassword);
                user.setFirstName(newFirstName);
                user.setLastName(newLastName);
                user.setBudget(newBudget);
                user.setPhone(phone);
                user.setProfilePic(url);
                user.setUserID(id);


                //update the user in the database
                CollectionReference usersRef = db.collection("UserCollection");
                usersRef.document(id).set(user);

                Toast.makeText(getActivity(), "Change Successful", Toast.LENGTH_SHORT).show();
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


                //update bundle to share with fragments
                bundle.putString("url", url);
                bundle.putString("firstName", newFirstName);
                bundle.putString("lastName", newLastName);
                bundle.putString("password", newPassword);
                bundle.putString("phone", phone);
                bundle.putString("email", newEmail);
                bundle.putString("id", id);
                bundle.putString("budget", newBudget);

            }
        });

        return view;
    }

}

