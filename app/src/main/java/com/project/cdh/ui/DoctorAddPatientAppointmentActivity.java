package com.project.cdh.ui;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.project.cdh.DrawerUtil;
import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.project.cdh.models.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DoctorAddPatientAppointmentActivity extends AppCompatActivity implements View.OnClickListener
        , View.OnFocusChangeListener
{
    ////////////////varable/////////////////////


    private Button add;
    private Button clear;
    private Button search;

    private Spinner spinnerT;
    private Spinner spinnerP;

    EditText searchP;
    EditText descripeC;

    List<String> dataT;
    List<String> dataP;
    List<String> idsP;

    private String testName = "";
    int testPOS = 0;

    private String patientName = "";
    private String patientId = "";
    int patientPOS = 0;

    private static final String TAG = "addNewTestAppointmen";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

   String Stamp = "";

    FloatingActionButton stamp;
//////////////////////////////////////////////////////
    @SuppressLint("LongLogTag")
    public boolean onSupportNavigateUp()
    {
        Log.w ("Add New Test Appointment.", "onSupportNavigateUp is calll");
        onBackPressed ();
        return super.onSupportNavigateUp ();
    }
    //
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //complet
        searchP.clearFocus();
        return super.onKeyDown(keyCode, event);
    }
    //
    @SuppressLint("LongLogTag")
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed ();
        Log.w ("Add New Test Appointment.", "this onbackpress is calll");

        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO EXIT?" ).setTitle ( "Exit Activity'Add New Test Appointment'" )

                .setPositiveButton ( "YES_EXIT", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w ("Add New Test Appointment.", "end");
                        Toast.makeText(getApplicationContext(), "Back...", Toast.LENGTH_SHORT).show();
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
        //complet
    }

    SwitchDateTimeDialogFragment dateTimeDialogFragment;

    ArrayAdapter<String> adapter1;

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_add_patient_appointment);


        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Initialize


        dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                "Set Date And Time",
                "OK",
                "Cancel"
        );

//Assign values
        dateTimeDialogFragment.startAtCalendarView();
        dateTimeDialogFragment.set24HoursMode(true);
       // dateTimeDialogFragment.setMinimumDateTime(Calendar.getInstance().getTime());
       // dateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(3000, Calendar.DECEMBER, 31).getTime());
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
                String month =""+ dateTimeDialogFragment.getMonth(); if(month.length()==1)month = "0"+month;
                String day =""+ dateTimeDialogFragment.getDay(); if(day.length()==1)day= "0"+day;
                String hour =""+ dateTimeDialogFragment.getHourOfDay(); if(hour.length()==1)hour = "0"+hour;
                String mnt =""+ dateTimeDialogFragment.getMinute();  if(mnt.length()==1)mnt = "0"+mnt;
                String date_T = dateTimeDialogFragment.getYear()+"-"+dateTimeDialogFragment.getMonth()+"-"+dateTimeDialogFragment.getDay()
                        +" "+dateTimeDialogFragment.getHourOfDay()+":"+dateTimeDialogFragment.getMinute();
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(year+"-"+month+"-"+day+" "+hour+":"+mnt+":0.0");
                Stamp = ""+timestamp;
          Toasty.showText(getApplicationContext(),(""+timestamp),Toasty.INFORMATION,Toast.LENGTH_SHORT);
            }

            @Override
            public void onNegativeButtonClick(Date date) {

            }
        });

