package com.ksu.nafea.logic;

import android.util.Log;

import com.ksu.nafea.data.pool.DatabasePool;
import com.ksu.nafea.data.pool.NafeaAPIPool;
import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequest;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.EntityObject;

import java.util.ArrayList;

public abstract class Entity<T>
{
    private final static DatabasePool pool = new DatabasePool();

    protected static DatabasePool getPool()
    {
        return  pool;
    }

    protected static <ReturnType> void sendFailureResponse(QueryRequestFlag<ReturnType> requestFlag, String startingNode, String msg)
    {
        FailureResponse failure = new FailureResponse(new ArrayList<String>(), new ArrayList<String>(), startingNode, msg);
        requestFlag.onQueryFailure(failure);
    }



    //----------------------------------------------------[Abstract Methods]----------------------------------------------------
    public abstract EntityObject toEntity();
    public abstract T toObject(EntityObject entityObject) throws ClassCastException;
    public abstract Class<T> getEntityClass();

}
