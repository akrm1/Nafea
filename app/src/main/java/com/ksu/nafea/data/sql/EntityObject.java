package com.ksu.nafea.data.sql;

import java.util.ArrayList;

public class EntityObject
{
    private String name;
    private ArrayList<Attribute> attributes;

    public EntityObject(String name)
    {
        this.name = name;
        attributes = new ArrayList<Attribute>();
    }


    public void addAttribute(Attribute attribute)
    {
        attributes.add(attribute);
    }
    public void addAttribute(int index, Attribute attribute)
    {
        attributes.add(index, attribute);
    }

    public void addAttribute(String name, ESQLDataType type, Object value)
    {
        attributes.add(new Attribute(name, type, value));
    }
    public void addAttribute(String name, ESQLDataType type, Object value, EAttributeConstraint constraint)
    {
        attributes.add(new Attribute(name, type, value, constraint));
    }
    public Attribute getAttribute(String name)
    {
        for(int i = 0; i < attributes.size(); i++)
        {
            Attribute attribute = attributes.get(i);
            if(attribute.getName().equalsIgnoreCase(name))
                return attribute;
        }

        return null;
    }

    public <T> T getAttributeValue(String name, ESQLDataType type, Class<T> cls)
    {
        Attribute attribute = getAttribute(name);
        return attribute != null ? attribute.getValue(cls, type) : null;
    }

    public Attribute getFirstAttribute(EAttributeConstraint constraint)
    {
        for(int i = 0; i < attributes.size(); i++)
        {
            Attribute attribute = attributes.get(i);
            if(attribute.getConstraint().equals(constraint))
                return attribute;
        }

        return null;
    }

    public boolean containAttribute(String name)
    {
        for(int i = 0; i < attributes.size(); i++)
        {
            Attribute attribute = attributes.get(i);
            if(attribute.getName().equalsIgnoreCase(name))
                return true;
        }

        return false;
    }

    public boolean removeAttribute(String name)
    {
        Attribute attribute = getAttribute(name);
        if(attribute != null)
            return attributes.remove(attribute);
        else
            return false;
    }


    public String getValuesSet(String start, String end, String separate)
    {
        String valuesSet = start;

        int attributesLength = attributes.size();
        for(int i = 0; i < attributesLength; i++)
        {
            valuesSet += attributes.get(i).getSQLValue();
            if(i < (attributesLength - 1))
                valuesSet += separate;
        }

        valuesSet += end;
        return valuesSet;
    }
    public String getValuesSet(String start, String end, String separate, EAttributeConstraint constraint, String attributeReplacement)
    {
        String valuesSet = start;

        int attributesLength = attributes.size();
        for(int i = 0; i < attributesLength; i++)
        {
            Attribute attribute = attributes.get(i);
            valuesSet += attribute.getConstraint().equals(constraint) ? attributeReplacement : attribute.getSQLValue();
            if(i < (attributesLength - 1))
                valuesSet += separate;
        }

        valuesSet += end;
        return valuesSet;
    }

    public String getNamesSet(String start, String end, String separate)
    {
        String namesSet = start;

        int attributesLength = attributes.size();
        for(int i = 0; i < attributesLength; i++)
        {
            namesSet += attributes.get(i).getName();
            if(i < (attributesLength - 1))
                namesSet += separate;
        }

        namesSet += end;
        return namesSet;
    }
    public String getNamesSet(String start, String end, String separate, EAttributeConstraint constraint, String attributeReplacement)
    {
        String namesSet = start;

        int attributesLength = attributes.size();
        for(int i = 0; i < attributesLength; i++)
        {
            Attribute attribute = attributes.get(i);
            namesSet += attribute.getConstraint().equals(constraint) ? attributeReplacement : attribute.getName();
            if(i < (attributesLength - 1))
                namesSet += separate;
        }

        namesSet += end;
        return namesSet;
    }

    public String getTypesSet(String start, String end, String separate)
    {
        String typesSet = start;

        int attributesLength = attributes.size();
        for(int i = 0; i < attributesLength; i++)
        {
            typesSet += attributes.get(i).getType();
            if(i < (attributesLength - 1))
                typesSet += separate;
        }

        typesSet += end;
        return typesSet;
    }

    public String getAttributesString(String start, String end, String separate)
    {
        String attributesString = start;

        int attributesLength = attributes.size();
        for(int i = 0; i < attributesLength; i++)
        {
            attributesString += attributes.get(i).toString();
            if(i < (attributesLength - 1))
                attributesString += separate;
        }

        attributesString += end;
        return attributesString;
    }
    public String getAttributesString(String start, String end, String separate, EAttributeConstraint constraint, String attributeReplacement)
    {
        String attributesString = start;

        int attributesLength = attributes.size();
        for(int i = 0; i < attributesLength; i++)
        {
            Attribute attribute = attributes.get(i);
            boolean replaceConstraint = attribute.getConstraint().equals(constraint);

            attributesString += replaceConstraint ? attributeReplacement : attribute.toString();
            if(i < (attributesLength - 1))
            {
                if(replaceConstraint)
                {
                    if(!attributeReplacement.isEmpty())
                        attributesString += separate;
                }
                else
                    attributesString += separate;
            }
        }

        attributesString += end;
        return attributesString;
    }






