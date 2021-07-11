package com.sunflower.vspace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class locationDetails extends AppCompatActivity implements View.OnClickListener  {

    private TextView title;
    private TextView bio;
    private Button info;
    private Button share;
    private ImageView img;
    private ListView frensList;
    private Button totList;
    private Button checkStatus;

    private FirebaseDatabase FB;
    private String locationName;
    private String locationBio;
    private String locaitonLink;
    private String locaitonSocial;
    private String locationRev;
    private String imgUrl;
    private String Id;
    private List<String> frensId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        getSupportActionBar().hide();
        Intent i = getIntent();
        Id = i.getStringExtra("id");
        title = (TextView)findViewById(R.id.locTitle);
        bio = (TextView)findViewById(R.id.locBio);
        img = (ImageView)findViewById(R.id.locImage);
        frensList = (ListView)findViewById(R.id.frensList);
        info = (Button)findViewById(R.id.locInfo);
        share = (Button)findViewById(R.id.locShare);
        totList = (Button) findViewById(R.id.loclist);
        checkStatus = (Button)findViewById(R.id.Checkin);
        checkStatus.setOnClickListener(this);
        share.setOnClickListener(this);
        info.setOnClickListener(this);
        totList.setOnClickListener(this);
        FB = FirebaseDatabase.getInstance();
        DatabaseReference myDB = FB.getReference().child("Locations").child(i.getStringExtra("id"));
        myDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                locationName = snapshot.child("Name").getValue(String.class);
                locationBio = snapshot.child("Bio").getValue(String.class);
                locaitonLink = snapshot.child("Link").getValue(String.class);
                locaitonSocial = snapshot.child("Social").getValue(String.class);
                locationRev =  snapshot.child("Rev").getValue(String.class);
                imgUrl = snapshot.child("Img").getValue(String.class);
                updateView();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }
    public void updateView(){
        title.setText(locationName);
        bio.setText(locationBio);
        try {
            Bitmap l = new DownloadImageTask().execute(imgUrl).get();
            img.setImageBitmap(l);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new getFriends().execute();
    }

    public void Check_in(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference myDb = FB.getReference().child("Users").child(auth.getUid().toString());
        myDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(!snapshot.child("check").getValue(boolean.class)){
                    myDb.child("check").setValue(true);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = "https://vspaceapi.herokuapp.com/v1/checkin/?uid="+auth.getUid()+"&location=" +Id ;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error
                                }
                            });
                    queue.add(jsonObjectRequest);
                }{
                    myDb.child("check").setValue(true);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = "https://vspaceapi.herokuapp.com/v1/checkin/?uid="+auth.getUid()+"&location=" +Id ;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error
                                }
                            });
                    queue.add(jsonObjectRequest);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... voids) {
            String myUrl = voids[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(myUrl).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
    }
    public void updateListItems(){
        ArrayList<String> data =
                new ArrayList<String>();
        for (String myFren: frensId)
        {
            HashMap<String, String> map = new HashMap<String, String>();
            DatabaseReference myDB = FB.getReference().child("Users").child(myFren);
            myDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String Name = "\n"+snapshot.child("Full Name").getValue(String.class);
                    data.add(Name);
                    ArrayAdapter<String> arr;
                    arr = new ArrayAdapter<String>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,data);
                    frensList.setAdapter(arr);

                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

        }
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, R.layout.friends_list_in_loc, data);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.locInfo){
            Uri uriUrl = Uri.parse(locaitonLink);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        }else if(id==R.id.locShare){
            Uri uriUrl = Uri.parse(locaitonSocial);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        }else if(id==R.id.loclist){
            Intent goToLocList = new Intent(getApplicationContext(),locationUsersList.class);
            goToLocList.putExtra("Id",id);
            startActivity(goToLocList);
        }else if(id==R.id.Checkin){
            Check_in();
        }else if(id==R.id.locRev){
            Uri uriUrl = Uri.parse(locationRev);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        }
    }


    public class getFriends extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            List<String> locationIds = new ArrayList<>();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "https://vspaceapi.herokuapp.com/v2/friends/location/?lid=" +Id ;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String f1 = response.toString();
                            try {
                                JSONArray lwhy = response.getJSONArray("User-Ids");
                                for(int i =0;i<lwhy.length();i++){
                                    frensId.add(lwhy.getJSONObject(i).getString("Id").toString());
                                }
                                updateListItems();
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