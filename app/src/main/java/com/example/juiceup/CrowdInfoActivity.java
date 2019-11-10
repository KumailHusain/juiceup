package com.example.juiceup;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class CrowdInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowd_info2);


        String strdata = getIntent().getStringExtra("jsondata");
        Log.d("KUMAIL", "GOT: " + strdata);
        try {
            JSONObject data = new JSONObject(strdata);
            TextView name = findViewById(R.id.crowd_info_name);
            name.setText(data.getString("add"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        FloatingActionButton fab = findViewById(R.id.fab);
    }

}
