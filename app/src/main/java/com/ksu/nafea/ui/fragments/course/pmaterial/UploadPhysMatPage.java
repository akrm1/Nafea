package com.ksu.nafea.ui.fragments.course.pmaterial;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.FilesStorage;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.material.PhysicalMaterial;
import com.ksu.nafea.ui.activities.CoursePageActivity;
import com.ksu.nafea.utilities.NafeaUtil;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadPhysMatPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadPhysMatPage extends Fragment
{
    private static final String TAG = "UploadPhysMat";
    protected View main;
    private ImageView matImageView;
    private Button chooseImgButton, postButton;
    private EditText nameField, priceField, phoneField, cityField;
    private ProgressDialog progressDialog;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private Uri imageUri = null;


    public UploadPhysMatPage()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadPhysMatPage.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadPhysMatPage newInstance(String param1, String param2)
    {
        UploadPhysMatPage fragment = new UploadPhysMatPage();
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
        main = inflater.inflate(R.layout.fragment_upload_phys_mat_page, container, false);

        initViews();
        initListeners();

        // Inflate the layout for this fragment
        return main;
    }

    private void initViews()
    {
        nameField = (EditText) main.findViewById(R.id.upldPhys_ed_name);
        priceField = (EditText) main.findViewById(R.id.upldPhys_ed_price);
        phoneField = (EditText) main.findViewById(R.id.upldPhys_ed_phone);
        cityField = (EditText) main.findViewById(R.id.upldPhys_ed_city);

        matImageView = (ImageView) main.findViewById(R.id.upldPhys_img_photo);
        chooseImgButton = (Button) main.findViewById(R.id.upldPhys_b_choosePhoto);
        postButton = (Button) main.findViewById(R.id.upldPhys_b_post);

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
        String name = nameField.getText().toString();
        String priceString = priceField.getText().toString();
        String phoneString = phoneField.getText().toString();
        String city = cityField.getText().toString();

        if(!isFieldsValid(name, priceString, phoneString, city))
            return;

        if(imageUri == null)
        {
            String msg = "إختر صورة للمحتوى اولاً";
            NafeaUtil.showToastMsg(getContext(), msg);
            return;
        }

        try
        {
            Integer phone = Integer.parseInt(phoneString);
            Double price = Double.parseDouble(priceString);

            Student student = (Student) User.userAccount;
            PhysicalMaterial material = new PhysicalMaterial(0, name, phone, null, city, price);
            uploadPMat(student, material);
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
            NafeaUtil.showToastMsg(getContext(), "هناك خطأ بالمدخلات \n(قد يكون برقم الجوال أو السعر)");
        }

    }


    private void uploadPMat(Student student, PhysicalMaterial material)
    {
        progressDialog.show();

        FilesStorage.uploadPMatFile(student, User.course, material, imageUri, new QueryRequestFlag<QueryPostStatus>()
        {
            @Override
            public void onQuerySuccess(QueryPostStatus resultObject)
            {
                if(resultObject != null)
                {
                    if(resultObject.getAffectedRows() > 0)
                        refreshPMats();
                    else
                        NafeaUtil.showToastMsg(getContext(), "فشل نشر العرض");
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                progressDialog.dismiss();

                NafeaUtil.showToastMsg(getContext(),"فشل نشر العرض");
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
            }
        });
    }

    private void refreshPMats()
    {
        PhysicalMaterial.retrieveAllPMatsInCourse(User.course, new QueryRequestFlag<ArrayList<PhysicalMaterial>>()
        {
            @Override
            public void onQuerySuccess(ArrayList<PhysicalMaterial> resultObject)
            {
                progressDialog.dismiss();

                if(resultObject != null)
                    User.course.updatePMats(resultObject);


                NafeaUtil.showToastMsg(getContext(), "تم نشر العرض بنجاح");

                CoursePageActivity activity = ((CoursePageActivity) getActivity());
                if(!activity.isPageStackEmpty())
                    activity.onBackClicked();
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                progressDialog.dismiss();
                NafeaUtil.showToastMsg(getContext(), "تم نشر العرض بنجاح");
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());

                CoursePageActivity activity = ((CoursePageActivity) getActivity());
                if(!activity.isPageStackEmpty())
                    activity.onBackClicked();
            }
        });
    }


    private boolean isFieldsValid(String name, String price, String phone, String city)
    {
        String msg = "";
        boolean fieldError = false;

        if(name.isEmpty())
        {
            msg = "خانة اسم المحتوى فارغة!";
            fieldError = true;
        }
        else if(price.isEmpty())
        {
            msg = "خانة السعر فارغة!";
            fieldError = true;
        }
        else if(phone.isEmpty())
        {
            msg = "خانة رقم الجوال فارغة!";
            fieldError = true;
        }
        else if(city.isEmpty())
        {
            msg = "خانة المدينة فارغة!";
            fieldError = true;
        }


        if(fieldError)
        {
            NafeaUtil.showToastMsg(getContext(), msg);
            return false;
        }

        return true;
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
            matImageView.setImageURI(imageUri);

        }
    }

}