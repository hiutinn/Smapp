package com.hiutin.smapp.dialog;

import android.app.AlertDialog;
import android.content.Context;

public class LoadingDialog extends AlertDialog {
    public LoadingDialog(Context context) {
        super(context);
        setMessage("Loading....");
    }
}
