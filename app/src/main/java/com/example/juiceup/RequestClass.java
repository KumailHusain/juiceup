package com.example.juiceup;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
}
