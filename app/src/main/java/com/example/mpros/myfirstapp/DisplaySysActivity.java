package com.example.mpros.myfirstapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplaySysActivity extends AppCompatActivity {

    private TextView textView = (TextView) findViewById(R.id.locationText);
    private LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            setText(textView, location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            // Do I need this?
        }

        @Override
        public void onProviderDisabled(String s) {
            // Raise alert message
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sys);
    }

    public void updateLocation(View view) {
        Location location;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                setText(textView, location);
            }
            else {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            }
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                setText(textView, location);
            }
            else {
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
            }
        }
    }

    private void setText(TextView textView, Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        textView.setText("Lat.: " + lat + ", Long.: " + lon);
    }
}
