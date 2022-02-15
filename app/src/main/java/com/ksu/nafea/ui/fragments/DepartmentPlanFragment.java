package com.ksu.nafea.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksu.nafea.R;
import com.ksu.nafea.logic.FilesStorage;
import com.ksu.nafea.logic.Major;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.material.ElectronicMaterial;
import com.ksu.nafea.utilities.NafeaUtil;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DepartmentPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DepartmentPlanFragment extends Fragment
{
    private ImageView planImage;
    private TextView download;
    private static final String DOWNLOAD_FAILED_MSG = "لا توجد خطة للتحميل";

    public DepartmentPlanFragment()
    {
        // Required empty public constructor
    }


    public static DepartmentPlanFragment newInstance(String param1, String param2) {
        DepartmentPlanFragment fragment = new DepartmentPlanFragment();
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
        View main = inflater.inflate(R.layout.fragment_department_plan, container, false);

        Major major = User.major;
        String title = major != null ? "خطة " + major.getName() : "خطة التخصص";
        NafeaUtil.setBarTitle(getActivity(), title);

        viewsInit(main);
        loadPlan();

        download.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onDownloadClicked();
            }
        });

        return main;
    }



    private void viewsInit(View main)
    {
        planImage = (ImageView) main.findViewById(R.id.depPlan_img);
        download = (TextView) main.findViewById(R.id.depPlan_txtb_download);
    }

    private void loadPlan()
    {
        String url = User.major.getPlanUrl();
        if(url != null)
        {
            Picasso.with(getContext()).load(url).into(planImage);

            if(planImage.getDrawable() == null)
                planImage.setImageResource(R.drawable.no_picture);
        }
    }


    private void onDownloadClicked()
    {
        String title = "خطة " + User.major.getName();
        FilesStorage.downloadFile(getActivity(), User.major.getPlanUrl(), title, DOWNLOAD_FAILED_MSG);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(FilesStorage.isPermissionProved(requestCode, grantResults))
        {
            String title = "خطة " + User.major.getName();
            FilesStorage.downloadFile(getActivity(), User.major.getPlanUrl(), title, DOWNLOAD_FAILED_MSG);
        }
        else
            NafeaUtil.showToastMsg(getContext(), "Permission denied...!");
    }

}