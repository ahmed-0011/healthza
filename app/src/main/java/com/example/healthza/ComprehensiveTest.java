package com.example.healthza;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import android.os.Bundle;

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
import java.util.HashMap;
import java.util.Map;

import static com.example.healthza.Functions.TAG_CT;

public class ComprehensiveTest extends AppCompatActivity implements View.OnClickListener
        ,CompoundButton.OnCheckedChangeListener
        , View.OnFocusChangeListener
{

    private static final  String ChannelID= "ADDComprehensiveTestNote";

    private ViewFlipper viewFlipper;

    CheckBox autoTD;

    TextView td;
    TextView datE;
    TextView timE;

    ImageView dateI;
    ImageView timeI;

    private EditText inputField [];

    private Button clear;
    private Button add;

    private static final String TAG = "AddKidneysTest";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    int ct = 0;

    //
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inf=getMenuInflater ();
        inf.inflate (R.menu.patient_menu,menu);
        if (menu!=null && menu instanceof MenuBuilder)
            ((MenuBuilder)menu).setOptionalIconsVisible ( true );
        return super.onCreateOptionsMenu ( menu );
    }
    //
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) { return super.onPrepareOptionsMenu ( menu ); }
    //
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) { return super.onMenuOpened ( featureId, menu ); }
    //
    @Override
    public void onOptionsMenuClosed(Menu menu) { super.onOptionsMenuClosed ( menu ); }
    //
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        //getSupportActionBar ().setTitle ( item.getTitle ()+ "  is pressed" );
        switch(item.getItemId())
        {
            case R.id.newIdentifierPM:
            {
                Intent I = new Intent(this, AddPatientIdentifier.class);
                startActivity(I);
                break;
            }

            case R.id.newChronicDiseasesPM:
            {
                Intent I = new Intent(this, newChronicDiseases.class);
                startActivity(I);
                break;
            }

            case R.id.GlucoseTestPM:
            {
                Intent I = new Intent(this, AddGlucoseTest.class);
                startActivity(I);
                break;
            }

            case R.id.FBStestPM:
            {
                Intent I = new Intent(this, AddFBStest.class);
                startActivity(I);
                break;
            }

            case R.id.HypertensionTestPM:
            {
                Intent I = new Intent(this, AddHypertensionTest.class);
                startActivity(I);
                break;
            }

            case R.id.CumulativeTestPM:
            {
                Intent I = new Intent(this, HbAlc.class);
                startActivity(I);
                break;
            }

            case R.id.KidneysTestPM:
            {
                Intent I = new Intent(this, AddKidneysTest .class);
                startActivity(I);
                break;
            }

            case R.id.LiverTestPM:
            {
                Intent I = new Intent(this, AddLiverTest.class);
                startActivity(I);
                break;
            }

            case R.id.CholesterolAndFatsTestPM:
            {
                Intent I = new Intent(this, AddCholesterolAndFatsTest.class);
                startActivity(I);
                break;
            }

            case R.id.ComprehensiveTestPM:
            {
                /*Intent I = new Intent(this, ComprehensiveTest.class);
                startActivity(I);*/
                break;
            }

            case R.id.logOutPM:
            {

                AlertDialog.Builder   x= new AlertDialog.Builder ( this );
                x.setMessage ( "DO YOU WANT TO LogOut?" ).setTitle ( "Patient LogOut" )

                        .setPositiveButton ( "YES_EXIT", new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "LogedOut...", Toast.LENGTH_SHORT).show();
                                //complet
                                // finish();
                                finishAffinity();
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

                break;
            }
            default:{}
        }
        return super.onOptionsItemSelected ( item );
    }
    //

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprehensive_test);

        inputField = new EditText[33];

        Log.w ("Add Comprehensive test.", "start");
        Toast.makeText(getApplicationContext(), "Add Comprehensive test....", Toast.LENGTH_SHORT).show();

        ActionBar bar = getSupportActionBar ();
        bar.setHomeButtonEnabled ( true );
        bar.setDisplayHomeAsUpEnabled ( true );
        bar.setHomeAsUpIndicator ( R.drawable.ex);
        bar.setTitle("Add Comprehensive test.");

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        datE = findViewById(R.id.dateText7);
        timE = findViewById(R.id.timeText7);
        td = findViewById(R.id.textView);

        datE.setText("YYYY/MM/DD");
        timE.setText("HH:MM");

        dateI = findViewById(R.id.DateIcon7);
        dateI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "ٌٌSet Date...", Toast.LENGTH_SHORT).show();
                showDatePickerDialog();
                //complet
            }
        });

        timeI = findViewById(R.id.TimeIcon7);
        timeI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Set Time...", Toast.LENGTH_SHORT).show();
                showTimePickerDialog();
                //complet
            }
        });

        autoTD = findViewById(R.id.TimeDateAuto7);
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


    //Date Picker
    public void showDatePickerDialog() {
        Functions.DatePickerFragment.setYear(0); Functions.DatePickerFragment.setMonth(0); Functions.DatePickerFragment.setDay(0);
        DialogFragment newFragment = new Functions.DatePickerFragment(datE);
        newFragment.show(getSupportFragmentManager(), "datePicker");
        newFragment = null;
    }

    //Time Picker
    public void showTimePickerDialog() {
        Functions.TimePickerFragment.setHour(0); Functions.TimePickerFragment.setMinute(0);
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


        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO ADD Comprehensive TEST?" ).setTitle ( "Add Comprehensive test" )

                .setPositiveButton ( "YES_ADD", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w ("ADD TEST", "ADD Comprehensive TEST");
                        // functions and codes
                        //complet

                        addTest();

                        notification("Comprehensive Test");
                        Toast.makeText(getApplicationContext(), "Comprehensive TEST IS ADD...", Toast.LENGTH_SHORT).show();


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
            dataTest.put("fbs_percent", Float.parseFloat(inputField[0].getText().toString()));

            db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("fbs_test")
                    .collection(datE.getText().toString())
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

            //

            dataTest = new HashMap<>();
            dataTest.put("date_add", datE.getText().toString());
            dataTest.put("time_add", timE.getText().toString());
            dataTest.put("SGOT_percent", Float.parseFloat(inputField[4].getText().toString()));
            dataTest.put("SGPT_percent", Float.parseFloat(inputField[3].getText().toString()));
            dataTest.put("GGT_percent", Float.parseFloat(inputField[2].getText().toString()));
            dataTest.put("AlkPhosphatese_percent", Float.parseFloat(inputField[1].getText().toString()));

            db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("liver_test")
                    .collection(datE.getText().toString())
                    .document("test# : "+ct)
                    .set(dataTest)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
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

            //

            dataTest = new HashMap<>();
            dataTest.put("date_add", datE.getText().toString());
            dataTest.put("time_add", timE.getText().toString());
            dataTest.put("UricAcid_percent", Float.parseFloat(inputField[5].getText().toString()));
            dataTest.put("Urea_percent", Float.parseFloat(inputField[6].getText().toString()));
            dataTest.put("Creatinine_percent", Float.parseFloat(inputField[7].getText().toString()));

            db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("Kidneys_test")
                    .collection(datE.getText().toString())
                    .document("test# : "+ct)
                    .set(dataTest)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
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

            //

            dataTest = new HashMap<>();
            dataTest.put("date_add", datE.getText().toString());
            dataTest.put("time_add", timE.getText().toString());
            dataTest.put("Triglycerid_percent", Float.parseFloat(inputField[8].getText().toString()));
            dataTest.put("LDLCholesterol_percent", Float.parseFloat(inputField[9].getText().toString()));
            dataTest.put("HDLCholesterol_percent", Float.parseFloat(inputField[10].getText().toString()));
            dataTest.put("CholesterolTotal_percent", Float.parseFloat(inputField[11].getText().toString()));

            db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("cholesterolAndFats_test")
                    .collection(datE.getText().toString())
                    .document("test# : "+ct)
                    .set(dataTest)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
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

            db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("tests")// table inside patient table
                    .document("other_test")
                    .collection(datE.getText().toString())
                    .document("test# : "+ct)
                    .set(dataTest)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
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
            //end add test -->


        } else {
            // No user is signed in
        }

    }


}