package com.example.juiceup;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RequestClass {
    private static String URL = "https://juiceup.000webhostapp.com/";
    private static RequestQueue requestQueue;

    public static void stringRequest(Context context, String api, int method, final Map<String, String> parameters, Response.Listener successs, Response.ErrorListener error) {

        requestQueue = Volley.newRequestQueue(context);
        Log.d("HUSSAIN", "Making request to " + URL + api);
        Log.d("HUSSAIN", "Params: " + parameters.toString());
        StringRequest request = new StringRequest(method, URL + api, successs, error) {
          @Override
          protected Map<String, String> getParams() throws AuthFailureError {
              if (parameters != null) {
                  return parameters;
              } else {
                  return super.getParams();
              }
          }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }


    public static void resolveLatLon(Context context, String placeID, Response.Listener success, Response.ErrorListener error) {
        requestQueue = Volley.newRequestQueue(context);
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" +
                placeID +
                "&key=" +
                "AIzaSyAlyemkP0ByIOkxlAW7KFIlj7wD8BYHQ_o";

        StringRequest request = new StringRequest(Request.Method.GET, url, success, error);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }



    public static void findNearbyPlace(Context context, LatLng latLng, Response.Listener success, Response.ErrorListener error) {
        requestQueue = Volley.newRequestQueue(context);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                String.valueOf(latLng.latitude) +
                "%2C" +
                String.valueOf(latLng.longitude) +
                "&rankby=distance&key=" +
                "AIzaSyAlyemkP0ByIOkxlAW7KFIlj7wD8BYHQ_o";

        StringRequest request = new StringRequest(Request.Method.GET, url, success, error);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }



    public static String reverseGeocoder(Context context, LatLng latLng) {
        Geocoder myLocation = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> myList = myLocation.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return myList.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
