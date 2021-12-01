package com.winapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.winapp.SFA.R;
import com.winapp.model.Product;


public class CatSubAdapter extends ArrayAdapter<Product> {

    Context context;
    int resource, textViewResourceId;
    List<Product> items, tempItems, suggestions;

    public CatSubAdapter(Context context, int resource, int textViewResourceId, List<Product> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<Product>(items); // this makes the difference.
        suggestions = new ArrayList<Product>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autotext_listitem, parent, false);
        }
        Product product = items.get(position);
        if (product != null) {
            TextView lblName = (TextView) view.findViewById(R.id.textView_titlevalue);
            if (lblName != null)
                lblName.setText(product.getDescription());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Product) resultValue).getDescription();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Product product : tempItems) {
                    if (product.getCode().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase())||product.getDescription().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(product);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            @SuppressWarnings("unchecked")
			List<Product> filterList = (ArrayList<Product>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Product product : filterList) {
                    add(product);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
