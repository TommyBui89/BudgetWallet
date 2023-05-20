package com.example.budgetapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    boolean isPassVisible = false;
    boolean isConfirmPassVisible = false;

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


        passwordID.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getRawX() >= (passwordID.getRight() - passwordID.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())){
                        if(isPassVisible){
                            //change the eye
                            passwordID.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_slash_solid, 0);
                            //hide the password
                            passwordID.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            isPassVisible = false;
                        }else{
                            //change the eye
                            passwordID.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_solid, 0);
                            //show the password
                            passwordID.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            isPassVisible = true;
                        }
                    }
                }
                return false;
            }
        });
        confirmPasswordID.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getRawX() >= (confirmPasswordID.getRight() - confirmPasswordID.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())){
                        if(isConfirmPassVisible){
                            //change the eye
                            confirmPasswordID.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_slash_solid, 0);
                            //hide the password
                            confirmPasswordID.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            isConfirmPassVisible = false;
                        }else{
                            //change the eye
                            confirmPasswordID.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_solid, 0);
                            //show the password
                            confirmPasswordID.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            isConfirmPassVisible = true;
                        }
                    }
                }
                return false;
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
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
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
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }
}
