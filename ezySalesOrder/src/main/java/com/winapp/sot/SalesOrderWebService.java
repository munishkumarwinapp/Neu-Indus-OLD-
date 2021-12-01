package com.winapp.sot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
import org.xmlpull.v1.XmlPullParserException;

import android.database.Cursor;
import android.util.Log;

import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.CurrencyDeno;
import com.winapp.sotdetails.CustomerSetterGetter;

public class SalesOrderWebService {
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	static ArrayList<HashMap<String, String>> customerList = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> userArr = new ArrayList<HashMap<String, String>>();
	static String custresult;
	static String vld_rslt = null, SettingID = "", SettingValue = "",
			gnrlStngs = "";
	static String validURL;
	static String resTxt = null;
	static ArrayList<String> crrncyVal = new ArrayList<String>();
	static ArrayList<HashMap<String, String>> crrncyAllVal = new ArrayList<HashMap<String, String>>();

	static ArrayList<String> cstmrgrpcdal = new ArrayList<String>();
	static ArrayList<HashMap<String, String>> SODetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> SOHeaderArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> DODetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> DOHeaderArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> INV_DetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> BarcodeArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> INV_HeaderArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> areaList = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> vanList = new ArrayList<HashMap<String, String>>();

	static ArrayList<HashMap<String, String>> graBatchArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> stockConsignmentArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> stockProductConsignmentArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> stockreqDetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> stockreqHeadersArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> stockreqBatchDetailArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> Gra_DetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> Gra_HeaderArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> EODetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> EOHeaderArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<String> customerall_Arr = new ArrayList<String>();

	static ArrayList<SO> receiptsearchlist = new ArrayList<SO>();
	static ArrayList<SO> stAdjsearchlist = new ArrayList<SO>();
	static String result = "";
	static ArrayList<SO> sosearchlist = new ArrayList<SO>();
	static ArrayList<SO> insearchlist = new ArrayList<SO>();
	static ArrayList<SO> dosearchlist = new ArrayList<SO>();
	static ArrayList<SO> grasearchlist = new ArrayList<SO>();
	static ArrayList<SO> stockreqsearchlist = new ArrayList<SO>();
	static ArrayList<SO> transfersearchlist = new ArrayList<SO>();
	static ArrayList<HashMap<String, String>> customerGroupCodeArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> SRDetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> SRHeaderArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<CurrencyDeno> settlementList = new ArrayList<CurrencyDeno>();

	static List<HashMap<String,String>> invoiceExceedTernsArr = new ArrayList<HashMap<String,String>>();
	static List<HashMap<String,String>> invoiceCreditLimitArr = new ArrayList<HashMap<String,String>>();

	public SalesOrderWebService(String url) {
		Log.d("ValidurlCheck","-->"+url);
		validURL = url;
	}

	public static ArrayList<HashMap<String, String>> getWholeCustomer(
			HashMap<String, String> hm, String webMethName) {
		customerList.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		for (HashMap.Entry<String, String> entry : hm.entrySet()) {

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
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		try {
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			custresult = " { SaleOCustomer : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(custresult);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SaleOCustomer");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String customercode = jsonChildNode.optString(
							"CustomerCode").toString();
					String customername = jsonChildNode.optString(
							"CustomerName").toString();

					String ReferenceLocation = jsonChildNode.optString(
							"ReferenceLocation").toString();

					Log.d("ReferenceLocation", "" + ReferenceLocation);

					HashMap<String, String> customerhm = new HashMap<String, String>();
					if (ReferenceLocation != null
							&& !ReferenceLocation.isEmpty()) {
						customerhm.put(customercode, customername + "/"
								+ ReferenceLocation);
					} else {
						customerhm.put(customercode, customername);
					}

					// HashMap<String, String> customerhm = new HashMap<String,
					// String>();
					// customerhm.put(customercode, customername);
					customerList.add(customerhm);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return customerList;

	}

	public static ArrayList<HashMap<String, String>> getAllUser(
			String webMethName, HashMap<String, String> param) {
		userArr.clear();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		for (HashMap.Entry<String, String> entry : param.entrySet()) {

			PropertyInfo Code = new PropertyInfo();

			Code.setName(entry.getKey());
			Code.setValue(entry.getValue());
			Code.setType(String.class);
			request.addProperty(Code);

			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}

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
			custresult = " { getuser : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(custresult);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("getuser");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String username = jsonChildNode.optString("UserName")
							.toString();
					String UserGroupCode = jsonChildNode.optString(
							"UserGroupCode").toString();

					HashMap<String, String> customerhm = new HashMap<String, String>();
					customerhm.put(UserGroupCode, username);
					userArr.add(customerhm);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return userArr;

	}

	public static ArrayList<HashMap<String, String>> getAllArea(
			String webMethName) {
		areaList.clear();
		custresult = "";
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

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
			resTxt = response.toString();
			custresult = " { area : " + resTxt + "}";
			Log.d("area list", "" + custresult);
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(custresult);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("area");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String area_Code = jsonChildNode.optString("Code")
							.toString();
					String area_Description = jsonChildNode.optString(
							"Description").toString();

					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put(area_Code, area_Description);
					areaList.add(hm);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return areaList;

	}

	public static ArrayList<HashMap<String, String>> getAllVan(
			String webMethName) {
		vanList.clear();
		custresult = "";
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

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
			resTxt = response.toString();
			custresult = " { van : " + resTxt + "}";
			Log.d("van list", "" + custresult);
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(custresult);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("van");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String Code = jsonChildNode.optString("Code").toString();
					String Description = jsonChildNode.optString("Description")
							.toString();

					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put(Code, Description);
					vanList.add(hm);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return vanList;

	}

	public static ArrayList<HashMap<String, String>> grasuppwebservice(
			String webMethName) {
		customerList.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

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
			resTxt = response.toString();
			custresult = " { GraSupplierr : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(custresult);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("GraSupplierr");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String customercode = jsonChildNode.optString(
							"SupplierCode").toString();
					String customername = jsonChildNode.optString(
							"SupplierName").toString();

					HashMap<String, String> customerhm = new HashMap<String, String>();
					customerhm.put(customercode, customername);
					customerList.add(customerhm);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return customerList;

	}

	public static String generalSettingsService(String webMethNameGet)
			throws JSONException, IOException, XmlPullParserException {

		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		resTxt = response.toString();
		String result = " { GeneralSettings : " + resTxt + "}";
		JSONObject jsonResponse;
		jsonResponse = new JSONObject(result);
		JSONArray jsonMainNode = jsonResponse.optJSONArray("GeneralSettings");
		int lengthJsonArr = jsonMainNode.length();
		for (int i = 0; i < lengthJsonArr; i++) {
			JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
			SettingID = jsonChildNode.optString("SettingID").toString();
			if (SettingID.matches("LOCALCURRENCY")) {
				SettingValue = jsonChildNode.optString("SettingValue")
						.toString();


			}


		}
		return SettingValue;
	}

	public static ArrayList<String> getCurrencyValues(String webMethNameGet,
			String currencycode) throws JSONException {
		PropertyInfo moduleName = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		moduleName.setName("CurrencyCode");
		moduleName.setValue(currencycode);
		moduleName.setType(String.class);
		request.addProperty(moduleName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

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
			String result = " { LocalCurrency : " + resTxt + "}";
			JSONObject jsonResponse;
			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("LocalCurrency");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String currencyName = jsonChildNode.optString(
							"CurrencyName").toString();
					String currencyRate = jsonChildNode.optString(
							"CurrencyRate").toString();
					crrncyVal.add(currencyName);
					crrncyVal.add(currencyRate);

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return crrncyVal;
	}


	public static ArrayList<HashMap<String, String>> getStockConsignmentDetailArr(
			String webMethNameGet, String customerCodes, String pCode) {
		int SlNo;
		Log.d("customerConsignmentArr","-->"+stockConsignmentArr.size());
		stockConsignmentArr.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo customerCode =new PropertyInfo();
		PropertyInfo productCode =new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		customerCode.setName("CustomerCode");
		customerCode.setValue(customerCodes);
		customerCode.setType(String.class);
		request.addProperty(customerCode);

		productCode.setName("ProductCode");
		productCode.setValue(pCode);
		productCode.setType(String.class);
		request.addProperty(productCode);

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
			String result = " { LocalCurrency : " + resTxt + "}";
			JSONObject jsonResponse;
			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("LocalCurrency");
				int lengthJsonArr = jsonMainNode.length();

				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String consignmentNo =jsonChildNode.optString(
							"ConsignmentNo").toString();
					String consignmentDate =jsonChildNode.optString("ConsignmentDate").toString();
					String durationDays =jsonChildNode.optString("DurationInDays").toString();
					String Con_ExpiryDate =jsonChildNode.optString("ConsignmentExpiry").toString();
					String productName = jsonChildNode.optString(
							"ProductName").toString();
					String batchNo =jsonChildNode.optString("BatchNo").toString();
					String pcsPerCarton =jsonChildNode.optString(
							"PcsPerCarton").toString();
					String expiryDate =jsonChildNode.optString("ExpiryDate").toString();
					String mfgDate =jsonChildNode.optString("MfgDate").toString();
					String cQty =jsonChildNode.optString( "CQty").toString();
					String lQty = jsonChildNode.optString("LQty").toString();
					String qty = jsonChildNode.optString("Qty").toString();
					SlNo=jsonChildNode.optInt("slNo");

					Log.d("consignmentNo",""+consignmentNo);
					Log.d("consignmentDate",""+consignmentDate);
					Log.d("pcsPerCarton",""+pcsPerCarton);
					Log.d("cQty",""+cQty +" "+SlNo);
					String slNo =String.valueOf(SlNo);

					HashMap<String, String> currencyhm = new HashMap<String, String>();

					currencyhm.put("SlNo",slNo);
					currencyhm.put("consignmentNo",consignmentNo);
					currencyhm.put("consignmentDate",consignmentDate);
					currencyhm.put("durationDays",durationDays);
					currencyhm.put("Con_ExpiryDate",Con_ExpiryDate);
					currencyhm.put("productName",productName);
					currencyhm.put("batchNo",batchNo);
					currencyhm.put("pcsPerCarton",pcsPerCarton);
					currencyhm.put("expiryDate",expiryDate);
					currencyhm.put("mfgDate",mfgDate);
					currencyhm.put("cQty",cQty);
					currencyhm.put("lqty",lQty);
					currencyhm.put("qty",qty);
					stockConsignmentArr.add(currencyhm);

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return stockConsignmentArr;
	}


	public static ArrayList<HashMap<String, String>> getStockConsignmentArr(
			String webMethNameGet, String customerCodes, String sales_prodCode) {
		int SlNo;
		String c_Qty="",l_Qty="",Qty="";
		Log.d("customerCodes", "-->" + customerCodes+" "+sales_prodCode);
		stockProductConsignmentArr.clear();
			SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
			PropertyInfo companyCode = new PropertyInfo();
			PropertyInfo customerCode = new PropertyInfo();
			PropertyInfo prodCode = new PropertyInfo();

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			customerCode.setName("CustomerCode");
			customerCode.setValue(customerCodes);
			customerCode.setType(String.class);
			request.addProperty(customerCode);

			prodCode.setName("ProductCode");
			prodCode.setValue(sales_prodCode);
			prodCode.setType(String.class);
			request.addProperty(prodCode);

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
				String result = " { LocalCurrency : " + resTxt + "}";
				JSONObject jsonResponse;
				try {

					jsonResponse = new JSONObject(result);
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("LocalCurrency");
					int lengthJsonArr = jsonMainNode.length();

					Log.d("lengthJsonArr","-->"+lengthJsonArr);

					for (int i = 0; i < lengthJsonArr; i++) {
						JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
						String productCode = jsonChildNode.optString(
								"ProductCode").toString();
						String productName = jsonChildNode.optString(
								"ProductName").toString();
						String pcsPerCarton = jsonChildNode.optString(
								"PcsPerCarton").toString();
						String cQty = jsonChildNode.optString("CQty").toString();
						String lQty = jsonChildNode.optString("LQty").toString();
						String qty = jsonChildNode.optString("Qty").toString();
						String con_no = jsonChildNode.optString("ConsignmentNo").toString();
						String con_name = jsonChildNode.optString("ConsignmentDate").toString();
						String duration = jsonChildNode.optString("DurationInDays").toString();
						String con_expiry = jsonChildNode.optString("ConsignmentExpiry").toString();
						String cust_code = jsonChildNode.optString("CustomerCode").toString();
						String batchNo = jsonChildNode.optString("BatchNo").toString();
						String exp_date = jsonChildNode.optString("ExpiryDate").toString();
						String mfgDate = jsonChildNode.optString("MfgDate").toString();
						String uom = jsonChildNode.optString("UOMCode").toString();
						SlNo = jsonChildNode.optInt("slNo");

						Log.d("productCode", "" + productCode+"-->"+con_no+"con name -"+con_name);
						Log.d("productName", "" + productName);
						Log.d("pcsPerCarton", "" + pcsPerCarton);
						Log.d("cQtyInsert", "" + cQty + " " + SlNo + " " + lQty + " " + qty);
						String slNo = String.valueOf(SlNo);
						int sl_no =Integer.parseInt(slNo);
						HashMap<String, String> currencyhm = new HashMap<String, String>();
						Log.d("SalesorderCl","-->"+SalesOrderSetGet.getType()+productCode);

						if(SalesOrderSetGet.getType().isEmpty()){
						currencyhm.put("SlNo", slNo);
						currencyhm.put("productCode", productCode);
						currencyhm.put("productName", productName);
						currencyhm.put("pcsPerCarton", pcsPerCarton);
						currencyhm.put("cQty", cQty);
						currencyhm.put("lqty", lQty);
						currencyhm.put("qty", qty);
						currencyhm.put("con_no", con_no);
						currencyhm.put("con_name", con_name);
						currencyhm.put("duration", duration);
						currencyhm.put("con_expiry", con_expiry);
						currencyhm.put("cust_code", cust_code);
						currencyhm.put("exp_date", exp_date);
						currencyhm.put("mfgDate", mfgDate);
						currencyhm.put("uom", uom);
						currencyhm.put("AvailCQty", "0");
						currencyhm.put("AvailLQty","0");
						currencyhm.put("AvailQty", "0");
						currencyhm.put("BatchNo",batchNo);
						stockProductConsignmentArr.add(currencyhm);
						SalesOrderSetGet.setSl_no(sl_no);
						Log.d("customerCodes123", "-->" + stockProductConsignmentArr.size());
					}else{

						Cursor cursor = SOTDatabase.getConsignmentStock(cust_code,productCode);

//						c_Qty =cursor.getString(cursor
//								.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
//						l_Qty=cursor.getString(cursor
//								.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
//						Qty = cursor.getString(cursor
//								.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

//						Log.d("QuantityEntity","-->"+c_Qty+"lQ"+l_Qty+"qty"+Qty);
                       Log.d("CursorValue", "" + cQty + " " + SlNo + " " + lQty + " " + qty);

						currencyhm.put("SlNo", slNo);
						currencyhm.put("productCode", productCode);
						currencyhm.put("productName", productName);
						currencyhm.put("pcsPerCarton", pcsPerCarton);
						currencyhm.put("cQty", "0");
						currencyhm.put("lqty", "0");
						currencyhm.put("qty", "0");
						currencyhm.put("con_no", con_no);
						currencyhm.put("con_name", con_name);
						currencyhm.put("duration", duration);
						currencyhm.put("con_expiry", con_expiry);
						currencyhm.put("cust_code", cust_code);
						currencyhm.put("batchNo", batchNo);
						currencyhm.put("exp_date", exp_date);
						currencyhm.put("mfgDate", mfgDate);
						currencyhm.put("uom", uom);
						currencyhm.put("AvailCQty", cQty);
						currencyhm.put("AvailLQty",lQty);
						currencyhm.put("AvailQty",qty);
						currencyhm.put("BatchNo",batchNo);
						stockProductConsignmentArr.add(currencyhm);
						SalesOrderSetGet.setSl_no(sl_no);
						Log.d("customerCodes", "-->" + stockProductConsignmentArr.size());
					}



					}


				} catch (JSONException e) {

					e.printStackTrace();
				}

			} catch (Exception e) {

				e.printStackTrace();
			}



		return stockProductConsignmentArr;

	}

	public static ArrayList<HashMap<String, String>> getAllCurrency(
			String webMethNameGet) {
		crrncyAllVal.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

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
			String result = " { LocalCurrency : " + resTxt + "}";
			JSONObject jsonResponse;
			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("LocalCurrency");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String currencyCode = jsonChildNode.optString(
							"CurrencyCode").toString();
					String currencyName = jsonChildNode.optString(
							"CurrencyName").toString();

					HashMap<String, String> currencyhm = new HashMap<String, String>();

					currencyhm.put(currencyCode, currencyName);
					crrncyAllVal.add(currencyhm);

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return crrncyAllVal;
	}



