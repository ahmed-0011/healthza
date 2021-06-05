package com.project.cdh.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorAddPatientMedicinesActivity extends AppCompatActivity implements View.OnClickListener
        , CompoundButton.OnCheckedChangeListener
        , View.OnFocusChangeListener
{

    private Spinner Medicines;
    ArrayAdapter<String> adapter1;

    List<String> medicinesList;
    private String MedicineName = "";
    int POS = 0;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    String patientID;

    EditText search;
    private Button searchs;
    private Button clear;

    EditText d1,d2,d3,d4,d5;

    MaterialButtonToggleGroup btngg;
    Button bt1,bt2;
    boolean absorption;

    Button Assign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_add_patient_medicine);

        patientID = getIntent().getStringExtra("patientID");
        if(patientID.isEmpty())
        {
            Toasty.showText(getApplicationContext(),"Error occurred!!!\\n patient ID is error",Toasty.ERROR,Toast.LENGTH_LONG);
            finish();
            return;
        }

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Medicines = findViewById(R.id.spinnerMedicine);
        flagMedicines();

        search = findViewById(R.id.innerMedicine); search.setOnFocusChangeListener(this);
        searchs = findViewById(R.id.searchMedicine); searchs.setOnClickListener(this);
        clear = findViewById(R.id.clearSearchComp); clear.setOnClickListener(this);

        d1 = findViewById(R.id.morning);
        d2 = findViewById(R.id.noon);
        d3 = findViewById(R.id.dinner);
        d4 = findViewById(R.id.before_sleep);
        d5 = findViewById(R.id.dec);


        btngg = findViewById(R.id.toggleButtonGroup);
        bt1 = findViewById(R.id.btn1);
        bt2 = findViewById(R.id.btn2);

        bt1.setEnabled(false);
        bt2.setEnabled(true);
        btngg.clearChecked();
        btngg.check(bt1.getId());
        absorption = false;

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt1.setEnabled(false);
                bt2.setEnabled(true);
                btngg.clearChecked();
                btngg.check(bt1.getId());
                absorption = false;
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt2.setEnabled(false);
                bt1.setEnabled(true);
                btngg.clearChecked();
                btngg.check(bt2.getId());
                absorption = true;
            }
        });

        Assign = findViewById(R.id.asssign); Assign.setOnClickListener(this);

    }

    void flagMedicines()
    {
         medicinesList =  new ArrayList<String>();
         getMedicines( );
    }

    synchronized void getMedicines()
    {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        DocumentReference drc = db.collection("medicines").document("medicinal_drugs");
        drc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                medicinesList = (List<String>) documentSnapshot.get("general");
                if(medicinesList==null)
                {
                    Medicines.setAdapter(null);
                    return;
                }

                adapter1 = new ArrayAdapter<>(DoctorAddPatientMedicinesActivity.this,
                        android.R.layout.simple_dropdown_item_1line, medicinesList);
               Medicines.setAdapter(adapter1);

                Medicines.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // if(true)return;
                        MedicineName = "" + parent.getItemAtPosition(position).toString();
                        ((TextView) Medicines.getSelectedView()).setTextColor(Color.rgb(111,186,221));
                        POS = position;
                        //!complet
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        MedicineName = "" + parent.getItemAtPosition(POS).toString();
                        ((TextView) Medicines.getSelectedView()).setTextColor(Color.rgb(111,186,221));
                        //!complet
                    }
                });

            }
        });
    }

    int searcH(String pd)
    {
        int i=-1,j=-1;

        for(String dada : medicinesList)
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
        String text = search.getText().toString();

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
            Toasty.showText(getApplicationContext(),"This medicine does not exist",Toasty.WARNING,Toast.LENGTH_SHORT);
            return;
        }

       Medicines.setSelection(index);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms

                System.out.print("\b");

                //masege

                Toasty.showText(getApplicationContext(),""
                        +( ((TextView) Medicines.getSelectedView()).getText().toString()
                        +" is Selected"),Toasty.INFORMATION,Toast.LENGTH_SHORT);
            }
        }, 100);

    }

    @Override
    public void onClick(View v) {
        if(v==searchs)
        {
            searchDO();
            return;
        }

        if(v == clear)
        {
            search.setText("");
            return;
        }

        if(v == Assign)
        {
            Assign_();
            return;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if(true)
        {
            if (!hasFocus) {
                Log.d("focus", "focus lost");
                // Do whatever you want here
            } else {
                //Toast.makeText(getApplicationContext(), " Tap outside edittext to lose focus ", Toast.LENGTH_SHORT).show();
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

    boolean ifEmptyFields()
    {
        //complet
        boolean empty = false;
        empty = empty || d1.getText().toString().isEmpty();
        empty = empty || d2.getText().toString().isEmpty();
        empty = empty || d3.getText().toString().isEmpty();
        empty = empty || d4.getText().toString().isEmpty();
        empty = empty || d5.getText().toString().isEmpty();
        return  empty;
    }


    void Assign_()
    {
        if(ifEmptyFields())
        {
            Toasty.showText(getApplicationContext(),"incomplete data : Please complete fill the form data.",
                    Toasty.WARNING,Toast.LENGTH_SHORT);
            return;
        }

        DocumentReference docIdRef =  db.collection("patients") // table
                .document(patientID) // patient id
                .collection("medicines")// table inside patient table
                .document(MedicineName);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Toasty.showText(getApplicationContext(),"This Medicine is used : you can update it",Toasty.WARNING,Toast.LENGTH_SHORT);

                    } else {

                        Map<String, Object> medicines= new HashMap<>();
                        medicines.put("medicine_name",MedicineName);
                        medicines.put("morning_dose",Long.parseLong(d1.getText().toString()));
                        medicines.put("noon_dose",Long.parseLong(d2.getText().toString()));
                        medicines.put("dinner_dose",Long.parseLong(d3.getText().toString()));
                        medicines.put("before_sleep_dose",Long.parseLong(d4.getText().toString()));
                        medicines.put("absorption",absorption);
                        medicines.put("notes",d5.getText().toString());
                        //
                        Calendar calendar = Calendar.getInstance();
                        Date date = calendar.getTime();
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                        String date1 = format1.format(date);
                        medicines.put("prescription_date",date1);
                        List<String> old_prescription_date = new ArrayList<>();
                        medicines.put("old_prescription_date",old_prescription_date);
                        medicines.put("status",true);

                        DocumentReference DRC1 = db.collection("doctors") // table
                                .document(firebaseAuth.getUid()); //  id
                        DRC1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    DocumentSnapshot doctor = task.getResult();
                                    String doctotr_name = doctor.getString("name");

                                    medicines.put("doctotr_name",doctotr_name);


                                    DocumentReference DRC = db.collection("patients") // table
                                            .document(patientID) // patient id
                                            .collection("medicines")// table inside patient table
                                            .document(MedicineName);

                                    DRC.set(medicines).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toasty.showText(getApplicationContext()," Assign Medicine is done",Toasty.SUCCESS,Toast.LENGTH_SHORT);
                                        }
                                    });

                                }
                            }
                        });


                    }
                } else {

                }
            }
        });



    }
}