package com.winapp.adapter;

/**
 * Created by USER on 16/5/2017.
 */

public class Attribute {
    int position;
    int value;
    String name;
    String code;
    String sizecode;
    String sizename;
    String colorQty;
    String sizeQty;
    boolean isSelected;
    String flag;
    String color;
    String productcodeno;
    String no;

    public String getProductcodeno() {
        return productcodeno;
    }

    public void setProductcodeno(String productcodeno) {
        this.productcodeno = productcodeno;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getSizecode() {
        return sizecode;
    }

    public void setSizecode(String sizecode) {
        this.sizecode = sizecode;
    }

    public String getSizename() {
        return sizename;
    }

    public void setSizename(String sizename) {
        this.sizename = sizename;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorQty() {
        return colorQty;
    }

    public void setColorQty(String colorQty) {
        this.colorQty = colorQty;
    }

    public String getSizeQty() {
        return sizeQty;
    }

    public void setSizeQty(String sizeQty) {
        this.sizeQty = sizeQty;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
