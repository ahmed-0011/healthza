package com.project.cdh.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DoctorAppointmentsActivity extends AppCompatActivity
{

    List<Map<String, Object>> NEW;
    List<Map<String, Object>> EXPIERD;

    private ChipNavigationBar chipNavigationBar;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    Button bt1,bt2;

    boolean bn1=true,bn2=false;

    boolean stu = false;
    boolean mod = false;

    TableLayout tb1,tb2;
    int child1=0,child2=0;
    Long sd,ed;

    private Long selectedDate;
    private String pickedDate;

    FloatingActionButton update;
    FloatingActionButton Range;

    MaterialButtonToggleGroup btngg;

    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String dId = firebaseAuth.getCurrentUser().getUid();
        getData();

        setContentView(R.layout.activity_doctor_appointments);

        stu = true;
        bn1=true;
        bn2=true;
        mod = true;

        chipNavigationBar = findViewById(R.id.bottomNavigationBar);

        setupBottomNavigationBar();

        tb1 = findViewById(R.id.idf);
        child1 = tb1.getChildCount();

        tb2 = findViewById(R.id.idf1);
        child2 = tb2.getChildCount();

        btngg = findViewById(R.id.toggleButtonGroup);

        viewFlipper = findViewById(R.id.view_flipper);

        bt1 = findViewById(R.id.btn1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt2.setEnabled(true);
                bt1.setEnabled(false);
                btngg.clearChecked();
                btngg.check(bt1.getId());
                mod = false;

                if(!bn1
                        &&NEW!=null)
                {
                    showN(NEW);
                    bn1=true;
                }
                stu = true;
                viewFlipper.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
                viewFlipper.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);
                viewFlipper.showNext();
            }
        });

        bt2 = findViewById(R.id.btn2);
        bt2.setEnabled(false);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bt1.setEnabled(true);
                bt2.setEnabled(false);
                btngg.clearChecked();
                btngg.check(bt2.getId());
                mod = true;
                if(!bn2
                        &&EXPIERD!=null)
                {
                    showE(EXPIERD);
                    bn2=true;
                }
                stu = false;

                viewFlipper.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
                viewFlipper.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);
                viewFlipper.showNext();
                //viewFlipper.showPrevious();
            }
        });


        update = findViewById(R.id.fpa2);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cls();
                getData();
                return;
            }
        });

        sd=Long.parseLong("-1");
        ed=Long.parseLong("-1");
        Range = findViewById(R.id.fpa1);
        Range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* cls();
                getData();*/
                setDateRange(sd,ed);
                return;
            }
        });
    }


    private void setupBottomNavigationBar()
    {
        chipNavigationBar.setItemSelected(R.id.appointmentsItem, true);

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(int i)
            {

                Intent intent = null;

                if (i == R.id.appointmentsItem)
                    return;
                else if (i == R.id.homeItem)
                    intent = new Intent(DoctorAppointmentsActivity.this, DoctorHomeActivity.class);
                else if (i == R.id.patientsItem)
                    intent = new Intent(DoctorAppointmentsActivity.this, PatientListActivity.class);
                else if (i == R.id.chatItem)
                    intent = new Intent(DoctorAppointmentsActivity.this, DoctorChatListActivity.class);


                if(intent != null)
                {
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }


    private void setDateRange(long x,long y)
    {
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker();

        if(x==-1 &&y==-1);
        else {
            androidx.core.util.Pair<Long, Long> selected = new androidx.core.util.Pair<>(x, y);
            builder.setSelection(selected);
        }
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        if(!mod) builder.setCalendarConstraints(constraintsBuilder.setValidator(DateValidatorPointBackward.now()).build());
        else builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<androidx.core.util.Pair<Long,Long>> materialDatePicker = builder
                .setTitleText("Select A Date Range")
                .build();

        materialDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            androidx.core.util.Pair<Long, Long> dateRange = ((Pair<Long, Long>) selection);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");

            selectedDate = dateRange.first - getHoursInMillies(3);
            pickedDate = simpleDateFormat.format(new Date(dateRange.first));
            cls();
            sd=dateRange.first;
            ed=dateRange.second;

            getData(new Date(dateRange.first), new Date(dateRange.second));
        });

        materialDatePicker.show(getSupportFragmentManager(), "DoctorAppointmentsActivity");
    }

    void getData(Date d1,Date d2)
    {
        ProgressDialog x= ProgressDialog.show(this, "",
                "Please Wait TO get and Classify appointments...", true);

        com.google.firebase.Timestamp t1 = com.google.firebase.Timestamp.now();

        List<Map<String, Object>> NEW_ = new ArrayList<>();
        List<Map<String, Object>> EXPIERD_ = new ArrayList<>();

        //EndDate+1
        Calendar cal = Calendar.getInstance();
        cal.setTime(d2);
        cal.add(Calendar.DATE, 1);
        d2=new Date(String.valueOf(cal.getTime()));

        for(int i=0;i<NEW.size();i++)
        {
            Date d=((Timestamp)NEW.get(i).get("timestamp")).toDate();
            Toasty.showText(getApplicationContext(),d.toString(),Toasty.INFORMATION, Toast.LENGTH_SHORT);
            if((d.compareTo(d1)>=0)
                    &&(d.compareTo(d2)<0))
            {
                NEW_.add(NEW.get(i));
            }
        }

        for(int i=0;i<EXPIERD.size();i++)
        {
            Date d=((Timestamp)EXPIERD.get(i).get("timestamp")).toDate();
            if((d.compareTo(d1)>=0)
                    &&(d.compareTo(d2)<0))
            {
                EXPIERD_.add(EXPIERD.get(i));
            }
        }
        showN(NEW_);
        showE(EXPIERD_);
        x.dismiss();

    }

    void getData()
    {
        ProgressDialog x= ProgressDialog.show(this, "",
                "Please Wait TO get and Classify appointments...", true);

        com.google.firebase.Timestamp t1 = com.google.firebase.Timestamp.now();

        NEW = new ArrayList<>();
        EXPIERD = new ArrayList<>();

        //List<Map<String, Object>> data = new ArrayList<>();
        String dId = firebaseAuth.getCurrentUser().getUid();
        Query DRC = db.collection("doctors")
                .document(dId).collection("appointments")
                .orderBy("timestamp");
        DRC.get().addOnCompleteListener(task -> {

            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    Map<String, Object> record = new HashMap<>();
                    record.put("patientName", document.getString("patientName"));
                    record.put("doctorId", document.getString("doctorId"));
                    record.put("type", document.getString("type"));
                    record.put("timestamp", document.getTimestamp("timestamp"));
                    record.put("expired", document.getBoolean("expired"));
                    record.put("description", document.getString("description"));
                    record.put("id", document.getId());

                    Query DRC0 =  db.collection("patients")
                            .whereEqualTo("identificationNumber",document.get("patientId"));

                    DRC0.get().addOnCompleteListener(task1 -> {

                        task1.getResult().getDocuments().get(0).get("identificationNumber");
                        record.put("pid", (""+task1.getResult().getDocuments().get(0).getId()));
                    });

                    if(document.getBoolean("expired"))EXPIERD.add(record);
                    else {

                        com.google.firebase.Timestamp t2 = document.getTimestamp("timestamp");
                        if (t2.compareTo(t1) <= 0) {

                            DocumentReference DRC1 =db.collection("doctors")
                                    .document(dId).collection("appointments").document(document.getId());
                            DRC1.update("expired", true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            EXPIERD.add(record);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                        else
                            NEW.add(record);
                    }
                }
                showN(NEW);
                showE(EXPIERD);
                x.dismiss();
            }
        });
    }




    void showE(List<Map<String, Object>> EXPIERD)
    {
        if(EXPIERD.size()==0)
        {
            TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,1);

            //tb2.removeView(tb2.getChildAt(tb2.getChildCount() - 1));

            TableRow tr1 = new TableRow(this);
            tr1.setPaddingRelative(5,5,5,5);
            tr1.setGravity(Gravity.CENTER);
            tr1.setScrollbarFadingEnabled(false);
            tr1.setScrollBarFadeDuration(0);

            TextView textview = new TextView(this);
            textview.setText("---------");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText("---------");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText("---------");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText("---------");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText("---------");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText("---------");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            tb2.addView(tr1);

           /* tr1 = new TableRow(this);
            tr1.setPaddingRelative(5,5,5,5);
            tr1.setGravity(Gravity.CENTER);
            tr1.setScrollbarFadingEnabled(false);
            tr1.setScrollBarFadeDuration(0);

            mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,5);

            textview = new TextView(this);
            textview.setText("no EXPIERD appointments");
            Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
            textview.setTypeface(tf, Typeface.BOLD);
            textview.setLayoutParams(mw);
            textview.setGravity(Gravity.CENTER);
            textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textview.setTextSize(18);
            textview.setTextColor(Color.rgb(255, 0, 0));
            tr1.addView(textview);
            tb2.addView(tr1);*/

        }

        ProgressDialog x = ProgressDialog.show(this, "",
                "Please Wait TO display appointments...", true);
        for(int i=0;i<EXPIERD.size();i++)
        {
            TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,1);

            TableRow tr1 = new TableRow(this);
            tr1.setPaddingRelative(5,5,5,5);
            tr1.setGravity(Gravity.CENTER);
            tr1.setScrollbarFadingEnabled(false);
            tr1.setScrollBarFadeDuration(0);

            TextView textview = new TextView(this);
            textview.setText(EXPIERD.get(i).get("type").toString());
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            Timestamp t1 = (Timestamp) EXPIERD.get(i).get("timestamp");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            String re=simpleDateFormat.format(t1.toDate());
            re= re.substring(0,(re.indexOf(" ")));

            textview = new TextView(this);
            textview.setText(re);
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTextColor(Color.rgb(255,0,0));//red color
            textview.setGravity(Gravity.CENTER); //gravity center
            //textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            re=simpleDateFormat.format(t1.toDate());
            re= re.substring((re.indexOf(" ")+1),re.lastIndexOf(":"));
            textview = new TextView(this);
            textview.setText(re);
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTextColor(Color.rgb(255,0,0)); //red color
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText(EXPIERD.get(i).get("patientName").toString());
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            String description = EXPIERD.get(i).get("description").toString();
            if(description==null
                    || description.isEmpty()) {
                textview = new TextView(this);
                textview.setText("-----");
                textview.setLayoutParams(mw); // match warp wighet
                textview.setGravity(Gravity.CENTER); //gravity center
                // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tr1.addView(textview);
            }
            else
            {
                TableRow.LayoutParams mw1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT,1);

                mw1.setMarginEnd(5);

                textview = new TextView(this);
                textview.setText("Description");
                textview.setLayoutParams(mw1);
                textview.setForeground(getResources().getDrawable(R.drawable.round_blue_prel));
                textview.setTextColor(Color.rgb(41, 182, 246));
                textview.setTypeface(Typeface.DEFAULT_BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(DoctorAppointmentsActivity.this)
                                .setMessage(description)
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        return;
                    }
                });
                tr1.addView(textview);
            }

            textview = new TextView(this);
            textview.setText("Remove");
            textview.setLayoutParams(mw);
            textview.setForeground(getResources().getDrawable(R.drawable.round_red));
            textview.setTextColor(Color.rgb(255,0,0));
            textview.setTypeface(Typeface.DEFAULT_BOLD);
            textview.setGravity(Gravity.CENTER);
            int finalI = i;
            textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DocumentReference Refs = db.collection("doctors")
                            .document(firebaseAuth.getCurrentUser().getUid())
                            .collection("appointments").document(EXPIERD.get(finalI).get("id").toString());
                    Refs.delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentReference refs = db.collection("patients")
                                    .document(EXPIERD.get(finalI).get("pid").toString())
                                    .collection("appointments").document(EXPIERD.get(finalI).get("id").toString());

                            refs.delete().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    //
                                    tb2.removeView(tr1);
                                    if(tb2.getChildCount()==child2)
                                    {
                                        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                                TableRow.LayoutParams.WRAP_CONTENT,1);

                                        TableRow tr1 = new TableRow(DoctorAppointmentsActivity.this);
                                        tr1.setPaddingRelative(5,5,5,5);
                                        tr1.setGravity(Gravity.CENTER);
                                        tr1.setScrollbarFadingEnabled(false);
                                        tr1.setScrollBarFadeDuration(0);

                                        TextView textview = new TextView(DoctorAppointmentsActivity.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tr1.addView(textview);

                                        textview = new TextView(DoctorAppointmentsActivity.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tr1.addView(textview);

                                        textview = new TextView(DoctorAppointmentsActivity.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tr1.addView(textview);

                                        textview = new TextView(DoctorAppointmentsActivity.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tr1.addView(textview);

                                        textview = new TextView(DoctorAppointmentsActivity.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tr1.addView(textview);

                                        textview = new TextView(DoctorAppointmentsActivity.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tr1.addView(textview);

                                        tb2.addView(tr1);
                                    }
                                    //
                                }
                            });
                        }
                    });
                    return;
                }
            });
            tr1.addView(textview);

            tb2.addView(tr1);

        }
        x.dismiss();
    }

    void showN(List<Map<String, Object>> NEW)
    {
        if(NEW.size()==0)
        {
            TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,1);

            //tb1.removeView(tb2.getChildAt(tb1.getChildCount() - 1));

            TableRow tr1 = new TableRow(this);
            tr1.setPaddingRelative(5,5,5,5);
            tr1.setGravity(Gravity.CENTER);
            tr1.setScrollbarFadingEnabled(false);
            tr1.setScrollBarFadeDuration(0);

            TextView textview = new TextView(this);
            textview.setText("---------");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText("---------");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText("---------");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText("---------");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText("---------");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText("---------");
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            tb1.addView(tr1);
        }

        ProgressDialog x = ProgressDialog.show(this, "",
                "Please Wait TO display appointments...", true);
        for(int i=0;i<NEW.size();i++)
        {
            TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,1);

            TableRow tr1 = new TableRow(this);
            tr1.setPaddingRelative(5,5,5,5);
            tr1.setGravity(Gravity.CENTER);
            tr1.setScrollbarFadingEnabled(false);
            tr1.setScrollBarFadeDuration(0);

            TextView textview = new TextView(this);
            textview.setText(NEW.get(i).get("type").toString());
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            Timestamp t1 = (Timestamp) NEW.get(i).get("timestamp");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            String re=simpleDateFormat.format(t1.toDate());
            re= re.substring(0,(re.indexOf(" ")));

            textview = new TextView(this);
            textview.setText(re);
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTextColor(Color.rgb(0,255,0));//green color
            textview.setGravity(Gravity.CENTER); //gravity center
            //textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            re=simpleDateFormat.format(t1.toDate());
            re= re.substring((re.indexOf(" ")+1),re.lastIndexOf(":"));
            textview = new TextView(this);
            textview.setText(re);
            textview.setLayoutParams(mw); // match warp wighet
            textview.setTextColor(Color.rgb(0,255,0)); //green color
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText(NEW.get(i).get("patientName").toString());
            textview.setLayoutParams(mw); // match warp wighet
            textview.setGravity(Gravity.CENTER); //gravity center
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr1.addView(textview);

            String description = NEW.get(i).get("description").toString();
            if(description==null
                    || description.isEmpty()) {
                textview = new TextView(this);
                textview.setText("-----");
                textview.setLayoutParams(mw); // match warp wighet
                textview.setGravity(Gravity.CENTER); //gravity center
                // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tr1.addView(textview);
            }
            else
            {
                TableRow.LayoutParams mw1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT,1);

                mw1.setMarginEnd(5);

                textview = new TextView(this);
                textview.setText("Description");
                textview.setLayoutParams(mw1);
                textview.setForeground(getResources().getDrawable(R.drawable.round_blue_prel));
                textview.setTextColor(Color.rgb(41, 182, 246));
                textview.setTypeface(Typeface.DEFAULT_BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(DoctorAppointmentsActivity.this)
                                .setMessage(description)
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        return;
                    }
                });
                tr1.addView(textview);
            }

            textview = new TextView(this);
            textview.setText("Remove");
            textview.setLayoutParams(mw);
            textview.setForeground(getResources().getDrawable(R.drawable.round_red));
            textview.setTextColor(Color.rgb(255,0,0));
            textview.setTypeface(Typeface.DEFAULT_BOLD);
            textview.setGravity(Gravity.CENTER);
            int finalI = i;
            textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference Refs = db.collection("doctors")
                            .document(firebaseAuth.getCurrentUser().getUid())
                            .collection("appointments").document(NEW.get(finalI).get("id").toString());
                    Refs.delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentReference refs = db.collection("patients")
                                    .document(NEW.get(finalI).get("pid").toString())
                                    .collection("appointments").document(NEW.get(finalI).get("id").toString());

                            refs.delete().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    //
                                    tb1.removeView(tr1);
                                    if(tb1.getChildCount()==child1)
                                    {
                                        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                                TableRow.LayoutParams.WRAP_CONTENT,1);

                                        TableRow tr1 = new TableRow(DoctorAppointmentsActivity.this);
                                        tr1.setPaddingRelative(5,5,5,5);
                                        tr1.setGravity(Gravity.CENTER);
                                        tr1.setScrollbarFadingEnabled(false);
                                        tr1.setScrollBarFadeDuration(0);

                                        TextView textview = new TextView(DoctorAppointmentsActivity.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tr1.addView(textview);

                                        textview = new TextView(DoctorAppointmentsActivity.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tr1.addView(textview);

                                        textview = new TextView(DoctorAppointmentsActivity.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tr1.addView(textview);

                                        textview = new TextView(DoctorAppointmentsActivity.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tr1.addView(textview);

                                        textview = new TextView(DoctorAppointmentsActivity.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tr1.addView(textview);

                                        textview = new TextView(DoctorAppointmentsActivity.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tr1.addView(textview);

                                        tb1.addView(tr1);
                                    }
                                    //
                                }
                            });
                        }
                    });
                    return;
                }
            });
            tr1.addView(textview);

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

        sd = Long.parseLong("-1");
        ed = Long.parseLong("-1");
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


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, DoctorHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}