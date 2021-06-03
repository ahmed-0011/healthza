package com.project.cdh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.cdh.R;
import com.project.cdh.TextInputEditTextFocusListenerHelper;
import com.project.cdh.Toasty;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button forgotPasswordButton;
    private TextInputEditText emailInputEditText;
    private TextInputLayout emailInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        emailInputEditText = findViewById(R.id.emailInputEditText);


        /*Class TextInputEditTextFocusListenerHelper has add() function
         which will set focus listener for TextInputEditText to change start and end icon color tint*/
        TextInputEditTextFocusListenerHelper.add(this, emailInputEditText);


        addOnTextChangeListenersForInputEditText();

        /* set on click listeners for Button to navigate to other activity */
        forgotPasswordButton.setOnClickListener(view -> {

            emailInputLayout.setError(null);

            String email = emailInputEditText.getText().toString();
            if (!isValidEmail(email))
                emailInputLayout.setError(getString(R.string.enter_a_valid_email));
            else {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {

                    if (task.isComplete() && task.isSuccessful())
                        startActivity(new Intent(this, OTPCodeMessageActivity.class));
                    else
                        Toasty.showText(ForgotPasswordActivity.this, "Email is not registered"
                                , Toasty.ERROR, Toast.LENGTH_LONG);
                });
            }
        });
    }


    private boolean isValidEmail(String email) {
        email = email.trim();
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void addOnTextChangeListenersForInputEditText() {
        emailInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidEmail(emailInputEditText.getText().toString()))
                    emailInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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