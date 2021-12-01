package com.winapp.sot;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.imagezoom.ImageAttacher;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.winapp.adapter.Attribute;
import com.winapp.adapter.ColorModifierAdapter;
import com.winapp.adapter.SizeModifierAdapter;
import com.winapp.catalog.SubImageAdapter;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.helper.Constants;
import com.winapp.helper.XMLParser;
import com.winapp.model.Product;
import com.winapp.printer.UIHelper;
import com.winapp.sotdetails.DBCatalog;
import com.winapp.util.ErrorLog;
import com.winapp.util.XMLAccessTask;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by USER on 16/5/2017.
 */

public class ColorAttributeDialog extends DialogFragment {
    private Matrix matrix = new Matrix();
    private  Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    private int mode = NONE;
    // Remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private  float oldDist = 1f;


    private ArrayList<Product> productSubImagesList;
    PointF startPoint = new PointF();
    PointF midPoint = new PointF();
    public ColorAttributeDialog() {

    }

    public interface OnCompletionListener {
        public void onCompleted(String qty, ArrayList<Attribute> AttributeArr);

    }
    public interface DismissDialogListener {
        public void onCompleted();

    }
    private OnCompletionListener listener;

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.listener = listener;
    }

    int qtyFlag = 1;
    Activity mActivity;
    PopupWindow popupWindow1;
    int totalqty, qtyInt;
    private ColorModifierAdapter coloradapter;
    private SizeModifierAdapter sizeadapter;
    private String colorcode, colorname, sizecode, sizename;
//    AlertDialog batDialog;
    private ArrayList<Attribute> colorArr = new ArrayList<>();
    private ArrayList<Attribute> colorArrList = new ArrayList<Attribute>();
    private ArrayList<Attribute> sizeArr = new ArrayList<>();
    TextView mName,title_code;
    String i;

    private ArrayList<Attribute> colournew = new ArrayList<Attribute>();

    EditText batch_codefield, batch_namefield, batchno, bat_cartonQty,
            bat_looseQty, bat_qty, batch_cartonPerQty, bat_foc,
            sl_cartonqty, sl_looseqty, sl_uom, sl_qty, sl_foc, sl_stock,
            sm_total, sm_tax, sm_netTotal;
    private String prodCode, slNo, prodName,slPrice="",
     colourcode, colourname, chcksizecode, chcksizename;

    GridView mSizeImageGrid;

    private String id = "", slUomCode = "", slCartonPerQty = "",catalogCartEdit="",productImageStr="",
            taxType = "", taxValue = "", ss_Cqty, beforeLooseQty,
            priceflag = "", calCarton = "",slid="";
    private EditText sl_codefield, sl_cartonQty, sl_namefield, sl_looseQty,
            sl_price, sl_itemDiscount, sl_total,sl_total_inclusive,
            sl_tax, sl_netTotal, sl_cartonPerQty, sl_cprice, sl_exchange,sladd_qty;
    private LinearLayout uomcperqty_ll, foc_layout, price_txt_layout, img_product_layout,mMainLayout;
    private ImageView expand, mDialogUpdateImgV, mDialogCancelImgV;
    private TextView price_txt,txt_price;
    private double tt, itmDisc = 0 ;
    private TextWatcher cqtyTW, lqtyTW, qtyTW;
    private Cursor cursor;
    private ProductModifierDialogListener mProductModifierDialogListener;
    private DismissDialogListener mDismissDialogListener;
    private boolean isSizeArrEmpty = false;
//    private Bitmap prodimage;
    private ImageView img_product,image;
