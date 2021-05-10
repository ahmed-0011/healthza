package com.example.healthza.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;

import com.example.healthza.DrawerUtil;
import com.example.healthza.ProgressDialog;
import com.example.healthza.Toasty;
import com.example.healthza.models.DailyTest;
import com.example.healthza.models.Disease;
import com.example.healthza.R;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatientHomeActivity extends AppCompatActivity
{
    private TextInputLayout weightInputlayout, heightInputlayout;
    private TextInputEditText weightInputEditText, heightInputEditText;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String patientId;
    private String patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        patientId = firebaseAuth.getCurrentUser().getUid();
        patientName = firebaseAuth.getCurrentUser().getDisplayName();


        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        boolean userCompleteInfo = sharedPreferences.getBoolean("user_complete_info", false);

        if (!userCompleteInfo)
            showWelcomeDialog();

        setGlucoseCardView();
        setHypertensionCardView();
        setCholesterolCardViews();

        ChipNavigationBar chipNavigationBar = findViewById(R.id.bottomNavigationBar);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(int i)
            {
                if (i == R.id.homeItem);
                else if (i == R.id.medicalHistoryItem)
                    startActivity(new Intent(PatientHomeActivity.this, medicalRecords.class));
                else if (i == R.id.chartsItem)
                {
                    startActivity(new Intent(PatientHomeActivity.this, PatientChartsActivity.class));
                }
                else if (i == R.id.appointmentsItem)
                    startActivity(new Intent(PatientHomeActivity.this, PatientAppointments.class));
                else if (i == R.id.chatItem)
                    startActivity(new Intent(PatientHomeActivity.this, PatientChatListActivity.class));
            }
        });


        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.drawer_header, null);

        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView emailTextView = view.findViewById(R.id.emailTextView);

        nameTextView.setText(firebaseAuth.getCurrentUser().getDisplayName());
        emailTextView.setText(firebaseAuth.getCurrentUser().getEmail());
        DrawerUtil.headerView = view;
        DrawerUtil.getPatientDrawer(this, -1);
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



    private void setGlucoseCardView()
    {
        TextView glucoseTodayTestsTextView, latestGlucoseTestLevelTextView, latestGlucoseTestTimeTextView, highestGlucoseLevelTextView, lowestGlucoseLevelTextView, totalGlucoseTestsTextView;

        glucoseTodayTestsTextView = findViewById(R.id.glucoseTodayTestsTextView);
        latestGlucoseTestLevelTextView = findViewById(R.id.latestGlucoseTestLevelTextView);
        latestGlucoseTestTimeTextView = findViewById(R.id.latestGlucoseTestTimeTextView);
        highestGlucoseLevelTextView = findViewById(R.id.highestGlucoseLevelTextView);
        lowestGlucoseLevelTextView = findViewById(R.id.lowestGlucoseLevelTextView);
        totalGlucoseTestsTextView = findViewById(R.id.totalGlucoseTestsTextView);

        db.collection("patients")
                .document(patientId)
                .collection("tests")
                .document("glucose_test")
                .collection(getTodayDate())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(glucoseDocuments ->
        {

            int todayTests = glucoseDocuments.size();

            if(todayTests != 0)
            {
                DocumentSnapshot glucoseDocument = glucoseDocuments.getDocuments().get(0);
                double testLevel = glucoseDocument.getDouble("glucose_percent");
                Timestamp timestamp = glucoseDocuments.getDocuments().get(0).getTimestamp("timestamp");

                String testTime = getTestTime(timestamp);

                latestGlucoseTestLevelTextView.append(testLevel + "");
                latestGlucoseTestTimeTextView.append(testTime);
                glucoseTodayTestsTextView.append(todayTests + "");
            }
        });
    }

    private void setHypertensionCardView()
    {
        TextView hypertensionTodayTestsTextView, latestHypertensionTestLevelTextView, latestHypertensionTestTimeTextView, highestHypertensionLevelTextView, lowestHypertensionLevelTextView, totalHypertensionTestsTextView;

        hypertensionTodayTestsTextView = findViewById(R.id.hypertensionTodayTestsTextView);
        latestHypertensionTestLevelTextView = findViewById(R.id.latestHypertensionTestLevelTextView);
        latestHypertensionTestTimeTextView = findViewById(R.id.latestHypertensionTestTimeTextView);
        highestHypertensionLevelTextView = findViewById(R.id.highestHypertensionLevelTextView);
        lowestHypertensionLevelTextView = findViewById(R.id.lowestHypertensionLevelTextView);
        totalHypertensionTestsTextView = findViewById(R.id.totalHypertensionTestsTextView);

        db.collection("patients")
                .document(patientId)
                .collection("tests")
                .document("hypertension_test")
                .collection(getTodayDate())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(hypertensionDocuments ->
        {


            int todayTests = hypertensionDocuments.size();
            if(todayTests != 0)
            {
                DocumentSnapshot hypertensionDocument = hypertensionDocuments.getDocuments().get(0);
                double testLevel = hypertensionDocument.getDouble("hypertension_percent");
                Timestamp timestamp = hypertensionDocuments.getDocuments().get(0).getTimestamp("timestamp");

                String testTime = getTestTime(timestamp);

                latestHypertensionTestLevelTextView.append(testLevel + "");
                latestHypertensionTestTimeTextView.append(testTime);
                hypertensionTodayTestsTextView.append(todayTests + "");

            }
        });
    }


    private void setCholesterolCardViews()
    {
        TextView hdlTodayTestsTextView, latestHDLTestLevelTextView, latestHDLTestTimeTextView, highestHDLLevelTextView, lowestHDLLevelTextView, totalHDLTestsTextView,
                ldlTodayTestsTextView, latestLDLTestLevelTextView, latestLDLTestTimeTextView, highestLDLLevelTextView, lowestLDLLevelTextView, totalLDLTestsTextView,
                triglycerideTodayTestsTextView, latestTriglycerideTestLevelTextView, latestTriglycerideTestTimeTextView, highestTriglycerideLevelTextView, lowestTriglycerideLevelTextView, totalTriglycerideTestsTextView,
                totalCholesterolTodayTestsTextView, latestTotalCholesterolTestLevelTextView, latestTotalCholesterolTestTimeTextView, highestTotalCholesterolLevelTextView, lowestTotalCholesterolLevelTextView, totalTotalCholesterolTestsTextView;

        hdlTodayTestsTextView = findViewById(R.id.hdlTodayTestsTextView);
        latestHDLTestLevelTextView = findViewById(R.id.latestHDLTestLevelTextView);
        latestHDLTestTimeTextView = findViewById(R.id.latestHDLTestTimeTextView);
        highestHDLLevelTextView = findViewById(R.id.highestHDLLevelTextView);
        lowestHDLLevelTextView = findViewById(R.id.lowestHDLLevelTextView);
        totalHDLTestsTextView = findViewById(R.id.totalHDLTestsTextView);

        ldlTodayTestsTextView = findViewById(R.id.ldlTodayTestsTextView);
        latestLDLTestLevelTextView = findViewById(R.id.latestLDLTestLevelTextView);
        latestLDLTestTimeTextView = findViewById(R.id.latestLDLTestTimeTextView);
        highestLDLLevelTextView = findViewById(R.id.highestLDLLevelTextView);
        lowestLDLLevelTextView = findViewById(R.id.lowestLDLLevelTextView);
        totalLDLTestsTextView = findViewById(R.id.totalLDLTestsTextView);

        triglycerideTodayTestsTextView = findViewById(R.id.triglycerideTodayTestsTextView);
        latestTriglycerideTestLevelTextView = findViewById(R.id.latestTriglycerideTestLevelTextView);
        latestTriglycerideTestTimeTextView = findViewById(R.id.latestTriglycerideTestTimeTextView);
        highestTriglycerideLevelTextView = findViewById(R.id.highestTriglycerideLevelTextView);
        lowestTriglycerideLevelTextView = findViewById(R.id.lowestTriglycerideLevelTextView);
        totalTriglycerideTestsTextView = findViewById(R.id.totalTriglycerideTestsTextView);

        totalCholesterolTodayTestsTextView = findViewById(R.id.totalCholesterolTodayTestsTextView);
        latestTotalCholesterolTestLevelTextView = findViewById(R.id.latestTotalCholesterolTestLevelTextView);
        latestTotalCholesterolTestTimeTextView = findViewById(R.id.latestTotalCholesterolTestTimeTextView);
        highestTotalCholesterolLevelTextView = findViewById(R.id.highestTotalCholesterolLevelTextView);
        lowestTotalCholesterolLevelTextView = findViewById(R.id.lowestTotalCholesterolLevelTextView);
        totalTotalCholesterolTestsTextView = findViewById(R.id.totalTotalCholesterolTestsTextView);

        db.collection("patients")
                .document(patientId)
                .collection("tests")
                .document("cholesterolAndFats_test")
                .collection(getTodayDate())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(cholesterolDocuments ->
        {

            int todayTests = cholesterolDocuments.size();
            if(todayTests != 0)
            {
                DocumentSnapshot cholesterolDocument = cholesterolDocuments.getDocuments().get(0);

                double totalCholesterolLevel = cholesterolDocument.getDouble("CholesterolTotal_percent");
                double hdlLevel = cholesterolDocument.getDouble("HDLCholesterol_percent");
                double ldlLevel = cholesterolDocument.getDouble("LDLCholesterol_percent");
                double triglycerideLevel = cholesterolDocument.getDouble("Triglycerid_percent");

                Timestamp timestamp = cholesterolDocument.getTimestamp("timestamp");

                String testTime = getTestTime(timestamp);

                latestHDLTestLevelTextView.append(hdlLevel + "");
                latestLDLTestLevelTextView.append(ldlLevel + "");
                latestTriglycerideTestLevelTextView.append(triglycerideLevel + "");
                latestTotalCholesterolTestLevelTextView.append(totalCholesterolLevel + "");

                latestHDLTestTimeTextView.append(testTime);
                latestLDLTestTimeTextView.append(testTime);
                latestTriglycerideTestTimeTextView.append(testTime);
                latestTotalCholesterolTestTimeTextView.append(testTime);

                hdlTodayTestsTextView.append(todayTests + "");
                ldlTodayTestsTextView.append(todayTests + "");
                triglycerideTodayTestsTextView.append(todayTests + "");
                totalCholesterolTodayTestsTextView.append(todayTests + "");
            }
        });
    }



    @SuppressLint("SetTextI18n")
    void showWelcomeDialog()
    {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.welcome_dialog, null);
        Button startButton = view.findViewById(R.id.startButton);
        TextView welcomeTextView = view.findViewById(R.id.welcomeTextView);
        welcomeTextView.setText(welcomeTextView.getText() + " " + patientName);
        AlertDialog welcomeDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();

        welcomeDialog.show();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);  // height in pixels for device
        welcomeDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, height);
        welcomeDialog.setContentView(view);


        startButton.setOnClickListener(v -> {
            showPatientDialog1();
            welcomeDialog.dismiss();
        });
    }


    private boolean isValidDiseaseType(String workplace) // No need to check if the field is empty
    {                                                    // because Regex won't match empty strings
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("[A-Za-z0-9 ]+");
        matcher = pattern.matcher(workplace);
        return matcher.matches();
    }

    private boolean isValidWeight(String weight) {
        if (weight.isEmpty())
            return false;

        double weight1 = Double.parseDouble(weight);

        return weight1 <= 300;
    }

    private boolean isValidHeight(String height) // No need to check if the field is empty
    {                                            // because Regex won't match empty strings
        if (height.isEmpty())
            return false;

        double height1 = Double.parseDouble(height);

        return height1 <= 2.5;

    }

    private void showPatientDialog1() {
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.patient_dialog1, null);

        CheckBox diabetesCheckBox, hypertensionCheckBox, cholestrolCheckBox;
        TextInputLayout diabetesTypeInputLayout, hypertensionTypeInputLayout, cholestrolTypeInputLayout;
        TextInputEditText diabetesTypeInputEditText, hypertensionTypeInputEditText,
                cholestrolTypeInputEditText;

        diabetesCheckBox = view.findViewById(R.id.diabetesCheckBox);
        hypertensionCheckBox = view.findViewById(R.id.bloodPressureCheckBox);
        cholestrolCheckBox = view.findViewById(R.id.cholestrolCheckBox);
        diabetesTypeInputEditText = view.findViewById(R.id.diabetesTypeInputEditText);
        hypertensionTypeInputEditText = view.findViewById(R.id.hypertensionTypeInputEditText);
        cholestrolTypeInputEditText = view.findViewById(R.id.cholestrolTypeInputEditText);
        diabetesTypeInputLayout = view.findViewById(R.id.diabetesTypeInputLayout);
        hypertensionTypeInputLayout = view.findViewById(R.id.hypertensionTypeInputLayout);
        cholestrolTypeInputLayout = view.findViewById(R.id.cholestrolTypeInputLayout);


        diabetesTypeInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidDiseaseType(diabetesTypeInputEditText.getText().toString()))
                    diabetesTypeInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        hypertensionTypeInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidDiseaseType(hypertensionTypeInputEditText.getText().toString()))
                    hypertensionTypeInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cholestrolTypeInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidDiseaseType(cholestrolTypeInputEditText.getText().toString()))
                    cholestrolTypeInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        /* enable Input Edit Text When checkbox is checked and disabled Input Edit Text if not */
        diabetesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            diabetesTypeInputLayout.setEnabled(isChecked);
            diabetesTypeInputEditText.requestFocus();
        });

        hypertensionCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            hypertensionTypeInputLayout.setEnabled(isChecked);
            hypertensionTypeInputEditText.requestFocus();
        });

        cholestrolCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cholestrolTypeInputLayout.setEnabled(isChecked);
            cholestrolTypeInputEditText.requestFocus();
        });


        diabetesTypeInputLayout.setEnabled(true);
        AlertDialog patientDialog1 = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Welcome, " + patientName)
                .setCancelable(false)
                .setPositiveButton(R.string.next_text, null)
                .create();

        patientDialog1.show();
        diabetesTypeInputLayout.setEnabled(false);
        Button positiveButton = patientDialog1.getButton(DialogInterface.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(v ->
        {
            // clear TextInputLayout's errors every time the button is pressed
            diabetesTypeInputLayout.setError(null);
            hypertensionTypeInputLayout.setError(null);
            cholestrolTypeInputLayout.setError(null);


            String diabetesType = diabetesTypeInputEditText.getText().toString().trim();
            String hypertensionType = hypertensionTypeInputEditText.getText().toString().trim();
            String cholestrolType = cholestrolTypeInputEditText.getText().toString().trim();


            Disease diabetes, hypertension, cholestrol;
            diabetes = hypertension = cholestrol = null;


            if (!diabetesCheckBox.isChecked() && !hypertensionCheckBox.isChecked() && !cholestrolCheckBox.isChecked())
                Toasty.showText(this, "Please select at least 1 disease",Toasty.WARNING ,Toast.LENGTH_LONG);
            else
            {
                boolean error = false;

                if (diabetesCheckBox.isChecked())
                {
                    if (!isValidDiseaseType(diabetesType))
                    {
                        diabetesTypeInputLayout.setError("Invalid Disease type, disease type must containts alphabetic and spaces only");
                        diabetesTypeInputEditText.requestFocus();
                        error = true;
                    }
                    else
                        diabetes = new Disease("diabetes", diabetesType, "", false);
                }


                if (hypertensionCheckBox.isChecked()) {
                    if (!isValidDiseaseType(hypertensionType)) {
                        hypertensionTypeInputLayout.setError("Invalid Disease type, disease type must containts alphabetic and spaces only");
                        hypertensionTypeInputEditText.requestFocus();
                        error = true;
                    } else
                        hypertension = new Disease("hypertension", hypertensionType, "", false);
                }

                if (cholestrolCheckBox.isChecked()) {
                    if (!isValidDiseaseType(cholestrolType)) {
                        cholestrolTypeInputLayout.setError("Invalid Disease type, disease type must containts alphabetic and spaces only");
                        cholestrolTypeInputEditText.requestFocus();
                        error = true;
                    } else
                        cholestrol = new Disease("cholesterol", cholestrolType, "", false);
                }

                if (error == false)          // if there is no any errors in validating the inputs
                {
                    showPatientDialog2(diabetes, hypertension, cholestrol);
                    patientDialog1.dismiss();
                }
            }
        });
    }


    void showPatientDialog2(Disease diabetes, Disease hypertension, Disease cholestrol)
    {
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.patient_dialog2, null);

        Button selectDiabetesDetectionDateButton, selectHypertensionDetectionDateButton, selectCholestrolDetectionDateButton;

        TextView selectedDiabetesDetectionDateTextView, selectedHypertensionDetectionDateTextView,
                selectedCholestrolDetectionDateTextView, diabetesDetectionTextView,
                hypertensionDetectionDateTextView, cholestrolDetectionDateTextView;

        diabetesDetectionTextView = view.findViewById(R.id.diabetesDetectionDateTextView);
        hypertensionDetectionDateTextView = view.findViewById(R.id.hypertensionDetectionDateTextView);
        cholestrolDetectionDateTextView = view.findViewById(R.id.cholestrolDetectionDateTextView);
        selectedDiabetesDetectionDateTextView = view.findViewById(R.id.selectedDiabetesDetectionDateTextView);
        selectedHypertensionDetectionDateTextView = view.findViewById(R.id.selectedHypertensionDetectionDateTextView);
        selectedCholestrolDetectionDateTextView = view.findViewById(R.id.selectedCholestrolDetectionDateTextView);
        selectDiabetesDetectionDateButton = view.findViewById(R.id.selectDiabetesDetectionDateButton);
        selectHypertensionDetectionDateButton = view.findViewById(R.id.selectHypertensionDetectionDateButton);
        selectCholestrolDetectionDateButton = view.findViewById(R.id.selectCholestrolDateButton);

        weightInputlayout = view.findViewById(R.id.weightInputLayout);
        heightInputlayout = view.findViewById(R.id.heightInputLayout);
        weightInputEditText = view.findViewById(R.id.weightInputEditText);
        heightInputEditText = view.findViewById(R.id.heightInputEditText);


        selectDiabetesDetectionDateButton.setOnClickListener(view1 -> {
            showDateDialog(selectedDiabetesDetectionDateTextView);
        });

        selectHypertensionDetectionDateButton.setOnClickListener(view1 -> {
            showDateDialog(selectedHypertensionDetectionDateTextView);
        });

        selectCholestrolDetectionDateButton.setOnClickListener(view1 -> {
            showDateDialog(selectedCholestrolDetectionDateTextView);
            cholestrol.setDiagnosisDate(selectedCholestrolDetectionDateTextView.getText().toString());
        });


        if (diabetes == null) {
            diabetesDetectionTextView.setVisibility(View.GONE);
            selectDiabetesDetectionDateButton.setVisibility(View.GONE);
        }

        if (hypertension == null) {
            hypertensionDetectionDateTextView.setVisibility(View.GONE);
            selectHypertensionDetectionDateButton.setVisibility(View.GONE);
        }

        if (cholestrol == null) {
            cholestrolDetectionDateTextView.setVisibility(View.GONE);
            selectCholestrolDetectionDateButton.setVisibility(View.GONE);
        }


        AlertDialog patientDialog2 = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Welcome, " + patientName)
                .setCancelable(false)
                .setPositiveButton(R.string.finish_text, null)
                .create();

        patientDialog2.show();


        Button positiveButton = patientDialog2.getButton(DialogInterface.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(v ->
        {

            weightInputlayout.setError(null);
            heightInputlayout.setError(null);


            String error = "";

            if (diabetes != null)
            {
                String diabetesDetectionDate = selectedDiabetesDetectionDateTextView.getText().toString();
                if (diabetesDetectionDate.isEmpty())
                    error = error + "Please enter detection date for diabetes.\n\n";
                else
                    diabetes.setDiagnosisDate(selectedDiabetesDetectionDateTextView.getText().toString());
            }

            if (hypertension != null)
            {
                String hypertensionDetectionDate = selectedHypertensionDetectionDateTextView.getText().toString();
                if (hypertensionDetectionDate.isEmpty())
                    error = error + "Please enter detection date for hypertension.\n\n";
                else
                    hypertension.setDiagnosisDate(selectedHypertensionDetectionDateTextView.getText().toString());
            }

            if (cholestrol != null)
            {
                String cholestrolDetectionDate = selectedCholestrolDetectionDateTextView.getText().toString();
                if (cholestrolDetectionDate.isEmpty())
                    error = error + "Please enter detection date for cholesterol.\n\n";
                else
                    cholestrol.setDiagnosisDate(selectedCholestrolDetectionDateTextView.getText().toString());
            }


            String weightString = weightInputEditText.getText().toString().trim();
            String heightString = heightInputEditText.getText().toString().trim();


            if (!error.isEmpty())
            {
                Toasty.showText(this, error, Toasty.WARNING, Toast.LENGTH_LONG);
            }
            else if (!isValidWeight(weightString)) {
                weightInputlayout.setError("Invalid weight, weight must be less than 300 KG");
                weightInputEditText.requestFocus();
            }
            else if (!isValidHeight(heightString)) {
                heightInputlayout.setError("Invalid height, height must be in meters..  ex: 2.5");
                heightInputEditText.requestFocus();
            }
            else
                {
                    hideKeyboard();

                double weight = Double.parseDouble(weightString);
                double height = Double.parseDouble(heightString);
                double bmi = weight / Math.pow(height, 2);

                BigDecimal bd = new BigDecimal(weight).setScale(2, RoundingMode.HALF_UP);
                weight = bd.doubleValue();
                bd = new BigDecimal(height).setScale(2, RoundingMode.HALF_UP);
                height = bd.doubleValue();
                bd = new BigDecimal(bmi).setScale(2, RoundingMode.HALF_UP);
                bmi = bd.doubleValue();

                Map<String, Object> additionalInfo = new HashMap<>();
                additionalInfo.put("weight", weight);
                additionalInfo.put("height", height);
                additionalInfo.put("bmi", bmi);
                additionalInfo.put("completeInfo", true);

                DocumentReference patientRef = db.collection("patients").document(patientId);
                CollectionReference diseasesRef = patientRef.collection("diseases");

                WriteBatch batch = db.batch();

                batch.update(patientRef, additionalInfo);

                if (diabetes != null) {
                    DocumentReference diabetesRef = diseasesRef.document();
                    batch.set(diabetesRef, diabetes);
                }
                if (hypertension != null) {
                    DocumentReference hypertensionRef = diseasesRef.document();
                    batch.set(hypertensionRef, hypertension);
                }
                if (cholestrol != null) {
                    DocumentReference cholestrolRef = diseasesRef.document();
                    batch.set(cholestrolRef, cholestrol);
                }

                batch.commit().addOnCompleteListener(task ->
                {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.showProgressDialog("Updating Profile...");
                    if (task.isSuccessful())
                    {
                        patientDialog2.dismiss();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                progressDialog.dismissProgressDialog();
                                Toasty.showText(PatientHomeActivity.this, "your profile updated successfully",
                                        Toasty.INFORMATION, Toast.LENGTH_LONG);
                            }
                        },3000);
                        /* this shared oeferences is used when the user is using the app right after register
                         *  and update his information */
                        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("user_complete_info", true);
                        editor.apply();
                    }
                    else
                        {
                        Toasty.showText(PatientHomeActivity.this,
                                "An error occurred while trying to update your profile",
                                Toasty.ERROR, Toast.LENGTH_LONG);
                    }
                });
            }
        });
    }

    private void addOnTextChangeListenersForInputEditText() {

        weightInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidHeight(weightInputEditText.getText().toString()))
                    weightInputlayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        heightInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidHeight(heightInputEditText.getText().toString()))
                    heightInputlayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showDateDialog(TextView textView)
    {
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker
                .Builder
                .datePicker()
                .build();

        materialDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            Long selectedDate = (Long) selection;
            String dateText = DateFormat.format("MM/dd/yyyy", selectedDate).toString();

            textView.setText(dateText);
            textView.setVisibility(TextView.VISIBLE);
        });

        materialDatePicker.show(getSupportFragmentManager(), "PatientHomeActivity");
    }

    public void logOut()
    {
        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO LogOut?" ).setTitle ( "Patient LogOut" )

                .setPositiveButton ( "YES_EXIT", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "LogedOut...", Toast.LENGTH_SHORT).show();
                        //complet
                        // finish();
                        firebaseAuth.signOut();
                        finishAffinity();
                        Intent I = new Intent(getApplicationContext(),WelcomeActivity.class);
                        startActivity(I);
                    }
                } )

                .setNegativeButton ( "CANCEL", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                })

                .setIcon(R.drawable.qus)
                .setPositiveButtonIcon (getDrawable ( R.drawable.yes))
                .setNegativeButtonIcon(getDrawable ( R.drawable.no))
                .show ();
    }

    private String getTestTime(Timestamp timestamp)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.US);

        String testTime = simpleDateFormat.format(timestamp.toDate());

        return testTime;
    }


    private String getTodayDate()
    {
        Calendar calendar = Calendar.getInstance();

        return DateFormat.format("yyyy-M-d", calendar).toString();
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