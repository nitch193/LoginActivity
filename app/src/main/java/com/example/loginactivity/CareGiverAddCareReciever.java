package com.example.loginactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

//import android.widget.Button;

public class CareGiverAddCareReciever extends AppCompatActivity implements Dialog.ExampleDialogListener {
    private FloatingActionButton button;
    private RecyclerView mListView;
    private Button btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_giver_add_care_reciever);
        button = (FloatingActionButton) findViewById(R.id.addUser);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        btnLogout = findViewById(R.id.button2);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent inToMain = new Intent(CareGiverAddCareReciever.this, MainActivity.class);
                startActivity(inToMain);
                finish();
            }
        });
    }
    public void openDialog() {
        Dialog exampleDialog = new Dialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String userEmail) {
        mListView = findViewById(R.id.listview);

        ArrayList array  = new ArrayList<>();
        if(array.contains(userEmail)){
            Toast.makeText(getBaseContext(), "Care receiver already added", Toast.LENGTH_LONG).show();
        }
        else if(userEmail == null || userEmail.trim().equals("")){
            Toast.makeText(getBaseContext(), "Enter valid email", Toast.LENGTH_LONG).show();
        }
        else{
            array.add("User: " + userEmail);

            RecyclerView.Adapter adapter;
            adapter = new RecyclerView.Adapter() {
                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return null;
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

                }

                @Override
                public int getItemCount() {
                    return 0;
                }
            };
            mListView.setAdapter(adapter);
            userEmail = "";
        }


    }
}
