package com.example.healthza.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthza.LoadingDialog;
import com.example.healthza.R;
import com.example.healthza.Toasty;
import com.example.healthza.models.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatientAccountActivity extends AppCompatActivity
{

    private EditText patientNameEditText, patientIdentificationNumberEditText, patientEmailEditText
            , patientPhoneNumberEditText, patientWeightEditText, patientHeightEditText;
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

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.showLoadingDialog();

        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        password = sharedPreferences.getString("password", "");

        patientNameEditText = findViewById(R.id.patientNameEditText);
        patientIdentificationNumberEditText = findViewById(R.id.patientIdentificationNumberEditText);
        patientEmailEditText = findViewById(R.id.patientEmailEditText);
        patientPhoneNumberEditText = findViewById(R.id.patientPhoneNumberEditText);
        patientWeightEditText = findViewById(R.id.patientWeightEditText);
        patientHeightEditText = findViewById(R.id.patientHeightEditText);
        selectedBirthDateTextView = findViewById(R.id.selectedBirthDateTextView);

        patientSexRadioGroup = findViewById(R.id.patientSexRadioGroup);

        selectBirthDateButton = findViewById(R.id.selectBirthDateButton);
        changeAccountPasswordButton = findViewById(R.id.changeAccountPasswordButton);
        saveButton = findViewById(R.id.saveButton);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        patientId = firebaseAuth.getCurrentUser().getUid();

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

            loadingDialog.dismissLoadingDialog();
        });


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

    private boolean isValidIdentificationNumber(String id) {
        return id.length() == 16;
    }


    private boolean isValidEmail(String email) // No need to check if the field is empty
    {                                          // because regex won't match empty strings
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber)  // No need to check if the field is empty
    {                                                       // because regex won't match empty strings
        return Patterns.PHONE.matcher(phoneNumber).matches();
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
            Toasty.showText(this, "please select your sex", Toasty.WARNING, Toast.LENGTH_LONG);
            patientSexRadioGroup.requestFocus();
        }
        else if (birthDate.isEmpty()) {
            Toasty.showText(this, "please select your birthdate", Toasty.WARNING, Toast.LENGTH_LONG);
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
            LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.showLoadingDialog();

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
                                Toasty.showText(this, "account information updated successfully",
                                        Toasty.SUCCESS, Toast.LENGTH_LONG);
                            else
                                Toasty.showText(this, "something went wrong...",
                                        Toasty.ERROR, Toast.LENGTH_LONG);
                            loadingDialog.dismissLoadingDialog();
                        });
            }
            else
                loadingDialog.dismissLoadingDialog();
        }
    }


    private void showDateDialog() {
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

                String todayDate = DateFormat.format("MM/dd/yyyy", calendar).toString();

                selectedBirthDateTextView.setText(todayDate);
            }
        }, year, month, date);

        datePickerDialog.show();
    }
}