package com.example.healthza;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Toaster
{
    public static int SUCCESS = 0;
    public static int WARNING = 1;
    public static int FAILURE =2;
    public static int ACTION = 4;

    public static void makeText(Activity activity, String message, int option)
    {
        /*
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.layout_toast, null);
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        if(option == SUCCESS)
        {
            messageTextView.setText("Success");
        }
        else if(option == WARNING)
        {
            messageTextView.setText("Failure");
        }
        else if(option == FAILURE)
        {
            messageTextView.setText("Warning");
        }
        else if(option == ACTION)
        {
            messageTextView.setText("Action");;
        }
        else
            return;


        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.BOTTOM, 0, 40);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
         */
    }

}