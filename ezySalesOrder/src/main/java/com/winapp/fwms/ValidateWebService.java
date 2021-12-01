package com.winapp.fwms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.winapp.model.CurrencyDeno;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.util.ErrorLog;

import android.util.Log;

public class ValidateWebService {

	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	static String vld_rslt = null, SettingID = "", SettingValue = "",
			gnrlStngs = "";
	static String resTxt = null;
	private static int TimeOut=200000;
	static String validatedeviceID;
	static ErrorLog errorLog;
	static List<CurrencyDeno> demolist = new ArrayList<>();

	public static String validateURLService(String URL, String webMethNameGet)
			throws JSONException, IOException, XmlPullParserException {
        try {
		errorLog = new ErrorLog();
		Log.d("URL", URL);
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11); 
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			
			androidHttpTransport = new HttpTransportSE(URL,TimeOut);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);	
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("Validate", resTxt);
			String result = " { Validate : " + resTxt + "}";

			JSONObject jsonResponse;

			jsonResponse = new JSONObject(result);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("Validate");
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				vld_rslt = jsonChildNode.optString("Result").toString();

				Log.d("result ", vld_rslt);
			}
		} catch (JSONException e) {

			e.printStackTrace();
			vld_rslt = "Error";
			Log.d("result ", vld_rslt);
			errorLog.write(e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			vld_rslt = "Error";
			Log.d("result ", vld_rslt);
			errorLog.write(e.getMessage());
		}
		return vld_rslt;
	}

	public static List validateCurrency(String URL, String webMethNameGet)
			throws JSONException, IOException, XmlPullParserException {
		try {
			errorLog = new ErrorLog();
			demolist.clear();
			Log.d("URL", URL);
			SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport = new HttpTransportSE(URL,TimeOut);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			Log.d("Validate", resTxt);
			String result = " { Validate : " + resTxt + "}";

			JSONObject jsonResponse;

			jsonResponse = new JSONObject(result);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("Validate");
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				CurrencyDeno demo = new CurrencyDeno();
				vld_rslt = jsonChildNode.optString("Currency").toString();
				demo.setCurency(vld_rslt);
				demo.setDenomination("0");
				demo.setTotal("0");
				demo.setSlno(""+(i+1));
				demolist.add(demo);

				Log.d("result ", vld_rslt);
			}
		} catch (JSONException e) {

			e.printStackTrace();
			vld_rslt = "Error";
			Log.d("result ", vld_rslt);
			errorLog.write(e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			vld_rslt = "Error";
			Log.d("result ", vld_rslt);
			errorLog.write(e.getMessage());
		}
		return demolist;
	}



	public static String generalSettingsService(String URL,
			String webMethNameGet,String cmpnyCode) throws JSONException, IOException,
			XmlPullParserException {
        try {
			Log.d("startsExecutingGeneral","-->"+"kjsdghjskdhgsj");
		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

		PropertyInfo companyCode = new PropertyInfo();
		companyCode.setName("CompanyCode");
				companyCode.setValue(cmpnyCode);
				companyCode.setType(String.class);
				request.addProperty(companyCode);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11); 
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,TimeOut);
		androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

		resTxt = response.toString();
		Log.d("GeneralSettingsss", resTxt);

		String result = " { GeneralSettings : " + resTxt + "}";
		JSONObject jsonResponse;

		jsonResponse = new JSONObject(result);
		JSONArray jsonMainNode = jsonResponse.optJSONArray("GeneralSettings");
		
		int lengthJsonArr = jsonMainNode.length();
		for (int i = 0; i < lengthJsonArr; i++) {
			
			JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
			SettingID = jsonChildNode.optString("SettingID").toString();

			Log.d("CheckSettingId",SettingID);

			if (SettingID.matches("APPTYPE")) {
				SettingValue = jsonChildNode.optString("SettingValue")
						.toString();

				if (SettingValue.matches("S")) {
					gnrlStngs = SettingValue;
					Log.d("result ", gnrlStngs);
				} else if (SettingValue.matches("W")) {
					gnrlStngs = SettingValue;
					Log.d("result ", gnrlStngs);
				}

			}

			if (SettingID.matches("IMMEDIATE_TRANSFER_ON_STOCKREQ")) {
				SettingValue = jsonChildNode.optString("SettingValue")
						.toString();

				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setTransfer_stockReq("0");
				} else if(SettingValue.matches("1")) {
					SalesOrderSetGet.setTransfer_stockReq("1");
				} else {
					SalesOrderSetGet.setTransfer_stockReq("0");
				}
				Log.d("Transfer_stockReq","-->"+SalesOrderSetGet.getTransfer_stockReq());
			}

			if (SettingID.matches("MOBILE_INVOICE_DATECHANGE")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				Log.d("checkSettingValue",SettingValue);
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setMobile_dateChange("0");
				} else if(SettingValue.matches("1")) {
					SalesOrderSetGet.setMobile_dateChange("1");
				} else {
					SalesOrderSetGet.setMobile_dateChange("0");
				}

				Log.d("checkValues",SalesOrderSetGet.getMobile_dateChange());
			}

			if (SettingID.matches("MOBILE_EXPENSE_DATECHANGE")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				Log.d("checkExpenseValue",SettingValue);
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setExpense_dateChange("0");
				} else if(SettingValue.matches("1")) {
					SalesOrderSetGet.setExpense_dateChange("1");
				} else {
					SalesOrderSetGet.setExpense_dateChange("0");
				}
				Log.d("checkValues",SalesOrderSetGet.getExpense_dateChange());
			}
			
			if (SettingID.matches("CALCCARTON")) {
				SettingValue = jsonChildNode.optString("SettingValue")
						.toString();

				if (SettingValue.matches("0")) {
					Log.d("CALCCARTON", LogOutSetGet.getCalcCarton());
					LogOutSetGet.setCalcCarton("0");
				} else if (SettingValue.matches("1")) {
					Log.d("CALCCARTON", LogOutSetGet.getCalcCarton());
					LogOutSetGet.setCalcCarton("1");
				}

			}
		  	
			if (SettingID.matches("CARTONPRICE")) {
				SettingValue = jsonChildNode.optString("SettingValue")
						.toString();
				Log.d("CartonPriceFlag", SalesOrderSetGet.getCartonpriceflag());
				if (SettingValue.matches("1")) {
					SalesOrderSetGet.setCartonpriceflag("1");

				} else if (SettingValue.matches("0")) {
					SalesOrderSetGet.setCartonpriceflag("0");
				}
			}
			
			if (SettingID.matches("RECEIPTONINVOICE")) {
				SettingValue = jsonChildNode.optString("SettingValue")
						.toString();
				
				Log.d("receipt invoice", "errg"+SettingValue);

				if (SettingValue.matches("1")) {
					SalesOrderSetGet.setReceiptoninvoice("1");
				} else if (SettingValue.matches("0")) {
					SalesOrderSetGet.setReceiptoninvoice("0");
				}

			}
			
			 if(SettingID.matches("MOBILEPRODUCTSTOCKPRINT")){
				    SettingValue = jsonChildNode.optString("SettingValue")
				      .toString();
				    
				    Log.d("product stock print", "errg"+SettingValue);

				    if (SettingValue.matches("1")) {
				     SalesOrderSetGet.setMobileproductstockprint("1");
				    } else if (SettingValue.matches("0")) {
				     SalesOrderSetGet.setMobileproductstockprint("0");
				    }
				   }
			 
			 if(SettingID.matches("AUTOBATCHNO")){
				    SettingValue = jsonChildNode.optString("SettingValue")
				      .toString();
				    
				    Log.d("mobile batch", "bat"+SettingValue);

				    if (SettingValue.matches("1")) {
				     SalesOrderSetGet.setAutoBatchNo("1");
				    } else if (SettingValue.matches("0")) {
				     SalesOrderSetGet.setAutoBatchNo("0");
				    }else{
					     SalesOrderSetGet.setAutoBatchNo("0");
					    }
				   }
			 
			 if(SettingID.matches("MOBILEPRINTINVOICEDETAIL")){
				 SettingValue = jsonChildNode.optString("SettingValue")
					      .toString();
				 
				 Log.d("invoice print", SettingValue);
				 
				 if (SettingValue.matches("1")) {
					 
					 SalesOrderSetGet.setInvoiceprintdetail("1");
					 
				 } else if (SettingValue.matches("0")) {
					 SalesOrderSetGet.setInvoiceprintdetail("0");
				 }else{
					 SalesOrderSetGet.setInvoiceprintdetail("0");
				 }
			 }
			 			 
			 if(SettingID.matches("MOBILELOGINPAGE")){
				 SettingValue = jsonChildNode.optString("SettingValue")
					      .toString();
				 
				 Log.d("mobile login", SettingValue);
				 
				 if (SettingValue.matches("M")) {
					 
					 SalesOrderSetGet.setMobileloginpage("M");
					 
				 } else if (SettingValue.matches("S")) {
					 SalesOrderSetGet.setMobileloginpage("S");
				 }else{
					 SalesOrderSetGet.setMobileloginpage("S");
				 }
			 }
			 
			 if(SettingID.matches("ENABLECUSTOMERCODE")){
				 SettingValue = jsonChildNode.optString("SettingValue")
					      .toString();
				 
				 Log.d("ENABLECUSTOMERCODE", SettingValue);
				 
				 if (SettingValue.matches("0")) {
					 
					 SalesOrderSetGet.setEnablecustomercode("0");
					 
				 } else if (SettingValue.matches("1")) {
					 SalesOrderSetGet.setEnablecustomercode("1");
				 }else{
					 SalesOrderSetGet.setEnablecustomercode("1");
				 }
			 }
			 
			 if(SettingID.matches("MOBILEOVERDUEALERT")){
			     SettingValue = jsonChildNode.optString("SettingValue").toString();
			     
			     Log.d("MOBILEOVERDUEALERT", SettingValue);
			     
			     if (SettingValue.matches("0")) {
			      
			      SalesOrderSetGet.setMobileoverduealert("0");
			      
			     } else if (SettingValue.matches("1")) {
			      SalesOrderSetGet.setMobileoverduealert("1");
			     }else{
			      SalesOrderSetGet.setMobileoverduealert("0");
			     }
			    }
			 
			 if (SettingID.matches("TRANSFERCHANGEFROMLOC")) {
					String settingValue = jsonChildNode.optString(
							"SettingValue").toString();

					if (settingValue.matches("1")) {
						SalesOrderSetGet.setTransferchangefromloc("1");
					} else if (settingValue.matches("0")) {
						SalesOrderSetGet.setTransferchangefromloc("0");
					} else {
						SalesOrderSetGet.setTransferchangefromloc("0");
					}
				}
			 
				/*if (SettingID.matches("DEFAUTSHOWCARTONORLOOSE")) {
			 //if (SettingID.matches("CARTONPRICE")) {
					String settingValue = jsonChildNode.optString(
							"SettingValue").toString();

					if (settingValue.matches("C")) {
						SalesOrderSetGet.setDefaultshowcartonorloose("C");
						// cartonloose = "L";
					} else if (settingValue.matches("L")) {
						SalesOrderSetGet.setDefaultshowcartonorloose("L");
//						Log.d("result ", cartonloose);
					} else {
						SalesOrderSetGet.setDefaultshowcartonorloose("");
//						Log.d("result ", cartonloose);
					}
				}*/
				
				 if (SettingID.matches("TRANBLOCKNEGATIVESTOCK")) {
						String settingValue = jsonChildNode.optString(
								"SettingValue").toString();

						if (settingValue.matches("1")) {
							SalesOrderSetGet.setTranblocknegativestock("1");
						} else if (settingValue.matches("0")) {
							SalesOrderSetGet.setTranblocknegativestock("0");
						} else {
							SalesOrderSetGet.setTranblocknegativestock("0");
						}
					}
				 
				 if (SettingID.matches("TRANBLOCKINVOICEABOVELIMIT")) {
						String settingValue = jsonChildNode.optString(
								"SettingValue").toString();

						if (settingValue.matches("1")) {
							SalesOrderSetGet.setTranblockinvoiceabovelimit("1");
						} else if (settingValue.matches("0")) {
							SalesOrderSetGet.setTranblockinvoiceabovelimit("0");
						} else {
							SalesOrderSetGet.setTranblockinvoiceabovelimit("0");
						}
					}
				 
				 
				 if (SettingID.matches("HAVETRACKING")) {
						SettingValue = jsonChildNode.optString("SettingValue").toString();

						if (SettingValue.matches("1")) {
							SalesOrderSetGet.setHaveTracking("1");
						} else if (SettingValue.matches("0")) {
							SalesOrderSetGet.setHaveTracking("0");
						}else {
							SalesOrderSetGet.setHaveTracking("0");
						}

					} 
				 
				 if (SettingID.matches("HAVEMERCHANDISING")) {
						SettingValue = jsonChildNode.optString("SettingValue").toString();

						if (SettingValue.matches("1")) {
							SalesOrderSetGet.setHaveMerchandising("1");
						} else if (SettingValue.matches("0")) {
							SalesOrderSetGet.setHaveMerchandising("0");
						}else {
							SalesOrderSetGet.setHaveMerchandising("0");
						}

					}
				 if (SettingID.matches("DOQTYVALIDATEWITHSO")) {
				      SettingValue = jsonChildNode.optString("SettingValue").toString();

				      if (SettingValue.matches("1")) {
				       SalesOrderSetGet.setDoQtyValidateWithSo("1");
//				      SalesOrderSetGet.setDoQtyValidateWithSo("0");
				      } else if (SettingValue.matches("0")) {
				       SalesOrderSetGet.setDoQtyValidateWithSo("0");
				       //SalesOrderSetGet.setDoQtyValidateWithSo("1");
				      }else {
				       SalesOrderSetGet.setDoQtyValidateWithSo("0");
				      }

				     }
				     if (SettingID.matches("SCHEDULINGTYPE")) {
				      SettingValue = jsonChildNode.optString("SettingValue").toString();
				      if (SettingValue.matches("DO")) {
				       SalesOrderSetGet.setSchedulingType("DO");
				      } else if (SettingValue.matches("C")) {
				      SalesOrderSetGet.setSchedulingType("C");
				//      SalesOrderSetGet.setSchedulingType("DO");
				      } else {
				       SalesOrderSetGet.setSchedulingType("");
				      }

				     }
				     
				     if (SettingID.matches("HAVEMULTIPLECUSTOMERPRICE")) {
				           SettingValue = jsonChildNode.optString("SettingValue").toString();
				           if (SettingValue.matches("1")) {
				            SalesOrderSetGet.setHaveMultipleCustomerPrice("1");
				           } else if (SettingValue.matches("0")) {
				           SalesOrderSetGet.setHaveMultipleCustomerPrice("0");
				     //      SalesOrderSetGet.setSchedulingType("DO");
				           } else {
				            SalesOrderSetGet.setHaveMultipleCustomerPrice("0");
				           }

				          }

			if (SettingID.matches("LOCALCURRENCY")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				SalesOrderSetGet.setLocalCurrency(SettingValue);
			}

			if (SettingID.matches("TRAN_BLOCK_TERMS")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("1")) {
					SalesOrderSetGet.setTranBlockTerms("1");
				} else if (SettingValue.matches("0")) {
					SalesOrderSetGet.setTranBlockTerms("0");
				} else {
					SalesOrderSetGet.setTranBlockTerms("0");
				}
			}

			if (SettingID.matches("TRAN_BLOCK_CREDITLIMIT")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("1")) {
					SalesOrderSetGet.setTranBlockCreditLimit("1");
				} else if (SettingValue.matches("0")) {
					SalesOrderSetGet.setTranBlockCreditLimit("0");
				} else {
					SalesOrderSetGet.setTranBlockCreditLimit("0");
				}
			}

			if (SettingID.matches("MALAYSIASHOWGST")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("1")) {
					SalesOrderSetGet.setMalaysiaShowGST("1");
				} else if (SettingValue.matches("0")) {
					SalesOrderSetGet.setMalaysiaShowGST("0");
				} else {
					SalesOrderSetGet.setMalaysiaShowGST("0");
				}
			}
			if (SettingID.matches("APPPRINTGROUP")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("C")) {
					SalesOrderSetGet.setAppPrintGroup("C");
				} else if (SettingValue.matches("S")) {
					SalesOrderSetGet.setAppPrintGroup("S");
				} else if (SettingValue.matches("N")) {
					SalesOrderSetGet.setAppPrintGroup("N");
				} else {
					SalesOrderSetGet.setAppPrintGroup(SettingValue);
				}
			}

			if (SettingID.matches("HAVEEMAILINTEGRATION")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("1")) {
					SalesOrderSetGet.setHaveemailintegration("1");
				} else if (SettingValue.matches("0")) {
					SalesOrderSetGet.setHaveemailintegration("0");
				} else {
					SalesOrderSetGet.setHaveemailintegration("0");
				}
			}

			if (SettingID.matches("HAVEATTRIBUTE")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("2")) {
					SalesOrderSetGet.setHaveAttribute("2");
				} else if (SettingValue.matches("1")) {
					SalesOrderSetGet.setHaveAttribute("1");
				} else {
					SalesOrderSetGet.setHaveAttribute("1");
				}
			}

			if (SettingID.matches("SHOW_UNITCOST_STOCKTAKE")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setShowUnitCostStockTake("0");
				} else if (SettingValue.matches("1")) {
					SalesOrderSetGet.setShowUnitCostStockTake("1");
				} else {
					SalesOrderSetGet.setShowUnitCostStockTake("0");
				}
			}
			if (SettingID.matches("SELFORDERSHOWADDTOCART")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setShowAddToCart("0");
				} else if (SettingValue.matches("1")) {
					SalesOrderSetGet.setShowAddToCart("1");
				} else {
					SalesOrderSetGet.setShowAddToCart("0");
				}
			}
			if (SettingID.matches("MOBILE_SHOW_CODE_ONSEARCH")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setMobileShowCodeOnSearch("0");
				} else if (SettingValue.matches("1")) {
					SalesOrderSetGet.setMobileShowCodeOnSearch("1");
				} else {
					SalesOrderSetGet.setMobileShowCodeOnSearch("0");
				}
			}
			if (SettingID.matches("SELFORDER_SHOW_PRODUCTCODE")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setSelfOrderShowProductCode("0");
				} else if (SettingValue.matches("1")) {
					SalesOrderSetGet.setSelfOrderShowProductCode("1");
				} else {
					SalesOrderSetGet.setSelfOrderShowProductCode("0");
				}
			}

			if (SettingID.matches("MOBILE_HAVE_OFFLINEMODE")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setMobileHaveOfflineMode("0");
				} else if (SettingValue.matches("1")) {
					SalesOrderSetGet.setMobileHaveOfflineMode("1");
				} else {
					SalesOrderSetGet.setMobileHaveOfflineMode("0");
				}
