package com.project.cdh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.cdh.R;
import com.project.cdh.models.ReceiveRequest;

import java.util.List;

public class ReceiveRequestAdapter extends RecyclerView.Adapter<ReceiveRequestAdapter.ViewHolder>
{
    private final Context context;
    private final List<ReceiveRequest> receiveRequests;
    private final OnReceiveRequestItemClickListener onReceiveRequestItemClickListener;

    public ReceiveRequestAdapter(Context context, List<ReceiveRequest> receiveRequests, OnReceiveRequestItemClickListener onReceiveRequestItemClickListener)
    {
        this.context = context;
        this.receiveRequests = receiveRequests;
        this.onReceiveRequestItemClickListener = onReceiveRequestItemClickListener;
    }

    public interface OnReceiveRequestItemClickListener
    {
        void onItemClick(int position);

        void onItemLongClick(int position);

        void onCancelClick(int position);

        void onAcceptClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView doctorNameTextView;
        private final TextView doctorPhoneNumberTextView;
        private final TextView requestDateTextView;
        private final Button acceptButton;
        private final Button cancelButton;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            doctorNameTextView = itemView.findViewById(R.id.doctorNameTextView);
            doctorPhoneNumberTextView = itemView.findViewById(R.id.doctorPhoneNumberTextView);
            requestDateTextView = itemView.findViewById(R.id.requestDateTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);


            itemView.setOnClickListener(v -> {
                onReceiveRequestItemClickListener.onItemClick(getBindingAdapterPosition());
            });

            itemView.setOnLongClickListener(v -> {
                onReceiveRequestItemClickListener.onItemLongClick(getBindingAdapterPosition());
                return true;
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_patient_receive_request, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReceiveRequest receiveRequest = receiveRequests.get(position);

        TextView doctorNameTextView = holder.doctorNameTextView;
        TextView doctorPhoneNumberTextView = holder.doctorPhoneNumberTextView;
        TextView requestDateTextView = holder.requestDateTextView;
        Button acceptButton = holder.acceptButton;
        Button cancelButton = holder.cancelButton;

        doctorNameTextView.append(receiveRequest.getDoctorName());
        doctorPhoneNumberTextView.append(receiveRequest.getPhoneNumber());
        requestDateTextView.setText(receiveRequest.getRequestDate());

        acceptButton.setOnClickListener(v ->
        {
            onReceiveRequestItemClickListener.onAcceptClick(holder.getBindingAdapterPosition());
            acceptButton.setEnabled(false);
            cancelButton.setEnabled(false);
        });

        cancelButton.setOnClickListener(v ->
        {
            onReceiveRequestItemClickListener.onCancelClick(holder.getBindingAdapterPosition());
            cancelButton.setEnabled(false);
            acceptButton.setEnabled(false);
        });
    }

    @Override
    public int getItemCount() {
        return receiveRequests.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder)
    {
        holder.doctorNameTextView.setText(R.string.doctor_name);
        holder.doctorPhoneNumberTextView.setText(R.string.phonenumber);
        holder.cancelButton.setEnabled(true);
        holder.acceptButton.setEnabled(true);
    }
}