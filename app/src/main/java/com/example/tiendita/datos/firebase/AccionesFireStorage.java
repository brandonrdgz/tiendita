package com.example.tiendita.datos.firebase;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AccionesFireStorage {
    public static void downloadImg(String remoteImage,
                                   Activity activity,
                                   Context context,
                                   DownloadCallback<Task<FileDownloadTask.TaskSnapshot>> downloadCallback,
                                   String id)  {
        downloadCallback.enInicioDesc();
        StorageReference imgRef = FirebaseStorage.getInstance().getReference().child(remoteImage);
        try {
            String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFile="JPEG_"+timeStamp+"_";
            File storageDir=activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File localFile = File.createTempFile(imageFile,".jpg",storageDir);
            imgRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if(AccionesFirebaseRTDataBase.getLocalImgRef(id,context)!=null) {
                            AccionesFirebaseRTDataBase.updateLocalImgRef(id, localFile.getAbsolutePath(), context);
                        }else{
                            AccionesFirebaseRTDataBase.insertLocalImgRef(id, localFile.getAbsolutePath(), context);
                        }
                        downloadCallback.enExitoDesc(task, localFile);
                }
            });
        } catch (IOException e) {
            downloadCallback.enFalloDesc(e);
        }
    }

    public static void updateImage(String UID, String oldRemoteImg, String localImg, Context context, UploadCallback<Task<Uri>> uploadCallback) {
        uploadCallback.enInicioCar();
        Uri file =Uri.fromFile(new File(localImg));
        StorageReference imgRef = FirebaseStorage.getInstance().getReference();
        final StorageReference ref = imgRef
                .child(UID+"/"+file.getLastPathSegment());
        StorageTask<UploadTask.TaskSnapshot> uploadTask = ref.putFile(file);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    uploadCallback.enFalloCar(null);
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    uploadCallback.enExitoCar(task);
                } else {
                    uploadCallback.enFalloCar(null);
                }
            }
        });
        imgRef
                .child(oldRemoteImg)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    uploadCallback.enFalloCar(null);
                }

            }
        });
    }
    public static void loadImage(String UID,  String localImg, Context context, UploadCallback<Task<Uri>> uploadCallback) {
        uploadCallback.enInicioCar();
        Uri file =Uri.fromFile(new File(localImg));
        StorageReference imgRef = FirebaseStorage.getInstance().getReference();
        final StorageReference ref = imgRef
                .child(UID+"/"+file.getLastPathSegment());
        StorageTask<UploadTask.TaskSnapshot> uploadTask = ref.putFile(file);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    uploadCallback.enFalloCar(null);
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    uploadCallback.enExitoCar(task);
                } else {
                    uploadCallback.enFalloCar(null);
                }
            }
        });
    }

}
