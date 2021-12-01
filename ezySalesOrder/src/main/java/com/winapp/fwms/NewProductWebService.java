package com.winapp.fwms;

import java.io.IOException;
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

import com.winapp.model.Barcode;

import static com.winapp.fwms.ValidateWebService.resTxt;

public class NewProductWebService {

	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	static String result;
	static String code = null;
	static ArrayList<String> proCodeList = new ArrayList<String>();
	static ArrayList<String> getProductCode = new ArrayList<String>();
	static int proId;
	static ArrayList<HashMap<String, String>> catArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> subCatArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> uomArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> searchProductArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> productStockArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> binArr= new ArrayList<HashMap<String, String>>();

	static ArrayList<String> editProductArr = new ArrayList<String>();
	static ArrayList<String> editBarcodeArr = new ArrayList<String>();
	static ArrayList<Barcode> barcodeArray = new ArrayList<>();

	String username = SupplierSetterGetter.getUsername();
	static boolean barcodeAssigned;
	static String validURL;
	static String value;
	static String resProd;
	static ArrayList<String> barcodeList = new ArrayList<String>();
	static String barCode_json = "", product_Json, strBarCode_json = "";
	static String updBarCode_json = "", updateProdJson,
			strUpdBarCode_json = "";

	public NewProductWebService(String url) {
		validURL = url;
	}

