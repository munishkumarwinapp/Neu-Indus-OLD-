package com.winapp.helper;

import java.io.IOException;

import android.util.Log;

public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
	 @Override
	 public void uncaughtException(Thread thread, Throwable ex) {
	 if(ex.getClass().equals(OutOfMemoryError.class))
	 {
	 try {
	   android.os.Debug.dumpHprofData("/sdcard/dump.hprof");
	   Log.d("Out of memory", "Out of memory");
	 } 
	 catch (IOException e) {
	   e.printStackTrace();
	 }
	 }
	 ex.printStackTrace();
	 }
}
