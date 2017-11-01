package com.example.mpros.myfirstapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplaySysActivity extends AppCompatActivity {
    private LocationManager manager;
    private LocationListener listener;
    private boolean dbReady = false;
    private SQLiteDatabase localDb;
    private Object dropdownItem;
    private TextView textView;
    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    private static String SQL_CREATE_ENTRIES =
            "Create Table " + LocalDBContract.LocationInfo.TABLE_NAME +
                    " (" + LocalDBContract.LocationInfo._ID + " INTEGER PRIMARY KEY," +
                    LocalDBContract.LocationInfo.COL_NAME_TIME + " TEXT," +
                    LocalDBContract.LocationInfo.COL_NAME_LAT + " REAL," +
                    LocalDBContract.LocationInfo.COL_NAME_LONG + " REAL," +
                    LocalDBContract.LocationInfo.COL_NAME_PROVIDER + " TEXT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LocalDBContract.LocationInfo.TABLE_NAME;

    private class LocalDBHelper extends SQLiteOpenHelper {
        public static final int DB_VERSION = 1;
        public static final String DB_NAME = "LocalDB.db";

        public LocalDBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This is just a simple app, so on upgrade we'll start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

    private class AsyncDBSetup extends AsyncTask<LocalDBHelper, Void, SQLiteDatabase> {
        @Override
        protected SQLiteDatabase doInBackground(LocalDBHelper... helpers) {
            return helpers[0].getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase db) {
            localDb = db;
            dbReady = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sys);
        setup();
        askForPermissions();
    }

    public void getCurrentLocation(View view) {
        Location location;
        String[] providers = getResources().getStringArray(R.array.gps_dropdown);

        if (dropdownItem.equals(providers[0]) &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                textView.setText("Lat.: " + location.getLatitude() + ", Long.: " + location.getLongitude());
                saveLocationInfo(location, providers[0]);
            }
            else {
                textView.setText(getResources().getString(R.string.location_err));
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            }
        } else if (dropdownItem.equals(providers[1]) &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                textView.setText("Lat.: " + location.getLatitude() + ", Long.: " + location.getLongitude());
                saveLocationInfo(location, providers[1]);
            }
            else {
                textView.setText(getResources().getString(R.string.location_err));
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
            }
        } else if (dropdownItem != null){
            permissionAlert();
        } else {
            // If we've reached this block, then an unexpected dropdown item was selected
            dropdownAlert();
        }
    }

    private void saveLocationInfo(Location location, String provider) {
        for (int i = 0; i < 15; i++) {
            if (!dbReady && i < 14) {
                try {wait(1000);}
                catch (Exception e) {if (dbReady) break;}
            } else if (i == 14){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("DB Error")
                        .setMessage("Problem preparing the database. Please restart the app.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            } else {
                break;
            }
        }
        ContentValues vals = new ContentValues();
        vals.put(LocalDBContract.LocationInfo.COL_NAME_TIME, dateFormat.format(new Date()));
        vals.put(LocalDBContract.LocationInfo.COL_NAME_LAT, location.getLatitude());
        vals.put(LocalDBContract.LocationInfo.COL_NAME_LONG, location.getLongitude());

        String p;
        switch (provider) {
            case "Access Fine Location":
                p = "GPS";
                break;
            case "Access Coarse Location":
                p = "Network";
                break;
            default:
                p = "ERR";
                break;
        }
        vals.put(LocalDBContract.LocationInfo.COL_NAME_PROVIDER, p);
        localDb.insert(LocalDBContract.LocationInfo.TABLE_NAME, null, vals);
    }

    public void showLocationData(View view) {
        Cursor cursor = getInfo();
        if (cursor != null) {
            String[][] results = getResults(cursor);
            fillTable(results);
            cursor.close();
        }
    }

    private void fillTable(String[][] results) {
        TableLayout table = (TableLayout) findViewById(R.id.gps_table);
        for (int i = 1; i < 11; i++) {
            TableRow row = table.findViewById(i);
            row.setVisibility(View.VISIBLE);
            for (int j = 1; j < 5; j++) {
                TextView cell = row.findViewById(j * 100);
                cell.setText(results[i-1][j-1]);
                cell.setVisibility(View.VISIBLE);
            }
        }
        table.setVisibility(View.VISIBLE);
    }

    private void setup() {
        new AsyncDBSetup().execute(new LocalDBHelper(this));
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // If the previous "getlastlocation" failed, do something here
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
        textView = (TextView) findViewById(R.id.textView);
        setupSpinner();
        setupTable();
    }

    private void setupSpinner() {
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

    private void setupTable() {
        TableLayout table = (TableLayout) findViewById(R.id.gps_table);
        for (int i = 0; i < 11; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));
            row.setId(i);
            for (int j = 1; j < 5; j++) {
                TextView cell = new TextView(this);
                cell.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                ));
                cell.setId(j * 100);
                if (i == 0) {
                    switch (j) {
                        case 1:
                            cell.setText(getResources().getString(R.string.gps_time));
                            break;
                        case 2:
                            cell.setText(getResources().getString(R.string.gps_lat));
                            break;
                        case 3:
                            cell.setText(getResources().getString(R.string.gps_lon));
                            break;
                        case 4:
                            cell.setText(getResources().getString(R.string.gps_provider));
                            break;
                        default:
                            cell.setText(getResources().getString(R.string.err));
                            break;
                    }
                }
                row.addView(cell);
            }
            table.addView(row, new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
            );
        }
    }

    private Cursor getInfo() {
        String[] projection = {
                LocalDBContract.LocationInfo.COL_NAME_TIME,
                LocalDBContract.LocationInfo.COL_NAME_LAT,
                LocalDBContract.LocationInfo.COL_NAME_LONG,
                LocalDBContract.LocationInfo.COL_NAME_PROVIDER
        };
        String sortOrder = LocalDBContract.LocationInfo.COL_NAME_TIME + " DESC";
        String limit = "10";

        try {
            return localDb.query(
                    LocalDBContract.LocationInfo.TABLE_NAME,
                    projection,
                    null, null, null, null,
                    sortOrder,
                    limit
            );
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("DB Error")
                    .setMessage("Exception: " + e.getMessage())
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
            return null;
        }
    }

    private String[][] getResults(Cursor cursor) {
        String[][] results = new String[10][4];
        for(int i = 0; i < 10; i++) {
            if (cursor.moveToNext()) {
                results[i][0] = cursor.getString(
                        cursor.getColumnIndex(LocalDBContract.LocationInfo.COL_NAME_TIME)
                );

                results[i][1] = cursor.getString(
                        cursor.getColumnIndex(LocalDBContract.LocationInfo.COL_NAME_LAT)
                );

                results[i][2] = cursor.getString(
                        cursor.getColumnIndex(LocalDBContract.LocationInfo.COL_NAME_LONG)
                );

                results[i][3] = cursor.getString(
                        cursor.getColumnIndex(LocalDBContract.LocationInfo.COL_NAME_PROVIDER)
                );
            } else {
                break;
            }
        }

        return results;
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
