package com.ksu.nafea.logic.material;

import com.ksu.nafea.data.request.QueryRequest;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.QueryPostStatus;
import com.ksu.nafea.logic.course.Course;
import com.ksu.nafea.logic.Entity;

import java.util.ArrayList;

public class PhysicalMaterial extends Material<PhysicalMaterial>
{
    private String owner;
    private String firstName,lastName;
    private Integer sellerPhone;
    private String imageUrl, city;
    private Double price;

    public PhysicalMaterial()
    {
        super();
        firstName="";
        lastName="";
        sellerPhone = 0;
        imageUrl = "";
        city = "";
        price = 0.0;
    }
    public PhysicalMaterial(Integer id, String name, Integer sellerPhone, String imageUrl, String city, Double price)
    {
        super(id, name);
        firstName="";
        lastName="";
        this.sellerPhone = sellerPhone;
        this.imageUrl = imageUrl;
        this.city = city;
        this.price = price;
    }

    @Override
    public String toString()
    {
        return super.toString() + "PhysicalMaterial{" +
                "firstName+"+firstName +'\'' +
                "lastName"+lastName + '\'' +
                "sellerPhone='" + sellerPhone + '\'' +
                ", image='" + imageUrl + '\'' +
                ", city='" + city + '\'' +
                ", price=" + price +
                '}';
    }


    //-----------------------------------------------[Queries]-----------------------------------------------

    public static void delete(Course course, PhysicalMaterial material, QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        try
        {
            //Output: Return type
            QueryRequest<QueryPostStatus, QueryPostStatus> request = new QueryRequest<>(QueryPostStatus.class);
            request.setRequestFlag(requestFlag);
//
            //delete from e_material where emat_id = 4;
            //Input: Queries
            String condition = "pmat_id = " + material.getId() + " AND crs_id = " + course.getId();
//
            String deleteQuery = material.toEntity().createDeleteQuery(condition);
            request.addQuery(deleteQuery);
//
//
            getPool().executeUpdateQuery(request);
        }
        catch (Exception e)
        {
            String msg = "Failed to delete " + material.getName() + " material: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }

    public static void retrieveAllPMatsInCourse(Course course, final QueryRequestFlag<ArrayList<PhysicalMaterial>> requestFlag)
    {
        String selectClause = "s.first_name, s.last_name, s.s_email, crs_id, pmat_name, phone, pmat_photo, pmat_id, pmat_city, pmat_price";
        String joinSection = "LEFT JOIN student as s ON s.s_email = physical_material.s_email";

        String condition = "crs_id = " + course.getId();


        try
        {
            getPool().retrieve(PhysicalMaterial.class, requestFlag, selectClause, joinSection, condition);
        }
        catch (Exception e)
        {
            String msg = "Failed to retrieve physical materials in " + course.getName() + " course: " + e.getMessage();
            Entity.sendFailureResponse(requestFlag, TAG, msg);
        }
    }


    //-----------------------------------------------[Entity Override Methods]-----------------------------------------------

    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("physical_material");

        //entityObject.addAttribute("buyer_email", ESQLDataType.STRING, null);


        entityObject.addAttribute("pmat_name", ESQLDataType.STRING, name);
        entityObject.addAttribute("phone", ESQLDataType.INT, sellerPhone);
        entityObject.addAttribute("pmat_photo", ESQLDataType.STRING, imageUrl);
        entityObject.addAttribute("pmat_id", ESQLDataType.INT, id, EAttributeConstraint.PRIMARY_KEY);
        entityObject.addAttribute("pmat_city", ESQLDataType.STRING, city);
        entityObject.addAttribute("pmat_price", ESQLDataType.DOUBLE, price);
        //entityObject.addAttribute("payment_date", ESQLDataType.STRING, null);

        return entityObject;
    }

    @Override
    public PhysicalMaterial toObject(EntityObject entityObject) throws ClassCastException
    {
        PhysicalMaterial material = new PhysicalMaterial();

        material.owner = entityObject.getAttributeValue("s_email", ESQLDataType.STRING, String.class);
        material.firstName = entityObject.getAttributeValue("first_name", ESQLDataType.STRING, String.class);
        material.lastName = entityObject.getAttributeValue("last_name", ESQLDataType.STRING, String.class);
        material.id = entityObject.getAttributeValue("pmat_id", ESQLDataType.INT, Integer.class);
        material.name = entityObject.getAttributeValue("pmat_name", ESQLDataType.STRING, String.class);
        material.sellerPhone = entityObject.getAttributeValue("phone", ESQLDataType.INT, Integer.class);
        material.imageUrl = entityObject.getAttributeValue("pmat_photo", ESQLDataType.STRING, String.class);
        material.city = entityObject.getAttributeValue("pmat_city", ESQLDataType.STRING, String.class);
        material.price = entityObject.getAttributeValue("pmat_price", ESQLDataType.DOUBLE, Double.class);

        return material;
    }

    @Override
    public Class<PhysicalMaterial> getEntityClass()
    {
        return PhysicalMaterial.class;
    }


    //-----------------------------------------------[Getters & Setters]-----------------------------------------------
    public String getOwner(){
        return owner;
    }

    public Integer getSellerPhone()
    {
        return sellerPhone;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public String getCity()
    {
        return city;
    }

    public Double getPrice()
    {
        return price;
    }

    public String getFullName(){
        return firstName+" "+lastName;

    }

}