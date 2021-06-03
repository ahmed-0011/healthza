package com.project.cdh;

import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class TextInputEditTextFocusListenerHelper {

    private TextInputEditTextFocusListenerHelper()
    {
    }

    public static void add(Context activity, TextInputEditText textInputEditText) {
        textInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                TextInputLayout textInputLayout = (TextInputLayout) v.getParent().getParent();

                if (hasFocus) {

                    textInputLayout.setStartIconTintList(ContextCompat.getColorStateList(activity, R.color.colorPrimary));
                    textInputLayout.setEndIconTintList(ContextCompat.getColorStateList(activity, R.color.colorPrimary));
                } else {
                    textInputLayout.setStartIconTintList(ContextCompat.getColorStateList(activity, R.color.hint_color));
                    textInputLayout.setEndIconTintList(ContextCompat.getColorStateList(activity, R.color.hint_color));
                }
            }
        });

    }
}
