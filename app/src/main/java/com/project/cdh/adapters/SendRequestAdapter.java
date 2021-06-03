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
import com.project.cdh.models.SendRequest;

import java.util.List;

public class SendRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final Context context;
    private final List<SendRequest> sendRequests;
    private final OnSendRequestItemClickListener onSendRequestItemClickListener;
    private final int PENDING = 0, COMPLETED = 1;

    public SendRequestAdapter(Context context, List<SendRequest> sendRequests, OnSendRequestItemClickListener onSendRequestItemClickListener)
    {
        this.context = context;
        this.sendRequests = sendRequests;
        this.onSendRequestItemClickListener = onSendRequestItemClickListener;
    }

    public interface OnSendRequestItemClickListener
    {
        void onItemClick(int position);

        void onItemLongClick(int position);

        void onCancelButtonClick(int position);

        void onRemoveButtonClick(int position);
    }


    class RequestPendingViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView patientNameTextView;
        private final TextView patientIdentificationNumberTextView;
        private final TextView patientPhoneNumberTextView;
        private final TextView patientEmailTextView;
        private final Button cancelButton;

        public RequestPendingViewHolder(@NonNull View itemView)
        {
            super(itemView);

            patientNameTextView = itemView.findViewById(R.id.patientNameTextView);
            patientIdentificationNumberTextView = itemView.findViewById(R.id.patientIdentificationNumberTextView);
            patientEmailTextView = itemView.findViewById(R.id.patientEmailTextView);
            patientPhoneNumberTextView = itemView.findViewById(R.id.patientPhoneNumberTextView);
            cancelButton = itemView.findViewById(R.id.cancelButton);

            itemView.setOnClickListener(v -> {
                onSendRequestItemClickListener.onItemClick(getBindingAdapterPosition());
            });

            itemView.setOnLongClickListener(v -> {
                onSendRequestItemClickListener.onItemLongClick(getBindingAdapterPosition());
                return true;
            });
        }

        private void setRequestPending(SendRequest sendRequest)
        {
            patientNameTextView.append(sendRequest.getPatientName());
            patientIdentificationNumberTextView.append(sendRequest.getIdentificationNumber());
            patientEmailTextView.append(sendRequest.getEmail());
            patientPhoneNumberTextView.append(sendRequest.getPhoneNumber());

            cancelButton.setOnClickListener(v ->
            {
                onSendRequestItemClickListener.onCancelButtonClick(getBindingAdapterPosition());
                cancelButton.setEnabled(false);
            });
        }

        private void clearViewHolder()
        {
            patientNameTextView.setText(R.string.patient_name);
            patientIdentificationNumberTextView.setText(R.string.id);
            patientEmailTextView.setText(R.string.email);
            patientPhoneNumberTextView.setText(R.string.phonenumber);
            cancelButton.setEnabled(true);
        }
    }

    class RequestCompletedViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView patientNameTextView;
        private final TextView patientIdentificationNumberTextView;
        private final TextView patientEmailTextView;
        private final TextView patientPhoneNumberTextView;
        private final Button removeButton;

        public RequestCompletedViewHolder(@NonNull View itemView)
        {
            super(itemView);

            patientNameTextView = itemView.findViewById(R.id.patientNameTextView);
            patientIdentificationNumberTextView = itemView.findViewById(R.id.patientIdentificationNumberTextView);
            patientEmailTextView = itemView.findViewById(R.id.patientEmailTextView);
            patientPhoneNumberTextView = itemView.findViewById(R.id.patientPhoneNumberTextView);
            removeButton = itemView.findViewById(R.id.removeButton);

            itemView.setOnClickListener(v -> {
                onSendRequestItemClickListener.onItemClick(getBindingAdapterPosition());
            });

            itemView.setOnLongClickListener(v -> {
                onSendRequestItemClickListener.onItemLongClick(getBindingAdapterPosition());
                return true;
            });
        }

        private void setRequestCompleted(SendRequest sendRequest)
        {
            patientNameTextView.append(sendRequest.getPatientName());
            patientIdentificationNumberTextView.append(sendRequest.getIdentificationNumber());
            patientEmailTextView.append(sendRequest.getEmail());
            patientPhoneNumberTextView.append(sendRequest.getPhoneNumber());
            removeButton.setOnClickListener(v -> {
                onSendRequestItemClickListener.onRemoveButtonClick(getBindingAdapterPosition());
                removeButton.setEnabled(false);
            });
        }

        private void clearViewHolder()
        {
            patientNameTextView.setText(R.string.patient_name);
            patientIdentificationNumberTextView.setText(R.string.id);
            patientEmailTextView.setText(R.string.email);
            patientPhoneNumberTextView.setText(R.string.phonenumber);
            removeButton.setEnabled(true);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType == PENDING)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_doctor_send_request_pending, parent, false);

            return new RequestPendingViewHolder(view);
        }

        else
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_doctor_send_request_completed, parent, false);

            return new RequestCompletedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        SendRequest sendRequest = sendRequests.get(position);

        if(getItemViewType(position) == PENDING)
            ((RequestPendingViewHolder) holder).setRequestPending(sendRequest);
        else
            ((RequestCompletedViewHolder) holder).setRequestCompleted(sendRequest);
    }

    @Override
    public int getItemCount()
    {
        return sendRequests.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        SendRequest sendRequest = sendRequests.get(position);

        String status = sendRequest.getStatus();
        if(status.equals("pending"))
            return PENDING;
        else
            return COMPLETED;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder)
    {
        if(holder instanceof RequestPendingViewHolder)
            ((RequestPendingViewHolder) holder).clearViewHolder();
        else
            ((RequestCompletedViewHolder) holder).clearViewHolder();
    }
}