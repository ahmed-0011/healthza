package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText passwordInputEditText, confirmPasswordInputEditText;
    private Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        /* Buttons */
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        passwordInputEditText = findViewById(R.id.passwordInputEditText);

        /* InputEditText */
        confirmPasswordInputEditText = findViewById(R.id.confirmPasswordInputEditText);


         /*Class TextInputEditTextFocusListenerHelper has add() function
         which will set focus listener for TextInputEditText to change start and end icon color tint*/
        TextInputEditTextFocusListenerHelper.add(this, passwordInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, confirmPasswordInputEditText);

        resetPasswordButton.setOnClickListener(view ->
                startActivity(new Intent(this, ResetPasswordMessageActivity.class)));
    }




}