package com.winapp.trackwithmap;

import java.io.Serializable;

/**
 * Created by USER on 19/9/2017.
 */

public class ScheduleDataNew implements Serializable {
    private String title, genre, year,price,color,time;
    private String sno;
    private String soNo;
    private String date;
    private String customerCode;
    private String customerName;
    private String nettotal;
    private String DelCustomerName;
    private String gotSignatureOnDO;
    private int image;
    private String invoiceNo,invoiceDate,delAddress1,delAddress2,delAddress3,delPostalCode,remarks,assignUser,assignDate,gotSignature,
            gotSignatureDate,estimatedReachTime,kiloMeter,requestedDeliveryTime,startTime,travelTimeInMinutes;
    private String  SettingID,SettingValue,destendpoint;
    static String countryname,trantype;
    private double latitude,longitude;

    public ScheduleDataNew() {
    }
    public ScheduleDataNew(String title, String genre, String year,String price,int image) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.price = price;
        this.image = image;
    }
    public ScheduleDataNew(String title, String genre, String year,String price,int image,String color) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.price = price;
        this.image = image;
        this.color = color;
    }

    public ScheduleDataNew(String sno, String date, String year,String time) {
        this.sno = sno;
        this.date = date;
        this.year = year;
        this.time = time;
    }

    public ScheduleDataNew(String title, String genre, String customerCode,String customerName
            ,String nettotal,String DelCustomerName,String gotSignatureOnDO) {
        this.title = title;
        this.genre = genre;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.nettotal = nettotal;
        this.DelCustomerName = DelCustomerName;
        this.gotSignatureOnDO = gotSignatureOnDO;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getNettotal() {
        return nettotal;
    }

    public void setNettotal(String nettotal) {
        this.nettotal = nettotal;
    }

    public String getDelCustomerName() {
        return DelCustomerName;
    }

    public void setDelCustomerName(String delCustomerName) {
        DelCustomerName = delCustomerName;
    }

    public String getGotSignatureOnDO() {
        return gotSignatureOnDO;
    }

    public void setGotSignatureOnDO(String gotSignatureOnDO) {
        this.gotSignatureOnDO = gotSignatureOnDO;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public String getSoNo() {
        return soNo;
    }

    public void setSoNo(String soNo) {
        this.soNo = soNo;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getDelAddress1() {
        return delAddress1;
    }

    public void setDelAddress1(String delAddress1) {
        this.delAddress1 = delAddress1;
    }

    public String getDelAddress2() {
        return delAddress2;
    }

    public void setDelAddress2(String delAddress2) {
        this.delAddress2 = delAddress2;
    }

    public String getDelAddress3() {
        return delAddress3;
    }

    public void setDelAddress3(String delAddress3) {
        this.delAddress3 = delAddress3;
    }

    public String getDelPostalCode() {
        return delPostalCode;
    }

    public void setDelPostalCode(String delPostalCode) {
        this.delPostalCode = delPostalCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAssignUser() {
        return assignUser;
    }

    public void setAssignUser(String assignUser) {
        this.assignUser = assignUser;
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getGotSignature() {
        return gotSignature;
    }

    public void setGotSignature(String gotSignature) {
        this.gotSignature = gotSignature;
    }

    public String getGotSignatureDate() {
        return gotSignatureDate;
    }

    public void setGotSignatureDate(String gotSignatureDate) {
        this.gotSignatureDate = gotSignatureDate;
    }

    public String getEstimatedReachTime() {
        return estimatedReachTime;
    }

    public void setEstimatedReachTime(String estimatedReachTime) {
        this.estimatedReachTime = estimatedReachTime;
    }

    public String getKiloMeter() {
        return kiloMeter;
    }

    public void setKiloMeter(String kiloMeter) {
        this.kiloMeter = kiloMeter;
    }

    public String getRequestedDeliveryTime() {
        return requestedDeliveryTime;
    }

    public void setRequestedDeliveryTime(String requestedDeliveryTime) {
        this.requestedDeliveryTime = requestedDeliveryTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTravelTimeInMinutes() {
        return travelTimeInMinutes;
    }

    public void setTravelTimeInMinutes(String travelTimeInMinutes) {
        this.travelTimeInMinutes = travelTimeInMinutes;
    }

    public String getSettingID() {
        return SettingID;
    }

    public void setSettingID(String settingID) {
        SettingID = settingID;
    }

    public String getSettingValue() {
        return SettingValue;
    }

    public void setSettingValue(String settingValue) {
        SettingValue = settingValue;
    }

    public String getDestendpoint() {
        return destendpoint;
    }

    public void setDestendpoint(String destendpoint) {
        this.destendpoint = destendpoint;
    }

    public static String getCountryname() {
        return countryname;
    }

    public static void setCountryname(String countryname) {
        ScheduleDataNew.countryname = countryname;
    }

    public static String getTrantype() {
        return trantype;
    }

    public static void setTrantype(String trantype) {
        ScheduleDataNew.trantype = trantype;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
