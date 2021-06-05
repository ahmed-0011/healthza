package com.project.cdh.ui;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.project.cdh.DrawerUtil;
import com.project.cdh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.cdh.Toasty;

import java.text.SimpleDateFormat;
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

public class PatientAddComprehensiveTestActivity extends AppCompatActivity implements View.OnClickListener
        ,CompoundButton.OnCheckedChangeListener
        , View.OnFocusChangeListener
{

    private ViewFlipper viewFlipper;

    CheckBox autoTD;
    FloatingActionButton stamp;
    SwitchDateTimeDialogFragment dateTimeDialogFragment;

    TextView datE;
    TextView timE;
    TextView td;

    boolean f=true;

    private EditText inputField [];

    private Button clear;
    private Button add;

    private static final String TAG = "PatientAddKidneysTestActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    int ct = 0;

    public boolean onSupportNavigateUp()
    {
        Log.w ("Add Comprehensive test.", "onSupportNavigateUp is calll");
        onBackPressed ();
        return super.onSupportNavigateUp ();
    }
    //
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //complet
        for(int i=0;i<inputField.length;i++){ inputField[i].clearFocus(); }
        return super.onKeyDown(keyCode, event);
    }
    //
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed ();
        Log.w ("Add Comprehensive test.", "this onbackpress is calll");

        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO EXIT?" ).setTitle ( "Exit Activity'Add Comprehensive test'" )

                .setPositiveButton ( "YES_EXIT", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w ("Add Comprehensive test.", "end");
                        Toasty.showText(getApplicationContext(),"Back...",Toasty.INFORMATION,Toast.LENGTH_SHORT);
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
        setContentView(R.layout.activity_patient_add_comprehensive_test);

        inputField = new EditText[33];

        Log.w ("Add Comprehensive test.", "start");
        Toasty.showText(getApplicationContext(),"New Comprehensive test...",Toasty.INFORMATION,Toast.LENGTH_SHORT);

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

        inputField[0] = findViewById(R.id.innerComprehensive1Percent);
        inputField[0].setOnFocusChangeListener(this);

        inputField[1] = findViewById(R.id.innerComprehensive2Percent);
        inputField[1].setOnFocusChangeListener(this);

        inputField[2] = findViewById(R.id.innerComprehensive3Percent);
        inputField[2].setOnFocusChangeListener(this);

        inputField[3] = findViewById(R.id.innerComprehensive4Percent);
        inputField[3].setOnFocusChangeListener(this);

        inputField[4] = findViewById(R.id.innerComprehensive5Percent);
        inputField[4].setOnFocusChangeListener(this);

        inputField[5] = findViewById(R.id.innerComprehensive6Percent);
        inputField[5].setOnFocusChangeListener(this);

        inputField[6] = findViewById(R.id.innerComprehensive7Percent);
        inputField[6].setOnFocusChangeListener(this);

        inputField[7] = findViewById(R.id.innerComprehensive8Percent);
        inputField[7].setOnFocusChangeListener(this);

        inputField[8] = findViewById(R.id.innerComprehensive9Percent);
        inputField[8].setOnFocusChangeListener(this);

        inputField[9] = findViewById(R.id.innerComprehensive10Percent);
        inputField[9].setOnFocusChangeListener(this);

        inputField[10] = findViewById(R.id.innerComprehensive11Percent);
        inputField[10].setOnFocusChangeListener(this);

        inputField[11] = findViewById(R.id.innerComprehensive12Percent);
        inputField[11].setOnFocusChangeListener(this);

        inputField[12] = findViewById(R.id.innerComprehensive13Percent);
        inputField[12].setOnFocusChangeListener(this);

        inputField[13] = findViewById(R.id.innerComprehensive14Percent);
        inputField[13].setOnFocusChangeListener(this);

        inputField[14] = findViewById(R.id.innerComprehensive15Percent);
        inputField[14].setOnFocusChangeListener(this);

        inputField[15] = findViewById(R.id.innerComprehensive16Percent);
        inputField[15].setOnFocusChangeListener(this);

        inputField[16] = findViewById(R.id.innerComprehensive17Percent);
        inputField[16].setOnFocusChangeListener(this);

        inputField[17] = findViewById(R.id.innerComprehensive18Percent);
        inputField[17].setOnFocusChangeListener(this);

        inputField[18] = findViewById(R.id.innerComprehensive19Percent);
        inputField[18].setOnFocusChangeListener(this);

        inputField[19] = findViewById(R.id.innerComprehensive20Percent);
        inputField[19].setOnFocusChangeListener(this);

        inputField[20] = findViewById(R.id.innerComprehensive21Percent);
        inputField[20].setOnFocusChangeListener(this);

        inputField[21] = findViewById(R.id.innerComprehensive22Percent);
        inputField[21].setOnFocusChangeListener(this);

        inputField[22] = findViewById(R.id.innerComprehensive23Percent);
        inputField[22].setOnFocusChangeListener(this);

        inputField[23] = findViewById(R.id.innerComprehensive24Percent);
        inputField[23].setOnFocusChangeListener(this);

        inputField[24] = findViewById(R.id.innerComprehensive25Percent);
        inputField[24].setOnFocusChangeListener(this);

        inputField[25] = findViewById(R.id.innerComprehensive26Percent);
        inputField[25].setOnFocusChangeListener(this);

        inputField[26] = findViewById(R.id.innerComprehensive27Percent);
        inputField[26].setOnFocusChangeListener(this);

        inputField[27] = findViewById(R.id.innerComprehensive28Percent);
        inputField[27].setOnFocusChangeListener(this);

        inputField[28] = findViewById(R.id.innerComprehensive29Percent);
        inputField[28].setOnFocusChangeListener(this);

        inputField[29] = findViewById(R.id.innerComprehensive30Percent);
        inputField[29].setOnFocusChangeListener(this);

        inputField[30] = findViewById(R.id.innerComprehensive31Percent);
        inputField[30].setOnFocusChangeListener(this);

        inputField[31] = findViewById(R.id.innerComprehensive32Percent);
        inputField[31].setOnFocusChangeListener(this);

        inputField[32] = findViewById(R.id.innerComprehensive33Percent);
        inputField[32].setOnFocusChangeListener(this);


        clear = findViewById(R.id.ClearComprehensiveTest); clear.setOnClickListener (this);
        add = findViewById(R.id.AddComprehensiveTest); add.setOnClickListener(this);



        viewFlipper = findViewById(R.id.view_flipper);
       /* TextView textView = new TextView(this);
        textView.setText("Dynamically added TextView");
        textView.setGravity(Gravity.CENTER);

        viewFlipper.addView(textView);
*/
        //viewFlipper.setFlipInterval(2000);
        //viewFlipper.startFlipping();
    }

    public void previousView(View v) {
        //viewFlipper.setInAnimation(this, R.anim.slide_in_right);
        //viewFlipper.setOutAnimation(this, R.anim.slide_out_left);
        viewFlipper.showPrevious();
    }

    public void nextView(View v) {
        viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
        viewFlipper.showNext();
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
        boolean empty=false;
        for(int i=0; i<inputField.length;i++) { empty = empty || inputField[i].getText().toString().isEmpty(); }
        empty = empty || timE.getText().toString().equals("HH:MM");
        empty = empty || datE.getText().toString().equals("YYYY/MM/DD");
        return empty;

    }

    // do
    void adD()
    {
        //complet
        if(ifEmptyFields())
        {
     //       Toasty.showText(getApplicationContext(), "Please complete fill the form data...",Toasty.ERROR, Toast.LENGTH_SHORT);
            return;
        }

        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO ADD Comprehensive TEST?" ).setTitle ( "Add Comprehensive test" )

                .setPositiveButton ( "YES_ADD", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w ("ADD TEST", "ADD Comprehensive TEST");
                        // functions and codes
                        //complet
                        if(f)
                        {
                            setAddOPT();

                            return;
                        }
                        addTest();
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
                        Log.w ("CLEAR FIELDS", "Comprehensive TEST CLEAR FIELDS");
                        Toasty.showText(getApplicationContext(),"FIELDS IS CLEARD...",Toasty.INFORMATION,Toast.LENGTH_SHORT);
                        // functions and codes
                        //complet
                        autoTD.setChecked(false);
                        autoTD.callOnClick();
                        datE.setText("YYYY/MM/DD");
                        timE.setText("HH:MM");
                        for(int i=0;i<inputField.length;i++){inputField[i].setText("");}

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

        for(int i=0; i<inputField.length;i++) {
            if (v == inputField[i]) {
                if (!hasFocus) {
                    Log.d("focus", "focus lost");
                    // Do whatever you want here
                } else {
                   // Toast.makeText(getApplicationContext(), " Tap outside edittext to lose focus ", Toast.LENGTH_SHORT).show();
                    Log.d("focus", "focused");
                }

                return;
            }
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

            //Time Stamp
            String Date =datE.getText().toString();
            int i1 = Date.indexOf("-");
            int i2 = Date.lastIndexOf("-");

            String year = Date.substring(0,i1);
            String month = Date.substring(i1+1,i2); if(month.length()==1)month = "0"+month;
            String day = Date.substring(i2+1); if(day.length()==1)day= "0"+day;

            String Time = timE.getText().toString();
            String hour = Time.substring(0,Time.indexOf(":")); if(hour.length()==1)hour = "0"+hour;
            String mnt = Time.substring(Time.indexOf(":")+1); if(mnt.length()==1)mnt = "0"+mnt;
            System.out.println(year+"-"+month+"-"+day+" "+hour+":"+mnt+":0.0");
            java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(year+"-"+month+"-"+day+" "+hour+":"+mnt+":0.0");
            //year+"-"+month+"-"+day+" "+hour+":"+mnt+":0.0"


            Map<String, Object> dataTest = new HashMap<>();
            dataTest.put("date_add", datE.getText().toString());
            dataTest.put("time_add", timE.getText().toString());
            dataTest.put("fbs_percent", Float.parseFloat(inputField[0].getText().toString()));
            dataTest.put("sub", true);
            dataTest.put("timestamp", timestamp);
            dataTest.put("type", "fbs");


            DocumentReference DRC = db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("fbs_test");

            Map<String, Object> finalDataTest = dataTest;
            DocumentReference finalDRC4 = DRC;
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
                            finalDRC4.set(datae)
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
                                finalDRC4.update("dates", dates)
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
                        //
                        finalDRC4.collection(datE.getText().toString())
                                .document("test# : "+ct)
                                .set(finalDataTest)
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

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());

                    }
                }
            });

            //end add test -->


            //
            //

            dataTest = new HashMap<>();
            dataTest.put("date_add", datE.getText().toString());
            dataTest.put("time_add", timE.getText().toString());
            dataTest.put("SGOT_percent", Float.parseFloat(inputField[4].getText().toString()));
            dataTest.put("SGPT_percent", Float.parseFloat(inputField[3].getText().toString()));
            dataTest.put("GGT_percent", Float.parseFloat(inputField[2].getText().toString()));
            dataTest.put("AlkPhosphatese_percent", Float.parseFloat(inputField[1].getText().toString()));
            dataTest.put("sub", true);
            dataTest.put("timestamp", timestamp);
            dataTest.put("type", "liver");

           DRC = db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("liver_test");

            Map<String, Object> finalDataTest1 = dataTest;
            DocumentReference finalDRC = DRC;
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
                            finalDRC.set(datae)
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
                                finalDRC.update("dates", dates)
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
                        //
                        finalDRC.collection(datE.getText().toString())
                                .document("test# : "+ct)
                                .set(finalDataTest1)
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

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());

                    }
                }
            });

            //end add test -->


            //
            //

            dataTest = new HashMap<>();
            dataTest.put("date_add", datE.getText().toString());
            dataTest.put("time_add", timE.getText().toString());
            dataTest.put("UricAcid_percent", Float.parseFloat(inputField[5].getText().toString()));
            dataTest.put("Urea_percent", Float.parseFloat(inputField[6].getText().toString()));
            dataTest.put("Creatinine_percent", Float.parseFloat(inputField[7].getText().toString()));
            dataTest.put("sub", true);
            dataTest.put("timestamp", timestamp);
            dataTest.put("type", "kidneys");

            DRC = db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("Kidneys_test");

            DocumentReference finalDRC1 = DRC;
            Map<String, Object> finalDataTest2 = dataTest;
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
                            finalDRC1.set(datae)
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
                                finalDRC1.update("dates", dates)
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
                        //
                        finalDRC1.collection(datE.getText().toString())
                                .document("test# : "+ct)
                                .set(finalDataTest2)
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

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());

                    }
                }
            });

            //end add test -->


            //
            //

            dataTest = new HashMap<>();
            dataTest.put("date_add", datE.getText().toString());
            dataTest.put("time_add", timE.getText().toString());
            dataTest.put("Triglycerid_percent", Float.parseFloat(inputField[8].getText().toString()));
            dataTest.put("LDLCholesterol_percent", Float.parseFloat(inputField[9].getText().toString()));
            dataTest.put("HDLCholesterol_percent", Float.parseFloat(inputField[10].getText().toString()));
            dataTest.put("CholesterolTotal_percent", Float.parseFloat(inputField[11].getText().toString()));
            dataTest.put("sub", true);
            dataTest.put("timestamp", timestamp);
            dataTest.put("type", "cholesterol");

            DRC = db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("cholesterolAndFats_test");

            Map<String, Object> finalDataTest3 = dataTest;
            DocumentReference finalDRC2 = DRC;
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
                            finalDRC2.set(datae)
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
                                finalDRC2.update("dates", dates)
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
                        //
                        finalDRC2.collection(datE.getText().toString())
                                .document("test# : "+ct)
                                .set(finalDataTest3)
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

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());

                    }
                }
            });

            //end add test -->


            //
            //

            dataTest = new HashMap<>();
            dataTest.put("date_add", datE.getText().toString());
            dataTest.put("time_add", timE.getText().toString());
            dataTest.put("albumin_percent", Float.parseFloat(inputField[12].getText().toString()));
            dataTest.put("acpTotal_percent", Float.parseFloat(inputField[13].getText().toString()));
            dataTest.put("calcium_percent", Float.parseFloat(inputField[14].getText().toString()));
            dataTest.put("chloride_percent", Float.parseFloat(inputField[15].getText().toString()));
            dataTest.put("sIron_percent", Float.parseFloat(inputField[16].getText().toString()));
            dataTest.put("tibc_percent", Float.parseFloat(inputField[17].getText().toString()));
            dataTest.put("acpProstatic_percent", Float.parseFloat(inputField[18].getText().toString()));
            dataTest.put("amylase_percent", Float.parseFloat(inputField[19].getText().toString()));
            dataTest.put("potassium_percent", Float.parseFloat(inputField[20].getText().toString()));
            dataTest.put("sodium_percent", Float.parseFloat(inputField[21].getText().toString()));
            dataTest.put("2hpps_percent", Float.parseFloat(inputField[22].getText().toString()));
            dataTest.put("rbs_percent", Float.parseFloat(inputField[23].getText().toString()));
            dataTest.put("bilirubinTotal_percent", Float.parseFloat(inputField[24].getText().toString()));
            dataTest.put("bilirubinDirect_percent", Float.parseFloat(inputField[25].getText().toString()));
            dataTest.put("psa_percent", Float.parseFloat(inputField[26].getText().toString()));
            dataTest.put("phosphours_percent", Float.parseFloat(inputField[27].getText().toString()));
            dataTest.put("ldh_percent", Float.parseFloat(inputField[28].getText().toString()));
            dataTest.put("ckMb_percent", Float.parseFloat(inputField[29].getText().toString()));
            dataTest.put("cpk_percent", Float.parseFloat(inputField[30].getText().toString()));
            dataTest.put("tProtein_percent", Float.parseFloat(inputField[31].getText().toString()));
            dataTest.put("magnesium_percent", Float.parseFloat(inputField[32].getText().toString()));
            dataTest.put("timestamp", timestamp);
            dataTest.put("type", "comprehensive");

           DRC = db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("other_test");

            DocumentReference finalDRC3 = DRC;
            Map<String, Object> finalDataTest4 = dataTest;
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
                            finalDRC3.set(datae)
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
                                finalDRC3.update("dates", dates)
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
                        //
                        finalDRC3.collection(datE.getText().toString())
                                .document("test# : "+ct)
                                .set(finalDataTest4)
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

            //end add test -->


        } else {
            // No user is signed in
        }

        Toasty.showText(getApplicationContext(),"successfully submit Comprehensive TEST...",Toasty.SUCCESS,Toast.LENGTH_SHORT);
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
                @SuppressLint("LongLogTag")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            String cte = "" + document.getData().toString();
                            ct = Integer.parseInt(cte.substring(7,cte.length()-1));
                            addTest();
                        } else {
                            Log.d(TAG, "No such document");
                            ct = 0;
                            addTest();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        ct = 0;
                        addTest();
                    }
                }
            });

        }
        //end get tests Count-->
    }
    @Override
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getPatientDrawer(this, 16);

    }
}