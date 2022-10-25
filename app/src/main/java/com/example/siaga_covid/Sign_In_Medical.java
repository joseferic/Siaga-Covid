package com.example.siaga_covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Sign_In_Medical extends AppCompatActivity {

    EditText namaRumahSakit,emailRumahSakit, kontakRumahSakit, lokasiRumahSakit;
    Button RegisterBtnRumahSakit;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__in__medical);


        namaRumahSakit = findViewById(R.id.nama_RumahSakit);
        emailRumahSakit = findViewById(R.id.email_rumahsakit);
        kontakRumahSakit = findViewById(R.id.kontak_rumahsakit);
        lokasiRumahSakit = findViewById(R.id.lokasi_rumahsakit);
        RegisterBtnRumahSakit = findViewById(R.id.button_daftarRumahsakit);
        progressBar = findViewById(R.id.progressBarMedical);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        RegisterBtnRumahSakit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String RumahSakitName = namaRumahSakit.getText().toString().trim();
                final String RumahSakitLocation = lokasiRumahSakit.getText().toString().trim();
                final String RumahSakitContact = kontakRumahSakit.getText().toString();
                final String RumahSakitEmail = emailRumahSakit.getText().toString();
                kontakRumahSakit.setInputType(InputType.TYPE_CLASS_NUMBER);

                if (TextUtils.isEmpty(RumahSakitName)){
                    namaRumahSakit.setError("Mohon Email Diisi!");
                    return;
                }
                if (TextUtils.isEmpty(RumahSakitLocation)){
                    lokasiRumahSakit.setError("Mohon Lokasi Diisi!");
                    return;
                }
                if (TextUtils.isEmpty(RumahSakitContact)){
                    kontakRumahSakit.setError("Mohon Kontak Diisi");
                    return;
                }
                if (RumahSakitContact.length()>13)
                {
                    kontakRumahSakit.setError("kontak maksimal 12 karakter");
                }
                if (TextUtils.isEmpty(RumahSakitEmail))
                {
                    emailRumahSakit.setError("Mohon Email Diisi");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //Register the place data

                Toast.makeText(Sign_In_Medical.this, "Hospital Created",Toast.LENGTH_SHORT).show();

                DocumentReference documentReference = fStore.collection("hospital").document(RumahSakitName);
                Map<String,Object> rumahSakit = new HashMap<>();
                rumahSakit.put("Nama Rumah Sakit",RumahSakitName);
                rumahSakit.put("Lokasi Rumah Sakit",RumahSakitLocation);
                rumahSakit.put("Kontak Rumah Sakit",RumahSakitContact);
                rumahSakit.put("Email Rumah Sakit",RumahSakitEmail);
                documentReference.set(rumahSakit).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG","hospital sign in by  " + userID);
                        progressBar.setVisibility(View.GONE);
//                        startActivity(new Intent(getApplicationContext(),Medical.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

//                final StorageReference fileRef = storageReference.child("places/"+"QR.jpg");
//
//                QRGEncoder qrgEncoder = new QRGEncoder(placeName, null, QRGContents.Type.TEXT,1000);
//                Bitmap qrBits = qrgEncoder.getBitmap();
//                UploadTask uploadTask;
//                uploadTask = fileRef.putFile(qrBits);


               startActivity(new Intent(getApplicationContext(),Medical.class));
            }
        });


    }
}