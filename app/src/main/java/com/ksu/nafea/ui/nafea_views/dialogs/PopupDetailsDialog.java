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

public class PopupDetailsDialog extends AppCompatDialogFragment
{
    private String title, msg, buttonText;
    private int buttonTextColor;

    public PopupDetailsDialog(String title, String msg, String buttonText)
    {
        this.title = title;
        this.msg = msg;
        this.buttonText = buttonText;
        this.buttonTextColor = Color.parseColor("#FF2B80FF");
    }
    public PopupDetailsDialog(String title, String msg, String buttonText, int buttonTextColor)
    {
        this.title = title;
        this.msg = msg;
        this.buttonText = buttonText;
        this.buttonTextColor = buttonTextColor;
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


        builder.setCustomTitle(titleView).setView(msgView).setPositiveButton(buttonText, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(buttonTextColor);

        return dialog;
    }


}
