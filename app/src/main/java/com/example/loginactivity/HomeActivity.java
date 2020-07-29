package com.example.loginactivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2000;
    private long FASTEST_INTERVAL = 5000;
    private LocationManager locationManager;
    private LatLng latLng;
    private boolean isPermission;
    private long onBackPressedTime;
    private Toast backToast;

    Button btnLogout, set;
    EditText temperature;
    EditText heartRate;
    Button viewData;
    Button help;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();



        temperature = findViewById(R.id.temperature);
        heartRate = findViewById(R.id.heartRate);
        set = findViewById(R.id.setValue);
        help = findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewActivity();
            }
        });
        btnLogout = findViewById(R.id.logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent inToMain = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(inToMain);
                finish();
            }
        });
        viewData = findViewById(R.id.view_items_screen);
        viewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent database = new Intent(HomeActivity.this, ViewDatabase.class);
                startActivity(database);
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String temp = temperature.getText().toString();
                final String HearRate = heartRate.getText().toString();
                HealthdataHelper health = new HealthdataHelper(
                        temp + "ᵒC",
                        HearRate + "bpm"
                );

                FirebaseDatabase.getInstance().getReference("Users").
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Health Params").
                        setValue(health).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(HomeActivity.this, "Data not saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void btnRetrieveLocation(View view) {

        startActivity(new Intent(getApplicationContext(), RetrieveMapActivity.class));


    }
    public void openNewActivity(){
        Intent intent = new Intent(this,NewActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {

            //handle the already login user

            if (requestSinglePermission()) {

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                            .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                            .addApi(LocationServices.API)
                            .build();
                    mGoogleApiClient.connect();

                    mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                    checkLocation();
                }

            }
        }
    }


    private boolean checkLocation() {

        if (!isLocationEnabled()) {
            showAlert();
        }
        return isLocationEnabled();

    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean requestSinglePermission() {

//        Toast.makeText(this, "User is already present", Toast.LENGTH_SHORT).show();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        isPermission = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            isPermission = false;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                    }


                }).check();

        return isPermission;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "At OnConnected", Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }

        startLocationUpdates();
        Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }

    }

    private void startLocationUpdates() {


        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, (LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

        if(mFirebaseUser != null) {


            LocationHelper helper = new LocationHelper(
                    location.getLongitude(),
                    location.getLatitude()
            );

            FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Current location")//add as a child to user node
                    .setValue(helper).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        //                 Toast.makeText(MainActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HomeActivity.this, "Location Not Saved", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onBackPressed() {

//        if (onBackPressedTime + 2000 > System.currentTimeMillis()) {
//            backToast.cancel();
//            super.onBackPressed();
//            finish();
//        } else {
//            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
//            backToast.show();
//        }
//        onBackPressedTime = System.currentTimeMillis();
        finish();
    }
}
