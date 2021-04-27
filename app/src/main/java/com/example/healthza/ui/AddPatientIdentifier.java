package com.example.healthza.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.NotificationCompat;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import com.example.healthza.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;


public class AddPatientIdentifier extends AppCompatActivity implements View.OnClickListener
        ,CompoundButton.OnCheckedChangeListener
        , View.OnFocusChangeListener {

    private static final String ChannelID = "AddPatientIdentifierNote";

    private EditText inputField[];

    private Button clear;
    private Button add;

    private static final String TAG = "AddPatientIdentifier";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    //
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.patient_menu, menu);
        if (menu != null && menu instanceof MenuBuilder)
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    //
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    //
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    //
    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    //
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //getSupportActionBar ().setTitle ( item.getTitle ()+ "  is pressed" );
        switch (item.getItemId()) {
            case R.id.newIdentifierPM: {
               /* Intent I = new Intent(this, AddPatientIdentifier.class);
                startActivity(I);*/
                break;
            }

            case R.id.newChronicDiseasesPM: {
                Intent I = new Intent(this, newChronicDiseases.class);
                startActivity(I);
                break;
            }

            case R.id.GlucoseTestPM: {
                Intent I = new Intent(this, AddGlucoseTest.class);
                startActivity(I);
                break;
            }

            case R.id.FBStestPM: {
                Intent I = new Intent(this, AddFBStest.class);
                startActivity(I);
                break;
            }

            case R.id.HypertensionTestPM: {
                Intent I = new Intent(this, AddHypertensionTest.class);
                startActivity(I);
                break;
            }

            case R.id.CumulativeTestPM: {
                Intent I = new Intent(this, HbAlc.class);
                startActivity(I);
                break;
            }

            case R.id.KidneysTestPM: {
                Intent I = new Intent(this, AddKidneysTest.class);
                startActivity(I);
                break;
            }

            case R.id.LiverTestPM: {
                Intent I = new Intent(this, AddLiverTest.class);
                startActivity(I);
                break;
            }

            case R.id.CholesterolAndFatsTestPM: {
                Intent I = new Intent(this, AddCholesterolAndFatsTest.class);
                startActivity(I);
                break;
            }

            case R.id.ComprehensiveTestPM: {
                Intent I = new Intent(this, ComprehensiveTest.class);
                startActivity(I);
                break;
            }

            case R.id.requestDoctorPm:
            {
                startActivity(new Intent(this, PatientReceiveRequestActivity.class));
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
                                firebaseAuth.signOut();
                                finishAffinity();
                                Intent I = new Intent(getApplicationContext(),WelcomeActivity.class);
                                startActivity(I);
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
            default: {
            }
        }
        return super.onOptionsItemSelected(item);
    }
    //

    public boolean onSupportNavigateUp() {
        Log.w("Add Patient Identifier.", "onSupportNavigateUp is calll");
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //complet
        for (int i = 0; i < inputField.length; i++) {
            inputField[i].clearFocus();
        }
        return super.onKeyDown(keyCode, event);
    }

    //
    @Override
    public void onBackPressed() {
        //super.onBackPressed ();
        Log.w("Add Patient Identifier.", "this onbackpress is calll");

        AlertDialog.Builder x = new AlertDialog.Builder(this);
        x.setMessage("DO YOU WANT TO EXIT?").setTitle("Exit Activity'Add Patient Identifier'")

                .setPositiveButton("YES_EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w("Add Patient Identifier.", "end");
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
    public void onDestroy() {
        super.onDestroy();
        //complet
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient_identifier);


        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inputField = new EditText[3];

        Log.w("Add Patient Identifier.", "start");
        Toast.makeText(getApplicationContext(), "Add Patient Identifier....", Toast.LENGTH_SHORT).show();

        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeAsUpIndicator(R.drawable.ex);
        bar.setTitle("Add Patient Identifier.");

        inputField[0] = findViewById(R.id.innerIdentifierName);
        inputField[0].setOnFocusChangeListener(this);

        inputField[1] = findViewById(R.id.innerIdentifierPhone);
        inputField[1].setOnFocusChangeListener(this);

        inputField[2] = findViewById(R.id.innerIdentifierRelationship);
        inputField[2].setOnFocusChangeListener(this);


        clear = findViewById(R.id.clearIdentifierInfo);
        clear.setOnClickListener(this);
        add = findViewById(R.id.addIdentifierInfo);
        add.setOnClickListener(this);
    }


    //Empty Fields
    boolean ifEmptyFields() {
        //complet
        boolean empty = false;
        for (int i = 0; i < inputField.length; i++) {
            empty = empty || inputField[i].getText().toString().isEmpty();
        }
        return empty;

    }

    // do
    void adD() {
        //complet
        if (ifEmptyFields()) {
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


        AlertDialog.Builder x = new AlertDialog.Builder(this);
        x.setMessage("DO YOU WANT TO Add Patient Identifier?").setTitle("Add Patient Identifier.")

                .setPositiveButton("YES_ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w("ADD Patient Identifier", "Add Patient Identifier.");
                        // functions and codes
                        //complet

                        addIDentifier();

                        notification();
                        Toast.makeText(getApplicationContext(), "Patient Identifier IS ADD...", Toast.LENGTH_SHORT).show();


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

    }

    // clear
    void cleaR() {
        //complet

        AlertDialog.Builder x = new AlertDialog.Builder(this);
        x.setMessage("DO YOU WANT TO CLEAR FIELDS?").setTitle("Clear Fields")

                .setPositiveButton("YES_CLEAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w("CLEAR FIELDS", "Add Patient Identifier CLEAR FIELDS");
                        Toast.makeText(getApplicationContext(), "FIELDS IS CLEARD...", Toast.LENGTH_SHORT).show();
                        // functions and codes
                        //complet
                        for (int i = 0; i < inputField.length; i++) {
                            inputField[i].setText("");
                        }

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

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {

        if (v == add) {
            adD();
            return;
        }
        if (v == clear) {
            cleaR();
            return;
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        for (int i = 0; i < inputField.length; i++) {
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
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
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

    void notification() {
        NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder note = null;


        createChannel();

        NotificationCompat.BigTextStyle bigtext = new NotificationCompat.BigTextStyle();
        bigtext.setBigContentTitle("Identifier Name:" + inputField[0].getText().toString());
        bigtext.bigText("Identifier Phone:" + inputField[1].getText().toString());
        bigtext.setSummaryText("New Identifier,t Relationship:" + inputField[2].getText().toString());

        note = new NotificationCompat.Builder(getApplicationContext(), ChannelID)
                /*.setContentTitle ( "New  Test ADD"  )
                .setSubText ( "Test Type:"+text
                        +"\nTest Date:"+ datE.getText().toString()
                        +"\nTest Time:"+timE.getText().toString()  )
                .setContentText ("")*/
                .setOngoing(false)
                .setColor(Color.RED)
                .setColorized(true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setShowWhen(true)
                .setUsesChronometer(true)
                .setSmallIcon(R.drawable.icof)
                .setStyle(bigtext)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icof))
                .setAutoCancel(true)
        //.setOnlyAlertOnce(true)
        //.addAction ( R.drawable.no,"Mark Complete", markCompleteIntent);
        ;

        man.notify(++Functions.ne, note.build());
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

    void addIDentifier() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // User is signed in


            String userId = user.getUid();

            //<!-- add test

            Map<String, Object> dataTest = new HashMap<>();
            dataTest.put("name",inputField[0].getText().toString());
            dataTest.put("phone",inputField[1].getText().toString());
            dataTest.put("relationship", inputField[2].getText().toString());

            db.collection("patients") // table
                    .document(userId) // patient id
                    .collection("identifier")// table inside patient table
                    .document(((""+inputField[0].getText().toString())
                            + " : "+inputField[1].getText().toString()))
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
        }
    }
}