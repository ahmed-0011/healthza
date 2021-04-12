package com.example.healthza;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder>
{

    private List<Patient> patients;
    private Context context;
    private RecyclerViewInterface recyclerViewInterface;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    public PatientAdapter(Context context, List<Patient> patients, RecyclerViewInterface recyclerViewInterface)
    {
        this.context = context;
        this.patients = patients;
        this.recyclerViewInterface = recyclerViewInterface;
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView patientNameTextView, patientPhoneNumberTextView, patientIdentificationNumberTextView;
        private Button removeButton;
        private Button profileButton;

        public ViewHolder(View itemView)
        {
            super(itemView);

            patientNameTextView = itemView.findViewById(R.id.patientNameTextView);
            patientPhoneNumberTextView = itemView.findViewById(R.id.patientPhoneNumberTextView);
            patientIdentificationNumberTextView = itemView.findViewById(R.id.patientIdentificationNumberTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
            profileButton = itemView.findViewById(R.id.profileButton);

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
                .inflate(R.layout.list_item_patient, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Patient patient = patients.get(position);

        TextView patientNameTextView = holder.patientNameTextView;
        TextView patientPhoneNumberTextView = holder.patientPhoneNumberTextView;
        TextView patientIdentificationNumberTextView = holder.patientIdentificationNumberTextView;
        Button deletePatient = holder.removeButton;
        Button profileButton = holder.profileButton;

        String name = patientNameTextView.getText().toString() + " " + patient.getName();
        patientNameTextView.setText(name);
        String phoneNumber = patientPhoneNumberTextView.getText().toString() + " " + patient.getPhoneNumber();
        patientPhoneNumberTextView.setText(phoneNumber);
        String identificationNumber = patientIdentificationNumberTextView.getText().toString()
                + " " + patient.getIdentificationNumber();
        patientIdentificationNumberTextView.setText(identificationNumber);

        Toast.makeText(context, patient.getPatientId() , Toast.LENGTH_LONG).show();

        String userId = firebaseAuth.getCurrentUser().getUid();

        DocumentReference patientRef = db.collection("doctors")
                .document(userId)
                .collection("patients")
                .document(patient.getPatientId());


        deletePatient.setOnClickListener(v ->
        {
            patientRef.delete().addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    patients.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Patient " + "\"" + patient.getName() + "\" " + "has beed deleted successfully.",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            });
        });


        profileButton.setOnClickListener(v ->
        {
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.patient_profile_dialog, null);

            TextView patientEmailTextView = view.findViewById(R.id.patientEmailTextView);
            TextView patientSexTextView = view.findViewById(R.id.patientSexTextView);
            TextView pateintWeightTextView = view.findViewById(R.id.patientWeightTextView);
            TextView pateintHeightTextView = view.findViewById(R.id.patientHeightTextView);
            TextView pateintBMITextView = view.findViewById(R.id.patientBMITextView);
            TextView pateintDiseasesTextView = view.findViewById(R.id.patientDiseasesTextView);
            TextView pateintComplicationsTextView = view.findViewById(R.id.patientComplicationsTextView);

            ImageButton editWeightImageButton = view.findViewById(R.id.editWeightImageButton);
            ImageButton editHeightImageButton = view.findViewById(R.id.editHeightImageButton);
            ImageButton editDiseasesImageButton = view.findViewById(R.id.editDiseasesImageButton);
            ImageButton editComplicationsImageButton = view.findViewById(R.id.editComplicationsImageButton);


            String email = patientEmailTextView.getText().toString() + " " + patient.getEmail();
            patientEmailTextView.setText(email);
            String sex = patientSexTextView.getText().toString() + " " + patient.getSex();
            patientSexTextView.setText(sex);
            String weight = pateintWeightTextView.getText().toString() + " " + patient.getWeight();
            pateintWeightTextView.setText(weight);
            String height = pateintHeightTextView.getText().toString() + " " + patient.getHeight();
            pateintHeightTextView.setText(height);
            String bmi = pateintBMITextView.getText().toString() + " " + patient.getBmi();
            pateintBMITextView.setText(bmi);


            CollectionReference diseasesRefs = patientRef.collection("diseases");
            diseasesRefs.get().addOnCompleteListener(task ->
            {
                Disease disease = new Disease();

                for (QueryDocumentSnapshot document : task.getResult())
                {
                    disease = document.toObject(Disease.class);
                    pateintDiseasesTextView.append(disease.getDiseaseName());
                }
            });

            AlertDialog patientProfileDialog = new AlertDialog.Builder(context)
                    .setView(view)
                    .setTitle("Edit information for " + patient.getName())
                    .create();

            patientProfileDialog.show();
        });
    }

    @Override
    public int getItemCount()
    {
        return patients.size();
    }
}