//    private ErrorLog errorLog;
    private Bitmap bitmap;
    private UIHelper helper;
    private double screenInches;
    private String valid_url;
    static final String ProductImg = "ProductImage";
    private XMLParser parser;
    private SubImageAdapter subImageAdapter;
    private ProgressBar img_load;

    public void setOnProductModifierDialogListener(ProductModifierDialogListener listener) {
        this.mProductModifierDialogListener = listener;
    }

    public void setOnDismissDialogListener(DismissDialogListener listener) {
        this.mDismissDialogListener = listener;
    }

    public void initiatePopupWindow(Activity context, String code, String name,
                                         ArrayList<HashMap<String, String>> colorArray, EditText sladd_qty,
                                    double screenInches) {
        sizeArr.clear();
        colorArr.clear();
        colournew.clear();
        this.mActivity = context;
        this.prodCode = code;
        this.prodName = name;
        this.sladd_qty = sladd_qty;
//        this.prodimage = prodimage;
//        screenInches = displayMetrics();
        this.screenInches= screenInches;
        Log.d("colorArr", "" + colorArray.size());


        for (int j = 0; j < colorArray.size(); j++) {
            colorcode = colorArray.get(j).get("ColorCode");
            colorname = colorArray.get(j).get("ColorName");
            sizecode = colorArray.get(j).get("SizeCode");
            sizename = colorArray.get(j).get("SizeName");

            Log.d("ColorCodeprint", colorcode);
            Attribute mRowItem = new Attribute();
            mRowItem.setName(colorname);
            mRowItem.setCode(colorcode);
            mRowItem.setSizename(sizename);
            mRowItem.setSizecode(sizecode);
            mRowItem.setSizeQty("0");
            mRowItem.setSelected(false);
            sizeArr.add(mRowItem);

        }

        colournew.add(sizeArr.get(0));
        for (Attribute colour : sizeArr) {
            boolean flag = false;
            for (Attribute colorUnique : colournew) {
                if (colorUnique.getCode().equals(colour.getCode())) {
                    flag = true;
                }
            }
            if (!flag)
                colournew.add(colour);
        }

        for (int i = 0; i < colournew.size(); i++) {

            Attribute mRowItem = new Attribute();
            mRowItem.setName(colournew.get(i).getName());
            mRowItem.setCode(colournew.get(i).getCode());
            mRowItem.setColorQty("0");
            mRowItem.setSelected(false);
            mRowItem.setFlag("color");
            colorArrList.add(mRowItem);
            colorArr.add(mRowItem);
        }
    }

    public void setCatalogCartEdit(String  catalogCartEdit){
        this.catalogCartEdit = catalogCartEdit;
    }
    public void setProductImage(String productImageString){
        this.productImageStr = productImageString;
    }

    public void setAttributeArr(Activity context, String sno, String name, String code, ArrayList<Attribute> mColorArr, ArrayList<Attribute> mSizeArr,
                                String slid,double screenInches) {
        colorArr = mColorArr;
        sizeArr = mSizeArr;
        this.mActivity = context;
        this.prodName = name;
        this.prodCode = code;
        this.slNo = sno;
        this.slid = slid;
        this.screenInches= screenInches;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        final View v = inflater
                .inflate(R.layout.dialog_product_modifier, container, false);

        SOTDatabase.init(getActivity());
        if(catalogCartEdit.equalsIgnoreCase("Catalog")){
            intiateCartitem(v);
        }else if (mActivity instanceof SalesSummary) {
            intiateCartitem(v);
        }else if(mActivity instanceof SalesReturnSummary){
            intiateCartitem(v);
        }

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        /*InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getDialog().getWindow().getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(getDialog().getWindow().getCurrentFocus().getWindowToken(), 0);
        }*/
        img_product = (ImageView) v.findViewById(R.id.img_product);
       /* if(prodimage!=null){
            img_product.setImageBitmap(prodimage);
        }*/
        title_code = (TextView) v.findViewById(R.id.title_code);
        batch_codefield = (EditText) v.findViewById(R.id.batch_codefield);
        batch_namefield = (EditText) v.findViewById(R.id.batch_namefield);
        batchno = (EditText) v.findViewById(R.id.batchno);
        //  expirydate = (EditText) v.findViewById(R.id.expirydate);
        bat_cartonQty = (EditText) v.findViewById(R.id.batch_cartonQty);
        bat_looseQty = (EditText) v.findViewById(R.id.batch_looseQty);
        bat_qty = (EditText) v.findViewById(R.id.batch_qty);
        batch_cartonPerQty = (EditText) v.findViewById(R.id.batch_cartonPerQty);
        bat_foc = (EditText) v.findViewById(R.id.batch_foc_qty);
        image = (ImageView) v.findViewById(R.id.image);
        img_product_layout = (LinearLayout) v.findViewById(R.id.img_product_layout);
        mMainLayout = (LinearLayout) v.findViewById(R.id.mMainLayout);
        img_load = (ProgressBar) v.findViewById(R.id.img_load);
        helper = new UIHelper(getActivity());



        if (screenInches > 7) {
            img_product_layout.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
        }else{
            img_product_layout.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
        }
        calCarton = LogOutSetGet.getCalcCarton();

        Button addBtn = (Button) v.findViewById(R.id.add);
        Button mPlusAll = (Button) v.findViewById(R.id.plus_All);
        GridView mColorImageGrid = (GridView) v.findViewById(R.id.colortwowaygridview);
        mSizeImageGrid = (GridView) v.findViewById(R.id.sizetwowaygridview);
        final EditText qty = (EditText) v.findViewById(R.id.edtQty);
        ImageView mClose = (ImageView) v.findViewById(R.id.close_img);
        ImageView mtick = (ImageView) v.findViewById(R.id.tick);
        mName = (TextView) v.findViewById(R.id.lblProdName);
        final TextView totalQty = (TextView) v.findViewById(R.id.total_qty);

        FWMSSettingsDatabase.init(getActivity());
        valid_url = FWMSSettingsDatabase.getUrl();
        if (screenInches > 7) {
            Log.d("screenInches", "" + screenInches);
            v.findViewById(R.id.img_product_load).setVisibility(View.VISIBLE);
            img_product.setVisibility(View.GONE);
            new XMLAccessTask(getActivity(),valid_url, "fncGetProductMainImage",prodCode, new XMLAccessTask.CallbackInterface() {
                @Override
                public void onSuccess(NodeList nl) {
                    Element e = (Element) nl.item(0);
                    String lo = XMLParser.getValue(e, ProductImg);
                    Log.d("imggg", lo);
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inSampleSize = 2;
                    byte[] encodeByte = Base64.decode(lo, Base64.DEFAULT);
                    bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                            encodeByte.length, o);
                    img_product.setImageBitmap(bitmap);
                    v.findViewById(R.id.img_product_load).setVisibility(View.GONE);
                    img_product.setVisibility(View.VISIBLE);
                }
                @Override
                public void onFailure(XMLAccessTask.ErrorType error) {
                    img_product.setImageResource(R.mipmap.no_image);
                    v.findViewById(R.id.img_product_load).setVisibility(View.GONE);
                    v.findViewById(R.id.retry_img_product).setVisibility(View.VISIBLE);
                    img_product.setVisibility(View.GONE);
                }

            }).execute();
            usingSimpleImage(img_product);
        }else{
            Log.d("screenInches", "" + screenInches);
        }
        v.findViewById(R.id.retry_img_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v.findViewById(R.id.retry_img_product).setVisibility(View.GONE);
                v.findViewById(R.id.img_product_load).setVisibility(View.VISIBLE);
                img_product.setVisibility(View.GONE);
                new XMLAccessTask(getActivity(),valid_url, "fncGetProductMainImage",prodCode, new XMLAccessTask.CallbackInterface() {
                    @Override
                    public void onSuccess(NodeList nl) {
                        Element e = (Element) nl.item(0);
                        String lo = XMLParser.getValue(e, ProductImg);
                        BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inSampleSize = 2;
                        byte[] encodeByte = Base64.decode(lo, Base64.DEFAULT);
                        bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, o);
                        img_product.setImageBitmap(bitmap);
                        v.findViewById(R.id.img_product_load).setVisibility(View.GONE);
                        img_product.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onFailure(XMLAccessTask.ErrorType error) {
                        img_product.setImageResource(R.mipmap.no_image);
                        v.findViewById(R.id.img_product_load).setVisibility(View.GONE);
                        v.findViewById(R.id.retry_img_product).setVisibility(View.VISIBLE);
                        img_product.setVisibility(View.GONE);
                    }

                }).execute();
            }
        });
        qty.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String qt= qty.getText().toString();
                if(qt.matches("0")){
                    qty.setText("");
                }
                return false;
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActivity instanceof SalesAddProduct || mActivity instanceof SalesSummary || mActivity instanceof SalesReturnSummary || mActivity instanceof SalesReturnAddProduct ) {
                 if(productImageStr!=null && !productImageStr.isEmpty()) {
                     showProductImageDialog();
                 }else{
                    helper.showProgressView(mMainLayout);
                    image.setVisibility(View.GONE);
                    img_load.setVisibility(View.VISIBLE);
                    new XMLAccessTask(getActivity(),valid_url, "fncGetProductMainImage",prodCode, new XMLAccessTask.CallbackInterface() {
                        @Override
                        public void onSuccess(NodeList nl) {
                            Element e = (Element) nl.item(0);
                            productImageStr = XMLParser.getValue(e, ProductImg);
                            Log.d("img", "->"+productImageStr);
                            image.setVisibility(View.VISIBLE);
                            img_load.setVisibility(View.GONE);
                            helper.dismissProgressView(mMainLayout);
                            showProductImageDialog();
                        }
                        @Override
                        public void onFailure(XMLAccessTask.ErrorType error) {
                            img_product.setImageResource(R.mipmap.no_image);
                            Log.d("img", "->"+"no image");
                            image.setVisibility(View.VISIBLE);
                            img_load.setVisibility(View.GONE);
                            helper.dismissProgressView(mMainLayout);
                            showProductImageDialog();
                        }

                    }).execute();

                 }
                }else{
                    image.setVisibility(View.VISIBLE);
                    img_load.setVisibility(View.GONE);
                    helper.dismissProgressView(mMainLayout);
                    showProductImageDialog();
                }

            }
        });
        productSubImagesList = new ArrayList<>();
        helper = new UIHelper(getActivity());


        sizeadapter = new SizeModifierAdapter();

        mName.setText(prodName);
        title_code.setText(prodCode);
        mName.setText(prodName+" ("+prodCode+")");
        qty.setText("" + qtyFlag);

        SOTDatabase.init(mActivity);

        coloradapter = new ColorModifierAdapter(mActivity, colorArr, sizeArr);
        mColorImageGrid.setAdapter(coloradapter);

        totalqty = coloradapter.getQty();
        if (totalqty > 0) {
            totalQty.setVisibility(View.VISIBLE);
            totalQty.setText("Total Quantity : " + totalqty);
        }

        coloradapter.setOnCompletionListener(new ColorModifierAdapter.OnCompletionListener() {
            @Override
            public void onCompleted(int position) {
                Attribute rowItem = coloradapter.getItem(position);
                colourcode = rowItem.getCode();
                colourname = rowItem.getName();
                ArrayList<Attribute> sizeArr = getSize(colourcode);
                if(sizeArr.size()>0){
                    isSizeArrEmpty = false;
                    sizeadapter = new SizeModifierAdapter(mActivity, sizeArr);
                    mSizeImageGrid.setAdapter(sizeadapter);
                    String color = coloradapter.getColorFlag();
                    sizeadapter.setColor(color);
                    sizeadapter.unSelectAll();
                    sizeadapter.setOnCompletionListener(new SizeModifierAdapter.OnCompletionListener() {
                        @Override
                        public void onCompleted(int position) {
                            Attribute rowItem = (Attribute) sizeadapter.getItem(position);
                            chcksizecode = rowItem.getSizecode();
                            chcksizename = rowItem.getSizename();
                            String sizeqty = rowItem.getSizeQty();
                            //  setSize(rowItem);
                            qty.setText(sizeqty);
                        }
                    });
                    v.findViewById(R.id.sizeLabelLayout).setVisibility(View.VISIBLE);
                    mSizeImageGrid.setVisibility(View.VISIBLE);
                }else{
                    isSizeArrEmpty = true;
                    v.findViewById(R.id.sizeLabelLayout).setVisibility(View.INVISIBLE);
                    mSizeImageGrid.setVisibility(View.INVISIBLE);
                }
            }
        });

        v.findViewById(R.id.qty_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qtyStr = qty.getText().toString();
                qtyFlag = qtyStr.equals("") ? 0 : Integer.valueOf(qtyStr);
                qtyFlag++;
                qty.setText("" + qtyFlag);
            }
        });
        v.findViewById(R.id.qty_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qtyStr = qty.getText().toString();
                qtyFlag = qtyStr.equals("") ? 0 : Integer.valueOf(qtyStr);
                if (qtyFlag != 0) {
                    qtyFlag--;
                    qty.setText("" + qtyFlag);
                }
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantity = qty.getText().toString();
                if(quantity!=null && !quantity.isEmpty()) {
                    if (isSizeArrEmpty) {
                        String colorCode = coloradapter.getColorCode();
                        coloradapter.setQty(quantity);
                        setColorQty(colorCode, quantity);
                    } else {
                        String color = coloradapter.getColorFlag();
                        sizeadapter.setQty(quantity, color);
                        sizeadapter.setColor(color);
                        qtyInt = sizeadapter.getQty(color);
                        coloradapter.setQty("" + qtyInt);
                    }
                    totalqty = coloradapter.getQty();
                    if (totalqty > 0) {
                        totalQty.setVisibility(View.VISIBLE);
                    }
                    totalQty.setText("Total Quantity : " + totalqty);
                    qty.setText("0");
                }

            }
        });

        mPlusAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qtyStr =  qty.getText().toString();
                int qy = qtyStr.equals("") ? 0 : Integer.valueOf(qtyStr);
                if(qy==0){
                    sizeadapter.setSizeQtyAll(colourname,qtyStr,false);
                }else{
                    sizeadapter.setSizeQtyAll(colourname,qtyStr,true);

                }
                int totalSizeQty = sizeadapter.getQty(colourname);
                coloradapter.setQty(""+totalSizeQty);
