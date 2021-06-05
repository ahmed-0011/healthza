package com.project.cdh.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.project.cdh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class PatientViewComplicationsActivity extends AppCompatActivity {

    /////////////////varable////////////
    TableLayout tb;
    ProgressDialog pb;

    int child;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
/////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_view_patient_complications);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        pb = new ProgressDialog(this);
        tb = findViewById(R.id.idf);
        tb.setStretchAllColumns(true);
        child = tb.getChildCount();

        getComp();
    }

    void getComp()
    {

        ProgressDialog x0= ProgressDialog.show(PatientViewComplicationsActivity.this, "",
                "Please Wait TO get Data...", true);

        String pId = firebaseAuth.getUid();

        CollectionReference Drc = db.collection("patients").document(pId)
                .collection("complications");


        Drc.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {

                if (task.getResult().size() == 0) {
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

                    textview = new TextView(this);
                    textview.setText("---------");
                    textview.setLayoutParams(mw); // match warp wighet
                    textview.setGravity(Gravity.CENTER); //gravity center
                    tr1.addView(textview);

                    tb.addView(tr1);
                    AlertDialog.Builder x = new AlertDialog.Builder(PatientViewComplicationsActivity.this);
                    x.setMessage("You not have complications.").setTitle("No Complications")

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
            }

            for (QueryDocumentSnapshot document : task.getResult()) {

                TableRow tr1 = new TableRow(this);
                tr1.setBackgroundColor(Color.rgb(236,239,241));
                tr1.setPaddingRelative(5,5,5,5);
                tr1.setGravity(Gravity.CENTER);

                TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT,1.0f);

                TextView textview = new TextView(this);
                textview.setText(document.get("ComplicationName").toString());
                textview.setLayoutParams(mw);
                textview.setGravity(Gravity.CENTER);
                tr1.addView(textview);

                textview = new TextView(this);
                textview.setText(document.get("diagnosticDate").toString());
                textview.setLayoutParams(mw);
                textview.setGravity(Gravity.CENTER);
                tr1.addView(textview);

                ArrayList<String> status = (ArrayList<String>) document.get("status");
                if((status!=null)&&(status.size()>=1))
                {
                    Collections.sort(status);
                    Collections.reverse(status);

                    textview = new TextView(this);
                    String stu = status.get(0);
                    textview.setText(stu.substring(stu.indexOf(":")+1));
                    textview.setLayoutParams(mw);
                    textview.setGravity(Gravity.CENTER);
                    tr1.addView(textview);
                }

                textview = new TextView(this);
                textview.setText("View");
                textview.setPaddingRelative(5,10,5,10);
                textview.setLayoutParams(mw);
                textview.setForeground(getResources().getDrawable(R.drawable.round_blue_prel));
                textview.setTextColor(Color.rgb(41,182,246));
                textview.setTypeface(Typeface.DEFAULT_BOLD);
                textview.setGravity(Gravity.CENTER);
                textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showStatuses(status);
                    }
                });
                tr1.addView(textview);


                tb.addView(tr1);
            }

            x0.dismiss();

        });

    }

    void showStatuses(ArrayList<String> status)
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.complication_statuses, null);

        TableLayout tb1;
        ProgressDialog pb1;

        ImageView nod1;
        int child1;


        pb1 = new ProgressDialog(this);
        tb1 = view.findViewById(R.id.idf1);
        tb1.setStretchAllColumns(true);
        child1 = tb1.getChildCount();

        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,1);

        for(int i=0;i<status.size();i++)
        {

            TableRow tr1 = new TableRow(this);
            tr1.setBackgroundColor(Color.rgb(236,239,241));
            tr1.setPaddingRelative(5,5,5,5);
            tr1.setGravity(Gravity.CENTER);

            String ste = status.get(i);

            TextView textview = new TextView(this);
            textview.setText(ste.substring(0,ste.indexOf(":")-1));
            textview.setLayoutParams(mw);
            textview.setGravity(Gravity.CENTER);
            tr1.addView(textview);

            textview = new TextView(this);
            textview.setText(ste.substring(ste.indexOf(":")+1));
            textview.setLayoutParams(mw);
            textview.setGravity(Gravity.CENTER);
            tr1.addView(textview);

            tb1.addView(tr1);

        }

        AlertDialog Dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        ImageButton imgButton;
        imgButton =(ImageButton)view.findViewById(R.id.imageButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.dismiss();
                return;
            }
        });

        Dialog.show();
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