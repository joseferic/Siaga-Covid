package com.example.siaga_covid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class generateQRHospital extends AppCompatActivity {

    Button generateBtn, scanBtn;
    ImageView qrImage;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    StorageReference storageReference;
    private FirebaseStorage storage;
    String placeName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_q_rhospital);


        generateBtn = findViewById(R.id.generateQRValueButton);
//        scanBtn = findViewById(R.id.ScanQRButton);
        qrImage = findViewById(R.id.ImageQrDefault);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference();


        DocumentReference documentReference = fStore.collection("users").document(userId).collection("hospital").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                placeName = (documentSnapshot.getString("Nama Rumah Sakit"));
            }
        });

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRGEncoder qrgEncoder = new QRGEncoder(placeName, null, QRGContents.Type.TEXT,1000);
                Bitmap qrBits = qrgEncoder.getBitmap();
                qrImage.setImageBitmap(qrBits);
            }
        });

    }
}