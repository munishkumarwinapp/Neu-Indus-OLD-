package com.winapp.sot;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.winapp.SFA.R;

public class Products extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(false);
		getActivity().getActionBar().setIcon(R.drawable.ic_menu);
		View rootView = inflater.inflate(R.layout.products_fragment, container,
				false);
		return rootView;
	}

}
