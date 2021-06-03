package com.project.cdh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.project.cdh.R;
import com.project.cdh.TextInputEditTextFocusListenerHelper;
import com.google.android.material.textfield.TextInputEditText;

public class ResetPasswordActivity extends AppCompatActivity
{
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