package com.winapp.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class DateTime {

	public static String date(String dateString) {
		String date = null;		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date myDate = dateFormat.parse(dateString);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(myDate);
			calendar.add(Calendar.DAY_OF_YEAR, -30);
			Date newDates = calendar.getTime();
			date = dateFormat.format(newDates);
			System.out.println("Date : " + date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;

	}

	public static String date_(String dateString) {
		String date = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		try {
			Date myDate = dateFormat.parse(dateString);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(myDate);
			calendar.add(Calendar.DAY_OF_YEAR, -30);
			Date newDates = calendar.getTime();
			date = dateFormat.format(newDates);
			System.out.println("Date : " + date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;

	}
	public static Date subtractDays(Date date, int days) {
		Log.d("dateSub","-->"+date);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);                  
        return cal.getTime();
    }
}
