package com.example.siaga_covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Sign_Place extends AppCompatActivity {
    EditText namaTempat,lokasiTempat, kontakTempat,deskripsiTempat;
    TextView gabungTempat;
    Button daftarkanTempat;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__place);

        daftarkanTempat =(Button)findViewById(R.id.button_daftar);

        namaTempat = findViewById(R.id.Nama_Tempat);
        lokasiTempat = findViewById(R.id.Lokasi_Tempat);
        kontakTempat =  findViewById(R.id.Contact);
        deskripsiTempat = findViewById(R.id.Place_Description);
        progressBar = findViewById(R.id.progress_Bar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        daftarkanTempat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String placeName= namaTempat.getText().toString().trim();
                final String placeLocation = lokasiTempat.getText().toString().trim();
                final String placeContact = kontakTempat.getText().toString();
                final String placeDescription = deskripsiTempat.getText().toString();
                kontakTempat.setInputType(InputType.TYPE_CLASS_NUMBER);

                if (TextUtils.isEmpty(placeName)){
                    namaTempat.setError("Mohon Email Diisi!");
                    return;
                }
                if (TextUtils.isEmpty(placeLocation)){
                    lokasiTempat.setError("Mohon Lokasi Diisi!");
                    return;
                }
                if (TextUtils.isEmpty(placeContact)){
                    kontakTempat.setError("Mohon Kontak Diisi");
                    return;
                }
                if (placeContact.length()>13)
                {
                    kontakTempat.setError("kontakmaksimal 12 karakter");
                }
                if (TextUtils.isEmpty(placeDescription))
                {
                    deskripsiTempat.setError("Mohon Deskripsi Diisi");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //Register the place data

                Toast.makeText(Sign_Place.this, "Place Created",Toast.LENGTH_SHORT).show();
                userID = fAuth.getCurrentUser().getUid();

                DocumentReference documentReference = fStore.collection("users").document(userID).collection("places").document(userID);
                Map<String,Object> userPlace = new HashMap<>();
                userPlace.put("Nama Tempat",placeName);
                userPlace.put("Lokasi Tempat",placeLocation);
                userPlace.put("Kontak Tempat",placeContact);
                userPlace.put("Deskripsi Tempat",placeDescription);
                documentReference.set(userPlace).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG","onSuccess: place created for " + userID);
                        progressBar.setVisibility(View.GONE);
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


                startActivity(new Intent(getApplicationContext(),Employee.class));
            }
        });


//        gabungTempat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),Employee.class));
//            }
//        });

    }
}