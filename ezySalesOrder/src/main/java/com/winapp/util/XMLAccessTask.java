package com.winapp.util;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.winapp.fwms.LoginActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.MyUncaughtExceptionHandler;
import com.winapp.helper.XMLParser;
import com.winapp.sot.CashInvoiceHeader;
import com.winapp.sot.ConsignmentHeader;
import com.winapp.sot.DeliveryOrderHeader;
import com.winapp.sot.GraHeader;
import com.winapp.sot.InvoiceHeader;
import com.winapp.sot.MerchandiseDetailHeader;
import com.winapp.sot.SalesAddProduct;
import com.winapp.sot.SalesOrderHeader;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesReturnAddProduct;
import com.winapp.sot.SalesReturnHeader;
import com.winapp.sot.SalesReturnSummary;
import com.winapp.sot.SalesSummary;
import com.winapp.sotdetails.CRMTaskAdd;

public class XMLAccessTask extends AsyncTask<Void, Void, NodeList> {
	private ErrorType mError;
	HashMap<String, String> params = new HashMap<String, String>();
	Context mContext;
	String xml,InvoiceNo,customerGroupCode, transtype,mCustomerCode;
	String mWebserviceUrl, methodname, productcode, locationcode, category,
			subcategory, pageNo,refNo="",TranType="",productName="";
	private CallbackInterface mCallbackInterface;
	NodeList nl;
	boolean location;
	private String mUrl="";
	public enum ErrorType {
		NETWORK_UNAVAILABLE, IO_ERROR, XML_PARSE_ERROR, JSON_PARSE_ERROR
	};

	public interface CallbackInterface {
		public void onSuccess(NodeList nl);

		public void onFailure(ErrorType error);
	}

	public XMLAccessTask(Context context, String webserviceUrl,
			String methodname, HashMap<String, String> params,
			boolean location, CallbackInterface callbackInterface) {
		Thread.currentThread();
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
		this.mWebserviceUrl = webserviceUrl;
		this.methodname = methodname;
		this.params = params;
		this.mContext = context;
		this.location = location;
		this.mCallbackInterface = callbackInterface;

	}
	public XMLAccessTask(Context context,String webserviceUrl,String methodname, String productCode,String customerGroupCode,
			   boolean location,CallbackInterface callbackInterface) { 
			  Thread.currentThread();
			  Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
			  this.mContext = context; 
			  this.mWebserviceUrl = webserviceUrl;
			  this.methodname = methodname;  
			  this.location = location;
			   params.put("PageNo", "");
			   params.put("ProductCode",productCode);
			   params.put("CategoryCode", "");
			   params.put("SubCategoryCode", "");
			   params.put("CustomerGroupCode", customerGroupCode);
		       params.put("CustomerCode", "");
		       params.put("TranType", "");
		       params.put("ProductName", "");
			   this.mCallbackInterface = callbackInterface;
			  
			 }
	public XMLAccessTask(Context context, String webserviceUrl,
			String methodname, CallbackInterface callbackInterface) {
		Thread.currentThread();
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
		this.mWebserviceUrl = webserviceUrl;
		this.methodname = methodname;
		this.mContext = context;
		this.mCallbackInterface = callbackInterface;
	}