//				SalesOrderSetGet.setMobileHaveOfflineMode("0");
			}

			if (SettingID.matches("RECEIPT_AUTO_CREDIT_AMOUNT")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setReceiptAutoCreditAmount("0");
				} else  if (SettingValue.matches("1")) {
					SalesOrderSetGet.setReceiptAutoCreditAmount("1");
				} else {
					SalesOrderSetGet.setReceiptAutoCreditAmount("0");
				}
			}

			if (SettingID.matches("HOSTING_VALIDATION")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setHostingValidation("0");
				} else if(SettingValue.matches("1")) {
					SalesOrderSetGet.setHostingValidation("1");
				}else {
					SalesOrderSetGet.setHostingValidation("0");
				}
			}

			if (SettingID.matches("CUSTOMER_HAVE_CASHBILL")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setCustomerHaveCashbill("0");
				} else if(SettingValue.matches("1")) {
					SalesOrderSetGet.setCustomerHaveCashbill("1");
				} else {
					SalesOrderSetGet.setCustomerHaveCashbill("0");
				}
			}

			if (SettingID.matches("MOBILE_DO_SHOW_ROUTE")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setMobileDoShowRoute("0");
				} else if(SettingValue.matches("1")) {
					SalesOrderSetGet.setMobileDoShowRoute("1");
				} else {
					SalesOrderSetGet.setMobileDoShowRoute("0");
				}
			}

			if (SettingID.matches("HAVEPOS")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setHavePos("0");
				} else if(SettingValue.matches("1")) {
					SalesOrderSetGet.setHavePos("1");
				} else {
					SalesOrderSetGet.setHavePos("0");
				}
			}

			if (SettingID.matches("MOBILE_INVOICE_DATECHANGE")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				Log.d("checkSettingValue",SettingValue);
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setMobile_dateChange("0");
				} else if(SettingValue.matches("1")) {
					SalesOrderSetGet.setMobile_dateChange("1");
				} else {
					SalesOrderSetGet.setMobile_dateChange("0");
				}

				Log.d("checkValues",SalesOrderSetGet.getMobile_dateChange());
			}

		}
		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
			gnrlStngs = "Error";
            errorLog.write("Method Name : " + webMethNameGet +" , "+"Error : " + e.getMessage());
		}
		
		return gnrlStngs;
	}
	
	public static  String validateDeviceIDService(String deviceID,String webMethName,String validURL)throws JSONException 
	{
        try {
		PropertyInfo moduleName = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		moduleName.setName("DeviceID");
		moduleName.setValue(deviceID);
		moduleName.setType(String.class);
		request.addProperty(moduleName);
		

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11); 
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request); 
			envelope.bodyOut = request;
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			String result = " { ValidateDeviceID : " + resTxt + "}";
			JSONObject jsonResponse;
			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("ValidateDeviceID");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) 
				{
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					validatedeviceID= jsonChildNode.optString("Result").toString();
					
				}
 
			} catch (JSONException e) {
                errorLog.write("Method Name : " + webMethName +" , "+"Error : " + e.getMessage());
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
            errorLog.write("Method Name : " + webMethName +" , "+"Error : " + e.getMessage());
		}

		return validatedeviceID;
	}
	public static  String activateDeviceIDService(String activatecode, String companyname, String deviceID,String webMethName,String validURL)throws JSONException, IOException, XmlPullParserException
	{
		
		PropertyInfo modulecode = new PropertyInfo();
		PropertyInfo moduleName = new PropertyInfo();
		PropertyInfo companyName = new PropertyInfo();
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		
		modulecode.setName("ActivationCode");
		modulecode.setValue(activatecode);
		modulecode.setType(String.class);
		request.addProperty(modulecode);
		
		moduleName.setName("DeviceID");
		moduleName.setValue(deviceID);
		moduleName.setType(String.class);
		request.addProperty(moduleName);
		
		companyName.setName("CompanyName");
		companyName.setValue(companyname);
		companyName.setType(String.class);
		request.addProperty(companyName);
		
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11); 
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request); 
			envelope.bodyOut = request;
			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			resTxt = response.toString();
			String result = " { ActivateDeviceID : " + resTxt + "}";
			JSONObject jsonResponse;
			try {

				jsonResponse = new JSONObject(result);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("ActivateDeviceID");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) 
				{
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					validatedeviceID= jsonChildNode.optString("Result").toString();
					
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}

		return validatedeviceID;
	}

	public static String generalService(String URL, String webMethNameGet,String cmpnyCode) throws JSONException, IOException,
			XmlPullParserException {
		try {
			Log.d("startsExecutingDisplay","-->"+URL+"CompanyCode:"+cmpnyCode);
			SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

			PropertyInfo companyCode = new PropertyInfo();
			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,TimeOut);
			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			resTxt = response.toString();
			Log.d("displaySrttings", resTxt);

			String result = " { displaySrttings : " + resTxt + "}";
			JSONObject jsonResponse;

			jsonResponse = new JSONObject(result);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("displaySrttings");

			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				SettingID = jsonChildNode.optString("SettingID").toString();

				Log.d("CheckSettingId", SettingID);
				if (SettingID.matches("CARTON_QUANTITY")) {
					SettingValue = jsonChildNode.optString("SettingValue")
							.toString();

					if (SettingValue.matches("0")) {
						SalesOrderSetGet.setCarton_quantity("0");
					} else if(SettingValue.matches("1")) {
						SalesOrderSetGet.setCarton_quantity("1");
					} else {
						SalesOrderSetGet.setCarton_quantity("0");
					}
					Log.d("getCarton_quantity","-->"+SalesOrderSetGet.getCarton_quantity());

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
			gnrlStngs = "Error";
			errorLog.write("Method Name : " + webMethNameGet +" , "+"Error : " + e.getMessage());
		}

		return gnrlStngs;
	}
}
