package com.ksu.nafea.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.ksu.nafea.R;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.utilities.NafeaUtil;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity
{

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private Menu navMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_homePage, R.id.nav_login, R.id.nav_browse, R.id.nav_majorPage, R.id.nav_addCourse, R.id.nav_removeCourse, R.id.nav_uploadDepartmentPlanPage)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        navMenu = navigationView.getMenu();
        initNavigationMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.navigation_menu, menu);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    private void initNavigationMenu()
    {
        final Context context = this;
        navMenu.findItem(R.id.nav_login).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if(User.userAccount != null)
                {
                    NafeaUtil.showToastMsg(context, "تم تسجيل الخروج");
                    User.userAccount = null;
                    setHeaderElementsVisibility(View.INVISIBLE);

                    MenuItem mngItem = navMenu.findItem(R.id.navSection_manageCourses);
                    MenuItem logoutItem = navMenu.findItem(R.id.navSection_logout);

                    if(mngItem.isVisible())
                        mngItem.setVisible(false);
                    if(logoutItem.isVisible())
                        logoutItem.setVisible(false);
                }

                return false;
            }
        });


        setHeaderElementsVisibility(View.INVISIBLE);
        navMenu.findItem(R.id.navSection_manageCourses).setVisible(false);
        navMenu.findItem(R.id.navSection_logout).setVisible(false);




        MenuItem.OnMenuItemClickListener mangingListener = new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                User.managingMajor = null;
                return false;
            }
        };


        navMenu.findItem(R.id.nav_addCourse).setOnMenuItemClickListener(mangingListener);
        navMenu.findItem(R.id.nav_removeCourse).setOnMenuItemClickListener(mangingListener);
        navMenu.findItem(R.id.nav_uploadDepartmentPlanPage).setOnMenuItemClickListener(mangingListener);

        navMenu.findItem(R.id.nav_browse).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                User.isAddingCourse = false;
                User.isRemovingCourse = false;
                User.isUploadDepPlan = false;
                User.isBrowsing = true;
                return false;
            }
        });
    }

    public void setHeaderElementsVisibility(int visibility)
    {
        View headerView = navigationView.getHeaderView(0);

        TextView email = (TextView) headerView.findViewById(R.id.navHeader_txt_email);
        TextView fullName = (TextView) headerView.findViewById(R.id.navHeader_txt_fullName);

        email.setVisibility(visibility);
        fullName.setVisibility(visibility);
    }

    public void setHeaderElementText(int elementID, String text)
    {
        View headerView = navigationView.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(elementID);

        if(textView != null)
        {
            textView.setText(text);
        }
    }


    public Menu getNavMenu()
    {
        return navMenu;
    }


}