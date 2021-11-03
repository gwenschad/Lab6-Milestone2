package com.example.lab6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity {
    //somewhere in Australia
    private final LatLng mDestinationLatLng = new LatLng(-33.8523341, 151.2106085);
    //Bascom Hall
    private final LatLng mBascomHallLatLng = new LatLng(43.0757339,-89.4061951);

    private GoogleMap mMap;

    //save the instance of this class
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //can be any number
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //obtain a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);

        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            //code to display marker
            googleMap.addMarker(new MarkerOptions().position(mBascomHallLatLng).title("Bascom Hall"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(mBascomHallLatLng));
            displayMyLocation();
        });

    }

    private void displayMyLocation() {
        //check if permission granted
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        //if not, ask for it
        if(permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        //if permission granted, display marker at current location
        else {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(this, task -> {
                Location mLastKnownLocation = task.getResult();
                //Log.i("task", String.valueOf(task.isSuccessful()));
                if(task.isSuccessful() && (mLastKnownLocation != null)) {
                    mMap.addPolyline(new PolylineOptions().add(
                            new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), mBascomHallLatLng
                    ));

                    //Log.i("latitude", String.valueOf(mLastKnownLocation.getLatitude()));
                    //Log.i("longitude", String.valueOf(mLastKnownLocation.getLongitude()));

                    //add marker for current location
                    LatLng mCurrentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(mCurrentLatLng).title("Current Location"));
                }
            });
        }
    }

    //handles the result of the request for location permissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            //if request is cancelled, the result arrays are empty
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayMyLocation();
            }
        }
    }
}