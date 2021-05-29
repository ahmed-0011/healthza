package com.example.healthza.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthza.DrawerUtil;
import com.example.healthza.Toasty;
import com.example.healthza.models.Doctor;
import com.example.healthza.adapters.DoctorAdapter;
import com.example.healthza.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorListActivity extends AppCompatActivity implements DoctorAdapter.OnDoctorItemClickListener
{

    private ImageView emptyDoctorListImageView;
    private TextView emptyDoctorListTextView;
    private SearchView doctorsSearchView;
    private RecyclerView doctorsRecyclerView;
    private DoctorAdapter doctorAdapter;
    private List<Doctor> doctors;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        doctorsSearchView = findViewById(R.id.doctorsSearchView);
        emptyDoctorListImageView = findViewById(R.id.emptyDoctorListImageView);
        emptyDoctorListTextView = findViewById(R.id.emptyDoctorListTextView);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String patientId = firebaseAuth.getCurrentUser().getUid();

        DrawerUtil.getPatientDrawer(this, 3);

        doctors = new ArrayList<>();
        doctorsRecyclerView = findViewById(R.id.doctorsRecyclerView);

        CollectionReference doctorsRef = db.collection("patients").document(patientId)
                .collection("doctors");

        doctorsRef.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult())
                    doctors.add(document.toObject(Doctor.class));


                if (doctors.isEmpty())
                {
                    emptyDoctorListImageView.setVisibility(View.VISIBLE);
                    emptyDoctorListTextView.setVisibility(View.VISIBLE);
                }
                doctorAdapter = new DoctorAdapter(this, doctors, this);
                doctorsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                doctorsRecyclerView.setAdapter(doctorAdapter);
            }
        });

        doctorsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                doctorAdapter.getFilter().filter(doctorsSearchView.getQuery().toString().trim());
                return true;
            }
        });
    }

    @Override
    public void onItemClick(int position)
    {
        if(position != -1)
            Toast.makeText(this, doctors.get(position).getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position)
    {
        //patients.remove(position);
        //patientAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onProfileButtonClick(int position)
    {
        Doctor doctor = doctors.get(position);

        String doctorId = doctor.getDoctorId();

        DocumentReference doctorRef = db.collection("doctors")
                .document(doctorId);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.doctor_profile_dialog, null);

        TextView doctorEmailTextView = view.findViewById(R.id.doctorEmailTextView);
        TextView doctorSexTextView = view.findViewById(R.id.doctorSexTextView);
        TextView doctorSpecialityTextView = view.findViewById(R.id.doctorSpecialityTextView);
        TextView doctorYearsOfExperienceTextView = view.findViewById(R.id.doctorYearsOfExperienceTextView);
        TextView doctorWorkplaceTextView = view.findViewById(R.id.doctorworkplaceTextView);
        TextView doctorWorkdaysTextView = view.findViewById(R.id.doctorWorkdaysTextView);


        doctorRef.get()
                .addOnSuccessListener(doctorDocument -> {

            if(doctorDocument.exists())
            {
                /* doctor1 is real doctor from root collection ..
                doctor is just contains the basic info */
                Doctor doctor1 = doctorDocument.toObject(Doctor.class);

                doctorEmailTextView.append(doctor1.getEmail());
                doctorSexTextView.append(doctor1.getSex());
                doctorSpecialityTextView.append(doctor1.getSpeciality());
                doctorYearsOfExperienceTextView.append(doctor1.getYearsOfExperience() + "");
                doctorWorkplaceTextView.append(doctor1.getWorkplace());
                doctorWorkdaysTextView.append(doctor1.getWorkdays().toString().replace("[", "")
                        .replace("]", ""));

                AlertDialog patientProfileDialog = new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle("Information for " + doctor.getName())
                        .create();

                patientProfileDialog.show();

            }
                });
    }

    @Override
    public void onRemoveButtonClick(int position) {
        Doctor doctor = doctors.get(position);

        String patientId = firebaseAuth.getCurrentUser().getUid();
        String doctorId = doctor.getDoctorId();

        DocumentReference doctorRef = db.collection("patients")
                .document(patientId)
                .collection("doctors")
                .document(doctorId);

        DocumentReference patientRef = db.collection("doctors")
                .document(doctorId)
                .collection("patients")
                .document(patientId);

        patientRef.delete().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                    doctorRef.delete();
                    doctors.remove(position);
                    doctorAdapter.notifyItemRemoved(position);

                    if (doctors.isEmpty())
                    {
                        emptyDoctorListImageView.setVisibility(View.VISIBLE);
                        emptyDoctorListTextView                 // check if there is no item and
                                .setVisibility(View.VISIBLE);   // show text view that there is no doctors
                    }
                    Toasty.showText(this, "patient " + "\"" + doctor.getName()
                            + "\" " + "has been removed successfully.", Toasty.SUCCESS, Toast.LENGTH_LONG);
            }
            else
                Toasty.showText(this, "something went wrong...", Toasty.ERROR, Toast.LENGTH_LONG);
        });
    }
}