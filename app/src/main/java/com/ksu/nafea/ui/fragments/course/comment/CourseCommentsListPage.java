package com.ksu.nafea.ui.fragments.course.comment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ksu.nafea.R;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.User;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.course.Comment;
import com.ksu.nafea.ui.fragments.course.ContentListFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CourseCommentsListPage extends ContentListFragment<Comment>
{
    private EditText commentField;
    private Button addCommentButton;
    private int COMMENT_MAX_LENGTH = 255;

    @Override
    protected void onContentListCreated(View main)
    {
        commentField = addField("أضف تعليقك");

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(COMMENT_MAX_LENGTH);
        commentField.setFilters(FilterArray);

        addCommentButton = addButtonView("أضف التعليق");
        addCommentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onAddCommentClicked();
            }
        });

        addCommentButton.setVisibility(View.GONE);

        commentField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String fieldText = commentField.getText().toString();
                if(!fieldText.isEmpty())
                {
                    addCommentButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    addCommentButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void updateData()
    {
        ArrayList<Comment> comments = User.course.getComments();
        this.data = comments;

        super.updateData();
    }



    @Override
    protected int getItemViewLayout()
    {
        return R.layout.item_view_course_comment;
    }

    @Override
    protected void onItemViewBind(View itemView, final int position)
    {
        ConstraintLayout mainLayout = (ConstraintLayout) itemView.findViewById(R.id.crsComment_mainLayout);
        TextView nameText = (TextView) itemView.findViewById(R.id.crsComment_txt_name);
        TextView commentText = (TextView) itemView.findViewById(R.id.crsComment_txt_comment);
        TextView dateText = (TextView) itemView.findViewById(R.id.crsComment_txt_date);

        final Comment comment = getData().get(position);

        ImageView trash = (ImageView) itemView.findViewById(R.id.crsComment_img_trash);
        String title = "";
        String msg = "هل أنت متأكد تريد أن تحذف هذا التعليق؟";
        assignDeleteProcess(trash, comment.getOwner(), comment, title, msg);

        nameText.setText(comment.getFullName());
        commentText.setText(comment.getComment());
        dateText.setText(comment.getTime());


        mainLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onCommentClicked(position);
            }
        });
    }


    @Override
    protected void onDeletionPerform(Comment targetData)
    {
        User.course.getComments().remove(targetData);
    }

    @Override
    protected void onConfirmDeleteClicked(Comment targetData, QueryRequestFlag<QueryPostStatus> onDeleteRequest)
    {
        Comment.delete(User.course, targetData, onDeleteRequest);
    }

    private void onCommentClicked(int position)
    {

    }

    private void onAddCommentClicked()
    {
        if(User.userAccount == null)
        {
            showToastMsg(getString(R.string.toastMsg_loginFirst));
            return;
        }

        Student student = (Student) User.userAccount;
        if(student.isAdmin())
        {
            showToastMsg(getString(R.string.toastMsg_notAllowedComment));
            return;
        }


        final String commentString = commentField.getText().toString();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(date);

        final Comment newComment = new Comment(student.getFirstName(), student.getLastName(), commentString, time);

        progressDialog.show();
        Comment.insertComment(student, User.course, newComment, new QueryRequestFlag<QueryPostStatus>()
        {
            @Override
            public void onQuerySuccess(QueryPostStatus resultObject)
            {
                progressDialog.dismiss();

                if(resultObject != null)
                {
                    User.course.getComments().add(0, newComment);
                    updateData();
                    updateRecyclerView();

                    commentField.setText("");
                    addCommentButton.setVisibility(View.GONE);
                    showToastMsg("تم إضافة تعليقك");
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                progressDialog.dismiss();
                showToastMsg("لا يمكنك إضافة أكثر من تعليق");
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
            }
        });
    }

}