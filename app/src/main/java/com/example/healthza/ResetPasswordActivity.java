package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText passwordInputEditText, confirmPasswordInputEditText;
    private Button resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetPassword = findViewById(R.id.resetPasswordButton);
        passwordInputEditText = findViewById(R.id.passwordInputEditText);
        confirmPasswordInputEditText = findViewById(R.id.confirmPasswordInputEditText);


        TextInputEditTextFocusListenerHelper.add(this, passwordInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, confirmPasswordInputEditText);


        setOnClickListenersForButtons();
    }


    /* set on click listeners for Buttons to navigate to other activity */
    private void setOnClickListenersForButtons()
    {
        resetPassword.setOnClickListener(view ->
                startActivity(new Intent(this, ResetPasswordMessageActivity.class)));

    }

}