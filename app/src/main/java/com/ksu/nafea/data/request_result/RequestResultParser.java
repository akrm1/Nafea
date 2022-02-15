package com.ksu.nafea.data.request_result;

import android.util.Log;

import com.google.gson.Gson;
import com.ksu.nafea.data.sql.Attribute;
import com.ksu.nafea.data.sql.ESQLDataType;
import com.ksu.nafea.data.sql.EntityObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RequestResultParser
{
    private static String TAG = "RequestResultParser";

    private static <T> JSONArray parseBodyToJSON(T body, boolean isPost)
    {
        String data = new Gson().toJson(body);
        JSONArray json = null;
        try
        {

            if(!isPost)
                json = new JSONArray(data);
            else
            {
                JSONObject jsonObject = new JSONObject(data);
                json = new JSONArray();
                json.put(jsonObject);
            }
        }
        catch(JSONException e)
        {
            Log.e(TAG, "couldn't convert request body to json array: " + e.getMessage());
        }

        return json;
    }


    private static Attribute parseJsonObjectAttribute(JSONObject jsonObject, String attrName) throws JSONException
    {
        Object value = jsonObject.get(attrName);
        Attribute attribute = new Attribute(attrName, ESQLDataType.NONE, value);

        if(value instanceof Integer)
        {
            attribute.setType(ESQLDataType.INT);
        }
        else if(value instanceof Double)
        {
            attribute.setType(ESQLDataType.DOUBLE);
        }
        else if(value instanceof String)
        {
            attribute.setType(ESQLDataType.STRING);
        }

        return attribute;
    }

    private static EntityObject parseJsonObject(JSONObject jsonObject) throws JSONException
    {
        EntityObject entityObject = new EntityObject(null);

        JSONArray jsonAttrs = jsonObject.names();
        for(int i = 0; i < jsonAttrs.length(); i++)
        {
            String attrName = jsonAttrs.getString(i);
            Attribute attribute = parseJsonObjectAttribute(jsonObject, attrName);

            entityObject.addAttribute(attribute);
        }

        return entityObject;
    }





    public static <T> EntityObject parseToPostStatus(T body)
    {
        JSONArray jsonArray = parseBodyToJSON(body, true);
        EntityObject record = null;

        try
        {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            record = parseJsonObject(jsonObject);
        }
        catch (JSONException e)
        {
            Log.e(TAG, "couldn't parse to post status: " + e.getMessage());
        }

        return record;
    }

    public static <T> EntityObject parseToRecord(T body, int index)
    {
        JSONArray jsonArray = parseBodyToJSON(body, false);
        EntityObject record = null;

        try
        {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            record = parseJsonObject(jsonObject);
        }
        catch (JSONException e)
        {
            Log.e(TAG, "couldn't parse json object to record: " + e.getMessage());
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }

        return record;
    }

    public static <T> ArrayList<EntityObject> parseToTable(T body)
    {
        JSONArray jsonArray = parseBodyToJSON(body, false);
        ArrayList<EntityObject> table = new ArrayList<EntityObject>();

        try
        {
            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                EntityObject record = parseJsonObject(jsonObject);

                table.add(record);
            }
        }
        catch (JSONException e)
        {
            Log.e(TAG, "couldn't parse json array to table: " + e.getMessage());
        }

        return table;
    }

}
