package com.ksu.nafea.data.pool;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksu.nafea.utilities.NafeaFile;
import java.util.UUID;

public class NafeaStoragePool
{
    public static final String TAG = "StoragePool";
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public static NafeaFile<Task<Uri>> uploadFile(Uri fileUri)
    {
        final NafeaFile<Task<Uri>> file = new NafeaFile<Task<Uri>>(fileUri);

        if(file.getUri() != null)
        {
            String folder = "Documents/";
            String path = folder + file.getName() + "_" + UUID.randomUUID() + file.getExtension();
            final StorageReference firebaseRef = storage.getReference(path);

            UploadTask uploadTask = firebaseRef.putFile(file.getUri());

            Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if(!task.isSuccessful())
                        throw  task.getException();

                    return firebaseRef.getDownloadUrl();
                }
            });

            file.setTask(getDownloadUriTask);
        }

        return file;
    }

    public static NafeaFile<Task<Uri>> uploadFile(Uri fileUri, String extension)
    {
        final NafeaFile<Task<Uri>> file = new NafeaFile<Task<Uri>>(fileUri, extension);

        if(file.getUri() != null)
        {
            String folder = "Other/";
            String path = folder + file.getName() + "_" + UUID.randomUUID() + file.getExtension();
            final StorageReference firebaseRef = storage.getReference(path);

            UploadTask uploadTask = firebaseRef.putFile(file.getUri());

            Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if(!task.isSuccessful())
                        throw  task.getException();

                    return firebaseRef.getDownloadUrl();
                }
            });

            file.setTask(getDownloadUriTask);
        }

        return file;
    }


    public static void downloadFile()
    {

    }


}
