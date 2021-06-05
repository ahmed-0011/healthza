package com.project.cdh.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.cdh.DrawerUtil;
import com.project.cdh.Toasty;
import com.project.cdh.models.Doctor;
import com.project.cdh.models.Patient;
import com.project.cdh.R;
import com.project.cdh.models.ReceiveRequest;
import com.project.cdh.adapters.ReceiveRequestAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientReceiveRequestActivity extends AppCompatActivity implements ReceiveRequestAdapter.OnReceiveRequestItemClickListener
{
    private ImageView emptyReceiveRequestListImageView;
    private TextView emptyReceiveRequestListTextView;
    private List<ReceiveRequest> receiveRequests;
    private RecyclerView receiveRequestsRecyclerView;
    private ReceiveRequestAdapter receiveRequestAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_receive_request);

        emptyReceiveRequestListImageView = findViewById(R.id.emptyReceiveRequestListImageView);
        emptyReceiveRequestListTextView = findViewById(R.id.emptyReceiveRequestListTextView);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String patientId = firebaseAuth.getCurrentUser().getUid();


        receiveRequests = new ArrayList<>();
        receiveRequestsRecyclerView = findViewById(R.id.receiveRequestsRecyclerView);
        receiveRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        CollectionReference recvRequestsRef = db.collection("requests")
                .document("recv_requests").collection(patientId);

        recvRequestsRef.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                for (QueryDocumentSnapshot document : task.getResult())
                    receiveRequests.add(document.toObject(ReceiveRequest.class));

                if (receiveRequests.isEmpty())
                {
                    emptyReceiveRequestListImageView.setVisibility(View.VISIBLE);
                    emptyReceiveRequestListTextView.setVisibility(View.VISIBLE);
                }

                receiveRequestAdapter = new ReceiveRequestAdapter(this, receiveRequests, this);
                receiveRequestsRecyclerView.setAdapter(receiveRequestAdapter);
            }
        });
    }

    @Override
    public void onItemClick(int position)
    {
        if(position != -1)
            Toast.makeText(this, receiveRequests.get(position).getDoctorName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position) { }

    @Override
    public void onAcceptClick(int position) {
        ReceiveRequest receiveRequest = receiveRequests.get(position);

        String patientId = firebaseAuth.getCurrentUser().getUid();
        String doctorId = receiveRequest.getDoctorId();

        DocumentReference patientRef = db.collection("patients")
                .document(patientId);

        DocumentReference recvRequestRef = db.collection("requests")
                .document("recv_requests").collection(patientId)
                .document(doctorId);

        DocumentReference doctorRef = db.collection("doctors")
                .document(doctorId);

        DocumentReference sendRequestRef = db.collection("requests")
                .document("send_requests").collection(doctorId)
                .document(patientId);

        /* get doctor info to add him to his patient doctors sub-collection */
        doctorRef.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    Doctor doctor = document.toObject(Doctor.class);

                    CollectionReference patientDoctorsRef = patientRef.collection("doctors");

                    Map<String, Object> doctorInfo = new HashMap<>();
                    doctorInfo.put("doctorId", doctor.getDoctorId());
                    doctorInfo.put("name", doctor.getName());
                    doctorInfo.put("phoneNumber", doctor.getPhoneNumber());
                    doctorInfo.put("startedChat", false);

                    patientDoctorsRef.document(doctorId).set(doctorInfo);

                    /* get patient Info to add him to his doctor patients sub-collection */
                    patientRef.get().addOnCompleteListener(task1 ->
                    {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot document1 = task1.getResult();
                            if (document1.exists())
                            {
                                Patient patient = document1.toObject(Patient.class);

                                CollectionReference doctorPatientsRef = doctorRef.
                                        collection("patients");

                                Map<String, Object> patientInfo = new HashMap<>();
                                patientInfo.put("patientId", patient.getPatientId());
                                patientInfo.put("name", patient.getName());
                                patientInfo.put("identificationNumber", patient.getIdentificationNumber());
                                patientInfo.put("phoneNumber", patient.getPhoneNumber());
                                patientInfo.put("startedChat", false);

                                doctorPatientsRef.document(patientId).set(patientInfo);

                                /* delete send request document from patient recv_requests sub-collection */
                                recvRequestRef.delete();

                                /* update the status of send request document for doctor to complete */
                                sendRequestRef.update("status", "complete");

                                receiveRequests.remove(position);
                                receiveRequestAdapter.notifyItemRemoved(position);

                                Toasty.showText(this, "Add request from "
                                        + receiveRequest.getDoctorName() +
                                        " accepted successfully", Toasty.SUCCESS, Toast.LENGTH_LONG);
                            } else
                                Toasty.showText(this, "Something went wrong...", Toasty.ERROR,Toast.LENGTH_LONG);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onCancelClick(int position)
    {
        ReceiveRequest receiveRequest = receiveRequests.get(position);

        String patientId = firebaseAuth.getCurrentUser().getUid();
        String doctorId = receiveRequest.getDoctorId();

        DocumentReference recvRequestRef = db.collection("requests")
                .document("recv_requests").collection(patientId)
                .document(doctorId);

        DocumentReference sendRequestRef = db.collection("requests")
                .document("send_requests").collection(doctorId)
                .document(patientId);

        /* delete receive request document for patient */
        recvRequestRef.delete().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                    /* delete send request document for doctor */
                    sendRequestRef.delete();
                    receiveRequests.remove(position);
                    receiveRequestAdapter.notifyItemRemoved(position);

                    if (receiveRequests.isEmpty())
                    {
                        emptyReceiveRequestListImageView.setVisibility(View.VISIBLE);
                        emptyReceiveRequestListTextView         // check if there is no item and
                                .setVisibility(View.VISIBLE);   // show text view that there is no requests
                    }
                    Toasty.showText(this, "Request from " + receiveRequest.getDoctorName() + " cancelled successfully", Toasty.SUCCESS,
                            Toast.LENGTH_LONG);
            }
            else
                Toasty.showText(this, "Something went wrong...", Toasty.ERROR,
                        Toast.LENGTH_LONG);
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getPatientDrawer(this, 1);
    }
}