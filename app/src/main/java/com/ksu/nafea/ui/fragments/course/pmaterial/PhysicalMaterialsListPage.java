package com.ksu.nafea.ui.fragments.course.pmaterial;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.material.PhysicalMaterial;
import com.ksu.nafea.ui.fragments.course.ContentListFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhysicalMaterialsListPage extends ContentListFragment<PhysicalMaterial>
{
    private static final int MAX_URL_LENGTH = 45;
    private static final String PMAT_NAME_PREFIX = "";
    private static final String PMAT_CITY_PREFIX = "";
    private static final String PMAT_PRICE_PREFIX = "";
    private static final String PMAT_PRICE_POSTFIX = " ريال";

    @Override
    protected void onContentListCreated(View main)
    {
        addButtonView(getString(R.string.PMaterial_addContent)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSellContentClicked();
            }
        });
    }

    @Override
    protected void updateData()
    {
        ArrayList<PhysicalMaterial> physMats = User.course.getPMats();
        this.data = physMats;

        super.updateData();
    }



    @Override
    protected int getItemViewLayout()
    {
        return R.layout.item_view_physical_material;
    }

    @Override
    protected void onItemViewBind(View itemView, final int position)
    {
        ConstraintLayout mainLayout = (ConstraintLayout) itemView.findViewById(R.id.physMat_mainLayout);
        ImageView matImg = (ImageView) itemView.findViewById(R.id.physMat_img_matImg);
        TextView matName = (TextView) itemView.findViewById(R.id.physMat_txt_matName);
        TextView matCity = (TextView) itemView.findViewById(R.id.physMat_txt_matCity);
        TextView matPrice = (TextView) itemView.findViewById(R.id.physMat_txt_matPrice);
        TextView reportButton = (TextView) itemView.findViewById(R.id.physMat_txtb_report);

        final PhysicalMaterial mat = getData().get(position);

        String name = mat.getName();
        if(name != null)
        {
            if(name.length() > MAX_URL_LENGTH)
                name = name.substring(0, MAX_URL_LENGTH - 1) + "...";
        }

        ImageView trash = (ImageView) itemView.findViewById(R.id.physMat_img_trash);
        assignDeleteProcess(trash, mat.getOwner(), mat, mat.getName());

        if(mat.getImageUrl() != null)
        {
            //matImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //Picasso.with(getContext()).load(mat.getImageUrl()).fit().centerInside().into(matImg);//.resize(128, 128).into(matImg);
            Picasso.with(getContext()).load(mat.getImageUrl()).into(matImg);

            if(matImg.getDrawable() == null)
                matImg.setImageResource(R.drawable.no_picture);
        }

        matName.setText(PMAT_NAME_PREFIX + name);
        matCity.setText(PMAT_CITY_PREFIX +  mat.getCity());
        matPrice.setText(PMAT_PRICE_PREFIX + String.valueOf(mat.getPrice()) + PMAT_PRICE_POSTFIX);


        reportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onReportClicked(position);
            }
        });

        mainLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onPMatClicked(position);
            }
        });
    }


    @Override
    protected void onDeletionPerform(PhysicalMaterial targetData)
    {
        User.course.getPMats().remove(targetData);
    }

    @Override
    protected void onConfirmDeleteClicked(PhysicalMaterial targetData, QueryRequestFlag<QueryPostStatus> onDeleteRequest)
    {
        PhysicalMaterial.delete(User.course, targetData, onDeleteRequest);
    }


    private void onPMatClicked(int position)
    {
        User.material = getData().get(position);
        openPage(R.id.action_physMats_to_physicalMaterialPage, R.id.action_physicalMaterialPage_to_physMats, false);
    }


    private  void onReportClicked(int position)
    {
        Student student = (Student) User.userAccount;
        if(User.userAccount != null)
        {
            if(student.isAdmin())
            {
                showToastMsg(getString(R.string.ematReport_notAllowdReport));
                return;
            }
        }

        User.material = getData().get(position);
        if(User.userAccount != null)
            openPage(R.id.action_physMats_to_physReportPage, R.id.action_physReportPage_to_physMats, false);
        else
            showToastMsg(getString(R.string.toastMsg_loginFirst));
    }

    private void onSellContentClicked()
    {
        if(User.userAccount != null)
            openPage(R.id.action_physMats_to_uploadPhysMatPage, R.id.action_uploadPhysMatPage_to_physMats, false);
        else
            showToastMsg(getString(R.string.toastMsg_loginFirst));
    }


}