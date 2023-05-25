package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextInputEditText emailID, passwordID;
    Button loginBTNID,googleBTN;
    boolean isPassVisible = false;

    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sign_in_page);

        // Initialize the variables
        emailID = findViewById(R.id.email);
        passwordID = findViewById(R.id.password);
        loginBTNID = findViewById(R.id.LoginButton);
        googleBTN = findViewById(R.id.GoogleButton);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        //lister to eye icon click
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

        googleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUsingGoogle();
            }
        });
        loginBTNID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String email, password;
                email = String.valueOf(emailID.getText());
                password = String.valueOf(passwordID.getText());
                //validate input
                if(email.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                        Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    int RC_SIGN_IN = 123;
    private void signInUsingGoogle() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                fireBaseAuth(account.getIdToken());
            }catch(ApiException e){}
        }



    }

    private void fireBaseAuth(String idToken) {
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user=mAuth.getCurrentUser();
                    Bundle googleNoti = new Bundle();

                    Intent intent = new Intent(LoginActivity.this, StartUpActivity.class);


                    googleNoti.putString("google", "true");
                    intent.putExtras(googleNoti);
                    startActivity(intent);

                }
            }
        });
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
