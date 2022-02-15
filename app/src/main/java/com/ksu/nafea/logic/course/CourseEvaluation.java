package com.ksu.nafea.logic.course;

import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequest;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.Entity;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.account.Student;

import java.util.ArrayList;

public class CourseEvaluation extends Entity<CourseEvaluation>
{
    public static final String TAG = "CourseEvaluation";
    private Double contentSize, assignmentsDifficulty, examsDifficulty;
    public static final int EVALUATION_MAX = 10;

    public CourseEvaluation()
    {
        contentSize = 0.0;
        assignmentsDifficulty = 0.0;
        examsDifficulty = 0.0;
    }
    public CourseEvaluation(Double contentSize, Double assignmentsDifficulty, Double examsDifficulty)
    {
        this.contentSize = contentSize;
        this.assignmentsDifficulty = assignmentsDifficulty;
        this.examsDifficulty = examsDifficulty;
    }


    public Integer getOverallCourseDifficulty()
    {
        if(contentSize == null && assignmentsDifficulty == null && examsDifficulty == null)
            return null;

        if(contentSize == null)
            contentSize = 0.0;
        if(assignmentsDifficulty == null)
            assignmentsDifficulty = 0.0;
        if(examsDifficulty == null)
            examsDifficulty = 0.0;

        Double overallEvaluation = (contentSize + assignmentsDifficulty + examsDifficulty) / 3;

        double percentage = (overallEvaluation / EVALUATION_MAX) * 100;
        return (int) percentage;
    }


    @Override
    public String toString()
    {
        return "CourseEvaluation{" +
                "contentSize=" + contentSize +
                ", assignmentsDifficulty=" + assignmentsDifficulty +
                ", examsDifficulty=" + examsDifficulty +
                '}';
    }


    //-----------------------------------------------[Queries]-----------------------------------------------
    public static void insert(Student student, Course course, CourseEvaluation evaluation, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        try
        {
            //Output: Return type
            QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
            request.setRequestFlag(requestFlag);


            //Input: Queries
            Attribute studentEmail = new Attribute("s_email", ESQLDataType.STRING, student.getEmail());
            Attribute courseID = new Attribute("crs_id", ESQLDataType.INT, course.getId());

            EntityObject evaluationEntity = evaluation.toEntity();
            evaluationEntity.addAttribute(0, studentEmail);
            evaluationEntity.addAttribute(courseID);

            request.addQuery(evaluationEntity.createInsertQuery());

            getPool().executeUpdateQuery(request);
        }
        catch (Exception e)
        {
            String msg = "Failed to insert evaluation to " + course.getName() + " course: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static void update(Student student, Course course, CourseEvaluation evaluation, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);


        //Input: Queries
        String studentEmail = Attribute.getSQLValue(student.getEmail(), ESQLDataType.STRING);

        String newValues = "content_size = " + evaluation.contentSize
                + ", assignments_difficulty = " + evaluation.assignmentsDifficulty
                + ", exams_difficulty = " + evaluation.examsDifficulty;
        String condition = " WHERE s_email = " + studentEmail + " AND crs_id = " + course.getId();
        String updateQuery = "UPDATE evaluate_course SET " + newValues + condition;

        request.addQuery(updateQuery);

        getPool().executeUpdateQuery(request);
    }



    public static void retrieveStudentEvaluation(Student student, Course course, final QueryRequestFlag<CourseEvaluation> requestFlag)
    {
        String studentEmail = Attribute.getSQLValue(student.getEmail(), ESQLDataType.STRING);
        String condition = "s_email = " + studentEmail + " AND crs_id = " + course.getId();

        try
        {
            getPool().retrieve(CourseEvaluation.class, new QueryRequestFlag<ArrayList<CourseEvaluation>>()
            {
                @Override
                public void onQuerySuccess(ArrayList<CourseEvaluation> resultObject)
                {
                    if(resultObject != null)
                    {
                        requestFlag.onQuerySuccess(resultObject.get(0));
                        return;
                    }

                    requestFlag.onQuerySuccess(null);
                }

                @Override
                public void onQueryFailure(FailureResponse failure)
                {
                    Entity.sendFailureResponse(requestFlag, TAG, failure.getMsg());
                }
            }, "*", condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve \"" + student.getFullName() + "\" evaluation for \"" + course.getName() + "\" course: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    //-----------------------------------------------[Entity Override Methods]-----------------------------------------------

    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("evaluate_course");

        entityObject.addAttribute("content_size", ESQLDataType.DOUBLE, contentSize);
        entityObject.addAttribute("assignments_difficulty", ESQLDataType.DOUBLE, assignmentsDifficulty);
        entityObject.addAttribute("exams_difficulty", ESQLDataType.DOUBLE, examsDifficulty);

        return entityObject;
    }

    @Override
    public CourseEvaluation toObject(EntityObject entityObject) throws ClassCastException
    {
        CourseEvaluation evaluation = new CourseEvaluation();

        evaluation.contentSize = entityObject.getAttributeValue("content_size", ESQLDataType.DOUBLE, Double.class);
        evaluation.assignmentsDifficulty = entityObject.getAttributeValue("assignments_difficulty", ESQLDataType.DOUBLE, Double.class);
        evaluation.examsDifficulty = entityObject.getAttributeValue("exams_difficulty", ESQLDataType.DOUBLE, Double.class);

        return evaluation;
    }

    @Override
    public Class<CourseEvaluation> getEntityClass()
    {
        return CourseEvaluation.class;
    }


    //--------------------------------------------------[Getters & Setters]--------------------------------------------------

    public Double getContentSize()
    {
        return contentSize;
    }

    public Double getAssignmentsDifficulty()
    {
        return assignmentsDifficulty;
    }

    public Double getExamsDifficulty()
    {
        return examsDifficulty;
    }

    public int getContentSizePercentage()
    {
        if(contentSize == null)
            return 0;

        double percentage = (contentSize / EVALUATION_MAX) * 100;
        return (int) percentage;
    }

    public int getAssignmentsDifficultyPercentage()
    {
        if(assignmentsDifficulty == null)
            return 0;

        double percentage = (assignmentsDifficulty / EVALUATION_MAX) * 100;
        return (int) percentage;
    }

    public int getExamsDifficultyPercentage()
    {
        if(examsDifficulty == null)
            return 0;

        double percentage = (examsDifficulty / EVALUATION_MAX) * 100;
        return (int) percentage;
    }


}
