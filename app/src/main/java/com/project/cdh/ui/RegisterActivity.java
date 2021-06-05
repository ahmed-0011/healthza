package com.project.cdh.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.cdh.Toasty;
import com.project.cdh.models.Doctor;
import com.project.cdh.models.Patient;
import com.project.cdh.R;
import com.project.cdh.TextInputEditTextFocusListenerHelper;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity
{
    private static final String TAG = "RegisterActivity";
    RadioGroup userTypeRadioGroup;
    ProgressBar progressBar;
    private Button registerButton, selectBirthDateButton, alreadyHaveAccountButton;
    private TextInputEditText nameInputEditText, identificationNumberInputEditText, emailInputEditText, phoneNumberInputEditText,
            passwordInputEditText, confirmPasswordInputEditText;
    private TextInputLayout nameInputLayout, identificationNumberInputLayout, emailInputLayout, phoneNumberInputLayout,
            passwordInputLayout, confirmPasswordInputLayout;
    private TextView selectedBirthDateTextView;
    private ArrayList<ValueAnimator> animations;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        /* InputEditLayout's */
        nameInputLayout = findViewById(R.id.nameInputLayout);
        identificationNumberInputEditText = findViewById(R.id.identificationNumberInputEditText);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        phoneNumberInputLayout = findViewById(R.id.phoneNumberInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);

        /* InputEditText's */
        nameInputEditText = findViewById(R.id.nameInputEditText);
        identificationNumberInputLayout = findViewById(R.id.identificationNumberInputLayout);
        emailInputEditText = findViewById(R.id.emailInputEditText);
        phoneNumberInputEditText = findViewById(R.id.phoneNumberInputEditText);
        passwordInputEditText = findViewById(R.id.passwordInputEditText);
        confirmPasswordInputEditText = findViewById(R.id.confirmPasswordInputEditText);

        /* Buttons */
        selectBirthDateButton = findViewById(R.id.selectBirthDateButton);
        registerButton = findViewById(R.id.signUpButton);
        alreadyHaveAccountButton = findViewById(R.id.alreadyHaveAccount);

        progressBar = findViewById(R.id.progressBar);

        animations = new ArrayList<>();

        /* TextView */
        selectedBirthDateTextView = findViewById(R.id.selectedBirthDateTextView);


        /*Class TextInputEditTextFocusListenerHelper has add() function
         which will set focus listener for TextInputEditText to change start and end icon color tint*/
        TextInputEditTextFocusListenerHelper.add(this, nameInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, identificationNumberInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, emailInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, phoneNumberInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, passwordInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, confirmPasswordInputEditText);
        addOnTextChangeListenersForInputEditText();


        setOnClickListenersForButtons();
    }

    private boolean isValidName(String name) // No need to check if the field is empty
    {                                        // because Regex won't match empty strings
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("[A-Za-z ]+");
        matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean isValidIdentificationNumber(String id) {
        return id.length() == 9;
    }


    private boolean isValidEmail(String email) // No need to check if the field is empty
    {                                          // because regex won't match empty strings
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber)  // No need to check if the field is empty
    {                                                       // because regex won't match empty strings
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }


    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    private boolean isPasswordMatched(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }


    private void setOnClickListenersForButtons() {
        registerButton.setOnClickListener(view -> {
            clearInputsErrors();
            signUp();
        });

        selectBirthDateButton.setOnClickListener(view -> {
            showDateDialog();
        });

        alreadyHaveAccountButton.setOnClickListener(view ->
                startActivity(new Intent(this, LoginActivity.class)));
    }


    private void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void signUp()
    {
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        RadioGroup sexRadioGroup = findViewById(R.id.sexRadioGroup);


        String name = nameInputEditText.getText().toString().trim();
        String identificationNumber = identificationNumberInputEditText.getText().toString().trim();
        String userType = userTypeRadioGroup.getCheckedRadioButtonId() ==
                R.id.patientRadioButton ? "patient" : "doctor";
        String sex = sexRadioGroup.getCheckedRadioButtonId() ==
                R.id.maleRadioButton ? "male" : "female";
        String birthDate = selectedBirthDateTextView.getText().toString();
        String email = emailInputEditText.getText().toString().trim();
        String phoneNumber = phoneNumberInputEditText.getText().toString().trim();
        String password = passwordInputEditText.getText().toString();
        String confirmPassword = confirmPasswordInputEditText.getText().toString();

        if (!isValidName(name))
        {
            nameInputLayout.setError("Invalid name, name must contains alphabetic and spaces only");
            nameInputEditText.requestFocus();
        }
        else if (userTypeRadioGroup.getCheckedRadioButtonId() == -1)
            Toasty.showText(this, "Please select account type", Toasty.WARNING, Toast.LENGTH_LONG);
        else if (sexRadioGroup.getCheckedRadioButtonId() == -1)
            Toasty.showText(this, "Please select your sex", Toasty.WARNING, Toast.LENGTH_LONG);
        else if (birthDate.isEmpty())
            Toasty.showText(this, "Please select your birthdate", Toasty.WARNING, Toast.LENGTH_LONG);
        else if (!isValidIdentificationNumber(identificationNumber))
        {
            identificationNumberInputLayout.setError(getString(R.string.enter_a_valid_id));
            identificationNumberInputEditText.requestFocus();
        }
        else if (!isValidEmail(email))
        {
            emailInputLayout.setError(getString(R.string.enter_a_valid_email));
            emailInputEditText.requestFocus();
        }
        else if (!isValidPhoneNumber(phoneNumber))
        {
            phoneNumberInputLayout.setError(getString(R.string.enter_a_valid_phonenumber));
            phoneNumberInputEditText.requestFocus();
        }
        else if (!isValidPassword(password)) {
            passwordInputLayout.setError(getString(R.string.password_length_error));
            passwordInputEditText.requestFocus();
        }
        else if (!isValidPassword(confirmPassword))
        {
            confirmPasswordInputLayout.setError(getString(R.string.password_length_error));
            confirmPasswordInputEditText.requestFocus();
        }
        else if (!isPasswordMatched(password, confirmPassword))
        {
            confirmPasswordInputLayout.setError(getString(R.string.passwords_not_match_error));
            confirmPasswordInputEditText.requestFocus();
        }
        else
        {
            hideKeyboard();
            progressButtonAnimation();
            db.collection("ids").document(identificationNumber)
                    .get().addOnSuccessListener(idDcument ->
            {
                if(idDcument.exists())
                {
                    Toasty.showText(this, "ID is already used", Toasty.ERROR, Toast.LENGTH_LONG);
                }
                else
                {
                    db.collection("phonenumbers")
                            .document(phoneNumber)
                            .get().addOnSuccessListener(phoneNumberDocument ->
                    {
                        if(phoneNumberDocument.exists())
                        {
                            Toasty.showText(this, "Phonenumber is already used",
                                    Toasty.ERROR, Toast.LENGTH_LONG);
                        }
                        else
                        {
                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(task ->
                                    {
                                        if (task.isSuccessful())
                                        {
                                            SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();

                                            // set display name for the user and update user profile
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(name)
                                                    .build();

                                            firebaseAuth.getCurrentUser().updateProfile(profileUpdates);

                                            String userId = firebaseAuth.getCurrentUser().getUid();

                                            Map<String, Object> userIdField = new HashMap<>();
                                            userIdField.put("userId", userId);
                                            db.collection("phonenumbers")
                                                    .document(phoneNumber)
                                                    .set(userIdField);

                                            db.collection("ids")
                                                    .document(identificationNumber)
                                                    .set(userIdField);

                                            Doctor newDoctor;
                                            Patient newPatient;

                                            if (userType.equals("patient"))
                                            {
                                                editor.putString("password", password);
                                                editor.putString("user_type", "patient");
                                                editor.putBoolean("user_complete_info", false);
                                                editor.apply();

                                                newPatient = new Patient(userId, name, identificationNumber, email, phoneNumber, birthDate, sex, false);

                                                DocumentReference patientRef = db.collection("patients").document(userId);

                                                patientRef.set(newPatient);
                                            }
                                            else
                                            {
                                                editor.putString("password", password);
                                                editor.putString("user_type", "doctor");
                                                editor.putBoolean("user_complete_info", false);
                                                editor.apply();

                                                newDoctor = new Doctor(userId, name, identificationNumber, email, phoneNumber, birthDate, sex, false);

                                                DocumentReference doctorReference = db.collection("doctors").document(userId);

                                                doctorReference.set(newDoctor);
                                            }

                                            Intent intent;

                                            if (userType.equals("patient"))
                                                intent = new Intent(this, PatientHomeActivity.class);
                                            else
                                                intent = new Intent(this, DoctorHomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                        else
                                            Toasty.showText(this, "Email is already used", Toasty.ERROR, Toast.LENGTH_LONG);
                                    });
                        }
                    });
                }
                progressButtonReverseAnimation();
            });
        }
    }

    private void addOnTextChangeListenersForInputEditText() {

        nameInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidName(nameInputEditText.getText().toString()))
                    nameInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        identificationNumberInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidIdentificationNumber(identificationNumberInputEditText.getText().toString()))
                    identificationNumberInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

        phoneNumberInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (isValidPhoneNumber(phoneNumberInputEditText.getText().toString()))
                    phoneNumberInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        passwordInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (isValidPassword(passwordInputEditText.getText().toString()))
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
                if (isValidPassword(confirmPasswordInputEditText.getText().toString()))
                    confirmPasswordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }


    private void clearInputsErrors()
    {
        identificationNumberInputLayout.setError(null);
        nameInputLayout.setError(null);
        emailInputLayout.setError(null);
        phoneNumberInputLayout.setError(null);
        passwordInputLayout.setError(null);
        confirmPasswordInputLayout.setError(null);
    }


    private void progressButtonAnimation()
    {
        GradientDrawable drawable = (GradientDrawable) registerButton.getBackground();

        int initialButtonWidth = registerButton.getWidth();
        float initialTextSize = registerButton.getTextSize();
        int targetButtonWidth = registerButton.getHeight();
        int targetWidth = targetButtonWidth;
        int targetCorrnerRadius = targetButtonWidth;

        ValueAnimator cornerAnimator = ValueAnimator.ofFloat(0f, targetCorrnerRadius);
        ValueAnimator widthAnimator = ValueAnimator.ofInt(initialButtonWidth, targetWidth);
        ValueAnimator textSizeAnimator = ValueAnimator.ofFloat(initialTextSize, 0f);
        ValueAnimator progressBarAlphaANimator = ObjectAnimator.ofFloat(progressBar, "alpha", 0f, 1f);

        cornerAnimator.setDuration(1000);
        widthAnimator.setDuration(1000);
        textSizeAnimator.setDuration(1000);
        progressBarAlphaANimator.setDuration(1000);

        cornerAnimator.addUpdateListener(animation -> {
            drawable.setCornerRadius((float) animation.getAnimatedValue());
            registerButton.setBackground(drawable);
        });

        widthAnimator.addUpdateListener(animation ->
        {
            ViewGroup.LayoutParams layoutParams = registerButton.getLayoutParams();
            layoutParams.width = (int) animation.getAnimatedValue();
            registerButton.setLayoutParams(layoutParams);
        });

        textSizeAnimator.addUpdateListener(animation -> {

            // to get text size in scale pixel
            float textSizeSp = (float) animation.getAnimatedValue() / getResources().getDisplayMetrics().density;
            registerButton.setTextSize(textSizeSp);
        });

        textSizeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                registerButton.setEnabled(false);
            }
        });

        progressBar.setAlpha(0f);
        progressBar.setVisibility(View.VISIBLE);

        animations.add(widthAnimator);
        animations.add(cornerAnimator);
        animations.add(progressBarAlphaANimator);
        animations.add(textSizeAnimator);

        widthAnimator.start();
        cornerAnimator.start();
        progressBarAlphaANimator.start();
        textSizeAnimator.start();

        /*this listener will be used when the animation is reversed*/
        textSizeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                registerButton.setEnabled(true);
            }
        });
    }


    private void progressButtonReverseAnimation()
    {
        for (int i = 0; i < animations.size(); i++)
            animations.get(i).reverse();

        animations.clear();
    }


    private void showDateDialog()
    {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder
                .datePicker();

        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.setValidator(DateValidatorPointBackward.now()).build());

        MaterialDatePicker<Long> materialDatePicker = builder.build();

        materialDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            Long selectedDate = (Long) selection;
            String birthDate = DateFormat.format("MM/dd/yyyy", new Date(selectedDate)).toString();

            selectedBirthDateTextView.setText(birthDate);
            selectedBirthDateTextView.setVisibility(TextView.VISIBLE);

        });

        materialDatePicker.show(getSupportFragmentManager(), "RegisterActivity");
    }
}