//Show

        stamp = findViewById(R.id.fpa);
        stamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeDialogFragment.show(getSupportFragmentManager(),"dialog_time");
            }
        });




        Log.w ("Add New Test Appointment.", "start");
        Toast.makeText(getApplicationContext(), "Add New Test Appointment....", Toast.LENGTH_SHORT).show();


        searchP = findViewById(R.id.innerPatient); searchP.setOnFocusChangeListener(this);
        descripeC = findViewById(R.id.innerDescribe); descripeC.setOnFocusChangeListener(this);

        add = findViewById(R.id.addAppointment); add.setOnClickListener(this);
        clear = findViewById(R.id.clearSearch); clear.setOnClickListener(this);
        search = findViewById(R.id.SearchPatient); search.setOnClickListener(this);

        spinnerP = findViewById(R.id.spinnerPatient);
        flagPatient();

        spinnerT = findViewById(R.id.spinnerTest);
        flagTest();
        spinnerT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                testName = "" + parent.getItemAtPosition(position).toString();
                testPOS = position;
                ((TextView) spinnerT.getSelectedView()).setTextColor(Color.rgb(129,212,250));
                //!complet

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                testName = "" + parent.getItemAtPosition(testPOS).toString();
                ((TextView) spinnerT.getSelectedView()).setTextColor(Color.rgb(129,212,250));
                //!complet
            }
        });

    }

    void flagTest()
    {
        Resources res = getResources();
        dataT = Arrays.asList(res.getStringArray(R.array.arrayTests));
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, dataT);
        //complet
        // ........... When create db [add this code]: 'pre check and drop 'remove' Chronic Diseases added in past'
        spinnerT.setAdapter(adapter1);

    }

    void flagPatient() {

        dataP = new ArrayList<String>();
        idsP = new ArrayList<String>();
        getPatient(dataP,idsP);
    }

    synchronized void getPatient(List<String> p,List<String> idse) {
        //sample of virtual Patients  for test 'should comment it after writing db code'
        //<!--
        /*p.add("keko ashraf hmayel : 1047823622");
        p.add("aaa bbb ccc : 123456789");
        p.add("yassein fareid ghanm : 1025748965");
        p.add("omar shafeq hady : 1000557458");
        p.add("zoew dorar awwad : 1025878963");*/
        //-->

        // complet db code to get Patient Name and ID
        // code ...

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String doctorId = firebaseAuth.getCurrentUser().getUid();

        CollectionReference patientsRefs = db.collection("doctors").document(doctorId)
                .collection("patients");
        patientsRefs.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {

                if( task.getResult().size() == 0)
                {
                    spinnerP.setAdapter(null);
                    return;
                }

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Patient patient = document.toObject(Patient.class);
                    p.add(patient.getName()+ " : " + patient.getIdentificationNumber());
                    idse.add(patient.getPatientId());
                }

                //<!--
                adapter1 = new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, dataP);
                spinnerP.setAdapter(adapter1);

                spinnerP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // if(true)return;
                        String temp = "" + parent.getItemAtPosition(position).toString();
                        patientName = temp.substring(0,temp.indexOf(" : "));
                        patientId = temp.substring((temp.indexOf(" : ")+3),temp.length());
                        patientPOS = position;
                        ((TextView) spinnerP.getSelectedView()).setTextColor(Color.rgb(129,212,250));
                        //!complet
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        String temp = "" + parent.getItemAtPosition(patientPOS).toString();
                        patientName = temp.substring(0,temp.indexOf(" : "));
                        patientId = temp.substring((temp.indexOf(" : ")+3),temp.length());
                        ((TextView) spinnerP.getSelectedView()).setTextColor(Color.rgb(129,212,250));
                        //!complet
                    }
                });

                //-->

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

    void searchDO()
    {
        String text = searchP.getText().toString();

        if(text.isEmpty())
        {
            AlertDialog.Builder x = new AlertDialog.Builder(this);
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
            AlertDialog.Builder x = new AlertDialog.Builder(this);
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

                System.out.print("\b");
                Toast.makeText(getApplicationContext(),""
                        +( ((TextView) spinnerP.getSelectedView()).getText().toString()
                        +" is Selected"),Toast.LENGTH_SHORT).show();

            }
        }, 100);

    }

    void adD(){

        if(Stamp.isEmpty())
        {
            Toasty.showText(getApplicationContext(),"Please Set Time And Date to Appoitmenta",Toasty.WARNING,Toast.LENGTH_LONG);
            return;
        }

        String year =""+ dateTimeDialogFragment.getYear();
        String month =""+ (dateTimeDialogFragment.getMonth()+1); if(month.length()==1)month = "0"+month;
        String day =""+ dateTimeDialogFragment.getDay(); if(day.length()==1)day= "0"+day;
        String hour =""+ dateTimeDialogFragment.getHourOfDay(); if(hour.length()==1)hour = "0"+hour;
        String mnt =""+ dateTimeDialogFragment.getMinute();  if(mnt.length()==1)mnt = "0"+mnt;
        String date_T = dateTimeDialogFragment.getYear()+"-"+dateTimeDialogFragment.getMonth()+"-"+dateTimeDialogFragment.getDay()
                +" "+dateTimeDialogFragment.getHourOfDay()+":"+dateTimeDialogFragment.getMinute();

        java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(year+"-"+month+"-"+day+" "+hour+":"+mnt+":0.0");
        //year+"-"+month+"-"+day+" "+hour+":"+mnt+":0.0"
        //  dataTest.put("timestamp", timestamp);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String re=simpleDateFormat.format(Timestamp.now().toDate());
        re= re.substring(0,(re.lastIndexOf(":")));

        String be = timestamp.toString().substring(0,timestamp.toString().lastIndexOf(":"));
        //System.out.println("1- --------->"+re);
       // System.out.println("2- --------->"+be);

        if(be.compareTo(re)<=0)
        {
            Toasty.showText(getApplicationContext(),"Please Set Correctly Time And Date to Appoitmenta",Toasty.WARNING,Toast.LENGTH_LONG);
            return;
        }

        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO Add New Test Appointment?" ).setTitle ( "Add New Test Appointment." )

                .setPositiveButton ( "YES_ADD", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addDo(timestamp,date_T);
                        return; }
                } )

                .setNegativeButton ( "CANCEL", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { return; }
                })

                .setIcon(R.drawable.qus)
                .setPositiveButtonIcon (getDrawable ( R.drawable.yes))
                .setNegativeButtonIcon(getDrawable ( R.drawable.no))
                .show ();
    }


    void addDo(java.sql.Timestamp timestamp,String date_T)
    {


        String s3 = "Patient Name: "+patientName
                +"\nPatient Id: "+patientId
                +"\nAppointment: "+testName
                +"\nTimeStamp: "+timestamp;

        // add code dd
        //<!--
        addF(testName, timestamp,patientId,patientName,date_T);
        //-->

        Toasty.showText(getApplicationContext(),"Assign Appointment is Done",Toasty.SUCCESS,Toast.LENGTH_SHORT);


    }

    void addF(String type,  java.sql.Timestamp timestamp, String patientId, String patientName,String date_T )
    {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String doctorId = firebaseAuth.getCurrentUser().getUid();


        DocumentReference docRef =db.collection("doctors") // table
                .document(doctorId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                   String idD = (String) document.get("identificationNumber");
                   String nmD =  (String) document.get("name");

                    Map<String, Object> data = new HashMap<>();
                    String type1 = type.equals("Doctor Appointment")? "Patient Appointment":(""+type);
                    data.put("type",type1);
                    data.put("timestamp", timestamp);
                    data.put("expired", false);
                    data.put("description",
                            descripeC.getText().toString().isEmpty()? "" : descripeC.getText().toString() );
                    data.put("patientId",patientId);
                    data.put("patientName",patientName);

                    Task<Void> dec =db.collection("doctors")
                            .document(doctorId)
                            .collection("appointments")
                            .document(type+" : "+date_T)
                            .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });

                    data = new HashMap<>();
                    data.put("type",type);
                    data.put("timestamp", timestamp);
                    data.put("expired", false);
                    data.put("description",
                            descripeC.getText().toString().isEmpty()? "" : descripeC.getText().toString() );
                    data.put("doctorId",idD);
                    data.put("doctorName",nmD);

                     dec =db.collection("patients")
                            .document(idsP.get(patientPOS))
                            .collection("appointments")
                            .document(type+" : "+date_T)
                            .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        if(v == add)
        {
            adD();
            return;
        }

        if(v == clear)
        {
            searchP.setText("");
            return;
        }

        if(v == search)
        {
            searchDO();
            return;
        }
    }



    //

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if(v==searchP)
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
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getDoctorDrawer(this, 4);
    }
}