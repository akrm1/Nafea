package com.ksu.nafea.logic;

import com.ksu.nafea.logic.account.UserAccount;
import com.ksu.nafea.logic.course.Course;
import com.ksu.nafea.logic.material.Material;

public class User
{
    public static UserAccount userAccount = null;

    public static University university = null;
    public static College college = null;
    public static Major major = null;
    public static Course course = null;
    public static boolean isBrowsing = true;

    public static Major managingMajor = null;
    public static boolean isAddingCourse = false;
    public static boolean isRemovingCourse = false;
    public static boolean isUploadDepPlan = false;;
    public static boolean isUserInfoUpdated = false;

    public static Material material;
}
