package com.ksu.nafea.logic;

import android.content.Context;

import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.course.Course;

import java.util.ArrayList;

public class Contain extends Entity<Contain>
{
    public static final String TAG = "Contain";
    private String level;
    private Integer major_id;
    private Integer course_id;

    public Contain()
    {
        level = "";
        major_id = 0;
        course_id = 0;
    }


    public static void retrieveAllLevels(Major major, final QueryRequestFlag<ArrayList<String>> requestFlag)
    {
        String selectClause = "DISTINCT level";
        String condition = "major_id = " + major.getId();
        String orderBy = "level_index asc";

        try
        {
            getPool().retrieve(Contain.class, new QueryRequestFlag<ArrayList<Contain>>()
            {
                @Override
                public void onQuerySuccess(ArrayList<Contain> resultObject)
                {
                    if(resultObject != null)
                    {
                        ArrayList<String> levels = new ArrayList<String>();

                        for(int i = 0; i < resultObject.size(); i++)
                        {
                            levels.add(resultObject.get(i).getLevel());
                        }

                        requestFlag.onQuerySuccess(levels);
                    }
                }

                @Override
                public void onQueryFailure(FailureResponse failure)
                {
                    failure.addNode(TAG);
                    requestFlag.onQueryFailure(failure);
                }
            }, selectClause, "", condition, "", orderBy);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve all levels in \"" + major.getName() + "\" major: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }


    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("contain");

        entityObject.addAttribute("level", ESQLDataType.STRING, level, EAttributeConstraint.PRIMARY_KEY);
        entityObject.addAttribute("major_id", ESQLDataType.INT, major_id);
        entityObject.addAttribute("crs_id", ESQLDataType.INT, course_id);

        return entityObject;
    }

    @Override
    public Contain toObject(EntityObject entityObject) throws ClassCastException
    {
        Contain contain = new Contain();

        contain.level = entityObject.getAttributeValue("level", ESQLDataType.STRING, String.class);
        contain.major_id = entityObject.getAttributeValue("major_id", ESQLDataType.INT, Integer.class);
        contain.course_id = entityObject.getAttributeValue("crs_id", ESQLDataType.INT, Integer.class);

        return contain;
    }

    @Override
    public Class<Contain> getEntityClass()
    {
        return Contain.class;
    }



    public String getLevel() {
        return level;
    }

    public Integer getMajor_id() {
        return major_id;
    }

    public Integer getCourse_id() {
        return course_id;
    }

}
