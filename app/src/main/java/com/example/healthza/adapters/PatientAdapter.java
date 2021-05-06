package com.example.healthza.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthza.models.Doctor;
import com.example.healthza.models.Patient;
import com.example.healthza.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> implements Filterable {
    private final List<Patient> patientsAll;
    private final List<Patient> patients;
    private final Context context;
    private final OnPatientItemClickListener onPatientItemClickListener;

    public PatientAdapter(Context context, List<Patient> patients, OnPatientItemClickListener onPatientItemClickListener)
    {
        this.context = context;
        this.patients = patients;
        patientsAll = new ArrayList<>(patients);
        this.onPatientItemClickListener = onPatientItemClickListener;
    }

    private final Filter filter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            List<Patient> patientsfilteredList = new ArrayList<>();

            if(constraint.toString().isEmpty())
                patientsfilteredList.addAll(patientsAll);
            else
            {
                for(Patient patient : patientsAll)
                {
                    String key = constraint.toString().toLowerCase().trim();
                    String name = patient.getName().toLowerCase();
                    String identificationNumber = patient.getIdentificationNumber().trim();
                    String phonenumber = patient.getPhoneNumber();

                    if (name.startsWith(key) ||
                            identificationNumber.startsWith(key) ||
                            phonenumber.startsWith(key))
                    {
                        patientsfilteredList.add(patient);
                    }
                }
            }

            FilterResults  filterResults = new FilterResults();
            filterResults.values = patientsfilteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            patients.clear();
            patients.addAll((Collection<? extends Patient>) results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnPatientItemClickListener
    {
        void onItemClick(int position);

        void onItemLongClick(int position);

        void onProfileButtonClick(int position);

        void onRemoveButtonClick(int position);

        void onChartsButtonClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView patientNameTextView;
        private final TextView patientPhoneNumberTextView;
        private final TextView patientIdentificationNumberTextView;
        private final Button removeButton;
        private final Button profileButton;
        private final Button chartsButton;

        public ViewHolder(View itemView)
        {
            super(itemView);

            patientNameTextView = itemView.findViewById(R.id.patientNameTextView);
            patientPhoneNumberTextView = itemView.findViewById(R.id.patientPhoneNumberTextView);
            patientIdentificationNumberTextView = itemView.findViewById(R.id.patientIdentificationNumberTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
            profileButton = itemView.findViewById(R.id.profileButton);
            chartsButton = itemView.findViewById(R.id.chartsButton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPatientItemClickListener.onItemClick(getBindingAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onPatientItemClickListener.onItemLongClick(getBindingAdapterPosition());
                    return true;
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        Button removeButton = holder.removeButton;
        Button profileButton = holder.profileButton;
        Button  chartsButton = holder.chartsButton;

        patientNameTextView.append(patient.getName());
        patientPhoneNumberTextView.append(patient.getPhoneNumber());
        patientIdentificationNumberTextView.append(patient.getIdentificationNumber());

        chartsButton.setOnClickListener(v ->
        {
            onPatientItemClickListener.onChartsButtonClick(position);
        });

        profileButton.setOnClickListener(v ->
        {
            onPatientItemClickListener.onProfileButtonClick(position);
        });

        removeButton.setOnClickListener(v ->
        {
            onPatientItemClickListener.onRemoveButtonClick(position);
            removeButton.setEnabled(false);
        });
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder)
    {
        holder.patientNameTextView.setText(R.string.patient_name);
        holder.patientIdentificationNumberTextView.setText(R.string.identification_number);
        holder.patientPhoneNumberTextView.setText(R.string.phonenumber);
    }

    @Override
    public Filter getFilter()
    {
        return filter;
    }
}