package com.winapp.adapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.sot.Area;



public class AreaAdapter extends BaseAdapter {
    private List<Area> mData = new ArrayList<Area>();
    private List<Area> dbData = new ArrayList<Area>();
    private int mResource;
    private LayoutInflater mInflater;
    private int selectedPosition = -1;
    //private Context mcontext;

    
    public AreaAdapter(Context context, int resource, List<Area> data,List<Area> dbdata) {
        mData = data;   
        dbData =dbdata;
     //   mcontext = context;
        mResource = resource;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Area getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Area area = getArea(position);
        /* Area dbarea = new Area();
        if(!dbData.isEmpty()){
        dbarea = dbData.get(position);
        }*/
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResource, parent, false);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);
			holder.column1 = (TextView) convertView.findViewById(R.id.areaCode);
			holder.column2 = (TextView) convertView.findViewById(R.id.areaName);
			
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
		
			holder.column1.setText(area.getAreacode());
			holder.column2.setText(area.getAreaname());
			holder.checkbox.setVisibility(View.VISIBLE);
			
			 holder.checkbox.setOnCheckedChangeListener(myCheckChangList);
			 holder.checkbox.setTag(position);
			 if(dbData.isEmpty()){
				 holder.checkbox.setChecked(false);
			 }
			 else{
				 for(Area p :dbData){
					 if(area.getAreacode().matches(p.getAreacode())){
						 holder.checkbox.setChecked(true);
					 }
				 }
				 
			 }
			 
			 holder.checkbox.setChecked(area.isBox());
	       
        return convertView;
    }
    Area getArea(int position) {
		return (getItem(position));
	}
    public ArrayList<Area> getBox() {
		ArrayList<Area> box = new ArrayList<Area>();
		for (Area p : mData) {
			if (p.box)
				box.add(p);
		}
		return box;
	}
    OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			getItem((Integer) buttonView.getTag()).box = isChecked;
		}
	};
   

    class ViewHolder {
    	TextView column1;
		TextView column2;
		
		
		CheckBox checkbox;
    }
}
/*import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winapp.adapter.CartAdapter.ViewHolder;
import com.winapp.ezySalesOrder.R;
import com.winapp.sot.Area;
import com.winapp.sot.SO;

public class AreaAdapter extends BaseAdapter {
	 
    Context context;
    List<Area> items = new ArrayList<Area>();
    int resourceId ;
    public AreaAdapter(Context context, int resourceId,
            List<Area> items) {
    	super();
    	this.resourceId = resourceId;
    	this.items = items;
        this.context = context;
    }

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Area getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
    private view holder class
    private class ViewHolder {
        TextView areaCode;
        TextView areaName;
    }
     
    public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
       //Area area = getItem(position);
       final Area area = items.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(resourceId, parent, false);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.area_dialog_listitem, null);
            holder = new ViewHolder();            
            holder.areaCode = (TextView) convertView.findViewById(R.id.areaCode);
            holder.areaName = (TextView) convertView.findViewById(R.id.areaName);
            convertView.setTag(holder);
        } else{
        	  holder = (ViewHolder) convertView.getTag();
        }
          
        
        holder.areaCode.setText(area.getAreacode());     
        holder.areaName.setText(area.getAreaname());
       
         
        return convertView;
    }
}*/