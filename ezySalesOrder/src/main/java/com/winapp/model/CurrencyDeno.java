package com.winapp.model;

/**
 * Created by Sathish on 2/3/2020.
 */

public class CurrencyDeno {
    private String slno;
    private String curency;
    private String denomination;
    private String total;
    String settlementNo;
    String settlementDate;
    String settlementBy;
    String locationcode;
    String totlAmt;

    public String getCurency() {
        return curency;
    }

    public void setCurency(String curency) {
        this.curency = curency;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSlno() {
        return slno;
    }

    public void setSlno(String slno) {
        this.slno = slno;
    }

    public String getSettlementNo() {
        return settlementNo;
    }

    public void setSettlementNo(String settlementNo) {
        this.settlementNo = settlementNo;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getSettlementBy() {
        return settlementBy;
    }

    public void setSettlementBy(String settlementBy) {
        this.settlementBy = settlementBy;
    }

    public String getLocationcode() {
        return locationcode;
    }

    public void setLocationcode(String locationcode) {
        this.locationcode = locationcode;
    }

    public String getTotlAmt() {
        return totlAmt;
    }

    public void setTotlAmt(String totlAmt) {
        this.totlAmt = totlAmt;
    }
}
