package com.example.rescue;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private SQLiteDatabase database;
    private TextView showVibrationTime, showSendDurationTime;

    private Switch smsSwitch, locationSwitch, audioSwitch, cameraSwitch;

    private int REQUEST_CODE_SMS = 3786;
    private int REQUEST_CODE_LOCATION = 3787;
    private int REQUEST_CODE_AUDIO = 3788;
    private int REQUEST_CODE_CAMERA = 3789;

    private void handlePermissions(){
        // SMS Permission
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){

            smsSwitch.setChecked(false);
        }else{
            smsSwitch.setChecked(true);
        }

        // LOCATION (FINE) Permission
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            locationSwitch.setChecked(false);
        }else{
            locationSwitch.setChecked(true);
        }

        // RECORD AUDIO Permission
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            audioSwitch.setChecked(true);
        }else{
            audioSwitch.setChecked(false);
        }

        // CAMERA Permission
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            cameraSwitch.setChecked(false);
        }else{
            cameraSwitch.setChecked(true);
        }

        smsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(smsSwitch.isChecked()){

                    ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE_SMS);
                }else{
                    Toast.makeText(SettingsActivity.this, "Disable the permission by goto the settings manually !", Toast.LENGTH_SHORT).show();
                    smsSwitch.setChecked(true);
                }
            }
        });

        locationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationSwitch.isChecked()){
                    ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
                }else{
                    Toast.makeText(SettingsActivity.this, "Disable the permission by goto the settings manually !", Toast.LENGTH_SHORT).show();
                    locationSwitch.setChecked(true);
                }
            }
        });

        audioSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audioSwitch.isChecked()){
                    ActivityCompat.requestPermissions(SettingsActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_AUDIO);

                }else{
                    Toast.makeText(SettingsActivity.this, "Disable the permission by goto the settings manually !", Toast.LENGTH_SHORT).show();
                    audioSwitch.setChecked(true);
                }
            }
        });

        cameraSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationSwitch.isChecked()){
                    ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                }else{
                    Toast.makeText(SettingsActivity.this, "Disable the permission by going to the settings manually !", Toast.LENGTH_SHORT).show();
                    cameraSwitch.setChecked(true);
                }
            }
        });

    }

    private void init(){
        showVibrationTime = (TextView)findViewById(R.id.show_vibration_time);
        showSendDurationTime = (TextView)findViewById(R.id.show_send_duration_time);
        smsSwitch = (Switch)findViewById(R.id.sms_switch);
        locationSwitch = (Switch)findViewById(R.id.location_switch);
        audioSwitch = (Switch)findViewById(R.id.audio_switch);
        cameraSwitch = (Switch)findViewById(R.id.camera_switch);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        database = openOrCreateDatabase("Rescue", Context.MODE_PRIVATE, null);

        init();

        retriveFromDB();

        handlePermissions();
    }

    public void logoutUser(View view) {

        if(mAuth.getCurrentUser() != null){
            mAuth.signOut();
        }
        Intent optionsActivityIntent = new Intent(getApplicationContext(), OptionsActivity.class);

        startActivity(optionsActivityIntent);

        finish();
    }

    public void setDurations(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        final EditText setVibration = (EditText)mView.findViewById(R.id.dialog_set_vibration);
        final EditText setSendDuration = (EditText)mView.findViewById(R.id.dialog_set_send_duration);
        Button setBtn = (Button)mView.findViewById(R.id.dialog_set_btn);
        Button cancelBtn = (Button)mView.findViewById(R.id.dialog_cancel_btn);
        builder.setView(mView);

        final AlertDialog dialog = builder.create();
        dialog.show();

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vibrationTime = setVibration.getText().toString().trim();
                String sendDurationTime = setSendDuration.getText().toString().trim();

                if(vibrationTime.matches("") || sendDurationTime.matches("")){
                    Toast.makeText(SettingsActivity.this, "Please fill all the input fields !", Toast.LENGTH_SHORT).show();
                }else{
                    saveToDB(vibrationTime, sendDurationTime);
                    retriveFromDB();
                    dialog.dismiss();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void saveToDB(String vibrationTime, String sendDurationTime) {
        database.execSQL("CREATE TABLE IF NOT EXISTS rescuedurations(vibrationtime varchar not null, senddurationtime varchar not null);");

        Cursor cursor = database.rawQuery("SELECT * FROM rescuedurations", null);

        if(cursor.getCount() == 0){

            database.execSQL("INSERT INTO rescuedurations VALUES(\""+vibrationTime+"\", \""+sendDurationTime+"\");");

        }else{
            database.execSQL("DELETE FROM rescuedurations");
            database.execSQL("INSERT INTO rescuedurations VALUES(\""+vibrationTime+"\", \""+sendDurationTime+"\");");
            Toast.makeText(this, "SAVED !", Toast.LENGTH_SHORT).show();
        }
    }

    private void retriveFromDB(){
        database.execSQL("CREATE TABLE IF NOT EXISTS rescuedurations(vibrationtime varchar not null, senddurationtime varchar not null);");

        Cursor cursor = database.rawQuery("SELECT * FROM rescuedurations", null);
        try{
            int i = 0;
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                String vTime = "";
                String sTime = "";
                do {
                    vTime = cursor.getString(cursor.getColumnIndex("vibrationtime"));
                    sTime = cursor.getString(cursor.getColumnIndex("senddurationtime"));


                    i++;
                } while (cursor.moveToNext());
                cursor.close();
                showVibrationTime.setText(vTime);
                showSendDurationTime.setText(sTime);
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_SMS){
            if(grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "ENABLED !", Toast.LENGTH_SHORT).show();
                smsSwitch.setChecked(true);
            }else{
                Toast.makeText(this, "You do not have the required the permission.", Toast.LENGTH_SHORT).show();
                smsSwitch.setChecked(false);
            }
        }

        if(requestCode == REQUEST_CODE_LOCATION){
            if(grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "ENABLED !", Toast.LENGTH_SHORT).show();
                locationSwitch.setChecked(true);
            }else{
                Toast.makeText(this, "You do not have the required the permission.", Toast.LENGTH_SHORT).show();
                locationSwitch.setChecked(false);
            }
        }

        if(requestCode == REQUEST_CODE_AUDIO){
            if(grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "ENABLED !", Toast.LENGTH_SHORT).show();
                audioSwitch.setChecked(true);
            }else{
                Toast.makeText(this, "You do not have the required the permission.", Toast.LENGTH_SHORT).show();
                audioSwitch.setChecked(false);
            }
        }

        if(requestCode == REQUEST_CODE_CAMERA){
            if(grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "ENABLED !", Toast.LENGTH_SHORT).show();
                cameraSwitch.setChecked(true);
            }else{
                Toast.makeText(this, "You do not have the required the permission.", Toast.LENGTH_SHORT).show();
                locationSwitch.setChecked(false);
            }
        }
    }
}

