package com.example.budgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextInputEditText emailID, passwordID;
    Button loginBTNID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Remember to comment the code below
        setContentView(R.layout.sign_in_page);

        // Initialize the variables
        emailID = findViewById(R.id.email);
        passwordID = findViewById(R.id.password);
        loginBTNID = findViewById(R.id.LoginButton);


        loginBTNID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String email, password;
                email = String.valueOf(emailID.getText());
                password = String.valueOf(passwordID.getText());

                //start Auth
                mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Redirect the user to the main page of your app
                        Toast.makeText(LoginActivity.this, "Welcome.", Toast.LENGTH_SHORT).show();
                        mainScrChange();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication failed. Pls try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }});


    }



    public void signUpScrChange(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void mainScrChange() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}