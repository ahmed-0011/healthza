package com.example.healthza.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthza.R;
import com.example.healthza.ui.LoginActivity;
import com.example.healthza.ui.RegisterActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button login, register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        login = findViewById(R.id.signInButton);
        register = findViewById(R.id.signUpButton);


        setOnClickListenersForButtons();
    }


    /* set on click listeners for Buttons to navigate to other activity */
    private void setOnClickListenersForButtons() {
        login.setOnClickListener(view ->
                startActivity(new Intent(this, LoginActivity.class)));

        register.setOnClickListener(view ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    //rotate
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.i(COMMON_TAG,"DoctorHomeActivity onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //  Log.i(COMMON_TAG,"DoctorHomeActivity onSaveInstanceState");
    }

}