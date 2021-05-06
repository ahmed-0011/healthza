package com.example.healthza.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthza.LoadingDialog;
import com.example.healthza.R;
import com.example.healthza.StickHeaderItemDecoration;
import com.example.healthza.adapters.DailyTestAdapter;
import com.example.healthza.adapters.DoctorAdapter;
import com.example.healthza.models.DailyTest;
import com.example.healthza.models.Patient;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PatientChartsActivity extends AppCompatActivity
{
    private LineChart chart;
    private CheckBox glucoseCheckBox, bloodPressureCheckBox, hdlCheckBox, ldlCheckBox,
            triglycerideCheckBox, totalCholesterolCheckBox;
    private FloatingActionButton pickDateFloatingActionButton;
    private String pickedDate;
    private RecyclerView patientDailyTestsRecyclerView;
    private DailyTestAdapter dailyTestAdapter;
    private List<DailyTest> dailyTests;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_charts);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        patientId = firebaseAuth.getCurrentUser().getUid();

        dailyTests = new ArrayList<>();
        dailyTests.add(null); // to display stick header instead of daily test item

        glucoseCheckBox = findViewById(R.id.glucoseCheckBox);
        bloodPressureCheckBox = findViewById(R.id.bloodPressureCheckBox);
        hdlCheckBox = findViewById(R.id.hdlCheckBox);
        ldlCheckBox = findViewById(R.id.ldlCheckBox);
        triglycerideCheckBox = findViewById(R.id.triglycerideCheckBox);
        totalCholesterolCheckBox = findViewById(R.id.totalCholesterolCheckBox);

        pickDateFloatingActionButton = findViewById(R.id.pickDateFloatingActionButton);

        patientDailyTestsRecyclerView = findViewById(R.id.patientDailyTestsRecyclerView);

        chart = (LineChart) findViewById(R.id.lineChart);

        Calendar calendar = Calendar.getInstance();

        pickDateFloatingActionButton.setOnClickListener(v ->
        {
            int date = calendar.get(Calendar.DATE);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                {

                    calendar.set(Calendar.DATE, dayOfMonth);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.YEAR, year);
                    
                    String pickedDate = DateFormat.format("yyyy-M-d", calendar).toString();


                    /* redraw only if the picked date is different than previous picked date */
                    if(!pickedDate.equals(PatientChartsActivity.this.pickedDate))
                    {
                        PatientChartsActivity.this.pickedDate = pickedDate;
                        pickDateFloatingActionButton.setEnabled(false);
                        dailyTests.clear();
                        dailyTests.add(null); // to display stick header instead of daily test item
                        setChart(pickedDate);
                    }
                }
            }, year, month, date);
            
            datePickerDialog.show();
        });

        pickedDate = getTodayDate();
        initChart();
        setChart(pickedDate);
    }

    private void initChart()
    {
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setDescription(null);
        Legend legend = chart.getLegend();

        legend.setTextSize(12);
        legend.setWordWrapEnabled(true);

        List<String> xAxisLabels = new ArrayList<>();

        xAxisLabels.add("12 AM");
        for (int i = 0, hour = 1; i < 24 - 2; i++, hour++)
        {
            if(i < 12)
            {
                xAxisLabels.add(hour +" AM");
                if(i == 11)
                    hour = 1;
            }
            else
                xAxisLabels.add(hour + " PM");
        }

        xAxisLabels.add(12, "12 PM");
        xAxisLabels.add("");

        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMaximum(24f);
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(240f);
        yAxis.setAxisMinimum(40);
        yAxis.setTextSize(12f);
        yAxis.setGranularity(3f);
    }


    private void setChart(String pickedDate)
    {
        disableTestsCheckBoxes();

        List<Entry> zeroEntries = new ArrayList<>();
        List<Entry> glucoseEntries = new ArrayList<>();
        List<Entry> bloodPressureEntries = new ArrayList<>();
        List<Entry> totalCholesterolEntries = new ArrayList<>();
        List<Entry> hdlEntries = new ArrayList<>();
        List<Entry> ldlEntries = new ArrayList<>();
        List<Entry> triglycerideEntries  = new ArrayList<>();

        CollectionReference testsRef = db.collection("patients")
                .document(patientId)
                .collection("tests");

        testsRef.document("glucose_test")
                .collection(pickedDate)
                .orderBy("timestamp")
                .get().addOnSuccessListener(glucoseDocuments ->
        {
            LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.showLoadingDialog();

            for(DocumentSnapshot glucoseDocument : glucoseDocuments.getDocuments())
            {
                double testLevel = glucoseDocument.getDouble("glucose_percent");
                Timestamp timestamp = glucoseDocument.getTimestamp("timestamp");

                DailyTest dailyTest = new DailyTest("Glucose", testLevel, timestamp);
                dailyTests.add(dailyTest);
                float time = getTestTime(timestamp);
                glucoseEntries.add(new Entry(time, (float) testLevel));
            }

            if(glucoseEntries.size() != 0)
            {
                glucoseCheckBox.setChecked(true);
                glucoseCheckBox.setEnabled(true);
            }

            testsRef.document("bloodPressure_test")
                    .collection(pickedDate)
                    .orderBy("timestamp")
                    .get().addOnSuccessListener(bloodPressureDocuments ->
            {
                for(DocumentSnapshot bloodPressureDocument : bloodPressureDocuments.getDocuments())
                {
                    double testLevel = bloodPressureDocument.getDouble("bloodPressure_percent");
                    Timestamp timestamp = bloodPressureDocument.getTimestamp("timestamp");
                    DailyTest dailyTest = new DailyTest("Hypertension", testLevel, timestamp);
                    dailyTests.add(dailyTest);
                    float time = getTestTime(timestamp);
                    bloodPressureEntries.add(new Entry(time, (float) testLevel));
                }

                if (bloodPressureEntries.size() != 0)
                {
                    bloodPressureCheckBox.setChecked(true);
                    bloodPressureCheckBox.setEnabled(true);
                }

                testsRef.document("cholesterolAndFats_test")
                        .collection(pickedDate)
                        .orderBy("timestamp")
                        .get().addOnSuccessListener(cholesterolDocuments ->
                {
                    for(DocumentSnapshot cholesterolDocument : cholesterolDocuments.getDocuments())
                    {

                        double totalCholesterolLevel = cholesterolDocument.getDouble("CholesterolTotal_percent");
                        double hdlLevel = cholesterolDocument.getDouble("HDLCholesterol_percent");
                        double ldlLevel = cholesterolDocument.getDouble("LDLCholesterol_percent");
                        double triglycerideLevel = cholesterolDocument.getDouble("Triglycerid_percent");

                        Timestamp timestamp = cholesterolDocument.getTimestamp("timestamp");

                        DailyTest dailyTest1 = new DailyTest("Total Cholesterol", totalCholesterolLevel, timestamp);
                        DailyTest dailyTest2 = new DailyTest("HDL", hdlLevel, timestamp);
                        DailyTest dailyTest3 = new DailyTest("LDL", ldlLevel , timestamp);
                        DailyTest dailyTest4 = new DailyTest("Triglyceride", triglycerideLevel , timestamp);
                        dailyTests.add(dailyTest1);
                        dailyTests.add(dailyTest2);
                        dailyTests.add(dailyTest3);
                        dailyTests.add(dailyTest4);
                        float time = getTestTime(timestamp);
                        totalCholesterolEntries.add(new Entry(time, (float) totalCholesterolLevel));
                        hdlEntries.add(new Entry(time, (float) hdlLevel));
                        ldlEntries.add(new Entry(time, (float) ldlLevel));
                        triglycerideEntries.add(new Entry(time, (float) triglycerideLevel));
                    }

                    if(totalCholesterolEntries.size() != 0)
                    {
                        totalCholesterolCheckBox.setChecked(true);
                        hdlCheckBox.setChecked(true);
                        ldlCheckBox.setChecked(true);
                        triglycerideCheckBox.setChecked(true);
                        totalCholesterolCheckBox.setEnabled(true);
                        hdlCheckBox.setEnabled(true);
                        ldlCheckBox.setEnabled(true);
                        triglycerideCheckBox.setEnabled(true);
                    }

                    Collections.sort(dailyTests);
                    dailyTestAdapter = new DailyTestAdapter( dailyTests, this);
                    patientDailyTestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    patientDailyTestsRecyclerView.addItemDecoration(new StickHeaderItemDecoration(dailyTestAdapter));
                    patientDailyTestsRecyclerView.setAdapter(dailyTestAdapter);


                    if(glucoseEntries.size() != 0)
                        zeroEntries.add(new Entry(0, glucoseEntries.get(0).getX()));
                    else if(bloodPressureEntries.size() != 0)
                        zeroEntries.add(new Entry(0, bloodPressureEntries.get(0).getX()));
                    else if(totalCholesterolEntries.size() != 0)
                        zeroEntries.add(new Entry(0, totalCholesterolEntries.get(0).getX()));


                    LineDataSet zeroDataSet = new LineDataSet(zeroEntries, "");
                    LineDataSet glucoseDataSet = new LineDataSet(glucoseEntries, "Glucose");
                    LineDataSet bloodPressureDataSet = new LineDataSet(bloodPressureEntries, "Blood Pressure");
                    LineDataSet totalCholesterolDataSet = new LineDataSet(totalCholesterolEntries, "Total Cholesterol");
                    LineDataSet hdlDataSet = new LineDataSet(hdlEntries, "HDL");
                    LineDataSet ldlDataSet = new LineDataSet(ldlEntries, "LDL");
                    LineDataSet triglycerideDataSet = new LineDataSet(triglycerideEntries, "Triglyceride");


                    glucoseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    {
                        if(isChecked)
                            glucoseDataSet.setVisible(true);
                        else
                            glucoseDataSet.setVisible(false);

                        chart.invalidate();
                    });

                    bloodPressureCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    {
                        if(isChecked)
                            bloodPressureDataSet.setVisible(true);
                        else
                            bloodPressureDataSet.setVisible(false);

                        chart.invalidate();
                    });

                    totalCholesterolCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    {
                        if(isChecked)
                            totalCholesterolDataSet.setVisible(true);
                        else
                            totalCholesterolDataSet.setVisible(false);

                        chart.invalidate();
                    });

                    hdlCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    {
                        if(isChecked)
                            hdlDataSet.setVisible(true);
                        else
                            hdlDataSet.setVisible(false);

                        chart.invalidate();
                    });

                    ldlCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    {
                        if(isChecked)
                            ldlDataSet.setVisible(true);
                        else
                            ldlDataSet.setVisible(false);

                        chart.invalidate();
                    });

                    triglycerideCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    {
                        if(isChecked)
                            triglycerideDataSet.setVisible(true);
                        else
                            triglycerideDataSet.setVisible(false);

                        chart.invalidate();
                    });

                    glucoseDataSet.setFillAlpha(110);
                    bloodPressureDataSet.setFillAlpha(110);
                    totalCholesterolDataSet.setFillAlpha(110);
                    hdlDataSet.setFillAlpha(110);
                    ldlDataSet.setFillAlpha(110);
                    triglycerideDataSet.setFillAlpha(110);

                    zeroDataSet.setForm(Legend.LegendForm.NONE);
                    zeroDataSet.setColor(getColor(R.color.transparent_color));
                    zeroDataSet.setLineWidth(0f);
                    zeroDataSet.setValueTextSize(0f);

                    glucoseDataSet.setColor(getColor(R.color.glucose_color));
                    glucoseDataSet.setLineWidth(3f);
                    glucoseDataSet.setValueTextSize(12f);

                    bloodPressureDataSet.setColor(getColor(R.color.blood_pressure_color));
                    bloodPressureDataSet.setLineWidth(3f);
                    bloodPressureDataSet.setValueTextSize(12f);

                    totalCholesterolDataSet.setColor(getColor(R.color.total_cholesterol_color));
                    totalCholesterolDataSet.setLineWidth(3f);
                    totalCholesterolDataSet.setValueTextSize(12f);

                    hdlDataSet.setColor(getColor(R.color.hdl_color));
                    hdlDataSet.setLineWidth(3f);
                    hdlDataSet.setValueTextSize(12f);

                    ldlDataSet.setColor(getColor(R.color.ldl_color));
                    ldlDataSet.setLineWidth(3f);
                    ldlDataSet.setValueTextSize(12f);

                    triglycerideDataSet.setColor(getColor(R.color.triglyceride_color));
                    triglycerideDataSet.setLineWidth(3f);
                    triglycerideDataSet.setValueTextSize(12f);

                    List<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(zeroDataSet);
                    dataSets.add(glucoseDataSet);
                    dataSets.add(bloodPressureDataSet);
                    dataSets.add(totalCholesterolDataSet);
                    dataSets.add(hdlDataSet);
                    dataSets.add(ldlDataSet);
                    dataSets.add(triglycerideDataSet);

                    LineData lineData = new LineData(dataSets);


                    chart.setData(lineData);
                    chart.invalidate();

                    loadingDialog.dismissLoadingDialog();
                    pickDateFloatingActionButton.setEnabled(true);
                });
            });

        });
    }


    private void disableTestsCheckBoxes()
    {
        glucoseCheckBox.setEnabled(false);
        bloodPressureCheckBox.setEnabled(false);
        totalCholesterolCheckBox.setEnabled(false);
        hdlCheckBox.setEnabled(false);
        ldlCheckBox.setEnabled(false);
        triglycerideCheckBox.setEnabled(false);
    }

    
    private float getTestTime(Timestamp timestamp)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.US);

        String dayTime = simpleDateFormat.format(timestamp.toDate());

        String period = dayTime.substring(dayTime.indexOf(" ") + 1);
        String hourString = dayTime.substring(0, dayTime.indexOf(":"));
        String minuteString = dayTime.substring(dayTime.indexOf(":") + 1, dayTime.indexOf(" "));

        float time;
        if(period.equals("AM"))
            time = Float.parseFloat(hourString + "." + minuteString);
        else
            time = Float.parseFloat(hourString + "." + minuteString) + 12;

        return time;
    }
    
    
    private String getTodayDate()
    {
        Calendar calendar = Calendar.getInstance();

        return DateFormat.format("yyyy-M-d", calendar).toString();
    }
}