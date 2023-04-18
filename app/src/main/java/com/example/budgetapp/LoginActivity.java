package com.example.budgetapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextInputEditText emailID, passwordID;
    Button loginBTNID,googleBTN,fbBTN;

    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CallbackManager callbackManager = CallbackManager.Factory.create();


        //Remember to comment the code below
        setContentView(R.layout.sign_in_page);

        // Initialize the variables
        emailID = findViewById(R.id.email);
        passwordID = findViewById(R.id.password);
        loginBTNID = findViewById(R.id.LoginButton);
        googleBTN = findViewById(R.id.GoogleButton);
        fbBTN= findViewById(R.id.FacebookButton);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

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
            }
        });


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        Toast.makeText(LoginActivity.this, "FB Good. Pls try again", Toast.LENGTH_SHORT).show();


                        mainScrChange();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, "Authentication failed. Pls try again", Toast.LENGTH_SHORT).show();

                    }
                });
        fbBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
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

                    mainScrChange();
                }
            }
        });
    }
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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