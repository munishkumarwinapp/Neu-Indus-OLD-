package com.winapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winapp.adapter.CalendarAdapter;
import com.winapp.SFA.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

// Custom calendar view for selecting from date and to date @Arunkumar

public class CustomCalendar {

	public GregorianCalendar month, itemmonth;// calendar instances.

	public CalendarAdapter adapter;// adapter instance
	public Handler handler;// for grabbing some event values for showing the dot
							// marker.
	public ArrayList<String> items; // container to store calendar items which
	TextView title; // needs showing the event marker
	Calendar getCalendar;
	DatePickerDialog.OnDateSetListener selectDate;
	private Activity mActivity;
	AlertDialog.Builder calendarDialog;
	boolean status = false;
	boolean dialogResult;
	Handler mHandler;
	View v = null;
	String selectYear="", selectMonth="", selectDay="";
	String selectedGridDate="";
	
	public CustomCalendar(Activity activity) {
		mActivity = activity;
		Log.d("constructor", ""+mActivity.getClass().getName());
	}

	public boolean getDialogResult() {
		return dialogResult;
	}

	public boolean setDialogResult(boolean dialogResult) {
		return this.dialogResult = dialogResult;
	}

	public boolean showCalendarView() {

		Log.d("showCalendarView", ""+mActivity.getClass().getName());
		
		calendarDialog = new AlertDialog.Builder(mActivity);

		LayoutInflater li = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = li.inflate(R.layout.calendar, null, false);

		calendarDialog.setView(v);
		calendarDialog.setCancelable(false);
		Locale.setDefault(Locale.US);
		month = (GregorianCalendar) Calendar.getInstance();
		itemmonth = (GregorianCalendar) month.clone();

		items = new ArrayList<String>();
		adapter = new CalendarAdapter(mActivity, month);

		GridView gridview = (GridView) v.findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		handler = new Handler();
		handler.post(calendarUpdater);

		title = (TextView) v.findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

		getCalendar = Calendar.getInstance();

		RelativeLayout previous = (RelativeLayout) v
				.findViewById(R.id.previous);
		
		DateFormat df;
		df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		selectedGridDate = df.format(getCalendar.getTime());
		
		selectDate = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				getCalendar.set(Calendar.YEAR, year);
				getCalendar.set(Calendar.MONTH, monthOfYear);
				getCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

				selDate();
			}
		};

		title.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (MotionEvent.ACTION_UP == event.getAction())
					Log.d("touch", "touch mode");
				disp();
				disp().show();

				return false;
			}
		});

		previous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPreviousMonth();
				refreshCalendar();
			}
		});

		RelativeLayout next = (RelativeLayout) v.findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setNextMonth();
				refreshCalendar();

			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				((CalendarAdapter) parent.getAdapter()).setSelected(v);
				selectedGridDate = CalendarAdapter.dayString
						.get(position);
				String[] separatedTime = selectedGridDate.split("-");
				String gridvalueString = separatedTime[2].replaceFirst("^0*",
						"");// taking last part of date. ie; 2 from 2012-12-02.
				int gridvalue = Integer.parseInt(gridvalueString);
				// navigate to next or previous month on clicking offdays.
				if ((gridvalue > 10) && (position < 8)) {
					setPreviousMonth();
					refreshCalendar();
				} else if ((gridvalue < 7) && (position > 28)) {
					setNextMonth();
					refreshCalendar();
				}
				((CalendarAdapter) parent.getAdapter()).setSelected(v);

