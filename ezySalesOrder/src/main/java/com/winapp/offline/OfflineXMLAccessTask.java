package com.winapp.offline;

import java.net.URL;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.winapp.helper.MyUncaughtExceptionHandler;
import com.winapp.helper.XMLParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class OfflineXMLAccessTask extends AsyncTask<Void, Void, NodeList> {
	private XMLErrorType mError;
	private HashMap<String, String> params;
	private Context mContext;
	private String xml;
	private String mWebserviceUrl, methodname, vlaue, companycode, ModifyDate, productcode, locationcode, category,
		subcategory, pageNo, customerGroupCode;
	private XMLCallbackInterface mCallbackInterface;
	private NodeList nl;
	private boolean location;;

	public enum XMLErrorType {
		NETWORK_UNAVAILABLE, IO_ERROR, XML_PARSE_ERROR, JSON_PARSE_ERROR
	};

	public interface XMLCallbackInterface {
		public void onSuccess(NodeList nl);

		public void onFailure(XMLErrorType error);
	}

	public OfflineXMLAccessTask(Context context, String webserviceUrl,
			String methodname, String params, XMLCallbackInterface callbackInterface) {
		// Thread.currentThread();
		// Thread.setDefaultUncaughtExceptionHandler(new
		// MyUncaughtExceptionHandler());
		this.mWebserviceUrl = webserviceUrl;
		this.methodname = methodname;
		this.mContext = context;
		this.vlaue = params;
		this.mCallbackInterface = callbackInterface;
	}
	
	public OfflineXMLAccessTask(Context context, String webserviceUrl,
			String methodname, HashMap<String, String> params, XMLCallbackInterface callbackInterface) {
		Thread.currentThread();
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
		this.mWebserviceUrl = webserviceUrl;
		this.methodname = methodname;
//		this.vlaue = params;
		this.mContext = context;
		this.mCallbackInterface = callbackInterface;
		
		companycode = params.get("CompanyCode");
		ModifyDate = params.get("ModifyDate");
		this.vlaue = companycode + "&ModifyDate="+ ModifyDate + "";
		
	}
	
	public OfflineXMLAccessTask(Context context, String webserviceUrl,
			String methodname, HashMap<String, String> params,
			boolean location, XMLCallbackInterface callbackInterface) {
		Thread.currentThread();
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
		this.mWebserviceUrl = webserviceUrl;
		this.methodname = methodname;
//		this.vlaue = params;
		this.mContext = context;
		this.location = location;
		this.mCallbackInterface = callbackInterface;
		
		companycode = params.get("CompanyCode");
		productcode = params.get("ProductCode");
		locationcode = params.get("LocationCode");
		category = params.get("CategoryCode");
		subcategory = params.get("SubCategoryCode");
		customerGroupCode = params.get("CustomerGroupCode");
		
		if (location == true) {
			
			this.vlaue = companycode + "&LocationCode="
					+ locationcode + "&ProductCode="
					+ productcode + "&CategoryCode=" + category
					+ "&SubCategoryCode=" + subcategory + "";
		} else {
			pageNo = params.get("PageNo");
			this.vlaue = companycode + "&PageNo=" + pageNo
					+ "&ProductCode=" + productcode
					+ "&CategoryCode=" + category
					+ "&SubCategoryCode=" + subcategory
					+ "&CustomerGroupCode=" + customerGroupCode + "";
		}
	}

	@Override
	protected void onPreExecute() {
		nl=null;
	}

	@Override
	protected NodeList doInBackground(Void... arg0) {

		// if(!params.matches("")){

		try {

			/*xml = XMLParser.getXmlFromUrl(mWebserviceUrl + "/" + methodname
					+ "?CompanyCode=" + vlaue); // getting URL

			 Log.d("-"+methodname, "->"+mWebserviceUrl + "/" + methodname+ "?CompanyCode=" + vlaue);
			Document doc = XMLParser.getDomElement(xml); // getting DOM element

			nl = doc.getElementsByTagName("tbl");*/

			String urlStr = mWebserviceUrl + "/" + methodname
					+ "?CompanyCode=" + vlaue;

			Log.d("-" + methodname, "->" + mWebserviceUrl + "/" + methodname + "?CompanyCode=" + vlaue);

			URL url = new URL(urlStr);

			//HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			// Download the XML file
			Document doc = db.parse(new InputSource(url.openStream()));
			doc.getDocumentElement().normalize();
			nl = doc.getElementsByTagName("tbl");

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			mError = XMLErrorType.IO_ERROR;
			e.printStackTrace();
			return null;
		}
		// }
		return nl;
	}

	@Override
	protected void onPostExecute(NodeList nl) {
		try {

			Log.d(methodname+" lenth","->"+nl.getLength());
			if (nl.getLength() >= 0) {
				mCallbackInterface.onSuccess(nl);
			} else {
				mCallbackInterface.onFailure(mError);
			}
		} catch (Exception e) {
			mCallbackInterface.onFailure(mError);
		}
	}
}
