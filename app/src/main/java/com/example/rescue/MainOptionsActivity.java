package com.example.rescue;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_options);
    }

    public void gotoFindDevice(View view) {
        Intent findDeviceIntent = new Intent(getApplicationContext(), FindDeviceActivity.class);
        startActivity(findDeviceIntent);
    }

    public void gotoAddContacts(View view) {
        Intent contactsIntent = new Intent(getApplicationContext(), ContactsActivity.class);
        startActivity(contactsIntent);
    }

    public void gotoSettings(View view) {
        Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void gotoHealth(View view) {
        Intent healthIntent = new Intent(getApplicationContext(), HealthActivity.class);
        startActivity(healthIntent);
    }
}