//                sizeadapter.setSelected(false);
                totalqty = coloradapter.getQty();
                totalQty.setText("Total Quantity : " + totalqty);
                qty.setText("0");
            }
        });
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (mDismissDialogListener != null) {
                            mDismissDialogListener.onCompleted();
                        }
                    }
                });
            }
        });
        mtick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalqty = coloradapter.getQty();
                if (totalqty > 0) {
                    getDialog().dismiss();
                    if (mActivity instanceof SalesSummary) {
                        SOTDatabase.updateAttribute(slNo, prodCode, sizeArr);
                        sl_qty.setText("" + totalqty);
                        storeDatabase();
                    }  else if(mActivity instanceof SalesReturnSummary){
                        SOTDatabase.updateAttribute(slNo, prodCode, sizeArr);
                        sl_qty.setText("" + totalqty);
                        storeDatabase();
                    } else if (catalogCartEdit.equalsIgnoreCase("Catalog")) {
                        SOTDatabase.updateAttribute(slNo, prodCode, sizeArr);
                        sl_qty.setText("" + totalqty);
                        storeDatabase();
                    } else {
                        int totalqty = coloradapter.getQty();
                        Log.d("Totalqty", String.valueOf(totalqty));

                        if (sladd_qty != null) {
                            sladd_qty.setText(String.valueOf(totalqty));
                        }

                        final String quantity = String.valueOf(totalqty);
                        mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onCompleted(quantity, sizeArr);
                                }
                            }
                        });

                    }
                }else{
                    Toast.makeText(getActivity(),"Please enter the quantity",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    public void setColorQty(String colorCode,String qty) {
        for (Attribute item : sizeArr) {
            if (item.getCode().equals(colorCode)) {
                item.setSizeQty(qty);
                item.setSelected(true);
            }
        }
    }
    public ArrayList<Attribute> getSize(String colorCode) {
        ArrayList<Attribute> sizeCodeArr = new ArrayList<>();
        for (Attribute item : sizeArr) {
            if (item.getCode().equals(colorCode) && !item.getSizecode().matches("0")) {
                sizeCodeArr.add(item);
            }
        }
        return sizeCodeArr;
    }

    public void Dismiss() {
        getDialog().dismiss();

    }

    public void showProductImageDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_product_image);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //   final ProgressBar mProgressBar = (ProgressBar) dialog.findViewById(R.id.load);
        final ImageView productImage = (ImageView) dialog.findViewById(R.id.productImage);
        // DB Initialization
        FWMSSettingsDatabase.init(getActivity());
        // Get URL from DB
        String valid_url = FWMSSettingsDatabase.getUrl();
        // final ZoomControls zoom = (ZoomControls) dialog.findViewById(R.id.zoomControls);
        final TwoWayGridView subImage = (TwoWayGridView) dialog.findViewById(R.id.subImageGridView);
        parser = new XMLParser();

        ImageView close = (ImageView) dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        subImage.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
                Product product = subImageAdapter.getItem(position);
                String productSubImage = product.getProductImage();
                showProductImage(productSubImage,productImage);

            }
        });
        Log.d("prodCode","-kjds->"+prodCode);

        // String productMainImage = product.getProductImage();
        showProductImage(productImageStr,productImage);
        usingSimpleImage(productImage);
        if(productSubImagesList.isEmpty()){
            //mProgressBar.setVisibility(View.VISIBLE);
            subImage.setVisibility(View.GONE);
            if(productImageStr!=null && !productImageStr.isEmpty()){
                Product product = new Product();
                product.setProductImage(productImageStr);
                productSubImagesList.add(product);
            }
            HashMap<String,String> paramshm = new HashMap<>();
            paramshm.put("ProductCode", prodCode);
            paramshm.put("CategoryCode", "");
            paramshm.put("SubCategoryCode", "");

            new XMLAccessTask(getActivity(),valid_url, "fncGetProductSubImages",
                    paramshm, false, new XMLAccessTask.CallbackInterface() {
                @Override
                public void onSuccess(NodeList nl) {
                    try {
                        for (int i = 0; i < nl.getLength(); i++) {
                            Element e = (Element) nl.item(i);
                            Product product = new Product();
                            product.setProductImage(parser.getValue(e, "ProductImage"));
                            productSubImagesList.add(product);
                        }
                        Log.d("productSubImagesList","onSuccess-"+productSubImagesList.size());
                        subImageAdapter = new SubImageAdapter(getActivity(), R.layout.item_sub_image, productSubImagesList);
                        subImage.setAdapter(subImageAdapter);
                        //  mProgressBar.setVisibility(View.GONE);
                        subImage.setVisibility(View.VISIBLE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(XMLAccessTask.ErrorType error) {
                    try {
                        Log.d("productSubImagesList","onFailure-"+productSubImagesList.size());
                    if (productSubImagesList != null) {
                        subImageAdapter = new SubImageAdapter(getActivity(), R.layout.item_sub_image, productSubImagesList);
                        subImage.setAdapter(subImageAdapter);
                    }
                    //    mProgressBar.setVisibility(View.GONE);
                    subImage.setVisibility(View.VISIBLE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }).execute();



        }else{
            subImage.setVisibility(View.VISIBLE);
            subImageAdapter = new SubImageAdapter(getActivity(),R.layout.item_sub_image,productSubImagesList);
            subImage.setAdapter(subImageAdapter);
        }
        dialog.show();
    }
    private void showProductImage(String productSubImage ,ImageView productImage){
        // String productSubImage = product.getProductImage();
        if (productSubImage != null && !productSubImage.isEmpty()) {
            try {
                byte[] imageAsBytes = Base64.decode(productSubImage.getBytes(),
                        Base64.DEFAULT);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0,
                        imageAsBytes.length, options);
                productImage.setImageBitmap(bmp);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            productImage.setImageResource(R.mipmap.no_image);
        }
    }
    public void usingSimpleImage(ImageView imageView) {
        ImageAttacher mAttacher = new ImageAttacher(imageView);
        // mAttacher.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageAttacher.MAX_ZOOM = 2.0f; // Double the current Size
        ImageAttacher.MIN_ZOOM = 0.5f; // Half the current Size
        MatrixChangeListener mMaListener = new MatrixChangeListener();
        mAttacher.setOnMatrixChangeListener(mMaListener);
        PhotoTapListener mPhotoTap = new PhotoTapListener();
        mAttacher.setOnPhotoTapListener(mPhotoTap);
    }

    private class PhotoTapListener implements ImageAttacher.OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
        }
    }

    private class MatrixChangeListener implements ImageAttacher.OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {

        }
    }
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub

        ImageView view = (ImageView) v;

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());

                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);

                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;

                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    System.out.println("Moving");
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);

                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);

        return true; // indicate event was handled
    }

    /** Determine the space between the first two fingers */

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    public void intiateCartitem(View dialogView){

        id= slid;
        if(catalogCartEdit.equalsIgnoreCase("Catalog")){
            cursor = DBCatalog.getCursorForEditProduct(id);
        }else{
            cursor = SOTDatabase.getCursorForEditProduct(id);
        }

        cursor.moveToPosition(0);
        mDialogCancelImgV = (ImageView) dialogView.findViewById(R.id.close);
        mDialogUpdateImgV = (ImageView) dialogView.findViewById(R.id.ok);
        foc_layout = (LinearLayout) dialogView.findViewById(R.id.foc_layout);
        sl_codefield = (EditText) dialogView.findViewById(R.id.sl_codefield);
        sl_namefield = (EditText) dialogView.findViewById(R.id.sl_namefield);
        sl_cartonQty = (EditText) dialogView.findViewById(R.id.sl_cartonQty);
        sl_looseQty = (EditText) dialogView.findViewById(R.id.sl_looseQty);
        sl_qty = (EditText) dialogView.findViewById(R.id.sl_qty);
        sl_foc = (EditText) dialogView.findViewById(R.id.sl_foc);
        sl_price = (EditText) dialogView.findViewById(R.id.sl_price);
        sl_itemDiscount = (EditText) dialogView
                .findViewById(R.id.sl_itemDiscount);
        sl_cartonPerQty = (EditText) dialogView
                .findViewById(R.id.sl_cartonPerQty);
        sl_uom = (EditText) dialogView.findViewById(R.id.sl_uom);
        sl_total = (EditText) dialogView.findViewById(R.id.sl_total);
        sl_total_inclusive = (EditText) dialogView.findViewById(R.id.sl_total_inclusive);
        sl_tax = (EditText) dialogView.findViewById(R.id.sl_tax);
        sl_netTotal = (EditText) dialogView.findViewById(R.id.sl_netTotal);

        sl_cprice = (EditText) dialogView.findViewById(R.id.sl_cprice);
        sl_exchange = (EditText) dialogView.findViewById(R.id.sl_exchange);
        price_txt = (TextView) dialogView.findViewById(R.id.price_txt);
        expand = (ImageView) dialogView.findViewById(R.id.expand);

        price_txt_layout = (LinearLayout) dialogView.findViewById(R.id.price_txt_layout);

        txt_price= (TextView) dialogView.findViewById(R.id.txt_price);

        uomcperqty_ll = (LinearLayout) dialogView
                .findViewById(R.id.uomcperqty_ll);
        final Spinner prompt = (Spinner) dialogView
                .findViewById(R.id.weight_status);
        uomcperqty_ll.setVisibility(View.VISIBLE);

        prompt.setVisibility(View.GONE);

        priceflag = SalesOrderSetGet.getCartonpriceflag();
        calCarton = LogOutSetGet.getCalcCarton();

//		taxType = cursor.getString(cursor
//				.getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));

        if(catalogCartEdit.equalsIgnoreCase("Catalog")){

            taxType = cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_TAXTYPE));
            taxValue = cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_TAXVALUE));
            slCartonPerQty = cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_PCSPERCARTON));
            slPrice = cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_PRICE));

        }else{
            taxValue = cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));

            taxType = SalesOrderSetGet.getCompanytax();
