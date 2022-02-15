package com.ksu.nafea.ui.fragments.browse;

import com.ksu.nafea.R;
import com.ksu.nafea.logic.GeneralPool;
import com.ksu.nafea.logic.University;
import com.ksu.nafea.logic.User;

public class SelectUniversityFragment extends SelectFragment<University>
{

    @Override
    protected void fillDropdown()
    {
        University.retrieveAllCities(retrieveDropdownData());
    }

    @Override
    protected void fillRecyclerView(final String selectedOption)
    {
        if(selectedOption != null && !selectedOption.equalsIgnoreCase(getString(R.string.allOption)))
            University.retrieveUniversitiesInCity(selectedOption, retrieveRecyclerViewData());
        else
            GeneralPool.retrieveAll(University.class, retrieveRecyclerViewData());
    }



    @Override
    protected String getDefaultItemString(int position)
    {
        return getData().get(position).getName();
    }

    @Override
    protected void onDefaultItemClicked(int position)
    {
        User.university = getData().get(position);
        openPage(R.id.action_selectUniversity_to_selectCollege);
    }

}
