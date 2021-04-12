package com.example.healthza;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DoctorSendRequestActivity extends AppCompatActivity implements RecyclerViewInterface
{

    List<SendRequest> sendRequests;
    private TextInputLayout patientEmailInputLayout;
    private TextInputEditText patientEmailInputEditText;
    private TextView emptyRequestListTextView;
    private Button addPatientButton;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private SendRequestAdapter sendRequestAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_send_request);


        patientEmailInputLayout = findViewById(R.id.patientEmailInputLayout);
        patientEmailInputEditText = findViewById(R.id.patientEmailInputEditText);
        addPatientButton = findViewById(R.id.addPatientButton);
        emptyRequestListTextView = findViewById(R.id.emptyRequestListTextView);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String doctorId = firebaseAuth.getCurrentUser().getUid();

        sendRequests = new ArrayList<>();

        recyclerView = findViewById(R.id.sendRequestsRecyclerView);


        CollectionReference sendRequestsRef = db.collection("requests")
                .document("send_requests").collection(doctorId);


        sendRequestsRef.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult())
                    sendRequests.add(document.toObject(SendRequest.class));

                if (sendRequests.isEmpty())
                    emptyRequestListTextView.setVisibility(View.VISIBLE);

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                sendRequestAdapter = new SendRequestAdapter(this, sendRequests, this);
                recyclerView.setAdapter(sendRequestAdapter);
            }
        });


        addPatientButton.setOnClickListener(v ->
        {
            String patientEmail = patientEmailInputEditText.getText().toString().trim();

            db.collection("patients")
                    .whereEqualTo("email", patientEmail)
                    .get()
                    .addOnCompleteListener(task ->
                    {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Patient patient = document.toObject(Patient.class);

                            String patientId = patient.getPatientId();


                            DocumentReference doctorRef = db.collection("doctors").document(doctorId);
                            DocumentReference patientRef = db.collection("patients").document(patientId);

                            CollectionReference recvRequestsRef = db.collection("requests")
                                    .document("recv_requests").collection(patientId);

                            //to check if there is already a request from that doctor
                            db.collection("requests").document("send_requests")
                                    .collection(doctorId).document(patientId)
                                    .get().addOnCompleteListener(task1 ->
                            {
                                if (task1.isSuccessful())
                                {
                                    DocumentSnapshot document1 = task1.getResult();

                                    if (!document1.exists())
                                    {
                                        doctorRef.get().addOnCompleteListener(task2 ->
                                        {
                                            if (task2.isSuccessful())
                                            {
                                                DocumentSnapshot document2 = task2.getResult();

                                                if (document2.exists())
                                                {
                                                    Doctor doctor = document2.toObject(Doctor.class);

                                                    ReceiveRequest receiveRequest = new ReceiveRequest(doctorId, doctor.getName(), doctor.getPhoneNumber(), getTodayDate());
                                                    recvRequestsRef.document(doctorId).set(receiveRequest);

                                                    SendRequest sendRequest = new SendRequest(patientId, patient.getName(), patient.getPhoneNumber(), getTodayDate(), patient.getIdentificationNumber(), "pending");
                                                    sendRequestsRef.document(patientId).set(sendRequest);

                                                    sendRequests.add(0, sendRequest);
                                                    sendRequestAdapter.notifyItemInserted(0);

                                                    emptyRequestListTextView.setVisibility(View.GONE);

                                                    Toast.makeText(this, "Add request was sent to " + patient.getName(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    }
                                    else
                                        Toast.makeText(this, "You already sent a request for that patient", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
        });
    }


/*
    public static void setSnackBar(View root, String snackTitle) {
        Snackbar snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_SHORT);
        snackbar.show();

    }
*/

    public String getTodayDate() {
        Calendar calendar = Calendar.getInstance();

        /*  get today date  */
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        String todayDate = DateFormat.format("MM/dd/yyyy", calendar).toString();

        return todayDate;
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, sendRequests.get(position).getPatientName(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemLongClick(int position) {
        //setSnackBar(findViewById(R.id.doctorSendRequestConstraintLayout), "fehh");
        sendRequests.remove(position);
        sendRequestAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onNoItems()
    {
        if (sendRequests.isEmpty())
            emptyRequestListTextView.setVisibility(View.VISIBLE);
    }

}