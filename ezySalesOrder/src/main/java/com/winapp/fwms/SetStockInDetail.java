package com.winapp.fwms;

import java.io.IOException;
import java.text.NumberFormat;
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
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import com.winapp.sot.Company;

public class SetStockInDetail {
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";

	static String result;
	static int stockId;
	static String stcode = null;
	static String procode;
	static String proavg;
	static HashMap<String, String> producthm;
	static HashMap<String, String> lochm = new HashMap<String, String>();
	static ArrayList<HashMap<String, String>> procost = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> loc = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> cmpyArr = new ArrayList<HashMap<String, String>>();
	static String avg;
	static String validURL;
	static String savestock;
	static MySQLiteDataBase sqldb;
	static double subnet = 0.0;
	static NumberFormat nf;
	static String detailsubnet = "";

	public SetStockInDetail(String url) {
		validURL = url;
	}

	public static String getAvgCost(String webMethNameGet, String productcode)
			throws JSONException {

		String resTxt = null;
		PropertyInfo moduleName = new PropertyInfo();

		// Create request
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		
		PropertyInfo companyCode = new PropertyInfo();
		
		String cmpnyCode=SupplierSetterGetter.getCompanyCode();
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		
		moduleName.setName("ProductCode");
		moduleName.setValue(productcode);
		moduleName.setType(String.class);
		request.addProperty(moduleName);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			result = " { AvgCost : " + resTxt + "}";

			JSONObject jsonResponse;
			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("AvgCost");
				
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					avg = jsonChildNode.optString("AverageCost").toString();

					Log.d("avgcost", avg);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return avg;
	}

