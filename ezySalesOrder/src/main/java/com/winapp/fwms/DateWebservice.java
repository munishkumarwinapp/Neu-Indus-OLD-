package com.winapp.fwms;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.winapp.offline.OfflineSettingsManager;

import android.content.Context;
import android.util.Log;

public class DateWebservice {
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";

	static String value, result;
	static String serverDate;
	static String validURL,cmpyCode="";
	private OfflineSettingsManager spManager;
	
	public DateWebservice(String url,Context context) {
		spManager = new OfflineSettingsManager(context);
		cmpyCode = spManager.getCompanyType();
		validURL = url;
		
	}

	public static String getDateService(String webMethNameSet) {

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

//		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		
		PropertyInfo companyCode = new PropertyInfo();
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

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

			result = " { Date : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse.optJSONArray("Date");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					serverDate = jsonChildNode.optString("ServerDate")
							.toString();

					Log.d("ServerDate", serverDate);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return serverDate;
	}

}
