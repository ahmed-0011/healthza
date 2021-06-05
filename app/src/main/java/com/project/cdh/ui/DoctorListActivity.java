package com.project.cdh.ui;

import android.content.DialogInterface;
import android.os.Bundle;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.project.cdh.DrawerUtil;
import com.project.cdh.Toasty;
import com.project.cdh.models.Doctor;
import com.project.cdh.adapters.DoctorAdapter;
import com.project.cdh.R;
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
    private String patientId;

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
        patientId = firebaseAuth.getCurrentUser().getUid();

        doctors = new ArrayList<>();
        doctorsRecyclerView = findViewById(R.id.doctorsRecyclerView);

        setupDoctorsRecyclerView();
    }


    private void setupDoctorsRecyclerView()
    {
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
    {    }

    @Override
    public void onProfileButtonClick(int position)
    {
        Doctor doctor = doctors.get(position);

        String doctorId = doctor.getDoctorId();

        DocumentReference doctorRef = db.collection("doctors")
                .document(doctorId);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_doctor_profile, null);

        TextView doctorEmailTextView, doctorSexTextView, doctorBirthDateTextView,
                 doctorSpecialityTextView, doctorYearsOfExperienceTextView, doctorWorkplaceTextView,
                 doctorWorkdaysTextView;


        doctorEmailTextView = view.findViewById(R.id.doctorEmailTextView);
        doctorSexTextView = view.findViewById(R.id.doctorSexTextView);
        doctorBirthDateTextView = view.findViewById(R.id.doctorBirthDateTextView);
        doctorSpecialityTextView = view.findViewById(R.id.doctorSpecialityTextView);
        doctorYearsOfExperienceTextView = view.findViewById(R.id.doctorYearsOfExperienceTextView);
        doctorWorkplaceTextView = view.findViewById(R.id.doctorWorkplaceTextView);
        doctorWorkdaysTextView = view.findViewById(R.id.doctorWorkdaysTextView);


        doctorRef
                .get()
                .addOnSuccessListener(doctorDocument ->
                {

                    if(doctorDocument.exists())
                    {

                        /* doctor1 is real doctor from root collection ..
                           doctor is just contains the basic info */
                        Doctor doctor1 = doctorDocument.toObject(Doctor.class);

                        doctorEmailTextView.setText(doctor1.getEmail());
                        doctorSexTextView.setText(doctor1.getSex());
                        doctorBirthDateTextView.setText(doctor1.getBirthDate());
                        doctorSpecialityTextView.setText(doctor1.getSpeciality());
                        doctorYearsOfExperienceTextView.setText(doctor1.getYearsOfExperience() + "");
                        doctorWorkplaceTextView.setText(doctor1.getWorkplace());
                        doctorWorkdaysTextView.setText(doctor1.getWorkdays().toString().replace("[", "")
                        .replace("]", ""));

                        AlertDialog patientProfileDialog = new AlertDialog.Builder(this)
                                .setView(view)
                                .setTitle("Information about " + doctor.getName())
                                .create();

                        patientProfileDialog.show();
                    }
                });
    }


    @Override
    public void onRemoveButtonClick(int position)
    {
        Doctor doctor = doctors.get(position);

        String doctorName = doctor.getName();

        new MaterialAlertDialogBuilder(this, R.style.DeleteDialogTheme)
                .setTitle("Delete Doctor")
                .setMessage("Do you want to delete doctor" +  doctorName + " ?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
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
                                    emptyDoctorListTextView.setVisibility(View.VISIBLE);    // check if there is no item and
                                                                                            // show text view that there is no doctors
                                }

                                Toasty.showText(DoctorListActivity.this, "Doctor " + "\"" + doctor.getName()
                                        + "\" " + "has been removed successfully.", Toasty.SUCCESS, Toast.LENGTH_LONG);
                            }
                            else
                                Toasty.showText(DoctorListActivity.this, "An error occurred while trying to remove this doctor", Toasty.ERROR, Toast.LENGTH_LONG);
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
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getPatientDrawer(this, 2);
    }
}