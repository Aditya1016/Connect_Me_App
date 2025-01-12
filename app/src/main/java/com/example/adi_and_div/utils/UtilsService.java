package com.example.adi_and_div.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.view.View;
import android.content.Context;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;

public class UtilsService {
    public void hideKeyboard(View view, Activity activity){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (ActivityNotFoundException e){
            e.printStackTrace();
        }
    }

    public void showSnackBar(View view, String msg){
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }
}
