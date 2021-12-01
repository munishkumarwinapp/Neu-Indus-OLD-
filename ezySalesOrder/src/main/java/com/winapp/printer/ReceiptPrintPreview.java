package com.winapp.printer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.model.Product;
import com.winapp.printcube.printcube.BluetoothService;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.sot.Company;
import com.winapp.sot.In_Cash;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.ProdDetails;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.SalesOrderSetGet;

public class ReceiptPrintPreview extends Activity {
	private List<ProdDetails> product, prodet;
	private List<ProductDetails> productdet;

	// private List<ProdDetails> prodet;
	// private List<ProdDetails> receiptproductdet;
	private ListView lv,lv1;
	private ImageButton back;
	private TextView title, invoiceno, invoicedate, custcode, custname,
			itemdisc, billdisc, subtotal, tax, nettotal, remarks,
			totaloutstanding, companyname, address1, address2, country, phone,
			receiptno, receiptdate, receiptno_txt, receiptdate_txt,
			invoiceno_txt, invoicedate_txt, custcode_txt, custname_txt,
			paymode_txt, bankcode_txt, chequeno_txt, chequedate_txt,sno_txt,description_txt,qty_txt,price_txt,total_txt,nettol_txt;

	private LinearLayout receiptno_ll, receiptdate_ll, companyname_ll,
			address1_ll, address2_ll, country_ll, phone_ll, remarks_ll,
			paymode_ll, bankcode_ll, chequeno_ll, chequedate_ll,custCode,custName,itmdsc,bdsc,net,toto,nets;
	private int s = 1;
	private List<String> sort;
	private ArrayList<Product> mAttributeArr;
	private HashSet<String> hs;
	private List<String> listDataHeader;
	private HashMap<String, ArrayList<HashMap<String, String>>> listDataChild;
	private List<String> listExpHeader;
	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private String key, companyname_str, address1_str, address2_str,
			country_str, phone_str, receiptno_str = "", receiptdate_str = "",
			custcode_str, custname_str, remark_str, gnrlStngs, zipcode_str,
			pay_Mode_str, payment_str, bank_code_str, cheque_no_str,
			cheque_date_str,taxType="",taxPerc="",taxName="";

