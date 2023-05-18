package com.hiutin.smapp.dialog;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;

public class ConfirmDialog extends AlertDialog.Builder {
    public ConfirmDialog(@NonNull Context context, String message, IOnConfirm onConfirm) {
        super(context);
        setTitle("Confirm");
        setMessage(message);
        setNegativeButton("OK", (dialogInterface, i) -> onConfirm.onConfirm());

        setPositiveButton("Cancel", (dialogInterface, i) -> {

        });
    }

    public interface IOnConfirm {
        void onConfirm();
    }
}
