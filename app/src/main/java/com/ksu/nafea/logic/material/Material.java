package com.ksu.nafea.logic.material;

import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequest;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.account.UserAccount;
import com.ksu.nafea.logic.course.Course;
import com.ksu.nafea.logic.Entity;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.account.Student;

import java.util.ArrayList;

public abstract class Material<MaterialType> extends Entity<MaterialType>
{
    public static final String TAG = "Material";
    protected Integer id;
    protected String name;

    public Material()
    {
        this.id = 0;
        this.name = "";
    }
    public Material(Integer id, String name)
    {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "Material{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }


    //-----------------------------------------------[Queries]-----------------------------------------------

    public static void insert(Student student, Course course, Material material, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        try
        {
            //Output: Return type
            QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
            request.setRequestFlag(requestFlag);


            //Input: Queries
            Attribute studentEmail = new Attribute("s_email", ESQLDataType.STRING, student.getEmail());
            Attribute courseID = new Attribute("crs_id", ESQLDataType.INT, course.getId());

            EntityObject matEntity = material.toEntity();
            matEntity.addAttribute(0, studentEmail);
            matEntity.addAttribute(1, courseID);

            Attribute matPrimaryKey = matEntity.getFirstAttribute(EAttributeConstraint.PRIMARY_KEY);

            request.addQuery(matEntity.createInsertQuery(EAttributeConstraint.PRIMARY_KEY, "[0]"));
            request.attachQuery(matEntity.createSelectQuery("MAX(" + matPrimaryKey.getName() + ") + 1 as result"));


            getPool().executeUpdateQuery(request);
        }
        catch (Exception e)
        {
            String msg = "Failed to insert " + material.getName() + " material: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static void deleteAllMatsInCourse(Course course, final QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        String condition = "crs_id = " + course.getId();

        try
        {
            //Output: Return type
            QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
            request.setRequestFlag(requestFlag);

            //Input: Queries
            EntityObject ematEntity = (new ElectronicMaterial()).toEntity();
            EntityObject pmatEntity = (new PhysicalMaterial()).toEntity();

            request.addQuery(ematEntity.createDeleteQuery(condition));
            request.addQuery(pmatEntity.createDeleteQuery(condition));

            getPool().executeUpdateQuery(request);
        }
        catch (Exception e)
        {
            String msg = "Failed to delete materials in " + course.getName() + " course: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static void retrieveAllMatsInCourse(final Course course, final QueryRequestFlag<ArrayList<Material>> requestFlag)
    {
        final ArrayList<Material> materials = new ArrayList<Material>();

        ElectronicMaterial.retrieveAllEMatsInCourse(course, new QueryRequestFlag<ArrayList<ElectronicMaterial>>()
        {
            @Override
            public void onQuerySuccess(ArrayList<ElectronicMaterial> resultObject)
            {
                if(resultObject != null)
                {
                    for(int i = 0; i < resultObject.size(); i++)
                    {
                        materials.add(resultObject.get(i));
                    }

                    PhysicalMaterial.retrieveAllPMatsInCourse(course, new QueryRequestFlag<ArrayList<PhysicalMaterial>>()
                    {
                        @Override
                        public void onQuerySuccess(ArrayList<PhysicalMaterial> resultObject)
                        {
                            if(resultObject != null)
                            {
                                for(int i = 0; i < resultObject.size(); i++)
                                {
                                    materials.add(resultObject.get(i));
                                }

                                requestFlag.onQuerySuccess(materials);
                            }
                        }

                        @Override
                        public void onQueryFailure(FailureResponse failure)
                        {
                            failure.addNode(TAG);
                            requestFlag.onQueryFailure(failure);
                        }
                    });
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                failure.addNode(TAG);
                requestFlag.onQueryFailure(failure);
            }
        });
    }


    //--------------------------------------------------[Getters & Setters]--------------------------------------------------
    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }


}
