package com.example.siaga_covid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.client.result.ProductParsedResult;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.google.firebase.firestore.Query;

public class history_user extends AppCompatActivity {

    private RecyclerView mFireStoreList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private FirestoreRecyclerAdapter adapter;

    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_user);

        //bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //bottom navigation view Set profile selected
        bottomNavigationView.setSelectedItemId(R.id.place_history_bottom);

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


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mFireStoreList = findViewById(R.id.firestore_list);

        userID = firebaseAuth.getCurrentUser().getUid();

        //Query
        Query query = firebaseFirestore.collection("users").document(userID).collection("history_places");
        //RecyclerOptions
        FirestoreRecyclerOptions<PlaceModel> options = new FirestoreRecyclerOptions.Builder<PlaceModel>().setQuery(query,PlaceModel.class).build();

        adapter = new FirestoreRecyclerAdapter<PlaceModel, PlaceViewHolder>(options) {
            @NonNull
            @Override
            public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
                return new PlaceViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PlaceViewHolder holder, int position, @NonNull PlaceModel model) {
                holder.list_name.setText(model.getPlaceName());
                holder.list_time.setText(model.getTime());
                Glide.with(holder.list_picture.getContext()).load(model.getPlacePicture()).into(holder.list_picture);
            }
        };

        mFireStoreList.setHasFixedSize(true);
        mFireStoreList.setLayoutManager(new LinearLayoutManager(this));
        mFireStoreList.setAdapter(adapter);

    }

    private class PlaceViewHolder extends RecyclerView.ViewHolder {

        private TextView list_name;
        private TextView list_time;
        private CircularImageView list_picture;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.place_name_list);
            list_time = itemView.findViewById(R.id.place_time_list);
            list_picture = itemView.findViewById(R.id.place_picture_list);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}