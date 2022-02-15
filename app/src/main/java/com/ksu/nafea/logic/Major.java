package com.ksu.nafea.logic;


import com.ksu.nafea.data.request.QueryRequest;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.course.Course;
import com.ksu.nafea.ui.nafea_views.IconData;

import java.util.ArrayList;

public class Major extends Entity<Major> implements IconData
{
    public static final String TAG = "Major";
    private Integer id;
    private String name;
    private String planUrl;
    private ArrayList<Course> courses;
    private ArrayList<String> levels;


    public Major()
    {
        this.id = 0;
        this.name = "";
        planUrl = "";
        courses = new ArrayList<Course>();
        levels = new ArrayList<String>();
    }
    public Major(Integer id, String name)
    {
        this.id = id;
        this.name = name;
        planUrl = "";
        courses = new ArrayList<Course>();
        levels = new ArrayList<String>();
    }
    public Major(Integer id, String name, String planUrl)
    {
        this.id = id;
        this.name = name;
        this.planUrl = planUrl;
        courses = new ArrayList<Course>();
        levels = new ArrayList<String>();
    }


    @Override
    public String toString()
    {
        return "Major{" + "id=" + id + ", name='" + name + '}';
    }


    //-----------------------------------------------[Queries]-----------------------------------------------


    public static void updatePlan(Major major, String planUrl, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(requestFlag);


        //Input: Queries
        String id = Attribute.getSQLValue(major.getId(), ESQLDataType.INT);
        String plan = Attribute.getSQLValue(planUrl, ESQLDataType.STRING);


        String insertQuery = "UPDATE major SET major_plan = " + plan + " WHERE major_id = " + id;

        request.addQuery(insertQuery);

        getPool().executeUpdateQuery(request);
    }


    public static void retrieveMajorsInCollege(College college, final QueryRequestFlag<ArrayList<Major>> requestFlag)
    {
        String condition = "coll_id = " + college.getId();

        try
        {
            getPool().retrieve(Major.class, requestFlag, "*", condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve majors in \"" + college.getName() + "\" college: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static void retrieveAllMajorsHasCourse(Course course, final QueryRequestFlag<ArrayList<Major>> requestFlag)
    {
        String condition = "major_id IN (SELECT major_id FROM contain WHERE crs_id = " + course.getId() + ")";

        try
        {
            getPool().retrieve(Major.class, requestFlag, "*", condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve majors has \"" + course.getName() + "\" course: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }


    //--------------------------------------------------[Entity Override Methods]--------------------------------------------------
    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("major");

        entityObject.addAttribute("major_id", ESQLDataType.INT, id, EAttributeConstraint.PRIMARY_KEY);
        entityObject.addAttribute("major_name", ESQLDataType.STRING, name);
        entityObject.addAttribute("major_plan", ESQLDataType.STRING, planUrl);

        return entityObject;
    }

    @Override
    public Major toObject(EntityObject entityObject) throws ClassCastException
    {
        Major major = new Major();

        major.id = entityObject.getAttributeValue("major_id", ESQLDataType.INT, Integer.class);
        major.name = entityObject.getAttributeValue("major_name", ESQLDataType.STRING, String.class);
        major.planUrl = entityObject.getAttributeValue("major_plan", ESQLDataType.STRING, String.class);

        return major;
    }

    @Override
    public Class<Major> getEntityClass()
    {
        return Major.class;
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

    public String getPlanUrl()
    {
        return planUrl;
    }

    public void setPlanUrl(String planUrl)
    {
        this.planUrl = planUrl;
    }

    public ArrayList<Course> getCourses()
    {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses)
    {
        this.courses = courses;
    }

    public ArrayList<String> getLevels() {
        return levels;
    }

    public void setLevels(String allLevels, ArrayList<String> levels)
    {
        this.levels = levels;
        this.levels.add(0, allLevels);
    }


}
