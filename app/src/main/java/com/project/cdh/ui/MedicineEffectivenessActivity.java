package com.project.cdh.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MedicineEffectivenessActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    String patientID;

    List<Map<String, Object>> tests;
    List<Map<String, Object>>  Morning,MorningB,MorningA;
    List<Map<String, Object>>  Afternoon,AfternoonA,AfternoonB;
    List<Map<String, Object>>  Evening,EveningA,EveningB;
    List<Map<String, Object>>  Night,NightA,NightB;

    TableLayout tb;
    int child;

    FloatingActionButton fpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_effectiveness);

        patientID = getIntent().getStringExtra("patientID");
        if(patientID.isEmpty())
        {
            Toasty.showText(getApplicationContext(),"Error occurred!!!\\n patient ID is error",Toasty.ERROR, Toast.LENGTH_LONG);
            finish();
            return;
        }

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tb = findViewById(R.id.idf);
        child = tb.getChildCount();



        getTests();


        fpa = findViewById(R.id.fpa4);
        fpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cls();
                getTests();
            }
        });
    }

    void getTests()
    {
        tests = new ArrayList<Map<String, Object>>();


        ProgressDialog x= ProgressDialog.show(this, "",
                "Please Wait TO get and Classify GlucoseTest...", true);
        DocumentReference de = db.collection("patients").document(patientID)
                .collection("tests").document("glucose_test");

        de.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    List<String> dates = (List<String>) task.getResult().get("dates");

                    if(dates!=null
                            && dates.size()!=0)
                    {
                        int finalS=dates.size()-1;
                        for(int i =0;i<dates.size();i++) {
                            int finalI = i;
                            CollectionReference drc = db.collection("patients").document(patientID)
                                    .collection("tests").document("glucose_test").collection(dates.get(i));

                            drc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task1) {

                                    for (QueryDocumentSnapshot document : task1.getResult()) {
                                        Map<String, Object> ad =document.getData();
                                        ad.put("ids",document.getId());
                                        tests.add(ad);
                                    }

                                    if(finalI ==finalS){
                                        // System.out.println("_____>"+finalI+" -------->"+finalS+" ---hh");
                                        CategoryDayParts(tests,x);
                                    }
                                }
                            });
                        }
                    }
                    // x.dismiss();
                }
            }
        });

    }


    void CategoryDayParts(List<Map<String, Object>> all, ProgressDialog x) {

        Morning = new ArrayList<Map<String, Object>>();
        Afternoon = new ArrayList<Map<String, Object>>();
        Evening = new ArrayList<Map<String, Object>>();
        Night = new ArrayList<Map<String, Object>>();
        DocumentReference de = db.collection("patients").document(patientID)
                .collection("tests").document("glucose_test");
        de.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    // System.out.println("-------------------------> ooooooooooooooooo ");
                    for (int i = 0; i < all.size(); i++) {

                        com.google.firebase.Timestamp timestamp =(com.google.firebase.Timestamp) all.get(i).get("timestamp");
                        Date currentDate = timestamp.toDate();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                        String time = dateFormat.format(currentDate);
                        // System.out.println(" ______________TimeStamp " +time);
                        int l2 = time.lastIndexOf(":");
                        int l1 = time.indexOf(" ");
                        time = time.substring(l1+1, l2);
                        // System.out.println(" ______________Time " +time);
                        if ((time.compareTo("05:00") >= 0)
                                && (time.compareTo("11:59") <= -1)) {
                            // System.out.println(" ______________Morning " +time);
                            Morning.add(all.get(i));
                        }   else {
                            if ((time.compareTo("12:00") >= 0)
                                    && (time.compareTo("16:59") <= -1)) {
                                // System.out.println(" ______________Afternoon "+time);
                                Afternoon.add(all.get(i));
                            } else {
                                if ((time.compareTo("17:00") >= 0)
                                        && (time.compareTo("20:59") <= -1)) {
                                    // System.out.println(" ______________Evening "+time);
                                    Evening.add(all.get(i));
                                } else {
                                    if (((time.compareTo("21:00") >= 0)
                                            && (time.compareTo("23:59") <= -1))
                                            ||
                                            ((time.compareTo("00:00") >= 0)
                                                    && (time.compareTo("04:59") <= -1))
                                    )
                                    {
                                        // System.out.println(" ______________Night "+time);
                                        Night.add(all.get(i));
                                    }
                                    else
                                    {
                                        // System.out.println(" ______________error "+time);
                                    }
                                }
                            }
                        }
                    }
                    CategoryDrugs(x);
                }
            }});
    }

    void CategoryDrugs( ProgressDialog x) {

        //  System.out.println("--------------> ggggg");
        CollectionReference de = db.collection("patients").document(patientID)
                .collection("medicines");

        de.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        catDrug(0,document,x);
                    }
                    if(tb.getChildCount()==child)
                    {


                        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.MATCH_PARENT, 1);
                        mw.topMargin=5;
                        TableRow tr = new TableRow(MedicineEffectivenessActivity.this);
                        tr.setGravity(Gravity.CENTER);
                        tr.setLayoutParams(mw);

                        mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.MATCH_PARENT, 1);
                        mw.topMargin=10;
                        TextView textview = new TextView(MedicineEffectivenessActivity.this);
                        textview.setText("NO Medicines_Drugs Active");
                        textview.setTextColor(Color.rgb(255,0,0));
                        Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                        textview.setTypeface(tf,Typeface.BOLD);
                        textview.setLayoutParams(mw);
                        textview.setGravity(Gravity.CENTER);
                        textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        tr.addView(textview);

                        tb.addView(tr);
                    }
                }

            }
        });


    }


    void catDrug(int p,QueryDocumentSnapshot document, ProgressDialog x)
    {
        System.out.println(" catDrug P ----> "+p);
        List<Map<String, Object>> part = null ,partA = null ,partB = null;

        switch(p)
        {
            case 0:
            {
                MorningA = new ArrayList<Map<String, Object>>();
                AfternoonA = new ArrayList<Map<String, Object>>();
                EveningA = new ArrayList<Map<String, Object>>();
                NightA = new ArrayList<Map<String, Object>>();

                MorningB = new ArrayList<Map<String, Object>>();
                AfternoonB = new ArrayList<Map<String, Object>>();
                EveningB = new ArrayList<Map<String, Object>>();
                NightB = new ArrayList<Map<String, Object>>();

                break;
            }

            case 1:
            {
                part = Morning;
                partA = MorningA;
                partB = MorningB;
                break;
            }

            case 2:
            {
                part = Afternoon;
                partA = AfternoonA;
                partB = AfternoonB;
                break;
            }

            case 3:
            {
                part = Evening;
                partA = EveningA;
                partB = EveningB;
                break;
            }

            case 4:
            {
                part = Night;
                partA = NightA;
                partB = NightB;
                break;
            }

            default:
            {
                printE(document,x);
                return;
            }
        };

        if(p==0)
        {
            catDrug(++p,document,x);
            return;
        }

        if(p>4)
        {     //System.out.println(document.getString("medicine_name")+"----> print");
            printE(document,x);
            return;
        }

        List<Map<String, Object>> finalPart = part;
        List<Map<String, Object>> finalPartB = partB;
        List<Map<String, Object>> finalPartA = partA;
System.out.println(p+ "------>"+ document.getBoolean("status"));
        if(document.getBoolean("status"))
        {
            System.out.println(document.getString("medicine_name")+"---->"+document.getString("prescription_date"));
            System.out.println("-------------->"+finalPart.size());
            for(int i = 0; i< finalPart.size(); i++)
            {                    ArrayList<String> old = (ArrayList<String>) document.get("old_prescription_date");
                String start = document.getString("prescription_date");
                old.add(start);

                Collections.sort(old);
                // Collections.reverse(old);
                String befor = old.get(0);
                Map<String, Object> ad = finalPart.get(i);

                com.google.firebase.Timestamp timestamp =(com.google.firebase.Timestamp) ad.get("timestamp");
                Date currentDate = timestamp.toDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
                String date_ = dateFormat.format(currentDate);

                if(date_.compareTo(befor)<=-1) finalPartB.add(ad);
                else  if(date_.compareTo(start)>=0) finalPartA.add(ad);

            }
            catDrug(++p,document,x);
        }
        x.dismiss();
        return;
    }


    void printE(QueryDocumentSnapshot document, ProgressDialog x)
    {

        double ae[] = new double[]{0,0,0,0};
        double be[] = new double[]{0,0,0,0};

        ArrayList<Map<String, Object>>[] partA = new ArrayList[]{(ArrayList) MorningA, (ArrayList) AfternoonA, (ArrayList) EveningA, (ArrayList) NightA};
        ArrayList<Map<String, Object>>[] partB = new ArrayList[]{(ArrayList) MorningB, (ArrayList) AfternoonB, (ArrayList) EveningB, (ArrayList) NightB};

        for(int i=0;i<ae.length;i++)
        {

            for(int j =0;j<partA[i].size();j++)
            {
                ae[i] = ae[i]+((double)partA[i].get(j).get("glucose_percent"));
            }

            if(partA[i].size()>0) ae[i]= ae[i]/partA[i].size();
            else if(partA[i].size()==0) ae[i]=Double.MIN_VALUE;
        }

        for(int i=0;i<be.length;i++)
        {

            for(int j =0;j<partB[i].size();j++)
            {
                be[i] = be[i]+((double)partB[i].get(j).get("glucose_percent"));
            }

            if(partB[i].size()>0) be[i]= be[i]/partB[i].size();
            else if(partB[i].size()==0) be[i]=Double.MIN_VALUE;
        }

        // System.out.println(document.getString("medicine_name")+"----> print");

        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT, 1);
        mw.topMargin=5;
        TableRow tr = new TableRow(this);
        tr.setGravity(Gravity.CENTER);
        tr.setLayoutParams(mw);

        mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT, 1);
        mw.topMargin=10;
        TextView textview = new TextView(this);
        textview.setText(document.getString("medicine_name"));
        Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
        textview.setTypeface(tf,Typeface.BOLD);
        textview.setLayoutParams(mw);
        textview.setGravity(Gravity.CENTER);
        textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textview.setTextSize(10);
        tr.addView(textview);
        tb.addView(tr);

        //
        mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT, 1);
        mw.topMargin=5;
        tr = new TableRow(this);
        tr.setGravity(Gravity.CENTER);
        tr.setLayoutParams(mw);

        TableRow.LayoutParams mw1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1);
        TableLayout tb1 = new TableLayout(this);
        tb1.setLayoutParams(mw1);

        TableRow  tr2 = new TableRow(this);
        tr2.setBackgroundColor(Color.rgb(227,242,253));
        tr2.setGravity(Gravity.CENTER);

        mw1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT, 1);
        TextView textview1 = new TextView(this);
        textview1.setText("Medicine");
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        textview1.setText("Morning");
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        textview1.setText("Afternoon");
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        textview1.setText("Evening");
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        textview1.setText("Night");
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);
        tb1.addView(tr2);

        //

        tr2 = new TableRow(this);
        tr2.setBackgroundColor(Color.rgb(197,225,165));
        tr2.setGravity(Gravity.CENTER);

        mw1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT, 1);
        textview1 = new TextView(this);
        textview1.setText("After");
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        if(ae[0]==Double.MIN_VALUE) textview1.setText(null);
        else textview1.setText(""+ae[0]);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        if(ae[1]==Double.MIN_VALUE) textview1.setText(null);
        else textview1.setText(""+ae[1]);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        if(ae[2]==Double.MIN_VALUE) textview1.setText(null);
        else textview1.setText(""+ae[2]);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        if(ae[3]==Double.MIN_VALUE) textview1.setText(null);
        else textview1.setText(""+ae[3]);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);
        tb1.addView(tr2);

        //

        tr2 = new TableRow(this);
        tr2.setBackgroundColor(Color.rgb(241,213,205));
        tr2.setGravity(Gravity.CENTER);

        mw1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT, 1);
        textview1 = new TextView(this);
        textview1.setText("Before");
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        if(be[0]==Double.MIN_VALUE) textview1.setText(null);
        else textview1.setText(""+be[0]);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        if(be[1]==Double.MIN_VALUE) textview1.setText(null);
        else textview1.setText(""+be[1]);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        if(be[2]==Double.MIN_VALUE) textview1.setText(null);
        else textview1.setText(""+be[2]);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        if(be[3]==Double.MIN_VALUE) textview1.setText(null);
        else textview1.setText(""+be[3]);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setLayoutParams(mw1);
        textview1.setGravity(Gravity.CENTER);
        tr2.addView(textview1);
        tb1.addView(tr2);

        tr.addView(tb1);
        tb.addView(tr);

        x.dismiss();

    }

    void cls()
    {
        int chd = tb.getChildCount() - 1;
        while(chd != (child-1))
        {
            tb.removeView(tb.getChildAt(chd));
            chd--;
        }
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