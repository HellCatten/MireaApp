package com.example.mireaapp;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Switch;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private Dialog dialog;

    private File path;
    private static Audience au = new Audience();

    private DBManager dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        //this.dbManager = new DBManager(new FilesDataBase(this, "my_database.db", null, 1));
        //this.dbManager.setPath(path);
        /*
        ActivityCompat.requestPermissions( this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                }, 1
        );
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
         */
        File path = context.getFilesDir();
        this.dbManager = new DBManager(new FilesDataBase(this, "my_database.db", null, 1));

        this.dbManager.setPath(path);
        dbManager.setContext(this);
        //dbManager.saveFilesToDatabase();
        Thread thread = new Thread(dbManager);
        thread.start();
        au.setPath(path);
        au.setContext(context);
        MaterialCalendarView cv = findViewById(R.id.calendarView);
        cv.setSelectedDate(CalendarDay.today());
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        dialog = new Dialog(this);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Аудитории");
        }
        Log.i("MIREA_APP_TAG", String.valueOf(path));
        //Thread thread = new Thread(au);
        //thread.start();
        //fileDataBaseWorker();
        //thread.start();
    }

    public void showCustomDialog() {
        dialog.setContentView(R.layout.settings_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        CheckBox checkBox1 = findViewById(R.id.checkBox1);
        CheckBox checkBox2 = findViewById(R.id.checkBox2);
        CheckBox checkBox3 = findViewById(R.id.checkBox3);
        CheckBox checkBox4 = findViewById(R.id.checkBox4);
        CheckBox checkBox5 = findViewById(R.id.checkBox5);
        CheckBox checkBox6 = findViewById(R.id.checkBox6);
        CheckBox checkBox7 = findViewById(R.id.checkBox7);
        CheckBox checkBox8 = findViewById(R.id.checkBox8);
        CheckBox checkBoxB78 = findViewById(R.id.checkBoxBuildingB78);
        CheckBox checkBoxB86 = findViewById(R.id.checkBoxBuildingB86);
        CheckBox checkBoxMP1 = findViewById(R.id.checkBoxBuildingMP1);
        CheckBox checkBoxC20 = findViewById(R.id.checkBoxBuildingC20);
        CheckBox checkBoxCG22 = findViewById(R.id.checkBoxBuildingCG22);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.simple_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                Log.i("MIREA_APP_TAG", "FILTER NORMAL");
                showCustomDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}