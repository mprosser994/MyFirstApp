package com.example.mpros.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.mpros.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void viewUI(View view) {
        Intent intent = new Intent(this, DisplayUIActivity.class);
        startActivity(intent);
    }

    public void viewSys(View view) {
        Intent intent = new Intent(this, DisplaySysActivity.class);
        startActivity(intent);
    }
}
