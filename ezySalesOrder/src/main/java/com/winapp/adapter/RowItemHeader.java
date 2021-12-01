package com.winapp.adapter;

import java.util.ArrayList;

/**
 * Created by USER on 16/5/2017.
 */

public class RowItemHeader {

    private String colorName;
    private ArrayList<Attribute> rowItemList = new ArrayList<Attribute>();
    public RowItemHeader(String colorName, ArrayList<Attribute> rowItemList) {
        super();
        this.colorName = colorName;
        this.rowItemList = rowItemList;
    }
    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public ArrayList<Attribute> getRowItemList() {
        return rowItemList;
    }

    public void setRowItemList(ArrayList<Attribute> rowItemList) {
        this.rowItemList = rowItemList;
    }
}

