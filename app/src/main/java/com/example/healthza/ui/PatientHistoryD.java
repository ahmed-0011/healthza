package com.example.healthza.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.healthza.ProgressDialog;
import com.example.healthza.R;
import com.example.healthza.Toasty;
import com.example.healthza.models.Patient;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class PatientHistoryD extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    List<String> dataP;
    List<String> idsP;
    ArrayAdapter<String> adapter1;

    private String patientName = "";
    private String patientId = "";
    int patientPOS = 0;

    ImageView set;
    ImageView view;

    String date_ = "";
    int yy = -1 , mm = -1, dd = -1;
    boolean tic = false;
    boolean bttn [];
    List<Integer> i_;

    TableLayout tb;
    ProgressDialog pb;

    int child;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history_d);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getP();
        pb = new ProgressDialog(this);
        tb = findViewById(R.id.idf);
        tb.setStretchAllColumns(true);
        child = tb.getChildCount();

        bttn = new boolean[8];
        for(int i=0; i<bttn.length ; i++)bttn[i]=false;


        set = findViewById(R.id.imageView_);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VS_();
            }
        });

        view = findViewById(R.id.imageView);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viweLog();
            }
        });

    }


    void viweLog()
    {
        boolean ver = false;
        for(int i=0; i<bttn.length;i++)ver = ver || bttn[i];

        if(!ver) {
            AlertDialog.Builder x = new AlertDialog.Builder(this);
            x.setMessage("Please Select Tests.").setTitle("incomplete data")

                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })

                    .setIcon(R.drawable.goo)
                    .setPositiveButtonIcon(getDrawable(R.drawable.yes))

                    .show();
            return;
        }

        cls();

        if(!tic)
        {
            List<String> dates = new ArrayList<String>();
            dates.add(date_);
            P_(dates,0,0);
        }
        else
        {
            printALL();
        }

    }

    void printALL()
    {
        List<String> dates = new ArrayList<String>();

        android.app.ProgressDialog x0= android.app.ProgressDialog.show(PatientHistoryD.this, "",
                "Please Wait TO get Dates...", true);

        getAllDate(dates,1,8,x0);

    }

    void getAllDate(List<String> dates, int t1, int t2, android.app.ProgressDialog finalX)
    {

        if(t1>8 || t1>t2){
            finalX.dismiss();
            Collections.sort(dates);
            int r = dates.size()-1;
            P_(dates,0,r);
            return;}

        String peId =idsP.get(patientPOS);
        String type="";

        switch (t1)
        {
            case 1:
            {
                type = "glucose_test";
                break;
            }

            case 2:
            {
                type = "fbs_test";
                break;
            }

            case 3:
            {
                type = "diabetes_cumulative_test";
                break;
            }

            case 4:
            {
                type = "hypertension_test";
                break;
            }

            case 5:
            {
                type = "cholesterolAndFats_test";
                break;
            }

            case 6:
            {
                type = "liver_test";
                break;
            }

            case 7:
            {
                type = "Kidneys_test";
                break;
            }

            case 8:
            {
                type = "other_test";
                break;
            }
        }

        if(bttn[t1-1]) {
            DocumentReference DRC = db.collection("patients").document(peId)
                    .collection("tests").document(type);

            String finalType = type;
            DRC.get().addOnCompleteListener(task ->
            {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> dates1 = (List<String>) document.get("dates");
                    DocumentReference DRC1 = db.collection("patients").document(peId)
                            .collection("tests").document(finalType);

                    if(dates1!=null) {
                        for (int i = 0; i < dates1.size(); i++) {
                            if (!dates.contains(dates1.get(i))) {
                                dates.add(dates1.get(i));
                            }
                        }
                    }
                    getAllDate(dates, t1 + 1, t2, finalX);
                }
                //
            });
        }
        else{
            getAllDate(dates,t1+1,t2,finalX);
        }

    }

    void P_(List<String> dates,int date,int ed)
    {

        android.app.ProgressDialog x0= android.app.ProgressDialog.show(PatientHistoryD.this, "",
                "Please Wait TO get Data...", true);

        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        mw.setMargins(5,-1,5,-1);

        TableRow tr1 = new TableRow(this);
        tr1.setPaddingRelative(5,5,5,5);
        tr1.setGravity(Gravity.CENTER);

        TextView textview = new TextView(this);
        textview.setText(dates.get(date));
        Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
        textview.setTypeface(tf,Typeface.BOLD);
        textview.setLayoutParams(mw);
        textview.setGravity(Gravity.CENTER);
        textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textview.setTextSize(18);
        textview.setTextColor(Color.rgb(0,255,0));
        tr1.addView(textview);
        tb.addView(tr1);

        pThread(1,8,x0, dates,date,ed);
    }

    void pThread(int t1, int t2, android.app.ProgressDialog finalX, List<String> dates, int date, int ed)
    {
        if(t1>8 || t1>t2){finalX.dismiss();
            if(date>=ed)return;
            else
            if(date < ed){ P_(dates,(date+1),ed);}
            return;}
        String peId = idsP.get(patientPOS);
        CollectionReference DRC = db.collection("patients").document(peId).collection("tests");
        DRC.get().addOnCompleteListener(task -> {
            // Thread t1
            if((t1>=1)&&(t1<=4))
            {
                if(bttn[t1-1])testR(t1,t2,finalX,dates.get(date),dates,date,ed);
                else{ pThread(t1+1,t2,finalX,dates,date,ed);}

            }
            else
            {
                if((t1>=5)&&(t1<=7))
                {
                    if(bttn[t1-1])testRS(t1,t2,finalX,dates.get(date),dates,date,ed);
                    else { pThread(t1 + 1, t2, finalX,dates,date,ed); }
                }
                else
                {
                    if(t1==8){
                        if(bttn[t1-1]){compRS(t1,t2,finalX,dates.get(date),dates,date,ed);}
                        else { pThread(t1 + 1, t2, finalX,dates,date,ed); }
                    }
                }
            }
        });
    }

    void testR(int set, int t2, android.app.ProgressDialog finalX, String date_, List<String> dates, int t, int ed)
    {
        String type="";
        String percent ="";

        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        mw.setMargins(10,-1,10,-1);

        switch (set)
        {
            case 1:
            {
                type = "glucose_test";
                percent = "glucose_percent";

                //test name
                TableRow tr1 = new TableRow(this);
                tr1.setBackgroundColor(Color.rgb(69,151,188));
                tr1.setPaddingRelative(5,5,5,5);
                tr1.setGravity(Gravity.CENTER);

                TextView textview = new TextView(this);
                textview.setText("Glucose Test");
                Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                textview.setTypeface(tf,Typeface.BOLD);
                textview.setLayoutParams(mw);
                textview.setGravity(Gravity.CENTER);
                textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textview.setTextSize(18);
                tr1.addView(textview);
                tb.addView(tr1);

                TableRow tr2 = new TableRow(this);
                tr2.setBackgroundColor(Color.rgb(236,239,241));
                tr2.setPaddingRelative(5,5,5,5);
                tr2.setGravity(Gravity.CENTER);

               /* Typeface tf1 = Typeface.createFromAsset(getAssets(),
                        "fonts/sans-serif.ttf");*/

                textview = new TextView(this);
                textview.setText("Time");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("Glucose %");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);
                tb.addView(tr2);

                break;
            }

            case 2:
            {
                type = "fbs_test";
                percent = "fbs_percent";

                //test name
                TableRow tr1 = new TableRow(this);
                tr1.setBackgroundColor(Color.rgb(69,151,188));
                tr1.setPaddingRelative(5,5,5,5);
                tr1.setGravity(Gravity.CENTER);

                TextView textview = new TextView(this);
                textview.setText("F.B.S Test");
                Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                textview.setTypeface(tf,Typeface.BOLD);
                textview.setLayoutParams(mw);
                textview.setGravity(Gravity.CENTER);
                textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textview.setTextSize(18);
                tr1.addView(textview);
                tb.addView(tr1);

                TableRow tr2 = new TableRow(this);
                tr2.setBackgroundColor(Color.rgb(236,239,241));
                tr2.setPaddingRelative(5,5,5,5);
                tr2.setGravity(Gravity.CENTER);

               /* Typeface tf1 = Typeface.createFromAsset(getAssets(),
                        "fonts/sans-serif.ttf");*/

                textview = new TextView(this);
                textview.setText("Time");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("F.B.S %");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);
                tb.addView(tr2);

                break;
            }

            case 3:
            {
                type = "diabetes_cumulative_test";
                percent = "hbAlc_percent";

                //test name
                TableRow tr1 = new TableRow(this);
                tr1.setBackgroundColor(Color.rgb(69,151,188));
                tr1.setPaddingRelative(5,5,5,5);
                tr1.setGravity(Gravity.CENTER);

                TextView textview = new TextView(this);
                textview.setText("Cumulative Test");
                Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                textview.setTypeface(tf,Typeface.BOLD);
                textview.setLayoutParams(mw);
                textview.setGravity(Gravity.CENTER);
                textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textview.setTextSize(18);
                tr1.addView(textview);
                tb.addView(tr1);

                TableRow tr2 = new TableRow(this);
                tr2.setBackgroundColor(Color.rgb(236,239,241));
                tr2.setPaddingRelative(5,5,5,5);
                tr2.setGravity(Gravity.CENTER);

                /*Typeface tf1 = Typeface.createFromAsset(getAssets(),
                        "fonts/sans-serif.ttf");*/

                textview = new TextView(this);
                textview.setText("Time");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("Hemoglobin A1c %");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);
                tb.addView(tr2);

                break;
            }

            case 4:
            {
                type = "hypertension_test";
                percent = "hypertension_percent";

                //test name
                TableRow tr1 = new TableRow(this);
                tr1.setBackgroundColor(Color.rgb(69,151,188));
                tr1.setPaddingRelative(5,5,5,5);
                tr1.setGravity(Gravity.CENTER);

                TextView textview = new TextView(this);
                textview.setText("HypertensionTest");
                Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                textview.setTypeface(tf,Typeface.BOLD);
                textview.setLayoutParams(mw);
                textview.setGravity(Gravity.CENTER);
                textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textview.setTextSize(18);
                tr1.addView(textview);
                tb.addView(tr1);

                TableRow tr2 = new TableRow(this);
                tr2.setBackgroundColor(Color.rgb(236,239,241));
                tr2.setPaddingRelative(5,5,5,5);
                tr2.setGravity(Gravity.CENTER);

               /* Typeface tf1 = Typeface.createFromAsset(getAssets(),
                        "fonts/sans-serif.ttf");*/

                textview = new TextView(this);
                textview.setText("Time");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("Blood Pressure %");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);
                tb.addView(tr2);

                break;
            }
        }

        String peId = idsP.get(patientPOS);

        CollectionReference testRefs = db.collection("patients").document(peId)
                .collection("tests").document(type).collection(date_);
        String finalPercent = percent;
        String finalType = type;
        testRefs.get().addOnCompleteListener(task ->
        {

            if (task.isSuccessful()) {
                if (task.getResult().size() == 0) {

                    tb.removeView(tb.getChildAt(tb.getChildCount() - 1));
                    TableRow tr1 = new TableRow(this);
                    tr1.setPaddingRelative(5, 5, 5, 5);
                    tr1.setGravity(Gravity.CENTER);

                    TextView textview = new TextView(this);
                    textview.setText("no data");
                    Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                    textview.setTypeface(tf, Typeface.BOLD);
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textview.setTextSize(18);
                    textview.setTextColor(Color.rgb(255, 0, 0));
                    tr1.addView(textview);
                    tb.addView(tr1);

                    //white space
                    TableRow tr = new TableRow(this);
                    tr.setPaddingRelative(5, 5, 5, 5);
                    tr.setGravity(Gravity.CENTER);

                    textview = new TextView(this);
                    textview.setText("");
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    tr.addView(textview);
                    tb.addView(tr);

                    pThread(set+1,t2,finalX,dates,t,ed);

                    return;
                }
                for (QueryDocumentSnapshot document : task.getResult()) {

                    if ((set == 2)
                            && (((boolean) document.get("sub"))
                            == true)) continue;

                    TableRow tr1 = new TableRow(this);
                    tr1.setBackgroundColor(Color.rgb(236, 239, 241));
                    tr1.setPaddingRelative(5, 5, 5, 5);
                    tr1.setGravity(Gravity.CENTER);

                    com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) document.get("timestamp");
                    Date currentDate = timestamp.toDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                    String time = dateFormat.format(currentDate);
                    int l2 = time.lastIndexOf(":");
                    int l1 = time.indexOf(" ");
                    time = time.substring(l1, l2);

                    TextView textview = new TextView(this);
                    textview.setText(time);
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    tr1.addView(textview);

                    textview = new TextView(this);
                    textview.setText("" + document.get(finalPercent));
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    tr1.addView(textview);

                    textview = new TextView(this);
                    textview.setText("Remove");
                    textview.setPaddingRelative(5, 10, 5, 10);
                    textview.setLayoutParams(mw);
                    textview.setForeground(getResources().getDrawable(R.drawable.round_red));
                    textview.setTextColor(Color.rgb(255, 0, 0));
                    textview.setTypeface(Typeface.DEFAULT_BOLD);
                    textview.setGravity(Gravity.CENTER);
                    textview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DocumentReference ter = db.collection("patients").document(peId)
                                    .collection("tests").document(finalType).collection(date_).document(document.getId());
                            ter.delete().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toasty.showText(getApplicationContext(), document.getId() + " IS Deleted...", Toasty.INFORMATION, Toast.LENGTH_SHORT);
                                    tb.removeView(tr1);
                                    if (tb.getChildCount() == child) {

                                        //
                                        TableRow tr1 = new TableRow(PatientHistoryD.this);
                                        tr1.setPaddingRelative(5, 5, 5, 5);
                                        tr1.setGravity(Gravity.CENTER);

                                        TextView textview = new TextView(PatientHistoryD.this);
                                        textview.setText("no data");
                                        Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                                        textview.setTypeface(tf, Typeface.BOLD);
                                        textview.setLayoutParams(mw);
                                        textview.setGravity(Gravity.CENTER);
                                        textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        textview.setTextSize(18);
                                        textview.setTextColor(Color.rgb(255, 0, 0));
                                        tr1.addView(textview);
                                        tb.addView(tr1);

                                        //white space
                                        TableRow tr = new TableRow(PatientHistoryD.this);
                                        tr.setPaddingRelative(5, 5, 5, 5);
                                        tr.setGravity(Gravity.CENTER);

                                        textview = new TextView(PatientHistoryD.this);
                                        textview.setText("");
                                        textview.setLayoutParams(mw);
                                        textview.setGravity(Gravity.CENTER);
                                        tr.addView(textview);
                                        tb.addView(tr);
                                        //
                                        return;

                                    }
                                } else {
                                    Toasty.showText(getApplicationContext(), "Error Occurred while deleting...", Toasty.INFORMATION, Toast.LENGTH_SHORT);

                                }

                            });
                        }
                    });
                    tr1.addView(textview);

                    tb.addView(tr1);
                }

                //white space
                TableRow tr = new TableRow(this);
                tr.setPaddingRelative(5, 5, 5, 5);
                tr.setGravity(Gravity.CENTER);

                TextView textview = new TextView(this);
                textview.setText("");
                textview.setLayoutParams(mw);
                textview.setGravity(Gravity.CENTER);
                tr.addView(textview);
                tb.addView(tr);
            }
            pThread(set+1,t2,finalX,dates,t,ed);
        });
        return;

    }

    void testRS(int set, int t2, android.app.ProgressDialog finalX, String date_, List<String> dates, int t, int ed) {

        String type="";
        String percent [];
        String P2[];
        int len = 0;

        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        mw.setMargins(10,-1,10,-1);

        switch (set) {

            case 5: {
                type = "cholesterolAndFats_test";
                percent = new String[]{"Triglycerid_percent", "LDLCholesterol_percent", "HDLCholesterol_percent","CholesterolTotal_percent"};
                len = percent.length;

                //test name
                TableRow tr1 = new TableRow(this);
                tr1.setBackgroundColor(Color.rgb(69,151,188));
                tr1.setPaddingRelative(5,5,5,5);
                tr1.setGravity(Gravity.CENTER);

                TextView textview = new TextView(this);
                textview.setText("Cholesterol And Fats Test");
                Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                textview.setTypeface(tf,Typeface.BOLD);
                textview.setLayoutParams(mw);
                textview.setGravity(Gravity.CENTER);
                textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textview.setTextSize(14);
                tr1.addView(textview);
                tb.addView(tr1);


                TableRow tr2 = new TableRow(this);
                tr2.setBackgroundColor(Color.rgb(236,239,241));
                tr2.setPaddingRelative(5,5,5,5);
                tr2.setGravity(Gravity.CENTER);

               /* Typeface tf1 = Typeface.createFromAsset(getAssets(),
                        "fonts/sans-serif.ttf");*/


                textview = new TextView(this);
                textview.setText("Elements");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("Percent %");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("Time");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);
                tb.addView(tr2);


                P2  =new String[] {"Triglycerid", "LDL", "HDL","Total"};


                break;
            }

            case 6: {
                type = "liver_test";
                percent = new String[]{"SGOT_percent", "SGPT_percent", "GGT_percent","AlkPhosphatese_percent"};
                len = percent.length;

                //test name
                TableRow tr1 = new TableRow(this);
                tr1.setBackgroundColor(Color.rgb(69,151,188));
                tr1.setPaddingRelative(5,5,5,5);
                tr1.setGravity(Gravity.CENTER);

                TextView textview = new TextView(this);
                textview.setText("Liver Test");
                Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                textview.setTypeface(tf,Typeface.BOLD);
                textview.setLayoutParams(mw);
                textview.setGravity(Gravity.CENTER);
                textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textview.setTextSize(18);
                tr1.addView(textview);
                tb.addView(tr1);

                TableRow tr2 = new TableRow(this);
                tr2.setBackgroundColor(Color.rgb(236,239,241));
                tr2.setPaddingRelative(5,5,5,5);
                tr2.setGravity(Gravity.CENTER);

               /* Typeface tf1 = Typeface.createFromAsset(getAssets(),
                        "fonts/sans-serif.ttf");*/

                textview = new TextView(this);
                textview.setText("Elements");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("Percent %");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("Time");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);
                tb.addView(tr2);

                P2  =new String[] {"S.GOT", "S.GPT", "G.G.T","Alk Phosphatese"};

                break;
            }

            case 7: {
                type = "Kidneys_test";
                percent = new String[]{"UricAcid_percent", "Urea_percent", "Creatinine_percent"};
                len = percent.length;

                //test name
                TableRow tr1 = new TableRow(this);
                tr1.setBackgroundColor(Color.rgb(69,151,188));
                tr1.setPaddingRelative(5,5,5,5);
                tr1.setGravity(Gravity.CENTER);

                TextView textview = new TextView(this);
                textview.setText("Kidneys Test");
                Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                textview.setTypeface(tf,Typeface.BOLD);
                textview.setLayoutParams(mw);
                textview.setGravity(Gravity.CENTER);
                textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textview.setTextSize(18);
                tr1.addView(textview);
                tb.addView(tr1);

                TableRow tr2 = new TableRow(this);
                tr2.setBackgroundColor(Color.rgb(236,239,241));
                tr2.setPaddingRelative(5,5,5,5);
                tr2.setGravity(Gravity.CENTER);

               /* Typeface tf1 = Typeface.createFromAsset(getAssets(),
                        "fonts/sans-serif.ttf");*/

                textview = new TextView(this);
                textview.setText("Elements");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("Percent %");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                textview = new TextView(this);
                textview.setText("Time");
                textview.setLayoutParams(mw);
                textview.setTypeface(null,Typeface.BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(14);
                tr2.addView(textview);

                tb.addView(tr2);

                P2  =new String[] {"Uric Acid", "Urea", "Creatinine"};


                break;
            }

            default:
                throw new IllegalStateException("Unexpected value: " + set);
        }

        String peId = idsP.get(patientPOS);

        CollectionReference testRefs = db.collection("patients").document(peId)
                .collection("tests").document(type).collection(date_);
        String []finalPercent = percent;

        String finalType = type;
        testRefs.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {

                if (task.getResult().size() == 0) {

                    tb.removeView(tb.getChildAt(tb.getChildCount() - 1));
                    TableRow tr1 = new TableRow(this);
                    tr1.setPaddingRelative(5, 5, 5, 5);
                    tr1.setGravity(Gravity.CENTER);

                    TextView textview = new TextView(this);
                    textview.setText("no data");
                    Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                    textview.setTypeface(tf, Typeface.BOLD);
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textview.setTextSize(18);
                    textview.setTextColor(Color.rgb(255, 0, 0));
                    tr1.addView(textview);
                    tb.addView(tr1);

                    //white space
                    TableRow tr = new TableRow(this);
                    tr.setPaddingRelative(5, 5, 5, 5);
                    tr.setGravity(Gravity.CENTER);

                    textview = new TextView(this);
                    textview.setText("");
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    tr.addView(textview);
                    tb.addView(tr);

                    pThread(set+1,t2,finalX,dates,t,ed);

                    return;
                }

                for (QueryDocumentSnapshot document : task.getResult()) {

                    if( ( (set==7) || (set==5) || (set==6) )
                            &&(((boolean)document.get("sub"))
                            ==true)) continue;



                    com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) document.get("timestamp");
                    Date currentDate = timestamp.toDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                    String time = dateFormat.format(currentDate);
                    int l2 = time.lastIndexOf(":");
                    int l1 = time.indexOf(" ");
                    time = time.substring(l1, l2);

                    for(int i=0;i<finalPercent.length;i++) {

                        TableRow tr1 = new TableRow(this);
                        tr1.setBackgroundColor(Color.rgb(236, 239, 241));
                        tr1.setPaddingRelative(5, 5, 5, 5);
                        tr1.setGravity(Gravity.CENTER);

                        TextView textview = new TextView(this);
                        textview.setText(P2[i]);
                        textview.setLayoutParams(mw);
                        textview.setGravity(Gravity.CENTER);
                        tr1.addView(textview);

                        textview = new TextView(this);
                        textview.setText("" + document.get(finalPercent[i]));
                        textview.setLayoutParams(mw);
                        textview.setGravity(Gravity.CENTER);
                        tr1.addView(textview);

                        textview = new TextView(this);
                        textview.setText(time);
                        textview.setLayoutParams(mw);
                        textview.setGravity(Gravity.CENTER);
                        tr1.addView(textview);

                        tb.addView(tr1);
                    }

                    TableRow tr2 = new TableRow(this);
                    tr2.setBackgroundColor(Color.rgb(236, 239, 241));
                    tr2.setPaddingRelative(5, 5, 5, 5);
                    tr2.setGravity(Gravity.CENTER);

                    TextView textview = new TextView(this);
                    textview.setText("Remove");
                    textview.setPaddingRelative(5, 10, 5, 10);
                    textview.setLayoutParams(mw);
                    textview.setForeground(getResources().getDrawable(R.drawable.round_red));
                    textview.setTextColor(Color.rgb(255, 0, 0));
                    textview.setTypeface(Typeface.DEFAULT_BOLD);
                    textview.setGravity(Gravity.CENTER);
                    textview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DocumentReference ter = db.collection("patients").document(peId)
                                    .collection("tests").document(finalType).collection(date_).document(document.getId());
                            ter.delete().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toasty.showText(getApplicationContext(), document.getId() + " IS Deleted...", Toasty.INFORMATION, Toast.LENGTH_SHORT);
                                    tb.removeView(tr2);
                                    if (tb.getChildCount() == child) {
                                        //
                                        TableRow tr1 = new TableRow(PatientHistoryD.this);
                                        tr1.setPaddingRelative(5, 5, 5, 5);
                                        tr1.setGravity(Gravity.CENTER);

                                        TextView textview = new TextView(PatientHistoryD.this);
                                        textview.setText("no data");
                                        Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                                        textview.setTypeface(tf, Typeface.BOLD);
                                        textview.setLayoutParams(mw);
                                        textview.setGravity(Gravity.CENTER);
                                        textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        textview.setTextSize(18);
                                        textview.setTextColor(Color.rgb(255, 0, 0));
                                        tr1.addView(textview);
                                        tb.addView(tr1);

                                        //white space
                                        TableRow tr = new TableRow(PatientHistoryD.this);
                                        tr.setPaddingRelative(5, 5, 5, 5);
                                        tr.setGravity(Gravity.CENTER);

                                        textview = new TextView(PatientHistoryD.this);
                                        textview.setText("");
                                        textview.setLayoutParams(mw);
                                        textview.setGravity(Gravity.CENTER);
                                        tr.addView(textview);
                                        tb.addView(tr);
                                        //
                                        return;

                                    }
                                } else {
                                    Toasty.showText(getApplicationContext(), "Error Occurred while deleting...", Toasty.INFORMATION, Toast.LENGTH_SHORT);

                                }

                            });
                        }
                    });
                    tr2.addView(textview);

                    tb.addView(tr2);
                }

                //white space
                TableRow tr = new TableRow(this);
                tr.setPaddingRelative(5, 5, 5, 5);
                tr.setGravity(Gravity.CENTER);

                TextView textview = new TextView(this);
                textview.setText("      ");
                textview.setLayoutParams(mw);
                textview.setGravity(Gravity.CENTER);
                tr.addView(textview);
                tb.addView(tr);
            }
            pThread(set+1,t2,finalX,dates,t,ed);
        });
        return;

    }

    void compRS(int set, int t2, android.app.ProgressDialog finalX, String date_, List<String> dates, int t, int ed) {


        String peId = idsP.get(patientPOS);

        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        mw.setMargins(10,-1,10,-1);


        String percent []=new String[]{
                "fbs_percent"
                ,"SGOT_percent", "SGPT_percent", "GGT_percent", "AlkPhosphatese_percent"
                ,"UricAcid_percent", "Urea_percent", "Creatinine_percent"
                ,"Triglycerid_percent", "LDLCholesterol_percent", "HDLCholesterol_percent","CholesterolTotal_percent"
                ,"albumin_percent"
                ,"acpTotal_percent"
                ,"calcium_percent"
                ,"chloride_percent"
                ,"sIron_percent"
                ,"tibc_percent"
                ,"acpProstatic_percent"
                ,"amylase_percent"
                ,"potassium_percent"
                ,"sodium_percent"
                ,"2hpps_percent"
                ,"rbs_percent"
                ,"bilirubinTotal_percent"
                ,"bilirubinDirect_percent"
                ,"psa_percent"
                ,"phosphours_percent"
                ,"ldh_percent"
                ,"ckMb_percent"
                ,"cpk_percent"
                ,"tProtein_percent"
                ,"magnesium_percent"
        };

        //test name
        TableRow tr1_ = new TableRow(this);
        tr1_.setBackgroundColor(Color.rgb(69,151,188));
        tr1_.setPaddingRelative(5,5,5,5);
        tr1_.setGravity(Gravity.CENTER);
        tr1_.setLayoutParams(mw);

        TextView textview1 = new TextView(this);
        textview1.setText("Comprehensive Test");
        Typeface tf1 = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
        textview1.setTypeface(tf1,Typeface.BOLD);
        textview1.setLayoutParams(mw);
        textview1.setGravity(Gravity.CENTER);
        textview1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textview1.setTextSize(18);
        tr1_.addView(textview1);
        tb.addView(tr1_);

        TableRow tr2 = new TableRow(this);
        tr2.setBackgroundColor(Color.rgb(236,239,241));
        tr2.setPaddingRelative(5,5,5,5);
        tr2.setGravity(Gravity.CENTER);
        tr2.setLayoutParams(mw);

               /* Typeface tf1 = Typeface.createFromAsset(getAssets(),
                        "fonts/sans-serif.ttf");*/

        textview1 = new TextView(this);
        textview1.setText("Time");
        textview1.setLayoutParams(mw);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setGravity(Gravity.CENTER);
        textview1.setTextSize(14);
        textview1.setWidth(100);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        textview1.setText("Values");
        textview1.setLayoutParams(mw);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setGravity(Gravity.CENTER);
        textview1.setTextSize(14);
        textview1.setWidth(100);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        textview1.setText("Remove");
        textview1.setLayoutParams(mw);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setGravity(Gravity.CENTER);
        textview1.setTextSize(14);
        textview1.setWidth(100);
        textview1.setVisibility(View.INVISIBLE);
        tr2.addView(textview1);

        tb.addView(tr2);


        String finalPercent [] = percent;

        CollectionReference testRefs = db.collection("patients").document(peId)
                .collection("tests").document("other_test").collection(date_);

        testRefs.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {

                if (task.getResult().size() == 0) {

                    tb.removeView(tb.getChildAt(tb.getChildCount() - 1));
                    TableRow tr1 = new TableRow(this);
                    tr1.setPaddingRelative(5, 5, 5, 5);
                    tr1.setGravity(Gravity.CENTER);

                    TextView textview = new TextView(this);
                    textview.setText("no data");
                    Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                    textview.setTypeface(tf, Typeface.BOLD);
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textview.setTextSize(18);
                    textview.setTextColor(Color.rgb(255, 0, 0));
                    tr1.addView(textview);
                    tb.addView(tr1);

                    //white space
                    TableRow tr = new TableRow(this);
                    tr.setPaddingRelative(5, 5, 5, 5);
                    tr.setGravity(Gravity.CENTER);

                    textview = new TextView(this);
                    textview.setText("");
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    tr.addView(textview);
                    tb.addView(tr);

                    pThread(set+1,t2,finalX,dates,t,ed);

                    return;
                }

                final int[] size = {task.getResult().size()};
                final int[] size_ = { 0 };
                for (QueryDocumentSnapshot document : task.getResult())
                {

                    String val [] = new String[33];
                    DocumentReference DRC = db.collection("patients").document(peId)
                            .collection("tests")
                            .document("fbs_test")
                            .collection(date_)
                            .document(document.getId());

                    DRC.get().addOnCompleteListener(task0 ->
                    {
                        if(task0.isSuccessful()) {
                            val[0] = "" + task0.getResult().get(percent[0]);

                            DocumentReference DRC2 = db.collection("patients").document(peId)
                                    .collection("tests")
                                    .document("liver_test")
                                    .collection(date_)
                                    .document(document.getId());

                            DRC2.get().addOnCompleteListener(task2 ->
                            {
                                if(task2.isSuccessful()) {
                                    val[1] = "" + task2.getResult().get(percent[1]);
                                    val[2] = "" + task2.getResult().get(percent[2]);
                                    val[3] = "" + task2.getResult().get(percent[3]);
                                    val[4] = "" + task2.getResult().get(percent[4]);

                                    DocumentReference DRC3 = db.collection("patients").document(peId)
                                            .collection("tests")
                                            .document("Kidneys_test")
                                            .collection(date_)
                                            .document(document.getId());

                                    DRC3.get().addOnCompleteListener(task3 ->
                                    {
                                        if(task3.isSuccessful()) {
                                            val[5] = "" + task3.getResult().get(percent[5]);
                                            val[6] = "" + task3.getResult().get(percent[6]);
                                            val[7] = "" + task3.getResult().get(percent[7]);

                                            DocumentReference DRC4 =db.collection("patients").document(peId)
                                                    .collection("tests")
                                                    .document("cholesterolAndFats_test")
                                                    .collection(date_)
                                                    .document(document.getId());

                                            DRC4.get().addOnCompleteListener(task4 ->
                                            {

                                                if(task4.isSuccessful()) {
                                                    val[8] = "" + task4.getResult().get(percent[8]);
                                                    val[9] = "" + task4.getResult().get(percent[9]);
                                                    val[10] = "" + task4.getResult().get(percent[10]);
                                                    val[11] = "" + task4.getResult().get(percent[11]);

                                                    for (int o=12;o<=32;o++)
                                                    {
                                                        val[o]= "" + document.get(percent[o]);
                                                    }

                                                    //
                                                    TableRow tr1 = new TableRow(this);
                                                    tr1.setBackgroundColor(Color.rgb(236, 239, 241));
                                                    tr1.setPaddingRelative(5, 5, 5, 5);
                                                    tr1.setGravity(Gravity.CENTER);
                                                    tr1.setLayoutParams(mw);

                                                    com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) document.get("timestamp");
                                                    Date currentDate = timestamp.toDate();
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                                                    String time = dateFormat.format(currentDate);
                                                    int l2 = time.lastIndexOf(":");
                                                    int l1 = time.indexOf(" ");
                                                    time = time.substring(l1, l2);

                                                    TextView textview = new TextView(this);
                                                    textview.setText(time);
                                                    textview.setLayoutParams(mw);
                                                    textview.setWidth(100);
                                                    textview.setGravity(Gravity.CENTER);
                                                    tr1.addView(textview);

                                                    textview = new TextView(this);
                                                    textview.setText("View");
                                                    textview.setPaddingRelative(5, 10, 5, 10);
                                                    textview.setLayoutParams(mw);
                                                    textview.setForeground(getResources().getDrawable(R.drawable.round_blue_prel));
                                                    textview.setTextColor(Color.rgb(41, 182, 246));
                                                    textview.setTypeface(Typeface.DEFAULT_BOLD);
                                                    textview.setGravity(Gravity.CENTER);
                                                    textview.setWidth(100);
                                                    textview.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            show(val);
                                                            return;
                                                        }
                                                    });
                                                    tr1.addView(textview);

                                                    textview = new TextView(this);
                                                    textview.setText("Remove");
                                                    textview.setPaddingRelative(5, 10, 5, 10);
                                                    textview.setLayoutParams(mw);
                                                    textview.setForeground(getResources().getDrawable(R.drawable.round_red));
                                                    textview.setTextColor(Color.rgb(255, 0, 0));
                                                    textview.setTypeface(Typeface.DEFAULT_BOLD);
                                                    textview.setGravity(Gravity.CENTER);
                                                    textview.setWidth(100);
                                                    textview.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            // record.put("del",new String[]{peId, "comprehensive",date_,document.getId()});
                                                            String delT [] =new String[]{"fbs_test","liver_test" ,"Kidneys_test","cholesterolAndFats_test", "other_test"};
                                                            if(delT!=null) {
                                                                for (int f = 0; f < delT.length; f++) {
                                                                    DocumentReference testRefs = db.collection("patients").document(peId)
                                                                            .collection("tests").document(delT[f]).collection(date_).document(document.getId());
                                                                    testRefs.delete().addOnCompleteListener(task -> {
                                                                        if (task.isSuccessful()) {

                                                                        }
                                                                        else{
                                                                            Toasty.showText(getApplicationContext(), "Error Occurred while deleting...", Toasty.INFORMATION, Toast.LENGTH_SHORT);
                                                                        }
                                                                    });
                                                                }

                                                                Toasty.showText(getApplicationContext(), document.getId() + " IS Deleted...", Toasty.INFORMATION, Toast.LENGTH_SHORT);
                                                                tb.removeView(tr1);
                                                                if (tb.getChildCount() == child) {
                                                                    //
                                                                    TableRow tr1 = new TableRow(PatientHistoryD.this);
                                                                    tr1.setPaddingRelative(5, 5, 5, 5);
                                                                    tr1.setGravity(Gravity.CENTER);

                                                                    TextView textview = new TextView(PatientHistoryD.this);
                                                                    textview.setText("no data");
                                                                    Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                                                                    textview.setTypeface(tf, Typeface.BOLD);
                                                                    textview.setLayoutParams(mw);
                                                                    textview.setGravity(Gravity.CENTER);
                                                                    textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                                    textview.setTextSize(18);
                                                                    textview.setTextColor(Color.rgb(255, 0, 0));
                                                                    tr1.addView(textview);
                                                                    tb.addView(tr1);

                                                                    //white space
                                                                    TableRow tr = new TableRow(PatientHistoryD.this);
                                                                    tr.setPaddingRelative(5, 5, 5, 5);
                                                                    tr.setGravity(Gravity.CENTER);

                                                                    textview = new TextView(PatientHistoryD.this);
                                                                    textview.setText("");
                                                                    textview.setLayoutParams(mw);
                                                                    textview.setGravity(Gravity.CENTER);
                                                                    tr.addView(textview);
                                                                    tb.addView(tr);
                                                                    //
                                                                    return;

                                                                }
                                                                //
                                                                return;
                                                            }
                                                        }
                                                    });

                                                    tr1.addView(textview);
                                                    tb.addView(tr1);
                                                    //
                                                    //System.out.println("Finish un auto print1____2");
                                                    size_[0]++;

                                                    if(size[0]==size_[0])
                                                    {
                                                        //white space
                                                        TableRow tr = new TableRow(this);
                                                        tr.setPaddingRelative(5, 5, 5, 5);
                                                        tr.setGravity(Gravity.CENTER);

                                                        textview = new TextView(this);
                                                        textview.setText("");
                                                        textview.setLayoutParams(mw);
                                                        textview.setGravity(Gravity.CENTER);
                                                        tr.addView(textview);
                                                        tb.addView(tr);

                                                        pThread(set+1,t2,finalX,dates,t,ed);
                                                    }
                                                }});
                                        }});
                                }});
                        }});
                }




            }
        });

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



    void getP()
    {
        ProgressDialog x = new ProgressDialog(this);
        x.showProgressDialog("Wait to get Patient List...");
        dataP = new ArrayList<String>();
        idsP = new ArrayList<String>();
        String doctorId = firebaseAuth.getCurrentUser().getUid();

        CollectionReference patientsRefs = db.collection("doctors").document(doctorId)
                .collection("patients");
        patientsRefs.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {

                if( task.getResult().size() == 0)
                {
                    return;
                }

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Patient patient = document.toObject(Patient.class);
                    dataP.add(patient.getName()+ " : " + patient.getIdentificationNumber());
                    idsP.add(patient.getPatientId());
                }
                adapter1 = new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, dataP);
                x.dismissProgressDialog();

            }
        });
    }


    int searcH(String pd)
    {
        int i=-1,j=-1;

        for(String dada : dataP)
        { j++;
            if(dada.contains(pd)||dada.equals(pd)){
                i=j;
                return i;
            }
        }

        return i;
    }

    ///
    void VS_()
    {
        if(dataP.size()==0)
        {
            return;
        }
        final boolean[] change = {false};
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.history_dialog2, null);

        Spinner spinnerP;
        EditText searchP;
        Button search;

        spinnerP = view.findViewById(R.id.spinnerPatientd);

        spinnerP.setAdapter(adapter1);

        spinnerP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if(true)return;
                String temp = "" + parent.getItemAtPosition(position).toString();
                patientName = temp.substring(0,temp.indexOf(" : "));
                patientId = temp.substring((temp.indexOf(" : ")+3),temp.length());
                patientPOS = position;
                // ((TextView) spinnerP.getSelectedView()).setTextColor(getResources().getColor(holo_green_dark));
                //!complet
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String temp = "" + parent.getItemAtPosition(patientPOS).toString();
                patientName = temp.substring(0,temp.indexOf(" : "));
                patientId = temp.substring((temp.indexOf(" : ")+3),temp.length());
                // ((TextView) spinnerP.getSelectedView()).setTextColor(getResources().getColor(holo_green_dark));
                //!complet
            }
        });

        spinnerP.setSelection(patientPOS);

        searchP = view.findViewById(R.id.innerPatientd);

        search = view.findViewById(R.id.searchPatientd);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = searchP.getText().toString();

                if(text.isEmpty())
                {
                    AlertDialog.Builder x = new AlertDialog.Builder(PatientHistoryD.this);
                    x.setMessage("Please complete fill the form data.").setTitle("incomplete data")

                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })

                            .setIcon(R.drawable.goo)
                            .setPositiveButtonIcon(getDrawable(R.drawable.yes))

                            .show();
                    return;
                }

                int index=searcH(text);

                if(index == -1)
                {
                    AlertDialog.Builder x = new AlertDialog.Builder(PatientHistoryD.this);
                    x.setMessage("The Patient is not Exist, or you are enter the wrong name or Id.").setTitle("Patient not Exist")

                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })

                            .setIcon(R.drawable.goo)
                            .setPositiveButtonIcon(getDrawable(R.drawable.yes))

                            .show();
                    return;
                }

                spinnerP.setSelection(index);

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms

                        Toasty.showText(PatientHistoryD.this.getApplicationContext(),
                                ""
                                        +( ((TextView) spinnerP.getSelectedView()).getText().toString()
                                        +" is Selected"),
                                Toasty.SUCCESS,Toast.LENGTH_SHORT);
                    }
                }, 100);

            }
        });

        Button btt [];
        btt = new Button[8];

        btt[0] = view.findViewById(R.id.btn1);
        btt[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[0]=!bttn[0];
            }
        });

        btt[1] = view.findViewById(R.id.btn2);
        btt[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[1]=!bttn[1];
            }
        });

        btt[2] = view.findViewById(R.id.btn3);
        btt[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[2]=!bttn[2];
            }
        });

        btt[3] = view.findViewById(R.id.btn4);
        btt[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[3]=!bttn[3];
            }
        });

        btt[4] = view.findViewById(R.id.btn5);
        btt[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[4]=!bttn[4];
            }
        });

        btt[5] = view.findViewById(R.id.btn6);
        btt[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[5]=!bttn[5];
            }
        });

        btt[6] = view.findViewById(R.id.btn7);
        btt[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[6]=!bttn[6];
                // Toasty.makeText(getApplicationContext(),"GGGGG  : "+bttn[6],Toasty.LENGTH_SHORT).show();
            }
        });

        btt[7] = view.findViewById(R.id.btn8);
        btt[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[7]=!bttn[7];
                //Toasty.makeText(getApplicationContext(),"GGGGG  : "+bttn[7],Toasty.LENGTH_SHORT).show();
            }
        });

        MaterialButtonToggleGroup btngg = view.findViewById(R.id.toggleButtonGroup);
        if(i_!=null) {
            for (int i = 0; i < i_.size(); i++) {
                btngg.check(i_.get(i));
                System.out.println("FFFF "+i_.get(i));
//                    bttn[i__.get(i)]=true;
            }
        }


        DatePicker datePicker = view.findViewById(R.id.datePicker);
        if( (yy != -1)
                && (mm != -1)
                && (dd != -1))
        {
            datePicker.init(yy,mm,dd,null);
        }

        if(tic)
        {
            datePicker.setEnabled(false);
            datePicker.setVisibility(View.INVISIBLE);
        }


        CheckBox chx_ = view.findViewById(R.id.checkBox);
        chx_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tic = chx_.isChecked();
                if(tic)
                {
                    datePicker.setEnabled(false);
                    datePicker.setVisibility(View.INVISIBLE);
                }
                else
                {
                    datePicker.setEnabled(true);
                    datePicker.setVisibility(View.VISIBLE);
                }
            }
        });
        chx_.setChecked(tic);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        ImageButton imgButton;
        imgButton =(ImageButton)view.findViewById(R.id.imageButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yy = datePicker.getYear();
                mm = datePicker.getMonth();
                dd = datePicker.getDayOfMonth();
                date_ = yy+"-"+(mm+1)+"-"+dd;

                dialog.dismiss();

                i_ = btngg.getCheckedButtonIds();

                // i__=new ArrayList<Integer>();
                //for (int i = 0 ; i< bttn.length ; i++)if(bttn[i])i__.add(i);

                if(tic && change[0])
                {
                    //  PT_();
                }
                System.out.println(bttn[7]);
                return;
            }
        });

        dialog.show();

        return;
    }

    void show(String []val)
    {

        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.comperhensive_values, null);

        TableLayout tb1 = view.findViewById(R.id.idf);

        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        mw.setMargins(20,-1,20,-1);

               /* Typeface tf1 = Typeface.createFromAsset(getAssets(),
                        "fonts/sans-serif.ttf");*/

        String column[]= {
                "F.B.S"
                ,"S.GOT", "S.GPT", "G.G.T", "Alk.Phosphatase"
                ,"Uric Acid", "Urea", "Creatinine"
                ,"Triglycerid", "LDL Cholesterol", "HDL Cholesterol","Cholesterol Total"
                ,"Albumin"
                ,"Acp.Total"
                ,"Calcium"
                ,"Chloride"
                ,"S.Iron"
                ,"TIBC"
                ,"Acp.Prostatic"
                ,"Amylase"
                ,"Potassium"
                ,"Sodium"
                ,"2HPPs"
                ,"R.B.S"
                ,"Bilirubin Total"
                ,"Bilirubin Direct"
                ,"PSA"
                ,"Phosphours"
                ,"LDH"
                ,"CK-MB"
                ,"CPK"
                ,"T.Protein"
                ,"Magnesium"
        };

        for(int i=0;i<val.length;i++) {

            TableRow tr2 = new TableRow(this);
            tr2.setBackgroundColor(Color.rgb(236,239,241));
            tr2.setPaddingRelative(5,5,5,5);
            tr2.setGravity(Gravity.CENTER);
            tr2.setLayoutParams(mw);

            TextView textview1 = new TextView(this);
            textview1.setText(column[i]+" %");
            textview1.setLayoutParams(mw);
            textview1.setTypeface(null, Typeface.BOLD);
            textview1.setGravity(Gravity.CENTER);
            textview1.setTextSize(14);
            tr2.addView(textview1);

            textview1 = new TextView(this);
            textview1.setText(val[i]);
            textview1.setLayoutParams(mw);
            textview1.setTypeface(null, Typeface.BOLD);
            textview1.setGravity(Gravity.CENTER);
            textview1.setTextSize(14);
            tr2.addView(textview1);

            tb1.addView(tr2);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.show();

        ImageButton imgButton;
        imgButton =(ImageButton)view.findViewById(R.id.imageButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                return;
            }
        });

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