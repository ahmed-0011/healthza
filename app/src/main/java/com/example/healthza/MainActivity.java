package com.example.healthza;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        showWelcomeDialog();
    }


    void showWelcomeDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.activity_welcome_dialog, null);

        Button startButton = view.findViewById(R.id.startButton);

        AlertDialog welcomeDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        welcomeDialog.show();


        DisplayMetrics metrics = new DisplayMetrics(); //get metrics of screen
        getDisplay().getRealMetrics(metrics);
        int height = (int) (metrics.heightPixels * 0.95); //set height to 90% of total
        int width = (int) (metrics.widthPixels * 0.95); //set width to 90% of total

        welcomeDialog.getWindow().setLayout(width, height); //set layout
        welcomeDialog.setContentView(R.layout.activity_welcome_dialog);
    }

    void showPatientDialog1()
    {
        LayoutInflater inflater = LayoutInflater.from(this);

        View view =  inflater.inflate(R.layout.activity_patient_dialog1,null);

        CheckBox diabeticCheckBox, hypertensionCheckBox ,cholestrolCheckBox;
        TextInputEditText diabeticTypeInputEditText, hypertensionTypeInputEditText,
                          cholestrolTypeInputEditText;

        diabeticCheckBox = view.findViewById(R.id.diabeticCheckBox);
        hypertensionCheckBox =view.findViewById(R.id.hypertensionCheckBox);
        cholestrolCheckBox = view.findViewById(R.id.cholestrolCheckBox);
        diabeticTypeInputEditText = view.findViewById(R.id.diabeticTypeInputEditText);
        hypertensionTypeInputEditText = view.findViewById(R.id.hypertensionTypeInputEditText);
        cholestrolTypeInputEditText = view.findViewById(R.id.cholestrolTypeInputEditText);


        AlertDialog patientDialog1 = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Welcome, patientName")
                .setPositiveButton(R.string.next_text, (dialog, which) -> {

                    /* check if all field filled */


                    /* if all required field filled */
                    showPatientDialog2();
                    dialog.dismiss();
                    /* else show error dialog */

                })
                .create();
        patientDialog1.show();

        DisplayMetrics metrics = new DisplayMetrics(); //get metrics of screen
        getDisplay().getRealMetrics(metrics);
        int height = (int) (metrics.heightPixels * 0.95); //set height to 90% of total
        int width = (int) (metrics.widthPixels * 0.95); //set width to 90% of total

        patientDialog1.getWindow().setLayout(width, height); //set layout
        patientDialog1.setContentView(R.layout.activity_welcome_dialog);
    }


    void showPatientDialog2()
            {
                LayoutInflater inflater = LayoutInflater.from(this);

                View view =  inflater.inflate(R.layout.activity_patient_dialog2,null);

                Button selectDiabeticDetectionDateButton, selectHypertensionDetectionDateButton
                        ,selectCholestrolDetectionDateButton;

                TextView selectedDiabeticDetectionDateTextView, selectedHypertensionDetectionDateTextView
                        ,selectedCholestrolDetectionDateTextView;

                selectDiabeticDetectionDateButton = view.findViewById(R.id.selectDiabeticDetectionDateButton);
                selectHypertensionDetectionDateButton =view.findViewById(R.id.selectHypertensionDetectionDateButton);
                selectCholestrolDetectionDateButton = view.findViewById(R.id.selectCholestrolDateButton);


                selectedDiabeticDetectionDateTextView = view.findViewById(R.id.selectedDiabeticDetectionDateTextView);
                selectedHypertensionDetectionDateTextView = view.findViewById(R.id.selectedHypertensionDetectionDateTextView);
                selectedCholestrolDetectionDateTextView = view.findViewById(R.id.selectedCholestrolDetectionDateTextView);



                selectDiabeticDetectionDateButton.setOnClickListener(v -> {
                    showDateDialog(selectedDiabeticDetectionDateTextView);
                });

                selectHypertensionDetectionDateButton.setOnClickListener(v -> {
                    showDateDialog(selectedHypertensionDetectionDateTextView);
                });

                selectCholestrolDetectionDateButton .setOnClickListener(v -> {
                    showDateDialog(selectedCholestrolDetectionDateTextView);
                });

                AlertDialog patientDialog2 = new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle("Welcome, patientName")
                        .setPositiveButton(R.string.finish_text, (dialog, which) -> {

                            /* check if all field filled */


                            /* if all required field filled */
                            dialog.dismiss();

                            /* else show error dialog */
                        })
                        .create();
                patientDialog2.show();

                DisplayMetrics metrics = new DisplayMetrics(); //get metrics of screen
                getDisplay().getRealMetrics(metrics);
                int height = (int) (metrics.heightPixels * 0.95); //set height to 90% of total
                int width = (int) (metrics.widthPixels * 0.95); //set width to 90% of total

                patientDialog2.getWindow().setLayout(width, height); //set layout
                patientDialog2.setContentView(R.layout.activity_welcome_dialog);
            }


    void showDoctorDialog1()
    {
        LayoutInflater inflater = LayoutInflater.from(this);

        View view =  inflater.inflate(R.layout.activity_doctor_dialog1,null);


        TextInputEditText specialityInputEditText, yearsOfExperienceInputEditText,
                          workplaceInputEditText;

        specialityInputEditText = view.findViewById(R.id.specialityInputEditText);
        yearsOfExperienceInputEditText = view.findViewById(R.id.yearsOfExperienceInputEditText);
        workplaceInputEditText = view.findViewById(R.id.workplaceInputEditText);


        AlertDialog doctorDialog1 = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Welcome, patientName")
                .setPositiveButton(R.string.next_text, (dialog, which) -> {

                    /* check if all field filled */


                    /* if all required field filled */
                    dialog.dismiss();

                    /* else show error dialog */

                })
                .create();

        doctorDialog1.show();
        doctorDialog1.getWindow().setBackgroundDrawableResource(R.drawable.welcome_dialog_background);
    }


    private void showDateDialog(TextView textView)
    {

        Calendar calendar = Calendar.getInstance();

        /*  get current date  */
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                calendar.set(Calendar.DATE, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);

                String dateText = DateFormat.format("MM/dd/yyyy",calendar).toString();

                textView.setText(dateText);
                textView.setVisibility(TextView.VISIBLE);
            }
        }, year, month, date);


        datePickerDialog.show();
    }
}