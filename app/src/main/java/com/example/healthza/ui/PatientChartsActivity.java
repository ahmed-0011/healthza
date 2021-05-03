package com.example.healthza.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.example.healthza.R;
import com.example.healthza.models.Patient;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatientChartsActivity extends AppCompatActivity
{
    private LineChart chart;
    private RecyclerView patientChartsRecyclerView;
    private ArrayList<Entry> glucoseTests;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_charts);


        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String patientId = firebaseAuth.getCurrentUser().getUid();

        LineChart chart = (LineChart) findViewById(R.id.lineChart);

        glucoseTests = new ArrayList<>();

        chart.getAxisRight().setEnabled(false);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMaximum(24f);
        xAxis.setTextSize(12f);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(130f);
        yAxis.setAxisMinimum(70);
        yAxis.setTextSize(12f);
        yAxis.setGranularity(3f);


        List<Entry> glucoseEntries = new ArrayList<>();
        List<Entry> hypertensionEntries = new ArrayList<>();
        List<Entry> cholestrolEntries = new ArrayList<>();

        CollectionReference testsRef = db.collection("patients")
                .document(patientId)
                .collection("tests");

        testsRef.document("glucose_test")
                .collection(getTodayDate())
                .get().addOnSuccessListener(glucoseDocuments ->
        {
            int i = 0;
            for(DocumentSnapshot glucoseDocument : glucoseDocuments.getDocuments())
            {
                glucoseTests.add(new Entry(i++, (float) (1f * glucoseDocument.getDouble("glucose_percent"))));
            }

            glucoseEntries.addAll(glucoseTests);

            /*
            testsRef.document("hypertension_test")
                    .collection(getTodayDate())
                    .get().addOnSuccessListener(queryDocumentSnapshots ->
            {
                for(DocumentSnapshot glucoseDocument : glucoseDocuments.getDocuments())
                {
                    glucoseTests.add(new Entry(i++, (float) (1f * glucoseDocument.getDouble("glucose_percent"))));
                }
            });
            */

            /*
            testsRef.document("hypertension_test")
                    .collection(getTodayDate())
                    .get().addOnSuccessListener(queryDocumentSnapshots ->
            {
                for(DocumentSnapshot glucoseDocument : glucoseDocuments.getDocuments())
                {
                    glucoseTests.add(new Entry(i++, (float) (1f * glucoseDocument.getDouble("glucose_percent"))));
                }
            });
            */

            /*
            testsRef.document("hypertension_test")
                    .collection(getTodayDate())
                    .get().addOnSuccessListener(queryDocumentSnapshots ->
            {
                for(DocumentSnapshot glucoseDocument : glucoseDocuments.getDocuments())
                {
                    glucoseTests.add(new Entry(i++, (float) (1f * glucoseDocument.getDouble("glucose_percent"))));
                }
            });
            */

            /*
            testsRef.document("hypertension_test")
                    .collection(getTodayDate())
                    .get().addOnSuccessListener(queryDocumentSnapshots ->
            {
                for(DocumentSnapshot glucoseDocument : glucoseDocuments.getDocuments())
                {
                    glucoseTests.add(new Entry(i++, (float) (1f * glucoseDocument.getDouble("glucose_percent"))));
                }
            });
            */

            /*
            testsRef.document("hypertension_test")
                    .collection(getTodayDate())
                    .get().addOnSuccessListener(queryDocumentSnapshots ->
            {
                for(DocumentSnapshot glucoseDocument : glucoseDocuments.getDocuments())
                {
                    glucoseTests.add(new Entry(i++, (float) (1f * glucoseDocument.getDouble("glucose_percent"))));
                }
            });
            */

            /*
            testsRef.document("hypertension_test")
                    .collection(getTodayDate())
                    .get().addOnSuccessListener(queryDocumentSnapshots ->
            {
                for(DocumentSnapshot glucoseDocument : glucoseDocuments.getDocuments())
                {
                    glucoseTests.add(new Entry(i++, (float) (1f * glucoseDocument.getDouble("glucose_percent"))));
                }
            });
            */

            LineDataSet dataSet = new LineDataSet(glucoseEntries, "Glucose");

            dataSet.setFillAlpha(110);

            dataSet.setColor(getColor(R.color.glucose_color));
            dataSet.setLineWidth(3f);
            dataSet.setValueTextSize(12f);

            ArrayList<IDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);

            LineData lineData = new LineData(dataSet);


            chart.setData(lineData);
            chart.invalidate();

        });
    }

    public String getTodayDate()
    {
        Calendar calendar = Calendar.getInstance();

        return DateFormat.format("yyyy-M-d", calendar).toString();
    }
}