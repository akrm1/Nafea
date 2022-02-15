package com.ksu.nafea.ui.fragments.major_management;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.FilesStorage;
import com.ksu.nafea.logic.Major;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.utilities.NafeaUtil;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadDepartmentPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadDepartmentPlanFragment extends Fragment
{
    private static final String TAG = "UploadDepPlan";
    private View main;
    private TextView majorLabel;
    private ImageView planImage;
    private Button chooseImgButton, postButton;
    private ProgressDialog progressDialog;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private Uri imageUri = null;


    public UploadDepartmentPlanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadDepartmentPlanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadDepartmentPlanFragment newInstance(String param1, String param2)
    {
        UploadDepartmentPlanFragment fragment = new UploadDepartmentPlanFragment();
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
        main = inflater.inflate(R.layout.fragment_upload_department_plan, container, false);

        Student student = (Student) User.userAccount;
        if(student.isAdmin() && User.managingMajor == null)
        {
            User.isUploadDepPlan = true;
            openPage(R.id.action_uploadDepartmentPlanPage_to_browse);
            return main;
        }
        else if(student.isCommunityManager() && User.managingMajor == null)
            User.managingMajor = student.getMajor();

        initViews();
        initListeners();
        majorLabel.setText(User.managingMajor.getName());

        return main;
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
        majorLabel = (TextView) main.findViewById(R.id.upldDep_txt_major);
        planImage = (ImageView) main.findViewById(R.id.upldDep_img_photo);
        chooseImgButton = (Button) main.findViewById(R.id.upldDep_b_choosePhoto);
        postButton = (Button) main.findViewById(R.id.upldDep_b_post);

        progressDialog = new ProgressDialog(getContext());
    }

    private void initListeners()
    {
        chooseImgButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onChooseImgClicked();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onPostClicked();
            }
        });
    }

    private void onChooseImgClicked()
    {
        //handle button click
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            {
                //permission not granted, request it.
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                //show popup for runtime permission
                requestPermissions(permissions, PERMISSION_CODE);
            }
            else
            {
                //permission already granted
                pickImageFromGallery();
            }
        }
        else
        {
            //system os is less then marshmallow
            pickImageFromGallery();
        }
    }

    private void onPostClicked()
    {
        final Major major = User.managingMajor;
        if(major == null)
            return;

        if(imageUri == null)
        {
            String msg = "إختر صورة للمحتوى اولاً";
            NafeaUtil.showToastMsg(getContext(), msg);
            return;
        }

        final Student student = (Student)User.userAccount;
        progressDialog.show();
        FilesStorage.uploadMajorPlan(major, imageUri, new QueryRequestFlag<QueryPostStatus>()
        {
            @Override
            public void onQuerySuccess(QueryPostStatus resultObject)
            {
                progressDialog.dismiss();

                if(resultObject != null)
                {
                    if(resultObject.getAffectedRows() > 0)
                    {
                        User.isUserInfoUpdated = true;
                        NafeaUtil.showToastMsg(getContext(), "تم نشر خطة " + major.getName());
                        openPage(R.id.action_uploadDepartmentPlanPage_to_homePage);
                        return;
                    }
                }

                NafeaUtil.showToastMsg(getContext(), "فشل نشر خطة القسم");
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                progressDialog.dismiss();

                NafeaUtil.showToastMsg(getContext(), "فشل نشر خطة القسم");
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
            }
        });

    }



    //---------------------------------------------------------[Pick Image methods]---------------------------------------------------------
    private void pickImageFromGallery()
    {
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //handle result of runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_CODE:
            {
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //permission was granted
                    pickImageFromGallery();
                }
                else
                {
                    //permission was denied
                    String msg = "تم رفض إذن الوصول";
                    NafeaUtil.showToastMsg(getContext(), msg);
                }
            }
        }
    }


    //handle result of picked image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE)
        {
            imageUri = data.getData();
            //set image to image view
            planImage.setImageURI(imageUri);

        }
    }



}