package com.winapp.fwms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class SearchStockInWebService {
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";

	static ArrayList<HashMap<String, String>> searchArr = new ArrayList<HashMap<String, String>>();
	static String value, result;

	static String validURL;

	public SearchStockInWebService(String url) {
		validURL = url;
	}

	public static ArrayList<HashMap<String, String>> SearchStockInService(
			String suppCode, String sdate, String edate, String webMethNameSet) {

		searchArr.clear();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo supCode = new PropertyInfo();
		PropertyInfo sDate = new PropertyInfo();
		PropertyInfo eDate = new PropertyInfo();

		PropertyInfo companyCode = new PropertyInfo();
		
		String cmpnyCode=SupplierSetterGetter.getCompanyCode();
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		
		supCode.setName("SupplierCode");
		supCode.setValue(suppCode);
		supCode.setType(String.class);
		request.addProperty(supCode);

		sDate.setName("FromDate");
		sDate.setValue(sdate);
		sDate.setType(String.class);
		request.addProperty(sDate);

		eDate.setName("ToDate");
		eDate.setValue(edate);
		eDate.setType(String.class);
		request.addProperty(eDate);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			value = response.toString();
			Log.d("Search Stock In Value", value);
			result = " { Search : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					/******* Fetch node values **********/
					String refNo = jsonChildNode.optString("RefNo").toString();

					String refDate = jsonChildNode.optString("RefDate")
							.toString();

					String supplierCode = jsonChildNode.optString(
							"SupplierCode").toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("RefNo", refNo);
					if (refDate != null) {
//						DateFormat f = new SimpleDateFormat(
//								"MM/dd/yyyy hh:mm:ss a");
						StringTokenizer tokens = new StringTokenizer(refDate, " ");
					    String date = tokens.nextToken();
						hm.put("RefDate", date);
					} else {
						hm.put("RefDate", refDate);
					}
					hm.put("SupplierCode", supplierCode);

					searchArr.add(hm);
					Log.d("Search Stock In Result", searchArr.toString());
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return searchArr;
	}

}
