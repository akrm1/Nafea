package com.ksu.nafea.logic;

import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.ui.nafea_views.IconData;

import java.util.ArrayList;

public class College extends Entity<College> implements IconData
{
    public static final String TAG = " College";
    private Integer id;
    private String name,category;
    private ArrayList<Major> majors;


    public College()
    {
        this.id = 0;
        this.name = "";
        this.category = "";
        majors = new ArrayList<Major>();
    }
    public College(Integer id, String name, String category)
    {
        this.id = id;
        this.name = name;
        this.category = category;
        majors = new ArrayList<Major>();
    }

    @Override
    public String toString()
    {
        return "College{" + "id=" + id + ", name='" + name + '\'' + ", category='" + category + '\'' + '}';
    }


    //-----------------------------------------------[Queries]-----------------------------------------------

    public static void retrieveCollegesInUniversity(University university, final QueryRequestFlag<ArrayList<College>> requestFlag)
    {
        String condition = "univ_id = " + university.getId();

        try
        {
            getPool().retrieve(College.class, requestFlag, "*", condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve colleges in \"" + university.getName() + "\" university: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static void retrieveCollegesInUniversity(University university, String collegeCategory, final QueryRequestFlag<ArrayList<College>> requestFlag)
    {
        String condition = "univ_id = " + university.getId() + " AND coll_category = " + Attribute.getSQLValue(collegeCategory, ESQLDataType.STRING);

        try
        {
            getPool().retrieve(College.class, requestFlag, "*", condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve colleges in \"" + university.getName() + "\" university with \"" + collegeCategory + "\" category: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static void retrieveCollegesWithCategory(String collegeCategory, final QueryRequestFlag<ArrayList<College>> requestFlag)
    {
        String condition = "coll_category = " + Attribute.getSQLValue(collegeCategory, ESQLDataType.STRING);

        try
        {
            getPool().retrieve(College.class, requestFlag, "*", condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve colleges with \"" + collegeCategory + "\" category: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static void retrieveAllCategories(final QueryRequestFlag<ArrayList<String>> requestFlag)
    {
        String selectClause = "DISTINCT coll_category";

        try
        {
            getPool().retrieve(College.class, new QueryRequestFlag<ArrayList<College>>()
            {
                @Override
                public void onQuerySuccess(ArrayList<College> resultObject)
                {
                    ArrayList<String> categories = new ArrayList<String>();
                    if(resultObject != null)
                    {
                        for(int i = 0; i < resultObject.size(); i++)
                            categories.add(resultObject.get(i).getCategory());

                        requestFlag.onQuerySuccess(categories);
                    }
                    else
                        requestFlag.onQuerySuccess(null);
                }

                @Override
                public void onQueryFailure(FailureResponse failure)
                {
                    failure.addNode(TAG);
                    requestFlag.onQueryFailure(failure);
                }
            }, selectClause);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve all colleges categories: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static void retrieveAllCategoriesInUniversity(University university, final QueryRequestFlag<ArrayList<String>> requestFlag)
    {
        String selectClause = "DISTINCT coll_category";
        String condition = "univ_id = " + university.getId();

        try
        {
            getPool().retrieve(College.class, new QueryRequestFlag<ArrayList<College>>()
            {
                @Override
                public void onQuerySuccess(ArrayList<College> resultObject)
                {
                    ArrayList<String> categories = new ArrayList<String>();
                    if(resultObject != null)
                    {
                        for(int i = 0; i < resultObject.size(); i++)
                            categories.add(resultObject.get(i).getCategory());

                        requestFlag.onQuerySuccess(categories);
                    }
                    else
                        requestFlag.onQuerySuccess(null);
                }

                @Override
                public void onQueryFailure(FailureResponse failure)
                {
                    failure.addNode(TAG);
                    requestFlag.onQueryFailure(failure);
                }
            }, selectClause, condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve all colleges categories in \"" + university.getName() + "\" university: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    //--------------------------------------------------[Entity Override Methods]--------------------------------------------------
    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("college");

        entityObject.addAttribute("coll_id", ESQLDataType.INT, id, EAttributeConstraint.PRIMARY_KEY);
        entityObject.addAttribute("coll_name", ESQLDataType.STRING, name);
        entityObject.addAttribute("coll_category", ESQLDataType.STRING, category);

        return entityObject;
    }

    @Override
    public College toObject(EntityObject entityObject) throws ClassCastException
    {
        College college = new College();

        college.id = entityObject.getAttributeValue("coll_id", ESQLDataType.INT, Integer.class);
        college.name = entityObject.getAttributeValue("coll_name", ESQLDataType.STRING, String.class);
        college.category = entityObject.getAttributeValue("coll_category", ESQLDataType.STRING, String.class);

        return college;
    }

    @Override
    public Class<College> getEntityClass()
    {
        return College.class;
    }


    //--------------------------------------------------[IconData Override Methods]--------------------------------------------------

    public Integer getIconID()
    {
        return id;
    }

    public String getText()
    {
        return name;
    }

    //--------------------------------------------------[Getters & Setters]--------------------------------------------------

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<Major> getMajors()
    {
        return majors;
    }

    public void setMajors(ArrayList<Major> majors)
    {
        this.majors = majors;
    }


}
