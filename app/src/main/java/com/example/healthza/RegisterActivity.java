package com.example.healthza;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener
        , View.OnFocusChangeListener {

    private Button registerButton, selectBirthDateButton, alreadyHaveAccountButton;

    private TextInputEditText nameInputEditText, emailInputEditText, phoneNumberInputEditText,
            passwordInputEditText, confirmPasswordInputEditText,IDp;

    private TextInputLayout nameInputLayout, emailInputLayout, phoneNumberInputLayout,
            passwordInputLayout, confirmPasswordInputLayout,idI;

    private TextView selectedBirthDateTextView ;
    private ArrayList<ValueAnimator> animations;
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    ProgressBar progressBar;

    boolean bol = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        /* InputEditLayout's */
        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        phoneNumberInputLayout = findViewById(R.id.phoneNumberInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        idI = findViewById(R.id.idInputLayout);

        /* InputEditText's */
        nameInputEditText = findViewById(R.id.nameInputEditText); nameInputEditText.setOnFocusChangeListener(this);
        emailInputEditText = findViewById(R.id.emailInputEditText); emailInputEditText.setOnFocusChangeListener(this);
        phoneNumberInputEditText = findViewById(R.id.phoneNumberInputEditText); phoneNumberInputEditText.setOnFocusChangeListener(this);
        passwordInputEditText = findViewById(R.id.passwordInputEditText); passwordInputEditText .setOnFocusChangeListener(this);
        confirmPasswordInputEditText = findViewById(R.id.confirmPasswordInputEditText); confirmPasswordInputEditText.setOnFocusChangeListener(this);
        IDp = findViewById(R.id.idInputEditText); IDp.setOnFocusChangeListener(this);

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
        TextInputEditTextFocusListenerHelper.add(this, emailInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, phoneNumberInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, passwordInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, confirmPasswordInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, IDp);
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

    private boolean isValidEmail(String email) // No need to check if the field is empty
    {                                          // because regex won't match empty strings
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber)  // No need to check if the field is empty
    {                                                       // because regex won't match empty strings
        return Patterns.PHONE.matcher(phoneNumber).matches();
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
            clearInputsErrors();
            signUp();
        });

        selectBirthDateButton.setOnClickListener(view -> {
            showDateDialog();
        });

        alreadyHaveAccountButton.setOnClickListener(view ->
                startActivity(new Intent(this, LoginActivity.class)));
    }


    private void signUp()
    {
        final Handler handler2 = new Handler(Looper.getMainLooper());
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                signUp_();
            }
        }, 100);
        //signUp_();
    }

    private synchronized void signUp_()
    {
        RadioGroup userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        RadioGroup sexRadioGroup = findViewById(R.id.sexRadioGroup);


        String name = nameInputEditText.getText().toString().trim();
        String userType = userTypeRadioGroup.getCheckedRadioButtonId() ==
                R.id.patientRadioButton ? "patient" : "doctor";
        String sex = sexRadioGroup.getCheckedRadioButtonId() ==
                R.id.maleRadioButton ? "male" : "female";
        String birthDate = selectedBirthDateTextView.getText().toString();
        String email = emailInputEditText.getText().toString().trim();
        String phoneNumber = phoneNumberInputEditText.getText().toString().trim();
        String password = passwordInputEditText.getText().toString();
        String confirmPassword = confirmPasswordInputEditText.getText().toString();
        String id_ = IDp.getText().toString();

        if(!isValidName(name))
        {
            nameInputLayout.setError("Invalid name, name must contains alphapetic and spaces only");
            nameInputEditText.requestFocus();
        }
        else if(userTypeRadioGroup.getCheckedRadioButtonId() == -1);
            //TODO
        else if(sexRadioGroup.getCheckedRadioButtonId() == -1);
            //TODO
        else if(birthDate.isEmpty());
            //TODO

        else if(IDp.getText().toString().isEmpty())
        {
            idI.setError("Please Fill the field.");
            IDp.requestFocus();
            return;
        }

        else if(bol==true) {
            idI.setError("Used ID, This Id has an account, must enter id have not used.");
            IDp.requestFocus();
            return;
        }

        else if(!isValidEmail(email))
        {
            emailInputLayout.setError(getString(R.string.enter_a_valid_email));
            emailInputEditText.requestFocus();
        }
        else if(!isValidPhoneNumber(phoneNumber))
        {
            phoneNumberInputLayout.setError(getString(R.string.enter_a_valid_phonenumber));
            phoneNumberInputEditText.requestFocus();
        }
        else if(!isValidPassword(password))
        {
            passwordInputLayout.setError(getString(R.string.password_length_error));
            passwordInputEditText.requestFocus();
        }
        else if(!isValidPassword(confirmPassword))
        {
            confirmPasswordInputLayout.setError(getString(R.string.password_length_error));
            confirmPasswordInputEditText.requestFocus();
        }
        else if(!isPasswordMatched(password, confirmPassword))
            confirmPasswordInputLayout.setError(getString(R.string.passwords_not_match_error));

        else
        {
            progressButtonAnimation();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            SharedPreferences sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();


                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            // set display name for the user and update user profile
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            firebaseAuth.getCurrentUser().updateProfile(profileUpdates);

                            String userId = firebaseAuth.getCurrentUser().getUid();

                            Doctor newDoctor;
                            Patient newPatient;

                            if(userType.equals("patient"))
                            {
                                editor.putString("user_type","patient");
                                editor.apply();

                                newPatient = new Patient(id_, name, email, phoneNumber, birthDate, sex, false);

                                DocumentReference patientRef = db.collection("patients").document(userId);

                                patientRef.set(newPatient).addOnCompleteListener(task1 -> {

                                    if(task1.isSuccessful())
                                    {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + patientRef.getId());
                                    }

                                    else
                                    {
                                        Log.w(TAG, "Error adding document", task.getException());
                                    }

                                });

                            }
                            else
                            {
                                editor.putString("user_type","doctor");
                                editor.apply();

                                newDoctor = new Doctor(id_, name, email, phoneNumber, birthDate, sex, false);

                                DocumentReference documentReference = db.collection("doctors").document(userId);

                                documentReference.set(newDoctor).addOnCompleteListener(task1 -> {

                                    if(task1.isSuccessful())
                                    {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    }

                                    else
                                    {
                                        Log.w(TAG, "Error adding document", task.getException());
                                    }

                                });
                            }


                            Intent intent = new Intent(this, EmailVerificationCodeActivity.class);
                            intent.putExtra("userType", userType);
                            startActivity(intent);
                        }
                        else
                        {
                            progressButtonReverseAnimation();
                            Toast.makeText(RegisterActivity.this, "Email is used in another account"
                                    ,Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void addOnTextChangeListenersForInputEditText()
    {

        nameInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isValidName(nameInputEditText.getText().toString()))
                    nameInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


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

        IDp.addTextChangedListener(new TextWatcher() {
            @Override
            public synchronized void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public synchronized void onTextChanged(CharSequence s, int start, int before, int count) {
                existsID(IDp.getText().toString(),0);
                if(bol)return;
                boolean temp = bol;
                existsID(IDp.getText().toString(), 1);
                bol = bol || temp;
               /* final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(bol)return;
                        else
                        {
                            boolean temp = bol;
                            existsID(IDp.getText().toString(), 1);
                            final Handler handler2 = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms
                                    bol = bol || temp;
                                }
                            }, 100);
                        }
                    }
                }, 100);*/
            }

            @Override
            public synchronized void afterTextChanged(Editable s) { }
        });
    }


    private  synchronized void existsID(String idd,int type)
    {
        bol =false;
        if( idd.isEmpty() ) return;
        final String c[]={"patients","doctors"},ids[]={"patient_id","doctor_id"};
        if(type > 1)return;

        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
        CollectionReference collRef = db1.collection(c[type]);
        collRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                    for(int i=0;((i<myListOfDocuments.size())&&(bol==false));i++)
                    {
                        DocumentSnapshot document = myListOfDocuments.get(i);
                        if (document.exists()) {
                            if(document.get(ids[type])!=null)
                                if (document.get(ids[type]).toString().equals(idd)){
                                    //Toast.makeText(getApplicationContext(),document.get(ids[type]).toString()+" : "+i,Toast.LENGTH_LONG).show();
                                    bol = true;  return;}
                        }
                    }
                }
            }
        });

        if(bol)return;
        bol =false;
        return;
    }

    private void clearInputsErrors()
    {
        nameInputLayout.setError(null);
        emailInputLayout.setError(null);
        phoneNumberInputLayout.setError(null);
        passwordInputLayout.setError(null);
        confirmPasswordInputLayout.setError(null);
        idI.setError(null);
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
        ValueAnimator progressBarAlphaANimator = ObjectAnimator.ofFloat(progressBar,"alpha",0f, 1f);

        cornerAnimator.setDuration(1000);
        widthAnimator.setDuration(1000);
        textSizeAnimator.setDuration(1000);
        progressBarAlphaANimator.setDuration(1000);

        cornerAnimator.addUpdateListener(animation -> {
            drawable.setCornerRadius((float)animation.getAnimatedValue());
            registerButton.setBackground(drawable);
        });

        widthAnimator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams layoutParams = registerButton.getLayoutParams();
            layoutParams.width = (int)animation.getAnimatedValue();
            registerButton.setLayoutParams(layoutParams);
        });

        textSizeAnimator.addUpdateListener(animation -> {

            // to get text size in scale pixel
            float textSizeSp = (float) animation.getAnimatedValue() /  getResources().getDisplayMetrics().density;
            registerButton.setTextSize(textSizeSp);
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
    }

    private void progressButtonReverseAnimation()
    {
        for(int i = 0; i < animations.size(); i++)
            animations.get(i).reverse();

        animations.clear();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if(v==v)
        {
            if (!hasFocus) {
                Log.d("focus", "focus lost");
                // Do whatever you want here
            } else {
                Toast.makeText(getApplicationContext(), " Tap outside edittext to lose focus ", Toast.LENGTH_SHORT).show();
                Log.d("focus", "focused");
            }

            return;
        }

    }

    //
    // <!-- Clear focus on touch outside for all EditText inputs. "Clear focus input"
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
// "Clear focus input" -->

    //rotate
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.i(COMMON_TAG,"MainActivity onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //  Log.i(COMMON_TAG,"MainActivity onSaveInstanceState");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}