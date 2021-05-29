package com.example.healthza.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.healthza.DrawerUtil;
import com.example.healthza.ProgressDialog;
import com.example.healthza.R;
import com.example.healthza.Toasty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.healthza.ui.Functions.TAG_CT;

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

        ChipNavigationBar chipNavigationBar = findViewById(R.id.bottomNavigationBar);

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i)
            {
                if (i == R.id.homeItem);
                else if (i == R.id.patientsItem)
                    startActivity(new Intent(DoctorHomeActivity.this, PatientListActivity.class));
                else if (i == R.id.appointmentsItem){startActivity(new Intent(DoctorHomeActivity.this, appointmentsD.class));}
                    //TODO
                else if (i == R.id.chatItem)
                    startActivity(new Intent(DoctorHomeActivity.this, DoctorChatListActivity.class));
            }
        });

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.drawer_header, null);

        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView emailTextView = view.findViewById(R.id.emailTextView);

        nameTextView.setText(firebaseAuth.getCurrentUser().getDisplayName());
        emailTextView.setText(firebaseAuth.getCurrentUser().getEmail());
        DrawerUtil.headerView = view;
        DrawerUtil.getDoctorDrawer(this, -1);
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
                    intent = new Intent(DoctorHomeActivity.this, medicalRecords.class);
                else if (i == R.id.chartsItem)
                    intent = new Intent(DoctorHomeActivity.this, PatientChartsActivity.class);
                else if (i == R.id.appointmentsItem)
                    intent = new Intent(DoctorHomeActivity.this,appointmentsP.class);
                else if (i == R.id.chatItem)
                    intent = new Intent(DoctorHomeActivity.this, PatientChatListActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }


    private void setupCardViews()
    {
        setupNextThreeAppointmentsCardViews();
    }




    private void setupNextThreeAppointmentsCardViews()
    {
        ConstraintLayout firstAppointmentLayout, secondAppointmentLayout, thirdAppointmentLayout;

        TextView firstDoctorNameTextView, firstAppointmentTypeTextView, firstAppointmentTimeTextView,
                secondDoctorNameTextView, secondAppointmentTypeTextView, secondAppointmentTimeTextView,
                thirdDoctorNameTextView, thirdAppointmentTypeTextView,  thirdAppointmentTimeTextView;


        firstAppointmentLayout = findViewById(R.id.firstAppointmentLayout);
        secondAppointmentLayout = findViewById(R.id.secondAppointmentLayout);
        thirdAppointmentLayout = findViewById(R.id.thirdAppointmentLayout);

        firstAppointmentTypeTextView = findViewById(R.id.firstAppointmentTypeTextView);
        firstDoctorNameTextView = findViewById(R.id.firstDoctorNameTextView);
        firstAppointmentTimeTextView = findViewById(R.id.firstAppointmentTimeTextView);
        secondAppointmentTypeTextView = findViewById(R.id.secondAppointmentTypeTextView);
        secondDoctorNameTextView = findViewById(R.id. secondDoctorNameTextView);
        secondAppointmentTimeTextView = findViewById(R.id.secondAppointmentTimeTextView);
        thirdAppointmentTypeTextView = findViewById(R.id.thirdAppointmentTypeTextView);
        thirdDoctorNameTextView = findViewById(R.id.thirdDoctorNameTextView);
        thirdAppointmentTimeTextView = findViewById(R.id.thirdAppointmentTimeTextView);

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
                    String doctorName = appointments.get(i).getString("doctorName");
                    String appointmentType = appointments.get(i).getString("type");
                    Timestamp timestamp = appointments.get(i).getTimestamp("timestamp");

                    if(i == 0)
                    {
                        firstDoctorNameTextView.setText(doctorName);
                        firstAppointmentTypeTextView.setText(appointmentType);
                        firstAppointmentTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                        firstAppointmentLayout.setVisibility(View.VISIBLE);
                    }
                    else if(i == 1)
                    {
                        secondDoctorNameTextView.setText(doctorName);
                        secondAppointmentTypeTextView.setText(appointmentType);
                        secondAppointmentTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                        secondAppointmentLayout.setVisibility(View.VISIBLE);
                    }
                    else if(i == 2)
                    {
                        thirdDoctorNameTextView.setText(doctorName);
                        thirdAppointmentTypeTextView.setText(appointmentType);
                        thirdAppointmentTimeTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                        thirdAppointmentLayout.setVisibility(View.VISIBLE);
                    }
                }
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

        View view = inflater.inflate(R.layout.doctor_dialog1, null);

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
                .setTitle("Welcome, " + doctorName)
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


    private void clearInputsErrors() {
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

    private void addOnTextChangeListenersForInputEditText() {

        specialityInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidSpeciality(specialityInputEditText.getText().toString()))
                    specialityInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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
            public void afterTextChanged(Editable s) {
            }
        });

        workplaceInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidWorkplace(workplaceInputEditText.getText().toString()))
                    workplaceInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

  /*  void addMed()
    {
        String medicines_drugs[] = new String[]
                {
                        "Acarbose",
                        "Albiglutide (Tanzeum)",
                        "Alogliptin (Nesina)",
                        "Alogliptin-metformin (Kazano)",
                        "Alogliptin-pioglitazone (Oseni)",
                        "Bromocriptine mesylate (Cycloset, Parlodel)",
                        "Canaglifozin (Invokana)",
                        "Canagliflozin-metformin (Invokamet)",
                        "Dapagliflozin (Farxiga)",
                        "Dapagliflozin-metformin (Xigduo XR)",
                        "Dulaglutide (Trulicity)",
                        "Empagliflozin (Jardiance)",
                        "Empagliflozin-linagliptin (Glyxambi)",
                        "Empagliflozin-metformin (Synjardy)",
                        "Ertugliflozin (Steglatro)",
                        "Ertugliflozin-metformin (Segluromet)",
                        "Ertugliflozin-sitagliptin (Steglujan)",
                        "Exenatide (Byetta)",
                        "Exenatide ER (Bydureon)",
                        "Glimepiride (Amaryl)",
                        "Glipizide (Glucotrol)",
                        "Glipizide-metformin (Metaglip)",
                        "Glyburide (DiaBeta, Glynase)",
                        "Glyburide-metformin (Glucovance)",
                        "Insulin aspart (Fiasp, NovoLog)",
                        "Insulin degludec (Tresiba)",
                        "Insulin glargine (Basaglar, Lantus, Toujeo)",
                        "Insulin inhaled (Afrezza)",
                        "Insulin Isophane (Humulin N, Novolin N)",
                        "Insulin Isophane/regular insulin (Humulin 70/30, Novolin 70/30)",
                        "Insulin lispro (Humalog)",
                        "Linagliptin (Tradjenta)",
                        "Lixisenatide (Adlyxin)",
                        "Liraglutide (Victoza)",
                        "Metformin (Glucophage)",
                        "Miglitol (Glyset)",
                        "Nateglinide (Starlix)",
                        "Pioglitazone (Actos)",
                        "Pioglitazone-metformin (ACTOplus met)",
                        "Pramlintide (Symlin)",
                        "Repaglinide (Prandin)",
                        "Repaglinide (PrandiMet)",
                        "Rosiglitazone (Avandia)",
                        "Rosiglitazone-glimepiride (Avandaryl)",
                        "Rosiglitazone-metformin (Avandamet)",
                        "Saxagliptin (Onglyza)",
                        "Semaglutide (Ozempic)",
                        "Sitagliptin (Januvia)",
                        "Sitagliptin-metformin (Janumet, Jentadueto)"
                };

        List<String> list1 = new ArrayList<String>();
        Collections.addAll(list1, medicines_drugs);

        Map<String, Object> medicines = new HashMap<>();
        medicines.put("general",list1);

        db.collection("medicines")
                .document("medicinal_drugs")
                .set(medicines)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG_CT, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG_CT, "Error writing document", e);
                    }
                });
    }*/
}