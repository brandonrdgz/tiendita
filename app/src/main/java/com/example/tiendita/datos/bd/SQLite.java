package com.example.tiendita.datos.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.util.Log;

import java.util.ArrayList;

public class SQLite {
    private Sql sql;
    private SQLiteDatabase db;

    public SQLite(Context context){
        sql=new Sql(context);
    }

    public void abrir(){
        Log.i("SQLite", "Se abre conexion con BD "+sql.getDatabaseName());
        db=sql.getWritableDatabase();
    }
    public void cerrar(){
        Log.i("SQLite", "Se cierra conexion con BD "+sql.getDatabaseName());
        sql.close();
    }

public boolean insertRef(
    String id,
    String imagenPath){
        ContentValues cv=new ContentValues();
        cv.put("ID",id);
        cv.put("IMAGEN",imagenPath);
        return (db.insert("LOCALIMG",null, cv)!=-1);

}

    public boolean actualizaRef(
            String id,
            String imagenPath){
                ContentValues cv=new ContentValues();
                cv.put("IMAGEN",imagenPath);
                return (db.update("LOCALIMG",cv,"ID = '"+id+"'",null)!=-1);
         }

         public String getImgRef(String id){
             String[] args = new String[] {id};
             Cursor cursor = db.rawQuery("SELECT IMAGEN FROM LOCALIMG WHERE ID=?",args);
             if(cursor.moveToFirst()) {
                 return cursor.getString(0);
             }else{
                 return null;
             }
         }

         public int deleteRef(String id){
            return db.delete("LOCALIMG","ID="+id,null);
         }

}