	public static ArrayList<HashMap<String, String>> categoryService(
			String webMethName) {

		catArr.clear();
		String resTxt = null;
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
			resTxt = response.toString();
			result = " { Category : " + resTxt + "}";
			JSONObject jsonResponse;

			try {

				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse.optJSONArray("Category");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String desc = jsonChildNode.optString("Description")
							.toString();

					String catCode = jsonChildNode.optString("CategoryCode")
							.toString();

					HashMap<String, String> producthm = new HashMap<String, String>();
					producthm.put(catCode, desc);
					catArr.add(producthm);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return catArr;
	}

	public static ArrayList<HashMap<String, String>> subCategoryService(
			String webMethName) {
		subCatArr.clear();
		String resTxt = null;
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
			resTxt = response.toString();
			result = " { subCategory : " + resTxt + "}";

			JSONObject jsonResponse;

			try {

				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("subCategory");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					/******* Fetch node values **********/
					String subCatCode = jsonChildNode.optString(
							"SubCategoryCode").toString();

					String desc = jsonChildNode.optString("Description")
							.toString();

					HashMap<String, String> producthm = new HashMap<String, String>();
					producthm.put(subCatCode, desc);
					subCatArr.add(producthm);

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return subCatArr;
	}

	public static ArrayList<HashMap<String,String>> binDetailService(String webMethName) {
		binArr.clear();
		String resTxt = null;
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
			resTxt = response.toString();
			result = " { binDetail : " + resTxt + "}";

			JSONObject jsonResponse;

			try {

				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("binDetail");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					/******* Fetch node values **********/
					String binCode = jsonChildNode.optString(
							"BinCode").toString();

					String desc = jsonChildNode.optString("BinName")
							.toString();

					HashMap<String, String> producthm = new HashMap<String, String>();
					producthm.put(binCode, desc);
					binArr.add(producthm);

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return binArr;
	}

	public static String addProductService(String webMethName, String pCode,
			String descStr, String strtsonStr, String endsonStr,
			String CtgryfldStr, String SubCtgryfldStr, String uomStr,
			String piecesprcrtnStr, boolean check_haveBatch,
			boolean check_haveExpire, boolean check_haveMFD,String weight, String cartonPrice, String units,String binCode)
			throws JSONException, IOException, XmlPullParserException {

		String supCode = SupplierSetterGetter.getSuppliercode();
		String date = SupplierSetterGetter.getDate();
		String username = SupplierSetterGetter.getUsername();

		if (strtsonStr.matches("") || endsonStr.matches("")) {
			strtsonStr = "0";
			endsonStr = "0";
			barcodeAssigned = false;
		} else {
			barcodeAssigned = true;
		}

		if (piecesprcrtnStr.matches("")) {
			piecesprcrtnStr = "1";
		}

		try{
			if(supCode.matches("null")){
				supCode="";
			}
		}catch(Exception e){
			supCode="";
		}
		
		product_Json = "{\"ProductCode\":\"" + pCode + "\",\"ProductName\":\""
				+ descStr + "\",\"CategoryCode\":\"" + CtgryfldStr
				+ "\",\"SubCategoryCode\":\"" + SubCtgryfldStr
				+ "\",\"SupplierCode\":\"" + supCode + "\",\"UOMCode\":\""
				+ uomStr + "\",\"PcsPerCarton\":\"" + piecesprcrtnStr
				+ "\",\"Weight\":" + weight + ",\"UnitCost\":" + "0"
				+ ",\"AverageCost\":" + "0" + ",\"RetailPrice\":" + "0"
				+ ",\"WholeSalePrice\":" + "0" + ",\"HaveBatch\":"
				+ check_haveBatch + ",\"HaveExpiry\":" + check_haveExpire
				+ ",\"HaveMfgDate\":" + check_haveMFD
				+ ",\"WeightBarcodeAssigned\":" + barcodeAssigned
				+ ",\"WeightBarcodeStartsOn\":\"" + strtsonStr
				+ "\",\"WeightBarcodeEndsOn\":\"" + endsonStr
				+ "\",\"IsActive\":true,\"CreateUser\":\"" + username
				+ "\",\"RetailPrice\":\""+cartonPrice
				+ "\",\"WholeSalePrice\":\""+units
				+ "\",\"CreateDate\":\"" + date + "\",\"ModifyUser\":\""
				+ username + "\",\"ModifyDate\":\"" + date
				+ "\",\"BinCode\":\"" + binCode + "\",\"ErrorMessage\":null}";

		Log.d("ProductJson", product_Json);
		// brcdArr.clear();
		barCode_json = "";
		strBarCode_json = "";

		brcdArr = NewProductWebService.getBarcodeArr();

		Log.d("brcdArr", brcdArr.toString());
		if (!barcodeArr.isEmpty()) {
			for (int i = 0; i < barcodeArr.size(); i++) {



			}
		/*	strBarCode_json = barCode_json.substring(0,
					barCode_json.length() - 1);
			Log.d("barCode_json", strBarCode_json);*/
		}

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo productJson = new PropertyInfo();
		PropertyInfo barcodeJson = new PropertyInfo();

		PropertyInfo companyCode = new PropertyInfo();
		
		String cmpnyCode=SupplierSetterGetter.getCompanyCode();
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		
		
		productJson.setName("Product");
		productJson.setValue(product_Json);
		productJson.setType(String.class);
		request.addProperty(productJson);

		barcodeJson.setName("Barcode");
		barcodeJson.setValue(strBarCode_json);
		barcodeJson.setType(String.class);
		request.addProperty(barcodeJson);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11); 
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		// Invoke web service
		androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		String resTxt = response.toString();
		Log.d("AddProduct", resTxt);
		result = " { AddProduct : " + resTxt + "}";
		JSONObject jsonResponse;

		try {

			jsonResponse = new JSONObject(result);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("AddProduct");
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				resProd = jsonChildNode.optString("Result").toString();
				Log.d("Add Product Result", resProd);
			}
	
		} catch (JSONException e) {

			e.printStackTrace();
		}
		barcodeArr.clear();
		return resProd;
	}

	public static ArrayList<HashMap<String, String>> uomService(
			String webMethName) {

		uomArr.clear();
		String resTxt = null;
		// Create request
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
			resTxt = response.toString();
			result = " { UOM : " + resTxt + "}";
			
			JSONObject jsonResponse;

			try {

				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse.optJSONArray("UOM");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					/******* Fetch node values **********/
					String desc = jsonChildNode.optString("Description")
							.toString();
					String uom = jsonChildNode.optString("UOMCode").toString();

					HashMap<String, String> producthm = new HashMap<String, String>();
					producthm.put(uom, desc);
					uomArr.add(producthm);
				}
				/************ Show Output on screen/activity **********/

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return uomArr;
	}

	public static ArrayList<String> getproductCode(String webMethName) {
		String resTxt = null;
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
			resTxt = response.toString();
			result = " { productcode : " + resTxt + "}";
			
			JSONObject jsonResponse;

			try {

				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("productcode");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					/******* Fetch node values **********/
					String productcode = jsonChildNode.optString("ProductCode")
							.toString();

					getProductCode.add(productcode);

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return getProductCode;
	}

	public static ArrayList<String> getEditProductService(String productCode,
			String webMethNameGet) {

		editProductArr.clear();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		
		PropertyInfo prodCode = new PropertyInfo();
		
		PropertyInfo companyCode = new PropertyInfo();
		
		String cmpnyCode=SupplierSetterGetter.getCompanyCode();
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		
		prodCode.setName("ProductCode");
		prodCode.setValue(productCode);
		prodCode.setType(String.class);
		request.addProperty(prodCode);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			value = response.toString();
			Log.d("Edit Product Value", value);
			result = " { EditProduct : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("EditProduct");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();

				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					/******* Fetch node values **********/
					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String ProductName = jsonChildNode.optString("ProductName")
							.toString();
					String WeightBarcodeStartsOn = jsonChildNode.optString(
							"WeightBarcodeStartsOn").toString();
					String WeightBarcodeEndsOn = jsonChildNode.optString(
							"WeightBarcodeEndsOn").toString();
					String HaveBatch = jsonChildNode.optString("HaveBatch")
							.toString();
					String HaveExpiry = jsonChildNode.optString("HaveExpiry")
							.toString();
					String HaveMfgDate = jsonChildNode.optString("HaveMfgDate")
							.toString();
					String CategoryCode = jsonChildNode.optString(
							"CategoryCode").toString();
					String SubCategoryCode = jsonChildNode.optString(
							"SubCategoryCode").toString();
					String UOMCode = jsonChildNode.optString("UOMCode")
							.toString();
					String PcsPerCarton = jsonChildNode.optString(
							"PcsPerCarton").toString();
					String weight = jsonChildNode.optString(
							"Weight").toString();
					
					String cartonPrice = jsonChildNode.optString(
							"RetailPrice").toString();
					String units = jsonChildNode.optString(
							"WholeSalePrice").toString();
					String bindetail =jsonChildNode.optString("BinDetail").toString();

					Log.d("ProductNameValue","-->"+ProductName);

					editProductArr.add(ProductCode);
					editProductArr.add(ProductName);
					editProductArr.add(WeightBarcodeStartsOn);
					editProductArr.add(WeightBarcodeEndsOn);
					editProductArr.add(HaveBatch);
					editProductArr.add(HaveExpiry);
					editProductArr.add(HaveMfgDate);
					editProductArr.add(CategoryCode);
					editProductArr.add(SubCategoryCode);
					editProductArr.add(UOMCode);
					editProductArr.add(weight);
					
					editProductArr.add(cartonPrice);
					editProductArr.add(units);
					
					if (PcsPerCarton.matches("null")) {
						editProductArr.add("1");
					} else {
						editProductArr.add(PcsPerCarton);
					}
					editProductArr.add(bindetail);

					Log.d("Edit Product Result", editProductArr.toString());
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return editProductArr;
	}

	public static ArrayList<String> getEditBarcodeService(String productCode,
			String webMethNameGet) {

		editBarcodeArr.clear();

		PropertyInfo prodCode = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

		PropertyInfo companyCode = new PropertyInfo();
		
		String cmpnyCode=SupplierSetterGetter.getCompanyCode();
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		
		prodCode.setName("ProductCode");
		prodCode.setValue(productCode);
		prodCode.setType(String.class);
		request.addProperty(prodCode);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			value = response.toString();
			Log.d("Edit Barcode Value", value);
			result = " { EditBarcode : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("EditBarcode");

				int lengthJsonArr = jsonMainNode.length();

				for (int i = 0; i < lengthJsonArr; i++) {
	
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String Barcode = jsonChildNode.optString("Barcode")
							.toString();
					String productCodes = jsonChildNode.optString("ProductCode");
					editBarcodeArr.add(Barcode);
					editProductArr.add(productCodes);

					Log.d("EditBarcode Result", editBarcodeArr.toString());
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return editBarcodeArr;
	}

	static String proCode;
	static ArrayList<String> barcodeArr = new ArrayList<String>();
	static ArrayList<String> brcdArr = new ArrayList<String>();

	static ArrayList<String> updBrcdArr = new ArrayList<String>();

	public static ArrayList<String> getBarcodeArr() {
		return barcodeArr;
	}

	public static void setBarcodeArr(ArrayList<String> barcodeArr) {
		NewProductWebService.barcodeArr = barcodeArr;
	}

	public static String getProCode() {
		return proCode;
	}

	public static void setProCode(String proCode) {
		NewProductWebService.proCode = proCode;
	}

	public static ArrayList<String> getBarcodeService(String webMethName) {
		String resTxt = null;
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
			resTxt = response.toString();
			result = " { Barcode : " + resTxt + "}";

			JSONObject jsonResponse;

			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Barcode");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
				
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String barcode = jsonChildNode.optString("Barcode")
							.toString();
					barcodeList.add(barcode);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return barcodeList;
	}

	public static String setBarcodeService(String webMethNameSet,
			String prodCode, String barcode) throws JSONException, IOException,
			XmlPullParserException {
		String resTxt = null;

		String username = SupplierSetterGetter.getUsername();
		String date = SupplierSetterGetter.getDate();

		String barCode_json = "{\"ProductCode\":\"" + prodCode + "\",\""
				+ "Barcode\":\"" + barcode + "\",\"CreateUser\":\"" + username
				+ "\",\"CreateDate\":\"" + date + "\",\"ModifyUser\":\""
				+ username + "\",\"ModifyDate\":\"" + date
				+ "\",\"ErrorMessage\":null}";
		Log.d("barCode_json", barCode_json);

		PropertyInfo jsonRequest = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);
		PropertyInfo companyCode = new PropertyInfo();		
		String cmpnyCode=SupplierSetterGetter.getCompanyCode();
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		
		jsonRequest.setName("Barcode");
		jsonRequest.setValue(barCode_json);
		jsonRequest.setType(String.class);
		request.addProperty(jsonRequest);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request); 
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		resTxt = response.toString();
		Log.d("SetBarCode", resTxt);

		return "Success";
	}

	public static String setWeightService(String webMethNameSet,
			String productCode, int startsOn, int endsOn, String wghtDcml) throws JSONException,
			IOException, XmlPullParserException {
		String resTxt = null;

		String stsOn = "" + startsOn;
		String edsOn = "" + endsOn;
		PropertyInfo productCodePI = new PropertyInfo();
		PropertyInfo startsOnPI = new PropertyInfo();
		PropertyInfo endsOnPI = new PropertyInfo();
		PropertyInfo wghtDecimalPI = new PropertyInfo();
		
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo companyCode = new PropertyInfo();
		
		String cmpnyCode=SupplierSetterGetter.getCompanyCode();
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		
		productCodePI.setName("ProductCode");
		productCodePI.setValue(productCode);
		productCodePI.setType(String.class);
		request.addProperty(productCodePI);

		startsOnPI.setName("WeightBarcodeStartsOn");
		startsOnPI.setValue(stsOn);
		startsOnPI.setType(String.class);
		request.addProperty(startsOnPI);

		endsOnPI.setName("WeightBarcodeEndsOn");
		endsOnPI.setValue(edsOn);
		endsOnPI.setType(String.class);
		request.addProperty(endsOnPI); 
		
		wghtDecimalPI.setName("WeightBarcodeDecimalPoints");
		wghtDecimalPI.setValue(wghtDcml);
		wghtDecimalPI.setType(String.class);
		request.addProperty(wghtDecimalPI);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11); 
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request); 
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
	
