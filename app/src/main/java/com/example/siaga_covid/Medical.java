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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Medical extends AppCompatActivity {
    TextView namaRumahSakit, emailRumahSakit, kontakRumahSakit, lokasiRumahSakit;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    String placeName;

    ImageView generateQRhospital,fotoProfile;
    Button changeProfileImage;

    public static final String EXTRA_TEXT = "com.example.application.siaga_covid.EXTRA_TEXT";
    public static final String EXTRA_NUMBER = "com.example.application.siaga_covid.EXTRA_NUMBER";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference();

        namaRumahSakit= findViewById(R.id.namaRumahSakit);
        emailRumahSakit = findViewById(R.id.emailRumahSakit);
        kontakRumahSakit = findViewById(R.id.kontakRumahSakit);
        lokasiRumahSakit = findViewById(R.id.lokasiRumahSakit);


        DocumentReference documentReference = fStore.collection("users").document(userId).collection("hospital").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                namaRumahSakit.setText(documentSnapshot.getString("Nama Rumah Sakit"));
                lokasiRumahSakit.setText(documentSnapshot.getString("Lokasi Rumah Sakit"));
                kontakRumahSakit.setText(documentSnapshot.getString("Kontak Rumah Sakit"));
                emailRumahSakit.setText(documentSnapshot.getString("Email Rumah Sakit"));
                placeName = (documentSnapshot.getString("Nama Rumah Sakit"));
            }
        });


        //nav bottom
        //bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationMedical);

        //bottom navigation view Set profile selected
        bottomNavigationView.setSelectedItemId(R.id.place_profile_bottom);

        //bottom navigation view selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.user_list:
                        Intent intent = new Intent(getApplicationContext(), HistoryUserRumahSakit.class);
                        intent.putExtra(EXTRA_TEXT, placeName);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.place_profile_bottom:
                        startActivity(new Intent(getApplicationContext(), Medical.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        final Spinner mySpinner = (Spinner) findViewById(R.id.change_user_type2);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Medical.this,
                R.layout.color_spinner_layout, getResources().getStringArray(R.array.list_type_user2));
        myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Intent intent = new Intent(Medical.this, Show_Profile.class);
                    startActivity(intent);
                }
                if (position == 2){
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mySpinner.setSelection(0);
        
        generateQRhospital = findViewById(R.id.scan_qrHospital);
        
        generateQRhospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), generateQRHospital.class);
                startActivity(intent);
            }
        });

/*        //menampilkan tanggal
        tanggal1 = findViewById(R.id.coba_tanggal);
//        tanggal2 = findViewById(R.id.waktutempat2);
        calendar = Calendar.getInstance();*/

//        date = new SimpleDateFormat("dd-MMM-yyyy").

//        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss");
//        String date = dateFormat.format(Calendar.getInstance().getTime());
//        tanggal1.setText((CharSequence) date);

        changeProfileImage = findViewById(R.id.button_edit_fotoProfile);
        fotoProfile = findViewById(R.id.photo_profile);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profiref =  storageReference.child("places/"+placeName);
        profiref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(fotoProfile);
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
                Toast.makeText(Medical.this,"Image Upload.", Toast.LENGTH_LONG).show();
                fileRefPlace.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(fotoProfile);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Medical.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}