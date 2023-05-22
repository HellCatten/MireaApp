package com.example.mireaapp;

import static java.sql.Types.NULL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteAccessPermException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DBManager implements Runnable {

    private SQLiteOpenHelper sqLiteHelper;

    private ArrayList<String> listOfFilesToDownload;

    private ArrayList<String> listOfFiles;
    private ArrayList<String> listOfFilestoDelete;
    //private SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();

    private Context context;
    private static File path;

    public void setContext(Context context) {
        this.context = context;
    }

    public static File getPath() {
        return path;
    }

    public static void setPath(File path) {
        DBManager.path = path;
    }

    public DBManager(SQLiteOpenHelper sqLiteHelper) {
        this.sqLiteHelper = sqLiteHelper;
    }

    public void saveFilesToDatabase() {

        initializeFilesInDatabase();

        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        for(int i = 0; i < listOfFiles.size(); i++ ) {
            saveFileToDatabase(listOfFiles.get(i), listOfFilesToDownload.get(i), db);
            //Log.i("MIREA_APP_TAG", "Try save " + listOfFiles.get(i) + " " + listOfFilesToDownload.get(i));
        }
        db.close();
        listOfFilestoDelete = loadAllFilestoDeleteFromDatabase();

        //deleteSchedule(listOfFilestoDelete);

        deleteUnusedFilesFromDatabase();

        deleteFile(listOfFilestoDelete);

        saveAndLoadFilesTODatabase();



    }

    public void deleteFile(ArrayList<String> filestoDelete) {
        for (String fileName : filestoDelete) {
            File file = new File(path, fileName);
            file.delete();
        }
    }


    public void saveAndLoadFilesTODatabase() {

        ArrayList<Myfile> listOfFiles = loadListOfFileNamesUndownloadedFromDatabase();
        //listOfFiles = listOfFiles;
        ArrayList<String> listNamesOfFiles = new ArrayList<>();
        ArrayList<String> listOfURL = new ArrayList<>();
        for(Myfile f : listOfFiles) {
            listNamesOfFiles.add(f.getFileName());
            listOfURL.add(f.getURL());
        }

        //listOfFilesToDownload = listOfFilestoDownload;

        Audience.downloadlistOfFiles(listOfURL,listNamesOfFiles, path);


        changeDownloadFileInDatabase();

        Audience au = new Audience();
        au.setContext(context);
        au.setSqLiteOpenHelper(sqLiteHelper);
        au.work(listNamesOfFiles, path);
        HashMap<String, Audience> hashMap = au.getAudienceHashMap();

        saveHashMapSimpleAudienceTodatabase(hashMap);
    }

    public void changeDownloadFileInDatabase() {
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        db.execSQL("UPDATE TABLE_FILES SET is_downloaded = 1");
        db.close();
    }

    public boolean saveFileToDatabase(String fileName, String loadName, SQLiteDatabase db) {
        //SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        ArrayList<Integer> isDownloadedList = new ArrayList<>();

        //SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        Cursor dbCursor = db.rawQuery("select * from TABLE_FILES where file_name = '" + fileName + "'",null);
            //String selection = "file_name = " + fileName;
        /*
            Cursor dbCursor = db.query("TABLE_FILES",
                    null, "file_name = ?", new String[]{fileName},
                    null, null, null);

                    */
        ContentValues cv = new ContentValues();
        if (dbCursor.moveToFirst()) {
            do {
                if (dbCursor.isNull(3)) {
                    cv.putNull("is_downloaded");
                } else {
                    int fileDownloaded = dbCursor.getInt(dbCursor.getColumnIndexOrThrow("is_downloaded"));
                    cv.put("is_downloaded", fileDownloaded);
                }
            } while (dbCursor.moveToNext());
        } else {
            cv.putNull("is_downloaded");
        }
        dbCursor.close();
        cv.put("file_name", fileName);
        cv.put("is_delete", 1);
        cv.put("file_load", loadName);
        db.replace("TABLE_FILES", null, cv);
        //db.execSQL("INSERT OR REPLACE INTO TABLE_FILES (file_name, is_downloaded, is_delete, file_load)" +
        //      " VALUES (" + fileName + ", " + isDownloaded + ", 1," + loadName + ");");
        cv.clear();
        //db.close();
        Log.i("MIREA_APP_TAG", "added good file" + fileName);



            /*
            SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
            Log.i("MIREA_APP_TAG", "added bad File" + fileName);
            ContentValues cv = new ContentValues();
            cv.put("file_name", fileName);
            cv.put("is_downloaded", NULL);
            cv.put("is_delete", 1);
            cv.put("file_load", loadName);
            db.insertOrThrow("TABLE_FILES", null, cv);
            cv.clear();
            db.close();
             */
        /*
        db.execSQL("INSERT OR REPLACE INTO TABLE_FILES (file_name, is_delete, file_load, is_downloaded)" +
                " VALUES ( " + fileName + ", 1, " + loadName + ", " +
                "(SELECT is_downloaded FROM TABLE_FILES WHERE file_name = " + fileName + "));");

         */
        /*
        try {
            db.execSQL("INSERT OR REPLACE INTO TABLE_FILES (file_name, is_downloaded, is_delete, file_load)" +
                    " VALUES (" + fileName + ", " + "(SELECT is_downloaded FROM TABLE_FILES WHERE file_name = " + fileName + "), " +
                    "1, " + loadName + ");");
            db.close();
        } catch (SQLiteException e) {
            ContentValues cv = new ContentValues();
            cv.put("file_name", fileName);
            cv.put("is_downloaded", isDownloaded);
            cv.put("is_delete",1);
            cv.put("file_load", loadName);
            long rowId = db.replace("TABLE_FILES", null, cv);
            cv.clear();
            db.close();
        }
         */

        return true;
    }

    public void initializeFilesInDatabase() {
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        try {
            db.execSQL("UPDATE TABLE_FILES SET is_delete = 0", null);

        }catch (IllegalArgumentException iae) {
            Log.i("MIREA_APP_TAG", "ERROR");
        } finally {
            db.close();
        }
    }

    public void deleteUnusedFilesFromDatabase() {
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        db.delete("TABLE_FILES","is_delete = ?", new String[]{"0"});
        db.close();
    }

    public void deleteSchedule(ArrayList<String> fileNames) {
        for (String i : fileNames) {
            SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
            String where = "file_name = " + i;
            db.delete("TABLE_AU_SCHEDULE", where,null);
            db.close();
        }
    }


    public ArrayList<Myfile> loadListOfFileNamesUndownloadedFromDatabase() {
        ArrayList<Myfile> files = new ArrayList<>();
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        Cursor dbCursor = db.rawQuery("select * from TABLE_FILES where is_downloaded is NULL",null);
        //Cursor dbCursor = db.query("TABLE_FILES",
        //        null,"is_downloaded = ?",new String[](NULL),
        //        null,null,null);
        if (dbCursor.moveToFirst()) {
            do{
                String fileName = dbCursor.getString(dbCursor.getColumnIndexOrThrow("file_name"));
                String fileDownload = dbCursor.getString(dbCursor.getColumnIndexOrThrow("file_load"));
                Myfile file = new Myfile(fileName, fileDownload);
                files.add(file);
            } while (dbCursor.moveToNext());
        }
        dbCursor.close();
        db.close();
        return files;
    }


    public ArrayList<String> loadAllFilestoDeleteFromDatabase() {
        ArrayList<String> files = new ArrayList<>();
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        Cursor dbCursor = db.query("TABLE_FILES",
                null,"is_delete = 0",null,
                null,null,null);
        if (dbCursor.moveToFirst()) {
            do{
                String fileName = dbCursor.getString(dbCursor.getColumnIndexOrThrow("file_name"));
                files.add(fileName);
            } while (dbCursor.moveToNext());
        }
        dbCursor.close();
        db.close();
        return files;
    }

    public void saveHashMapSimpleAudienceTodatabase(HashMap<String, Audience> hashMap) {
        for (HashMap.Entry<String, Audience> set : hashMap.entrySet()) {
            saveSimpleAudienceToDatabase(set.getValue());
        }
    }

    public boolean saveSimpleAudienceToDatabase(Audience au) {
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("file_name", au.getFileName());
        cv.put("cab_name", au.getNameOfClass());
        cv.put("building", au.getBuilding());
        cv.put("campus", au.getCampus());
        long rowId = db.replace("TABLE_AUDIENCES", null, cv);
        cv.clear();
        db.close();
        return rowId != -1;
    }

    public void saveRaspAudienceToDatabase(Audience au, SQLiteOpenHelper sqLiteHelper) {
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("file_name", au.getFileName());
        cv.put("cab_name", au.getNameOfClass());
        cv.put("building", au.getBuilding());
        if (au.getCampus() == null) {
            cv.putNull("campus");
        } else {
            cv.put("campus", au.getCampus());
        }
        //cv.put("campus", au.getCampus());
        cv.put("number_para", au.getNumOfClass());
        cv.put("day", au.getDay());
        cv.put("number_week", au.getWeek());
        long rowId = db.insert("TABLE_AU_SCHEDULE", null, cv);
        cv.clear();
        db.close();
        //return rowId != -1;
    }

    @Override
    public void run() {
        listOfFilesToDownload = Audience.getListOfFilesToDownload();
        listOfFiles = Audience.getListOfNamesFromListOfLinks(listOfFilesToDownload);
        saveFilesToDatabase();
    }



}

class Myfile {
    private String fileName;
    private String URL;

    public Myfile(String fileName, String URL) {
        this.fileName = fileName;
        this.URL = URL;
    }

    public String getFileName() {
        return fileName;
    }

    public String getURL() {
        return URL;
    }
}