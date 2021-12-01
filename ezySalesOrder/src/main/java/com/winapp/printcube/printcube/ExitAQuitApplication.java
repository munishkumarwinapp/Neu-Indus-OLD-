package com.winapp.printcube.printcube;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
public class ExitAQuitApplication 
{
	public static ArrayList<Activity> activityList=new ArrayList<Activity>();

    public static void add(Activity activity){
      activityList.add(activity);
    }

    public static void finishAll(){
      int size=activityList.size();
         if(size>0){
            for(int i=0;i<size;i++){
                if(!activityList.get(i).isFinishing()){
                   activityList.get(i).finish();
                }
            }
        }
    }

    public static void remove(Activity activity){
       int size=activityList.size();
        if(size>0){
            for(int i=0;i<size;i++){
               if(activityList.get(i)==activity){
                  activityList.remove(i);
                  break;
               }
            }
        }
    }

}
