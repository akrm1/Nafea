package com.ksu.nafea.logic.account;

import com.ksu.nafea.data.request.FailureResponse;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.EAttributeConstraint;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;
import com.ksu.nafea.logic.College;
import com.ksu.nafea.logic.Entity;
import com.ksu.nafea.logic.Major;
import com.ksu.nafea.logic.University;

import java.util.ArrayList;
import java.util.logging.Level;

public class Student extends UserAccount<Student>
{
    private boolean hasAdminAuthority = false;

    private Integer assignedMajor;
    private University university;
    private College college;
    private Major major;

    public Student()
    {
        super();
        assignedMajor = null;
        university = null;
        college = null;
        major = null;
    }
    public Student(String email, String password)
    {
        super(email, password, "None", "None");
        assignedMajor = null;
        university = null;
        college = null;
        major = null;
    }
    public Student(String email, String password, String firstName, String lastName, boolean hasAdminAuthority)
    {
        super(email, password, firstName, lastName);
        assignedMajor = null;
        university = null;
        college = null;
        this.major = null;

        this.hasAdminAuthority = hasAdminAuthority;
    }
    public Student(String email, String password, String firstName, String lastName, Integer majorID)
    {
        super(email, password, firstName, lastName);
        assignedMajor = null;
        university = null;
        college = null;
        this.major = new Major(majorID, "");
      //  level=null;
    }


    @Override
    public String toString()
    {
        return "Student:\n" + super.toString() + "\n" +
                "[" + university.toString() + "]\n" +
                "[" + college.toString() + "]\n" +
                "[" + major.toString() + "]\n";
        //        "[" + level.toString() + "]" ;
    }


    public boolean isAdmin()
    {
        return hasAdminAuthority;
    }

    public boolean hasAuthorityOnMajor(Integer majorID)
    {
        if(assignedMajor == null)
            return false;

        return assignedMajor == majorID;
    }

    public boolean isCommunityManager()
    {
        return assignedMajor != null;
    }



    // -----------------------------------------------[UserAccount Override Methods]-----------------------------------------------

    @Override
    protected String getLoginSelectData()
    {
        return "student.s_email, student.password, student.first_name, student.last_name,\n" +
                " major.major_id, major.major_name, major.major_plan,\n" +
                " college.coll_id, college.coll_name, college.coll_category,\n" +
                " university.univ_id, university.univ_name, university.univ_city,\n" +
                " major_adminstration.major_id as community_manager\n";
            //    " level.level_num, level.level_written";
    }

    @Override
    protected String getLoginJoinData()
    {
        String joinData = EntityObject.createLeftJoinSection("student", "major", "major_id") + "\n ";
        joinData += EntityObject.createLeftJoinSection("major", "college", "coll_id") + "\n ";
        joinData += EntityObject.createLeftJoinSection("college", "university", "univ_id") + "\n ";
        joinData += EntityObject.createLeftJoinSection("student", "major_adminstration", "s_email");

        return joinData;
    }

    @Override
    protected String getLoginCondition()
    {
        String email = Attribute.getSQLValue(getEmail(), ESQLDataType.STRING);
        String password = Attribute.getSQLValue(getPassword(), ESQLDataType.STRING);

        return "student.s_email = " + email + " AND student.password = " + password;
    }

    @Override
    protected String getEmailAttribute()
    {
        return "s_email";
    }

    //-----------------------------------------------[Entity Override Methods]-----------------------------------------------
    @Override
    public EntityObject toEntity()
    {
        EntityObject entityObject = new EntityObject("student");

        entityObject.addAttribute("s_email", ESQLDataType.STRING, email, EAttributeConstraint.PRIMARY_KEY);
        entityObject.addAttribute("password", ESQLDataType.STRING, password);
        entityObject.addAttribute("first_name", ESQLDataType.STRING, firstName);
        entityObject.addAttribute("last_name", ESQLDataType.STRING, lastName);

        Integer majorID = major != null ? major.getId() : null;
        entityObject.addAttribute("major_id", ESQLDataType.INT, majorID, EAttributeConstraint.FOREIGN_KEY);
        entityObject.addAttribute("assigned_major", ESQLDataType.INT, null);

        return entityObject;
    }

    @Override
    public Student toObject(EntityObject entityObject) throws ClassCastException
    {
        Student student = new Student();

        student.email = entityObject.getAttributeValue("s_email", ESQLDataType.STRING, String.class);
        student.password = entityObject.getAttributeValue("password", ESQLDataType.STRING, String.class);
        student.firstName = entityObject.getAttributeValue("first_name", ESQLDataType.STRING, String.class);
        student.lastName = entityObject.getAttributeValue("last_name", ESQLDataType.STRING, String.class);

        student.assignedMajor = entityObject.getAttributeValue("community_manager", ESQLDataType.INT, Integer.class);

        Integer majorID = entityObject.getAttributeValue("major_id", ESQLDataType.INT, Integer.class);
        String majorName = entityObject.getAttributeValue("major_name", ESQLDataType.STRING, String.class);
        String majorPlanUrl = entityObject.getAttributeValue("major_plan", ESQLDataType.STRING, String.class);

        Integer collegeID = entityObject.getAttributeValue("coll_id", ESQLDataType.INT, Integer.class);
        String collegeName = entityObject.getAttributeValue("coll_name", ESQLDataType.STRING, String.class);
        String collegeCategory = entityObject.getAttributeValue("coll_category", ESQLDataType.STRING, String.class);

        Integer universityID = entityObject.getAttributeValue("univ_id", ESQLDataType.INT, Integer.class);
        String universityName = entityObject.getAttributeValue("univ_name", ESQLDataType.STRING, String.class);
        String universityCity = entityObject.getAttributeValue("univ_city", ESQLDataType.STRING, String.class);

        student.university = new University(universityID, universityName, universityCity);
        student.college = new College(collegeID, collegeName, collegeCategory);
        student.major = new Major(majorID, majorName, majorPlanUrl);

        return student;
    }

    @Override
    public Class<Student> getEntityClass() {
        return Student.class;
    }


    //-----------------------------------------------[Getters & Setters]-----------------------------------------------

    public University getUniversity()
    {
        return university;
    }

    public College getCollege()
    {
        return college;
    }

    public Major getMajor()
    {
        return major;
    }

    public Integer getAssignedMajor()
    {
        return assignedMajor;
    }

  //  public level getLevel(){return level;}



}