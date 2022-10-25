package com.example.siaga_covid;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Daftar extends AppCompatActivity {
    EditText mFullName, mPassword, mEmail;
    TextView LoginBtn;
    Button RegisterBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);
        mFullName = findViewById(R.id.Nama);
        mEmail = findViewById(R.id.EmailField);
        LoginBtn = findViewById(R.id.keLoginScreen);
        RegisterBtn = findViewById(R.id.button_daftar);
        mPassword = findViewById(R.id.PasswordField);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
//        if (fAuth.getCurrentUser()!=null){  //nanti langsung ke login screen kalau ini di uncomment.
//            startActivity(new Intent(getApplicationContext(), Login.class));
//            finish();
//        }
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email= mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                final String userName = mFullName.getText().toString();


                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Mohon Email Diisi!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Mohon Password Diisi!");
                    return;
                }
                if (password.length()<6)
                {
                    mPassword.setError("Password minimal 6 karakter");
                }

                progressBar.setVisibility(View.VISIBLE);
                //Register the user data
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Daftar.this, "User Created",Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("Username",userName);
                            user.put("Email",email);
                            user.put("Password",password);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG","onSuccess: user profile is created for"+ userID);
                                }
                            });
                            startActivity(new Intent(new Intent(getApplicationContext(),Login.class)));
                        }
                        else {
                            Toast.makeText(Daftar.this, "Error"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

//        Spinner mySpinner = (Spinner) findViewById(R.id.spinner1);
//
//        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Daftar.this,
//                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.list_type_user));
//        myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        mySpinner.setAdapter(myAdapter);
    }
}