package com.example.budgetapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.budgetapp.Model.Users;
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

    TextInputEditText emailID, passwordID, confirmPasswordID;
    String userDBID;
    Button signUpBTN;

    ToggleButton passwordToggleButton;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        // Initialize the variables
        emailID = findViewById(R.id.email);
        passwordID = findViewById(R.id.password);
        confirmPasswordID = findViewById(R.id.confirmPassword);
        signUpBTN = findViewById(R.id.SignUpButton);
        passwordToggleButton = (ToggleButton) findViewById(R.id.hideBtn);

        //toggle hide password onClick
        passwordToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                int inputType = isChecked ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                passwordID.setInputType(inputType);
                confirmPasswordID.setInputType(inputType);

                // Update the button icon
                int drawableId = isChecked ? R.drawable.ic_hide_password : R.drawable.ic_show_password;
                passwordToggleButton.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
            }
        });

        signUpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, confirmPassword;
                email = String.valueOf(emailID.getText());
                password = String.valueOf(passwordID.getText());
                confirmPassword = String.valueOf(confirmPasswordID.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
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
                                    userDBID = user.getUid();

                                    Toast.makeText(SignUpActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                    setUpScrChange();
                                } else {
                                    // If sign Up fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    public void signInScrChange(View view) {
        finish();
    }

    private void setUpScrChange() {
        Intent intent = new Intent(SignUpActivity.this, StartUpActivity.class);
        Bundle user = new Bundle();
        user.putString("id", mAuth.getCurrentUser().getUid());
        user.putString("email", emailID.getText().toString());
        user.putString("password", passwordID.getText().toString());
        intent.putExtras(user);
        startActivity(intent);
        finish();
    }
}
