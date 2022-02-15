package com.ksu.nafea.logic.material;

import android.util.Log;

import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequest;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.account.UserAccount;
import com.ksu.nafea.logic.course.Course;
import com.ksu.nafea.logic.Entity;

import java.util.ArrayList;

public class ElectronicMaterial extends Material<ElectronicMaterial>
{
    private String owner, type, extension, url;
    private ArrayList<String> likes, dislikes;

    public ElectronicMaterial()
    {
        super();
        owner = "";
        type = "";
        extension = "";
        url = "";

        likes = new ArrayList<String>();
        dislikes = new ArrayList<String>();
    }
    public ElectronicMaterial(Integer id, String name, String type, String url, String extension)
    {
        super(id, name);
        owner = "";
        this.type = type;
        this.url = url;
        this.extension = extension;

        likes = new ArrayList<String>();
        dislikes = new ArrayList<String>();
    }


    @Override
    public String toString()
    {
        return "ElectronicMaterial{" +
                "owner='" + owner + '\'' +
                ", type='" + type + '\'' +
                ", extension='" + extension + '\'' +
                ", url='" + url + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static ArrayList<ElectronicMaterial> getEMaterialsByType(ArrayList<ElectronicMaterial> eMats, String type)
    {
        ArrayList<ElectronicMaterial> targetEMats = new ArrayList<ElectronicMaterial>();

        for(int i = 0; i < eMats.size(); i++)
        {
            if(eMats.get(i).type.equalsIgnoreCase(type))
                targetEMats.add(eMats.get(i));
        }

        return targetEMats;
    }


    private boolean resetEvaluator(ArrayList<String> firstEvaluation, ArrayList<String> secondEvaluation, String evaluator)
    {
        if(firstEvaluation.contains(evaluator))
            return false;
        secondEvaluation.remove(evaluator);
        return true;
    }

    public boolean addLike(String email)
    {
        if(!resetEvaluator(likes, dislikes, email))
            return false;

        likes.add(email);
        return true;
    }
    public boolean addDislike(String email)
    {
        if(!resetEvaluator(dislikes, likes, email))
            return false;

        dislikes.add(email);
        return true;
    }

    public int getLikes()
    {
        return likes.size();
    }
    public int getDislikes()
    {
        return dislikes.size();
    }

    //-----------------------------------------------[Queries]-----------------------------------------------

    public static void delete(Course course, ElectronicMaterial material, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        try
        {
            //Output: Return type
            QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
            request.setRequestFlag(requestFlag);

            //delete from e_material where emat_id = 4;
            //Input: Queries
            String condition = "emat_id = " + material.getId() + " AND crs_id = " + course.getId();

            String deleteQuery = material.toEntity().createDeleteQuery(condition);
            request.addQuery(deleteQuery);


            getPool().executeUpdateQuery(request);
        }
        catch (Exception e)
        {
            String msg = "Failed to delete " + material.getName() + " material: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }


    public static void retrieveAllEMatsInCourse(final Course course, final QueryRequestFlag<ArrayList<ElectronicMaterial>> requestFlag)
    {
        String condition = "crs_id = " + course.getId();

        try
        {
            getPool().retrieve(ElectronicMaterial.class, new QueryRequestFlag<ArrayList<ElectronicMaterial>>()
            {
                @Override
                public void onQuerySuccess(ArrayList<ElectronicMaterial> resultObject)
                {
                    if(resultObject != null)
                    {
                        EMaterialEvaluation.retrieveAllEvaluationsInCourse(course, resultObject, new QueryRequestFlag<ArrayList<ElectronicMaterial>>()
                        {
                            @Override
                            public void onQuerySuccess(ArrayList<ElectronicMaterial> resultObject)
                            {
                                if(resultObject != null && requestFlag != null)
                                {
                                    requestFlag.onQuerySuccess(resultObject);

                                    for(int i = 0; i < resultObject.size(); i++)
                                    {
                                        ArrayList<String> eva = resultObject.get(i).likes;
                                        for(int j = 0; j < eva.size(); j++)
                                            Log.d(TAG, eva.get(j) + ", " + resultObject.get(i).id);
                                    }
                                }
                            }

                            @Override
                            public void onQueryFailure(FailureResponse failure)
                            {

                            }
                        });
                    }

                    if(requestFlag != null)
                        requestFlag.onQuerySuccess(resultObject);
                }

                @Override
                public void onQueryFailure(FailureResponse failure)
                {
                    Log.d(TAG, failure.getMsg() + "\n" + failure.toString());
                    if(requestFlag != null)
                        Entity.sendFailureResponse(requestFlag, TAG, failure.getMsg());
                }
            }, "*", condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve electronic materials in " + course.getName() + " course: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }


    //-----------------------------------------------[Entity Override Methods]-----------------------------------------------

    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("e_material");

        entityObject.addAttribute("emat_type", ESQLDataType.STRING, type);
        entityObject.addAttribute("emat_url", ESQLDataType.STRING, url);
        entityObject.addAttribute("emat_name", ESQLDataType.STRING, name);
        entityObject.addAttribute("emat_id", ESQLDataType.INT, id, EAttributeConstraint.PRIMARY_KEY);
        entityObject.addAttribute("emat_extension", ESQLDataType.STRING, extension);

        return entityObject;
    }

    @Override
    public ElectronicMaterial toObject(EntityObject entityObject) throws ClassCastException
    {
        ElectronicMaterial material = new ElectronicMaterial();

        material.owner = entityObject.getAttributeValue("s_email", ESQLDataType.STRING, String.class);
        material.id = entityObject.getAttributeValue("emat_id", ESQLDataType.INT, Integer.class);
        material.name = entityObject.getAttributeValue("emat_name", ESQLDataType.STRING, String.class);
        material.type = entityObject.getAttributeValue("emat_type", ESQLDataType.STRING, String.class);
        material.url = entityObject.getAttributeValue("emat_url", ESQLDataType.STRING, String.class);
        material.extension = entityObject.getAttributeValue("emat_extension", ESQLDataType.STRING, String.class);

        return material;
    }

    @Override
    public Class<ElectronicMaterial> getEntityClass()
    {
        return ElectronicMaterial.class;
    }


    //-----------------------------------------------[Getters & Setters]-----------------------------------------------
    public String getOwner()
    {
        return owner;
    }

    public String getType()
    {
        return type;
    }

    public String getExtension()
    {
        return extension;
    }

    public String getUrl()
    {
        return url;
    }


}
