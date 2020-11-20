package com.example.siaga_covid;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import javax.annotation.Nullable;

public class Show_Profile extends AppCompatActivity {
    TextView username, email, password;
    ImageView fotoProfile,logout,passwordIcon;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button changeProfileImage,
            revealPassword,
            hidePassword,
            changePassword;
    private StorageReference storageReference;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        //Ganti tipe akun
        Spinner mySpinner = (Spinner) findViewById(R.id.change_user_type);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Show_Profile.this,
                R.layout.color_spinner_layout, getResources().getStringArray(R.array.list_type_user));
        myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        //Warna tab diprofile
        ImageButton recentPlace_tab = (ImageButton) this.findViewById(R.id.recent_place);
        ImageButton covidTestResult_tab = (ImageButton) this.findViewById(R.id.covid_test_result);
//        recentPlace_tab.setBackgroundColor(Color.parseColor("#EBECEE"));

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
    }

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
    }

    private void uploadImageFirebase(Uri imageUri) {
        //upload image
        final StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"Profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Show_Profile.this,"Image Upload.", Toast.LENGTH_LONG).show();
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
}



