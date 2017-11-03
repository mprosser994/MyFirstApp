package com.example.cimon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String logged_in = getResources().getString(R.string.login_state_logged_in);
        String code_sent = getResources().getString(R.string.login_state_code_sent);

        SharedPreferences prefs = this.getSharedPreferences(
                getString(R.string.prefs_file_key), Context.MODE_PRIVATE
        );
        String reg_state = prefs.getString(getString(R.string.login_state), getResources().getString(R.string.login_state_new_user));

        if (reg_state.equals(logged_in)) {
            setContentView(R.layout.activity_main);
        } else if (reg_state.equals(code_sent)) {
            startActivity(new Intent(this, EnterCodeActivity.class));
        } else {
            startActivity(new Intent(this, GetEmailActivity.class));
        }
    }
}