//		taxValue = SalesOrderSetGet.getTaxValue();

            slCartonPerQty = cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
            slPrice = cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));

//            mimimumSellingPrice = cursor.getString(cursor
//                    .getColumnIndex(SOTDatabase.COLUMN_MINIMUM_SELLING_PRICE));
//            mimimumCartonSellingPrice = cursor.getString(cursor
//                    .getColumnIndex(SOTDatabase.COLUMN_MINIMUM_CARTON_SELLING_PRICE));
        }


//        mDialogUpdateImgV.setOnClickListener(mUpdateOnClickListener);
//        mDialogCancelImgV.setOnClickListener(mDismissOnClickListener);
        if (priceflag.matches("null") || priceflag.matches("")) {
            priceflag = "0";
        }

        if (priceflag.matches("1")) {
            sl_cprice.setVisibility(View.VISIBLE);
            price_txt.setVisibility(View.GONE);

            price_txt_layout.setVisibility(View.VISIBLE);
        } else {
            sl_cprice.setVisibility(View.GONE);
            price_txt.setVisibility(View.VISIBLE);

            price_txt_layout.setVisibility(View.GONE);
        }
        String netTotal = "",tax="";
        if(catalogCartEdit.equalsIgnoreCase("Catalog")){
            sl_codefield.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_PRODUCT_CODE)));
            sl_namefield.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_PRODUCT_NAME)));
            sl_cartonQty.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_CARTON_QTY)));
            sl_looseQty.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_LOOSE_QTY)));
            sl_qty.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_QUANTITY)));
            sl_price.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_PRICE)));
            sl_foc.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_FOC)));
            sl_itemDiscount.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_ITEM_DISCOUNT)));
            sl_uom.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_UOMCODE)));
            sl_cprice.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_CARTONPRICE)));
            sl_exchange.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_EXCHANGEQTY)));

            netTotal = cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_NETTOTAL));
            tax = cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_TAX));

        }else{
            sl_codefield.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE)));
            sl_namefield.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME)));
            sl_cartonQty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));
            sl_looseQty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));
            sl_qty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));
            sl_price.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE)));
            sl_foc.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_FOC)));
            sl_itemDiscount.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT)));
            Log.d("item disc in color",cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT)));
            sl_uom.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_UOM)));
            sl_cprice.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE)));
            sl_exchange.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY)));

            netTotal = cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
            tax = cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_TAX));
        }





