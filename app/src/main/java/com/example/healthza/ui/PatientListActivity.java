package com.example.healthza.ui;

import android.annotation.SuppressLint;
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

import com.example.healthza.R;
import com.example.healthza.Toasty;
import com.example.healthza.adapters.PatientAdapter;
import com.example.healthza.models.Disease;
import com.example.healthza.models.Patient;
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

    private ImageView emptyPatientListImageView;
    private TextView emptyPatientListTextView;
    private SearchView patientsSearchView;
    private RecyclerView patientsRecyclerView;
    private PatientAdapter patientAdapter;
    private List<Patient> patients;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        emptyPatientListImageView = findViewById(R.id.emptyPatientListImageView);
        emptyPatientListTextView = findViewById(R.id.emptyPatientListTextView);
        patientsSearchView = findViewById(R.id.patientsSearchView);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String doctorId = firebaseAuth.getCurrentUser().getUid();

       // DrawerUtil.getDoctorDrawer(this, 3);

        patients = new ArrayList<>();
        patientsRecyclerView = findViewById(R.id.patientsRecyclerView);

        CollectionReference patientsRefs = db.collection("doctors").document(doctorId)
                .collection("patients");

        patientsRefs.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
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
            }
        });

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

    @Override
    public void onItemClick(int position)
    {
        if(position != -1)
            Toast.makeText(this, patients.get(position).getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position)
    {
        //patients.remove(position);
        //patientAdapter.notifyItemRemoved(position);
    }


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

        DocumentReference patientRef = db.collection("patients")
                .document(patientId);

        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.patient_profile_dialog, null);

        EditText patientEmailEditText = view.findViewById(R.id.patientEmailEditText);
        EditText patientWeightEditText = view.findViewById(R.id.patientWeightEditText);
        EditText patientHeightEditText = view.findViewById(R.id.patientHeightEditText);
        EditText patientDiseasesEditText = view.findViewById(R.id.patientDiseasesEditText);
        EditText complicationsEditText = view.findViewById(R.id.complicationsEditText);
        RadioGroup patientSexRadioGroup = view.findViewById(R.id.patientSexRadioGroup);
        Button saveButton = view.findViewById(R.id.saveButton);

        patientRef.get()
                .addOnSuccessListener(patientDocument ->
        {

            if(patientDocument.exists())
            {
                Patient patient1 = patientDocument.toObject(Patient.class);

                patientEmailEditText.setText(patient1.getEmail());
                if(patient1.getSex().equals("male"))
                    patientSexRadioGroup.check(R.id.maleRadioButton);
                else
                    patientSexRadioGroup.check(R.id.femaleRadioButton);
                patientWeightEditText.setText(patient1.getWeight() + "");
                patientHeightEditText.setText(patient1.getHeight() + "");

                CollectionReference diseasesRefs = patientRef.collection("diseases");
                diseasesRefs.get().addOnCompleteListener(task ->
                {
                    Disease disease = new Disease();

                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        disease = document.toObject(Disease.class);
                        patientDiseasesEditText.setText(disease.getDiseaseName());
                    }
                });

                AlertDialog patientProfileDialog = new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle("Edit information for " + patient.getName())
                        .create();

                patientProfileDialog.show();

                saveButton.setOnClickListener(v ->
                {
                    String sex;
                    String email = patientEmailEditText.getText().toString().trim();
                    String weightString = patientWeightEditText.getText().toString();
                    String heightString = patientHeightEditText.getText().toString();
                    if(patientSexRadioGroup.getCheckedRadioButtonId() == R.id.maleRadioButton)
                        sex = "male";
                    else
                        sex = "female";

                    double weight = Double.parseDouble(weightString);
                    double height = Double.parseDouble(heightString);
                    double bmi = weight / Math.pow(height, 2);
                    BigDecimal bd = new BigDecimal(weight).setScale(2, RoundingMode.HALF_UP);
                    weight = bd.doubleValue();
                    bd = new BigDecimal(height).setScale(2, RoundingMode.HALF_UP);
                    height = bd.doubleValue();
                    bd = new BigDecimal(bmi).setScale(2, RoundingMode.HALF_UP);
                    bmi = bd.doubleValue();


                    Map<String, Object> patientInfo = new HashMap<>();
                    patientInfo.put("email",email);
                    patientInfo.put("sex", sex);
                    patientInfo.put("weight", weight);
                    patientInfo.put("height", height);
                    patientInfo.put("bmi",bmi);

                    patientRef.update(patientInfo);
                });
            }
        });
    }

    @Override
    public void onRemoveButtonClick(int position) {
        Patient patient = patients.get(position);

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
                    Toasty.showText(this, "patient " + "\"" + patient.getName()
                            + "\" " + "has been deleted successfully.", Toasty.SUCCESS
                            , Toast.LENGTH_LONG);
            }
            else
                Toasty.showText(this, "something went wrong...", Toasty.ERROR
                        , Toast.LENGTH_LONG);
        });
    }
}