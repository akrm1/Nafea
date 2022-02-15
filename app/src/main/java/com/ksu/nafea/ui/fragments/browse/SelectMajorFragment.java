package com.ksu.nafea.ui.fragments.browse;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.College;
import com.ksu.nafea.logic.Major;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.University;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.account.UserAccount;

public class SelectMajorFragment extends SelectFragment<Major>
{
    private University university;
    private College college;


    @Override
    protected void onSelectFragmentCreated(View main)
    {
        university = User.university;
        college = User.college;

        LinearLayout topGroup = (LinearLayout) main.findViewById(R.id.select_topGroup);
        topGroup.removeView(dropdown);

        if(university != null)
            addTextView(university.getName());
        if(college != null)
            addTextView(college.getName());

        fillRecyclerView(getString(R.string.allOption));
    }

    @Override
    protected void fillRecyclerView(final String selectedOption)
    {
        Major.retrieveMajorsInCollege(college, retrieveRecyclerViewData());
    }



    @Override
    protected String getDefaultItemString(int position)
    {
        return getData().get(position).getName();
    }

    @Override
    protected void onDefaultItemClicked(int position)
    {
        Major major = getData().get(position);
        User.major = major;

        if(User.isAddingCourse)
        {
            User.managingMajor = major;
            User.isAddingCourse = false;
            openPage(R.id.action_selectMajor_to_addCourse);
            return;
        }
        else if(User.isRemovingCourse)
        {
            User.managingMajor = major;
            User.isRemovingCourse = false;
            openPage(R.id.action_selectMajor_to_removeCourse);
            return;
        }
        else if(User.isUploadDepPlan)
        {
            User.managingMajor = major;
            User.isUploadDepPlan = false;
            openPage(R.id.action_selectMajor_to_uploadDepartmentPlanPage);
            return;
        }



        if(User.isBrowsing)
        {
            User.major = major;
            openPage(R.id.action_selectMajor_to_majorPage);
        }
        else
        {
            String email = User.userAccount.getEmail();
            String password = User.userAccount.getPassword();
            String firstName = User.userAccount.getFirstName();
            String lastName = User.userAccount.getLastName();

            Student newStudent = new Student(email, password, firstName, lastName, major.getId());

            UserAccount.register(newStudent, new QueryRequestFlag<QueryPostStatus>()
            {
                @Override
                public void onQuerySuccess(QueryPostStatus resultObject)
                {
                    showToastMsg("تم التسجيل بنجاح");
                    onRegisterComplete();
                }

                @Override
                public void onQueryFailure(FailureResponse failure)
                {
                    showToastMsg("فشل التسجيل");
                    Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
                    onRegisterComplete();
                }
            });
        }

    }

    private void onRegisterComplete()
    {
        User.isBrowsing = true;
        openPage(R.id.action_selectMajor_to_login);
    }


}