//				showToast(selectedGridDate);

			}
		});

		try {

			calendarDialog.setPositiveButton("Done",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface,
								int i) {

						}
					});
			calendarDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface,
								int i) {

						}
					});

			final AlertDialog dialog = calendarDialog.create();
			dialog.show();
			dialog.getButton(DialogInterface.BUTTON_POSITIVE)
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							showToast(selectedGridDate);
							status=true;
							endDialog(true);							
							dialog.dismiss();
							
						}
					});

			dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							endDialog(false);
						}
					});

		} catch (Exception e) {
e.printStackTrace();
		}
		return status;
	}

	public void endDialog(boolean result) {

		setDialogResult(result);
		Message m = mHandler.obtainMessage();
		mHandler.sendMessage(m);
	}

	
	public boolean showDialog() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message mesg) {
				// process incoming messages here
				// super.handleMessage(msg);
				throw new RuntimeException();
			}
		};
		// super.show();
		try {
			Looper.getMainLooper();
			Looper.loop();
		} catch (RuntimeException e2) {
		}
		return dialogResult;
	}

	public String getSelectDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int currentDayend = cal.get(Calendar.DAY_OF_MONTH);
		int currentMonend = cal.get(Calendar.MONTH);
		int currentyearend = cal.get(Calendar.YEAR);
		String sdate = currentDayend + "/" + (currentMonend + 1) + "/"
				+ currentyearend;
		return sdate;
	}

	protected void setNextMonth() {
		if (month.get(Calendar.MONTH) == month
				.getActualMaximum(Calendar.MONTH)) {
			month.set((month.get(Calendar.YEAR) + 1),
					month.getActualMinimum(Calendar.MONTH), 1);
		} else {
			month.set(Calendar.MONTH,
					month.get(Calendar.MONTH) + 1);
		}

	}

	protected void setPreviousMonth() {
		if (month.get(Calendar.MONTH) == month
				.getActualMinimum(Calendar.MONTH)) {
			month.set((month.get(Calendar.YEAR) - 1),
					month.getActualMaximum(Calendar.MONTH), 1);
		} else {
			month.set(Calendar.MONTH,
					month.get(Calendar.MONTH) - 1);
		}

	}

	protected void showToast(String string) {

		String[] str = string.split("-");

		String year = str[0];
		String month = str[1];
		String day = str[2];
		
		selectYear=year;
		selectMonth=month;
		selectDay=day;

//		Toast.makeText(mActivity, day + "/" + month + "/" + year,
//				Toast.LENGTH_SHORT).show();

	}

	public void refreshCalendar() {
		// TextView title = (TextView) v.findViewById(R.id.title);

		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		handler.post(calendarUpdater); // generate some calendar items

		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}

	public Runnable calendarUpdater = new Runnable() {

		@Override
		public void run() {
			items.clear();

			// Print dates of the current week
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			String itemvalue;
			// for (int i = 0; i < 7; i++) {
			itemvalue = df.format(itemmonth.getTime());
			itemmonth.add(Calendar.DATE, 1);
			// items.add("2012-09-12");
			// items.add("2012-10-07");
			// items.add("2012-10-15");
			// items.add("2012-10-20");
			// items.add("2012-11-30");
			// items.add("2012-11-28");
			// }

			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};

	public DatePickerDialog disp() {

		// Calendar dueDateCalendar = Calendar.getInstance();

		int y = month.get(Calendar.YEAR);
		int m = month.get(Calendar.MONTH);
		int d = month.get(Calendar.DAY_OF_MONTH);
		Log.d("year, month, day", y + "->" + m + "->" + d);

		DatePickerDialog dlg = new DatePickerDialog(mActivity, selectDate, y,
				m, d);
		int day = mActivity.getResources().getIdentifier("android:id/day",
				null, null);
		if (day != 0) {
			View dayPicker = dlg.getDatePicker().findViewById(day);
			if (dayPicker != null) {
				dayPicker.setVisibility(View.GONE);
			}
		}

		return dlg;
	}

	private void selDate() {

		int mMonth = getCalendar.get(Calendar.MONTH);
		int myear = getCalendar.get(Calendar.YEAR);

		String myFormat = "MMMM yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//		Toast.makeText(mActivity, "" + sdf.format(getCalendar.getTime()),
//				Toast.LENGTH_SHORT).show();
		title.setText(sdf.format(getCalendar.getTime()));

		Log.d("month", "" + mMonth);
		Log.d("year", "" + myear);

		month.set(Calendar.YEAR, myear);
		month.set(Calendar.MONTH, mMonth);

		refreshCalendar();

	}
	
	public String getSelectDate() {
		String sdate = selectDay + "/" + selectMonth + "/"
				+ selectYear;
		return sdate;
	}

}
