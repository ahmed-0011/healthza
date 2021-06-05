package com.project.cdh.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.cdh.ProgressDialog;
import com.project.cdh.R;
import com.project.cdh.Toasty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivity extends AppCompatActivity
{
    private EditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button changePasswordButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        password = sharedPreferences.getString("password", "");

        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        changePasswordButton = findViewById(R.id.changePasswordButton);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }


    private boolean isPasswordMatched(String password, String confirmPassword)
    {
        return password.equals(confirmPassword);
    }


    private void changePassword()
    {
        String currentPassword = currentPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if(!isValidPassword(currentPassword))
        {
           Toasty.showText(this, getString(R.string.password_length_error), Toasty.WARNING, Toast.LENGTH_LONG);
           currentPasswordEditText.requestFocus();
        }
        else if(!isValidPassword(newPassword))
        {
            Toasty.showText(this, getString(R.string.password_length_error), Toasty.WARNING, Toast.LENGTH_LONG);
            newPasswordEditText.requestFocus();
        }
        else if(!isValidPassword(confirmPassword))
        {
            Toasty.showText(this, getString(R.string.password_length_error), Toasty.WARNING, Toast.LENGTH_LONG);
            confirmPasswordEditText.requestFocus();
        }
        else if (!isPasswordMatched(newPassword, confirmPassword))
        {
            Toasty.showText(this, getString(R.string.passwords_not_match_error), Toasty.WARNING, Toast.LENGTH_LONG);
            confirmPasswordEditText.requestFocus();
        }
        else
        {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.showProgressDialog();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            String email = user.getEmail();

            AuthCredential credential = EmailAuthProvider
                    .getCredential(email, currentPassword);

            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();

                                            password = newPassword;
                                            editor.putString("password", password);
                                            editor.apply();

                                            Toasty.showText(ChangePasswordActivity.this, "Password updated successfully"
                                            ,Toasty.SUCCESS, Toast.LENGTH_LONG);

                                        }
                                    }
                                });
                            }
                            else
                                Toasty.showText(ChangePasswordActivity.this, "Current password you entered is not correct", Toasty.ERROR, Toast.LENGTH_LONG);

                            progressDialog.dismissProgressDialog();

                        }
                    });
        }
    }
}