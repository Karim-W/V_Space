package com.sunflower.vspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class locationUsersList extends AppCompatActivity {
    private ListView thList;
    private List<String> usersIds = new ArrayList<String>();
    private String Id;
    private FirebaseDatabase FB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_users_list);
        getSupportActionBar().hide();
        thList = (ListView)findViewById(R.id.imtired);
        Id = getIntent().getStringExtra("ID");
        FB = FirebaseDatabase.getInstance();
        new getUsers().execute();

    }

    public void updateListItems(){
        ArrayList<String> data =
                new ArrayList<String>();
        for (String myFren: usersIds)
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
                    thList.setAdapter(arr);

                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

        }
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, R.layout.friends_list_in_loc, data);
    }

    public class getUsers extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            List<String> locationIds = new ArrayList<>();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "https://vspaceapi.herokuapp.com/v2/locations/list?lid=" +Id ;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String f1 = response.toString();
                            try {
                                JSONArray lwhy = response.getJSONArray("User-Ids");
                                for(int i =0;i<lwhy.length();i++){
                                    usersIds.add(lwhy.getJSONObject(i).getString("Id").toString());
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