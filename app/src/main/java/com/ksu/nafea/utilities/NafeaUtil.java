package com.ksu.nafea.utilities;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Random;

public class NafeaUtil
{

    public static void showToastMsg(Context context, String msg)
    {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void setBarTitle(FragmentActivity fragmentActivity, String title)
    {
        ((AppCompatActivity) fragmentActivity).getSupportActionBar().setTitle(title);
    }

    public static void clearFields(ArrayList<EditText> field)
    {
        for(int i = 0; i < field.size(); i++)
        {
            field.get(i).setText("");
            field.get(i).setError(null);
            field.get(i).setTextColor(Color.BLACK);
        }
    }

    public static void updateField(EditText field, String errorMsg)
    {
        // if errorMsg is empty that's mean there is no error.
        if(errorMsg.isEmpty())
        {
            field.setTextColor(Color.BLACK);
        }
        else
        {
            field.setError(errorMsg);
            field.setTextColor(Color.RED);
        }
    }

    public static String validateEmptyField(TextView label, EditText field) throws Exception
    {
        String fieldText = field.getText().toString();
        if(fieldText.isEmpty())
        {
            String msg = "خانة " + label.getText().toString().replace(":", "") + " فارغة!";
            field.setError(msg);
            throw new Exception(msg);
        }

        return fieldText;
    }

    public static int getRandomColor(int alpha)
    {
        Random randGen = new Random();
        int red = randGen.nextInt(256);
        int green = randGen.nextInt(256);
        int blue = randGen.nextInt(256);

        return Color.argb(alpha,red, green, blue);
    }

    public static int getRangedRandomColor(int min, int max, int alpha)
    {
        Random randGen = new Random();
        int red = randGen.nextInt((max - min) + 1) + min;
        int green = randGen.nextInt((max - min) + 1) + min;
        int blue = randGen.nextInt((max - min) + 1) + min;

        return Color.argb(alpha,red, green, blue);
    }

    public static int changeColorAlpha(int color, int alpha)
    {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        return Color.argb(alpha,red, green, blue);
    }



}
