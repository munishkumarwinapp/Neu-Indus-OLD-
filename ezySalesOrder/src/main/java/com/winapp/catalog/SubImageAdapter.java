package com.winapp.catalog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 19-Jul-17.
 */

public class SubImageAdapter extends ArrayAdapter<Product> {
    Context context;
    int layoutResourceId;
    private int selectedPosition = -1;
    List<Product> data=new ArrayList<Product>();
    public SubImageAdapter(Context context, int layoutResourceId, List<Product> productSubImagesList) {
        super(context, layoutResourceId, productSubImagesList);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = productSubImagesList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ImageHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ImageHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.title);
            holder.imgIcon = (ImageView)row.findViewById(R.id.image);



            row.setTag(holder);

        }
        else
        {
            holder = (ImageHolder)row.getTag();
        }

        // Product myImage = data.get(position);
        // holder.txtTitle.setVisibility(View.GONE);
        //   int outImage=myImage.getProductImage();
        // holder.imgIcon.setImageResource(outImage);
        Product product = data.get(position);
        String productSubImages = product.getProductImage();
        if(productSubImages!=null && !productSubImages.isEmpty()){
            try{
                byte[] imageAsBytes = Base64.decode(productSubImages.getBytes(), Base64.DEFAULT);
                //   holder.productImage.setImageBitmap(
                //  BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bmp =  BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length, options);
                holder.imgIcon.setImageBitmap(bmp);
            }
            catch(OutOfMemoryError e){
                e.printStackTrace();
            }
        }
        else{
            //holder.imgIcon.setImageResource(R.mipmap.no_image);
        }




        return row;

    }
    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }
    static class ImageHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}


