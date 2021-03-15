package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.healthza.R;

public class WelcomeActivity extends AppCompatActivity {

    Button login, register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        login = findViewById(R.id.signInButton);
        register = findViewById(R.id.signUpButton);


        login.setOnClickListener(view ->
                startActivity(new Intent(this, LoginActivity.class)));


        register.setOnClickListener(view ->
                startActivity(new Intent(this, RegisterActivity.class)));

    }
}