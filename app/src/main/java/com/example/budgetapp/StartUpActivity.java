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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StartUpActivity extends AppCompatActivity {

    TextInputEditText firstNameID, givennameID, phoneID;
    Button startBTN;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_up_page);


        // Initialize the variables
        firstNameID = findViewById(R.id.firstName);
        givennameID = findViewById(R.id.givenName);
        phoneID = findViewById(R.id.phone);

        startBTN = findViewById(R.id.StartButton);

        startBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userDBID, firstName, givenName, phone;
                firstName = String.valueOf(firstNameID.getText());
                givenName = String.valueOf(givennameID.getText());
                phone = String.valueOf(phoneID.getText());


                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(StartUpActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(givenName)) {
                    Toast.makeText(StartUpActivity.this, "Please enter your given name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(StartUpActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }


                userDBID=mAuth.getInstance().getCurrentUser().getUid();
                syncToFirestore(userDBID);
                finishSetup();
            }
        });

    }

    private void finishSetup() {
        Intent intent = new Intent(StartUpActivity.this, MainActivity.class);
        startActivity(intent);

    }

    /*
     * Schema
     * 
     * userId (document)
     * Email: String
     * FirstName: String
     * GivenName: String
     * Password: String
     * Phone: String
     * 
     */
    private void syncToFirestore(String userDBID) {
        Map<String, Object> User = new HashMap<>();
        User.put("FirstName", firstNameID.getText().toString());
        User.put("GivenName", givennameID.getText().toString());
        User.put("Phone", phoneID.getText().toString());


        CollectionReference usersRef =db.collection("UserCollection");
        usersRef.document(userDBID).update(User);
    }

}