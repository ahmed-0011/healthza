package com.project.cdh.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.project.cdh.DrawerUtil;
import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class PatientViewRelatives extends AppCompatActivity {
//////////////////varable//////////////
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    TableLayout tb;
    ProgressDialog pb;

    int child;
///////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_relatives);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        pb = new ProgressDialog(this);
        tb = findViewById(R.id.idf);
        tb.setStretchAllColumns(true);
        child = tb.getChildCount();

        getIdf();
    }

    void getIdf() {

        ProgressDialog x0= ProgressDialog.show(PatientViewRelatives.this, "",
                "Please Wait TO get Data...", true);

        String pId = firebaseAuth.getCurrentUser().getUid();

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

                    textview = new TextView(this);
                    textview.setText("---------");
                    textview.setLayoutParams(mw); // match warp wighet
                    textview.setGravity(Gravity.CENTER); //gravity center
                    tr1.addView(textview);

                    tb.addView(tr1);
                    AlertDialog.Builder x = new AlertDialog.Builder(PatientViewRelatives.this);
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

                   textview = new TextView(this);
                    textview.setText("Remove");
                    textview.setPaddingRelative(5,10,5,10);
                    textview.setLayoutParams(mw);
                    textview.setForeground(getResources().getDrawable(R.drawable.round_red));
                    textview.setTextColor(Color.rgb(255,0,0));
                    textview.setTypeface(Typeface.DEFAULT_BOLD);
                    textview.setGravity(Gravity.CENTER);
                    textview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DocumentReference ter = db.collection("patients").document(pId)
                                    .collection("relatives")
                                    .document(document.getId());
                            ter.delete().addOnCompleteListener(task -> {
                                if(task.isSuccessful())
                                {
                                    Toasty.showText(getApplicationContext(),document.get("name").toString()+" IS Deleted...",Toasty.INFORMATION, Toast.LENGTH_SHORT);
                                    tb.removeView(tr1);
                                    if(tb.getChildCount()==child)
                                    {
                                        TableRow.LayoutParams mw = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                                TableRow.LayoutParams.WRAP_CONTENT,1);

                                        TableRow tr1 = new TableRow(PatientViewRelatives.this);
                                        tr1.setGravity(Gravity.CENTER);

                                        TextView textview = new TextView(PatientViewRelatives.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        tr1.addView(textview);

                                        textview = new TextView(PatientViewRelatives.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        tr1.addView(textview);

                                        textview = new TextView(PatientViewRelatives.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        tr1.addView(textview);

                                        textview = new TextView(PatientViewRelatives.this);
                                        textview.setText("---------");
                                        textview.setLayoutParams(mw); // match warp wighet
                                        textview.setGravity(Gravity.CENTER); //gravity center
                                        tr1.addView(textview);

                                        tb.addView(tr1);

                                        AlertDialog.Builder x = new AlertDialog.Builder(PatientViewRelatives.this);
                                        x.setMessage("You not have Relatives.").setTitle("No Relatives")

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
                                }
                                else
                                {
                                    Toasty.showText(getApplicationContext(),"Error Occurred while deleting...",Toasty.INFORMATION, Toast.LENGTH_SHORT);

                                }

                            });
                        }
                    });
                    tr1.addView(textview);

                    /*Button bt = new Button(this);
                    bt.setText("Remove");
                    bt.setLayoutParams(mw);
                    bt.setForeground(getResources().getDrawable(R.drawable.round_red));
                    bt.setTextColor(Color.rgb(255,0,0));
                    bt.setTypeface(Typeface.DEFAULT_BOLD);
                    bt.setGravity(Gravity.CENTER);

                    bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DocumentReference ter = db.collection("patients").document(pId)
                                    .collection("identifier")
                                    .document(document.getId());
                            ter.delete().addOnCompleteListener(task -> {
                                if(task.isSuccessful())
                                {
                                    Toasty.showText(getApplicationContext(),document.get("name").toString()+" IS Deleted...",Toasty.INFORMATION, Toast.LENGTH_SHORT);
                                    tb.removeView(tr1);
                                    if(tb.getChildCount()==child)
                                    {
                                        nod.setVisibility(View.VISIBLE);

                                        AlertDialog.Builder x = new AlertDialog.Builder(PatientViewRelatives.this);
                                        x.setMessage("You not have Identifiers.").setTitle("No Identifiers")

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
                                }
                                else
                                {
                                    Toasty.showText(getApplicationContext(),"Error Occurred while deleting...",Toasty.INFORMATION, Toast.LENGTH_SHORT);

                                }

                            });
                        }
                    });
                    tr1.addView(bt);*/

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
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getPatientDrawer(this, 7);

    }

}