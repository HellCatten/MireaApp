package com.example.mireaapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {




    private File path;
    private static Audience au = new Audience();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        ActivityCompat.requestPermissions( this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                }, 1
        );
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        //File path = context.getFilesDir();
        au.setPath(path);
        Log.i("MIREA_APP_TAG", String.valueOf(path));
        Thread thread = new Thread(au);
        thread.start();
        //fileDataBaseWorker();
    }



    public void fileDataBaseWorker() {
        ArrayList <String> listOfFileNames = au.getListOfFileNames();
        for (String n : listOfFileNames) {
            /*
            SQLiteOpenHelper sqLiteOpenHelper = new FilesDataBase(getApplicationContext(),
                "my_bd.db",
                null,
                1);
                */

            //SQLiteDatabase bd = sqLiteOpenHelper.getWritableDatabase();
            SQLiteDatabase bd = getApplicationContext().openOrCreateDatabase("my_db.db",MODE_PRIVATE,null);
            ContentValues cv = new ContentValues();
            cv.put("file_name", n);
            cv.put("is_usable", 1);
            bd.insert("TABLE_FILES",null,cv);
            bd.close();
        }

    }
}