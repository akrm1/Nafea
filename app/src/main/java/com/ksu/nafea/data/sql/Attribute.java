package com.ksu.nafea.data.sql;

public class Attribute
{
    private String name;
    private ESQLDataType type;
    private Object value;
    private EAttributeConstraint constraint;

    public Attribute(String name, ESQLDataType type, Object value)
    {
        this.name = name;
        this.type = type;
        this.value = value;
        this.constraint = EAttributeConstraint.NONE;
    }
    public Attribute(String name, ESQLDataType type, Object value, EAttributeConstraint constraint)
    {
        this.name = name;
        this.type = type;
        this.value = value;
        this.constraint = constraint;
    }


    public String getSQLValue()
    {
        if(value == null)
            return "null";

        String sqlValue = value.toString();
        switch (type)
        {
            case NONE:
            case DOUBLE:
            case INT:
                return sqlValue;
            case STRING:
                return "\"" + sqlValue + "\"";
            default:
                return "";
        }
    }


    private Number getNumberAs(ESQLDataType type)
    {
        Number number = (Number)value;
        switch (type)
        {
            case INT:
                return number != null ? number.intValue() : null;
            case DOUBLE:
                return number != null ? number.doubleValue() : null;
        }

        return null;
    }
    public <T> T getValue(Class<T> cls, ESQLDataType type)
    {
        Object value = this.value;

        switch (type)
        {
            case INT:
            case DOUBLE:
                value = getNumberAs(type);
                break;
        }

        return value != null ? cls.cast(value) : null;
    }


    @Override
    public String toString()
    {
        return this.name + " = " + getSQLValue();
    }



    public static String getSQLValue(Object value, ESQLDataType dataType)
    {
        if(value == null)
            return "null";

        String sqlValue = value.toString();
        switch (dataType)
        {
            case NONE:
            case DOUBLE:
            case INT:
                return sqlValue;
            case STRING:
                return "\"" + sqlValue + "\"";
            default:
                return "";
        }
    }


    //---------------------------------------------[Getters & Setters]---------------------------------------------
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ESQLDataType getType()
    {
        return type;
    }

    public void setType(ESQLDataType type)
    {
        this.type = type;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public EAttributeConstraint getConstraint()
    {
        return constraint;
    }

    public void setConstraint(EAttributeConstraint constraint)
    {
        this.constraint = constraint;
    }
}
