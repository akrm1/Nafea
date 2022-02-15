package com.ksu.nafea.ui.fragments.course.ematerial.video;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.FilesStorage;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.material.EMaterialEvaluation;
import com.ksu.nafea.logic.material.ElectronicMaterial;
import com.ksu.nafea.ui.fragments.course.ContentListFragment;

import java.util.ArrayList;

public class VideosListPage extends ContentListFragment<ElectronicMaterial>
{
    private static final int MAX_URL_LENGTH = 40;

    @Override
    protected void onContentListCreated(View main)
    {
        addButtonView(getString(R.string.EMaterial_addContent)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onAddContentClicked();
            }
        });
    }

    @Override
    protected void updateData()
    {
        String type = getString(R.string.ematType_VideoType);
        ArrayList<ElectronicMaterial> documents = ElectronicMaterial.getEMaterialsByType(User.course.getEMats(), type);
        this.data = documents;

        super.updateData();
    }



    @Override
    protected int getItemViewLayout()
    {
        return R.layout.item_view_video;
    }

    @Override
    protected void onItemViewBind(View itemView, final int position)
    {
        ConstraintLayout mainLayout = (ConstraintLayout) itemView.findViewById(R.id.video_mainLayout);
        LinearLayout likeLayout = (LinearLayout) itemView.findViewById(R.id.video_layout_like);
        LinearLayout dislikeLayout = (LinearLayout) itemView.findViewById(R.id.video_layout_dislike);
        TextView videoName = (TextView) itemView.findViewById(R.id.video_txt_videoName);
        final TextView likeText = (TextView) itemView.findViewById(R.id.video_txt_like);
        final TextView dislikeText = (TextView) itemView.findViewById(R.id.video_txt_dislike);
        TextView videoUrl = (TextView) itemView.findViewById(R.id.video_txt_url);
        TextView reportButton = (TextView) itemView.findViewById(R.id.video_txtb_report);

        final ElectronicMaterial video = getData().get(position);

        ImageView trash = (ImageView) itemView.findViewById(R.id.video_img_trash);
        assignDeleteProcess(trash, video.getOwner(), video, video.getName());

        String url = video.getUrl();
        if(url != null)
        {
            if(url.length() > MAX_URL_LENGTH)
                url = url.substring(0, MAX_URL_LENGTH - 1) + "...";
        }

        videoName.setText(video.getName());
        videoUrl.setText(url);
        updateEvaluationsOnScreen(likeText, dislikeText, video);


        reportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onReportClicked(position);
            }
        });

        likeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onEvaluationClicked(video, true);
                updateEvaluationsOnScreen(likeText, dislikeText, video);
            }
        });
        dislikeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onEvaluationClicked(video, false);
                updateEvaluationsOnScreen(likeText, dislikeText, video);
            }
        });

        mainLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onVideoClicked(position);
            }
        });
    }

    private void onReportClicked(int position)
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
            openPage(R.id.action_videos_to_EMatReportPage, R.id.action_EMatReportPage_to_videos, false);
        else
            showToastMsg(getString(R.string.toastMsg_loginFirst));
    }

    private void onVideoClicked(int position)
    {
        User.material = getData().get(position);
        try
        {
            FilesStorage.watchVideo(getActivity(), getData().get(position).getUrl());
        }
        catch (Exception e)
        {
            showToastMsg("رابط المشاهدة لا يعمل");
        }
    }

    private void onAddContentClicked()
    {
        if(User.userAccount != null)
            openPage(R.id.action_videos_to_uploadEMaterialPage, R.id.action_uploadEMaterialPage_to_videos, false);
        else
            showToastMsg(getString(R.string.toastMsg_loginFirst));
    }

    @Override
    protected void onDeletionPerform(ElectronicMaterial targetData)
    {
        User.course.getEMats().remove(targetData);
    }

    @Override
    protected void onConfirmDeleteClicked(ElectronicMaterial targetData, QueryRequestFlag<QueryPostStatus> onDeleteRequest)
    {
        ElectronicMaterial.delete(User.course, targetData, onDeleteRequest);
    }


    //--------------------------------------------------------[Evaluation methods]--------------------------------------------------------

    private void updateEvaluationsOnScreen(TextView likeText, TextView dislikeText, ElectronicMaterial eMaterial)
    {
        likeText.setText(String.valueOf(eMaterial.getLikes()));
        dislikeText.setText(String.valueOf(eMaterial.getDislikes()));
    }


    private void onEvaluationClicked(ElectronicMaterial eMaterial, final boolean like)
    {
        if(User.userAccount == null)
        {
            showToastMsg("يجب أن يكون لديك حساب لتقيم");
            return;
        }

        Student student = (Student) User.userAccount;
        if(student.isAdmin())
        {
            showToastMsg(getString(R.string.emat_notAllowedEvaluate));
            return;
        }

        String email = User.userAccount.getEmail();
        boolean isSuccess = like ? eMaterial.addLike(email) : eMaterial.addDislike(email);

        if(isSuccess)
            updateEvaluation(eMaterial, like);
    }

    private void updateEvaluation(final ElectronicMaterial eMaterial, final boolean like)
    {
        final Student student = (Student) User.userAccount;

        EMaterialEvaluation.insertEvaluation(student, User.course, eMaterial, like, new QueryRequestFlag<QueryPostStatus>()
        {
            @Override
            public void onQuerySuccess(QueryPostStatus resultObject)
            {
                if(resultObject != null)
                {
                    if(resultObject.getAffectedRows() > 0)
                        return;
                }

                EMaterialEvaluation.updateEvaluation(student, User.course, eMaterial, like, null);
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                EMaterialEvaluation.updateEvaluation(student, User.course, eMaterial, like, null);
            }
        });
    }

}