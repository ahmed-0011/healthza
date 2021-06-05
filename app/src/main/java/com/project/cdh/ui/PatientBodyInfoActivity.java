package com.project.cdh.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.WriteBatch;
import com.project.cdh.ProgressDialog;
import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.project.cdh.adapters.BodyInfoAdapter;
import com.project.cdh.models.BodyInfo;
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
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_body_info);

        noBodyInfoImageView = findViewById(R.id.noBodyInfoImageView);
        noBodyInfoTextView = findViewById(R.id.noBodyInfoTextView);

        firebaseAuth = firebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        patientId = getIntent().getStringExtra("patientID");
        if((patientId == null) || patientId.isEmpty() )
            patientId = FirebaseAuth.getInstance().getUid();


        bodyInfoList = new ArrayList<>();
        bodyInfoList.add(null);    // to display stick header instead of daily test item

        bodyInfoRecyclerView = findViewById(R.id.bodyInfoRecyclerView);

        setupActionBar();

        progressDialog = new ProgressDialog(this);
        progressDialog.showProgressDialog("Displaying body info records...");

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


                        progressDialog.dismissProgressDialog();
                });
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

        new MaterialAlertDialogBuilder(this, R.style.DeleteDialogTheme)
                .setTitle("Delete Record")
                .setMessage("Do you want to delete this record ?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("patients")
                                .document(patientId)
                                .collection("bodyInfoRecords")
                                .document(bodyInfoList.get(position).getBodyInfoId())
                                .delete().addOnCompleteListener(task ->
                        {
                            if (task.isSuccessful()) {
                                bodyInfoList.remove(position);
                                bodyInfoAdapter.notifyItemRemoved(position);


                                if (bodyInfoList.size() == 1)    // empty (contains only the header)
                                {
                                    noBodyInfoImageView.setVisibility(View.VISIBLE);
                                    noBodyInfoTextView.setVisibility(View.VISIBLE);
                                }

                                Toasty.showText(PatientBodyInfoActivity.this, "Record has been deleted successfully",
                                        Toasty.SUCCESS, Toast.LENGTH_LONG);
                            }
                            else
                                Toasty.showText(PatientBodyInfoActivity.this, "An error occurred while trying to remove this record",
                                        Toasty.ERROR, Toast.LENGTH_LONG);
                        });
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                })
                .show();
    }



    private void setupActionBar()
    {
        getSupportActionBar().setTitle("Patient Body Info Records");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}