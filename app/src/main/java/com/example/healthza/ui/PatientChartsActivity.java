package com.example.healthza.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.healthza.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class PatientChartsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_charts);

        LineChart chart = (LineChart) findViewById(R.id.lineChart);

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


        List<Entry> entries = new ArrayList<>();

        entries.add(new Entry(0, 60));
        entries.add(new Entry(0, 55));
        entries.add(new Entry(1, 70));
        entries.add(new Entry(2, 88));
        entries.add(new Entry(3, 95));
        entries.add(new Entry(4, 90));
        entries.add(new Entry(5, 84));
        entries.add(new Entry(6, 80));
        entries.add(new Entry(7, 60));
        entries.add(new Entry(8, 60));
        entries.add(new Entry(9, 120));

        LineDataSet dataSet = new LineDataSet(entries, "Glucose");

        dataSet.setFillAlpha(110);

        dataSet.setColor(Color.GREEN);
        dataSet.setLineWidth(3f);
        dataSet.setValueTextSize(12f);
        ArrayList<IDataSet> dataSets = new ArrayList<>();

        dataSets.add(dataSet);

        LineData lineData = new LineData(dataSet);


        chart.setData(lineData);
        chart.invalidate();
    }
}