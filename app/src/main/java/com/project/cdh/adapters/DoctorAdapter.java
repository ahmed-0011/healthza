package com.project.cdh.adapters;

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
import com.project.cdh.models.Doctor;
import com.project.cdh.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> implements Filterable
{
    private final Context context;
    private final List<Doctor> doctorsAll;
    private final List<Doctor> doctors;
    private final OnDoctorItemClickListener onDoctorItemClickListener;

    public DoctorAdapter(Context context, List<Doctor> doctors, OnDoctorItemClickListener onDoctorItemClickListener)
    {
        this.context = context;
        this.doctors = doctors;
        doctorsAll = new ArrayList<>(doctors);
        this.onDoctorItemClickListener = onDoctorItemClickListener;
    }

    private final Filter filter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            List<Doctor> doctorsFilteredList = new ArrayList<>();

            if(constraint.toString().isEmpty())
                doctorsFilteredList.addAll(doctorsAll);
            else
            {
                for(Doctor doctor : doctorsAll)
                {
                    String key = constraint.toString().toLowerCase().trim();
                    String doctorName = doctor.getName().toLowerCase();
                    String doctorPhoneNumber = doctor.getPhoneNumber();

                    if (doctorName.startsWith(key) || doctorPhoneNumber.startsWith(key))
                        doctorsFilteredList.add(doctor);

                }
            }

            FilterResults  filterResults = new FilterResults();
            filterResults.values = doctorsFilteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            doctors.clear();
            doctors.addAll((Collection<? extends Doctor>) results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnDoctorItemClickListener
    {
        void onItemClick(int position);
        void onItemLongClick(int position);
        void onProfileButtonClick(int position);
        void onRemoveButtonClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView doctorNameTextView;
        private final TextView doctorPhoneNumberTextView;
        private final Button profileButton;
        private final Button removeButton;

        public ViewHolder(View itemView)
        {
            super(itemView);

            doctorNameTextView = itemView.findViewById(R.id.doctorNameTextView);
            doctorPhoneNumberTextView = itemView.findViewById(R.id.doctorPhoneNumberTextView);
            profileButton = itemView.findViewById(R.id.profileButton);
            removeButton = itemView.findViewById(R.id.removeButton);

            itemView.setOnClickListener(v ->
                    onDoctorItemClickListener.onItemClick(getBindingAdapterPosition()));

            itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    onDoctorItemClickListener.onItemLongClick(getBindingAdapterPosition());
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
    public void onBindViewHolder(@NonNull DoctorAdapter.ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);

        TextView doctorNameTextView = holder.doctorNameTextView;
        TextView doctorPhoneNumberTextView = holder.doctorPhoneNumberTextView;
        Button profileButton = holder.profileButton;
        Button removeButton = holder.removeButton;

        doctorNameTextView.append(doctor.getName());
        doctorPhoneNumberTextView.append(doctor.getPhoneNumber());

        profileButton.setOnClickListener(v ->
        {
            onDoctorItemClickListener.onProfileButtonClick(holder.getBindingAdapterPosition());
        });

        removeButton.setOnClickListener(v ->
        {
            onDoctorItemClickListener.onRemoveButtonClick(holder.getBindingAdapterPosition());
            removeButton.setEnabled(false);
        });
    }

    @Override
    public int getItemCount()
    {
        return doctors.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder)
    {
        holder.doctorNameTextView.setText(R.string.doctor_name);
        holder.doctorPhoneNumberTextView.setText(R.string.phonenumber);
        holder.removeButton.setEnabled(true);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

}
