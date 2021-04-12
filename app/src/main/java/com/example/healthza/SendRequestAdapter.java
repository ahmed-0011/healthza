package com.example.healthza;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SendRequestAdapter extends RecyclerView.Adapter<SendRequestAdapter.ViewHolder>
{
    private List<SendRequest> sendRequests;
    private Context context;
    private RecyclerViewInterface recyclerViewInterface;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    public SendRequestAdapter(Context context, List<SendRequest> sendRequests, RecyclerViewInterface recyclerViewInterface)
    {
        this.sendRequests = sendRequests;
        this.context= context;
        this.recyclerViewInterface = recyclerViewInterface;
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {

        private TextView patientNameTextView;
        private TextView patientPhoneNumberTextView;
        private  TextView patientIdentificationNumberTextView;
        private  TextView requestStatusTextView;
        private  Button cancelButton;
        private Button removeButton;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            patientNameTextView = itemView.findViewById(R.id.patientNameTextView);
            patientPhoneNumberTextView = itemView.findViewById(R.id.patientPhoneNumberTextView);
            patientIdentificationNumberTextView =itemView.findViewById(R.id.patientIdentificationNumberTextView);
            requestStatusTextView = itemView.findViewById(R.id.requestStatusTextView);
            cancelButton = itemView.findViewById(R.id.cancelButton);
            removeButton = itemView.findViewById(R.id.removeButton);


            if(sendRequests.isEmpty())


            itemView.setOnClickListener(v -> {
                recyclerViewInterface.onItemClick(getBindingAdapterPosition());
            });

            itemView.setOnLongClickListener(v -> {
                recyclerViewInterface.onItemLongClick(getBindingAdapterPosition());
                return  true;
            });
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_doctor_send_request, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        SendRequest sendRequest = sendRequests.get(position);

        TextView patientNameTextView = holder.patientNameTextView;
        TextView patientPhoneNumberTextView = holder.patientPhoneNumberTextView;
        TextView patientIdentificationNumberTextView = holder.patientIdentificationNumberTextView;
        TextView requestStatusTextView = holder.requestStatusTextView;
        Button cancelButton = holder.cancelButton;
        Button removeButton = holder.removeButton;


        String patientName = patientNameTextView.getText().toString() + " " + sendRequest.getPatientName();
        patientNameTextView.setText(patientName);
        String phoneNumber = patientPhoneNumberTextView.getText().toString() + " " + sendRequest.getPhoneNumber();
        patientPhoneNumberTextView.setText(phoneNumber);
        String identificationNumber = patientIdentificationNumberTextView.getText().toString() + " "
            + sendRequest.getIdentificationNumber();
        patientIdentificationNumberTextView.setText(identificationNumber);
        requestStatusTextView.setText(sendRequest.getStatus());


        String patientId = sendRequest.getPatientId();
        String doctorId = firebaseAuth.getCurrentUser().getUid();


        DocumentReference recvRequestRef = db.collection("requests")
                .document("recv_requests").collection(patientId)
                .document(doctorId);

        DocumentReference sendRequestRef = db.collection("requests")
                .document("send_requests").collection(doctorId)
                .document(patientId);

        if(sendRequest.getStatus().equals("pending"))
            requestStatusTextView.setTextColor(context.getResources().getColor(R.color.yellow, null));
        else
        {
            cancelButton.setVisibility(View.GONE);
            removeButton.setVisibility(View.VISIBLE);


            requestStatusTextView.setTextColor(context.getResources().getColor(R.color.green, null));

            removeButton.setOnClickListener(v ->
            {
                sendRequestRef.delete().addOnCompleteListener(task ->
                {
                    if(task.isSuccessful())
                    {
                        sendRequests.remove(holder.getBindingAdapterPosition());
                        notifyItemRemoved(holder.getBindingAdapterPosition());

                        recyclerViewInterface.onNoItems(); // check if there is no item and
                        // show text view that there is no requests

                        Toast.makeText(context, "request removed", Toast.LENGTH_LONG).show();
                    }
                });
            });
        }


        cancelButton.setOnClickListener(v ->
        {
            /* delete send request document for doctor */
            sendRequestRef.delete().addOnCompleteListener(task ->
            {
                if(task.isSuccessful())
                {
                    /* delete receive request document for patient */
                    recvRequestRef.delete();
                    sendRequests.remove(holder.getBindingAdapterPosition());
                    notifyItemRemoved(holder.getBindingAdapterPosition());

                    recyclerViewInterface.onNoItems(); // check if there is no item and
                                                       // show text view that there is no requests

                    Toast.makeText(context, "request to " + sendRequest.getPatientName() + " cancelled successfully", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public int getItemCount()
    {
        return sendRequests.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder)
    {
        holder.patientNameTextView.setText(R.string.patient_name);
        holder.patientPhoneNumberTextView.setText(R.string.phonenumber);
        holder.patientIdentificationNumberTextView.setText(R.string.id);
    }
}
