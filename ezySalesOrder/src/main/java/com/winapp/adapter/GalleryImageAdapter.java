package com.winapp.adapter;
import java.util.List;

import com.winapp.SFA.R;
import com.winapp.model.Product;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryImageAdapter extends BaseAdapter {

	private Activity context;

	private static ImageView imageView;

	private List<Product> subImages;

	private static ViewHolder holder;

	public GalleryImageAdapter(Activity context, List<Product> subImages) {

		this.context = context;
		this.subImages = subImages;
		//Thread.currentThread();
		//Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
	}

	@Override
	public int getCount() {
		return subImages.size();
	}

	@Override
	public Product getItem(int position) {
		return subImages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			holder = new ViewHolder();

			imageView = new ImageView(this.context);

			//imageView.setPadding(3, 3, 3, 3);

			convertView = imageView;

			holder.imageView = imageView;

			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		 Product product = subImages.get(position);
		 String productSubImages = product.getProductImage();

		if(productSubImages!=null){
			decodeBase64File(productSubImages,holder.imageView);
		}else{
			holder.imageView.setImageResource(R.mipmap.no_image);
		}
//		 if(productSubImages!=null && !productSubImages.isEmpty()){
//			   try{
//		   byte[] imageAsBytes = Base64.decode(productSubImages.getBytes(), Base64.DEFAULT);
//		//   holder.productImage.setImageBitmap(
//		 //  BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
//
//		   BitmapFactory.Options options = new BitmapFactory.Options();
//		   options.inSampleSize = 2;
//		   Bitmap bmp =  BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length, options);
//
//		   Bitmap scaledBitmap = scaleDown(bmp,100, true);
//		   holder.imageView.setImageBitmap(scaledBitmap);
//			   }
//			   catch(OutOfMemoryError e){
//				   e.printStackTrace();
//			   }catch (Exception e) {
//					 e.printStackTrace();
//				}
//		   }
//		   else{
//			   holder.imageView.setImageResource(R.mipmap.no_image);
//		     }
		//holder.imageView.setImageDrawable(subImages.get(position));

		holder.imageView.setScaleType(ImageView.ScaleType.CENTER);
		holder.imageView.setLayoutParams(new Gallery.LayoutParams(150, 90));

		return imageView;
	}

	public void decodeBase64File(String imageString,ImageView imageView) {
		try{
			byte[] encodeByte = Base64.decode(imageString, Base64.DEFAULT);

			/* There isn't enough memory to open up more than a couple camera photos */
			/* So pre-scale the target bitmap into which the file is decoded */

			/* Get the size of the ImageView */
			int targetW = imageView.getWidth();
			int targetH = imageView.getHeight();

			/* Get the size of the image */
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			//BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
			Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,encodeByte.length, bmOptions);
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;

			/* Figure out which way needs to be reduced less */
			int scaleFactor = 1;
			if ((targetW > 0) || (targetH > 0)) {
				scaleFactor = Math.min(photoW/targetW, photoH/targetH);
			}

			/* Set bitmap options to scale the image decode target */
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;
			bmOptions.inPurgeable = true;

			/* Decode the JPEG file into a Bitmap */
			//	Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
			bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length, bmOptions);

//			Bitmap bitmaprst = rotate(bitmap,90);
			Bitmap scaledBitmap = scaleDown(bitmap,155, true);

			imageView.setImageBitmap(scaledBitmap);
		}catch (OutOfMemoryError e){
			e.printStackTrace();
		}
	}

	private static class ViewHolder {
		ImageView imageView;
	}
	public Bitmap scaleDown(Bitmap realImage, float maxImageSize,
	        boolean filter) {
	    float ratio = Math.min(
	            maxImageSize / realImage.getWidth(),
	            maxImageSize / realImage.getHeight());
	    int width = Math.round(ratio * realImage.getWidth());
	    int height = Math.round(ratio * realImage.getHeight());

	    Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
	            height, filter);
	    return newBitmap;
	}

}
