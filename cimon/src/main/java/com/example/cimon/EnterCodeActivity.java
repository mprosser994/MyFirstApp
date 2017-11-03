package com.example.cimon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EnterCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);
    }

    public void ConfirmCode(View view) {
        EditText code_txt = (EditText) findViewById(R.id.txt_code);
        String code = code_txt.getText().toString();
        // Invoke web service to validate code
        // If web service returns true:
        if (code.equals("1234")) { // placeholder
            // Set user state to "Logged In"
            SharedPreferences prefs = this.getSharedPreferences(
                    getString(R.string.prefs_file_key), Context.MODE_PRIVATE
            );
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.login_state), getResources().getString(R.string.login_state_logged_in));
            editor.apply();

            // Start main activity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            code_txt.setError("The code you entered is incorrect");
        }
    }
}
