package com.example.healthza;

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

import java.util.Calendar;

public class Functions extends AppCompatActivity{

    public static final String TAG_CT = "countIsUpdated";

    public static String dateS = "";
    public static String timeS = "";

    public static String loginSUC = "147";
    public  static  boolean loginSUCV = true;

    static    int ne=1;//  Notification ID"Counter"

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
            dateS = getYear()+"/"+getMonth()+"/"+getDay();
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
