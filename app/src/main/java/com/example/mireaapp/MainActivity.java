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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private Dialog dialog;

    private static CheckBox checkBox1;
    private static CheckBox checkBox2;
    private static CheckBox checkBox3;
    private static CheckBox checkBox4;
    private static CheckBox checkBox5;
    private static CheckBox checkBox6;
    private static CheckBox checkBox7;
    private static CheckBox checkBox8;
    private static CheckBox checkBoxB78;
    private static CheckBox checkBoxB86;
    private static CheckBox checkBoxMP1;
    private static CheckBox checkBoxC20;
    private static CheckBox checkBoxCG22;

    private File path;
    private String day;
    private int weekNumber;

    private ArrayList <String> numbers;
    private ArrayList <String> buildings;

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
        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        CalendarDay cd = CalendarDay.today();
        calendarView.setSelectedDate(CalendarDay.today());
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        dialog = new Dialog(this);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Аудитории");
        }
        Log.i("MIREA_APP_TAG", String.valueOf(path));
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {

            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay calendarDate, boolean selected) {
                Log.i("MIREA_APP_TAG", "Day " + calendarDate);
                Date date = calendarDate.getDate();
                Log.i("MIREA_APP_TAG", "Day " + dayName(date));
                day = dayName(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setFirstDayOfWeek(6);
                calendar.setMinimalDaysInFirstWeek(7);
                calendar.set(date.getYear(), date.getMonth(), date.getDate());
                Log.i("MIREA_APP_TAG", String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
                int num = calendar.get(Calendar.WEEK_OF_YEAR);
                if (num % 2 == 0 && ((num - 5 >= 1 || num - 5 <= 16) || (num >= 35 || num - 5 < 51))) {
                    weekNumber = 2;
                } else if ((num % 2 != 0 && ((num - 5 >= 1 || num - 5 <= 16) || (num >= 35 || num - 5 < 51)))) {
                    weekNumber = 1;
                }
                loadShedule();
            }
        });
    }



    public String dayName (Date d) {
        DateFormat f = new SimpleDateFormat("EEEE");
        try {
            return f.format(d);
        } catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void showCustomDialog() {
        dialog.setContentView(R.layout.settings_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        //setContentView(R.layout.settings_dialog_layout);
        checkBox1 = dialog.findViewById(R.id.checkBox1);
        checkBox2 = dialog.findViewById(R.id.checkBox2);
        checkBox3 = dialog.findViewById(R.id.checkBox3);
        checkBox4 = dialog.findViewById(R.id.checkBox4);
        checkBox5 = dialog.findViewById(R.id.checkBox5);
        checkBox6 = dialog.findViewById(R.id.checkBox6);
        checkBox7 = dialog.findViewById(R.id.checkBox7);
        checkBox8 = dialog.findViewById(R.id.checkBox8);
        checkBoxB78 = dialog.findViewById(R.id.checkBoxBuildingB78);
        checkBoxB86 = dialog.findViewById(R.id.checkBoxBuildingB86);
        checkBoxMP1 = dialog.findViewById(R.id.checkBoxBuildingMP1);
        checkBoxC20 = dialog.findViewById(R.id.checkBoxBuildingC20);
        checkBoxCG22 = dialog.findViewById(R.id.checkBoxBuildingCG22);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                numbers = new ArrayList<>();
                buildings = new ArrayList<>();
                if (checkBox1.isChecked()) {
                    numbers.add("1");
                }
                if (checkBox2.isChecked()) {
                    numbers.add("2");
                }
                if (checkBox3.isChecked()) {
                    numbers.add("3");
                }
                if (checkBox4.isChecked()) {
                    numbers.add("4");
                }
                if (checkBox5.isChecked()) {
                    numbers.add("5");
                }
                if (checkBox6.isChecked()) {
                    numbers.add("6");
                }
                if (checkBox7.isChecked()) {
                    numbers.add("7");
                }
                if (checkBox8.isChecked()) {
                    numbers.add("8");
                }
                if (checkBoxB78.isChecked()) {
                    buildings.add("В-78");
                }
                if (checkBoxB86.isChecked()) {
                    buildings.add("В-86");
                }
                if (checkBoxC20.isChecked()) {
                    buildings.add("С-20");
                }
                if (checkBoxMP1.isChecked()) {
                    buildings.add("МП-1");
                }
                if (checkBoxCG22.isChecked()) {
                    buildings.add("СГ-22");
                }
                Log.i("MIREA_APP_TAG", String.valueOf(numbers));
                Log.i("MIREA_APP_TAG", String.valueOf(buildings));
                loadShedule();
            }
        });
    }

    public void loadShedule() {
        try {
            if (!numbers.isEmpty() && !day.isEmpty() && !buildings.isEmpty()) {
                RecyclerView rcView = findViewById(R.id.recyclerView);
                ArrayList<Audience> aus = dbManager.loadScheduleFromDatabase(numbers, buildings, day, weekNumber);
                if (aus != null) {
                    AudienceAdapter adapter = new AudienceAdapter(aus);
                    rcView.setLayoutManager(new LinearLayoutManager(this,
                            LinearLayoutManager.VERTICAL, false));
                    rcView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "Неправильный ввод параметров", Toast.LENGTH_LONG).show();
                }
            }
        }catch (NullPointerException npe) {
            Toast.makeText(this, "Неправильный ввод параметров", Toast.LENGTH_LONG).show();
        }
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