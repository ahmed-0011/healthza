package com.example.healthza;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class SplashScreenActivity extends AppCompatActivity
{

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null)
        {
            SharedPreferences sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);

            String userType = sharedPreferences.getString("user_type", "");

            Intent intent;
            if(userType.equals("patient"))
                intent = new Intent(this, PatientHomeActivity.class);
            else
                intent = new Intent(this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            Toast.makeText(this, "welcome " + firebaseAuth.getCurrentUser().getDisplayName()
                    , Toast.LENGTH_LONG).show();
        }
        else
            startActivity(new Intent(this, WelcomeActivity.class));
    }

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
}