	public XMLAccessTask(Context context, String webserviceUrl,
						 String methodname, String productcode,CallbackInterface callbackInterface) {
		Thread.currentThread();
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
		this.mWebserviceUrl = webserviceUrl;
		this.methodname = methodname;
		this.mContext = context;
		this.mCallbackInterface = callbackInterface;
		this.productcode = productcode;
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected NodeList doInBackground(Void... arg0) {

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();		

			try {
				if (!cmpnyCode.matches("")) {
				if (mContext instanceof LoginActivity) {

					Log.d("mWebserviceUrl", ""+mWebserviceUrl + "/"
							+ methodname + "?CompanyCode=" + cmpnyCode);
					
					mUrl = mWebserviceUrl + "/"
							+ methodname + "?CompanyCode=" + cmpnyCode;

//					mUrl = mUrl.replaceAll("\\s+","");
				}else if(mContext instanceof SalesAddProduct || mContext instanceof SalesSummary || mContext instanceof SalesReturnSummary || mContext instanceof SalesReturnAddProduct) {

					if(methodname.equals("fncGetProductSubImages")){
						productcode = params.get("ProductCode");
						category = "";
						subcategory = "";
						locationcode = SalesOrderSetGet.getLocationcode();
						mUrl = ""+mWebserviceUrl+"/"+methodname + "?CompanyCode=" + cmpnyCode
								+ "&ProductCode=" + productcode
								+ "&CategoryCode=" + category
								+ "&SubCategoryCode=" + subcategory
								+ "&LocationCode=" + locationcode + "";
						mUrl = mUrl.replaceAll(" ", "%20");

						Log.d("url", "getproduct sub->"+mUrl);
					}else{

						mUrl = mWebserviceUrl
								+ "/" + methodname + "?CompanyCode="
								+ cmpnyCode + "&PageNo=" + ""
								+ "&ProductCode=" + productcode
								+ "&CategoryCode=" + ""
								+ "&SubCategoryCode=" + ""
								+ "&CustomerGroupCode=" + ""
								+ "&CustomerCode=" + ""
								+ "&TranType=" + ""
								+ "&ProductName=" + "";

						Log.d("mWebserviceUrl", ""+mUrl);
					}

				} else if (mContext instanceof MerchandiseDetailHeader) {
				     
				     refNo = params.get("RefNo");     
				     
				     mUrl = mWebserviceUrl + "/"
				       + methodname + "?CompanyCode=" + cmpnyCode + "&RefNo=" + refNo;

				     mUrl = mUrl.replaceAll(" ", "%20");
				     
				     Log.d("url", ""+mUrl);

					//     xml = XMLParser.getXmlFromUrl(mUrl);
				    } else if(methodname.equals("fncGetProductSubImages")){
					productcode = params.get("ProductCode");
					category = "";
					subcategory = "";
					locationcode = SalesOrderSetGet.getLocationcode();
					mUrl = ""+mWebserviceUrl+"/"+methodname + "?CompanyCode=" + cmpnyCode
							+ "&ProductCode=" + productcode
							+ "&CategoryCode=" + category
							+ "&SubCategoryCode=" + subcategory
					        + "&LocationCode=" + locationcode + "";
					mUrl = mUrl.replaceAll(" ", "%20");

					Log.d("url", "getproduct sub->"+mUrl);
				}
				else {

					if (mContext instanceof InvoiceHeader || mContext instanceof DeliveryOrderHeader || mContext instanceof SalesReturnHeader||
							mContext instanceof SalesOrderHeader || mContext instanceof GraHeader  || mContext instanceof CRMTaskAdd
							|| mContext instanceof CashInvoiceHeader || mContext instanceof ConsignmentHeader) {
						
						InvoiceNo = params.get("InvoiceNo");
						transtype = params.get("TranType");
						
						mUrl = mWebserviceUrl + "/"
								+ methodname + "?CompanyCode=" + cmpnyCode + "&InvoiceNo=" + InvoiceNo + "&TranType=" + transtype;

						mUrl = mUrl.replaceAll(" ", "%20");
						
					//	xml = XMLParser.getXmlFromUrl(mUrl); 
						
//						xml = XMLParser.getXmlFromUrl(mWebserviceUrl + "/"
//								+ methodname + "?CompanyCode=" + cmpnyCode + "&InvoiceNo=" + InvoiceNo); 
					
					}

					else {

						productcode = params.get("ProductCode");
						locationcode = params.get("LocationCode");
						category = params.get("CategoryCode");
						subcategory = params.get("SubCategoryCode");
						customerGroupCode = params.get("CustomerGroupCode");

						mCustomerCode = params.get("CustomerCode");
						TranType = params.get("TranType");
						productName = params.get("ProductName");

						if (location == true) {  // for catalog subimage

							mUrl = 	mWebserviceUrl
									+ "/" + methodname + "?CompanyCode="
									+ cmpnyCode + "&LocationCode="
									+ locationcode + "&ProductCode="
									+ productcode + "&CategoryCode=" + category
									+ "&SubCategoryCode=" + subcategory;												// URL
							
							mUrl = mUrl.replaceAll(" ", "%20");
							Log.d("URK",""+mUrl);
						//	xml = XMLParser.getXmlFromUrl(mUrl);
							
						} else { // catalog main image
							pageNo = params.get("PageNo");
							/*xml = XMLParser.getXmlFromUrl("" + mWebserviceUrl
									+ "/" + methodname + "?CompanyCode="
									+ cmpnyCode + "&PageNo=" + pageNo
									+ "&ProductCode=" + productcode
									+ "&CategoryCode=" + category
									+ "&SubCategoryCode=" + subcategory
									+ "&CustomerGroupCode=" + customerGroupCode + "");*/ // getting
							mUrl = mWebserviceUrl
									+ "/" + methodname + "?CompanyCode="
									+ cmpnyCode + "&PageNo=" + pageNo
									+ "&ProductCode=" + productcode
									+ "&CategoryCode=" + category
									+ "&SubCategoryCode=" + subcategory
									+ "&CustomerGroupCode=" + customerGroupCode
									+ "&CustomerCode=" + mCustomerCode
									+ "&TranType=" + TranType
							        + "&ProductName=" + productName;
							// URL
							
							mUrl = mUrl.replaceAll(" ", "%20");
							Log.d("url", ""+mUrl);
						//	xml = XMLParser.getXmlFromUrl(mUrl);
							
							
 
							// xml = XMLParser
							// .getXmlFromUrl(""+mWebserviceUrl+"/"+methodname+"?CompanyCode="+cmpnyCode+"&ProductCode="+productcode+"&CategoryCode="+category+"&SubCategoryCode="+subcategory+"");
						}
					}

				}
				URL url = new URL(mUrl);
				
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				// Download the XML file
				Document doc = db.parse(new InputSource(url.openStream()));
				doc.getDocumentElement().normalize();
				NodeList nl = doc.getElementsByTagName("tbl");
				
			//	Document doc = XMLParser.getDomElement(xml); // getting DOM
																// element
				//nl = doc.getElementsByTagName("tbl");
				Log.d("nl.getLength()", "---"+nl.getLength());
				return nl;
				}	
				return null;
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				mError = ErrorType.IO_ERROR;
				e.printStackTrace();
				return null;
			}
			
	}

	@Override
	protected void onPostExecute(NodeList nl) {
		try {
			Log.d("try","try");
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			if (!cmpnyCode.matches("")) {
				if (nl.getLength() > 0) {
					mCallbackInterface.onSuccess(nl);
				} else {
					mCallbackInterface.onFailure(mError);
				}
			} else {
				mCallbackInterface.onFailure(mError);
			}
		} catch (IllegalArgumentException e) {
			mCallbackInterface.onFailure(mError);
		} catch (Exception e) {
			Log.d("Exception",e.toString());
			mCallbackInterface.onFailure(mError);
		}
	}
}
