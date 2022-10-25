package com.example.siaga_covid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Employee extends AppCompatActivity {
    ImageView fotoProfileTempat,showqr;
    TextView namaTempat, lokasiTempat, kontakTempat, deskripsiTempat,gantiTempat;
    Button changePlaceProfileImage;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    String placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        fotoProfileTempat = findViewById(R.id.photo_profileTempat);
        namaTempat = findViewById(R.id.place_name);
        lokasiTempat = findViewById(R.id.place_location);
        kontakTempat = findViewById(R.id.place_contact);
        deskripsiTempat = findViewById(R.id.place_description);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference();

        showqr = findViewById(R.id.show_qr);

        showqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), generateQR.class);
                startActivity(i);
            }
        });

        //nav bottom
        //bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationPlace);

        //bottom navigation view Set profile selected
        bottomNavigationView.setSelectedItemId(R.id.place_profile_bottom);

        //bottom navigation view selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.user_history_bottom:
                        startActivity(new Intent(getApplicationContext(), HistoryUserBerkunjungTempat.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.place_profile_bottom:
                        startActivity(new Intent(getApplicationContext(), Employee.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });





        DocumentReference documentReference = fStore.collection("users").document(userId).collection("places").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                namaTempat.setText(documentSnapshot.getString("Nama Tempat"));
                lokasiTempat.setText(documentSnapshot.getString("Lokasi Tempat"));
                kontakTempat.setText(documentSnapshot.getString("Kontak Tempat"));
                deskripsiTempat.setText(documentSnapshot.getString("Deskripsi Tempat"));
                placeName = (documentSnapshot.getString("Nama Tempat"));
            }
        });

        StorageReference fileRefPlace = storageReference.child("places/"+placeName);
        fileRefPlace.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(fotoProfileTempat);
            }
        });


        final Spinner mySpinner = (Spinner) findViewById(R.id.change_user_type1);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Employee.this,
                R.layout.color_spinner_layout, getResources().getStringArray(R.array.list_type_user1));
        myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Intent intent = new Intent(Employee.this, Medical.class);
                    startActivity(intent);
                    mySpinner.setSelection(0);
                }
                if (position == 2){
                    Intent intent = new Intent(Employee.this, Show_Profile.class);
                    startActivity(intent);
                    mySpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mySpinner.setSelection(0);

        gantiTempat = findViewById(R.id.keDaftarkanTempatScreen);
        gantiTempat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Employee.this, Sign_Place.class));
            }
        });

        //ganti foro profile tempat
        changePlaceProfileImage = (Button) findViewById(R.id.button_edit_fotoProfile);
        changePlaceProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // buka galeri foto
                Intent openGaleryIntent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGaleryIntent,1);
                StorageReference fileRefPlace =  storageReference.child("places/"+placeName);
                fileRefPlace.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(fotoProfileTempat);
                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 ){
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //fotoProfile.setImageURI(imageUri);
                uploadImageFirebase(imageUri);
            }
        }
    }

    private void uploadImageFirebase(Uri imageUri) {
        //upload image
        final StorageReference fileRefPlace = storageReference.child("places/"+placeName);
        fileRefPlace.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Employee.this,"Image Upload.", Toast.LENGTH_LONG).show();
                fileRefPlace.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(fotoProfileTempat);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Employee.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}