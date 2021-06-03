package com.example.healthza.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthza.R;
import com.example.healthza.Toasty;
import com.example.healthza.adapters.BodyInfoAdapter;
import com.example.healthza.adapters.PatientAdapter;
import com.example.healthza.models.BodyInfo;
import com.example.healthza.models.Patient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PatientBodyInfoActivity extends AppCompatActivity implements BodyInfoAdapter.OnBodyInfoItemClickListener
{
    private ImageView noBodyInfoImageView;
    private TextView noBodyInfoTextView;
    private RecyclerView bodyInfoRecyclerView;
    private BodyInfoAdapter bodyInfoAdapter;
    private List<BodyInfo> bodyInfoList;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_body_info);

        noBodyInfoImageView = findViewById(R.id.noBodyInfoImageView);
        noBodyInfoTextView = findViewById(R.id.noBodyInfoTextView);

        firebaseAuth = firebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        patientId = firebaseAuth.getCurrentUser().getUid();

        bodyInfoList = new ArrayList<>();
        bodyInfoList.add(null);    // to display stick header instead of daily test item

        bodyInfoRecyclerView = findViewById(R.id.bodyInfoRecyclerView);

        setupActionBar();

        setupBodyInfoRecyclerView();
    }


    private void setupBodyInfoRecyclerView()
    {
        CollectionReference bodyInfoRefs = db.collection("patients").document(patientId)
                .collection("bodyInfoRecords");

        bodyInfoRefs
                .get()
                .addOnSuccessListener(bodyInfoDocuments ->
                {
                        for (QueryDocumentSnapshot bodyInfoDocument : bodyInfoDocuments)
                            bodyInfoList.add(bodyInfoDocument.toObject(BodyInfo.class));

                        if(bodyInfoList.size() == 1)    // empty (contains only the header)
                        {
                            noBodyInfoImageView.setVisibility(View.VISIBLE);
                            noBodyInfoTextView.setVisibility(View.VISIBLE);
                        }

                        bodyInfoAdapter = new BodyInfoAdapter(this, bodyInfoList, this);
                        bodyInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        bodyInfoRecyclerView.setAdapter(bodyInfoAdapter);
                });
    }


    private void setupActionBar()
    {
        getSupportActionBar().setTitle("Patient Body Info Records");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    @Override
    public void onBodyInfoItemClick(int position)
    {
        if(position != -1)
            Toasty.showText(this, "bmi = " + bodyInfoList.get(position).getBmi(),
                    Toasty.INFORMATION,Toast.LENGTH_SHORT);
    }


    @Override
    public void onBodyInfoItemLongClick(int position) { }


    @Override
    public void onRemoveButtonClick(int position)
    {
        db.collection("patients")
                .document(patientId)
                .collection("bodyInfoRecords")
                .document(bodyInfoList.get(position).getBodyInfoId())
                .delete().addOnCompleteListener(task ->
        {
            if(task.isSuccessful())
            {
                bodyInfoList.remove(position);
                bodyInfoAdapter.notifyItemRemoved(position);

                if(bodyInfoList.size() == 1)    // empty (contains only the header)
                {
                    noBodyInfoImageView.setVisibility(View.VISIBLE);
                    noBodyInfoTextView.setVisibility(View.VISIBLE);
                }

                Toasty.showText(this, "Record has been deleted successfully",
                        Toasty.SUCCESS, Toast.LENGTH_LONG);
            }
            else
                Toasty.showText(this, "An error occurred while trying to remove this record",
                        Toasty.ERROR, Toast.LENGTH_LONG);
        });
    }
}