package com.project.cdh.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.project.cdh.DrawerUtil;
import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.project.cdh.ui.Functions.TAG_CT;


public class PatientAddHbAlcTestActivity extends AppCompatActivity implements View.OnClickListener
        ,CompoundButton.OnCheckedChangeListener
        , View.OnFocusChangeListener
{

    CheckBox autoTD;
    FloatingActionButton stamp;
    SwitchDateTimeDialogFragment dateTimeDialogFragment;

    TextView datE;
    TextView timE;
    TextView td;

    private EditText hbAlc;

    private Button clear;
    private Button add;

    private static final String TAG = "addHbAlcTest";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    int ct = 0;
    int ctt=0;
    float Max=-999;
    float Min=999;

    boolean f=true;

    @SuppressLint("LongLogTag")
    public boolean onSupportNavigateUp()
    {
        Log.w ("Add PatientAddHbAlcTestActivity test.", "onSupportNavigateUp is calll");
        onBackPressed ();
        return super.onSupportNavigateUp ();
    }
    //
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //complet
        hbAlc.clearFocus();
        return super.onKeyDown(keyCode, event);
    }
    //
    @SuppressLint("LongLogTag")
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed ();
        Log.w ("Add PatientAddHbAlcTestActivity test.", "this onbackpress is calll");

        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO EXIT?" ).setTitle ( "Exit Activity'Add PatientAddHbAlcTestActivity test'" )

                .setPositiveButton ( "YES_EXIT", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w ("Add PatientAddHbAlcTestActivity test.", "end");
                        Toasty.showText(getApplicationContext(), "Back...",Toasty.INFORMATION, Toast.LENGTH_SHORT);
                        //complet
                        finish();
                    }
                } )

                .setNegativeButton ( "CANCEL", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                })

                .setIcon(R.drawable.qus)
                .setPositiveButtonIcon (getDrawable ( R.drawable.yes))
                .setNegativeButtonIcon(getDrawable ( R.drawable.no))
                .show ();
        return;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
       // Functions.pact=-999;
        //complet
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_add_hb_alc_test);

        Log.w ("Add PatientAddHbAlcTestActivity test.", "start");
        Toasty.showText(getApplicationContext(), "New Cumulative Test...",Toasty.INFORMATION, Toast.LENGTH_SHORT);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        datE = findViewById(R.id.dateText0);
        timE = findViewById(R.id.timeText0);
        td = findViewById(R.id.textView10);

        datE.setText("YYYY/MM/DD");
        timE.setText("HH:MM");


        dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                "Set Date And Time",
                "OK",
                "Cancel"
        );

//Assign values
        dateTimeDialogFragment.startAtCalendarView();
        dateTimeDialogFragment.set24HoursMode(true);
        dateTimeDialogFragment.setMinimumDateTime(new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime());
        dateTimeDialogFragment.setMaximumDateTime(Calendar.getInstance().getTime());
        //dateTimeDialogFragment.setDefaultDateTime(new GregorianCalendar(2017, Calendar.MARCH, 4, 15, 20).getTime());

//Define new day and month format

        try {
            dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("YYYY/MM/DD", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }
//Set listener
        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                String year =""+ dateTimeDialogFragment.getYear();
                String month =""+ (dateTimeDialogFragment.getMonth()+1);// if(month.length()==1)month = "0"+month;
                String day =""+ dateTimeDialogFragment.getDay(); //if(day.length()==1)day= "0"+day;
                String hour =""+ dateTimeDialogFragment.getHourOfDay(); //if(hour.length()==1)hour = "0"+hour;
                String mnt =""+ dateTimeDialogFragment.getMinute();  //if(mnt.length()==1)mnt = "0"+mnt;
                String date_T = dateTimeDialogFragment.getYear()+"-"+dateTimeDialogFragment.getMonth()+"-"+dateTimeDialogFragment.getDay()
                        +" "+dateTimeDialogFragment.getHourOfDay()+":"+dateTimeDialogFragment.getMinute();

                datE.setText(year+"-"+month+"-"+day);
                timE.setText(hour+":"+mnt);
                //  java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(year+"-"+month+"-"+day+" "+hour+":"+mnt+":0.0");
                //timestamp;
                //Toasty.showText(getApplicationContext(),(""+timestamp),Toasty.INFORMATION,Toast.LENGTH_SHORT);
            }

            @Override
            public void onNegativeButtonClick(Date date) {

            }
        });

