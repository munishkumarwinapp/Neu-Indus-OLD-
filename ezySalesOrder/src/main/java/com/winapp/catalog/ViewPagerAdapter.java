package com.winapp.catalog;

import java.util.ArrayList;
import java.util.HashMap;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.winapp.catalog.BaseFragment;
import com.winapp.model.Customer;
import com.winapp.model.Product;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	private BaseFragment mFragment;
	private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
	private ImageButton mDownloadIcon, mChangeView,mCartIcon,mFilterIcon,mSearchIcon,mCartSaveIcon,mListingSearchIcon,mCartClearAll,mClose,mBack;
	private TextView mCartText;
	private ArrayList<HashMap<String, String>> mCustomerArrHm;
	private ArrayList<Customer> mCustomerArrList;
//	private EditText mSearchEd;
	private ArrayList<Product> mProductList;
	private HashMap<String, ImageButton> mHashMapIcon;
	private HashMap<String, TextView> mHashMapTxtV;
	private HashMap<String, EditText> mHashMapEdt;

	public ViewPagerAdapter(FragmentManager fm/*,
			ArrayList<HashMap<String, String>> customerArrHm,
			ArrayList<Customer> customerArrList, ArrayList<Product> productList,TextView mCartText,
			EditText mSearchEd, HashMap<String, ImageButton> mHashMapBtn*/) {
		 super(fm);		 
	  
		/* mCustomerArrHm = new ArrayList<HashMap<String,String>>();
		mCustomerArrList = new ArrayList<Customer>();
		mProductList = new ArrayList<Product>();		
		
		this.mCartText = mCartText;
		//this.mSearchEd = mSearchEd;
		this.mCustomerArrHm = customerArrHm;		
		this.mCustomerArrList = customerArrList;
		this.mProductList = productList;
		this.mDownloadIcon = mHashMapBtn.get("DownloadIcon");
		this.mChangeView = mHashMapBtn.get("ChangeView");
		this.mCartIcon = mHashMapBtn.get("CartIcon");
		this.mFilterIcon = mHashMapBtn.get("FilterIcon");
		this.mSearchIcon = mHashMapBtn.get("SearchIcon");
		this.mCartSaveIcon = mHashMapBtn.get("CartSaveIcon");
		this.mListingSearchIcon = mHashMapBtn.get("ListingSearchIcon");
		this.mCartClearAll = mHashMapBtn.get("CartClearAll");	
		this.mClose = mHashMapBtn.get("Close");
		this.mBack =  mHashMapBtn.get("Back");*/
				
		
	}

	@Override
	public Fragment getItem(int position) {
		mHashMapIcon = new HashMap<>();
		mHashMapTxtV = new HashMap<>();
		mHashMapEdt = new HashMap<>();
		switch (position) {
		case 0:
			// First Fragment of First Tab
		//	mHashMapIcon.put("DownloadIcon",mDownloadIcon);
			//mFragment = new CatalogCustomerFragment(mDownloadIcon, mCustomerArrList);
			mFragment = CatalogCustomerFragment.newInstance(/*mHashMapIcon,mCustomerArrList*/);
			break;
		case 1:
			// First Fragment of Second Tab
//			mHashMapIcon.put("ChangeView",mChangeView);
//			mHashMapIcon.put("CartIcon",mCartIcon);
//			mHashMapIcon.put("FilterIcon",mFilterIcon);
//			mHashMapIcon.put("SearchIcon",mSearchIcon);
//			mHashMapTxtV.put("CartText",mCartText);
//			//mHashMapEdt.put("SearchEdit",mSearchEd);
//			mHashMapIcon.put("Close",mClose);
//			mHashMapIcon.put("Back",mBack);
			mFragment = CatalogProductFragment.newInstance(false/*mHashMapIcon,mHashMapTxtV,mHashMapEdt,mProductList*/);
		//	mFragment = new CatalogProductFragment(mChangeView,mCartIcon,mFilterIcon,mSearchIcon,mCartText,mSearchEd,mClose,mBack,mProductList);
			break;
		case 2:
			// First Fragment of Third Tab
			//mFragment = new CatalogCartFragment(mCartSaveIcon,mCartClearAll,mCustomerArrList,mCustomerArrHm);
//			mHashMapIcon.put("CartSaveIcon",mCartSaveIcon);
//			mHashMapIcon.put("CartClearAll",mCartClearAll);
			mFragment = CatalogCartFragment.newInstance(/*mHashMapIcon,mCustomerArrList,mCustomerArrHm*/);
			break;
		case 3:
			// First Fragment of Fourth Tab
		//	mHashMapIcon.put("ListingSearchIcon",mListingSearchIcon);
			//mFragment = new CatalogListingFragment(mListingSearchIcon,mCustomerArrHm);
			mFragment = CatalogListingFragment.newInstance(/*mHashMapIcon,mCustomerArrHm*/);
			break;
		default:
			mFragment = null;
			break;
		}

		return mFragment;
	}

	@Override
	public int getCount() {
		return 4;
	}

	/**
	 * On each Fragment instantiation we are saving the reference of that
	 * Fragment in a Map It will help us to retrieve the Fragment by position
	 * 
	 * @param container
	 * @param position
	 * @return
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = (Fragment) super.instantiateItem(container,
				position);
		registeredFragments.put(position, fragment);
		return fragment;
	}

	/**
	 * Remove the saved reference from our Map on the Fragment destroy
	 * 
	 * @param container
	 * @param position
	 * @param object
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		registeredFragments.remove(position);
		super.destroyItem(container, position, object);
	}

	/**
	 * Get the Fragment by position
	 * 
	 * @param position
	 *            tab position of the fragment
	 * @return
	 */
	public Fragment getRegisteredFragment(int position) {
		return registeredFragments.get(position);
	}
}
