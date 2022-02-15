package com.ksu.nafea.logic;

import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.ui.nafea_views.IconData;

import java.util.ArrayList;

public class University extends Entity<University> implements IconData
{
    public static final String TAG = "University";
    private Integer id;
    private String name,city;
    private ArrayList<College> colleges;


    public University()
    {
        this.id = 0;
        this.name = "";
        this.city = "";
        colleges = new ArrayList<College>();
    }
    public University(Integer id,String name, String city)
    {
        this.id =id;
        this.name = name;
        this.city = city;
        colleges = new ArrayList<College>();
    }


    @Override
    public String toString()
    {
        return "University{" + "id=" + id + ", name='" + name + '\'' + ", city='" + city + '\'' + '}';
    }

    //-----------------------------------------------[Queries]-----------------------------------------------

    public static void retrieveAllCities(final QueryRequestFlag<ArrayList<String>> requestFlag)
    {
        String selectClause = "DISTINCT univ_city";

        try
        {
            getPool().retrieve(University.class, new QueryRequestFlag<ArrayList<University>>()
            {
                @Override
                public void onQuerySuccess(ArrayList<University> resultObject)
                {
                    ArrayList<String> cities = new ArrayList<String>();
                    if(resultObject != null)
                    {
                        for(int i = 0; i < resultObject.size(); i++)
                            cities.add(resultObject.get(i).getCity());

                        requestFlag.onQuerySuccess(cities);
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
            String msg = "Failed to retrieve all cities: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static void retrieveUniversitiesInCity(String city, final QueryRequestFlag<ArrayList<University>> requestFlag)
    {
        String condition = "univ_city = " + Attribute.getSQLValue(city, ESQLDataType.STRING);

        try
        {
            getPool().retrieve(University.class, requestFlag, "*", condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve all university in \"" + city + "\" city: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }


    //--------------------------------------------------[Entity Override Methods]--------------------------------------------------
    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("university");

        entityObject.addAttribute("univ_id", ESQLDataType.INT, id, EAttributeConstraint.PRIMARY_KEY);
        entityObject.addAttribute("univ_name", ESQLDataType.STRING, name);
        entityObject.addAttribute("univ_city", ESQLDataType.STRING, city);

        return entityObject;
    }

    @Override
    public University toObject(EntityObject entityObject) throws ClassCastException
    {
        University university = new University();

        university.id = entityObject.getAttributeValue("univ_id", ESQLDataType.INT, Integer.class);
        university.name = entityObject.getAttributeValue("univ_name", ESQLDataType.STRING, String.class);
        university.city = entityObject.getAttributeValue("univ_city", ESQLDataType.STRING, String.class);

        return university;
    }

    @Override
    public Class<University> getEntityClass()
    {
        return University.class;
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

    //--------------------------------------------------[Setters & Getters]--------------------------------------------------
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public ArrayList<College> getColleges()
    {
        return colleges;
    }

    public void setColleges(ArrayList<College> colleges)
    {
        this.colleges = colleges;
    }


}
