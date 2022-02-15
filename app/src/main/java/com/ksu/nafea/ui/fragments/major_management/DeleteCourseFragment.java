package com.ksu.nafea.ui.fragments.major_management;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.course.Course;
import com.ksu.nafea.ui.nafea_views.dialogs.PopupDetailsDialog;
import com.ksu.nafea.ui.nafea_views.recycler_view.GeneralRecyclerAdapter;
import com.ksu.nafea.ui.nafea_views.recycler_view.ListAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeleteCourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeleteCourseFragment extends Fragment
{
    public static final String TAG = "DeleteCourseActivity";
    private View main;
    private Context context;
    private ProgressDialog progressDialog;

    private RecyclerView coursesListView;
    private Button deleteButton;
    private TextView majorLabel;

    private ArrayList<Course> courses;
    private ArrayList<Course> targetCourses;

    public DeleteCourseFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeleteCourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeleteCourseFragment newInstance(String param1, String param2)
    {
        DeleteCourseFragment fragment = new DeleteCourseFragment();
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
        main = inflater.inflate(R.layout.activity_delete_course, container, false);
        context = getContext();

        Student student = (Student) User.userAccount;
        if(student.isAdmin() && User.managingMajor == null)
        {
            User.isRemovingCourse = true;
            openPage(R.id.action_removeCourse_to_browse);
            return main;
        }
        else if(student.isCommunityManager() && User.managingMajor == null)
            User.managingMajor = student.getMajor();


        progressDialog = new ProgressDialog(context);
        coursesListView = (RecyclerView) main.findViewById(R.id.delCrs_coursesList);
        deleteButton = (Button) main.findViewById(R.id.delCrs_b_delete);
        majorLabel = (TextView) main.findViewById(R.id.delCrs_txt_major);

        majorLabel.setText(User.managingMajor.getName());


        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                executeDelete();
            }
        });



        if(User.managingMajor != null)
        {
            progressDialog.show();


            Course.retrieveAllCoursesInMajor(User.managingMajor, new QueryRequestFlag<ArrayList<Course>>()
            {
                @Override
                public void onQuerySuccess(final ArrayList<Course> resultObject)
                {
                    progressDialog.dismiss();

                    if(resultObject != null)
                    {
                        courses = resultObject;
                        targetCourses = new ArrayList<Course>();

                        refreshCoursesRecycleView();
                    }
                }

                @Override
                public void onQueryFailure(FailureResponse failure)
                {
                    progressDialog.dismiss();

                    showToastMsg("فشل عرض المقررات");
                    Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
                }
            });
        }

        return main;
    }


    @Override
    public void onStop()
    {
        super.onStop();
        //User.managingMajor = null;
    }



    protected void setBarTitle(String title)
    {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    protected void openPage(int pageID)
    {
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(pageID);
    }

    private void showToastMsg(String msg)
    {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }



    private void executeDelete()
    {
        if(targetCourses.isEmpty())
            return;


        progressDialog.show();
        Course.deleteAllCourses(User.managingMajor, targetCourses, new QueryRequestFlag<QueryPostStatus>()
        {
            @Override
            public void onQuerySuccess(QueryPostStatus resultObject)
            {
                progressDialog.dismiss();

                if(resultObject != null)
                {
                    if(resultObject.getAffectedRows() > 0)
                    {
                        showToastMsg("تم حذف المقررات");

                        for(int i = 0; i < targetCourses.size(); i++)
                            courses.remove(targetCourses.get(i));

                        targetCourses.clear();
                        refreshCoursesRecycleView();
                    }
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                progressDialog.dismiss();
                showToastMsg("فشل حذف المقررات");
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
            }
        });

    }


    public void refreshCoursesRecycleView()
    {
        ListAdapter listAdapter = new ListAdapter()
        {
            @Override
            public int getResourceLayout()
            {
                return R.layout.item_view_delete_course;
            }

            @Override
            public int getItemCount()
            {
                return courses.size();
            }

            @Override
            public void onBind(View itemView, final int position)
            {
                CheckBox course = (CheckBox) itemView.findViewById(R.id.delRow_chBox_course);
                TextView courseDetails = (TextView) itemView.findViewById(R.id.delRow_txtb_course);

                course.setText(courses.get(position).getSymbol());

                course.setOnCheckedChangeListener(getOnCourseCheckedChangeListener(position));
                courseDetails.setOnClickListener(getOnCourseDetailsClicked(position));
            }
        };


        GeneralRecyclerAdapter adapter = new GeneralRecyclerAdapter(context, listAdapter);
        coursesListView.setItemViewCacheSize(courses.size());
        coursesListView.setAdapter(adapter);
        coursesListView.setLayoutManager(new LinearLayoutManager(context));

    }


    private CompoundButton.OnCheckedChangeListener getOnCourseCheckedChangeListener(final int position)
    {
        return new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                    targetCourses.add(courses.get(position));
                else
                    targetCourses.remove(courses.get(position));
            }
        };
    }

    private View.OnClickListener getOnCourseDetailsClicked(final int position)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Course course = courses.get(position);

                PopupDetailsDialog detailsDialog = new PopupDetailsDialog(course.getName(), course.getDescription(), "حسناً");
                detailsDialog.show(getActivity().getSupportFragmentManager(), course.getSymbol());
            }
        };
    }


}