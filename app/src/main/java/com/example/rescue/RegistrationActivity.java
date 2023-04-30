package com.example.rescue;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Data.User;
import dmax.dialog.SpotsDialog;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner bloodGrp;
    private EditText name, dob, phone, emergencyNum, medicalHistory, pwd, email;
    private RadioButton genderMale, genderFemale;
    public static final String[] bloods = {"A+", "O+", "B+", "AB+", "A-", "O-", "B-", "AB-"};
    private TextView status;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    AlertDialog.Builder builder;
    AlertDialog dialog;

    private void progessActivate(boolean key){

        if(key){
            dialog.show();
        }else{
            dialog.cancel();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("RescueUser");

        builder = new AlertDialog.Builder(RegistrationActivity.this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        dialog = builder.create();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, bloods);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGrp.setAdapter(adapter);
        bloodGrp.setOnItemSelectedListener(this);
    }

    private void sinupUser(final User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmai(), user.getPwd())
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if(user.getEmai().matches(authResult.getUser().getEmail())){
                            user.setEmai(authResult.getUser().getEmail());
                            user.setPwd(authResult.getUser().getUid());
                            database(user);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progessActivate(false);
                        Toast.makeText(RegistrationActivity.this, "Failed !"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        status.setText(e.getMessage());
                    }
                });
    }

    private void database(User user){

        databaseReference.child(databaseReference.push().getKey())
                .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progessActivate(false);
                Toast.makeText(RegistrationActivity.this, "Success :)", Toast.LENGTH_SHORT).show();
                ////
                Intent mainOptionsActivityIntent = new Intent(getApplicationContext(), MainOptionsActivity.class);
                startActivity(mainOptionsActivityIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progessActivate(false);
                Toast.makeText(RegistrationActivity.this, "Failed !"+e.getMessage(), Toast.LENGTH_SHORT).show();
                status.setText(e.getMessage());
                mAuth.signOut();
            }
        });
    }

    private void init(){
        bloodGrp = (Spinner)findViewById(R.id.register_blood_grp);
        genderMale = (RadioButton)findViewById(R.id.register_gender_male);
        genderFemale = (RadioButton)findViewById(R.id.register_gender_female);
        name = (EditText)findViewById(R.id.register_name);
        dob = (EditText)findViewById(R.id.register_dob);
        phone = (EditText)findViewById(R.id.register_phone);
        email = (EditText)findViewById(R.id.register_email);
        emergencyNum = (EditText)findViewById(R.id.register_emergency_contact);
        medicalHistory = (EditText)findViewById(R.id.register_medical_history);
        pwd = (EditText)findViewById(R.id.register_pwd);
        status = (TextView)findViewById(R.id.register_status);
    }

    private User fetchData(){

        String n = name.getText().toString().trim();
        String ph = phone.getText().toString().trim();
        String em = emergencyNum.getText().toString().trim();
        String d = dob.getText().toString().trim();
        String med = medicalHistory.getText().toString().trim();
        String pw = pwd.getText().toString().trim();
        String gender;
        String bl = bloodGrp.getSelectedItem().toString().trim();
        String e = email.getText().toString().trim();
        if(genderMale.isChecked()){
            gender = "male";
        }else if(genderFemale.isChecked()){
            gender = "female";
        }else{
            gender = "no gender";
        }

//        Log.i("DATA:::", n+" "+ph+" "+em+" "+d+" "+med+" "+pw+" "+gender+" "+bl);
        if(n.matches("") || ph.matches("") || em.matches("") || d.matches("") || med.matches("")
                || pw.matches("") || gender.matches("no gender") || e.matches("")){
//            Toast.makeText(this, "All fields all required", Toast.LENGTH_SHORT).show();
            status.setText("All fields are all required");
        }else{
            if(pw.length() < 6){
//                Toast.makeText(this, "Password length should be greater then or equal to 6", Toast.LENGTH_SHORT).show();
                status.setText("Password length should be greater than or equal to 6 characters");
            }else{
                status.setText("");
                User user = new User(n, d, gender, bl, ph, em, med, pw, e);
                return user;

            }
        }

        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                break;
            case 2:
                // Whatever you want to happen when the third item gets selected
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void gotoLogin(View view) {
        Intent loginActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginActivityIntent);
    }

    public void registerBtn(View view) {

        User user = fetchData();
        if(user != null){
            progessActivate(true);
            sinupUser(user);
//            database(user);
        }
    }


}
