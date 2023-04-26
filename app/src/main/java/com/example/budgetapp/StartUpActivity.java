package com.example.budgetapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.canhub.cropper.CropImage;
import com.example.budgetapp.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartUpActivity extends AppCompatActivity {

    TextInputEditText firstNameID, givennameID, phoneID;
    Button startBTN;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    CircleImageView profileImageView;

    Uri imageURL;


    FirebaseStorage storage = FirebaseStorage.getInstance();
    // Create a storage reference from our app


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_up_page);


        // Initialize the variables
        firstNameID = findViewById(R.id.firstName);
        givennameID = findViewById(R.id.givenName);
        phoneID = findViewById(R.id.phone);

        startBTN = findViewById(R.id.StartButton);

        profileImageView = findViewById(R.id.avt);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,11);
            }
        });

        startBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String firstName, givenName, phone;
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

                syncToFirestore();
                finishSetup();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==11 && resultCode == RESULT_OK && data !=null){
            imageURL=data.getData();
            getImageInImageView();
            
        }
    }

    private void getImageInImageView() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageURL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        profileImageView.setImageBitmap(bitmap);

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
    private void updateProfilePicURL(String url){

        Users user = new Users();
    }
    private void syncToFirestore() {

        Users user = new Users();
        user.setFirstName(firstNameID.getText().toString());
        user.setLastName(givennameID.getText().toString());
        user.setPhone(phoneID.getText().toString());

        CollectionReference usersRef =db.collection("UserCollection");
        usersRef.document(user.getUserID()).set(user);
    }


}