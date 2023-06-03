package com.example.mireaapp;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {



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

    private ArrayList<Audience> aus;

    private Handler handler = new Handler(Looper.getMainLooper());

    private File path;
    private String day;
    private int weekNumber;

    private ArrayList <String> checkNumbers;
    private ArrayList <String> buildings;

    private static Audience au = new Audience();

    private DBManager dbManager;

    private MaterialCalendarView calendarView;

    private Toolbar tb;
    private RecyclerView recyclerView;

    private Dialog progressDialog;

    private static Thread thread;
    private boolean animationStarted = false;

    public static Thread getThread() {
        return thread;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.Theme_MireaApp_NoActionBar);
        //getWindow().getDecorView().setSystemUiVisibility(
        //        View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
        thread = new Thread(dbManager);
        //thread.start();
        Dialog pd = new Dialog(this);
        pd.setContentView(R.layout.progress_dialog);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        TextView name = pd.findViewById(R.id.progress_status);
        name.setText("Загрузка базы данных");
        pd.show();
        pd.dismiss();
        //setProgressDialog();

        au.setPath(path);
        au.setContext(context);
        calendarView = findViewById(R.id.calendarView);
        CalendarDay cd = CalendarDay.today();
        updateDay(cd);
        calendarView.setSelectedDate(CalendarDay.today());
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        //dialog = new Dialog(this);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Аудитории");
        }
        Log.i("MIREA_APP_TAG", String.valueOf(path));
        //runSchedule();



        //threadWaitToLoadDatabase.start();
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
                if (((num-5) % 2) == 0 && ((num - 4 >= 1 || num - 4 <= 16) || (num >= 35 || num <= 51))) {
                    weekNumber = 2;
                } else if ((num-5) % 2 != 0 && ((num - 4 >= 1 || num - 4 <= 16) || (num >= 35 || num - 5 <= 51))) {
                    weekNumber = 1;
                }
                Log.i("MIREA_APP_TAG",String.valueOf(weekNumber));
                runSchedule();
            }
        });
    }



    private void animate() {
        ImageView logoImageView = (ImageView) findViewById(R.id.img_logo);
        ViewGroup container = (ViewGroup) findViewById(R.id.container);

        ViewCompat.animate(logoImageView)
                .translationY(-250)
                .setStartDelay(300)
                .setDuration(1000).setInterpolator(
                        new DecelerateInterpolator(1.2f)).start();

        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            ViewPropertyAnimatorCompat viewAnimator;

            if (!(v instanceof Button)) {
                viewAnimator = ViewCompat.animate(v)
                        .translationY(50).alpha(1)
                        .setStartDelay((300 * i) + 500)
                        .setDuration(1000);
            } else {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1).scaleX(1)
                        .setStartDelay((300 * i) + 500)
                        .setDuration(500);
            }

            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    public void updateDay(CalendarDay calendarDate) {
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
        if (((num-5) % 2) == 0 && ((num - 4 >= 1 || num - 4 <= 16) || (num >= 35 || num <= 51))) {
            weekNumber = 2;
        } else if ((num-5) % 2 != 0 && ((num - 4 >= 1 || num - 4 <= 16) || (num >= 35 || num - 5 <= 51))) {
            weekNumber = 1;
        }
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

    public void runSchedule() {
        try {
            aus.clear();
        }catch (NullPointerException npe) {

        }
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Thread t = new Thread(() -> {

            aus = loadArrayListOfScheduleFromDatadase();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(aus != null) {
                        updateRecyclerViewByArrayList(aus);
                        progressDialog.dismiss();
                    }
                }
            });
        });
        t.start();
    }
    public void saveSwitchState(String name, boolean state) {
        SharedPreferences preferences = getSharedPreferences("my_app_prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(name, state);
        editor.apply();
    }

    public boolean getSwitchValuesFromPrefs (String name) {
        SharedPreferences preferences = getSharedPreferences("my_app_prefs",MODE_PRIVATE);
        boolean state = preferences.getBoolean(name, false);
        return state;
    }

    public void showCustomDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.settings_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog.show();
        //setContentView(R.layout.settings_dialog_layout);
        checkBox1 = dialog.findViewById(R.id.checkBox1);
        checkBox1.setChecked(getSwitchValuesFromPrefs(checkBox1.getText().toString()));
        checkBox2 = dialog.findViewById(R.id.checkBox2);
        checkBox2.setChecked(getSwitchValuesFromPrefs(checkBox2.getText().toString()));
        checkBox3 = dialog.findViewById(R.id.checkBox3);
        checkBox3.setChecked(getSwitchValuesFromPrefs(checkBox3.getText().toString()));
        checkBox4 = dialog.findViewById(R.id.checkBox4);
        checkBox4.setChecked(getSwitchValuesFromPrefs(checkBox4.getText().toString()));
        checkBox5 = dialog.findViewById(R.id.checkBox5);
        checkBox5.setChecked(getSwitchValuesFromPrefs(checkBox5.getText().toString()));
        checkBox6 = dialog.findViewById(R.id.checkBox6);
        checkBox6.setChecked(getSwitchValuesFromPrefs(checkBox6.getText().toString()));
        checkBox7 = dialog.findViewById(R.id.checkBox7);
        checkBox7.setChecked(getSwitchValuesFromPrefs(checkBox7.getText().toString()));
        checkBox8 = dialog.findViewById(R.id.checkBox8);
        checkBox8.setChecked(getSwitchValuesFromPrefs(checkBox8.getText().toString()));
        checkBoxB78 = dialog.findViewById(R.id.checkBoxBuildingB78);
        checkBoxB78.setChecked(getSwitchValuesFromPrefs(checkBoxB78.getText().toString()));
        checkBoxB86 = dialog.findViewById(R.id.checkBoxBuildingB86);
        checkBoxB86.setChecked(getSwitchValuesFromPrefs(checkBoxB86.getText().toString()));
        checkBoxMP1 = dialog.findViewById(R.id.checkBoxBuildingMP1);
        checkBoxMP1.setChecked(getSwitchValuesFromPrefs(checkBoxMP1.getText().toString()));
        checkBoxC20 = dialog.findViewById(R.id.checkBoxBuildingC20);
        checkBoxC20.setChecked(getSwitchValuesFromPrefs(checkBoxC20.getText().toString()));
        checkBoxCG22 = dialog.findViewById(R.id.checkBoxBuildingCG22);
        checkBoxCG22.setChecked(getSwitchValuesFromPrefs(checkBoxCG22.getText().toString()));
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                ArrayList<String> numbers = new ArrayList<>();
                buildings = new ArrayList<>();
                if (checkBox1.isChecked()) {
                    numbers.add("1");
                    saveSwitchState(checkBox1.getText().toString(),true);
                } else {
                    saveSwitchState(checkBox1.getText().toString(),false);
                }
                if (checkBox2.isChecked()) {
                    numbers.add("2");
                    saveSwitchState(checkBox2.getText().toString(),true);
                } else {
                    saveSwitchState(checkBox2.getText().toString(),false);
                }
                if (checkBox3.isChecked()) {
                    numbers.add("3");
                    saveSwitchState(checkBox3.getText().toString(),true);
                } else {
                    saveSwitchState(checkBox3.getText().toString(),false);
                }
                if (checkBox4.isChecked()) {
                    numbers.add("4");
                    saveSwitchState(checkBox4.getText().toString(),true);
                } else {
                    saveSwitchState(checkBox4.getText().toString(),false);
                }
                if (checkBox5.isChecked()) {
                    numbers.add("5");
                    saveSwitchState(checkBox5.getText().toString(),true);
                } else {
                    saveSwitchState(checkBox5.getText().toString(),false);
                }
                if (checkBox6.isChecked()) {
                    numbers.add("6");
                    saveSwitchState(checkBox6.getText().toString(),true);
                } else {
                    saveSwitchState(checkBox6.getText().toString(),false);
                }
                if (checkBox7.isChecked()) {
                    numbers.add("7");
                    saveSwitchState(checkBox7.getText().toString(),true);
                } else {
                    saveSwitchState(checkBox7.getText().toString(),false);
                }
                if (checkBox8.isChecked()) {
                    numbers.add("8");
                    saveSwitchState(checkBox8.getText().toString(),true);
                } else {
                    saveSwitchState(checkBox8.getText().toString(),false);
                }
                if (checkBoxB78.isChecked()) {
                    buildings.add("В-78");
                    saveSwitchState(checkBoxB78.getText().toString(),true);
                } else {
                    saveSwitchState(checkBoxB78.getText().toString(),false);
                }
                if (checkBoxB86.isChecked()) {
                    buildings.add("В-86");
                    saveSwitchState(checkBoxB86.getText().toString(),true);
                } else {
                    saveSwitchState(checkBoxB86.getText().toString(),false);
                }
                if (checkBoxC20.isChecked()) {
                    buildings.add("С-20");
                    saveSwitchState(checkBoxC20.getText().toString(),true);
                } else {
                    saveSwitchState(checkBoxC20.getText().toString(),false);
                }
                if (checkBoxMP1.isChecked()) {
                    buildings.add("МП-1");
                    saveSwitchState(checkBoxMP1.getText().toString(),true);
                } else {
                    saveSwitchState(checkBoxMP1.getText().toString(),false);
                }
                if (checkBoxCG22.isChecked()) {
                    buildings.add("СГ-22");
                    saveSwitchState(checkBoxCG22.getText().toString(),true);
                } else {
                    saveSwitchState(checkBoxCG22.getText().toString(),false);
                }
                Log.i("MIREA_APP_TAG", String.valueOf(numbers));
                Log.i("MIREA_APP_TAG", String.valueOf(buildings));
                if (!numbers.equals(checkNumbers)) {
                    checkNumbers = numbers;
                    runSchedule();
                }
            }
        });
    }

    public void updateRecyclerViewByArrayList(ArrayList<Audience> auditories) {
        try {

            if (!checkNumbers.isEmpty() && !day.isEmpty() && !buildings.isEmpty()) {
                recyclerView = findViewById(R.id.recyclerView);
                //aus = dbManager.loadScheduleFromDatabase(numbers, buildings, day, weekNumber);
                if (auditories != null) {
                    AudienceAdapter adapter = new AudienceAdapter(aus);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this,
                            LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(adapter);
                }
            }

        }catch (NullPointerException npe) {
            Toast.makeText(this, "Неправильный ввод параметров", Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<Audience> loadArrayListOfScheduleFromDatadase(){
        try {
            if (!checkNumbers.isEmpty() && !day.isEmpty() && !buildings.isEmpty()) {
                ArrayList<Audience> aus = dbManager.loadScheduleFromDatabase(checkNumbers, buildings, day, weekNumber);
                return aus;
            } else {
                progressDialog.dismiss();
                return null;
            }
        }catch (NullPointerException npe) {
            //Toast.makeText(this, "Неправильный ввод параметров", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return null;
        }
    }
    public void loadShedule() {
        try {
            if (!checkNumbers.isEmpty() && !day.isEmpty() && !buildings.isEmpty()) {
                RecyclerView rcView = findViewById(R.id.recyclerView);
                aus = dbManager.loadScheduleFromDatabase(checkNumbers, buildings, day, weekNumber);
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