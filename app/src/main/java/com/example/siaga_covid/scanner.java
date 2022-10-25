package com.example.siaga_covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
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
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class scanner extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scannView;
    TextView resultData;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;


    private StorageReference storageReference;

    private String userID;

    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        scannView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(this,scannView);
        resultData = findViewById(R.id.resultOfQR);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();


        firebaseFirestore = FirebaseFirestore.getInstance();


        calendar = Calendar.getInstance();
       // SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss"); lengkap
        SimpleDateFormat dateFormat = new SimpleDateFormat(" d MMMM yyyy ");
        date = dateFormat.format(Calendar.getInstance().getTime());

        storageReference = FirebaseStorage.getInstance().getReference();

        //bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //bottom navigation view Set profile selected
        bottomNavigationView.setSelectedItemId(R.id.scanner_bottom);

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


        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                userName =(documentSnapshot.getString("Username"));
/*                email.setText(documentSnapshot.getString("Email"));
                password.setText(documentSnapshot.getString("Password"));*/
            }
        });


        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultData.setText(result.getText());
                        final String namaTempatHasilScan = result.getText();
                        final StorageReference fileRef = storageReference.child("places/"+namaTempatHasilScan);
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DocumentReference documentReference = firebaseFirestore.collection("users").document(userID).collection("history_places").document();
                                Map<String, Object> place= new HashMap<>();
                                place.put("placeName", result.getText());
                                place.put("time",date );
                                place.put("placePicture",uri.toString());

                                documentReference.set(place)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Tag", "Data saved");

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Tag", "Error writing document", e);
                                            }
                                        });

/*                              DocumentReference documentReference1 = firebaseFirestore.collection("users").document(userID).collection("hospital_history_places").document();
                                final StorageReference fileRef1 = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"Profile.jpg");
                                Map<String, Object> users= new HashMap<>();
                                users.put("userName", userName);
                                users.put("userTime",date );
                                users.put("userPicture",uri.toString());

                                documentReference1.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Tag", "Data saved");

                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Tag", "Error writing document", e);
                                            }
                                        });*/

                                DocumentReference documentReference1 = firebaseFirestore.collection("hospital").document(result.getText()).collection("hospital_history_user").document(userName);
                                final StorageReference fileRef1 = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"Profile.jpg");
                                fileRef1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri1) {
                                        Map<String, Object> users= new HashMap<>();
                                        users.put("userName", userName);
                                        users.put("userTime",date );
                                        users.put("userPicture",uri1.toString());
                                        documentReference1.set(users);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                                startActivity(new Intent(getApplicationContext(),history_user.class));


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Tag", "Error Tempat tidak tersedia", e);
                            }
                        });
                        
                    }
                });

            }
        });

        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
//        codeScanner.startPreview();
    }

    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(scanner.this, "Camera Permision Required", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
}