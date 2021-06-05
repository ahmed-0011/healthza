package com.project.cdh.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.project.cdh.DrawerUtil;
import com.project.cdh.ProgressDialog;
import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.project.cdh.models.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoctorAccountActivity extends AppCompatActivity
{
    private EditText doctorNameEditText, doctorIdentificationNumberEditText, doctorEmailEditText,
            doctorPhoneNumberEditText, doctorSpecialityEditText, doctorYearsOfExperienceEditText,
            doctorWorkplaceEditText;
    private MaterialButtonToggleGroup doctorWorkdaysFirstToggleButton, doctorWorkdaysSecondToggleButton;
    private RadioGroup doctorSexRadioGroup;
    private TextView selectedBirthDateTextView;
    private Button selectBirthDateButton, changeAccountPasswordButton, saveButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String doctorId;
    private String password;
    private String name, sex, birthDate, identificationNumber, email, phoneNumber,
            speciality, workplace,  yearsOfExperience;
    private List<String> workdays;
    private boolean informationChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_account);

        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        password = sharedPreferences.getString("password", "");

        selectedBirthDateTextView = findViewById(R.id.selectedBirthDateTextView);

        doctorNameEditText = findViewById(R.id.doctorNameEditText);
        doctorIdentificationNumberEditText = findViewById(R.id.doctorIdentificationNumberEditText);
        doctorEmailEditText = findViewById(R.id.doctorEmailEditText);
        doctorPhoneNumberEditText = findViewById(R.id.doctorPhoneNumberEditText);
        doctorSpecialityEditText = findViewById(R.id.doctorSpecialityEditText);
        doctorYearsOfExperienceEditText = findViewById(R.id.doctorYearsOfExperienceEditText);
        doctorWorkplaceEditText = findViewById(R.id.doctorWorkplaceEditText);

        doctorWorkdaysFirstToggleButton = findViewById(R.id.doctorWorkdaysFirstToggleButton);
        doctorWorkdaysSecondToggleButton = findViewById(R.id.doctorWorkdaysSecondToggleButton);

        doctorSexRadioGroup = findViewById(R.id.doctorSexRadioGroup);

        selectBirthDateButton = findViewById(R.id.selectBirthDateButton);
        changeAccountPasswordButton = findViewById(R.id.changeAccountPasswordButton);
        saveButton = findViewById(R.id.saveButton);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        doctorId = firebaseAuth.getCurrentUser().getUid();

        initAccountInformation();

        selectBirthDateButton.setOnClickListener(v -> showDateDialog());

        changeAccountPasswordButton.setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class))
        );

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
        return id.length() == 9;
    }


    private boolean isValidEmail(String email)  // No need to check if the field is empty
    {                                           // because regex won't match empty strings
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber)  // No need to check if the field is empty
    {                                                       // because regex won't match empty strings
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }


    private boolean isValidSpeciality(String speciality) // No need to check if the field is empty
    {                                                   // because Regex won't match empty strings
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("[A-Za-z ]+");
        matcher = pattern.matcher(speciality);
        return matcher.matches();
    }

    private boolean isValidYearsOfExperience(String yearsOfExperience)
    {
        int yearsOfExperience1 = Integer.parseInt(yearsOfExperience);

        return yearsOfExperience1 <= 60;
    }

    private boolean isValidWorkplace(String workpalce)  // No need to check if the field is empty
    {                                                   // because Regex won't match empty strings
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("[A-Za-z ]+");
        matcher = pattern.matcher(workpalce);
        return matcher.matches();
    }


    private void initAccountInformation()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.showProgressDialog();

        db.collection("doctors")
                .document(doctorId)
                .get().addOnSuccessListener(doctorDocument ->
        {
            if (doctorDocument.exists()) {
                Doctor doctor = doctorDocument.toObject(Doctor.class);

                name = doctor.getName();
                sex = doctor.getSex();
                birthDate = doctor.getBirthDate();
                identificationNumber = doctor.getIdentificationNumber();
                email = doctor.getEmail();
                phoneNumber = doctor.getPhoneNumber();
                speciality = doctor.getSpeciality();
                yearsOfExperience = doctor.getYearsOfExperience() + "";
                workplace = doctor.getWorkplace();
                workdays = doctor.getWorkdays();

                doctorNameEditText.setText(name);
                if (sex.equals("male"))
                    doctorSexRadioGroup.check(R.id.maleRadioButton);
                else
                    doctorSexRadioGroup.check(R.id.femaleRadioButton);
                selectedBirthDateTextView.setText(birthDate);
                selectedBirthDateTextView.setVisibility(View.VISIBLE);
                doctorIdentificationNumberEditText.setText(identificationNumber);
                doctorEmailEditText.setText(email);
                doctorPhoneNumberEditText.setText(phoneNumber);
                doctorSpecialityEditText.setText(speciality);
                doctorYearsOfExperienceEditText.setText(yearsOfExperience + "");
                doctorWorkplaceEditText.setText(workplace);

                if(workdays.contains("saturday"))
                    doctorWorkdaysFirstToggleButton.check(R.id.saturdayButton);
                if(workdays.contains("sunday"))
                    doctorWorkdaysFirstToggleButton.check(R.id.sundayButton);
                if(workdays.contains("monday"))
                    doctorWorkdaysFirstToggleButton.check(R.id.mondayButton);
                if(workdays.contains("tuesday"))
                    doctorWorkdaysFirstToggleButton.check(R.id.tuesdayButton);
                if(workdays.contains("wednesday"))
                    doctorWorkdaysSecondToggleButton.check(R.id.wednesdayButton);
                if(workdays.contains("thursday"))
                    doctorWorkdaysSecondToggleButton.check(R.id.thursdayButton);
                if(workdays.contains("friday"))
                    doctorWorkdaysSecondToggleButton.check(R.id.fridayButton);
            }

            progressDialog.dismissProgressDialog();
        });

    }

    private void saveAccountInformation()
    {
        String newName = doctorNameEditText.getText().toString().trim();
        String newSex = doctorSexRadioGroup.getCheckedRadioButtonId() ==
                R.id.maleRadioButton ? "male" : "female";
        String newBirthDate = selectedBirthDateTextView.getText().toString();
        String newIdentificationNumber = doctorIdentificationNumberEditText.getText().toString().trim();
        String newEmail = doctorEmailEditText.getText().toString().trim();
        String newPhoneNumber = doctorPhoneNumberEditText.getText().toString().trim();
        String newSpeciality = doctorSpecialityEditText.getText().toString().trim();
        String newYearsOfExperience = doctorYearsOfExperienceEditText.getText().toString();
        String newWorkplace = doctorWorkplaceEditText.getText().toString().trim();

        List<Integer> workdaysGroup1 = doctorWorkdaysFirstToggleButton.getCheckedButtonIds();
        List<Integer> workdaysGroup2 = doctorWorkdaysSecondToggleButton.getCheckedButtonIds();

        List<String> newWorkdays = new ArrayList<>();

        if (workdaysGroup1.contains(R.id.saturdayButton))
            newWorkdays.add("saturday");
        if (workdaysGroup1.contains(R.id.sundayButton))
            newWorkdays.add("sunday");
        if (workdaysGroup1.contains(R.id.mondayButton))
            newWorkdays.add("monday");
        if (workdaysGroup1.contains(R.id.tuesdayButton))
            newWorkdays.add("tuesday");
        if (workdaysGroup2.contains(R.id.wednesdayButton))
            newWorkdays.add("wednesday");
        if (workdaysGroup2.contains(R.id.thursdayButton))
            newWorkdays.add("thursday");
        if (workdaysGroup2.contains(R.id.fridayButton))
            newWorkdays.add("friday");

        if (!isValidName(newName))
        {
            Toasty.showText(this, "Invalid name, name must contains alphabetic and spaces only"
                    , Toasty.WARNING, Toast.LENGTH_LONG);
            doctorNameEditText.requestFocus();
        }
        else if (doctorSexRadioGroup.getCheckedRadioButtonId() == -1)
        {
            Toasty.showText(this, "Please select your sex", Toasty.WARNING, Toast.LENGTH_LONG);
            doctorSexRadioGroup.requestFocus();
        }
        else if (birthDate.isEmpty())
        {
            Toasty.showText(this, "Please select your birthdate", Toasty.WARNING, Toast.LENGTH_LONG);
            selectedBirthDateTextView.requestFocus();
        }
        else if (!isValidIdentificationNumber(newIdentificationNumber))
        {
            Toasty.showText(this, getString(R.string.enter_a_valid_id)
                    , Toasty.WARNING, Toast.LENGTH_LONG);
            doctorIdentificationNumberEditText.requestFocus();
        }
        else if (!isValidEmail(newEmail))
        {
            Toasty.showText(this, getString(R.string.enter_a_valid_email)
                    , Toasty.WARNING, Toast.LENGTH_LONG);
            doctorEmailEditText.requestFocus();
        }
        else if (!isValidPhoneNumber(newPhoneNumber)) {
            Toasty.showText(this, getString(R.string.enter_a_valid_phonenumber)
                    , Toasty.WARNING, Toast.LENGTH_LONG);
            doctorPhoneNumberEditText.requestFocus();
        }
        else if (!isValidSpeciality(newSpeciality))
        {
            Toasty.showText(this, "Invalid speciality, speciality must contains alphabetic and spaces only"
                    , Toasty.WARNING, Toast.LENGTH_LONG);
            doctorSpecialityEditText.requestFocus();
        }
        else if (!isValidYearsOfExperience(newYearsOfExperience))
        {
            Toasty.showText(this, "Invalid years of experience, years can't be more than 60 years"
                    , Toasty.WARNING, Toast.LENGTH_LONG);
            doctorYearsOfExperienceEditText.requestFocus();
        }
        else if (!isValidWorkplace(newWorkplace))
        {
            Toasty.showText(this, "Invalid workplace, workplace must contains alphabetic and spaces only"
                    , Toasty.WARNING, Toast.LENGTH_LONG);
            doctorWorkplaceEditText.requestFocus();
        }
        else
            {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.showProgressDialog("Updating Account Information...");

            Map<String, Object> accountInformation = new HashMap<>();

            if (!name.equals(newName))
            {
                accountInformation.put("name", newName);
                name = newName;
                informationChanged = true;
            }
            if (!sex.equals(newSex)) {
                accountInformation.put("sex", newSex);
                sex = newSex;
                informationChanged = true;
            }
            if (!birthDate.equals(newBirthDate))
            {
                accountInformation.put("birthDate", newBirthDate);
                birthDate = newBirthDate;
                informationChanged = true;
            }
            if (!identificationNumber.equals(newIdentificationNumber))
            {
                db.collection("ids").document(identificationNumber).delete();

                Map<String, Object> userIdField = new HashMap<>();
                userIdField.put("userId", doctorId);
                db.collection("ids").document(newIdentificationNumber).set(userIdField);

                accountInformation.put("identificationNumber", newIdentificationNumber);
                identificationNumber = newIdentificationNumber;
                informationChanged = true;
            }
            if (!email.equals(newEmail))
            {
                accountInformation.put("email", newEmail);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, password);

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
            if (!phoneNumber.equals(newPhoneNumber))
            {
                db.collection("phonenumbers").document(phoneNumber).delete();

                Map<String, Object> userIdField = new HashMap<>();
                userIdField.put("userId", doctorId);
                db.collection("phonenumbers").document(newPhoneNumber).set(userIdField);

                accountInformation.put("phoneNumber", newPhoneNumber);
                phoneNumber = newPhoneNumber;
                informationChanged = true;
            }
            if (!newSpeciality.equals(speciality))
            {
                accountInformation.put("speciality", newSpeciality);
                speciality = newSpeciality;
                informationChanged = true;
            }
            if (!newYearsOfExperience.equals(yearsOfExperience))
            {
                accountInformation.put("yearsOfExperience", Integer.parseInt((newYearsOfExperience)));
                yearsOfExperience = newYearsOfExperience;
                informationChanged = true;
            }
            if (!newWorkplace.equals(workplace))
            {
                accountInformation.put("workplace", newWorkplace);
                workplace = newWorkplace;
                informationChanged = true;
            }
            if(!newWorkdays.equals(workdays))
            {
                accountInformation.put("workdays", newWorkdays);
                workdays = newWorkdays;
                informationChanged = true;
            }
            if (informationChanged) // update account information only when at least one field changed
            {
                db.collection("doctors")
                        .document(doctorId).update(accountInformation)
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
        });

        materialDatePicker.show(getSupportFragmentManager(), "DoctorAccountActivity");
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getDoctorDrawer(this, 0);
    }
}
