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
import java.util.Collections;
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

        deleteSchedule(listOfFilestoDelete);

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
        ArrayList<Integer> isDownloadedList = new ArrayList<>();
        Cursor dbCursor = db.rawQuery("select * from TABLE_FILES where file_name = '" + fileName + "'",null);
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
        cv.clear();
        Log.i("MIREA_APP_TAG", "added good file" + fileName);
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
        SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
        String where = "";
        for (int i = 0; i < fileNames.size(); i++) {
            if (i + 1 == fileNames.size()) {
                where += fileNames.get(i);
            } else {
                where += fileNames.get(i) + ", ";
            }
        }
        db.execSQL("DELETE FROM TABLE_AU_SCHEDULE WHERE file_name IN (" + where + ")");
        db.close();

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
        Cursor dbCursor = db.rawQuery("select * from TABLE_FILES where is_delete = 0", null);
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

    public ArrayList<Audience> loadScheduleFromDatabase(ArrayList<String> numbers, ArrayList<String> buildings, String day, int weekNumber) {
        if (numbers.isEmpty() || buildings.isEmpty()) {
            return null;
        } else {
            SQLiteDatabase db = this.sqLiteHelper.getWritableDatabase();
            ArrayList<Audience> ausFromFirstTable = new ArrayList<>();
            ArrayList<Audience> aus = new ArrayList<>();
            String whereNumbers = "";
            for (int i = 0; i < numbers.size(); i++) {
                if (i + 1 == numbers.size()) {
                    whereNumbers += numbers.get(i);
                } else {
                    whereNumbers += numbers.get(i) + ", ";
                }
            }
            String whereBuildings = "";
            for (int i = 0; i < buildings.size(); i++) {
                if (i + 1 == buildings.size()) {
                    whereBuildings += "'" + buildings.get(i) + "'";
                } else {
                    whereBuildings += "'" + buildings.get(i) + "', ";
                }
            }



            Cursor dbGetterCursor = db.rawQuery("select * from TABLE_AUDIENCES where building IN (" + whereBuildings + ")", null);

            if (dbGetterCursor.moveToFirst()) {
                do {
                    String building = dbGetterCursor.getString(dbGetterCursor.getColumnIndexOrThrow("building"));
                    String name = dbGetterCursor.getString(dbGetterCursor.getColumnIndexOrThrow("cab_name"));
                    Audience au = new Audience();
                    au.setBuilding(building);
                    au.setNameOfClass(name);
                    ausFromFirstTable.add(au);
                } while (dbGetterCursor.moveToNext());
            }
            dbGetterCursor.close();
            for (Audience au : ausFromFirstTable) {
                for (String para : numbers) {
                    Cursor dbCursor = db.rawQuery("select * from TABLE_AU_SCHEDULE where day = '" + day + "' and number_week = " + weekNumber + " and building = '" + au.getBuilding()
                            + "' and cab_name = '" + au.getNameOfClass() + "' and number_para = '" + para + "'", null);
                    if (dbCursor.getCount()==0) {
                        Audience auf = new Audience();
                        auf.setDay(day);
                        auf.setWeek(weekNumber);
                        auf.setBuilding(au.getBuilding());
                        auf.setNameOfClass(au.getNameOfClass());
                        auf.setNumOfClass(para);
                        aus.add(auf);
                    }
                    dbCursor.close();
                }
            }
            db.close();
            Collections.sort(aus);
            return aus;
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