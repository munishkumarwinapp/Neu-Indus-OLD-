package com.winapp.fwms;

import java.util.HashSet;
import java.util.Set;

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

public class LocationWebService {

	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	static String result;
	static String OutputData = "";
	static Set<String> st = new HashSet<String>();

	static String validURL;

	public LocationWebService(String url) {
		validURL = url;
	}

	public static Set<String> soapwebservice(String webMethName, String CompanyCode) {
		
		String resTxt = null;
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		PropertyInfo jsonRequest = new PropertyInfo();

		jsonRequest.setName("CompanyCode");
		jsonRequest.setValue(CompanyCode);
		jsonRequest.setType(String.class);
		request.addProperty(jsonRequest);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		try {

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			result = " { Location : " + resTxt + "}";
			Log.d("Result", result);

			JSONObject jsonResponse;

			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Location");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String locationname = jsonChildNode.optString(
							"LocationName").toString();
					st.add(locationname);
					System.out.print("name" + st);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return st;
	}
}
