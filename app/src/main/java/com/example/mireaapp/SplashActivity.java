package com.example.mireaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.io.File;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler(Looper.getMainLooper());
    private DBManager dbManager;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Context context = this;
        File path = context.getFilesDir();
        this.dbManager = new DBManager(new FilesDataBase(this, "my_database.db", null, 1));
        this.dbManager.setPath(path);
        dbManager.setContext(this);
        //dbManager.saveFilesToDatabase();
        thread = new Thread(dbManager);
        //thread.start();
        Thread t = new Thread(() -> {
            dbManager.prerun();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
        });
        t.start();
    }

}