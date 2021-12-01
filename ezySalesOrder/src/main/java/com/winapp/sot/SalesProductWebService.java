package com.winapp.sot;

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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.printer.UIHelper;

public class SalesProductWebService {

	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String PRODUCT_NAME = "ProductName";
	private static String PRODUCT_PRICE = "WholeSalePrice";
	private static String PRODUCT_AVGCOST = "AverageCost";
	private static String PRODUCT_UOM = "UOMCode";
	private static String PRODUCT_CARTONPERQTY = "PcsPerCarton";
	private static String COMPANY_TAXTYPE = "TaxType";
	private static String COMPANY_TAXVALUE = "TaxValue";
	static ArrayList<String> productArr = new ArrayList<String>();
	static ArrayList<String> companyArr = new ArrayList<String>();
	static ArrayList<String> priceArr = new ArrayList<String>();
	static String value, result, slPrice = "", cPrice = "", settingvalue,
			crrncynm, crrncyrt;
	static String validURL;
	private static String COMPANY_NAME = "CompanyName";
	// private static String COMPANY_CODE = "CompanyCode";
	private static String COMPANY_ADDRESS1 = "Address1";
	private static String COMPANY_ADDRESS2 = "Address2";
	private static String COMPANY_COUNTRY = "Country";
	private static String COMPANY_PHONENUMBER = "PhoneNo";
	private static String COMPANY_ADDRESS3 = "Address3";
	private static String COMPANY_ZIPCODE = "ZipCode";

	static ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> currencycode = new ArrayList<HashMap<String, String>>();
	static String serverdate;
	static String custresult;
	static String suppTxt = null;
	static ArrayList<String> alclcrrncy = new ArrayList<String>();
	static ArrayList<String> cstmrgrpcdal = new ArrayList<String>();
	static ArrayList<String> alsaleorder = new ArrayList<String>();
	static HashMap<String, String> hashmap = new HashMap<String, String>();
	static ArrayList<SO> OverdueArr = new ArrayList<SO>();
	static Context context;

	private static UIHelper helper;

	public SalesProductWebService(String url) {
		validURL = url;
	}

	public SalesProductWebService(Context context) {
		SalesProductWebService.context = context;
		helper = new UIHelper(context);
	}

	public static ArrayList<String> getSaleProduct(String productCode,
			String webMethNameSet, String classname) {

		productArr.clear();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo prodCode = new PropertyInfo();

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

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
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			value = response.toString();
			Log.d("Sales Product Value", value);
			result = " { SaleProduct : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SaleProduct");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					/******* Fetch node values **********/
					
					String slCode = jsonChildNode.optString(PRODUCT_CODE)
						       .toString();
						     
					String slName = jsonChildNode.optString(PRODUCT_NAME)
						       .toString();
						     
					String slPrice = jsonChildNode.optString(PRODUCT_PRICE)
							.toString();

					String slUomCode = jsonChildNode.optString(PRODUCT_UOM)
							.toString();

					String slCartonPerQty = jsonChildNode.optString(
							PRODUCT_CARTONPERQTY).toString();

					String taxValue = jsonChildNode.optString("TaxPerc")
							.toString();

					String cartonprice = jsonChildNode.optString("RetailPrice")
							.toString();

					String MinimumSellingPrice = jsonChildNode.optString(
							"MinimumSellingPrice").toString();

					String MinimumCartonSellingPrice = jsonChildNode.optString(
							"MinimumCartonSellingPrice").toString();

					String haveBatch = jsonChildNode.optString("HaveBatch")
						       .toString();

					 String haveExpiry = jsonChildNode.optString("HaveExpiry")
						       .toString();
					 
					 String Weight = jsonChildNode.optString("Weight")
						       .toString();

					 String avg_cost =jsonChildNode.optString("AverageCost").toString();

					 String retail_price = jsonChildNode.optString("OutletPrice").toString();

					 Log.d("retailPriceValue",retail_price);

