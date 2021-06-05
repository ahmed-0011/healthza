package com.project.cdh.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.WriteBatch;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.project.cdh.DrawerUtil;
import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.project.cdh.adapters.PatientAdapter;
import com.project.cdh.models.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientListActivity extends AppCompatActivity implements PatientAdapter.OnPatientItemClickListener
{
    private ChipNavigationBar chipNavigationBar;
    private ImageView emptyPatientListImageView;
    private TextView emptyPatientListTextView;
    private SearchView patientsSearchView;
    private RecyclerView patientsRecyclerView;
    private PatientAdapter patientAdapter;
    private List<Patient> patients;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);


        emptyPatientListImageView = findViewById(R.id.emptyPatientListImageView);
        emptyPatientListTextView = findViewById(R.id.emptyPatientListTextView);
        patientsSearchView = findViewById(R.id.patientsSearchView);

        chipNavigationBar = findViewById(R.id.bottomNavigationBar);

        setupBottomNavigationBar();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        doctorId = firebaseAuth.getCurrentUser().getUid();

        patients = new ArrayList<>();
        patientsRecyclerView = findViewById(R.id.patientsRecyclerView);

        setupPatientsRecyclerView();
    }


    private void setupBottomNavigationBar()
    {
        chipNavigationBar.setItemSelected(R.id.patientsItem, true);

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(int i)
            {
                Intent intent = null;

                if (i == R.id.patientsItem)
                    return;
                else if (i == R.id.homeItem)
                    intent = new Intent(PatientListActivity.this, DoctorHomeActivity.class);
                else if (i == R.id.appointmentsItem)
                    intent = new Intent(PatientListActivity.this, DoctorAppointmentsActivity.class);
                else if (i == R.id.chatItem)
                    intent = new Intent(PatientListActivity.this, DoctorChatListActivity.class);

                if (intent != null)
                {
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }


    private void setupPatientsRecyclerView()
    {
        CollectionReference patientsRefs = db.collection("doctors").document(doctorId)
                .collection("patients");

        patientsRefs
                .get()
                .addOnCompleteListener(task -> 
                { 
                    if (task.isSuccessful()) 
                    { 
                        for (QueryDocumentSnapshot document : task.getResult()) 
                            patients.add(document.toObject(Patient.class));

                        if (patients.isEmpty())
                        {
                            emptyPatientListImageView.setVisibility(View.VISIBLE);
                            emptyPatientListTextView.setVisibility(View.VISIBLE);
                        }

                        patientAdapter = new PatientAdapter(this, patients, this);
                        patientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        patientsRecyclerView.setAdapter(patientAdapter);


                        patientsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText)
                            {
                                patientAdapter.getFilter().filter(patientsSearchView.getQuery().toString().trim());
                                return true;
                            }
                        });
                    }
                });
    }


    @Override
    public void onItemClick(int position)
    {
        if(position != -1)
            Toasty.showText(this, patients.get(position).getName(), Toasty.INFORMATION,Toast.LENGTH_SHORT);
    }


    @Override
    public void onItemLongClick(int position) { }


    @Override
    public void onChartsButtonClick(int position)
    {
        Patient patient = patients.get(position);

        String patientId = patient.getPatientId();

        Intent intent = new Intent(this, DoctorPatientChartsActivity.class);
        intent.putExtra("patientId", patientId);
        startActivity(intent);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onProfileButtonClick(int position)
    {
        Patient patient = patients.get(position);

        String patientId = patient.getPatientId();



        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.dialog_patient_profile, null);


        TextView patientEmailTextView, patientSexTextView, patientIdentificationNumberTextView,
                 patientPhoneNumberTextView, patientBirthDateTextView, patientWeightTextView,
                 patientHeightTextView, patientBMITextView;

        patientEmailTextView = view.findViewById(R.id.patientEmailTextView);

        patientSexTextView = view.findViewById(R.id. patientSexTextView);
        patientIdentificationNumberTextView = view.findViewById(R.id.patientIdentificationNumberTextView);
        patientPhoneNumberTextView = view.findViewById(R.id.patientPhoneNumberTextView);
        patientBirthDateTextView = view.findViewById(R.id.patientBirthDateTextView);
        patientWeightTextView = view.findViewById(R.id.patientWeightTextView);
        patientHeightTextView = view.findViewById(R.id.patientHeightTextView);
        patientBMITextView = view.findViewById(R.id.patientBMITextView);

        DocumentReference patientRef = db.collection("patients")
                .document(patientId);


        patientRef
                .get()
                .addOnSuccessListener(patientDocument ->
                {
                    if (patientDocument.exists())
                    {
                        Patient patient1 = patientDocument.toObject(Patient.class);

                        patientEmailTextView.setText(patient1.getEmail());
                        patientSexTextView.setText(patient1.getSex());
                        patientIdentificationNumberTextView.setText(patient1.getIdentificationNumber());
                        patientPhoneNumberTextView.setText(patient1.getPhoneNumber());
                        patientBirthDateTextView.setText(patient1.getBirthDate());
                        patientWeightTextView.setText(patient1.getWeight() + "");
                        patientHeightTextView.setText(patient1.getHeight() + "");
                        patientBMITextView.setText(patient1.getBmi() + "");


                        AlertDialog patientProfileDialog = new AlertDialog.Builder(this)
                                .setView(view)
                                .setTitle("Information about " + patient.getName())
                                .create();

                        patientProfileDialog.show();
                    }
                });
    }


    @Override
    public void onRemoveButtonClick(int position)
    {

        Patient patient = patients.get(position);

        String patientName = patient.getName();

        new MaterialAlertDialogBuilder(this, R.style.DeleteDialogTheme)
                .setTitle("Delete Patient")
                .setMessage("Do you want to delete patient" +  patientName + " ?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String doctorId = firebaseAuth.getCurrentUser().getUid();
                        String patientId = patient.getPatientId();


                        DocumentReference doctorRef = db.collection("patients")
                                .document(patientId)
                                .collection("doctors")
                                .document(doctorId);

                        DocumentReference patientRef = db.collection("doctors")
                                .document(doctorId)
                                .collection("patients")
                                .document(patientId);


                        doctorRef.delete().addOnCompleteListener(task ->
                        {
                            if (task.isSuccessful())
                            {
                                patientRef.delete();
                                patients.remove(position);
                                patientAdapter.notifyItemRemoved(position);

                                if (patients.isEmpty())
                                {
                                    emptyPatientListImageView.setVisibility(View.VISIBLE);
                                    emptyPatientListTextView                 // check if there is no item and
                                            .setVisibility(View.VISIBLE);    // show text view that there is no patients
                                }
                                Toasty.showText(PatientListActivity.this, "Patient " + "\"" + patient.getName()
                                                + "\" " + "has been deleted successfully.", Toasty.SUCCESS
                                        , Toast.LENGTH_LONG);
                            }
                            else
                                Toasty.showText(PatientListActivity.this, "An error occurred while trying to remove this patient", Toasty.ERROR, Toast.LENGTH_LONG);
                        });

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                })
                .show();
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, DoctorHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        DrawerUtil.getDoctorDrawer(this, -1);
    }
}