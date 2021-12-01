package com.winapp.sot;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.util.Log;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.util.ErrorLog;

public class WebServiceClass {
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static int TimeOut = 20000;
	static String vld_rslt = "", header_json = "", detail_json = "",sales_return_json="";
	static String resTxt = "";
	static String result = "";
	static double dbCreditAmount = 0.0;
	static double dbPaidAmount = 0.0;
	static String URL, deviceId;
	static ErrorLog errorLog;
	public WebServiceClass(String url) {
		URL = url;
		deviceId = RowItem.getDeviceID();
		errorLog = new ErrorLog();
		Log.d("device id", "dev "+ deviceId);
	}

	public static String URLService(String webMethNameGet) {

		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		PropertyInfo companyCode = new PropertyInfo();
//		PropertyInfo outstandingAmount =new PropertyInfo();
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		Log.d("company code GS", "gen " + cmpnyCode);

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

//		outstandingAmount.setName("NeedOutstandingAmount");
//		outstandingAmount.setValue("1");
//		outstandingAmount.setType(String.class);
//		request.addProperty(outstandingAmount);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport = new HttpTransportSE(URL, TimeOut);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			result = " { JsonArray: " + resTxt + "}";
			Log.d("Online Result", result);

		} catch (Exception e) {
			e.printStackTrace();
			vld_rslt = "Error";
			Log.d("result 2:", vld_rslt);
		}
		return result;
	}

	public static String URLServices(String webMethNameGet) {

		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo locationCode =new PropertyInfo();
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		String locationcode = SalesOrderSetGet.getLocationcode();

		Log.d("companyGS", "gen " + cmpnyCode+",,,"+locationcode);

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		locationCode.setName("LocationCode");
		locationCode.setValue(locationcode);
		locationCode.setType(String.class);
		request.addProperty(locationCode);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport = new HttpTransportSE(URL, TimeOut);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			result = " { JsonArray: " + resTxt + "}";
			Log.d("Online Result", result);

		} catch (Exception e) {
			e.printStackTrace();
			vld_rslt = "Error";
			Log.d("result 2:", vld_rslt);
		}
		return result;
	}


	public static String URLServices(String webMethNameGet, String proCode) {

		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo productCode =new PropertyInfo();
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		Log.d("companycodeGS", "gen " + cmpnyCode +"  "+proCode);

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		productCode.setName("ProductCode");
		productCode.setValue(proCode);
		productCode.setType(String.class);
		request.addProperty(productCode);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport = new HttpTransportSE(URL, TimeOut);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			result = " { JsonArray: " + resTxt + "}";
			Log.d("Online Result", result);

		} catch (Exception e) {
			e.printStackTrace();
			vld_rslt = "Error";
			Log.d("result 2:", vld_rslt);
		}
		return result;
	}

	public static String parameterService(HashMap<String, String> hashValue,
			String webMethNameGet) {
		resTxt="";
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

		for (HashMap.Entry<String, String> entry : hashValue.entrySet()) {

			Log.d("getValue()",entry.getValue()+","+entry.getKey());

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);

			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		envelope.bodyOut = request;

		Log.e("Delete Request", request.toString());

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("resTxt",resTxt);
			result = " { JsonArray: " + resTxt + "}";
			Log.d("OnlineCustomerResult", result);

		} catch (Exception e) {
			e.printStackTrace();
			vld_rslt = "Error";
			Log.d("result 2:", vld_rslt);
			result="";
		}

		return result;
	}

	public static String AddInvoiceService(
			ArrayList<HashMap<String, String>> SaveArray,
			String webMethNameGet, String serverdate,ArrayList<HashMap<String, String>> salesReturnArray) {

		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

		Log.d("Save Result", "Save Result.......");
		detail_json = "";
		dbCreditAmount = 0.0;
		dbPaidAmount = 0.0;
		sales_return_json ="";

		for (int i = 0; i < SaveArray.size(); i++) {
			String s1 = SaveArray.get(i).get("Invoice");
			String s2 = SaveArray.get(i).get("NetValue");
			String s3 = SaveArray.get(i).get("PaidValue");
			String s4 = SaveArray.get(i).get("CreditValue");

			dbCreditAmount = dbCreditAmount + Double.parseDouble(s4);
			dbPaidAmount = dbPaidAmount + Double.parseDouble(s3);

			detail_json += s1 + "^" + s2 + "^" + s3 + "^" + s4 + "!";


		}

		dbCreditAmount = Math.round(dbCreditAmount * 100.0) / 100.0;
		dbPaidAmount = Math.round(dbPaidAmount * 100.0) / 100.0;

		detail_json = detail_json.substring(0, detail_json.length() - 1);

		Log.d("Detail", detail_json);

		Log.d("salesReturnArray","ss "+salesReturnArray.size());

		if(salesReturnArray.size()>0) {
			for (int i = 0; i < salesReturnArray.size(); i++) {
				String s1 = salesReturnArray.get(i).get("SalesReturnNo");
				String s2 = salesReturnArray.get(i).get("Total");
				String s3 = salesReturnArray.get(i).get("ReturnType");

				sales_return_json += s1 + "^" + s2 + "^" + s3 + "!";

				Log.d("Sales Return Detail", sales_return_json);
			}
			sales_return_json = sales_return_json.substring(0, sales_return_json.length() - 1);
		}
		String payment = "", bank_Code = "", check_No = "", cust_Date = "";
		String cust_Code = In_Cash.getCust_Code();
		String pay_Mode = In_Cash.getPay_Mode();
		String locattionCode =SalesOrderSetGet.getLocationcode();

		Log.d("locattionCode","-->"+locattionCode);

		if (pay_Mode.toLowerCase().matches("cheque")) {
			payment = pay_Mode;
			bank_Code = In_Cash.getBank_code();
			check_No = In_Cash.getCheck_No();
			cust_Date = In_Cash.getCheck_Date();

		} else{// if (pay_Mode.matches("Cash")) {
			payment = pay_Mode;
			bank_Code = "";
			check_No = "";
			cust_Date = "01/01/1900";

		}
		Log.d("Payment Mode", "" + pay_Mode);
		// header_json
		// ="^25/08/2014^25/08/2014^6565^67^2889.92^30.0^100.0^130.0^2789.92^0^2789.92^^0^SGD^1.000000000^112233^11";
		String user = SupplierSetterGetter.getUsername();

		if (cust_Date.matches("")) {
			cust_Date = serverdate;
		}
		deviceId = RowItem.getDeviceID();
		
		header_json = "^" + serverdate + "^" + cust_Code + "^" + dbPaidAmount
				+ "^" + dbCreditAmount + "^" + payment + "^" + bank_Code + "^"
				+ check_No + "^" + cust_Date + "^" + deviceId + "^" + user + "^" +locattionCode+ "^" +"M";

		Log.d("Header", header_json);

		PropertyInfo prodCode = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo HeaderprodCode = new PropertyInfo();
		PropertyInfo ReturnDetail = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		prodCode.setName("sDetail");
		prodCode.setValue(detail_json);
		prodCode.setType(String.class);
		request.addProperty(prodCode);

		HeaderprodCode.setName("sHeader");
		HeaderprodCode.setValue(header_json);
		HeaderprodCode.setType(String.class);
		request.addProperty(HeaderprodCode);

		ReturnDetail.setName("sReturnDetail");
		ReturnDetail.setValue(sales_return_json);
		ReturnDetail.setType(String.class);
		request.addProperty(ReturnDetail);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport = new HttpTransportSE(URL, TimeOut);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			resTxt = response.toString();

			result = " { JsonArray: " + resTxt + "}";
			Log.d("Result", result);

		} catch (Exception e) {
			e.printStackTrace();
			vld_rslt = "Error";
			Log.d("result 2:", vld_rslt);
		}
		return result;
	}

	public static String SaveCustomerService(HashMap<String, String> SaveArray,
			String webMethNameGet) {

		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

		for (HashMap.Entry<String, String> entry : SaveArray.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);

			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport = new HttpTransportSE(URL, TimeOut);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			resTxt = response.toString();

			result = " { saveArray: " + resTxt + "}";
			Log.d("Result", resTxt);

		} catch (Exception e) {
			e.printStackTrace();
			vld_rslt = "Error";
			Log.d("result 2:", vld_rslt);
		}
		return result;
	}
	
	public static String SaveCustomerOffline(HashMap<String, String> SaveArray,
			String webMethNameGet) {

		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

		for (HashMap.Entry<String, String> entry : SaveArray.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);

			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport = new HttpTransportSE(URL, TimeOut);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			resTxt = response.toString();

			result = " { saveArray: " + resTxt + "}";
			Log.d("Result", resTxt);

		} catch (Exception e) {
			e.printStackTrace();
			vld_rslt = "Error";
			Log.d("result 2:", vld_rslt);
		}
		return result;
	}
	
	public static JSONArray parameterWebservice(HashMap<String, String> param, String webMethNameGet) {
		JSONArray jsonArray=null;
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

		for (HashMap.Entry<String, String> entry : param.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();
			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);

			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			jsonArray = new JSONArray(response.toString());
			Log.d("Online result test log", jsonArray.toString());

		} catch (Exception e) {
			e.printStackTrace();
			errorLog.write("Method Name : " + webMethNameGet +" , "+"Error : " + e.getMessage());
		}
		
		return jsonArray;
	}
	
}
