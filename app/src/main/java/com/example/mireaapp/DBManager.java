package com.example.mireaapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteAccessPermException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DBManager implements Runnable {

    private SQLiteOpenHelper sqLiteHelper;

    private ArrayList<String> listOfFilesToDownload = Audience.getListOfFilesToDownload();

    private ArrayList<String> listOfFiles = Audience.getListOfNamesFromListOfLinks(listOfFilesToDownload);
    private ArrayList<String> listOfFilestoDelete;

    private static File path;

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

        for(int i = 0; i < this.listOfFiles.size(); i++ ) {
            saveFileToDatabase(listOfFiles.get(i), listOfFilesToDownload.get(i));
        }

        listOfFilestoDelete = loadAllFilestoDeleteFromDatabase();

        deleteSchedule(listOfFilestoDelete);

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

        ArrayList<String> listOfFiles = loadListOfFileNamesUndownloadedFromDatabase();
        this.listOfFiles = listOfFiles;
        ArrayList<String> listOfFilestoDownload = loadListOfFileDownloadsUndownloadedFromDatabase();
        this.listOfFilesToDownload = listOfFilestoDownload;
        for (int i = 0; i < listOfFiles.size(); i++) {
            Audience.downloadlistOfFiles(listOfFilestoDownload,listOfFiles, path);
        }

        changeDownloadFileInDatabase();

        Audience au = new Audience();
        au.work(listOfFiles, path);
        HashMap<String, Audience> hashMap = au.getAudienceHashMap();

        saveHashMapSimpleAudienceTodatabase(hashMap);
    }

    public void changeDownloadFileInDatabase() {
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        db.execSQL("UPDATE TABLE_FILES SET id_downloaded = 1", null);
        db.close();
    }

    public boolean saveFileToDatabase(String fileName, String loadName) {
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        /*
        ContentValues cv = new ContentValues();
        cv.put("file_name", fileName);
        cv.put("is_downloaded", 0);
        cv.put("is_delete",1);
        cv.put("file_load", loadName);
        long rowId = db.replace("TABLE_FILES", null, cv);
        cv.clear();
        db.close();
        try {
            Audience.downloadOneFile(loadName, fileName, path);
        }catch (IOException ioe) {

        }
         */

        db.execSQL("INSERT OR REPLACE INTO TABLE_FILES (file_name, is_downloaded, is_delete, file_load)" +
                " VALUES (" + fileName + ", " + "(SELECT is_downloaded FROM TABLE_FILES WHERE file_name = " + fileName + "), " +
                "1, " + loadName + ");");
        return true;
    }

    public void initializeFilesInDatabase() {
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        db.execSQL("UPDATE TABLE_FILES SET is_delete = 0", null);
        db.close();
    }

    public void deleteUnusedFilesFromDatabase() {
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        db.delete("TABLE_FILES","is_delete = 0", null);
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


    public ArrayList<String> loadListOfFileNamesUndownloadedFromDatabase() {
        ArrayList<String> files = new ArrayList<>();
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        Cursor dbCursor = db.query("TABLE_FILES",
                null,"is_downloaded = NULL",null,
                null,null,null);
        if (dbCursor.moveToFirst()) {
            do{
                String fileName = dbCursor.getString(dbCursor.getColumnIndexOrThrow("file_name"));
                String fileDownload = dbCursor.getString(dbCursor.getColumnIndexOrThrow("file_load"));
                files.add(fileName);
            } while (dbCursor.moveToNext());
        }
        dbCursor.close();
        db.close();
        return files;
    }

    public ArrayList<String> loadListOfFileDownloadsUndownloadedFromDatabase() {
        ArrayList<String> files = new ArrayList<>();
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        Cursor dbCursor = db.query("TABLE_FILES",
                null,"is_downloaded = NULL",null,
                null,null,null);
        if (dbCursor.moveToFirst()) {
            do{

                String fileDownload = dbCursor.getString(dbCursor.getColumnIndexOrThrow("file_load"));
                files.add(fileDownload);
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

    public boolean saveRaspAudienceToDatabase(Audience au) {
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("file_name", au.getFileName());
        cv.put("cab_name", au.getNameOfClass());
        cv.put("building", au.getBuilding());
        cv.put("campus", au.getCampus());
        cv.put("number_para", au.getNumOfClass());
        cv.put("day", au.getDay());
        cv.put("number_week", au.getWeek());
        long rowId = db.insert("TABLE_AU_SCHEDULE", null, cv);
        cv.clear();
        db.close();
        return rowId != -1;
    }

    @Override
    public void run() {
        saveFilesToDatabase();
    }


}