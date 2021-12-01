package com.winapp.model;

import java.util.ArrayList;

import com.winapp.sot.SO;

public class Schedule {
 
 private String name;
 private ArrayList<SO> scheduleList = new ArrayList<SO>();
 private static ArrayList<StockCount> stockcount = new ArrayList<StockCount>();
 public Schedule(){
	 
 }
 public Schedule(String name, ArrayList<SO> scheduleList) {
	  super();
	  this.name = name;
	  this.scheduleList = scheduleList;
	 }
 public String getName() {
  return name;
 }
 public void setName(String name) {
  this.name = name;
 }
 public ArrayList<SO> getScheduleList() {
  return scheduleList;
 }
 public void setScheduleList(ArrayList<SO> scheduleList) {
  this.scheduleList = scheduleList;
 }

 public static ArrayList<StockCount> getStockcount() {
  return stockcount;
 }

 public static void setStockcount(ArrayList<StockCount> stockcount) {
  Schedule.stockcount = stockcount;
 }
}