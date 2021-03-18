package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button forgotPasswordButton;
    private TextInputEditText emailInputEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        emailInputEditText = findViewById(R.id.emailInputEditText);

        /*Class TextInputEditTextFocusListenerHelper has add() function
         which will set focus listener for TextInputEditText to change start and end icon color tint*/
        TextInputEditTextFocusListenerHelper.add(this, emailInputEditText);


        setOnClickListenersForButtons();
    }


    /* set on click listeners for Buttons to navigate to other activity */
    private void setOnClickListenersForButtons()
    {
        forgotPasswordButton.setOnClickListener(view ->
            startActivity(new Intent(this, CodeVerificationMessageActivity.class)));

    }

}