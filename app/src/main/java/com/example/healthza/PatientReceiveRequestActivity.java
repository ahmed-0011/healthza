package com.example.healthza;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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

public class PatientReceiveRequestActivity extends AppCompatActivity implements RecyclerViewInterface {
    private List<ReceiveRequest> receiveRequests;
    private RecyclerView recyclerView;
    private ReceiveRequestAdapter receiveRequestAdapter;
    private TextView emptyRequestListTextView;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    //
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inf=getMenuInflater ();
        inf.inflate (R.menu.patient_menu,menu);
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
            case R.id.newIdentifierPM:
            {
                Intent I = new Intent(this, AddPatientIdentifier.class);
                startActivity(I);
                break;
            }

            case R.id.newChronicDiseasesPM:
            {
                Intent I = new Intent(this, newChronicDiseases.class);
                startActivity(I);
                break;
            }

            case R.id.GlucoseTestPM:
            {
                Intent I = new Intent(this, AddGlucoseTest.class);
                startActivity(I);
                break;
            }

            case R.id.FBStestPM:
            {
                Intent I = new Intent(this, AddFBStest.class);
                startActivity(I);
                break;
            }

            case R.id.HypertensionTestPM:
            {
                Intent I = new Intent(this, AddHypertensionTest.class);
                startActivity(I);
                break;
            }

            case R.id.CumulativeTestPM:
            {
                Intent I = new Intent(this, HbAlc.class);
                startActivity(I);
                break;
            }

            case R.id.KidneysTestPM:
            {
                Intent I = new Intent(this, AddKidneysTest .class);
                startActivity(I);
                break;
            }

            case R.id.LiverTestPM:
            {
                Intent I = new Intent(this, AddLiverTest.class);
                startActivity(I);
                break;
            }

            case R.id.CholesterolAndFatsTestPM:
            {
                Intent I = new Intent(this, AddCholesterolAndFatsTest.class);
                startActivity(I);
                break;
            }

            case R.id.ComprehensiveTestPM:
            {
                Intent I = new Intent(this, ComprehensiveTest.class);
                startActivity(I);
                break;
            }

            case R.id.listDoctorPm:
            {
                Intent I = new Intent(this, DoctorListActivity.class);
                startActivity(I);
                break;
            }

            case R.id.requestDoctorPm:
            {
                startActivity(new Intent(this, PatientReceiveRequestActivity.class));
                break;
            }

            case R.id.logOutPM:
            {

                AlertDialog.Builder   x= new AlertDialog.Builder ( this );
                x.setMessage ( "DO YOU WANT TO LogOut?" ).setTitle ( "Patient LogOut" )

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
        x.setMessage ( "DO YOU WANT TO EXIT?" ).setTitle ( "Exit Activity'Add ٌRequests'" )

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
        setContentView(R.layout.activity_patient_receive_request);

        ActionBar bar = getSupportActionBar ();
        bar.setHomeButtonEnabled ( true );
        bar.setDisplayHomeAsUpEnabled ( true );
        bar.setHomeAsUpIndicator ( R.drawable.ex);
        bar.setTitle("Add ٌRequests.");

        emptyRequestListTextView = findViewById(R.id.emptyRequestListTextView);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String patientId = firebaseAuth.getCurrentUser().getUid();

        receiveRequests = new ArrayList<>();

        recyclerView = findViewById(R.id.receiveRequestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        CollectionReference recvRequestsRef = db.collection("requests")
                .document("recv_requests").collection(patientId);

        recvRequestsRef.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                for (QueryDocumentSnapshot document : task.getResult())
                    receiveRequests.add(document.toObject(ReceiveRequest.class));

                if (receiveRequests.isEmpty())
                    emptyRequestListTextView.setVisibility(View.VISIBLE);

                receiveRequestAdapter = new ReceiveRequestAdapter(this, receiveRequests, this);
                recyclerView.setAdapter(receiveRequestAdapter);
            }
        });
    }

    @Override
    public void onItemClick(int position)
    {
        Toast.makeText(this, receiveRequests.get(position).getDoctorName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position)
    {

    }

    @Override
    public void onNoItems()
    {
        if (receiveRequests.isEmpty())
            emptyRequestListTextView.setVisibility(View.VISIBLE);
    }
}