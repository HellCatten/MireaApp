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

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private Dialog dialog;

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
        Thread thread = new Thread(au);
        thread.start();
        //fileDataBaseWorker();
    }

    public void showCustomDialog() {
        dialog.setContentView(R.layout.settings_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
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