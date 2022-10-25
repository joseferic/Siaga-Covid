package com.example.siaga_covid;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;

import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import javax.annotation.Nullable;

public class Show_Profile extends AppCompatActivity {
    TextView username, email, password;
    ImageView fotoProfile,logout,passwordIcon, takeQR;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button changeProfileImage,
            revealPassword,
            hidePassword,
            changePassword;


    private StorageReference storageReference;
    private FirebaseStorage storage;

    ImageView testResultPlaceHolder;
    private SlidrInterface slidr;
    Button insertTestResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        //bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //bottom navigation view Set profile selected
        bottomNavigationView.setSelectedItemId(R.id.profile_user_bottom);

        //bottom navigation view selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.scanner_bottom:
                        startActivity(new Intent(getApplicationContext(), scanner.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.place_history_bottom:
                        startActivity(new Intent(getApplicationContext(), history_user.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile_user_bottom:
                        startActivity(new Intent(getApplicationContext(), Show_Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        //coba ambil token setelah login


        //Ganti tipe akun
        final Spinner mySpinner = (Spinner) findViewById(R.id.change_user_type);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Show_Profile.this,
                R.layout.color_spinner_layout, getResources().getStringArray(R.array.list_type_user));
        myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        Configuration conf = getResources().getConfiguration();

        //creating the position of the language in spinner from arraylist
        int currentLanguage = Arrays.asList(R.array.list_type_user).indexOf(
                conf.locale.getLanguage());

        mySpinner.setSelection(currentLanguage);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1 ) {
                  fStore.collection("users").document(userId).collection("places").document(userId)
                           .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                       @Override
                       public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                Intent intent = new Intent(getApplicationContext(),Employee.class);
                                startActivity(intent);
                                mySpinner.setSelection(0);
                            }
                            else {
                                Intent intent = new Intent(getApplicationContext(),Sign_Place.class);
                                startActivity(intent);
                                mySpinner.setSelection(0);
                            }
                       }
                   });
                }
                if(position == 2){
                    fStore.collection("users").document(userId).collection("hospital").document(userId)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                Intent intent = new Intent(Show_Profile.this,Medical.class);
                                startActivity(intent);
                                mySpinner.setSelection(0);
                            }
                            else {
                                Intent intent = new Intent(getApplicationContext(),Sign_In_Medical.class);
                                startActivity(intent);
                                mySpinner.setSelection(0);
                            }
                        }
                    });
                }
            };

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mySpinner.setSelection(0);


        //generate and scan qr
        takeQR = findViewById(R.id.scan_qr);
        takeQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), scanner.class);
                startActivity(intent);
            }
        });

        //Fetch data diri
        username = findViewById(R.id.username_on_profile);
        email = findViewById(R.id.email_on_Profile);
        password = findViewById(R.id.password_on_Profile);
        fotoProfile = findViewById(R.id.photo_profile);
        changeProfileImage = findViewById(R.id.button_edit_fotoProfile);
        logout = findViewById(R.id.option);
        passwordIcon = findViewById(R.id.password_icon);
        revealPassword = findViewById(R.id.Reveal_Password);
        hidePassword = findViewById(R.id.Hide_Password);
        changePassword = findViewById(R.id.change_password);



        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profiref =  storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"Profile.jpg");
        profiref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(fotoProfile);
            }
        });

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                username.setText(documentSnapshot.getString("Username"));
                email.setText(documentSnapshot.getString("Email"));
                password.setText(documentSnapshot.getString("Password"));
                password.setVisibility(View.GONE);
                passwordIcon.setVisibility(View.GONE);
                hidePassword.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
            }
        });
        revealPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealPassword.setVisibility(View.GONE);
                password.setVisibility(View.VISIBLE);
                passwordIcon.setVisibility(View.VISIBLE);
                hidePassword.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.VISIBLE);
            }
        });

        hidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealPassword.setVisibility(View.VISIBLE);
                password.setVisibility(View.GONE);
                passwordIcon.setVisibility(View.GONE);
                hidePassword.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
            }
        });


        //ganti akun
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });


        //ganti foto profile
        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // buka galeri foto
                Intent openGaleryIntent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGaleryIntent,1);
            }
        });



        //show hasil covid
        insertTestResult= findViewById(R.id.insert_test_result);



        testResultPlaceHolder = findViewById(R.id.test_result_placeholder);
        testResultPlaceHolder.setVisibility(View.GONE);
        insertTestResult.setVisibility(View.GONE);

        storageReference.child("usersTestResult/"+fAuth.getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                insertTestResult.setVisibility(View.GONE);
                StorageReference testResult =  storageReference.child("usersTestResult/"+fAuth.getCurrentUser().getUid());
                testResult.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri1) {
                        Picasso.get().load(uri1).into(testResultPlaceHolder);
                    }
                });
                testResultPlaceHolder.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                insertTestResult.setVisibility(View.VISIBLE);
            }
        });

        insertTestResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTestResult.setVisibility(View.GONE);
                Intent openGaleryIntent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGaleryIntent,2);
                StorageReference testResult =  storageReference.child("usersTestResult/"+fAuth.getCurrentUser().getUid());
                testResult.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(testResultPlaceHolder);
                    }
                });
                testResultPlaceHolder.setVisibility(View.VISIBLE);
            }
        });



    }


    //Upload Profile Picture to firebase
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== 1 ){
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //fotoProfile.setImageURI(imageUri);
                uploadImageFirebase(imageUri);
            }
        }
        if (requestCode== 2 ){
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //fotoProfile.setImageURI(imageUri);
                uploadTestFirebase(imageUri);
            }
        }
    }

    private void uploadImageFirebase(Uri imageUri) {
        //upload image
        final StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "Profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Show_Profile.this, "Image Upload.", Toast.LENGTH_LONG).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(fotoProfile);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Show_Profile.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

        //Upload Test result to firebase
        private void uploadTestFirebase(Uri imageUriTest) {
        //upload image
        final StorageReference testResult = storageReference.child("usersTestResult/"+fAuth.getCurrentUser().getUid());
            testResult.putFile(imageUriTest).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Show_Profile.this,"Image Upload.", Toast.LENGTH_LONG).show();
                testResult.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(testResultPlaceHolder);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Show_Profile.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}




