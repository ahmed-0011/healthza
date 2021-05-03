package com.example.healthza.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthza.R;
import com.example.healthza.Toasty;
import com.example.healthza.adapters.DoctorAppointmentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorAppointments extends AppCompatActivity implements DoctorAppointmentAdapter.OnRecordItemClickListener {


    RecyclerView recyclerView;
    List<Map<String, Object>> appointments;
    DoctorAppointmentAdapter adp;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    private SearchView SearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointments);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String doctorId = firebaseAuth.getCurrentUser().getUid();

        appointments = new ArrayList<>();
        recyclerView = findViewById(R.id.RecyclerView);

        getData();

        ProgressDialog x= ProgressDialog.show(this, "Display Data...",
                "Please Wait while Display of Data...", true);
        pvThread(1,3,x);

        SearchView = findViewById(R.id.SearchView);

        SearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

              if(!query.isEmpty())
              {
                  if(query.equals("."))
                  {
                      ProgressDialog x= ProgressDialog.show(DoctorAppointments.this, "Display Data...",
                              "Please Wait while Display of Data...", true);
                      pvThread(1,3,x);
                      SearchView.setQuery("",false);
                      Toasty.showText(getApplicationContext(),"The ALL Data was displayed",Toasty.INFORMATION, Toast.LENGTH_SHORT);
                      return false;
                  }
                  ProgressDialog x= ProgressDialog.show(DoctorAppointments.this, "Display Data...",
                          "Please Wait while Display of Data...", true);
                  getDataSER(1,3,x,query);
                  Toasty.showText(getApplicationContext(),"The Search Result was displayed",Toasty.INFORMATION, Toast.LENGTH_SHORT);
              }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
              //  adp.getFilter().filter(SearchView.getQuery().toString().trim());
                return true;
            }
        });

        /*MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        // now define the properties of the
        // materialDateBuilder that is title text as SELECT A DATE
        materialDateBuilder.setTitleText("SELECT A DATE");

        // now create the instance of the material date
        // picker
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        // handle select date button which opens the

                        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");


        // now handle the positive button click from the
        // material design date picker
        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        // if the user clicks on the positive
                        // button that is ok button update the
                        // selected date
                        //mShowSelectedDateText.setText("Selected Date is : " + materialDatePicker.getHeaderText());
                        // in the above statement, getHeaderText
                        // is the selected date preview from the
                        // dialog
                    }
                });*/

    }

   // List<Map<String, Object>>
    void getData()
    {
        ProgressDialog x= ProgressDialog.show(this, "",
                "Please Wait TO get Data...", true);

        List<Map<String, Object>> data = new ArrayList<>();
        String doctorId = firebaseAuth.getCurrentUser().getUid();
        Query DRC = db.collection("doctors")
                .document(doctorId).collection("appointments")
                .orderBy("date");
        DRC.get().addOnCompleteListener(task -> {

            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    Map<String, Object> record = new HashMap<>();
                    record.put("patientName", document.get("patientName"));
                    record.put("patientId", document.get("patientId"));
                    record.put("type", document.get("type"));
                    record.put("date", document.get("date"));
                    record.put("description", document.get("description"));

                    Query DRC1 =  db.collection("patients")
                            .whereEqualTo("identificationNumber",document.get("patientId"));

                    DRC1.get().addOnCompleteListener(task1 -> {

                        task1.getResult().getDocuments().get(0).get("identificationNumber");
                       //Toasty.showText(this,
                        // "("+task1.getResult().getDocuments().get(0).get("identificationNumber")+")"
                        // ,Toasty.SUCCESS, Toast.LENGTH_LONG);

                        record.put("del", new String[]
                                {
                                doctorId,
                                task1.getResult().getDocuments().get(0).getId(),
                                document.getId()
                                });
                        data.add(record);

                    });
                }
                appointments = data;
                x.dismiss();
            }
        });
       //return data;
    }

    void getDataSER(int t1,int t2, ProgressDialog x,String ser) {
        if (t1 > t2) return;
        String doctorId = firebaseAuth.getCurrentUser().getUid();
        Query DRC = db.collection("doctors")
                .document(doctorId).collection("appointments")
                .orderBy("date");
        DRC.get().addOnCompleteListener(task -> {
            // Thread t1
            if (t1 == t2) {
                List<Map<String, Object>> data = new ArrayList<>();

                for (int i = 0; i < appointments.size(); i++) {
                    Map<String, Object> record = appointments.get(i);
                    if (record.get("patientName").equals(ser)
                            || record.get("patientId").equals(ser)
                            || record.get("date").equals(ser)
                    ) {
                       // System.out.println("dddd-----ddddd");
                        Map<String, Object> record1 = new HashMap<>();
                        record1.putAll(record);
                        data.add(record1);
                    }
                }

                x.dismiss();
                if (data.size() == 0)
                {
                    x.dismiss();
                    AlertDialog.Builder x1 = new AlertDialog.Builder(DoctorAppointments.this);
                    x1.setMessage("No Data Found.").setTitle("incomplete data")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    return;
                                }
                            })
                            .setIcon(R.drawable.goo)
                            .setPositiveButtonIcon(getDrawable(R.drawable.yes))
                            .show();
                }
                else {
                    adp = new DoctorAppointmentAdapter(DoctorAppointments.this, data,DoctorAppointments.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(DoctorAppointments.this));
                    recyclerView.setAdapter(adp);
                    x.dismiss();
                }
                return;
            }
            else getDataSER(t1+1,t2,x,ser);
        });


    }

    void pvThread(int t1,int t2,ProgressDialog x)
    {
        if(t1>t2)return;
        String doctorId = firebaseAuth.getCurrentUser().getUid();
        Query DRC = db.collection("doctors")
                .document(doctorId).collection("appointments")
                .orderBy("date");
        DRC.get().addOnCompleteListener(task -> {
            // Thread t1
            if (t1==t2)
            {
                {
                    System.out.println("D11D");
                    if (appointments.size() == 0)
                    {
                        x.dismiss();
                        AlertDialog.Builder x1 = new AlertDialog.Builder(DoctorAppointments.this);
                        x1.setMessage("No Data Found.").setTitle("incomplete data")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        return;
                                    }
                                })
                                .setIcon(R.drawable.goo)
                                .setPositiveButtonIcon(getDrawable(R.drawable.yes))
                                .show();
                    }
                    else {
                        adp = new DoctorAppointmentAdapter(DoctorAppointments.this, appointments,DoctorAppointments.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(DoctorAppointments.this));
                        recyclerView.setAdapter(adp);
                        x.dismiss();
                    }
                }
            }
            else pvThread(t1+1,t2,x);
        });
    }


    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void onRemoveButtonClick(int position) {

    }

    @Override
    public void onViewButtonClick(int position) {

    }
}
