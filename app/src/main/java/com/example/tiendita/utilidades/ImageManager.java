package com.example.tiendita.utilidades;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.tiendita.R;

import java.io.File;

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
