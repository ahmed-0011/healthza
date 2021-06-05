package com.project.cdh.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.CalendarWeekDay;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.project.cdh.DrawerUtil;
import com.project.cdh.ProgressDialog;
import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.project.cdh.models.Patient;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;


public class DoctorPatientMedicalHistoryActivity extends AppCompatActivity
{
    ImageView set;

    String date_ = "";
    int yy = -1 , mm = -1, dd = -1;
    boolean tic = false;
    boolean bttn [];
    List<Integer> i_;

    boolean  change_;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    TableLayout tb;
    android.app.ProgressDialog pb;

    int child;

    boolean mod = false;
    Long sd,ed;
    private Long selectedDate;
    private String pickedDate;

    List<String> dates1;
    List<String> dates2;

    List<Calendar> datesM;

    Long re;

    List<String> dataP;
    List<String> idsP;
    ArrayAdapter<String> adapter1;

    private String patientName = "";
    private String patientId = "";
    int patientPOS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patient_medical_history);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getP();

        sd=Long.parseLong("-1");
        ed=Long.parseLong("-1");
        re=Long.parseLong("-1");


        datesM = new ArrayList<Calendar>();

        pb = new android.app.ProgressDialog(this);
        tb = findViewById(R.id.idf);
        tb.setStretchAllColumns(true);
        child = tb.getChildCount();

        bttn = new boolean[8];
        for(int i=0; i<bttn.length ; i++)bttn[i]=false;

        set = findViewById(R.id.imageView_);
        set.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                VS_();
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
            if(dates1!=null) {
                List<String> dates = dates1;
                //dates.add(date_);
                P_(dates, 0, dates1.size()-1);
            }
        }
        else
        {
            printALL();
        }

    }

    void printALL()
    {
        List<String> dates = new ArrayList<String>();

        android.app.ProgressDialog x0= android.app.ProgressDialog.show(DoctorPatientMedicalHistoryActivity.this, "",
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
        if(dates.size()==0
        || dates == null ) {  // tb.removeView(tb.getChildAt(tb.getChildCount() - 1));

            TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,1);
            mw.topMargin=5;
            mw.bottomMargin=5;

            TableRow tr1 = new TableRow(this);
            //  tr1.setPaddingRelative(5, 5, 5, 5);
            tr1.setGravity(Gravity.CENTER);

            TextView textview = new TextView(this);
            textview.setText("no Tests");
            Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
            textview.setTypeface(tf, Typeface.BOLD);
            textview.setLayoutParams(mw);
            textview.setGravity(Gravity.CENTER);
            // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textview.setTextSize(18);
            textview.setTextColor(Color.rgb(255, 0, 0));
            tr1.addView(textview);
            tb.addView(tr1);

            //white space
            TableRow tr = new TableRow(this);
            // tr.setPaddingRelative(5, 5, 5, 5);
            tr.setGravity(Gravity.CENTER);

            textview = new TextView(this);
            textview.setText("");
            textview.setLayoutParams(mw);
            textview.setGravity(Gravity.CENTER);
            tr.addView(textview);
            tb.addView(tr);
            return;
        }
        android.app.ProgressDialog x0= android.app.ProgressDialog.show(DoctorPatientMedicalHistoryActivity.this, "",
                "Please Wait TO get Data...", true);

        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,1);
        mw.topMargin=5;
        //mw.setMargins(5,-1,5,-1);

        TableRow tr1 = new TableRow(this);
        // tr1.setPaddingRelative(5,5,5,5);
        tr1.setGravity(Gravity.CENTER);

        TextView textview = new TextView(this);
        textview.setText(dates.get(date));
        Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
        textview.setTypeface(tf,Typeface.BOLD);
        textview.setLayoutParams(mw);
        textview.setGravity(Gravity.CENTER);
        //textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
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

        String testName="";
        String testD="";

        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,1);
        mw.topMargin=5;
        mw.bottomMargin=5;
        // mw.setMargins(10,-1,10,-1);

        TableLayout tb1 = new TableLayout(this);
        tb1.setLayoutParams(mw);

        switch (set)
        {
            case 1:
            {
                type = "glucose_test";
                percent = "glucose_percent";
                testName = "Glucose Test";
                testD = "Glucose (mg\\dl)";
                break;
            }

            case 2:
            {
                type = "fbs_test";
                percent = "fbs_percent";
                testName = "F.B.S Test";
                testD = "F.B.S (mg\\dl)";
                break;
            }

            case 3:
            {
                type = "diabetes_cumulative_test";
                percent = "hbAlc_percent";
                testName = "Cumulative Test";
                testD = "Hemoglobin A1c (mmol/mol)";
                break;
            }

            case 4:
            {
                type = "hypertension_test";
                percent = "hypertension_percent";
                testName = "Hypertension Test";
                testD = "Blood Pressure (mmHg)";
                break;
            }
        }

        //<test name
        TableRow tr1N = new TableRow(this);
        tr1N.setBackgroundColor(Color.rgb(69,151,188));
        //tr1.setPaddingRelative(5,5,5,5);
        tr1N.setGravity(Gravity.CENTER);

        TextView textviewn = new TextView(this);
        textviewn.setText(testName);
        Typeface tfN = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
        textviewn.setTypeface(tfN,Typeface.BOLD);
        textviewn.setLayoutParams(mw);
        textviewn.setGravity(Gravity.CENTER);
        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textviewn.setTextSize(18);
        tr1N.addView(textviewn);
        tb.addView(tr1N);
        //test name>

        //< test d
        TableRow tr2N = new TableRow(this);
        tr2N.setBackgroundColor(Color.rgb(236,239,241));
        //tr2N.setPaddingRelative(5,5,5,5);
        tr2N.setGravity(Gravity.CENTER);

               /* Typeface tf1 = Typeface.createFromAsset(getAssets(),
                        "fonts/sans-serif.ttf");*/

        textviewn = new TextView(this);
        textviewn.setText("Time");
        textviewn.setLayoutParams(mw);
        textviewn.setTypeface(null,Typeface.BOLD);
        textviewn.setGravity(Gravity.CENTER);
        // textview.setTextSize(14);
        tr2N.addView(textviewn);

        textviewn = new TextView(this);
        textviewn.setText(testD);
        textviewn.setLayoutParams(mw);
        textviewn.setTypeface(null,Typeface.BOLD);
        textviewn.setGravity(Gravity.CENTER);
        //textview.setTextSize(14);
        tr2N.addView(textviewn);

        textviewn = new TextView(this);
        textviewn.setText("");
        textviewn.setLayoutParams(mw);
        textviewn.setTypeface(null,Typeface.BOLD);
        textviewn.setGravity(Gravity.CENTER);
        //textview.setTextSize(14);
        tr2N.addView(textviewn);

        tb1.addView(tr2N);
        //test d>

        String peId = idsP.get(patientPOS);

        CollectionReference testRefs = db.collection("patients").document(peId)
                .collection("tests").document(type).collection(date_);
        String finalPercent = percent;
        String finalType = type;
        testRefs.get().addOnCompleteListener(task ->
        {

            if (task.isSuccessful()) {
                if (task.getResult().size() == 0) {

                    // tb.removeView(tb.getChildAt(tb.getChildCount() - 1));
                    TableRow tr1 = new TableRow(this);
                    //  tr1.setPaddingRelative(5, 5, 5, 5);
                    tr1.setGravity(Gravity.CENTER);

                    tr1.addView(tb1);
                    tb.addView(tr1);

                    tr1 = new TableRow(this);
                    //  tr1.setPaddingRelative(5, 5, 5, 5);
                    tr1.setGravity(Gravity.CENTER);

                    TextView textview = new TextView(this);
                    textview.setText("no data");
                    Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                    textview.setTypeface(tf, Typeface.BOLD);
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textview.setTextSize(18);
                    textview.setTextColor(Color.rgb(255, 0, 0));
                    tr1.addView(textview);
                    tb.addView(tr1);

                    //white space
                    TableRow tr = new TableRow(this);
                    // tr.setPaddingRelative(5, 5, 5, 5);
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
                    //tr1.setPaddingRelative(5, 5, 5, 5);
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
                                    tb1.removeView(tr1);
                                    if (tb1.getChildCount() == 1 ) {

                                        //  tb1.removeView(tb1.getChildAt(tb1.getChildCount() - 1));
                                        TableRow tr1 = new TableRow(DoctorPatientMedicalHistoryActivity.this);
                                        //  tr1.setPaddingRelative(5, 5, 5, 5);
                                        tr1.setGravity(Gravity.CENTER);

                                        TextView textview = new TextView(DoctorPatientMedicalHistoryActivity.this);
                                        textview.setText("no data");
                                        Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                                        textview.setTypeface(tf, Typeface.BOLD);
                                        textview.setLayoutParams(mw);
                                        textview.setGravity(Gravity.CENTER);
                                        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        textview.setTextSize(18);
                                        textview.setTextColor(Color.rgb(255, 0, 0));
                                        tr1.addView(textview);
                                        tb1.addView(tr1);

                                        //white space
                                        TableRow tr = new TableRow(DoctorPatientMedicalHistoryActivity.this);
                                        // tr.setPaddingRelative(5, 5, 5, 5);
                                        tr.setGravity(Gravity.CENTER);

                                        textview = new TextView(DoctorPatientMedicalHistoryActivity.this);
                                        textview.setText("");
                                        textview.setLayoutParams(mw);
                                        textview.setGravity(Gravity.CENTER);
                                        tr.addView(textview);
                                        tb1.addView(tr);
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

                    tb1.addView(tr1);
                }


                TableRow tr_ = new TableRow(this);
                tr_.setGravity(Gravity.CENTER);

                tr_.addView(tb1);
                tb.addView(tr_);

                //white space
                TableRow tr = new TableRow(this);
                //tr.setPaddingRelative(5, 5, 5, 5);
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
        String testName="";
        int len = 0;

        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,1);
        mw.topMargin=5;
        mw.bottomMargin=5;
        //mw.setMargins(10,-1,10,-1);

        TableLayout tb_ = new TableLayout(this);
        tb_.setLayoutParams(mw);

        switch (set) {

            case 5: {
                type = "cholesterolAndFats_test";
                percent = new String[]{"Triglycerid_percent", "LDLCholesterol_percent", "HDLCholesterol_percent","CholesterolTotal_percent"};
                len = percent.length;

                testName = "Cholesterol And Fats Test";
                P2  =new String[] {"Triglycerid (mg\\dl)", "LDL (u\\l)", "HDL (u\\l)","Total (mg\\dl)"};

                break;
            }

            case 6: {
                type = "liver_test";
                percent = new String[]{"SGOT_percent", "SGPT_percent", "GGT_percent","AlkPhosphatese_percent"};
                len = percent.length;

                testName = "Liver Test";
                P2  =new String[] {"S.GOT (u\\l)", "S.GPT (u\\l)", "G.G.T (u\\l)","Alk Phosphatese (u\\l)"};

                break;
            }

            case 7: {
                type = "Kidneys_test";
                percent = new String[]{"UricAcid_percent", "Urea_percent", "Creatinine_percent"};
                len = percent.length;

                testName = "Kidneys Test";
                P2  =new String[] {"Uric Acid (mg\\dl)", "Urea (mg\\dl)", "Creatinine (mg\\dl)"};


                break;
            }

            default:
                throw new IllegalStateException("Unexpected value: " + set);
        }

        //<test name
        TableRow tr1N = new TableRow(this);
        tr1N.setBackgroundColor(Color.rgb(69,151,188));
        //tr1.setPaddingRelative(5,5,5,5);
        tr1N.setGravity(Gravity.CENTER);

        TextView textviewn = new TextView(this);
        textviewn.setText(testName);
        Typeface tfN = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
        textviewn.setTypeface(tfN,Typeface.BOLD);
        textviewn.setLayoutParams(mw);
        textviewn.setGravity(Gravity.CENTER);
        // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textviewn.setTextSize(18);
        tr1N.addView(textviewn);
        tb_.addView(tr1N);
        //test name>


        String peId = idsP.get(patientPOS);

        CollectionReference testRefs = db.collection("patients").document(peId)
                .collection("tests").document(type).collection(date_);
        String []finalPercent = percent;

        String finalType = type;
        testRefs.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {

                if (task.getResult().size() == 0) {

                    TableLayout tb1 = new TableLayout(this);
                    tb1.setLayoutParams(mw);

                    TableRow tr1 = new TableRow(this);
                    tr1.setBackgroundColor(Color.rgb(236, 239, 241));
                    //tr1.setPaddingRelative(5, 5, 5, 5);
                    tr1.setGravity(Gravity.CENTER);

                    for(int i=0;i<finalPercent.length;i++) {

                        TextView textview = new TextView(this);
                        textview.setTextColor(Color.rgb(25,25,92));
                        textview.setText(P2[i]);
                        textview.setLayoutParams(mw);
                        textview.setGravity(Gravity.CENTER);
                        tr1.addView(textview);
                    }

                    tb1.addView(tr1);

                    TableRow tr3 = new TableRow(this);
                    tr3.setGravity(Gravity.CENTER);

                    tr3.addView(tb1);
                    tb_.addView(tr3);

                    //tb.removeView(tb.getChildAt(tb.getChildCount() - 1));
                    tr1 = new TableRow(this);
                    //tr1.setPaddingRelative(5, 5, 5, 5);
                    tr1.setGravity(Gravity.CENTER);

                    TextView textview = new TextView(this);
                    textview.setText("no data");
                    Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                    textview.setTypeface(tf, Typeface.BOLD);
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textview.setTextSize(18);
                    textview.setTextColor(Color.rgb(255, 0, 0));
                    tr1.addView(textview);
                    tb_.addView(tr1);

                    tr3 = new TableRow(this);
                    tr3.setGravity(Gravity.CENTER);
                    tr3.addView(tb_);
                    tb.addView(tr3);

                    //white space
                    TableRow tr = new TableRow(this);
                    //tr.setPaddingRelative(5, 5, 5, 5);
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

                    TableLayout tb1 = new TableLayout(this);
                    tb1.setLayoutParams(mw);

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
                        //tr1.setPaddingRelative(5, 5, 5, 5);
                        tr1.setGravity(Gravity.CENTER);

                        TextView textview = new TextView(this);
                        textview.setTextColor(Color.rgb(25,25,92));
                        textview.setText(P2[i]);
                        textview.setLayoutParams(mw);
                        textview.setGravity(Gravity.CENTER);
                        tr1.addView(textview);

                        textview = new TextView(this);
                        textview.setText("" + document.get(finalPercent[i]));
                        textview.setLayoutParams(mw);
                        textview.setGravity(Gravity.CENTER);
                        tr1.addView(textview);

                        tb1.addView(tr1);
                    }

                    TableRow tr3 = new TableRow(this);
                    tr3.setGravity(Gravity.CENTER);

                    TableRow tr2 = new TableRow(this);
                    tr2.setBackgroundColor(Color.rgb(236, 239, 241));
                    //  tr2.setPaddingRelative(5, 5, 5, 5);
                    tr2.setGravity(Gravity.CENTER);


                    TextView textview = new TextView(this);
                    textview.setTextColor(Color.rgb(25,92,92));
                    textview.setText("Time : " + time);
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    tr2.addView(textview);

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
                                    tb_.removeView(tr3);
                                    if (tb_.getChildCount() == 1) {
                                        //

                                        TableLayout tb1 = new TableLayout(DoctorPatientMedicalHistoryActivity.this);
                                        tb1.setLayoutParams(mw);

                                        TableRow tr1 = new TableRow(DoctorPatientMedicalHistoryActivity.this);
                                        //tr1.setPaddingRelative(5, 5, 5, 5);
                                        tr1.setGravity(Gravity.CENTER);

                                        TextView textview = new TextView(DoctorPatientMedicalHistoryActivity.this);
                                        textview.setText("no data");
                                        Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                                        textview.setTypeface(tf, Typeface.BOLD);
                                        textview.setLayoutParams(mw);
                                        textview.setGravity(Gravity.CENTER);
                                        //  textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        textview.setTextSize(18);
                                        textview.setTextColor(Color.rgb(255, 0, 0));
                                        tr1.addView(textview);
                                        tb_.addView(tr1);
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

                    tb1.addView(tr2);

                    tr3.addView(tb1);

                    tb_.addView(tr3);
                }

                TableRow tr3 = new TableRow(this);
                tr3.setGravity(Gravity.CENTER);
                tr3.addView(tb_);
                tb.addView(tr3);

                //white space
                TableRow tr = new TableRow(this);
                // tr.setPaddingRelative(5, 5, 5, 5);
                tr.setGravity(Gravity.CENTER);

                TextView textview = new TextView(this);
                textview.setText(" ");
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
                TableRow.LayoutParams.WRAP_CONTENT,1);
        mw.topMargin=5;
        mw.bottomMargin=5;
        //mw.leftMargin=5;
        //  mw.rightMargin=5;

        TableRow.LayoutParams mw1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,1);
        mw1.topMargin=5;
        mw1.bottomMargin=5;
        mw1.leftMargin=10;
        mw1.rightMargin=10;


        TableLayout tb1 = new TableLayout(this);
        tb1.setLayoutParams(mw);


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
        // tr1_.setPaddingRelative(5,5,5,5);
        tr1_.setGravity(Gravity.CENTER);
        tr1_.setLayoutParams(mw);

        TextView textview1 = new TextView(this);
        textview1.setText("Comprehensive Test");
        Typeface tf1 = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
        textview1.setTypeface(tf1,Typeface.BOLD);
        textview1.setLayoutParams(mw);
        textview1.setGravity(Gravity.CENTER);
        //textview1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textview1.setTextSize(18);
        tr1_.addView(textview1);
        tb.addView(tr1_);

        TableRow tr2 = new TableRow(this);
        tr2.setBackgroundColor(Color.rgb(236,239,241));
        // tr2.setPaddingRelative(5,5,5,5);
        tr2.setGravity(Gravity.CENTER);
        tr2.setLayoutParams(mw);

               /* Typeface tf1 = Typeface.createFromAsset(getAssets(),
                        "fonts/sans-serif.ttf");*/

        textview1 = new TextView(this);
        textview1.setText("Time");
        textview1.setLayoutParams(mw1);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setGravity(Gravity.CENTER);
        // textview1.setTextSize(14);
        // textview1.setWidth(100);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        textview1.setText("Values");
        textview1.setLayoutParams(mw1);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setGravity(Gravity.CENTER);
        // textview1.setTextSize(14);
        //textview1.setWidth(100);
        tr2.addView(textview1);

        textview1 = new TextView(this);
        textview1.setText("Remove");
        textview1.setLayoutParams(mw1);
        textview1.setTypeface(null,Typeface.BOLD);
        textview1.setGravity(Gravity.CENTER);
        // textview1.setTextSize(14);
        // textview1.setWidth(100);
        textview1.setVisibility(View.INVISIBLE);
        tr2.addView(textview1);


        /*TableLayout tb_ = new TableLayout(this);
        tb_.setLayoutParams(mw);*/

        tb1.addView(tr2);

        /*TableRow tr_ = new TableRow(this);
        tr_.setGravity(Gravity.CENTER);
        tr_.addView(tb_);
        tb1.addView(tr_);*/


        String finalPercent [] = percent;

        CollectionReference testRefs = db.collection("patients").document(peId)
                .collection("tests").document("other_test").collection(date_);

        testRefs.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {

                if (task.getResult().size() == 0) {


                    TableLayout tb1__ = new TableLayout(this);
                    tb1__.setLayoutParams(mw);

                    TableRow tr1 = new TableRow(this);
                    //tr1.setPaddingRelative(5, 5, 5, 5);
                    tr1.setGravity(Gravity.CENTER);

                    TextView textview = new TextView(this);
                    textview.setText("no data");
                    Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                    textview.setTypeface(tf, Typeface.BOLD);
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    //textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textview.setTextSize(18);
                    textview.setTextColor(Color.rgb(255, 0, 0));
                    tr1.addView(textview);

                    tb1__.addView(tr1);

                    TableRow tr3 = new TableRow(this);
                    tr3.setGravity(Gravity.CENTER);
                    tr3.addView(tb1__);

                    tb1.addView(tr3);

                    tr3 = new TableRow(this);
                    tr3.setGravity(Gravity.CENTER);
                    tr3.addView(tb1);

                    tb.addView(tr3);

                    //white space
                    TableRow tr = new TableRow(this);
                    //tr.setPaddingRelative(5, 5, 5, 5);
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



                                                   /* TableLayout tb2 = new TableLayout(this);
                                                    tb2.setLayoutParams(mw);


                                                    TableRow tr__ = new TableRow(this);
                                                    tr__.setGravity(Gravity.CENTER);*/

                                                    //
                                                    TableRow tr1 = new TableRow(this);
                                                    tr1.setBackgroundColor(Color.rgb(236, 239, 241));
                                                    // tr1.setPaddingRelative(5, 5, 5, 5);
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
                                                    textview.setLayoutParams(mw1);
                                                    // textview.setWidth(100);
                                                    textview.setGravity(Gravity.CENTER);
                                                    tr1.addView(textview);

                                                    textview = new TextView(this);
                                                    textview.setText("View");
                                                    textview.setPaddingRelative(5, 10, 5, 10);
                                                    textview.setLayoutParams(mw1);
                                                    textview.setForeground(getResources().getDrawable(R.drawable.round_blue_prel));
                                                    textview.setTextColor(Color.rgb(41, 182, 246));
                                                    textview.setTypeface(Typeface.DEFAULT_BOLD);
                                                    textview.setGravity(Gravity.CENTER);
                                                    // textview.setWidth(100);
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
                                                    textview.setLayoutParams(mw1);
                                                    textview.setForeground(getResources().getDrawable(R.drawable.round_red));
                                                    textview.setTextColor(Color.rgb(255, 0, 0));
                                                    textview.setTypeface(Typeface.DEFAULT_BOLD);
                                                    textview.setGravity(Gravity.CENTER);
                                                    //  textview.setWidth(100);
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
                                                                tb1.removeView(tr1);
                                                                if (tb1.getChildCount() == 1) {
                                                                    TableLayout tb1__ = new TableLayout(DoctorPatientMedicalHistoryActivity.this);
                                                                    tb1__.setLayoutParams(mw);

                                                                    TableRow tr1 = new TableRow(DoctorPatientMedicalHistoryActivity.this);
                                                                    //tr1.setPaddingRelative(5, 5, 5, 5);
                                                                    tr1.setGravity(Gravity.CENTER);

                                                                    TextView textview = new TextView(DoctorPatientMedicalHistoryActivity.this);
                                                                    textview.setText("no data");
                                                                    Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.candal);
                                                                    textview.setTypeface(tf, Typeface.BOLD);
                                                                    textview.setLayoutParams(mw);
                                                                    textview.setGravity(Gravity.CENTER);
                                                                    // textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                                    textview.setTextSize(18);
                                                                    textview.setTextColor(Color.rgb(255, 0, 0));
                                                                    tr1.addView(textview);
                                                                    tb1__.addView(tr1);

                                                                    TableRow tr3 = new TableRow(DoctorPatientMedicalHistoryActivity.this);
                                                                    tr3.setGravity(Gravity.CENTER);
                                                                    tr3.addView(tb1__);
                                                                    tb1.addView(tr3);


                                                                    return;

                                                                }
                                                                //
                                                                return;
                                                            }
                                                        }
                                                    });

                                                    tr1.addView(textview);
                                                    tb1.addView(tr1);
                                                    //   tr__.addView(tb2);
                                                    // tb1.addView(tr__);

                                                    //
                                                    //System.out.println("Finish un auto print1____2");
                                                    size_[0]++;

                                                    if(size[0]==size_[0])
                                                    {

                                                        TableRow tr3 = new TableRow(this);
                                                        tr3.setGravity(Gravity.CENTER);


                                                        tr3.addView(tb1);
                                                        tb.addView(tr3);

                                                        //white space
                                                        TableRow tr = new TableRow(this);
                                                        // tr.setPaddingRelative(5, 5, 5, 5);
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



    void cls()
    {
        int chd = tb.getChildCount() - 1;
        while(chd != (child-1))
        {
            tb.removeView(tb.getChildAt(chd));
            chd--;
        }
    }


    ///
    @RequiresApi(api = Build.VERSION_CODES.O)
    void VS_()
    {

        dates2 = dates1;
        change_ = false;
        final boolean[] change = {false};
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.dialog_history2, null);

        if(dataP.size()==0)
        {
            return;
        }

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
                    AlertDialog.Builder x = new AlertDialog.Builder(DoctorPatientMedicalHistoryActivity.this);
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
                    AlertDialog.Builder x = new AlertDialog.Builder(DoctorPatientMedicalHistoryActivity.this);
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

                        Toasty.showText(DoctorPatientMedicalHistoryActivity.this.getApplicationContext(),
                                ""
                                        +( ((TextView) spinnerP.getSelectedView()).getText().toString()
                                        +" is Selected"),
                                Toasty.SUCCESS,Toast.LENGTH_SHORT);
                    }
                }, 100);

            }
        });

        String name[]= new String[]{"  Glucose Test ",
                "  F.B.S test ",
                "  Cumulative Test ",
                "  Hypertension Test ",
                "  Cholesterol and FatsTest ",
                "  Liver Test ",
                "  Kidneys Test ",
                "  Comprehensive Test "};

        Button btt [];
        btt = new Button[8];

        btt[0] = view.findViewById(R.id.btn1);
        {
            ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
            SpannableString content = new SpannableString(name[0]);
            content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
            btt[0].setText(content);
            btt[0].setPaddingRelative(10,5,10,8);
        }
        btt[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[0]=!bttn[0];
                String f = "Glucose Test";
                if(bttn[0])
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_check);
                    SpannableString content = new SpannableString(name[0]);
                    content.setSpan(imageSpan, 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[0].setText(content);
                    btt[0].setPaddingRelative(10,5,10,8);
                }
                else
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
                    SpannableString content = new SpannableString(name[0]);
                    content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[0].setText(content);
                    btt[0].setPaddingRelative(10,5,10,8);
                }
            }
        });

        btt[1] = view.findViewById(R.id.btn2);
        {
            ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
            SpannableString content = new SpannableString(name[1]);
            content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
            btt[1].setText(content);
            btt[1].setPaddingRelative(10,5,10,8);
        }
        btt[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[1]=!bttn[1];

                if(bttn[1])
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_check);
                    SpannableString content = new SpannableString(name[1]);
                    content.setSpan(imageSpan, 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[1].setText(content);
                    btt[1].setPaddingRelative(10,5,10,8);
                }
                else
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
                    SpannableString content = new SpannableString(name[1]);
                    content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[1].setText(content);
                    btt[1].setPaddingRelative(10,5,10,8);
                }
            }
        });

        btt[2] = view.findViewById(R.id.btn3);
        {
            ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
            SpannableString content = new SpannableString(name[2]);
            content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
            btt[2].setText(content);
            btt[2].setPaddingRelative(10,5,10,8);
        }
        btt[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[2]=!bttn[2];

                if(bttn[2])
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_check);
                    SpannableString content = new SpannableString(name[2]);
                    content.setSpan(imageSpan, 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[2].setText(content);
                    btt[2].setPaddingRelative(10,5,10,8);
                }
                else
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
                    SpannableString content = new SpannableString(name[2]);
                    content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[2].setText(content);
                    btt[2].setPaddingRelative(10,5,10,8);
                }
            }
        });

        btt[3] = view.findViewById(R.id.btn4);
        {
            ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
            SpannableString content = new SpannableString(name[3]);
            content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
            btt[3].setText(content);
            btt[3].setPaddingRelative(10,5,10,8);
        }
        btt[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[3]=!bttn[3];

                if(bttn[3])
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_check);
                    SpannableString content = new SpannableString(name[3]);
                    content.setSpan(imageSpan, 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[3].setText(content);
                    btt[3].setPaddingRelative(10,5,10,8);
                }
                else
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
                    SpannableString content = new SpannableString(name[3]);
                    content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[3].setText(content);
                    btt[3].setPaddingRelative(10,5,10,8);
                }
            }
        });

        btt[4] = view.findViewById(R.id.btn5);
        {
            ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
            SpannableString content = new SpannableString(name[4]);
            content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
            btt[4].setText(content);
            btt[4].setPaddingRelative(10,5,10,8);
        }
        btt[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[4]=!bttn[4];
                if(bttn[4])
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_check);
                    SpannableString content = new SpannableString(name[4]);
                    content.setSpan(imageSpan, 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[4].setText(content);
                    btt[4].setPaddingRelative(10,5,10,8);
                }
                else
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
                    SpannableString content = new SpannableString(name[4]);
                    content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[4].setText(content);
                    btt[4].setPaddingRelative(10,5,10,8);
                }
            }
        });

        btt[5] = view.findViewById(R.id.btn6);
        {
            ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
            SpannableString content = new SpannableString(name[5]);
            content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
            btt[5].setText(content);
            btt[5].setPaddingRelative(10,5,10,8);
        }
        btt[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[5]=!bttn[5];
                if(bttn[5])
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_check);
                    SpannableString content = new SpannableString(name[5]);
                    content.setSpan(imageSpan, 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[5].setText(content);
                    btt[5].setPaddingRelative(10,5,10,8);
                }
                else
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
                    SpannableString content = new SpannableString(name[5]);
                    content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[5].setText(content);
                    btt[5].setPaddingRelative(10,5,10,8);
                }
            }
        });

        btt[6] = view.findViewById(R.id.btn7);
        {
            ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
            SpannableString content = new SpannableString(name[6]);
            content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
            btt[6].setText(content);
            btt[6].setPaddingRelative(10,5,10,8);
        }
        btt[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[6]=!bttn[6];
                // Toasty.makeText(getApplicationContext(),"GGGGG  : "+bttn[6],Toasty.LENGTH_SHORT).show();
                if(bttn[6])
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_check);
                    SpannableString content = new SpannableString(name[6]);
                    content.setSpan(imageSpan, 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[6].setText(content);
                    btt[6].setPaddingRelative(10,5,10,8);
                }
                else
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
                    SpannableString content = new SpannableString(name[6]);
                    content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[6].setText(content);
                    btt[6].setPaddingRelative(10,5,10,8);
                }
            }
        });

        btt[7] = view.findViewById(R.id.btn8);
        {
            ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
            SpannableString content = new SpannableString(name[7]);
            content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
            btt[7].setText(content);
            btt[7].setPaddingRelative(10,5,10,8);
        }
        btt[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[7]=!bttn[7];
                //Toasty.makeText(getApplicationContext(),"GGGGG  : "+bttn[7],Toasty.LENGTH_SHORT).show();
                if(bttn[7])
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_check);
                    SpannableString content = new SpannableString(name[7]);
                    content.setSpan(imageSpan, 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[7].setText(content);
                    btt[7].setPaddingRelative(10,5,10,8);
                }
                else
                {
                    ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_remove);
                    SpannableString content = new SpannableString(name[7]);
                    content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spanned.SPAN_MARK_MARK);
                    btt[7].setText(content);
                    btt[7].setPaddingRelative(10,5,10,8);
                }
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

        for(int i=0;((btt!=null)&&(i<btt.length)); i++)
        {
            if(bttn[i])
            {
                ImageSpan imageSpan = new ImageSpan(DoctorPatientMedicalHistoryActivity.this, R.drawable.ic_check);
                SpannableString content = new SpannableString(name[i]);
                content.setSpan(imageSpan, 0, 1, Spanned.SPAN_MARK_MARK);
                btt[i].setText(content);
                btt[i].setPaddingRelative(10,5,10,8);
            }
        }


       /* DatePicker datePicker = view.findViewById(R.id.datePicker);
        if( (yy != -1)
                && (mm != -1)
                && (dd != -1))
        {
            datePicker.init(yy,mm,dd,null);
        }*/



        /*if(tic)
        {
           // datePicker.setEnabled(false);
          //  datePicker.setVisibility(View.INVISIBLE);
        }*/

        FloatingActionButton pickASingleDateFloatingActionButton,
                pickDateRangeFloatingActionButton,
                pickMultipleDatesFloatingActionButton;

        pickASingleDateFloatingActionButton =  view.findViewById(R.id.pickASingleDateFloatingActionButton);
        pickDateRangeFloatingActionButton =  view.findViewById(R.id.pickDateRangeFloatingActionButton);
        pickMultipleDatesFloatingActionButton =  view.findViewById(R.id.pickMultipleDatesFloatingActionButton);


        pickASingleDateFloatingActionButton.setOnClickListener(v ->
        {
            setSingleDate();
        });

        pickDateRangeFloatingActionButton.setOnClickListener(v ->
        {
            setDateRange(sd,ed);
        });

        pickMultipleDatesFloatingActionButton.setOnClickListener(v -> {
            setMultiDates();
        });


        if(tic)
        {
            //datePicker.setEnabled(false);
            // datePicker.setVisibility(View.INVISIBLE);

            pickASingleDateFloatingActionButton.hide();
            pickDateRangeFloatingActionButton.hide();
            pickMultipleDatesFloatingActionButton.hide();

            view.findViewById(R.id.textView9).setVisibility(View.GONE);
            view.findViewById(R.id.singleDateTextView).setVisibility(View.GONE);
            view.findViewById(R.id.dateRangeTextView).setVisibility(View.GONE);
            view. findViewById(R.id.multipleDatesTextView).setVisibility(View.GONE);
        }
        else
        {
            pickASingleDateFloatingActionButton.show();
            pickDateRangeFloatingActionButton.show();
            pickMultipleDatesFloatingActionButton.show();

            view.findViewById(R.id.textView9).setVisibility(View.VISIBLE);
            view.findViewById(R.id.singleDateTextView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.dateRangeTextView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.multipleDatesTextView).setVisibility(View.VISIBLE);

        }


        CheckBox chx_ = view.findViewById(R.id.checkBox);
        chx_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tic = chx_.isChecked();
                change[0] =true;
                if(tic)
                {
                    //datePicker.setEnabled(false);
                    // datePicker.setVisibility(View.INVISIBLE);

                    pickASingleDateFloatingActionButton.hide();
                    pickDateRangeFloatingActionButton.hide();
                    pickMultipleDatesFloatingActionButton.hide();

                    view.findViewById(R.id.textView9).setVisibility(View.GONE);
                    view.findViewById(R.id.singleDateTextView).setVisibility(View.GONE);
                    view.findViewById(R.id.dateRangeTextView).setVisibility(View.GONE);
                    view. findViewById(R.id.multipleDatesTextView).setVisibility(View.GONE);
                }
                else
                {
                    pickASingleDateFloatingActionButton.show();
                    pickDateRangeFloatingActionButton.show();
                    pickMultipleDatesFloatingActionButton.show();

                    view.findViewById(R.id.textView9).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.singleDateTextView).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.dateRangeTextView).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.multipleDatesTextView).setVisibility(View.VISIBLE);

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
                // yy = datePicker.getYear();
                //  mm = datePicker.getMonth();
                //dd = datePicker.getDayOfMonth();
                //date_ = yy+"-"+(mm+1)+"-"+dd;

                dialog.dismiss();

                i_ = btngg.getCheckedButtonIds();

                // i__=new ArrayList<Integer>();
                //for (int i = 0 ; i< bttn.length ; i++)if(bttn[i])i__.add(i);

               /* if(tic && change[0])
                {
                    //viweLog();
                }*/
                System.out.println(bttn[7]);

                if(change[0] || change_) viweLog();

                return;
            }
        });

        dialog.show();

        return;
    }


    private void setSingleDate()
    {

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder
                .datePicker();

        if(re!=-1)builder.setSelection(re);

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        if(!mod) builder.setCalendarConstraints(constraintsBuilder.setValidator(DateValidatorPointBackward.now()).build());
        else builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Long> materialDatePicker = builder
                .setTitleText("Select A Date:")
                .build();

        materialDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            selectedDate = (Long) selection;
            re = selectedDate;
            String pickedDate = DateFormat.format("yyyy-M-d", selectedDate).toString();
            System.out.println("----->>>>>>>>>>>>>>>>>>> "+pickedDate);
            List<String> dates = new ArrayList<String>();
            dates.add(pickedDate);
            dates1=dates;

            if((dates1!=null)&&(dates2!=null)
                    ||(!dates1.equals(dates2)))
            {
                change_=true;
            }
        });
        materialDatePicker.show(getSupportFragmentManager(), "PatientMedicalHistoryActivity");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDateRange(long x, long y)
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
            //cls();
            sd=dateRange.first;
            ed=dateRange.second;

            dates1 = getDateS(  Instant.ofEpochMilli(sd).atZone(ZoneId.systemDefault()).toLocalDate(),
                    Instant.ofEpochMilli(ed).atZone(ZoneId.systemDefault()).toLocalDate());

            if((dates1!=null)&&(dates2!=null)
                    ||(!dates1.equals(dates2)))
            {
                change_=true;
            }

            //  getData(new Date(dateRange.first), new Date(dateRange.second));
        });

        materialDatePicker.show(getSupportFragmentManager(), "PatientMedicalHistoryActivity");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    List<String> getDateS(LocalDate startDate, LocalDate endDate)
    {

        long numOfDays = ChronoUnit.DAYS.between(startDate, endDate);

        List<LocalDate> listOfDates = LongStream.range(0, numOfDays+1)
                .mapToObj(startDate::plusDays)
                .collect(Collectors.toList());

        List<String> dates = new ArrayList<String>();

        for(int i=0;i<listOfDates.size();i++)
        {
            System.out.println(listOfDates.get(i).getYear()+" "+listOfDates.get(i).getMonthValue()+" "+listOfDates.get(i).getDayOfMonth());
            dates.add(listOfDates.get(i).getYear()+"-"+
                    listOfDates.get(i).getMonthValue()+"-"+
                    listOfDates.get(i).getDayOfMonth());
        }

        return dates;
    }


    private void setMultiDates()
    {
        DatePickerBuilder builder = new DatePickerBuilder(this, new OnSelectDateListener() {
            @Override
            public void onSelect(@NotNull List<Calendar> list)
            {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");

                pickedDate = simpleDateFormat.format(list.get(0).getTimeInMillis());
                selectedDate = list.get(0).getTimeInMillis();
                if(list.size() != 1)
                {
                    List<Calendar> list1 = new ArrayList<Calendar>();
                    List<String> dates = new ArrayList<String>();
                    for(int i=0;i<list.size();i++)
                    {
                        list1.add(list.get(i));
                        System.out.println("--->----> "+simpleDateFormat.format(list.get(i).getTime()));
                        dates.add(simpleDateFormat.format(list.get(i).getTime()));
                    }
                    dates1 = dates;

                    if((dates1!=null)&&(dates2!=null)
                            ||(!dates1.equals(dates2)))
                    {
                        change_=true;
                    }

                    datesM = list1;
                }
                else{}

            }
        })
                .pickerType(CalendarView.MANY_DAYS_PICKER)
                .date(Calendar.getInstance())
                .maximumDate(Calendar.getInstance())
                .headerColor(R.color.colorPrimary)
                .abbreviationsLabelsColor(R.color.black)
                .selectionColor(R.color.colorPrimary)
                .todayLabelColor(R.color.colorAccent)
                .dialogButtonsColor(R.color.colorPrimary)
                .firstDayOfWeek(CalendarWeekDay.SATURDAY);

        if(datesM.size()>0){builder.setSelectedDays(datesM);}

        DatePicker multiDatePicker = builder.build();
        multiDatePicker.show();
    }



    void show(String []val)
    {

        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.comperhensive_values, null);

        TableLayout tb1 = view.findViewById(R.id.idf);

        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1);
        mw.setMargins(5,5,5,5);

               /* Typeface tf1 = Typeface.createFromAsset(getAssets(),
                        "fonts/sans-serif.ttf");*/

        String column[]= {
                "F.B.S (mg\\dl)"
                ,"S.GOT (u\\l)", "S.GPT (u\\l)", "G.G.T (u\\l)","Alk Phosphatese (u\\l)"
                ,"Uric Acid (mg\\dl)", "Urea (mg\\dl)", "Creatinine (mg\\dl)"
                ,"Triglycerid (mg\\dl)", "LDL (u\\l)", "HDL (u\\l)","Total (mg\\dl)"
                ,"Albumin (mg\\dl)"
                ,"Acp.Total (u\\l)"
                ,"Calcium (mE\\dl)"
                ,"Chloride (mE\\dl)"
                ,"S.Iron (mg\\dl)"
                ,"TIBC (mg\\dl)"
                ,"Acp.Prostatic (u\\l)"
                ,"Amylase (mg\\dl)"
                ,"Potassium (mE\\dl)"
                ,"Sodium (mE\\dl)"
                ,"2HPPs (mg\\dl)"
                ,"R.B.S (mg\\dl)"
                ,"Bilirubin Total (mg\\dl)"
                ,"Bilirubin Direct (mg\\dl)"
                ,"PSA (u\\l)"
                ,"Phosphours (mg\\dl)"
                ,"LDH (u\\l)"
                ,"CK-MB (u\\l)"
                ,"CPK (u\\l)"
                ,"T.Protein (mg\\dl)"
                ,"Magnesium (u\\l)"
        };

        for(int i=0;i<val.length;i++) {

            TableRow tr2 = new TableRow(this);
            tr2.setBackgroundColor(Color.rgb(236,239,241));
            //tr2.setPaddingRelative(5,5,5,5);
            tr2.setGravity(Gravity.CENTER);
            tr2.setLayoutParams(mw);

            TextView textview1 = new TextView(this);
            textview1.setText(column[i]);
            textview1.setLayoutParams(mw);
            textview1.setTypeface(null, Typeface.BOLD);
            textview1.setGravity(Gravity.CENTER);
            // textview1.setTextSize(14);
            tr2.addView(textview1);

            textview1 = new TextView(this);
            textview1.setText(val[i]);
            textview1.setLayoutParams(mw);
            textview1.setTypeface(null, Typeface.BOLD);
            textview1.setGravity(Gravity.CENTER);
            // textview1.setTextSize(14);
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

    private Long getHoursInMillies(float hour)
    {
        return TimeUnit.HOURS.toMillis((long) hour);
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


    @Override
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getDoctorDrawer(this, 2);
    }
}