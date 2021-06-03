package com.project.cdh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.project.cdh.R;

public class OTPCodeActivity extends AppCompatActivity {

    private Button confirmButton;
    private PinView otpCodePinView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_code);

        confirmButton = findViewById(R.id.confirmButton);
        otpCodePinView = findViewById(R.id.otpCodePinView);



        /* set on click listeners for Button to navigate to other activity */
        confirmButton.setOnClickListener(v -> {

            /* if opt is incorrect show dialog */
            Toast.makeText(OTPCodeActivity.this, otpCodePinView.getText(), Toast.LENGTH_LONG);

            /* if otp is correct navigate to next activity */
            startActivity(new Intent(this, ResetPasswordActivity.class));
        });
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