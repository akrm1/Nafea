package com.ksu.nafea.ui.fragments.browse;


import android.view.View;

import com.ksu.nafea.R;
import com.ksu.nafea.logic.College;
import com.ksu.nafea.logic.University;
import com.ksu.nafea.logic.User;

public class SelectCollegeFragment extends SelectFragment<College>
{
    private University university;


    @Override
    protected void onSelectFragmentCreated(View main)
    {
        university = User.university;

        if(university != null)
            addTextView(university.getName());
    }



    @Override
    protected void fillDropdown()
    {
        College.retrieveAllCategoriesInUniversity(university, retrieveDropdownData());
    }

    @Override
    protected void fillRecyclerView(final String selectedOption)
    {
        if(selectedOption != null && !selectedOption.equalsIgnoreCase(getString(R.string.allOption)))
            College.retrieveCollegesInUniversity(university, selectedOption, retrieveRecyclerViewData());
        else
            College.retrieveCollegesInUniversity(university, retrieveRecyclerViewData());

    }



    @Override
    protected String getDefaultItemString(int position)
    {
        return getData().get(position).getName();
    }

    @Override
    protected void onDefaultItemClicked(int position)
    {
        User.college = getData().get(position);
        openPage(R.id.action_selectCollege_to_selectMajor);
    }


}
