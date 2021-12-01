package com.winapp.model;

import org.json.JSONArray;

/**
 * Created by Sathish on 1/13/2020.
 */

public class LocationGetSet {
    String slno;
    String pro_code;
    JSONArray location_code;
    String total;
    String locatn;
    static String isMainLocation;
    String pro_name;
    String pcsperCarton;

    String categoryCode;
    String categoryName;

    public LocationGetSet(){

    }

    public LocationGetSet(String pro_name, String pro_code, String slno, String locatn, String pcsperCarton, JSONArray location_code,
                          String total) {
        this.pro_code = pro_code;
        this.pro_name = pro_name;
        this.slno = slno;
        this.locatn = locatn;
        this.pcsperCarton = pcsperCarton;
        this.location_code = location_code;
        this.total = total;

    }

    public LocationGetSet(String categoryCode, String categoryName) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
    }

    public String getSlno() {
        return slno;
    }

    public void setSlno(String slno) {
        this.slno = slno;
    }

    public String getPro_code() {
        return pro_code;
    }

    public void setPro_code(String pro_code) {
        this.pro_code = pro_code;
    }

    public JSONArray getLocation_code() {
        return location_code;
    }

    public void setLocation_code(JSONArray location_code) {
        this.location_code = location_code;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getLocatn() {
        return locatn;
    }

    public void setLocatn(String locatn) {
        this.locatn = locatn;
    }

    public static String getIsMainLocation() {
        return isMainLocation;
    }

    public static void setIsMainLocation(String isMainLocation) {
        LocationGetSet.isMainLocation = isMainLocation;
    }

    public String getPro_name() {
        return pro_name;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public String getPcsperCarton() {
        return pcsperCarton;
    }

    public void setPcsperCarton(String pcsperCarton) {
        this.pcsperCarton = pcsperCarton;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
