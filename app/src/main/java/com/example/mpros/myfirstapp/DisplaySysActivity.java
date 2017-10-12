package com.example.mpros.myfirstapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DisplaySysActivity extends AppCompatActivity {
    private LocationManager manager;
    private LocationListener listener;
    private Object dropdownItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sys);
        setup();
        askForPermissions();
    }

    public void getCurrentLocation(View view) {
        Location location;
        String[] array = getResources().getStringArray(R.array.gps_dropdown);

        if (dropdownItem.equals(array[0]) &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                // save location data to DB
            }
            else {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                // alert user there is no location at present
            }
        } else if (dropdownItem.equals(array[1]) &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                // save location data to DB
            }
            else {
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
                // alert user there is no location at present
            }
        } else if (dropdownItem != null){
            permissionAlert();
        } else {
            // If we've reached this block, then an unexpected dropdown item was selected
            dropdownAlert();
        }
    }

    public void showLocationData(View view) {
        // query DB, show results somehow
    }

    private void setup() {
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Probably do nothing
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                // Probably do nothing
            }

            @Override
            public void onProviderEnabled(String s) {
                // Probably do nothing
            }

            @Override
            public void onProviderDisabled(String s) {
                permissionAlert();
            }
        };

        // Set up dropdown
        Spinner dropdown = (Spinner) findViewById(R.id.gps_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.gps_dropdown, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                dropdownItem = adapterView.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                dropdownItem = null;
            }
        };
        dropdown.setOnItemSelectedListener(listener);
    }

    private void permissionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions Needed")
                .setMessage("Please allow this app to access your location.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        askForPermissions();
                    }
                })
                .show();
    }

    private void dropdownAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage("It looks like there was a problem with the dropdown. Please make sure " +
                            "you've selected an item.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void askForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);

        }
    }
}
