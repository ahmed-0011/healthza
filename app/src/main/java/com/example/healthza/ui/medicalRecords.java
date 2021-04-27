package com.example.healthza.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.healthza.R;
import com.example.healthza.adapters.RecordsAdapter;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class medicalRecords extends AppCompatActivity implements RecordsAdapter.OnRecordItemClickListener  ,View.OnClickListener
        , View.OnFocusChangeListener
{

    RecyclerView recyclerView;
    List<Map<String, Object>> recordsList;
    RecordsAdapter radp;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    int recordsType;

    Button viewT;
    Button selecT;
    Button clear;

    EditText dateT;
    TextInputLayout deteT_;

    boolean tic = false;

    CheckBox chx;

    boolean bttn [];

    List<Integer> i_;
   // ArrayList<Integer> i__;
    //int di[] ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_records);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recordsList = new ArrayList<>();
        recyclerView = findViewById(R.id.recordsRecyclerView);


       // i_ = new ArrayList<Integer>();
        //di = new int [8];
       // for(int i=0; i<di.length ; i++)di[i]=-999;

                bttn = new boolean[8];
                for(int i=0; i<bttn.length ; i++)bttn[i]=false;

        //checkBTN();

        dateT = findViewById(R.id.dateTestEditText); deteT_ = findViewById(R.id.dateTestInputLayout); dateT.setOnFocusChangeListener(this);
        viewT = findViewById(R.id.testViewButton); viewT.setOnClickListener(this);
        clear = findViewById(R.id.cleartButton); clear.setOnClickListener(this);
        selecT = findViewById(R.id.selectTestButton); selecT.setOnClickListener(this);

        chx = findViewById(R.id.dateCB);
        chx.setOnClickListener(this);
    }

    //time and date auto
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        tic = checked;
        if(tic) { dateT.setEnabled(false);
            dateT.setVisibility(View.INVISIBLE);
            deteT_.setVisibility(View.INVISIBLE);
            PT_();
        }
        else {  dateT.setEnabled(true);
            dateT.setVisibility(View.VISIBLE);
            deteT_.setVisibility(View.VISIBLE); }
    }

    void viwe_() throws InterruptedException {

        if (dateT.getText().toString().isEmpty()&&(!tic)) {
            AlertDialog.Builder x = new AlertDialog.Builder(this);
            x.setMessage("Please Enter Date of Tests.").setTitle("incomplete data")

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


        if(!tic){
            P_();
        }

        ProgressDialog x= ProgressDialog.show(this, "Display Data...",
                "Please Wait while Display of Data...", true);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                pvThread(1,14,x);

            }
        }, 100);

        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        if( v == viewT ){
            try {
                viwe_();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;}

        if(v == chx) { onCheckboxClicked(v); return; }

        if( v == selecT) {
            VS_();
            return;
        }

        if(v == clear){cls();}
    }

    //
    synchronized void copTest( )
    {
        String peId = firebaseAuth.getCurrentUser().getUid();
        String date_ = dateT.getText().toString();

        String percent []=new String[]{
                "fbs_percent"
                ,"SGOT_percent", "SGPT_percent", "GGT_percent","AlkPhosphatese_percent"
                ,"UricAcid_percent", "Urea_percent", "Creatinine_percent"
                , "Triglycerid_percent", "LDLCholesterol_percent", "HDLCholesterol_percent","CholesterolTotal_percent"
                ,"albumin_percent"
                ,"acpTotal_percent"
                ,"calcium_percent"
                ,"chloride_percent"
                ,"sIron_percent"
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

        String finalPercent [] = percent;

        CollectionReference testRefs = db.collection("patients").document(peId)
                .collection("tests").document("other_test").collection(date_);

        testRefs.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    Map<String, Object> record = new HashMap<>();
                    record.put("type", 8);
                    record.put("addDate", document.get("date_add"));
                    record.put("addTime", document.get("time_add"));

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
                        record.put("val",val.clone());
                        record.put("del",new String[]{peId, "comprehensive",date_,document.getId()});
                        recordsList.add(record);
                        System.out.println("Finish un auto print1____2");
                }});
                }});
                }});
                }});
                }
            }
        });
    }

    synchronized void copTestT( )
    {
        String peId = firebaseAuth.getCurrentUser().getUid();

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

        String finalPercent [] = percent;

        DocumentReference DRC10 = db.collection("patients").document(peId)
                .collection("tests").document("other_test");

        DRC10.get().addOnCompleteListener(task10 ->
        {
            if (task10.isSuccessful())
            {
                DocumentSnapshot document10 = task10.getResult();
                List<String> dates = (List<String>) document10.get("dates");
                // System.out.println(dates.size()==0);
                // System.out.println(dates==null);
                if((dates!=null)&&(dates.size()!=0))
                {
                    //System.out.println("4544FFf");
                    for(int o_=0;o_<dates.size();o_++) {
                        String date_ = dates.get(o_);


        CollectionReference testRefs = db.collection("patients").document(peId)
        .collection("tests").document("other_test").collection(date_);

          testRefs.get().addOnCompleteListener(task ->
          {
             if (task.isSuccessful()) {
              for (QueryDocumentSnapshot document : task.getResult()) {
                Map<String, Object> record = new HashMap<>();
                record.put("type", 8);
                record.put("addDate", document.get("date_add"));
                record.put("addTime", document.get("time_add"));

                String val[] = new String[33];
                DocumentReference DRC = db.collection("patients").document(peId)
                 .collection("tests")
                 .document("fbs_test")
                 .collection(date_)
                 .document(document.getId());

          DRC.get().addOnCompleteListener(task0 ->
            {
               if (task0.isSuccessful()) {
                 val[0] = "" + task0.getResult().get(percent[0]);

          DocumentReference DRC2 = db.collection("patients").document(peId)
           .collection("tests")
            .document("liver_test")
            .collection(date_)
            .document(document.getId());

         DRC2.get().addOnCompleteListener(task2 ->
           {
            if (task2.isSuccessful()) {
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
           if (task3.isSuccessful()) {
            val[5] = "" + task3.getResult().get(percent[5]);
            val[6] = "" + task3.getResult().get(percent[6]);
            val[7] = "" + task3.getResult().get(percent[7]);

         DocumentReference DRC4 = db.collection("patients").document(peId)
           .collection("tests")
           .document("cholesterolAndFats_test")
           .collection(date_)
           .document(document.getId());

         DRC4.get().addOnCompleteListener(task4 ->
          {

           if (task4.isSuccessful()) {
            val[8] = "" + task4.getResult().get(percent[8]);
            val[9] = "" + task4.getResult().get(percent[9]);
            val[10] = "" + task4.getResult().get(percent[10]);
            val[11] = "" + task4.getResult().get(percent[11]);

               for (int o = 12; o <= 32; o++) {
                 val[o] = "" + document.get(percent[o]);
                 }

              record.put("val", val.clone());
              record.put("del", new String[]{peId, "comprehensive", date_, document.getId()});
              recordsList.add(record);
              }
              });
              }
               });
               }
              });
              }
              });
              }
              }
              });
                        //
              }}}});
    }

    //
    synchronized  void testStr(int set)
    {
        String type="";
        String percent [];
        int len = 0;

        switch (set) {

            case 5: {
                type = "cholesterolAndFats_test";
                percent = new String[]{"Triglycerid_percent", "LDLCholesterol_percent", "HDLCholesterol_percent","CholesterolTotal_percent"};
                len = percent.length;
                break;
            }

            case 6: {
                type = "liver_test";
                percent = new String[]{"SGOT_percent", "SGPT_percent", "GGT_percent","AlkPhosphatese_percent"};
                len = percent.length;
                break;
            }

            case 7: {
                type = "Kidneys_test";
                percent = new String[]{"UricAcid_percent", "Urea_percent", "Creatinine_percent"};
                len = percent.length;
                break;
            }

            default:
                throw new IllegalStateException("Unexpected value: " + set);
        }

        String peId = firebaseAuth.getCurrentUser().getUid();
        String date_ = dateT.getText().toString();

        CollectionReference testRefs = db.collection("patients").document(peId)
                .collection("tests").document(type).collection(date_);
        String finalType = type;
        String finalPercent [] = percent;
        int finalLen = len;
        testRefs.get().addOnCompleteListener(task ->
        {
            System.out.println(""+finalLen+" "+finalPercent[1]);
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    if( ( (set==7) || (set==5) || (set==6) )
                            &&(((boolean)document.get("sub"))
                            ==true)) continue;

                    Map<String, Object> record = new HashMap<>();
                    record.put("type",set);
                    record.put("addDate",document.get("date_add"));
                    record.put("addTime",document.get("time_add"));

                    String vall []= new String[finalLen];
                    for(int i=0;i<finalLen;i++) {
                        vall[i]= "" + document.get(finalPercent[i]);

                    }
                    record.put("val",vall.clone());
                    record.put("del",new String[]{peId, finalType,date_,document.getId()});
                    recordsList.add(record);

                }
            }
        });
        return;
    }

    synchronized  void testStrT(int set)
    {
        String type="";
        String percent [];
        int len = 0;

        switch (set) {

            case 5: {
                type = "cholesterolAndFats_test";
                percent = new String[]{"Triglycerid_percent", "LDLCholesterol_percent", "HDLCholesterol_percent","CholesterolTotal_percent"};
                len = percent.length;
                break;
            }

            case 6: {
                type = "liver_test";
                percent = new String[]{"SGOT_percent", "SGPT_percent", "GGT_percent","AlkPhosphatese_percent"};
                len = percent.length;
                break;
            }

            case 7: {
                type = "Kidneys_test";
                percent = new String[]{"UricAcid_percent", "Urea_percent", "Creatinine_percent"};
                len = percent.length;
                break;
            }

            default:
                throw new IllegalStateException("Unexpected value: " + set);
        }
        String peId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference DRC = db.collection("patients").document(peId)
                .collection("tests").document(type);

        String finalType1 = type;
        int finalLen1 = len;
        DRC.get().addOnCompleteListener(task1 ->
        {
            if (task1.isSuccessful())
            {
                DocumentSnapshot document1 = task1.getResult();
                List<String> dates = (List<String>) document1.get("dates");

                if((dates!=null)&&(dates.size()!=0))
                {
                    for(int o=0;o<dates.size();o++)
                    {
                        String date_ = dates.get(o);
                        CollectionReference RC = DRC.collection(date_);
                        //
                        String finalType = finalType1;
                        String finalPercent[] = percent;
                        int finalLen = finalLen1;
                        RC.get().addOnCompleteListener(task ->
                        {
                            System.out.println("" + finalLen + " " + finalPercent[1]);
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    if( ( (set==7) || (set==5) || (set==6) )
                                            &&(((boolean)document.get("sub"))
                                            ==true)) continue;

                                    Map<String, Object> record = new HashMap<>();
                                    record.put("type", set);
                                    record.put("addDate", document.get("date_add"));
                                    record.put("addTime", document.get("time_add"));

                                    String vall[] = new String[finalLen];
                                    for (int i = 0; i < finalLen; i++) {
                                        vall[i] = "" + document.get(finalPercent[i]);
                                    }
                                    record.put("val", vall.clone());
                                    record.put("del", new String[]{peId, finalType, date_, document.getId()});
                                    recordsList.add(record);

                                }
                            }
                        });
                    }
                }
            }
        });
        return;
    }


    //glc && fbs && hypertension && cumulative
    synchronized  void testAtoT(int set)
    {
        String type="";
        String percent ="";

        switch (set)
        {
            case 1:
            {
                type = "glucose_test";
                percent = "glucose_percent";
                break;
            }

            case 2:
            {
                type = "fbs_test";
                percent = "fbs_percent";
                break;
            }

            case 3:
            {
                type = "diabetes_cumulative_test";
                percent = "hbAlc_percent";

                break;
            }

            case 4:
            {
                type = "hypertension_test";
                percent = "hypertension_percent";
                break;
            }
        }

        String peId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference DRC = db.collection("patients").document(peId)
                .collection("tests").document(type);

        String finalType = type;
        String finalPercent = percent;

        System.out.println(type);

        DRC.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                List<String> dates = (List<String>) document.get("dates");
                // System.out.println(dates.size()==0);
                // System.out.println(dates==null);
                if((dates!=null)&&(dates.size()!=0)) {
                    //System.out.println("4544FFf");
                    for(int i=0;i<dates.size();i++) {
                        String date_ = dates.get(i);
                        CollectionReference RC = DRC.collection(date_);
                        RC.get().addOnCompleteListener(task1 ->
                        {
                            if (task1.isSuccessful()) {
                                for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                    //System.out.println(peId + " : " +document1.get("glucose_percent"));

                                    if(  (set==2)
                                            &&(((boolean)document1.get("sub"))
                                            ==true)) continue;

                                    Map<String, Object> record = new HashMap<>();
                                    record.put("type",set);
                                    record.put("addDate",document1.get("date_add"));
                                    record.put("addTime",document1.get("time_add"));
                                    record.put("val",document1.get(finalPercent));
                                    record.put("del",new String[]{peId, finalType,date_,document1.getId()});
                                    recordsList.add(record);
                                    //System.out.println(peId + date_);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    synchronized  void testAto(int set)
    {
        String type="";
        String percent ="";

        switch (set)
        {
            case 1:
            {
                type = "glucose_test";
                percent = "glucose_percent";
                break;
            }

            case 2:
            {
                type = "fbs_test";
                percent = "fbs_percent";
                break;
            }

            case 3:
            {
                type = "diabetes_cumulative_test";
                percent = "hbAlc_percent";
                break;
            }

            case 4:
            {
                type = "hypertension_test";
                percent = "hypertension_percent";
                break;
            }
        }

        String peId = firebaseAuth.getCurrentUser().getUid();
        String date_ = dateT.getText().toString();

        CollectionReference testRefs = db.collection("patients").document(peId)
                .collection("tests").document(type).collection(date_);
        String finalPercent = percent;
        String finalType = type;
        testRefs.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    if(  (set==2)
                            &&(((boolean)document.get("sub"))
                            ==true)) continue;

                    Map<String, Object> record = new HashMap<>();
                    record.put("type",set);
                    record.put("addDate",document.get("date_add"));
                    record.put("addTime",document.get("time_add"));
                    record.put("val",document.get(finalPercent));
                    record.put("del",new String[]{peId, finalType,date_,document.getId()});
                    recordsList.add(record);
                    //System.out.println(peId + date_);
                }
            }
        });
        return;
    }

    void VS_()
    {
        final boolean[] change = {false};
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.dialog_records, null);

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
               // Toast.makeText(getApplicationContext(),"GGGGG  : "+bttn[6],Toast.LENGTH_SHORT).show();
            }
        });

        btt[7] = view.findViewById(R.id.btn8);
        btt[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change[0] =true;
                bttn[7]=!bttn[7];
                //Toast.makeText(getApplicationContext(),"GGGGG  : "+bttn[7],Toast.LENGTH_SHORT).show();
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


        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        ImageButton imgButton;
        imgButton =(ImageButton)view.findViewById(R.id.imageButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                i_ = btngg.getCheckedButtonIds();

               // i__=new ArrayList<Integer>();
                //for (int i = 0 ; i< bttn.length ; i++)if(bttn[i])i__.add(i);

                if(tic && change[0])
                {
                    PT_();
                }
                System.out.println(bttn[7]);
                return;
            }
        });

        dialog.show();

        return;
    }

    void P_()
    {
        recordsList = new ArrayList<>();
        String peId = firebaseAuth.getCurrentUser().getUid();

        ProgressDialog x= ProgressDialog.show(this, "",
                "Please Wait TO get Data...", true);
        pThread(1,8,x);
    }

    void PT_()
    {
        recordsList = new ArrayList<>();
        String peId = firebaseAuth.getCurrentUser().getUid();


        boolean l=false;

        for( int i=0 ; i<bttn.length ; i++ )l=l||bttn[i];

        if(!l) { return; }

        ProgressDialog x= ProgressDialog.show(this, "",
                "Please Wait TO get Data...", true);

        ptThread(1,8,x);
    }

    void pThread(int t1,int t2,ProgressDialog finalX)
    {
        if(t1>8 || t1>t2)return;
        String peId = firebaseAuth.getCurrentUser().getUid();
        CollectionReference DRC = db.collection("patients").document(peId).collection("tests");
        DRC.get().addOnCompleteListener(task -> {
            // Thread t1
            if((t1>=1)&&(t1<=4))
            {
                if(bttn[t1-1])testAto(t1);
                pThread(t1+1,t2,finalX);
            }
            else
            {
                if((t1>=5)&&(t1<=7))
                {
                    if(bttn[t1-1])testStr(t1);
                    pThread(t1+1,t2,finalX);
                }
                else
                {
                    if(t1==8){
                        if(bttn[t1-1])copTest();
                        finalX.dismiss();
                    }
                }
            }
        });
    }

    void ptThread(int t1,int t2,ProgressDialog finalX)
    {
        if(t1>8 || t1>t2)return;
        String peId = firebaseAuth.getCurrentUser().getUid();
        CollectionReference DRC = db.collection("patients").document(peId).collection("tests");
        DRC.get().addOnCompleteListener(task -> {
            // Thread t1
            if((t1>=1)&&(t1<=4))
            {
                if(bttn[t1-1])testAtoT(t1);
                ptThread(t1+1,t2,finalX);
            }
            else
            {
                if((t1>=5)&&(t1<=7))
                {
                    if(bttn[t1-1])testStrT(t1);
                    ptThread(t1+1,t2,finalX);
                }
                else
                {
                    if(t1==8){
                        if(bttn[t1-1])copTestT();
                        finalX.dismiss();
                    }
                }
            }
        });
    }

    void pvThread(int t1,int t2,ProgressDialog x)
    {
        if(t1>t2)return;
        String peId = firebaseAuth.getCurrentUser().getUid();
        CollectionReference DRC = db.collection("patients").document(peId).collection("tests");
        DRC.get().addOnCompleteListener(task -> {
            // Thread t1
            if (t1==t2)
            {
                {
                    System.out.println("D11D");
                    if (recordsList.size() == 0)
                    {
                        x.dismiss();
                        AlertDialog.Builder x1 = new AlertDialog.Builder(medicalRecords.this);
                        x1.setMessage("No Data Found.").setTitle("incomplete data")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                       // if(!tic)finalX.dismiss();
                                        return;
                                    }
                                })
                                .setIcon(R.drawable.goo)
                                .setPositiveButtonIcon(getDrawable(R.drawable.yes))
                                .show();
                    }
                    else {
                        radp = new RecordsAdapter(medicalRecords.this, recordsList, medicalRecords.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(medicalRecords.this));
                        recyclerView.setAdapter(radp);
                        x.dismiss();
                        System.out.println("Finish un auto print1___3");
                        //if (!tic) finalX.dismiss();
                        // x.cancel();
                    }
                }
            }
           else pvThread(t1+1,t2,x);
        });
    }

    void cls()
    {
        recordsList = new ArrayList<>();
        bttn =new boolean[8];
        for (int i=0;i<bttn.length;i++)bttn[i]=false;
        i_=null;
        dateT.setText("");
        radp = new RecordsAdapter(medicalRecords.this, recordsList, medicalRecords.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(medicalRecords.this));
        recyclerView.setAdapter(radp);
        if(tic)
        {
            chx.callOnClick();
            dateT.setEnabled(true);
            dateT.setVisibility(View.VISIBLE);
            deteT_.setVisibility(View.VISIBLE);
        }
        chx.setChecked(false);
        tic=false;
    }

    //

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        {
            if (!hasFocus) {
                Log.d("focus", "focus lost");
                // Do whatever you want here
            } else {
                Toast.makeText(getApplicationContext(), " Tap outside edittext to lose focus ", Toast.LENGTH_SHORT).show();
                Log.d("focus", "focused");
            }

            return;
        }

    }

    //
    // <!-- Clear focus on touch outside for all EditText inputs. "Clear focus input"
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
// "Clear focus input" -->

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
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void onRemoveButtonClick(int position) {

    }

    @Override
    public void onViewButtonClick(int position) {

    }
}