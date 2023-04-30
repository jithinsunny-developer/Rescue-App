package com.example.rescue;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AlarmActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private int vibrationTime, sendDurationTime;


    private TextView alarmCounterShow;
    private Button sendQuicklyBtn;
    private Button cancelSendSMSBtn;
    private RelativeLayout parentLayout;


    private SQLiteDatabase database;

    private FirebaseAuth mAuth;

    private Location location;


    private int REQUEST_CODE_AUDIO = 3788;
    private int REQUEST_CODE_CAMERA = 3789;

    MediaRecorder mediaRecorder1;
    SurfaceHolder surfaceHolder;
    SurfaceView videoSurfaceVeiw;
    private void startVideo(){
        mediaRecorder1.start();
    }
    private void cancelVideo(){
        mediaRecorder1.stop();
        mediaRecorder1.release();
    }

    private void videoRecord(){


        mediaRecorder1 = new MediaRecorder();
        initMediaRecorder();


        surfaceHolder = videoSurfaceVeiw.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);



    }



    private String[] retriveTimesFromDB() {
        database.execSQL("CREATE TABLE IF NOT EXISTS rescuedurations(vibrationtime varchar not null, senddurationtime varchar not null);");

        Cursor cursor = database.rawQuery("SELECT * FROM rescuedurations", null);
        try {
            int i = 0;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String vTime = "";
                String sTime = "";
                do {
                    vTime = cursor.getString(cursor.getColumnIndex("vibrationtime"));
                    sTime = cursor.getString(cursor.getColumnIndex("senddurationtime"));


                    i++;
                } while (cursor.moveToNext());
                cursor.close();
                String[] str = {vTime, sTime};
                return str;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);



        mAuth = FirebaseAuth.getInstance();
        initializeViews();
        database = openOrCreateDatabase("Rescue", Context.MODE_PRIVATE, null);

        ///VIDEO CHECK
        int perm1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int perm2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        int perm3 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);

        if(perm1 == PackageManager.PERMISSION_GRANTED && perm2 == PackageManager.PERMISSION_GRANTED && perm3 == PackageManager.PERMISSION_GRANTED){
            videoRecord();

        }



        //GET Times From DB
        String[] str = retriveTimesFromDB();
        if (str != null) {
            if (!(str[0].matches("") || str[1].matches(""))) {
                vibrationTime = Integer.parseInt(str[0]);
                sendDurationTime = Integer.parseInt(str[1]);

                Log.i("SEND DURATION: ", sendDurationTime + "");

                int sendDurationTimeInMilli = sendDurationTime * 1000;

                //CREATE COUNTER
                final CountDownTimer sendDurationTimer = new CountDownTimer(sendDurationTimeInMilli, 1000) {

                    public void onTick(long millisUntilFinished) {
                        long val = millisUntilFinished / 1000;
                        if (val % 2 == 0) {
                            parentLayout.setBackgroundColor(Color.RED);
                            alarmCounterShow.setTextColor(Color.RED);
                        } else {
                            parentLayout.setBackgroundColor(Color.GREEN);
                            alarmCounterShow.setTextColor(Color.GREEN);
                        }
                        alarmCounterShow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80);

                        alarmCounterShow.setText("" + val);
                        //here you can have your logic to set text to edittext

                    }

                    public void onFinish() {
                        alarmCounterShow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        alarmCounterShow.setTextColor(Color.GREEN);
                        alarmCounterShow.setText("Sending ...");
                        Log.i("SENDING::::", "Srv");
                        sendSmsToContacts();

                        start();

                    }

                };

                //start SEND DURATION count
                sendDurationTimer.start();

                //cancel alarm and sending sms process
                cancelSendSMSBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDurationTimer.cancel();
                        alarmCounterShow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        alarmCounterShow.setText("Canceled!");

                        ///VIDEO CHECK
                        int perm1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        int perm2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
                        int perm3 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);

                        if(perm1 == PackageManager.PERMISSION_GRANTED && perm2 == PackageManager.PERMISSION_GRANTED && perm3 == PackageManager.PERMISSION_GRANTED){
                            try{
                                cancelVideo();
                            }catch (Exception e){

                            }

                        }

                        AlarmActivity.this.finish();

                    }
                });

                sendQuicklyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendQuicklyBtn.setText("Sending...");
                        sendSmsToContacts();

                        sendDurationTimer.start();
                    }
                });
            }
        }


    }

    private void initializeViews() {
        alarmCounterShow = (TextView) findViewById(R.id.alarm_counter_show);
        sendQuicklyBtn = (Button) findViewById(R.id.send_quickly_btn);
        cancelSendSMSBtn = (Button) findViewById(R.id.cancel_send_sms_btn);
        parentLayout = (RelativeLayout) findViewById(R.id.parent_layout);
        videoSurfaceVeiw = (SurfaceView)findViewById(R.id.video_surface_view);
    }


    private ArrayList<String> fetchContactsFromDB() {

        database.execSQL("CREATE TABLE IF NOT EXISTS rescuecontacts(name varchar not null, mobile varchar not null);");
        Cursor cursor = database.rawQuery("SELECT mobile FROM rescuecontacts", null);
        ArrayList<String> mobiles = new ArrayList<>();
        int i = 0;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                mobiles.add(cursor.getString(cursor.getColumnIndex("mobile")));
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        }
        return mobiles;
    }

    private void sendSmsToContacts() {


        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.SEND_SMS);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            getLocMessage();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 3786);
        }


    }

    private void sendMessage(String message) {
        if (mAuth.getCurrentUser() != null) {
            String newMsg = "Hey! I am in an emergency situation and I require immediate help.\n Current Location: \n";
//            newMsg += message;
            ArrayList<String> mobiles = fetchContactsFromDB();
            if (mobiles != null && mobiles.size() > 0) {
                for (String mobile : mobiles) {
                    String temp = mobile.trim();
                    String finalMobile;
                    if (temp.contains("+91")) {
                        String s = temp.substring(3);
                        finalMobile = s;
                    } else if (temp.contains("+91 ")) {
                        String s = temp.split(" ")[1];
                        finalMobile = s;
                    } else {
                        String s = temp;
                        finalMobile = s;
                    }
                    Log.i("NUM", finalMobile);
                    Log.i("SEND INFO", newMsg);
                    SmsManager smsManager = SmsManager.getDefault();

                    smsManager.sendTextMessage(finalMobile.trim(), null, newMsg, null, null);
                    smsManager.sendTextMessage(finalMobile.trim(), null, message, null, null);
                    Toast.makeText(this, "Send! to"+finalMobile, Toast.LENGTH_SHORT).show();
                    sendQuicklyBtn.setText("Send Quickly");

                }

                alarmCounterShow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                alarmCounterShow.setText("Send!");
            }
        } else {
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

    }

    private void getLocMessage() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AlarmActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3787);

        } else {
            getLoc();
        }

    }

    private void getLoc() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    String locationLink = "https://maps.google.com/?q="+lat+","+lon;


                    try {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
//                        String city = addresses.get(0).getLocality();
//                        String state = addresses.get(0).getAdminArea();
//                        String zip = addresses.get(0).getPostalCode();
//                        String country = addresses.get(0).getCountryName();

                        String msg = addresses.get(0).getAddressLine(0);
                        msg += ("\n"+locationLink);
//                        Log.i("LOCATION: ", msg);
                        sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("LOCATION: ", locationLink);
                        sendMessage(locationLink);
                    }


                } else {
                    Log.i("LOCATION: ", "NOT FOUND !");
                    sendMessage("Location Not Found!");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        try{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.


            }
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
        }catch (Exception e){}



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 3786){
            if(grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                sendMessage();
                getLocMessage();
            }else{
                Toast.makeText(this, "You do not have the required permission", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == 3787){
            if(grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLoc();
            }else{
                Toast.makeText(this, "You do not have the required permission", Toast.LENGTH_SHORT).show();

            }
        }


    }
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        prepareMediaRecorder();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    private void initMediaRecorder(){
        mediaRecorder1.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder1.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        CamcorderProfile camcorderProfile_HQ = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mediaRecorder1.setProfile(camcorderProfile_HQ);

        File folder = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()+"/RescueVideos/");
        boolean success = false;
        if(folder.exists()){
            success = true;
        }else{
            success = folder.mkdir();

        }
        if(success){
            String pathSave = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()+"/RescueVideos/"
                    + UUID.randomUUID().toString()+"_video_record.mp4";
            mediaRecorder1.setOutputFile(pathSave);
        }else{
            mediaRecorder1.setOutputFile("noname.mp4");
        }

//        mediaRecorder.setMaxDuration(60000); // Set max duration 60 sec.
//        mediaRecorder.setMaxFileSize(5000000); // Set max file size 5M
    }

    private void prepareMediaRecorder(){
        mediaRecorder1.setPreviewDisplay(surfaceHolder.getSurface());
        try {
            mediaRecorder1.prepare();
            startVideo();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}

/**
 * <android.support.v7.widget.Toolbar
 *         android:id="@+id/toolbar"
 *         android:minHeight="?attr/actionBarSize"
 *         android:layout_width="match_parent"
 *         android:layout_height="wrap_content"
 *         android:background="@android:color/transparent">
 *
 *         <LinearLayout
 *             android:layout_width="match_parent"
 *             android:layout_height="wrap_content"
 *             android:orientation="horizontal"
 *             android:gravity="center">
 *             <TextView
 *                 android:layout_width="wrap_content"
 *                 android:layout_height="wrap_content"
 *                 android:text="Rescue"
 *                 android:id="@+id/toolbar_title"
 *                 android:fontFamily="@font/star1"
 *                 android:textSize="20sp"
 *                 android:textColor="@color/color0"/>
 *
 *         </LinearLayout>
 *
 *     </android.support.v7.widget.Toolbar>
 *
 *     <LinearLayout
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent"
 *         android:orientation="vertical"
 *         android:gravity="center">
 *
 *         <LinearLayout
 *             android:layout_width="match_parent"
 *             android:layout_height="wrap_content"
 *             android:orientation="horizontal"
 *             android:background="@color/color2"
 *             android:padding="20dp"
 *             android:gravity="center">
 *             <TextView
 *                 android:layout_width="match_parent"
 *                 android:layout_height="wrap_content"
 *                 android:text="60"
 *                 android:textSize="80sp"
 *                 android:textColor="@color/color3"
 *                 android:fontFamily="@font/calibrib"
 *                 android:textStyle="italic"
 *                 android:gravity="center"
 *                 android:id="@+id/alarm_counter_show"/>
 *         </LinearLayout>
 *         <Button
 *             android:layout_width="200dp"
 *             android:layout_height="40dp"
 *             android:background="@drawable/round_background0"
 *             android:text="SOS"
 *             android:layout_margin="40dp"
 *             android:id="@+id/sos_btn"/>
 *
 *         <ImageButton
 *             android:layout_width="100dp"
 *             android:layout_height="100dp"
 *             android:src="@drawable/ic_cancel_black_24dp"
 *             android:background="@drawable/round_background4"
 *             android:scaleType="fitCenter"
 *             android:id="@+id/cancel_send_sms_btn"/>
 *     </LinearLayout>
 * */


///////////////////////////////////////////////////////////////////
//private void setupMediaRecorder(){
//    mediaRecorder = new MediaRecorder();
//    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//    mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
//    mediaRecorder.setOutputFile(pathRecordSave);
//
//}
//
//    private void audioRecord(){
//        if(
//                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED
//                        && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
//        ){
//            audioRecordSart();
//        }else{
//            ActivityCompat.requestPermissions(AlarmActivity.this,
//                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    REQUEST_CODE_AUDIO);
//        }
//    }
//
//    private void audioRecordSart(){
//        audioRecordFolder = new File(Environment.getExternalStorageDirectory()
//                .getAbsolutePath()+"/RescueAudio/");
//        boolean success = false;
//        if(audioRecordFolder.exists()){
//            success = true;
//        }else{
//            success = audioRecordFolder.mkdir();
//
//        }
//
//        if(success){
//            pathRecordSave = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath()+"/RescueAudio/"
//                    + UUID.randomUUID().toString()+"_rescue_audio.3gp";
//            setupMediaRecorder();
//            try{
//
//                mediaRecorder.prepare();
//                mediaRecorder.start();
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//
//
//            Toast.makeText(AlarmActivity.this, "Recording ...", Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(AlarmActivity.this, "Failed - error", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void audioRecordStop(){
//        mediaRecorder.stop();
//
//    }
//
//if(requestCode == REQUEST_CODE_AUDIO){
//        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//        /////////
//        audioRecordSart();
//        }else{
//        Toast.makeText(this, "You Do'nt Have Required Permissions", Toast.LENGTH_SHORT).show();
//        }
//        }
//
//private int REQUEST_CODE_AUDIO = 3788;
//    String pathRecordSave = "";
//    MediaRecorder mediaRecorder;
//    MediaPlayer mediaPlayer;
//    File audioRecordFolder;

