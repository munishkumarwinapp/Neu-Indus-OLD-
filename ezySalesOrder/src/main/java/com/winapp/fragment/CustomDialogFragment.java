package com.winapp.fragment;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SearchView;
import com.winapp.SFA.R;
import com.winapp.model.Attendance;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by user on 03-Sep-16.
 */
public class CustomDialogFragment extends DialogFragment implements SearchView.OnQueryTextListener {

    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private Bundle mBundle;
    private CustomerAdapter mCustomerAdapter;
    private ImageView mClose;
    private OnDialogCompletionListener listener;

    public interface OnDialogCompletionListener {
        public void OnDialogCompletionListener(String code, String name);
    }

    public void OnDialogCompletionListener(OnDialogCompletionListener listener) {
        this.listener = listener;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mSearchView = (SearchView) view.findViewById(R.id.searchView);
        mClose = (ImageView) view.findViewById(R.id.close);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mBundle = this.getArguments();
        ArrayList<Attendance> mCustomerArr = (ArrayList<Attendance>) mBundle.getSerializable("CustomerData");
        Log.d("size", "--->" + mCustomerArr.size());
        setupSearchView();
        mCustomerAdapter = new CustomerAdapter(getActivity(), mCustomerArr);
        mRecyclerView.setAdapter(mCustomerAdapter);
        mCustomerAdapter.SetOnItemClickListener(new CustomerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Attendance mAttendance= mCustomerAdapter.getItem(position);
                mSubmitDialog(mAttendance.getCode(), mAttendance.getName());

            }
        });
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        return view;

    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("User Search..");
    }

    public void mSubmitDialog(final String code, final String name) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    ((Activity) getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.OnDialogCompletionListener(code, name);
                            }
                        }
                    });
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();
    }

    public void dismiss() {
        getDialog().dismiss();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mCustomerAdapter.filter(newText);
        return false;
    }
}

class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {
    private OnItemClickListener mItemClickListener;
    private List<Attendance> listItems, filterList;
    private Context context;


    public CustomerAdapter(Context context, List<Attendance> listItems) {
        this.listItems = listItems;
        this.context = context;
        this.filterList = new ArrayList<Attendance>();
        // we copy the original list to the filter list and use it for setting row values
        this.filterList.addAll(this.listItems);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mNameTxtV.setText(filterList.get(position).getName());
//            holder.mNameTxtV.setText(filterList.get(position).getName());
    }

    public Attendance getItem(int position) {
        return filterList.get(position);
    }

    @Override
    public int getItemCount() {
        return (null != filterList ? filterList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNameTxtV;

        public ViewHolder(View view) {
            super(view);
            mNameTxtV = (TextView) view.findViewById(R.id.name);
//                mNameTxtV = (TextView) view.findViewById(R.id.name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void filter(final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Clear the filter list
                filterList.clear();
                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {
                    filterList.addAll(listItems);
                } else {
                    // Iterate in the original List and add it to filter list...
                    for (Attendance mAttendance : listItems) {
                        if (mAttendance.getCode().toLowerCase().contains(text.toLowerCase()) ||
                                mAttendance.getCode().toLowerCase().contains(text.toLowerCase())) {
                            // Adding Matched items
                            filterList.add(mAttendance);
                        }
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Notify the List that the DataSet has changed...
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }
}



/*import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.winapp.adapter.DialogFragmentAdapter;
import com.winapp.ezySalesOrder.R;
import com.winapp.model.Attendance;

import java.util.ArrayList;
import java.util.List;


*//**
 * Created by user on 03-Sep-16.
 *//*
public class CustomDialogFragment extends DialogFragment implements SearchView.OnQueryTextListener {

    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private Bundle mBundle;
    private DialogFragmentAdapter mCustomerAdapter;
    private ImageView mClose;
    private OnDialogCompletionListener listener;

    public interface OnDialogCompletionListener {
        public void OnDialogCompletionListener(String code, String name);
    }

    public void OnDialogCompletionListener(OnDialogCompletionListener listener) {
        this.listener = listener;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mSearchView = (SearchView) view.findViewById(R.id.searchView);
        mClose = (ImageView) view.findViewById(R.id.close);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mBundle = this.getArguments();
        ArrayList<Attendance> mCustomerArr = (ArrayList<Attendance>) mBundle.getSerializable("CustomerData");
        Log.d("size", "--->" + mCustomerArr.size());
        setupSearchView();
        mCustomerAdapter = new DialogFragmentAdapter(getActivity(), mCustomerArr);
        mRecyclerView.setAdapter(mCustomerAdapter);
        mCustomerAdapter.SetOnItemClickListener(new DialogFragmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Attendance mAttendance= mCustomerAdapter.getItem(position);
                mSubmitDialog(mAttendance.getCode(), mAttendance.getName());

            }
        });
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        return view;

    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("User Search..");
    }

    public void mSubmitDialog(final String code, final String name) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    ((Activity) getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.OnDialogCompletionListener(code, name);
                            }
                        }
                    });
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();
    }

    public void dismiss() {
        getDialog().dismiss();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mCustomerAdapter.filter(newText);
        return false;
    }
}*/

