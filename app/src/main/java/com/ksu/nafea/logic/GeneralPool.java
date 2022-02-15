package com.ksu.nafea.logic;

import com.ksu.nafea.data.pool.DatabasePool;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequest;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.course.Comment;
import com.ksu.nafea.logic.course.Course;
import com.ksu.nafea.logic.material.ElectronicMaterial;
import com.ksu.nafea.logic.material.PhysicalMaterial;

import java.util.ArrayList;
import java.util.Stack;

public class GeneralPool
{
    public final static String TAG = "GeneralPool";

    public static Stack<String> getQueryStack()
    {
        return DatabasePool.getQueryStack();
    }



    //-----------------------------------------------------------[General Queries]-----------------------------------------------------------

    public static void insertEMatReport(Student student, Course course, ElectronicMaterial material,
                                        String reportType, String userComment, Integer similarMatID,
                                        final QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);


        //Input: Queries
        String studentEmail = Attribute.getSQLValue(student.getEmail(), ESQLDataType.STRING);
        reportType = Attribute.getSQLValue(reportType, ESQLDataType.STRING);
        userComment = Attribute.getSQLValue(userComment, ESQLDataType.STRING);
        String similarMat = Attribute.getSQLValue(similarMatID, ESQLDataType.INT);

        String table = "report_ematerial";
        String insertQuery = "INSERT INTO " + table + " VALUES(" + studentEmail + ", "
                + reportType + ", "
                + userComment + ", "
                + similarMat + ", "
                + material.getId() + ", "
                + course.getId() + ")";

        request.addQuery(insertQuery);

        Entity.getPool().executeUpdateQuery(request);
    }

    public static void insertPMatReport(Student student, Course course, PhysicalMaterial material,
                                        String reportType, String userComment,
                                        final QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);


        //Input: Queries
        String studentEmail = Attribute.getSQLValue(student.getEmail(), ESQLDataType.STRING);
        reportType = Attribute.getSQLValue(reportType, ESQLDataType.STRING);
        userComment = Attribute.getSQLValue(userComment, ESQLDataType.STRING);

        String table = "report_pmaterial";
        String insertQuery = "INSERT INTO " + table + " VALUES(" + studentEmail + ", "
                + reportType + ", "
                + userComment + ", "
                + material.getId() + ", "
                + course.getId() + ")";

        request.addQuery(insertQuery);

        Entity.getPool().executeUpdateQuery(request);
    }






    //-----------------------------------------------------------[Update Queries]-----------------------------------------------------------
    public static <EntityType extends Entity<EntityType>> void
    insert(Entity<EntityType> newRecord, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        try
        {
            Entity.getPool().insertUnique(newRecord, requestFlag);
        }
        catch (Exception e)
        {
            String msg = "Failed to insert record: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }


    public static <EntityType extends Entity<EntityType>> void
    update(Integer targetID, Entity<EntityType> entity, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        try
        {
            Entity.getPool().update(entity, requestFlag, targetID);
        }
        catch (Exception e)
        {
            String msg = "Failed to update record: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static <EntityType extends Entity<EntityType>> void
    updateInRange(Integer minTargetID, Integer maxTargetID, Entity<EntityType> entity, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        try
        {
            EntityObject entityObject = entity.toEntity();
            Attribute primaryKey = entityObject.getFirstAttribute(EAttributeConstraint.PRIMARY_KEY);
            String condition = primaryKey.getName() + " >= " + minTargetID;
            condition += " AND " + primaryKey.getName() + " <= " + maxTargetID;

            Entity.getPool().update(entity, requestFlag, condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to update record inn the provided range: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }


    public static <EntityType extends Entity<EntityType>> void
    delete(Class<EntityType> entityClass, Integer targetID, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        try
        {
            Entity.getPool().delete(entityClass, requestFlag, targetID);
        }
        catch (Exception e)
        {
            String msg = "Failed to delete record: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static <EntityType extends Entity<EntityType>> void
    deleteInRange(Class<EntityType> entityClass, Integer minTargetID, Integer maxTargetID, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        try
        {
            EntityObject entityObject = entityClass.newInstance().toEntity();
            Attribute primaryKey = entityObject.getFirstAttribute(EAttributeConstraint.PRIMARY_KEY);
            String condition = primaryKey.getName() + " >= " + minTargetID;
            condition += " AND " + primaryKey.getName() + " <= " + maxTargetID;

            Entity.getPool().delete(entityClass, requestFlag, condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to delete records in the provided range: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }



    //-----------------------------------------------------------[Retrieve Queries]-----------------------------------------------------------
    public static <EntityType extends Entity<EntityType>> void
    retrieveAll(Class<EntityType> entityClass, QueryRequestFlag<ArrayList<EntityType>> requestFlag)
    {
        try
        {
            Entity.getPool().retrieveAll(entityClass, requestFlag);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve all records: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static <EntityType extends Entity<EntityType>> void
    retrieve(Class<EntityType> entityClass, Integer targetID, final QueryRequestFlag<EntityType> requestFlag)
    {
        try
        {
            Entity.getPool().retrieve(entityClass, new QueryRequestFlag<ArrayList<EntityType>>()
            {
                @Override
                public void onQuerySuccess(ArrayList<EntityType> resultObject)
                {
                    if(resultObject != null)
                    {
                        EntityType object = resultObject.get(0);
                        requestFlag.onQuerySuccess(object);
                    }
                }

                @Override
                public void onQueryFailure(FailureResponse failure)
                {
                    failure.addNode(TAG);
                    requestFlag.onQueryFailure(failure);
                }
            }, targetID);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve record: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static <EntityType extends Entity<EntityType>> void
    retrieveInRange(Class<EntityType> entityClass, Integer minTargetID, Integer maxTargetID, QueryRequestFlag<ArrayList<EntityType>> requestFlag)
    {
        try
        {
            EntityObject entityObject = entityClass.newInstance().toEntity();
            Attribute primaryKey = entityObject.getFirstAttribute(EAttributeConstraint.PRIMARY_KEY);
            String condition = primaryKey.getName() + " >= " + minTargetID;
            condition += " AND " + primaryKey.getName() + " <= " + maxTargetID;

            Entity.getPool().retrieve(entityClass, requestFlag, "*", condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve records in the provided range: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

}
