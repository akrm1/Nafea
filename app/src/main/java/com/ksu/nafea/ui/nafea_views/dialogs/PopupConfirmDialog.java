package com.ksu.nafea.ui.nafea_views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class PopupConfirmDialog extends AppCompatDialogFragment
{

    private String title, msg, positiveText, negativeText;
    private int positiveTextColor, negativeTextColor;

    private DialogInterface.OnClickListener positiveClickListener, negativeClickListener;


    public PopupConfirmDialog(String title, String msg, String positiveText, String negativeText,
                              DialogInterface.OnClickListener positiveClickListener, DialogInterface.OnClickListener negativeClickListener)
    {
        this.title = title;
        this.msg = msg;
        this.positiveText = positiveText;
        this.negativeText = negativeText;
        this.positiveTextColor = this.negativeTextColor = Color.parseColor("#FF2B80FF");

        this.positiveClickListener = positiveClickListener;
        this.negativeClickListener = negativeClickListener;
    }
    public PopupConfirmDialog(String title, String msg, String positiveText, String negativeText, int positiveTextColor,
                              int negativeTextColor, DialogInterface.OnClickListener positiveClickListener, DialogInterface.OnClickListener negativeClickListener)
    {
        this.title = title;
        this.msg = msg;
        this.positiveText = positiveText;
        this.negativeText = negativeText;
        this.positiveTextColor = positiveTextColor;
        this.negativeTextColor = negativeTextColor;

        this.positiveClickListener = positiveClickListener;
        this.negativeClickListener = negativeClickListener;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView titleView = new TextView(getActivity());
        TextView msgView = new TextView(getActivity());

        titleView.setText(title);
        int padding = 20;
        titleView.setPadding(padding, padding, padding, padding);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(24);

        msgView.setText(msg);
        //msgView.setPadding(10, 10, 10, 10);
        msgView.setPadding(padding, padding, padding, padding);
        msgView.setGravity(Gravity.RIGHT);
        msgView.setTextSize(18);


        builder.setCustomTitle(titleView)
                .setView(msgView)
                .setPositiveButton(positiveText, positiveClickListener)
                .setNegativeButton(negativeText, negativeClickListener);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(positiveTextColor);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(negativeTextColor);

        return dialog;
    }

}
