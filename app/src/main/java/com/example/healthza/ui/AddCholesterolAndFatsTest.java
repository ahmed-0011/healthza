package com.example.healthza.ui;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import com.example.healthza.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.healthza.ui.Functions.TAG_CT;


public class AddCholesterolAndFatsTest extends AppCompatActivity implements View.OnClickListener
        ,CompoundButton.OnCheckedChangeListener
        , View.OnFocusChangeListener {

    /////////////////////////varible////////////////////////////////////

    private static final String ChannelID = "AddCholesterolAndFatsTestNote";

    CheckBox autoTD;
    ImageView dateI;
    ImageView timeI;

    TextView datE;
    TextView timE;
    TextView td;

    private static final String TAG = "AddCholesterolAndFatsTest";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    int ct = 0;
    int ctt=0;
    float Max[];
    float Min[];
    boolean first = false;
    float latest[];

    private EditText inputField[];

    private Button clear;
    private Button add;
////////////////////////////////////////////////////////////////////////
    @SuppressLint("LongLogTag")
    public boolean onSupportNavigateUp() {
        Log.w("Add Cholesterol And Fats test.", "onSupportNavigateUp is calll");
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    ///////////////////////////////////////
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //complet
        for (int i = 0; i < inputField.length; i++) {
            inputField[i].clearFocus();
        }
        return super.onKeyDown(keyCode, event);
    }

    //
    @SuppressLint("LongLogTag")
 /////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        //super.onBackPressed ();
        Log.w("Add Cholesterol And Fats test.", "this onbackpress is calll");

        AlertDialog.Builder x = new AlertDialog.Builder(this);
        x.setMessage("DO YOU WANT TO EXIT?").setTitle("Exit Activity'Add Cholesterol And Fats test'")

                .setPositiveButton("YES_EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w("Add Cholesterol And Fats test.", "end");
                        Toast.makeText(getApplicationContext(), "Back...", Toast.LENGTH_SHORT).show();
                        //complet
                        finish();
                    }
                })

                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })

                .setIcon(R.drawable.qus)
                .setPositiveButtonIcon(getDrawable(R.drawable.yes))
                .setNegativeButtonIcon(getDrawable(R.drawable.no))
                .show();
        return;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //Functions.pact=-999;
        //complet
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cholesterol_and_fats_test);

        /*Functions.pact=11;
        LayoutInflater inflater1_ = LayoutInflater.from(this);
        View view1_ = inflater1_.inflate(R.layout.drawer_header, null);
        DrawerUtil.headerView = view1_;
        DrawerUtil.getPatientDrawer(this, -1);*/

        inputField = new EditText[4];

        Max = new float[]{ -999,-999,-999,-999};
        Min = new float[]{ 999,999,999,999};
        latest = new float[4];


        Log.w("Add Cholesterol And Fats test.", "start");
        Toast.makeText(getApplicationContext(), "Add Cholesterol And Fats test....", Toast.LENGTH_SHORT).show();
