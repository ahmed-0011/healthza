package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class CodeVerificationActivity extends AppCompatActivity {

    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        confirmButton = findViewById(R.id.confirmButton);
        setOnClickListenersForButtons();
    }

    /* set on click listeners for Buttons to navigate to other activity */
    private void setOnClickListenersForButtons()
    {
        confirmButton.setOnClickListener(view ->
                startActivity(new Intent(this, ResetPasswordActivity.class)));

    }

}