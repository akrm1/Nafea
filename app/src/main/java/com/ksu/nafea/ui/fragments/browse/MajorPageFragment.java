package com.ksu.nafea.ui.fragments.browse;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.constraintlayout.widget.ConstraintLayout;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.College;
import com.ksu.nafea.logic.Contain;
import com.ksu.nafea.logic.Major;
import com.ksu.nafea.logic.University;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.course.Course;
import com.ksu.nafea.ui.activities.CoursePageActivity;
import com.ksu.nafea.ui.nafea_views.dialogs.PopupDetailsDialog;

public class MajorPageFragment extends SelectFragment<Course>
{
    private University university;
    private College college;
    private Major major;

    private int courseDataCounter = 0;

    @Override
    protected void onSelectFragmentCreated(View main)
    {
        university = User.university;
        college = User.college;
        major = User.major;

        if(major != null)
            setBarTitle(major.getName());

        addTextView(getString(R.string.clickableText_showDepPlan), R.style.Nafea_TextButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onShowPlanClicked();
            }
        });
    }


    @Override
    protected void fillDropdown()
    {
        if(User.major == null)
            User.major = new Major(0, "");

        Contain.retrieveAllLevels(User.major, retrieveDropdownData());
    }

    @Override
    protected void fillRecyclerView(String selectedOption)
    {
        if(selectedOption != null && !selectedOption.equalsIgnoreCase(getString(R.string.allOption)))
            Course.retrieveCoursesInMajor(major, selectedOption, retrieveRecyclerViewData());
        else
            Course.retrieveAllCoursesInMajor(major, retrieveRecyclerViewData());
    }



    @Override
    protected int getItemViewLayout()
    {
        return R.layout.item_view_course_info;
    }

    @Override
    protected void onItemViewBind(View itemView, int position)
    {
        ConstraintLayout mainLayout = (ConstraintLayout) itemView.findViewById(R.id.crsInfo_mainLayout);
        TextView courseSymbol = (TextView) itemView.findViewById(R.id.crsInfo_crsSymbol);
        TextView courseDetails = (TextView) itemView.findViewById(R.id.crsInfo_crsDetails);
        TextView courseDifficultyPercent = (TextView) itemView.findViewById(R.id.crsInfo_crsDifficultyPercent);
        ProgressBar courseDifficultyBar = (ProgressBar) itemView.findViewById(R.id.crsInfo_crsDifficultyBar);

        final Course course = getData().get(position);
        Integer percent = course.getEvaluation().getOverallCourseDifficulty();
        String percentageString = "";

        if(percent != null)
        {
            percentageString = percent  + "%";
            courseDifficultyBar.setProgress(percent);
            courseDifficultyPercent.setTextColor(Color.WHITE);
        }
        else
        {
            percentageString = "غير معروف";
            courseDifficultyBar.setVisibility(View.INVISIBLE);
            courseDifficultyPercent.setTextColor(Color.WHITE);
        }

        courseSymbol.setText(course.getSymbol());
        courseDifficultyPercent.setText(percentageString);


        courseDetails.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PopupDetailsDialog detailsDialog = new PopupDetailsDialog(course.getName(), course.getDescription(), "حسناً");
                detailsDialog.show(getParentFragmentManager(), course.getSymbol());
            }
        });

        mainLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onItemClicked(course);
            }
        });

    }


    private void onShowPlanClicked()
    {
        openPage(R.id.action_majorPage_to_departmentPlan);
    }
    private void onItemClicked(Course course)
    {
        User.course = retrieveCourseData(course);
    }
    private void openCoursePage()
    {
        courseDataCounter = 0;

        // To-Do open Course Page
        Intent intent = new Intent(getContext(), CoursePageActivity.class);
        startActivity(intent);
    }


    private Course retrieveCourseData(final Course course)
    {
        progressDialog.show();

        return Course.retrieveFullCourse(course, new QueryRequestFlag<Boolean>()
        {
            @Override
            public void onQuerySuccess(Boolean resultObject)
            {
                if(resultObject != null)
                {
                    if(resultObject)
                    {
                        if(courseDataCounter == 2)
                        {
                            progressDialog.dismiss();
                            openCoursePage();
                        }
                        else
                            ++courseDataCounter;
                    }
                    else
                    {
                        progressDialog.dismiss();
                        showToastMsg("فشل إظهار البيانات");
                    }
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                progressDialog.dismiss();
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
                showToastMsg("فشلت العملية");
            }
        });
    }



}
