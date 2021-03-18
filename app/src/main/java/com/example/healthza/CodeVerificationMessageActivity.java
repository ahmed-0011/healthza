package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class CodeVerificationMessageActivity extends AppCompatActivity {

    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification_message);

        nextButton = findViewById(R.id.nextButton);
        setOnClickListenersForButtons();
    }


    /* set on click listeners for Buttons to navigate to other activity */
    private void setOnClickListenersForButtons()
    {
        nextButton.setOnClickListener(view ->
                startActivity(new Intent(this, CodeVerificationActivity.class)));

    }
}