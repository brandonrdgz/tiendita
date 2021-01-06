package com.example.tiendita.utilidades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.tiendita.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageManager {



    public static  void loadImage(String localImg, ImageView view, Context context){
        File filePhoto= new File(localImg);
        try{
            Uri photoUri= FileProvider.getUriForFile(context,"com.example.tiendita",filePhoto);
            view.setImageURI(photoUri);
        }catch(Exception ex){
            Toast.makeText(context, R.string.error_cargar_img,Toast.LENGTH_LONG).show();
            Log.d("Cargar imagen","Error al cargar la imagen: "+localImg+"\n Mensaje: "+ex.getMessage()+"\n Causa: "+ex.getCause());
        }
    }



}