		resTxt = response.toString();
		Log.d("Set Starts Ends On", resTxt);

		return "Success";
	}

	public static ArrayList<HashMap<String, String>> searchProduct(
			String category, String subCategory, String webMethName) {
		String resTxt = null;
		searchProductArr.clear();
		PropertyInfo catPI = new PropertyInfo();
		PropertyInfo subCatPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();
		
		String cmpnyCode=SupplierSetterGetter.getCompanyCode();
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		
		catPI.setName("CategoryCode");
		catPI.setValue(category);
		catPI.setType(String.class);
		request.addProperty(catPI);

		subCatPI.setName("SubCategoryCode");
		subCatPI.setValue(subCategory);
		subCatPI.setType(String.class);
		request.addProperty(subCatPI);

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

			result = " { productSearch : " + resTxt + "}";
			Log.d("product search", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("productSearch");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String ProductName = jsonChildNode.optString("ProductName")
							.toString();
					String WholeSalePrice = jsonChildNode.optString(
							"WholeSalePrice").toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", ProductName);
					hm.put("WholeSalePrice", WholeSalePrice);
					hm.put("Qty", "0");
					searchProductArr.add(hm);

					Log.d("Product search List", searchProductArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return searchProductArr;
	}
	
	public static ArrayList<HashMap<String, String>> customerPriceProduct(HashMap<String, String> hmValue, String webMethName) {
		String resTxt = null;
		searchProductArr.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		  for (HashMap.Entry<String, String> entry : hmValue.entrySet()) {

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

			result = " { productSearch : " + resTxt + "}";
			Log.d("product search", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("productSearch");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String ProductName = jsonChildNode.optString("ProductName")
							.toString();
					String RetailPrice = jsonChildNode.optString(
							"RetailPrice").toString();

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", ProductName);
					hm.put("WholeSalePrice", RetailPrice);
					hm.put("Qty", "0");
					searchProductArr.add(hm);

					Log.d("Product search List", searchProductArr.toString());

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return searchProductArr;
	}
	
	public static ArrayList<HashMap<String, String>> getProductStock(String locationCode, String webMethName) {
		String resTxt = null;
		productStockArr.clear();

		PropertyInfo locPI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();
		
		String cmpnyCode=SupplierSetterGetter.getCompanyCode();
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);
		
		locPI.setName("LocationCode");
		locPI.setValue(locationCode);
		locPI.setType(String.class);
		request.addProperty(locPI);

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

			result = " { productstock : " + resTxt + "}";
			Log.d("product stock", result);

			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("productstock");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String ProductName = jsonChildNode.optString("ProductName")
							.toString();					
					String Qty = jsonChildNode.optString("Qty")
							.toString();				
					String WholeSalePrice = jsonChildNode.optString("WholeSalePrice")
							.toString();
					String UOMCode = jsonChildNode.optString("UOMCode")
							.toString();
					String PcsPerCarton = jsonChildNode.optString("PcsPerCarton")
							.toString();

					int qty = (int) (Double.parseDouble(Qty));
					
					if(qty>0){
					
					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", ProductName);
					hm.put("Qty", Qty);
					hm.put("WholeSalePrice", WholeSalePrice);
					hm.put("UOMCode", UOMCode);
					hm.put("PcsPerCarton", PcsPerCarton);
					
					productStockArr.add(hm);
					}
	
				}
				
				Log.d("Product Stock Arr", productStockArr.toString());

			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return productStockArr;
	}


	public static ArrayList<Barcode> getEditBarcode(String webMethNameSet) {

		barcodeArray.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo sBarcode = new PropertyInfo();
		PropertyInfo productCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		sBarcode.setName("Barcode");
		sBarcode.setValue("");
		sBarcode.setType(String.class);
		request.addProperty(sBarcode);

		productCode.setName("ProductCode");
		productCode.setValue("");
		productCode.setType(String.class);
		request.addProperty(productCode);

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
			Log.d("searchBarcode", resTxt);
			String result = " { Barcode : " + resTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("Barcode");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					// int sno=1+i;
					String barcodeNo = jsonChildNode.optString("Barcode")
							.toString();
					String barCodeProduct = jsonChildNode.optString("ProductCode")
							.toString();

					Barcode barcode = new Barcode();
					barcode.setBarcode(barcodeNo);
					barcode.setProductCode(barCodeProduct);


					barcodeArray.add(barcode);

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return barcodeArray;
	}
}