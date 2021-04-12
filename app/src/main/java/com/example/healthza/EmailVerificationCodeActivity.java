package com.example.healthza;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;

public class EmailVerificationCodeActivity extends AppCompatActivity {

    PinView verificationCode;
    private Button verifyButton;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification_code);


        userType = getIntent().getStringExtra("userType");

        verificationCode = findViewById(R.id.emailVerificationCodePinView);
        /* Button */
        verifyButton = findViewById(R.id.verifyButton);


        /* set on click listeners for Button to navigate to other activity */
        verifyButton.setOnClickListener(view -> {

            Intent intent;

            if (userType.equals("patient"))
                intent = new Intent(this, PatientHomeActivity.class);
            else
                intent = new Intent(this, MainActivity.class);

            intent.putExtra("Flagre",true);
            startActivity(intent);
        });
    }

    //rotate
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.i(COMMON_TAG,"MainActivity onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //  Log.i(COMMON_TAG,"MainActivity onSaveInstanceState");
    }
}