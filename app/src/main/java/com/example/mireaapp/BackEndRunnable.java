package com.example.mireaapp;

import java.io.File;
import java.util.ArrayList;

public class BackEndRunnable implements Runnable{


    @Override
    public void run() {
        ArrayList<String> list = Audience.getListOfFilesToDownload("https://www.mirea.ru/schedule/");
    }
}
