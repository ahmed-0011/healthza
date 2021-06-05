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
import android.widget.CheckBox;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorUpdatePatientComplicationsActivity extends AppCompatActivity implements View.OnClickListener
        , CompoundButton.OnCheckedChangeListener
        , View.OnFocusChangeListener
{
/////////////////varable///////////////

    private Button update;
    private Button clear;
    private Button clearC;
    private Button search;

    private Spinner spinnerC;
    private Spinner spinnerP;

    private DatePicker datePicker;

    EditText searchP;
    EditText describeC;

    List<String> dataC;
    List<String> dataP;
    List<String> idsP;

    private String complicationName = "";
    private String complicationDate = "";
    int compPOS = 0;

    private String patientName = "";
    private String patientId = "";
    int patientPOS = 0;

    CheckBox autoTD;

    TextView tog;

    boolean dt = false;

    private static final String TAG = "updateNewComplicationNote";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
//////////////////////////////////////////////////
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
        describeC.clearFocus();
        return super.onKeyDown(keyCode, event);
    }
    //
    @SuppressLint("LongLogTag")
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed ();
        Log.w ("Update Complication Status.", "this onbackpress is calll");

        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO EXIT?" ).setTitle ( "Exit Activity'Update Complication Status'" )

                .setPositiveButton ( "YES_EXIT", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w ("Update Complication Status.", "end");
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

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_update_patient_complication);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Log.w("Update Complication Status.", "start");
        Toast.makeText(getApplicationContext(), "Update Complication Status....", Toast.LENGTH_SHORT).show();

        tog = findViewById(R.id.textView2); tog.setVisibility(View.VISIBLE);

        datePicker = findViewById(R.id.datePickerCU);
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Toasty.makeText(getApplicationContext()," You are changed date is : "+dayOfMonth +" - "+monthOfYear+ " - "+year,Toasty.LENGTH_SHORT).show();
            }
        });

        searchP = findViewById(R.id.innerPatientCompU); searchP.setOnFocusChangeListener(this);
        describeC = findViewById(R.id.innerComplicationStatusDescribeU); describeC.setOnFocusChangeListener(this);

        update = findViewById(R.id.updateComplicationS); update.setOnClickListener(this);
        clear = findViewById(R.id.clearSearchCompU); clear.setOnClickListener(this);
        clearC = findViewById(R.id.clearCompDES); clearC.setOnClickListener(this);
        search = findViewById(R.id.SearchPatientCompU); search.setOnClickListener(this);

        spinnerP = findViewById(R.id.spinnerPatientCompU);
        flagPatient();


        spinnerC = findViewById(R.id.spinnerComplicationU);
        flagComplication();

        autoTD = findViewById(R.id.TimeDateAutoCU);
        autoTD.setChecked(false);
        autoTD.setOnClickListener(this);
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
                         flagComplication();
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

    void flagComplication() {

        if ( (idsP==null || idsP.size()==0) )return;

            dataC = new ArrayList<String>();
            getComplication(dataC);
    }

    synchronized void getComplication(List<String> p) {
        //sample of virtual Patients  for test 'should comment it after writing db code'
        //<!--
        /*p.add("Eye damage : 2020/09/10");
        p.add("Cardiovascular disease : 2019/07/12");
        p.add("Foot damage : 2011/08/29");
        p.add("Hearing impairment : 2008/12/12");
        p.add("Alzheimer's disease : 2003/02/28");
        p.add("Depression : 2011/01/26");
        p.add("Nerve damage (neuropathy) : 2020/08/08");*/
        //-->

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        CollectionReference patientsRefs = db.collection("patients")
                .document(idsP.get(patientPOS))
                .collection("complications");
        patientsRefs.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                if( task.getResult().size() == 0)
                {
                    spinnerC.setAdapter(null);
                    return;
                }
                for (QueryDocumentSnapshot document : task.getResult()) {
                   String n = document.get("ComplicationName").toString();
                   String d = document.get("diagnosticDate").toString();
                    p.add(n+ " : " + d);
                }

                //<!--

                adapter1 = new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, dataC);
                spinnerC.setAdapter(adapter1);

                spinnerC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String temp = "" + parent.getItemAtPosition(position).toString();
                        complicationName = temp.substring(0,temp.indexOf(" : "));
                        complicationDate = temp.substring((temp.indexOf(" : ")+3),temp.length());
                        compPOS = position;
                        // ((TextView) spinnerC.getSelectedView()).setTextColor(getResources().getColor(holo_green_dark));
                        //!complet

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        String temp = "" + parent.getItemAtPosition(compPOS).toString();
                        complicationName = temp.substring(0,temp.indexOf(" : "));
                        complicationDate = temp.substring((temp.indexOf(" : ")+3),temp.length());
                        ((TextView) spinnerC.getSelectedView()).setTextColor(Color.rgb(129,212,250));
                        //!complet
                    }
                });

                //-->
            }

        });

        // complet db code to get Complication
        // code ...

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

    boolean ifEmptyFields()
    {
        //complet
        return  describeC.getText().toString().isEmpty();
    }

    void updateD(){

        //complet
        if( ifEmptyFields()
        || dataP == null
        || dataP.size() == 0
        || dataC == null
        || dataC.size() == 0 )
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
        x.setMessage ( "DO YOU WANT TO Update Status describe of Complication'"+complicationName+"'?" ).setTitle ( "Update Status Complication." )

                .setPositiveButton ( "YES_UPDATE", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDo();
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


    void updateDo()
    {

        int year_ = datePicker.getYear();
        int month_ = datePicker.getMonth();
        int day_ = datePicker.getDayOfMonth();
        String date_=""+year_+"-"+month_+"-"+day_;

        String s3 = "Patient Name: "+patientName
                +"\nPatient Id: "+patientId
                +"\nComplication: "+complicationName
                +"\nNew Describe of Status: "+describeC.getText().toString()
                +"\nUPDATE Date: "+date_;

        // add code dd
        //<!--
        updateC(describeC.getText().toString(),date_);
        //-->

    }

    void updateC(String c, String d)
    {

        Map<String, Object> datac = new HashMap<>();

        DocumentReference dec =db.collection("patients") // table
                .document(idsP.get(patientPOS)) // patient id
                .collection("complications")// table inside patient table
                .document(complicationName);

        dec.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    List<String> status = (List<String>) document.get("status");
                    status.add(d+" : "+c);

                   // Toasty.makeText(getApplicationContext(),d+" : "+c,Toasty.LENGTH_SHORT).show();

                    dec
                       .update("status", status)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    Toasty.showText(getApplicationContext(),"Update Done...",Toasty.SUCCESS,Toast.LENGTH_SHORT);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                   // Toasty.makeText(getApplicationContext(),d+" 11 "+c,Toasty.LENGTH_SHORT).show();
                                }
                            });


                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                }
            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {

        if(v == update) { updateD(); return;  }

        if(v == clear) {  searchP.setText(""); return; }

        if(v == clearC) {  describeC.setText(""); return; }

        if(v == search) { searchDO(); return; }

        if (v == autoTD) { onCheckboxClicked(v); return; }
    }

    //time and date auto
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        if (autoTD.equals(view)) {
            if (checked) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/mm/dd hh:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                Functions.timeS = now.getHour()+":"+now.getMinute();
                Functions.dateS = now.getYear()+"/"+now.getMonthValue()+"/"+now.getDayOfMonth();
                datePicker.init(now.getYear(),now.getMonthValue(),now.getDayOfMonth(),null);
                datePicker.setEnabled(false);
                tog.setVisibility(View.INVISIBLE);

            } else {
                datePicker.setEnabled(true);
                tog.setVisibility(View.VISIBLE);
            }

            // TODO: Veggie sandwich
        }
    }


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

        DrawerUtil.getDoctorDrawer(this, 7);
    }
}