	public static ArrayList<HashMap<String, String>> getProductAvgCost(
			String webMethNameGet) throws JSONException {

		String resTxt = null;
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		PropertyInfo companyCode = new PropertyInfo();
	
		String cmpnyCode=SupplierSetterGetter.getCompanyCode();		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11); 
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request); 
			envelope.bodyOut = request;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();	
			result = " { productCode : " + resTxt + "}";
	
			JSONObject jsonResponse;
			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("productCode");
				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					/******* Fetch node values **********/
					procode = jsonChildNode.optString("ProductCode").toString();
					proavg = jsonChildNode.optString("AverageCost").toString();
					producthm = new HashMap<String, String>();
					producthm.put(procode, proavg);
					procost.add(producthm);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return procost;
	}
	
	public static ArrayList<HashMap<String, String>> getCompany(String webMethName)
			throws JSONException {
		String resTxt = null;
		cmpyArr.clear();
		try {
		
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			result = " { company: " + resTxt + "}";
			
//		 Log.d("Result-->", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("company");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					/******* Fetch node values **********/
					
					HashMap<String, String> hm = new HashMap<String, String>();

					String companycode = jsonChildNode.optString(
							"CompanyCode").toString();
					String companyname = jsonChildNode.optString(
							"CompanyName").toString();
					String taxCode =jsonChildNode.optString
							("TaxCode").toString();
					String shortCode = jsonChildNode.optString("ShortCode").toString();


					Company.setTaxcode(taxCode);
					Company.setShortCode(shortCode);

					hm.put("CompanyCode", companycode);
					hm.put("CompanyName", companyname);
					hm.put("TaxCode",taxCode);

					Log.d("TaxCodeINFO","-->"+taxCode);

					cmpyArr.add(hm);
					

				}
//				Log.d("cmpyArr-sev>", ""+cmpyArr.size());

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return cmpyArr;
	}
	
	/** company code & name end **/

	public static HashMap<String, String> getLocationcode(String webMethName, String CompanyCode)
			throws JSONException {
		lochm.clear();
		String resTxt = null;
		try {
		
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
		
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			result = " { Location : " + resTxt + "}";
	
			JSONObject jsonResponse;

			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Location");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String locationcode = jsonChildNode.optString(
							"LocationCode").toString();
					String locationname = jsonChildNode.optString(
							"LocationName").toString();
					lochm.put(locationname, locationcode);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return lochm;
	}

	public static String setStockIn(String webMethName, String location,
			String supcodes, String remarks, String date, String username,
			ArrayList<String> pc, ArrayList<String> pn, ArrayList<String> pb, ArrayList<String> pw,
			ArrayList<String> dpc, ArrayList<String> dpn, ArrayList<Double> dpqty, ArrayList<String> palecode,
			ArrayList<Double> price, ArrayList<String> averagecost,
			ArrayList<Double> qty, String totalsuppweight,
			ArrayList<String> countcarton, ArrayList<String> allsno, String subnettotal)
			throws IOException, XmlPullParserException {
		// TODO Auto-generated method stub

		String jsonSetStockInDetail = "";
		String jsonSetStockInCartonDetail = "";
//		String jsonSetProductStock = "";

		for (int i = 0; i < pc.size(); i++) {
			int no = 1;
			int sno = no + i;
			jsonSetStockInCartonDetail += "{\"RefNo\":\"" + "\",\"SlNo\":"
					+ sno + ",\"PalletCode\":\"" + palecode.get(i)
					+ "\",\"ProductCode\":\"" + pc.get(i)
					+ "\",\"WeightBarcode\":\"" + pb.get(i) + "\",\"Weight\":"
					+ pw.get(i) + ",\"CreateUser\":\"" + username
					+ "\",\"CreateDate\":\"" + date + "\",\"ModifyUser\":\""
					+ username + "\",\"ModifyDate\":\"" + date
					+ "\",\"Pid\":1,\"ErrorMessage\":null}^";

		}

		for (int i = 0; i < dpc.size(); i++) {
			int no = 1;
			int sno = no + i;
			nf = NumberFormat.getInstance();

			subnet = subnet + (qty.get(i) * price.get(i));
			nf.setMaximumFractionDigits(2);
			String subnetostr = nf.format(subnet);
			detailsubnet = subnetostr.replace(",", "");

			Double headerSubNet = (qty.get(i) * price.get(i));
			nf.setMaximumFractionDigits(2);
			String det = nf.format(headerSubNet);
			String dtlsbnt = det.replace(",", "");

			jsonSetStockInDetail += "{\"RefNo\":\"" + "\",\"SlNo\":" + sno
					+ ",\"ProductCode\":\"" + dpc.get(i)
					+ "\",\"ProductName\":\"" + dpn.get(i) + "\",\"Qty\":"
					+ qty.get(i) + ",\"Price\":" + price.get(i)
					+ ",\"SubTotal\":" + dtlsbnt + ",\"Tax\":0,\"NetTotal\":"
					+ dtlsbnt + ",\"CreateUser\":\"" + username
					+ "\",\"CreateDate\":\"" + date + "\",\"ModifyUser\":\""
					+ username + "\",\"ModifyDate\":\"" + date
					+ "\",\"ErrorMessage\":null}^";

		}
		String jsonStockinHeader = "{\"RefNo\":\""

		+ "\",\"RefDate\":\"" + date + "\",\"LocationCode\":\"" + location
				+ "\",\"SupplierCode\":\"" + supcodes + "\",\"Remarks\":\""
				+ remarks + "\",\"SubTotal\":" + detailsubnet
				+ ",\"Tax\":0,\"NetTotal\":" + detailsubnet
				+ ",\"PaidAmount\":0,\"BalanceAmount\":0,\"CreateUser\":\""
				+ username + "\",\"CreateDate\":\"" + date
				+ "\",\"ModifyUser\":\"" + username + "\",\"ModifyDate\":\""
				+ date + "\",\"DebitAmount\":null,\"ErrorMessage\":null}";

		String strjsonSetStockInDetail = jsonSetStockInDetail.substring(0,
				jsonSetStockInDetail.length() - 1);

		String strjsonSetStockInCartonDetail = jsonSetStockInCartonDetail
				.substring(0, jsonSetStockInCartonDetail.length() - 1);

		Log.d("Locationcode", location);
		Log.d("Product jsonStockinHeader", jsonStockinHeader);
		Log.d("Product jsonSetStockInDetail", strjsonSetStockInDetail);
		Log.d("Product jsonSetStockInCartonDetail",
				strjsonSetStockInCartonDetail);
	
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		PropertyInfo jsonRequestSH = new PropertyInfo();
		PropertyInfo jsonRequestSD = new PropertyInfo();
		PropertyInfo jsonRequestSCD = new PropertyInfo();
		PropertyInfo jsonRequestSPS = new PropertyInfo();

		PropertyInfo companyCode = new PropertyInfo();
		
		String cmpnyCode=SupplierSetterGetter.getCompanyCode();
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		jsonRequestSPS.setName("LocationCode");
		jsonRequestSPS.setValue(location);
		jsonRequestSPS.setType(String.class);
		request.addProperty(jsonRequestSPS);

		jsonRequestSH.setName("Header");
		jsonRequestSH.setValue(jsonStockinHeader);
		jsonRequestSH.setType(String.class);
		request.addProperty(jsonRequestSH);

		jsonRequestSD.setName("Detail");
		jsonRequestSD.setValue(strjsonSetStockInDetail);
		jsonRequestSD.setType(String.class);
		request.addProperty(jsonRequestSD);

		jsonRequestSCD.setName("CartonDetail");
		jsonRequestSCD.setValue(strjsonSetStockInCartonDetail);
		jsonRequestSCD.setType(String.class);
		request.addProperty(jsonRequestSCD);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11); 
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request); 
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		
		String resTxt = response.toString();

		Log.d("AddSetStockIn", resTxt);

		resTxt = " { SaveStockIn : " + resTxt + "}";
		Log.d("SaveStockIn", resTxt);

		JSONObject jsonResponse;

		try {

			jsonResponse = new JSONObject(resTxt);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("SaveStockIn");

			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

				savestock = jsonChildNode.optString("Result").toString();
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return savestock;
	}
}
