package com.example.loginactivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class IntermediateActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("User data"); //Does Users -> Groups
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermediate);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    public void onStart(){
        super.onStart();
        if (mAuth.getCurrentUser() == null){
            Intent intent = new Intent(IntermediateActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (mAuth.getCurrentUser() != null){

            final DatabaseReference newRef = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("User data");
            newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot != null) {
                        final String usertype = String.valueOf(snapshot.child("usertype").getValue());
                        if(usertype != null && usertype.equals("Care Reciever")){
                            Intent intent = new Intent(IntermediateActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }

                        else if(usertype != null && usertype.equals("Care Taker")){
                            Intent mIntent = new Intent(IntermediateActivity.this, CareGiverAddCareReciever.class);
                            startActivity(mIntent);
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}