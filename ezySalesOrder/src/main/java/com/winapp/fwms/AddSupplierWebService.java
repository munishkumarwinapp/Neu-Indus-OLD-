package com.winapp.fwms;

import java.util.ArrayList;
import java.util.HashMap;

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

public class AddSupplierWebService {
	private static String NAMESPACE = "http://tempuri.org/";
	// private static String URL =
	// "http://winappazure.cloudapp.net:8080/FWMS_API/FrozenAgent.asmx";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static final String SUPPLIER_CODE = "SupplierCode";
	private static final String SUPPLIER_NAME = "SupplierName";
	static ArrayList<HashMap<String, String>> supplierList = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> supplierName = new ArrayList<HashMap<String, String>>();
	static String supresult;

	static String validURL;

	public AddSupplierWebService(String url) {
		validURL = url;
	}

	public static ArrayList<HashMap<String, String>> soapwebservice(String URL,
			String webMethName) {

		String suppTxt = null;
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		
		PropertyInfo companyCode = new PropertyInfo();
		
		String cmpnyCode=SupplierSetterGetter.getCompanyCode();
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		try {
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			suppTxt = response.toString();
			supresult = " { SupplierDetails : " + suppTxt + "}";

			JSONObject jsonResponse;

			try {

				jsonResponse = new JSONObject(supresult);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SupplierDetails");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					Log.d("jsonobject", jsonChildNode.getString(SUPPLIER_CODE));
					Log.d("jsonname", jsonChildNode.getString(SUPPLIER_NAME));

					String suppliercode = jsonChildNode.optString(
							"SupplierCode").toString();
					String suppliername = jsonChildNode.optString(
							"SupplierName").toString();

					HashMap<String, String> supplierhm = new HashMap<String, String>();
					supplierhm.put(suppliercode, suppliername);
					// supplierhm.put("SupplierName",suppliername);
					supplierList.add(supplierhm);
					Log.d("SupplierName", supplierhm.toString());
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			suppTxt = "Error occured";
		}
		return supplierList;

	}
}
