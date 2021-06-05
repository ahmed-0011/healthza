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
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.cdh.R;
import com.project.cdh.TextInputEditTextFocusListenerHelper;
import com.project.cdh.Toasty;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity
{

    ProgressBar progressBar;
    private Button loginButton, newUserButton, forgotPasswordButton;
    private TextInputEditText emailInputEditText, passwordInputEditText;
    private TextInputLayout emailInputLayout, passwordInputLayout;
    private ArrayList<ValueAnimator> animations;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        /* InputEditText's */
        emailInputEditText = findViewById(R.id.emailInputEditText);
        passwordInputEditText = findViewById(R.id.passwordInputEditText);

        /* TextInput'sLayout's */
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);

        /* Buttons */
        loginButton = findViewById(R.id.signInButton);
        newUserButton = findViewById(R.id.newUserButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);

        progressBar = findViewById(R.id.progressBar);

        /* animations list */
        animations = new ArrayList<>();

        /*Class TextInputEditTextFocusListenerHelper has add() function
         which will set focus listener for TextInputEditText to change start and end icon color tint*/
        TextInputEditTextFocusListenerHelper.add(this, emailInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, passwordInputEditText);


        addOnTextChangeListenersForInputEditText();
        setOnClickListenersForButtons();
    }

    private boolean isValidEmail(String email) {
        email = email.trim();
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
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


    /* set on click listeners for Buttons to navigate to other activity */
    private void setOnClickListenersForButtons() {
        loginButton.setOnClickListener(v ->
        {
            emailInputLayout.setError(null);
            passwordInputLayout.setError(null);

            signIn();
        });

        newUserButton.setOnClickListener(view ->
                startActivity(new Intent(this, RegisterActivity.class)));

        forgotPasswordButton.setOnClickListener(view ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }


    private void signIn()
    {

        String email = emailInputEditText.getText().toString().trim();
        String password = passwordInputEditText.getText().toString();

        if (!isValidEmail(email)) {
            emailInputLayout.setError(getString(R.string.enter_a_valid_email));
            emailInputEditText.requestFocus();
        } else if (!isValidPassword(password)) {
            passwordInputLayout.setError(getString(R.string.password_length_error));
            passwordInputEditText.requestFocus();
        } else
        {

            hideKeyboard();
            progressButtonAnimation();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task ->
            {
                if (task.isSuccessful())
                {
                    progressButtonReverseAnimation();

                    SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    String userId = firebaseAuth.getCurrentUser().getUid();

                    DocumentReference patientRef = db.collection("patients").document(userId);

                    patientRef.get().addOnCompleteListener(task1 ->
                    {
                        if (task1.isSuccessful())
                        {
                            DocumentSnapshot document1 = task1.getResult();

                            if (document1.exists())              // if the user exist in patients collection
                            {                                    // then userType is patient,
                                // it's impossible to be doctor

                                boolean completeInfo = document1.getBoolean("completeInfo");
                                editor.putString("password", password);
                                editor.putString("user_type", "patient");
                                editor.putBoolean("user_complete_info", completeInfo);
                                editor.apply();
                                Intent intent;
                                intent = new Intent(this, PatientHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else
                            {
                                DocumentReference doctorRef = db.collection("doctors").document(userId);

                                doctorRef.get().addOnCompleteListener(task2 ->
                                {
                                    if (task2.isSuccessful()) {
                                        DocumentSnapshot document2 = task2.getResult();

                                        if (document2.exists()) {
                                            boolean completeProfile = document2.getBoolean("completeInfo");
                                            editor.putString("password", password);
                                            editor.putString("user_type", "doctor");
                                            editor.putBoolean("user_complete_info", completeProfile);
                                            editor.apply();
                                            Intent intent;
                                            intent = new Intent(this, DoctorHomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }

                            Toasty.showText(this, "Successfully logged in!"
                                    ,Toasty.INFORMATION, Toast.LENGTH_LONG);
                        }
                    });
                } else
                    Toasty.showText(this, "Account is not found",Toasty.ERROR, Toast.LENGTH_LONG);
                progressButtonReverseAnimation();
            });
        }
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

        passwordInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidPassword(passwordInputEditText.getText().toString()))
                    passwordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void progressButtonAnimation() {
        GradientDrawable drawable = (GradientDrawable) loginButton.getBackground();

        int initialButtonWidth = loginButton.getWidth();
        float initialTextSize = loginButton.getTextSize();
        int targetButtonWidth = loginButton.getHeight();
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
            loginButton.setBackground(drawable);
        });

        widthAnimator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams layoutParams = loginButton.getLayoutParams();
            layoutParams.width = (int) animation.getAnimatedValue();
            loginButton.setLayoutParams(layoutParams);
        });

        textSizeAnimator.addUpdateListener(animation -> {

            // to get text size in scale pixel
            float textSizeSp = (float) animation.getAnimatedValue() / getResources().getDisplayMetrics().density;
            loginButton.setTextSize(textSizeSp);
        });

        textSizeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                loginButton.setEnabled(false);    // maybe call login here on the listener
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


        /* this listener will be used when the animation is reversed */
        textSizeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginButton.setEnabled(true);
            }
        });
    }


    private void progressButtonReverseAnimation() {
        for (int i = 0; i < animations.size(); i++)
            animations.get(i).reverse();


        animations.clear();
    }

    //rotate
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.i(COMMON_TAG,"DoctorHomeActivity onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        //  Log.i(COMMON_TAG,"DoctorHomeActivity onSaveInstanceState");
    }
}