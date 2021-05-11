package com.example.healthza.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthza.DetailsMarkerView;
import com.example.healthza.ProgressDialog;
import com.example.healthza.R;
import com.example.healthza.StickHeaderItemDecoration;
import com.example.healthza.Toasty;
import com.example.healthza.adapters.DailyTestAdapter;
import com.example.healthza.models.DailyTest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PatientChartsActivity extends AppCompatActivity
{
    private LineChart chart;
    private CheckBox glucoseCheckBox, bloodPressureCheckBox, hdlCheckBox, ldlCheckBox,
            triglycerideCheckBox, totalCholesterolCheckBox;
    private FloatingActionButton pickDateFloatingActionButton, pickASingleDateFloatingActionButton, pickDateRangeFloatingActionButton, pickMultipleDatesFloatingActionButton;
    private RangeSeekBar hoursRangeSeekBar, daysRangeSeekBar;
    private Long selectedDate;
    private String pickedDate;
    private Timestamp firstTimestamp, secondTimestamp;
    private RecyclerView patientDailyTestsRecyclerView;
    private DailyTestAdapter dailyTestAdapter;
    private List<DailyTest> dailyTests;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String patientId;
    private boolean isFloatingButtonsVisible = false;

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
        pickASingleDateFloatingActionButton = findViewById(R.id.pickASingleDateFloatingActionButton);
        pickDateRangeFloatingActionButton = findViewById(R.id.pickDateRangeFloatingActionButton);
        pickMultipleDatesFloatingActionButton = findViewById(R.id.pickMultipleDatesFloatingActionButton);

        TextView singleDateTextView, dateRangeTextView, multipleDatesTextView;
        singleDateTextView = findViewById(R.id.singleDateTextView);
        dateRangeTextView = findViewById(R.id.dateRangeTextView);
        multipleDatesTextView = findViewById(R.id.multipleDatesTextView);

        hoursRangeSeekBar = findViewById(R.id.hoursRangeSeekBar);
        daysRangeSeekBar = findViewById(R.id.daysRangeSeekBar);


        hoursRangeSeekBar.setProgress(0, 24);
        hoursRangeSeekBar.getLeftSeekBar().setIndicatorText("12 AM");
        hoursRangeSeekBar.getRightSeekBar().setIndicatorText("11:59 PM");
        hoursRangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener()
        {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser)
            {
                view.getLeftSeekBar().setIndicatorText(getHourString((int) leftValue));
                view.getRightSeekBar().setIndicatorText(getHourString((int) rightValue));



            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft)
            {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft)
            {

                int firstHour = (int) view.getLeftSeekBar().getProgress();
                int lastHour = (int) view.getRightSeekBar().getProgress();


                firstTimestamp = getTimestamp(selectedDate + getHoursInMillies(firstHour)
                        + getHoursInMillies(hoursRangeSeekBar.getLeftSeekBar().getProgress()));
                secondTimestamp = getTimestamp(selectedDate + getHoursInMillies(lastHour)
                        + getHoursInMillies(hoursRangeSeekBar.getRightSeekBar().getProgress()));

                dailyTests.clear();
                setChart();
            }
        });


        patientDailyTestsRecyclerView = findViewById(R.id.patientDailyTestsRecyclerView);
        chart = findViewById(R.id.lineChart);

        pickASingleDateFloatingActionButton.setOnClickListener(v ->
        {
            setSingleDate();
        });

        pickDateRangeFloatingActionButton.setOnClickListener(v ->
        {
            setDateRange();
        });

        pickDateFloatingActionButton.setOnClickListener(v ->
        {
            if(isFloatingButtonsVisible)
            {
                pickASingleDateFloatingActionButton.hide();
                pickDateRangeFloatingActionButton.hide();
                pickMultipleDatesFloatingActionButton.hide();

                singleDateTextView.setVisibility(View.GONE);
                dateRangeTextView.setVisibility(View.GONE);
                multipleDatesTextView.setVisibility(View.GONE);

                isFloatingButtonsVisible = false;
            }
            else
            {
                pickASingleDateFloatingActionButton.show();
                pickDateRangeFloatingActionButton.show();
                pickMultipleDatesFloatingActionButton.show();

                singleDateTextView.setVisibility(View.VISIBLE);
                dateRangeTextView.setVisibility(View.VISIBLE);
                multipleDatesTextView.setVisibility(View.VISIBLE);

                isFloatingButtonsVisible = true;
            }
        });

        selectedDate = MaterialDatePicker.todayInUtcMilliseconds();

        firstTimestamp = getTimestamp(selectedDate - getHoursInMillies(3));
        secondTimestamp = getTimestamp(selectedDate + getHoursInMillies(21));

        pickedDate = getTodayDate();
        initChart();
        setChart();
    }

    private void initChart()
    {
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        Description description = chart.getDescription();
        description.setText(pickedDate);
        description.setTextSize(16f);
        description.setTextColor(getColor(R.color.colorPrimaryDark));

        description.setXOffset(4f);
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


        XAxis xAxis = chart.getXAxis();
        //xAxis.setTextColor(Color.parseColor("#333333"));
        xAxis.setAxisLineWidth(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMaximum(24f);
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setGranularity(1f);

        chart.getAxisRight().setEnabled(false);
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisLineWidth(1f);
        yAxis.setAxisMaximum(240f);
        yAxis.setAxisMinimum(40);
        yAxis.setTextSize(12f);
        yAxis.setGranularity(1f);
    }


    private void setChart()
    {
        clearTestsCheckBoxes();

        /* set picked date as description for the chart */
        chart.getDescription().setText(pickedDate);
        
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
                .whereGreaterThanOrEqualTo("timestamp", firstTimestamp)
                .whereLessThanOrEqualTo("timestamp", secondTimestamp)
                .orderBy("timestamp")
                .get().addOnSuccessListener(glucoseDocuments ->
        {
            //ProgressDialog progressDialog = new ProgressDialog(this);
           // progressDialog.showProgressDialog("Displaying Data...");

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

            testsRef.document("hypertension_test")
                    .collection(pickedDate)
                    .orderBy("timestamp")
                    .get().addOnSuccessListener(bloodPressureDocuments ->
            {
                for(DocumentSnapshot bloodPressureDocument : bloodPressureDocuments.getDocuments())
                {
                    double testLevel = bloodPressureDocument.getDouble("hypertension_percent");
                    Timestamp timestamp = bloodPressureDocument.getTimestamp("timestamp");
                    DailyTest dailyTest = new DailyTest("Blood Pressure", testLevel, timestamp);
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
                        {
                            glucoseDataSet.setHighlightEnabled(true);
                            glucoseDataSet.setVisible(true);
                        }
                        else
                        {
                            glucoseDataSet.setHighlightEnabled(false);
                            glucoseDataSet.setVisible(false);
                        }

                        chart.invalidate();
                    });

                    bloodPressureCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    {
                        if(isChecked)
                        {
                            bloodPressureDataSet.setHighlightEnabled(true);
                            bloodPressureDataSet.setVisible(true);
                        }
                        else
                        {
                            bloodPressureDataSet.setHighlightEnabled(false);
                            bloodPressureDataSet.setVisible(false);
                        }

                        chart.invalidate();
                    });

                    totalCholesterolCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    {
                        if(isChecked)
                        {
                            totalCholesterolDataSet.setHighlightEnabled(true);
                            totalCholesterolDataSet.setVisible(true);
                        }
                        else
                        {
                            totalCholesterolDataSet.setHighlightEnabled(false);
                            totalCholesterolDataSet.setVisible(false);
                        }

                        chart.invalidate();
                    });

                    hdlCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    {
                        if(isChecked)
                        {
                            hdlDataSet.setHighlightEnabled(true);
                            hdlDataSet.setVisible(true);
                        }

                        else
                        {
                            hdlDataSet.setHighlightEnabled(false);
                            hdlDataSet.setVisible(false);
                        }

                        chart.invalidate();
                    });

                    ldlCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    {
                        if(isChecked)
                        {
                            ldlDataSet.setHighlightEnabled(true);
                            ldlDataSet.setVisible(true);
                        }
                        else
                        {
                            ldlDataSet.setHighlightEnabled(false);
                            ldlDataSet.setVisible(false);
                        }

                        chart.invalidate();
                    });

                    triglycerideCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    {
                        if(isChecked)
                        {
                            triglycerideDataSet.setHighlightEnabled(true);
                            triglycerideDataSet.setVisible(true);
                        }
                        else
                        {
                            triglycerideDataSet.setHighlightEnabled(false);
                            triglycerideDataSet.setVisible(false);
                        }

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
                    glucoseDataSet.setCircleColor(getColor(R.color.colorPrimary));
                    glucoseDataSet.setCircleHoleRadius(2f);
                    glucoseDataSet.setCircleRadius(4f);
                    glucoseDataSet.setLineWidth(3f);
                    glucoseDataSet.setValueTextSize(12f);

                    bloodPressureDataSet.setColor(getColor(R.color.blood_pressure_color));
                    bloodPressureDataSet.setCircleColor(getColor(R.color.colorPrimary));
                    bloodPressureDataSet.setCircleHoleRadius(2f);
                    bloodPressureDataSet.setCircleRadius(4f);
                    bloodPressureDataSet.setLineWidth(3f);
                    bloodPressureDataSet.setValueTextSize(12f);

                    totalCholesterolDataSet.setColor(getColor(R.color.total_cholesterol_color));
                    totalCholesterolDataSet.setCircleColor(getColor(R.color.colorPrimary));
                    totalCholesterolDataSet.setCircleHoleRadius(2f);
                    totalCholesterolDataSet.setCircleRadius(4f);
                    totalCholesterolDataSet.setLineWidth(3f);
                    totalCholesterolDataSet.setValueTextSize(12f);

                    hdlDataSet.setColor(getColor(R.color.hdl_color));
                    hdlDataSet.setCircleColor(getColor(R.color.colorPrimary));
                    hdlDataSet.setCircleHoleRadius(2f);
                    hdlDataSet.setCircleRadius(4f);
                    hdlDataSet.setLineWidth(3f);
                    hdlDataSet.setValueTextSize(12f);

                    ldlDataSet.setColor(getColor(R.color.ldl_color));
                    ldlDataSet.setCircleColor(getColor(R.color.colorPrimary));
                    ldlDataSet.setCircleHoleRadius(2f);
                    ldlDataSet.setCircleRadius(4f);
                    ldlDataSet.setLineWidth(3f);
                    ldlDataSet.setValueTextSize(12f);

                    triglycerideDataSet.setColor(getColor(R.color.triglyceride_color));
                    triglycerideDataSet.setCircleColor(getColor(R.color.colorPrimary));
                    triglycerideDataSet.setCircleHoleRadius(2f);
                    triglycerideDataSet.setCircleRadius(4f);
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
                    lineData.setDrawValues(false);


                    DetailsMarkerView detailsMarkerView = new DetailsMarkerView(PatientChartsActivity.this, R.layout.daily_test_markerview);
                    detailsMarkerView.setChartView(chart);


                    chart.setMarker(detailsMarkerView);
                    chart.setData(lineData);
                    chart.invalidate();

                    //progressDialog.dismissProgressDialog();
                    pickDateFloatingActionButton.setEnabled(true);
                });
            });

        });
    }


    private void setSingleDate()
    {

        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder
                .datePicker()
                .setSelection(selectedDate)
                .build();

        materialDatePicker.addOnPositiveButtonClickListener(selection ->
        {

            selectedDate = (Long) selection;
            String pickedDate = DateFormat.format("yyyy-M-d", selectedDate).toString();


            /* redraw only if the picked date is different than previous picked date */
            if(!pickedDate.equals(PatientChartsActivity.this.pickedDate))
            {
                PatientChartsActivity.this.pickedDate = pickedDate;
                pickDateFloatingActionButton.setEnabled(false);
                dailyTests.clear();
                dailyTests.add(null); // to display stick header instead of daily test item
                setChart();
            }
        });

        materialDatePicker.show(getSupportFragmentManager(), "PatientChartsActivity");
    }


    private void setDateRange()
    {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker();

        Pair<Long, Long> selected = new Pair<>(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds());
        builder.setSelection(selected);

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.setValidator(DateValidatorPointBackward.now()).build());

        MaterialDatePicker<Pair<Long,Long>> materialDatePicker = builder
                .setTitleText("Select A Date Range")
                .build();

        materialDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            Pair<Long, Long> dateRange = ((Pair<Long, Long>) selection);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");

            selectedDate = dateRange.first - getHoursInMillies(3);

            String date;
            String firstDate = simpleDateFormat.format(new Date(dateRange.first))  ;
            int firstDateNumber = Integer.parseInt(firstDate.substring(firstDate.lastIndexOf('-') + 1));

            String lastDate = simpleDateFormat.format(new Date(dateRange.second));
            int lastDateNumber = Integer.parseInt(lastDate.substring(lastDate.lastIndexOf('-') + 1));

            List<String> dates = new ArrayList<>();
            date = firstDate.substring(0, firstDate.lastIndexOf('-') + 1);
            for(int i = firstDateNumber; i <= lastDateNumber; i++)
            {
                dates.add(date + i);
            }

            daysRangeSeekBar.setRange(firstDateNumber - 1, lastDateNumber);
            daysRangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
                @Override
                public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser)
                {
                    int dayNumber = (int) leftValue;
                    if(dayNumber < dates.size())
                    {
                        view.getLeftSeekBar().setIndicatorText(dates.get(dayNumber));
                        pickedDate = dates.get(dayNumber);
                    }
                }

                @Override
                public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft)
                {

                }

                @Override
                public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft)
                {
                    int firstDay = (int) view.getLeftSeekBar().getProgress();

                    firstTimestamp = getTimestamp(selectedDate + getHoursInMillies(24) * firstDay
                            + getHoursInMillies(hoursRangeSeekBar.getLeftSeekBar().getProgress()));
                    secondTimestamp = getTimestamp(selectedDate + getHoursInMillies(24) * firstDay
                            + getHoursInMillies(hoursRangeSeekBar.getRightSeekBar().getProgress()));

                    dailyTests.clear();
                    setChart();
                }
            });
        });

        materialDatePicker.show(getSupportFragmentManager(), "PatientChartsActivity");
    }

    
    private void clearTestsCheckBoxes()
    {
        glucoseCheckBox.setChecked(false);
        bloodPressureCheckBox.setChecked(false);
        totalCholesterolCheckBox.setChecked(false);
        hdlCheckBox.setChecked(false);
        ldlCheckBox.setChecked(false);
        triglycerideCheckBox.setChecked(false);
        glucoseCheckBox.setEnabled(false);
        bloodPressureCheckBox.setEnabled(false);
        totalCholesterolCheckBox.setEnabled(false);
        hdlCheckBox.setEnabled(false);
        ldlCheckBox.setEnabled(false);
        triglycerideCheckBox.setEnabled(false);
    }


    private String getHourString(int hour)
    {
        if(hour == 0)
            return "12 AM";
        else if(hour == 12)
            return "12 PM";
        else if(hour == 24)
            return "11:59 PM";
        else if(hour < 12)
            return hour + " AM";
        else
            return hour - 12 + " PM";
    }

    private Timestamp getTimestamp(Long millieSeconds)
    {
        return new Timestamp(new Date(millieSeconds));
    }

    private Long getHoursInMillies(float hour)
    {
        return TimeUnit.HOURS.toMillis((long) hour);
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
        {
            if(!hourString.equals("12"))
                time = Float.parseFloat(hourString + "." + minuteString);
            else
                time = Float.parseFloat(hourString + "." + minuteString)  - 12;
        }
        else
            {
                if(!hourString.equals("12"))
                    time = Float.parseFloat(hourString + "." + minuteString) + 12;
                else
                    time = Float.parseFloat(hourString + "." + minuteString);
        }

        return time;
    }
    
    
    private String getTodayDate()
    {
        return DateFormat.format("yyyy-M-d", new Date()).toString();
    }
}