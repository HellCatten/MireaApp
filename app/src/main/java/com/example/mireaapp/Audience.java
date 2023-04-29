package com.example.mireaapp;

import static org.apache.xmlbeans.impl.piccolo.xml.Piccolo.STRING;

import static java.sql.Types.NUMERIC;

import android.util.Log;

import java.io.FileNotFoundException;

import org.apache.poi.ss.usermodel.Cell;
import java.io.File;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.io.InputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.util.Iterator;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.util.stream.Stream;
import java.net.URLConnection;
import java.io.IOException;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Audience implements Runnable {

    private String name;
    private String building;
    private String day;
    private int numOfClass;
    private boolean isFree;
    private String group;
    private File path;

    public Audience(String name, String building, String day, int numOfClass, boolean isFree) {
        this.name = name;
        this.building = building;
        this.day = day;
        this.numOfClass = numOfClass;
        this.isFree = isFree;
    }

    public Audience(File path) {
        this.path = path;
    }

    public String getName() {
        return this.name;
    }

    public String getBuilding() {
        return this.building;
    }

    public String getDay() {
        return this.day;
    }

    public int getNumOfClass() {
        return this.numOfClass;
    }

    public boolean isIsFree() {
        return this.isFree;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setNumOfClass(int numOfClass) {
        this.numOfClass = numOfClass;
    }

    public void setIsFree(boolean isFree) {
        this.isFree = isFree;
    }



    public static ArrayList<String> getListOfFilesToDownload(String source) {
        ArrayList<String> listURL = new ArrayList<String>();
        try {
            String current;
            URL url = new URL(source);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = (HttpURLConnection)urlConnection;
            connection.setRequestMethod("GET");
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader in = new BufferedReader(isr);
            StringBuilder urlString = new StringBuilder();
            while ((current = in.readLine()) != null) {
                urlString.append(current);
            }
            String[] listUrl = urlString.toString().split("\"");
            Stream<String> stream = Arrays.stream(listUrl);
            stream.filter(x -> x.toString().contains(".xlsx")).distinct().forEach(x -> listURL.add((String)x));
            System.out.println(listURL);
            System.out.println(listURL.size());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return listURL;
    }

    public static void downloadOneFile(String urlStr, String fileName, File path) throws IOException {
        URL url = new URL(urlStr);

        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        //Context context = getApplicationContext();
        //File path = context.getFilesDir();
        File file = new File(path, fileName);
        if (!file.isFile()) {
            FileOutputStream fis = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = bis.read(buffer, 0, 1024)) != -1) {
                fis.write(buffer, 0, count);
            }
            fis.close();
            bis.close();
            Log.i("MIREA_APP_TAG", "Download: " + fileName);

        }
    }

    public ArrayList<String> getListOfNamesFromListOfLinks(ArrayList<String> links) {
        ArrayList<String> listOfNames = new ArrayList<>();
        for (String u : links) {
            String[] link = u.split("/");
            Stream<String> stream = Arrays.stream(link);
            stream.filter(x -> x.toString().contains(".xlsx")).distinct().forEach(x -> listOfNames.add(x));
        }
        Log.i("MIREA_APP_TAG", String.valueOf(listOfNames));

        return listOfNames;

    }

    public  void downloadlistOfFiles(ArrayList<String> listURL,ArrayList<String> listOfNames, File path) {

        for (int i = 0; i< listURL.size();i++) {
            String fileName = listOfNames.get(i);
            String url = listURL.get(i);
            try {
                downloadOneFile(url,fileName,path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        /*
        for (String u : listURL) {
            String[] listUrl = u.split("/");
            Stream<String> stream = Arrays.stream(listUrl);
            stream.filter(x -> x.toString().contains(".xlsx")).distinct().forEach(x -> {
                String fileName = x;
                try {
                    Audience.downloadOneFile(u, fileName, path);
                    Log.i("MIREA_APP_TAG", "File is downloaded" + fileName);
                }
                catch (IOException ex) {

                }
            });
        }
        */

    }

    public static void geekforgeeks() throws IOException {
        FileInputStream file = new FileInputStream("C:\\Basalykov\\e\\IRI_2-kurs_22_23_vesna_TANDEM_22.03.2023.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook((InputStream)file);
        XSSFSheet sh = wb.getSheetAt(0);
        ArrayList<String> map = new ArrayList<String>();
        for (int r = 0; r <= sh.getLastRowNum(); ++r) {
            try {
                String value = sh.getRow(r).getCell(1).getStringCellValue();
                map.add(value);
                continue;
            }
            catch (NullPointerException npe) {
                System.out.println('n');
            }
        }
        for (String m : map) {
            System.out.println(m);
        }
        wb.close();
        file.close();
    }

    public static void objectRealiser(String filePath) throws FileNotFoundException, IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook((InputStream)fis);
        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator rIterator = sheet.rowIterator();
        ArrayList<String> list = new ArrayList<String>();
        XSSFRow row = sheet.getRow(2);
        Iterator Cell2 = row.cellIterator();
        while (Cell2.hasNext()) {
            Cell cell = (Cell)Cell2.next();
            switch (cell.getCellType()) {
                case STRING: {
                    list.add(cell.getStringCellValue());
                    break;
                }
                case NUMERIC: {
                    System.out.print(cell.getNumericCellValue());
                    break;
                }

            }
            System.out.println("|");
            System.out.println();
        }
        for (String l : list) {
            System.out.println(l);
        }
        wb.close();
        fis.close();
    }

    public static void getListOfGroupsFromFile(File path, String fileName) throws FileNotFoundException, IOException {
        File file = new File(path, fileName);
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(0);
        ArrayList<SheetHelper> groupList = new ArrayList<SheetHelper>();
        XSSFRow row = sheet.getRow(1);
        Iterator Cell2 = row.cellIterator();
        while (Cell2.hasNext()) {
            Cell cell = (Cell)Cell2.next();
            switch (cell.getCellType()) {
                case STRING: {
                    String value = cell.getStringCellValue();
                    if (value.length() != 10) {
                        groupList.add(new SheetHelper(cell.getColumnIndex(), value));
                    }
                }
            }
        }
        for (SheetHelper sh : groupList) {
            Log.d("MIREA_APP_TAG", "Group: " + sh.getCellNumber());
            System.out.println(sh.getGroupName());
        }
    }

    @Override
    public void run() {
        String url = "https://www.mirea.ru/schedule/";
        Log.i("MIREA_APP_TAG", "This is fine");
        ArrayList<String> listOfLinksToDownload = getListOfFilesToDownload(url);
        Log.i("MIREA_APP_TAG", String.valueOf(listOfLinksToDownload));
        ArrayList<String> listOfFileNames = getListOfNamesFromListOfLinks(listOfLinksToDownload);
        downloadlistOfFiles(listOfLinksToDownload,listOfFileNames,path);
        for (String u : listOfFileNames) {
            try {
                getListOfGroupsFromFile(path,u);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
