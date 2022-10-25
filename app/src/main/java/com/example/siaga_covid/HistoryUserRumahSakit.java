package com.example.siaga_covid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class HistoryUserRumahSakit extends AppCompatActivity {

    private RecyclerView mFireStoreList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private FirestoreRecyclerAdapter adapterUser;

    private String userID;

    public static final String EXTRA_TEXT = "com.example.application.siaga_covid.EXTRA_TEXT";

    private List<String> data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_user_rumah_sakit);

        //nav bottom
        //bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationMedical);

        //bottom navigation view Set profile selected
        bottomNavigationView.setSelectedItemId(R.id.user_list);

        //bottom navigation view selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.user_list:
                        startActivity(new Intent(getApplicationContext(), HistoryUserRumahSakit.class));
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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mFireStoreList = findViewById(R.id.firestore_listRumahSakit);

        userID = firebaseAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        String text = intent.getStringExtra(Medical.EXTRA_TEXT);



        //Query
        Query query = firebaseFirestore.collection("hospital").document(text).collection("hospital_history_user");
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>().setQuery(query,UserModel.class).build();

        adapterUser = new FirestoreRecyclerAdapter<UserModel, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_single, parent, false);
                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull UserModel model) {
                holder.list_Username.setText(model.getUserName());
                holder.list_Usertime.setText(model.getUserTime());
                Glide.with(holder.list_Userpicture.getContext()).load(model.getUserPicture()).into(holder.list_Userpicture);
                String Device = data.get(position);
                holder.device.setText(model.getDevice());


            }
        };

        mFireStoreList.setHasFixedSize(true);
        mFireStoreList.setLayoutManager(new LinearLayoutManager(this));
        mFireStoreList.setAdapter(adapterUser);



    }



    private class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView list_Username;
        private TextView list_Usertime;
        private CircularImageView list_Userpicture;
        private TextView device;
        private Button button_send;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            list_Username = itemView.findViewById(R.id.user_name_list);
            list_Usertime = itemView.findViewById(R.id.user_time_list);
            list_Userpicture = itemView.findViewById(R.id.user_picture_list);
            device = itemView.findViewById(R.id.user_device_list);
            button_send = itemView.findViewById(R.id.button_send);

/*            button_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(),Send_Notification_KotlinKt.class);
                    intent.putExtra("Device", data.get(getAdapterPosition()));;
                    v.getContext().startActivity(intent);
                }
            });*/
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapterUser.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterUser.startListening();
    }

}