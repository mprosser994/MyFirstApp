package com.example.mpros.myfirstapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class DisplayUIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ui);

        // Set spinner contents
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.dropdown_list, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set list contents
        ListView listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> s_adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.table_list)
        );
        listView.setAdapter(s_adapter);
    }
}
