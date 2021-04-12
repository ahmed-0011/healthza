package com.example.healthza;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder>
{
    private List<Doctor> doctors;
    private Context context;
    private RecyclerViewInterface recyclerViewInterface;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;



    public DoctorAdapter(Context context, List<Doctor> doctors, RecyclerViewInterface recyclerViewInterface)
    {
        this.context = context;
        this.doctors = doctors;
        this.recyclerViewInterface = recyclerViewInterface;
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView doctorNameTextView, doctorPhoneNumberTextView;
        private Button profileButton, removeButton;

        public ViewHolder(View itemView)
        {
            super(itemView);

            doctorNameTextView = itemView.findViewById(R.id.doctorNameTextView);
            doctorPhoneNumberTextView = itemView.findViewById(R.id.patientPhoneNumberTextView);
            profileButton = itemView.findViewById(R.id.profileButton);
            removeButton = itemView.findViewById(R.id.removeButton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    recyclerViewInterface.onItemClick(getBindingAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v)
                {
                    recyclerViewInterface.onItemLongClick(getBindingAdapterPosition());
                    return true;
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_doctor, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorAdapter.ViewHolder holder, int position)
    {
        Doctor doctor = doctors.get(position);

        TextView doctorNameTextView = holder.doctorNameTextView;
        TextView doctorPhoneNumberTextView = holder.doctorPhoneNumberTextView;
        Button profileButton = holder.profileButton;
        Button removeButton = holder.removeButton;

        String name = doctorNameTextView.getText().toString() + " " + doctor.getName();
        doctorNameTextView.setText(name);
        String phoneNumber = doctorPhoneNumberTextView.getText().toString() + " " + doctor.getPhoneNumber();
        doctorPhoneNumberTextView.setText(phoneNumber);

        String patientId = firebaseAuth.getCurrentUser().getUid();

        DocumentReference patientRef = db.collection("patients")
                .document(patientId)
                .collection("doctors")
                .document(doctor.getDoctorId());


        removeButton.setOnClickListener(v ->
        {
            patientRef.delete().addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    doctors.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Patient " + "\"" + doctor.getName() + "\" " + "has been removed successfully.",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            });
        });


        profileButton.setOnClickListener(v ->
        {
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.doctor_profile_dialog, null);

            TextView doctorEmailTextView = view.findViewById(R.id.doctorEmailTextView);
            TextView doctorSexTextView = view.findViewById(R.id.doctorSexTextView);
            TextView doctorSpecialityTextView = view.findViewById(R.id.doctorSpecialityTextView);
            TextView doctorYearsOfExperienceTextView= view.findViewById(R.id.doctorYearsOfExperienceTextView);
            TextView doctorWorkplaceTextView = view.findViewById(R.id.doctorworkplaceTextView);
            TextView doctorWorkdaysTextView = view.findViewById(R.id.doctorWorkdaysTextView);


            doctorEmailTextView.setText(doctorEmailTextView.getText().toString() + " " + doctor.getEmail());
            doctorSexTextView.setText(doctorSexTextView.getText().toString() + " " + doctor.getSex());
            doctorSpecialityTextView.setText(doctorSpecialityTextView.getText().toString() + " " + doctor.getSpeciality());
            doctorYearsOfExperienceTextView.setText(doctorYearsOfExperienceTextView.getText().toString() + " " + doctor.getYearsOfExperience());
            doctorWorkplaceTextView.setText(doctorWorkplaceTextView.getText().toString() + " " + doctor.getWorkplace());
            doctorWorkdaysTextView.setText(doctorWorkdaysTextView.getText().toString() + " " + doctor.getWorkdays());


            AlertDialog patientProfileDialog = new AlertDialog.Builder(context)
                    .setView(view)
                    .setTitle("Edit information for " + doctor.getName())
                    .create();

            patientProfileDialog.show();
        });
    }

    @Override
    public int getItemCount()
    {
        return doctors.size();
    }

}
