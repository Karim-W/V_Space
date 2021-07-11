package com.sunflower.vspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterScreen extends AppCompatActivity implements View.OnClickListener{

    //region ::::::::VARIABLES::::::::::::
    private FirebaseAuth mAuth;
    private String fullName;
    private String eMail;
    private String password;
    private String cPassword;
    private String phoneNumber;
    //endregion

    //region ::::::::VIEW ELEMENTS
    private TextView fNameTV;
    private EditText fNameET;
    private TextView EmailTV;
    private EditText EmailET;
    private TextView passTV;
    private EditText passET;
    private TextView cPassTV;
    private EditText cPassET;
    private TextView phoneTV;
    private EditText phoneET;
    private Button Register;
    private TextView RegisterTV;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register_screen);
        getSupportActionBar().hide();
        fNameET = (EditText) findViewById(R.id.editTextTextPersonName2);
        EmailET = (EditText) findViewById(R.id.editTextTextEmailAddress);
        passET = (EditText) findViewById(R.id.editTextTextPassword3);
        cPassET = (EditText) findViewById(R.id.editTextTextPassword4);
        phoneET = (EditText) findViewById(R.id.editTextNumber);

        Register = (Button) findViewById(R.id.button2);
        Register.setPadding(0,5,0,5);
        Register.setBackgroundColor(Color.GRAY);
        Register.setTextColor(Color.WHITE);
    }

    @Override
    public void onClick(View v) {
//        eMail = "k@gmail.com";
//        password="1kimoasK";
//        cPassword = password;
//        mAuth.createUserWithEmailAndPassword(eMail, password);

        if(v.getId()==R.id.button2){
            try {
                fullName = String.valueOf(fNameET.getText());
                eMail = String.valueOf(EmailET.getText());
                password = String.valueOf(passET.getText());
                cPassword = String.valueOf(cPassET.getText());
                phoneNumber =String.valueOf(phoneET.getText());
            }catch(Exception e){
                Log.e("TAG", e.getLocalizedMessage() );
            }
            if(fullName.equals("")||eMail.equals("")||password.equals("")||cPassword.equals("")||phoneNumber.equals("")){
                Toast.makeText(this,"One of the field was left empty, Please fill in all fields.",Toast.LENGTH_LONG).show();
            }else{
                if(password.equals(cPassword)){
                    mAuth.createUserWithEmailAndPassword(eMail, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
//                                        updateUI(user);
                                        goToProfileSetup(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
//                                        Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                                Toast.LENGTH_SHORT).show();
//                                        updateUI(null);
                                    }
                                }
                            });
                }
            }

        }
        else if(v.getId()==R.id.textView7){
            Intent GoToLogin = new Intent(getApplicationContext(),Auth.class);
            startActivity(GoToLogin);
        }
    }
    public void goToProfileSetup(FirebaseUser me){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String path = String.format("/Users/%s",me.getUid());
        DatabaseReference myRef = database.getReference(path);
        String[] theName = fullName.split(" ");
        aUser itsMe = new aUser(theName[0],theName[theName.length-1],phoneNumber,eMail);
        myRef.setValue(itsMe);
        Intent segueToRegProfileSetup = new Intent(getApplicationContext(),RegProfileSetup.class);
        startActivity(segueToRegProfileSetup);
    }
}