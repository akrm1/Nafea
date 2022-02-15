package com.ksu.nafea.logic.material;

import com.ksu.nafea.data.request.QueryRequest;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.Entity;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.course.Comment;
import com.ksu.nafea.logic.course.Course;

import java.util.ArrayList;

public class PMatComment extends Entity<PMatComment>
{
    public static final String TAG = "PMatComment";

    private String owner;
    private String firstName, lastName;
    private String comment;
    private String time;


    public PMatComment()
    {
        owner = "";
        firstName = "";
        lastName = "";
        comment = "";
        time = "";
    }

    public PMatComment(String firstName, String lastName, String comment, String time)
    {
        owner = "";
        this.firstName = firstName;
        this.lastName = lastName;
        this.comment = comment;
        this.time = time;
    }


    @Override
    public String toString()
    {
        return "Comment{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", comment='" + comment + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    //-----------------------------------------------[Queries]-----------------------------------------------

    public static void insertComment(Student student, Course course, PhysicalMaterial physicalMaterial, PMatComment comment, final QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);


        //Input: Queries
        String studentEmail = Attribute.getSQLValue(student.getEmail(), ESQLDataType.STRING);
        String commentString = Attribute.getSQLValue(comment.getComment(), ESQLDataType.STRING);
        String time = Attribute.getSQLValue(comment.getTime(), ESQLDataType.STRING);

        String insertQuery = "INSERT INTO comment_on_pmaterial VALUES(" + studentEmail + ", "
                + commentString + ", "
                + physicalMaterial.getId() + ", "
                + course.getId() + ", "
                + time + ")";

        request.addQuery(insertQuery);

        getPool().executeUpdateQuery(request);
    }

    public static void delete(Course course, PhysicalMaterial physicalMaterial, PMatComment comment, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        try
        {
            //Output: Return type
            QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
            request.setRequestFlag(requestFlag);

            //Input: Queries
            String condition = "pmat_id = " + physicalMaterial.getId() + " AND crs_id = " + course.getId();

            String deleteQuery = comment.toEntity().createDeleteQuery(condition);
            request.addQuery(deleteQuery);


            getPool().executeUpdateQuery(request);
        }
        catch (Exception e)
        {
            String msg = "Failed to delete comment on " + physicalMaterial.getName() + " physical material: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static void retrieveAllComments(Course course, PhysicalMaterial physicalMaterial, final QueryRequestFlag<ArrayList<PMatComment>> requestFlag)
    {
        String selectClause = "comment_on_pmaterial.s_email, s.first_name, s.last_name, pmat_comment ,pcomment_time";
        String joinSection = "RIGHT JOIN student as s ON s.s_email = comment_on_pmaterial.s_email";
        String condition = "comment_on_pmaterial.pmat_id = " + physicalMaterial.getId() + " AND comment_on_pmaterial.crs_id = " + course.getId();
        String orderBy = "pcomment_time desc";

        try
        {
            getPool().retrieve(PMatComment.class, requestFlag, selectClause, joinSection, condition, "", orderBy);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve comments in \"" + physicalMaterial.getName() + "\" physical material: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }


    //-----------------------------------------------[Entity Override Methods]-----------------------------------------------

    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("comment_on_pmaterial");

        entityObject.addAttribute("first_name", ESQLDataType.STRING, firstName);
        entityObject.addAttribute("last_name", ESQLDataType.STRING, lastName);
        entityObject.addAttribute("pmat_comment", ESQLDataType.STRING, comment);
        entityObject.addAttribute("pcomment_time", ESQLDataType.STRING, time);

        return entityObject;
    }

    @Override
    public PMatComment toObject(EntityObject entityObject) throws ClassCastException
    {
        PMatComment cmt = new PMatComment();

        cmt.owner = entityObject.getAttributeValue("s_email", ESQLDataType.STRING, String.class);
        cmt.firstName = entityObject.getAttributeValue("first_name", ESQLDataType.STRING, String.class);
        cmt.lastName = entityObject.getAttributeValue("last_name", ESQLDataType.STRING, String.class);
        cmt.comment = entityObject.getAttributeValue("pmat_comment", ESQLDataType.STRING, String.class);
        cmt.time = entityObject.getAttributeValue("pcomment_time", ESQLDataType.STRING, String.class)
                .replace("T", " ")
                .replace("Z", " ")
                .replace(".000", "");

        return cmt;
    }

    @Override
    public Class<PMatComment> getEntityClass()
    {
        return PMatComment.class;
    }


    //--------------------------------------------------[Getters & Setters]--------------------------------------------------

    public String getOwner()
    {
        return owner;
    }

    public String getFullName()
    {
        return firstName + " " + lastName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getComment()
    {
        return comment;
    }

    public String getTime()
    {
        return time;
    }


}
