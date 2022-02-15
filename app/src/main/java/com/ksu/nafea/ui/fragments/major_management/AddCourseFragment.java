package com.ksu.nafea.ui.fragments.major_management;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.course.Course;
import com.ksu.nafea.utilities.NafeaUtil;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCourseFragment extends Fragment
{
    private View main;
    public static final String TAG = "AddCourseActivity";
    private ProgressDialog progressDialog;
    private ArrayList<TextView> labels;
    private ArrayList<EditText> fields;
    private TextView majorLabel;
    private Button addButton;


    private static final int NAME           = 0;
    private static final int SYMBOL         = 1;
    private static final int SYMBOL_NUM     = 2;
    private static final int LEVEL          = 3;
    private static final int LEVEL_NUM      = 4;
    private static final int DESCRIPTION    = 5;


    public AddCourseFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddCourseFragment newInstance(String param1, String param2)
    {
        AddCourseFragment fragment = new AddCourseFragment();
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
        main = inflater.inflate(R.layout.activity_add_course, container, false);

        Student student = (Student) User.userAccount;
        if(student.isAdmin() && User.managingMajor == null)
        {
            User.isAddingCourse = true;
            openPage(R.id.action_addCourse_to_browse);
            return main;
        }
        else if(student.isCommunityManager() && User.managingMajor == null)
            User.managingMajor = student.getMajor();



        initViews();
        majorLabel.setText(User.managingMajor.getName());


        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onAddClicked();
            }
        });

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

    private void initViews()
    {
        majorLabel = (TextView) main.findViewById(R.id.addCrs_txt_major);

        labels = new ArrayList<TextView>();
        fields = new ArrayList<EditText>();

        labels.add(NAME, (TextView) main.findViewById(R.id.addCrs_txt_crsName));
        labels.add(SYMBOL, (TextView) main.findViewById(R.id.addCrs_txt_crsSymbol));
        labels.add(SYMBOL_NUM, (TextView) main.findViewById(R.id.addCrs_txt_crsNum));
        labels.add(LEVEL, (TextView) main.findViewById(R.id.addCrs_txt_levelText));
        labels.add(LEVEL_NUM, (TextView) main.findViewById(R.id.addCrs_txt_levelNum));
        labels.add(DESCRIPTION, (TextView) main.findViewById(R.id.addCrs_txt_crsDesc));

        fields.add(NAME, (EditText) main.findViewById(R.id.addCrs_edt_crsName));
        fields.add(SYMBOL, (EditText) main.findViewById(R.id.addCrs_edt_crsSymbol));
        fields.add(SYMBOL_NUM, (EditText) main.findViewById(R.id.addCrs_edt_crsNum));
        fields.add(LEVEL, (EditText) main.findViewById(R.id.addCrs_edt_levelText));
        fields.add(LEVEL_NUM, (EditText) main.findViewById(R.id.addCrs_edt_levelNum));
        fields.add(DESCRIPTION, (EditText) main.findViewById(R.id.addCrs_edt_crsDesc));

        addButton = (Button) main.findViewById(R.id.addCrs_b_add);

        progressDialog = new ProgressDialog(getContext());
    }


    private void onAddClicked()
    {
        if(User.managingMajor != null)
        {
            try
            {
                String courseName = NafeaUtil.validateEmptyField(labels.get(NAME), fields.get(NAME));
                String crsSymbol = NafeaUtil.validateEmptyField(labels.get(SYMBOL), fields.get(SYMBOL));
                String crsNumber = NafeaUtil.validateEmptyField(labels.get(SYMBOL_NUM), fields.get(SYMBOL_NUM));
                String courseLevel = NafeaUtil.validateEmptyField(labels.get(LEVEL), fields.get(LEVEL));
                String courseLevelNum = NafeaUtil.validateEmptyField(labels.get(LEVEL_NUM), fields.get(LEVEL_NUM));
                String courseDesc = NafeaUtil.validateEmptyField(labels.get(DESCRIPTION), fields.get(DESCRIPTION));

                Integer courseLevelIndex = 0;
                try
                {
                    courseLevelIndex = Integer.parseInt(courseLevelNum);
                }
                catch (Exception e)
                {
                    courseLevelIndex = 0;
                }


                String courseSymbol = crsSymbol + " " + crsNumber;
                final Course course = new Course(0, courseName, courseSymbol, courseDesc);

                progressDialog.show();
                Course.insert(User.managingMajor, courseLevel, courseLevelIndex, course, new QueryRequestFlag<QueryPostStatus>()
                {
                    @Override
                    public void onQuerySuccess(QueryPostStatus resultObject)
                    {
                        progressDialog.dismiss();

                        if(resultObject != null)
                        {
                            if(resultObject.getAffectedRows() > 0)
                            {
                                showToastMsg("تم إضافة المقرر \"" + course.getName() + "\" بنجاح.");
                                clearFields();
                            }
                        }
                    }

                    @Override
                    public void onQueryFailure(FailureResponse failure)
                    {
                        progressDialog.dismiss();
                        showToastMsg("فشل إضافة المقرر");
                        Log.e(TAG, failure.getMsg() + "\n" + failure.toString());
                    }
                });
            }
            catch(Exception e)
            {
                showToastMsg("خطأ في الإدخال!");
            }
        }
    }


    private void clearFields()
    {
        fields.get(NAME).setText("");
        fields.get(SYMBOL).setText("");
        fields.get(SYMBOL_NUM).setText("");
        fields.get(LEVEL).setText("");
        fields.get(LEVEL_NUM).setText("");
        fields.get(DESCRIPTION).setText("");
    }


}