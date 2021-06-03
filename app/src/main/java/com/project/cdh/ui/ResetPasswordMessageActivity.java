package com.project.cdh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.project.cdh.R;

public class ResetPasswordMessageActivity extends AppCompatActivity {

    private Button goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_message);

        goToLogin = findViewById(R.id.goToLoginButton);
        setOnClickListenersForButtons();
    }

    /* set on click listeners for Buttons to navigate to other activity */
    private void setOnClickListenersForButtons() {
        goToLogin.setOnClickListener(view ->
                startActivity(new Intent(this, LoginActivity.class)));

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