package com.example.cimon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

public class GetEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_email);
    }

    public void SendCode(View view) {
        EditText email_txt = (EditText) findViewById(R.id.txt_email);
        String email = email_txt.getText().toString();

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Call web service to store email in DB, have web service send email
            // Set user state to "Code Sent"
            SharedPreferences prefs = this.getSharedPreferences(
                    getString(R.string.prefs_file_key), Context.MODE_PRIVATE
            );
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.login_state), getResources().getString(R.string.login_state_code_sent));
            editor.apply();

            startActivity(new Intent(this, EnterCodeActivity.class));
        } else {
            email_txt.setError("Please enter a valid email address");
        }
    }
}
