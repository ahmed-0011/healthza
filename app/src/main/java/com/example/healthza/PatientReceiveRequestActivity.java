package com.example.healthza;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PatientReceiveRequestActivity extends AppCompatActivity implements RecyclerViewInterface {
    private List<ReceiveRequest> receiveRequests;
    private RecyclerView recyclerView;
    private ReceiveRequestAdapter receiveRequestAdapter;
    private TextView emptyRequestListTextView;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_receive_request);

        emptyRequestListTextView = findViewById(R.id.emptyRequestListTextView);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String patientId = firebaseAuth.getCurrentUser().getUid();

        receiveRequests = new ArrayList<>();

        recyclerView = findViewById(R.id.receiveRequestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        CollectionReference recvRequestsRef = db.collection("requests")
                .document("recv_requests").collection(patientId);

        recvRequestsRef.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                for (QueryDocumentSnapshot document : task.getResult())
                    receiveRequests.add(document.toObject(ReceiveRequest.class));

                if (receiveRequests.isEmpty())
                    emptyRequestListTextView.setVisibility(View.VISIBLE);

                receiveRequestAdapter = new ReceiveRequestAdapter(this, receiveRequests, this);
                recyclerView.setAdapter(receiveRequestAdapter);
            }
        });
    }

    @Override
    public void onItemClick(int position)
    {
        Toast.makeText(this, receiveRequests.get(position).getDoctorName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position)
    {

    }

    @Override
    public void onNoItems()
    {
        if (receiveRequests.isEmpty())
            emptyRequestListTextView.setVisibility(View.VISIBLE);
    }
}