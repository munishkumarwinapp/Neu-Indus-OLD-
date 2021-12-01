package com.winapp.trackwithmap;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.model.ScheduleDate;

import java.util.ArrayList;

/**
 * Created by USER on 18/9/2017.
 */

public class ScheduleDateAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<ScheduleDate> dates;
    private LayoutInflater inflater;
    private int currentItemPos;

    public ScheduleDateAdapter(Context context, ArrayList<ScheduleDate> objects) {
        this.mContext = context;
        this.dates = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCurrentItem(int item) {
        this.currentItemPos = item;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewHolder holder;

        ScheduleDate date = this.dates.get(position);

        View convertView = inflater.inflate(R.layout.item_schedule_date,
                container, false);
        holder = new ViewHolder();

        holder.dateTextView = (TextView) convertView
                .findViewById(R.id.date_text);
        holder.dayTextview = (TextView) convertView
                .findViewById(R.id.date_day);
        holder.monthTextView = (TextView) convertView
                .findViewById(R.id.date_month);

        holder.outerLayout = (LinearLayout) convertView
                .findViewById(R.id.date_outer_layout);

        Log.d("value", String.valueOf(position));

        convertView.setTag(Integer.valueOf(position));

        holder.dateTextView.setText(date.getDate());
        holder.dayTextview.setText(date.getDay());
        holder.monthTextView.setText(date.getMonth());

        Log.d("Date", "" + date.getDate());
        Log.d("Day", "" + date.getDay());
        Log.d("Month", "" + date.getMonth());

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((DeliveryOrderNewHeader) mContext).onPagerItemClick(v,
                        (Integer) v.getTag());


            }
        });

        if (position == currentItemPos) {
           // holder.outerLayout.setBackgroundResource(R.drawable.tab_green);
            holder.dateTextView.setTextColor(Color.parseColor("#ff33b5e5"));
            holder.dayTextview.setTextColor(Color.parseColor("#ff33b5e5"));
            holder.monthTextView.setTextColor(Color.parseColor("#ff33b5e5"));
        } else {
          //  holder.outerLayout.setBackgroundResource(R.mipmap.tab_bg);
            holder.dateTextView.setTextColor(Color.parseColor("#000000"));
            holder.dayTextview.setTextColor(Color.parseColor("#000000"));
            holder.monthTextView.setTextColor(Color.parseColor("#000000"));
        }

        ((ViewPager) container).addView(convertView);

        return convertView;
    }

    private class ViewHolder {
        private TextView monthTextView;
        private TextView dayTextview;
        private TextView dateTextView;

        private LinearLayout outerLayout;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // TODO Auto-generated method stub
        return view == (object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        ((ViewPager) container).removeView(view);
        view = null;
    }

    public float getPageWidth(int position) {
        return 0.2f;
    }


}