//Show

        stamp = findViewById(R.id.fpat);
        stamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeDialogFragment.show(getSupportFragmentManager(),"dialog_time");
            }
        });

        autoTD = findViewById(R.id.TimeDateAuto0);
        autoTD.setChecked(false);
        autoTD.setOnClickListener(this);

        td.setVisibility(View.VISIBLE);

        hbAlc = findViewById(R.id.innerHbAlcPercent);
        hbAlc.setOnFocusChangeListener(this);

        clear = findViewById(R.id.ClearHbAlcTest); clear.setOnClickListener (this);
        add = findViewById(R.id.AddHbAlcTest); add.setOnClickListener(this);

    }



    //time and date auto
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        if (autoTD.equals(view)) {
            if (checked) {
                stamp.setEnabled(false);
                td.setVisibility(View.INVISIBLE);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/mm/dd hh:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                Functions.timeS = (now.getHour()>9?now.getHour():(/*"0"+*/now.getHour()))
                        +":"+
                        (now.getMinute()>9?now.getMinute():(/*"0"+*/now.getMinute()));
                Functions.dateS = now.getYear()+"-"+
                        (now.getMonthValue()>9?now.getMonthValue():(/*"0"+*/now.getMonthValue()))
                        +"-"+
                        (now.getDayOfMonth()>9?now.getDayOfMonth():(/*"0"+*/now.getDayOfMonth()));
                timE.setText(Functions.timeS);
                datE.setText(Functions.dateS);
            } else {
                stamp.setEnabled(true);
                td.setVisibility(View.VISIBLE);
            }

            // TODO: Veggie sandwich
        }
    }

    //Empty Fields
    boolean ifEmptyFields()
    {
        //complet
        boolean empty = false;
        empty = empty || datE.getText().toString().equals("YYYY/MM/DD");
        empty = empty || timE.getText().toString().equals("HH:MM");
        return empty || hbAlc.getText().toString().isEmpty();
    }

    // do
    void adD()
    {
        //complet
        if(ifEmptyFields())
        {
            Toasty.showText(getApplicationContext(), "Please complete fill the form data...",Toasty.ERROR, Toast.LENGTH_SHORT);
            return;
        }


        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO ADD PatientAddHbAlcTestActivity TEST?" ).setTitle ( "Add PatientAddHbAlcTestActivity test" )

                .setPositiveButton ( "YES_ADD", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w ("ADD TEST", "ADD PatientAddHbAlcTestActivity TEST");
                        // functions and codes
                        //complet


                        if(f)
                        {
                            setAddOPT();

                            return;
                        }
                        addTest();

                       // Toast.makeText(getApplicationContext(), "PatientAddHbAlcTestActivity TEST IS ADD...", Toast.LENGTH_SHORT).show();


                    }
                } )

                .setNegativeButton ( "CANCEL", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                })

                .setIcon(R.drawable.qus)
                .setPositiveButtonIcon (getDrawable ( R.drawable.yes))
                .setNegativeButtonIcon(getDrawable ( R.drawable.no))
                .show ();
    }

    // clear
    void cleaR()
    {
        //complet

        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO CLEAR FIELDS?" ).setTitle ( "Clear Fields" )

                .setPositiveButton ( "YES_CLEAR", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w ("CLEAR FIELDS", "PatientAddHbAlcTestActivity TEST CLEAR FIELDS");
                        Toasty.showText(getApplicationContext(), "FIELDS IS CLEARD...",Toasty.INFORMATION, Toast.LENGTH_SHORT);
                        // functions and codes
                        //complet
                        autoTD.setChecked(false);
                        autoTD.callOnClick();
                        datE.setText("YYYY/MM/DD");
                        timE.setText("HH:MM");
                        hbAlc.setText("");

                    }
                } )

                .setNegativeButton ( "CANCEL", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                })

                .setIcon(R.drawable.qus)
                .setPositiveButtonIcon (getDrawable ( R.drawable.yes))
                .setNegativeButtonIcon(getDrawable ( R.drawable.no))
                .show ();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {

        if (v == add) { adD(); return; }
        if (v == clear) { cleaR(); return; }
        if (v == autoTD) { onCheckboxClicked(v); return; }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if(v==hbAlc)
        {
            if (!hasFocus) {
                Log.d("focus", "focus lost");
                // Do whatever you want here
            } else {
                Toasty.showText(getApplicationContext(), " Tap outside edittext to lose focus ",Toasty.INFORMATION, Toast.LENGTH_SHORT);
                Log.d("focus", "focused");
            }

            return;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //complet

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

    // db code;

    private void addTest()
    {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in

            String userId = user.getUid();

            //<!--update tests Count

            Map<String, Object> Count = new HashMap<>();
            Count.put("count", ++ct);

            db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("count")
                    .set(Count)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG_CT, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG_CT, "Error writing document", e);
                        }
                    });

            //ende update tests Count-->

            //<!-- add test

            Map<String, Object> dataTest = new HashMap<>();
            dataTest.put("date_add", datE.getText().toString());
            dataTest.put("time_add", timE.getText().toString());
            dataTest.put("hbAlc_percent", Float.parseFloat(hbAlc.getText().toString()));
            dataTest.put("type", "cumulative");

            //Time Stamp
            String Date =datE.getText().toString();
            int i1 = Date.indexOf("-");
            int i2 = Date.lastIndexOf("-");

            String year = Date.substring(0,i1);
            String month = Date.substring(i1+1,i2); //if(month.length()==1)month = "0"+month;
            String day = Date.substring(i2+1); //if(day.length()==1)day= "0"+day;

            String Time = timE.getText().toString();
            String hour = Time.substring(0,Time.indexOf(":")); //if(hour.length()==1)hour = "0"+hour;
            String mnt = Time.substring(Time.indexOf(":")+1); //if(mnt.length()==1)mnt = "0"+mnt;
            System.out.println(year+"-"+month+"-"+day+" "+hour+":"+mnt+":0.0");
            java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(year+"-"+month+"-"+day+" "+hour+":"+mnt+":0.0");
            dataTest.put("timestamp", timestamp);

            DocumentReference DRC = db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("diabetes_cumulative_test");

            DRC.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();
                        List<String> dates = (List<String>) document.get("dates");
                        if((dates==null)||(dates.size()==0))
                        {
                            Map<String, Object> datae = new HashMap<>();
                            dates = new ArrayList<>();
                            dates.add(datE.getText().toString());
                            datae.put("dates",dates);
                            DRC.set(datae)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                            f=false;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });
                        }

                        else {
                            boolean bool =false;
                            for(int i=0;((i<dates.size())&&(!bool));i++) {
                                if (dates.get(i).equals(datE.getText().toString()))
                                {  bool = true; }
                            }
                            if(!bool)
                            {
                                dates.add(datE.getText().toString());
                                DRC.update("dates", dates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                                // Toasty.makeText(getApplicationContext(),d+" 11 "+c,Toasty.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        MaxMinThisTestCountUpdate(DRC,document);

                        dataTest.put("this_test_count", ctt);

                        //
                        DRC.collection(datE.getText().toString())
                                .document("test# : "+ct)
                                .set(dataTest)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                        f=false;
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        f=false;
                    }
                    f=false;
                }
            });

            //end add test -->



        } else {
            // No user is signed in
        }
        Toasty.showText(getApplicationContext(),"successfully submit Hemoglobin A1c (mmol/mol)...",Toasty.SUCCESS,Toast.LENGTH_SHORT);

    }

    void MaxMinThisTestCountSet()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userId = user.getUid();

        DocumentReference DRC = db.collection("patients") // table
                .document(userId) // patient id
                .collection("tests")// table inside patient table
                .document("diabetes_cumulative_test");

        DRC.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot document = task.getResult();
                if (document.get("count") == null
                        || document.get("max_hba1c") == null
                        || document.get("min_hba1c") == null) {

                    HashMap Mp = new HashMap();
                    Mp.put("count", 0);
                    Mp.put("max_hba1c", Float.MIN_VALUE);
                    Mp.put("min_hba1c",Float.MAX_VALUE);

                    DRC.set(Mp)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    addTest();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                } else {
                    Max = Float.parseFloat(document.get("max_hba1c").toString());
                    Min = Float.parseFloat(document.get("min_hba1c").toString());
                    ctt = Integer.parseInt(document.get("count").toString());

                    addTest();
                }
            }
        });
    }


    void MaxMinThisTestCountUpdate(DocumentReference DRC,DocumentSnapshot document )
    {
        Max = Float.parseFloat(document.get("max_hba1c").toString());
        Min = Float.parseFloat(document.get("min_hba1c").toString());
        ctt = Integer.parseInt(document.get("count").toString());
        //
        DRC.update("count", ++ctt)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        //
        if (Float.parseFloat(hbAlc.getText().toString()) > Max) {
            Max = Float.parseFloat(hbAlc.getText().toString());
        }
        DRC.update("max_hba1c", Max)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        //
        if (Float.parseFloat(hbAlc.getText().toString()) < Min) {
            Min = Float.parseFloat(hbAlc.getText().toString());
        }
        DRC.update("min_hba1c", Min)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        //
    }

    void setAddOPT()
    {

        //complet

        //<!--get tests Count
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            String userId = user.getUid();
            DocumentReference docRef = db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("count");

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            String cte = "" + document.getData().toString();
                            ct = Integer.parseInt(cte.substring(7,cte.length()-1));
                        } else {
                            Log.d(TAG, "No such document");
                            ct = 0;
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        ct = 0;
                    }
                }
            });
        }
        //end get tests Count-->

        MaxMinThisTestCountSet();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getPatientDrawer(this, 12);

    }

}