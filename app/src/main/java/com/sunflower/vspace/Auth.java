package com.sunflower.vspace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Auth extends AppCompatActivity implements View.OnClickListener {
    //region ::::::VARIABLES:::::
    private FirebaseAuth mAuth;
    //endregion
    //region ::::::VIEW ELEMENTS::::::
    private EditText Email;
    private EditText PassWord;
    private Button LoginButton;
    private String emailString;
    private String passString;
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        Email = (EditText) findViewById(R.id.editTextTextPersonName);
        PassWord = (EditText) findViewById(R.id.editTextTextPassword);
        LoginButton = (Button) findViewById(R.id.button);
        LoginButton.setPadding(0,5,0,5);
        LoginButton.setBackgroundColor(Color.GRAY);
        LoginButton.setTextColor(Color.WHITE);
        LoginButton.setOnClickListener(this);
        if(mAuth.getCurrentUser()==null){
            Intent goToRegProfileSetup = new Intent(getApplicationContext(),RegProfileSetup.class);
            startActivity(goToRegProfileSetup);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button){
            emailString = Email.getText().toString();
            passString = PassWord.getText().toString();
//            Toast.makeText(this,emailString,Toast.LENGTH_LONG).show();
//            if(emailString.equals("")||passString.equals("")){
//
//            }else{
            mAuth.signInWithEmailAndPassword("karim.wael@gmail.com", "123456")
                        .addOnCompleteListener(this, task -> {
                            Toast.makeText(this,"ImHEREE",Toast.LENGTH_LONG).show();
                            Log.d("hi hello", "signInWithEmail:success");
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent goToMain = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(goToMain);
//                                    updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.getException());

//                                    updateUI(null);
                            }});
            }else if(v.getId()==R.id.goToRegisterLable){
            Intent goToRegister = new Intent(getApplicationContext(),RegisterScreen.class);
            startActivity(goToRegister);

        }

//        }
    }
}