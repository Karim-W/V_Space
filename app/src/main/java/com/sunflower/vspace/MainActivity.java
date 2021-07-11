package com.sunflower.vspace;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sunflower.vspace.Models.LocationMarkerItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Implement OnMapReadyCallback.
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, ConnectionCallbacks, OnConnectionFailedListener,GoogleMap.OnMarkerClickListener {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private double Lng = 55.334;
    private double Lat = 25.332;
    private GoogleMap myMap;
    private RequestQueue mQueue;
    private FusedLocationProviderClient fusedLocationClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleApiClient googleApiClient; //to turn requests on and off
    public static final int UPDATE_INTERVAL = 5000; // 5 secs
    public static final int FASTEST_UPDATE_INTERVAL = 2000;
    private LocationRequest locationRequest;
    private Button Add;
    private String myLongitude;
    private String myLatitude;
    private List<LocationMarkerItems> nearbyLocs = new ArrayList<LocationMarkerItems>();
    private Marker[] Locations;
    private Marker[] Friends;
    private FirebaseAuth mAuth;
    private FirebaseDatabase FB;
    private TextView locationTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        try {
            mAuth = FirebaseAuth.getInstance();
            mAuth.getCurrentUser();
        }catch (Exception e){
            Intent goToAuth = new Intent(getApplicationContext(),Auth.class);
            startActivity(goToAuth);
        }
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FB = FirebaseDatabase.getInstance();
        LinearLayout i = (LinearLayout)findViewById(R.id.TitleLinearLayout);
        locationTitle = (TextView)findViewById(R.id.locTitle);

        //myMap.setOnMarkerClickListener(this);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.setOnMarkerClickListener(this);

    }

    @Override
    //@SuppressWarnings("deprecated")
    public void onConnected(Bundle dataBundle) {
        // Put code to run after connecting here ex. register to receive location updates

        // Check Permissions Now
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 123);

        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                myLongitude = location.getLongitude() + "";
                myLatitude = location.getLatitude() + "";
                LatLng loc = new LatLng(Float.parseFloat(myLatitude), Float.parseFloat(myLongitude));
                try {
                    //                myMap.addMarker(new MarkerOptions()
                    //                        .position(loc)
                    //                        .title("my location"));
                    myMap.getUiSettings().setZoomControlsEnabled(true);
                    if (myMap != null) {
                        myMap.animateCamera(

                                CameraUpdateFactory.newCameraPosition(
                                        new CameraPosition.Builder()
                                                /* Creates a builder for a camera position.*/
                                                .target(new LatLng(location.getLatitude(),
                                                        location.getLongitude()))
                                                .zoom(16.5f)
                                                .bearing(0)
                                                .tilt(50)
                                                .build() /*builds a CameraPosition*/
                                )
                        );
                    }
                    myMap.addCircle(new CircleOptions().center(loc).radius(20).strokeColor(Color.WHITE).fillColor(Color.argb(255, 0, 122, 255)));
                    new getLocations().execute();
                } catch (Exception e) {}
                Log.d("TS", "" + location.getLatitude() + "|" + location.getLongitude());
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        } catch (SecurityException s) {
            Log.d("TS", "Not able to run location services...");
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123)
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                onConnected(new Bundle());
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude() + "";
        myLongitude = location.getLongitude() + "";
    }

    @Override
    protected void onStart() { //called before on resume – corresponds to onStop
        super.onStart();
        googleApiClient.connect(); //connect to google play services not GPS!
    }

    @Override
    protected void onPause() {
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(
            ConnectionResult connectionResult) {
        // Put code to run if connection fails here
        // ex. print out an error message !
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(getApplicationContext(),"Iwas clicked"+marker.getTag(),Toast.LENGTH_LONG).show();
        showBottomSheetDialog(marker.getTag()+"");
        return false;
    }
    private void showBottomSheetDialog(String id) {
        Intent goToLoc = new Intent(getApplicationContext(),locationDetails.class);
        goToLoc.putExtra("id",id);
        startActivity(goToLoc);

    }

    public class getLocations extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            List<String> locationIds = new ArrayList<>();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "https://vspaceapi.herokuapp.com/v2/locations/near?uid=" + mAuth.getCurrentUser().getUid();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            String f1 = response.toString();
                            try {
                                JSONArray lwhy = response.getJSONArray("Location-Ids");
                                JSONObject temp;
                                for (int i = 0; i < lwhy.length(); i++) {
                                    temp = lwhy.getJSONObject(i);
                                    locationIds.add(temp.getString("Id"));
                                }
                                Log.d("daasd", "hi");
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef =database.getReference().child("Locations");
                                Log.d("Testing: ", String.valueOf(locationIds.size()));
                                for (int i = 0; i < locationIds.size(); i++) {
                                    String id = locationIds.get(i);
                                    myRef.child(locationIds.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            LocationMarkerItems inity = new LocationMarkerItems(id,
                                                    snapshot.child("Name").getValue(String.class),
                                                    snapshot.child("Lat").getValue(Float.class),
                                                    snapshot.child("Long").getValue(Float.class));
                                            nearbyLocs.add(inity);
                                            LatLng initMarker = new LatLng(inity.getLat(), inity.getLong());

                                            Marker initAMarker = myMap.addMarker(new MarkerOptions()
                                                    .position(initMarker)
                                                    .title(inity.getName()));
                                            initAMarker.showInfoWindow();
                                            initAMarker.setTag(inity.getLocationId());


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {

                                        }
                                    });

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Log.d("GGGGG", error.getMessage());
                        }
                    });
            queue.add(jsonObjectRequest);
            return null;
        }



    }
}