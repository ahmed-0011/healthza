package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.resources.MaterialResources;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private Button alreadyHaveAccountButton;
    private TextInputEditText nameInputEditText, emailInputEditText, phoneNumberInputEditText,
                              passwordInputEditText, confirmPasswordInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInputEditText = findViewById(R.id.nameInputEditText);
        emailInputEditText = findViewById(R.id.emailInputEditText);
        phoneNumberInputEditText = findViewById(R.id.phoneNumberInputEditText);
        passwordInputEditText = findViewById(R.id.passwordInputEditText);
        confirmPasswordInputEditText = findViewById(R.id.confirmPasswordInputEditText);
        alreadyHaveAccountButton = findViewById(R.id.alreadyHaveAccount);


        /*Class TextInputEditTextFocusListenerHelper has add() function
         which will set focus listener for TextInputEditText to change start and end icon color tint*/
        TextInputEditTextFocusListenerHelper.add(this, nameInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, emailInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, phoneNumberInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, passwordInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, confirmPasswordInputEditText);


        setOnClickListenersForButtons();
    }
    

    /* set on click listeners for Buttons to navigate to other activity */
    private void setOnClickListenersForButtons()
    {
        alreadyHaveAccountButton.setOnClickListener(view ->
                startActivity(new Intent(this, LoginActivity.class)));
    }




}