package com.example.healthza;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String userId;
    String patientName;
    TextInputLayout specialityInputLayout, yearsOfExperienceInputLayout, workplaceInputLayout;
    TextInputEditText specialityInputEditText,
            yearsOfExperienceInputEditText, workplaceInputEditText;

        startButton.setOnClickListener(v -> {
            showDoctorDialog();
            welcomeDialog.dismiss();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        patientName = firebaseAuth.getCurrentUser().getDisplayName();


        SharedPreferences sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);

        boolean userCompleteinfo = sharedPreferences.getBoolean("user_complete_info", false);

        if(!userCompleteinfo)
            showWelcomeDialog();
    }

    @SuppressLint("SetTextI18n")
    void showWelcomeDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.activity_welcome_dialog, null);
        Button startButton = view.findViewById(R.id.startButton);
        TextView welcomeTextView = view.findViewById(R.id.welcomeTextView);
        welcomeTextView.setText(welcomeTextView.getText() + " " + patientName);
        AlertDialog welcomeDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();
        doctorDialog.show();

        Button positiveButton = doctorDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(v ->
        {
            clearInputsErrors();

            String speciality = specialityInputEditText.getText().toString();
            String yearsOfExperience = yearsOfExperienceInputEditText.getText().toString();
            String workplace = workplaceInputEditText.getText().toString();

            MaterialButtonToggleGroup workDaysFirstToggleButton, workDaysSecondToggleButton;
            workDaysFirstToggleButton = view.findViewById(R.id.workDaysFirsttoggleButton);
            workDaysSecondToggleButton = view.findViewById(R.id.workDaysSecondToggleButton);


            List<String> workdays = getWorkDays(workDaysFirstToggleButton, workDaysSecondToggleButton );

            if (!isValidSpeciality(speciality)) {
                specialityInputLayout.setError("Invalid speciality, speciality must contains alphabetic and spaces only");
                specialityInputEditText.requestFocus();
            } else if (!isValidYearsOfExperience(yearsOfExperience)) {
                yearsOfExperienceInputLayout.setError("Invalid years of experience, years can't be more than 60 years");
                yearsOfExperienceInputEditText.requestFocus();
            } else if (!isValidWorkplace(workplace)) {
                workplaceInputLayout.setError("Invalid workplace, workplace must contains alphabetic and spaces only");
                workplaceInputEditText.requestFocus();
            } else {
                Map<String, Object> additionalData = new HashMap<>();

        welcomeDialog.show();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);  // height in pixels for
        welcomeDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, height);
        welcomeDialog.setContentView(view);

        startButton.setOnClickListener(v -> {
            showDoctorDialog();
            welcomeDialog.dismiss();
        });
    }


    private boolean isValidSpeciality(String speciality) // No need to check if the field is empty
    {                                                   // because Regex won't match empty strings
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("[A-Za-z ]+");
        matcher = pattern.matcher(speciality);
        return matcher.matches();
    }

    private boolean isValidYearsOfExperience(String yearsOfExperience)  // No need to check if the field is empty
    {                                                                   // because regex won't match empty strings
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("[1-5][0-9]?");
        matcher = pattern.matcher(yearsOfExperience);
        return matcher.matches();
    }

    private boolean isValidWorkplace(String workpalce) // No need to check if the field is empty
    {                                                   // because Regex won't match empty strings
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("[A-Za-z ]+");
        matcher = pattern.matcher(workpalce);
        return matcher.matches();
    }

    void showDoctorDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.activity_doctor_dialog1, null);

        specialityInputLayout = view.findViewById(R.id.specialityInputLayout);
        yearsOfExperienceInputLayout = view.findViewById(R.id.yearsOfExperienceInputLayout);
        workplaceInputLayout = view.findViewById(R.id.workplaceInputLayout);
        specialityInputEditText = view.findViewById(R.id.specialityInputEditText);
        yearsOfExperienceInputEditText = view.findViewById(R.id.yearsOfExperienceInputEditText);
        workplaceInputEditText = view.findViewById(R.id.workplaceInputEditText);


        /*
        TextInputEditTextFocusListenerHelper.add(this, specialityInputEditText);
        TextInputEditTextFocusListenerHelper.add(this,yearsOfExperienceInputEditText);
        TextInputEditTextFocusListenerHelper.add(this,workplaceInputEditText);
        */

        AlertDialog doctorDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Welcome, " + patientName)
                .setPositiveButton("Next", null)
                .create();
        doctorDialog.show();

        Button positiveButton = doctorDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(v ->
        {
            clearInputsErrors();

            String speciality = specialityInputEditText.getText().toString();
            String yearsOfExperience = yearsOfExperienceInputEditText.getText().toString();
            String workplace = workplaceInputEditText.getText().toString();

            MaterialButtonToggleGroup workDaysFirstToggleButton, workDaysSecondToggleButton;
            workDaysFirstToggleButton = view.findViewById(R.id.workDaysFirsttoggleButton);
            workDaysSecondToggleButton = view.findViewById(R.id.workDaysSecondToggleButton);


            List<String> workdays = getWorkDays(workDaysFirstToggleButton, workDaysSecondToggleButton );

            if (!isValidSpeciality(speciality)) {
                specialityInputLayout.setError("Invalid speciality, speciality must contains alphabetic and spaces only");
                specialityInputEditText.requestFocus();
            } else if (!isValidYearsOfExperience(yearsOfExperience)) {
                yearsOfExperienceInputLayout.setError("Invalid years of experience, years can't be more than 60 years");
                yearsOfExperienceInputEditText.requestFocus();
            } else if (!isValidWorkplace(workplace)) {
                workplaceInputLayout.setError("Invalid workplace, workplace must contains alphabetic and spaces only");
                workplaceInputEditText.requestFocus();
            } else {
                Map<String, Object> additionalData = new HashMap<>();

                additionalData.put("speciality", speciality);
                additionalData.put("years_of_experience", yearsOfExperience);
                additionalData.put("workplace", workplace);
                additionalData.put("workdays", workdays);
                additionalData.put("completeInfo", true);

                DocumentReference doctorRef = db.collection("doctors").document(userId);

                doctorRef.set(additionalData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "profile updated successfully", Toast.LENGTH_LONG).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("user_complete_info", true);
                            editor.apply();
                        }
                        else
                            Toast.makeText(MainActivity.this, "an error occurred during updating your profile", Toast.LENGTH_LONG).show();
                        doctorDialog.dismiss();
                    }
                });
            }
        });
    }


    private void clearInputsErrors() {
        specialityInputLayout.setError(null);
        yearsOfExperienceInputLayout.setError(null);
        workplaceInputLayout.setError(null);
    }


    private List<String> getWorkDays(MaterialButtonToggleGroup workDaysFirstToggleButton,
                                     MaterialButtonToggleGroup workDaysSecondToggleButton)
    {
        List<Integer> workDaysGroup1 = workDaysFirstToggleButton.getCheckedButtonIds();
        List<Integer> workDaysGroup2 = workDaysSecondToggleButton.getCheckedButtonIds();

        List<String> workDays = new ArrayList<>();

        if(workDaysGroup1.contains(R.id.saturdayButton))
            workDays.add("saturday");
        if(workDaysGroup1.contains(R.id.sundayButton))
            workDays.add("sunday");
        if(workDaysGroup1.contains(R.id.mondayButton))
            workDays.add("monday");
        if(workDaysGroup1.contains(R.id.tuesdayButton))
            workDays.add("tuesday");
        if(workDaysGroup2.contains(R.id.wednesdayButton))
            workDays.add("wednesday");
        if(workDaysGroup2.contains(R.id.thursdayButton))
            workDays.add("thursday");
        if(workDaysGroup2.contains(R.id.fridayButton))
            workDays.add("friday");

        return workDays;
    }

    private void addOnTextChangeListenersForInputEditText()
    {

        specialityInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isValidSpeciality(specialityInputEditText.getText().toString()))
                    specialityInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        yearsOfExperienceInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isValidYearsOfExperience(yearsOfExperienceInputEditText.getText().toString()))
                    yearsOfExperienceInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        workplaceInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isValidWorkplace(workplaceInputEditText.getText().toString()))
                    workplaceInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });



    }


    private void showDateDialog(TextView textView)
    {
        Calendar calendar = Calendar.getInstance();

        /*  get current date  */
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                calendar.set(Calendar.DATE, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);

                String dateText = DateFormat.format("MM/dd/yyyy", calendar).toString();

                textView.setText(dateText);
                textView.setVisibility(TextView.VISIBLE);
            }
        }, year, month, date);

        datePickerDialog.show();
    }



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
}