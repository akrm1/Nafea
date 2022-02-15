package com.ksu.nafea.ui.fragments.course.ematerial.document;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.ksu.nafea.logic.material.Material;
import com.ksu.nafea.ui.fragments.course.ContentListFragment;
import com.ksu.nafea.ui.nafea_views.dialogs.PopupConfirmDialog;
import com.ksu.nafea.ui.nafea_views.dialogs.PopupDetailsDialog;

import java.util.ArrayList;

public class DocumentsListPage extends ContentListFragment<ElectronicMaterial>
{
    private static final String DOWNLOAD_FAILED_MSG = "هناك خلل في رابط تحميل الملف";


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
        String type = getString(R.string.ematType_DocumentType);
        ArrayList<ElectronicMaterial> documents = ElectronicMaterial.getEMaterialsByType(User.course.getEMats(), type);
        this.data = documents;

        super.updateData();
    }




    @Override
    protected int getItemViewLayout()
    {
        return R.layout.item_view_document;
    }

    @Override
    protected void onItemViewBind(View itemView, final int position)
    {
        ConstraintLayout mainLayout = (ConstraintLayout) itemView.findViewById(R.id.doc_mainLayout);
        LinearLayout likeLayout = (LinearLayout) itemView.findViewById(R.id.doc_layout_like);
        LinearLayout dislikeLayout = (LinearLayout) itemView.findViewById(R.id.doc_layout_dislike);
        TextView documentName = (TextView) itemView.findViewById(R.id.doc_txt_docName);
        final TextView likeText = (TextView) itemView.findViewById(R.id.doc_txt_like);
        final TextView dislikeText = (TextView) itemView.findViewById(R.id.doc_txt_dislike);
        TextView documentExt = (TextView) itemView.findViewById(R.id.doc_txt_ext);
        TextView reportButton = (TextView) itemView.findViewById(R.id.doc_txtb_report);

        final ElectronicMaterial document = getData().get(position);

        documentName.setText(document.getName());
        documentExt.setText(document.getExtension());
        updateEvaluationsOnScreen(likeText, dislikeText, document);


        ImageView trash = (ImageView) itemView.findViewById(R.id.doc_img_trash);
        assignDeleteProcess(trash, document.getOwner(), document, document.getName());


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
                onEvaluationClicked(document, true);
                updateEvaluationsOnScreen(likeText, dislikeText, document);
            }
        });
        dislikeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onEvaluationClicked(document, false);
                updateEvaluationsOnScreen(likeText, dislikeText, document);
            }
        });

        mainLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onDocumentClicked(position);
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
            openPage(R.id.action_documents_to_EMatReportPage, R.id.action_EMatReportPage_to_documents, false);
        else
            showToastMsg(getString(R.string.toastMsg_loginFirst));
    }

    private void onAddContentClicked()
    {
        if(User.userAccount != null)
            openPage(R.id.action_documents_to_uploadEMaterialPage, R.id.action_uploadEMaterialPage_to_documents, false);
        else
            showToastMsg(getString(R.string.toastMsg_loginFirst));
    }

    private void onDocumentClicked(int position)
    {
        User.material = getData().get(position);
        FilesStorage.downloadFile(getActivity(), getData().get(position), DOWNLOAD_FAILED_MSG);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(FilesStorage.isPermissionProved(requestCode, grantResults))
        {
            FilesStorage.downloadFile(getActivity(), (ElectronicMaterial)User.material, DOWNLOAD_FAILED_MSG);
        }
        else
            showToastMsg("Permission denied...!");
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