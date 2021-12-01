package com.winapp.catalog;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.TransactionTooLargeException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.adapter.SearchAdapter;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.helper.Catalog;
import com.winapp.helper.Constants;
import com.winapp.helper.XMLParser;
import com.winapp.model.Product;
import com.winapp.offline.OfflineDatabase;
import com.winapp.printer.UIHelper;
import com.winapp.sot.FilterCS;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sotdetails.DBCatalog;
import com.winapp.util.EndlessScrollListener;
import com.winapp.util.XMLAccessTask;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 06-Jul-17.
 */

public class CatalogProductImage  extends BaseFragment {

    private GridView mGridView;
    private ArrayList<Product> productList, loadmoreProduct;
    // private ProgressBar load_more;
    private ProductMainImageAdapter mainImageAdapter;
    // XML node keys
    private static final String KEY_PRODUCTCODE = "ProductCode";
    private static final String KEY_PRODUCTNAME = "ProductName";
    private static final String KEY_CATEGORYCODE = "CategoryCode";
    private static final String KEY_SUBCATEGORYCODE = "SubCategoryCode";
    private static final String KEY_UOMCODE = "UomCode";
    private static final String KEY_PCSPERCARTON = "PcsPerCarton";
    private static final String KEY_WHOLESALEPRICE = "WholeSalePrice";
    private static final String KEY_PRODUCTIMAGE = "ProductImage";
    private static final String KEY_RETAILPRICE = "RetailPrice";
    private static final String KEY_SPECIFICATION = "Specification";
    private HashMap<String, String> params;
    private int mPosition = 0, orientation, totalItem,/* currentPage = -1, */
            pageNo = 0;
    private UIHelper helper;
    private String mkeyActionDoneStr = "", mCategory = "",
            mChangingView = "ListView", mSubcategory = "", valid_url = "",
            mCustomerGroupCode = "", jsonString = null, mDialogStatus = "", mCustomerCode = "";
    private ViewPager mPager;
    private View view, keyViewActionDone;
//    private ImageButton mChangeView, mCartIcon, mFilterIcon, mSearchIcon,
//            mClose, mBack;
    private double screenInches;
    private CarouselFragment mCarouselFragment;
    private OfflineDatabase offlineDatabase;
    private JSONObject jsonResponse;
    private JSONArray jsonMainNode;
    private EditText mSearchEd, mEditValue;
    private TextView mCartTxt;
    private FilterCS filtercs;
    private CatalogProductDetailFragment catalogproductdetailfragment;
    private LinearLayout mCatalogLayout, load_more, mGridLayout, mSearchLayout;
    private ArrayList<Product> mProductList;
    private SearchAdapter mSearchAdapter;
    private ListView mListView;
    private boolean isScrollListener = true;
    private String catalogSaveType = "";

    public CatalogProductImage() {
        // Required empty public constructor
    }

    public static CatalogProductImage newInstance(/*HashMap<String, ImageButton> mHashMapIcon, HashMap<String, TextView> mHashMapTxtV,HashMap<String, EditText> mHashMapEdt,ArrayList<Product> productList*/) {
        CatalogProductImage frag = new CatalogProductImage();
//        Bundle args = new Bundle();
//        args.putSerializable("CatalogIcon", mHashMapIcon);
//        args.putSerializable("CatalogTextV", mHashMapTxtV);
//        args.putSerializable("CatalogEdtV", mHashMapEdt);
//        args.putSerializable("ProductObjArr", productList);
//        frag.setArguments(args);
        return frag;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_catalog, container, false);
        mProductList = new ArrayList<Product>();
//        HashMap<String ,ImageButton> hmIcon= (HashMap<String ,ImageButton>) getArguments().getSerializable("CatalogIcon");
//        HashMap<String ,TextView> hmTextV= (HashMap<String ,TextView>) getArguments().getSerializable("CatalogTextV");
//        HashMap<String ,EditText> hmEdtV = (HashMap<String ,EditText>) getArguments().getSerializable("CatalogEdtV");
//        mProductList = (ArrayList<Product>)  getArguments().getSerializable("ProductObjArr");
//        mChangeView = hmIcon.get("ChangeView");
//        mCartIcon = hmIcon.get("CartIcon");
//        mFilterIcon = hmIcon.get("FilterIcon");
//        mSearchIcon = hmIcon.get("SearchIcon");
//        mClose = hmIcon.get("Close");
//        mBack = hmIcon.get("Back");
//        mCartTxt =  hmTextV.get("CartText");
//        mSearchEd =  hmEdtV.get("SearchEdit");



        mGridView = (GridView) view.findViewById(R.id.gridViewCustom);
        mListView = (ListView) view.findViewById(R.id.listView);
        // Object Initialization
        helper = new UIHelper(getActivity());
        params = new HashMap<String, String>();
        productList = new ArrayList<Product>();
        filtercs = new FilterCS(getActivity());
        mainImageAdapter = new ProductMainImageAdapter();
        mCarouselFragment = new CarouselFragment();
        // DB Initialization
        FWMSSettingsDatabase.init(getActivity());
        // Get URL from DB
        valid_url = FWMSSettingsDatabase.getUrl();
        // Get CustomerCode from Pojo class
        mCustomerGroupCode = Catalog.getCustomerGroupCode();
        offlineDatabase = new OfflineDatabase(getActivity());