	public static String getCrrncyRate(String currencycode,
			String webMethNameGet) throws JSONException {
		String currencyRate = "";
		PropertyInfo moduleName = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		moduleName.setName("CurrencyCode");
		moduleName.setValue(currencycode);
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
			String result = " { LocalCurrency : " + resTxt + "}";
			Log.d("currencyRateresult", result);
			JSONObject jsonResponse;
			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("LocalCurrency");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					currencyRate = jsonChildNode.optString("CurrencyRate")
							.toString();

				}
				Log.d("currencyRate", currencyRate);
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return currencyRate;
	}

	public static ArrayList<SO> SearchSOCustCode(String cuscode, String sdate,
			String edate, String status, String webMethNameSet) {

		sosearchlist.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo supCode = new PropertyInfo();
		PropertyInfo sDate = new PropertyInfo();
		PropertyInfo eDate = new PropertyInfo();

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		supCode.setName("CustomerCode");
		supCode.setValue(cuscode);
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
			resTxt = response.toString();
			Log.d("soheader", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					// int sno=1+i;
					String ccSno = jsonChildNode.optString("SoNo").toString();
					/*
					 * String ccDate = jsonChildNode.optString("SoDate")
					 * .toString();
					 */
					String ccDate = jsonChildNode.optString("DeliveryDate")
							.toString();
					String customerCode = jsonChildNode.optString(
							"CustomerCode").toString();
					String customerName = jsonChildNode.optString(
							"CustomerName").toString();
					String amount = jsonChildNode.optString("NetTotal")
							.toString();
					String statusValue = jsonChildNode.optString("Status")
							.toString();

					SO so = new SO();
					// Based on status it show individual values
					if (status.matches(statusValue)) {
						so.setSno(ccSno);
						so.setCustomerCode(customerCode);
						so.setCustomerName(customerName);
						so.setNettotal(amount);

						if (statusValue.matches("0")) {
							so.setStatus("open");
						} else if (statusValue.matches("2")) {
							so.setStatus("InProgress Invoice");
						} else if (statusValue.matches("1")) {
							so.setStatus("InProgress DO");
						} else if (statusValue.matches("3")) {
							so.setStatus("closed");
						}
						// else if(statusValue.matches("4")){
						// so.setStatus("deleted");
						// }
						else {
							so.setStatus("open");
						}

						if (ccDate != null) {
							StringTokenizer tokens = new StringTokenizer(
									ccDate, " ");
							String date = tokens.nextToken();
							so.setDate(date);
						} else {
							so.setDate(ccDate);
						}

						sosearchlist.add(so);
					}
					// Show all values
					else if (status.matches("5")) {
						so.setSno(ccSno);
						so.setCustomerCode(customerCode);
						so.setCustomerName(customerName);
						so.setNettotal(amount);
						// so.setStatus(statusValue);

						if (statusValue.matches("0")) {
							so.setStatus("open");
						} else if (statusValue.matches("2")) {
							so.setStatus("InProgress Invoice");
						} else if (statusValue.matches("1")) {
							so.setStatus("InProgress DO");
						} else if (statusValue.matches("3")) {
							so.setStatus("closed");
						}
						// else if(statusValue.matches("4")){
						// so.setStatus("deleted");
						// }

						if (ccDate != null) {
							StringTokenizer tokens = new StringTokenizer(
									ccDate, " ");
							String date = tokens.nextToken();
							so.setDate(date);
						} else {
							so.setDate(ccDate);
						}

						sosearchlist.add(so);
					}
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sosearchlist;
	}

	public static ArrayList<SO> SearchConsignment(String cuscode, String sdate,
												 String edate, String select_van,String locCode, String webMethNameSet,String tranType) {

		dosearchlist.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo supCode = new PropertyInfo();
		PropertyInfo sDate = new PropertyInfo();
		PropertyInfo eDate = new PropertyInfo();
		PropertyInfo VanCode = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo LocationCode = new PropertyInfo();
		PropertyInfo TranType = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		supCode.setName("CustomerCode");
		supCode.setValue(cuscode);
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

		VanCode.setName("VanCode");
		VanCode.setValue(select_van);
		VanCode.setType(String.class);
		request.addProperty(VanCode);

		LocationCode.setName("LocationCode");
		LocationCode.setValue(locCode);
		LocationCode.setType(String.class);
		request.addProperty(LocationCode);

		TranType.setName("TranType");
		TranType.setValue(tranType);
		TranType.setType(String.class);
		request.addProperty(TranType);
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("soheader", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String ccSno = jsonChildNode.optString("ConsignmentNo").toString();
					Log.d("ccsNo",ccSno);
					String ccDate = jsonChildNode.optString("ConsignmentDate")
							.toString();
					String customerCode = jsonChildNode.optString(
							"CustomerCode").toString();
					String customerName = jsonChildNode.optString(
							"CustomerName").toString();
					String amount = jsonChildNode.optString("NetTotal")
							.toString();
					String gotSignatureOnDo = jsonChildNode.optString("GotSignatureOnDO")
							.toString();
					String DelCustomerName = jsonChildNode.optString("DelCustomerName")
							.toString();

					SO so = new SO();
					so.setSno(ccSno);
					so.setSoNo("0");
					so.setCustomerCode(customerCode);
					so.setCustomerName(customerName);
					so.setNettotal(amount);
					so.setDelCustomerName(DelCustomerName);
					//so.setGotSignatureOnDO(gotSignatureOnDo);
					if (gotSignatureOnDo != null && !gotSignatureOnDo.isEmpty()) {
						so.setGotSignatureOnDO(gotSignatureOnDo);
					} else {
						so.setGotSignatureOnDO("False");
					}
					SO.setNetTotal(amount);
					if (ccDate != null) {
						StringTokenizer tokens = new StringTokenizer(ccDate, " ");
						String date = tokens.nextToken();
						so.setDate(date);
					} else {
						so.setDate(ccDate);
					}

					dosearchlist.add(so);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return dosearchlist;
	}


	public static ArrayList<SO> SearchDOCustCode(String cuscode, String sdate,
			String edate, String select_van,String locCode, String webMethNameSet) {

		dosearchlist.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo supCode = new PropertyInfo();
		PropertyInfo sDate = new PropertyInfo();
		PropertyInfo eDate = new PropertyInfo();
		PropertyInfo VanCode = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo LocationCode = new PropertyInfo();
		PropertyInfo TranType = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		supCode.setName("CustomerCode");
		supCode.setValue(cuscode);
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

		VanCode.setName("VanCode");
		VanCode.setValue(select_van);
		VanCode.setType(String.class);
		request.addProperty(VanCode);

		LocationCode.setName("LocationCode");
		LocationCode.setValue(locCode);
		LocationCode.setType(String.class);
		request.addProperty(LocationCode);


		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("soheader", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String ccSno = jsonChildNode.optString("DoNo").toString();
					String ccSoNo = jsonChildNode.optString("SoNo")
						       .toString();
					String ccDate = jsonChildNode.optString("DoDate")
							.toString();
					String customerCode = jsonChildNode.optString(
							"CustomerCode").toString();
					String customerName = jsonChildNode.optString(
							"CustomerName").toString();
					String amount = jsonChildNode.optString("NetTotal")
							.toString();
					String gotSignatureOnDo = jsonChildNode.optString("GotSignatureOnDO")
							.toString();
					String DelCustomerName = jsonChildNode.optString("DelCustomerName")
							.toString();

					SO so = new SO();
					so.setSno(ccSno);
					so.setSoNo(ccSoNo);
					so.setCustomerCode(customerCode);
					so.setCustomerName(customerName);
					so.setNettotal(amount);
					if(gotSignatureOnDo!=null&& !gotSignatureOnDo.isEmpty()){
						so.setGotSignatureOnDO(gotSignatureOnDo);
					}else{
						so.setGotSignatureOnDO("False");
					}
						
					
					
					so.setDelCustomerName(DelCustomerName);
					// if (ccDate != null) {
					// DateFormat f = new SimpleDateFormat(
					// "MM/dd/yyyy hh:mm:ss a");
					// Date d = f.parse(ccDate);
					// DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
					//
					// so.setDate(date.format(d));
					// } else {
					//
					// so.setDate(ccDate);
					// }

					if (ccDate != null) {
						StringTokenizer tokens = new StringTokenizer(ccDate,
								" ");
						String date = tokens.nextToken();
						so.setDate(date);
					} else {
						so.setDate(ccDate);
					}

					dosearchlist.add(so);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return dosearchlist;
	}

	public static ArrayList<SO> SearchStockAdjustment(String webMethNameSet) {

		stAdjsearchlist.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo CompanyCode = new PropertyInfo();
		PropertyInfo LocationCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		String locCode = SalesOrderSetGet.getLocationcode();

		CompanyCode.setName("CompanyCode");
		CompanyCode.setValue(cmpnyCode);
		CompanyCode.setType(String.class);
		request.addProperty(CompanyCode);

		LocationCode.setName("LocationCode");
		LocationCode.setValue(locCode);
		LocationCode.setType(String.class);
		request.addProperty(LocationCode);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("stAdjheader", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String StockAdjNo = jsonChildNode.optString("StockAdjNo")
							.toString();
					String StockAdjDate = jsonChildNode.optString(
							"StockAdjDate").toString();
					String LocationCd = jsonChildNode.optString("LocationCode")
							.toString();

					SO so = new SO();
					so.setStAdjust_no(StockAdjNo);
					so.setStAdjust_date(StockAdjDate);
					so.setStAdjust_location(LocationCd);

					stAdjsearchlist.add(so);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return stAdjsearchlist;
	}

	public static ArrayList<SO> SearchIOCustCode(String cuscode, String sdate,
			String edate, String status, String select_van,String locationCode,
			String webMethNameSet) {

		double paidtotalamount = 0.00,totalNet =0.00,totalbalamt = 0 ;
		String username = "";
//		SO.setTotalamount(totalamount);
		insearchlist.clear();

		int modeid = FWMSSettingsDatabase.getInvoiceuserModeId();
		if (modeid == 1) {
			FWMSSettingsDatabase.updateInvoiceuserMode(1);
			username = SupplierSetterGetter.getUsername();
		} else {
			FWMSSettingsDatabase.updateInvoiceuserMode(0);
			username = "";
		}

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo supCode = new PropertyInfo();
		PropertyInfo sDate = new PropertyInfo();
		PropertyInfo eDate = new PropertyInfo();
		PropertyInfo balanceflag = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo VanCode = new PropertyInfo();
		PropertyInfo LocationCode = new PropertyInfo();
		PropertyInfo User = new PropertyInfo();
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		supCode.setName("CustomerCode");
		supCode.setValue(cuscode);
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

		balanceflag.setName("BalanceFlag");
		balanceflag.setValue(status);
		balanceflag.setType(String.class);
		request.addProperty(balanceflag);

		VanCode.setName("VanCode");
		VanCode.setValue(select_van);
		VanCode.setType(String.class);
		request.addProperty(VanCode);
		Log.d("LocationCodes","--->"+locationCode);
		if(locationCode!=null && !locationCode.isEmpty()){
			LocationCode.setName("LocationCode");
			LocationCode.setValue(locationCode);
			LocationCode.setType(String.class);
			request.addProperty(LocationCode);
		}else{
			Log.d("LocationCode","--->"+locationCode);
		}

		Log.d("status","ss "+status+","+companyCode);
		Log.d("select_van","sv "+select_van);
		Log.d("locationCode","loc "+locationCode);
		Log.d("cuscode","cu "+cuscode);
		Log.d("DateCheck","-->"+sdate+","+edate);

		if(webMethNameSet.matches("fncGetInvoiceHeader")){
			User.setName("User");
			User.setValue(username);
			User.setType(String.class);
			request.addProperty(User);
		}
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("soheaderInvoice", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

//					String ccSno = jsonChildNode.optString("InvoiceNo")
//							.toString();
//					String ccDate = jsonChildNode.optString("InvoiceDate")
//							.toString();

					String ccSno="",ccDate="";

					if(webMethNameSet.matches("fncGetCashInvoiceHeader")){
						ccSno = jsonChildNode.optString("CashTranNo")
								.toString();
						ccDate = jsonChildNode
								.optString("CashTranDate").toString();
					}else{
						ccSno = jsonChildNode.optString("InvoiceNo")
								.toString();
						ccDate = jsonChildNode
								.optString("InvoiceDate").toString();
					}


					String customerCode = jsonChildNode.optString(
							"CustomerCode").toString();
					String amount = jsonChildNode.optString("NetTotal")
							.toString();

					String customerName = jsonChildNode.optString(
							"CustomerName").toString();

					String balanceamount = jsonChildNode.optString(
							"BalanceAmount").toString();

					String NoOfDays = jsonChildNode.optString("NoOfDays")
							.toString();
					String DueDays = jsonChildNode.optString("DueDays")
							.toString();
					String InvoiceSigned = jsonChildNode.optString("InvoiceSigned")
							.toString();
					String IsClosed = jsonChildNode.optString("IsClosed")
							.toString();
					String IsPosted = jsonChildNode.optString("IsPosted")
							.toString();
					String PaidAmount = jsonChildNode.optString("PaidAmount")
							.toString();
					String DONo =jsonChildNode.optString("DoNo").toString();
					String salesType  =jsonChildNode.optString("SalesType").toString();
					String balanceStatus = jsonChildNode.optString("BalanceStatus").toString();

					String customeraddress1 = jsonChildNode.optString("CustomerAddress1").toString();
					String customeraddress2 = jsonChildNode.optString("CustomerAddress2").toString();
					String customeraddress3 = jsonChildNode.optString("CustomerAddress3").toString();
					Log.d("DonoValue1","-->"+DONo);

					Log.d("PaidAmtCheck",PaidAmount+","+balanceamount);

					SO so = new SO();
					so.setSno(ccSno);
					so.setCustomerCode(customerCode);
					so.setCustomerName(customerName);
					so.setNettotal(amount);
					so.setBalanceamount(balanceamount);
					so.setNoOfDays(NoOfDays);
					so.setDueDays(DueDays);
					so.setInvoiceSigned(InvoiceSigned);
					so.setIsClosed(IsClosed);
					so.setIsPosted(IsPosted);
					so.setDono(DONo);
					so.setSalesType(salesType);
					so.setStatus(balanceStatus);
					so.setGotSignatureOnDO("False");
					so.setSoNo("0");
					so.setCustomeraddress1(customeraddress1);
					so.setCustomeraddress1(customeraddress2);
					so.setCustomeraddress3(customeraddress3);
					so.setPaidAmts(PaidAmount);

					double balamt = 0;
					if (balanceamount != null && !balanceamount.isEmpty()) {
						balamt = Double.parseDouble(balanceamount);
					}else{
						balamt = 0;
					}

					if(balamt>0) {
						totalbalamt = totalbalamt + balamt;
					}


					SO.setTotalamount(totalbalamt);

					double paidAmt = 0;
					if (PaidAmount != null && !PaidAmount.isEmpty()) {
						paidAmt = Double.parseDouble(PaidAmount);
					}

					if(paidAmt>0) {
						paidtotalamount = paidtotalamount + Double.valueOf(paidAmt);
					}

					double netTotal = 0;
					if(amount!=null && !amount.isEmpty()){
						netTotal = Double.parseDouble(amount);
					}

					if(netTotal>0) {
						totalNet = totalNet + netTotal;
					}

					if (ccDate != null) {
						StringTokenizer tokens = new StringTokenizer(ccDate,
								" ");
						String date = tokens.nextToken();
						so.setDate(date);
					} else {
						so.setDate(ccDate);
					}

					insearchlist.add(so);

				}



				if(status.matches("0")){
					Log.d("NotPaid","NP " + totalbalamt);
//					SO.setTotalpaid();
					SO.setTotalamount(totalbalamt);
				}else if(status.matches("1")){
					Log.d("Paid","P " + paidtotalamount);
					SO.setTotalamount(paidtotalamount);
				}else if(status.matches("2")){
					Log.d("All","NT " + totalNet);
					SO.setTotalamount(totalNet);
				}else{
					Log.d("Nothing","N ");
					SO.setTotalamount(0);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return insearchlist;
	}

	public static ArrayList<SO> Barcodesearch(String invoiceno, String cuscode, String sdate,
											  String edate, String status, String select_van,String locationCode,
											  String webMethNameSet) {

		double paidtotalamount = 0.00,totalNet =0.00,totalbalamt = 0 ;
		String username = "";
//		SO.setTotalamount(totalamount);
		insearchlist.clear();

		int modeid = FWMSSettingsDatabase.getInvoiceuserModeId();
		if (modeid == 1) {
			FWMSSettingsDatabase.updateInvoiceuserMode(1);
			username = SupplierSetterGetter.getUsername();
		} else {
			FWMSSettingsDatabase.updateInvoiceuserMode(0);
			username = "";
		}

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo invoiceNo = new PropertyInfo();
		PropertyInfo supCode = new PropertyInfo();
		PropertyInfo sDate = new PropertyInfo();
		PropertyInfo eDate = new PropertyInfo();
		PropertyInfo balanceflag = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo VanCode = new PropertyInfo();
		PropertyInfo LocationCode = new PropertyInfo();
		PropertyInfo User = new PropertyInfo();
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		invoiceNo.setName("InvoiceNo");
		invoiceNo.setValue(invoiceno);
		invoiceNo.setType(String.class);
		request.addProperty(invoiceNo);

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		supCode.setName("CustomerCode");
		supCode.setValue(cuscode);
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

		balanceflag.setName("BalanceFlag");
		balanceflag.setValue(status);
		balanceflag.setType(String.class);
		request.addProperty(balanceflag);

		VanCode.setName("VanCode");
		VanCode.setValue(select_van);
		VanCode.setType(String.class);
		request.addProperty(VanCode);
		Log.d("LocationCodes","--->"+locationCode);
		if(locationCode!=null && !locationCode.isEmpty()){
			LocationCode.setName("LocationCode");
			LocationCode.setValue(locationCode);
			LocationCode.setType(String.class);
			request.addProperty(LocationCode);
		}else{
			Log.d("LocationCode","--->"+locationCode);
		}

		Log.d("status","ss "+status+","+companyCode);
		Log.d("select_van","sv "+select_van);
		Log.d("locationCode","loc "+locationCode);
		Log.d("cuscode","cu "+cuscode);
		Log.d("DateCheck","-->"+sdate+","+edate);

		if(webMethNameSet.matches("fncGetInvoiceHeader")){
			User.setName("User");
			User.setValue(username);
			User.setType(String.class);
			request.addProperty(User);
		}
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("soheaderInvoice", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

//					String ccSno = jsonChildNode.optString("InvoiceNo")
//							.toString();
//					String ccDate = jsonChildNode.optString("InvoiceDate")
//							.toString();

					String ccSno="",ccDate="";

					if(webMethNameSet.matches("fncGetCashInvoiceHeader")){
						ccSno = jsonChildNode.optString("CashTranNo")
								.toString();
						ccDate = jsonChildNode
								.optString("CashTranDate").toString();
					}else{
						ccSno = jsonChildNode.optString("InvoiceNo")
								.toString();
						ccDate = jsonChildNode
								.optString("InvoiceDate").toString();
					}


					String customerCode = jsonChildNode.optString(
							"CustomerCode").toString();
					String amount = jsonChildNode.optString("NetTotal")
							.toString();

					String customerName = jsonChildNode.optString(
							"CustomerName").toString();

					String balanceamount = jsonChildNode.optString(
							"BalanceAmount").toString();

					String NoOfDays = jsonChildNode.optString("NoOfDays")
							.toString();
					String DueDays = jsonChildNode.optString("DueDays")
							.toString();
					String InvoiceSigned = jsonChildNode.optString("InvoiceSigned")
							.toString();
					String IsClosed = jsonChildNode.optString("IsClosed")
							.toString();
					String IsPosted = jsonChildNode.optString("IsPosted")
							.toString();
					String PaidAmount = jsonChildNode.optString("PaidAmount")
							.toString();
					String DONo =jsonChildNode.optString("DoNo").toString();
					String salesType  =jsonChildNode.optString("SalesType").toString();
					String balanceStatus = jsonChildNode.optString("BalanceStatus").toString();

					String customeraddress1 = jsonChildNode.optString("CustomerAddress1").toString();
					String customeraddress2 = jsonChildNode.optString("CustomerAddress2").toString();
					String customeraddress3 = jsonChildNode.optString("CustomerAddress3").toString();
					Log.d("DonoValue1","-->"+DONo);

					Log.d("PaidAmtCheck",PaidAmount+","+balanceamount);

					SO so = new SO();
					so.setSno(ccSno);
					so.setCustomerCode(customerCode);
					so.setCustomerName(customerName);
					so.setNettotal(amount);
					so.setBalanceamount(balanceamount);
					so.setNoOfDays(NoOfDays);
					so.setDueDays(DueDays);
					so.setInvoiceSigned(InvoiceSigned);
					so.setIsClosed(IsClosed);
					so.setIsPosted(IsPosted);
					so.setDono(DONo);
					so.setSalesType(salesType);
					so.setStatus(balanceStatus);
					so.setGotSignatureOnDO("False");
					so.setSoNo("0");
					so.setCustomeraddress1(customeraddress1);
					so.setCustomeraddress1(customeraddress2);
					so.setCustomeraddress3(customeraddress3);
					so.setPaidAmts(PaidAmount);

					double balamt = 0;
					if (balanceamount != null && !balanceamount.isEmpty()) {
						balamt = Double.parseDouble(balanceamount);
					}else{
						balamt = 0;
					}

					if(balamt>0) {
						totalbalamt = totalbalamt + balamt;
					}

					SO.setTotalamount(totalbalamt);

					double paidAmt = 0;
					if (PaidAmount != null && !PaidAmount.isEmpty()) {
						paidAmt = Double.parseDouble(PaidAmount);
					}

					if(paidAmt>0) {
						paidtotalamount = paidtotalamount + Double.valueOf(paidAmt);
					}

					double netTotal = 0;
					if(amount!=null && !amount.isEmpty()){
						netTotal = Double.parseDouble(amount);
					}

					if(netTotal>0) {
						totalNet = totalNet + netTotal;
					}

					if (ccDate != null) {
						StringTokenizer tokens = new StringTokenizer(ccDate,
								" ");
						String date = tokens.nextToken();
						so.setDate(date);
					} else {
						so.setDate(ccDate);
					}

					insearchlist.add(so);

				}



				if(status.matches("0")){
					Log.d("NotPaid","NP " + totalbalamt);
//					SO.setTotalpaid();
					SO.setTotalamount(totalbalamt);
				}else if(status.matches("1")){
					Log.d("Paid","P " + paidtotalamount);
					SO.setTotalamount(paidtotalamount);
				}else if(status.matches("2")){
					Log.d("All","NT " + totalNet);
					SO.setTotalamount(totalNet);
				}else{
					Log.d("Nothing","N ");
					SO.setTotalamount(0);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return insearchlist;
	}

	public static ArrayList<String> getCustGroupCode(String cuscode,
			String webMethName) throws JSONException {
		cstmrgrpcdal.clear();
		PropertyInfo moduleName = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		moduleName.setName("CustomerCode");
		moduleName.setValue(cuscode);
		moduleName.setType(String.class);
		request.addProperty(moduleName);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("customerCode", resTxt);

			String results = " { CustomerGroups : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(results);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("CustomerGroups");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String custcode = jsonChildNode.optString("CustomerCode")
							.toString();
					String CustomerName = jsonChildNode.optString(
							"CustomerName").toString();
					String custgroup = jsonChildNode.optString(
							"CustomerGroupCode").toString();
					if(custgroup.matches("")){
						custgroup ="0";
						Log.d("custgroup","-->"+custgroup);
					}else{
						custgroup = jsonChildNode.optString(
								"CustomerGroupCode").toString();
					}
					String Address1 = jsonChildNode.optString("Address1")
							.toString();
					String Address2 = jsonChildNode.optString("Address2")
							.toString();
					String Address3 = jsonChildNode.optString("Address3")
							.toString();
					String PhoneNo = jsonChildNode.optString("PhoneNo")
							.toString();
					String HandphoneNo = jsonChildNode.optString("HandphoneNo")
							.toString();
					String Email = jsonChildNode.optString("Email").toString();
					String TermName = jsonChildNode.optString("TermName")
							.toString();
					String OutstandingAmount = jsonChildNode.optString(
							"OutstandingAmount").toString();
					String DiscountPercentage = jsonChildNode.optString("DiscountPercentage").toString();
					Log.d("DiscountPercent","-->"+DiscountPercentage);
					CustomerSetterGetter.setCustomerCode(custcode);
					CustomerSetterGetter.setCustomerName(CustomerName);
					CustomerSetterGetter.setCustomerAddress1(Address1);
					CustomerSetterGetter.setCustomerAddress2(Address2);
					CustomerSetterGetter.setCustomerAddress3(Address3);
					CustomerSetterGetter.setCustomerPhone(PhoneNo);
					CustomerSetterGetter.setCustomerHP(HandphoneNo);
					CustomerSetterGetter.setCustomerEmail(Email);
					CustomerSetterGetter.setCustomerTerms(TermName);
					CustomerSetterGetter.setTotalOutstanding(OutstandingAmount);
					CustomerSetterGetter.setDiscountPercentage(DiscountPercentage);

					cstmrgrpcdal.add(custcode);
					cstmrgrpcdal.add(custgroup);

				}
				Log.d("cstmrgrpcdal", "" + cstmrgrpcdal);

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return cstmrgrpcdal;
	}

	public static List<HashMap<String,String>> getInvoiceHeaderExceedsTerms(String cuscode,
																			String webMethName) throws JSONException {
		invoiceExceedTernsArr.clear();
		PropertyInfo moduleName = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		Log.d("CompanyCode", "-->"+companyCode);

		moduleName.setName("CustomerCode");
		moduleName.setValue(cuscode);
		moduleName.setType(String.class);
		request.addProperty(moduleName);


		Log.d("CustomerCode", "-->"+cuscode);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("HeaderExceedsTerms", resTxt);

			String results = " { ExceedsTerms : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(results);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("ExceedsTerms");
				int lengthJsonArr = jsonMainNode.length();
				if(lengthJsonArr>0) {
					for (int i = 0; i < lengthJsonArr; i++) {
						JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
						String InvoiceNo = jsonChildNode.optString("InvoiceNo")
								.toString();
						String InvoiceDate = jsonChildNode.optString(
								"InvoiceDate").toString().split("\\ ")[0];
						String NetTotal = jsonChildNode.optString(
								"NetTotal").toString();

						HashMap<String, String> hm = new HashMap<String,String>();
						hm.put("InvoiceNo"  ,InvoiceNo);
						hm.put("InvoiceDate",InvoiceDate);
						hm.put("NetTotal"   ,NetTotal);
						invoiceExceedTernsArr.add(hm);

						//invoiceExceedTernsArr.add("Invoice No : " + InvoiceNo);
						//invoiceExceedTernsArr.add("Invoice Date : " + InvoiceDate);
						//invoiceExceedTernsArr.add("NetTotal : " + NetTotal);

					}
					Log.d("invoiceExceedTernsArr", "" + invoiceExceedTernsArr);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return invoiceExceedTernsArr;
	}

	public static List<HashMap<String,String>> fncGetOutstandingSummary(String cuscode,
																			String webMethName) throws JSONException {
		invoiceCreditLimitArr.clear();
		PropertyInfo moduleName = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		Log.d("CompanyCode", "-->"+companyCode);

		moduleName.setName("Customercode");
		moduleName.setValue(cuscode);
		moduleName.setType(String.class);
		request.addProperty(moduleName);


		Log.d("CustomerCode", "-->"+cuscode);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("Outstanding", resTxt);

			String results = " { Outstanding : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(results);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("Outstanding");
				int lengthJsonArr = jsonMainNode.length();
				if(lengthJsonArr>0) {
					for (int i = 0; i < lengthJsonArr; i++) {
						JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
						String Customercode = jsonChildNode.optString("Customercode")
								.toString();
						String CustomerName = jsonChildNode.optString(
								"CustomerName").toString();
						String BalanceAmount = jsonChildNode.optString(
								"BalanceAmount").toString();

						HashMap<String, String> hm = new HashMap<String,String>();
						hm.put("Customercode"  ,Customercode);
						hm.put("CustomerName",CustomerName);
						hm.put("BalanceAmount"   ,BalanceAmount);
						invoiceCreditLimitArr.add(hm);

					}
					Log.d("invoiceCreditLimitArr", "" + invoiceCreditLimitArr);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return invoiceCreditLimitArr;
	}

	public static void getCustomerTax(String cuscode, String webMethName)
			throws JSONException {

		PropertyInfo moduleName = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		moduleName.setName("CustomerCode");
		moduleName.setValue(cuscode);
		moduleName.setType(String.class);
		request.addProperty(moduleName);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("customerTax", resTxt);

			String results = " { CustomerTax : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(results);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("CustomerTax");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String HaveTax = jsonChildNode.optString("HaveTax")
							.toString();

					String TaxType = jsonChildNode.optString("TaxType")
							.toString();

					String TaxValue = jsonChildNode.optString("TaxValue")
							.toString();

					if (HaveTax.matches("True")) {
//						SalesOrderSetGet.setTaxValue(TaxValue);
						SalesOrderSetGet.setCompanytax(TaxType);
						SalesOrderSetGet.setCustomerTaxPerc(TaxValue);
					} else {
//						SalesOrderSetGet.setTaxValue("");
						SalesOrderSetGet.setCompanytax("Z");
						SalesOrderSetGet.setCustomerTaxPerc("0.00");
					}
					Log.d("CustomerTaxType", ""+TaxType);
					Log.d("CustomerTaxValue", ""+TaxValue);

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

	}

	public static void getSupplierTax(String suppcode, String webMethName)
			throws JSONException {

		PropertyInfo moduleName = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		moduleName.setName("SupplierCode");
		moduleName.setValue(suppcode);
		moduleName.setType(String.class);
		request.addProperty(moduleName);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("SupplierTax", resTxt);

			String results = " { SupplierTax : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(results);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SupplierTax");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String HaveTax = jsonChildNode.optString("HaveTax")
							.toString();

					String TaxType = jsonChildNode.optString("TaxType")
							.toString();

					String TaxValue = jsonChildNode.optString("TaxValue")
							.toString();

					if (HaveTax.matches("True") || HaveTax.matches("true")) {
//						SalesOrderSetGet.setTaxValue(TaxValue);
						SalesOrderSetGet.setSuppliertax(TaxType);
						SalesOrderSetGet.setCompanytax(TaxType);
					} else {
//						SalesOrderSetGet.setTaxValue("");
						SalesOrderSetGet.setSuppliertax("");
						SalesOrderSetGet.setCompanytax("Z");
					}

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

	}

	public static ArrayList<String> getSuppTermCode(String cuscode,
			String webMethName) throws JSONException {
		cstmrgrpcdal.clear();
		PropertyInfo moduleName = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		moduleName.setName("SupplierCode");
		moduleName.setValue(cuscode);
		moduleName.setType(String.class);
		request.addProperty(moduleName);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("customerCode", resTxt);

			String results = " { SupplierGroups : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(results);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SupplierGroups");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String custcode = jsonChildNode.optString("SupplierCode")
							.toString();
					String custgroup = jsonChildNode.optString("TermCode")
							.toString();
					cstmrgrpcdal.add(custcode);
					cstmrgrpcdal.add(custgroup);

				}
				Log.d("cstmrgrpcdal", "" + cstmrgrpcdal);

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return cstmrgrpcdal;

	}

	public static ArrayList<SO> SearchGRACustCode(String cuscode, String sDate,
			String eDate, String webMethNameSet) {

		grasearchlist.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo supCode = new PropertyInfo();
		PropertyInfo gsDate = new PropertyInfo();
		PropertyInfo geDate = new PropertyInfo();

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		supCode.setName("SupplierCode");
		supCode.setValue(cuscode);
		supCode.setType(String.class);
		request.addProperty(supCode);

		gsDate.setName("FromDate");
		gsDate.setValue(sDate);
		gsDate.setType(String.class);
		request.addProperty(gsDate);

		geDate.setName("ToDate");
		geDate.setValue(eDate);
		geDate.setType(String.class);
		request.addProperty(geDate);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("soheader", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String ccSno = jsonChildNode.optString("GRANo").toString();
					String ccDate = jsonChildNode.optString("GRADate")
							.toString();
					String customerCode = jsonChildNode.optString(
							"SupplierCode").toString();
					String SupplierName = jsonChildNode.optString(
								"SupplierName").toString();
					String amount = jsonChildNode.optString("NetTotal")
							.toString();
					String status = jsonChildNode.optString("Status")
							.toString();

					SO so = new SO();
					so.setSno(ccSno);
					so.setCustomerCode(customerCode);
					so.setCustomerName(SupplierName);
					so.setNettotal(amount);
					so.setStatus(status);

					if (ccDate != null) {
						StringTokenizer tokens = new StringTokenizer(ccDate,
								" ");
						String date = tokens.nextToken();
						so.setDate(date);
					} else {
						so.setDate(ccDate);
					}

					grasearchlist.add(so);

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return grasearchlist;
	}

	public static ArrayList<HashMap<String, String>> getSODetails(String soNo,
			String webMethName) {
		String resTxt = null;
		SODetailsArr.clear();
		PropertyInfo soNoPI = new PropertyInfo();
		PropertyInfo locationcode = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		String location = SalesOrderSetGet.getLocationcode();
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		soNoPI.setName("SoNo");
		soNoPI.setValue(soNo);
		soNoPI.setType(String.class);
		request.addProperty(soNoPI);

		locationcode.setName("LocationCode");
		locationcode.setValue(location);
		locationcode.setType(String.class);
		request.addProperty(locationcode);

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

			result = " { SODetails : " + resTxt + "}";
			Log.d("SODetails search", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("SODetails");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String slNo = jsonChildNode.optString("slNo").toString();
					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String ProductName = jsonChildNode.optString("ProductName")
							.toString();
					String CQty = jsonChildNode.optString("CQty").toString();
					String LQty = jsonChildNode.optString("LQty").toString();
					String Qty = jsonChildNode.optString("Qty").toString();
					String FOCQty = jsonChildNode.optString("FOCQty")
							.toString();
					String PcsPerCarton = jsonChildNode.optString(
							"PcsPerCarton").toString();
					String RetailPrice = jsonChildNode.optString("RetailPrice")
							.toString();
					String Price = jsonChildNode.optString("Price").toString();
					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDiscount = jsonChildNode.optString(
							"BillDiscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String TaxType = jsonChildNode.optString("TaxType")
							.toString();
					String TaxPerc = jsonChildNode.optString("TaxPerc")
							.toString();

					String ExchangeQty = jsonChildNode.optString("ExchangeQty")
							.toString();
					String CartonPrice = jsonChildNode.optString("CartonPrice")
							.toString();
					String MinimumSellingPrice = jsonChildNode.optString(
							"MinimumSellingPrice").toString();
					String MinimumCartonSellingPrice = jsonChildNode.optString(
							"MinimumCartonSellingPrice").toString();
					String ItemRemarks = jsonChildNode.optString("ItemRemarks")
							.toString();

					String InvoiceQty = jsonChildNode.optString("InvoiceQty")
							.toString();
					String InvoiceFocQty = jsonChildNode.optString(
							"InvoiceFocQty").toString();

					String DOQty = jsonChildNode.optString("DOQty").toString();
					String DOFocQty = jsonChildNode.optString("DOFocQty")
							.toString();

					String QtyOnHand = jsonChildNode.optString("QtyOnHand")
							.toString();
					String avg_cost =jsonChildNode.optString("AverageCost").toString();
					SalesOrderSetGet.setAvg_cost(avg_cost);

					if (MinimumSellingPrice.matches("null")) {
						MinimumSellingPrice = "0.00";
					}
					if (MinimumCartonSellingPrice.matches("null")) {
						MinimumCartonSellingPrice = "0.00";
					}
					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("slNo", slNo);
					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", ProductName);
					hm.put("CQty", CQty);
					hm.put("LQty", LQty);
					hm.put("Qty", Qty);
					hm.put("FOCQty", FOCQty);
					hm.put("PcsPerCarton", PcsPerCarton);
					hm.put("RetailPrice", RetailPrice);
					hm.put("Price", Price);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDiscount", BillDiscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("TaxType", TaxType);
					hm.put("TaxPerc", TaxPerc);

					hm.put("ExchangeQty", ExchangeQty);
					hm.put("CartonPrice", CartonPrice);
					hm.put("MinimumSellingPrice", MinimumSellingPrice);
					hm.put("MinimumCartonSellingPrice", MinimumCartonSellingPrice);
					hm.put("ItemRemarks", ItemRemarks);

					hm.put("InvoiceQty", InvoiceQty);
					hm.put("InvoiceFocQty", InvoiceFocQty);

					hm.put("DOQty", DOQty);
					hm.put("DOFocQty", DOFocQty);
					hm.put("QtyOnHand", QtyOnHand);
					hm.put("AverageCost",avg_cost);
					SODetailsArr.add(hm);

					Log.d("SO Detail List", SODetailsArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return SODetailsArr;
	}

	public static ArrayList<HashMap<String, String>> getDODetails(String doNo,
			String webMethName) {

		String resTxt = null;
		DODetailsArr.clear();
		PropertyInfo soNoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		soNoPI.setName("DoNo");
		soNoPI.setValue(doNo);
		soNoPI.setType(String.class);
		request.addProperty(soNoPI);

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

			result = " { DODetails : " + resTxt + "}";
			Log.d("DODetails search", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("DODetails");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String slNo = jsonChildNode.optString("slNo").toString();
					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String ProductName = jsonChildNode.optString("ProductName")
							.toString();
					String CQty = jsonChildNode.optString("CQty").toString();
					String LQty = jsonChildNode.optString("LQty").toString();
					String Qty = jsonChildNode.optString("Qty").toString();
					String FOCQty = jsonChildNode.optString("FOCQty")
							.toString();
					String PcsPerCarton = jsonChildNode.optString(
							"PcsPerCarton").toString();
					String RetailPrice = jsonChildNode.optString("RetailPrice")
							.toString();
					String Price = jsonChildNode.optString("Price").toString();
					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDiscount = jsonChildNode.optString(
							"BillDiscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String TaxType = jsonChildNode.optString("TaxType")
							.toString();
					String TaxPerc = jsonChildNode.optString("TaxPerc")
							.toString();
					String ExchangeQty = jsonChildNode.optString("ExchangeQty")
							.toString();
					String CartonPrice = jsonChildNode.optString("CartonPrice")
							.toString();
					String MinimumSellingPrice = jsonChildNode.optString(
							"MinimumSellingPrice").toString();

					String DOQty = jsonChildNode.optString("DOQty").toString();
					String DOFocQty = jsonChildNode.optString("DOFocQty")
							.toString();

					String ItemRemarks = jsonChildNode.optString("ItemRemarks")
							.toString();

					String avg_cost = jsonChildNode.optString("AverageCost")
							.toString();

					if (MinimumSellingPrice.matches("null")) {
						MinimumSellingPrice = "0";
					}

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("slNo", slNo);
					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", ProductName);
					hm.put("CQty", CQty);
					hm.put("LQty", LQty);
					hm.put("Qty", Qty);
					hm.put("FOCQty", FOCQty);
					hm.put("PcsPerCarton", PcsPerCarton);
					hm.put("RetailPrice", RetailPrice);
					hm.put("Price", Price);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDiscount", BillDiscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("TaxType", TaxType);
					hm.put("TaxPerc", TaxPerc);

					hm.put("ExchangeQty", ExchangeQty);
					hm.put("CartonPrice", CartonPrice);
					hm.put("MinimumSellingPrice", MinimumSellingPrice);

					hm.put("DOQty", DOQty);
					hm.put("DOFocQty", DOFocQty);
					hm.put("ItemRemarks", ItemRemarks);
					hm.put("AverageCost",avg_cost);

					DODetailsArr.add(hm);
					Log.d("DO Detail List", DODetailsArr.toString());
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return DODetailsArr;
	}

	public static ArrayList<HashMap<String, String>> getDOHeader(String doNo,
			String webMethName) {

		String resTxt = null;
		DOHeaderArr.clear();
		PropertyInfo doNoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		doNoPI.setName("DoNo");
		doNoPI.setValue(doNo);
		doNoPI.setType(String.class);
		request.addProperty(doNoPI);

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

			result = " { DOHeader : " + resTxt + "}";
			Log.d("DOHeader", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("DOHeader");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String DoNo = jsonChildNode.optString("DoNo").toString();
					String DoDate = jsonChildNode.optString("DoDate")
							.toString();
					String LocationCode = jsonChildNode.optString(
							"LocationCode").toString();
					String CustomerCode = jsonChildNode.optString(
							"CustomerCode").toString();

					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDIscount = jsonChildNode.optString(
							"BillDIscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String Remarks = jsonChildNode.optString("Remarks")
							.toString();
					String Status = jsonChildNode.optString("Status")
							.toString();
					String CurrencyCode = jsonChildNode.optString(
							"CurrencyCode").toString();
					String CurrencyRate = jsonChildNode.optString(
							"CurrencyRate").toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("DoNo", DoNo);
					hm.put("DoDate", DoDate);
					hm.put("LocationCode", LocationCode);
					hm.put("CustomerCode", CustomerCode);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDIscount", BillDIscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("Remarks", Remarks);
					hm.put("Status", Status);
					hm.put("CurrencyCode", CurrencyCode);
					hm.put("CurrencyRate", CurrencyRate);

					DOHeaderArr.add(hm);

					Log.d("DOHeader List", DOHeaderArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return DOHeaderArr;
	}

	public static ArrayList<HashMap<String, String>> getSRDetails(String SRNo,
																  String webMethName) {

		String resTxt = null;
		SRDetailsArr.clear();
		PropertyInfo soNoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		soNoPI.setName("SRNumber");
		soNoPI.setValue(SRNo);
		soNoPI.setType(String.class);
		request.addProperty(soNoPI);

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

			result = " { SRDetails : " + resTxt + "}";
			Log.d("SRDetails search", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("SRDetails");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String slNo = jsonChildNode.optString("slNo").toString();
					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String ProductName = jsonChildNode.optString("ProductName")
							.toString();
					String CQty = jsonChildNode.optString("CQty").toString();
					String LQty = jsonChildNode.optString("LQty").toString();
					String Qty = jsonChildNode.optString("Qty").toString();
					String FOCQty = jsonChildNode.optString("FOCQty")
							.toString();
					String PcsPerCarton = jsonChildNode.optString(
							"PcsPerCarton").toString();
					String RetailPrice = jsonChildNode.optString("RetailPrice")
							.toString();
					String Price = jsonChildNode.optString("Price").toString();
					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDiscount = jsonChildNode.optString(
							"BillDiscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String TaxType = jsonChildNode.optString("TaxType")
							.toString();
					String TaxPerc = jsonChildNode.optString("TaxPerc")
							.toString();
					String ExchangeQty = jsonChildNode.optString("ExchangeQty")
							.toString();
					String CartonPrice = jsonChildNode.optString("CartonPrice")
							.toString();
					String StockAdjRefCode = jsonChildNode.optString("StockAdjRefCode")
							.toString();
//					String MinimumSellingPrice = jsonChildNode.optString(
//							"MinimumSellingPrice").toString();

//					String ItemRemarks = jsonChildNode.optString("ItemRemarks")
//							.toString();


					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("slNo", slNo);
					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", ProductName);
					hm.put("CQty", CQty);
					hm.put("LQty", LQty);
					hm.put("Qty", Qty);
					hm.put("FOCQty", FOCQty);
					hm.put("PcsPerCarton", PcsPerCarton);
					hm.put("RetailPrice", RetailPrice);
					hm.put("Price", Price);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDiscount", BillDiscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("TaxType", TaxType);
					hm.put("TaxPerc", TaxPerc);

					hm.put("ExchangeQty", ExchangeQty);
					hm.put("CartonPrice", CartonPrice);
					hm.put("StockAdjRefCode", StockAdjRefCode);
//					hm.put("MinimumSellingPrice", MinimumSellingPrice);

//					hm.put("ItemRemarks", ItemRemarks);

					SRDetailsArr.add(hm);
					Log.d("SR Detail List", SRDetailsArr.toString());
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return SRDetailsArr;
	}

	public static ArrayList<HashMap<String, String>> getSRHeader(String SRNo,
																 String webMethName) {

		String resTxt = null;
		SRHeaderArr.clear();
		PropertyInfo doNoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		doNoPI.setName("SRNumber");
		doNoPI.setValue(SRNo);
		doNoPI.setType(String.class);
		request.addProperty(doNoPI);

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

			result = " { SRHeader : " + resTxt + "}";
			Log.d("SRHeader", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("SRHeader");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String SalesReturnNo = jsonChildNode.optString("SalesReturnNo").toString();
					String SalesReturnDate = jsonChildNode.optString("SalesReturnDate")
							.toString();
					String LocationCode = jsonChildNode.optString(
							"LocationCode").toString();
					String CustomerCode = jsonChildNode.optString(
							"CustomerCode").toString();

					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDIscount = jsonChildNode.optString(
							"BillDIscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String Remarks = jsonChildNode.optString("Remarks")
							.toString();
					String InvoiceNo = jsonChildNode.optString("InvoiceNo")
							.toString();
					String SalesType = jsonChildNode.optString("SalesType").toString();


					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("SalesReturnNo", SalesReturnNo);
					hm.put("SalesReturnDate", SalesReturnDate);
					hm.put("LocationCode", LocationCode);
					hm.put("CustomerCode", CustomerCode);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDIscount", BillDIscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("Remarks", Remarks);
					hm.put("InvoiceNo", InvoiceNo);
					hm.put("SalesType",SalesType);

					SalesOrderSetGet.setType(SalesReturnNo);


					SRHeaderArr.add(hm);

					Log.d("SRHeader List", SRHeaderArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return SRHeaderArr;
	}

	public static ArrayList<HashMap<String, String>> getSOHeader(String soNo,
			String webMethName) {

		String resTxt = null;
		SOHeaderArr.clear();
		PropertyInfo soNoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		soNoPI.setName("SoNo");
		soNoPI.setValue(soNo);
		soNoPI.setType(String.class);
		request.addProperty(soNoPI);

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

			result = " { SOHeader : " + resTxt + "}";
			Log.d("SOHeader", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("SOHeader");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String SoNo = jsonChildNode.optString("SoNo").toString();
					String SoDate = jsonChildNode.optString("SoDate")
							.toString();
					String DeliveryDate = jsonChildNode.optString(
							"DeliveryDate").toString();
					String LocationCode = jsonChildNode.optString(
							"LocationCode").toString();
					String CustomerCode = jsonChildNode.optString(
							"CustomerCode").toString();
					String CustomerName = jsonChildNode.optString(
							"CustomerName").toString();
					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDIscount = jsonChildNode.optString(
							"BillDIscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String Remarks = jsonChildNode.optString("Remarks")
							.toString();
					String Status = jsonChildNode.optString("Status")
							.toString();
					String CurrencyCode = jsonChildNode.optString(
							"CurrencyCode").toString();
					String CurrencyRate = jsonChildNode.optString(
							"CurrencyRate").toString();

					String ModifyUser = jsonChildNode.optString("ModifyUser")
							.toString();

					String isPacked = jsonChildNode.optString("isPacked").toString();
					String isQtyVerified = jsonChildNode.optString("isQtyVerified").toString();

					String DiscountPercentage = jsonChildNode.optString("DiscountPercentage").toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("SoNo", SoNo);
					hm.put("SoDate", SoDate);
					hm.put("DeliveryDate", DeliveryDate);
					hm.put("LocationCode", LocationCode);
					hm.put("CustomerCode", CustomerCode);
					hm.put("CustomerName", CustomerName);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDIscount", BillDIscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("Remarks", Remarks);
					hm.put("Status", Status);
					hm.put("CurrencyCode", CurrencyCode);
					hm.put("CurrencyRate", CurrencyRate);
					hm.put("ModifyUser", ModifyUser);
					hm.put("isPacked", isPacked);
					hm.put("isQtyVerified", isQtyVerified);
					hm.put("DiscountPercentage",DiscountPercentage);

					SOHeaderArr.add(hm);

					Log.d("SOHeader List", SOHeaderArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return SOHeaderArr;
	}

	public static ArrayList<SO> getSOHeaderList(
			HashMap<String, String> hashValue, String status,
			String webMethNameSet) {

		sosearchlist.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		for (HashMap.Entry<String, String> entry : hashValue.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);
			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
//			Log.d("soheader", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					// int sno=1+i;
					String ccSno = jsonChildNode.optString("SoNo").toString();
					/*
					 * String ccDate = jsonChildNode.optString("SoDate")
					 * .toString();
					 */
					String ccDate = jsonChildNode.optString("DeliveryDate")
							.toString();
					String customerCode = jsonChildNode.optString(
							"CustomerCode").toString();
					String customerName = jsonChildNode.optString(
							"CustomerName").toString();
					String amount = jsonChildNode.optString("NetTotal")
							.toString();
					String statusValue = jsonChildNode.optString("Status")
							.toString();
					String SubTotal = jsonChildNode.optString("SubTotal");
					String customerAddress1 = jsonChildNode.optString("CustomerAddress1")
							.toString();
					String customerAddress2 = jsonChildNode.optString("CustomerAddress2")
							.toString();
					String customerAddress3 = jsonChildNode.optString("CustomerAddress3")
							.toString();
					// Log.d("status", status);
					Log.d("statusValue", statusValue);

					SO so = new SO();
					// Based on status it show individual values
					if (status.matches(statusValue)) {
						so.setSno(ccSno);
						so.setCustomerCode(customerCode);
						so.setCustomerName(customerName);
						so.setNettotal(amount);
						so.setSubTotal(SubTotal);
						so.setCustomeraddress1(customerAddress1);
						so.setCustomeraddress2(customerAddress2);
						so.setCustomeraddress3(customerAddress3);

						if (statusValue.matches("0")) {
							so.setStatus("open");
						} else if (statusValue.matches("2")) {
							so.setStatus("InProgress Invoice");
						} else if (statusValue.matches("1")) {
							so.setStatus("InProgress DO");
						} else if (statusValue.matches("3")) {
							so.setStatus("closed");
						}
						// else if(statusValue.matches("4")){
						// so.setStatus("deleted");
						// }
						else {
							so.setStatus("open");
						}

						if (ccDate != null && !ccDate.isEmpty()) {
							StringTokenizer tokens = new StringTokenizer(
									ccDate, " ");
							String date = tokens.nextToken();
							so.setDate(date);
						} else {
							so.setDate(ccDate);
						}

						sosearchlist.add(so);
					}
					// Show all values
					else if (status.matches("5")) {
						so.setSno(ccSno);
						so.setCustomerCode(customerCode);
						so.setCustomerName(customerName);
						so.setNettotal(amount);
						so.setSubTotal(SubTotal);
						so.setCustomeraddress1(customerAddress1);
						so.setCustomeraddress2(customerAddress2);
						so.setCustomeraddress3(customerAddress3);
						// so.setStatus(statusValue);

						if (statusValue.matches("0")) {
							so.setStatus("open");
						} else if (statusValue.matches("2")) {
							so.setStatus("InProgress Invoice");
						} else if (statusValue.matches("1")) {
							so.setStatus("InProgress DO");
						} else if (statusValue.matches("3")) {
							so.setStatus("closed");
						}
						// else if(statusValue.matches("4")){
						// so.setStatus("deleted");
						// }

						if (ccDate != null && !ccDate.isEmpty()) {
							StringTokenizer tokens = new StringTokenizer(
									ccDate, " ");
							String date = tokens.nextToken();
							so.setDate(date);
						} else {
							so.setDate(ccDate);
						}

						sosearchlist.add(so);
					}
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sosearchlist;
	}

	public static String getSODetail(HashMap<String, String> hashValue,
			String webMethNameGet) {

		Log.d("webMethNameGet",webMethNameGet);
		Log.d("validURL","v "+validURL);

		String result = null;
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

		for (HashMap.Entry<String, String> entry : hashValue.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);
			System.out.printf(" key %s", entry.getKey());
			System.out.printf(" Value %s%n", entry.getValue());
		}
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;
			Log.e("getSoRequest", request.toString());
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.e("getResponse", resTxt.toString());
			result = " { SODetails : " + resTxt + "}";

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return result;
	}

	public static ArrayList<SO> SearchSRCustCode(String cuscode, String sdate,
			String edate, String select_van,String locCode , String webMethNameSet) {
		double balanceAmount  = 0.00;
		SO.setTotalamount(balanceAmount);
		sosearchlist.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo supCode = new PropertyInfo();
		PropertyInfo sDate = new PropertyInfo();
		PropertyInfo eDate = new PropertyInfo();
		PropertyInfo VanCode = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo LocationCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		supCode.setName("CustomerCode");
		supCode.setValue(cuscode);
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

		VanCode.setName("VanCode");
		VanCode.setValue(select_van);
		VanCode.setType(String.class);
		request.addProperty(VanCode);

		LocationCode.setName("LocationCode");
		LocationCode.setValue(locCode);
		LocationCode.setType(String.class);
		request.addProperty(LocationCode);
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("soheader", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					// int sno=1+i;
					String ccSno = jsonChildNode.optString("SalesReturnNo")
							.toString();
					String ccDate = jsonChildNode.optString("SalesReturnDate")
							.toString();
					String customerCode = jsonChildNode.optString(
							"CustomerCode").toString();
					String CustomerName = jsonChildNode.optString(
							"CustomerName").toString();
					String amount = jsonChildNode.optString("NetTotal")
							.toString();
					String balanceamount = jsonChildNode.optString(
							"BalanceAmount").toString();
					String SalesType = jsonChildNode.optString(
							"SalesType").toString();

					SO so = new SO();
					so.setSno(ccSno);
					so.setCustomerCode(customerCode);
					so.setNettotal(amount);
					so.setBalanceamount(balanceamount);
					so.setSalesType(SalesType);
					so.setCustomerName(CustomerName);

					if (ccDate != null) {
						StringTokenizer tokens = new StringTokenizer(ccDate,
								" ");
						String date = tokens.nextToken();
						so.setDate(date);
					} else {
						so.setDate(ccDate);
					}
					if (balanceamount != null && !balanceamount.isEmpty()) {
						double balanceAmt = Double.parseDouble(balanceamount);
						if(balanceAmt>0){
							balanceAmount = balanceAmount + balanceAmt;
						}
					}
					sosearchlist.add(so);

				}
				SO.setTotalamount(balanceAmount);
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sosearchlist;
	}

	public static ArrayList<SO> SearchStockRequest(String fromdate,
			String todate, String fromlocation, String tolocation,
			String status, String webMethNameSet) {

		stockreqsearchlist.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo gsDate = new PropertyInfo();
		PropertyInfo geDate = new PropertyInfo();
		PropertyInfo fromLoc = new PropertyInfo();
		PropertyInfo toLoc = new PropertyInfo();

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		gsDate.setName("FromDate");
		gsDate.setValue(fromdate);
		gsDate.setType(String.class);
		request.addProperty(gsDate);

		geDate.setName("ToDate");
		geDate.setValue(todate);
		geDate.setType(String.class);
		request.addProperty(geDate);

		fromLoc.setName("FromLocation");
		fromLoc.setValue(fromlocation);
		fromLoc.setType(String.class);
		request.addProperty(fromLoc);

		toLoc.setName("ToLocation");
		toLoc.setValue(tolocation);
		toLoc.setType(String.class);
		request.addProperty(toLoc);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("soheader", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String ccSno = jsonChildNode.optString("StockReqNo")
							.toString();
					String ccDate = jsonChildNode.optString("StockReqDate")
							.toString();
					String fromloc = jsonChildNode.optString("FromLocation")
							.toString();
					String toloc = jsonChildNode.optString("ToLocation")
							.toString();
					String statuss = jsonChildNode.optString("Status")
							.toString();
					String remarks =jsonChildNode.optString("Remarks").toString();

					Log.d("RemarksStatus","->"+remarks);

					Log.d("SO status", statuss);


					SO so = new SO();
						so.setRemarks(remarks);
					if (status.matches(statuss)) {
						so.setSno(ccSno);
						so.setFromlocation(fromloc);
						so.setTolocation(toloc);

						if (statuss.matches("0")) {
							so.setStatus("open");
						} else if (statuss.matches("1")) {
							so.setStatus("InProgress");
						} else if (statuss.matches("2")) {
							so.setStatus("Closed");
							// }else if(statuss.matches("3")){
							// so.setStatus("closed");
							// }else if(statuss.matches("4")){
							// so.setStatus("deleted");
						} else {
							so.setStatus("open");
						}

						if (ccDate != null) {
							StringTokenizer tokens = new StringTokenizer(
									ccDate, " ");
							String date = tokens.nextToken();
							so.setDate(date);
						} else {
							so.setDate(ccDate);
						}

						stockreqsearchlist.add(so);
					}
					// else if (status.matches("5")) {
					// so.setSno(ccSno);
					// so.setFromlocation(fromloc);
					// so.setTolocation(toloc);
					//
					// if(statuss.matches("0")){
					// so.setStatus("open");
					// }else if(statuss.matches("1")){
					// so.setStatus("view");
					// }else if(statuss.matches("2")){
					// so.setStatus("partial");
					// }else if(statuss.matches("3")){
					// so.setStatus("closed");
					// }else if(statuss.matches("4")){
					// so.setStatus("deleted");
					// }
					//
					// if (ccDate != null) {
					// StringTokenizer tokens = new StringTokenizer(
					// ccDate, " ");
					// String date = tokens.nextToken();
					// so.setDate(date);
					// } else {
					// so.setDate(ccDate);
					// }
					//
					// stockreqsearchlist.add(so);
					// }
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return stockreqsearchlist;
	}

	public static ArrayList<SO> SearchTransfer(String fromdate, String todate,
											   String fromlocation, String tolocation, String transferstatus,
											   String webMethNameSet) {

		Log.d("from loc search", "fro " + fromlocation);

		Log.d("to loc search", "to " + tolocation);

		transfersearchlist.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo gsDate = new PropertyInfo();
		PropertyInfo geDate = new PropertyInfo();
		PropertyInfo fromLoc = new PropertyInfo();
		PropertyInfo toLoc = new PropertyInfo();

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		gsDate.setName("FromDate");
		gsDate.setValue(fromdate);
		gsDate.setType(String.class);
		request.addProperty(gsDate);

		geDate.setName("ToDate");
		geDate.setValue(todate);
		geDate.setType(String.class);
		request.addProperty(geDate);

		fromLoc.setName("FromLocation");
		fromLoc.setValue(fromlocation);
		fromLoc.setType(String.class);
		request.addProperty(fromLoc);

		toLoc.setName("ToLocation");
		toLoc.setValue(tolocation);
		toLoc.setType(String.class);
		request.addProperty(toLoc);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("soheader", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String ccSno = jsonChildNode.optString("TransferNo")
							.toString();
					String ccDate = jsonChildNode.optString("TransferDate")
							.toString();
					String fromloc = jsonChildNode.optString("FromLocation")
							.toString();
					String toloc = jsonChildNode.optString("ToLocation")
							.toString();
					String status = jsonChildNode.optString("Status")
							.toString();
					String remarks = jsonChildNode.optString("Remarks")
							.toString();

					Log.d("setSearchStatusCheck","-->"+status);
					Log.d("transferstatus", transferstatus);

					if (status.matches(transferstatus) || transferstatus.matches("2")) {
						SO so = new SO();
						so.setSno(ccSno);
						so.setFromlocation(fromloc);
						so.setTolocation(toloc);
						so.setRemarks1(remarks);
						Log.d("status", "status");

						if (status.matches("0")) {
							so.setStatus("open");
						}
//      else if (status.matches("1")) {
//       so.setStatus("view");
//      } else if (status.matches("2")) {
//       so.setStatus("partial");
//      }
						else if (status.matches("1")) {
							so.setStatus("Pending Transfer");
						}
//      else if (status.matches("4")) {
//       so.setStatus("deleted");
//      }
						else if (status.matches("2")) {
							so.setStatus("Closed");
						}

						if (ccDate != null) {
							StringTokenizer tokens = new StringTokenizer(
									ccDate, " ");
							String date = tokens.nextToken();
							so.setDate(date);
						} else {
							so.setDate(ccDate);
						}

						transfersearchlist.add(so);
					}

//					if (transferstatus.matches("2")) {
//						SO so = new SO();
//						so.setSno(ccSno);
//						so.setFromlocation(fromloc);
//						so.setTolocation(toloc);
//						so.setRemarks1(remarks);
//						Log.d("status", "status");
//
//						if (status.matches("0")) {
//							so.setStatus("open");
//						}
////      else if (status.matches("1")) {
////       so.setStatus("view");
////      } else if (status.matches("2")) {
////       so.setStatus("partial");
////      }
//						else if (status.matches("1")) {
//							so.setStatus("closed");
//						}
////      else if (status.matches("4")) {
////       so.setStatus("deleted");
////      }
//						else {
//							so.setStatus("open");
//						}
//
//						if (ccDate != null) {
//							StringTokenizer tokens = new StringTokenizer(
//									ccDate, " ");
//							String date = tokens.nextToken();
//							so.setDate(date);
//						} else {
//							so.setDate(ccDate);
//						}
//
//						transfersearchlist.add(so);
//					}
     /*else if (transferstatus.matches("5")) {

      SO so = new SO();
      so.setSno(ccSno);
      so.setFromlocation(fromloc);
      so.setTolocation(toloc);
      Log.d("status", "status");

      if (status.matches("0")) {
       so.setStatus("open");
      } else if (status.matches("1")) {
       so.setStatus("view");
      } else if (status.matches("2")) {
       so.setStatus("partial");
      } else if (status.matches("3")) {
       so.setStatus("closed");
      } else if (status.matches("4")) {
       so.setStatus("deleted");
      }

      if (ccDate != null) {
       StringTokenizer tokens = new StringTokenizer(
         ccDate, " ");
       String date = tokens.nextToken();
       so.setDate(date);
      } else {
       so.setDate(ccDate);
      }

      transfersearchlist.add(so);

     }*/

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return transfersearchlist;
	}
	public static ArrayList<SO> SearchReceipt(String cuscode, String sdate,
			String edate, String user, String webMethNameSet) {
		double totalamount = 0.00;
		SO.setTotalamount(totalamount);
		receiptsearchlist.clear();
		// String user = SupplierSetterGetter.getUsername();
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo supCode = new PropertyInfo();
		PropertyInfo sDate = new PropertyInfo();
		PropertyInfo eDate = new PropertyInfo();
		PropertyInfo suser = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		supCode.setName("CustomerCode");
		supCode.setValue(cuscode);
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

		suser.setName("User");
		suser.setValue(user);
		suser.setType(String.class);
		request.addProperty(suser);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("soheader", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String ccSno = jsonChildNode.optString("ReceiptNo")
							.toString();
					String ccDate = jsonChildNode.optString("ReceiptDate")
							.toString();
					String customerCode = jsonChildNode.optString(
							"CustomerCode").toString();
					String amount = jsonChildNode.optString("PaidAmount")
							.toString();

					SO so = new SO();
					so.setSno(ccSno);
					so.setCustomerCode(customerCode);
					so.setNettotal(amount);
					totalamount = totalamount + Double.valueOf(amount);
					/*
					 * if (ccDate != null) { DateFormat f = new
					 * SimpleDateFormat( "dd/MM/yyyy hh:mm:ss a"); Date d =
					 * f.parse(ccDate); DateFormat date = new
					 * SimpleDateFormat("dd/MM/yyyy");
					 * 
					 * so.setDate(date.format(d));
					 * 
					 * } else {
					 * 
					 * so.setDate(ccDate); }
					 */
					if (ccDate != null) {
						StringTokenizer tokens = new StringTokenizer(ccDate,
								" ");
						String date = tokens.nextToken();
						so.setDate(date);
					} else {
						so.setDate(ccDate);
					}
					receiptsearchlist.add(so);

				}
				SO.setTotalamount(totalamount);

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return receiptsearchlist;
	}

	public static ArrayList<HashMap<String, String>> getInvoiceDetails(
			String Inv_No, String webMethName) {
		String resTxt = null;
		INV_DetailsArr.clear();
		PropertyInfo INV_NoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		INV_NoPI.setName("InvoiceNo");
		INV_NoPI.setValue(Inv_No);
		INV_NoPI.setType(String.class);
		request.addProperty(INV_NoPI);

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

			result = " { INV_Details : " + resTxt + "}";
			Log.d("INV_Details search", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("INV_Details");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String slNo = jsonChildNode.optString("slNo").toString();
					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String ProductName = jsonChildNode.optString("ProductName")
							.toString();
					String CQty = jsonChildNode.optString("CQty").toString();
					String LQty = jsonChildNode.optString("LQty").toString();
					String Qty = jsonChildNode.optString("Qty").toString();
					String FOCQty = jsonChildNode.optString("FOCQty")
							.toString();
					String ExchangeQty = jsonChildNode.optString("ExchangeQty")
							.toString();
					String PcsPerCarton = jsonChildNode.optString(
							"PcsPerCarton").toString();
					String RetailPrice = jsonChildNode.optString("RetailPrice")
							.toString();
					String Price = jsonChildNode.optString("Price").toString();
					String CartonPrice = jsonChildNode.optString("CartonPrice")
							.toString();

					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDiscount = jsonChildNode.optString(
							"BillDiscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String TaxType = jsonChildNode.optString("TaxType")
							.toString();
					String TaxPerc = jsonChildNode.optString("TaxPerc")
							.toString();
					String ItemRemarks = jsonChildNode.optString("ItemRemarks")
							.toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("slNo", slNo);
					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", ProductName);
					hm.put("CQty", CQty);
					hm.put("LQty", LQty);
					hm.put("Qty", Qty);
					hm.put("FOCQty", FOCQty);
					hm.put("PcsPerCarton", PcsPerCarton);
					hm.put("RetailPrice", RetailPrice);
					hm.put("Price", Price);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDiscount", BillDiscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("TaxType", TaxType);
					hm.put("TaxPerc", TaxPerc);

					hm.put("CartonPrice", CartonPrice);
					hm.put("ItemRemarks", ItemRemarks);
					hm.put("ExchangeQty",ExchangeQty);
					INV_DetailsArr.add(hm);

					Log.d("INV_DetailList", INV_DetailsArr.toString());

				}


				Comparator<HashMap<String, String>> comparator = new Comparator<HashMap<String,String>>() {

					@Override
					public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
						// Get the distance and compare the distance.
						Integer distance1 = Integer.parseInt(o1.get("slNo"));
						Integer distance2 = Integer.parseInt(o2.get("slNo"));

						return distance1.compareTo(distance2);
					}
				};

				// And then sort it using collections.sort().
				Collections.sort(INV_DetailsArr, comparator);

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return INV_DetailsArr;
	}

	public static ArrayList<HashMap<String, String>> getGraDetails(
			String Gra_No, String webMethName) {
		String resTxt = null;
		Gra_DetailsArr.clear();
		PropertyInfo INV_NoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		INV_NoPI.setName("GRANo");
		INV_NoPI.setValue(Gra_No);
		INV_NoPI.setType(String.class);
		request.addProperty(INV_NoPI);

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

			result = " { Gra_Details : " + resTxt + "}";
			Log.d("Gra_Details", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("Gra_Details");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String slNo = jsonChildNode.optString("slNo").toString();
					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String ProductName = jsonChildNode.optString("ProductName")
							.toString();
					String CQty = jsonChildNode.optString("CQty").toString();
					String LQty = jsonChildNode.optString("LQty").toString();
					String Qty = jsonChildNode.optString("Qty").toString();
					String FOCQty = jsonChildNode.optString("FOCQty")
							.toString();
					String PcsPerCarton = jsonChildNode.optString(
							"PcsPerCarton").toString();
					String UnitPrice = jsonChildNode.optString("UnitPrice")
							.toString();
					String GrossPrice = jsonChildNode.optString("GrossPrice")
							.toString();
					String CartonPrice = jsonChildNode.optString("CartonPrice")
							.toString();
					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDiscount = jsonChildNode.optString(
							"BillDiscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String TaxType = jsonChildNode.optString("TaxType")
							.toString();
					String TaxPerc = jsonChildNode.optString("TaxPerc")
							.toString();
					String AverageCost = jsonChildNode.optString("AverageCost")
							.toString();
					String QtyOnHand = jsonChildNode.optString("QtyOnHand")
							.toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("slNo", slNo);
					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", ProductName);
					hm.put("CQty", CQty);
					hm.put("LQty", LQty);
					hm.put("Qty", Qty);
					hm.put("FOCQty", FOCQty);
					hm.put("PcsPerCarton", PcsPerCarton);
					hm.put("UnitPrice", UnitPrice);
					hm.put("GrossPrice", GrossPrice);
					hm.put("CartonPrice", CartonPrice);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDiscount", BillDiscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("TaxType", TaxType);
					hm.put("TaxPerc", TaxPerc);
					hm.put("AverageCost", AverageCost);
					hm.put("QtyOnHand", QtyOnHand);

					Gra_DetailsArr.add(hm);

					Log.d("Gra_ Detail List", Gra_DetailsArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return Gra_DetailsArr;
	}

	public static ArrayList<HashMap<String, String>> getInvoiceHeader(
			String Inv_No, String webMethName) {

		String resTxt = null;
		INV_HeaderArr.clear();
		PropertyInfo INV_NoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		INV_NoPI.setName("InvoiceNo");
		INV_NoPI.setValue(Inv_No);
		INV_NoPI.setType(String.class);
		request.addProperty(INV_NoPI);

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

			result = " { INV_Header : " + resTxt + "}";
			Log.d("INV_Header", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("INV_Header");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String Invoive_No="",Invoive_Date="";

					if(webMethName.matches("fncGetCashInvoiceHeader")){
						Invoive_No = jsonChildNode.optString("CashTranNo")
								.toString();
						Invoive_Date = jsonChildNode
								.optString("CashTranDate").toString();
					}else{
						Invoive_No = jsonChildNode.optString("InvoiceNo")
								.toString();
						Invoive_Date = jsonChildNode
								.optString("InvoiceDate").toString();
					}


					String LocationCode = jsonChildNode.optString(
							"LocationCode").toString();
					String CustomerCode = jsonChildNode.optString(
							"CustomerCode").toString();

					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDIscount = jsonChildNode.optString(
							"BillDIscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String Remarks = jsonChildNode.optString("Remarks")
							.toString();
					String CurrencyCode = jsonChildNode.optString(
							"CurrencyCode").toString();
					String CurrencyRate = jsonChildNode.optString(
							"CurrencyRate").toString();
					String BalanceAmount = jsonChildNode.optString(
							"BalanceAmount").toString();
					String isPacked = jsonChildNode.optString("isPacked").toString();
					String CreateDate = jsonChildNode.optString("CreateDate").toString();
					String SalesType = jsonChildNode.optString("SalesType").toString();
					String BillDiscountPercentage =jsonChildNode.optString("BillDiscountPercentage").toString();

					Log.d("BillDiscountPercentage","-->"+BillDiscountPercentage);

					if (!BalanceAmount.matches("")) {
						SalesOrderSetGet.setBalanceAmount(BalanceAmount);
					}
					if (!NetTotal.matches("")) {
						SalesOrderSetGet.setNetTotal(NetTotal);
					}

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("Invoive_No", Invoive_No);
					hm.put("Invoive_Date", Invoive_Date);
					hm.put("LocationCode", LocationCode);
					hm.put("CustomerCode", CustomerCode);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDIscount", BillDIscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("Remarks", Remarks);
					hm.put("CurrencyCode", CurrencyCode);
					hm.put("CurrencyRate", CurrencyRate);
					hm.put("isPacked", isPacked);
					hm.put("CreateDate",CreateDate);
					hm.put("SalesType",SalesType);
					hm.put("BillDiscountPercentage",BillDiscountPercentage);
					INV_HeaderArr.add(hm);
					SalesOrderSetGet.setType(Invoive_No);
					Log.d("INV_Header List", INV_HeaderArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return INV_HeaderArr;
	}
	
	public static ArrayList<HashMap<String, String>> getInvoiceHeaderDeliveryVerification(
			String Inv_No, String webMethName) {

		String resTxt = null;
		INV_HeaderArr.clear();
		PropertyInfo INV_NoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		INV_NoPI.setName("InvoiceNo");
		INV_NoPI.setValue(Inv_No);
		INV_NoPI.setType(String.class);
		request.addProperty(INV_NoPI);

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

			result = " { INV_Header : " + resTxt + "}";
			Log.d("INV_Header", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("INV_Header");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String Invoive_No = jsonChildNode.optString("InvoiceNo")
							.toString();
					String Invoive_Date = jsonChildNode
							.optString("InvoiceDate").toString();
					String LocationCode = jsonChildNode.optString(
							"LocationCode").toString();
					String CustomerCode = jsonChildNode.optString(
							"CustomerCode").toString();

					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDIscount = jsonChildNode.optString(
							"BillDIscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String Remarks = jsonChildNode.optString("Remarks")
							.toString();
					String CurrencyCode = jsonChildNode.optString(
							"CurrencyCode").toString();
					String CurrencyRate = jsonChildNode.optString(
							"CurrencyRate").toString();

					String BalanceAmount = jsonChildNode.optString(
							"BalanceAmount").toString();
					String isPacked = jsonChildNode.optString("isPacked").toString();
					
					if (!BalanceAmount.matches("")) {
						SalesOrderSetGet.setBalanceAmount(BalanceAmount);
					}
					if (!NetTotal.matches("")) {
						SalesOrderSetGet.setNetTotal(NetTotal);
					}

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("InvoiceNo", Invoive_No);
					hm.put("InvoiceDate", Invoive_Date);
					hm.put("LocationCode", LocationCode);
					hm.put("CustomerCode", CustomerCode);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDIscount", BillDIscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("Remarks", Remarks);
					hm.put("CurrencyCode", CurrencyCode);
					hm.put("CurrencyRate", CurrencyRate);
					hm.put("isPacked", isPacked);
					
					INV_HeaderArr.add(hm);

					Log.d("INV_Header List", INV_HeaderArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return INV_HeaderArr;
	}

	public static ArrayList<HashMap<String, String>> getGraHeader(
			String Gra_No, String webMethName) {

		String resTxt = null;
		Gra_HeaderArr.clear();
		PropertyInfo Gra_NoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		Gra_NoPI.setName("GRANo");
		Gra_NoPI.setValue(Gra_No);
		Gra_NoPI.setType(String.class);
		request.addProperty(Gra_NoPI);

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

			result = " { Gra_Header : " + resTxt + "}";
			Log.d("Gra_Header", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("Gra_Header");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String GRANo = jsonChildNode.optString("GRANo").toString();
					String GRADate = jsonChildNode.optString("GRADate")
							.toString();
					String InvoiceNo = jsonChildNode.optString("InvoiceNo")
							.toString();
					String InvoiceDate = jsonChildNode.optString("InvoiceDate")
							.toString();
					String DONo = jsonChildNode.optString("DONo").toString();
					String DODate = jsonChildNode.optString("DODate")
							.toString();
					String LocationCode = jsonChildNode.optString(
							"LocationCode").toString();
					String SupplierCode = jsonChildNode.optString(
							"SupplierCode").toString();
					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDIscount = jsonChildNode.optString(
							"BillDIscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String Remarks = jsonChildNode.optString("Remarks")
							.toString();
					String Status = jsonChildNode.optString("Status")
							.toString();
					String CurrencyCode = jsonChildNode.optString(
							"CurrencyCode").toString();
					String CurrencyRate = jsonChildNode.optString(
							"CurrencyRate").toString();
					String TransferNo = jsonChildNode.optString("TransferNo")
							.toString();
					String TransferLocation = jsonChildNode.optString(
							"TransferLocation").toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("GRANo", GRANo);
					hm.put("GRADate", GRADate);
					hm.put("InvoiceNo", InvoiceNo);
					hm.put("InvoiceDate", InvoiceDate);
					hm.put("DONo", DONo);
					hm.put("DODate", DODate);
					hm.put("LocationCode", LocationCode);
					hm.put("SupplierCode", SupplierCode);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDIscount", BillDIscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("Remarks", Remarks);
					hm.put("Status", Status);
					hm.put("CurrencyCode", CurrencyCode);
					hm.put("CurrencyRate", CurrencyRate);
					hm.put("TransferNo", TransferNo);
					hm.put("TransferLocation", TransferLocation);

					Gra_HeaderArr.add(hm);

					Log.d("Gra_Header List", Gra_HeaderArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return Gra_HeaderArr;
	}

	public static ArrayList<HashMap<String, String>> getGraBatch(String GraNo,
			String webMethName) {

		String resTxt = null;
		graBatchArr.clear();
		PropertyInfo number = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		number.setName("GRANo");
		number.setValue(GraNo);
		number.setType(String.class);
		request.addProperty(number);

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

			result = " { GRA_Batch : " + resTxt + "}";
			Log.d("GRA Batch", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("GRA_Batch");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String slNo = jsonChildNode.optString("slNo").toString();
					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String BatchNo = jsonChildNode.optString("BatchNo")
							.toString();
					String ExpiryDate = jsonChildNode.optString("ExpiryDate")
							.toString();
					String Remarks = jsonChildNode.optString("Remarks")
							.toString();
					String CQty = jsonChildNode.optString("CQty").toString();
					String LQty = jsonChildNode.optString("LQty").toString();
					String Qty = jsonChildNode.optString("Qty").toString();
					String FOCQty = jsonChildNode.optString("FOCQty")
							.toString();
					String UnitCost = jsonChildNode.optString("UnitCost")
							.toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("slNo", slNo);
					hm.put("ProductCode", ProductCode);
					hm.put("BatchNo", BatchNo);
					hm.put("ExpiryDate", ExpiryDate);
					hm.put("Remarks", Remarks);
					hm.put("CQty", CQty);
					hm.put("LQty", LQty);
					hm.put("Qty", Qty);
					hm.put("FOCQty", FOCQty);
					hm.put("UnitCost", UnitCost);

					graBatchArr.add(hm);

					Log.d("graBatchArr List", graBatchArr.toString());
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return graBatchArr;
	}

	public static ArrayList<HashMap<String, String>> getProductBarcode(
			String no, String parameter, String webMethName) {

		Log.d("parameter", "" + parameter);
		Log.d("no", "" + no);

		String resTxt = null;
		BarcodeArr.clear();
		PropertyInfo number = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		number.setName(parameter);
		number.setValue(no);
		number.setType(String.class);
		request.addProperty(number);

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

			result = " { INV_Barcode : " + resTxt + "}";
			Log.d("INV_Barcode", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("INV_Barcode");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					/*
					 * String Invoice_No = jsonChildNode.optString("InvoiceNo")
					 * .toString();
					 */

					String slNo = jsonChildNode.optString("slNo").toString();
					String Prdcode = jsonChildNode.optString("ProductCode")
							.toString();
					String Prdname = jsonChildNode.optString("ProductName")
							.toString();
					String SeqNo = jsonChildNode.optString("SeqNo").toString();
					String WeightBarcode = jsonChildNode.optString(
							"WeightBarcode").toString();
					String Weight = jsonChildNode.optString("Weight")
							.toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					// hm.put("Invoive_No", Invoice_No);
					hm.put("slNo", slNo);
					hm.put("SeqNo", SeqNo);
					hm.put("ProductName", Prdname);
					hm.put("Weight", Weight);
					hm.put("ProductCode", Prdcode);
					hm.put("WeightBarcode", WeightBarcode);
					BarcodeArr.add(hm);

					Log.d("INV_BarcodeArr List", BarcodeArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return BarcodeArr;
	}

	public static ArrayList<String> getAllCustomer(String webMethNameGet) {
		customerall_Arr.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

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
			String result = " { customerall : " + resTxt + "}";
			JSONObject jsonResponse;
			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("customerall");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String CustomerCode = jsonChildNode.optString(
							"CustomerCode").toString();

					customerall_Arr.add(CustomerCode);

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return customerall_Arr;
	}

	public static ArrayList<HashMap<String, String>> getStockReqDetails(
			String stockreqno, String webMethName) {
		String resTxt = null;
		stockreqDetailsArr.clear();
		PropertyInfo srNoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		srNoPI.setName("StockReqNo");
		srNoPI.setValue(stockreqno);
		srNoPI.setType(String.class);
		request.addProperty(srNoPI);

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

			result = " { SRDetails : " + resTxt + "}";
			Log.d("SRDetails search", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("SRDetails");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String slNo = jsonChildNode.optString("SlNo").toString();
					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String ProductName = jsonChildNode.optString("ProductName")
							.toString();
					String CQty = jsonChildNode.optString("ReqCQty").toString();
					String LQty = jsonChildNode.optString("ReqLQty").toString();
					String Qty = jsonChildNode.optString("ReqQty").toString();
					String PcsPerCarton = jsonChildNode.optString(
							"PcsPerCarton").toString();
					String TransferQty = jsonChildNode.optString("TransferQty")
							.toString();
					String Price = jsonChildNode.optString("AverageCost")
							.toString();

					Double dcQty = new Double(CQty);
					Double dlQty = new Double(LQty);
					Double dqty = new Double(Qty);
					Double tqty = new Double(TransferQty);
					Double dpcsPerCarton = new Double(PcsPerCarton);

					int cQty = dcQty.intValue();
					int lQty = dlQty.intValue();
					int qty = dqty.intValue();
					int pcsPerCarton = dpcsPerCarton.intValue();
					int transferqty = tqty.intValue();

					String calCarton = LogOutSetGet.getCalcCarton();
					if (calCarton.matches("0")) {

						int reqQty = cQty - transferqty;

						if (reqQty > 0) {

							SalesOrderSetGet.setStocktotransfer("True");

						} else {

							String str = SalesOrderSetGet.getStocktotransfer();

							if (str != null && !str.isEmpty()) {

								if (!str.matches("True")) {
									SalesOrderSetGet
											.setStocktotransfer("False");
								}

							} else {
								SalesOrderSetGet.setStocktotransfer("False");
							}
						}

					} else {

						int reqQty = qty - transferqty;

						cQty = reqQty / pcsPerCarton;
						lQty = reqQty % pcsPerCarton;

						Log.d("cqty", "" + cQty);
						Log.d("lqty", "" + lQty);

						if (reqQty > 0) {

							SalesOrderSetGet.setStocktotransfer("True");
							Log.d("stock transfer", "stock trans");
						} else {
							String str = SalesOrderSetGet.getStocktotransfer();

							Log.d("stock transfer", "tt " + str);

							if (str != null && !str.isEmpty()) {

								if (!str.matches("True")) {
									SalesOrderSetGet
											.setStocktotransfer("False");
								}

							} else {
								SalesOrderSetGet.setStocktotransfer("False");
							}
						}
					}

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("slNo", slNo);
					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", ProductName);
					hm.put("CQty", CQty);
					hm.put("LQty", LQty);
					hm.put("Qty", Qty);
					hm.put("PcsPerCarton", PcsPerCarton);
					hm.put("TransferQty", TransferQty);
					hm.put("Price", Price);

					stockreqDetailsArr.add(hm);

					Log.d("SR Detail List", stockreqDetailsArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return stockreqDetailsArr;
	}

	public static ArrayList<HashMap<String, String>> getStockReqHeader(
			String SrNo, String webMethName) {

		String resTxt = null;
		stockreqHeadersArr.clear();
		PropertyInfo srNoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		srNoPI.setName("StockReqNo");
		srNoPI.setValue(SrNo);
		srNoPI.setType(String.class);
		request.addProperty(srNoPI);

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

			result = " { SRHeader : " + resTxt + "}";
			Log.d("SRHeader", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("SRHeader");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String StockReqNo = jsonChildNode.optString("StockReqNo")
							.toString();
					String StockReqDate = jsonChildNode.optString(
							"StockReqDate").toString();

					String FromLocation = jsonChildNode.optString(
							"FromLocation").toString();
					String ToLocation = jsonChildNode.optString("ToLocation")
							.toString();

					String Remarks = jsonChildNode.optString("Remarks")
							.toString();
					String Status = jsonChildNode.optString("Status")
							.toString();

					Log.d("FromAndToLocationCheck",""+FromLocation+"  "+ToLocation);

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("StockReqNo", StockReqNo);
					hm.put("StockReqDate", StockReqDate);
					hm.put("FromLocation", ToLocation);
					hm.put("ToLocation", FromLocation);
					hm.put("Remarks", Remarks);
					hm.put("Status", Status);

					stockreqHeadersArr.add(hm);

					Log.d("SRHeader List", stockreqHeadersArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return stockreqHeadersArr;
	}

	public static ArrayList<HashMap<String, String>> getStockReqBatchDetail(
			String SrNo, String webMethName) {

		String resTxt = null;
		stockreqBatchDetailArr.clear();
		PropertyInfo srNoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		srNoPI.setName("StockReqNo");
		srNoPI.setValue(SrNo);
		srNoPI.setType(String.class);
		request.addProperty(srNoPI);

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

			result = " { SRStockreqBatchDetail : " + resTxt + "}";
			Log.d("SRStockreqBatchDetail", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SRStockreqBatchDetail");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String StockReqNo = jsonChildNode.optString(
							"StockRequestNo").toString();
					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String BatchNo = jsonChildNode.optString("BatchNo")
							.toString();
					String ExpiryDate = jsonChildNode.optString("ExpiryDate")
							.toString();
					String MfgDate = jsonChildNode.optString("MfgDate")
							.toString();
					String CQty = jsonChildNode.optString("CQty").toString();
					String LQty = jsonChildNode.optString("LQty").toString();
					String Qty = jsonChildNode.optString("Qty").toString();
					String slNo = jsonChildNode.optString("slNo").toString();
					String Remarks = jsonChildNode.optString("Remarks")
							.toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("StockReqNo", StockReqNo);
					hm.put("ProductCode", ProductCode);
					hm.put("BatchNo", BatchNo);
					hm.put("ExpiryDate", ExpiryDate);
					hm.put("MfgDate", MfgDate);
					hm.put("CQty", CQty);
					hm.put("LQty", LQty);
					hm.put("Qty", Qty);
					hm.put("SR_Slno", slNo);
					hm.put("Remarks", Remarks);

					stockreqBatchDetailArr.add(hm);
					Log.d("stockreqBatchDeta",
							stockreqBatchDetailArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return stockreqBatchDetailArr;
	}

	/** Expense start **/

	public static ArrayList<ProductDetails> getExpenseDetails(String expanseno,
			String webMethNameSet) {

		ArrayList<ProductDetails> detail = new ArrayList<ProductDetails>();
		detail.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo expenseno = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		expenseno.setName("ExpenseNo");
		expenseno.setValue(expanseno);
		expenseno.setType(String.class);
		request.addProperty(expenseno);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("Expense Detail", resTxt);
			String result = " { Details : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Details");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String code = jsonChildNode.optString("ExpenseAccountNo")
							.toString();
					String desc = jsonChildNode.optString("ExpenseDescription")
							.toString();
					String amount = jsonChildNode.optString("Amount")
							.toString();

					ProductDetails eDetail = new ProductDetails();
					eDetail.setDescription(desc);
					eDetail.setNettot(amount);
					detail.add(eDetail);

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return detail;
	}

	/** Expense End **/

	/** fncGetSOAdditionalInfo **/
	public static String getSOAdditionalInfo(String sono, String webMethNameSet) {

		String Result = "";

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo transno = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		transno.setName("Tranno");
		transno.setValue(sono);
		transno.setType(String.class);
		request.addProperty(transno);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("Additional info", resTxt);
			String result = " { Additionalinfo : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("Additionalinfo");
				int lengthJsonArr = jsonMainNode.length();
				// for (int i = 0; i < lengthJsonArr; i++) {
				if (lengthJsonArr > 0) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

					Result = jsonChildNode.optString("Result").toString();

					Log.d("additional info", "soaddi" + Result);
				}

				// }

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return Result;
	}

	public static ArrayList<HashMap<String, String>> getOutstandingSummary(
			String webMethName) {
		ArrayList<HashMap<String, String>> outstandingList = new ArrayList<HashMap<String, String>>();
		outstandingList.clear();
		String mResult = "";
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

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
			resTxt = response.toString();
			mResult = " { array : " + resTxt + "}";
			Log.d(" list", "" + mResult);
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(mResult);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("array");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String Customercode = jsonChildNode.optString(
							"Customercode").toString();
					String CustomerName = jsonChildNode.optString(
							"CustomerName").toString();
					String BalanceAmount = jsonChildNode.optString(
							"BalanceAmount").toString();

					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put("Customercode", Customercode);
					hm.put("CustomerName", CustomerName);
					hm.put("BalanceAmount", BalanceAmount);
					outstandingList.add(hm);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return outstandingList;

	}

	public static ArrayList<HashMap<String, String>> getOutstandingDetails(
			HashMap<String, String> hm, String webMethName) {
		ArrayList<HashMap<String, String>> outstandingList = new ArrayList<HashMap<String, String>>();
		outstandingList.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		for (HashMap.Entry<String, String> entry : hm.entrySet()) {

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
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		try {
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			custresult = " { SaleOCustomer : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(custresult);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SaleOCustomer");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String InvoiceNo = jsonChildNode.optString("InvoiceNo")
							.toString();
					String InvoiceDate = jsonChildNode.optString("InvoiceDate")
							.toString();
					String OverdueDays = jsonChildNode.optString("OverdueDays")
							.toString();
					String BalanceAmount = jsonChildNode.optString(
							"BalanceAmount").toString();

					String mdate = "";
					if (InvoiceDate != null && !InvoiceDate.isEmpty()) {
						StringTokenizer tokens = new StringTokenizer(
								InvoiceDate, " ");
						mdate = tokens.nextToken();
					} else {
						mdate = InvoiceDate;
					}

					HashMap<String, String> customerhm = new HashMap<String, String>();
					customerhm.put("InvoiceNo", InvoiceNo);
					customerhm.put("InvoiceDate", mdate);
					customerhm.put("OverdueDays", OverdueDays);
					customerhm.put("BalanceAmount", BalanceAmount);

					outstandingList.add(customerhm);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return outstandingList;

	}

	public static ArrayList<HashMap<String, String>> getAppMessage(
			HashMap<String, String> hm, String webMethName) {
		ArrayList<HashMap<String, String>> msgArr = new ArrayList<HashMap<String, String>>();
		msgArr.clear();
		int msgCount = 0;

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		for (HashMap.Entry<String, String> entry : hm.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);

			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			custresult = " { getmsg : " + resTxt + "}";
			JSONObject jsonResponse;

			jsonResponse = new JSONObject(custresult);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("getmsg");
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				String TranNo = jsonChildNode.optString("TranNo").toString();
				String SeqNo = jsonChildNode.optString("SeqNo").toString();
				String TranDate = jsonChildNode.optString("TranDate")
						.toString();
				String CompanyCode = jsonChildNode.optString("CompanyCode")
						.toString();
				String UserName = jsonChildNode.optString("UserName")
						.toString();
				String Message = jsonChildNode.optString("Message").toString();
				String Status = jsonChildNode.optString("Status").toString();

				if (Status.matches("0")) {
					msgCount = msgCount + 1;
				}

				HashMap<String, String> msgHM = new HashMap<String, String>();
				msgHM.put("TranNo", TranNo);
				msgHM.put("SeqNo", SeqNo);
				msgHM.put("TranDate", TranDate);
				msgHM.put("CompanyCode", CompanyCode);
				msgHM.put("UserName", UserName);
				msgHM.put("Message", Message);
				msgHM.put("Status", Status);
				msgArr.add(msgHM);
			}

			LogOutSetGet.setMsgCount(msgCount);

		} catch (Exception e) {
			e.printStackTrace();
			msgArr.clear();
		}
		return msgArr;

	}

	public static String updateAppMessage(HashMap<String, String> hm,
			String webMethName) {
		custresult = "";
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		for (HashMap.Entry<String, String> entry : hm.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);

			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			String mResult = " { AppMsg : " + resTxt + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(mResult);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("AppMsg");
				int lengthJsonArr = jsonMainNode.length();
				if (lengthJsonArr > 0) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

					String result = jsonChildNode.optString("Result")
							.toString();
					Log.d("app Msg", "-" + result);
					if (result.matches("True")) {
						custresult = result;
					} else {
						custresult = "";
					}
				}

			} catch (Exception e) {

				custresult = "";
			}

		} catch (Exception e) {
			custresult = "";

		}
		return custresult;
	}

	public static ArrayList<HashMap<String, String>> getTaskList(
			HashMap<String, String> param, String webMethName) {
		ArrayList<HashMap<String, String>> taskList = new ArrayList<HashMap<String, String>>();
		taskList.clear();
		String result = "";
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		for (HashMap.Entry<String, String> entry : param.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);

			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			result = " { Task : " + resTxt + "}";

			// {"CompanyCode":"1","TaskID":"5","TaskDate":"5/1/2016 12:00:00 AM","TaskUser":"aa","TaskName":"New Order","DueDate":"21/1/2016 12:00:00 AM",
			// "TaskPriority":"0","TaskStatus":"0","TaskDescription":"task has been assigned"}

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Task");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String TaskID = jsonChildNode.optString("TaskID")
							.toString();
					String TaskDate = jsonChildNode.optString("TaskDate")
							.toString();
					String TaskUser = jsonChildNode.optString("TaskUser")
							.toString();
					String TaskName = jsonChildNode.optString("TaskName")
							.toString();
					String DueDate = jsonChildNode.optString("DueDate")
							.toString();
					String TaskPriority = jsonChildNode.optString(
							"TaskPriority").toString();
					String TaskStatus = jsonChildNode.optString("TaskStatus")
							.toString();
					String TaskDescription = jsonChildNode.optString(
							"TaskDescription").toString();

					// "CompanyCode":"1","TaskID":"5","TaskDate":"5/1/2016 12:00:00 AM","TaskUser":"aa","TaskName":"New Order",
					// "DueDate":"21/1/2016 12:00:00 AM","TaskPriority":"0","TaskStatus":"0","TaskDescription":"task has been assigned"}

					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put("TaskID", TaskID);
					hm.put("TaskUser", TaskUser);
					hm.put("TaskDate", TaskDate);
					hm.put("TaskName", TaskName);
					hm.put("DueDate", DueDate);
					hm.put("TaskPriority", TaskPriority);
					hm.put("TaskStatus", TaskStatus);
					hm.put("TaskDescription", TaskDescription);
					taskList.add(hm);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			result = "Error occured";
		}
		return taskList;

	}

	public static String saveCrmTask(HashMap<String, String> hm,
			String webMethName) {
		custresult = "";
		resTxt = "";
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		for (HashMap.Entry<String, String> entry : hm.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);

			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			String mResult = " { tsak : " + resTxt + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(mResult);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("tsak");
				int lengthJsonArr = jsonMainNode.length();
				if (lengthJsonArr > 0) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

					String result = jsonChildNode.optString("Result")
							.toString();
					Log.d("CRM Task", "->" + result);
					if (result.matches("True")) {
						custresult = result;
					} else {
						custresult = "";
					}
				}

			} catch (Exception e) {

				custresult = "";
			}

		} catch (Exception e) {
			custresult = "";

		}
		return custresult;
	}

	public static String getCustomerOutstandingAmount(String customercode,
			String webMethNameGet, String taxCodetxt) throws JSONException {
		String outstandingAmt = "", CreditLimit = "",mTaxCode="",mTaxPerc="",mTaxType="",mDiscountPercentage="";

		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo moduleName = new PropertyInfo();
		PropertyInfo moduleNeedOutstanding = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		moduleName.setName("CustomerCode");
		moduleName.setValue(customercode);
		moduleName.setType(String.class);
		request.addProperty(moduleName);

		if(taxCodetxt.matches("")){
			moduleNeedOutstanding.setName("NeedOutstandingAmount");
			moduleNeedOutstanding.setValue("1");
			moduleNeedOutstanding.setType(String.class);
			request.addProperty(moduleNeedOutstanding);
		}else{

		}

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
			String result = " { Output : " + resTxt + "}";
			Log.d("Result", result);
			JSONObject jsonResponse;
			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Output");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					outstandingAmt = jsonChildNode.optString(
							"OutstandingAmount").toString();

					CreditLimit = jsonChildNode.optString("CreditLimit")
							.toString();

					mTaxCode = jsonChildNode.optString("TaxCode")
							.toString();

					mTaxType= jsonChildNode.optString("TaxType")
							.toString();

					mTaxPerc= jsonChildNode.optString("TaxValue")
							.toString();

					mDiscountPercentage =jsonChildNode.optString("DiscountPercentage").toString();

					Log.d("ValidDiscount","->"+mDiscountPercentage);
					CustomerSetterGetter.setDiscountPercentage(mDiscountPercentage);


				}

				if(taxCodetxt.matches("getTaxCode")){
					SalesOrderSetGet.setTaxCode(mTaxCode);
					SalesOrderSetGet.setTaxType(mTaxType);
					SalesOrderSetGet.setTaxPerc(mTaxPerc);
				}

				Log.d("outstandingAmt", outstandingAmt);
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return outstandingAmt + "," + CreditLimit;
	}
	
	public static ArrayList<SO> getDeliveryVerficationList(HashMap<String, String> hashValue,String webMethNameSet) {

		  sosearchlist.clear();
		  SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		  for (HashMap.Entry<String, String> entry : hashValue.entrySet()) {

		   PropertyInfo prodCode = new PropertyInfo();

		   prodCode.setName(entry.getKey());
		   prodCode.setValue(entry.getValue());
		   prodCode.setType(String.class);
		   request.addProperty(prodCode);
		   System.out.printf("%s", entry.getKey());
		   System.out.printf("%s%n", entry.getValue());
		  }

		  try {
		   SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
		     SoapEnvelope.VER11);
		   envelope.dotNet = true;
		   envelope.bodyOut = request;
		   envelope.setOutputSoapObject(request);
		   HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		   androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
		   SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		   resTxt = response.toString();
		   Log.d("soheader", resTxt);
		   String result = " { Search : " + resTxt + "}";
		   JSONObject jsonResponse;
		   try {
		    jsonResponse = new JSONObject(result);
		    JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
		    int lengthJsonArr = jsonMainNode.length();
		    for (int i = 0; i < lengthJsonArr; i++) {
		     JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

		//"SoNo":"SO2016-000026","SoDate":"17/03/2016 00:00:00","DeliveryDate":"17/03/2016 00:00:00","LocationCode":"HQ",
		//"CustomerCode":"10023","CustomerName":"abc marine","Total":"34.00","ItemDiscount":"0.00","BillDIscount":"0.00",
		//"TotalDiscount":"0.00","SubTotal":"34.00","Tax":"2.3800","NetTotal":"36.38","Remarks":"","Status":"0",
		//"CurrencyCode":"SGD","CurrencyRate":"1.000000000","VanCode":"","ModifyUser":null,"isPacked":"0","isQtyVerified":""},     
		     
		     String ccSno = jsonChildNode.optString("InvoiceNo").toString();
		     String ccDate = jsonChildNode.optString("InvoiceDate").toString();
		     String customerCode = jsonChildNode.optString("CustomerCode").toString();
		     String customerName = jsonChildNode.optString("CustomerName").toString();
		     String amount = jsonChildNode.optString("NetTotal").toString();
		     String statusValue = jsonChildNode.optString("isPacked")
		       .toString();
		     // Log.d("status", status);
		     Log.d("statusValue", statusValue);

		     SO so = new SO();
		     // Show all values
		      so.setSno(ccSno);
		      so.setCustomerCode(customerCode);
		      so.setCustomerName(customerName);
		      so.setNettotal(amount);

		      if (statusValue.matches("1")) {
		       so.setStatus("Verified");
		      } else {
		       so.setStatus("Not Verified");
		      }

		      if (ccDate != null && !ccDate.isEmpty()) {
		       StringTokenizer tokens = new StringTokenizer(ccDate, " ");
		       String date = tokens.nextToken();
		       so.setDate(date);
		      } else {
		       so.setDate(ccDate);
		      }

		      sosearchlist.add(so);
		     }
		    

		   } catch (JSONException e) {

		    e.printStackTrace();
		   }

		  } catch (Exception e) {

		   e.printStackTrace();
		  }

		  return sosearchlist;
		 }
	
	public static ArrayList<HashMap<String, String>> getDeliveryVerificationDetails(String soNo,
			   String webMethName) {
			  String resTxt = null;
			  SODetailsArr.clear();
			  PropertyInfo soNoPI = new PropertyInfo();
			  PropertyInfo locationcode = new PropertyInfo();
			  SoapObject request = new SoapObject(NAMESPACE, webMethName);

			  PropertyInfo companyCode = new PropertyInfo();

			  String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			  String location = SalesOrderSetGet.getLocationcode();
			  companyCode.setName("CompanyCode");
			  companyCode.setValue(cmpnyCode);
			  companyCode.setType(String.class);
			  request.addProperty(companyCode);

			  soNoPI.setName("InvoiceNo");
			  soNoPI.setValue(soNo);
			  soNoPI.setType(String.class);
			  request.addProperty(soNoPI);
//			  
//			  locationcode.setName("LocationCode");
//			  locationcode.setValue(location);
//			  locationcode.setType(String.class);
//			  request.addProperty(locationcode);

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

			   result = " { SODetails : " + resTxt + "}";
			   Log.d("SODetails search", result);

			   JSONObject jsonResponse;

			   try {
			    jsonResponse = new JSONObject(result);
			    JSONArray jsonMainNode = jsonResponse.optJSONArray("SODetails");

			    int lengthJsonArr = jsonMainNode.length();
			    for (int i = 0; i < lengthJsonArr; i++) {
			     JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

			     String slNo = jsonChildNode.optString("slNo").toString();
			     String ProductCode = jsonChildNode.optString("ProductCode")
			       .toString();
			     String ProductName = jsonChildNode.optString("ProductName")
			       .toString();
			     String CQty = jsonChildNode.optString("CQty").toString();
			     String LQty = jsonChildNode.optString("LQty").toString();
			     String Qty = jsonChildNode.optString("Qty").toString();
			     String FOCQty = jsonChildNode.optString("FOCQty")
			       .toString();
			     String PcsPerCarton = jsonChildNode.optString(
			       "PcsPerCarton").toString();
			     String RetailPrice = jsonChildNode.optString("RetailPrice")
			       .toString();
			     String Price = jsonChildNode.optString("Price").toString();
			     String Total = jsonChildNode.optString("Total").toString();
			     String ItemDiscount = jsonChildNode.optString(
			       "ItemDiscount").toString();
			     String BillDiscount = jsonChildNode.optString(
			       "BillDiscount").toString();
			     String TotalDiscount = jsonChildNode.optString(
			       "TotalDiscount").toString();
			     String SubTotal = jsonChildNode.optString("SubTotal")
			       .toString();
			     String Tax = jsonChildNode.optString("Tax").toString();
			     String NetTotal = jsonChildNode.optString("NetTotal")
			       .toString();
			     String TaxType = jsonChildNode.optString("TaxType")
			       .toString();
			     String TaxPerc = jsonChildNode.optString("TaxPerc")
			       .toString();

			     String ExchangeQty = jsonChildNode.optString("ExchangeQty")
			       .toString();
			     String CartonPrice = jsonChildNode.optString("CartonPrice")
			       .toString();
			     String MinimumSellingPrice = jsonChildNode.optString(
			       "MinimumSellingPrice").toString();
			     String ItemRemarks = jsonChildNode.optString("ItemRemarks")
			       .toString();

			     String InvoiceQty = jsonChildNode.optString("InvoiceQty")
			       .toString();
			     String InvoiceFocQty = jsonChildNode.optString(
			       "InvoiceFocQty").toString();

			     String DOQty = jsonChildNode.optString("DOQty").toString();
			     String DOFocQty = jsonChildNode.optString("DOFocQty")
			       .toString();

			     String QtyOnHand = jsonChildNode.optString("QtyOnHand").toString();
			     
			     if (MinimumSellingPrice.matches("null")) {
			      MinimumSellingPrice = "0.00";
			     }
			     
			     String OriginalCQty = jsonChildNode.optString("CQty").toString();
			     String OriginalQty = jsonChildNode.optString("Qty").toString();
			     
			     if (OriginalCQty !=null && !OriginalCQty.isEmpty()) {
			     }else{
			      OriginalCQty = "0";
			     }
			     
			     if (OriginalQty !=null && !OriginalQty.isEmpty()) {
			     }else{
			      OriginalQty = "0";
			     }
			     
//			     "OriginalCQty":"1.00","OriginalQty":"12.000"
			     String IsPacked = jsonChildNode.optString("IsPacked").toString();
			     
			     HashMap<String, String> hm = new HashMap<String, String>();

			     hm.put("slNo", slNo);
			        hm.put("ProductCode", ProductCode);
			        hm.put("ProductName", ProductName);
			        hm.put("CQty", CQty);
			        hm.put("LQty", LQty);
			        hm.put("Qty", Qty);
			        hm.put("FOCQty", FOCQty);
			        hm.put("PcsPerCarton", PcsPerCarton);
			        hm.put("RetailPrice", RetailPrice);
			        hm.put("Price", Price);
			        hm.put("Total", Total);
			        hm.put("ItemDiscount", ItemDiscount);
			        hm.put("BillDiscount", BillDiscount);
			        hm.put("TotalDiscount", TotalDiscount);
			        hm.put("SubTotal", SubTotal);
			        hm.put("Tax", Tax);
			        hm.put("NetTotal", NetTotal);
			        hm.put("TaxType", TaxType);
			        hm.put("TaxPerc", TaxPerc);

			     hm.put("ExchangeQty", ExchangeQty);
			     hm.put("CartonPrice", CartonPrice);
			     hm.put("MinimumSellingPrice", MinimumSellingPrice);
			     hm.put("ItemRemarks", ItemRemarks);

			     hm.put("InvoiceQty", InvoiceQty);
			     hm.put("InvoiceFocQty", InvoiceFocQty);

			     hm.put("DOQty", DOQty);
			     hm.put("DOFocQty", DOFocQty);
			     hm.put("QtyOnHand", QtyOnHand);
			     
			     hm.put("OriginalCQty", OriginalCQty);
			     hm.put("OriginalQty", OriginalQty);
			     
			     hm.put("IsPacked", IsPacked);
			     
			     SODetailsArr.add(hm);

			     Log.d("getDeliveryVerification", SODetailsArr.toString());

			    }

			   } catch (JSONException e) {

			    e.printStackTrace();
			   }

			  } catch (Exception e) {
			   e.printStackTrace();
			   resTxt = "Error occured";
			  }

			  return SODetailsArr;
			 }
	
	public static ArrayList<SO> getPackedHeaderList(HashMap<String, String> hashValue,String webMethNameSet, String status) {

		  sosearchlist.clear();
		  SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		  for (HashMap.Entry<String, String> entry : hashValue.entrySet()) {

		   PropertyInfo prodCode = new PropertyInfo();

		   prodCode.setName(entry.getKey());
		   prodCode.setValue(entry.getValue());
		   prodCode.setType(String.class);
		   request.addProperty(prodCode);
		   System.out.printf("%s", entry.getKey());
		   System.out.printf("%s%n", entry.getValue());
		  }

		  try {
		   SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
		     SoapEnvelope.VER11);
		   envelope.dotNet = true;
		   envelope.bodyOut = request;
		   envelope.setOutputSoapObject(request);
		   HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		   androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
		   SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		   resTxt = response.toString();
		   Log.d("soheader", resTxt);
		   String result = " { Search : " + resTxt + "}";
		   JSONObject jsonResponse;
		   try {
		    jsonResponse = new JSONObject(result);
		    JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
		    int lengthJsonArr = jsonMainNode.length();
		    for (int i = 0; i < lengthJsonArr; i++) {
		     JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
		     String statusValue="";
		//"SoNo":"SO2016-000026","SoDate":"17/03/2016 00:00:00","DeliveryDate":"17/03/2016 00:00:00","LocationCode":"HQ",
		//"CustomerCode":"10023","CustomerName":"abc marine","Total":"34.00","ItemDiscount":"0.00","BillDIscount":"0.00",
		//"TotalDiscount":"0.00","SubTotal":"34.00","Tax":"2.3800","NetTotal":"36.38","Remarks":"","Status":"0",
		//"CurrencyCode":"SGD","CurrencyRate":"1.000000000","VanCode":"","ModifyUser":null,"isPacked":"0","isQtyVerified":""},     
		     
		     String ccSno = jsonChildNode.optString("InvoiceNo").toString();
		     String ccDate = jsonChildNode.optString("InvoiceDate").toString();
		     String customerCode = jsonChildNode.optString("CustomerCode").toString();
		     String customerName = jsonChildNode.optString("CustomerName").toString();
		     String remarks = jsonChildNode.optString("Remarks").toString();
		     String statusStr = jsonChildNode.optString("isPacked").toString();
		      Log.d("statusStr", "->"+statusStr);
		      
		      if (remarks!=null && !remarks.isEmpty()){
		      }else{
		    	  remarks=" - ";
		      }  
		      
		      
		      if (statusStr.equals("null") || statusStr.matches("0")) {
		    	  statusValue="0";
		      } else if(statusStr.matches("1")){
		    	  statusValue="1";
		      }else{
		    	  if (statusStr!=null && !statusStr.isEmpty()){
		    		  statusValue="2";
			      }else{
			    	  statusValue="0";
			      }  
		      }
		      Log.d("statusValue", statusValue);
		      Log.d("status", status);
		      
		      SO so = new SO();

			if (status.matches(statusValue)) {
				// Based on status it show individual values
				  so.setSno(ccSno);
			      so.setCustomerCode(customerCode);
			      so.setCustomerName(customerName);
			      so.setRemarks1(remarks);
			      
			      if (statusValue.matches("0")) {
						so.setStatus("Open");
					} else if (statusValue.matches("1")) {
						so.setStatus("In Progress");
					} else if (statusValue.matches("2")) {
						so.setStatus("Packed");
					}
			      
			      if (ccDate != null && !ccDate.isEmpty()) {
				       StringTokenizer tokens = new StringTokenizer(ccDate, " ");
				       String date = tokens.nextToken();
				       so.setDate(date);
				      } else {
				       so.setDate(ccDate);
				      }
			      
				      sosearchlist.add(so);
				      
			} else if (status.matches("3")) {
				 // Show all values
				 so.setSno(ccSno);
			      so.setCustomerCode(customerCode);
			      so.setCustomerName(customerName);
			      so.setRemarks1(remarks);
			      
			      if (statusValue.matches("0")) {
						so.setStatus("Open");
					} else if (statusValue.matches("1")) {
						so.setStatus("In Progress");
					} else if (statusValue.matches("2")) {
						so.setStatus("Packed");
					}
			      
			      if (ccDate != null && !ccDate.isEmpty()) {
				       StringTokenizer tokens = new StringTokenizer(ccDate, " ");
				       String date = tokens.nextToken();
				       so.setDate(date);
				      } else {
				       so.setDate(ccDate);
				      }

				      sosearchlist.add(so);
		     }else{
		    	 
		    	 if(status!=null && !status.isEmpty()){
		    		 
		    	 }else{
		    	// Show open & inprogress values
		    	 if(statusValue.matches("0") || statusValue.matches("1")){
		    	 so.setSno(ccSno);
			      so.setCustomerCode(customerCode);
			      so.setCustomerName(customerName);
			      so.setRemarks1(remarks);
			      
			      if (statusValue.matches("0")) {
						so.setStatus("Open");
					} else if (statusValue.matches("1")) {
						so.setStatus("In Progress");
					} else if (statusValue.matches("2")) {
						so.setStatus("Packed");
					}
			      
			      if (ccDate != null && !ccDate.isEmpty()) {
				       StringTokenizer tokens = new StringTokenizer(ccDate, " ");
				       String date = tokens.nextToken();
				       so.setDate(date);
				      } else {
				       so.setDate(ccDate);
				      }

				      sosearchlist.add(so);
		    	 }
		    	 }
		     }
		    
		    }

		   } catch (JSONException e) {

		    e.printStackTrace();
		   }

		  } catch (Exception e) {

		   e.printStackTrace();
		  }

		  return sosearchlist;
		 }
	
	public static ArrayList<HashMap<String, String>> getCustomerPriceMultiple(HashMap<String, String> hm, String webMethName) {
		  customerGroupCodeArr.clear();
		  resTxt = "";
		  SoapObject request = new SoapObject(NAMESPACE, webMethName);

		  for (HashMap.Entry<String, String> entry : hm.entrySet()) {

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
		   envelope.bodyOut = request;
		   envelope.setOutputSoapObject(request);
		   HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		   try {
		    androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		    SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		    resTxt = response.toString();
		    custresult = " { SaleOCustomer : " + resTxt + "}";
		    JSONObject jsonResponse;
		    try {
		     jsonResponse = new JSONObject(custresult);
		     JSONArray jsonMainNode = jsonResponse
		       .optJSONArray("SaleOCustomer");
		     int lengthJsonArr = jsonMainNode.length();
		     for (int i = 0; i < lengthJsonArr; i++) {
		      JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

		      String CustomerCode = jsonChildNode.optString("CustomerCode")
		        .toString();
		      String CustomerGroupCode = jsonChildNode.optString("CustomerGroupCode")
		        .toString();
		      
		      HashMap<String, String> customerhm = new HashMap<String, String>();
		      customerhm.put(CustomerCode, CustomerGroupCode);
		      customerGroupCodeArr.add(customerhm);
		     }
		    } catch (JSONException e) {

		     e.printStackTrace();
		    }

		   } catch (Exception e) {
		    e.printStackTrace();
		    resTxt = "Error occured";
		   }

		  return customerGroupCodeArr;
		 }

	public static ArrayList<HashMap<String, String>> getConsignmentHeader(String doNo,String trantype,String webMethName) {

		String resTxt = null;
		DOHeaderArr.clear();
		PropertyInfo doNoPI = new PropertyInfo();


		Log.d("tranTypeCheck","-->"+trantype);

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		PropertyInfo tranType =new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		doNoPI.setName("ConsignmentNo");
		doNoPI.setValue(doNo);
		doNoPI.setType(String.class);
		request.addProperty(doNoPI);

//
		tranType.setName("TranType");
		tranType.setValue(trantype);
		tranType.setType(String.class);
		request.addProperty(tranType);


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

			result = " { CGHeader : " + resTxt + "}";
			Log.d("CGHeader", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("CGHeader");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String DoNo = jsonChildNode.optString("ConsignmentNo").toString();
					String DoDate = jsonChildNode.optString("ConsignmentDate")
							.toString();

					Log.d("ConsignmentNoDate",DoNo+"  "+DoDate);
					String LocationCode = jsonChildNode.optString(
							"LocationCode").toString();
					String CustomerCode = jsonChildNode.optString(
							"CustomerCode").toString();

					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDIscount = jsonChildNode.optString(
							"BillDIscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
//					String Total ="0.00";
//					String ItemDiscount ="0.00";
//					String BillDIscount ="0.00";
//					String TotalDiscount ="0.00";
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String Remarks = jsonChildNode.optString("Remarks")
							.toString();
					String Status = jsonChildNode.optString("Status")
							.toString();
					String CurrencyCode = jsonChildNode.optString(
							"CurrencyCode").toString();
					String CurrencyRate = jsonChildNode.optString(
							"CurrencyRate").toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("DoNo", DoNo);
					hm.put("DoDate", DoDate);
					hm.put("LocationCode", LocationCode);
					hm.put("CustomerCode", CustomerCode);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDIscount", BillDIscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("Remarks", Remarks);
					hm.put("Status", Status);
					hm.put("CurrencyCode", CurrencyCode);
					hm.put("CurrencyRate", CurrencyRate);
					SalesOrderSetGet.setType(DoNo);
					Log.d("setName","-->"+SalesOrderSetGet.getName());
					DOHeaderArr.add(hm);

					Log.d("DOHeaderList", DOHeaderArr.toString() +"hmValues :"+hm);

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return DOHeaderArr;
	}

	public static ArrayList<HashMap<String, String>> getConsignmentDetails(String doNo,String trantype, String webMethName) {

		String resTxt = null;
		DODetailsArr.clear();
		PropertyInfo soNoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		PropertyInfo tranType =new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		Log.d("cmpnyCode","-->"+cmpnyCode);
		Log.d("Sosno","-->"+doNo);
		Log.d("trantype","-->"+trantype);

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		soNoPI.setName("ConsignmentNo");
		soNoPI.setValue(doNo);
		soNoPI.setType(String.class);
		request.addProperty(soNoPI);

		tranType.setName("TranType");
		tranType.setValue(trantype);
		tranType.setType(String.class);
		request.addProperty(tranType);

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

			result = " { CGDetails : " + resTxt + "}";
			Log.d("CGDetails search", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("CGDetails");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String slNo = jsonChildNode.optString("slNo").toString();
					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String ProductName = jsonChildNode.optString("ProductName")
							.toString();
					String CQty = jsonChildNode.optString("CQty").toString();
					String LQty = jsonChildNode.optString("LQty").toString();
					String Qty = jsonChildNode.optString("Qty").toString();
					String FOCQty = jsonChildNode.optString("FOCQty")
							.toString();
					String PcsPerCarton = jsonChildNode.optString(
							"PcsPerCarton").toString();
					String RetailPrice = jsonChildNode.optString("RetailPrice")
							.toString();
					String Price = jsonChildNode.optString("Price").toString();
					String Total = jsonChildNode.optString("Total").toString();
					String ItemDiscount = jsonChildNode.optString(
							"ItemDiscount").toString();
					String BillDiscount = jsonChildNode.optString(
							"BillDiscount").toString();
					String TotalDiscount = jsonChildNode.optString(
							"TotalDiscount").toString();
//					String Total ="0.00";
//					String ItemDiscount ="0.00";
//					String BillDiscount ="0.00";
//					String TotalDiscount ="0.00";
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();
					String TaxType = jsonChildNode.optString("TaxType")
							.toString();
					String TaxPerc = jsonChildNode.optString("TaxPerc")
							.toString();
					String ExchangeQty = jsonChildNode.optString("ExchangeQty")
							.toString();
					String CartonPrice = jsonChildNode.optString("CartonPrice")
							.toString();
					String MinimumSellingPrice = jsonChildNode.optString(
							"MinimumSellingPrice").toString();

					String ItemRemarks = jsonChildNode.optString("ItemRemarks")
							.toString();
					String avg_cost = jsonChildNode.optString("AverageCost")
							.toString();
					SalesOrderSetGet.setAvg_cost(avg_cost);

					if (MinimumSellingPrice.matches("null")) {
						MinimumSellingPrice = "0";
					}
					Log.d("TotalCalculate","-->"+Total+"SubTotal :"+SubTotal+"NetTotal :"+NetTotal);
					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("slNo", slNo);
					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", ProductName);
					hm.put("CQty", CQty);
					hm.put("LQty", LQty);
					hm.put("Qty", Qty);
					hm.put("FOCQty", FOCQty);
					hm.put("PcsPerCarton", PcsPerCarton);
					hm.put("RetailPrice", RetailPrice);
					hm.put("Price", Price);
					hm.put("Total", Total);
					hm.put("ItemDiscount", ItemDiscount);
					hm.put("BillDiscount", BillDiscount);
					hm.put("TotalDiscount", TotalDiscount);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("TaxType", TaxType);
					hm.put("TaxPerc", TaxPerc);

					hm.put("ExchangeQty", ExchangeQty);
					hm.put("CartonPrice", CartonPrice);
					hm.put("MinimumSellingPrice", MinimumSellingPrice);
					hm.put("ItemRemarks", ItemRemarks);
//					hm.put("AverageCost",avg_cost);
					DODetailsArr.add(hm);
					Log.d("DO Detail List", DODetailsArr.toString());
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return DODetailsArr;
	}

	public static ArrayList<HashMap<String,String>> getStockConsignmentArrs(String webMethNameGet, String customerCodes, String sales_prodCode) {
		int SlNo;
		String c_Qty="",l_Qty="",Qty="";
		Log.d("customerCodes", "-->" + customerCodes+" "+sales_prodCode +" stockProductConsignmentArr :"+stockProductConsignmentArr.size());
		stockProductConsignmentArr.clear();
			SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
			PropertyInfo companyCode = new PropertyInfo();
			PropertyInfo customerCode = new PropertyInfo();
			PropertyInfo prodCode = new PropertyInfo();

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			customerCode.setName("CustomerCode");
			customerCode.setValue(customerCodes);
			customerCode.setType(String.class);
			request.addProperty(customerCode);

			prodCode.setName("ProductCode");
			prodCode.setValue(sales_prodCode);
			prodCode.setType(String.class);
			request.addProperty(prodCode);

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
				String result = " { LocalCurrency : " + resTxt + "}";
				JSONObject jsonResponse;
				try {

					jsonResponse = new JSONObject(result);
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("LocalCurrency");
					int lengthJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {
						JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
						String productCode = jsonChildNode.optString(
								"ProductCode").toString();
						String productName = jsonChildNode.optString(
								"ProductName").toString();
						String pcsPerCarton = jsonChildNode.optString(
								"PcsPerCarton").toString();
						String cQty = jsonChildNode.optString("CQty").toString();
						String lQty = jsonChildNode.optString("LQty").toString();
						String qty = jsonChildNode.optString("Qty").toString();
						String con_no = jsonChildNode.optString("ConsignmentNo").toString();
						String con_name = jsonChildNode.optString("ConsignmentDate").toString();
						String duration = jsonChildNode.optString("DurationInDays").toString();
						String con_expiry = jsonChildNode.optString("ConsignmentExpiry").toString();
						String cust_code = jsonChildNode.optString("CustomerCode").toString();
						String batchNo = jsonChildNode.optString("BatchNo").toString();
						String exp_date = jsonChildNode.optString("ExpiryDate").toString();
						String mfgDate = jsonChildNode.optString("MfgDate").toString();
						String uom = jsonChildNode.optString("UOMCode").toString();
						SlNo = jsonChildNode.optInt("slNo");


						Log.d("productCode", "" + productCode);
						Log.d("productName", "" + productName);
						Log.d("pcsPerCarton", "" + pcsPerCarton);
						Log.d("cQtyInsert", "" + cQty + " " + SlNo + " " + lQty + " " + qty);
						String slNo = String.valueOf(SlNo);
						int sl_no =Integer.parseInt(slNo);


						HashMap<String, String> currencyhm = new HashMap<String, String>();

//						Cursor cursor = SOTDatabase.getConsignmentStocks(productCode);
//						Log.d("SalesorderVAlue","-->"+SalesOrderSetGet.getType()+"  "+cursor.getCount());
//
//						c_Qty =cursor.getString(cursor
//								.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
//						l_Qty=cursor.getString(cursor
//								.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
//						Qty = cursor.getString(cursor
//								.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

						Log.d("QuantityEntity","-->"+c_Qty+"lQ"+l_Qty+"qty"+Qty);

						currencyhm.put("SlNo", slNo);
						currencyhm.put("productCode", productCode);
						currencyhm.put("productName", productName);
						currencyhm.put("pcsPerCarton", pcsPerCarton);
						currencyhm.put("cQty", cQty);
						currencyhm.put("lqty", lQty);
						currencyhm.put("qty",qty);
						currencyhm.put("con_no", con_no);
						currencyhm.put("con_name", con_name);
						currencyhm.put("duration", duration);
						currencyhm.put("con_expiry", con_expiry);
						currencyhm.put("cust_code", cust_code);
						currencyhm.put("batchNo", batchNo);
						currencyhm.put("exp_date", exp_date);
						currencyhm.put("mfgDate", mfgDate);
						currencyhm.put("uom", uom);
						currencyhm.put("AvailCQty", "0");
						currencyhm.put("AvailLQty","0");
						currencyhm.put("AvailQty", "0");
						currencyhm.put("BatchNo",batchNo);
						stockProductConsignmentArr.add(currencyhm);
						SalesOrderSetGet.setSl_no(sl_no);




						Log.d("customerCodes345", "-->" + stockProductConsignmentArr.size());



					}


				} catch (JSONException e) {

					e.printStackTrace();
				}

			} catch (Exception e) {

				e.printStackTrace();



		}
		return stockProductConsignmentArr;

	}

	public static ArrayList<HashMap<String,String>> getExpenseDetail(String expnseno, String webMethName) {
		String resTxt = null;
		EODetailsArr.clear();
		PropertyInfo soNoPI = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		String location = SalesOrderSetGet.getLocationcode();
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		soNoPI.setName("TranNo");
		soNoPI.setValue(expnseno);
		soNoPI.setType(String.class);
		request.addProperty(soNoPI);


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

			result = " { EODetails : " + resTxt + "}";
			Log.d("EODetailssearch", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("EODetails");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String slNo = jsonChildNode.optString("SlNo").toString();
					String TranNo = jsonChildNode.optString("TranNo")
							.toString();
					String GroupNo = jsonChildNode.optString("GroupNo")
							.toString();
					String GroupName = jsonChildNode.optString("GroupName").toString();
					String ItemRemarks = jsonChildNode.optString("ItemRemarks").toString();
					String Qty = jsonChildNode.optString("Qty").toString();
					String Price = jsonChildNode.optString("Price")
							.toString();
					String SubTotal = jsonChildNode.optString(
							"SubTotal").toString();
					String Tax = jsonChildNode.optString("Tax")
							.toString();
					String NetTotal = jsonChildNode.optString("NetTotal").toString();
					String TaxCode = jsonChildNode.optString("TaxCode").toString();
					String TaxPerc = jsonChildNode.optString(
							"TaxPerc").toString();
					String TranType = jsonChildNode.optString(
							"TranType").toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("slNo", slNo);
					hm.put("TranNo", TranNo);
					hm.put("GroupNo", GroupNo);
					hm.put("GroupName", GroupName);
					hm.put("ItemRemarks", ItemRemarks);
					hm.put("Qty", Qty);
					hm.put("Price", Price);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);
					hm.put("TaxCode", TaxCode);
					hm.put("TaxPerc", TaxPerc);
					hm.put("TranType", TranType);

					EODetailsArr.add(hm);

					Log.d("EODetailsArrList", EODetailsArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return EODetailsArr;
	}

	public static ArrayList<HashMap<String,String>> getExpenseHeader(String expnseno, String webMethName) {

		String resTxt = null;
		EOHeaderArr.clear();
		PropertyInfo soNoPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		soNoPI.setName("TranNo");
		soNoPI.setValue(expnseno);
		soNoPI.setType(String.class);
		request.addProperty(soNoPI);

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

			result = " { EOHeader : " + resTxt + "}";
			Log.d("EOHeader", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("EOHeader");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String TranNo = jsonChildNode.optString("TranNo")
							.toString();
					String TranDate = jsonChildNode.optString(
							"TranDate").toString();
					String TranType = jsonChildNode.optString(
							"TranType").toString();
					String LocationCode = jsonChildNode.optString(
							"LocationCode").toString();
					String ReferenceNo = jsonChildNode.optString(
							"ReferenceNo").toString();
					String PayTo = jsonChildNode.optString("PayTo").toString();
					String GroupNo = jsonChildNode.optString(
							"GroupNo").toString();
					String GroupName = jsonChildNode.optString(
							"GroupName").toString();
					String Remarks = jsonChildNode.optString(
							"Remarks").toString();
					String SubTotal = jsonChildNode.optString("SubTotal")
							.toString();
					String Tax = jsonChildNode.optString("Tax").toString();
					String NetTotal = jsonChildNode.optString("NetTotal")
							.toString();


					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("SoNo", TranNo);
					hm.put("TranDate", TranDate);
					hm.put("TranType", TranType);
					hm.put("LocationCode", LocationCode);
					hm.put("ReferenceNo", ReferenceNo);
					hm.put("PayTo", PayTo);
					hm.put("GroupNo", GroupNo);
					hm.put("GroupName", GroupName);
					hm.put("Remarks", Remarks);
					hm.put("SubTotal", SubTotal);
					hm.put("Tax", Tax);
					hm.put("NetTotal", NetTotal);

					EOHeaderArr.add(hm);

					Log.d("SOHeaderList", EOHeaderArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return EOHeaderArr;
	}

	public static List<CurrencyDeno> getSettlementHeaderList(HashMap<String, String> hm, String webMethNameSet) throws IOException, XmlPullParserException {
		settlementList.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		for (HashMap.Entry<String, String> entry : hm.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);
			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("soettlementheader", resTxt);
			String result = " { Search : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Search");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String settlementNo = jsonChildNode.optString("SettlementNo");
					String settolementDate = jsonChildNode.optString("SettlementDate");
					StringTokenizer tokens = new StringTokenizer(
							settolementDate, " ");
					String date = tokens.nextToken();
					String  totalAmt = jsonChildNode.optString("TotalAmount").toString();
					String SettlementBy = jsonChildNode.optString("SettlementBy").toString();

					CurrencyDeno deno = new CurrencyDeno();
					deno.setSettlementNo(settlementNo);
					deno.setSettlementBy(SettlementBy);
					deno.setSettlementDate(date);
					deno.setTotlAmt(totalAmt);
					settlementList.add(deno);


				}

			} catch (JSONException e) {

				e.printStackTrace();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return settlementList;
	}

	public static String getSettlementDetail(HashMap<String, String> hashValue, String webMethNameGet) {
		Log.d("webMethNameGet",webMethNameGet);
		Log.d("validURL","v "+validURL);

		String result = null;
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

		for (HashMap.Entry<String, String> entry : hashValue.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);
			System.out.printf(" key %s", entry.getKey());
			System.out.printf(" Value %s%n", entry.getValue());
		}
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
			result = " { SODetails : " + resTxt + "}";

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return result;
	}
}
