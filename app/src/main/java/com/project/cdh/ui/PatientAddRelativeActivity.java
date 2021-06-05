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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.project.cdh.DrawerUtil;
import com.project.cdh.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.cdh.Toasty;

import java.util.HashMap;
import java.util.Map;


public class PatientAddRelativeActivity extends AppCompatActivity implements View.OnClickListener
        ,CompoundButton.OnCheckedChangeListener
        , View.OnFocusChangeListener {

    private EditText inputField[];

    private Button clear;
    private Button add;

    private static final String TAG = "PatientAddRelativeActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    public boolean onSupportNavigateUp() {
        Log.w("Add Patient Relative.", "onSupportNavigateUp is calll");
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
        Log.w("Add Patient Relative.", "this onbackpress is calll");

        AlertDialog.Builder x = new AlertDialog.Builder(this);
        x.setMessage("DO YOU WANT TO EXIT?").setTitle("Exit Activity'Add Patient Relative'")

                .setPositiveButton("YES_EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w("Add Patient Relative.", "end");
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
        setContentView(R.layout.activity_patient_add_relative);


        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inputField = new EditText[3];

        Log.w("Add Patient Relative.", "start");
        Toast.makeText(getApplicationContext(), "Add Patient Relative....", Toast.LENGTH_SHORT).show();

        inputField[0] = findViewById(R.id.innerRelativeName);
        inputField[0].setOnFocusChangeListener(this);

        inputField[1] = findViewById(R.id.innerRelativePhone);
        inputField[1].setOnFocusChangeListener(this);

        inputField[2] = findViewById(R.id.innerRelativeRelationship);
        inputField[2].setOnFocusChangeListener(this);


        clear = findViewById(R.id.clearRelativeInfo);
        clear.setOnClickListener(this);
        add = findViewById(R.id.addRelativeInfo);
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
        x.setMessage("DO YOU WANT TO Add Patient Relative?").setTitle("Add Patient Relative.")

                .setPositiveButton("YES_ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w("ADD Patient Relative", "Add Patient Relative.");
                        // functions and codes
                        //complet

                        addRelative();

                        Toast.makeText(getApplicationContext(), "Patient Relative IS ADD...", Toast.LENGTH_SHORT).show();

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
                        Log.w("CLEAR FIELDS", "Add Patient Relative CLEAR FIELDS");
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

    void addRelative() {

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
                    .collection("relatives")// table inside patient table
                    .document(((""+inputField[0].getText().toString())
                            + " : "+inputField[1].getText().toString()))
                    .set(dataTest)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            Toasty.showText(getApplicationContext(),"Add Done...",Toasty.SUCCESS,Toast.LENGTH_SHORT);
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
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        DrawerUtil.getPatientDrawer(this, 6);

    }
}