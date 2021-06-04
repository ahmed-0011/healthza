package com.project.cdh.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;

public class Functions extends AppCompatActivity{

   static boolean threead=false;
    static void wait_()
    {
        threead=true;
        while (threead);
    }

   static void signal_()
    {
        threead=false;
    }

    public static final String TAG_CT = "countIsUpdated";

    public static PatientHomeActivity PatientHomeActivity_;

    public static int pact = -999;
    public static int pact2 = -999;

    public static String dateS = "";
    public static String timeS = "";

    public static String loginSUC = "147";
    public  static  boolean loginSUCV = true;

    static    int ne=1;//  Notification ID"Counter"

    public static class MDatePicker
    {
        private static int Year;
        private static int Month;
        private static int Day;

        TextView f;

        public static int getYear() {
            return Year;
        }

        public static int getMonth() {
            return Month;
        }

        public static int getDay() {
            return Day;
        }

        public static void setYear(int year) {
            Year = year;
        }

        public static void setMonth(int month) {
            Month = month;
        }

        public static void setDay(int day) {
            Day = day;
        }

       static void show(Object This) {
            MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

            // now define the properties of the
            // materialDateBuilder that is title text as SELECT A DATE
            materialDateBuilder.setTitleText("SELECT A DATE");

            // now create the instance of the material date
            // picker
            final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

            // handle select date button which opens the
            materialDatePicker.show(((PatientAddHypertensionTestActivity)This).getSupportFragmentManager(), "MATERIAL_DATE_PICKER");


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
                    });
        }


    }
    //Date
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private static int Year;
        private static int Month;
        private static int Day;

        TextView f;

        public static int getYear() {
            return Year;
        }

        public static int getMonth() {
            return Month;
        }

        public static int getDay() {
            return Day;
        }

        public static void setYear(int year) {
            Year = year;
        }

        public static void setMonth(int month) {
            Month = month;
        }

        public static void setDay(int day) {
            Day = day;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            setYear(year);
            setMonth(month+1);
            setDay(day);
            dateS = getYear()+"-"+getMonth()+"-"+getDay();
            f.setText(dateS);
            Toast.makeText(this.getContext(), "..."+getYear()+" "+getMonth()+" "+getDay()+"...", Toast.LENGTH_SHORT).show();
        }

        public DatePickerFragment(TextView g)
        {
            f=g;
        }
    }

    //Time
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private static int Hour;
        private static int Minute;

        TextView f;

        public static int getHour() {
            return Hour;
        }

        public static int getMinute() {
            return Minute;
        }

        public static void setHour(int hour) {
            Hour = hour;
        }

        public static void setMinute(int minute) {
            Minute = minute;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            setHour(hourOfDay);
            setMinute(minute);
            timeS = TimePickerFragment.getHour()+":"+TimePickerFragment.getMinute();
            f.setText(timeS);
            Toast.makeText(this.getContext(), "..." + getHour() + " " + getMinute() + "...", Toast.LENGTH_SHORT).show();
        }

        public TimePickerFragment(TextView g)
        {
            f=g;
        }
    }

}
