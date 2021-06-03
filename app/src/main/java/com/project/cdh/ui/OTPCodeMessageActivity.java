package com.project.cdh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.project.cdh.R;

public class OTPCodeMessageActivity extends AppCompatActivity {

    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_code_message);

        nextButton = findViewById(R.id.nextButton);


        /* set on click listeners for Button to navigate to other activity */
        nextButton.setOnClickListener(view ->
                startActivity(new Intent(this, OTPCodeActivity.class)));
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