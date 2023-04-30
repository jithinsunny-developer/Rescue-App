package com.example.rescue;


import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        Intent registrationActivityIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
//
//        startActivity(registrationActivityIntent);
//        Intent loginActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
//
//        startActivity(loginActivityIntent);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent optionsActivityIntent = new Intent(getApplicationContext(), OptionsActivity.class);

                startActivity(optionsActivityIntent);

                finish();

            }
        }, SPLASH_DISPLAY_LENGTH);


    }
}
