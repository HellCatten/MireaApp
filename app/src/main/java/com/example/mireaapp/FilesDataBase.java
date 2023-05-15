package com.example.mireaapp;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FilesDataBase extends SQLiteOpenHelper {


    public FilesDataBase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public FilesDataBase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }



    @Override
    public void onCreate(SQLiteDatabase bd) {
        bd.execSQL("create table " + "TABLE_FILES" + " ("
                + "_id integer primary key autoincrement,"
                + "file_name text,"
                + "is_usable integer"
                + ");");
        bd.execSQL("create table " + "TABLE_AUDIENCES" + " ("
                + "_id integer primary key autoincrement,"
                + "cab_name text,"
                + "building text,"
                + "campus text"
                + ");");
        bd.execSQL("create table " + "TABLE_RASP" + " ("
                + "_id integer primary key autoincrement,"
                + "cab_name text,"
                + "building text,"
                + "campus text,"
                + "number_h text,"
                + "day text,"
                + "number_week text,"
                + "teacher text"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
