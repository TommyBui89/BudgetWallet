package com.example.budgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Un comment this to view editing page
        //setContentView(R.layout.withdraw_page);


        //Remember to comment the code below
        setContentView(R.layout.sign_in_page);
        signUpScrChange();
    }



    private void signUpScrChange() {
        TextView textView = findViewById(R.id.SignUpHereButton);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}