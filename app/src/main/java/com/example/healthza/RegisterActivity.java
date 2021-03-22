package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.resources.MaterialResources;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button registerButton, selectBirthDateButton, alreadyHaveAccountButton;

    private TextInputEditText nameInputEditText, emailInputEditText, phoneNumberInputEditText,
            passwordInputEditText, confirmPasswordInputEditText;

    private TextInputLayout nameInputLayout, emailInputLayout, phoneNumberInputLayout,
            passwordInputLayout, confirmPasswordInputLayout;

    private TextView selectedBirthDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        firebaseAuth = FirebaseAuth.getInstance();

        /* InputEditLayout's */
        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        phoneNumberInputLayout = findViewById(R.id.phoneNumberInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);

        /* InputEditText's */
        nameInputEditText = findViewById(R.id.nameInputEditText);
        emailInputEditText = findViewById(R.id.emailInputEditText);
        phoneNumberInputEditText = findViewById(R.id.phoneNumberInputEditText);
        passwordInputEditText = findViewById(R.id.passwordInputEditText);
        confirmPasswordInputEditText = findViewById(R.id.confirmPasswordInputEditText);

        /* Buttons */
        selectBirthDateButton = findViewById(R.id.selectBirthDateButton);
        registerButton = findViewById(R.id.signUpButton);
        alreadyHaveAccountButton = findViewById(R.id.alreadyHaveAccount);

        /* TextView */
        selectedBirthDateTextView = findViewById(R.id.selectedBirthDateTextView);


        /*Class TextInputEditTextFocusListenerHelper has add() function
         which will set focus listener for TextInputEditText to change start and end icon color tint*/
        TextInputEditTextFocusListenerHelper.add(this, nameInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, emailInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, phoneNumberInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, passwordInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, confirmPasswordInputEditText);


        addOnTextChangeListenersForInputEditText();
        setOnClickListenersForButtons();
    }


    private boolean isValidEmail(String email)
    {
        email = email.trim();
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private boolean isValidPhoneNumber(String phoneNumber)
    {
        return !phoneNumber.isEmpty() && Patterns.PHONE.matcher(phoneNumber).matches();
    }


    private boolean isValidPassword(String password)
    {
        return  password.length() >= 8;
    }

    private boolean isPasswordMatched(String password, String confirmPassword)
    {
        return password.equals(confirmPassword);
    }


    private void setOnClickListenersForButtons()
    {

        registerButton.setOnClickListener(view -> {

            clearInputErrors();

            String name = nameInputEditText.getText().toString();
            String email = emailInputEditText.getText().toString();
            String phoneNumber = phoneNumberInputEditText.getText().toString();
            String password = passwordInputEditText.getText().toString();
            String confirmPassword = confirmPasswordInputEditText.getText().toString();


            if(!isValidEmail(email))
                emailInputLayout.setError(getString(R.string.enter_a_valid_email));
            else if(!isValidPhoneNumber(phoneNumber))
                phoneNumberInputLayout.setError(getString(R.string.enter_a_valid_phonenumber));
            else if(!isValidPassword(password))
                passwordInputLayout.setError(getString(R.string.password_length_error));
            else if(!isValidPassword(confirmPassword))
                confirmPasswordInputLayout.setError(getString(R.string.password_length_error));
            else if(!isPasswordMatched(password, confirmPassword))
                confirmPasswordInputLayout.setError(getString(R.string.passwords_not_match_error));

            else
            {
                firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
                        .addOnCompleteListener(task -> {
                            if(task.isComplete() && task.isSuccessful())
                            {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                firebaseAuth.getCurrentUser().updateProfile(profileUpdates);

                                startActivity(new Intent(this, EmailVerificationCodeActivity.class));
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this, "Something went wrong"
                                ,Toast.LENGTH_LONG).show();
                            }
                });

            }
        });


        selectBirthDateButton.setOnClickListener(view -> {
            showDateDialog();
        });

        alreadyHaveAccountButton.setOnClickListener(view ->
                startActivity(new Intent(this, LoginActivity.class)));
    }


    private void addOnTextChangeListenersForInputEditText()
    {
        emailInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isValidEmail(emailInputEditText.getText().toString()))
                    emailInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        phoneNumberInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isValidPhoneNumber(phoneNumberInputEditText.getText().toString()))
                    phoneNumberInputLayout.setError(null);
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

        confirmPasswordInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isValidPassword(confirmPasswordInputEditText.getText().toString()))
                    confirmPasswordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }


    private void clearInputErrors()
    {
        emailInputLayout.setError(null);
        phoneNumberInputLayout.setError(null);
        passwordInputLayout.setError(null);
        confirmPasswordInputLayout.setError(null);
    }

    private void showDateDialog()
    {
        Calendar calendar = Calendar.getInstance();

        /*  get today date  */
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                calendar.set(Calendar.DATE, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);

                String dateText = DateFormat.format("MM/dd/yyyy",calendar).toString();

                selectedBirthDateTextView.setText(dateText);
                selectedBirthDateTextView.setVisibility(TextView.VISIBLE);

            }
        }, year, month, date);

        datePickerDialog.show();
    }
}