    //-----------------------------------------------------[Queries]-----------------------------------------------------
    public String createInsertQuery()
    {
        String valuesSet = getValuesSet("(", ")", ", ");
        String query = "INSERT INTO " + name + " VALUES" + valuesSet;

        return query;
    }

    public String createInsertQuery(EAttributeConstraint constraint, String attributeReplacement)
    {
        String valuesSet = getValuesSet("(", ")", ", ", constraint, attributeReplacement);
        String query = "INSERT INTO " + name + " VALUES" + valuesSet;

        return query;
    }


    public String createDeleteQuery(String condition)
    {
        String query = "DELETE FROM " + name + " WHERE " + condition;
        return query;
    }

    public String createUpdateQuery(String condition)
    {
        String attributesString = getAttributesString("", "", ", ");
        String query = "UPDATE " + name + " SET " + attributesString + " WHERE " + condition;
        return query;
    }

    public String createUpdateQuery(String updateSet, String condition)
    {
        String query = "UPDATE " + name + " SET " + updateSet + " WHERE " + condition;
        return query;
    }

    public String createUpdateQuery(String condition, EAttributeConstraint constraint, String attributeReplacement)
    {
        String attributesString = getAttributesString("", "", ", ", constraint, attributeReplacement);
        String query = "UPDATE " + name + " SET " + attributesString + " WHERE " + condition;
        return query;
    }

    public String createSelectQuery(String selectClause)
    {
        String query = "SELECT " + selectClause + " FROM " + name;
        return query;
    }

    public String createSelectQuery(String selectClause, String condition)
    {
        String query = "SELECT " + selectClause + " FROM " + name + " WHERE " + condition;
        return query;
    }

    public String createSelectQuery(String selectClause, String joinSection, String condition)
    {
        String query = "SELECT " + selectClause + "\n FROM " + name + "\n " + joinSection + "\n WHERE " + condition;
        return query;
    }

    public String createNestedSelectWhereQuery(String selectClause, String whereClause, String innerWhereQuery)
    {
        String query = "SELECT " + selectClause + " FROM " + name + " WHERE " + whereClause + " " + innerWhereQuery;
        return query;
    }

    public String createCustomSelectQuery(String selectClause, String joinSection, String whereClause, String groupByClause, String orderByClause)
    {
        String query = "SELECT " + selectClause + "\n ";
        query += "FROM " + name + "\n ";

        if(!joinSection.isEmpty())
            query += joinSection + "\n ";
        if(!whereClause.isEmpty())
            query += "WHERE " + whereClause + "\n ";
        if(!groupByClause.isEmpty())
            query += "GROUP BY " + groupByClause + "\n ";
        if(!orderByClause.isEmpty())
            query += "ORDER BY " + orderByClause + "\n ";

        return query;
    }

    public static String createNestedSelectFromQuery(String selectClause, String innerTableQuery, String tableName)
    {
        String query = "SELECT " + selectClause + " FROM (" + innerTableQuery + ") AS " + tableName;
        return query;
    }

    public static String createNestedSelectFromQuery(String selectClause, String innerTableQuery, String tableName, String condition)
    {
        String query = "SELECT " + selectClause + " FROM (" + innerTableQuery + ") AS " + tableName + " WHERE " + condition;
        return query;
    }

    public static String createInnerJoinSection(String mainTable, String joinedTable, String foreignKey)
    {
        String mainForeignKey = mainTable + "." + foreignKey;
        String joinedForeignKey = joinedTable + "." + foreignKey;

        String query = "INNER JOIN " + joinedTable + " ON " + mainForeignKey + " = " + joinedForeignKey;
        return query;
    }

    public static String createLeftJoinSection(String mainTable, String joinedTable, String foreignKey)
    {
        String mainForeignKey = mainTable + "." + foreignKey;
        String joinedForeignKey = joinedTable + "." + foreignKey;

        String query = "LEFT JOIN " + joinedTable + " ON " + mainForeignKey + " = " + joinedForeignKey;
        return query;
    }

    public static String createCustomSelectQuery(String selectClause, String fromClause, String joinSection, String whereClause, String groupByClause, String orderByClause)
    {
        String query = "SELECT " + selectClause + "\n ";
        query += "FROM " + fromClause + "\n ";

        if(!joinSection.isEmpty())
            query += joinSection + "\n ";
        if(!whereClause.isEmpty())
            query += "WHERE " + whereClause + "\n ";
        if(!groupByClause.isEmpty())
            query += "GROUP BY " + groupByClause + "\n ";
        if(!orderByClause.isEmpty())
            query += "ORDER BY " + orderByClause + "\n ";

        return query;
    }

}