	private UIHelper helper;
	private ImageView print_iv;
	private PrintPreviewAdapter ppa;
	private  ArrayList<HashMap<String, String>> salesreturnArr;
	private ArrayList<ProductDetails> mSRHeaderDetailArr;
	LinearLayout customeraddr1layout, customeraddr2layout, customeraddr3layout;
	double calculate =0.00;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.receipt_print_preview);
		back = (ImageButton) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);

		receiptno_txt = (TextView) findViewById(R.id.receiptno_txt);
		receiptdate_txt = (TextView) findViewById(R.id.receiptdate_txt);

		invoiceno_txt = (TextView) findViewById(R.id.invoiceno_txt);
		invoicedate_txt = (TextView) findViewById(R.id.invoicedate_txt);

		custcode_txt = (TextView) findViewById(R.id.custcode_txt);
		custname_txt = (TextView) findViewById(R.id.custname_txt);

		receiptno = (TextView) findViewById(R.id.receiptno);
		receiptdate = (TextView) findViewById(R.id.receiptdate);
		invoiceno = (TextView) findViewById(R.id.invoiceno);
		invoicedate = (TextView) findViewById(R.id.invoicedate);
		custcode = (TextView) findViewById(R.id.custcode);
		custname = (TextView) findViewById(R.id.custname);
		itemdisc = (TextView) findViewById(R.id.itemdisc);
		billdisc = (TextView) findViewById(R.id.billdisc);
		subtotal = (TextView) findViewById(R.id.subtotal);
		tax = (TextView) findViewById(R.id.tax);
		nettotal = (TextView) findViewById(R.id.nettotal);
		remarks = (TextView) findViewById(R.id.remarks);
		totaloutstanding = (TextView) findViewById(R.id.totaloutstanding);
		receiptno_ll = (LinearLayout) findViewById(R.id.receiptno_ll);
		receiptdate_ll = (LinearLayout) findViewById(R.id.receiptdate_ll);
		paymode_ll = (LinearLayout) findViewById(R.id.paymode_ll);
		bankcode_ll = (LinearLayout) findViewById(R.id.bankcode_ll);
		chequeno_ll = (LinearLayout) findViewById(R.id.chequeno_ll);
		chequedate_ll = (LinearLayout) findViewById(R.id.chequedate_ll);
		custCode = (LinearLayout)findViewById(R.id.cust);
		custName = (LinearLayout)findViewById(R.id.custName);
		itmdsc  = (LinearLayout)findViewById(R.id.itmdsc);
		bdsc = (LinearLayout)findViewById(R.id.bdsc);
		net = (LinearLayout)findViewById(R.id.net);
		nets = (LinearLayout)findViewById(R.id.nets);
		toto = (LinearLayout)findViewById(R.id.tout);
		sno_txt = (TextView)findViewById(R.id.sno_txt);
		description_txt = (TextView) findViewById(R.id.description_txt);
		qty_txt = (TextView)findViewById(R.id.qty_txt);
		price_txt = (TextView)findViewById(R.id.price_txt);
		total_txt = (TextView)findViewById(R.id.total_txt);

		companyname_ll = (LinearLayout) findViewById(R.id.companyname_ll);
		address1_ll = (LinearLayout) findViewById(R.id.address1_ll);
		address2_ll = (LinearLayout) findViewById(R.id.address2_ll);
		country_ll = (LinearLayout) findViewById(R.id.country_ll);
		phone_ll = (LinearLayout) findViewById(R.id.phone_ll);
		remarks_ll = (LinearLayout) findViewById(R.id.remarks_ll);
		companyname = (TextView) findViewById(R.id.companyname);
		address1 = (TextView) findViewById(R.id.address1);
		address2 = (TextView) findViewById(R.id.address2);
		country = (TextView) findViewById(R.id.country);
		phone = (TextView) findViewById(R.id.phone);

		paymode_txt = (TextView) findViewById(R.id.paymode);
		bankcode_txt = (TextView) findViewById(R.id.bankcode);
		chequeno_txt = (TextView) findViewById(R.id.chequeno);
		chequedate_txt = (TextView) findViewById(R.id.chequedate);

		customeraddr1layout = (LinearLayout) findViewById(R.id.customeraddr1layout);
		customeraddr2layout = (LinearLayout) findViewById(R.id.customeraddr2layout);
		customeraddr3layout = (LinearLayout) findViewById(R.id.customeraddr3layout);
		nettol_txt = (TextView)findViewById(R.id.nettotals);

		customeraddr1layout.setVisibility(View.GONE);
		customeraddr2layout.setVisibility(View.GONE);
		customeraddr3layout.setVisibility(View.GONE);

		print_iv = (ImageView) findViewById(R.id.printer);
		expListView = (ExpandableListView) findViewById(R.id.preview_list);

		GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		GlobalData.mService = new BluetoothService(this, mHandler);



		helper = new UIHelper(ReceiptPrintPreview.this);
		product = new ArrayList<ProdDetails>();
		productdet = new ArrayList<ProductDetails>();
		prodet = new ArrayList<ProdDetails>();

		sort = new ArrayList<String>();
		hs = new HashSet<String>();
		mAttributeArr = new ArrayList<Product>();
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, ArrayList<HashMap<String, String>>>();
		listExpHeader = new ArrayList<String>();
		// receiptproductdet = new ArrayList<ProdDetails>();

		listExpHeader.clear();
		lv = (ListView) findViewById(R.id.preview_list1);
		lv1 = (ListView)findViewById(R.id.preview_list2);

		Bundle b = getIntent().getExtras();
		title.setText("Receipt");
		if (b != null) {
			custcode_str = b.getString("customerCode");
			custname_str = b.getString("customerName");
			key = b.getString("Key");
			receiptno_str = b.getString("no");
			receiptdate_str = b.getString("date");
			sort = b.getStringArrayList("sort");
			gnrlStngs = b.getString("gnrlStngs");

		}
		Log.d("sort", "" + sort);
		if (key.matches("InvoiceCashCollection")) {
			receiptno_ll.setVisibility(View.GONE);
			receiptdate_ll.setVisibility(View.GONE);
			custcode.setText(custcode_str);
			custname.setText(custname_str);
			nets.setVisibility(View.GONE);
		} else if (key.matches("Receipt")) {
			receiptno_ll.setVisibility(View.VISIBLE);
			receiptdate_ll.setVisibility(View.VISIBLE);
			receiptno_txt.setText("Cust Code");
			receiptdate_txt.setText("Cust Name");
			invoiceno_txt.setText("Receipt No");
			invoicedate_txt.setText("Receipt Date");
			custcode_txt.setText("Invoice No");
			custname_txt.setText("Invoice Date");
			nets.setVisibility(View.GONE);

			receiptno.setText(custcode_str);
			receiptdate.setText(custname_str);
			invoiceno.setText(receiptno_str);
			invoicedate.setText(receiptdate_str);
		}else if(key.matches("Receipt Multi")){
			mAttributeArr = PreviewPojo.getmAttributeDetails();
			Log.d("getDataList","-->"+mAttributeArr.size());
			receiptno_ll.setVisibility(View.VISIBLE);
			receiptdate_ll.setVisibility(View.VISIBLE);
			custCode.setVisibility(View.GONE);
			custName.setVisibility(View.GONE);
			receiptno_txt.setText("Cust Code");
			receiptdate_txt.setText("Cust Name");
			invoiceno_txt.setText("Receipt No");
			invoicedate_txt.setText("Receipt Date");
			for(int i =0;i<mAttributeArr.size();i++){
				String  paid_amt = mAttributeArr.get(i).getDescription();
				double paid = Double.parseDouble(paid_amt);
				calculate = calculate+paid;
			}
			Log.d("calculatecxheck","-->"+calculate);
			nettol_txt.setText(""+twoDecimalPoint(calculate));
			nets.setVisibility(View.VISIBLE);

			receiptno.setText(custcode_str);
			receiptdate.setText(custname_str);
			invoiceno.setText(receiptno_str);
			invoicedate.setText(receiptdate_str);

			itmdsc.setVisibility(View.GONE);
			bdsc.setVisibility(View.GONE);
			remarks_ll.setVisibility(View.GONE);
			net.setVisibility(View.GONE);
			toto.setVisibility(View.GONE);

			description_txt.setText("Invoice No");
			qty_txt.setText("Invoice Date");
			price_txt.setText("Amount");
			total_txt.setVisibility(View.GONE);
			lv.setVisibility(View.GONE);
			lv1.setVisibility(View.VISIBLE);
			PreviewAdapter ppa = new PreviewAdapter(ReceiptPrintPreview.this, R.layout.printpreview_listitem, mAttributeArr);
			lv1.setAdapter(ppa);

		}
		productdet = PreviewPojo.getProductsDetails();
		product = PreviewPojo.getReceiptproducts();

		taxPerc = SalesOrderSetGet.getCustomerTaxPerc();
		if(taxPerc==null || taxPerc.trim().equals("")){
			taxPerc = "0.00";
		}
		for (ProdDetails products : product) {
			taxType = products.getTaxType();
			taxPerc = products.getTaxPerc();

		}
		for (int i = 0; i < productdet.size(); i++) {
			ProductDetails productdetails = productdet.get(i);
			if (key.matches("InvoiceCashCollection")) {
				invoiceno.setText(productdetails.getItemno());
				invoicedate.setText(productdetails.getItemdate());
			} else if (key.matches("Receipt")) {
				custcode.setText(productdetails.getItemno());
				custname.setText(productdetails.getItemdate());
			}
			Log.d("taxType&taxPerc","-->"+taxType+"taxPerc :"+taxPerc);
			if(taxType.equalsIgnoreCase("I")){
				taxName = "Incl "+taxPerc.split("\\.")[0]+"%";
			}else if(taxType.equalsIgnoreCase("E")){
				taxName = "Excl "+taxPerc.split("\\.")[0]+"%";
			}else{
				taxName = "Tax";
			}
			((TextView) findViewById(R.id.tax_txt)).setText(taxName+" :");
			Log.d("taxType","-->"+taxType);
			/********************/
			//If taxtype is I then subtotal = netotal - tax
			String netTotal = productdetails.getNettot().toString();
			if(netTotal!=null && !netTotal.isEmpty()){

			}else{
				netTotal = "0.00";
			}
			String taxStr = productdetails.getTax().toString();
			Log.d("taxStr","-->"+taxStr);
			if(taxStr!=null && !taxStr.isEmpty()){

			}else{
				taxStr = "0.00";
			}


			double dNetTotal = Double.valueOf(netTotal);
			double dTax = Double.valueOf(taxStr);
			double dSubtotal =  dNetTotal - dTax;
			Log.d("dNetTotal","-->"+dNetTotal);
			Log.d("dTax","-->"+dTax);
			Log.d("subtotal","-->"+dSubtotal);

			itemdisc.setText(productdetails.getItemdisc());
			billdisc.setText(productdetails.getBilldisc());
			if(taxType!=null && !taxType.isEmpty()){
				if (taxType.matches("I")) {
					subtotal.setText(twoDecimalPoint(dSubtotal));
				}else{
					subtotal.setText(productdetails.getSubtotal());
				}
			}else{
				subtotal.setText(productdetails.getSubtotal());
			}
			//subtotal.setText(productdetails.getSubtotal());
			tax.setText(productdetails.getTax());
			nettotal.setText(productdetails.getNettot());
			// remarks.setText(productdetails.getRemarks());
			remark_str = productdetails.getRemarks();
			if (remark_str.matches("")) {
				remarks_ll.setVisibility(View.GONE);
			} else {
				remarks.setText(remark_str);
			}
			totaloutstanding.setText(productdetails.getTotaloutstanding());
		}
		companyname_str = Company.getCompanyName();
		address1_str = Company.getAddress1();
		address2_str = Company.getAddress2();
		country_str = Company.getCountry();
		phone_str = Company.getPhoneNo();
		zipcode_str = Company.getZipCode();

		if (companyname_str.matches("")) {
			companyname_ll.setVisibility(View.GONE);
		} else {
			companyname.setText(companyname_str);
		}

		if (address1_str.matches("")) {
			address1_ll.setVisibility(View.GONE);
		} else {
			address1.setText(address1_str);
		}

		if (address2_str.matches("")) {
			address2_ll.setVisibility(View.GONE);
		} else {
			address2.setText(address2_str);
		}

		if (country_str.matches("")) {
			country_ll.setVisibility(View.GONE);
		} else {
			// country.setText(country_str);
			country.setText(country_str + " " + zipcode_str);
		}

		if (phone_str.matches("")) {
			phone_ll.setVisibility(View.GONE);
		} else {
			phone.setText(phone_str);
		}
		if (gnrlStngs.matches("C") || gnrlStngs.matches("S")) {
			int no = 1;
			/* sort product by None */
			for (ProdDetails products : product) {

				ProdDetails prod = new ProdDetails();
				if ((products.getSortproduct().matches(""))) {
					int i = 1;
					// prod.setSno(products.getSno());
					prod.setSno(String.valueOf(no));
					prod.setDescription(products.getDescription());
					prod.setQty(products.getQty());
					prod.setPrice(products.getPrice());
					prod.setTotal(products.getTotal());
					prod.setFocqty(products.getFocqty());

					prodet.add(prod);
					s += i;
					i++;
					no++;
				}
			}

			if (prodet.size() > 0) {
				Log.d("snno-size-<", "" + prodet.size());
				PrintPreviewAdapter ppa = new PrintPreviewAdapter(
						ReceiptPrintPreview.this,
						R.layout.printpreview_listitem, prodet);
				lv.setAdapter(ppa);
			} else {
				Log.d("snno-size-<", "" + prodet.size());
				lv.setVisibility(View.GONE);
			}

			for (int i = 0; i < sort.size(); i++) {
				String catagory = sort.get(i).toString();
				for (ProdDetails products : product) {

					if (catagory.matches(products.getSortproduct())) {

						listExpHeader.add(catagory);
					}
				}
			}
			hs.addAll(listExpHeader);
			listDataHeader.clear();
			listDataHeader.addAll(hs);
			prepareListData();
			Log.d("exp-size-<", "" + listDataChild.size());
			if (listDataChild.size() > 0) {
				listAdapter = new ExpandableListAdapter(this, listDataHeader,
						listDataChild);
				expListView.setAdapter(listAdapter);

				for (int i = 0; i < listAdapter.getGroupCount(); i++) {
					expListView.expandGroup(i);
				}
			} else {
				expListView.setVisibility(View.GONE);
			}
		} else {
			Log.d("product--<", "" + product.size());
			expListView.setVisibility(View.GONE);
			ppa = new PrintPreviewAdapter(ReceiptPrintPreview.this,
					R.layout.printpreview_listitem, product);
			lv.setAdapter(ppa);
		}
		pay_Mode_str = In_Cash.getPay_Mode();
		if (!pay_Mode_str.matches("") || !pay_Mode_str.matches("null")
				|| !pay_Mode_str.matches(null)) {
			Log.d("pay_Mode_str", "--->" + pay_Mode_str);
			paymode_txt.setText(pay_Mode_str);
			payment_str = paymode_txt.getText().toString();
			if (pay_Mode_str.matches(payment_str)) {
				// payment_str = "Cheque";
				bank_code_str = In_Cash.getBank_code();
				Log.d("bank_code_str", "--->" + bank_code_str);
				// bank_Name = In_Cash.getBank_Name();
				cheque_no_str = In_Cash.getCheck_No();
				cheque_date_str = In_Cash.getCheck_Date();
				Log.d("cheque_date_str","-->"+cheque_date_str);

			} else if (pay_Mode_str.matches(payment_str)) {
				Log.d("bank_code_str", "--->" + "ccccash");
				// payment_str = "Cash";
				bank_code_str = "";
				cheque_no_str = "";
				cheque_date_str = "01/01/1900";

			}

			if (bank_code_str.matches("")) {
				bankcode_ll.setVisibility(View.GONE);

			} else {
				bankcode_txt.setText(bank_code_str);
			}
			if (cheque_no_str.matches("")) {
				chequeno_ll.setVisibility(View.GONE);

			} else {
				chequeno_txt.setText(cheque_no_str);
			}
			if (cheque_no_str.matches("")) {

				chequedate_ll.setVisibility(View.GONE);

			} else {
				StringTokenizer tk = new StringTokenizer(cheque_date_str);

				String date = tk.nextToken();  // <---  yyyy-mm-dd
//				String time = tk.nextToken();  // <---  hh:mm:ss
				chequedate_txt.setText(date);

			}

		} else {
			paymode_ll.setVisibility(View.GONE);
			bankcode_ll.setVisibility(View.GONE);
			chequeno_ll.setVisibility(View.GONE);
			chequedate_ll.setVisibility(View.GONE);
		}

		print_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialogPrint();
			}
		});

		/*
		 * lv.setOnTouchListener(new ListView.OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { int
		 * action = event.getAction(); switch (action) { case
		 * MotionEvent.ACTION_DOWN: // Disallow ScrollView to intercept touch
		 * events. v.getParent().requestDisallowInterceptTouchEvent(true);
		 * break;
		 * 
		 * case MotionEvent.ACTION_UP: // Allow ScrollView to intercept touch
		 * events. v.getParent().requestDisallowInterceptTouchEvent(false);
		 * break; }
		 * 
		 * // Handle ListView touch events. v.onTouchEvent(event); return true;
		 * } });
		 * 
		 * setListViewHeightBasedOnChildren(lv);
		 */

		/*
		 * ExpandableListView: view = listAdapter.getView(0, view, listView);
		 * int widthMeasureSpec =
		 * View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.MATCH_PARENT,
		 * View.MeasureSpec.EXACTLY); int heightMeasureSpec =
		 * View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT,
		 * View.MeasureSpec.EXACTLY); view.measure(widthMeasureSpec,
		 * heightMeasureSpec);
		 */
	}

	/*
	 * private void setListViewHeightBasedOnChildren(ListView lv2) { // TODO
	 * Auto-generated method stub ListAdapter listAdapter = lv2.getAdapter(); if
	 * (listAdapter == null) { // pre-condition return; }
	 * 
	 * int totalHeight = lv2.getPaddingTop() + lv2.getPaddingBottom(); for (int
	 * i = 0; i < listAdapter.getCount(); i++) { View listItem =
	 * listAdapter.getView(i, null, lv2); if (listItem instanceof ViewGroup) {
	 * listItem.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
	 * LayoutParams.MATCH_PARENT)); } listItem.measure(0, 0); totalHeight +=
	 * listItem.getMeasuredHeight(); }
	 * 
	 * ViewGroup.LayoutParams params = lv2.getLayoutParams(); params.height =
	 * totalHeight + (lv2.getDividerHeight() * (listAdapter.getCount() - 1));
	 * lv2.setLayoutParams(params); lv2.requestLayout(); }
	 */
	public static String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}
	private void prepareListData() {

		for (int i = 0; i < listDataHeader.size(); i++) {
			ArrayList<HashMap<String, String>> sublistData = new ArrayList<HashMap<String, String>>();
			String headerString = listDataHeader.get(i);

			for (ProdDetails products : product) {

				if (headerString.matches(products.getSortproduct())) {
					HashMap<String, String> sublistDataItem = new HashMap<String, String>();
					sublistDataItem.put("Sno", String.valueOf(s));
					// sublistDataItem.put("Sno",products.getSno());
					sublistDataItem.put("Description",
							products.getDescription());
					sublistDataItem.put("Qty", products.getQty());
					sublistDataItem.put("cat", products.getSortproduct());
					sublistDataItem.put("Price", products.getPrice());
					sublistDataItem.put("Total", products.getTotal());
					sublistData.add(sublistDataItem);
					s++;
				}
				Log.d("sublistData -->", "" + sublistData.size());
			}
			Log.d("sublistData", sublistData.toString());
			listDataChild.put(headerString, sublistData);
		}
	}

	/* sort product by None */
	public class PrintPreviewAdapter extends BaseAdapter {
		List<ProductDetails> productdet;
		List<ProdDetails> product;
		private int mResource;
		List<String> invoiceheader;
		private LayoutInflater mInflater;
		Context activity;

		public PrintPreviewAdapter(Context context, int resource,
				List<ProdDetails> product) {
			this.product = product;
			mResource = resource;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.activity = context;
		}

		@Override
		public int getCount() {
			return product.size();
		}

		@Override
		public ProdDetails getItem(int position) {
			return product.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ProdDetails productdetails = product.get(position);
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(mResource, parent, false);
				holder.sno = (TextView) convertView.findViewById(R.id.sno);
				holder.description = (TextView) convertView
						.findViewById(R.id.description);
				holder.qty = (TextView) convertView.findViewById(R.id.qty);
				holder.price = (TextView) convertView.findViewById(R.id.price);
				holder.total = (TextView) convertView.findViewById(R.id.total);
				holder.foc =(TextView)convertView.findViewById(R.id.foc);
				holder.foc_txt =(TextView)findViewById(R.id.foc_txt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.sno.setText(productdetails.getSno());
			holder.description.setText(productdetails.getDescription());
			holder.qty.setText(productdetails.getQty());
			String decimal= MobileSettingsSetterGetter.getDecimalPoints();
			holder.price.setText(String.format("%."+decimal+"f", Double.parseDouble(productdetails.getPrice())));
			holder.total.setText(productdetails.getTotal());
			Log.d("products.getFocqty()","-->"+productdetails.getFocqty());
			if(productdetails.getFocqty()>0.0){
				holder.foc_txt.setVisibility(View.VISIBLE);
				holder.foc.setVisibility(View.VISIBLE);
				holder.foc.setText(""+(int) productdetails.getFocqty());
			}

			return convertView;
		}

		class ViewHolder {
			TextView sno;
			TextView description;
			TextView qty;
			TextView price;
			TextView total;
			TextView foc,foc_txt;
		}
	}

	/* sort product by Catagory and Sub Catagory */
	public class ExpandableListAdapter extends BaseExpandableListAdapter {

		private Context _context;
		private List<String> _listDataHeader;
		private HashMap<String, ArrayList<HashMap<String, String>>> _listDataChild;
		List<ProductDetails> product = new ArrayList<ProductDetails>();

		public ExpandableListAdapter(
				Context context,
				List<String> listDataHeader,
				HashMap<String, ArrayList<HashMap<String, String>>> listChildData) {
			this._context = context;
			this._listDataHeader = listDataHeader;
			this._listDataChild = listChildData;
		}

		@Override
		public Object getChild(int groupPosition, int childPosititon) {
			return this._listDataChild.get(
					this._listDataHeader.get(groupPosition))
					.get(childPosititon);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			final HashMap<String, String> childValue = (HashMap<String, String>) getChild(
					groupPosition, childPosition);
			Log.d("childValue", "" + childValue.size());
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(
						R.layout.printpreview_listitem, null);
			}

			TextView sno = (TextView) convertView.findViewById(R.id.sno);
			TextView description = (TextView) convertView
					.findViewById(R.id.description);
			TextView qty = (TextView) convertView.findViewById(R.id.qty);
			TextView price = (TextView) convertView.findViewById(R.id.price);
			TextView total = (TextView) convertView.findViewById(R.id.total);

			Log.d("_listDataChild value", "" + _listDataChild.toString());
			Log.d("childPosition", "" + childPosition);
			Log.d("childValue", "" + childValue.size());

			for (int i = 0; i < childValue.size(); i++) {
				sno.setText(childValue.get("Sno"));
				description.setText(childValue.get("Description"));
				qty.setText(childValue.get("Qty"));
				price.setText(childValue.get("Price"));
				total.setText(childValue.get("Total"));
			}
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return this._listDataChild.get(
					this._listDataHeader.get(groupPosition)).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return this._listDataHeader.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return this._listDataHeader.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			String headerTitle = (String) getGroup(groupPosition);
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(
						R.layout.preview_exp_header, null);
			}

			TextView lblListHeader = (TextView) convertView
					.findViewById(R.id.lblListHeader);
			lblListHeader.setTypeface(null, Typeface.BOLD);
			lblListHeader.setText(headerTitle);

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	private void print(ArrayList<Product> mAttributeArr) throws IOException {
		mSRHeaderDetailArr = new ArrayList<>();
		salesreturnArr = new ArrayList<HashMap<String, String>>();
		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

		if(printertype.matches("Zebra iMZ320")) {
			helper.dismissProgressDialog();
			try {
				Printer printer = new Printer(ReceiptPrintPreview.this, macaddress);

				List<ProductDetails> footerArr = new ArrayList<ProductDetails>();
				if (mAttributeArr.size() != 0) {
					Log.d("multiprint()", "started!!"+calculate);
					printer.printMultiReceipt(custcode_str, custname_str, receiptno_str, receiptdate_str, mAttributeArr, 1, true, calculate);
				} else {
					printer.printReceipt(custcode_str, custname_str, receiptno_str,
							receiptdate_str, productdet, listDataHeader, gnrlStngs, 1,
							true, footerArr, salesreturnArr, mSRHeaderDetailArr);
				}
			} catch (IllegalArgumentException e) {
				helper.showLongToast(R.string.error_configure_printer);
			}
		}else if(printertype.matches("4 Inch Bluetooth")){

			if (BluetoothAdapter.checkBluetoothAddress(macaddress))
			{
				BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
				// Attempt to connect to the device
				GlobalData.mService.setHandler(mHandler);
				GlobalData.mService.connect(device,true);
			}

			helper.dismissProgressDialog();
		}
		else if(printertype.matches("3 Inch Bluetooth Generic")){
			helper.dismissProgressDialog();
			try {
				final CubePrint print = new CubePrint(ReceiptPrintPreview.this,macaddress);
				print.initGenericPrinter();
				print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
					@Override
					public void initCompleted() {
						List<ProductDetails> footerArr = new ArrayList<ProductDetails>();
						print.printReceipt(custcode_str, custname_str, receiptno_str,
								receiptdate_str, productdet, listDataHeader, gnrlStngs, 1, true, footerArr,1,salesreturnArr);

						print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
							@Override
							public void onCompleted() {
								helper.showLongToast(R.string.printed_successfully);
							}
						});
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(printertype.matches("Zebra iMZ320 4 Inch")){

			helper.dismissProgressDialog();
			try {
				PrinterZPL printer = new PrinterZPL(ReceiptPrintPreview.this, macaddress);

				List<ProductDetails> footerArr = new ArrayList<ProductDetails>();
				printer.printReceipt(custcode_str, custname_str, receiptno_str,
						receiptdate_str, productdet, listDataHeader, gnrlStngs, 1,
						true, footerArr,salesreturnArr);
			} catch (IllegalArgumentException e) {
				helper.showLongToast(R.string.error_configure_printer);
			}
		}
	}

	public void alertDialogPrint() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				ReceiptPrintPreview.this);
		alertDialog.setTitle("Print");
		alertDialog.setMessage("Do you want to print the Receipt");
		// alertDialog.setIcon(R.drawable.slidemenu_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							helper.showProgressDialog(R.string.generating_receipt);
							print(mAttributeArr);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});
		alertDialog.show();
	}

	@Override
	public void onStart() {
		super.onStart();
		//if(D) Log.e(TAG, "--- onStart ---");
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

		if(printertype.matches("4 Inch Bluetooth")) {
			if (!GlobalData.mBluetoothAdapter.isEnabled()) {
				GlobalData.mBluetoothAdapter.enable();
				//Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				//startActivity(enableIntent);*/
				// Otherwise, setup the chat session
			} else {
				if (GlobalData.mService == null) {
					GlobalData.mService = new BluetoothService(this, mHandler);
				}
			}
		}
	}
	protected void onDestroy(){
		super.onDestroy();
		if(GlobalData.mService!=null){
			GlobalData.mService.stop();
		}
	}
	public void reconnectDialog(String msg){
		final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		final Dialog dialog = new Dialog(ReceiptPrintPreview.this);

		dialog.setContentView(R.layout.dialog_reconnect);
		dialog.setTitle(msg);
		ImageView reconnect = (ImageView) dialog.findViewById(R.id.reconnect);

		reconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (BluetoothAdapter.checkBluetoothAddress(macaddress))
				{
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device,true);

				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case GlobalData.MESSAGE_STATE_CHANGE:
					Log.d("case","MESSAGE_STATE_CHANGE");
					//if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
					switch (msg.arg1) {
						case GlobalData.STATE_CONNECTED:
							//mTitle.setText(R.string.title_connected_to);
							//mTitle.append(mConnectedDeviceName);
							//Intent intent = new Intent(BluetoothActivity.this,PrintActivity.class);
							//intent.putExtra("COMM", 0);//0-BLUETOOTH
							// Set result and finish this Activity
							// startActivity(intent);
							Log.d("case","STATE_CONNECTED");
							print4Inch();
							break;
						case GlobalData.STATE_CONNECTING:
							//mTitle.setText(R.string.title_connecting);
							Log.d("case","STATE_CONNECTING");

							break;
						case GlobalData.STATE_LISTEN:
							Log.d("case","STATE_LISTEN");
							break;
						case GlobalData.STATE_NONE:
							Log.d("case","STATE_NONE");
							//mTitle.setText(R.string.title_not_connected);
							break;
					}
					break;
				case GlobalData.MESSAGE_DEVICE_NAME:
					// save the connected device's name
					String mConnectedDeviceName = msg.getData().getString("device_name");
					Toast.makeText(getApplicationContext(), "Connected to "
							+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
					break;
				case GlobalData.MESSAGE_TOAST:
					reconnectDialog(msg.getData().getString("toast"));
					Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
							Toast.LENGTH_SHORT).show();
                   /* helper.dismissProgressDialog();
                    finish();
     *//*Intent i = new Intent(InvoiceCashCollection.this,
       InvoiceHeader.class);
     i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
     startActivity(i);*//*
                    if (header.matches("InvoiceHeader")) {
                        intent = new Intent(InvoiceCashCollection.this,
                                InvoiceHeader.class);

                    } else if (header.matches("CustomerHeader")) {
                        intent = new Intent(InvoiceCashCollection.this,
                                CustomerListActivity.class);

                    } else if (header.matches("RouteHeader")) {
                        intent = new Intent(InvoiceCashCollection.this,
                                RouteHeader.class);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    InvoiceCashCollection.this.finish();*/
					break;
			}

		}
	};

	public void print4Inch(){
//		int noofInvoice = noOfInvoice();
		CubePrint mPrintCube = new CubePrint(ReceiptPrintPreview.this,FWMSSettingsDatabase.getPrinterAddress());
		mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
			@Override
			public void onCompleted() {

				helper.showLongToast(R.string.printed_successfully);
			}
		});

		List<ProductDetails> footerArr = new ArrayList<ProductDetails>();

		mPrintCube.printReceipt(custcode_str, custname_str, receiptno_str,
					receiptdate_str, productdet, listDataHeader, gnrlStngs, 1, true, footerArr,1,salesreturnArr);


	}

	@Override
	public void onBackPressed() {

		ReceiptPrintPreview.this.finish();

	}

	private class PreviewAdapter extends BaseAdapter{
		Context context;
		List<Product> array;
		int mResource;
		LayoutInflater mInflater;
		public PreviewAdapter(Context context, int resourcer, List<Product> mAttributeArr) {
			this.array = mAttributeArr;
			this.mResource = resourcer;
			this.mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;
			Log.d("mAttributeArr","-->"+array.size());
		}

		@Override
		public int getCount() {
			return array.size();
		}

		@Override
		public Object getItem(int position) {
			return array.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Product details = array.get(position);
			Log.d("productdetails","-->"+array.size());
			final ViewHolder holder;
			holder = new ViewHolder();
			convertView = mInflater.inflate(mResource, parent, false);
			Log.d("productdetails123",details.getCode());
			holder.sno = (TextView)convertView.findViewById(R.id.sno);
			holder.invoiceNo = (TextView)convertView.findViewById(R.id.description);
			holder.invoiceDte = (TextView)convertView.findViewById(R.id.qty);
			holder.amt = (TextView)convertView.findViewById(R.id.price);
			holder.hide = (TextView)convertView.findViewById(R.id.total);
			holder.hide.setVisibility(View.GONE);

			Log.d("productdetails",details.getCode());
			holder.sno.setText(details.getNo());
			holder.invoiceNo.setText(details.getCode());
			holder.invoiceDte.setText(details.getTranDate());
			holder.amt.setText(details.getDescription());

			return convertView;
		}

		class ViewHolder{
			TextView sno,invoiceNo,invoiceDte,amt,hide;
		}
	}
}
