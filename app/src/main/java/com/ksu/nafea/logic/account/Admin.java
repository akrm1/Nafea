package com.ksu.nafea.logic.account;

import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.Entity;
import com.ksu.nafea.logic.QueryPostStatus;

public class Admin extends UserAccount<Admin>
{


    public Admin()
    {
        super();
    }
    public Admin(String email, String password)
    {
        super(email, password, "Admin", "");
    }


    //-----------------------------------------------[Queries Methods]-----------------------------------------------


    public static void loginAdmin(Admin admin, final QueryRequestFlag<Student> requestFlag)
    {
        UserAccount.login(admin, new QueryRequestFlag<Admin>()
        {
            @Override
            public void onQuerySuccess(Admin resultObject)
            {
                if(resultObject != null)
                {
                    Student student = new Student(resultObject.getEmail(), resultObject.getPassword(), "Admin", "", true);
                    UserAccount.register(student, new QueryRequestFlag<QueryPostStatus>()
                    {
                        @Override
                        public void onQuerySuccess(QueryPostStatus resultObject)
                        {

                        }

                        @Override
                        public void onQueryFailure(FailureResponse failure)
                        {

                        }
                    });


                    requestFlag.onQuerySuccess(student);
                }
            }

            @Override
            public void onQueryFailure(FailureResponse failure)
            {
                Entity.sendFailureResponse(requestFlag, TAG, failure.getMsg());
            }
        });
    }


    // -----------------------------------------------[UserAccount Override Methods]-----------------------------------------------

    @Override
    protected String getLoginSelectData()
    {
        return "*";
    }

    @Override
    protected String getLoginJoinData()
    {
        return "";
    }

    @Override
    protected String getLoginCondition()
    {
        String email = Attribute.getSQLValue(getEmail(), ESQLDataType.STRING);
        String password = Attribute.getSQLValue(getPassword(), ESQLDataType.STRING);

        return "admin.a_email = " + email + " AND admin.password = " + password;
    }

    @Override
    protected String getEmailAttribute()
    {
        return "a_email";
    }


    //-----------------------------------------------[Entity Override Methods]-----------------------------------------------

    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("admin");

        entityObject.addAttribute("a_email", ESQLDataType.STRING, email, EAttributeConstraint.PRIMARY_KEY);
        entityObject.addAttribute("password", ESQLDataType.STRING, password);

        return entityObject;
    }

    @Override
    public Admin toObject(EntityObject entityObject) throws ClassCastException
    {
        Admin admin = new Admin();

        admin.email = entityObject.getAttributeValue("a_email", ESQLDataType.STRING, String.class);
        admin.password = entityObject.getAttributeValue("password", ESQLDataType.STRING, String.class);

        return admin;
    }

    @Override
    public Class<Admin> getEntityClass()
    {
        return Admin.class;
    }


}
