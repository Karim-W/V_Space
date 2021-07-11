//package com.sunflower.vspace;
//
//import android.os.AsyncTask;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import java.util.Map;
//
//public class getLocations extends AsyncTask<Map<Double,Double>,Void,Map<Double,Double>[]> {
//
//    @Override
//    protected Map<Double,Double>[] doInBackground(Map<Double,Double>... strings) {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url ="http://localhost:3006/v1/nearby/?";
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//
//            });
//
//        return null;
//    }
//
//
//    @Override
//    protected void onPostExecute(Map<Double,Double>[] s) {
//
//    }
//}
