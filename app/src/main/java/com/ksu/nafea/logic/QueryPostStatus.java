package com.ksu.nafea.logic;

import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;

public class QueryPostStatus extends Entity<QueryPostStatus>
{
    private Integer affectedRows;


    public QueryPostStatus()
    {
        affectedRows = 0;
    }



    @Override
    public String toString()
    {
        return "QueryPostStatus{" +
                "affectedRows=" + affectedRows +
                '}';
    }


    //-----------------------------------------------[Entity Override Methods]-----------------------------------------------
    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("QueryPostStatus");

        entityObject.addAttribute("affectedRows", ESQLDataType.INT, affectedRows);

        return entityObject;
    }

    @Override
    public QueryPostStatus toObject(EntityObject entityObject) throws ClassCastException
    {
        QueryPostStatus queryPostStatus = new QueryPostStatus();

        queryPostStatus.affectedRows = entityObject.getAttributeValue("affectedRows", ESQLDataType.INT, Integer.class);

        return queryPostStatus;
    }

    @Override
    public Class<QueryPostStatus> getEntityClass()
    {
        return QueryPostStatus.class;
    }



    public Integer getAffectedRows() {
        return affectedRows;
    }

}
