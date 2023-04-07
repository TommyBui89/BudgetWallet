package com.example.budgetapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText usernameID, emailID, passwordID, confirmPasswordID;
    String userDBID;
    Button signUpBTN;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);



        // Initialize the variables
        usernameID = findViewById(R.id.username);
        emailID = findViewById(R.id.email);
        passwordID = findViewById(R.id.password);
        confirmPasswordID = findViewById(R.id.confirmPassword);
        signUpBTN = findViewById(R.id.SignUpButton);

        signUpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, email, password, confirmPassword;
                username = String.valueOf(usernameID.getText());
                email = String.valueOf(emailID.getText());
                password = String.valueOf(passwordID.getText());
                confirmPassword = String.valueOf(confirmPasswordID.getText());

                signInScrChange();

                if(TextUtils.isEmpty(username)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign Up success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    userDBID=user.getUid();
                                    syncToFirestore(userDBID);

                                    Toast.makeText(SignUpActivity.this, "Successful",Toast.LENGTH_SHORT).show();
                                    setUpScrChange();
                                } else {
                                    // If sign Up fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }



        });
    }

    private void signInScrChange() {
        TextView textView = findViewById(R.id.SignIn);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setUpScrChange() {
        Intent intent = new Intent(SignUpActivity.this, StartUpActivity.class);
        startActivity(intent);
        finish();
    }
    

    /*  Schema
    
userId (document)
    Email: String
    FirstName: String
    GivenName: String
    Password: String
    Phone: String

* */
    private void syncToFirestore(String userDBID) {
    Map<String, Object> User = new HashMap<>();
    User.put("email", emailID.getText().toString());
    User.put("password", passwordID.getText().toString());


    CollectionReference usersRef =db.collection("UserCollection");
    usersRef.document(userDBID).set(User);

    }
}


