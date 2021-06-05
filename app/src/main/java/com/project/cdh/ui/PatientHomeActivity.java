package com.project.cdh.ui;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
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
import com.project.cdh.DrawerUtil;
import com.project.cdh.ProgressDialog;
import com.project.cdh.R;
import com.project.cdh.TextInputEditTextFocusListenerHelper;
import com.project.cdh.Toasty;
import com.project.cdh.models.BodyInfo;
import com.project.cdh.models.DailyTest;
import com.project.cdh.models.Disease;
import com.project.cdh.models.Patient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatientHomeActivity extends AppCompatActivity
{
    private TextInputLayout weightInputLayout, heightInputLayout;
    private TextInputEditText weightInputEditText, heightInputEditText, bmiInputEditText;
    private ChipNavigationBar chipNavigationBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String patientId;
    private String patientName;
    private DocumentReference patientRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        patientId = firebaseAuth.getCurrentUser().getUid();
        patientName = firebaseAuth.getCurrentUser().getDisplayName();

        patientRef = db.collection("patients")
                .document(patientId);

        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        boolean userCompleteInfo = sharedPreferences.getBoolean("user_complete_info", false);

        if (!userCompleteInfo)
            showWelcomeDialog();
        else
            setupCardViews();

        chipNavigationBar = findViewById(R.id.bottomNavigationBar);

        setupBottomNavigationBar();
    }


    private void setupBottomNavigationBar()
    {
        chipNavigationBar.setItemSelected(R.id.homeItem, true);

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(int i)
            {
                Intent intent = null;

                if (i == R.id.homeItem)
                    return;
                else if (i == R.id.medicalHistoryItem)
                    intent = new Intent(PatientHomeActivity.this, PatientMedicalHistoryActivity.class);
                else if (i == R.id.chartsItem)
                    intent = new Intent(PatientHomeActivity.this, PatientChartsActivity.class);
                else if (i == R.id.appointmentsItem)
                    intent = new Intent(PatientHomeActivity.this, PatientAppointmentsActivity.class);
                else if (i == R.id.chatItem)
                    intent = new Intent(PatientHomeActivity.this, PatientChatListActivity.class);

                if (intent != null)
                {
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }


    private void setupCardViews()
    {
        setupTestStatisticsCardView();
        setupBodyInfoCardView();
        setupLatestThreeTestCardViews();
        setupNextThreeAppointmentsCardViews();
    }


    private void setupTestStatisticsCardView()
    {
        TextView glucoseTestsTextView, hypertensionTestsTextView, cholesterolAndFatsTextView;

        Button testsStatisticsButton;

        glucoseTestsTextView = findViewById(R.id.glucoseTestsTextView);
        hypertensionTestsTextView = findViewById(R.id.hypertensionTestsTextView);
        cholesterolAndFatsTextView = findViewById(R.id.cholesterolAndFatsTextView);

        testsStatisticsButton = findViewById(R.id.testsStatisticsButton);

        testsStatisticsButton.setOnClickListener(v ->
                startActivity(new Intent(this, PatientTestsStatisticsActivity.class)));


        patientRef.collection("tests")
                .document("glucose_test")
                .get()
                .addOnSuccessListener(glucoseTestsDocument ->
                {
                    if (glucoseTestsDocument.exists())
                    {
                        int totalNumberOfTests = glucoseTestsDocument.getDouble("count").intValue();
                        glucoseTestsTextView.append(totalNumberOfTests + "");
                    }
                    else
                        glucoseTestsTextView.append("0");
                });

        patientRef
                .collection("tests")
                .document("hypertension_test")
                .get()
                .addOnSuccessListener(hypertensionTestsDocument ->
                {
                    if (hypertensionTestsDocument.exists())
                    {
                        int totalNumberOfTests = hypertensionTestsDocument
                                .getDouble("count").intValue();

                        hypertensionTestsTextView.append(totalNumberOfTests + "");
                    }
                    else
                        hypertensionTestsTextView.append("0");

                });

        patientRef
                .collection("tests")
                .document("cholesterolAndFats_test")
                .get()
                .addOnSuccessListener(cholesterolAndFatsDocument ->
                {
                    if (cholesterolAndFatsDocument.exists())
                    {
                        int totalNumberOfTests = cholesterolAndFatsDocument
                                .getDouble("count").intValue();
                        cholesterolAndFatsTextView.append(totalNumberOfTests + "");
                    }
                    else
                        cholesterolAndFatsTextView.append("0");
                });
    }


    private void setupBodyInfoCardView()
    {
        TextView weightTextView, heightTextView, bmiTextView;

        Button bodyInfoButton = findViewById(R.id.bodyInfoButton);

        weightTextView = findViewById(R.id.weightTextView);
        heightTextView = findViewById(R.id.heightTextView);
        bmiTextView = findViewById(R.id.bmiTextView);

        patientRef.get().addOnSuccessListener(patientDocument ->
        {
            if (patientDocument.exists())
            {
                Patient patient = patientDocument.toObject(Patient.class);

                weightTextView.append(patient.getWeight() + " KG");
                heightTextView.append(patient.getHeight() + "  M");
                bmiTextView.append(patient.getBmi() + "");
            }

            bodyInfoButton.setOnClickListener(v ->
                    startActivity(new Intent(this, PatientBodyInfoActivity.class)));
        });
    }


    private void setupLatestThreeTestCardViews()
    {
        ConstraintLayout firstTestLayout, secondTestLayout, thirdTestLayout;

        TextView firstTestTypeTextView, firstTestLevelTextView, firstTestTimeTextView,
                secondTestTypeTextView, secondTestLevelTextView, secondTestTimeTextView,
                thirdTestTypeTextView, thirdTestLevelTextView, thirdTestTimeTextView,
                noTestsAvailableTextView;


        firstTestLayout = findViewById(R.id.firstTestLayout);
        secondTestLayout = findViewById(R.id.secondTestLayout);
        thirdTestLayout = findViewById(R.id.thirdTestLayout);

        firstTestTypeTextView = findViewById(R.id.firstTestTypeTextView);
        firstTestLevelTextView = findViewById(R.id.firstTestLevelTextView);
        firstTestTimeTextView = findViewById(R.id.firstTestTimeTextView);
        secondTestTypeTextView = findViewById(R.id.secondTestTypeTextView);
        secondTestLevelTextView = findViewById(R.id.secondTestLevelTextView);
        secondTestTimeTextView = findViewById(R.id.secondTestTimeTextView);
        thirdTestTypeTextView = findViewById(R.id.thirdTestTypeTextView);
        thirdTestLevelTextView = findViewById(R.id.thirdTestLevelTextView);
        thirdTestTimeTextView = findViewById(R.id.thirdTestTimeTextView);

        noTestsAvailableTextView = findViewById(R.id.noTestsAvailableTextView);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d hh:mm aa", Locale.US);

        CollectionReference testsRef = patientRef.collection("tests");

        List<DailyTest> latestTests = new ArrayList<>();

        String todayDate = getTodayDate();

        testsRef
                .document("glucose_test")
                .collection(todayDate)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(glucoseDocuments ->
                {
                    for (int i = 0; i < glucoseDocuments.size(); i++)
                    {
                        DocumentSnapshot glucoseDocument = glucoseDocuments.getDocuments().get(i);

                        String type = glucoseDocument.getString("type");
                        Double level = glucoseDocument.getDouble("glucose_percent");
                        Timestamp timestamp = glucoseDocument.getTimestamp("timestamp");

                        DailyTest dailyTest = new DailyTest(type, level, timestamp);
                        latestTests.add(dailyTest);
                    }

                    testsRef.document("hypertension_test")
                            .collection(todayDate)
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .limit(3)
                            .get()
                            .addOnSuccessListener(hypertensionDocuments ->
                            {
                                for (int i = 0; i < hypertensionDocuments.size(); i++)
                                {
                                    DocumentSnapshot hypertensionDocument = hypertensionDocuments.getDocuments().get(i);

                                    String type = hypertensionDocument.getString("type");
                                    Double level = hypertensionDocument.getDouble("hypertension_percent");
                                    Timestamp timestamp = hypertensionDocument.getTimestamp("timestamp");

                                    DailyTest dailyTest = new DailyTest(type, level, timestamp);
                                    latestTests.add(dailyTest);
                                }

                                testsRef.document("cholesterolAndFats_test")
                                        .collection(todayDate)
                                        .orderBy("timestamp", Query.Direction.DESCENDING)
                                        .limit(3)
                                        .get()
                                        .addOnSuccessListener(cholesterolDocuments ->
                                        {
                                            for (int i = 0; i < cholesterolDocuments.size(); i++)
                                            {
                                                DocumentSnapshot cholesterolDocument = cholesterolDocuments.getDocuments().get(i);

                                                String type = cholesterolDocument.getString("type");
                                                Double level = cholesterolDocument.getDouble("CholesterolTotal_percent");
                                                Timestamp timestamp = cholesterolDocument.getTimestamp("timestamp");
                                                DailyTest dailyTest = new DailyTest(type, level, timestamp);
                                                latestTests.add(dailyTest);
                                            }

                                            Collections.sort(latestTests);

                                            if (latestTests.size() != 0)
                                            {
                                                for (int i = 0; i < latestTests.size() && i < 3; i++)
                                                {
                                                    String testType = latestTests.get(i).getType();
                                                    String testLevel = "";
                                                    if (testType.equals("glucose"))
                                                        testLevel = latestTests.get(i).getLevel() + " mg/dl";
                                                    else if (testType.equals("hypertension"))
                                                        testLevel = latestTests.get(i).getLevel() + " mm Hg";
                                                    else if (testType.equals("cholesterol"))
                                                        testLevel = latestTests.get(i).getLevel() + " mg/dl";

                                                    Timestamp timestamp = latestTests.get(i).getTimestamp();

                                                    if (i == 0)
                                                    {
                                                        if (testType.equals("glucose"))
                                                            firstTestTypeTextView.setTextColor(getColor(R.color.glucose_color));
                                                        else if (testType.equals("hypertension"))
                                                            firstTestTypeTextView.setTextColor(getColor(R.color.blood_pressure_color));
                                                        else if (testType.equals("cholesterol"))
                                                            firstTestTypeTextView.setTextColor(getColor(R.color.total_cholesterol_color));

                                                        firstTestTypeTextView.setText(testType);
                                                        firstTestLevelTextView.setText(testLevel);
                                                        firstTestTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                                                        firstTestLayout.setVisibility(View.VISIBLE);
                                                    }
                                                    else if (i == 1)
                                                    {
                                                        if (testType.equals("glucose"))
                                                            secondTestTypeTextView.setTextColor(getColor(R.color.glucose_color));
                                                        else if (testType.equals("hypertension"))
                                                            secondTestTypeTextView.setTextColor(getColor(R.color.blood_pressure_color));
                                                        else if (testType.equals("cholesterol"))
                                                            secondTestTypeTextView.setTextColor(getColor(R.color.total_cholesterol_color));

                                                        secondTestTypeTextView.setText(testType);
                                                        secondTestLevelTextView.setText(testLevel);
                                                        secondTestTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                                                        secondTestLayout.setVisibility(View.VISIBLE);
                                                    } else if (i == 2)
                                                    {
                                                        if (testType.equals("glucose"))
                                                            thirdTestTypeTextView.setTextColor(getColor(R.color.glucose_color));
                                                        else if (testType.equals("hypertension"))
                                                            thirdTestTypeTextView.setTextColor(getColor(R.color.blood_pressure_color));
                                                        else if (testType.equals("cholesterol"))
                                                            thirdTestTypeTextView.setTextColor(getColor(R.color.total_cholesterol_color));

                                                        thirdTestTypeTextView.setText(testType);
                                                        thirdTestLevelTextView.setText(testLevel);
                                                        thirdTestTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                                                        thirdTestLayout.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            }
                                            else
                                                noTestsAvailableTextView.setVisibility(View.VISIBLE);
                                        });
                            });
                });
    }


    private void setupNextThreeAppointmentsCardViews()
    {
        ConstraintLayout firstAppointmentLayout, secondAppointmentLayout, thirdAppointmentLayout;

        TextView firstDoctorNameTextView, firstAppointmentTypeTextView, firstAppointmentTimeTextView,
                secondDoctorNameTextView, secondAppointmentTypeTextView, secondAppointmentTimeTextView,
                thirdDoctorNameTextView, thirdAppointmentTypeTextView, thirdAppointmentTimeTextView,
                noAppointmentsAvailableTextView;


        firstAppointmentLayout = findViewById(R.id.firstAppointmentLayout);
        secondAppointmentLayout = findViewById(R.id.secondAppointmentLayout);
        thirdAppointmentLayout = findViewById(R.id.thirdAppointmentLayout);

        firstAppointmentTypeTextView = findViewById(R.id.firstAppointmentTypeTextView);
        firstDoctorNameTextView = findViewById(R.id.firstDoctorNameTextView);
        firstAppointmentTimeTextView = findViewById(R.id.firstAppointmentTimeTextView);
        secondAppointmentTypeTextView = findViewById(R.id.secondAppointmentTypeTextView);
        secondDoctorNameTextView = findViewById(R.id.secondDoctorNameTextView);
        secondAppointmentTimeTextView = findViewById(R.id.secondAppointmentTimeTextView);
        thirdAppointmentTypeTextView = findViewById(R.id.thirdAppointmentTypeTextView);
        thirdDoctorNameTextView = findViewById(R.id.thirdDoctorNameTextView);
        thirdAppointmentTimeTextView = findViewById(R.id.thirdAppointmentTimeTextView);

        noAppointmentsAvailableTextView = findViewById(R.id.noAppointmentsAvailableTextView);

        patientRef
                .collection("appointments")
                .whereEqualTo("expired", false)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .limit(3)
                .get().addOnSuccessListener(nextThreeAppointmentsDocuments ->
        {
            if (!nextThreeAppointmentsDocuments.isEmpty())
            {
                List<DocumentSnapshot> appointments = nextThreeAppointmentsDocuments.getDocuments();


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d hh:mm aa", Locale.US);

                for (int i = 0; i < appointments.size(); i++)
                {
                    String doctorName = appointments.get(i).getString("doctorName");
                    String appointmentType = appointments.get(i).getString("type");
                    Timestamp timestamp = appointments.get(i).getTimestamp("timestamp");

                    if (i == 0)
                    {
                        firstDoctorNameTextView.setText(doctorName);
                        firstAppointmentTypeTextView.setText(appointmentType);
                        firstAppointmentTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                        firstAppointmentLayout.setVisibility(View.VISIBLE);
                    }
                    else if (i == 1)
                    {
                        secondDoctorNameTextView.setText(doctorName);
                        secondAppointmentTypeTextView.setText(appointmentType);
                        secondAppointmentTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                        secondAppointmentLayout.setVisibility(View.VISIBLE);
                    }
                    else if (i == 2)
                    {
                        thirdDoctorNameTextView.setText(doctorName);
                        thirdAppointmentTypeTextView.setText(appointmentType);
                        thirdAppointmentTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                        thirdAppointmentLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
            else
                noAppointmentsAvailableTextView.setVisibility(View.VISIBLE);
        });
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


    @SuppressLint("SetTextI18n")
    private void showWelcomeDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_welcome, null);
        Button startButton = view.findViewById(R.id.startButton);
        TextView welcomeTextView = view.findViewById(R.id.welcomeTextView);
        welcomeTextView.setText(welcomeTextView.getText() + " " + patientName);

        AlertDialog welcomeDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();

        welcomeDialog.show();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);    // height in pixels for device
        welcomeDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, height);
        welcomeDialog.setContentView(view);

        startButton.setOnClickListener(v ->
        {
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


    private boolean isValidWeight(String weight)
    {
        if (weight.isEmpty() || weight.equals("."))
            return false;

        double weight1 = Double.parseDouble(weight);

        return weight1 > 0 && weight1 <= 300;
    }


    private boolean isValidHeight(String height)
    {
        if (height.isEmpty() || height.equals("."))
            return false;

        double height1 = Double.parseDouble(height);

        return height1 > 0 && height1 <= 2.5;
    }


    private void showPatientDialog1()
    {
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.dialog_patient1, null);

        CheckBox diabetesCheckBox, hypertensionCheckBox, cholesterolCheckBox;
        TextInputLayout diabetesTypeInputLayout, hypertensionTypeInputLayout, cholesterolTypeInputLayout;
        TextInputEditText diabetesTypeInputEditText, hypertensionTypeInputEditText,
                cholesterolTypeInputEditText;

        diabetesCheckBox = view.findViewById(R.id.diabetesCheckBox);
        hypertensionCheckBox = view.findViewById(R.id.bloodPressureCheckBox);
        cholesterolCheckBox = view.findViewById(R.id.cholesterolCheckBox);
        diabetesTypeInputEditText = view.findViewById(R.id.diabetesTypeInputEditText);
        hypertensionTypeInputEditText = view.findViewById(R.id.hypertensionTypeInputEditText);
        cholesterolTypeInputEditText = view.findViewById(R.id.cholesterolTypeInputEditText);
        diabetesTypeInputLayout = view.findViewById(R.id.diabetesTypeInputLayout);
        hypertensionTypeInputLayout = view.findViewById(R.id.hypertensionTypeInputLayout);
        cholesterolTypeInputLayout = view.findViewById(R.id.cholesterolTypeInputLayout);


        diabetesTypeInputEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (isValidDiseaseType(diabetesTypeInputEditText.getText().toString()))
                    diabetesTypeInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {  }
        });

        hypertensionTypeInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (isValidDiseaseType(hypertensionTypeInputEditText.getText().toString()))
                    hypertensionTypeInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }

        });

        cholesterolTypeInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (isValidDiseaseType(cholesterolTypeInputEditText.getText().toString()))
                    cholesterolTypeInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        /* enable Input Edit Text When checkbox is checked and disabled Input Edit Text if not */
        diabetesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            diabetesTypeInputLayout.setEnabled(isChecked);
            diabetesTypeInputEditText.requestFocus();
        });

        hypertensionCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            hypertensionTypeInputLayout.setEnabled(isChecked);
            hypertensionTypeInputEditText.requestFocus();
        });

        cholesterolCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            cholesterolTypeInputLayout.setEnabled(isChecked);
            cholesterolTypeInputEditText.requestFocus();
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
            cholesterolTypeInputLayout.setError(null);

            String diabetesType = diabetesTypeInputEditText.getText().toString().trim();
            String hypertensionType = hypertensionTypeInputEditText.getText().toString().trim();
            String cholesterolType = cholesterolTypeInputEditText.getText().toString().trim();

            Disease diabetes, hypertension, cholesterol;
            diabetes = hypertension = cholesterol = null;

            if (!diabetesCheckBox.isChecked() && !hypertensionCheckBox.isChecked() && !cholesterolCheckBox.isChecked())
                Toasty.showText(this, "Please select at least 1 disease", Toasty.WARNING, Toast.LENGTH_LONG);
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


                if (hypertensionCheckBox.isChecked())
                {
                    if (!isValidDiseaseType(hypertensionType))
                    {
                        hypertensionTypeInputLayout.setError("Invalid Disease type, disease type must containts alphabetic and spaces only");
                        hypertensionTypeInputEditText.requestFocus();
                        error = true;
                    }
                    else
                        hypertension = new Disease("hypertension", hypertensionType, "", false);
                }

                if (cholesterolCheckBox.isChecked())
                {
                    if (!isValidDiseaseType(cholesterolType))
                    {
                        cholesterolTypeInputLayout.setError("Invalid Disease type, disease type must containts alphabetic and spaces only");
                        cholesterolTypeInputEditText.requestFocus();
                        error = true;
                    } else
                        cholesterol = new Disease("cholesterol", cholesterolType, "", false);
                }

                if (error == false)     // if there is no any errors in validating the inputs
                {
                    showPatientDialog2(diabetes, hypertension, cholesterol);
                    patientDialog1.dismiss();
                }
            }
        });
    }


    void showPatientDialog2(Disease diabetes, Disease hypertension, Disease cholesterol)
    {
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.dialog_patient2, null);

        Button selectDiabetesDetectionDateButton, selectHypertensionDetectionDateButton, selectCholesterolDetectionDateButton;

        CheckBox diabetesInheritedCheckBox, hypertensionInheritedCheckBox, cholesterolInheritedCheckBox;

        TextView selectedDiabetesDetectionDateTextView, selectedHypertensionDetectionDateTextView,
                selectedCholesterolDetectionDateTextView, diabetesDetectionTextView,
                hypertensionDetectionDateTextView, cholesterolDetectionDateTextView;

        diabetesDetectionTextView = view.findViewById(R.id.diabetesDetectionDateTextView);
        hypertensionDetectionDateTextView = view.findViewById(R.id.hypertensionDetectionDateTextView);
        cholesterolDetectionDateTextView = view.findViewById(R.id.cholesterolDetectionDateTextView);
        selectedDiabetesDetectionDateTextView = view.findViewById(R.id.selectedDiabetesDetectionDateTextView);
        selectedHypertensionDetectionDateTextView = view.findViewById(R.id.selectedHypertensionDetectionDateTextView);
        selectedCholesterolDetectionDateTextView = view.findViewById(R.id.selectedCholesterolDetectionDateTextView);
        selectDiabetesDetectionDateButton = view.findViewById(R.id.selectDiabetesDetectionDateButton);
        selectHypertensionDetectionDateButton = view.findViewById(R.id.selectHypertensionDetectionDateButton);
        selectCholesterolDetectionDateButton = view.findViewById(R.id.selectCholesterolDateButton);

        diabetesInheritedCheckBox = view.findViewById(R.id.diabetesInheritedCheckBox);
        hypertensionInheritedCheckBox = view.findViewById(R.id.hypertensionInheritedCheckBox);
        cholesterolInheritedCheckBox = view.findViewById(R.id.cholesterolInheritedCheckBox);

        weightInputLayout = view.findViewById(R.id.weightInputLayout);
        heightInputLayout = view.findViewById(R.id.heightInputLayout);
        weightInputEditText = view.findViewById(R.id.weightInputEditText);
        heightInputEditText = view.findViewById(R.id.heightInputEditText);
        bmiInputEditText = view.findViewById(R.id.bmiInputEditText);

        TextInputEditTextFocusListenerHelper.add(this, weightInputEditText);
        TextInputEditTextFocusListenerHelper.add(this, heightInputEditText);

        addOnTextChangeListenersForInputEditText();

        selectDiabetesDetectionDateButton.setOnClickListener(view1 ->
                showDateDialog(selectedDiabetesDetectionDateTextView)
        );

        selectHypertensionDetectionDateButton.setOnClickListener(view1 ->
                showDateDialog(selectedHypertensionDetectionDateTextView)
        );

        selectCholesterolDetectionDateButton.setOnClickListener(view1 ->
        {
            showDateDialog(selectedCholesterolDetectionDateTextView);
            cholesterol.setDetectionDate(selectedCholesterolDetectionDateTextView.getText().toString());
        });


        if (diabetes == null)
        {
            diabetesInheritedCheckBox.setVisibility(View.GONE);
            diabetesDetectionTextView.setVisibility(View.GONE);
            selectDiabetesDetectionDateButton.setVisibility(View.GONE);
        }

        if (hypertension == null)
        {
            hypertensionInheritedCheckBox.setVisibility(View.GONE);
            hypertensionDetectionDateTextView.setVisibility(View.GONE);
            selectHypertensionDetectionDateButton.setVisibility(View.GONE);
        }

        if (cholesterol == null)
        {
            cholesterolInheritedCheckBox.setVisibility(View.GONE);
            cholesterolDetectionDateTextView.setVisibility(View.GONE);
            selectCholesterolDetectionDateButton.setVisibility(View.GONE);
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
            weightInputLayout.setError(null);
            heightInputLayout.setError(null);

            String error = "";

            if (diabetes != null)
            {
                String diabetesDetectionDate = selectedDiabetesDetectionDateTextView.getText().toString();
                if (diabetesDetectionDate.isEmpty())
                    error = error + "Please enter detection date for diabetes.\n";
                else
                    diabetes.setDetectionDate(selectedDiabetesDetectionDateTextView.getText().toString());
            }

            if (hypertension != null)
            {
                String hypertensionDetectionDate = selectedHypertensionDetectionDateTextView.getText().toString();
                if (hypertensionDetectionDate.isEmpty())
                    error = error + "Please enter detection date for hypertension.\n";
                else
                    hypertension.setDetectionDate(selectedHypertensionDetectionDateTextView.getText().toString());
            }

            if (cholesterol != null)
            {
                String cholesterolDetectionDate = selectedCholesterolDetectionDateTextView.getText().toString();
                if (cholesterolDetectionDate.isEmpty())
                    error = error + "Please enter detection date for cholesterol.\n";
                else
                    cholesterol.setDetectionDate(selectedCholesterolDetectionDateTextView.getText().toString());
            }


            String weightString = weightInputEditText.getText().toString().trim();
            String heightString = heightInputEditText.getText().toString().trim();


            if (!error.isEmpty())
            {
                Toasty.showText(this, error, Toasty.WARNING, Toast.LENGTH_LONG);
            }
            else if (!isValidWeight(weightString))
            {
                weightInputLayout.setError("Invalid weight, weight must be less than 300 KG");
                weightInputEditText.requestFocus();
            }
            else if (!isValidHeight(heightString))
            {
                heightInputLayout.setError("Invalid height, height must be in meters..  ex: 2.5");
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


                DocumentReference bodyInfoRef = db.collection("patients")
                        .document(patientId).collection("bodyInfoRecords").document();
                BodyInfo bodyInfo = new BodyInfo(bodyInfoRef.getId(), weight, height,
                        bmi, Timestamp.now());

                DocumentReference patientRef = db.collection("patients").document(patientId);
                CollectionReference diseasesRef = patientRef.collection("diseases");

                WriteBatch batch = db.batch();

                batch.set(bodyInfoRef, bodyInfo);
                batch.update(patientRef, additionalInfo);

                if (diabetes != null)
                {
                    diabetes.setInherited(diabetesInheritedCheckBox.isChecked());

                    DocumentReference diabetesRef = diseasesRef.document();
                    batch.set(diabetesRef, diabetes);
                }
                if (hypertension != null)
                {
                    hypertension.setInherited(hypertensionInheritedCheckBox.isChecked());

                    DocumentReference hypertensionRef = diseasesRef.document();
                    batch.set(hypertensionRef, hypertension);
                }
                if (cholesterol != null)
                {
                    cholesterol.setInherited(cholesterolInheritedCheckBox.isChecked());

                    DocumentReference cholesterolRef = diseasesRef.document();
                    batch.set(cholesterolRef, cholesterol);
                }

                batch.commit().addOnCompleteListener(task ->
                {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.showProgressDialog("Updating Profile...");
                    if (task.isSuccessful()) {
                        setupCardViews();
                        patientDialog2.dismiss();


                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run() {
                                progressDialog.dismissProgressDialog();
                                Toasty.showText(PatientHomeActivity.this, "Your profile updated successfully",
                                        Toasty.INFORMATION, Toast.LENGTH_LONG);
                            }
                        }, 3000);

                        /* this shared preferences is used when the user is using the app right after register
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


    private void addOnTextChangeListenersForInputEditText()
    {
        weightInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String weightString = weightInputEditText.getText().toString();
                String heightString = heightInputEditText.getText().toString();

                bmiInputEditText.setText("");

                if (isValidWeight(weightString)) {
                    weightInputLayout.setError(null);

                    if (isValidHeight(heightString))
                    {
                        double bmi = calculateBMI(weightString, heightString);
                        bmiInputEditText.setText(bmi + "");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        heightInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String weightString = weightInputEditText.getText().toString();
                String heightString = heightInputEditText.getText().toString();

                bmiInputEditText.setText("");

                if (isValidHeight(heightString))
                {
                    heightInputLayout.setError(null);

                    if (isValidWeight(weightString))
                    {
                        double bmi = calculateBMI(weightString, heightString);
                        bmiInputEditText.setText(bmi + "");
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }


    private double calculateBMI(String weightString, String heightString)
    {
        double weight = Double.parseDouble(weightString);
        double height = Double.parseDouble(heightString);
        double bmi = weight / Math.pow(height, 2);

        BigDecimal bd = new BigDecimal(bmi).setScale(2, RoundingMode.HALF_UP);
        bmi = bd.doubleValue();

        return bmi;
    }


    private void showDateDialog(TextView textView)
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
            String dateText = DateFormat.format("MM/dd/yyyy", selectedDate).toString();

            textView.setText(dateText);
            textView.setVisibility(TextView.VISIBLE);
        });

        materialDatePicker.show(getSupportFragmentManager(), "PatientHomeActivity");
    }

    private String getTodayDate()
    {
        return DateFormat.format("yyyy-M-d", new Date()).toString();
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.header_drawer, null);

        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView emailTextView = view.findViewById(R.id.emailTextView);

        nameTextView.setText(firebaseAuth.getCurrentUser().getDisplayName());
        emailTextView.setText(firebaseAuth.getCurrentUser().getEmail());
        DrawerUtil.headerView = view;
        DrawerUtil.getPatientDrawer(this, -1);
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