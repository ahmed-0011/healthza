package com.project.cdh.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DoctorUpdatePatientMedicinesActivity extends AppCompatActivity implements View.OnClickListener
        , CompoundButton.OnCheckedChangeListener
        , View.OnFocusChangeListener
{
////////////////////varable///////////////
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    String patientID;

    EditText d1,d2,d3,d4,d5;

    MaterialButtonToggleGroup btngg;
    Button bt1,bt2;

    MaterialButtonToggleGroup btngg1;
    Button bt11,bt21;
    boolean absorption,b1;

    Button update;

    private Spinner Medicines;
    ArrayAdapter<String> adapter1;

    ArrayList<Map<String, Object>>  medicinesList;
    ArrayList<String>  medicinesListN;
    private String MedicineName = "";
    int POS = 0;

    TableLayout g1;

    TextInputLayout g2;
///////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_update_patient_medicines);

        patientID = getIntent().getStringExtra("patientID");
        if(patientID.isEmpty())
        {
            Toasty.showText(getApplicationContext(),"Error occurred!!!\\n patient ID is error",Toasty.ERROR, Toast.LENGTH_LONG);
            finish();
            return;
        }

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Medicines = findViewById(R.id.spinnerMedicine);


        d1 = findViewById(R.id.morning);
        d2 = findViewById(R.id.noon);
        d3 = findViewById(R.id.dinner);
        d4 = findViewById(R.id.before_sleep);
        d5 = findViewById(R.id.dec);

        g1 = findViewById(R.id.tableLayout);
        g2 = findViewById(R.id.textInputLayout);


        btngg1 = findViewById(R.id.toggleButtonGroup1);

        bt11 = findViewById(R.id.btn11);
        bt21 = findViewById(R.id.btn21);

        bt1 = findViewById(R.id.btn1);
        bt2 = findViewById(R.id.btn2);


        btngg1.clearChecked();
        btngg1.check(bt11.getId());
        bt11.setEnabled(false);
        bt21.setEnabled(true);
        b1=false;

        bt11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt11.setEnabled(false);
                bt21.setEnabled(true);
                btngg1.clearChecked();
                btngg1.check(bt11.getId());
                b1=false;

                g1.setVisibility(View.GONE);
                btngg.setVisibility(View.GONE);
                g2.setVisibility(View.GONE);

            }
        });

        bt21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt21.setEnabled(false);
                bt11.setEnabled(true);
                btngg1.clearChecked();
                btngg1.check(bt21.getId());
                b1=true;

                g1.setVisibility(View.VISIBLE);
                btngg.setVisibility(View.VISIBLE);
                g2.setVisibility(View.VISIBLE);

            }
        });


        btngg = findViewById(R.id.toggleButtonGroup);


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

        update = findViewById(R.id.asssign); update.setOnClickListener(this);

        flagMedicines();

    }

    void flagMedicines()
    {
        medicinesList =  new ArrayList<Map<String, Object>>();
        medicinesListN = new ArrayList<String>();
        getMedicines();
    }

    synchronized void getMedicines()
    {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        ProgressDialog x= ProgressDialog.show(this, "",
                "Please Wait TO get and Classify Medicines...", true);
        Query DRC = db.collection("patients") // table
                .document(patientID) // patient id
                .collection("medicines")
                .orderBy("medicine_name");

        DRC.get().addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Map<String, Object> medicines= new HashMap<>();
                            medicines.put("medicine_name",document.getString("medicine_name"));
                            medicines.put("morning_dose",document.getLong("morning_dose"));
                            medicines.put("noon_dose",document.getLong("noon_dose"));
                            medicines.put("dinner_dose",document.getLong("dinner_dose"));
                            medicines.put("before_sleep_dose",document.getLong("before_sleep_dose"));
                            medicines.put("absorption",document.getBoolean("absorption"));
                            medicines.put("notes",document.getString("notes"));
                            medicines.put("doctotr_name",document.getString("doctotr_name"));
                            medicines.put("prescription_date",document.getString("prescription_date"));
                            medicines.put("old_prescription_date", (ArrayList<String>) document.get("old_prescription_date"));
                            medicines.put("status", document.getBoolean("status"));
                            medicines.put("ids", document.getId());
                            medicinesList.add(medicines);
                            medicinesListN.add(document.getString("medicine_name"));

                        }

                        if(medicinesList==null)
                        {
                            Medicines.setAdapter(null);
                            return;
                        }

                        adapter1 = new ArrayAdapter<String>(DoctorUpdatePatientMedicinesActivity.this,
                                android.R.layout.simple_dropdown_item_1line, medicinesListN);
                        Medicines.setAdapter(adapter1);

                        Medicines.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                // if(true)return;
                                MedicineName = "" + parent.getItemAtPosition(position).toString();
                                ((TextView) Medicines.getSelectedView()).setTextColor(Color.rgb(111,186,221));
                                POS = position;

                              /*  btngg1.clearChecked();
                                btngg1.check(bt11.getId());
                                bt11.setEnabled(false);
                                bt21.setEnabled(true);
                                b1=false;*/
                               // btngg.clearChecked();


                                d1.setText(medicinesList.get(POS).get("morning_dose").toString());
                                d2.setText(medicinesList.get(POS).get("noon_dose").toString());
                                d3.setText(medicinesList.get(POS).get("dinner_dose").toString());
                                d4.setText(medicinesList.get(POS).get("before_sleep_dose").toString());
                                d5.setText(medicinesList.get(POS).get("notes").toString());

                               if((boolean)medicinesList.get(POS).get("absorption"))
                               {
                                 // bt2.callOnClick();
                                  btngg.clearChecked();
                                  btngg.check(bt2.getId());
                                   bt2.callOnClick();

                               }
                               else
                               {
                                  // bt1.callOnClick();
                                   btngg.clearChecked();
                                   btngg.check(bt1.getId());
                                   bt1.callOnClick();
                               }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                MedicineName = "" + parent.getItemAtPosition(POS).toString();
                                ((TextView) Medicines.getSelectedView()).setTextColor(Color.rgb(111,186,221));
                                //!complet
                            }
                        });
                        x.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View v) {

        if(v==update)
        {
            update_();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    void update_()
    {
        DocumentReference DRC = db.collection("patients") // table
                .document(patientID) // patient id
                .collection("medicines")
                .document(medicinesList.get(POS).get("ids").toString());

        ArrayList<String> arr = (ArrayList<String>)medicinesList.get(POS).get("old_prescription_date");
        String ne = medicinesList.get(POS).get("prescription_date").toString();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = b1? format1.format(date) : "";

        if(!ne.equals(date1)
        /*&& (arr.indexOf(ne)>=0)*/) { if(!ne.isEmpty()) arr.add(ne); }
        DRC.update("old_prescription_date", arr);
        DRC.update("prescription_date", date1);
        DRC.update("status", b1);
        if(b1)
        {
            DRC.update("morning_dose",Long.parseLong(d1.getText().toString()));
            DRC.update("noon_dose",Long.parseLong(d2.getText().toString()));
            DRC.update("dinner_dose",Long.parseLong(d3.getText().toString()));
            DRC.update("before_sleep_dose",Long.parseLong(d4.getText().toString()));
            DRC.update("notes",d5.getText().toString());
            DRC.update("absorption",absorption);
        }
        Toasty.showText(getApplicationContext(),"Update "+medicinesListN.get(POS)+" Is done...",
                Toasty.SUCCESS,Toast.LENGTH_SHORT);

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

}