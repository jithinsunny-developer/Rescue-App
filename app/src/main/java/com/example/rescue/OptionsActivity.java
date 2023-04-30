package com.example.rescue;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OptionsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Intent mainOptionsActivityIntent = new Intent(getApplicationContext(), MainOptionsActivity.class);
            startActivity(mainOptionsActivityIntent);
            finish();
        }
    }
    public void gotoRegistration(View view) {
        Intent registrationActivityIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(registrationActivityIntent);
    }

    public void gotoLogin(View view) {
        Intent loginActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginActivityIntent);
    }
}

