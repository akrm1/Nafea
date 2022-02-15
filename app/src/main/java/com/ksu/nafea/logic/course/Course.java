package com.ksu.nafea.logic.course;

import android.util.Log;

import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequest;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.Entity;
import com.ksu.nafea.logic.Major;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.material.ElectronicMaterial;
import com.ksu.nafea.logic.material.PhysicalMaterial;

import java.util.ArrayList;

public class Course extends Entity<Course>
{
    public final static String TAG = "Course";
    private Integer id;
    private String name,symbol,description;

    private CourseEvaluation evaluation;
    private ArrayList<ElectronicMaterial> eMats;
    private ArrayList<PhysicalMaterial> pMats;
    private ArrayList<Comment> comments;


    public Course()
    {
        this.id = 0;
        this.name = "None";
        this.symbol = "None";
        this.description = "None";

        this.evaluation = null;
        this.eMats = null;
        this.pMats = null;
        this.comments = null;
    }
    public Course(Integer id, String name, String symbol, String description)
    {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.description = description;

        this.evaluation = null;
        this.eMats = null;
        this.pMats = null;
        this.comments = null;
    }



    public static EntityObject getContainEntity(Integer courseID, Integer majorID, String level, Integer levelIndex)
    {
        EntityObject entityObject = new EntityObject("contain");

        entityObject.addAttribute("level_index", ESQLDataType.INT, levelIndex);
        entityObject.addAttribute("level", ESQLDataType.STRING, level);
        entityObject.addAttribute("major_id", ESQLDataType.INT, majorID, EAttributeConstraint.FOREIGN_KEY);
        entityObject.addAttribute("crs_id", ESQLDataType.INT, courseID, EAttributeConstraint.PRIMARY_KEY);

        return entityObject;
    }


    @Override
    public String toString()
    {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", description='" + description + '\'' +
                ", evaluation=" + evaluation.toString() +
                '}';
    }

