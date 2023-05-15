package com.example.mireaapp;

import android.util.Log;

import java.io.FileNotFoundException;

import org.apache.poi.ss.usermodel.Cell;
import java.io.File;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.io.InputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.util.HashSet;
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

    private static String url = "https://www.mirea.ru/schedule/";
    private String name;
    private String nameOfClass;
    private String building; //Какой корпус
    private String day; //День недели
    private String numOfClass; //Номер пары
    private boolean isFree; //Свободен ли кабинет
    private String group; //Название группы
    private String discipline; //название дисциплины
    private String teacher; // Преподаватель
    private String typeOfClass; //Тип пары, лк, пр, лаб
    private String campus; //Корпус, А,Б,В,Г,Д
    private File path;

    private static HashSet audiences;

    public HashSet getAudiences() {
        return audiences;
    }

    public void setAudiences(HashSet audiences) {
        this.audiences = audiences;
    }

    private ArrayList<String> listOfFileNames;
    private int week;
    public Audience() {
    }

    public Audience(String name, String building, String day, String numOfClass, boolean isFree) {
        this.name = name;
        this.building = building;
        this.day = day;
        this.numOfClass = numOfClass;
        this.isFree = isFree;
    }

    public Audience(String name, String building, String day, String numOfClass, boolean isFree, String group, String discipline) {
        this.name = name;
        this.building = building;
        this.day = day;
        this.numOfClass = numOfClass;
        this.isFree = isFree;
        this.group = group;
        this.discipline = discipline;
    }

    public static String getUrl() {
        return url;
    }

    public String getNameOfClass() {
        return nameOfClass;
    }

    public static void setUrl(String url) {
        Audience.url = url;
    }

    public void setNameOfClass(String nameOfClass) {
        this.nameOfClass = nameOfClass;
    }

    public ArrayList<String> getListOfFileNames() {
        return listOfFileNames;
    }

    public void setListOfFileNames(ArrayList<String> listOfFileNames) {
        this.listOfFileNames = listOfFileNames;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public Audience(File path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getWeek() {
        return week;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setNumOfClass(String numOfClass) {
        this.numOfClass = numOfClass;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setTypeOfClass(String typeOfClass) {
        this.typeOfClass = typeOfClass;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getBuilding() {
        return building;
    }

    public String getDay() {
        return day;
    }

    public String getNumOfClass() {
        return numOfClass;
    }

    public boolean isFree() {
        return isFree;
    }

    public String getGroup() {
        return group;
    }

    public String getDiscipline() {
        return discipline;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getTypeOfClass() {
        return typeOfClass;
    }

    public File getPath() {
        return path;
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
                case Cell.CELL_TYPE_STRING: {
                    String value = cell.getStringCellValue();
                    if (value.length() == 10 && value.contains("-")) {
                        groupList.add(new SheetHelper(cell.getColumnIndex(), value));
                    }
                }
            }
        }
        for (SheetHelper sh : groupList) {
            Log.d("MIREA_APP_TAG", "Cell: " + sh.getCellNumber());
            Log.d("MIREA_APP_TAG", "Group: " + sh.getGroupName());
        }
    }

    public static ArrayList<SheetHelper> getListOfGroupsFromFile(XSSFSheet sheet) throws FileNotFoundException, IOException {

        ArrayList<SheetHelper> groupList = new ArrayList<SheetHelper>();
        XSSFRow row = sheet.getRow(1);
        Iterator Cell2 = row.cellIterator();
        while (Cell2.hasNext()) {
            Cell cell = (Cell)Cell2.next();
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING: {
                    String value = cell.getStringCellValue();
                    if (value.length() == 10 && value.contains("-")) {
                        groupList.add(new SheetHelper(cell.getColumnIndex(), value));
                    }
                }
            }
        }
        return groupList;
    }
    public void dataBaseFileCreator(File path, String fileName) throws FileNotFoundException, IOException, NullPointerException {
        File file = new File(path, fileName);
        Log.d("MIREA_APP_TAG","File: " + fileName);
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(0);
        ArrayList<SheetHelper> groupList = getListOfGroupsFromFile(sheet);
        int numOfClassesInDay = checkNumbersOfClassinday(sheet);
        int k =86;
        if(numOfClassesInDay==8) {
            k = 104;
        }
        Iterator rIterator = sheet.rowIterator();
        for (int i = 3; i<=k; i++) {
            Row row = sheet.getRow(i);
            getInfoFromManyRows(row, sheet, fileName, groupList, numOfClassesInDay, i);

        }
    }


    public int checkNumbersOfClassinday(XSSFSheet sheet) {


        int maxValue=0;
        int value = 0;
        for (int i = 3; i<21;i++) {
            Row row = sheet.getRow(i);
            Cell cell = row.getCell(1);
            try {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        value = Integer.parseInt(cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        value = (int) cell.getNumericCellValue();
                        break;
                }
            }catch (NullPointerException npe) {

            }
            if(value > maxValue) {
                maxValue = value;
            }
        }

        return maxValue;
    }

    public static String[] deleteValueFromArray (String[] array, int value) {
        String [] newArray = new String[array.length-1];
        for (int i = 0, k = 0; i < array.length; i++) {

            // check if index is crossed, continue without copying
            if (i == value) {
                continue;
            }

            // else copy the element
            newArray[k++] = array[i];
        }
        return newArray;
    }


    private static void convertNameStringToManyValues (String value) {
        String[] values = value.split("[, ]+");

    }

    private void getInfoFromManyRows(Row row, XSSFSheet sheet, String fileName, ArrayList<SheetHelper> groupList, int numOfClassesInDay, int rowNum) {
        for (SheetHelper sh : groupList) {
            String group = sh.getGroupName();
            int cellNum = sh.getCellNumber();
            Cell cell = row.getCell(cellNum);
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    String value = cell.getStringCellValue();
                    if (value.length() != 0) {
                        getInfoFromRow(row, sheet, fileName, sh, numOfClassesInDay, rowNum);
                    }
            }
        }
    }
    public static void getInfoFromRow(Row row,XSSFSheet sheet, String fileName, SheetHelper sh, int numOfClassesInDay, int rowNum) {


        //Log.i("MIREA_APP_TAG", "Group: " + group);
        ArrayList<Audience> info = new ArrayList<>(); // Лист с значениями
        //Audience cellInfo = new Audience();
        String group = sh.getGroupName();
        int cellNum = sh.getCellNumber();
        /*
        Нахождение названий пар. Хранение в массиве.
        dinamyc
        */

        /*
        Cell cellDiscipline = row.getCell(sh.getCellNumber());
        switch (cellDiscipline.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                String value = cellDiscipline.getStringCellValue().replaceAll("( ){4,}", "\n");
                String[] disciplineValues = value.split("\\v+");
                for (int i = 0; i < disciplineValues.length; i++) {
                    if (disciplineValues[i].contains("каф.")) {
                        disciplineValues = deleteValueFromArray(disciplineValues, i);
                    }
                }
                if (disciplineValues[0].equals("Военная")) {
                    Audience au = new Audience();

                    au.setDiscipline(value.replace("\n", " "));
                    au.setGroup(group);
                    info.add(au);
                    //info.get(0).setDiscipline(value.replace("\n", " "));
                } else {
                    for (int i = 0; i < disciplineValues.length; i++) {
                        Audience au = new Audience();

                        au.setDiscipline(disciplineValues[i]);
                        au.setGroup(group);
                        info.add(au);
                    }
                }
        }
        */


        /*
        Нахождение название класса. Хранится в массиве
         */


        Cell cellName = row.getCell(sh.getCellNumber() + 3);
        //ArrayList<String> info = new ArrayList<>();
        switch (cellName.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                String value = cellName.getStringCellValue().replaceAll("[., ]+", "\n");
                String[] nameValues = value.split("\\v+");
                for (int i = 0; i < nameValues.length; i++) {
                    Audience au = new Audience();
                    au.setGroup(group);
                    info.add(au);
                }
                int counter = 0;
                if (nameValues.length > 1) {
                    for (int i = 0; i < nameValues.length; i++) {

                        if (nameValues[i].contains("лаб") || nameValues[i].contains("ауд") || nameValues[i].contains("физ") || nameValues[i].contains("комп") || nameValues[i].contains("№")
                                || nameValues[i].contains("СДО") || nameValues[i].contains("аф") || nameValues[i].equals("д") || nameValues[i].equals("Д") || nameValues[i].contains("истанционно")) {
                            continue;
                        } else if (nameValues[i].contains("(") && nameValues[i].contains(")")) {
                            info.get(counter).setBuilding(nameValues[i].replace("(", "")
                                    .replace(")", ""));
                        } else {
                            info.get(counter).setNameOfClass(nameValues[i]);
                        }

                        Audience au = info.get(counter);
                        if (au.getBuilding()!=null && au.getNameOfClass()!=null ) {
                            counter++;
                        }
                    }
                }
        }

        for (int i = 0; i < info.size(); i++) {
            Audience au = info.get(i);
            if (au.getBuilding()==null && au.getNameOfClass()==null) {
                info.remove(i);
                i--;
            }
        }
        /*
        try {
            for (Audience au : info) {
                String name = au.getName().replace("лаб", "")
                        .replace("ауд", "")
                        .replace("физ", "")
                        .replace("комп", "");

                String[] values = name.split("[,. ]+");
                for (int i = 0; i < values.length; i++) {

                    if (values[i].contains("(") && values[i].contains(")")) {
                        au.setBuilding(values[i].replace("(", "")
                                .replace(")", ""));
                    } else {
                        au.setNameOfClass(values[i]);
                    }


                }
            }
        }catch(NullPointerException npe) {

        }

         */
        /*
        Добавление преподавателя
        dinamyc
         */
        /*
            try {
                Cell cellTeacher = row.getCell(sh.getCellNumber() + 2);
                switch (cellTeacher.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        String value = cellTeacher.getStringCellValue().replaceAll("( ){10,}", "\n");
                        String[] teacherValues = value.split("\\v+");
                        for (int i = 0; i < teacherValues.length; i++) {
                            info.get(i).setTeacher(teacherValues[i]);
                        }
                }
            } catch (IndexOutOfBoundsException ioe) {
                Cell cellTeacher = row.getCell(sh.getCellNumber() + 2);
                switch (cellTeacher.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        String value = cellTeacher.getStringCellValue();
                        String[] teacherValues = value.split("\\v+");
                        for (int i = 0; i < teacherValues.length; i++) {
                            info.get(i).setTeacher(teacherValues[i]);
                        }
                }
            }
            */

        /*
        Добавление типа предмета
        dynamic
         */
        /*
            Cell cellTypeOfClass = row.getCell(sh.getCellNumber() + 1);
            switch (cellTypeOfClass.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    String value = cellTypeOfClass.getStringCellValue().replaceAll("( ){4,}", "\n");
                    String[] classesValues = value.split("\\v+");
                    for (int i = 0; i < classesValues.length; i++) {
                        info.get(i).setTypeOfClass(classesValues[i]);
                    }
            }

         */
        /*
        Добавление дня недели
         */
        if (numOfClassesInDay == 7) {
            if (rowNum < 17) {
                for (Audience au : info) {
                    au.setDay("ПОНЕДЕЛЬНИК");
                }
            } else if (rowNum >= 17 && rowNum < 31) {
                for (Audience au : info) {
                    au.setDay("ВТОРНИК");
                }
            } else if (rowNum >= 31 && rowNum < 45) {
                for (Audience au : info) {
                    au.setDay("СРЕДА");
                }
            } else if (rowNum >= 45 && rowNum < 59) {
                for (Audience au : info) {
                    au.setDay("ЧЕТВЕРГ");
                }
            } else if (rowNum >= 59 && rowNum < 73) {
                for (Audience au : info) {
                    au.setDay("ПЯТНИЦА");
                }
            } else if (rowNum >= 73 && rowNum < 86) {
                for (Audience au : info) {
                    au.setDay("СУББОТА");
                }
            }
        } else if(numOfClassesInDay==8) {
            if (rowNum < 21) {
                for (Audience au : info) {
                    au.setDay("ПОНЕДЕЛЬНИК");
                }
            } else if (rowNum >= 21 && rowNum < 39) {
                for (Audience au : info) {
                    au.setDay("ВТОРНИК");
                }
            } else if (rowNum >= 39 && rowNum < 57) {
                for (Audience au : info) {
                    au.setDay("СРЕДА");
                }
            } else if (rowNum >= 57 && rowNum < 75) {
                for (Audience au : info) {
                    au.setDay("ЧЕТВЕРГ");
                }
            } else if (rowNum >= 75 && rowNum < 93) {
                for (Audience au : info) {
                    au.setDay("ПЯТНИЦА");
                }
            } else if (rowNum >= 93 && rowNum < 104) {
                for (Audience au : info) {
                    au.setDay("СУББОТА");
                }
            }
        }
        /*
        Добавление недели
         */
            Cell cellNumOfWeek = row.getCell(4);
            switch (cellNumOfWeek.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    String value = cellNumOfWeek.getStringCellValue();
                    int numOfWeek = value.length();
                    for (Audience au : info) {
                        au.setWeek(numOfWeek);
                    }
            }
        /*
        Добавление номера пары
         */
            try {
                Cell cellNumOfClass = row.getCell(1);
                switch (cellNumOfClass.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        String value = cellNumOfClass.getStringCellValue();
                        for (Audience au : info) {
                            au.setNumOfClass(value);
                        }
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        int intValue = (int) cellNumOfClass.getNumericCellValue();
                        String newvalue = String.valueOf(intValue);
                        for (Audience au : info) {
                            au.setNumOfClass(newvalue);
                        }
                        break;
                }
            } catch (NullPointerException npe) {

            }
        /*
        Проверка номера пары
         */
            for (Audience au : info) {
                if (au.getNumOfClass() == null) {
                    int rowNumber = row.getRowNum();
                    Row checkRow = sheet.getRow(rowNumber - 1);
                    Cell cellNumOfClass = checkRow.getCell(1);
                    switch (cellNumOfClass.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            String value = cellNumOfClass.getStringCellValue();
                            au.setNumOfClass(value);
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            int intValue = (int) cellNumOfClass.getNumericCellValue();
                            String newvalue = String.valueOf(intValue);
                            au.setNumOfClass(newvalue);
                            break;
                    }
                }
            }


        for (Audience au: info) {
            Log.i("MIREA_APP_TAG"," Неделя " + au.getWeek() + " Day: " + au.getDay() + " Пара: " + au.getNumOfClass()
                     + " Группа "+ au.getGroup() + " Адрес " + au.getBuilding() + " Кабинет " + au.getNameOfClass());
        }

        for (Audience au : info) {
            //audiences.add(au);
        }
        /*
            for (Audience au : info) {
                if (au.getNumOfClass() == null || au.getTypeOfClass() == null || au.getDiscipline() == null || au.getName() == null) {
                    Log.i("MIREA_APP_TAG", "File: " + fileName);
                    Log.i("MIREA_APP_TAG", " Неделя " + au.getWeek() + " Day: " + au.getDay() + " Пара: " + au.getNumOfClass() + " Кабинет: " + au.getName()
                            + " Дисциплина: " + au.getDiscipline() + " Группы " + au.getGroup() + " тип пары" + au.getTypeOfClass() + " Преподаватель" + au.getTeacher());
                }
            }
        */
    }

    public void differentialDataBaseCreator(File path, String fileName) {
        if (fileName.contains("ekz")) {
        } else if (fileName.contains("zach")) {
        } else if (fileName.contains("gia")) {
        } else {
            try {
                dataBaseFileCreator(path, fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }





    @Override
    public void run() {


        ArrayList<String> listOfLinksToDownload = getListOfFilesToDownload(url);
        Log.i("MIREA_APP_TAG", String.valueOf(listOfLinksToDownload));
        listOfFileNames = getListOfNamesFromListOfLinks(listOfLinksToDownload);

        downloadlistOfFiles(listOfLinksToDownload,listOfFileNames,path);

        for (String u : listOfFileNames) {

                differentialDataBaseCreator(path,u);

        }


    }

}
