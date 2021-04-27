package com.example.healthza.ui;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.NotificationCompat;

import com.example.healthza.R;
import com.example.healthza.ui.AddCholesterolAndFatsTest;
import com.example.healthza.ui.AddFBStest;
import com.example.healthza.ui.AddGlucoseTest;
import com.example.healthza.ui.AddHypertensionTest;
import com.example.healthza.ui.AddKidneysTest;
import com.example.healthza.ui.AddLiverTest;
import com.example.healthza.ui.AddPatientIdentifier;
import com.example.healthza.ui.ComprehensiveTest;
import com.example.healthza.ui.Functions;
import com.example.healthza.ui.HbAlc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class newChronicDiseases extends AppCompatActivity implements View.OnClickListener {


    private static final String ChannelID = "newChronicDiseasesNote";

    boolean emp = false;
    List<String> data;
    List<String> data2;
    private Button add;
    private Spinner spinner1;
    private DatePicker datePicker;
    private String name_ = "";
    private String type_ = "";
    private String itemTemp = "";

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
                Intent I = new Intent(this, AddPatientIdentifier.class);
                startActivity(I);
                break;
            }

            case R.id.newChronicDiseasesPM: {
               /* Intent I = new Intent(this, newChronicDiseases.class);
                startActivity(I);*/
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

            case R.id.logOutPM: {

                AlertDialog.Builder x = new AlertDialog.Builder(this);
                x.setMessage("DO YOU WANT TO LogOut?").setTitle("Patient LogOut")

                        .setPositiveButton("YES_EXIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "LogedOut...", Toast.LENGTH_SHORT).show();
                                //complet
                                // finish();
                                finishAffinity();
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

                break;
            }
            default: {
            }
        }
        return super.onOptionsItemSelected(item);
    }
    //

    @SuppressLint("LongLogTag")
    public boolean onSupportNavigateUp() {
        Log.w("Add New Chronic Diseases.", "onSupportNavigateUp is calll");
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //complet
        return super.onKeyDown(keyCode, event);
    }

    //
    @SuppressLint("LongLogTag")
    @Override
    public void onBackPressed() {
        //super.onBackPressed ();
        Log.w("Add New Chronic Diseases.", "this onbackpress is calll");

        AlertDialog.Builder x = new AlertDialog.Builder(this);
        x.setMessage("DO YOU WANT TO EXIT?").setTitle("Exit Activity'Add New Chronic Diseases'")

                .setPositiveButton("YES_EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w("Add New Chronic Diseases.", "end");
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

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chronic_diseases);

        Log.w("Add New Chronic Diseases.", "start");
        Toast.makeText(getApplicationContext(), "Add New Chronic Diseases....", Toast.LENGTH_SHORT).show();

        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeAsUpIndicator(R.drawable.ex);
        bar.setTitle("Add New Chronic Diseases.");

        add = findViewById(R.id.addNewChronicDiseases);
        add.setOnClickListener(this);
        spinner1 = findViewById(R.id.spinner);
        flagChronicDiseases();
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                String select = "" + parent.getItemAtPosition(position).toString();

                switch (select) {
                    case "Diabetes ( Type 1 )": {
                        name_ = "Diabetes";
                        type_ = "Type 1";
                        break;
                    }

                    case "Diabetes ( Type 2 )": {
                        name_ = "Diabetes";
                        type_ = "Type 2";
                        break;
                    }

                    case "Diabetes ( Gestational (GDM) )": {
                        name_ = "Diabetes";
                        type_ = "TGestational (GDM)";
                        break;
                    }

                    case "Hypertension ( Essential )": {
                        name_ = "Hypertension";
                        type_ = "Essential";
                        break;
                    }

                    case "Hypertension ( Secondary )": {
                        name_ = "Hypertension";
                        type_ = "Secondary";
                        break;
                    }

                    case "Hypertension ( Isolated systolic  )": {
                        name_ = "Hypertension";
                        type_ = "Isolated systolic";
                        break;
                    }

                    case "Hypertension ( Malignant )": {
                        name_ = "Hypertension";
                        type_ = "Malignant";
                        break;
                    }

                    case "Hypertension ( Resistant )": {
                        name_ = "Hypertension";
                        type_ = "Resistant";
                        break;
                    }


                    case "Cholesterol and Fats": {
                        name_ = "Cholesterol and Fats";
                        type_ = "null";
                        break;
                    }

                    default: {
                        Log.w("Error Add New Chronic Diseases.", "Error 407!!!");
                    }
                }

                itemTemp = select;

                //Copy(array2,array1)
                data2 = new ArrayList();
                data2.addAll(data);
                //delete selected Item to delete from spinner after added'Chronic Diseases'
                data2.remove(select);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        datePicker = findViewById(R.id.datePicker1);

    }

    void flagChronicDiseases() {
        Resources res = getResources();
        data = Arrays.asList(res.getStringArray(R.array.arrayChronicDiseases));
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, data);
        //complet
        // ........... When create db [add this code]: 'pre check and drop 'remove' Chronic Diseases added in past'
        spinner1.setAdapter(adapter1);

    }

    private void createChannel() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel x;
            x = new NotificationChannel(ChannelID, "My  Hi Channel with you", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            man.createNotificationChannel(x);


        }
    }

    void notification(String text, String date) {
        NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder note = null;


        createChannel();

        NotificationCompat.BigTextStyle bigtext = new NotificationCompat.BigTextStyle();
        bigtext.setBigContentTitle("Name and Type:" + text);
        bigtext.bigText("Detection Date:" + date);
        bigtext.setSummaryText("Add New Chronic Diseases");

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

   /* //Empty Fields
    boolean ifEmptyFields()
    {
        //complet
        return data.size() <= 0 ;
    }*/

    void adD() {

        //complet
        if (emp) {
            AlertDialog.Builder x = new AlertDialog.Builder(this);
            x.setMessage("No New Chronic Diseases For add.").setTitle("Added Chronic Diseases")

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
        x.setMessage("DO YOU WANT TO ADD new Chronic Disease?").setTitle("Add new Chronic Disease")

                .setPositiveButton("YES_ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        do_();
                        return;
                    }
                })

                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data2 = null;
                        return;
                    }
                })

                .setIcon(R.drawable.qus)
                .setPositiveButtonIcon(getDrawable(R.drawable.yes))
                .setNegativeButtonIcon(getDrawable(R.drawable.no))
                .show();
    }

    @SuppressLint("LongLogTag")
    void do_() {
        Log.w("Add new Chronic Diseases", itemTemp);
        // functions and codes
        //complet

        data = null;
        data = data2;
        data2 = null;
        // update new list after delete selected Item
        if (data.isEmpty()) {
            data.add("No New Chronic Diseases For add");
            emp = true;
            spinner1.setEnabled(false);//wor
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, data);
        spinner1.setAdapter(adapter);


        //Set Detection Date:
        int year_ = datePicker.getYear();
        int month_ = datePicker.getMonth();
        int day_ = datePicker.getDayOfMonth();
        String date_ = "" + year_ + "/" + month_ + "/" + day_;

        notification(itemTemp, date_);

        Toast.makeText(getApplicationContext(), "new Chronic Disease is added :" + itemTemp + " \n\nDate:" + date_, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View v) {
        if (v == add) {
            adD();
            return;
        }
    }

    //rotate
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.i(COMMON_TAG,"DoctorHomeActivity onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //  Log.i(COMMON_TAG,"DoctorHomeActivity onSaveInstanceState");
    }
}