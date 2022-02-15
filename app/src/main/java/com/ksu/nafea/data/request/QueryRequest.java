package com.ksu.nafea.data.request;


import com.ksu.nafea.data.request_result.ERequestResultType;
import com.ksu.nafea.data.request_result.RequestResult;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.Entity;

import java.util.ArrayList;

public class QueryRequest<EntityType extends Entity<EntityType>, ReturnType>
{
    private ArrayList<String> mainQueries;
    private ArrayList<String> attachedQueries;
    private QueryRequestFlag<ReturnType> requestFlag;
    private Class<EntityType> entityClass;


    public QueryRequest(Class<EntityType> entityClass)
    {
        this.mainQueries = new ArrayList<String>();
        this.attachedQueries = new ArrayList<String>();
        this.requestFlag = null;
        this.entityClass = entityClass;
    }



    public void addQuery(String query)
    {
        mainQueries.add(query);
    }
    public void attachQuery(String query)
    {
        attachedQueries.add(query);
    }
    public void setRequestFlag(QueryRequestFlag<ReturnType> requestFlag)
    {
        this.requestFlag = requestFlag;
    }



    public String getQuery(int index)
    {
        return mainQueries.get(index);
    }
    public String getAttachedQuery(int index)
    {
        return attachedQueries.get(index);
    }




    //------------------------------------------------[Flags]------------------------------------------------
    @SuppressWarnings("unchecked")
    public <E> void sendSuccessSingleResponse(E body, ERequestResultType resultType) throws InstantiationException, IllegalAccessException, ClassCastException
    {
        RequestResult result = new RequestResult(body, resultType);

        EntityObject entityObject = result.getRecord(0);
        EntityType record = entityClass.newInstance().toObject(entityObject);

        if(requestFlag != null)
            requestFlag.onQuerySuccess((ReturnType) record);
    }
    @SuppressWarnings("unchecked")
    public <E> void sendSuccessListResponse(E body, ERequestResultType resultType) throws InstantiationException, IllegalAccessException, ClassCastException
    {
        RequestResult result = new RequestResult(body, resultType);

        ArrayList<EntityType> table = new ArrayList<EntityType>();
        for(int i = 0; i < result.length(); i++)
        {
            EntityObject entityObject = result.getRecord(i);
            EntityType record = entityClass.newInstance().toObject(entityObject);

            table.add(record);
        }

        if(requestFlag != null)
            requestFlag.onQuerySuccess((ReturnType)table);
    }

    public void sendFailureResponse(String starterNode, String msg)
    {
        FailureResponse failure = new FailureResponse(mainQueries, attachedQueries, starterNode, msg);
        if(requestFlag != null)
           requestFlag.onQueryFailure(failure);
    }
    public void sendFailureResponse(FailureResponse failure, String node)
    {
        failure.addNode(node);
        if(requestFlag != null)
            requestFlag.onQueryFailure(failure);
    }




    public String getMainQueries()
    {
        String queries = "";
        for(int i = 0; i < mainQueries.size(); i++)
        {
            queries += mainQueries.get(i) + ";";
        }

        return queries;
    }
    public String getAttachedQueries()
    {
        String queries = "";

        int attachedLength = attachedQueries.size();
        for(int i = 0; i < attachedLength; i++)
        {
            queries += attachedQueries.get(i);
            if(i < (attachedLength - 1))
                queries += ",";
        }

        return queries;
    }

    public String getQueryRequestFormat()
    {
        if(attachedQueries.isEmpty())
            return "/get/execute/" + getMainQueries();
        else
            return "/get/execute/" + getMainQueries() + "?attach=" + getAttachedQueries();
    }


    @Override
    public String toString()
    {
        String string = "QueryRequest:\n{\nMain Queries:\n";

        int mainLength = mainQueries.size();
        for(int i = 0; i < mainLength; i++)
        {
            string += "  [" + i + "]:" + mainQueries.get(i);
            if(i < (mainLength - 1))
                string += ",\n";
        }
        string += "\nAttached Queries:\n";


        int attachedLength = attachedQueries.size();
        for(int i = 0; i < attachedLength; i++)
        {
            string += "  [" + i + "]:" + attachedQueries.get(i);
            if(i < (attachedLength - 1))
                string += ",\n";
        }
        string += "\n}";

        return string;
    }

}
