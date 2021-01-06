package com.example.tiendita.datos.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Sql extends SQLiteOpenHelper {
    private static final String dataBase="LOCALIMG";
    private static final int VERSION=1;
    private final String tImg="CREATE TABLE LOCALIMG (" +
            "ID TEXT PRIMARY KEY NOT NULL ,"+
            "IMAGEN TEXT NOT NULL);";

    public Sql(Context context){
        super(context, dataBase,null,VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(tImg);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i1>i){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS LOCALIMG");
            sqLiteDatabase.execSQL(tImg);
        }
    }
}