//firbase
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        datE = findViewById(R.id.dateText5);
        timE = findViewById(R.id.timeText5);
        td = findViewById(R.id.textView);

        datE.setText("YYYY/MM/DD");
        timE.setText("HH:MM");


        dateI = findViewById(R.id.DateIcon5);
        dateI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "ٌٌSet Date...", Toast.LENGTH_SHORT).show();
                showDatePickerDialog();
                //complet
            }
        });

        timeI = findViewById(R.id.TimeIcon5);
        timeI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Set Time...", Toast.LENGTH_SHORT).show();
                showTimePickerDialog();
                //complet
            }
        });

        autoTD = findViewById(R.id.TimeDateAuto5);
        autoTD.setChecked(false);
        autoTD.setOnClickListener(this);
        td.setVisibility(View.VISIBLE);

        inputField[0] = findViewById(R.id.innerTriglyceridepercent);
        inputField[0].setOnFocusChangeListener(this);

        inputField[1] = findViewById(R.id.innerLDLCholesterolPercent);
        inputField[1].setOnFocusChangeListener(this);

        inputField[2] = findViewById(R.id.innerHDLCholesterolPercent);
        inputField[2].setOnFocusChangeListener(this);

        inputField[3] = findViewById(R.id.innerCholesterolTotalPercent);
        inputField[3].setOnFocusChangeListener(this);

        clear = findViewById(R.id.ClearCholesterolFatsTest);
        clear.setOnClickListener(this);
        add = findViewById(R.id.AddCholesterolFatsTest);
        add.setOnClickListener(this);

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

    //Date Picker
    public void showDatePickerDialog() {
        Functions.DatePickerFragment.setYear(0);
        Functions.DatePickerFragment.setMonth(0);
        Functions.DatePickerFragment.setDay(0);
        DialogFragment newFragment = new Functions.DatePickerFragment(datE);
        newFragment.show(getSupportFragmentManager(), "datePicker");
        newFragment = null;
    }

    //Time Picker
    public void showTimePickerDialog() {
        Functions.TimePickerFragment.setHour(0);
        Functions.TimePickerFragment.setMinute(0);
        DialogFragment newFragment = new Functions.TimePickerFragment(timE);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        newFragment = null;
    }

    //time and date auto
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        if (autoTD.equals(view)) {
            if (checked) {
                timeI.setEnabled(false);
                dateI.setEnabled(false);
                td.setVisibility(View.INVISIBLE);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/mm/dd hh:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                Functions.timeS = now.getHour()+":"+now.getMinute();
                Functions.dateS = now.getYear()+"-"+now.getMonthValue()+"-"+now.getDayOfMonth();
                timE.setText(Functions.timeS);
                datE.setText(Functions.dateS);
            } else {
                timeI.setEnabled(true);
                dateI.setEnabled(true);
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
            ////dialog
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

         ///////dialog
        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO ADD Cholesterol And Fats TEST?" ).setTitle ( "Add Cholesterol And Fats test" )

                .setPositiveButton ( "YES_ADD", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w ("ADD TEST", "ADD Cholesterol And Fats TEST");
                        // functions and codes
                        //complet
                        addTest();
                        notification("Cholesterol And Fats  Test");
                        Toast.makeText(getApplicationContext(), "Cholesterol And Fats TEST IS ADD...", Toast.LENGTH_SHORT).show();


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
                        Log.w ("CLEAR FIELDS", "Liver Cholesterol And Fats CLEAR FIELDS");
                        Toast.makeText(getApplicationContext(), "FIELDS IS CLEARD...", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), " Tap outside edittext to lose focus ", Toast.LENGTH_SHORT).show();
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

    // notification
    private void createChannel() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel x;
            x = new NotificationChannel(ChannelID, "My  Hi Channel with you", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            man.createNotificationChannel(x);


        }
    }

    void notification(String text)
    {
        NotificationManager man= (NotificationManager)getSystemService ( NOTIFICATION_SERVICE );
        NotificationCompat.Builder  note=null;


        createChannel();

        NotificationCompat.BigTextStyle bigtext = new NotificationCompat.BigTextStyle ();
        bigtext.setBigContentTitle ("Test Type:"+text);
        bigtext.bigText ("Test Date:"+ datE.getText().toString()+ " && Test Time:"+timE.getText().toString() );
        bigtext.setSummaryText ("New  Test ADD");

        note = new NotificationCompat.Builder ( getApplicationContext(),ChannelID )
                /*.setContentTitle ( "New  Test ADD"  )
                .setSubText ( "Test Type:"+text
                        +"\nTest Date:"+ datE.getText().toString()
                        +"\nTest Time:"+timE.getText().toString()  )
                .setContentText ("")*/
                .setOngoing ( false )
                .setColor ( Color.RED  )
                .setColorized ( true )
                .setPriority ( NotificationManager.IMPORTANCE_HIGH )
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setShowWhen ( true )
                .setUsesChronometer ( true )
                .setSmallIcon ( R.drawable.icof)
                .setStyle ( bigtext )
                .setLargeIcon ( BitmapFactory.decodeResource ( getResources (),R.drawable.icof ) )
                .setAutoCancel ( true )
        //.setOnlyAlertOnce(true)
        //.addAction ( R.drawable.no,"Mark Complete", markCompleteIntent);
        ;

        man.notify (++Functions.ne, note.build ());

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
////document to set data
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
            dataTest.put("Triglycerid_percent", Float.parseFloat(inputField[0].getText().toString()));
            dataTest.put("LDLCholesterol_percent", Float.parseFloat(inputField[1].getText().toString()));
            dataTest.put("HDLCholesterol_percent", Float.parseFloat(inputField[2].getText().toString()));
            dataTest.put("CholesterolTotal_percent", Float.parseFloat(inputField[3].getText().toString()));
            dataTest.put("sub",false);
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
            dataTest.put("timestamp", timestamp);
/////document refrence
            DocumentReference DRC =  db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("cholesterolAndFats_test");

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
                        ///document set data
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

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());

                    }
                }
            });

            //end add test -->



        } else {
            // No user is signed in
        }

    }

    void MaxMinThisTestCountSet()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userId = user.getUid();

        DocumentReference DRC = db.collection("patients") // table
                .document(userId) // patient id
                .collection("tests")// table inside patient table
                .document("cholesterolAndFats_test");

        DRC.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot document = task.getResult();
                if (document.get("count") == null

                        || document.get("max_triglyceride") == null
                        || document.get("min_triglyceride") == null

                        || document.get("max_ldl") == null
                        || document.get("min_ldl") == null

                        || document.get("max_hdl") == null
                        || document.get("min_hdl") == null

                        || document.get("max_total") == null
                        || document.get("min_total") == null

                        || document.get("first_triglyceride") == null
                        || document.get("latest_triglyceride") == null

                        || document.get("first_ldl") == null
                        || document.get("latest_ldl") == null

                        || document.get("first_hdl") == null
                        || document.get("latest_hdl") == null

                        || document.get("first_total") == null
                        || document.get("latest_total") == null
                ) {

                    HashMap Mp = new HashMap();
                    Mp.put("count", 0);

                    Mp.put("max_triglyceride",Float.MIN_VALUE);
                    Mp.put("min_triglyceride",Float.MAX_VALUE);

                    Mp.put("max_ldl",Float.MIN_VALUE);
                    Mp.put("min_ldl",Float.MAX_VALUE);

                    Mp.put("max_hdl",Float.MIN_VALUE);
                    Mp.put("min_hdl",Float.MAX_VALUE);

                    Mp.put("max_total",Float.MIN_VALUE);
                    Mp.put("min_total",Float.MAX_VALUE);
                    first = true;

                    DRC.set(Mp)
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
                } else {

                    Max[0] = Float.parseFloat(document.get("max_triglyceride").toString());
                    Min[0] = Float.parseFloat(document.get("min_triglyceride").toString());

                    Max[1] = Float.parseFloat(document.get("max_ldl").toString());
                    Min[1] = Float.parseFloat(document.get("min_ldl").toString());

                    Max[2] = Float.parseFloat(document.get("max_hdl").toString());
                    Min[2] = Float.parseFloat(document.get("min_hdl").toString());

                    Max[3] = Float.parseFloat(document.get("max_total").toString());
                    Min[3] = Float.parseFloat(document.get("min_total").toString());

                    ctt = Integer.parseInt(document.get("count").toString());

                    latest[0]=Float.parseFloat(document.get("latest_triglyceride").toString());
                    latest[1]=Float.parseFloat(document.get("latest_ldl").toString());
                    latest[2]=Float.parseFloat(document.get("latest_hdl").toString());
                    latest[3]=Float.parseFloat(document.get("latest_total").toString());

                    first =false;
                }
            }
        });
    }

    void MaxMinThisTestCountUpdate(DocumentReference DRC,DocumentSnapshot document )
    {
        Max[0] = Float.parseFloat(document.get("max_triglyceride").toString());
        Min[0] = Float.parseFloat(document.get("min_triglyceride").toString());

        Max[1] = Float.parseFloat(document.get("max_ldl").toString());
        Min[1] = Float.parseFloat(document.get("min_ldl").toString());

        Max[2] = Float.parseFloat(document.get("max_hdl").toString());
        Min[2] = Float.parseFloat(document.get("min_hdl").toString());

        Max[3] = Float.parseFloat(document.get("max_total").toString());
        Min[3] = Float.parseFloat(document.get("min_total").toString());

        latest[0]=Float.parseFloat(inputField[0].getText().toString());
        latest[1]=Float.parseFloat(inputField[1].getText().toString());
        latest[2]=Float.parseFloat(inputField[2].getText().toString());
        latest[3]=Float.parseFloat(inputField[3].getText().toString());

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
        if (Float.parseFloat(inputField[0].getText().toString()) > Max[0]) {
            Max[0] = Float.parseFloat(inputField[0].getText().toString());
        }
        DRC.update("max_triglyceride", Max[0])
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
        if (Float.parseFloat(inputField[0].getText().toString()) < Min[0]) {
            Min[0] = Float.parseFloat(inputField[0].getText().toString());
        }
        DRC.update("min_triglyceride", Min[0])
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


        //
        if (Float.parseFloat(inputField[1].getText().toString()) > Max[1]) {
            Max[1] = Float.parseFloat(inputField[1].getText().toString());
        }
        DRC.update("max_ldl", Max[1])
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
        if (Float.parseFloat(inputField[1].getText().toString()) < Min[1]) {
            Min[1] = Float.parseFloat(inputField[1].getText().toString());
        }
        DRC.update("min_ldl", Min[1])
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


        //
        if (Float.parseFloat(inputField[2].getText().toString()) > Max[2]) {
            Max[2] = Float.parseFloat(inputField[2].getText().toString());
        }
        DRC.update("max_hdl", Max[2])
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
        if (Float.parseFloat(inputField[2].getText().toString()) < Min[2]) {
            Min[2] = Float.parseFloat(inputField[2].getText().toString());
        }
        DRC.update("min_hdl", Min[2])
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

        //
        if (Float.parseFloat(inputField[3].getText().toString()) > Max[3]) {
            Max[3] = Float.parseFloat(inputField[3].getText().toString());
        }
        DRC.update("max_total", Max[3])
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
        if (Float.parseFloat(inputField[3].getText().toString()) < Min[3]) {
            Min[3] = Float.parseFloat(inputField[3].getText().toString());
        }
        DRC.update("min_total", Min[3])
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

        DRC.update("latest_triglyceride", latest[0])
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

        DRC.update("latest_ldl", latest[1])
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

        DRC.update("latest_hdl", latest[2])
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

        DRC.update("latest_total", latest[3])
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

        if(first)
        {
            DRC.update("first_triglyceride", latest[0])
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

            DRC.update("first_ldl", latest[1])
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

            DRC.update("first_hdl", latest[2])
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

            DRC.update("first_total", latest[3])
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
            first = false;
        }
    }

}
