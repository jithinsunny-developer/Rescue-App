package com.example.rescue;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, pwd;
    private FirebaseAuth mAuth;
    TextView loginStatus;

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
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.login_email);
        pwd = (EditText)findViewById(R.id.login_pwd);
        loginStatus = (TextView)findViewById(R.id.login_status);

        mAuth = FirebaseAuth.getInstance();

        builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        dialog = builder.create();
    }
    public void gotoRegistration(View view) {
        Intent registrationActivityIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(registrationActivityIntent);
    }

    public void gotoMainOptions(View view) {


        String e = email.getText().toString().trim();
        String p = pwd.getText().toString().trim();
        if(e.matches("") || p.matches("")){
            loginStatus.setText("Please fill all the input fields!");
        }else{
            progessActivate(true);

            mAuth.signInWithEmailAndPassword(e, p)
                    .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progessActivate(false);
                            Toast.makeText(LoginActivity.this, "Login Success :)", Toast.LENGTH_SHORT).show();
                            Intent mainOptionsActivityIntent = new Intent(getApplicationContext(), MainOptionsActivity.class);
                            startActivity(mainOptionsActivityIntent);
                            finish();
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progessActivate(false);
                            Toast.makeText(LoginActivity.this, "Login Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            loginStatus.setText(e.getMessage());
                        }
                    });
        }

    }
}

