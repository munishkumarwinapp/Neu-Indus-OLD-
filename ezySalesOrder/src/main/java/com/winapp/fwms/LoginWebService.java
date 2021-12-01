package com.winapp.fwms;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.winapp.sot.SalesOrderSetGet;

import android.util.Log;

public class LoginWebService {
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	static String value;
	static String result;
	static String validURL;
	static ArrayList<String> loginArr = new ArrayList<String>();
	public LoginWebService(String url) {
		validURL = url;
	}

	public static ArrayList<String> loginWS(String username, String password,
			String webMethName) {
		loginArr.clear();
		String dialogStatus, mobileLoginPage="";
		
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		PropertyInfo unamePI = new PropertyInfo();
		PropertyInfo passPI = new PropertyInfo();

		unamePI.setName("sUserID");
		unamePI.setValue(username);
		unamePI.setType(String.class);
		request.addProperty(unamePI);
		
		passPI.setName("sPassword");
		passPI.setValue(password);
		passPI.setType(String.class);
		request.addProperty(passPI);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);
		
		try {
		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			value = response.toString();
			Log.d("Login", value);

			result = " { Login : " + value + "}";

			JSONObject jsonResponse;

			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("Login");

			/*********** Process each JSON Node ************/
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				String Result = jsonChildNode.optString("Result").toString();
				String userGroup = jsonChildNode.optString("UserGroup").toString();
				String isActive = jsonChildNode.optString("isActive").toString();
			    String PhoneNo = jsonChildNode.optString("PhoneNo").toString();
				String isShowCost =jsonChildNode.optString("IsShowCost").toString();
				
				loginArr.add(Result);
				loginArr.add(userGroup);
				loginArr.add(isActive);
				mobileLoginPage = SalesOrderSetGet.getMobileloginpage();
			    if(mobileLoginPage.matches("M")){
			    String CompanyCode = jsonChildNode.optString("CompanyCode").toString();
			    String CompanyName = jsonChildNode.optString("CompanyName").toString();
			    String LastLoginLocation = jsonChildNode.optString("LastLoginLocation").toString();
			    String LocationName = jsonChildNode.optString("LocationName").toString();
			    
			    loginArr.add(CompanyCode);
			    loginArr.add(CompanyName);
			    loginArr.add(LastLoginLocation);
			    loginArr.add(LocationName);
			    }
			    loginArr.add(PhoneNo);
				SalesOrderSetGet.setIsShowCost(isShowCost);
				Log.d(Result, isActive);
			}

		} catch (SocketTimeoutException e) {
			
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	
		return loginArr;
	}
	
	public static String lastLoginLocationWS(String webMethName, String username, String locationcode) {
		  String mobileLoginstr="";
		  
		  SoapObject request = new SoapObject(NAMESPACE, webMethName);
		  PropertyInfo companyPI = new PropertyInfo();
		  PropertyInfo unamePI = new PropertyInfo();
		  PropertyInfo locationPI = new PropertyInfo();
		  
		  String companyCode = SupplierSetterGetter.getCompanyCode();

		  companyPI.setName("CompanyCode");
		  companyPI.setValue(companyCode);
		  companyPI.setType(String.class);
		  request.addProperty(companyPI);
		  
		  unamePI.setName("UserName");
		  unamePI.setValue(username);
		  unamePI.setType(String.class);
		  request.addProperty(unamePI);
		  
		  locationPI.setName("LastLoginLocation");
		  locationPI.setValue(locationcode);
		  locationPI.setType(String.class);
		  request.addProperty(locationPI);
		  
		  SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
		    SoapEnvelope.VER11);
		  envelope.setOutputSoapObject(request);
		  envelope.dotNet = true;
		  
		  try {
		  HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		   androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		   SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		   value = response.toString();
		   

		   result = " { LastLoginLocation : " + value + "}";
		   Log.d("LastLoginLocation ", "--> "+result.toString());

		   JSONObject jsonResponse;

		   jsonResponse = new JSONObject(result);

		   JSONArray jsonMainNode = jsonResponse.optJSONArray("LastLoginLocation");

		  
		   int lengthJsonArr = jsonMainNode.length();
		   for (int i = 0; i < lengthJsonArr; i++) {
		   
		    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
		    mobileLoginstr = jsonChildNode.optString("Result").toString();
		    Log.d("Result", "--> "+mobileLoginstr);
		   }

		  } catch (Exception e) {
			  mobileLoginstr="";
		   e.printStackTrace();
		  }
		 
		  return mobileLoginstr;
		 }

}
