package com.example.budgetapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.budgetapp.databinding.HomePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private static final int TIME_DELAY = 1000;
    private static long back_pressed;

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
                        replaceFrame(new Home(), bundle);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error getting documents", Toast.LENGTH_SHORT).show();
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
            String id = document.getString("userID");
            String budget = document.getString("budget");

            bundle.putString("url", url);
            bundle.putString("firstName", firstName);
            bundle.putString("lastName", lastName);
            bundle.putString("password", password);
            bundle.putString("phone", phone);
            bundle.putString("email", email);
            bundle.putString("id", id);
            bundle.putString("budget", budget);
        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

}
