package com.project.cdh.ui;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.CalendarWeekDay;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.project.cdh.DetailsMarkerView;
import com.project.cdh.ProgressDialog;
import com.project.cdh.R;
import com.project.cdh.StickHeaderItemDecoration;
import com.project.cdh.adapters.DailyTestAdapter;
import com.project.cdh.models.DailyTest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DoctorPatientChartsActivity extends AppCompatActivity
{
    private ProgressBar chartProgressBar;
    private LineChart chart;
    private CheckBox glucoseCheckBox, bloodPressureCheckBox, hdlCheckBox, ldlCheckBox,
            triglycerideCheckBox, totalCholesterolCheckBox;
    private FloatingActionButton pickDateFloatingActionButton, pickASingleDateFloatingActionButton, pickDateRangeFloatingActionButton, pickMultiDatesFloatingActionButton;
    private RangeSeekBar hoursRangeSeekBar, daysRangeSeekBar;
    private Long selectedDate;
    private String pickedDate;
    private Timestamp firstTimestamp, lastTimestamp;
    private RecyclerView patientDailyTestsRecyclerView;
    private DailyTestAdapter dailyTestAdapter;
    private List<DailyTest> dailyTests;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String patientId;
    private boolean isFloatingButtonsVisible = false;
    private int selectState;
    private final int PICKER = 0;
    private final int HOURS_SLIDER = 1;
    private final int DAYS_SLIDER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patient_charts);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        patientId= getIntent().getStringExtra("patientId");

        glucoseCheckBox = findViewById(R.id.glucoseCheckBox);
        bloodPressureCheckBox = findViewById(R.id.bloodPressureCheckBox);
        hdlCheckBox = findViewById(R.id.hdlCheckBox);
        ldlCheckBox = findViewById(R.id.ldlCheckBox);
        triglycerideCheckBox = findViewById(R.id.triglycerideCheckBox);
        totalCholesterolCheckBox = findViewById(R.id.totalCholesterolCheckBox);

        pickDateFloatingActionButton = findViewById(R.id.pickDateFloatingActionButton);
        pickASingleDateFloatingActionButton = findViewById(R.id.pickASingleDateFloatingActionButton);
        pickDateRangeFloatingActionButton = findViewById(R.id.pickDateRangeFloatingActionButton);
        pickMultiDatesFloatingActionButton = findViewById(R.id.pickMultiDatesFloatingActionButton);

        chart = findViewById(R.id.lineChart);
        chartProgressBar = findViewById(R.id.chartProgressBar);

        hoursRangeSeekBar = findViewById(R.id.hoursRangeSeekBar);
        daysRangeSeekBar = findViewById(R.id.daysRangeSeekBar);

        dailyTests = new ArrayList<>();

        patientDailyTestsRecyclerView = findViewById(R.id.patientDailyTestsRecyclerView);

        dailyTestAdapter = new DailyTestAdapter(this, dailyTests);
        patientDailyTestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        patientDailyTestsRecyclerView.addItemDecoration(new StickHeaderItemDecoration(dailyTestAdapter));
        patientDailyTestsRecyclerView.setAdapter(dailyTestAdapter);

        pickASingleDateFloatingActionButton.setOnClickListener(v -> setSingleDate());

        pickDateRangeFloatingActionButton.setOnClickListener(v -> setDateRange());

        pickMultiDatesFloatingActionButton.setOnClickListener(v -> setMultiDates());

        pickDateFloatingActionButton.setOnClickListener(v ->
        {
            if (isFloatingButtonsVisible)
                hideFloatingButtons();
            else
                showFloatingButtons();
        });

        selectedDate = MaterialDatePicker.todayInUtcMilliseconds() - getHoursInMillis(3);

        firstTimestamp = getTimestamp(selectedDate);
        lastTimestamp = getTimestamp(selectedDate + getHoursInMillis(24));


        hoursRangeSeekBar.setProgress(0, 24);
        hoursRangeSeekBar.getLeftSeekBar().setIndicatorText("12 AM");
        hoursRangeSeekBar.getRightSeekBar().setIndicatorText("11:59 PM");

        setHoursRangeSeekBar();
        selectState = PICKER;
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
            if (i < 12) {
                xAxisLabels.add(hour + " AM");
                if (i == 11)
                    hour = 1;
            } else
                xAxisLabels.add(hour + " PM");
        }

        xAxisLabels.add(12, "12 PM");
        xAxisLabels.add("");

        XAxis xAxis = chart.getXAxis();
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
        yAxis.setGranularityEnabled(true);
    }


    private void setChart()
    {
        /* set picked date as description for the chart */
        chart.getDescription().setText(pickedDate);

        List<DailyTest> dailyTestsAll = new ArrayList<>();
        dailyTestsAll.add(null);    // to display stick header instead of daily test item


        List<Entry> zeroEntries = new ArrayList<>();
        List<Entry> glucoseEntries = new ArrayList<>();
        List<Entry> bloodPressureEntries = new ArrayList<>();
        List<Entry> totalCholesterolEntries = new ArrayList<>();
        List<Entry> hdlEntries = new ArrayList<>();
        List<Entry> ldlEntries = new ArrayList<>();
        List<Entry> triglycerideEntries = new ArrayList<>();

        CollectionReference testsRef = db.collection("patients")
                .document(patientId)
                .collection("tests");

        testsRef.document("glucose_test")
                .collection(pickedDate)
                .whereGreaterThanOrEqualTo("timestamp", firstTimestamp)
                .whereLessThanOrEqualTo("timestamp", lastTimestamp)
                .orderBy("timestamp")
                .get().addOnSuccessListener(glucoseDocuments ->
        {

            ProgressDialog progressDialog = new ProgressDialog(this);

            if (selectState == PICKER)
                progressDialog.showProgressDialog("Displaying Data...");
            else if(selectState == DAYS_SLIDER)
                chartProgressBar.setVisibility(View.VISIBLE);

            for (DocumentSnapshot glucoseDocument : glucoseDocuments.getDocuments())
            {
                double testLevel = glucoseDocument.getDouble("glucose_percent");
                Timestamp timestamp = glucoseDocument.getTimestamp("timestamp");

                DailyTest dailyTest = new DailyTest("Glucose", testLevel, timestamp);
                dailyTestsAll.add(dailyTest);
                float time = getTestTime(timestamp);
                glucoseEntries.add(new Entry(time, (float) testLevel));
            }

            testsRef.document("hypertension_test")
                    .collection(pickedDate)
                    .whereGreaterThanOrEqualTo("timestamp", firstTimestamp)
                    .whereLessThanOrEqualTo("timestamp", lastTimestamp)
                    .orderBy("timestamp")
                    .get().addOnSuccessListener(bloodPressureDocuments ->
            {
                for (DocumentSnapshot bloodPressureDocument : bloodPressureDocuments.getDocuments()) {
                    double testLevel = bloodPressureDocument.getDouble("hypertension_percent");
                    Timestamp timestamp = bloodPressureDocument.getTimestamp("timestamp");
                    DailyTest dailyTest = new DailyTest("Blood Pressure", testLevel, timestamp);
                    dailyTestsAll.add(dailyTest);
                    float time = getTestTime(timestamp);
                    bloodPressureEntries.add(new Entry(time, (float) testLevel));
                }

                testsRef.document("cholesterolAndFats_test")
                        .collection(pickedDate)
                        .whereGreaterThanOrEqualTo("timestamp", firstTimestamp)
                        .whereLessThanOrEqualTo("timestamp", lastTimestamp)
                        .orderBy("timestamp")
                        .get().addOnSuccessListener(cholesterolDocuments ->
                {
                    for (DocumentSnapshot cholesterolDocument : cholesterolDocuments.getDocuments())
                    {
                        double totalCholesterolLevel = cholesterolDocument.getDouble("CholesterolTotal_percent");
                        double hdlLevel = cholesterolDocument.getDouble("HDLCholesterol_percent");
                        double ldlLevel = cholesterolDocument.getDouble("LDLCholesterol_percent");
                        double triglycerideLevel = cholesterolDocument.getDouble("Triglycerid_percent");

                        Timestamp timestamp = cholesterolDocument.getTimestamp("timestamp");

                        DailyTest dailyTest1 = new DailyTest("Total Cholesterol", totalCholesterolLevel, timestamp);
                        DailyTest dailyTest2 = new DailyTest("HDL", hdlLevel, timestamp);
                        DailyTest dailyTest3 = new DailyTest("LDL", ldlLevel, timestamp);
                        DailyTest dailyTest4 = new DailyTest("Triglyceride", triglycerideLevel, timestamp);
                        dailyTestsAll.add(dailyTest1);
                        dailyTestsAll.add(dailyTest2);
                        dailyTestsAll.add(dailyTest3);
                        dailyTestsAll.add(dailyTest4);
                        float time = getTestTime(timestamp);
                        totalCholesterolEntries.add(new Entry(time, (float) totalCholesterolLevel));
                        hdlEntries.add(new Entry(time, (float) hdlLevel));
                        ldlEntries.add(new Entry(time, (float) ldlLevel));
                        triglycerideEntries.add(new Entry(time, (float) triglycerideLevel));
                    }

                    if (dailyTestsAll.size() == 1)  // the list contains only the header item
                    {
                        clearTestsCheckBoxes();
                        chart.invalidate();
                    }

                    Collections.sort(dailyTestsAll);

                    if (!dailyTests.equals(dailyTestsAll))
                    {
                        if(selectState != HOURS_SLIDER)
                            clearTestsCheckBoxes();

                        dailyTests.clear();
                        dailyTests.addAll(dailyTestsAll);
                        dailyTestAdapter.notifyDataSetChanged();

                        if (glucoseEntries.size() != 0)
                        {
                            if(selectState != HOURS_SLIDER)
                                glucoseCheckBox.setChecked(true);
                            glucoseCheckBox.setEnabled(true);
                            zeroEntries.add(new Entry(0, glucoseEntries.get(0).getX()));
                        }

                        if (bloodPressureEntries.size() != 0)
                        {
                            if(selectState != HOURS_SLIDER)
                                bloodPressureCheckBox.setChecked(true);
                            bloodPressureCheckBox.setEnabled(true);
                            zeroEntries.add(new Entry(0, bloodPressureEntries.get(0).getX()));
                        }

                        if (totalCholesterolEntries.size() != 0)
                        {
                            if(selectState != HOURS_SLIDER)
                            {
                                totalCholesterolCheckBox.setChecked(true);
                                hdlCheckBox.setChecked(true);
                                ldlCheckBox.setChecked(true);
                                triglycerideCheckBox.setChecked(true);
                            }

                            totalCholesterolCheckBox.setEnabled(true);
                            hdlCheckBox.setEnabled(true);
                            ldlCheckBox.setEnabled(true);
                            triglycerideCheckBox.setEnabled(true);
                            zeroEntries.add(new Entry(0, totalCholesterolEntries.get(0).getX()));
                        }

                        LineDataSet glucoseDataSet = new LineDataSet(glucoseEntries, "Glucose");
                        LineDataSet bloodPressureDataSet = new LineDataSet(bloodPressureEntries, "Blood Pressure");
                        LineDataSet totalCholesterolDataSet = new LineDataSet(totalCholesterolEntries, "Total Cholesterol");
                        LineDataSet hdlDataSet = new LineDataSet(hdlEntries, "HDL");
                        LineDataSet ldlDataSet = new LineDataSet(ldlEntries, "LDL");
                        LineDataSet triglycerideDataSet = new LineDataSet(triglycerideEntries, "Triglyceride");
                        LineDataSet zeroDataSet = new LineDataSet(zeroEntries, "");


                        setDataSetCheckBoxListener(glucoseCheckBox, glucoseDataSet);
                        setDataSetCheckBoxListener(bloodPressureCheckBox, bloodPressureDataSet);
                        setDataSetCheckBoxListener(totalCholesterolCheckBox, totalCholesterolDataSet);
                        setDataSetCheckBoxListener(hdlCheckBox, hdlDataSet);
                        setDataSetCheckBoxListener(ldlCheckBox, ldlDataSet);
                        setDataSetCheckBoxListener(triglycerideCheckBox, triglycerideDataSet);

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
                        if(!glucoseCheckBox.isChecked())
                            glucoseDataSet.setVisible(false);

                        bloodPressureDataSet.setColor(getColor(R.color.blood_pressure_color));
                        bloodPressureDataSet.setCircleColor(getColor(R.color.colorPrimary));
                        bloodPressureDataSet.setCircleHoleRadius(2f);
                        bloodPressureDataSet.setCircleRadius(4f);
                        bloodPressureDataSet.setLineWidth(3f);
                        bloodPressureDataSet.setValueTextSize(12f);
                        if(!bloodPressureCheckBox.isChecked())
                            bloodPressureDataSet.setVisible(false);

                        totalCholesterolDataSet.setColor(getColor(R.color.total_cholesterol_color));
                        totalCholesterolDataSet.setCircleColor(getColor(R.color.colorPrimary));
                        totalCholesterolDataSet.setCircleHoleRadius(2f);
                        totalCholesterolDataSet.setCircleRadius(4f);
                        totalCholesterolDataSet.setLineWidth(3f);
                        totalCholesterolDataSet.setValueTextSize(12f);
                        if(!totalCholesterolCheckBox.isChecked())
                            totalCholesterolDataSet.setVisible(false);

                        hdlDataSet.setColor(getColor(R.color.hdl_color));
                        hdlDataSet.setCircleColor(getColor(R.color.colorPrimary));
                        hdlDataSet.setCircleHoleRadius(2f);
                        hdlDataSet.setCircleRadius(4f);
                        hdlDataSet.setLineWidth(3f);
                        hdlDataSet.setValueTextSize(12f);
                        if(!hdlCheckBox.isChecked())
                            hdlDataSet.setVisible(false);

                        ldlDataSet.setColor(getColor(R.color.ldl_color));
                        ldlDataSet.setCircleColor(getColor(R.color.colorPrimary));
                        ldlDataSet.setCircleHoleRadius(2f);
                        ldlDataSet.setCircleRadius(4f);
                        ldlDataSet.setLineWidth(3f);
                        ldlDataSet.setValueTextSize(12f);
                        if(!ldlCheckBox.isChecked())
                            ldlDataSet.setVisible(false);

                        triglycerideDataSet.setColor(getColor(R.color.triglyceride_color));
                        triglycerideDataSet.setCircleColor(getColor(R.color.colorPrimary));
                        triglycerideDataSet.setCircleHoleRadius(2f);
                        triglycerideDataSet.setCircleRadius(4f);
                        triglycerideDataSet.setLineWidth(3f);
                        triglycerideDataSet.setValueTextSize(12f);
                        if(!triglycerideCheckBox.isChecked())
                            triglycerideDataSet.setVisible(false);


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

                        DetailsMarkerView detailsMarkerView = new DetailsMarkerView(this, R.layout.daily_test_markerview);
                        detailsMarkerView.setChartView(chart);

                        chart.setMarker(detailsMarkerView);
                        chart.setData(lineData);
                        chart.invalidate();
                    }

                    if (selectState == PICKER)
                        progressDialog.dismissProgressDialog();
                    else if(selectState == DAYS_SLIDER)
                        chartProgressBar.setVisibility(View.GONE);

                    pickDateFloatingActionButton.setEnabled(true);
                });
            });
        });
    }


    private void setSingleDate()
    {
        int day = (int) daysRangeSeekBar.getLeftSeekBar().getProgress();

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder
                .datePicker();

        builder.setSelection(selectedDate + getHoursInMillis(24) * day + getHoursInMillis(3));

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.setValidator(DateValidatorPointBackward.now()).build());

        MaterialDatePicker<Long> materialDatePicker = builder.build();

        materialDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            hideFloatingButtons();
            daysRangeSeekBar.setVisibility(View.GONE);
            daysRangeSeekBar.setProgress(0);

            selectedDate = (Long) selection - getHoursInMillis(3);
            String pickedDate = DateFormat.format("yyyy-M-d", selectedDate).toString();


            /* redraw only if the picked date is different than previous picked date */
            if (!pickedDate.equals(this.pickedDate))
            {
                int firstHour = (int) hoursRangeSeekBar.getLeftSeekBar().getProgress();
                int lastHour = (int) hoursRangeSeekBar.getRightSeekBar().getProgress();

                firstTimestamp = getTimestamp(selectedDate + getHoursInMillis(firstHour));
                lastTimestamp = getTimestamp(selectedDate + getHoursInMillis(lastHour));

                this.pickedDate = pickedDate;
                pickDateFloatingActionButton.setEnabled(false);

                setHoursRangeSeekBar();
                selectState = PICKER;
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

        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder
                .build();

        materialDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            hideFloatingButtons();
            daysRangeSeekBar.setVisibility(View.VISIBLE);

            Pair<Long, Long> dateRange = ((Pair<Long, Long>) selection);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d", Locale.US);

            selectedDate = dateRange.first - getHoursInMillis(3);

            String firstDate = simpleDateFormat.format(dateRange.first);
            int firstDateNumber = Integer.parseInt(firstDate.substring(firstDate.lastIndexOf('-') + 1));

            String lastDate = simpleDateFormat.format(dateRange.second);
            int lastDateNumber = Integer.parseInt(lastDate.substring(lastDate.lastIndexOf('-') + 1));

            String date = firstDate.substring(0, firstDate.lastIndexOf('-') + 1);

            setHoursRangeSeekBar();
            setDaysRangeSeekBar(date, firstDateNumber, lastDateNumber);

            pickedDate = firstDate;

            int firstHour = (int) hoursRangeSeekBar.getLeftSeekBar().getProgress();
            int lastHour = (int) hoursRangeSeekBar.getRightSeekBar().getProgress();

            firstTimestamp = getTimestamp(selectedDate + getHoursInMillis(firstHour));
            lastTimestamp = getTimestamp(selectedDate + getHoursInMillis(lastHour));

            selectState = PICKER;
            setChart();
        });

        materialDatePicker.show(getSupportFragmentManager(), "PatientChartsActivity");
    }


    private void setMultiDates()
    {
        OnSelectDateListener onSelectDateListener = new OnSelectDateListener()
        {
            @Override
            public void onSelect(@NotNull List<Calendar> list)
            {
                hideFloatingButtons();
                if(list.size() > 1)
                {
                    daysRangeSeekBar.setVisibility(View.VISIBLE);
                    setMultiDatesSeekBar(list);
                }
                else
                {
                    daysRangeSeekBar.setVisibility(View.GONE);
                    daysRangeSeekBar.setProgress(0);
                    setHoursRangeSeekBar();
                }


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d", Locale.US);
                pickedDate = simpleDateFormat.format(list.get(0).getTimeInMillis());

                selectedDate = list.get(0).getTimeInMillis();

                int firstHour = (int) hoursRangeSeekBar.getLeftSeekBar().getProgress();
                int lastHour = (int) hoursRangeSeekBar.getRightSeekBar().getProgress();

                firstTimestamp = getTimestamp(selectedDate + getHoursInMillis(firstHour));
                lastTimestamp = getTimestamp(selectedDate + getHoursInMillis(lastHour));

                selectState = PICKER;
                setChart();
            }
        };

        DatePickerBuilder builder = new DatePickerBuilder(this, onSelectDateListener)
                .pickerType(CalendarView.MANY_DAYS_PICKER)
                .date(Calendar.getInstance())
                .maximumDate(Calendar.getInstance())
                .headerColor(R.color.colorPrimary)
                .abbreviationsLabelsColor(R.color.black)
                .selectionColor(R.color.colorPrimary)
                .todayLabelColor(R.color.colorAccent)
                .dialogButtonsColor(R.color.colorPrimary)
                .firstDayOfWeek(CalendarWeekDay.SATURDAY);

        DatePicker multiDatePicker = builder.build();
        multiDatePicker.show();
    }


    private void setHoursRangeSeekBar()
    {
        hoursRangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener()
        {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser)
            {
                view.getLeftSeekBar().setIndicatorText(getHourString((int) leftValue));
                view.getRightSeekBar().setIndicatorText(getHourString((int) rightValue));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) { }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft)
            {
                int firstHour = (int) view.getLeftSeekBar().getProgress();
                int lastHour = (int) view.getRightSeekBar().getProgress();
                long day = getHoursInMillis(24) * (int) daysRangeSeekBar.getLeftSeekBar().getProgress();

                firstTimestamp = getTimestamp(selectedDate + getHoursInMillis(firstHour) + day);
                lastTimestamp = getTimestamp(selectedDate + getHoursInMillis(lastHour) + day);

                selectState = HOURS_SLIDER;
                setChart();
            }
        });
    }


    private void setDaysRangeSeekBar(String date, int firstDateNumber, int lastDateNumber)
    {
        List<String> dates = new ArrayList<>();

        for (int i = firstDateNumber; i <= lastDateNumber; i++)
            dates.add(date + i);

        daysRangeSeekBar.setProgress(0);
        daysRangeSeekBar.setSteps(dates.size() - 1);
        daysRangeSeekBar.getLeftSeekBar().setIndicatorText(dates.get(0));
        daysRangeSeekBar.setRange(0, lastDateNumber - firstDateNumber);
        daysRangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser)
            {
                int dayNumber = (int) leftValue;

                view.getLeftSeekBar().setIndicatorText(dates.get(dayNumber));
                pickedDate = dates.get(dayNumber);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {   }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft)
            {
                int day = (int) view.getLeftSeekBar().getProgress();
                int firstHour = (int) hoursRangeSeekBar.getLeftSeekBar().getProgress();
                int lastHour = (int) hoursRangeSeekBar.getRightSeekBar().getProgress();

                firstTimestamp = getTimestamp(selectedDate + getHoursInMillis(24) * day
                        + getHoursInMillis(firstHour));
                lastTimestamp = getTimestamp(selectedDate + getHoursInMillis(24) * day
                        + getHoursInMillis(lastHour));

                selectState = DAYS_SLIDER;
                setChart();
            }
        });
    }


    private void setMultiDatesSeekBar(List<Calendar> list)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d", Locale.US);
        List<String> dates = new ArrayList<>();

        for (int i = 0; i < list.size(); i++)
        {
            long dayInMillis = list.get(i).getTimeInMillis();
            dates.add(simpleDateFormat.format(dayInMillis));
        }

        hoursRangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener()
        {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser)
            {
                view.getLeftSeekBar().setIndicatorText(getHourString((int) leftValue));
                view.getRightSeekBar().setIndicatorText(getHourString((int) rightValue));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {   }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft)
            {
                int firstHour = (int) view.getLeftSeekBar().getProgress();
                int lastHour = (int) view.getRightSeekBar().getProgress();
                long day = convertMillisToDay(list.get((int) daysRangeSeekBar.getLeftSeekBar().getProgress()).getTimeInMillis()
                        - list.get(0).getTimeInMillis());

                firstTimestamp = getTimestamp(selectedDate + getHoursInMillis(firstHour) +  getDaysInMillis(day));
                lastTimestamp = getTimestamp(selectedDate + getHoursInMillis(lastHour) +  getDaysInMillis(day));

                selectState = HOURS_SLIDER;
                setChart();
            }
        });

        daysRangeSeekBar.setProgress(0);
        daysRangeSeekBar.setSteps(dates.size() - 1);
        daysRangeSeekBar.getLeftSeekBar().setIndicatorText(dates.get(0));
        daysRangeSeekBar.setRange(0, dates.size() - 1);
        daysRangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener()
        {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser)
            {
                int dayNumber = (int) leftValue;

                view.getLeftSeekBar().setIndicatorText(dates.get(dayNumber));
                pickedDate = dates.get(dayNumber);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) { }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft)
            {
                long day = convertMillisToDay(list.get((int)view.getLeftSeekBar().getProgress()).getTimeInMillis()
                        - list.get(0).getTimeInMillis());

                int firstHour = (int) hoursRangeSeekBar.getLeftSeekBar().getProgress();
                int lastHour = (int) hoursRangeSeekBar.getRightSeekBar().getProgress();

                firstTimestamp = getTimestamp(selectedDate + getDaysInMillis(day)
                        + getHoursInMillis(firstHour));
                lastTimestamp = getTimestamp(selectedDate + getDaysInMillis(day)
                        + getHoursInMillis(lastHour));

                selectState = DAYS_SLIDER;
                setChart();
            }
        });
    }


    private void setDataSetCheckBoxListener(CheckBox checkBox, DataSet dataSet)
    {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                dataSet.setHighlightEnabled(true);
                dataSet.setVisible(true);
            }
            else
            {
                dataSet.setHighlightEnabled(false);
                dataSet.setVisible(false);
            }

            chart.invalidate();
        });
    }


    private void clearTestsCheckBoxes()
    {
        glucoseCheckBox.setEnabled(false);
        bloodPressureCheckBox.setEnabled(false);
        totalCholesterolCheckBox.setEnabled(false);
        hdlCheckBox.setEnabled(false);
        ldlCheckBox.setEnabled(false);
        triglycerideCheckBox.setEnabled(false);
        glucoseCheckBox.setChecked(false);
        bloodPressureCheckBox.setChecked(false);
        totalCholesterolCheckBox.setChecked(false);
        hdlCheckBox.setChecked(false);
        ldlCheckBox.setChecked(false);
        triglycerideCheckBox.setChecked(false);
    }


    private String getHourString(int hour)
    {
        if (hour == 0)
            return "12 AM";
        else if (hour == 12)
            return "12 PM";
        else if (hour == 24)
            return "11:59 PM";
        else if (hour < 12)
            return hour + " AM";
        else
            return hour - 12 + " PM";
    }


    private Timestamp getTimestamp(Long milliSeconds)
    {
        return new Timestamp(new Date(milliSeconds));
    }


    private Long getHoursInMillis(float hours)
    {
        return TimeUnit.HOURS.toMillis((long) hours);
    }


    private Long getDaysInMillis(long days)
    {
        return TimeUnit.DAYS.toMillis(days);
    }


    private Long convertMillisToDay(Long date)
    {
        return TimeUnit.MILLISECONDS.toDays(date);
    }


    private float getTestTime(Timestamp timestamp)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.US);

        String dayTime = simpleDateFormat.format(timestamp.toDate());

        String hoursSystem = dayTime.substring(dayTime.indexOf(" ") + 1);
        String hourString = dayTime.substring(0, dayTime.indexOf(":"));
        String minuteString = dayTime.substring(dayTime.indexOf(":") + 1, dayTime.indexOf(" "));

        float time;
        if (hoursSystem.equals("AM"))
        {
            if (!hourString.equals("12"))
                time = Float.parseFloat(hourString + "." + minuteString);
            else
                time = Float.parseFloat(hourString + "." + minuteString) - 12;
        }
        else
        {
            if (!hourString.equals("12"))
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


    private void showFloatingButtons()
    {
        pickDateFloatingActionButton.setImageResource(R.drawable.ic_close_datepicker_floatingbutton);

        pickASingleDateFloatingActionButton.show();
        pickDateRangeFloatingActionButton.show();
        pickMultiDatesFloatingActionButton.show();

        findViewById(R.id.singleDateTextView).setVisibility(View.VISIBLE);
        findViewById(R.id.dateRangeTextView).setVisibility(View.VISIBLE);
        findViewById(R.id.multiDatesTextView).setVisibility(View.VISIBLE);

        isFloatingButtonsVisible = true;
    }


    private void hideFloatingButtons()
    {
        pickDateFloatingActionButton.setImageResource(R.drawable.ic_calendar);
        pickASingleDateFloatingActionButton.hide();
        pickDateRangeFloatingActionButton.hide();
        pickMultiDatesFloatingActionButton.hide();

        findViewById(R.id.singleDateTextView).setVisibility(View.GONE);
        findViewById(R.id.dateRangeTextView).setVisibility(View.GONE);
        findViewById(R.id.multiDatesTextView).setVisibility(View.GONE);

        isFloatingButtonsVisible = false;
    }
}