					 SalesOrderSetGet.setAvg_cost(avg_cost);

					productArr.add(slPrice);
					productArr.add(slUomCode);
					if (slCartonPerQty.matches("0")
							|| slCartonPerQty.matches("null")) {
						productArr.add("1");
					} else {	
	
//						slCartonPerQty = "12";
						String part1="", part2="";
						try{
							Log.d("point", "...");
							StringTokenizer tokens = new StringTokenizer(slCartonPerQty, ".");
							part1 = tokens.nextToken(); // 12
							part2 = tokens.nextToken(); // 000
							
							if(part2.matches("0") || part2.matches("00") || part2.matches("000") || part2.matches("0000")
									|| part2.matches("00000")){
								productArr.add(part1);
								
								Log.d("part1", ""+part1);
								Log.d("part2", ""+part2);
							}else{
								
								
								if(classname.matches("SalesAddProduct")){
									productArr.add(slCartonPerQty);
								}else{
									productArr.add(part1);
								}
								
							}
						}catch(Exception e){
							Log.d("no point", ""+slCartonPerQty);
							Log.d("part1", ""+part1);
							e.printStackTrace();
//							part1 = tokens.nextToken();
							productArr.add(part1);
							
							
						}
						
//						}

//						productArr.add(slCartonPerQty);
					}

					productArr.add(taxValue);
					productArr.add(cartonprice);
					productArr.add(MinimumSellingPrice);
					productArr.add(haveBatch);
				     productArr.add(haveExpiry);
				     productArr.add(slCode);       
			         productArr.add(slName); 
			         
			         productArr.add(Weight);
					productArr.add(MinimumCartonSellingPrice);
					productArr.add(avg_cost);
					productArr.add(retail_price);

