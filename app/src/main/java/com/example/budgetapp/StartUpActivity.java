package com.example.budgetapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartUpActivity extends AppCompatActivity {

    TextInputEditText firstNameID, givennameID, phoneID, budgetID, balanceID;
    Button startBTN;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    CircleImageView profileImageView;

    Uri imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_up_page);


        // Initialize the variables
        firstNameID = findViewById(R.id.firstName);
        givennameID = findViewById(R.id.givenName);
        phoneID = findViewById(R.id.phone);
        budgetID = findViewById(R.id.Budget);
        balanceID = findViewById(R.id.Balance);

        startBTN = findViewById(R.id.StartButton);

        profileImageView = findViewById(R.id.avt);

        Bundle bundle = getIntent().getExtras();
        String googleNoti = bundle.getString("google");

        if (googleNoti != null) {

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


            DocumentReference documentRef = FirebaseFirestore.getInstance().collection("UserCollection").document(mAuth.getCurrentUser().getUid());
            documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String budget = task.getResult().getString("budget");
                    String balance = task.getResult().getString("balance");
                    if (budget != null) {
                        Intent intent = new Intent(StartUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        budgetID.setText(budget);
                        balanceID.setText(balance);
                    }
                }
            });

            if (account != null) {
                firstNameID.setText(account.getGivenName());
                givennameID.setText(account.getFamilyName());
                imageURL = account.getPhotoUrl();
                Picasso.get().load(imageURL).into(profileImageView);
            }
        }

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, 11);
            }
        });

        startBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBTN.setEnabled(false);



                String firstName, givenName, phone, budget, balance;
                firstName = String.valueOf(firstNameID.getText());
                givenName = String.valueOf(givennameID.getText());
                budget = String.valueOf(budgetID.getText());
                balance = String.valueOf(balanceID.getText());
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
                if (TextUtils.isEmpty(budget)) {
                    Toast.makeText(StartUpActivity.this, "Please enter your budget", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(balance)) {
                    Toast.makeText(StartUpActivity.this, "Please enter your balance", Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadImage(profileImageView);

                ProgressDialog progressDialog = new ProgressDialog(StartUpActivity.this);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Successful");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode == RESULT_OK && data != null) {
            imageURL = data.getData();
            getImageInImageView();

        }
    }

    private void getImageInImageView() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        profileImageView.setImageBitmap(bitmap);

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

    private void syncToFirestore(String url) {

        Bundle bundle = getIntent().getExtras();
        String google = bundle.getString("google");


        Users user = new Users();
        user.setFirstName(firstNameID.getText().toString());
        user.setLastName(givennameID.getText().toString());
        user.setPhone(phoneID.getText().toString());
        user.setUserID(bundle.getString("id"));
        user.setEmail(bundle.getString("email"));
        user.setPassword(bundle.getString("password"));
        user.setBudget(budgetID.getText().toString());
        user.setBalance(balanceID.getText().toString());
        user.setProfilePic(url);

        if (google != null) {
            user.setUserID(mAuth.getCurrentUser().getUid());
        }

        CollectionReference usersRef = db.collection("UserCollection");
        usersRef.document(user.getUserID()).set(user);

        Intent intent = new Intent(StartUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void uploadImage(CircleImageView profileImageView) {

        Bundle bundle = getIntent().getExtras();
        String googleNoti = bundle.getString("google");

        if (googleNoti != null) {

            Bitmap bitmap = null;
            Drawable drawable = profileImageView.getDrawable();
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            FirebaseStorage.getInstance().getReference("image/" + UUID.randomUUID()).putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    syncToFirestore(task.getResult().toString());
                                } else {
                                    Toast.makeText(StartUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(StartUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            if (imageURL == null) {
                // If no image is selected, upload the default image
                Drawable drawable = getResources().getDrawable(R.drawable.user);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                FirebaseStorage.getInstance().getReference("image/" + UUID.randomUUID()).putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        syncToFirestore(task.getResult().toString());
                                    } else {
                                        Toast.makeText(StartUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(StartUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                // If an image is selected, upload it
                FirebaseStorage.getInstance().getReference("image/" + UUID.randomUUID()).putFile(imageURL).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        syncToFirestore(task.getResult().toString());
                                    } else {
                                        Toast.makeText(StartUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(StartUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }


}
