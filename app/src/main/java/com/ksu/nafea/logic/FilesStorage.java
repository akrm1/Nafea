package com.ksu.nafea.logic;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ksu.nafea.data.pool.NafeaStoragePool;
import com.ksu.nafea.data.request.QueryRequestFlag;
import com.ksu.nafea.logic.account.Student;
import com.ksu.nafea.logic.course.Course;
import com.ksu.nafea.logic.material.ElectronicMaterial;
import com.ksu.nafea.logic.material.PhysicalMaterial;
import com.ksu.nafea.utilities.NafeaFile;
import com.ksu.nafea.utilities.NafeaUtil;

import java.io.File;
import java.net.URI;

public class FilesStorage
{
    public static final String TAG = "FilesStorage";

    public static void uploadEMatFile(final Student student, final Course course, final String matName, final String matType, Uri fileUri, final QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        final NafeaFile<Task<Uri>> file = NafeaStoragePool.uploadFile(fileUri);

        if(file.getTask() == null)
            return;


        file.getTask().addOnCompleteListener(new OnCompleteListener<Uri>()
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                if(task.isSuccessful())
                {
                    String url = task.getResult() == null ? null : task.getResult().toString();
                    final ElectronicMaterial material = new ElectronicMaterial(0, matName, matType, url, file.getExtension());
                    ElectronicMaterial.insert(student, course, material, requestFlag);
                }
                else
                    Entity.sendFailureResponse(requestFlag, TAG, "Unable to upload \"" + matName + "\" material");
            }
        });

    }

    public static void uploadPMatFile(final Student student, final Course course, final PhysicalMaterial material, Uri fileUri, final QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        final NafeaFile<Task<Uri>> file = NafeaStoragePool.uploadFile(fileUri, null);

        if(file.getTask() == null)
            return;


        file.getTask().addOnCompleteListener(new OnCompleteListener<Uri>()
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                if(task.isSuccessful())
                {
                    String url = task.getResult() == null ? null : task.getResult().toString();
                    final PhysicalMaterial mat = new PhysicalMaterial(material.getId(), material.getName(), material.getSellerPhone(), url, material.getCity(), material.getPrice());
                    PhysicalMaterial.insert(student, course, mat, requestFlag);
                }
                else
                    Entity.sendFailureResponse(requestFlag, TAG, "Unable to upload \"" + material.getName() + "\" material");
            }
        });

    }

    public static void uploadMajorPlan(final Major major, Uri fileUri, final QueryRequestFlag<QueryPostStatus> requestFlag)
    {
        final NafeaFile<Task<Uri>> file = NafeaStoragePool.uploadFile(fileUri, null);

        if(file.getTask() == null)
            return;


        file.getTask().addOnCompleteListener(new OnCompleteListener<Uri>()
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                if(task.isSuccessful())
                {
                    String url = task.getResult() == null ? null : task.getResult().toString();
                    Major.updatePlan(major, url, requestFlag);
                }
                else
                    Entity.sendFailureResponse(requestFlag, TAG, "Unable to upload the selected plan to \"" + major.getName() + "\" major");
            }
        });

    }


    public static void watchVideo(Activity activity, String url) throws Exception
    {
        Uri uri = Uri.parse(url);
        Intent watchIntent = new Intent(Intent.ACTION_VIEW,uri);
        activity.startActivity(watchIntent);
    }


    public static void downloadFile(Activity activity, ElectronicMaterial material, String errorMsg)
    {
        final int PERMISSION_CODE = 1000;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            {
                //permission not granted, request it.
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //show popup for runtime permission
                activity.requestPermissions(permissions, PERMISSION_CODE);//DEFFER

                return;
            }
        }

        String description = "Downloading file...";
        try
        {
            startDownloading(activity, material.getUrl(), material.getName(), description);
        }
        catch (Exception e)
        {
            NafeaUtil.showToastMsg(activity, errorMsg);
        }
    }
    private static void startDownloading(Activity activity, String url, String title, String description)
    {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));


        File nafeaDir = new File("", "نافع");
        if (!nafeaDir.exists())
            nafeaDir.mkdir();

        //String date = DateFormat.getDateTimeInstance().format(new Date());
        //String file = activity.getString(R.string.app_name) + "-image-" + date.replace(" ", "").replace(":", "").replace(".", "") + extension;
        String extension = url.substring(url.lastIndexOf('.'));
        String fileName = title + extension;

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setTitle(title)
                .setDescription(description)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .allowScanningByMediaScanner();


        try
        {
            request.setDestinationInExternalPublicDir(nafeaDir + File.separator, fileName);
        }
        catch(Exception e)
        {
            request.setDestinationInExternalFilesDir(activity, nafeaDir + File.separator, fileName);
        }

        DownloadManager manager = (DownloadManager)activity.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        String msg = "بدء التحميل...";
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }


    public static boolean isPermissionProved(int requestCode, @NonNull int[] grantResults)
    {
        final int PERMISSION_CODE = 1000;

        if(requestCode == PERMISSION_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                return true;
        }

        return false;
    }





    public static void downloadFile(Activity activity, String url, String title, String errorMsg)
    {
        final int PERMISSION_CODE = 1000;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            {
                //permission not granted, request it.
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //show popup for runtime permission
                activity.requestPermissions(permissions, PERMISSION_CODE);//DEFFER

                return;
            }
        }


        String description = "Downloading file...";
        try
        {
            startNormalDownloading(activity, url, title, description);
        }
        catch (Exception e)
        {
            NafeaUtil.showToastMsg(activity, errorMsg);
        }
    }
    private static void startNormalDownloading(Activity activity, String url, String title, String description)
    {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));


        File nafeaDir = new File("", "نافع");
        if (!nafeaDir.exists())
            nafeaDir.mkdir();


        String extension = url.substring(url.lastIndexOf('.'));
        String fileName = title + extension;

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setTitle(title)
                .setDescription(description)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .allowScanningByMediaScanner();

        try
        {
            request.setDestinationInExternalPublicDir(nafeaDir + File.separator, fileName);
        }
        catch(Exception e)
        {
            request.setDestinationInExternalFilesDir(activity, nafeaDir + File.separator, fileName);
        }

        DownloadManager manager = (DownloadManager)activity.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        String msg = "بدء التحميل...";
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }




}
