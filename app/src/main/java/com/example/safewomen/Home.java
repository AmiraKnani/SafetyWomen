package com.example.safewomen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.osmdroid.config.Configuration;

import java.util.Map;

public class Home extends AppCompatActivity {


    private void saveCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Save the current location in Firebase
                                saveLocationInFirebase(latitude, longitude);

                                Toast.makeText(Home.this, "Current location saved in Firebase", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            requestLocationPermission();
        }
    }


    private void saveLocationInFirebase(double latitude, double longitude) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("locations");
        String locationId = databaseReference.push().getKey();

        LocationData locationData = new LocationData(latitude, longitude);

        if (locationId != null) {
            databaseReference.child(locationId).setValue(locationData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Home.this, "Location saved successfully.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Home.this, "Failed to save location.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }





    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private FusedLocationProviderClient fusedLocationClient;

    private void sendEmailWithLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                String locationText = "Current location: Latitude = " + location.getLatitude() + ", Longitude = " + location.getLongitude();
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("plain/text");
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"amiraknani@gmail.com", "amiraknani@gmail.com"});
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Request for Help");
                                intent.putExtra(Intent.EXTRA_TEXT, "I need help because I feel insecure at my current location.\n" + locationText);
                                startActivity(Intent.createChooser(intent, "send"));
                            }
                        }
                    });
        } else {
            requestLocationPermission();
        }
    }

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestLocationPermission();
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        setContentView(R.layout.activity_maps);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button buttonRequestHelp = findViewById(R.id.button_request_help);
        buttonRequestHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailWithLocation();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Button button_get_to_know_suspicious_place = findViewById(R.id.button_get_to_know_suspicious_place);
        button_get_to_know_suspicious_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Home.this, Maps.class);
                startActivity(intent);

            }
        });
        Button buttonAlertSuspiciousPlace = findViewById(R.id.button_alert_suspicious_place);
        buttonAlertSuspiciousPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentLocation();
            }
        });
    }

    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        intent = new Intent(Home.this, Home.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_settings:
                        intent = new Intent(Home.this, SettingsActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_faq:
                        intent = new Intent(Home.this, FAQ.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_logout:
                        intent = new Intent(Home.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }
}
