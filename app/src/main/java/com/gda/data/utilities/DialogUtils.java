package com.gda.data.utilities;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public final class DialogUtils {
    public static void showInputDialog(Activity activity, View view, DialogInterface.OnShowListener callback) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .setView(view)
                .setPositiveButton("OK", null)
                .create();

        dialog.setOnShowListener(callback);

        Window window = dialog.getWindow();
        window.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.y = DimensionsUtils.dialogYOffset;
        window.setAttributes(layoutParams);

        dialog.show();
    }

    private DialogUtils() {
    }
}
