package com.ksu.nafea.utilities;

import android.net.Uri;

public class NafeaFile<Task>
{
    private String name;
    private String extension;
    private String path;
    private Uri uri;
    private Task task;

    public NafeaFile(Uri fileUri)
    {
        uri = fileUri;
        if(uri != null)
        {
            path = uri.getPath();

            int nameFirstIndex = path.lastIndexOf('/') + 1;
            name = path.substring(nameFirstIndex);

            int extFirstIndex = name.indexOf('.');
            extension = name.substring(extFirstIndex);

            name = name.substring(0, extFirstIndex);
        }
        else
        {
            name = null;
            extension = null;
            path = null;
            uri = null;
            task = null;
        }

    }
    public NafeaFile(Uri fileUri, String extension)
    {
        uri = fileUri;
        if(uri != null)
        {
            path = uri.getPath();

            int nameFirstIndex = path.lastIndexOf('/') + 1;
            name = path.substring(nameFirstIndex);

            this.extension = extension;
        }
        else
        {
            name = null;
            this.extension = null;
            path = null;
            uri = null;
            task = null;
        }

    }




    public String getName()
    {
        return name;
    }

    public String getExtension()
    {
        return extension != null ? extension : "";
    }

    public String getPath()
    {
        return path;
    }

    public Uri getUri()
    {
        return uri;
    }

    public void setTask(Task task)
    {
        this.task = task;
    }

    public Task getTask()
    {
        return task;
    }


}
