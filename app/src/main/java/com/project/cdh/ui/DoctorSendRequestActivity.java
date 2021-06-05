package com.project.cdh.ui;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.cdh.DrawerUtil;
import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.project.cdh.adapters.SendRequestAdapter;
import com.project.cdh.models.Doctor;
import com.project.cdh.models.Patient;
import com.project.cdh.models.ReceiveRequest;
import com.project.cdh.models.SendRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DoctorSendRequestActivity extends AppCompatActivity implements SendRequestAdapter.OnSendRequestItemClickListener
{
    List<SendRequest> sendRequests;
    private ImageView emptySendRequestListImageView;
    private TextView emptySendRequestListTextView;
    private Button addPatientButton;
    private String fieldName;
    private String fieldValue;
    private SendRequestAdapter sendRequestAdapter;
    private RecyclerView sendRequestsRecyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_send_request);

        addPatientButton = findViewById(R.id.addPatientButton);
        emptySendRequestListImageView = findViewById(R.id.emptySendRequestListImageView);
        emptySendRequestListTextView = findViewById(R.id.emptySendRequestListTextView);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String doctorId = firebaseAuth.getCurrentUser().getUid();


        sendRequests = new ArrayList<>();
        sendRequestsRecyclerView = findViewById(R.id.sendRequestsRecyclerView);

        CollectionReference sendRequestsRef = db.collection("requests")
                .document("send_requests").collection(doctorId);

        sendRequestsRef.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                for (QueryDocumentSnapshot document : task.getResult())
                    sendRequests.add(document.toObject(SendRequest.class));

                if (sendRequests.isEmpty())
                {
                    emptySendRequestListImageView.setVisibility(View.VISIBLE);
                    emptySendRequestListTextView.setVisibility(View.VISIBLE);
                }

                sendRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                sendRequestAdapter = new SendRequestAdapter(this, sendRequests, this);
                sendRequestsRecyclerView.setAdapter(sendRequestAdapter);
            }
        });

        addPatientButton.setOnClickListener(v ->
        {

            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.dialog_add_patient, null);

            Button addPatientButton = view.findViewById(R.id.addPatientButton);
            RadioButton identificationNumberRadioButton = view.findViewById(R.id.identificationNumberRadioButton);
            RadioButton emailRadioButton = view.findViewById(R.id.emailRadioButton);
            RadioButton phoneNumberRadioButton = view.findViewById(R.id.phoneNumberRadioButton);
            EditText identificationNumberEditText = view.findViewById(R.id.identificationNumberEditText);
            EditText emailEditText = view.findViewById(R.id.patientEmailEditText);
            EditText phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);

            AlertDialog addPatientDialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .create();

            identificationNumberRadioButton.setOnClickListener(v1 ->
            {
                emailRadioButton.setChecked(false);
                phoneNumberRadioButton.setChecked(false);
                identificationNumberEditText.setEnabled(true);
                identificationNumberEditText.requestFocus();
                emailEditText.setEnabled(false);
                emailEditText.setText("");
                phoneNumberEditText.setEnabled(false);
                phoneNumberEditText.setText("");
            });

            emailRadioButton.setOnClickListener(v1 ->
            {
                identificationNumberRadioButton.setChecked(false);
                phoneNumberRadioButton.setChecked(false);
                emailEditText.setEnabled(true);
                emailEditText.requestFocus();
                identificationNumberEditText.setEnabled(false);
                identificationNumberEditText.setText("");
                phoneNumberEditText.setEnabled(false);
                phoneNumberEditText.setText("");
            });

            phoneNumberRadioButton.setOnClickListener(v1 ->
            {
                identificationNumberRadioButton.setChecked(false);
                emailRadioButton.setChecked(false);
                phoneNumberEditText.setEnabled(true);
                phoneNumberEditText.requestFocus();
                identificationNumberEditText.setEnabled(false);
                identificationNumberEditText.setText("");
                emailEditText.setEnabled(false);
                emailEditText.setText("");
            });

            addPatientButton.setOnClickListener(v1 ->
            {
                if(identificationNumberRadioButton.isChecked())
                {
                    fieldName = "identificationNumber";
                    fieldValue = identificationNumberEditText.getText().toString().trim();
                }
                else if(emailRadioButton.isChecked())
                {
                    fieldName = "email";
                    fieldValue = emailEditText.getText().toString().trim();
                }
                else
                {
                    fieldName = "phonenumber";
                    fieldValue = phoneNumberEditText.getText().toString().trim();
                }


            db.collection("patients")
                    .whereEqualTo(fieldName, fieldValue).limit(1)
                    .get()
                    .addOnSuccessListener(patientsDocuments ->
                    {
                        if (!patientsDocuments.isEmpty())
                        {
                            Patient patient = patientsDocuments.getDocuments()
                                    .get(0).toObject(Patient.class);

                            String patientId = patient.getPatientId();

                            DocumentReference doctorRef = db.collection("doctors")
                                    .document(doctorId);

                            DocumentReference patientRef = db.collection("patients")
                                    .document(patientId);

                            CollectionReference recvRequestsRef = db.collection("requests")
                                    .document("recv_requests").collection(patientId);

                            //to check if this patient is already in the doctor list
                            db.collection("doctors")
                                    .document(doctorId)
                                    .collection("patients")
                                    .document(patientId)
                                    .get()
                                    .addOnSuccessListener(patientDocument ->
                                    {
                                        if (patientDocument.exists())
                                        {
                                            Toasty.showText(this, "This patient is already on your list",
                                                    Toasty.WARNING, Toast.LENGTH_LONG);
                                        }
                                        else
                                        {
                                            //to check if there is already a request from that doctor
                                            db.collection("requests").document("send_requests")
                                                    .collection(doctorId).document(patientId)
                                                    .get().addOnSuccessListener(sendRequestDocument ->
                                            {
                                                if (sendRequestDocument.exists())
                                                    Toasty.showText(this, "You already sent a request to this patient",
                                                            Toasty.WARNING, Toast.LENGTH_LONG);
                                                else
                                                {
                                                    doctorRef.get().addOnSuccessListener(doctorDocument ->
                                                    {
                                                        if (doctorDocument.exists())
                                                        {
                                                            Doctor doctor = doctorDocument.toObject(Doctor.class);

                                                            ReceiveRequest receiveRequest = new ReceiveRequest(doctorId, doctor.getName(), doctor.getPhoneNumber(), getTodayDate());
                                                            recvRequestsRef.document(doctorId).set(receiveRequest);

                                                            SendRequest sendRequest = new SendRequest(patientId, patient.getName(), patient.getIdentificationNumber(), patient.getEmail(),
                                                                    patient.getPhoneNumber(), getTodayDate(),  "pending");
                                                            sendRequestsRef.document(patientId).set(sendRequest);

                                                            sendRequests.add(0, sendRequest);
                                                            sendRequestAdapter.notifyItemInserted(0);

                                                            emptySendRequestListImageView.setVisibility(View.GONE);
                                                            emptySendRequestListTextView.setVisibility(View.GONE);

                                                            Toasty.showText(this, "Add request was sent to " + patient.getName(),
                                                                    Toasty.INFORMATION, Toast.LENGTH_LONG);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                        }
                        else
                            Toasty.showText(this, "Patient not found",
                                    Toasty.ERROR, Toast.LENGTH_LONG);
                    });
                addPatientDialog.dismiss();
            });
            addPatientDialog.show();
            identificationNumberEditText.requestFocus();
        });
    }

   /* public static void setSnackBar(View root, String snackTitle) {
        Snackbar snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_SHORT);
        snackbar.show();

    }
*/

    public String getTodayDate()
    {
        Calendar calendar = Calendar.getInstance();

        return DateFormat.format("MM/dd/yyyy", calendar).toString();
    }

    @Override
    public void onItemClick(int position)
    {
        if (position != -1)
            Toast.makeText(this, sendRequests.get(position).getPatientName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void onCancelButtonClick(int position)
    {
        SendRequest sendRequest = sendRequests.get(position);

        String doctorId = firebaseAuth.getCurrentUser().getUid();
        String patientId = sendRequest.getPatientId();

        DocumentReference recvRequestRef = db.collection("requests")
                .document("recv_requests").collection(patientId)
                .document(doctorId);

        DocumentReference sendRequestRef = db.collection("requests")
                .document("send_requests").collection(doctorId)
                .document(patientId);

        /* delete send request document for doctor */
        sendRequestRef.delete().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                /* delete receive request document for patient */
                recvRequestRef.delete();
                sendRequests.remove(position);
                sendRequestAdapter.notifyItemRemoved(position);

                if (sendRequests.isEmpty())
                {
                    emptySendRequestListImageView.setVisibility(View.VISIBLE);
                    emptySendRequestListTextView            // check if there is no item and
                            .setVisibility(View.VISIBLE);   // show text view that there is no requests
                }
                Toasty.showText(this, "Request to " + sendRequest.getPatientName() + " has been cancelled successfully",
                        Toasty.SUCCESS, Toast.LENGTH_LONG);

            }
        });
    }

    @Override
    public void onRemoveButtonClick(int position) {
        SendRequest sendRequest = sendRequests.get(position);

        String doctorId = firebaseAuth.getCurrentUser().getUid();
        String patientId = sendRequest.getPatientId();

        DocumentReference sendRequestRef = db.collection("requests")
                .document("send_requests").collection(doctorId)
                .document(patientId);

        sendRequestRef.delete().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                sendRequests.remove(position);
                sendRequestAdapter.notifyItemRemoved(position);

                if (sendRequests.isEmpty()) {
                    emptySendRequestListImageView.setVisibility(View.VISIBLE);
                    emptySendRequestListTextView            // check if there is no item and
                            .setVisibility(View.VISIBLE);   // show text view that there is no requests
                }

                Toasty.showText(this, "Completed request removed",
                        Toasty.SUCCESS, Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getDoctorDrawer(this, 1);
    }
}