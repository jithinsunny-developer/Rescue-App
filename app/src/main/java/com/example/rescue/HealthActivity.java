package com.example.rescue;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import Data.User;
import Data.UserHealth;

public class HealthActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);


    }


}

/**
 * implements SensorEventListener
 *
 *
 *  builder = new AlertDialog.Builder(HealthActivity.this);
 *         builder.setCancelable(false); // if you want user to wait for some process to finish,
 *         builder.setView(R.layout.layout_loading_dialog);
 *         dialog = builder.create();
 *
 *         init();
 *         mAuth = FirebaseAuth.getInstance();
 *
 *         currentUser = mAuth.getCurrentUser();
 *         firebaseDatabase = FirebaseDatabase.getInstance();
 *         databaseReference = firebaseDatabase.getReference().child("RescueUser");
 *         databaseReference2 = firebaseDatabase.getReference().child("RescueUserHealth");
 *
 *         sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
 *         sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
 *
 *         if(sensor == null){
 *             status.setVisibility(View.VISIBLE);
 *         }
 *
 *         if(currentUser != null){
 *             progessActivate(true);
 *             final String uid = currentUser.getUid();
 *             databaseReference.addValueEventListener(new ValueEventListener() {
 *                 @Override
 *                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
 *                     for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
 *                         User user = dataSnapshot1.getValue(User.class);
 *                         if(user.getPwd().matches(uid)){
 *                             progessActivate(false);
 *
 *                             String name = user.getUserName();
 *                             String dob = user.getDob();
 *                             String bloodGrp = user.getBloodGrp();
 *
 *                             int year = Calendar.getInstance().get(Calendar.YEAR);
 *                             String[] str = dob.split("/");
 *
 *                             int ageInt = year - Integer.parseInt(str[2]);
 *
 *                             userName.setText(name);
 *                             userAge.setText(ageInt+"");
 *                             userBloodGrp.setText(bloodGrp);
 *
 *
 *
 *                             getHealthDB();
 *
 *
 *                             break;
 *                         }
 *
 *                     }
 *                     progessActivate(false);
 *                 }
 *
 *                 @Override
 *                 public void onCancelled(@NonNull DatabaseError databaseError) {
 *                     progessActivate(false);
 *                     Toast.makeText(HealthActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
 *
 *                 }
 *             });
 *         }else{
 *             Toast.makeText(this, "No User Found", Toast.LENGTH_SHORT).show();
 *         }
 *
 *     private TextView userName, userAge, userBloodGrp, userHt, userWt, stepsWalk, caloriesBurnt, distanceCovered, status;
 *     private FirebaseAuth mAuth;
 *     private FirebaseUser currentUser;
 *     private FirebaseDatabase firebaseDatabase;
 *     private DatabaseReference databaseReference, databaseReference2;
 *
 *     AlertDialog.Builder builder;
 *     AlertDialog dialog;
 *
 *     private SensorManager sensorManager;
 *     private Sensor sensor;
 *     private long steps = 0;
 *     private void init() {
 *
 *         userName = (TextView)findViewById(R.id.health_user_name);
 *         userAge = (TextView)findViewById(R.id.health_user_age);
 *         userBloodGrp = (TextView)findViewById(R.id.health_user_blood_grp);
 *         userHt = (TextView)findViewById(R.id.health_user_height);
 *         userWt = (TextView)findViewById(R.id.health_user_weight);
 *         stepsWalk = (TextView)findViewById(R.id.health_steps_walk);
 *         caloriesBurnt = (TextView)findViewById(R.id.health_calories_burnt);
 *         distanceCovered = (TextView)findViewById(R.id.health_distance_covered);
 *         status = (TextView)findViewById(R.id.health_status_show);
 *     }
 *
 *
 *     public void setHealthHeightWeight(View view) {
 *         AlertDialog.Builder alertDialog = new AlertDialog.Builder(HealthActivity.this);
 *
 *         alertDialog.setTitle("Health");
 *
 *
 * //        alertDialog.setMessage("Enter Height in { X ft. YY in. }: ");
 *
 *         LinearLayout linearLayout = new LinearLayout(getApplicationContext());
 *         linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
 *
 *         linearLayout.setOrientation(LinearLayout.VERTICAL);
 *         final EditText input1 = new EditText(this);
 *         input1.setHint("Enter Height in { X ft. YY in. }");
 *
 *         linearLayout.addView(input1);
 *
 *
 * //        alertDialog.setMessage("Enter Weight in { XYZ kg }: ");
 *         final EditText input2 = new EditText(this);
 *         input2.setHint("Enter Weight in { XYZ kg }: ");
 *
 *
 *         linearLayout.addView(input2);
 *
 *         alertDialog.setView(linearLayout);
 *
 *
 *         alertDialog.setPositiveButton("Set",
 *                 new DialogInterface.OnClickListener() {
 *                     public void onClick(DialogInterface dialog,int which) {
 *
 *                         if(currentUser != null){
 *                             final String email = currentUser.getEmail();
 *                             String in2 = input2.getText().toString();
 *                             String in1 = input1.getText().toString();
 *
 *                             if(in2 != null && in1 != null && email != null){
 *                                 if(email.matches("") || in1.matches("") || in2.matches("")){
 *                                     Toast.makeText(HealthActivity.this, "Please fill the inputs", Toast.LENGTH_SHORT).show();
 *                                 }else{
 *                                     progessActivate(true);
 *                                     Query query = databaseReference2.orderByChild("userEmail").equalTo(email);
 *                                     query.addListenerForSingleValueEvent(new ValueEventListener() {
 *                                         @Override
 *                                         public void onDataChange(DataSnapshot dataSnapshot) {
 *
 *                                             for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
 *                                                 appleSnapshot.getRef().removeValue();
 *                                                 progessActivate(false);
 *
 *                                             }
 *                                             progessActivate(false);
 *                                         }
 *
 *                                         @Override
 *                                         public void onCancelled(DatabaseError databaseError) {
 *                                             progessActivate(false);
 *                                         }
 *                                     });
 *
 *                                     progessActivate(true);
 *                                     databaseReference2.child(databaseReference2.push().getKey()).setValue(new UserHealth(email, in1, in2), new DatabaseReference.CompletionListener() {
 *                                         @Override
 *                                         public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
 *                                             progessActivate(false);
 *                                             if(databaseError == null){
 *                                                 Toast.makeText(HealthActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
 *
 *                                                 getHealthDB();
 *                                             }else{
 *                                                 Toast.makeText(HealthActivity.this, "Not Saved!", Toast.LENGTH_SHORT).show();
 *                                             }
 *                                         }
 *                                     });
 *                                 }
 *                             }
 *                         }
 *
 *                     }
 *                 });
 *         // Setting Negative "NO" Button
 *         alertDialog.setNegativeButton("Cancel",
 *                 new DialogInterface.OnClickListener() {
 *                     public void onClick(DialogInterface dialog, int which) {
 *
 *                         dialog.cancel();
 *                     }
 *                 });
 *         alertDialog.show();
 *     }
 *
 *     @Override
 *     public void onSensorChanged(SensorEvent event) {
 *         Sensor sensor = event.sensor;
 *         if(sensor != null){
 *             float[] values = event.values;
 *             int value = -1;
 *
 *             if (values.length > 0) {
 *                 value = (int) values[0];
 *             }
 *
 *
 *             if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
 *                 steps++;
 *                 stepsWalk.setText(steps+"");
 *                 calculateDistanceCovered(steps);
 *             }
 *         }
 *     }
 *
 *     @Override
 *     public void onAccuracyChanged(Sensor sensor, int accuracy) {
 *
 *     }
 *
 *     @Override
 *     protected void onResume() {
 *         super.onResume();
 *         if(sensor != null){
 *             sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
 *         }
 *     }
 *
 *     @Override
 *     protected void onStop() {
 *         super.onStop();
 *         if(sensor != null){
 *             sensorManager.unregisterListener(this, sensor);
 *         }
 *     }
 *
 *     private void calculateDistanceCovered(long step){
 *         float distance = (float)(steps*78)/(float)100000;
 *         distanceCovered.setText(distance+" KM");
 *     }
 *
 * private void progessActivate(boolean key){
 *
 *         if(key){
 *             dialog.show();
 *         }else{
 *             dialog.cancel();
 *         }
 *     }
 *
 *     private void getHealthDB(){
 *         if(currentUser != null){
 *             final String email = currentUser.getEmail();
 *
 *             progessActivate(true);
 *
 *
 *
 *             databaseReference2.addValueEventListener(new ValueEventListener() {
 *                 @Override
 *                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
 *                     for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
 *                         UserHealth health = dataSnapshot1.getValue(UserHealth.class);
 *                         if(health.getUserEmail().matches(email)){
 *                             progessActivate(false);
 *
 *                             String h = health.getUserHeight();
 *                             String w = health.getUserWeight();
 *
 *                             userHt.setText(h);
 *                             userWt.setText(w);
 *
 *                             break;
 *
 *
 *                         }
 *                     }
 *                     progessActivate(false);
 *                 }
 *
 *                 @Override
 *                 public void onCancelled(@NonNull DatabaseError databaseError) {
 *                     progessActivate(false);
 *                 }
 *             });
 *         }
 *     }
 *
 * <ScrollView
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent"
 *         android:layout_below="@id/health_toolbar">
 *         <LinearLayout
 *             android:layout_width="match_parent"
 *             android:layout_height="match_parent"
 *             android:orientation="vertical"
 *             android:padding="20dp">
 *
 *             <LinearLayout
 *                 android:layout_width="match_parent"
 *                 android:layout_height="wrap_content"
 *                 android:orientation="vertical"
 *                 android:background="@drawable/background8"
 *                 android:padding="20dp"
 *                 android:gravity="center_horizontal">
 *                 <TextView
 *                     android:layout_width="wrap_content"
 *                     android:layout_height="wrap_content"
 *                     android:text=""
 *                     android:textColor="@android:color/holo_orange_light"
 *                     android:fontFamily="@font/ccalibri"
 *                     android:textSize="20sp"
 *                     android:id="@+id/health_user_name"/>
 *                 <LinearLayout
 *                     android:layout_width="wrap_content"
 *                     android:layout_height="wrap_content"
 *                     android:orientation="horizontal">
 *                     <TextView
 *                         android:layout_width="wrap_content"
 *                         android:layout_height="wrap_content"
 *                         android:text="AGE: "
 *                         android:textColor="@color/color1"
 *                         android:fontFamily="@font/calibrib"
 *                         android:textSize="18sp"/>
 *                     <TextView
 *                         android:layout_width="wrap_content"
 *                         android:layout_height="wrap_content"
 *                         android:text=""
 *                         android:textColor="@android:color/holo_orange_light"
 *                         android:fontFamily="@font/ccalibri"
 *                         android:textSize="25sp"
 *                         android:id="@+id/health_user_age"/>
 *                 </LinearLayout>
 *                 <LinearLayout
 *                     android:layout_width="wrap_content"
 *                     android:layout_height="wrap_content"
 *                     android:orientation="horizontal">
 *                     <TextView
 *                         android:layout_width="wrap_content"
 *                         android:layout_height="wrap_content"
 *                         android:text="BLOOD GROUP: "
 *                         android:textColor="@color/color1"
 *                         android:fontFamily="@font/calibrib"
 *                         android:textSize="18sp"/>
 *                     <TextView
 *                         android:layout_width="wrap_content"
 *                         android:layout_height="wrap_content"
 *                         android:text=""
 *                         android:textColor="@android:color/holo_orange_light"
 *                         android:fontFamily="@font/ccalibri"
 *                         android:textSize="25sp"
 *                         android:id="@+id/health_user_blood_grp"/>
 *                 </LinearLayout>
 *
 *             </LinearLayout>
 *             <LinearLayout
 *                 android:layout_width="match_parent"
 *                 android:layout_height="wrap_content"
 *                 android:orientation="horizontal"
 *                 android:padding="20dp"
 *                 android:background="@drawable/background8"
 *                 android:layout_marginTop="10dp"
 *                 android:layout_marginBottom="10dp"
 *                 android:gravity="center">
 *                 <TextView
 *                     android:layout_width="wrap_content"
 *                     android:layout_height="wrap_content"
 *                     android:textColor="@color/color1"
 *                     android:textSize="40sp"
 *                     android:text="H:"
 *                     android:fontFamily="@font/calibrib"
 *                     android:layout_weight="1"/>
 *                 <TextView
 *                     android:layout_width="wrap_content"
 *                     android:layout_height="wrap_content"
 *                     android:textColor="@android:color/holo_orange_light"
 *                     android:textSize="20sp"
 *                     android:text=""
 *                     android:fontFamily="@font/ccalibri"
 *                     android:layout_weight="1"
 *                     android:id="@+id/health_user_height"/>
 *
 *
 *                 <TextView
 *                     android:layout_width="wrap_content"
 *                     android:layout_height="wrap_content"
 *                     android:textColor="@color/color1"
 *                     android:textSize="40sp"
 *                     android:text="W:"
 *                     android:fontFamily="@font/calibrib"
 *                     android:layout_weight="1"/>
 *                 <TextView
 *                     android:layout_width="wrap_content"
 *                     android:layout_height="wrap_content"
 *                     android:textColor="@android:color/holo_orange_light"
 *                     android:textSize="20sp"
 *                     android:text=""
 *                     android:fontFamily="@font/ccalibri"
 *                     android:layout_weight="1"
 *                     android:id="@+id/health_user_weight"/>
 *
 *                 <ImageButton
 *                     android:layout_width="wrap_content"
 *                     android:layout_height="wrap_content"
 *                     android:src="@drawable/ic_report_black_24dp"
 *                     android:background="@android:color/transparent"
 *                     android:onClick="setHealthHeightWeight"/>
 *
 *             </LinearLayout>
 *
 *             <LinearLayout
 *                 android:layout_width="match_parent"
 *                 android:layout_height="wrap_content"
 *                 android:padding="40dp"
 *                 android:background="@drawable/background8"
 *                 android:orientation="horizontal"
 *                 android:gravity="center">
 *
 *                 <LinearLayout
 *                     android:layout_width="wrap_content"
 *                     android:layout_height="wrap_content"
 *                     android:orientation="vertical"
 *                     android:padding="10dp"
 *                     android:gravity="center"
 *                     android:layout_weight="1">
 *                     <TextView
 *                         android:layout_width="wrap_content"
 *                         android:layout_height="wrap_content"
 *                         android:textColor="@color/color1"
 *                         android:textSize="20dp"
 *                         android:text="Steps Walk: "
 *                         android:fontFamily="@font/calibrib"/>
 *                     <TextView
 *                         android:layout_width="wrap_content"
 *                         android:layout_height="wrap_content"
 *                         android:textColor="@android:color/holo_orange_light"
 *                         android:textSize="30dp"
 *                         android:text=""
 *                         android:fontFamily="@font/calibril"
 *                         android:id="@+id/health_steps_walk"/>
 *
 *                 </LinearLayout>
 *
 *                 <LinearLayout
 *                     android:layout_width="wrap_content"
 *                     android:layout_height="wrap_content"
 *                     android:orientation="vertical"
 *                     android:layout_weight="1"
 *                     android:gravity="center">
 *                     <TextView
 *                         android:layout_width="wrap_content"
 *                         android:layout_height="wrap_content"
 *                         android:textSize="15sp"
 *                         android:textColor="@color/color1"
 *                         android:fontFamily="@font/calibrib"
 *                         android:text="Calories Burnt:"/>
 *                     <TextView
 *                         android:layout_width="wrap_content"
 *                         android:layout_height="wrap_content"
 *                         android:textSize="18sp"
 *                         android:textColor="@android:color/holo_orange_light"
 *                         android:fontFamily="@font/calibrib"
 *                         android:text=""
 *                         android:id="@+id/health_calories_burnt"/>
 *                     <TextView
 *                         android:layout_width="wrap_content"
 *                         android:layout_height="wrap_content"
 *                         android:textSize="15sp"
 *                         android:textColor="@color/color1"
 *                         android:fontFamily="@font/calibrib"
 *                         android:text="Distance Covered:"/>
 *                     <TextView
 *                         android:layout_width="wrap_content"
 *                         android:layout_height="wrap_content"
 *                         android:textSize="18sp"
 *                         android:textColor="@android:color/holo_orange_light"
 *                         android:fontFamily="@font/calibrib"
 *                         android:text=""
 *                         android:id="@+id/health_distance_covered"/>
 *                 </LinearLayout>
 *
 *
 *
 *             </LinearLayout>
 *
 *             <TextView
 *                 android:layout_width="wrap_content"
 *                 android:layout_height="wrap_content"
 *                 android:textSize="20sp"
 *                 android:textColor="@android:color/holo_red_light"
 *                 android:text="Required Sensor not Supported !"
 *                 android:layout_gravity="center_horizontal"
 *                 android:fontFamily="@font/ccalibri"
 *                 android:layout_margin="10dp"
 *                 android:id="@+id/health_status_show"
 *                 android:visibility="invisible"/>
 *
 *
 *         </LinearLayout>
 *     </ScrollView>
 * */

