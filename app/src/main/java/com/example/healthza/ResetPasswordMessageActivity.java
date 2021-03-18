package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ResetPasswordMessageActivity extends AppCompatActivity {

    private Button goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_message);

        goToLogin = findViewById(R.id.goToLoginButton);
        setOnClickListenersForButtons();
    }

    /* set on click listeners for Buttons to navigate to other activity */
    private void setOnClickListenersForButtons()
    {
        goToLogin.setOnClickListener(view ->
                startActivity(new Intent(this, LoginActivity.class)));

    }

}