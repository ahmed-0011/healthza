package com.project.cdh;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class ProgressDialog
{
    private Activity activity;
    private AlertDialog progressDialog;

    public ProgressDialog(Activity activity)
    {
        this.activity = activity;
    }

    public void showProgressDialog()
    {

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.progress_dialog, null);

        progressDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(false)
                .create();

        progressDialog.show();
    }

    public void showProgressDialog(String message)
    {

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.progress_dialog, null);

        TextView progressBarTextView = view.findViewById(R.id.progressBarTextView);
        progressBarTextView.setText(message);

        progressDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(false)
                .create();

        progressDialog.show();
    }

    public void dismissProgressDialog()
    {
        progressDialog.dismiss();
    }
}