// Added New 13.04.2017
        if (taxType != null && !taxType.isEmpty()) {
            if (taxType.matches("I")) {
                double dTax = tax.equals("") ? 0 : Double.valueOf(tax);
                double dNetTotal = netTotal.equals("") ? 0 : Double.valueOf(netTotal);
                double dTotalIncl = dNetTotal - dTax;
                String totIncl = twoDecimalPoint(dTotalIncl);
                sl_total_inclusive.setText(totIncl);
                sl_total.setVisibility(View.GONE);
                sl_total_inclusive.setVisibility(View.VISIBLE);
            }else{
                if(catalogCartEdit.equalsIgnoreCase("Catalog")){
                    sl_total_inclusive.setText(cursor.getString(cursor
                            .getColumnIndex(Constants.COLUMN_TOTAL)));
                }else{
                    sl_total_inclusive.setText(cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_TOTAL)));
                }

                sl_total.setVisibility(View.VISIBLE);
                sl_total_inclusive.setVisibility(View.GONE);
            }
        }else{
            if(catalogCartEdit.equalsIgnoreCase("Catalog")){

                sl_total_inclusive.setText(cursor.getString(cursor
                        .getColumnIndex(Constants.COLUMN_TOTAL)));
            }else{
                sl_total_inclusive.setText(cursor.getString(cursor
                        .getColumnIndex(SOTDatabase.COLUMN_TOTAL)));
            }

            sl_total.setVisibility(View.VISIBLE);
            sl_total_inclusive.setVisibility(View.GONE);
        }
        if(catalogCartEdit.equalsIgnoreCase("Catalog")) {
            sl_total.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_TOTAL)));
            sl_tax.setText(tax);
            sl_netTotal.setText(netTotal);
            sl_total.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_TOTAL)));
        }else{
            sl_total.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_TOTAL)));
            sl_tax.setText(tax);
            sl_netTotal.setText(netTotal);
            sl_total.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_TOTAL)));
        }



        if (priceflag.matches("1")) {

            if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
                    || slCartonPerQty.matches("")) {
                sl_price.setText("");
                sl_price.setVisibility(View.INVISIBLE);
                txt_price.setVisibility(View.GONE);
            }else{
                sl_price.setVisibility(View.VISIBLE);
                txt_price.setVisibility(View.VISIBLE);
            }

        } else {
            sl_price.setVisibility(View.VISIBLE);
            txt_price.setVisibility(View.VISIBLE);
        }

        if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
                || slCartonPerQty.matches("")) {

            // sl_cartonQty.setEnabled(false);
            sl_cartonQty.setFocusable(false);
            sl_cartonQty.setBackgroundResource(R.drawable.ic_edit_disable_bg);

            // sl_looseQty.setEnabled(false);
            sl_looseQty.setFocusable(false);
            sl_looseQty.setBackgroundResource(R.drawable.ic_edit_disable_bg);

            sl_qty.requestFocus();

        } else {
            sl_cartonQty.setFocusableInTouchMode(true);
            sl_cartonQty.setBackgroundResource(R.drawable.ic_edit_enable_bg);

            sl_looseQty.setFocusableInTouchMode(true);
            sl_looseQty.setBackgroundResource(R.drawable.ic_edit_enable_bg);

            sl_cartonQty.requestFocus();
        }
        sl_qty.setFocusableInTouchMode(true);
        sl_qty.setBackgroundResource(R.drawable.ic_edit_enable_bg);

        if (FormSetterGetter.isEditPrice()) {
            sl_price.setEnabled(true);
            sl_price.setFocusableInTouchMode(true);
            sl_price.setBackgroundResource(R.drawable.ic_edit_enable_bg);
        } else {
            sl_price.setEnabled(false);
            sl_price.setFocusable(false);
            sl_price.setGravity(Gravity.LEFT);
            sl_price.setBackgroundResource(R.drawable.ic_edit_disable_bg);
        }
        sl_foc.setFocusableInTouchMode(true);
        sl_foc.setBackgroundResource(R.drawable.ic_edit_enable_bg);
        sl_itemDiscount.setFocusableInTouchMode(true);
        sl_itemDiscount.setBackgroundResource(R.drawable.ic_edit_enable_bg);
        sl_uom.setFocusableInTouchMode(false);
        sl_uom.setBackgroundResource(R.drawable.ic_edit_disable_bg);

        if(catalogCartEdit.equalsIgnoreCase("Catalog")) {
            sl_cartonPerQty.setText(cursor.getString(cursor
                    .getColumnIndex(Constants.COLUMN_PCSPERCARTON)));
        }else{
            sl_cartonPerQty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY)));
        }


        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (foc_layout.getVisibility() == View.VISIBLE) {
                    // Its visible
                    foc_layout.setVisibility(View.GONE);
                } else {
                    foc_layout.setVisibility(View.VISIBLE);
                    // Either gone or invisible
                }
            }
        });

        sl_cartonQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            // public boolean onEditorAction(TextView v, int
            // actionId,
            // KeyEvent event) {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // TODO Auto-generated method stub
                    slPrice = sl_price.getText().toString();

                    if (calCarton.matches("0")) {
                        if (priceflag.matches("0")) {

                        } else if (priceflag.matches("1")) {
                            cartonQtyNew();
                        }
                    } else {
                        if (priceflag.matches("0")) {
                            cartonQty();
                        } else if (priceflag.matches("1")) {
                            cartonQtyNew();
                        }
                    }

                    sl_looseQty.requestFocus();
                    return true;
                }
                return false;
            }
        });

        cqtyTW = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                ss_Cqty = sl_cartonQty.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                slPrice = sl_price.getText().toString();

                if (calCarton.matches("0")) {
                    if (priceflag.matches("0")) {

                    } else if (priceflag.matches("1")) {
                        cartonQtyNew();
                    }
                } else {
                    if (priceflag.matches("0")) {
                        cartonQty();
                    } else if (priceflag.matches("1")) {
                        cartonQtyNew();
                    }
                }

                int length = sl_cartonQty.length();
                if (length == 0) {

                    if (calCarton.matches("0")) {

                    } else {

                        String lqty = sl_looseQty.getText().toString();
                        if (!lqty.matches("")) {
                            sl_qty.removeTextChangedListener(qtyTW);
                            sl_qty.setText(lqty);
                            sl_qty.addTextChangedListener(qtyTW);

                            if (sl_qty.length() != 0) {
                                sl_qty.setSelection(sl_qty.length());
                            }
                            double lsQty = Double.parseDouble(lqty);

                            if (priceflag.matches("0")) {
                                productTotal(lsQty);
                            } else if (priceflag.matches("1")) {
                                // String cprice =
                                // sl_price.getText().toString();
                                productTotalNew();
                            }
                        }
                    }
                }
            }
        };
        sl_looseQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    slPrice = sl_price.getText().toString();

                    if (calCarton.matches("0")) {
                        if (priceflag.matches("0")) {

                        } else if (priceflag.matches("1")) {
                            looseQtyCalcNew();
                        }
                    } else {
                        if (priceflag.matches("0")) {
                            looseQtyCalc();
                        } else if (priceflag.matches("1")) {
                            looseQtyCalcNew();
                        }
                    }

                    sl_qty.requestFocus();
                    return true;
                }
                return false;
            }

        });

        lqtyTW = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                beforeLooseQty = sl_looseQty.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                slPrice = sl_price.getText().toString();

                if (calCarton.matches("0")) {
                    if (priceflag.matches("0")) {

                    } else if (priceflag.matches("1")) {
                        looseQtyCalcNew();
                    }
                } else {
                    if (priceflag.matches("0")) {
                        looseQtyCalc();
                    } else if (priceflag.matches("1")) {
                        looseQtyCalcNew();
                    }
                }

                int length = sl_looseQty.length();
                if (length == 0) {
                    if (calCarton.matches("0")) {

                    } else {

                        String qty = sl_qty.getText().toString();
                        if (!beforeLooseQty.matches("") && !qty.matches("")) {

                            double qtyCnvrt = Double.parseDouble(qty);
                            double lsCnvrt = Double.parseDouble(beforeLooseQty);

                            sl_qty.removeTextChangedListener(qtyTW);
                            sl_qty.setText("" + (qtyCnvrt - lsCnvrt));
                            sl_qty.addTextChangedListener(qtyTW);

                            if (sl_qty.length() != 0) {
                                sl_qty.setSelection(sl_qty.length());
                            }

                            if (priceflag.matches("0")) {
                                looseQtyCalc();
                            } else if (priceflag.matches("1")) {
                                looseQtyCalcNew();
                            }
                        }
                    }
                }
            }
        };

        sl_qty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    slPrice = sl_price.getText().toString();
                    String qty = sl_qty.getText().toString();

                    if (calCarton.matches("0")) {

                    } else {
                        if (!qty.matches("")) {
                            clQty();
                        }
                    }

                    if (priceflag.matches("1")) {
                        sl_cprice.requestFocus();
                    } else {
                        sl_price.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });

        qtyTW = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                slPrice = sl_price.getText().toString();
                String qty = sl_qty.getText().toString();

                if (qty.matches("")) {
                    qty = "0";
                }

                if (calCarton.matches("0")) {

                    if (priceflag.matches("0")) {

                        if (!qty.matches("")) {
                            double quantity = Double.parseDouble(qty);
                            productTotal(quantity);

                            int length = sl_qty.length();
                            if (length == 0) {
                                if (calCarton.matches("0")) {
                                    productTotal(0);
                                } else {
                                    productTotal(0);

                                    sl_cartonQty
                                            .removeTextChangedListener(cqtyTW);
                                    sl_cartonQty.setText("");
                                    sl_cartonQty.addTextChangedListener(cqtyTW);

                                    sl_looseQty
                                            .removeTextChangedListener(lqtyTW);
                                    sl_looseQty.setText("");
                                    sl_looseQty.addTextChangedListener(lqtyTW);
                                }
                            }
                        }
                    } else if (priceflag.matches("1")) {
                        String pcsPerCarton = sl_cartonPerQty.getText()
                                .toString();
                        if (!pcsPerCarton.matches("")) {
                            double pcsPerCartonCalc = Double
                                    .parseDouble(pcsPerCarton);
                            if (pcsPerCartonCalc == 1) {

                                if (!qty.matches("")) {
                                    double quantity = Double.parseDouble(qty);
                                    productTotal(quantity);
                                }

                            }
                        }

                        int length = sl_qty.length();
                        if (length == 0) {
                            if (calCarton.matches("0")) {

                            } else {
                                productTotal(0);

                                sl_cartonQty.removeTextChangedListener(cqtyTW);
                                sl_cartonQty.setText("");
                                sl_cartonQty.addTextChangedListener(cqtyTW);

                                sl_looseQty.removeTextChangedListener(lqtyTW);
                                sl_looseQty.setText("");
                                sl_looseQty.addTextChangedListener(lqtyTW);
                            }
                        }

                    }

                } else {
                    if (!qty.matches("")) {
                        clQty();
                    }

                    int length = sl_qty.length();
                    if (length == 0) {
                        if (calCarton.matches("0")) {

                        } else {
                            productTotal(0);

                            sl_cartonQty.removeTextChangedListener(cqtyTW);
                            sl_cartonQty.setText("");
                            sl_cartonQty.addTextChangedListener(cqtyTW);

                            sl_looseQty.removeTextChangedListener(lqtyTW);
                            sl_looseQty.setText("");
                            sl_looseQty.addTextChangedListener(lqtyTW);
                        }
                    }
                }

            }

        };

        sl_foc.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    if (sl_price.getText().toString().equals("0.00")
                            || sl_price.getText().toString().equals("0")
                            || sl_price.getText().toString().equals("0.0")
                            || sl_price.getText().toString().equals(".0")) {
                        sl_price.setText("");
                    }
                    sl_price.requestFocus();
                    return true;
                }
                return false;
            }
        });

        sl_foc.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

        });
        sl_price.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    String qty = sl_qty.getText().toString();
                    if (!qty.matches("")) {
                        double qtyCalc = Double.parseDouble(qty);
                        slPrice = sl_price.getText().toString();
                        if (priceflag.matches("0")) {
                            productTotal(qtyCalc);
                        } else if (priceflag.matches("1")) {
                            productTotalNew();
                        }
                    }
                    sl_itemDiscount.requestFocus();
                    return true;
                }
                return false;
            }
        });
        sl_price.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (sl_price.getText().toString().equals("0.00")
                        || sl_price.getText().toString().equals("0")
                        || sl_price.getText().toString().equals("0.0")
                        || sl_price.getText().toString().equals(".0")) {
                    sl_price.setText("");
                }
                return false;
            }
        });
        sl_price.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                String qty = sl_qty.getText().toString();
                if (!qty.matches("")) {
                    double qtyCalc = Double.parseDouble(qty);
                    slPrice = sl_price.getText().toString();
                    if (priceflag.matches("0")) {
                        productTotal(qtyCalc);
                    } else if (priceflag.matches("1")) {
                        productTotalNew();
                    }
                }
            }

        });
        sl_itemDiscount.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_DONE) {

                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            sl_itemDiscount.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        sl_cprice.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    String qty = sl_qty.getText().toString();
                    if (!qty.matches("")) {
                        double qtyCalc = Double.parseDouble(qty);
                        String cPrice = sl_cprice.getText().toString();

                        if (priceflag.matches("0")) {
                            productTotal(qtyCalc);
                        } else if (priceflag.matches("1")) {
                            productTotalNew();
                        }
                    }
                    sl_price.requestFocus();
                    return true;
                }
                return false;
            }
        });
        sl_cprice.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (sl_cprice.getText().toString().equals("0.00")
                        || sl_cprice.getText().toString().equals("0")
                        || sl_cprice.getText().toString().equals("0.0")
                        || sl_cprice.getText().toString().equals(".0")) {
                    sl_cprice.setText("");
                }
                return false;
            }
        });
        sl_cprice.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                String qty = sl_qty.getText().toString();
                if (!qty.matches("")) {
                    double qtyCalc = Double.parseDouble(qty);
                    String cPrice = sl_cprice.getText().toString();

                    if (priceflag.matches("0")) {
                        productTotal(qtyCalc);
                    } else if (priceflag.matches("1")) {
                        productTotalNew();
                    }
                }
            }

        });

        sl_itemDiscount.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                slPrice = sl_price.getText().toString();

                if (priceflag.matches("0")) {
                    itemDiscountCalc();
                } else if (priceflag.matches("1")) {
                    itemDiscountCalcNew();
                }

            }
        });

        sl_cartonQty.addTextChangedListener(cqtyTW);
        sl_looseQty.addTextChangedListener(lqtyTW);
        sl_qty.addTextChangedListener(qtyTW);
    }


    public void storeDatabase(){

        double cartTotal, cartTax, cartNetTotal;
        String codeStr = sl_codefield.getText().toString();
        String nameStr = sl_namefield.getText().toString();
        String uomStr = sl_uom.getText().toString();
        String cartonQtyStr = sl_cartonQty.getText().toString();
        String looseQtyStr = sl_looseQty.getText().toString();
        String qtyStr = sl_qty.getText().toString();
        String focStr = sl_foc.getText().toString();
        String priceStr = sl_price.getText().toString();
        String dicountStr = sl_itemDiscount.getText().toString();
        String cartonPerQtyStr = sl_cartonPerQty.getText().toString();
        String totalStr = sl_total.getText().toString();
        String taxStr = sl_tax.getText().toString();
        String netTotalStr = sl_netTotal.getText().toString();
        String cpriceStr = sl_cprice.getText().toString();
        String exQtyStr = sl_exchange.getText().toString();

        int Id = 0;

        if (id != null && !id.isEmpty()) {
            Id = Integer.valueOf(id);
        }

        if (priceStr.matches("")) {
            priceStr = "0";
        }

        if (cpriceStr.matches("")) {
            cpriceStr = "0";
        }

        try {
            if (calCarton.matches("1") && qtyStr.matches("")
                    && focStr.matches("") && exQtyStr.matches("")) {
                Toast.makeText(getActivity(), "Enter the quantity",
                        Toast.LENGTH_SHORT).show();

            } else if (calCarton.matches("0") && cartonQtyStr.matches("")
                    && looseQtyStr.matches("") && qtyStr.matches("")) {
                Toast.makeText(getActivity(), "Enter the carton/quantity",
                        Toast.LENGTH_SHORT).show();
            } else {

                int foc = 0;
                double price = 0, discount = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0, cartonQty = 0, looseQty = 0, qty = 0, cartonPerQty = 0;
                String sbTtl = "";
                String netT = "";
                if (!cartonQtyStr.matches("")) {
                    cartonQty = Double.parseDouble(cartonQtyStr);
                }
                if (!looseQtyStr.matches("")) {
                    looseQty = Double.parseDouble(looseQtyStr);
                }
                if (!qtyStr.matches("")) {
                    qty = Double.parseDouble(qtyStr);
                }
                if (!focStr.matches("")) {
                    foc = Integer.parseInt(focStr);
                }
                if (!cartonPerQtyStr.matches("")) {
                    cartonPerQty = Double.parseDouble(cartonPerQtyStr);
                }
                if (!priceStr.matches("")) {
                    price = Double.parseDouble(priceStr);
                }

                if (cpriceStr.matches("")) {
                    cpriceStr = "0.00";
                }

                if (priceflag.matches("0")) {
                    cpriceStr = "0.00";
                }

                if (exQtyStr.matches("")) {
                    exQtyStr = "0";
                }

                if (!totalStr.matches("")) {
                    total = Double.parseDouble(totalStr);

                    String itemDisc = sl_itemDiscount.getText().toString();
                    if (!itemDisc.matches("")) {
                        itmDisc = Double.parseDouble(itemDisc);
                        subTotal = total;
                    } else {
                        subTotal = total;
                    }

                    sbTtl = twoDecimalPoint(subTotal);

                }
                if(catalogCartEdit.equalsIgnoreCase("Catalog")) {

                }else{
                    int i_sssno = Integer.parseInt(id);
                    SOTDatabase.updateBillDisc(i_sssno, nameStr, sbTtl);
                }



                if (!taxStr.matches("")) {
                    tax = Double.parseDouble(taxStr);
                }
                if (!netTotalStr.matches("")) {
                    if (taxType != null && !taxType.isEmpty()) {
                        if (taxType.matches("I") || taxType.matches("Z")) {
                            ntTot = subTotal;
                        } else {
                            ntTot = subTotal + tax;
                        }
                    } else {
                        ntTot = subTotal + tax;
                    }

                    netT = twoDecimalPoint(ntTot);
                }

                if (taxValue.matches("") || taxValue == null) {
                    taxValue = "0";
                }

                if (priceflag.matches("0")) {
                    itemDiscountCalc();
                } else if (priceflag.matches("1")) {
                    itemDiscountCalcNew();
                }

                String disctStr = sl_itemDiscount.getText().toString();
                if (!disctStr.matches("")) {
                    discount = Double.parseDouble(disctStr);

                }
                String totl = twoDecimalPoint(tt);
                Log.d("total" + tt, totl);

                double dis = 0.0;
                if (!dicountStr.matches("")) {
                    dis = Double.parseDouble(dicountStr);
                }
                Log.d("tt+dis", "->" + tt + dis);
                if(catalogCartEdit.equalsIgnoreCase("Catalog")) {
                    DBCatalog.updateProduct(codeStr, cartonQty,
                            looseQty, price, qty, tt + dis,
                            netT, foc, tax, discount, sbTtl,
                            cpriceStr, exQtyStr);
                    cursor.requery();
                    if(mProductModifierDialogListener!=null){
                        mProductModifierDialogListener.refreshAdapter();
                    }
                    //  mProductModifierDialogListener = (ProductModifierDialogListener) this;
                    //   mProductModifierDialogListener.refreshAdapter();
                }else{
                    SOTDatabase.updateProduct(prodCode, prodName, cartonQty,
                            looseQty, qty, foc, price, discount, uomStr,
                            cartonPerQty, tt + dis, tax, sbTtl, netT, Id,
                            cpriceStr, exQtyStr);

                    cursor.requery();

                    mProductModifierDialogListener = (ProductModifierDialogListener)getActivity();
                    mProductModifierDialogListener.refreshAdapter();
                }


                getDialog().dismiss();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void cartonQty() {
        String crtnQty = sl_cartonQty.getText().toString();

        if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

            double cartonQtyCalc = Double.parseDouble(crtnQty);

            double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);

            double qty = 0;

            String lsQty = sl_looseQty.getText().toString();

            if (!lsQty.matches("")) {
                double lsQtyCnvrt = Double.parseDouble(lsQty);
                qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

            } else {
                qty = cartonQtyCalc * cartonPerQtyCalc;
            }

            String q = twoDecimalPoint(qty);

            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("" + q);
            sl_qty.addTextChangedListener(qtyTW);

            if (sl_qty.length() != 0) {
                sl_qty.setSelection(sl_qty.length());
            }

            productTotal(qty);
        }
    }

    public void looseQtyCalc() {
        String crtnQty = sl_cartonQty.getText().toString();
        String lsQty = sl_looseQty.getText().toString();

        if (lsQty.matches("")) {
            lsQty = "0";
        }

        if (!slCartonPerQty.matches("") && !crtnQty.matches("")
                && !lsQty.matches("")) {

            double cartonQtyCalc = Double.parseDouble(crtnQty);
            double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
            double looseQtyCalc = Double.parseDouble(lsQty);

            double qty;

            qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

            String q = twoDecimalPoint(qty);

            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("" + q);
            sl_qty.addTextChangedListener(qtyTW);

            if (sl_qty.length() != 0) {
                sl_qty.setSelection(sl_qty.length());
            }

            productTotal(qty);
        }

        if (!lsQty.matches("")) {

            double looseQtyCalc = Double.parseDouble(lsQty);
            double qty;

            if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
                double cartonQtyCalc = Double.parseDouble(crtnQty);
                double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);

                qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
            } else {
                qty = looseQtyCalc;
            }

            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("" + qty);
            sl_qty.addTextChangedListener(qtyTW);

            if (sl_qty.length() != 0) {
                sl_qty.setSelection(sl_qty.length());
            }

            productTotal(qty);
        }
    }

    public void itemDiscountCalc() {

        try {
            String itmDscnt = sl_itemDiscount.getText().toString();
            String qty = sl_qty.getText().toString();
            String prc = sl_price.getText().toString();

            if (itmDscnt.matches("")) {
                itmDscnt = "0";
            }

            if (!itmDscnt.matches("") && !qty.matches("") && !prc.matches("")) {

                double itemDiscountCalc = 0.0;

                itemDiscountCalc = Double.parseDouble(itmDscnt);

                double quantityCalc = Double.parseDouble(qty);
                double priceCalc = Double.parseDouble(prc);

                tt = (quantityCalc * priceCalc) - itemDiscountCalc;

                Log.d("ttl", "" + tt);
                String Prodtotal = twoDecimalPoint(tt);
                sl_total.setText("" + Prodtotal);
                sl_total_inclusive.setText("" + Prodtotal);
                double taxAmount = 0.0, netTotal = 0.0;
                if (!taxType.matches("") && !taxValue.matches("")) {

                    double taxValueCalc = Double.parseDouble(taxValue);

                    if (taxType.matches("E")) {

                        taxAmount = (tt * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);

                    } else if (taxType.matches("I")) {

                        taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);


                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        sl_total_inclusive.setText(totalIncl);

                    } else if (taxType.matches("Z")) {

                        sl_tax.setText("0.0");

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);

                    } else {
                        sl_tax.setText("0.0");
                        sl_netTotal.setText("" + Prodtotal);
                    }
                } else if (taxValue.matches("")) {
                    sl_tax.setText("0.0");
                    sl_netTotal.setText("" + Prodtotal);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void productTotal(double qty) {

        try {

            double taxAmount = 0.0, netTotal = 0.0;
            double taxAmount1 = 0.0, netTotal1 = 0.0;

            if (slPrice.matches("")) {
                slPrice = "0";
            }

            if (!slPrice.matches("")) {

                double slPriceCalc = Double.parseDouble(slPrice);
                String itmDscnt = sl_itemDiscount.getText().toString();
                if (!itmDscnt.matches("")) {

                    tt = (qty * slPriceCalc);

                } else {

                    tt = qty * slPriceCalc;

                }

                String Prodtotal = twoDecimalPoint(tt);

                double subTotal = 0.0;

                String itemDisc = sl_itemDiscount.getText().toString();
                if (!itemDisc.matches("")) {
                    itmDisc = Double.parseDouble(itemDisc);
                    subTotal = tt - itmDisc;
                } else {
                    subTotal = tt;
                }

                String sbTtl = twoDecimalPoint(subTotal);

                sl_total.setText("" + sbTtl);
                sl_total_inclusive.setText("" + Prodtotal);
                if (!taxType.matches("") && !taxValue.matches("")) {

                    double taxValueCalc = Double.parseDouble(taxValue);

                    if (taxType.matches("E")) {

                        if (!itemDisc.matches("")) {
                            taxAmount1 = (subTotal * taxValueCalc) / 100;
                            String prodTax = fourDecimalPoint(taxAmount1);
                            sl_tax.setText("" + prodTax);

                            netTotal1 = subTotal + taxAmount1;
                            String ProdNetTotal = twoDecimalPoint(netTotal1);
                            sl_netTotal.setText("" + ProdNetTotal);
                        } else {

                            taxAmount = (tt * taxValueCalc) / 100;
                            String prodTax = fourDecimalPoint(taxAmount);
                            sl_tax.setText("" + prodTax);

                            netTotal = tt + taxAmount;
                            String ProdNetTotal = twoDecimalPoint(netTotal);
                            sl_netTotal.setText("" + ProdNetTotal);
                        }

                    } else if (taxType.matches("I")) {
                        if (!itemDisc.matches("")) {
                            taxAmount1 = (subTotal * taxValueCalc)
                                    / (100 + taxValueCalc);
                            String prodTax = fourDecimalPoint(taxAmount1);
                            sl_tax.setText("" + prodTax);

                            // netTotal1 = subTotal + taxAmount;
                            netTotal1 = subTotal;
                            String ProdNetTotal = twoDecimalPoint(netTotal1);
                            sl_netTotal.setText("" + ProdNetTotal);


                            double dTotalIncl = netTotal1 - taxAmount1;
                            String totalIncl = twoDecimalPoint(dTotalIncl);
                            Log.d("totalIncl", "" + totalIncl);
                            sl_total_inclusive.setText(totalIncl);
                        } else {
                            taxAmount = (tt * taxValueCalc)
                                    / (100 + taxValueCalc);
                            String prodTax = fourDecimalPoint(taxAmount);
                            sl_tax.setText("" + prodTax);

                            // netTotal = tt + taxAmount;
                            netTotal = tt;
                            String ProdNetTotal = twoDecimalPoint(netTotal);
                            sl_netTotal.setText("" + ProdNetTotal);


                            double dTotalIncl = netTotal - taxAmount;
                            String totalIncl = twoDecimalPoint(dTotalIncl);
                            Log.d("totalIncl", "" + totalIncl);
                            sl_total_inclusive.setText(totalIncl);
                        }

                    } else if (taxType.matches("Z")) {

                        sl_tax.setText("0.0");
                        if (!itemDisc.matches("")) {
                            // netTotal1 = subTotal + taxAmount;
                            netTotal1 = subTotal;
                            String ProdNetTotal = twoDecimalPoint(netTotal1);
                            sl_netTotal.setText("" + ProdNetTotal);
                        } else {
                            // netTotal = tt + taxAmount;
                            netTotal = tt;
                            String ProdNetTotal = twoDecimalPoint(netTotal);
                            sl_netTotal.setText("" + ProdNetTotal);
                        }

                    } else {
                        sl_tax.setText("0.0");
                        sl_netTotal.setText("" + Prodtotal);
                    }

                } else if (taxValue.matches("")) {
                    sl_tax.setText("0.0");
                    sl_netTotal.setText("" + Prodtotal);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clQty() {
        String qty = sl_qty.getText().toString();
        String crtnperQty = sl_cartonPerQty.getText().toString();
        double q = 0, r = 0;

        if (crtnperQty.matches("0") || crtnperQty.matches("null")
                || crtnperQty.matches("0.00")) {
            crtnperQty = "1";
        }

        if (!crtnperQty.matches("")) {
            if (!qty.matches("")) {
                try {
                    double qty_nt = Double.parseDouble(qty);
                    double pcs_nt = Double.parseDouble(crtnperQty);

                    Log.d("qty_nt", "" + qty_nt);
                    Log.d("pcs_nt", "" + pcs_nt);

                    q = (int) (qty_nt / pcs_nt);
                    r = qty_nt % pcs_nt;

                    String ctn = twoDecimalPoint(q);
                    String loose = twoDecimalPoint(r);

                    Log.d("cqty", "" + ctn);
                    Log.d("lqty", "" + loose);

                    sl_cartonQty.removeTextChangedListener(cqtyTW);
                    sl_cartonQty.setText("" + ctn);
                    sl_cartonQty.addTextChangedListener(cqtyTW);

                    sl_looseQty.removeTextChangedListener(lqtyTW);
                    sl_looseQty.setText("" + loose);
                    sl_looseQty.addTextChangedListener(lqtyTW);

                    if (priceflag.matches("0")) {
                        productTotal(qty_nt);
                    } else if (priceflag.matches("1")) {
                        productTotalNew();
                    }

                    // sl_cartonQty.setText("" + ctn);
                    // sl_looseQty.setText("" + loose);

                } catch (ArithmeticException e) {
                    System.out.println("Err: Divided by Zero");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void cartonQtyNew() {
        String crtnQty = sl_cartonQty.getText().toString();

        if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

            double cartonQtyCalc = Double.parseDouble(crtnQty);
            double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
            double qty = 0;

            String lsQty = sl_looseQty.getText().toString();

            if (!lsQty.matches("")) {
                double lsQtyCnvrt = Double.parseDouble(lsQty);
                qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

            } else {
                qty = cartonQtyCalc * cartonPerQtyCalc;
            }

            String q = twoDecimalPoint(qty);

            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("" + q);
            sl_qty.addTextChangedListener(qtyTW);

            if (sl_qty.length() != 0) {
                sl_qty.setSelection(sl_qty.length());
            }

            productTotalNew();
        }
    }

    public void looseQtyCalcNew() {
        String crtnQty = sl_cartonQty.getText().toString();
        String lsQty = sl_looseQty.getText().toString();

        if (lsQty.matches("")) {
            lsQty = "0";
        }

        if (!slCartonPerQty.matches("") && !crtnQty.matches("")
                && !lsQty.matches("")) {

            double cartonQtyCalc = Double.parseDouble(crtnQty);
            double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
            double looseQtyCalc = Double.parseDouble(lsQty);

            double qty;
            qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

            String q = twoDecimalPoint(qty);

            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("" + q);
            sl_qty.addTextChangedListener(qtyTW);

            if (sl_qty.length() != 0) {
                sl_qty.setSelection(sl_qty.length());
            }

            productTotalNew();
        }

        if (!lsQty.matches("")) {

            double looseQtyCalc = Double.parseDouble(lsQty);
            double qty;

            if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
                double cartonQtyCalc = Double.parseDouble(crtnQty);
                double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);

                qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
            } else {
                qty = looseQtyCalc;
            }

            String q = twoDecimalPoint(qty);

            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("" + q);
            sl_qty.addTextChangedListener(qtyTW);

            if (sl_qty.length() != 0) {
                sl_qty.setSelection(sl_qty.length());
            }

            productTotalNew();
        }
    }

    public void itemDiscountCalcNew() {

        try {
            String itmDscnt = sl_itemDiscount.getText().toString();

            if (itmDscnt.matches("")) {
                itmDscnt = "0";
            }

            String lPrice = sl_price.getText().toString();
            String cPrice = sl_cprice.getText().toString();
            String cqty = sl_cartonQty.getText().toString();
            String lqty = sl_looseQty.getText().toString();

            if (lPrice.matches("")) {
                lPrice = "0";
            }

            if (cPrice.matches("")) {
                cPrice = "0";
            }

            if (cqty.matches("")) {
                cqty = "0";
            }

            if (lqty.matches("")) {
                lqty = "0";
            }

            if (!itmDscnt.matches("")) {
                double itemDiscountCalc = 0.0;
                itemDiscountCalc = Double.parseDouble(itmDscnt);

                double lPriceCalc = Double.parseDouble(lPrice);
                double cPriceCalc = Double.parseDouble(cPrice);

                double cqtyCalc = Double.parseDouble(cqty);
                double lqtyCalc = Double.parseDouble(lqty);

                tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc)
                        - itemDiscountCalc;

                Log.d("ttl", "" + tt);
                String Prodtotal = twoDecimalPoint(tt);
                sl_total.setText("" + Prodtotal);
                sl_total_inclusive.setText("" + Prodtotal);
                double taxAmount = 0.0, netTotal = 0.0;
                if (!taxType.matches("") && !taxValue.matches("")) {

                    double taxValueCalc = Double.parseDouble(taxValue);

                    if (taxType.matches("E")) {

                        taxAmount = (tt * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);

                    } else if (taxType.matches("I")) {

                        taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);


                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        sl_total_inclusive.setText(totalIncl);

                    } else if (taxType.matches("Z")) {

                        sl_tax.setText("0.0");

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);

                    } else {
                        sl_tax.setText("0.0");
                        sl_netTotal.setText("" + Prodtotal);
                    }
                } else if (taxValue.matches("")) {
                    sl_tax.setText("0.0");
                    sl_netTotal.setText("" + Prodtotal);
                }
            }

        } catch (Exception e) {

        }
    }

    public void productTotalNew() {

        try {
            double taxAmount = 0.0, netTotal = 0.0;
            double taxAmount1 = 0.0, netTotal1 = 0.0;

            String lPrice = sl_price.getText().toString();
            String cPrice = sl_cprice.getText().toString();
            String cqty = sl_cartonQty.getText().toString();
            String lqty = sl_looseQty.getText().toString();

            if (lPrice.matches("")) {
                lPrice = "0";
            }

            if (cPrice.matches("")) {
                cPrice = "0";
            }

            if (cqty.matches("")) {
                cqty = "0";
            }

            if (lqty.matches("")) {
                lqty = "0";
            }

            double lPriceCalc = Double.parseDouble(lPrice);
            double cPriceCalc = Double.parseDouble(cPrice);

            double cqtyCalc = Double.parseDouble(cqty);
            double lqtyCalc = Double.parseDouble(lqty);

            tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc);

            String Prodtotal = twoDecimalPoint(tt);

            double subTotal = 0.0;

            String itemDisc = sl_itemDiscount.getText().toString();
            if (!itemDisc.matches("")) {
                itmDisc = Double.parseDouble(itemDisc);
                subTotal = tt - itmDisc;
            } else {
                subTotal = tt;
            }

            String sbTtl = twoDecimalPoint(subTotal);

            sl_total.setText("" + sbTtl);
            sl_total_inclusive.setText("" + Prodtotal);
            if (!taxType.matches("") && !taxValue.matches("")) {

                double taxValueCalc = Double.parseDouble(taxValue);

                if (taxType.matches("E")) {

                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subTotal * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount1);
                        sl_tax.setText("" + prodTax);

                        netTotal1 = subTotal + taxAmount1;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        sl_netTotal.setText("" + ProdNetTotal);
                    } else {

                        taxAmount = (tt * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);
                    }

                } else if (taxType.matches("I")) {
                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subTotal * taxValueCalc)
                                / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount1);
                        sl_tax.setText("" + prodTax);

                        // netTotal1 = subTotal + taxAmount1;
                        netTotal1 = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        sl_netTotal.setText("" + ProdNetTotal);


                        double dTotalIncl = netTotal1 - taxAmount1;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        sl_total_inclusive.setText(totalIncl);

                    } else {
                        taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);


                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        sl_total_inclusive.setText(totalIncl);
                    }

                } else if (taxType.matches("Z")) {

                    sl_tax.setText("0.0");
                    if (!itemDisc.matches("")) {
                        // netTotal1 = subTotal + taxAmount;
                        netTotal1 = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        sl_netTotal.setText("" + ProdNetTotal);
                    } else {
                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);
                    }

                } else {
                    sl_tax.setText("0.0");
                    sl_netTotal.setText("" + Prodtotal);
                }

            } else if (taxValue.matches("")) {
                sl_tax.setText("0.0");
                sl_netTotal.setText("" + Prodtotal);
            }

        } catch (Exception e) {

        }
    }

    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tot = df.format(d);

        return tot;
    }

    public String fourDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setMinimumFractionDigits(4);
        String tot = df.format(d);

        return tot;
    }

    public interface ProductModifierDialogListener {
        void refreshAdapter();
    }



   /* public class GetProductImage implements XMLAccessTask.CallbackInterface {

        @Override
        public void onSuccess(NodeList nl) {
            try {
                for (int i = 0; i < nl.getLength(); i++) {

                    Element e = (Element) nl.item(i);
                    String lo = XMLParser.getValue(e, ProductImg);

                    Log.d("imggg", lo);

                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inSampleSize = 2;
                    byte[] encodeByte = Base64.decode(lo, Base64.DEFAULT);
                    bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                            encodeByte.length, o);
                    img_product.setImageBitmap(bitmap);

                }
            }catch ( Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(XMLAccessTask.ErrorType error) {
            onError(error);
        }

    }

    private void onError(final XMLAccessTask.ErrorType error) {
        new Thread() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (error == XMLAccessTask.ErrorType.NETWORK_UNAVAILABLE) {
                            helper.showLongToast(R.string.error_showing_image_no_network_connection);
                        } else {

                        }
                    }
                });
            }
        }.start();
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                Log.d("Backpressed","Backpressed");
            }
        };
    }
}
