package com.example.cimon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.ErrnoException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnterCodeActivity extends AppCompatActivity {

    public CimonService service;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://129.74.247.110/cimoninterface/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(CimonService.class);

        prefs = this.getSharedPreferences(
                getString(R.string.prefs_file_key), Context.MODE_PRIVATE
        );
        String email = prefs.getString("email", "");
        String msg = getString(R.string.notif, email);
        TextView textView = (TextView) findViewById(R.id.txt_notif);
        textView.setText(msg);
    }

    public void ConfirmCode(View view) {
        EditText code_txt = (EditText) findViewById(R.id.txt_code);
        String code = code_txt.getText().toString();

        // Get UUID to send to service
        String uuid = prefs.getString("uuid", "");
        String email = prefs.getString("email", "");

        Call<CimonResponse> call = service.verifyToken(email, uuid, code);
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
                showError(new Exception("Invalid token"));
                break;
            case -2:
                showError(new Exception("Invalid input"));
                break;
            case -3:
                showError(new Exception("Token mismatch"));
                break;
            case -9:
            default:
                showError(new Exception("Server error"));
                break;
        }
    }

    private void startNextActivity() {
        // Set user state to "Logged In"
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.login_state), getResources().getString(R.string.login_state_logged_in));
        editor.apply();

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        // Instead of taking user back to another activity, do nothing
        // Maybe just exit app instead?
    }
}
