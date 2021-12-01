package com.winapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.model.User;

import java.util.ArrayList;

/**
 * Created by user on 02-Mar-17.
 */

public class UserAdapter extends BaseAdapter {

    private Context activity;
    private ArrayList<User> data;
    private static LayoutInflater inflater = null;
    private View vi;
    private ViewHolder viewHolder;

    public UserAdapter(Context activity,ArrayList<User> item){
        this.activity = activity;
        this.data = item;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public User getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        vi= convertView;
        final int pos = position;
        User user = getItem(pos);
        if(convertView == null){
            vi = inflater.inflate(R.layout.listview_item,null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) vi.findViewById(R.id.name);
            viewHolder.checkBox = (CheckBox) vi.findViewById(R.id.checkbox);
             vi.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) vi.getTag();
        }
        viewHolder.name.setText(user.getName());
        if(user.isCheckbox()){
            viewHolder.checkBox.setChecked(true);
        }else{
            viewHolder.checkBox.setChecked(false);
        }

        return vi;
    }
    public void setCheckBox(int position){
        User user = data.get(position);
        user.setCheckbox(!user.isCheckbox());
        notifyDataSetChanged();
    }
    public class ViewHolder{
        TextView name;
        CheckBox checkBox;
    }
}
