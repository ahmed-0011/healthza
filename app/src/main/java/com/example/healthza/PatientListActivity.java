package com.example.healthza;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PatientListActivity extends AppCompatActivity implements RecyclerViewInterface {
    RecyclerView recyclerView;
    PatientAdapter patientAdapter;
    List<Patient> patients;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String doctorId = firebaseAuth.getCurrentUser().getUid();

        patients = new ArrayList<>();

        recyclerView = findViewById(R.id.patientsRecyclerView);

        CollectionReference patientsRefs = db.collection("doctors").document(doctorId)
                .collection("patients");
        patientsRefs.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult())
                    patients.add(document.toObject(Patient.class));

                patientAdapter = new PatientAdapter(this, patients, this);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(patientAdapter);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, patients.get(position).getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position) {
        //patients.remove(position);
        //patientAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onNoItems() {

    }
}