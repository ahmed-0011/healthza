package com.project.cdh.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.project.cdh.DrawerUtil;
import com.project.cdh.R;
import com.project.cdh.models.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorViewPatientsRelativesActivity extends AppCompatActivity implements  View.OnClickListener
        , View.OnFocusChangeListener{

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    ArrayAdapter<String> adapter1;

    EditText searchP;
    private Spinner spinnerP;
    List<String> dataP;
    List<String> idsP;

    private Button search;

    private String patientName = "";
    private String patientId = "";
    int patientPOS = 0;

    TableLayout tb;
    ProgressDialog pb;

    int child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_view_patient_relatives);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        pb = new ProgressDialog(this);
        tb = findViewById(R.id.idf);
        tb.setStretchAllColumns(true);
        child = tb.getChildCount();

        search = findViewById(R.id.SearchPatient); search.setOnClickListener(this);
        searchP = findViewById(R.id.innerPatient); //searchP.setOnFocusChangeListener(this);
        spinnerP = findViewById(R.id.spinnerPatient);
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

                        getIdf();
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

    void getIdf() {

        ProgressDialog x0= ProgressDialog.show(DoctorViewPatientsRelativesActivity.this, "",
                "Please Wait TO get Data...", true);

        String pId = idsP.get(patientPOS);

        CollectionReference Drc = db.collection("patients").document(pId)
                .collection("relatives");
        Drc.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {

                if( task.getResult().size() == 0)
                {

                    TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT,1);

                    TableRow tr1 = new TableRow(this);
                    tr1.setGravity(Gravity.CENTER);

                    TextView textview = new TextView(this);
                    textview.setText("---------");
                    textview.setLayoutParams(mw); // match warp wighet
                    textview.setGravity(Gravity.CENTER); //gravity center
                    tr1.addView(textview);

                    textview = new TextView(this);
                    textview.setText("---------");
                    textview.setLayoutParams(mw); // match warp wighet
                    textview.setGravity(Gravity.CENTER); //gravity center
                    tr1.addView(textview);

                    textview = new TextView(this);
                    textview.setText("---------");
                    textview.setLayoutParams(mw); // match warp wighet
                    textview.setGravity(Gravity.CENTER); //gravity center
                    tr1.addView(textview);


                    tb.addView(tr1);
                    AlertDialog.Builder x = new AlertDialog.Builder(DoctorViewPatientsRelativesActivity.this);
                    x.setMessage("You not have Relatives.").setTitle("No Relatives")

                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    x0.dismiss();
                                    return;
                                }
                            })

                            .setIcon(R.drawable.goo)
                            .setPositiveButtonIcon(getDrawable(R.drawable.yes))

                            .show();
                    x0.dismiss();
                    return;
                }

                for (QueryDocumentSnapshot document : task.getResult()) {

                    TableRow tr1 = new TableRow(this);
                    tr1.setBackgroundColor(Color.rgb(236,239,241));
                    tr1.setPaddingRelative(5,5,5,5);
                    tr1.setGravity(Gravity.CENTER);

                    TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT,1.0f);

                    TextView textview = new TextView(this);
                    textview.setText(document.get("name").toString());
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    tr1.addView(textview);

                    textview = new TextView(this);
                    textview.setText(document.get("phone").toString());
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    tr1.addView(textview);

                    textview = new TextView(this);
                    textview.setText(document.get("relationship").toString());
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    tr1.addView(textview);

                    tb.addView(tr1);
                }

                x0.dismiss();


            }
        });

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

    @Override
    public void onClick(View v) {
        if(v == search)
        {
            searchDO();
            return;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getDoctorDrawer(this, 3);
    }
}