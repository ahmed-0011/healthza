package com.example.healthza;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class LoadingDialog
{
    private Activity activity;
    private AlertDialog loadingDialog;
    public LoadingDialog(Activity activity)
    {
        this.activity = activity;
    }

    public void showLoadingDialog()
    {

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.loading_dialog, null);

        loadingDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(false)
                .create();

        loadingDialog.show();
    }

    public void dismissLoadingDialog()
    {
        loadingDialog.dismiss();
    }
}
