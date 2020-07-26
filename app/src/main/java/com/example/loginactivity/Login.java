package com.example.loginactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone;
    private Button login;
    private Spinner userTypeSpinner;
    private String userType;
    String TAG = "Sample";



    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private com.google.android.gms.location.LocationListener listener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextName = findViewById(R.id.et_name);
        editTextEmail = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_password);
        editTextPhone = findViewById(R.id.phone);
        login = findViewById(R.id.button);
        userTypeSpinner = findViewById(R.id.user_type);


        mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");

        findViewById(R.id.button).setOnClickListener(this);

        findViewById(R.id.btn_register).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                registerUser();
                break;
            case R.id.button:
                Intent i = new Intent(Login.this, MainActivity.class);
                startActivity(i);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }


    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String userType = userTypeSpinner.getSelectedItem().toString().trim();
        boolean error = false;

        if (name.isEmpty()) {
            error = true;
            editTextName.setError(getString(R.string.input_error_name));
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            error = true;
            editTextEmail.setError(getString(R.string.input_error_email));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error = true;
            editTextEmail.setError(getString(R.string.input_error_email_invalid));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            error = true;
            editTextPassword.setError(getString(R.string.input_error_password));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            error = true;
            editTextPassword.setError(getString(R.string.input_error_password_length));
            editTextPassword.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            error = true;
            editTextPhone.setError(getString(R.string.input_error_phone));
            editTextPhone.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            error = true;
            editTextPhone.setError(getString(R.string.input_error_phone_invalid));
            editTextPhone.requestFocus();
            return;
        }

        if (userType.equals("Choose a role")){
            error = true;
            findViewById(R.id.invalid_role).setVisibility(View.VISIBLE);
        }

            if(!error)
            {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();
                                // Creates a new user in the database

                                Intent intent = new Intent(Login.this, IntermediateActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                                findViewById(R.id.register_invalid_email).setVisibility(View.VISIBLE);
                            }


                            Toast.makeText(Login.this, "User registered", Toast.LENGTH_SHORT).show();
                            Intent t = new Intent(Login.this, IntermediateActivity.class);
                            startActivity(t);

                            UserInformation user = new UserInformation(
                                    name,
                                    email,
                                    userType

                            );

                            DatabaseReference mPathReference = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("User data");
                            mPathReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                    } else {
                                        //display a failure message
                                    }
                                }

                            });
                        }

                    });
        }
//            else {
//                Toast.makeText(this,"Check all the fields", Toast.LENGTH_SHORT).show();
//            }

    }





}