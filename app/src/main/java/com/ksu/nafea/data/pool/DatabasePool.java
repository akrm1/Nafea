package com.ksu.nafea.data.pool;

import com.ksu.nafea.data.request.QueryRequest;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.Entity;
import com.ksu.nafea.logic.QueryPostStatus;

import java.util.ArrayList;
import java.util.Stack;

public class DatabasePool
{

    public static Stack<String> getQueryStack()
    {
        return NafeaAPIPool.requestsStack;
    }


    //-----------------------------------------------------------[Update Queries]-----------------------------------------------------------
    public <EntityType extends Entity<EntityType>> void
    insertUnique(Entity<EntityType> entity, QueryRequestFlag<QueryPostStatus> requestFlag) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entity.toEntity();
        Attribute primaryKey = entityObject.getFirstAttribute(EAttributeConstraint.PRIMARY_KEY);
        request.addQuery(entityObject.createInsertQuery(EAttributeConstraint.PRIMARY_KEY, "[0]"));
        request.attachQuery(entityObject.createSelectQuery("MAX(" + primaryKey.getName() + ") + 1 as result"));

        NafeaAPIPool.executePostQuery(request);
    }


    public <EntityType extends Entity<EntityType>> void
    insert(Entity<EntityType> entity, QueryRequestFlag<QueryPostStatus> requestFlag) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entity.toEntity();
        request.addQuery(entityObject.createInsertQuery());

        NafeaAPIPool.executePostQuery(request);
    }


    public <EntityType extends Entity<EntityType>> void
    update(Entity<EntityType> entity, QueryRequestFlag<QueryPostStatus> requestFlag, Integer targetID) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entity.toEntity();
        Attribute primaryKey = entityObject.getFirstAttribute(EAttributeConstraint.PRIMARY_KEY);
        String condition = primaryKey.getName() + " = " + targetID;
        request.addQuery(entityObject.createUpdateQuery(condition, EAttributeConstraint.PRIMARY_KEY, ""));

        NafeaAPIPool.executePostQuery(request);
    }

    public <EntityType extends Entity<EntityType>> void
    update(Entity<EntityType> entity, QueryRequestFlag<QueryPostStatus> requestFlag, String condition) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entity.toEntity();
        request.addQuery(entityObject.createUpdateQuery(condition, EAttributeConstraint.PRIMARY_KEY, ""));

        NafeaAPIPool.executePostQuery(request);
    }

    public <EntityType extends Entity<EntityType>> void
    update(Class<EntityType> entityClass, QueryRequestFlag<QueryPostStatus> requestFlag, String updateSet, String condition) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entityClass.newInstance().toEntity();
        request.addQuery(entityObject.createUpdateQuery(updateSet, condition));

        NafeaAPIPool.executePostQuery(request);
    }


    public <EntityType extends Entity<EntityType>> void
    delete(Class<EntityType> entityClass, QueryRequestFlag<QueryPostStatus> requestFlag, Integer targetID) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entityClass.newInstance().toEntity();
        Attribute primaryKey = entityObject.getFirstAttribute(EAttributeConstraint.PRIMARY_KEY);
        String condition = primaryKey.getName() + " = " + targetID;
        request.addQuery(entityObject.createDeleteQuery(condition));

        NafeaAPIPool.executePostQuery(request);
    }

    public <EntityType extends Entity<EntityType>> void
    delete(Class<EntityType> entityClass, QueryRequestFlag<QueryPostStatus> requestFlag, String condition) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entityClass.newInstance().toEntity();
        request.addQuery(entityObject.createDeleteQuery(condition));

        NafeaAPIPool.executePostQuery(request);
    }




    //-----------------------------------------------------------[Retrieve Queries]-----------------------------------------------------------
    public <EntityType extends Entity<EntityType>, ReturnType> void
    retrieveAll(Class<EntityType> entityClass, QueryRequestFlag<ReturnType> requestFlag) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<EntityType, ReturnType> request = new QueryRequest<>(entityClass);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entityClass.newInstance().toEntity();
        request.addQuery(entityObject.createSelectQuery("*"));

        NafeaAPIPool.executeGetQuery(request);
    }

    public <EntityType extends Entity<EntityType>, ReturnType> void
    retrieve(Class<EntityType> entityClass, QueryRequestFlag<ReturnType> requestFlag, Integer targetID) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<EntityType, ReturnType> request = new QueryRequest<>(entityClass);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entityClass.newInstance().toEntity();
        Attribute primaryKey = entityObject.getFirstAttribute(EAttributeConstraint.PRIMARY_KEY);
        String condition = primaryKey.getName() + " = " + targetID;
        request.addQuery(entityObject.createSelectQuery("*", condition));

        NafeaAPIPool.executeGetQuery(request);
    }

    public <EntityType extends Entity<EntityType>, ReturnType> void
    retrieve(Class<EntityType> entityClass, QueryRequestFlag<ReturnType> requestFlag, String selectClause) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<EntityType, ReturnType> request = new QueryRequest<>(entityClass);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entityClass.newInstance().toEntity();
        request.addQuery(entityObject.createSelectQuery(selectClause));

        NafeaAPIPool.executeGetQuery(request);
    }

    public <EntityType extends Entity<EntityType>, ReturnType> void
    retrieve(Class<EntityType> entityClass, QueryRequestFlag<ReturnType> requestFlag, String selectClause, String condition) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<EntityType, ReturnType> request = new QueryRequest<>(entityClass);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entityClass.newInstance().toEntity();
        request.addQuery(entityObject.createSelectQuery(selectClause, condition));

        NafeaAPIPool.executeGetQuery(request);
    }

    public <EntityType extends Entity<EntityType>, ReturnType> void
    retrieve(Class<EntityType> entityClass, QueryRequestFlag<ReturnType> requestFlag, String selectClause, String joinSection, String condition) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<EntityType, ReturnType> request = new QueryRequest<>(entityClass);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entityClass.newInstance().toEntity();
        request.addQuery(entityObject.createSelectQuery(selectClause, joinSection, condition));

        NafeaAPIPool.executeGetQuery(request);
    }

    public <EntityType extends Entity<EntityType>, ReturnType> void
    retrieve(Class<EntityType> entityClass, QueryRequestFlag<ReturnType> requestFlag, String selectClause, String joinSection, String whereClause, String groupByClause, String orderByClause) throws InstantiationException, IllegalAccessException
    {
        //Output: Return type
        QueryRequest<EntityType, ReturnType> request = new QueryRequest<>(entityClass);
        request.setRequestFlag(requestFlag);

        //Input: Queries
        EntityObject entityObject = entityClass.newInstance().toEntity();
        request.addQuery(entityObject.createCustomSelectQuery(selectClause, joinSection, whereClause, groupByClause, orderByClause));

        NafeaAPIPool.executeGetQuery(request);
    }



    //-----------------------------------------------------------[Custom Queries]-----------------------------------------------------------
    public void
    executeUpdateQuery(QueryRequest<QueryPostStatus, QueryPostStatus> queryRequest)
    {
        NafeaAPIPool.executePostQuery(queryRequest);
    }

    public <EntityType extends Entity<EntityType>, ReturnType> void
    executeRetrieveQuery(QueryRequest<EntityType, ReturnType> queryRequest)
    {
        NafeaAPIPool.executeGetQuery(queryRequest);
    }


}