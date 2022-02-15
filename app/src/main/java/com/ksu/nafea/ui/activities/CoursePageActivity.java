package com.ksu.nafea.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ksu.nafea.R;

import java.util.Stack;

public class CoursePageActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private AppBarConfiguration appBarConfiguration;
    public BottomNavigationView bottomNav;

    private Stack<Integer> pagesStack;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_page);

        pagesStack = new Stack<Integer>();

        toolbar = (Toolbar) findViewById(R.id.crs_toolbar);
        setSupportActionBar(toolbar);


        bottomNav = (BottomNavigationView)findViewById(R.id.crs_bottomNav);
        NavController navController = Navigation.findNavController(this, R.id.crs_navHost);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.crsNav_aboutCourse, R.id.crsNav_documents, R.id.crsNav_videos, R.id.crsNav_physMats, R.id.crsNav_comments).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }


    @Override
    public void onBackPressed()
    {
        onBackClicked();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        toolbar.setNavigationIcon(R.drawable.ic_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackClicked();
            }
        });
        return true;
    }

   @Override
   public boolean onSupportNavigateUp()
   {
       return super.onSupportNavigateUp();
   }


   public void onBackClicked()
   {
       if(pagesStack.isEmpty())
           finish();
       else
       {
           openPage(pagesStack.pop());

           if(pagesStack.isEmpty())
               bottomNav.setVisibility(View.VISIBLE);
           else
               bottomNav.setVisibility(View.GONE);
       }

       toolbar.setNavigationIcon(R.drawable.ic_arrow);
   }

    private void openPage(int pageID)
    {
        Navigation.findNavController(this, R.id.crs_navHost).navigate(pageID);
    }


    public boolean isPageStackEmpty()
    {
        return pagesStack.isEmpty();
    }

    public void pushPage(int pageID)
    {
        pagesStack.push(pageID);
    }

    protected void setBottomNavigationVisibility(boolean visibility)
    {
        int visibilityValue = visibility ? View.VISIBLE : View.GONE;
        bottomNav.setVisibility(visibilityValue);
    }

    public void openPage(int targetPageID, int backPageID, boolean visibility)
    {
        setBottomNavigationVisibility(visibility);
        Navigation.findNavController(this, R.id.crs_navHost).navigate(targetPageID);
        pushPage(backPageID);
    }


}