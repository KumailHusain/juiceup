package com.example.juiceup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChargerFeedback extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger_feedback);

        final double lat = getIntent().getDoubleExtra("lat", 0);
        final double lon = getIntent().getDoubleExtra("lon", 0);




        RequestClass.findNearbyPlace(this, new LatLng(lat, lon), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("lat", String.valueOf(lat));
                    params.put("lon", String.valueOf(lon));
                    params.put("lev", ((EditText) findViewById(R.id.cfb_level)).getText().toString());
                    params.put("lstatus", String.valueOf(((CheckBox) findViewById(R.id.cfb_checkbox)).isChecked()));
                    String add = (new JSONObject(response)).getJSONObject("result").getJSONArray("address_components").getJSONObject(0).getString("long_name");
                    params.put("add", add);
                    ((TextView) findViewById(R.id.cfb_add)).setText(add);
                    final Map<String, String> params1 = params;
                    Button btn = findViewById(R.id.cfb_button);
                    btn.setEnabled(true);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RequestClass.stringRequest(getApplicationContext(), "putchargespots.php", Request.Method.POST, params1, new Response.Listener() {
                                @Override
                                public void onResponse(Object response) {

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                            finish();
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });




    }
}

// lat, lon, lev, lstatus, add