package com.winapp.model;

/**
 * Created by user on 02-Mar-17.
 */

public class User {
    String name;
    boolean checkbox;

    public User() {
         /*Empty Constructor*/
    }
    public  User(String country, boolean status){
        this.name = country;
        this.checkbox = status;
    }
    //Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheckbox() {
        return checkbox;
    }

    public void setCheckbox(boolean checkbox) {
        this.checkbox = checkbox;
    }

}
