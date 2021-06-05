package com.project.cdh.ui;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.project.cdh.ProgressDialog;
import com.project.cdh.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.cdh.Toasty;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PatientTestsStatisticsActivity extends AppCompatActivity
{
    private CardView glucoseCardView, hypertensionCardView, totalCholesterolCardView,
                     hdlCardView, ldlCardView, triglycerideCardView;
    private ImageView noTestsImageView;
    private TextView noTestsTextView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String patientId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_tests_statistics);

        noTestsImageView = findViewById(R.id.noTestsImageView);
        noTestsTextView = findViewById(R.id.noTestsTextView);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        patientId = getIntent().getStringExtra("patientID");


        if((patientId == null) || patientId.isEmpty() )
            patientId = FirebaseAuth.getInstance().getUid();


        setupActionBar();

        progressDialog = new ProgressDialog(this);
        progressDialog.showProgressDialog("Displaying tests statistics...");


        setupTestsCardViews();
    }


    private void setupTestsCardViews()
    {
        setupGlucoseCardView();
    }


    private void setupGlucoseCardView()
    {

        TextView glucoseTodayTestsTextView, latestGlucoseTestLevelTextView, latestGlucoseTestTimeTextView,
                highestGlucoseLevelTextView, lowestGlucoseLevelTextView, totalGlucoseTestsTextView;

        glucoseCardView = findViewById(R.id. glucoseCardView);
        glucoseTodayTestsTextView = findViewById(R.id.glucoseTodayTestsTextView);
        latestGlucoseTestLevelTextView = findViewById(R.id.latestGlucoseTestLevelTextView);
        latestGlucoseTestTimeTextView = findViewById(R.id.latestGlucoseTestTimeTextView);
        highestGlucoseLevelTextView = findViewById(R.id.highestGlucoseLevelTextView);
        lowestGlucoseLevelTextView = findViewById(R.id.lowestGlucoseLevelTextView);
        totalGlucoseTestsTextView = findViewById(R.id.totalGlucoseTestsTextView);


        db.collection("patients")
                .document(patientId)
                .collection("tests")
                .document("glucose_test")
                .get()
                .addOnSuccessListener(glucoseTestsDocument ->
                {
                    if (glucoseTestsDocument.exists())
                    {
                        glucoseCardView.setVisibility(View.VISIBLE);

                        int totalNumberOfTests = glucoseTestsDocument.getDouble("count").intValue();
                        double maxGlucoseLevel = glucoseTestsDocument.getDouble("max_glucose");
                        double minGlucoseLevel = glucoseTestsDocument.getDouble("min_glucose");

                        highestGlucoseLevelTextView.append(maxGlucoseLevel + " mg/dl");
                        lowestGlucoseLevelTextView.append(minGlucoseLevel + " mg/dl");
                        totalGlucoseTestsTextView.append(totalNumberOfTests + "");

                        db.collection("patients")
                                .document(patientId)
                                .collection("tests")
                                .document("glucose_test")
                                .collection(getTodayDate())
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .get().addOnSuccessListener(glucoseDocuments ->
                        {

                            int todayTests = glucoseDocuments.size();

                            if (todayTests != 0)
                            {
                                DocumentSnapshot glucoseDocument = glucoseDocuments.getDocuments().get(0);
                                double testLevel = glucoseDocument.getDouble("glucose_percent");
                                Timestamp timestamp = glucoseDocuments.getDocuments().get(0).getTimestamp("timestamp");

                                String testTime = getTestTime(timestamp);

                                latestGlucoseTestLevelTextView.append(testLevel + " mg/dl");
                                latestGlucoseTestTimeTextView.append(testTime);
                                glucoseTodayTestsTextView.append(todayTests + "");
                            }
                            else
                                glucoseTodayTestsTextView.append("0");
                        });
                    }
                    setupHypertensionCardView();
                });
    }


    private void setupHypertensionCardView()
    {
        View glucoseHypertensionSeperator;
        TextView hypertensionTodayTestsTextView, latestHypertensionTestLevelTextView, latestHypertensionTestTimeTextView,
                highestHypertensionLevelTextView, lowestHypertensionLevelTextView, totalHypertensionTestsTextView;

        hypertensionCardView = findViewById(R.id.hypertensionCardView);
        glucoseHypertensionSeperator = findViewById(R.id.glucoseHypertensionSeperator);
        hypertensionTodayTestsTextView = findViewById(R.id.hypertensionTodayTestsTextView);
        latestHypertensionTestLevelTextView = findViewById(R.id.latestHypertensionTestLevelTextView);
        latestHypertensionTestTimeTextView = findViewById(R.id.latestHypertensionTestTimeTextView);
        highestHypertensionLevelTextView = findViewById(R.id.highestHypertensionLevelTextView);
        lowestHypertensionLevelTextView = findViewById(R.id.lowestHypertensionLevelTextView);
        totalHypertensionTestsTextView = findViewById(R.id.totalHypertensionTestsTextView);


        db.collection("patients")
                .document(patientId)
                .collection("tests")
                .document("hypertension_test")
                .get()
                .addOnSuccessListener(hypertensionTestsDocument ->
                {
                    if (hypertensionTestsDocument.exists())
                    {
                        hypertensionCardView.setVisibility(View.VISIBLE);
                        glucoseHypertensionSeperator.setVisibility(View.VISIBLE);

                        int totalNumberOfTests = hypertensionTestsDocument.getDouble("count").intValue();
                        double highestHypertensionLevel = hypertensionTestsDocument.getDouble("max_hypertension");
                        double lowestHypertensionLevel = hypertensionTestsDocument.getDouble("min_hypertension");


                        highestHypertensionLevelTextView.append(highestHypertensionLevel + " mm Hg");
                        lowestHypertensionLevelTextView.append(lowestHypertensionLevel + " mm Hg");
                        totalHypertensionTestsTextView.append(totalNumberOfTests + "");


                        db.collection("patients")
                                .document(patientId)
                                .collection("tests")
                                .document("hypertension_test")
                                .collection(getTodayDate())
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .get().addOnSuccessListener(hypertensionDocuments ->
                        {

                            int todayTests = hypertensionDocuments.size();

                            if (todayTests != 0)
                            {
                                DocumentSnapshot hypertensionDocument = hypertensionDocuments.getDocuments().get(0);
                                double testLevel = hypertensionDocument.getDouble("hypertension_percent");
                                Timestamp timestamp = hypertensionDocuments.getDocuments().get(0).getTimestamp("timestamp");

                                String testTime = getTestTime(timestamp);

                                latestHypertensionTestLevelTextView.append(testLevel + " mm Hg");
                                latestHypertensionTestTimeTextView.append(testTime);
                                hypertensionTodayTestsTextView.append(todayTests + "");
                            }
                            else
                                hypertensionTodayTestsTextView.append("0");
                        });
                    }
                    setupCholesterolCardViews();
                });
    }


    private void setupCholesterolCardViews()
    {
        View hypertensionTotalCholesterolSeperator, totalCholesterolHDLSeperator,
                hdlLDLSeperator, ldlTriglycerideSeperator;

        TextView hdlTodayTestsTextView, latestHDLTestLevelTextView, latestHDLTestTimeTextView, highestHDLLevelTextView,
                lowestHDLLevelTextView, totalHDLTestsTextView, ldlTodayTestsTextView, latestLDLTestLevelTextView,
                latestLDLTestTimeTextView, highestLDLLevelTextView, lowestLDLLevelTextView, totalLDLTestsTextView,
                triglycerideTodayTestsTextView, latestTriglycerideTestLevelTextView, latestTriglycerideTestTimeTextView,
                highestTriglycerideLevelTextView, lowestTriglycerideLevelTextView, totalTriglycerideTestsTextView,
                totalCholesterolTodayTestsTextView, latestTotalCholesterolTestLevelTextView, latestTotalCholesterolTestTimeTextView,
                highestTotalCholesterolLevelTextView, lowestTotalCholesterolLevelTextView, totalTotalCholesterolTestsTextView;

        totalCholesterolCardView = findViewById(R.id.totalCholesterolCardView);
        hdlCardView = findViewById(R.id.hdlCardView);
        ldlCardView = findViewById(R.id.ldlCardView);
        triglycerideCardView = findViewById(R.id.triglycerideCardView);
        hypertensionTotalCholesterolSeperator = findViewById(R.id.hypertensionTotalCholesterolSeperator);
        totalCholesterolHDLSeperator = findViewById(R.id.totalCholesterolHDLSeperator);
        hdlLDLSeperator = findViewById(R.id.hdlLDLSeperator);
        ldlTriglycerideSeperator = findViewById(R.id.ldlTriglycerideSeperator);


        hdlTodayTestsTextView = findViewById(R.id.hdlTodayTestsTextView);
        latestHDLTestLevelTextView = findViewById(R.id.latestHDLTestLevelTextView);
        latestHDLTestTimeTextView = findViewById(R.id.latestHDLTestTimeTextView);
        highestHDLLevelTextView = findViewById(R.id.highestHDLLevelTextView);
        lowestHDLLevelTextView = findViewById(R.id.lowestHDLLevelTextView);
        totalHDLTestsTextView = findViewById(R.id.totalHDLTestsTextView);

        ldlTodayTestsTextView = findViewById(R.id.ldlTodayTestsTextView);
        latestLDLTestLevelTextView = findViewById(R.id.latestLDLTestLevelTextView);
        latestLDLTestTimeTextView = findViewById(R.id.latestLDLTestTimeTextView);
        highestLDLLevelTextView = findViewById(R.id.highestLDLLevelTextView);
        lowestLDLLevelTextView = findViewById(R.id.lowestLDLLevelTextView);
        totalLDLTestsTextView = findViewById(R.id.totalLDLTestsTextView);

        triglycerideTodayTestsTextView = findViewById(R.id.triglycerideTodayTestsTextView);
        latestTriglycerideTestLevelTextView = findViewById(R.id.latestTriglycerideTestLevelTextView);
        latestTriglycerideTestTimeTextView = findViewById(R.id.latestTriglycerideTestTimeTextView);
        highestTriglycerideLevelTextView = findViewById(R.id.highestTriglycerideLevelTextView);
        lowestTriglycerideLevelTextView = findViewById(R.id.lowestTriglycerideLevelTextView);
        totalTriglycerideTestsTextView = findViewById(R.id.totalTriglycerideTestsTextView);

        totalCholesterolTodayTestsTextView = findViewById(R.id.totalCholesterolTodayTestsTextView);
        latestTotalCholesterolTestLevelTextView = findViewById(R.id.latestTotalCholesterolTestLevelTextView);
        latestTotalCholesterolTestTimeTextView = findViewById(R.id.latestTotalCholesterolTestTimeTextView);
        highestTotalCholesterolLevelTextView = findViewById(R.id.highestTotalCholesterolLevelTextView);
        lowestTotalCholesterolLevelTextView = findViewById(R.id.lowestTotalCholesterolLevelTextView);
        totalTotalCholesterolTestsTextView = findViewById(R.id.totalTotalCholesterolTestsTextView);


        db.collection("patients")
                .document(patientId)
                .collection("tests")
                .document("cholesterolAndFats_test")
                .get()
                .addOnSuccessListener(cholesterolAndFatsDocument ->
                {
                    if (cholesterolAndFatsDocument.exists())
                    {
                        totalCholesterolCardView.setVisibility(View.VISIBLE);
                        hdlCardView.setVisibility(View.VISIBLE);
                        ldlCardView.setVisibility(View.VISIBLE);
                        triglycerideCardView.setVisibility(View.VISIBLE);
                        hypertensionTotalCholesterolSeperator.setVisibility(View.VISIBLE);
                        totalCholesterolHDLSeperator.setVisibility(View.VISIBLE);
                        hdlLDLSeperator.setVisibility(View.VISIBLE);
                        ldlTriglycerideSeperator.setVisibility(View.VISIBLE);

                        int totalNumberOfTests = cholesterolAndFatsDocument.getDouble("count").intValue();
                        double highestTotalCholesterolLevel = cholesterolAndFatsDocument.getDouble("max_total");

                        double lowestTotalCholesterolLevelLevel = cholesterolAndFatsDocument.getDouble("min_total");

                        highestTotalCholesterolLevelTextView.append(highestTotalCholesterolLevel + " mg/dl");
                        lowestTotalCholesterolLevelTextView.append(lowestTotalCholesterolLevelLevel + " mg/dl");
                        totalTotalCholesterolTestsTextView.append(totalNumberOfTests + "");

                        double highestHDLLevel = cholesterolAndFatsDocument.getDouble("max_hdl");
                        double lowestHDLLevel = cholesterolAndFatsDocument.getDouble("min_hdl");

                        highestHDLLevelTextView.append(highestHDLLevel + " u/l");
                        lowestHDLLevelTextView.append(lowestHDLLevel + " u/l");
                        totalHDLTestsTextView.append(totalNumberOfTests + "");

                        double highestLDLLevel = cholesterolAndFatsDocument.getDouble("max_ldl");
                        double lowestLDLLevel = cholesterolAndFatsDocument.getDouble("min_ldl");

                        highestLDLLevelTextView.append(highestLDLLevel + " u/l");
                        lowestLDLLevelTextView.append(lowestLDLLevel + " u/l");
                        totalLDLTestsTextView.append(totalNumberOfTests + "");

                        double highestTriglycerideLevel = cholesterolAndFatsDocument.getDouble("max_triglyceride");
                        double lowestTriglycerideLevel = cholesterolAndFatsDocument.getDouble("min_triglyceride");

                        highestTriglycerideLevelTextView.append(highestTriglycerideLevel + " mg/dl");
                        lowestTriglycerideLevelTextView.append(lowestTriglycerideLevel + " mg/dl");
                        totalTriglycerideTestsTextView.append(totalNumberOfTests + "");


                        db.collection("patients")
                                .document(patientId)
                                .collection("tests")
                                .document("cholesterolAndFats_test")
                                .collection(getTodayDate())
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .get().addOnSuccessListener(cholesterolDocuments ->
                        {

                            int todayTests = cholesterolDocuments.size();

                            if (todayTests != 0)
                            {
                                DocumentSnapshot cholesterolDocument = cholesterolDocuments.getDocuments().get(0);

                                double totalCholesterolLevel = cholesterolDocument.getDouble("CholesterolTotal_percent");
                                double hdlLevel = cholesterolDocument.getDouble("HDLCholesterol_percent");
                                double ldlLevel = cholesterolDocument.getDouble("LDLCholesterol_percent");
                                double triglycerideLevel = cholesterolDocument.getDouble("Triglycerid_percent");

                                Timestamp timestamp = cholesterolDocument.getTimestamp("timestamp");

                                String testTime = getTestTime(timestamp);

                                latestTotalCholesterolTestLevelTextView.append(totalCholesterolLevel + " mg/dl");
                                latestHDLTestLevelTextView.append(hdlLevel + "u/l");
                                latestLDLTestLevelTextView.append(ldlLevel + "u/l");
                                latestTriglycerideTestLevelTextView.append(triglycerideLevel + " mg/dl");

                                latestTotalCholesterolTestTimeTextView.append(testTime);
                                latestHDLTestTimeTextView.append(testTime);
                                latestLDLTestTimeTextView.append(testTime);
                                latestTriglycerideTestTimeTextView.append(testTime);

                                totalCholesterolTodayTestsTextView.append(todayTests + "");
                                hdlTodayTestsTextView.append(todayTests + "");
                                ldlTodayTestsTextView.append(todayTests + "");
                                triglycerideTodayTestsTextView.append(todayTests + "");
                            }
                            else
                            {
                                totalCholesterolTodayTestsTextView.append("0");
                                hdlTodayTestsTextView.append("0");
                                ldlTodayTestsTextView.append("0");
                                triglycerideTodayTestsTextView.append("0");
                            }
                        });
                    }
                    if(checkNoTests())
                    {
                        noTestsImageView.setVisibility(View.VISIBLE);
                        noTestsTextView.setVisibility(View.VISIBLE);
                    }

                    progressDialog.dismissProgressDialog();
                });
    }


    private boolean checkNoTests()
    {
        return glucoseCardView.getVisibility() == View.GONE &&
                hypertensionCardView.getVisibility() == View.GONE &&
                totalCholesterolCardView.getVisibility() == View.GONE;
    }


    private String getTestTime(Timestamp timestamp)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy h:mm aa", Locale.US);

        return simpleDateFormat.format(timestamp.toDate());
    }


    private String getTodayDate()
    {
        return DateFormat.format("yyyy-M-d", new Date()).toString();
    }


    private void setupActionBar()
    {
        getSupportActionBar().setTitle("Patient Tests Statistics");
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