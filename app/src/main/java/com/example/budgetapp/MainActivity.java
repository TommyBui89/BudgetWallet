package com.example.budgetapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.budgetapp.databinding.FragmentHomeBinding;
import com.example.budgetapp.databinding.HomePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    HomePageBinding binding;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("UserCollection");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomePageBinding.inflate(getLayoutInflater());
        Bundle bundle = new Bundle();
        setContentView(binding.getRoot());

        DocumentReference documentRef = usersRef.document(mAuth.getCurrentUser().getUid());

        Log.d("out", "1");
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        createBundleFromFirestore(document, bundle);
                        Log.d("out", bundle.getString("phone"));
                        replaceFrame(new Home(), bundle);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homeFrag:
                    replaceFrame(new Home(), bundle);
                    return true;
                case R.id.walletFrag:
                    replaceFrame(new wallet(), bundle);
                    return true;
                case R.id.addFrag:
                    replaceFrame(new add(), bundle);
                    return true;
                case R.id.budgetFrag:
                    replaceFrame(new Budget(), bundle);
                    return true;
                case R.id.userFrag:
                    replaceFrame(new user(), bundle);
                    return true;
            }
            return true;
        });

    }

    private void replaceFrame(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle); // Pass the bundle to the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void createBundleFromFirestore(DocumentSnapshot document, Bundle bundle) {
        if (document.exists()) {
            // Access the data from the document object here and update the bundle
            String url = document.getString("profilePic");
            String firstName = document.getString("firstName");
            String lastName = document.getString("lastName");
            String password = document.getString("password");
            String phone = document.getString("phone");
            String email = document.getString("email");

            bundle.putString("url", url);
            bundle.putString("firstName", firstName);
            bundle.putString("lastName", lastName);
            bundle.putString("password", password);
            bundle.putString("phone", phone);
            bundle.putString("email", email);
            Log.d("main", "pass done");
            Log.d("main", url);
        } else {
            Log.d(TAG, "No such document");
        }
    }
}