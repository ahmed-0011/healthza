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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReceiveRequestAdapter extends RecyclerView.Adapter<ReceiveRequestAdapter.ViewHolder>
{
    List<ReceiveRequest> receiveRequests;
    Context context;
    RecyclerViewInterface recyclerViewInterface;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    public ReceiveRequestAdapter(Context context, List<ReceiveRequest> receiveRequests, RecyclerViewInterface recyclerViewInterface)
    {
        this.receiveRequests = receiveRequests;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {

        private TextView doctorNameTextView, doctorPhoneNumberTextView, requestDateTextView;
        private Button acceptButton, cancelButton;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            doctorNameTextView = itemView.findViewById(R.id.doctorNameTextView);
            doctorPhoneNumberTextView = itemView.findViewById(R.id.doctorPhoneNumberTextView);
            requestDateTextView = itemView.findViewById(R.id.requestDateTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);


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
                .inflate(R.layout.list_item_patient_receive_request, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        ReceiveRequest receiveRequest = receiveRequests.get(position);

        TextView doctorNameTextView = holder.doctorNameTextView;
        TextView doctorPhoneNumberTextView = holder.doctorPhoneNumberTextView;
        TextView requestDateTextView = holder.requestDateTextView;
        Button acceptButton = holder.acceptButton;
        Button cancelButton = holder.cancelButton;

        String doctorName = doctorNameTextView.getText() + " " + receiveRequest.getDoctorName();
        doctorNameTextView.setText(doctorName);
        String phoneNumber = doctorPhoneNumberTextView.getText().toString() + " " + receiveRequest.getPhoneNumber();
        doctorPhoneNumberTextView.setText(phoneNumber);
        requestDateTextView.setText(receiveRequest.getRequestDate());

        String doctorId = receiveRequest.getDoctorId();
        String patientId = firebaseAuth.getCurrentUser().getUid();

        DocumentReference patientRef = db.collection("patients")
                .document(patientId);

        DocumentReference recvRequestRef = db.collection("requests")
                .document("recv_requests").collection(patientId).document(doctorId);

        DocumentReference doctorRef = db.collection("doctors")
                .document(doctorId);

        DocumentReference sendRequestRef = db.collection("requests")
                .document("send_requests").collection(doctorId)
                .document(patientId);

        acceptButton.setOnClickListener(v ->
        {
            /* get doctor document to add him to his patient doctors sub-collection */
            doctorRef.get().addOnCompleteListener(task ->
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();

                    if(document.exists())
                    {
                        Doctor doctor = document.toObject(Doctor.class);

                        CollectionReference patientDoctorsRef = patientRef.collection("doctors");

                        patientDoctorsRef.document(doctorId).set(doctor);

                        /* get patient document to add him to his doctor patients sub-collection */
                        patientRef.get().addOnCompleteListener(task1 ->
                        {
                            if(task1.isSuccessful())
                            {
                                DocumentSnapshot document1 = task1.getResult();
                                if(document1.exists())
                                {
                                    Patient patient = document1.toObject(Patient.class);

                                    CollectionReference doctorPatientsRef = doctorRef.
                                            collection("patients");

                                    doctorPatientsRef.document(patientId).set(patient);

                                    /* delete send request document from patient recv_requests sub-collection */
                                    recvRequestRef.delete();

                                    /* update the status of send request document for doctor to complete */
                                    sendRequestRef.update("status", "complete");

                                    receiveRequests.remove(holder.getBindingAdapterPosition());
                                    notifyItemRemoved(holder.getBindingAdapterPosition());

                                    recyclerViewInterface.onNoItems(); // check if there is no item and
                                                                       // show text view that there is no requests

                                    Toast.makeText(context, "Add request from "
                                            + receiveRequest.getDoctorName() +
                                            " Accepted successfully", Toast.LENGTH_LONG).show();
                                }
                                else
                                    Toast.makeText(context, "Something went wrong...",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        });

        cancelButton.setOnClickListener(v ->
        {
            /* delete receive request document for patient */
            recvRequestRef.delete().addOnCompleteListener(task ->
            {
                if(task.isSuccessful())
                {
                    /* delete send request document for doctor */
                    sendRequestRef.delete();

                    receiveRequests.remove(holder.getBindingAdapterPosition());
                    notifyItemRemoved(holder.getBindingAdapterPosition());

                    recyclerViewInterface.onNoItems(); // check if there is no item and
                                                       // show text view that there is no requests

                    Toast.makeText(context, "request from " + receiveRequest.getDoctorName() + " cancelled successfully", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(context, "Something went wrong...",Toast.LENGTH_LONG).show();
            });
        });
    }

    @Override
    public int getItemCount()
    {
        return receiveRequests.size();
    }
}