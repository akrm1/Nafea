package com.ksu.nafea.ui.fragments.course;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.course.CourseEvaluation;
import com.ksu.nafea.ui.activities.CoursePageActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutCourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutCourseFragment extends Fragment
{
    public static final String TAG = "AboutCourse";

    private static final String PREFIX_COURSE_NAME = "اسم المقرر: ";
    private static final String PREFIX_COURSE_SYMBOL = "رمز المقرر: ";
    private static final String PREFIX_COURSE_DESCRIPTION = "وصف المقرر:\n";
    private static final String POSTFIX_PERCENTAGE = "%";
    private static final int DB_TRANS_FACTOR = 2;

    private View main;

    private TextView courseName, courseSymbol, courseDesc;
    private ConstraintLayout evaluationsLayout;
    private TextView unknownEvaluation;
    private ArrayList<TextView> progressPercentages;
    private ArrayList<ProgressBar> progressBars;
    private ArrayList<RatingBar> ratingBars;
    private Button confirmEvaluationButton;

    private static final int CONTENT_SIZE = 0;
    private static final int ASSIGNMENT_DIFFICULTY = 1;
    private static final int EXAMS_DIFFICULTY = 2;
    private static final int OVERALL_COURSE_DIFFICULTY = 3;

    private boolean hasEvaluatedBefore = false;

    public AboutCourseFragment()
    {

        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutCourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutCourseFragment newInstance(String param1, String param2)
    {
        AboutCourseFragment fragment = new AboutCourseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        main = inflater.inflate(R.layout.fragment_about_course, container, false);

        initView();
        setBarTitle(User.course.getSymbol());
        initOnConfirmEvaluationListener();

        fillCourseBasicInfo();
        fillCourseEvaluation();
        fillStudentRating();

        return main;
    }


    //--------------------------------------------------------------[UI methods]--------------------------------------------------------------
    private void initView()
    {
        progressPercentages = new ArrayList<TextView>();
        progressBars = new ArrayList<ProgressBar>();
        ratingBars = new ArrayList<RatingBar>();

        courseName = (TextView) main.findViewById(R.id.aboutCrs_txt_crsName);
        courseSymbol = (TextView) main.findViewById(R.id.aboutCrs_txt_crsSymbol);
        courseDesc = (TextView) main.findViewById(R.id.aboutCrs_txt_crsDesc);

        evaluationsLayout = (ConstraintLayout) main.findViewById(R.id.aboutCrs_layout_crsEvaluations);
        unknownEvaluation = (TextView) main.findViewById(R.id.aboutCrs_staticTxt_unkownEva);

        progressPercentages.add(CONTENT_SIZE, (TextView) main.findViewById(R.id.aboutCrs_txt_crsContentSize));
        progressPercentages.add(ASSIGNMENT_DIFFICULTY, (TextView) main.findViewById(R.id.aboutCrs_txt_crsAssignmentsDiff));
        progressPercentages.add(EXAMS_DIFFICULTY, (TextView) main.findViewById(R.id.aboutCrs_txt_crsExamsEase));
        progressPercentages.add(OVERALL_COURSE_DIFFICULTY, (TextView) main.findViewById(R.id.aboutCrs_txt_crsDifficulty));

        progressBars.add(CONTENT_SIZE, (ProgressBar) main.findViewById(R.id.aboutCrs_progress_crsContentSize));
        progressBars.add(ASSIGNMENT_DIFFICULTY, (ProgressBar) main.findViewById(R.id.aboutCrs_progress_crsAssignmentsDiff));
        progressBars.add(EXAMS_DIFFICULTY, (ProgressBar) main.findViewById(R.id.aboutCrs_progress_crsExamsEase));
        progressBars.add(OVERALL_COURSE_DIFFICULTY, (ProgressBar) main.findViewById(R.id.aboutCrs_progress_crsDifficulty));


        ratingBars.add(CONTENT_SIZE, (RatingBar) main.findViewById(R.id.aboutCrs_rate_evaContentSize));
        ratingBars.add(ASSIGNMENT_DIFFICULTY, (RatingBar) main.findViewById(R.id.aboutCrs_rate_evaAssignmentsDiff));
        ratingBars.add(EXAMS_DIFFICULTY, (RatingBar) main.findViewById(R.id.aboutCrs_rate_evaExamsEase));

        confirmEvaluationButton = (Button) main.findViewById(R.id.aboutCrs_b_confirmEvaluation);
    }

    private void initOnConfirmEvaluationListener()
    {
        confirmEvaluationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onConfirmEvaluationClicked();
            }
        });
    }



    protected void showToastMsg(String msg)
    {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    protected void openPage(int targetPageID, int backPageID, boolean visibility)
    {
        ((CoursePageActivity) getActivity()).openPage(targetPageID, backPageID, visibility);
    }

    protected void setBarTitle(String title)
    {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }


    //--------------------------------------------------------------[logic methods]--------------------------------------------------------------

    private void fillCourseBasicInfo()
    {
        String name = PREFIX_COURSE_NAME + User.course.getName();
        String symbol = PREFIX_COURSE_SYMBOL + User.course.getSymbol();
        String desc = PREFIX_COURSE_DESCRIPTION + User.course.getDescription();

        courseName.setText(name);
        courseSymbol.setText(symbol);
        courseDesc.setText(desc);
    }

    private void fillCourseEvaluation()
    {
        Integer overallEvaluation = User.course.getEvaluation().getOverallCourseDifficulty();
        if(overallEvaluation == null)
        {
            evaluationsLayout.setVisibility(View.GONE);
            unknownEvaluation.setVisibility(View.VISIBLE);
            return;
        }

        int contentSize = User.course.getEvaluation().getContentSizePercentage();
        int assignmentDifficulty = User.course.getEvaluation().getAssignmentsDifficultyPercentage();
        int examsDifficulty = User.course.getEvaluation().getExamsDifficultyPercentage();

        progressBars.get(CONTENT_SIZE).setProgress(contentSize);
        progressBars.get(ASSIGNMENT_DIFFICULTY).setProgress(assignmentDifficulty);
        progressBars.get(EXAMS_DIFFICULTY).setProgress(examsDifficulty);
        progressBars.get(OVERALL_COURSE_DIFFICULTY).setProgress(overallEvaluation);

        progressPercentages.get(CONTENT_SIZE).setText(contentSize + POSTFIX_PERCENTAGE);
        progressPercentages.get(ASSIGNMENT_DIFFICULTY).setText(assignmentDifficulty + POSTFIX_PERCENTAGE);
        progressPercentages.get(EXAMS_DIFFICULTY).setText(examsDifficulty + POSTFIX_PERCENTAGE);
        progressPercentages.get(OVERALL_COURSE_DIFFICULTY).setText(overallEvaluation + POSTFIX_PERCENTAGE);
    }

    private void fillStudentRating()
    {
        if(User.userAccount == null)
            return;

        Student student = (Student) User.userAccount;

        CourseEvaluation.retrieveStudentEvaluation(student, User.course, new QueryRequestFlag<CourseEvaluation>()
        {
            @Override
            public void onQuerySuccess(CourseEvaluation resultObject)
            {
                if(resultObject != null)
                {
                    hasEvaluatedBefore = true;

                    Double contentSize = resultObject.getContentSize();
                    Double assignmentsDifficulty = resultObject.getAssignmentsDifficulty();
                    Double examsDifficulty = resultObject.getExamsDifficulty();

                    if(contentSize != null)
                        ratingBars.get(CONTENT_SIZE).setRating(contentSize.floatValue() / DB_TRANS_FACTOR);
                    if(assignmentsDifficulty != null)
                        ratingBars.get(ASSIGNMENT_DIFFICULTY).setRating(assignmentsDifficulty.floatValue() / DB_TRANS_FACTOR);
                    if(examsDifficulty != null)
                        ratingBars.get(EXAMS_DIFFICULTY).setRating(examsDifficulty.floatValue() / DB_TRANS_FACTOR);
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
            }
        });
    }



    private void onConfirmEvaluationClicked()
    {
        if(User.userAccount == null)
        {
            showToastMsg(getString(R.string.toastMsg_loginFirst));
            return;
        }

        Student student = (Student) User.userAccount;
        if(student.isAdmin())
        {
            showToastMsg(getString(R.string.emat_notAllowedEvaluate));
            return;
        }


        double contentSize = ratingBars.get(CONTENT_SIZE).getRating() * DB_TRANS_FACTOR;
        double assignmentsDifficulty = ratingBars.get(ASSIGNMENT_DIFFICULTY).getRating() * DB_TRANS_FACTOR;
        double examsDifficulty = ratingBars.get(EXAMS_DIFFICULTY).getRating() * DB_TRANS_FACTOR;

        CourseEvaluation newEvaluation = new CourseEvaluation(contentSize, assignmentsDifficulty, examsDifficulty);

        if(hasEvaluatedBefore)
            CourseEvaluation.update(student, User.course, newEvaluation, createUpdateEvaluationRequestFlag());
        else
            CourseEvaluation.insert(student, User.course, newEvaluation, createUpdateEvaluationRequestFlag());

    }
    private QueryRequestFlag<QueryPostStatus> createUpdateEvaluationRequestFlag()
    {
        return new QueryRequestFlag<QueryPostStatus>()
        {
            @Override
            public void onQuerySuccess(QueryPostStatus resultObject)
            {
                if(resultObject != null)
                {
                    showToastMsg("تم التقييم بنجاح");
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
            }
        };
    }


}