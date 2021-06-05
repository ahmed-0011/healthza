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
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorAddPatientComplicationsActivity extends AppCompatActivity implements View.OnClickListener
        , CompoundButton.OnCheckedChangeListener
        , View.OnFocusChangeListener
{
    /////////////////varible///////////////////////////



    private Button add;
    private Button clear;
    private Button clearC;
    private Button search;

    private Spinner spinnerP;

    private DatePicker datePicker;

    EditText searchP;
    EditText nameC;
    EditText statusC;

    List<String> dataP;
    List<String> idsP;

    private String patientName = "";
    private String patientId = "";
    int patientPOS = 0;

    private static final String TAG = "addNewComplicationNote";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
////////////////////////////////////////////////////////
    @SuppressLint("LongLogTag")
    public boolean onSupportNavigateUp()
    {
        Log.w ("Add New Complication.", "onSupportNavigateUp is calll");
        onBackPressed ();
        return super.onSupportNavigateUp ();
    }
    //
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //complet
        searchP.clearFocus();
        nameC.clearFocus();
        statusC.clearFocus();
        return super.onKeyDown(keyCode, event);
    }
    //
    @SuppressLint("LongLogTag")
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed ();
        Log.w ("Add New Complication.", "this onbackpress is calll");
///dialog
        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO EXIT?" ).setTitle ( "Exit Activity'Add New Complication'" )

                .setPositiveButton ( "YES_EXIT", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w ("Add New Complication.", "end");
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

    ArrayAdapter<String> adapter1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_add_patient_complication);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Log.w("Add New Complication.", "start");
        Toast.makeText(getApplicationContext(), "Add New Complication....", Toast.LENGTH_SHORT).show();

        datePicker = findViewById(R.id.datePickerC);
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Toasty.makeText(getApplicationContext()," You are changed date is : "+dayOfMonth +" - "+monthOfYear+ " - "+year,Toasty.LENGTH_SHORT).show();
            }
        });

        searchP = findViewById(R.id.innerPatientComp); searchP.setOnFocusChangeListener(this);
        nameC = findViewById(R.id.innerComplicationName); nameC.setOnFocusChangeListener(this);
        statusC = findViewById(R.id.innerComplicationStatusDescribe); statusC.setOnFocusChangeListener(this);

        add = findViewById(R.id.addComplication); add.setOnClickListener(this);
        clear = findViewById(R.id.clearSearchComp); clear.setOnClickListener(this);
        clearC = findViewById(R.id.clearComp); clearC.setOnClickListener(this);
        search = findViewById(R.id.SearchPatientComp); search.setOnClickListener(this);

        spinnerP = findViewById(R.id.spinnerPatientComp);
        flagPatient();

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
//refrence database firbase
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

                //masege
                Toast.makeText(getApplicationContext(),""
                        +( ((TextView) spinnerP.getSelectedView()).getText().toString()
                        +" is Selected"),Toast.LENGTH_SHORT).show();

            }
        }, 100);

    }

    //Empty Fields
    boolean ifEmptyFields()
    {
        //complet
        return  statusC.getText().toString().isEmpty()
                || nameC.getText().toString().isEmpty();
    }

    void adD(){

        //complet
        if( ifEmptyFields()
        || dataP == null
        || dataP.size() == 0 )
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
        x.setMessage ( "DO YOU WANT TO Add New Complication?" ).setTitle ( "Add New Complication." )

                .setPositiveButton ( "YES_ADD", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addDo();
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


    void addDo()
    {

        int year_ = datePicker.getYear();
        int month_ = datePicker.getMonth();
        int day_ = datePicker.getDayOfMonth();
        String date_=""+year_+"-"+month_+"-"+day_;

        add_O(date_,nameC.getText().toString(),statusC.getText().toString());

        String s3 = "Patient Name: "+patientName
                +"\nPatient Id: "+patientId
                +"\nComplication Name: "+nameC.getText().toString()
                +"\nStatus Describe: "+statusC.getText().toString()
                +"\nDetection Date: "+date_;

        // add code dd
        //<!--

        //-->

    }

    void add_O(String d,String n ,String c)
    {

        Map<String, Object> data = new HashMap<>();
        data.put("diagnosticDate", d);
        data.put("ComplicationName", n);
        List<String> status = new ArrayList<String>();
        status.add(0,d+" : "+ c);
        data.put("status",status);
//document reference
        DocumentReference dec =db.collection("patients") // table
                .document(idsP.get(patientPOS)) // patient id
                .collection("complications")// table inside patient table
                .document(n);

            dec.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toasty.showText(getApplicationContext(),"ADD Done...",Toasty.SUCCESS,Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
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

        if(v == clearC)
        {
            nameC.setText("");
            statusC.setText("");
            return;
        }

        if(v == search)
        {
            searchDO();
            return;
        }
    }
///end on clike


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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }


    @Override
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getDoctorDrawer(this, 6);
    }

}