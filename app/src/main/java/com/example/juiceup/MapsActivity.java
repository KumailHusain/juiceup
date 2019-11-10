package com.example.juiceup;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    void requestChargingPoints(final GoogleMap map, LatLng myLocation) {
        Map<String, String> params = new HashMap<>();
        params.put("lat", String.valueOf(myLocation.latitude));
        params.put("lon", String.valueOf(myLocation.longitude));

        RequestClass.stringRequest(this, "getchargespots.php", Request.Method.POST, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Log.d("HUSSAIN", response);
                    JSONArray result = new JSONArray(response);
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject body = result.getJSONObject(i);
                        placeMarker(map, new LatLng(body.getDouble("lat"), body.getDouble("lon")), 'C');
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HUSSAIN", "FAILED");
                Log.d("HUSSAIN", error.toString());
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(53.525763, -113.520690);

        Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> myList = null;
        try {
            myList = myLocation.getFromLocation(sydney.latitude, sydney.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("HUSSAIN", myList.toString());

        String Title = myList.get(0).getAddressLine(0);


        mMap.addMarker(new MarkerOptions().position(sydney).title(Title).icon(BitmapDescriptorFactory.fromResource(R.drawable.book4)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        requestChargingPoints(mMap, new LatLng(53.526688, -113.52490));
    }

    public void placeMarker(GoogleMap googleMap, LatLng coordinates, Character type) {
        mMap = googleMap;

        if (type == 'C') {
            mMap.addMarker(new MarkerOptions().position(coordinates).title("Charge Point").icon(BitmapDescriptorFactory.fromResource(R.drawable.lightning)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));

            // If age is greater than 18, print "access granted"
        } else if (type == 'S') {
            mMap.addMarker(new MarkerOptions().position(coordinates).title("Study Spot").icon(BitmapDescriptorFactory.fromResource(R.drawable.book2)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
        }


    }




}
