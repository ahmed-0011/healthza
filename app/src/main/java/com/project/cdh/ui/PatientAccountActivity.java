package com.project.cdh.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.project.cdh.DrawerUtil;
import com.project.cdh.ProgressDialog;
import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.project.cdh.models.BodyInfo;
import com.project.cdh.models.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatientAccountActivity extends AppCompatActivity
{
    private EditText patientNameEditText, patientIdentificationNumberEditText, patientEmailEditText
            , patientPhoneNumberEditText, patientWeightEditText, patientHeightEditText, patientBMIEditText;
    private RadioGroup patientSexRadioGroup;
    private TextView selectedBirthDateTextView;
    private Button selectBirthDateButton, changeAccountPasswordButton, saveButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String patientId;
    private String password;
    private String name, sex, birthDate, identificationNumber, email, phoneNumber,
            weight, height;
    private boolean informationChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_account);

        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        password = sharedPreferences.getString("password", "");

        patientNameEditText = findViewById(R.id.patientNameEditText);
        patientIdentificationNumberEditText = findViewById(R.id.patientIdentificationNumberEditText);
        patientEmailEditText = findViewById(R.id.patientEmailEditText);
        patientPhoneNumberEditText = findViewById(R.id.patientPhoneNumberEditText);
        patientWeightEditText = findViewById(R.id.patientWeightEditText);
        patientHeightEditText = findViewById(R.id.patientHeightEditText);
        patientBMIEditText = findViewById(R.id.patientBMIEditText);
        selectedBirthDateTextView = findViewById(R.id.selectedBirthDateTextView);

        patientSexRadioGroup = findViewById(R.id.patientSexRadioGroup);

        selectBirthDateButton = findViewById(R.id.selectBirthDateButton);
        changeAccountPasswordButton = findViewById(R.id.changeAccountPasswordButton);
        saveButton = findViewById(R.id.saveButton);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        patientId = firebaseAuth.getCurrentUser().getUid();

        initAccountInformation();

        addOnTextChangeListenersForWeightAndHeightEditText();

        selectBirthDateButton.setOnClickListener(v ->
        {
            showDateDialog();
        });

        changeAccountPasswordButton.setOnClickListener(v ->
        {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        saveButton.setOnClickListener(v ->
        {
            informationChanged = false;
            saveAccountInformation();
        });
    }

    private boolean isValidName(String name) // No need to check if the field is empty
    {                                        // because Regex won't match empty strings
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("[A-Za-z ]+");
        matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean isValidIdentificationNumber(String id)
    {
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

    private boolean isValidWeight(String weight)
    {
        if (weight.isEmpty())
            return false;

        double weight1 = Double.parseDouble(weight);

        return weight1 <= 300;
    }

    private boolean isValidHeight(String height)
    {
        if (height.isEmpty())
            return false;

        double height1 = Double.parseDouble(height);

        return height1 <= 2.5;

    }


    private void initAccountInformation()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.showProgressDialog();

        db.collection("patients")
                .document(patientId)
                .get().addOnSuccessListener(patientDocument ->
        {
            if(patientDocument.exists())
            {
                Patient patient = patientDocument.toObject(Patient.class);

                name = patient.getName();
                sex = patient.getSex();
                birthDate = patient.getBirthDate();
                identificationNumber = patient.getIdentificationNumber();
                email = patient.getEmail();
                phoneNumber = patient.getPhoneNumber();
                weight = patient.getWeight() + "";
                height = patient.getHeight() + "";

                patientNameEditText.setText(name);
                if(sex.equals("male"))
                    patientSexRadioGroup.check(R.id.maleRadioButton);
                else
                    patientSexRadioGroup.check(R.id.femaleRadioButton);
                selectedBirthDateTextView.setText(birthDate);
                selectedBirthDateTextView.setVisibility(View.VISIBLE);
                patientIdentificationNumberEditText.setText(identificationNumber);
                patientEmailEditText.setText(email);
                patientPhoneNumberEditText.setText(phoneNumber);
                patientWeightEditText.setText(weight);
                patientHeightEditText.setText(height);
            }

            progressDialog.dismissProgressDialog();
        });
    }


    private void saveAccountInformation()
    {

        String newName = patientNameEditText.getText().toString().trim();
        String newSex = patientSexRadioGroup.getCheckedRadioButtonId() ==
                R.id.maleRadioButton ? "male" : "female";
        String newBirthDate = selectedBirthDateTextView.getText().toString();
        String newIdentificationNumber = patientIdentificationNumberEditText.getText().toString().trim();
        String newEmail = patientEmailEditText.getText().toString().trim();
        String newPhoneNumber = patientPhoneNumberEditText.getText().toString().trim();

        String weightString = patientWeightEditText.getText().toString();
        String heightString = patientHeightEditText.getText().toString();


        if (!isValidName(newName))
            Toasty.showText(this, "Invalid name, name must contains alphabetic and spaces only"
                    , Toasty.WARNING, Toast.LENGTH_LONG);
        else if (patientSexRadioGroup.getCheckedRadioButtonId() == -1)
        {
            Toasty.showText(this, "Please select your sex", Toasty.WARNING, Toast.LENGTH_LONG);
            patientSexRadioGroup.requestFocus();
        }
        else if (birthDate.isEmpty()) {
            Toasty.showText(this, "Please select your birthdate", Toasty.WARNING, Toast.LENGTH_LONG);
            selectedBirthDateTextView.requestFocus();
        }
        else if (!isValidIdentificationNumber(newIdentificationNumber))
        {
            Toasty.showText(this, getString(R.string.enter_a_valid_id)
                    , Toasty.WARNING, Toast.LENGTH_LONG);
            patientIdentificationNumberEditText.requestFocus();
        }
        else if (!isValidEmail(newEmail))
        {
            Toasty.showText(this, getString(R.string.enter_a_valid_email)
                    , Toasty.WARNING, Toast.LENGTH_LONG);
            patientEmailEditText.requestFocus();
        }
        else if (!isValidPhoneNumber(newPhoneNumber))
        {
            Toasty.showText(this, getString(R.string.enter_a_valid_phonenumber)
                    , Toasty.WARNING, Toast.LENGTH_LONG);
            patientPhoneNumberEditText.requestFocus();
        }
        else if (!isValidWeight(weightString))
        {
            Toasty.showText(this, "Invalid weight, weight must be less than 300 KG"
                    , Toasty.WARNING, Toast.LENGTH_LONG);
            patientWeightEditText.requestFocus();
        }
        else if (!isValidHeight(heightString))
        {
            Toasty.showText(this, "Invalid height, height must be in meters..  ex: 2.5"
                    , Toasty.WARNING, Toast.LENGTH_LONG);
            patientHeightEditText.requestFocus();
        }
        else
        {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.showProgressDialog("Updating Account Information...");

            Map<String, Object> accountInformation = new HashMap<>();

            if(!name.equals(newName))
            {
                accountInformation.put("name", newName);
                name = newName;
                informationChanged = true;
            }
            if(!sex.equals(newSex))
            {
                accountInformation.put("sex", newSex);
                sex = newSex;
                informationChanged = true;
            }
            if(!birthDate.equals(newBirthDate))
            {
                accountInformation.put("birthDate", newBirthDate);
                birthDate = newBirthDate;
                informationChanged = true;
            }
            if(!identificationNumber.equals(newIdentificationNumber))
            {
                db.collection("ids").document(identificationNumber).delete();

                Map<String, Object> userIdField = new HashMap<>();
                userIdField.put("userId", patientId);
                db.collection("ids").document(newIdentificationNumber).set(userIdField);

                accountInformation.put("identificationNumber", newIdentificationNumber);
                identificationNumber = newIdentificationNumber;
                informationChanged = true;
            }
            if(!email.equals(newEmail))
            {
                accountInformation.put("email", newEmail);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, password);

                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {

                        });

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.updateEmail(newEmail);
                                email = newEmail;
                            }
                        });

                informationChanged = true;
            }
            if(!phoneNumber.equals(newPhoneNumber))
            {
                db.collection("phonenumbers").document(phoneNumber).delete();

                Map<String, Object> userIdField = new HashMap<>();
                userIdField.put("userId", patientId);
                db.collection("phonenumbers").document(newPhoneNumber).set(userIdField);

                accountInformation.put("phoneNumber", newPhoneNumber);
                phoneNumber = newPhoneNumber;
                informationChanged = true;
            }
            if(!weightString.equals(weight) || !heightString.equals(height))
            {
                double newWeight = Double.parseDouble(weightString);
                double newHeight = Double.parseDouble(heightString);
                double newBmi = newWeight / Math.pow(newHeight, 2);
                BigDecimal bd = new BigDecimal(newWeight).setScale(2, RoundingMode.HALF_UP);
                newWeight = bd.doubleValue();
                bd = new BigDecimal(newHeight).setScale(2, RoundingMode.HALF_UP);
                newHeight = bd.doubleValue();
                bd = new BigDecimal(newBmi).setScale(2, RoundingMode.HALF_UP);
                newBmi = bd.doubleValue();

                accountInformation.put("weight", newWeight);
                accountInformation.put("height", newHeight);
                accountInformation.put("bmi", newBmi);


                DocumentReference bodyInfoRef = db.collection("patients")
                        .document(patientId)
                        .collection("bodyInfoRecords")
                        .document();

                BodyInfo bodyInfo = new BodyInfo(bodyInfoRef.getId(), newWeight, newHeight,
                        newBmi, Timestamp.now());

                bodyInfoRef.set(bodyInfo);

                weight = weightString;
                height = heightString;
                informationChanged = true;
            }


            if(informationChanged) // update account information only when at least one field changed
            {
                db.collection("patients")
                        .document(patientId).update(accountInformation)
                        .addOnCompleteListener(task ->
                        {
                            if (task.isSuccessful())
                                Toasty.showText(this, "Account information updated successfully",
                                        Toasty.SUCCESS, Toast.LENGTH_LONG);
                            else
                                Toasty.showText(this, "Something went wrong...",
                                        Toasty.ERROR, Toast.LENGTH_LONG);
                            progressDialog.dismissProgressDialog();
                        });
            }
            else
                progressDialog.dismissProgressDialog();
        }
    }

    
    private void addOnTextChangeListenersForWeightAndHeightEditText()
    {
        patientHeightEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String weightString = patientWeightEditText.getText().toString();
                String heightString = patientHeightEditText.getText().toString();
                if(isValidHeight(heightString) && isValidWeight(weightString))
                {
                    double bmi = calculateBMI(weightString, heightString);
                    patientBMIEditText.setText(bmi + "");
                }
                else
                    patientBMIEditText.setText("");

            }

            @Override
            public void afterTextChanged(Editable s) {  }
        });

        patientWeightEditText.addTextChangedListener(new TextWatcher()
        {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            String weightString = patientWeightEditText.getText().toString();
            String heightString = patientHeightEditText.getText().toString();
            if(isValidHeight(heightString) && isValidWeight(weightString))
            {
                double bmi = calculateBMI(weightString, heightString);
                patientBMIEditText.setText(bmi + "");
            }
            else
                patientBMIEditText.setText("");

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

        return  bmi;
    }


    private void showDateDialog()
    {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder
                .datePicker();

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.setValidator(DateValidatorPointBackward.now()).build());

        MaterialDatePicker<Long> materialDatePicker = builder.build();

        materialDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            Long selectedDate = (Long) selection;
            String birthDate = DateFormat.format("MM/dd/yyyy", new Date(selectedDate)).toString();

            selectedBirthDateTextView.setText(birthDate);
        });

        materialDatePicker.show(getSupportFragmentManager(), "PatientAccountActivity");
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getPatientDrawer(this, 0);
    }
}