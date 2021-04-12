package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorListActivity extends AppCompatActivity implements  RecyclerViewInterface
{

    RecyclerView recyclerView;
    DoctorAdapter doctorAdapter;
    List<Doctor> doctors;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String patientId = firebaseAuth.getCurrentUser().getUid();

        doctors = new ArrayList<>();

        recyclerView = findViewById(R.id.patientsRecyclerView);

        CollectionReference doctorsRef = db.collection("patients").document(patientId)
                .collection("doctors");

        doctorsRef.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                for (QueryDocumentSnapshot document : task.getResult())
                    doctors.add(document.toObject(Doctor.class));

                doctorAdapter = new DoctorAdapter(this, doctors, this);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(doctorAdapter);
            }
        });
    }

    @Override
    public void onItemClick(int position)
    {
        Toast.makeText(this, doctors.get(position).getName() ,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position)
    {
        //patients.remove(position);
        //patientAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onNoItems() {

    }
}