    //-----------------------------------------------[Queries]-----------------------------------------------
    public static void insert(final Major major, final String level, final Integer levelIndex, final Course course, final QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(new QueryRequestFlag<QueryPostStatus>()
        {
            @Override
            public void onQuerySuccess(QueryPostStatus resultObject)
            {
                if(resultObject != null)
                {
                    if(resultObject.getAffectedRows() > 0)
                    {
                        //Output: Return type
                        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
                        request.setRequestFlag(requestFlag);

                        String majorID = Attribute.getSQLValue(major.getId(), ESQLDataType.INT);
                        String symbol = Attribute.getSQLValue(course.getSymbol(), ESQLDataType.STRING).replace(" ", "%");
                        String levelString = Attribute.getSQLValue(level, ESQLDataType.STRING);
                        String levelIndexString = Attribute.getSQLValue(levelIndex, ESQLDataType.INT);

                        String values = levelIndexString + ", " + levelString + ", " + majorID;
                        String containInsertQuery = "INSERT INTO contain(level_index, level, major_id, crs_id) " +
                                "VALUES(" + values + ", (SELECT crs_id FROM course WHERE crs_symbol LIKE " + symbol + "))";

                        request.addQuery(containInsertQuery);
                        getPool().executeUpdateQuery(request);
                    }
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                Entity.sendFailureResponse(requestFlag, TAG, failure.getMsg());
            }
        });


        //Input: Queries
        EntityObject courseEntity = course.toEntity();
        Attribute coursePrimaryKey = courseEntity.getFirstAttribute(EAttributeConstraint.PRIMARY_KEY);

        String name = Attribute.getSQLValue(course.getName(), ESQLDataType.STRING);
        String symbol = Attribute.getSQLValue(course.getSymbol(), ESQLDataType.STRING);
        String description = Attribute.getSQLValue(course.getDescription(), ESQLDataType.STRING);

        String values = "[0], " + name + ", " + symbol + ", " + description;
        symbol = symbol.replace(" ", "%");
        String courseInsertQuery = "INSERT INTO course(crs_id, crs_name, crs_symbol, crs_desc) \n" +
                             "SELECT " + values + " FROM dual \n" +
                             "WHERE NOT EXISTS (SELECT * FROM course WHERE crs_symbol LIKE " + symbol + ")";


        request.addQuery(courseInsertQuery);
        request.attachQuery(courseEntity.createSelectQuery("MAX(" + coursePrimaryKey.getName() + ") + 1 as result"));


        getPool().executeUpdateQuery(request);
    }

    public static void deleteAllCourses(Major major, final ArrayList<Course> courses, final QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        //Output: Return type
        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
        request.setRequestFlag(new QueryRequestFlag<QueryPostStatus>()
        {
            @Override
            public void onQuerySuccess(QueryPostStatus resultObject)
            {
                requestFlag.onQuerySuccess(resultObject);

                if(resultObject != null)
                {
                    if(resultObject.getAffectedRows() > 0 && courses.size() == 1)
                    {
                        QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
                        request.setRequestFlag(new QueryRequestFlag<QueryPostStatus>() {
                            @Override
                            public void onQuerySuccess(QueryPostStatus resultObject) {

                            }

                            @Override
                            public void onQueryFailure(FailureResponse failure) {

                            }
                        });

                        Integer id = courses.get(0).getId();
                        request.addQuery("DELETE FROM course WHERE crs_id = " + id + " AND NOT EXISTS (SELECT * FROM contain WHERE crs_id = " + id + ")");
                        
                        getPool().executeUpdateQuery(request);
                    }
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                Entity.sendFailureResponse(requestFlag, TAG, failure.getMsg());
            }
        });


        //Input: Queries
        String condition = "";
        for(int i = 0; i < courses.size(); i++)
        {
            condition += "crs_id = " + courses.get(i).getId();

            if(i < (courses.size() - 1))
                condition += " OR ";
        }

        request.addQuery("DELETE FROM contain WHERE major_id = " + major.getId() + " AND " + condition);


        getPool().executeUpdateQuery(request);
    }


    public static void retrieveAllCoursesInMajor(Major major, final QueryRequestFlag<ArrayList<Course>> requestFlag)
    {
        String selectClause = "course.crs_id, crs_name, crs_symbol, crs_desc,\n" +
                            "  avg(content_size) as content_size,\n" +
                            "  avg(assignments_difficulty) as assignments_difficulty,\n" +
                            "  avg(exams_difficulty) as exams_difficulty";
        String joinSection = "LEFT JOIN evaluate_course ON evaluate_course.crs_id = course.crs_id";
        String condition = "course.crs_id IN (SELECT crs_id FROM contain WHERE major_id = " + major.getId() + ")";
        String groupBy = "course.crs_id";
        String orderBy = "";

        try
        {
            getPool().retrieve(Course.class, requestFlag, selectClause, joinSection, condition, groupBy, orderBy);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve courses in \"" + major.getName() + "\" major: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static void retrieveCoursesInMajor(Major major, String level, final QueryRequestFlag<ArrayList<Course>> requestFlag)
    {
        String levelValue = Attribute.getSQLValue(level, ESQLDataType.STRING);

        String selectClause = "course.crs_id, crs_name, crs_symbol, crs_desc, level,\n" +
                "  avg(content_size) as content_size,\n" +
                "  avg(assignments_difficulty) as assignments_difficulty,\n" +
                "  avg(exams_difficulty) as exams_difficulty";
        String joinSection = "LEFT JOIN evaluate_course ON evaluate_course.crs_id = course.crs_id\n";
        joinSection += " LEFT JOIN contain ON contain.crs_id = course.crs_id";
        String condition = "course.crs_id IN (SELECT crs_id FROM contain WHERE major_id = " + major.getId() + "  AND level = " + levelValue + ")";
        String groupBy = "course.crs_id";
        String orderBy = "";

        try
        {
            getPool().retrieve(Course.class, requestFlag, selectClause, joinSection, condition, groupBy, orderBy);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve courses in \"" + major.getName() + "\" major: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }



    public static Course retrieveFullCourse(Course course, final QueryRequestFlag<Boolean> requestFlag)
    {
        ElectronicMaterial.retrieveAllEMatsInCourse(course, course.retrieveCourseData(ElectronicMaterial.class, requestFlag));
        PhysicalMaterial.retrieveAllPMatsInCourse(course, course.retrieveCourseData(PhysicalMaterial.class, requestFlag));
        Comment.retrieveAllComments(course, course.retrieveCourseData(Comment.class, requestFlag));

        return course;
    }
    private <T> QueryRequestFlag<ArrayList<T>> retrieveCourseData(final Class<T> cls, final QueryRequestFlag<Boolean> requestFlag)
    {
        return new QueryRequestFlag<ArrayList<T>>()
        {
            @Override
            public void onQuerySuccess(ArrayList<T> resultObject)
            {
                if(resultObject != null)
                {
                    if(cls == ElectronicMaterial.class)
                        eMats = (ArrayList<ElectronicMaterial>) resultObject;
                    else if(cls == PhysicalMaterial.class)
                        pMats = (ArrayList<PhysicalMaterial>) resultObject;
                    else if(cls == Comment.class)
                        comments = (ArrayList<Comment>) resultObject;
                    else
                    {
                        requestFlag.onQuerySuccess(false);
                        return;
                    }

                    requestFlag.onQuerySuccess(true);
                }
                else
                    requestFlag.onQuerySuccess(false);
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
                failure.addNode(TAG);
                requestFlag.onQueryFailure(failure);
            }
        };
    }


    //-----------------------------------------------[Entity Override Methods]-----------------------------------------------
    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("course");

        entityObject.addAttribute("crs_id", ESQLDataType.INT, id, EAttributeConstraint.PRIMARY_KEY);
        entityObject.addAttribute("crs_name", ESQLDataType.STRING, name);
        entityObject.addAttribute("crs_symbol", ESQLDataType.STRING, symbol);
        entityObject.addAttribute("crs_desc", ESQLDataType.STRING, description);

        return entityObject;
    }

    @Override
    public Course toObject(EntityObject entityObject) throws ClassCastException
    {
        Course crs = new Course();

        crs.id = entityObject.getAttributeValue("crs_id", ESQLDataType.INT, Integer.class);
        crs.name = entityObject.getAttributeValue("crs_name", ESQLDataType.STRING, String.class);
        crs.symbol = entityObject.getAttributeValue("crs_symbol", ESQLDataType.STRING, String.class);
        crs.description = entityObject.getAttributeValue("crs_desc", ESQLDataType.STRING, String.class);
        Double contentSize = entityObject.getAttributeValue("content_size", ESQLDataType.DOUBLE, Double.class);
        Double assignmentsDifficulty = entityObject.getAttributeValue("assignments_difficulty", ESQLDataType.DOUBLE, Double.class);
        Double examsDifficulty = entityObject.getAttributeValue("exams_difficulty", ESQLDataType.DOUBLE, Double.class);

        crs.evaluation = new CourseEvaluation(contentSize, assignmentsDifficulty, examsDifficulty);

        return crs;
    }

    @Override
    public Class<Course> getEntityClass()
    {
        return Course.class;
    }


    //--------------------------------------------------[Getters & Setters]--------------------------------------------------
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }

    public CourseEvaluation getEvaluation()
    {
        return evaluation;
    }

    public ArrayList<ElectronicMaterial> getEMats()
    {
        return eMats != null ? eMats : new ArrayList<ElectronicMaterial>();
    }

    public ArrayList<PhysicalMaterial> getPMats()
    {
        return pMats != null ? pMats : new ArrayList<PhysicalMaterial>();
    }

    public ArrayList<Comment> getComments()
    {
        return comments != null ? comments : new ArrayList<Comment>();
    }

    public void updateEMats(ArrayList<ElectronicMaterial> eMats) {
        this.eMats = eMats;
    }

    public void updatePMats(ArrayList<PhysicalMaterial> pMats) {
        this.pMats = pMats;
    }


    public void updateCommentss(ArrayList<Comment> comments)
    {
        this.comments = comments;
    }


}
