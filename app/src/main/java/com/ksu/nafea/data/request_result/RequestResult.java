package com.ksu.nafea.data.request_result;

import com.ksu.nafea.data.sql.EntityObject;

import java.util.ArrayList;

public class RequestResult
{
    private static final String TAG = "RequestResult";
    private ArrayList<EntityObject> records;
    private ERequestResultType resultType;


    public <T> RequestResult(T body, ERequestResultType resultType)
    {
        this.resultType = resultType;

        switch (resultType)
        {
            case Status:
                records = new ArrayList<EntityObject>();
                records.add(RequestResultParser.parseToPostStatus(body));
                break;
            case Table:
                records = RequestResultParser.parseToTable(body);
                break;
        }
    }


    public int length()
    {
        return records.size();
    }

    public EntityObject getRecord(int index)
    {
        try
        {
            return records.get(index);
        }
        catch (IndexOutOfBoundsException e)
        {
            return null;
        }
    }

    public EntityObject getQueryStatus()
    {
        if(!records.isEmpty() && resultType == ERequestResultType.Status)
            return records.get(0);

        return null;
    }


    //-------------------------------[Getters & Setters]-------------------------------
    public ERequestResultType getResultType() {
        return resultType;
    }
}
