package com.ksu.nafea.utilities;

public class InvalidFieldException extends Exception
{
    private String title;
    private String msg;

    public InvalidFieldException(String title, String msg)
    {
        this.title = title;
        this.msg = msg;
    }


    @Override
    public String getMessage()
    {
        return title + "\n" + msg;
    }
}