					Log.d("SaleProductResult", productArr.toString());
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return productArr;
	}

	public static ArrayList<String> getProductPrice(String custGroupCode,
			String custCode, String ProCode, String webMethNameSet,
			String formCode) {

		priceArr.clear();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo formCodePI = new PropertyInfo();
		PropertyInfo custGroupCodePI = new PropertyInfo();
		PropertyInfo custCodePI = new PropertyInfo();
		PropertyInfo ProCodePI = new PropertyInfo();

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		formCodePI.setName("FormCode");
		formCodePI.setValue(formCode);
		formCodePI.setType(String.class);
		request.addProperty(formCodePI);

		custGroupCodePI.setName("CustomerGroupCode");
		custGroupCodePI.setValue(custGroupCode);
		custGroupCodePI.setType(String.class);
		request.addProperty(custGroupCodePI);

		custCodePI.setName("CustomerCode");
		custCodePI.setValue(custCode);
		custCodePI.setType(String.class);
		request.addProperty(custCodePI);

		ProCodePI.setName("ProductCode");
		ProCodePI.setValue(ProCode);
		ProCodePI.setType(String.class);
		request.addProperty(ProCodePI);

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
			Log.d("Product Price", value +" "+webMethNameSet);
			result = " { ProductPrice : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("ProductPrice");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					slPrice = jsonChildNode.optString("Price").toString();
					Log.d("Product Price Result", slPrice);
					cPrice = jsonChildNode.optString("CartonPrice").toString();

					if (slPrice.matches("null") || slPrice.matches("")
							|| slPrice.matches("0.0")
							|| slPrice.matches("0.00")) {
						priceArr.add("0");
					} else {
						priceArr.add(slPrice);
					}

					if (cPrice.matches("null") || cPrice.matches("")
							|| cPrice.matches("0.0") || cPrice.matches("0.00")) {
						priceArr.add("0");
					} else {
						priceArr.add(cPrice);
					}
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			if (priceArr.isEmpty()) {
				priceArr.add("0");
				priceArr.add("0");
			}

			// Log.d("Error", e.getMessage());
			// helper.showErrorDialog(Log.getStackTraceString(e));

			e.printStackTrace();

		}

		return priceArr;
	}

	public static ArrayList<String> getSuppProductPrice(String custGroupCode,
			String custCode, String ProCode, String webMethName) {
		priceArr.clear();
		PropertyInfo formCodePI = new PropertyInfo();
		PropertyInfo custGroupCodePI = new PropertyInfo();
		PropertyInfo custCodePI = new PropertyInfo();
		PropertyInfo ProCodePI = new PropertyInfo();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		PropertyInfo companyCode = new PropertyInfo();
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		formCodePI.setName("FormCode");
		formCodePI.setValue("");
		formCodePI.setType(String.class);
		request.addProperty(formCodePI);

		custGroupCodePI.setName("SupplierGroupCode");
		custGroupCodePI.setValue("");
		custGroupCodePI.setType(String.class);
		request.addProperty(custGroupCodePI);

		custCodePI.setName("SupplierCode");
		custCodePI.setValue(custCode);
		custCodePI.setType(String.class);
		request.addProperty(custCodePI);

		ProCodePI.setName("ProductCode");
		ProCodePI.setValue(ProCode);
		ProCodePI.setType(String.class);
		request.addProperty(ProCodePI);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			value = response.toString();
			Log.d("SUPProduct Price", value);
			result = " { ProductPrice : " + value + "}";
			Log.d("SUPProduct Price", result);
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("ProductPrice");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					slPrice = jsonChildNode.optString("Price").toString();
					cPrice = jsonChildNode.optString("CartonPrice").toString();

					if (slPrice.matches("null") || slPrice.matches("")
							|| slPrice.matches("0.0")
							|| slPrice.matches("0.00")) {
						priceArr.add("0");
					} else {
						priceArr.add(slPrice);
					}
					if (cPrice.matches("null") || cPrice.matches("")
							|| cPrice.matches("0.0") || cPrice.matches("0.00")) {
						priceArr.add("0");
					} else {
						priceArr.add(cPrice);
					}

				}
				Log.d("custCode", custCode);
				Log.d("ProCode", ProCode);
				Log.d("Product Price Result", slPrice);
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			if (priceArr.isEmpty()) {
				priceArr.add("0");
				priceArr.add("0");
			}
			e.printStackTrace();
		}

		return priceArr;
	}

	public static ArrayList<String> getCompany(String webMethNameSet) {

		companyArr.clear();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);
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
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			value = response.toString();
//			Log.d("Get Company Value", value);
			result = " { Company : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse.optJSONArray("Company");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String TaxType = jsonChildNode.optString(COMPANY_TAXTYPE)
							.toString();
					String TaxValue = jsonChildNode.optString(COMPANY_TAXVALUE)
							.toString();
					String CompanyName = jsonChildNode.optString(COMPANY_NAME)
							.toString();

					String CompanyAddress1 = jsonChildNode.optString(
							COMPANY_ADDRESS1).toString();
					String CompanyAddress2 = jsonChildNode.optString(
							COMPANY_ADDRESS2).toString();
					String CompanyCountry = jsonChildNode.optString(
							COMPANY_COUNTRY).toString();
					String CompanyPhoneno = jsonChildNode.optString(
							COMPANY_PHONENUMBER).toString();
					String CompanyAddress3 = jsonChildNode.optString(
							COMPANY_ADDRESS3).toString();
					String CompanyZipCode = jsonChildNode.optString(
							COMPANY_ZIPCODE).toString();



					String fax = jsonChildNode.optString("FaxNo").toString();
					String email = jsonChildNode.optString("Email").toString();
					
					String TaxRegNo = jsonChildNode.optString("TaxRegNo").toString();
					String BusinessRegNo = jsonChildNode.optString("BusinessRegNo").toString();

					String AndroidVersion_SFA = jsonChildNode.optString("AndroidVersion_SFA").toString();

					String AndroidVersion_SFA_CheckPlayStore = jsonChildNode.optString("AndroidVersion_SFA_CheckPlayStore").toString();
					String shortCode =jsonChildNode.optString("ShortCode").toString();
					String taxCode =jsonChildNode.optString("TaxCode").toString();
					SalesOrderSetGet.setShortCode(shortCode);
					companyArr.add(TaxType);
					companyArr.add(TaxValue);
					companyArr.add(CompanyName);
					companyArr.add(CompanyAddress1);
					companyArr.add(CompanyAddress2);
					companyArr.add(CompanyAddress3);
					companyArr.add(CompanyCountry);
					companyArr.add(CompanyZipCode);
					companyArr.add(CompanyPhoneno);
					companyArr.add(fax);
					companyArr.add(email);
					companyArr.add(TaxRegNo);
					companyArr.add(BusinessRegNo);
					companyArr.add(AndroidVersion_SFA);
					companyArr.add(AndroidVersion_SFA_CheckPlayStore);
					companyArr.add(shortCode);
					companyArr.add(taxCode);
					Company.setShortCode(shortCode);
					Log.d("CompanyResult", companyArr.toString());
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return companyArr;
	}

	public static ArrayList<String> getGraProduct(String productCode, String webMethNameSet) {

		productArr.clear();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo prodCode = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

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
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			value = response.toString();
			Log.d("Sales Product Value", value);
			result = " { SaleProduct : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SaleProduct");

				/* Process each JSON Node */
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/* Get Object for each JSON node. **** */
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					/* Fetch node values ** */
					
					String slCode = jsonChildNode.optString(PRODUCT_CODE)
							.toString();
					
					String slName = jsonChildNode.optString(PRODUCT_NAME)
							.toString();
					
					String slPrice = jsonChildNode.optString(PRODUCT_AVGCOST)
							.toString();

					String slUomCode = jsonChildNode.optString(PRODUCT_UOM)
							.toString();

					String slCartonPerQty = jsonChildNode.optString(
							PRODUCT_CARTONPERQTY).toString();

					String taxValue = jsonChildNode.optString("TaxPerc")
							.toString();

					String haveBatch = jsonChildNode.optString("HaveBatch")
							.toString();

					String haveExpiry = jsonChildNode.optString("HaveExpiry")
							.toString();
					
					 String Weight = jsonChildNode.optString("Weight")
						       .toString();

					String cartonprice = jsonChildNode.optString("RetailPrice")
							.toString();

					String MinimumSellingPrice = jsonChildNode.optString(
							"MinimumSellingPrice").toString();

					String MinimumCartonSellingPrice = jsonChildNode.optString(
							"MinimumCartonSellingPrice").toString();
					
					productArr.add(slPrice);
					productArr.add(slUomCode);
					
					if (slCartonPerQty.matches("0")
							|| slCartonPerQty.matches("null")) {
						productArr.add("1");
						// returnpcs="1";
					} else {
						String part1 = "", part2 = "";
						try {
							Log.d("point", "...");
							StringTokenizer tokens = new StringTokenizer(
									slCartonPerQty, ".");
							part1 = tokens.nextToken(); // 12
							part2 = tokens.nextToken(); // 000
							productArr.add(part1);
							// returnpcs= part1;

						} catch (Exception e) {
							Log.d("no point", "" + slCartonPerQty);
							Log.d("part1", "" + part1);
							e.printStackTrace();
							productArr.add(part1);
							// returnpcs= part1;

						}

					}
