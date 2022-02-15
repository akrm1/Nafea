package com.ksu.nafea.ui.fragments.course.ematerial;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.FilesStorage;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.material.ElectronicMaterial;
import com.ksu.nafea.ui.activities.CoursePageActivity;
import com.ksu.nafea.ui.nafea_views.NSpinner;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadEMaterialPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadEMaterialPage extends Fragment
{

    private static final String TAG = "UploadEMaterial";
    private View main;
    private TextView typeLabel, matPathLabel, videoLinkLabel;
    private EditText matNameField, videoLinkField;
    private NSpinner typeDropdown;
    private Button uploadButton, chooseButton;
    private ProgressDialog progressDialog;

    private Uri file = null;
    private String selectedMatType = null;


    private String documentType = null;
    private String videoType = null;
    private String documentOption = null;
    private String videoOption = null;

    private Boolean cbc ;
    private CheckBox cb;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private String mParam2;

    public UploadEMaterialPage()
    {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadEMaterialPage.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadEMaterialPage newInstance(String param1, String param2) {
        UploadEMaterialPage fragment = new UploadEMaterialPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        main = inflater.inflate(R.layout.fragment_upload_e_material_page, container, false);

        documentType = getString(R.string.ematType_DocumentType);
        videoType = getString(R.string.ematType_VideoType);
        documentOption = getString(R.string.ematType_DocumentOption);
        videoOption = getString(R.string.ematType_VideoOption);


        viewsInit();
        toggleView(documentOption);
        initButtonsListeners();
        initDropdownListeners();
        initCheckBoxListeners();

        return main;
    }


    private void viewsInit()
    {
        typeDropdown = (NSpinner) main.findViewById(R.id.updMat_dropdown_matType);

        typeLabel = (TextView) main.findViewById(R.id.updMat_txt_matName);
        matPathLabel = (TextView) main.findViewById(R.id.updMat_txt_matPath);
        videoLinkLabel = (TextView) main.findViewById(R.id.updMat_txt_videoLink);

        videoLinkField = (EditText) main.findViewById(R.id.updMat_ed_videoLink);
        matNameField = (EditText) main.findViewById(R.id.updMat_ed_matName);

        chooseButton = (Button) main.findViewById(R.id.updMat_b_chooseMat);
        uploadButton = (Button) main.findViewById(R.id.updMat_b_upload);

        typeDropdown.addOption(documentOption);
        typeDropdown.addOption(videoOption);

        cb=(CheckBox)main.findViewById(R.id.checkBox);
        cbc=false;


        progressDialog = new ProgressDialog(getContext());
    }

    private void initButtonsListeners()
    {
        chooseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onChooseFileClicked();

            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onUploadFileClicked();
            }
        });
    }

    private void initCheckBoxListeners()
    {
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb.isChecked())
                    cbc=true;
            }
        });
    }

    private void initDropdownListeners()
    {
        typeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedOption = typeDropdown.getSelectedOption();
                toggleView(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

    }

    private void toggleView(String matType)
    {
        if(matType.equalsIgnoreCase(documentOption))
        {
            typeLabel.setText("اسم للملف:");
            videoLinkField.setVisibility(View.INVISIBLE);
            chooseButton.setVisibility(View.VISIBLE);
            videoLinkLabel.setVisibility(View.INVISIBLE);
            matPathLabel.setVisibility(View.VISIBLE);
            cb.setVisibility(View.VISIBLE);
            selectedMatType = documentType;
        }
        else
        {
            typeLabel.setText("اسم للرابط:");
            videoLinkField.setVisibility(View.VISIBLE);
            videoLinkLabel.setVisibility(View.VISIBLE);
            chooseButton.setVisibility(View.INVISIBLE);
            matPathLabel.setVisibility(View.INVISIBLE);
            cb.setVisibility(View.INVISIBLE);
            selectedMatType = videoType;
        }
    }

    private void showToastMsg(String msg)
    {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }





    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData)
    {
        if(requestCode == 10)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                file = resultData.getData();

                String path = resultData.getData().getPath();
                matPathLabel.setText("File Path:" + path);
            }
        }
    }


    public void onChooseFileClicked()
    {
        Intent selectFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectFileIntent.setType("*/*");
        startActivityForResult(selectFileIntent, 10);
    }


    private boolean isValidInput()
    {
        String matName = matNameField.getText().toString();
        if(matName.isEmpty())
        {
            showToastMsg("خانة الاسم خالية");
            return false;
        }


        if(selectedMatType.equalsIgnoreCase(documentType))
        {
            if(file == null)
            {
                showToastMsg("اختار الملف المراد رفعه");
                return false;
            }
            if(cb.isChecked()==false) {
                showToastMsg( "  يرجى الموافقة على الشرط ");
                return false;
        }
        }
        else if(selectedMatType.equalsIgnoreCase(videoType))
        {
            String videoLink = videoLinkField.getText().toString();
            if(videoLink.isEmpty())
            {
                showToastMsg("ضع رابط الفيديو");
                return false;
            }
        }

        return true;
    }

    private void onUploadFileClicked()
    {
        if(!isValidInput())
            return;

        Student student = (Student) User.userAccount;
        String materialName = matNameField.getText().toString();


        progressDialog.show();

        if(selectedMatType.equalsIgnoreCase(documentType))
            FilesStorage.uploadEMatFile(student, User.course, materialName, selectedMatType, file, onUploadComplete());
        else if(selectedMatType.equalsIgnoreCase(videoType))
        {
            String url = videoLinkField.getText().toString();
            ElectronicMaterial material = new ElectronicMaterial(0, materialName, selectedMatType, url, null);
            ElectronicMaterial.insert(student, User.course, material, onUploadComplete());
        }

    }


    private void refreshEMats()
    {
        ElectronicMaterial.retrieveAllEMatsInCourse(User.course, new QueryRequestFlag<ArrayList<ElectronicMaterial>>()
        {
            @Override
            public void onQuerySuccess(ArrayList<ElectronicMaterial> resultObject)
            {
                progressDialog.dismiss();

                if(resultObject != null)
                    User.course.updateEMats(resultObject);

                showToastMsg("تم الرفع بنجاح");

                CoursePageActivity activity = ((CoursePageActivity) getActivity());
                if(!activity.isPageStackEmpty())
                    activity.onBackClicked();

            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                progressDialog.dismiss();
                showToastMsg("تم الرفع بنجاح");
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());

                CoursePageActivity activity = ((CoursePageActivity) getActivity());
                if(!activity.isPageStackEmpty())
                    activity.onBackClicked();
            }
        });
    }


    private QueryRequestFlag<QueryPostStatus> onUploadComplete()
    {
        return new QueryRequestFlag<QueryPostStatus>()
        {
            @Override
            public void onQuerySuccess(QueryPostStatus resultObject)
            {
                if(resultObject != null)
                {
                    if(resultObject.getAffectedRows() > 0)
                        refreshEMats();
                    else
                        showToastMsg("فشل الرفع");
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                progressDialog.dismiss();

                showToastMsg("فشل الرفع");
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
            }
        };
    }



}