package com.winapp.catalog;


import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment implements OnBackPressListener {

	@Override
	public boolean onBackPressed() {
		return new BackPressImpl(this).onBackPressed();
	}	

}
