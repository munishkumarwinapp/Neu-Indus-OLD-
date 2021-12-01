package com.winapp.fwms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.SalesOrderSetGet;

import android.util.Log;

public class GetUserPermission {
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	static String value;
	static String result;
	static String validURL;
	static ArrayList<String> userGroupcodeArr = new ArrayList<String>();

	public GetUserPermission(String url) {
		validURL = url;
	}

	public static ArrayList<String> userGroupCode(String usercode,String CompanyCode,
												  String webMethName) {
		userGroupcodeArr.clear();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		PropertyInfo userGroup = new PropertyInfo();
		PropertyInfo jsonRequest = new PropertyInfo();

		userGroup.setName("sUserGroupCode");
		userGroup.setValue(usercode);
		userGroup.setType(String.class);
		request.addProperty(userGroup);

		jsonRequest.setName("CompanyCode");
		jsonRequest.setValue(CompanyCode);
		jsonRequest.setType(String.class);
		request.addProperty(jsonRequest);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		try {

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			value = response.toString();
			Log.d("UserGroup", value);
			result = " { UserGroupCode : " + value + "}";
			JSONObject jsonResponse;
			jsonResponse = new JSONObject(result);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("UserGroupCode");
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				
				String formName = jsonChildNode.optString("FormName")
						.toString();
				String result = jsonChildNode.optString("Result").toString();
				if (result.matches("True")) {
					userGroupcodeArr.add(formName);
				}	
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userGroupcodeArr;
	}
	
	public static HashMap<String, String> getLocationHaveBatch(String webMethName, String CompanyCode, String LocationCode)
	        throws JSONException {
	       String haveBatchOnStockIn = "", haveBatchOnTransfer = "", haveBatchOnStockAdjustment="", NextBatchNo="",
	    		   HaveBatchOnStockOut="",haveBatchConsignmentOut ="";
	       String resTxt = null;
	       HashMap<String, String> hm = new HashMap<String, String>();
	       try {
	       
	       SoapObject request = new SoapObject(NAMESPACE, webMethName);
	       
	       PropertyInfo jsonRequest = new PropertyInfo();
	       jsonRequest.setName("CompanyCode");
	       jsonRequest.setValue(CompanyCode);
	       jsonRequest.setType(String.class);
	       
	       PropertyInfo locRequest = new PropertyInfo();
	       locRequest.setName("LocationCode");
	       locRequest.setValue(LocationCode);
	       locRequest.setType(String.class);
	       
	       request.addProperty(jsonRequest);
	       request.addProperty(locRequest);
	       
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

	         int lengthJsonArr = jsonMainNode.length();
	         for (int i = 0; i < lengthJsonArr; i++) {
	          JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

	          haveBatchOnStockIn = jsonChildNode.optString(
	            "HaveBatchOnStockIn").toString();
	          
	          haveBatchOnTransfer = jsonChildNode.optString(
	              "HaveBatchOnTransfer").toString();
	          
	          haveBatchOnStockAdjustment = jsonChildNode.optString(
	               "HaveBatchOnStockAdjustment").toString();

	          haveBatchConsignmentOut = jsonChildNode.optString("HaveBatchOnConsignmentOut").toString();
	          
	          NextBatchNo = jsonChildNode.optString(
	              "NextBatchNo").toString();
	          
	          HaveBatchOnStockOut= jsonChildNode.optString(
		              "HaveBatchOnStockOut").toString();
	          
	          SalesOrderSetGet.setNextBatchNo(NextBatchNo);
	          
	         }

	        } catch (JSONException e) {

	         e.printStackTrace();
	        }

	       } catch (Exception e) {
	        e.printStackTrace();
	        resTxt = "Error occured";
	       }
	       
	       hm.put("HaveBatchOnStockIn",haveBatchOnStockIn);
	       hm.put("HaveBatchOnTransfer",haveBatchOnTransfer);
	       hm.put("HaveBatchOnStockAdjustment",haveBatchOnStockAdjustment);
	       hm.put("HaveBatchOnStockOut",HaveBatchOnStockOut);
	       hm.put("HaveBatchOnConsignmentOut",haveBatchConsignmentOut);
	       return hm;
	  }
	
