package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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

}