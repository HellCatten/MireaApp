package com.example.mireaapp;

public class SheetHelper {

    private int cellNumber;
    private String groupName;

    public SheetHelper(int cellNumber, String groupName) {
        this.cellNumber = cellNumber;
        this.groupName = groupName;
    }

    public int getCellNumber() {
        return this.cellNumber;
    }

    public String getGroupName() {
        return this.groupName;
    }

}
