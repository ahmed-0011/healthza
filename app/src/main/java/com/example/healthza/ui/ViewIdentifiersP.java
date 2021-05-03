package com.example.healthza.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthza.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewIdentifiersP extends AppCompatActivity {

    List<Map<String, Object>>  idfr;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;


    TableLayout tb;
    ProgressDialog pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_identifiers_p);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        pb = new ProgressDialog(this);
        tb = findViewById(R.id.idf);
        tb.setStretchAllColumns(true);

        getIdf();
    }

    public void startLoadData() {
       pb.setCancelable(false);
       pb.setMessage("Fetching Invoices..");
      pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
       pb.show();
        //new LoadDataTask().execute(0);
    }

    void getIdf() {

        ProgressDialog x0= ProgressDialog.show(ViewIdentifiersP.this, "",
                "Please Wait TO get Data...", true);

        idfr =new ArrayList<>();
        String pId = firebaseAuth.getCurrentUser().getUid();

        CollectionReference Drc = db.collection("patients").document(pId)
                .collection("identifier");
        Drc.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {

                if( task.getResult().size() == 0)
                {
                    AlertDialog.Builder x = new AlertDialog.Builder(ViewIdentifiersP.this);
                    x.setMessage("The Patient is not have Identifiers.").setTitle("No Identifiers")

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
                    Map<String, Object> pdr = new HashMap<>();
                    pdr.put("name",document.get("name"));
                    pdr.put("phone",document.get("phone"));
                    pdr.put("relationship",document.get("relationship"));
                    pdr.put("del",document.getId());
                    idfr.add(pdr);

                    //tl.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                    TableRow tr1 = new TableRow(this);
                    tr1.setBackgroundColor(Color.rgb(236,239,241));
                    tr1.setPaddingRelative(5,-1,5,-1);
                    TextView textview = new TextView(this);
                    textview.setText(document.get("name").toString());
                    textview.setWidth(100);
                    textview.setWidth(0);
                    tr1.addView(textview);

                    textview = new TextView(this);
                    textview.setText(document.get("phone").toString());
                    textview.setWidth(100);
                    textview.setWidth(0);
                    tr1.addView(textview);

                    textview = new TextView(this);
                    textview.setText(document.get("relationship").toString());
                    textview.setWidth(100);
                    textview.setWidth(0);
                    tr1.addView(textview);

                    Button bt = new Button(this);
                    bt.setText("Remove");
                    bt.setBackground(getResources().getDrawable(R.drawable.round_empty));
                    bt.setBackgroundColor(Color.rgb(255,0,0));
                    bt.setWidth(100);

                    tr1.addView(bt);

                   tb.addView(tr1);
                }

                x0.dismiss();


            }
        });

    }
}