        mDialogStatus = mCarouselFragment.checkInternetStatus(getActivity());
        Log.d("checkInternetStatus", "" + mDialogStatus);
        params.put("ProductCode", "");
        params.put("CategoryCode", mCategory);
        params.put("SubCategoryCode", mSubcategory);
        params.put("CustomerGroupCode", mCustomerGroupCode);
        params.put("PageNo", String.valueOf(pageNo));
        params.put("CustomerCode", mCustomerCode);
        params.put("TranType", catalogSaveType);
        loadProductMainImage(params);

        return view;
    }

    @Override
    public void onResume() {
        // View ID
        mGridView = (GridView) view.findViewById(R.id.gridViewCustom);
        load_more = (LinearLayout) view.findViewById(R.id.load_more);
        mPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        mListView = (ListView) view.findViewById(R.id.listView);
        mCatalogLayout = (LinearLayout) view.findViewById(R.id.catalog_layout);
        mGridLayout = (LinearLayout) view.findViewById(R.id.gridView_layout);
        mSearchLayout = (LinearLayout) view.findViewById(R.id.search_layout);
        // Object Initialization

        params = new HashMap<String, String>();
        helper = new UIHelper(getActivity());

        // DB Initialization
        FWMSSettingsDatabase.init(getActivity());
        // Get URL from DB
        valid_url = FWMSSettingsDatabase.getUrl();
        // new SalesOrderWebService(valid_url);

        String catalogType = FWMSSettingsDatabase.getCatalogTypeStr();
        Log.d("catalogSaveType", catalogType);

        if(catalogType.matches("Invoice")){
            catalogSaveType = "IN";
        }else{
            catalogSaveType="SO";
        }

        // Get CustomerCode from Pojo class
        mCustomerGroupCode = Catalog.getCustomerGroupCode();
        mCustomerCode = Catalog.getCustomerCode();

        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your
                // AdapterView
                String searchCheck = SOCatalogActivity.mSearchEd.getText().toString();
                if(searchCheck.matches("")) {
                    if (isScrollListener) {
                        load_more.setVisibility(View.VISIBLE);
                        params.put("PageNo", String.valueOf(page));
                        params.put("ProductCode", "");
                        params.put("CategoryCode", mCategory);
                        params.put("SubCategoryCode", mSubcategory);
                        params.put("CustomerGroupCode", mCustomerGroupCode);
                        params.put("CustomerCode", mCustomerCode);
                        params.put("TranType", catalogSaveType);
                        loadMoreProducts(params);
                    }
                }else{
                    params.put("ProductCode", "");
                    params.put("CategoryCode", "");
                    params.put("SubCategoryCode", "");
                    params.put("CustomerGroupCode", mCustomerGroupCode);
                    params.put("PageNo", String.valueOf(page));
                    params.put("CustomerCode", mCustomerCode);
                    params.put("TranType", catalogSaveType);
                    params.put("ProductName", searchCheck);
                    loadMoreProducts(params);
                }
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded;
                // false otherwise.
            }
        });

        params.put("ProductCode", "");
        params.put("CategoryCode", mCategory);
        params.put("SubCategoryCode", mSubcategory);
        params.put("CustomerGroupCode", mCustomerGroupCode);
        params.put("PageNo", String.valueOf(pageNo));

        params.put("CustomerCode", mCustomerCode);
        params.put("TranType", catalogSaveType);

        helper.showProgressView(mCatalogLayout);
        loadProductMainImage(params);

        super.onResume();
    }
    private void loadProductMainImage(HashMap<String, String> params) {

        productList.clear();

        new XMLAccessTask(getActivity(), valid_url, "fncGetProductMainImage",
                params, false, new XMLAccessTask.CallbackInterface() {

            @Override
            public void onSuccess(NodeList nl) {

                Log.d("catalog load more", "more more");

                for (int i = 0; i < nl.getLength(); i++) {

                    Log.d("product load", "loading");

                    // creating new HashMap
                    Element e = (Element) nl.item(i);
                    Product product = new Product();
                    // adding each child node to Product Pojo class =>
                    // value
                    Log.d("product p",
                            "pp"
                                    + XMLParser.getValue(e,
                                    KEY_PRODUCTCODE));
                    product.setProductCode(XMLParser.getValue(e,
                            KEY_PRODUCTCODE));
                    product.setProductName(XMLParser.getValue(e,
                            KEY_PRODUCTNAME));
                    product.setCategoryCode(XMLParser.getValue(e,
                            KEY_CATEGORYCODE));
                    product.setSubCategoryCode(XMLParser.getValue(e,
                            KEY_SUBCATEGORYCODE));
                    product.setUom(XMLParser.getValue(e, KEY_UOMCODE));
                    String pcspercarton = XMLParser.getValue(e,
                            KEY_PCSPERCARTON);
                    product.setPcspercarton(pcspercarton);
                    // product.setPcspercarton(parser.getValue(e,
                    // KEY_PCSPERCARTON));
                    product.setWholesalePrice(XMLParser.getValue(e,
                            KEY_WHOLESALEPRICE));
                    product.setProductImage(XMLParser.getValue(e,
                            KEY_PRODUCTIMAGE));
                    product.setSpecification(XMLParser.getValue(e,
                            KEY_SPECIFICATION));
                    String retailPrice = null;
                    retailPrice = XMLParser
                            .getValue(e, KEY_RETAILPRICE);
                    if (retailPrice != null && !retailPrice.isEmpty()) {
                        product.setRetailPrice(XMLParser.getValue(e,
                                KEY_RETAILPRICE));
                    } else {
                        product.setRetailPrice("0");
                    }

                    if (pcspercarton != null && !pcspercarton.isEmpty()) {
                        if (Double.valueOf(pcspercarton) > 1) {

                            double pcs = Double.valueOf(pcspercarton);
                            double carton = pcs * 1;
                            product.setQty(String.valueOf(carton));
                        } else {
                            product.setQty("1");

                        }
                    }
                    product.setCqty("1");
                    product.setLqty("0");

                    // adding all childnode to ArrayList
                    productList.add(product);

                    Log.d("product size", "" + productList.size());
                }

                if (productList.size() > 0) {

                    mainImageAdapter = new ProductMainImageAdapter(
                            getActivity(), R.layout.cataloggriditem,
                            productList);
                    mGridView.setAdapter(mainImageAdapter);
                    mainImageAdapter.notifyDataSetChanged();

                     mGridView.setNumColumns(2);


                } else {
                    // helper.showLongToast(R.string.no_data_found);
                    Log.d("No product found", "No product found");

                }
                helper.dismissProgressView(mCatalogLayout);

            }

            @Override
            public void onFailure(XMLAccessTask.ErrorType error) {
                onError(error);
                helper.dismissProgressView(mCatalogLayout);
            }

        }).execute();

    }
    private void loadMoreProducts(HashMap<String, String> params) {
        loadmoreProduct = new ArrayList<Product>();
        loadmoreProduct.clear();
        new XMLAccessTask(getActivity(), valid_url, "fncGetProductMainImage",
                params, false, new XMLAccessTask.CallbackInterface() {

            @Override
            public void onSuccess(NodeList nl) {
                try {
                    for (int i = 0; i < nl.getLength(); i++) {
                        // creating new HashMap
                        Element e = (Element) nl.item(i);
                        Product product = new Product();
                        // adding each child node to Product Pojo class
                        // => value
                        product.setProductCode(XMLParser.getValue(e,
                                KEY_PRODUCTCODE));
                        product.setProductName(XMLParser.getValue(e,
                                KEY_PRODUCTNAME));
                        product.setCategoryCode(XMLParser.getValue(e,
                                KEY_CATEGORYCODE));
                        product.setSubCategoryCode(XMLParser.getValue(
                                e, KEY_SUBCATEGORYCODE));
                        product.setUom(XMLParser.getValue(e,
                                KEY_UOMCODE));
                        String pcspercarton = XMLParser.getValue(e,
                                KEY_PCSPERCARTON);
                        product.setPcspercarton(pcspercarton);
                        // product.setPcspercarton(parser.getValue(e,
                        // KEY_PCSPERCARTON));
                        product.setWholesalePrice(XMLParser.getValue(e,
                                KEY_WHOLESALEPRICE));
                        product.setProductImage(XMLParser.getValue(e,
                                KEY_PRODUCTIMAGE));
                        product.setSpecification(XMLParser.getValue(e,
                                KEY_SPECIFICATION));
                        product.setRetailPrice(XMLParser.getValue(e,
                                KEY_RETAILPRICE));

                        Log.d("retail",
                                XMLParser.getValue(e, KEY_RETAILPRICE));

                        if (pcspercarton != null
                                && !pcspercarton.isEmpty()) {
                            if (Double.valueOf(pcspercarton) > 1) {
                                // product.setCqty("1");
                                double pcs = Double.valueOf(pcspercarton);
                                double carton = pcs * 1;
                                product.setQty(String.valueOf(carton));
                            } else {
                                product.setQty("1");
                                // product.setCqty("0");
                            }
                        }
                        product.setCqty("1");
                        product.setLqty("0");

                        // adding all childnode to ArrayList
                        loadmoreProduct.add(product);
                    }
                    load_more.setVisibility(View.GONE);
                    productList.addAll(loadmoreProduct);
                    mainImageAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(XMLAccessTask.ErrorType error) {
                onError(error);
            }

        }).execute();

    }

    private void onError(final XMLAccessTask.ErrorType error) {
        new Thread() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (error == XMLAccessTask.ErrorType.NETWORK_UNAVAILABLE) {
                                helper.showLongToast(R.string.error_showing_image_no_network_connection);
                            } else {
                                // helper.showLongToast(R.string.error_showing_image);

                                load_more.setVisibility(View.GONE);
                                mainImageAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        }.start();
    }

    public class ProductMainImageAdapter extends BaseAdapter implements
            Constants {

        private Activity mContext;
        private Product product;
        private LayoutInflater inflater;
        private List<Product> mProductList;
        private int resourceId;
        private String pcspercarton, priceflag = "";
        private ViewHolder holder;
        private DBCatalog dbhelper;
        private HashMap<String, String> productvalues;
        private double qtyFlag = 0, cartonFlag = 0;

        public ProductMainImageAdapter() {
        }

        public ProductMainImageAdapter(Activity context, int resourceId,
                                       List<Product> productList) {
            mContext = context;
            dbhelper = new DBCatalog(context);
            this.mProductList = new ArrayList<Product>();
            this.productvalues = new HashMap<String, String>();
            mProductList.clear();
            this.mProductList = productList;

            this.resourceId = resourceId;
            this.inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            priceflag = SalesOrderSetGet.getCartonpriceflag();

        }

        @Override
        public int getCount() {
            return mProductList.size();
        }

        @Override
        public Product getItem(int position) {
            return mProductList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                holder = new ViewHolder();
                row = inflater.inflate(resourceId, parent, false);
                holder.productName = (TextView) row
                        .findViewById(R.id.productName);
                holder.wholesalePrice = (TextView) row
                        .findViewById(R.id.wholeSalePrice);
                holder.cartonPrice = (TextView) row
                        .findViewById(R.id.cartonPrice);
                holder.productImage = (ImageView) row
                        .findViewById(R.id.productImage);
                holder.qty = (EditText) row.findViewById(R.id.qty);
                holder.qtyMinus = (ImageView) row.findViewById(R.id.qty_minus);
                holder.qtyPlus = (ImageView) row.findViewById(R.id.qty_plus);

                holder.carton = (EditText) row.findViewById(R.id.carton);
                holder.cartonMinus = (ImageView) row
                        .findViewById(R.id.carton_minus);
                holder.cartonPlus = (ImageView) row
                        .findViewById(R.id.carton_plus);

                holder.add = (Button) row.findViewById(R.id.add);
                holder.carton_lbl = (TextView) row
                        .findViewById(R.id.carton_lbl);
                holder.qty_lbl = (TextView) row.findViewById(R.id.qty_lbl);
                holder.loose = (TextView) row.findViewById(R.id.loose);
                holder.pcspercarton = (TextView) row
                        .findViewById(R.id.pcspercarton);

                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            product = getItem(position);
            row.setId(position);

            holder.qty.setId(position);
            holder.qtyMinus.setId(position);
            holder.qtyPlus.setId(position);

            holder.carton.setId(position);
            holder.cartonMinus.setId(position);
            holder.cartonPlus.setId(position);

            holder.add.setId(position);
            holder.productImage.setId(position);

            holder.productName.setText(product.getProductName());
            holder.wholesalePrice.setText(product.getWholesalePrice());
            holder.cartonPrice.setText(product.getRetailPrice());
            holder.carton.setText(product.getCqty());
            holder.carton.setSelection(holder.carton.getText().length());
            holder.loose.setText(product.getLqty());
            holder.qty.setText(product.getQty());
            holder.qty.setSelection(holder.qty.getText().length());

            // taxValue = product.getTaxPerc();

            holder.pcspercarton.setText("PcsPerCarton :"
                    + product.getPcspercarton());

            pcspercarton = product.getPcspercarton();
            if (pcspercarton != null && !pcspercarton.isEmpty()) {
                if (Double.valueOf(pcspercarton) > 1) {
                    holder.carton.setVisibility(View.VISIBLE);
                    holder.carton_lbl.setVisibility(View.VISIBLE);
                    holder.cartonMinus.setVisibility(View.VISIBLE);
                    holder.cartonPlus.setVisibility(View.VISIBLE);
                    holder.qty_lbl.setVisibility(View.VISIBLE);

                } else {
                    holder.carton.setVisibility(View.GONE);
                    holder.carton_lbl.setVisibility(View.GONE);
                    holder.cartonMinus.setVisibility(View.GONE);
                    holder.cartonPlus.setVisibility(View.GONE);
                }
            }

            String productCode = DBCatalog.getProductCodeValue(product
                    .getProductCode());
            if (productCode != null && !productCode.isEmpty()) {

                holder.add.setBackgroundResource(R.drawable.add_cart_selected);
                holder.add.setText("Added");
                holder.add.setTextColor(Color.parseColor("#FFFFFF"));
            } else {

                holder.add.setBackgroundResource(R.drawable.add_cart);
                holder.add.setText("Add");
                holder.add.setTextColor(Color.parseColor("#000000"));
            }
            if (screenInches > 7) {
                if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
                    if (mPosition == position) {
                        if (mkeyActionDoneStr.matches("Qty")) {
                            holder.qty
                                    .setBackgroundResource(R.drawable.edittext_focused);
                            holder.carton
                                    .setBackgroundResource(R.drawable.edittext_normal);
                        } else if (mkeyActionDoneStr.matches("CQty")) {
                            holder.carton
                                    .setBackgroundResource(R.drawable.edittext_focused);
                            holder.qty
                                    .setBackgroundResource(R.drawable.edittext_normal);
                        }
                    } else {
                        holder.qty
                                .setBackgroundResource(R.drawable.edittext_normal);
                        holder.carton
                                .setBackgroundResource(R.drawable.edittext_normal);
                    }

                }
            }
            String base = product.getProductImage();

            /*if (base != null && !base.isEmpty()) {
                try {
					byte[] imageAsBytes = Base64.decode(base.getBytes(),
							Base64.DEFAULT);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0,
							imageAsBytes.length, options);
					holder.productImage.setImageBitmap(bmp);
                } catch (OutOfMemoryError e) {
					*//*byte[] imageAsBytes = Base64.decode(base.getBytes(),
							Base64.DEFAULT);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 8;
					Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0,
							imageAsBytes.length, options);
					holder.productImage.setImageBitmap(bmp);*//*

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                holder.productImage.setImageResource(R.mipmap.no_image);
            }*/
            if (base != null && !base.isEmpty()) {
            try{
                byte[] encodeByte = Base64.decode(base, Base64.DEFAULT);

                int targetW = holder.productImage.getWidth();
                int targetH = holder.productImage.getHeight();

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                //BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,encodeByte.length, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                int scaleFactor = 1;
                if ((targetW > 0) || (targetH > 0)) {
                    scaleFactor = Math.min(photoW/targetW, photoH/targetH);
                }

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                // Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                encodeByte.length, bmOptions);

                //Bitmap bitmaprst = rotate(bitmap,90);

                holder.productImage.setImageBitmap(bitmap);

            }catch (OutOfMemoryError e){
                e.printStackTrace();
            }
            } else {
                holder.productImage.setImageResource(R.mipmap.no_image);
            }

            holder.qty.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent motionevent) {
                    EditText edQty = (EditText) v;
                    orientation = getResources().getConfiguration().orientation;
                    if (screenInches > 7) {
                        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
                            mPosition = v.getId();
                            hideKeyboard(edQty);
                            mEditValue = edQty;
                            mkeyActionDoneStr = "Qty";
                            keyViewActionDone = v;
                            edQty.setFocusable(false);
                            edQty.setFocusableInTouchMode(false);
                            notifyDataSetChanged();
                        } else if (Configuration.ORIENTATION_PORTRAIT == orientation) {
                            Log.d("PORTRAIT", "PORTRAIT");
                            edQty.requestFocus();
                            showKeyboard(edQty);
                        }
                    } else {
                        Log.d("mobile", "mobile");
                        edQty.requestFocus();
                        showKeyboard(edQty);
                    }
                    return false;
                }
            });
            holder.qty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId,
                                              KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        qtyActionDone(v);
                        return true;
                    }
                    return false;
                }
            });
            holder.qtyMinus.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    qtyMinusAction(v);
                }
            });
            holder.qtyPlus.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    qtyPlusAction(v);

                }
            });

            holder.carton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent motionevent) {
                    EditText edCarton = (EditText) v;
                    orientation = getResources().getConfiguration().orientation;
                    if (screenInches > 7) {
                        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
                            mPosition = v.getId();
                            hideKeyboard(edCarton);
                            mEditValue = edCarton;
                            mkeyActionDoneStr = "CQty";
                            keyViewActionDone = v;
                            edCarton.setFocusable(false);
                            edCarton.setFocusableInTouchMode(false);
                            notifyDataSetChanged();
                        } else if (Configuration.ORIENTATION_PORTRAIT == orientation) {
                            edCarton.setFocusable(true);
                            edCarton.setFocusableInTouchMode(true);
                            edCarton.requestFocus();
                            showKeyboard(edCarton);
                        }
                    } else {
                        edCarton.requestFocus();
                        showKeyboard(edCarton);
                    }
                    return false;
                }
            });
            holder.carton
                    .setOnEditorActionListener(new TextView.OnEditorActionListener() {

                        @Override
                        public boolean onEditorAction(TextView v, int actionId,
                                                      KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                cartonActionDone(v);
                                return true;
                            }
                            return false;
                        }
                    });

            holder.cartonMinus.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    cartonMinusAction(v);
                }
            });

            holder.cartonPlus.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    cartonPlusAction(v);
                }
            });
            holder.add.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {

                    String customercode = Catalog.getCustomerCode();

                    if (customercode != null && !customercode.isEmpty()) {

                        final Button b = (Button) v;
                        int position = v.getId();
                        product = getItem(position);

                        String productCode = DBCatalog
                                .getProductCodeValue(product.getProductCode());
                        if (productCode != null && !productCode.isEmpty()) {
                            Toast.makeText(
                                    mContext,
                                    "This product is already added to the cart",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            if ((product.getCqty() != null && !product
                                    .getCqty().isEmpty())
                                    && (product.getQty() != null && !product
                                    .getQty().isEmpty())) {

                                if ((Double.valueOf(product.getCqty()) > 0)
                                        || (0 < Double.valueOf(product
                                        .getQty()))) {

                                    totalCalc(v);
                                    animCartIcon();
                                    b.setText("Added");
                                    b.setBackgroundResource(R.drawable.add_cart_selected);
                                    b.setTextColor(Color.parseColor("#FFFFFF"));

                                } else {

                                    Log.d("product.getCqty()",
                                            "" + product.getCqty());

                                    Log.d("product.getQty()",
                                            "" + product.getQty());
                                    Toast.makeText(mContext,
                                            "Please Enter the Value",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("product.getCqty()",
                                        "null" + product.getCqty());

                                Log.d("product.getQty()",
                                        "null" + product.getQty());

                                Toast.makeText(mContext,
                                        "Please Enter the Value",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }

                    } else {
                        mPager.setCurrentItem(0);
                    }

                }
            });

            holder.productImage.setOnClickListener(new View.OnClickListener() {

                @SuppressLint("NewApi")
                @Override
                public void onClick(View v) {
                    try {
                        launchActivity(v);
                    } catch (TransactionTooLargeException e) {
                        e.printStackTrace();
                    }

                }
            });

            return row;

        }

        class ViewHolder {
            private TextView productName, carton_lbl, wholesalePrice,
                    cartonPrice, qty_lbl, loose, pcspercarton;

            private ImageView productImage, qtyMinus, qtyPlus, cartonMinus,
                    cartonPlus;
            private EditText qty, carton;
            // private ImageButton qtyMinus, qtyPlus, cartonMinus, cartonPlus;
            private Button add;
        }

        public void animCartIcon() {
            Cursor cursor = DBCatalog.getCursor();
            int cartSize = cursor.getCount();
            if (cartSize > 0) {
                if (Catalog.isSearchVisible()) {
                    SOCatalogActivity.mCartIcon.setVisibility(View.GONE);
                    mCartTxt.setVisibility(View.GONE);
                } else {
                    SOCatalogActivity.mCartIcon.setVisibility(View.VISIBLE);
                    mCartTxt.setVisibility(View.VISIBLE);
                    ObjectAnimator anim = ObjectAnimator.ofFloat(mCartTxt,
                            "rotationY", 0.0f, 360.0f);
                    anim.setDuration(1000);
                    anim.start();
                    SOCatalogActivity.mCartIcon.setVisibility(View.VISIBLE);
                    mCartTxt.setText(cartSize + "");
                }
            } else {
                mCartTxt.setVisibility(View.GONE);
                mCartTxt.setText("");
            }
        }

        public void launchActivity(View v) throws TransactionTooLargeException {
            if (Catalog.getCustomerCode() != null
                    && !Catalog.getCustomerCode().isEmpty()
                    && Catalog.getCustomerName() != null
                    && !Catalog.getCustomerName().isEmpty()) {


               /* HashMap<String ,ImageButton> mHashMapIcon = new HashMap<>();
                mHashMapIcon.put("CartIcon",mCartIcon);

                HashMap<String, TextView> mHashMapTextView = new HashMap<>();
                mHashMapTextView.put("CartText",mCartTxt);*/

                int position = v.getId();
                product = getItem(position);
                CatalogProductDetails.setProductCode(product.getProductCode());
                CatalogProductDetails.setProductName(product.getProductName());
                CatalogProductDetails
                        .setCategoryCode(product.getCategoryCode());
                CatalogProductDetails.setSubCategoryCode(product
                        .getSubCategoryCode());
                CatalogProductDetails.setUom(product.getUom());
                CatalogProductDetails
                        .setPcsPerCarton(product.getPcspercarton());
                CatalogProductDetails.setWholesalePrice(product
                        .getWholesalePrice());
                CatalogProductDetails
                        .setProductImage(product.getProductImage());
                CatalogProductDetails.setSpecification(product
                        .getSpecification());
                CatalogProductDetails.setRetailPrice(product.getRetailPrice());

                catalogproductdetailfragment = CatalogProductDetailFragment.newInstance(/*
                        mHashMapIcon, mHashMapTextView*/);
                FragmentTransaction transaction = getChildFragmentManager()
                        .beginTransaction();
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragment_mainLayout,
                        catalogproductdetailfragment).commit();
                SOCatalogActivity.mChangeView.setVisibility(View.GONE);
                SOCatalogActivity.mFilterIcon.setVisibility(View.GONE);
                SOCatalogActivity.mSearchIcon.setVisibility(View.GONE);
                SOCatalogActivity.mBack.setVisibility(View.GONE);
                SOCatalogActivity.mClose.setVisibility(View.GONE);
                SOCatalogActivity.mSearchEd.setVisibility(View.GONE);
                Catalog.setChildFragmentVisible(true);
            } else {
                mPager.setCurrentItem(0);
            }

        }

        private void qtyPlusAction(View v) {
            int position = v.getId();
            mPosition = position;
            product = getItem(position);
            if (product.getQty() != null && !product.getQty().isEmpty()) {
                qtyFlag = Double.valueOf(product.getQty().toString());
            } else {
                qtyFlag = 0;
            }
            product.setQty(String.valueOf(++qtyFlag));
            mkeyActionDoneStr = "Qty";
            notifyDataSetChanged();
            qtyCalc(v);
        }

        private void qtyMinusAction(View v) {
            int position = v.getId();
            mPosition = position;
            product = getItem(position);
            if (product.getQty() != null && !product.getQty().isEmpty()) {
                qtyFlag = Double.valueOf(product.getQty().toString());
                if (qtyFlag > 1) {
                    product.setQty(String.valueOf(--qtyFlag));
                } else {
                    product.setQty("1");
                }
            }
            mkeyActionDoneStr = "Qty";
            notifyDataSetChanged();
            qtyCalc(v);
        }

        private void qtyActionDone(View v) {
            EditText edQty = (EditText) v;
            int position = v.getId();
            mPosition = position;
            product = getItem(position);
            product.setQty(edQty.getText().toString());

            notifyDataSetChanged();
            qtyCalc(v);
            hideKeyboard(edQty);
        }

        private void cartonPlusAction(View v) {
            int position = v.getId();
            mPosition = position;
            product = getItem(position);
            if (product.getCqty() != null && !product.getCqty().isEmpty()) {
                cartonFlag = Double.valueOf(product.getCqty().toString());
            } else {
                cartonFlag = 1;
            }
            product.setCqty(String.valueOf(++cartonFlag));
            mkeyActionDoneStr = "CQty";
            notifyDataSetChanged();
            cQtyCalc(v);
        }

        private void cartonMinusAction(View v) {
            int position = v.getId();
            mPosition = position;
            product = getItem(position);
            if (product.getCqty() != null && !product.getCqty().isEmpty()) {
                cartonFlag = Double.valueOf(product.getCqty().toString());
                if (cartonFlag > 1) {
                    product.setCqty(String.valueOf(--cartonFlag));
                } else {
                    product.setCqty("1");
                }
            }
            mkeyActionDoneStr = "CQty";
            notifyDataSetChanged();
            cQtyCalc(v);
        }

        private void cartonActionDone(View v) {
            EditText edCarton = (EditText) v;
            int position = v.getId();
            mPosition = position;
            product = getItem(position);
            product.setCqty(edCarton.getText().toString());

            notifyDataSetChanged();

            cQtyCalc(v);
            hideKeyboard(edCarton);
        }

        private void qtyCalc(View v) {
            double cqty = 0, qty = 0, pcs = 0, lqty = 0;
            int position = v.getId();
            product = getItem(position);

            String quantity = product.getQty();
            String pcspercarton = product.getPcspercarton();

            try {

                if ((pcspercarton != null && !pcspercarton.isEmpty() && !pcspercarton
                        .equals("0"))
                        && (quantity != null && !quantity.isEmpty())) {

                    pcs = Double.valueOf(pcspercarton);

                    qty = Double.valueOf(quantity);

                    if (qty > 0) {
                        cqty = (int) (qty / pcs);
                        lqty = qty % pcs;

                        String ctn = twoDecimalPoint(cqty);
                        String loose = twoDecimalPoint(lqty);

                        product.setLqty(String.valueOf(loose));
                        product.setCqty(String.valueOf(ctn));

                        Log.d("lQtyCalc", "" + loose);
                        Log.d("cQtyCalc", "" + ctn);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (StackOverflowError e) {
                e.printStackTrace();
            }
            notifyDataSetChanged();
        }

        // cQtyCalc Calculation
        private void cQtyCalc(View v) {

            double cqty = 0, pcs = 1, lqty = 0, qty = 0;
            int position = v.getId();
            product = getItem(position);

            String cartonQty = product.getCqty();
            String pcspercarton = product.getPcspercarton();

            try {
                if ((cartonQty != null && !cartonQty.isEmpty())
                        && (pcspercarton != null && !pcspercarton.isEmpty())) {
                    cqty = Double.valueOf(cartonQty);

                    pcs = Double.valueOf(pcspercarton);

                    if (cqty > 0) {
                        qty = cqty * pcs;
                    }

                    String quantity = twoDecimalPoint(qty);

                    Log.d("cQtyCalc", "" + quantity);
                    product.setQty(""+quantity);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } catch (StackOverflowError e) {
                e.printStackTrace();

            }
            notifyDataSetChanged();
        }

        // TotalCalc Calculation
        private void totalCalc(View v) {
            double dTotal = 0.00, dQty = 0.00, dCQty = 0.00, dLQty = 0.00, dPrice = 0.00, dCPrice = 0.00;
            int position = v.getId();
            product = getItem(position);
            String quantity = product.getQty();
            String cqty = product.getCqty();
            String lqty = product.getLqty();
            String wholesaleprice = product.getWholesalePrice();
            String cPrice = product.getRetailPrice();

            if (wholesaleprice != null && !wholesaleprice.isEmpty()) {

            } else {
                wholesaleprice = "0";
            }

            if (cPrice != null && !cPrice.isEmpty()) {

            } else {
                cPrice = "0";
            }

            if (cqty != null && !cqty.isEmpty()) {

            } else {
                cqty = "0";
            }

            if (lqty != null && !lqty.isEmpty()) {

            } else {
                lqty = "0";
            }

            if (quantity != null && !quantity.isEmpty()) {

            } else {
                quantity = "0";
            }

            try {

                priceflag = SalesOrderSetGet.getCartonpriceflag();
                if (priceflag.matches("1")) {

                    dPrice = Double.parseDouble(wholesaleprice);
                    dCPrice = Double.parseDouble(cPrice);

                    dCQty = Double.parseDouble(cqty);
                    dLQty = Double.parseDouble(lqty);

                    dTotal = (dCQty * dCPrice) + (dLQty * dPrice);

                } else {
                    dQty = Double.valueOf(quantity);
                    dPrice = Double.valueOf(wholesaleprice);
                    Log.d("dQty", "" + dQty);
                    Log.d("dPrice", "" + dPrice);
                    dTotal = dQty * dPrice;
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
            Log.d("dTotal", "--" + dTotal);
            taxCalc(position, dTotal);
        }

        public void taxCalc(int position, double dTotal) {
            product = getItem(position);
            double dSubTotal = 0.00, dTaxValue = 0.00, dTaxAmount = 0.00, dNetTotal = 0.00;
            String total = "0.00", tax = "0.000", netTotal = "0.00", subTotal = "0.00";

            String taxType = SalesOrderSetGet.getCompanytax();
            String taxValue = SalesOrderSetGet.getTaxValue();

            if (taxType != null && !taxType.isEmpty()) {

            } else {
                taxType = "Z";
            }
            if (taxValue != null && !taxValue.isEmpty()) {

            } else {
                taxValue = "0.0";
            }

            Log.d("taxType", taxType);
            Log.d("taxValue adapter", taxValue);

            try {

                dSubTotal = dTotal;

                if ((taxType != null && !taxType.isEmpty())
                        && (taxValue != null && !taxValue.isEmpty())) {

                    dTaxValue = Double.parseDouble(taxValue);

                    if (taxType.matches("E")) {

                        dTaxAmount = (dTotal * dTaxValue) / 100;
                        tax = fourDecimalPoint(dTaxAmount);

                        dNetTotal = dTotal + dTaxAmount;
                        netTotal = twoDecimalPoint(dNetTotal);
                    } else if (taxType.matches("I")) {

                        dTaxAmount = (dTotal * dTaxValue) / (100 + dTaxValue);
                        tax = fourDecimalPoint(dTaxAmount);

                        // dNetTotal = dTotal + dTaxAmount;
                        dNetTotal = dTotal;
                        netTotal = twoDecimalPoint(dNetTotal);

                    } else if (taxType.matches("Z")) {

                        // dNetTotal = dTotal + dTaxAmount;
                        dNetTotal = dTotal;
                        netTotal = twoDecimalPoint(dNetTotal);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            total = twoDecimalPoint(dTotal);
            subTotal = twoDecimalPoint(dSubTotal);
            // netTotal = twoDecimalPoint(dNetTotal);

            Log.d("total", "" + total);
            Log.d("subTotal", "" + subTotal);
            Log.d("taxType", "" + taxType);
            Log.d("taxValue", "" + taxValue);
            Log.d("tax", "" + tax);
            Log.d("netTotal", "" + netTotal);

            Log.d("getCqty", "" + product.getCqty());
            Log.d("getLqty", "" + product.getLqty());
            Log.d("getQty", "" + product.getQty());

            int slNo = dbhelper.getCount();
            Log.d("dbhelper.getCount()", "" + slNo);

            String sno = String.valueOf(++slNo);
            productvalues.put(COLUMN_PRODUCT_SLNO, sno);
            productvalues.put(COLUMN_PRODUCT_CODE, product.getProductCode());
            productvalues.put(COLUMN_PRODUCT_NAME, product.getProductName());
            productvalues.put(COLUMN_CARTON_QTY, product.getCqty());
            productvalues.put(COLUMN_LOOSE_QTY, product.getLqty());
            productvalues.put(COLUMN_QUANTITY, product.getQty());
            productvalues.put(COLUMN_PRICE, product.getWholesalePrice());
            productvalues.put(COLUMN_WHOLESALEPRICE,
                    product.getWholesalePrice());
            productvalues.put(COLUMN_UOMCODE, product.getUom());
            productvalues.put(COLUMN_PCSPERCARTON, product.getPcspercarton());
            productvalues.put(COLUMN_TAXTYPE, taxType);
            productvalues.put(COLUMN_TAXVALUE, taxValue);
            productvalues.put(COLUMN_TAX, tax);
            productvalues.put(COLUMN_TOTAL, total);
            productvalues.put(COLUMN_SUB_TOTAL, subTotal);
            productvalues.put(COLUMN_NETTOTAL, netTotal);
            productvalues.put(COLUMN_PRODUCT_IMAGE, product.getProductImage());

            productvalues.put(COLUMN_FOC, "0");
            productvalues.put(COLUMN_ITEM_DISCOUNT, "0");
            productvalues.put(COLUMN_CARTONPRICE, product.getRetailPrice());
            productvalues.put(COLUMN_EXCHANGEQTY, "0");

            DBCatalog.storeCatalog(productvalues);

        }

        public void bitmap() {

        }

        protected void showKeyboard(EditText editText) {
            InputMethodManager inputManager = (InputMethodManager) mContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText,
                    InputMethodManager.SHOW_IMPLICIT);
        }

        public void hideKeyboard(EditText edittext) {
            InputMethodManager inputmethodmanager = (InputMethodManager) mContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmethodmanager.hideSoftInputFromWindow(
                    edittext.getWindowToken(), 0);

        }

        protected String twoDecimalPoint(double d) {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setMinimumFractionDigits(2);
            String tot = df.format(d);
            return tot;
        }

        protected String fourDecimalPoint(double d) {
            DecimalFormat df = new DecimalFormat("#.####");
            df.setMinimumFractionDigits(4);
            String tot = df.format(d);
            return tot;
        }
    }

}
