package com.example.siaga_covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HistoryUserBerkunjungTempat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_user_berkunjung_tempat);


        //nav bottom
        //bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationPlace);

        //bottom navigation view Set profile selected
        bottomNavigationView.setSelectedItemId(R.id.user_history_bottom);

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
    }
}