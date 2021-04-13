package com.example.healthza;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PatientListActivity extends AppCompatActivity implements RecyclerViewInterface {
    RecyclerView recyclerView;
    PatientAdapter patientAdapter;
    List<Patient> patients;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    //
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inf=getMenuInflater ();
        inf.inflate (R.menu.doctor_menu,menu);
        if (menu!=null && menu instanceof MenuBuilder)
            ((MenuBuilder)menu).setOptionalIconsVisible ( true );
        return super.onCreateOptionsMenu ( menu );
    }
    //
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) { return super.onPrepareOptionsMenu ( menu ); }
    //
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) { return super.onMenuOpened ( featureId, menu ); }
    //
    @Override
    public void onOptionsMenuClosed(Menu menu) { super.onOptionsMenuClosed ( menu ); }
    //
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        //getSupportActionBar ().setTitle ( item.getTitle ()+ "  is pressed" );
        switch(item.getItemId())
        {
            case R.id.newAppointmentsDM:
            {
                Intent I = new Intent(this, addNewTestAppointment.class);
                startActivity(I);
                break;
            }

            case R.id.logOutDM:
            {

                AlertDialog.Builder   x= new AlertDialog.Builder ( this );
                x.setMessage ( "DO YOU WANT TO LogOut?" ).setTitle ( "Doctor LogOut" )

                        .setPositiveButton ( "YES_EXIT", new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "LogedOut...", Toast.LENGTH_SHORT).show();
                                //complet
                                // finish();
                                firebaseAuth.signOut();
                                finishAffinity();
                                Intent I = new Intent(getApplicationContext(),WelcomeActivity.class);
                                startActivity(I);
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

                break;
            }

            case R.id.newComplicationDM:
            {
                Intent I = new Intent(this, addComplications.class);
                startActivity(I);
                break;
            }

            case R.id.updateComplicationDM:
            {
                Intent I = new Intent(this, updateComplicationStatus.class);
                startActivity(I);
                break;
            }

            case R.id.add_PatientsDM:
            {

                startActivity(new Intent(this, DoctorSendRequestActivity.class));
                break;
            }

            case R.id.list_PatientsDM:
            {

                startActivity(new Intent(this, PatientListActivity.class));
                break;
            }


            default:{}
        }
        return super.onOptionsItemSelected ( item );
    }
    //

    //
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed ();
        return super.onSupportNavigateUp ();
    }
    //
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //complet
        return super.onKeyDown(keyCode, event);
    }
    //
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed ();

        AlertDialog.Builder   x= new AlertDialog.Builder ( this );
        x.setMessage ( "DO YOU WANT TO EXIT?" ).setTitle ( "Exit Activity'Patients List'" )

                .setPositiveButton ( "YES_EXIT", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        ActionBar bar = getSupportActionBar ();
        bar.setHomeButtonEnabled ( true );
        bar.setDisplayHomeAsUpEnabled ( true );
        bar.setHomeAsUpIndicator ( R.drawable.ex);
        bar.setTitle("Patients List.");

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String doctorId = firebaseAuth.getCurrentUser().getUid();

        patients = new ArrayList<>();

        recyclerView = findViewById(R.id.patientsRecyclerView);

        CollectionReference patientsRefs = db.collection("doctors").document(doctorId)
                .collection("patients");
        patientsRefs.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult())
                    patients.add(document.toObject(Patient.class));

                patientAdapter = new PatientAdapter(this, patients, this);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(patientAdapter);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, patients.get(position).getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position) {
        //patients.remove(position);
        //patientAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onNoItems() {

    }
}