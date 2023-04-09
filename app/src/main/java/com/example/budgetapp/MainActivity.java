package com.example.budgetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.budgetapp.databinding.FragmentHomeBinding;
import com.example.budgetapp.databinding.HomePageBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    HomePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFrame(new Home());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homeFrag:
                    replaceFrame(new Home());
                    return true;
                case R.id.walletFrag:
                    replaceFrame(new wallet());
                    return true;
                case R.id.addFrag:
                    replaceFrame(new add());
                    return true;
                case R.id.budgetFrag:
                    replaceFrame(new Budget());
                    return true;
                case R.id.userFrag:
                    replaceFrame(new user());
                    return true;

            }
            return true;
        });

    }

    private void replaceFrame(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
