package com.sunflower.vspace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

import java.util.ArrayList;
import java.util.List;

public class myfriends extends AppCompatActivity implements View.OnClickListener {

    private Button add;
    private String m_Text = "";
    FirebaseAuth m = FirebaseAuth.getInstance();
    FirebaseDatabase FB = FirebaseDatabase.getInstance();
    private ListView L1;
    private ListView L2;
    private List<String> nF = new ArrayList<String>();
    private List<String> aF = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfriends);
        add = (Button) findViewById(R.id.adduser);
        add.setOnClickListener(this);
        L1 = (ListView) findViewById(R.id.l1);
        new getfriends().execute();
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                addF();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    public void addF(){
        DatabaseReference db = FB.getReference().child("Users").child(m.getUid()).child("Friends");
        db.child(m_Text).setValue("NULL");

    }
    public class getfriends extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            List<String> fIds = new ArrayList<>();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
               String url = "https://vspaceapi.herokuapp.com/v2/friends/near?userid=" + m.getCurrentUser().getUid()+"&Long="+getIntent().getStringExtra("ln")+"&Lat="+getIntent().getStringExtra("la");
//            String url = "https://vspaceapi.herokuapp.com/v2/locations/list?lid=334";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String f1 = response.toString();
                            try {
                                JSONArray lwhy = response.getJSONArray("User-Ids");
                                JSONObject temp;
                                for (int i = 0; i < lwhy.length(); i++) {
                                    temp = lwhy.getJSONObject(i);
                                    fIds.add(temp.getString("Id"));
                                }
                                Log.d("daasd", "hi");
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef =database.getReference().child("Users");
                                Log.d("Testing: ", String.valueOf(fIds.size()));
                                for (int i = 0; i < fIds.size(); i++) {
                                    String id = fIds.get(i);
                                    myRef.child(fIds.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            nF.add(snapshot.child("Full Name").getValue(String.class));
                                            ArrayAdapter<String> arr;
                                            arr = new ArrayAdapter<String>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,nF);
                                            L1.setAdapter(arr);
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