	public static void getLocationHaveBatchTransfer(String webMethName, String locationcode)
	        throws JSONException {
	       String haveBatchOnTransfer = "";
	       String resTxt = null;
	       String CompanyCode = SupplierSetterGetter.getCompanyCode();
	       
	       try {
	       
	       SoapObject request = new SoapObject(NAMESPACE, webMethName);
	       
	       PropertyInfo jsonRequest = new PropertyInfo();
	       jsonRequest.setName("CompanyCode");
	       jsonRequest.setValue(CompanyCode);
	       jsonRequest.setType(String.class);
	       
	       PropertyInfo locRequest = new PropertyInfo();
	       locRequest.setName("LocationCode");
	       locRequest.setValue(locationcode);
	       locRequest.setType(String.class);
	       
	       request.addProperty(jsonRequest);
	       request.addProperty(locRequest);
	       
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

	         int lengthJsonArr = jsonMainNode.length();
	         for (int i = 0; i < lengthJsonArr; i++) {
	          JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

	
	          haveBatchOnTransfer = jsonChildNode.optString(
	              "HaveBatchOnTransfer").toString();
	          

	          SalesOrderSetGet.setHaveBatchOnTransferToLocation(haveBatchOnTransfer);
	          
	         }

	        } catch (JSONException e) {

	         e.printStackTrace();
	        }

	       } catch (Exception e) {
	        e.printStackTrace();
	        resTxt = "Error occured";
	       }
	       

	  }
	
	
	public static void mobileSettings(String webMethName) {
			
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		PropertyInfo companycode = new PropertyInfo();
			
		companycode.setName("CompanyCode");
		companycode.setValue(cmpnyCode);
		companycode.setType(String.class);
		request.addProperty(companycode);
			
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		try {

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			value = response.toString();
			Log.d("MobileSettings", value);
			result = " { MobileSettings : " + value + "}";
			JSONObject jsonResponse;
			jsonResponse = new JSONObject(result);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("MobileSettings");
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				
				String ShowLogo = jsonChildNode.optString("ShowLogo")
						.toString();
				String ShowAddress1 = jsonChildNode.optString("ShowAddress1")
						.toString();
				String ShowAddress2 = jsonChildNode.optString("ShowAddress2")
						.toString();
				String ShowAddress3 = jsonChildNode.optString("ShowAddress3")
						.toString();
				String ShowCountryPostal = jsonChildNode.optString("ShowCountryPostal")
						.toString();
				String ShowPhone = jsonChildNode.optString("ShowPhone")
						.toString();
				String ShowFax = jsonChildNode.optString("ShowFax")
						.toString();
				String ShowEmail = jsonChildNode.optString("ShowEmail")
						.toString();
				String ShowTaxRegNo = jsonChildNode.optString("ShowTaxRegNo")
						.toString();
				String ShowBizRegNo = jsonChildNode.optString("ShowBizRegNo")
						.toString();				
				String ShowCustomerCode = jsonChildNode.optString("ShowCustomerCode")
						.toString();
				String ShowCustomerName = jsonChildNode.optString("ShowCustomerName")
						.toString();
				String ShowCustomerAddress1 = jsonChildNode.optString("ShowCustomerAddress1")
						.toString();
				String ShowCustomerAddress2 = jsonChildNode.optString("ShowCustomerAddress2")
						.toString();
				String ShowCustomerAddress3 = jsonChildNode.optString("ShowCustomerAddress3")
						.toString();
				String ShowCustomerPhone = jsonChildNode.optString("ShowCustomerPhone")
						.toString();
				String ShowCustomerHP = jsonChildNode.optString("ShowCustomerHP")
						.toString();
				String ShowCustomerEmail = jsonChildNode.optString("ShowCustomerEmail")
						.toString();
				String ShowCustomerTerms = jsonChildNode.optString("ShowCustomerTerms")
						.toString();
				String ShowProductFullName = jsonChildNode.optString("ShowProductFullName")
						.toString();
				String ShowTotalOutstanding = jsonChildNode.optString("ShowTotalOutstanding")
						.toString();
				String ShowFooter = jsonChildNode.optString("ShowFooter")
						.toString();
				
				String ShowBatchDetails = jsonChildNode.optString("ShowBatchDetails")
						.toString();
				String HaveDeliveryVerification = jsonChildNode.optString("HaveDeliveryVerification")
					      .toString();
				String ShowPriceOnDO = jsonChildNode.optString("ShowPriceOnDO")
				           .toString();
				String ShowUserPhoneNo = jsonChildNode.optString("ShowUserPhoneNo")
			               .toString();
				String printSalesReturnSummaryOnReceipt = jsonChildNode.optString("PrintSalesReturnSummaryOnReceipt")
						.toString();
				String printSalesReturnDetailOnReceipt = jsonChildNode.optString("PrintSalesReturnDetailOnReceipt")
						.toString();

				String InvoiceDetailPrintUOM = jsonChildNode.optString("InvoiceDetailPrintUOM")
						.toString();
				String InvoiceHeaderCaption = jsonChildNode.optString("InvoiceHeaderCaption")
						.toString();
				String InvoiceTelCaption = jsonChildNode.optString("InvoiceTelCaption")
						.toString();
				String InvoiceFaxCaption = jsonChildNode.optString("InvoiceFaxCaption")
						.toString();
				String InvoiceEmailCaption = jsonChildNode.optString("InvoiceEmailCaption")
						.toString();
				String InvoiceBizRegNoCaption = jsonChildNode.optString("InvoiceBizRegNoCaption")
						.toString();
				String InvoiceTaxRegNoCaption = jsonChildNode.optString("InvoiceTaxRegNoCaption")
						.toString();
				String InvoiceSubTotalCaption = jsonChildNode.optString("InvoiceSubTotalCaption")
						.toString();
				String InvoiceTaxCaption = jsonChildNode.optString("InvoiceTaxCaption")
						.toString();
				String InvoiceNetTotalCaption = jsonChildNode.optString("InvoiceNetTotalCaption")
						.toString();
				String ShowGST = jsonChildNode.optString("ShowGST")
						.toString();
				String CenterAlignCompanyName = jsonChildNode.optString("CenterAlignCompanyName")
						.toString();
				String PrintReceiptSummary_PrintInvoiceDetail = jsonChildNode.optString("PrintReceiptSummary_PrintInvoiceDetail")
						.toString();
				String ShowCreateTime = jsonChildNode.optString("ShowCreateTime")
						.toString();
				String CompanyNameAlias =jsonChildNode.optString("CompanyNameAlias").toString();

				String fixPrecision  = "3";

				Log.d("ShowTaxRegNo","-->"+ShowTaxRegNo+"InvoiceTaxRegNoCaption"+InvoiceTaxRegNoCaption);

				MobileSettingsSetterGetter.setPrintSalesReturnDetailOnReceipt(printSalesReturnDetailOnReceipt);
				MobileSettingsSetterGetter.setPrintSalesReturnSummaryOnReceipt(printSalesReturnSummaryOnReceipt);

				MobileSettingsSetterGetter.setShowLogo(ShowLogo);
				MobileSettingsSetterGetter.setShowAddress1(ShowAddress1);
				MobileSettingsSetterGetter.setShowAddress2(ShowAddress2);
				MobileSettingsSetterGetter.setShowAddress3(ShowAddress3);
				MobileSettingsSetterGetter.setShowCountryPostal(ShowCountryPostal);
				MobileSettingsSetterGetter.setShowPhone(ShowPhone);
				MobileSettingsSetterGetter.setShowFax(ShowFax);
				MobileSettingsSetterGetter.setShowEmail(ShowEmail);
				MobileSettingsSetterGetter.setShowTaxRegNo(ShowTaxRegNo);
				MobileSettingsSetterGetter.setShowBizRegNo(ShowBizRegNo);
				MobileSettingsSetterGetter.setShowCustomerCode(ShowCustomerCode);
				MobileSettingsSetterGetter.setShowCustomerName(ShowCustomerName);
				MobileSettingsSetterGetter.setShowCustomerAddress1(ShowCustomerAddress1);
				MobileSettingsSetterGetter.setShowCustomerAddress2(ShowCustomerAddress2);
				MobileSettingsSetterGetter.setShowCustomerAddress3(ShowCustomerAddress3);
				MobileSettingsSetterGetter.setShowCustomerPhone(ShowCustomerPhone);
				MobileSettingsSetterGetter.setShowCustomerHP(ShowCustomerHP);
				MobileSettingsSetterGetter.setShowCustomerEmail(ShowCustomerEmail);
				MobileSettingsSetterGetter.setShowCustomerTerms(ShowCustomerTerms);
				MobileSettingsSetterGetter.setShowProductFullName(ShowProductFullName);
				MobileSettingsSetterGetter.setShowTotalOutstanding(ShowTotalOutstanding);
				MobileSettingsSetterGetter.setShowFooter(ShowFooter);
				MobileSettingsSetterGetter.setShowBatchDetails(ShowBatchDetails);
				MobileSettingsSetterGetter.setHaveDeliveryVerification(HaveDeliveryVerification);
				MobileSettingsSetterGetter.setShowPriceOnDO(ShowPriceOnDO);
				MobileSettingsSetterGetter.setShowUserPhoneNo(ShowUserPhoneNo);
				MobileSettingsSetterGetter.setInvoiceDetailPrintUOM(InvoiceDetailPrintUOM);
				MobileSettingsSetterGetter.setInvoiceHeaderCaption(InvoiceHeaderCaption);
				MobileSettingsSetterGetter.setInvoiceTelCaption(InvoiceTelCaption);
				MobileSettingsSetterGetter.setInvoiceFaxCaption(InvoiceFaxCaption);
				MobileSettingsSetterGetter.setInvoiceEmailCaption(InvoiceEmailCaption);
				MobileSettingsSetterGetter.setInvoiceBizRegNoCaption(InvoiceBizRegNoCaption);
				MobileSettingsSetterGetter.setInvoiceTaxRegNoCaption(InvoiceTaxRegNoCaption);
				MobileSettingsSetterGetter.setInvoiceSubTotalCaption(InvoiceSubTotalCaption);
				MobileSettingsSetterGetter.setInvoiceTaxCaption(InvoiceTaxCaption);
				MobileSettingsSetterGetter.setInvoiceNetTotalCaption(InvoiceNetTotalCaption);
				MobileSettingsSetterGetter.setShowGST(ShowGST);
				MobileSettingsSetterGetter.setCenterAlignCompanyName(CenterAlignCompanyName);
				MobileSettingsSetterGetter.setPrintReceiptSummary_PrintInvoiceDetail(PrintReceiptSummary_PrintInvoiceDetail);
				MobileSettingsSetterGetter.setShowCreateTime(ShowCreateTime);
				MobileSettingsSetterGetter.setCompanyNameAlias(CompanyNameAlias);
				MobileSettingsSetterGetter.setDecimalPoints(fixPrecision);
//				MobileSettingsSetterGetter.setShowPriceOnDO("true");
				Log.d("MobileSettings ", "--->"+ShowFooter);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void getRouteMaster(String webMethName, String CompanyCode,
			String username, String date) throws JSONException {
		String mresult = "";
		String resTxt = null;
		try {
//			System.out.println(date);
			SimpleDateFormat oldFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date mDate  = oldFormat.parse(date);
			
			SimpleDateFormat dayFormat = new SimpleDateFormat("EEE, dd MMM yyyy");
			String mday = dayFormat.format(mDate);
			
			StringTokenizer tokens = new StringTokenizer(mday, ",");
			String first = tokens.nextToken();
			
			if(first.matches("Mon")){
				mresult = "1";
			}else if(first.matches("Tue")){
				mresult = "2";
			}else if(first.matches("Wed")){
				mresult = "3";
			}else if(first.matches("Thu")){
				mresult = "4";
			}else if(first.matches("Fri")){
				mresult = "5";
			}else if(first.matches("Sat")){
				mresult = "6";
			}else if(first.matches("Sun")){
				mresult = "7";
			}
			System.out.println("day : "+mresult);
			
			SoapObject request = new SoapObject(NAMESPACE, webMethName);

			PropertyInfo companycode = new PropertyInfo();
			companycode.setName("CompanyCode");
			companycode.setValue(CompanyCode);
			companycode.setType(String.class);

			PropertyInfo user = new PropertyInfo();
			user.setName("AssignUser");
			user.setValue(username);
			user.setType(String.class);

			PropertyInfo day = new PropertyInfo();
			day.setName("RouteDay");
			day.setValue(mresult);
			day.setType(String.class);

			request.addProperty(companycode);
			request.addProperty(user);
			request.addProperty(day);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			result = " { Route : " + resTxt + "}";

		   	JSONObject jsonResponse;
			jsonResponse = new JSONObject(result);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("Route");
			int lengthJsonArr = jsonMainNode.length();

			if (lengthJsonArr > 0) {
				SalesOrderSetGet.setRoutepermission("True");
			} else {
				SalesOrderSetGet.setRoutepermission("False");
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			SalesOrderSetGet.setRoutepermission("False");
		}
	}

	public static String getCompanyHostingDetails(String CompanyCode,String webMethName)
			throws JSONException {
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
			result = " { CompanyHostingDetails : " + resTxt + "}";

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return result;
	}
}
