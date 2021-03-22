package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private Button loginButton, newUserButton, forgotPasswordButton;
    private TextInputEditText emailInputEditText, passwordInputEditText;
    private TextInputLayout emailInputLayout, passwordInputLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        /* InputEditText's */
        emailInputEditText = findViewById(R.id.emailInputEditText);
        passwordInputEditText = findViewById(R.id.passwordInputEditText);


        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);

        /* Buttons */
        loginButton = findViewById(R.id.signInButton);
        newUserButton = findViewById(R.id.newUserButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);


        /*Class TextInputEditTextFocusListenerHelper has add() function
         which will set focus listener for TextInputEditText to change start and end icon color tint*/
        TextInputEditTextFocusListenerHelper.add(this, emailInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, passwordInputEditText);


        addOnTextChangeListenersForInputEditText();
        setOnClickListenersForButtons();
    }

    private boolean isValidEmail(String email)
    {
        email = email.trim();
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password)
    {
        return  password.length() >= 8;
    }


    /* set on click listeners for Buttons to navigate to other activity */
    private void setOnClickListenersForButtons()
    {
        loginButton.setOnClickListener(v -> {

            emailInputLayout.setError(null);
            passwordInputLayout.setError(null);

            String email = emailInputEditText.getText().toString();
            String password = passwordInputEditText.getText().toString();



            if(!isValidEmail(email))
                emailInputLayout.setError(getString(R.string.enter_a_valid_email));
            else if(!isValidPassword(password))
                passwordInputLayout.setError(getString(R.string.password_length_error));
            else
            {
                firebaseAuth.signInWithEmailAndPassword(email.trim(), password).addOnCompleteListener(task -> {

                    if(task.isComplete() && task.isSuccessful()) {
                        startActivity(new Intent(this, MainActivity.class));
                        Toast.makeText(this, "welcome" + firebaseAuth.getCurrentUser().getDisplayName()
                                , Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();;

                    }
                });
            }


        });

        newUserButton.setOnClickListener(view ->
                startActivity(new Intent(this, RegisterActivity.class)));

        forgotPasswordButton.setOnClickListener(view ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }


    private void addOnTextChangeListenersForInputEditText() {
        emailInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidEmail(emailInputEditText.getText().toString()))
                    emailInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        passwordInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isValidPassword(passwordInputEditText.getText().toString()))
                    passwordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

}