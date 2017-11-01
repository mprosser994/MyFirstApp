package com.example.cimon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class GetEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_email);
    }

    public void SendCode(View view) {
        EditText email = (EditText) findViewById(R.id.txt_email);
        // Check if email is valid? (Does this particular widget do that already?)
        // Call web service to store email in DB, have web service send email
        // Do something to save this state so app doesn't open on this activity anymore
        Intent intent = new Intent(this, EnterCodeActivity.class);
        startActivity(intent);
    }
}
