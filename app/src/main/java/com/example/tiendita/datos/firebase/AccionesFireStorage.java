package com.example.tiendita.datos.firebase;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tiendita.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccionesFireStorage {
    public static void downloadImg(String remoteImage,
                                   Activity activity,
                                   Context context,DowloadCallback<Task<FileDownloadTask.TaskSnapshot>> dowloadCallback,
                                   String id,
                                   int UPDATE_TYPE)  {
        dowloadCallback.enInicioDesc();
        StorageReference imgRef = FirebaseStorage.getInstance().getReference().child(remoteImage);
        try {
            String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFile="JPEG_"+timeStamp+"_";
            File storageDir=activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File localFile = File.createTempFile(imageFile,".jpg",storageDir);
            imgRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        dowloadCallback.enExitoDesc(task, localFile);
                        AccionesFirebaseRTDataBase.updateLocalImgRef(id,localFile.getAbsolutePath(),UPDATE_TYPE);
                }
            });
        } catch (IOException e) {
            dowloadCallback.enFalloDesc(e);
        }
    }
}
