package com.project.cdh.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.project.cdh.DrawerUtil;
import com.project.cdh.R;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PatientMedicinesActivity extends AppCompatActivity {
/////////////////varable/////////////
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    String patientID;

    ArrayList<Map<String, Object>> New;
    ArrayList<Map<String, Object>> Canceled;

    TableLayout tb1,tb2;
    int child1=0,child2=0;

    MaterialButtonToggleGroup btngg;
    Button bt1,bt2;

    private ViewFlipper viewFlipper;

    FloatingActionButton fpa;
////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_medicines);

        patientID = getIntent().getStringExtra("patientID");
        if((patientID == null) || patientID.isEmpty() )
        {
           // Toasty.showText(getApplicationContext(),"Error occurred!!!\\n patient ID is error",Toasty.ERROR, Toast.LENGTH_LONG);
           // finish();
            //return;
            patientID = FirebaseAuth.getInstance().getUid();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tb1 = findViewById(R.id.idf);
        child1 = tb1.getChildCount();

        tb2 = findViewById(R.id.idf1);
        child2 = tb2.getChildCount();

        getMedicines();

        viewFlipper = findViewById(R.id.view_flipper);
        btngg = findViewById(R.id.toggleButtonGroup);
        bt1 = findViewById(R.id.btn1);
        bt2 = findViewById(R.id.btn2);

        bt2.setEnabled(false);
        bt1.setEnabled(true);
        btngg.clearChecked();
        btngg.check(bt2.getId());

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt1.setEnabled(false);
                bt2.setEnabled(true);
                btngg.clearChecked();
                btngg.check(bt1.getId());

                viewFlipper.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
                viewFlipper.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);
                viewFlipper.showNext();
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt2.setEnabled(false);
                bt1.setEnabled(true);
                btngg.clearChecked();
                btngg.check(bt2.getId());

                viewFlipper.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
                viewFlipper.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);
                viewFlipper.showNext();
            }
        });

        fpa = findViewById(R.id.fpa3);
        fpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cls();
                getMedicines();
            }
        });

    }

    void getMedicines()
    {
        New = new ArrayList<>();
        Canceled = new ArrayList<>();
        ProgressDialog x= ProgressDialog.show(this, "",
                "Please Wait TO get and Classify Medicines...", true);
        Query DRC = db.collection("patients") // table
                .document(patientID) // patient id
                .collection("medicines")
                .orderBy("medicine_name");

        DRC.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    Map<String, Object> medicines= new HashMap<>();
                    medicines.put("medicine_name",document.getString("medicine_name"));
                    medicines.put("morning_dose",document.getLong("morning_dose"));
                    medicines.put("noon_dose",document.getLong("noon_dose"));
                    medicines.put("dinner_dose",document.getLong("dinner_dose"));
                    medicines.put("before_sleep_dose",document.getLong("before_sleep_dose"));
                    medicines.put("absorption",document.getBoolean("absorption"));
                    medicines.put("notes",document.getString("notes"));
                    medicines.put("doctotr_name",document.getString("doctotr_name"));
                    medicines.put("prescription_date",document.getString("prescription_date"));
                    medicines.put("old_prescription_date", (ArrayList<String>) document.get("old_prescription_date"));
                    if(document.getBoolean("status")) { New.add(medicines); }
                    else { Canceled.add(medicines); }
                }

                ShowD(tb1,New);
                ShowD(tb2,Canceled);
                x.dismiss();
            }
        });

    }

    void ShowD(TableLayout tb1,ArrayList<Map<String, Object>> New)
    {
        if(New.size()==0)
        {
            TableRow tr1 = new TableRow(this);
            tr1.setPaddingRelative(5, 5, 5, 5);
            tr1.setGravity(Gravity.CENTER);

            TextView textview = new TextView(this);
            textview.setText("no data");
            Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
            textview.setTypeface(tf, Typeface.BOLD);
           // textview.setLayoutParams(mw);
            textview.setGravity(Gravity.CENTER);
            textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textview.setTextSize(18);
            textview.setTextColor(Color.rgb(255, 0, 0));
            tr1.addView(textview);
            tb1.addView(tr1);

            //white space
            TableRow tr = new TableRow(this);
            tr.setPaddingRelative(5, 5, 5, 5);
            tr.setGravity(Gravity.CENTER);

            textview = new TextView(this);
            textview.setText("");
            //textview.setLayoutParams(mw);
            textview.setGravity(Gravity.CENTER);
            tr.addView(textview);
            tb1.addView(tr);
            return;
        }

        ProgressDialog x = ProgressDialog.show(this, "",
                "Please Wait TO display medicines...", true);
        for(int i=0;i<New.size();i++)
        {
            Map<String, Object> medicines=New.get(i);

            TableRow  tr1 = new TableRow(this);
            tr1.setPaddingRelative(5,5,5,5);
            tr1.setBackgroundColor(Color.rgb(25,25,25));
            tr1.setGravity(Gravity.CENTER);
            // tr1.setScrollbarFadingEnabled(false);
            //  tr1.setScrollBarFadeDuration(0);
            tb1.addView(tr1);

            tr1 = new TableRow(this);
            tr1.setPaddingRelative(5,5,5,5);
            tr1.setGravity(Gravity.CENTER);
          //  tr1.setScrollbarFadingEnabled(false);
           // tr1.setScrollBarFadeDuration(0);

            TableRow.LayoutParams mw1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,1);

            TableLayout TB1 = new TableLayout(this);
            TB1.setLayoutParams(mw1);

            //

            TableRow tr2 = new TableRow(this);
            tr2.setPaddingRelative(5,5,5,5);
            tr2.setGravity(Gravity.CENTER);
          //  tr2.setScrollbarFadingEnabled(false);
          //  tr2.setScrollBarFadeDuration(0);

            TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,1);

            TextView textview = new TextView(this);
            textview.setText(medicines.get("medicine_name").toString());
            textview.setLayoutParams(mw); // match warp wighet
            Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
            textview.setTextSize(18);
            textview.setTypeface(tf,Typeface.BOLD);
            textview.setGravity(Gravity.CENTER); //gravity center
            tr2.addView(textview);
            TB1.addView(tr2);

            tr2 = new TableRow(this);
            tr2.setPaddingRelative(5,5,5,5);
            tr2.setGravity(Gravity.CENTER);
           // tr2.setScrollbarFadingEnabled(false);
            //tr2.setScrollBarFadeDuration(0);

            textview = new TextView(this);
            if(medicines.get("prescription_date").toString().isEmpty())
            {
                textview.setText("Old Prescription Date");
            }
                else {textview.setText(medicines.get("prescription_date").toString());}
            textview.setLayoutParams(mw); // match warp wighet
            tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
            textview.setTypeface(tf,Typeface.BOLD);
            textview.setTextColor(Color.rgb(1,87,155));
            textview.setGravity(Gravity.CENTER); //gravity center
            ArrayList old =  (ArrayList<String>) medicines.get("old_prescription_date");
            if(old.size()!=0)
            {
                textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String data="";

                        for(int i_ =0;i_<old.size();i_++){data = data +"\n"+old.get(i_);}
                        data = data +"\n";
                        new AlertDialog.Builder(PatientMedicinesActivity.this)
                                .setTitle("Old Prescription Date")
                                .setMessage(data)
                                .setPositiveButton(android.R.string.ok, null)
                                .show();

                        return;

                    }
                });
            }
            tr2.addView(textview);
            TB1.addView(tr2);


            tr2 = new TableRow(this);
            tr2.setPaddingRelative(5,5,5,5);
            tr2.setGravity(Gravity.CENTER);
           // tr2.setScrollbarFadingEnabled(false);
          //  tr2.setScrollBarFadeDuration(0);
            tr2.setBackgroundColor(Color.rgb(111,186,221));


            TableRow.LayoutParams mw2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,4);

            textview = new TextView(this);
            textview.setText("Daily dose");
            textview.setLayoutParams(mw2); // match warp wighet
            tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
            textview.setTypeface(tf,Typeface.BOLD);
            textview.setTextSize(18);
            textview.setBackgroundColor(Color.rgb(255,255,255));
            textview.setGravity(Gravity.CENTER); //gravity center
            tr2.addView(textview);
            TB1.addView(tr2);

            tr1.addView(TB1);
            tb1.addView(tr1);

            tr1 = new TableRow(this);
            tr1.setPaddingRelative(5,5,5,5);
            tr1.setGravity(Gravity.CENTER);
            // tr1.setScrollbarFadingEnabled(false);
            //  tr1.setScrollBarFadeDuration(0);

            TableLayout TB2 = new TableLayout(this);
            TB2.setLayoutParams(mw1);

            TableRow tr3 = new TableRow(this);
            tr3.setPaddingRelative(5,5,5,5);
            tr3.setGravity(Gravity.CENTER);

            tr3 = new TableRow(this);
            tr3.setPaddingRelative(5,5,5,5);
            tr3.setGravity(Gravity.CENTER);
          //  tr2.setScrollbarFadingEnabled(false);
          //  tr2.setScrollBarFadeDuration(0);

            textview = new TextView(this);
            textview.setText("Morning");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTypeface(null,Typeface.BOLD);
            textview.setGravity(Gravity.CENTER); //gravity center
            tr3.addView(textview);

            textview = new TextView(this);
            textview.setText("Noon");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTypeface(null,Typeface.BOLD);
            textview.setGravity(Gravity.CENTER); //gravity center
            tr3.addView(textview);

            textview = new TextView(this);
            textview.setText("Dinner");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTypeface(null,Typeface.BOLD);
            textview.setGravity(Gravity.CENTER); //gravity center
            tr3.addView(textview);

            textview = new TextView(this);
            textview.setText("Before Sleep");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTypeface(null,Typeface.BOLD);
            textview.setGravity(Gravity.CENTER); //gravity center
            tr3.addView(textview);
            TB2.addView(tr3);


            tr3 = new TableRow(this);
            tr3.setPaddingRelative(5,5,5,5);
            tr3.setGravity(Gravity.CENTER);
          //  tr2.setScrollbarFadingEnabled(false);
         //   tr2.setScrollBarFadeDuration(0);

            textview = new TextView(this);
            textview.setText(medicines.get("morning_dose").toString());
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            tr3.addView(textview);

            textview = new TextView(this);
            textview.setText(medicines.get("noon_dose").toString());
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            tr3.addView(textview);

            textview = new TextView(this);
            textview.setText(medicines.get("dinner_dose").toString());
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            tr3.addView(textview);

            textview = new TextView(this);
            textview.setText(medicines.get("before_sleep_dose").toString());
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            tr3.addView(textview);
            TB2.addView(tr3);

            tr1.addView(TB2);
            tb1.addView(tr1);

            tr1 = new TableRow(this);
            tr1.setPaddingRelative(5,5,5,5);
            tr1.setGravity(Gravity.CENTER);
           // tr1.setScrollbarFadingEnabled(false);
          //  tr1.setScrollBarFadeDuration(0);


            TB1 = new TableLayout(this);
            TB1.setLayoutParams(mw1);

            tr2 = new TableRow(this);
            tr2.setPaddingRelative(5,5,5,5);
            tr2.setGravity(Gravity.CENTER);
            // tr2.setScrollbarFadingEnabled(false);
            //  tr2.setScrollBarFadeDuration(0);
            tr2.setBackgroundColor(Color.rgb(111,186,221));


            TableRow.LayoutParams mw3 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,3);

            textview = new TextView(this);
            textview.setText("Info");
            textview.setLayoutParams(mw3); // match warp wighet
            tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
            textview.setTypeface(tf,Typeface.BOLD);
            textview.setTextSize(18);
            textview.setBackgroundColor(Color.rgb(255,255,255));
            textview.setGravity(Gravity.CENTER); //gravity center
            tr2.addView(textview);
            TB1.addView(tr2);


            tr2 = new TableRow(this);
            tr2.setPaddingRelative(5,5,5,5);
            tr2.setGravity(Gravity.CENTER);
        //    tr2.setScrollbarFadingEnabled(false);
           // tr2.setScrollBarFadeDuration(0);

            textview = new TextView(this);
            textview.setText("Doctor");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTypeface(null,Typeface.BOLD);
            textview.setGravity(Gravity.CENTER); //gravity center
            tr2.addView(textview);

            textview = new TextView(this);
            textview.setText("Absorption");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTypeface(null,Typeface.BOLD);
            textview.setGravity(Gravity.CENTER); //gravity center
            tr2.addView(textview);

            textview = new TextView(this);
            textview.setText("Note");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTypeface(null,Typeface.BOLD);
            textview.setGravity(Gravity.CENTER); //gravity center
            tr2.addView(textview);

            textview = new TextView(this);
            textview.setText("");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTypeface(null,Typeface.BOLD);
            textview.setGravity(Gravity.CENTER); //gravity center
            tr2.addView(textview);
            TB1.addView(tr2);


            tr2 = new TableRow(this);
            tr2.setPaddingRelative(5,5,5,5);
            tr2.setGravity(Gravity.CENTER);
          //  tr2.setScrollbarFadingEnabled(false);
         //   tr2.setScrollBarFadeDuration(0);

            textview = new TextView(this);
            textview.setText(medicines.get("doctotr_name").toString());
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            tr2.addView(textview);

            textview = new TextView(this);
            textview.setText((((boolean)medicines.get("absorption"))? "After Eating":"Before Eating"));
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            tr2.addView(textview);

            textview = new TextView(this);
            textview.setText("Notes");
            textview.setLayoutParams(mw);
            textview.setForeground(getResources().getDrawable(R.drawable.round_blue_prel));
            textview.setTextColor(Color.rgb(0, 90, 255));
            textview.setGravity(Gravity.CENTER);
            textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PatientMedicinesActivity.this)
                            .setMessage(medicines.get("notes").toString())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                    return;
                }
            });
            tr2.addView(textview);

            textview = new TextView(this);
            textview.setText("");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTypeface(null,Typeface.BOLD);
            textview.setGravity(Gravity.CENTER); //gravity center
            tr2.addView(textview);
            TB1.addView(tr2);

            tr1.addView(TB1);
            tb1.addView(tr1);

           /* tr1 = new TableRow(this);
            tr1.setPaddingRelative(5,5,5,5);
            tr1.setBackgroundColor(Color.rgb(25,25,25));
            tr1.setGravity(Gravity.CENTER);
            // tr1.setScrollbarFadingEnabled(false);
            //  tr1.setScrollBarFadeDuration(0);
            tb1.addView(tr1);*/

            tr1 = new TableRow(this);
            tr1.setPaddingRelative(5,5,5,5);
            tr1.setBackgroundColor(Color.rgb(255,255,255));
            tr1.setGravity(Gravity.CENTER);
            // tr1.setScrollbarFadingEnabled(false);
            //  tr1.setScrollBarFadeDuration(0);
            tb1.addView(tr1);
        }
        x.dismiss();

    }

    void cls()
    {
        int chd = tb1.getChildCount() - 1;
        while(chd != (child1-1))
        {
            tb1.removeView(tb1.getChildAt(chd));
            chd--;
        }

        chd = tb2.getChildCount() - 1;
        while(chd != (child2-1))
        {
            tb2.removeView(tb2.getChildAt(chd));
            chd--;
        }
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getPatientDrawer(this, 17);

    }

    //rotate
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.i(COMMON_TAG,"MainActivity onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //  Log.i(COMMON_TAG,"MainActivity onSaveInstanceState");
    }
}