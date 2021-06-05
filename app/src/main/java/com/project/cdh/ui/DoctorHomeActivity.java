package com.project.cdh.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.project.cdh.DrawerUtil;
import com.project.cdh.ProgressDialog;
import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.project.cdh.models.Doctor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoctorHomeActivity extends AppCompatActivity
{
    private ChipNavigationBar chipNavigationBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String doctorId;
    private String doctorName;
    private TextInputLayout specialityInputLayout, yearsOfExperienceInputLayout, workplaceInputLayout;
    private TextInputEditText specialityInputEditText,
            yearsOfExperienceInputEditText, workplaceInputEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);


        chipNavigationBar = findViewById(R.id.bottomNavigationBar);

        setupBottomNavigationBar();

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        doctorId = firebaseAuth.getCurrentUser().getUid();
        doctorName = firebaseAuth.getCurrentUser().getDisplayName();

        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);

        boolean userCompleteinfo = sharedPreferences.getBoolean("user_complete_info", false);

        if (!userCompleteinfo)
            showWelcomeDialog();
        else
            setupCardViews();

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
                else if (i == R.id.patientsItem)
                    intent = new Intent(DoctorHomeActivity.this, PatientListActivity.class);
                else if (i == R.id.appointmentsItem)
                    intent = new Intent(DoctorHomeActivity.this, DoctorAppointmentsActivity.class);
                else if (i == R.id.chatItem)
                    intent = new Intent(DoctorHomeActivity.this, DoctorChatListActivity.class);

                if(intent != null)
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
        setupDoctorInfoCardView();
        setupNextFiveAppointmentsCardViews();
    }


    private void setupDoctorInfoCardView()
    {
        TextView doctorNameTextView, doctorSpecialityTextView, doctorWorkplaceTextView;

        doctorNameTextView = findViewById(R.id.doctorNameTextView);
        doctorSpecialityTextView = findViewById(R.id.doctorSpecialityTextView);
        doctorWorkplaceTextView = findViewById(R.id.doctorWorkplaceTextView);


        db
                .collection("doctors")
                .document(doctorId)
                .get()
                .addOnSuccessListener(doctorDocument ->
                {

                    if(doctorDocument.exists())
                    {
                        Doctor doctor = doctorDocument.toObject(Doctor.class);

                        String doctorName = doctor.getName();
                        String doctorSpeciality = doctor.getSpeciality();
                        String doctorWorkplace = doctor.getWorkplace();

                        doctorNameTextView.append(doctorName);
                        doctorSpecialityTextView.append(doctorSpeciality);
                        doctorWorkplaceTextView.append(doctorWorkplace);
                    }
                });
    }


    private void setupNextFiveAppointmentsCardViews()
    {
        ConstraintLayout firstAppointmentLayout, secondAppointmentLayout, thirdAppointmentLayout,
                         fourthAppointmentLayout, fifthAppointmentLayout;

        TextView firstPatientNameTextView, firstAppointmentTypeTextView, firstAppointmentTimeTextView,
                 secondPatientNameTextView, secondAppointmentTypeTextView, secondAppointmentTimeTextView,
                 thirdPatientNameTextView, thirdAppointmentTypeTextView,  thirdAppointmentTimeTextView,
                 fourthPatientNameTextView, fourthAppointmentTypeTextView, fourthAppointmentTimeTextView,
                 fifthPatientNameTextView, fifthAppointmentTypeTextView, fifthAppointmentTimeTextView,
                 noAppointmentsAvailableTextView;


        firstAppointmentLayout = findViewById(R.id.firstAppointmentLayout);
        secondAppointmentLayout = findViewById(R.id.secondAppointmentLayout);
        thirdAppointmentLayout = findViewById(R.id.thirdAppointmentLayout);
        fourthAppointmentLayout = findViewById(R.id.fourthAppointmentLayout);
        fifthAppointmentLayout = findViewById(R.id.fifthAppointmentLayout);

        firstAppointmentTypeTextView = findViewById(R.id.firstAppointmentTypeTextView);
        firstPatientNameTextView = findViewById(R.id.firstPatientNameTextView);
        firstAppointmentTimeTextView = findViewById(R.id.firstAppointmentTimeTextView);
        secondAppointmentTypeTextView = findViewById(R.id.secondAppointmentTypeTextView);
        secondPatientNameTextView = findViewById(R.id. secondPatientNameTextView);
        secondAppointmentTimeTextView = findViewById(R.id.secondAppointmentTimeTextView);
        thirdAppointmentTypeTextView = findViewById(R.id.thirdAppointmentTypeTextView);
        thirdPatientNameTextView = findViewById(R.id.thirdPatientNameTextView);
        thirdAppointmentTimeTextView = findViewById(R.id.thirdAppointmentTimeTextView);
        fourthAppointmentTypeTextView = findViewById(R.id.fourthAppointmentTypeTextView);
        fourthPatientNameTextView = findViewById(R.id.fourthPatientNameTextView);
        fourthAppointmentTimeTextView = findViewById(R.id.fourthAppointmentTimeTextView);
        fifthAppointmentTypeTextView = findViewById(R.id.fifthAppointmentTypeTextView);
        fifthPatientNameTextView = findViewById(R.id.fifthPatientNameTextView);
        fifthAppointmentTimeTextView = findViewById(R.id.fifthAppointmentTimeTextView);

        noAppointmentsAvailableTextView = findViewById(R.id.noAppointmentsAvailableTextView);

        db
                .collection("doctors")
                .document(doctorId)
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
                    String patientName = appointments.get(i).getString("patientName");
                    String appointmentType = appointments.get(i).getString("type");
                    Timestamp timestamp = appointments.get(i).getTimestamp("timestamp");

                    if(i == 0)
                    {
                        firstPatientNameTextView.setText(patientName);
                        firstAppointmentTypeTextView.setText(appointmentType);
                        firstAppointmentTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                        firstAppointmentLayout.setVisibility(View.VISIBLE);
                    }
                    else if(i == 1)
                    {
                        secondPatientNameTextView.setText(patientName);
                        secondAppointmentTypeTextView.setText(appointmentType);
                        secondAppointmentTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                        secondAppointmentLayout.setVisibility(View.VISIBLE);
                    }
                    else if(i == 2)
                    {
                        thirdPatientNameTextView.setText(patientName);
                        thirdAppointmentTypeTextView.setText(appointmentType);
                        thirdAppointmentTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                        thirdAppointmentLayout.setVisibility(View.VISIBLE);
                    }
                    else if(i == 2)
                    {
                        fourthPatientNameTextView.setText(patientName);
                        fourthAppointmentTypeTextView.setText(appointmentType);
                        fourthAppointmentTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                        fourthAppointmentLayout.setVisibility(View.VISIBLE);
                    }
                    else if(i == 2)
                    {
                        fifthPatientNameTextView.setText(patientName);
                        fifthAppointmentTypeTextView.setText(appointmentType);
                        fifthAppointmentTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                        fifthAppointmentLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
            else
                noAppointmentsAvailableTextView.setVisibility(View.VISIBLE);
        });
    }


    @SuppressLint("SetTextI18n")
    void showWelcomeDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_welcome, null);
        Button startButton = view.findViewById(R.id.startButton);
        TextView welcomeTextView = view.findViewById(R.id.welcomeTextView);
        welcomeTextView.setText(welcomeTextView.getText() + " " + doctorName);
        AlertDialog welcomeDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();

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

    private boolean isValidYearsOfExperience(String yearsOfExperience)
    {
        if(yearsOfExperience.isEmpty())
            return false;

        int yearsOfExperience1 = Integer.parseInt(yearsOfExperience);

        return yearsOfExperience1 <= 60;
    }

    private boolean isValidWorkplace(String workpalce) // No need to check if the field is empty
    {                                                   // because Regex won't match empty strings
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("[A-Za-z ]+");
        matcher = pattern.matcher(workpalce);
        return matcher.matches();
    }

    void showDoctorDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.dialog_doctor, null);

        specialityInputLayout = view.findViewById(R.id.specialityInputLayout);
        yearsOfExperienceInputLayout = view.findViewById(R.id.yearsOfExperienceInputLayout);
        workplaceInputLayout = view.findViewById(R.id.workplaceInputLayout);
        specialityInputEditText = view.findViewById(R.id.specialityInputEditText);
        yearsOfExperienceInputEditText = view.findViewById(R.id.yearsOfExperienceInputEditText);
        workplaceInputEditText = view.findViewById(R.id.workplaceInputEditText);

        addOnTextChangeListenersForInputEditText();


        AlertDialog doctorDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Welcome, " + doctorName)
                .setPositiveButton("Next", null)
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


            List<String> workdays = getWorkdays(workDaysFirstToggleButton, workDaysSecondToggleButton);

            if (!isValidSpeciality(speciality))
            {
                specialityInputLayout.setError("Invalid speciality, speciality must contains alphabetic and spaces only");
                specialityInputEditText.requestFocus();
            }
            else if (!isValidYearsOfExperience(yearsOfExperience)) {
                yearsOfExperienceInputLayout.setError("Invalid years of experience, years can't be more than 60 years");
                yearsOfExperienceInputEditText.requestFocus();
            }
            else if (!isValidWorkplace(workplace))
            {
                workplaceInputLayout.setError("Invalid workplace, workplace must contains alphabetic and spaces only");
                workplaceInputEditText.requestFocus();
            }
            else
            {

                ProgressDialog progressDialog = new ProgressDialog(this);

                Map<String, Object> additionalData = new HashMap<>();

                additionalData.put("speciality", speciality);
                additionalData.put("yearsOfExperience", Integer.parseInt(yearsOfExperience));
                additionalData.put("workplace", workplace);
                additionalData.put("workdays", workdays);
                additionalData.put("completeInfo", true);

                DocumentReference doctorRef = db.collection("doctors").document(doctorId);

                doctorRef.set(additionalData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        progressDialog.showProgressDialog("Updating Profile...");
                        if (task.isSuccessful())
                        {
                            setupCardViews();
                            doctorDialog.dismiss();


                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    progressDialog.dismissProgressDialog();
                                    Toasty.showText(DoctorHomeActivity.this, "Your profile updated successfully"
                                            , Toasty.INFORMATION, Toast.LENGTH_LONG);
                                }
                            },3000);

                            SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("user_complete_info", true);
                            editor.apply();
                        }
                        else
                            Toasty.showText(DoctorHomeActivity.this, "An error occurred while trying to update your profile",
                                    Toasty.ERROR, Toast.LENGTH_LONG);
                    }
                });
            }
        });
    }


    private void clearInputsErrors()
    {
        specialityInputLayout.setError(null);
        yearsOfExperienceInputLayout.setError(null);
        workplaceInputLayout.setError(null);
    }


    private List<String> getWorkdays(MaterialButtonToggleGroup workdaysFirstToggleButton,
                                     MaterialButtonToggleGroup workdaysSecondToggleButton)
    {
        List<Integer> workdaysGroup1 = workdaysFirstToggleButton.getCheckedButtonIds();
        List<Integer> workdaysGroup2 = workdaysSecondToggleButton.getCheckedButtonIds();

        List<String> workdays = new ArrayList<>();

        if (workdaysGroup1.contains(R.id.saturdayButton))
            workdays.add("saturday");
        if (workdaysGroup1.contains(R.id.sundayButton))
            workdays.add("sunday");
        if (workdaysGroup1.contains(R.id.mondayButton))
            workdays.add("monday");
        if (workdaysGroup1.contains(R.id.tuesdayButton))
            workdays.add("tuesday");
        if (workdaysGroup2.contains(R.id.wednesdayButton))
            workdays.add("wednesday");
        if (workdaysGroup2.contains(R.id.thursdayButton))
            workdays.add("thursday");
        if (workdaysGroup2.contains(R.id.fridayButton))
            workdays.add("friday");


        return workdays;
    }

    private void addOnTextChangeListenersForInputEditText()
    {

        specialityInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidSpeciality(specialityInputEditText.getText().toString()))
                    specialityInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        yearsOfExperienceInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidYearsOfExperience(yearsOfExperienceInputEditText.getText().toString()))
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
                if (isValidWorkplace(workplaceInputEditText.getText().toString()))
                    workplaceInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
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
        DrawerUtil.getDoctorDrawer(this, -1);
    }
}