package com.example.juiceup;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    List<Marker> markerList = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key), Locale.CANADA);
        }
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                removeAllMarkers();


                LatLng coords = place.getLatLng();
                if (coords == null) {

                    RequestClass.resolveLatLon(getApplicationContext(), place.getId(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                JSONObject result = obj.getJSONObject("result");
                                JSONObject geometry = result.getJSONObject("geometry");
                                JSONObject location = geometry.getJSONObject("location");
                                LatLng loc = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                                requestChargingPoints(loc);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Failed to resolve coordinates", Toast.LENGTH_SHORT);
                        }
                    });
                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 17));
                    requestChargingPoints(coords);
                }

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });


    }

    void removeAllMarkers() {
        for (Marker marker : markerList) {
            marker.remove();
        }
        markerList = new ArrayList<Marker>();
    }

    void requestChargingPoints(LatLng myLocation) {
        Map<String, String> params = new HashMap<>();
        params.put("lat", String.valueOf(myLocation.latitude));
        params.put("lon", String.valueOf(myLocation.longitude));

        RequestClass.stringRequest(this, "getchargespots.php", Request.Method.POST, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray result = new JSONArray(response);
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject body = result.getJSONObject(i);
                        placeMarker(new LatLng(body.getDouble("lat"), body.getDouble("lon")), body);
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


    void requestStudyPlaces(LatLng myLocation) {
        Map<String, String> params = new HashMap<>();
        params.put("lat", String.valueOf(myLocation.latitude));
        params.put("lon", String.valueOf(myLocation.longitude));
        params.put("day", getDayNumber());

        RequestClass.stringRequest(this, "getstudyspaces.php", Request.Method.POST, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject result = new JSONObject(response);

                    Iterator<String> iter = result.keys();
                    while (iter.hasNext()) {
                        JSONObject item = new JSONObject();

                        String key = iter.next();
                        JSONArray keyobj = new JSONArray(key);
                        item.put("lat", keyobj.getDouble(0));
                        item.put("lon", keyobj.getDouble(1));
                        item.put("level", keyobj.getDouble(2));
                        item.put("add", keyobj.getString(3));

                        JSONArray value = result.getJSONArray(key);
                        item.put("crowd", value.getJSONArray(0));
                        item.put("noise", value.getJSONArray(1));

                        placeMarker(new LatLng(item.getDouble("lat"), item.getDouble("lon")), item);
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

    private String getDayNumber() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day = day - 1;
        if (day == 0)
            day = 7;
        return String.valueOf(day);
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
        mMap.setOnMarkerClickListener(this);


        LatLng currentLocation = getCurrentLocation();
        if (currentLocation == null)
            currentLocation = new LatLng(53.526688, -113.52490);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));

        requestChargingPoints(currentLocation);
        requestStudyPlaces(currentLocation);
    }


    private void placeMarker(LatLng coordinates, JSONObject data) {
        MarkerOptions options = new MarkerOptions().position(coordinates);
        if (data.has("lock")) {
            // This is a C
            try {
                String title = data.getString("add");
                if (data.getInt("lock") == 1) {
                    title += "\nThere is a locker for your phone!";
                }
                options.title(title);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // This is an S

        }
        Marker marker = mMap.addMarker(options);
        marker.setTag(data);
        markerList.add(marker);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        JSONObject data = (JSONObject) marker.getTag();
        if (data.has("lock")) {
            return false;
        } else {
            Intent intent = new Intent(this, CrowdInfoActivity.class);
            intent.putExtra("jsondata", data.toString());
            startActivity(intent);
        }
        return false;
    }

    LatLng getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return null;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        return new LatLng(lat, lng);

    }


}
