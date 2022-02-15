package com.ksu.nafea.logic.material;

import android.util.Log;

import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequest;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.Entity;
import com.ksu.nafea.logic.GeneralPool;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.course.Course;

import java.util.ArrayList;

public class EMaterialEvaluation extends Entity<EMaterialEvaluation>
{
    public static final String TAG = "EMaterialEvaluation";

    private String ownerEmail;
    private Integer evaluation;
    private Integer eMatID;

    public EMaterialEvaluation()
    {
        eMatID = -1;
        ownerEmail = "";
        evaluation = 0;
    }



    //-----------------------------------------------[Queries]-----------------------------------------------

    public static void insertEvaluation(Student student, final Course course, ElectronicMaterial eMaterial, boolean like, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);


        //Input: Queries
        int evaluation = like ? 1 : -1;
        String studentEmail = Attribute.getSQLValue(student.getEmail(), ESQLDataType.STRING);
        String insertQuery = "INSERT INTO evaluate_ematerial VALUES(" + studentEmail + ", "
                                                                        + evaluation + ", "
                                                                        + eMaterial.getId() + ", "
                                                                        + course.getId() + ")";

        request.addQuery(insertQuery);

        getPool().executeUpdateQuery(request);
    }

    public static void updateEvaluation(Student student, Course course, ElectronicMaterial eMaterial, boolean like, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);


        //Input: Queries
        int newEvaluation = like ? 1 : -1;
        String studentEmail = Attribute.getSQLValue(student.getEmail(), ESQLDataType.STRING);

        String updateQuery = "UPDATE evaluate_ematerial SET emat_like = " + newEvaluation;
        String condition = " WHERE s_email = " + studentEmail + " AND emat_id = " + eMaterial.getId() + " AND crs_id = " + course.getId();

        request.addQuery(updateQuery + condition);


        getPool().executeUpdateQuery(request);
    }



    public static void retrieveAllEvaluationsInCourse(final Course course, final ArrayList<ElectronicMaterial> eMats, final QueryRequestFlag<ArrayList<ElectronicMaterial>> requestFlag)
    {
        String condition = "crs_id = " + course.getId();

        try
        {
            getPool().retrieve(EMaterialEvaluation.class, new QueryRequestFlag<ArrayList<EMaterialEvaluation>>()
            {
                @Override
                public void onQuerySuccess(ArrayList<EMaterialEvaluation> resultObject)
                {
                    if(resultObject != null && requestFlag != null)
                    {
                        requestFlag.onQuerySuccess(getEMatsWithEvaluation(resultObject, eMats));
                        return;
                    }

                    if(requestFlag != null)
                        requestFlag.onQuerySuccess(null);
                }

                @Override
                public void onQueryFailure(FailureResponse failure)
                {
                    Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
                    if(requestFlag != null)
                        Entity.sendFailureResponse(requestFlag, TAG, failure.getMsg());
                }
            }, "*", condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve EMaterials Evaluations in \"" + course.getName() + "\" course: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }
    private static ArrayList<ElectronicMaterial> getEMatsWithEvaluation(ArrayList<EMaterialEvaluation> evaluations, ArrayList<ElectronicMaterial> eMats)
    {
        for(int i = 0; i < evaluations.size(); i++)
        {
            EMaterialEvaluation evaluation = evaluations.get(i);
            for(int j = 0; j < eMats.size(); j++)
            {

                if(evaluation.getEMatID().equals(eMats.get(j).getId()))
                {
                    if(evaluation.evaluation > 0)
                        eMats.get(j).addLike(evaluation.ownerEmail);
                    else if(evaluation.evaluation < 0)
                        eMats.get(j).addDislike(evaluation.ownerEmail);
                }

            }
        }

        return eMats;
    }

    //-----------------------------------------------[Entity Override Methods]-----------------------------------------------

    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("evaluate_ematerial");

        entityObject.addAttribute("s_email", ESQLDataType.STRING, ownerEmail);
        entityObject.addAttribute("emat_like", ESQLDataType.INT, evaluation);
        entityObject.addAttribute("emat_id", ESQLDataType.INT, eMatID);

        return entityObject;
    }

    @Override
    public EMaterialEvaluation toObject(EntityObject entityObject) throws ClassCastException
    {
        EMaterialEvaluation eMatEvaluation = new EMaterialEvaluation();

        eMatEvaluation.ownerEmail = entityObject.getAttributeValue("s_email", ESQLDataType.STRING, String.class);
        eMatEvaluation.evaluation = entityObject.getAttributeValue("emat_like", ESQLDataType.INT, Integer.class);
        eMatEvaluation.eMatID = entityObject.getAttributeValue("emat_id", ESQLDataType.INT, Integer.class);

        return eMatEvaluation;
    }

    @Override
    public Class<EMaterialEvaluation> getEntityClass()
    {
        return EMaterialEvaluation.class;
    }


    //--------------------------------------------------[Getters & Setters]--------------------------------------------------

    public String getOwnerEmail()
    {
        return ownerEmail;
    }

    public Integer getEvaluation()
    {
        return evaluation;
    }

    public Integer getEMatID()
    {
        return eMatID;
    }


}
