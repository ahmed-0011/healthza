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

import com.example.healthza.DrawerUtil;
import com.example.healthza.LoadingDialog;
import com.example.healthza.Toasty;
import com.example.healthza.models.Disease;
import com.example.healthza.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatientHomeActivity extends AppCompatActivity
{
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String userId;
    String patientName;

    TextInputLayout weightInputlayout, heightInputlayout;
    TextInputEditText weightInputEditText, heightInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        patientName = firebaseAuth.getCurrentUser().getDisplayName();


        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        boolean userCompleteInfo = sharedPreferences.getBoolean("user_complete_info", false);

        if (!userCompleteInfo)
            showWelcomeDialog();


        ChipNavigationBar chipNavigationBar = findViewById(R.id.bottomNavigationBar);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(int i)
            {
                if (i == R.id.homeItem);
                else if (i == R.id.medicalHistoryItem)
                    startActivity(new Intent(PatientHomeActivity.this, medicalRecords.class));
                else if (i == R.id.chartsItem);
                else if (i == R.id.appointmentsItem) ;
                    //TODO
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
        hypertensionCheckBox = view.findViewById(R.id.hypertensionCheckBox);
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
                ;
                //TODO
            else {
                boolean error = false;

                if (diabetesCheckBox.isChecked()) {
                    if (!isValidDiseaseType(diabetesType)) {
                        diabetesTypeInputLayout.setError("Invalid Disease type, disease type must containts alphabetic and spaces only");
                        diabetesTypeInputEditText.requestFocus();
                        error = true;
                    } else
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
                        cholestrol = new Disease("cholestrol", cholestrolType, "", false);
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

            if (diabetes != null) {
                String diabetesDetectionDate = selectedDiabetesDetectionDateTextView.getText().toString();
                if (diabetesDetectionDate.isEmpty())
                    error = error + "enter detection date for diabetes.\n";
                else
                    diabetes.setDiagnosisDate(selectedDiabetesDetectionDateTextView.getText().toString());
            }

            if (hypertension != null) {
                String hypertensionDetectionDate = selectedHypertensionDetectionDateTextView.getText().toString();
                if (hypertensionDetectionDate.isEmpty())
                    error = error + "enter detection date for hypertenstion.\n";
                else
                    hypertension.setDiagnosisDate(selectedHypertensionDetectionDateTextView.getText().toString());
            }

            if (cholestrol != null) {
                String cholestrolDetectionDate = selectedCholestrolDetectionDateTextView.getText().toString();
                if (cholestrolDetectionDate.isEmpty())
                    error = error + "enter detection date for cholestrol.\n";
                else
                    cholestrol.setDiagnosisDate(selectedCholestrolDetectionDateTextView.getText().toString());
            }


            String weightString = weightInputEditText.getText().toString().trim();
            String heightString = heightInputEditText.getText().toString().trim();


            if (!error.isEmpty())
            {
                new MaterialAlertDialogBuilder(PatientHomeActivity.this)
                        .setTitle("Detection date")
                        .setMessage(error)
                        .setNegativeButton("Ok", (dialog, which) ->
                        { }).show();
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

                DocumentReference patientRef = db.collection("patients").document(userId);
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
                    LoadingDialog loadingDialog = new LoadingDialog(this);
                    loadingDialog.showLoadingDialog();
                    if (task.isSuccessful())
                    {
                        patientDialog2.dismiss();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                loadingDialog.dismissLoadingDialog();
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
                                "an error occurred while trying to update your profile",
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

    private void showDateDialog(TextView textView) {

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
}