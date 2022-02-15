package com.ksu.nafea.data.request;
import java.util.ArrayList;

public class FailureResponse
{
    private ArrayList<String> mainQueries;
    private ArrayList<String> attachedQueries;
    private ArrayList<String> stackTrace;
    private String msg;


    public FailureResponse(ArrayList<String> mainQueries, ArrayList<String> attachedQueries, String starter, String msg)
    {
        this.mainQueries = mainQueries;
        this.attachedQueries = attachedQueries;
        this.stackTrace = new ArrayList<String>();
        this.msg = msg;

        stackTrace.add(starter);
    }


    public void addNode(String node)
    {
        this.stackTrace.add(node);
    }

    public String getStackTrace()
    {
        String stack = "";
        for(int i = stackTrace.size() - 1; i >= 0;  i--)
        {
            stack += stackTrace.get(i);
            stack += i > 0 ? "\\" : "";
        }

        return stack;
    }


    public String getMainQueries()
    {
        String queries = "";
        for(int i = 0; i < mainQueries.size(); i++)
        {
            queries += mainQueries.get(i) + ";";
        }

        return queries;
    }
    public String getAttachedQueries()
    {
        String queries = "";

        int attachedLength = attachedQueries.size();
        for(int i = 0; i < attachedLength; i++)
        {
            queries += attachedQueries.get(i);
            if(i < (attachedLength - 1))
                queries += ",";
        }

        return queries;
    }

    public String getQueryRequestFormat()
    {
        return getMainQueries() + "?attach=" + getAttachedQueries();
    }


    @Override
    public String toString()
    {
        String string = "QueryRequest:\n{\nMain Queries:\n";

        int mainLength = mainQueries.size();
        for(int i = 0; i < mainLength; i++)
        {
            string += "  [" + i + "]:" + mainQueries.get(i);
            if(i < (mainLength - 1))
                string += ",\n";
        }
        string += "\nAttached Queries:\n";


        int attachedLength = attachedQueries.size();
        for(int i = 0; i < attachedLength; i++)
        {
            string += "  [" + i + "]:" + attachedQueries.get(i);
            if(i < (attachedLength - 1))
                string += ",\n";
        }
        string += "\n}";

        return string;
    }



    //----------------------------------[Getters & Setters]----------------------------------
    public String getMsg() {
        return msg;
    }
}