//					productArr.add(slCartonPerQty);
					productArr.add(taxValue);
					productArr.add(haveBatch);
					productArr.add(haveExpiry);
					productArr.add(slCode);
					productArr.add(slName);
					productArr.add(Weight);

					productArr.add(cartonprice);
					productArr.add(MinimumSellingPrice);
					productArr.add(MinimumCartonSellingPrice);
					productArr.add(""+(i+1));
					Log.d("SaleProductResult", productArr.toString());
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return productArr;
	}

	public static ArrayList<HashMap<String, String>> getProductBatchStock(
			String productCode, String webMethNameSet) {

		al.clear();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo prodCode = new PropertyInfo();
		PropertyInfo locCode = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		// String locationCode = SalesOrderSetGet.getTransferfromloc();
		// String locationCode = SalesOrderSetGet.getLocationcode();
	

		HashMap<String, String> location_code_name = new HashMap<String, String>();
		location_code_name = SupplierSetterGetter.getLoc_code_name();
		String fromlocationname = SalesOrderSetGet.getTransferfromloc();
		String locationCode = location_code_name.get(fromlocationname);
		Log.d("from locationname", "->" + fromlocationname);
		
		
		String haveBatchOnTransfer = SalesOrderSetGet.getHaveBatchOnTransfer();
		if(haveBatchOnTransfer!=null && !haveBatchOnTransfer.isEmpty()){
			
		}else{
			haveBatchOnTransfer="";
		}
		if (haveBatchOnTransfer.matches("False")){
			locationCode = SupplierSetterGetter.getLocCode();
		}
		Log.d(" locationCode", "->" + locationCode);
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		locCode.setName("LocationCode");
		locCode.setValue(locationCode);
		locCode.setType(String.class);
		request.addProperty(locCode);

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
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			value = response.toString();
			Log.d("Sales Product Value", value);
			result = " { SaleProduct : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SaleProduct");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					HashMap<String, String> hashmap = new HashMap<String, String>();
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String tLocationCode = jsonChildNode.optString(
							"LocationCode").toString();

					String tProductCode = jsonChildNode
							.optString("ProductCode").toString();

					String tBatchNo = jsonChildNode.optString("BatchNo")
							.toString();

					String dateTime = jsonChildNode.optString("ExpiryDate")
							.toString();
					StringTokenizer tokens = new StringTokenizer(dateTime, " ");
					String tExpiryDate = tokens.nextToken();

					// String tExpiryDate =
					// jsonChildNode.optString("ExpiryDate")
					// .toString();

					String tMfgDate = jsonChildNode.optString("MfgDate")
							.toString();

					String tPcsPerCarton = jsonChildNode.optString(
							"PcsPerCarton").toString();

					String tQty = jsonChildNode.optString("Qty").toString();
					
					String NoOfCarton = jsonChildNode.optString("NoOfCarton").toString();
					
					
					double cartonqty=0, dqty=0;
					
					if(!tQty.matches("")){
						dqty =  Double.parseDouble(tQty);
					}
					
					String calCarton = LogOutSetGet.getCalcCarton();
					if(calCarton.matches("0")){
						
						if(!NoOfCarton.matches("")){
							cartonqty = Double.parseDouble(NoOfCarton);
							
							if(cartonqty>0 || dqty>0){
								
								int iQty = (int) dqty;
								hashmap.put("LocationCode", tLocationCode);
								hashmap.put("ProductCode", tProductCode);
								hashmap.put("BatchNo", tBatchNo);
								hashmap.put("ExpiryDate", tExpiryDate);
								hashmap.put("MfgDate", tMfgDate);
								hashmap.put("PcsPerCarton", tPcsPerCarton);
								hashmap.put("Qty", iQty + "");
								hashmap.put("NoOfCarton", NoOfCarton);

								al.add(hashmap);

							}
							}
					}else{
						if(dqty>0){
							int iQty = (int) dqty;
							hashmap.put("LocationCode", tLocationCode);
							hashmap.put("ProductCode", tProductCode);
							hashmap.put("BatchNo", tBatchNo);
							hashmap.put("ExpiryDate", tExpiryDate);
							hashmap.put("MfgDate", tMfgDate);
							hashmap.put("PcsPerCarton", tPcsPerCarton);
							hashmap.put("Qty", iQty + "");
							hashmap.put("NoOfCarton", NoOfCarton);

							al.add(hashmap);
						}
					}
					
					
					Log.d("fncGetProductBatchStock", al.toString());
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return al;
	}
	
	public static ArrayList<HashMap<String, String>> getProductBatchStockAdjustment(
			String productCode, String webMethNameSet) {

		al.clear();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo prodCode = new PropertyInfo();
		PropertyInfo locCode = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		// String locationCode = SalesOrderSetGet.getTransferfromloc();
		String locationCode = SalesOrderSetGet.getLocationcode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		locCode.setName("LocationCode");
		locCode.setValue(locationCode);
		locCode.setType(String.class);
		request.addProperty(locCode);

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
			androidHttpTransport.call(SOAP_ACTION + webMethNameSet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			value = response.toString();
			Log.d("Sales Product Value", value);
			result = " { SaleProduct : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SaleProduct");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					HashMap<String, String> hashmap = new HashMap<String, String>();
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String tLocationCode = jsonChildNode.optString(
							"LocationCode").toString();

					String tProductCode = jsonChildNode
							.optString("ProductCode").toString();

					String tBatchNo = jsonChildNode.optString("BatchNo")
							.toString();

					String dateTime = jsonChildNode.optString("ExpiryDate")
							.toString();
					StringTokenizer tokens = new StringTokenizer(dateTime, " ");
					String tExpiryDate = tokens.nextToken();

					String tMfgDate = jsonChildNode.optString("MfgDate")
							.toString();

					String tPcsPerCarton = jsonChildNode.optString(
							"PcsPerCarton").toString();

					String tQty = jsonChildNode.optString("Qty").toString();
					
					String NoOfCarton = jsonChildNode.optString("NoOfCarton").toString();

					
					double cartonqty=0, dqty=0;
					
					if(!tQty.matches("")){
						dqty =  Double.parseDouble(tQty);
					}
					
					String calCarton = LogOutSetGet.getCalcCarton();
					if(calCarton.matches("0")){
						
						if(!NoOfCarton.matches("")){
							cartonqty = Double.parseDouble(NoOfCarton);
							
							if(cartonqty>0 || dqty>0){
								
								int iQty = (int) dqty;
								hashmap.put("LocationCode", tLocationCode);
								hashmap.put("ProductCode", tProductCode);
								hashmap.put("BatchNo", tBatchNo);
								hashmap.put("ExpiryDate", tExpiryDate);
								hashmap.put("MfgDate", tMfgDate);
								hashmap.put("PcsPerCarton", tPcsPerCarton);
								hashmap.put("Qty", iQty + "");
								hashmap.put("NoOfCarton", NoOfCarton);

								al.add(hashmap);

							}
							}
					}else{
						if(dqty>0){
							int iQty = (int) dqty;
							hashmap.put("LocationCode", tLocationCode);
							hashmap.put("ProductCode", tProductCode);
							hashmap.put("BatchNo", tBatchNo);
							hashmap.put("ExpiryDate", tExpiryDate);
							hashmap.put("MfgDate", tMfgDate);
							hashmap.put("PcsPerCarton", tPcsPerCarton);
							hashmap.put("Qty", iQty + "");
							hashmap.put("NoOfCarton", NoOfCarton);

							al.add(hashmap);
						}
					}										
				}
				
				Log.d("fncGetProductBatchStock", al.toString());

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return al;
	}

	public static ArrayList<HashMap<String, String>> getProductBatchReturn(
			String productCode,String customercode,String invoiceno, String webMethNameSet) {

		al.clear();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo prodCode = new PropertyInfo();
//		PropertyInfo locCode = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo InvoiceNo= new PropertyInfo();
		PropertyInfo CustomerCode= new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		// String locationCode = SalesOrderSetGet.getTransferfromloc();
		String locationCode = SalesOrderSetGet.getLocationcode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

//		locCode.setName("LocationCode");
//		locCode.setValue(locationCode);
//		locCode.setType(String.class);
//		request.addProperty(locCode);

		prodCode.setName("ProductCode");
		prodCode.setValue(productCode);
		prodCode.setType(String.class);
		request.addProperty(prodCode);

		InvoiceNo.setName("InvoiceNo");
		InvoiceNo.setValue(invoiceno);
		InvoiceNo.setType(String.class);
		request.addProperty(InvoiceNo);

		CustomerCode.setName("CustomerCode");
		CustomerCode.setValue(customercode);
		CustomerCode.setType(String.class);
		request.addProperty(CustomerCode);

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
			Log.d("Sales Product Value", value);
			result = " { SaleProduct : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SaleProduct");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					HashMap<String, String> hashmap = new HashMap<String, String>();
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String tLocationCode = jsonChildNode.optString(
							"LocationCode").toString();

					String tProductCode = jsonChildNode
							.optString("ProductCode").toString();

					String tBatchNo = jsonChildNode.optString("BatchNo")
							.toString();

					String dateTime = jsonChildNode.optString("ExpiryDate")
							.toString();
					StringTokenizer tokens = new StringTokenizer(dateTime, " ");
					String tExpiryDate = tokens.nextToken();

					String tMfgDate = jsonChildNode.optString("MfgDate")
							.toString();

					String tPcsPerCarton = jsonChildNode.optString(
							"PcsPerCarton").toString();

					String tQty = jsonChildNode.optString("Qty").toString();

					String NoOfCarton = jsonChildNode.optString("NoOfCarton").toString();


					double cartonqty=0, dqty=0;

					if(!tQty.matches("")){
						dqty =  Double.parseDouble(tQty);
					}

					String calCarton = LogOutSetGet.getCalcCarton();
					if(calCarton.matches("0")){

						if(!NoOfCarton.matches("")){
							cartonqty = Double.parseDouble(NoOfCarton);

							if(cartonqty>0 || dqty>0){

								int iQty = (int) dqty;
								hashmap.put("LocationCode", tLocationCode);
								hashmap.put("ProductCode", tProductCode);
								hashmap.put("BatchNo", tBatchNo);
								hashmap.put("ExpiryDate", tExpiryDate);
								hashmap.put("MfgDate", tMfgDate);
								hashmap.put("PcsPerCarton", tPcsPerCarton);
								hashmap.put("Qty", iQty + "");
								hashmap.put("NoOfCarton", NoOfCarton);

								al.add(hashmap);

							}
						}
					}else{
						if(dqty>0){
							int iQty = (int) dqty;
							hashmap.put("LocationCode", tLocationCode);
							hashmap.put("ProductCode", tProductCode);
							hashmap.put("BatchNo", tBatchNo);
							hashmap.put("ExpiryDate", tExpiryDate);
							hashmap.put("MfgDate", tMfgDate);
							hashmap.put("PcsPerCarton", tPcsPerCarton);
							hashmap.put("Qty", iQty + "");
							hashmap.put("NoOfCarton", NoOfCarton);

							al.add(hashmap);
						}
					}
				}

				Log.d("InvoBatchDetailByCust", al.toString());

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return al;
	}

	public static ArrayList<SO> getInvoiceOverdue(HashMap<String, String> hashValue, String webMethNameGet) {

		  OverdueArr.clear();

		  SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

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
		   SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		   envelope.dotNet = true;
		   envelope.setOutputSoapObject(request);
		   envelope.bodyOut = request;
		  
		    HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		    androidHttpTransport = new HttpTransportSE(validURL);
		    androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
		    SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		    String resTxt = response.toString();
		    
		    result = " { JsonArray: " + resTxt + "}";
		    Log.d("Online Customer result", result);

		   JSONObject jsonResponse;
		   try {
		    jsonResponse = new JSONObject(result);

		    JSONArray jsonMainNode = jsonResponse.optJSONArray("JsonArray");

		    int lengthJsonArr = jsonMainNode.length();
		    for (int i = 0; i < lengthJsonArr; i++) {
		 
		     JSONObject jsonChildNode;
		     try {
		 
		      jsonChildNode = jsonMainNode.getJSONObject(i);
		      String mCustCode = jsonChildNode.optString("CustomerCode").toString();
		      String mCustName = jsonChildNode.optString("CustomerName").toString();
		      String mInvNo = jsonChildNode.optString("InvoiceNo").toString();
		      String mInvDate = jsonChildNode.optString("InvoiceDate").toString();
		      String mTerms = jsonChildNode.optString("Terms").toString();
		      String mPending = jsonChildNode.optString("Pending").toString();
		      String mBlnAmount = jsonChildNode.optString("BalanceAmount").toString();
		      
		      SO so = new SO();
		      so.setCustomerCode(mCustCode);
		      so.setCustomerName(mCustName);
		      so.setSno(mInvNo);
		      if (mInvDate != null && !mInvDate.isEmpty()) {
		       StringTokenizer tokens = new StringTokenizer(mInvDate, " ");
		       String mdate = tokens.nextToken();
		       so.setDate(mdate);
		      } else {
		       so.setDate(mInvDate);
		      }
		      
		      so.setTerms(mTerms);
		      so.setPending(mPending);
		      so.setBalanceamount(mBlnAmount);
		      OverdueArr.add(so);
		 
		     } catch (JSONException e) {
		 
		      e.printStackTrace();
		     }
		    
		 
		    }
		    
		    Log.d("overduelistArray Result", OverdueArr.toString());

		   } catch (JSONException e) {

		    e.printStackTrace();
		   }

		  } catch (Exception e) {

		   e.printStackTrace();
		  }

		  return OverdueArr;
		 }

	public static ArrayList<HashMap<String, String>> getProductAttribute(
			String productCode, String webMethNameSet) {

		al.clear();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameSet);

		PropertyInfo prodCode = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		HashMap<String, String> location_code_name = new HashMap<String, String>();
		location_code_name = SupplierSetterGetter.getLoc_code_name();
		String fromlocationname = SalesOrderSetGet.getTransferfromloc();
		String locationCode = location_code_name.get(fromlocationname);
		Log.d("from locationname", "->" + fromlocationname);


		String haveBatchOnTransfer = SalesOrderSetGet.getHaveBatchOnTransfer();
		if(haveBatchOnTransfer!=null && !haveBatchOnTransfer.isEmpty()){

		}else{
			haveBatchOnTransfer="";
		}
		if (haveBatchOnTransfer.matches("False")){
			locationCode = SupplierSetterGetter.getLocCode();
		}
		Log.d(" locationCode", "->" + locationCode);

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		Log.d(" CompanyCode", "->" + cmpnyCode);

		prodCode.setName("ProductCode");
		prodCode.setValue(productCode);
		prodCode.setType(String.class);
		request.addProperty(prodCode);

		Log.d(" ProductCode", "->" + productCode);

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
			Log.d("Color", value);
			result = " { Color : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("Color");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					HashMap<String, String> hashmap = new HashMap<String, String>();
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String tColorCode = jsonChildNode.optString(
							"ColorCode").toString();

					String tColorName = jsonChildNode
							.optString("ColorName").toString();

					String tSizeCode = jsonChildNode.optString("SizeCode")
							.toString();

					String tSizeName = jsonChildNode.optString("SizeName")
							.toString();


					// String tExpiryDate =
					// jsonChildNode.optString("ExpiryDate")
					// .toString();
					hashmap.put("ColorCode", tColorCode);
					hashmap.put("ColorName", tColorName);
					hashmap.put("SizeCode", tSizeCode);
					hashmap.put("SizeName", tSizeName);
					al.add(hashmap);

					Log.d("ColorName", tColorName.toString());
					Log.d("ColorCode", tColorCode.toString());
					Log.d("SizeCode", tSizeCode.toString());
					Log.d("SizeName", tSizeName.toString());

					Bundle args = new Bundle();
					args.putString("ColorName",tColorName);

				}
				Log.d("fncGetProductAttribute", al.toString());
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return al;
	}

}
