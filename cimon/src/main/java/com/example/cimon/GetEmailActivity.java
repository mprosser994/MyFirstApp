package com.example.cimon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetEmailActivity extends AppCompatActivity {

    public CimonService service;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_email);

        // Build the CIMON web service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://129.74.247.110/cimoninterface/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(CimonService.class);

        // Initialize the SharedPreferences file for use
        prefs = this.getSharedPreferences(
                getString(R.string.prefs_file_key), Context.MODE_PRIVATE
        );

        // If it doesn't exist yet, generate a UUID to help identify the user to the server
        generateUUID();
    }

    // Called on button press
    public void SendCode(View view) {
        EditText email_txt = (EditText) findViewById(R.id.txt_email);
        String email = email_txt.getText().toString();

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Get UUID to send to service
            String uuid = prefs.getString("uuid", "");

            // Save email
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email", email);
            editor.apply();

            Call<CimonResponse> call = service.signup(email, uuid);
            try {
                call.enqueue(new Callback<CimonResponse>() {
                    @Override
                    public void onResponse(Call<CimonResponse> call, Response<CimonResponse> response) {
                        Log.d("Response from server", String.valueOf(response.body().getCode()) + ", " + response.body().getMessage());
                        if (response.body().getCode() == 0) {
                            startNextActivity();
                        } else {
                            handleServerError(response.body().getCode());
                        }
                    }

                    @Override
                    public void onFailure(Call<CimonResponse> call, Throwable t) {
                        showError((Exception) t);
                    }
                });
            }catch (Exception e){
                showError(e);
            }
        } else {
            email_txt.setError("Please enter a valid email address");
        }
    }

    private void generateUUID() {
        String uuid = prefs.getString("uuid", "");
        if (uuid.equals("")) {
            UUID u = UUID.randomUUID();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("uuid", u.toString());
            editor.apply();
        }
    }

    private void showError(Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage("Exception: " + e.getMessage())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .show();
    }

    private void handleServerError(int code) {
        switch (code) {
            case -1:
                showError(new Exception("Invalid input"));
                break;
            case -9:
            default:
                showError(new Exception("Server error"));
                break;
        }
    }

    private void startNextActivity() {
        // Set user state to "Code Sent"
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.login_state), getResources().getString(R.string.login_state_code_sent));
        editor.apply();

        startActivity(new Intent(this, EnterCodeActivity.class));
    }

    @Override
    public void onBackPressed() {
        // Instead of taking user back to Main Activity, do nothing
        // Maybe just exit app instead?
    }
}
