package com.winapp.printer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Looper;
import android.text.Html;
import android.util.Base64;
import android.util.Log;

import com.winapp.SFA.R;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.CurrencyDeno;
import com.winapp.model.Product;
import com.winapp.model.Receipt;
import com.winapp.sot.Company;
import com.winapp.sot.In_Cash;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.ProdDetails;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sotdetails.CustomerSetterGetter;
import com.winapp.sotdetails.ProductStockGetSet;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

public class Printer {


	public interface OnCompletionListener {
		public void onCompleted();
	}

	private Context context;
	private UIHelper helper;
	private String macAddress;
	private ZebraPrinter printer;
	private BluetoothAdapter bluetoothAdapter;
	// private SettingsManager settings;
	private static final String FILE_NAME = "TEMP.LBL";
	private static final String LINE_SEPARATOR = "\r\n";
	private static final String LINEBREAK = "\r\b";
	private static final int LABEL_WIDTH = 574;
	private static final int FONT = 5;
	private static final int FONT_SIZE = 0;
	private static final int LEFT_MARGIN = 10;
	private static final int RIGHT_MARGIN = 564;
	private static final int LINE_THICKNESS = 2;
	private static final int LINE_SPACING = 40;
	private static final String CMD_TEXT = "T";
	private static final String CMD_LINE = "L";
	private static final String CMD_PRINT = "PRINT" + LINE_SEPARATOR;
	private static final String SPACE = " ";
	private static final int POSTFEED = 50;
	public static int PAPER_WIDTH = 78;
	Bitmap logo,image;
	String logoStr = "",mUser="";
	List<ProductDetails> footerArr = new ArrayList<ProductDetails>();
	private static final String HEADER = "PW " + LABEL_WIDTH + LINE_SEPARATOR
			+ "TONE 0" + LINE_SEPARATOR + "SPEED 3" + LINE_SEPARATOR + "ZPL"
			+ POSTFEED + LINE_SEPARATOR + "NO-PACE" + "BAR-SENSE"
			+ LINE_SEPARATOR;
	byte FONT_TYPE;
	String custlastname;
	private OnCompletionListener listener;
	private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				switch (state) {
				case BluetoothAdapter.STATE_OFF:
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					context.unregisterReceiver(bluetoothReceiver);
					break;
				case BluetoothAdapter.STATE_ON:
					Log.d("BluetoothTurnedOn","bluetooth()");
					print();
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					break;
				}
			}
		}
	};

	public Printer(Context context, String macAddress) {
		if (!macAddress
				.matches("[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {
			throw new IllegalArgumentException(macAddress
					+ context.getString(R.string.is_not_valid_mac_address));
		}
		this.context = context;
		this.macAddress = macAddress;
		try {
			Connection connection = null;
			connection = new BluetoothConnection(macAddress);
			connection.open();

			connection.write("! U1 setvar \"device.languages\" \"zpl\""
					.getBytes());

			// SGD.SET("device.languages", "hybrid_xml_zpl", connection);
			// ! U1 do "device.reset" "" // reset device
			connection.close();
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		helper = new UIHelper(context);
		// settings = new SettingsManager(context);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	public void setOnCompletionListener(OnCompletionListener listener) {
		this.listener = listener;
	}


	private void jubiPrint(){
		Log.d("logoStrjubi", "......" + logoStr);

		String img = SOTDatabase.getSignatureImage();
		Log.d("Print_imgjubi", "" + img);

		helper.updateProgressDialog(R.string.connecting_to_printer);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				Connection connection = null;
				Log.d("macAddress","->"+macAddress);
				Log.d("SalesordersetgetCode","-->"+SalesOrderSetGet.getShortCode());
				connection = new BluetoothConnection(macAddress);
				try {
					connection.open();
					printer = ZebraPrinterFactory.getInstance(connection);



					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							helper.updateProgressDialog(R.string.printing_in_progress);
						}
					});

					PrinterLanguage printerLanguage = printer
							.getPrinterControlLanguage();

					Matrix matrix = new Matrix();
					matrix.postRotate(180);

					// //////////////////

					String showLogo = MobileSettingsSetterGetter.getShowLogo();
					Log.d("showLogo", "......" + showLogo +"  "+printerLanguage);

					if (logoStr.matches("logoprint")) {

						if (showLogo.matches("True")) {



							//Bitmap logo = BitmapFactory.decodeResource(context.getResources(),R.drawable.aathilogoedit1);

							logo = LogOutSetGet.getBitmap();

							// Bitmap logo =
							// BitmapFactory.decodeResource(context.getResources(),
							// R.drawable.test2); // don't remove

							int width = 570, imgWidth, imgHeight;
							int diff;

							Log.d("PrintinZPLPrinter", "ZPL");
							/*if(SalesOrderSetGet.getShortCode().matches("HEZOMs")){
							}else {
								image = Bitmap.createBitmap(logo, 0, 0,
										logo.getWidth(), logo.getHeight(), matrix,
										true);
							}

							if (width > logo.getWidth()) {
								diff = (570 - logo.getWidth()) / 2;
								imgWidth = image.getHeight();
								imgHeight = image.getHeight();
								Log.d("logo.getWidth()", "......" + image.getWidth()+","+image.getHeight());
							} else {
								diff = 0;
								imgWidth = width;
								imgHeight = 250;
								Log.d("width", "......" + width);
							}*/

							if (printerLanguage == PrinterLanguage.ZPL) {

								Log.d("PrintinZPLPrinterjubii", "ZPL");

								image = Bitmap.createBitmap(logo, 0, 0,
										logo.getWidth(), logo.getHeight(), matrix,
										true);
								printer.printImage(new ZebraImageAndroid(image),
										-200, 0, 200, 100, false);

								connection
										.write("! U1 setvar \"zpl.label_length\" \"90\""
												.getBytes());

								connection
										.write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n"
												.getBytes());

								connection
										.write("! U1 setvar \"ezpl.media_type\" \"continuous\""
												.getBytes());
							}

							else if (printerLanguage == PrinterLanguage.CPCL) {

								logo = Bitmap.createScaledBitmap(logo,
										logo.getWidth(), logo.getHeight(), true);

								connection
										.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n"
												.getBytes());
								printer.printImage(new ZebraImageAndroid(logo), 0,
										0, -1, -1, false);
							}
						}

					} else {
						Log.d("Logo", "No Logo");
					}
					// /////////////////



					File filepath = context.getFileStreamPath(FILE_NAME);
					printer.sendFileContents(filepath.getAbsolutePath());

					SOTDatabase.init(context);
					String img = SOTDatabase.getSignatureImage();
					Log.d("Print_imgjubiii", "" + img);

					if (!img.matches("")) {

						// byte[] encodeByte = Base64.decode(img,
						// Base64.DEFAULT);
						// Bitmap photo = BitmapFactory.decodeByteArray(
						// encodeByte, 0, encodeByte.length);

						// //
						byte[] encodeByte;

						byte[] encodeByte1 = Base64.decode(img, Base64.DEFAULT);

						String s;
						try {
							s = new String(encodeByte1, "UTF-8");
							encodeByte = Base64.decode(s, Base64.DEFAULT);
						} catch (Exception e) {
							encodeByte = encodeByte1;
						}

						if (printerLanguage == PrinterLanguage.ZPL) {

							Log.d("PrintinZPLPrinter", "ZPL");

							Bitmap photo = BitmapFactory.decodeByteArray(
									encodeByte, 0, encodeByte.length);
							image = Bitmap.createBitmap(photo, 0, 0,
									photo.getWidth(), photo.getHeight(),
									matrix, true);
							printer.printImage(new ZebraImageAndroid(image),
									-300, 0, 300, 80, false);

							connection
									.write("! U1 setvar \"zpl.label_length\" \"90\""
											.getBytes());

							connection
									.write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n"
											.getBytes());

							connection
									.write("! U1 setvar \"ezpl.media_type\" \"continuous\""
											.getBytes());



						} else if (printerLanguage == PrinterLanguage.CPCL) {

							Bitmap photo = BitmapFactory.decodeByteArray(
									encodeByte, 0, encodeByte.length);

							Log.d("Print in CPCL Printer", "CPCL");
							photo = Bitmap.createScaledBitmap(photo, 300, 80,
									true);

							connection
									.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n"
											.getBytes());
							printer.printImage(new ZebraImageAndroid(photo), 0,
									0, -1, -1, false);
						}

						try {
							bottomPrint();

							File filepath1 = context
									.getFileStreamPath(FILE_NAME);
							printer.sendFileContents(filepath1
									.getAbsolutePath());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

						else{
							try {
								bottomPrint();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					if (printerLanguage == PrinterLanguage.ZPL) {
						connection
								.write("! U1 setvar \"zpl.label_length\" \"100\""
										.getBytes());

						connection
								.write("! U1 setvar \"ezpl.media_type\" \"continuous\""
										.getBytes());
					}


					connection.close();
					helper.showLongToast(R.string.printed_successfully);



				} catch (ZebraPrinterLanguageUnknownException e) {
					helper.showErrorDialog(e.getMessage());
				} catch (ConnectionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					helper.dismissProgressDialog();
					disableBluetooth();
					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (listener != null) {
								listener.onCompleted();
							}
						}
					});
				}
				Looper.loop();
				Looper.myLooper().quit();
			}
		}).start();
	}

	private void print() {
		Log.d("logoStr", "......" + logoStr);

		String img = SOTDatabase.getSignatureImage();
		Log.d("Print_img", "" + img);

		helper.updateProgressDialog(R.string.connecting_to_printer);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				Connection connection = null;
				Log.d("macAddress","->"+macAddress);
				Log.d("SalesordersetgetCode","-->"+SalesOrderSetGet.getShortCode());
				connection = new BluetoothConnection(macAddress);
				try {
					connection.open();
					printer = ZebraPrinterFactory.getInstance(connection);



					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							helper.updateProgressDialog(R.string.printing_in_progress);
						}
					});

					PrinterLanguage printerLanguage = printer
							.getPrinterControlLanguage();

					Matrix matrix = new Matrix();
					matrix.postRotate(180);

					// //////////////////

					String showLogo = MobileSettingsSetterGetter.getShowLogo();
					Log.d("showLogo", "......" + showLogo +"  "+printerLanguage);

					if (logoStr.matches("logoprint")) {

						if (showLogo.matches("True")) {


							//Bitmap logo = BitmapFactory.decodeResource(context.getResources(),R.drawable.aathilogoedit1);

							logo = LogOutSetGet.getBitmap();

							// Bitmap logo =
							// BitmapFactory.decodeResource(context.getResources(),
							// R.drawable.test2); // don't remove

							int width = 570, imgWidth, imgHeight;
							int diff;

							if (Company.getShortCode().matches("JUBI")) {
								if (printerLanguage == PrinterLanguage.ZPL) {

									Log.d("PrintinZPLPrinterjubii", "ZPL");

									image = Bitmap.createBitmap(logo, 0, 0,
											logo.getWidth(), logo.getHeight(), matrix,
											true);
									printer.printImage(new ZebraImageAndroid(image),
											-200, 0, 200, 100, false);

									connection
											.write("! U1 setvar \"zpl.label_length\" \"90\""
													.getBytes());

									connection
											.write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n"
													.getBytes());

									connection
											.write("! U1 setvar \"ezpl.media_type\" \"continuous\""
													.getBytes());
								} else if (printerLanguage == PrinterLanguage.CPCL) {

									logo = Bitmap.createScaledBitmap(logo,
											logo.getWidth(), logo.getHeight(), true);

									connection
											.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n"
													.getBytes());
									printer.printImage(new ZebraImageAndroid(logo), 0,
											0, -1, -1, false);
								}
							} else {
								Log.d("PrintinZPLPrinter", "ZPL");
								if (SalesOrderSetGet.getShortCode().matches("HEZOMs")) {
								} else {
									image = Bitmap.createBitmap(logo, 0, 0,
											logo.getWidth(), logo.getHeight(), matrix,
											true);
								}

								if (width > logo.getWidth()) {
									diff = (570 - logo.getWidth()) / 2;
									imgWidth = image.getHeight();
									imgHeight = image.getHeight();
									Log.d("logo.getWidth()", "......" + image.getWidth() + "," + image.getHeight());
								} else {
									diff = 0;
									imgWidth = width;
									imgHeight = 250;
									Log.d("width", "......" + width);
								}

								if (printerLanguage == PrinterLanguage.ZPL) {

									connection
											.write(("! U1 setvar \"zpl.label_length\" \""
													+ String.valueOf(imgHeight) + "\"")
													.getBytes());
									connection
											.write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n"
													.getBytes());

									connection
											.write("! U1 setvar \"ezpl.media_type\" \"continuous\""
													.getBytes());
//
									if (SalesOrderSetGet.getShortCode().matches("HEZOM")) {

									} else {
										printer.printImage(new ZebraImageAndroid(image),
												-300, 0, 300, 80, false);
									}

								} else if (printerLanguage == PrinterLanguage.CPCL) {

									logo = Bitmap.createScaledBitmap(logo,
											logo.getWidth(), logo.getHeight(), true);

									connection
											.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n"
													.getBytes());
									printer.printImage(new ZebraImageAndroid(logo),
											diff, 0, imgWidth, imgHeight, false);
								}
							}

						}

						} else {
							Log.d("Logo", "No Logo");
						}

					// /////////////////



					File filepath = context.getFileStreamPath(FILE_NAME);
					printer.sendFileContents(filepath.getAbsolutePath());

					SOTDatabase.init(context);
					String img = SOTDatabase.getSignatureImage();
					Log.d("Print_img", "" + img);

						if (!img.matches("")) {

							// byte[] encodeByte = Base64.decode(img,
							// Base64.DEFAULT);
							// Bitmap photo = BitmapFactory.decodeByteArray(
							// encodeByte, 0, encodeByte.length);

							// //
							byte[] encodeByte;

							byte[] encodeByte1 = Base64.decode(img, Base64.DEFAULT);

							String s;
							try {
								s = new String(encodeByte1, "UTF-8");
								encodeByte = Base64.decode(s, Base64.DEFAULT);
							} catch (Exception e) {
								encodeByte = encodeByte1;
							}

							if (printerLanguage == PrinterLanguage.ZPL) {

								Log.d("PrintinZPLPrinter", "ZPL");

									Bitmap photo = BitmapFactory.decodeByteArray(
											encodeByte, 0, encodeByte.length);
									image = Bitmap.createBitmap(photo, 0, 0,
											photo.getWidth(), photo.getHeight(),
											matrix, true);
									printer.printImage(new ZebraImageAndroid(image),
											-300, 0, 300, 80, false);

								connection
										.write("! U1 setvar \"zpl.label_length\" \"90\""
												.getBytes());

								connection
										.write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n"
												.getBytes());

								connection
										.write("! U1 setvar \"ezpl.media_type\" \"continuous\""
												.getBytes());



							} else if (printerLanguage == PrinterLanguage.CPCL) {

								Bitmap photo = BitmapFactory.decodeByteArray(
										encodeByte, 0, encodeByte.length);

								Log.d("Print in CPCL Printer", "CPCL");
								photo = Bitmap.createScaledBitmap(photo, 300, 80,
										true);

								connection
										.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n"
												.getBytes());
								printer.printImage(new ZebraImageAndroid(photo), 0,
										0, -1, -1, false);
							}

							try {
								bottomPrint();

								File filepath1 = context
										.getFileStreamPath(FILE_NAME);
								printer.sendFileContents(filepath1
										.getAbsolutePath());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

//						else{
//							try {
//								bottomPrint();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//						}

						if (printerLanguage == PrinterLanguage.ZPL) {
							connection
									.write("! U1 setvar \"zpl.label_length\" \"100\""
											.getBytes());

							connection
									.write("! U1 setvar \"ezpl.media_type\" \"continuous\""
											.getBytes());
						}


						connection.close();
						helper.showLongToast(R.string.printed_successfully);



				} catch (ZebraPrinterLanguageUnknownException e) {
					helper.showErrorDialog(e.getMessage());
				} catch (ConnectionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					helper.dismissProgressDialog();
					disableBluetooth();
					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (listener != null) {
								listener.onCompleted();
							}
						}
					});
				}
				Looper.loop();
				Looper.myLooper().quit();
			}
		}).start();
	}

	private boolean disableBluetooth() {
		if ((bluetoothAdapter != null) && bluetoothAdapter.isEnabled()) {
			return bluetoothAdapter.disable();
		}
		return false;
	}

	/**
	 * Return true if Bluetooth is currently enabled and ready for use
	 *
	 * @return true if bluetooth is enabled
	 */
	private boolean isBluetoothEnabled() {
		if (bluetoothAdapter != null) {
			return bluetoothAdapter.isEnabled();
		}
		return false;
	}

	private boolean enableBluetooth() {
		if (bluetoothAdapter == null) {
			helper.showErrorDialog(R.string.no_bluetooth_support);
			return false;
		} else if (!bluetoothAdapter.isEnabled()) {
			return bluetoothAdapter.enable();
		}
		return false;
	}

	private int printTitle(int x, int y, String title,
			StringBuilder cpclConfigLabel) {
		cpclConfigLabel.append(text(x, y += LINE_SPACING, title));
		return y;
	}

	private String text(int x, int y, double number) {
		return text(x, y, String.format("%.2f", number));
	}

	private String text(int x, int y, String text) {
		return new StringBuilder(CMD_TEXT).append(SPACE).append(FONT)
				.append(SPACE).append(FONT_SIZE).append(SPACE).append(x)
				.append(SPACE).append(y).append(SPACE).append(text)
				.append(LINE_SEPARATOR).toString();
	}

	private String horizontalLine(int y, int thickness) {
		return new StringBuilder(CMD_LINE).append(SPACE).append(LEFT_MARGIN)
				.append(SPACE).append(y).append(SPACE).append(RIGHT_MARGIN)
				.append(SPACE).append(y).append(SPACE).append(thickness)
				.append(LINE_SEPARATOR).toString();
	}

	private String verticalLine(int y, int thickness) {
		return new StringBuilder(CMD_LINE).append(SPACE).append(RIGHT_MARGIN)
				.append(SPACE).append(y).append(SPACE).append(LEFT_MARGIN)
				.append(SPACE).append(y).append(SPACE).append(thickness)
				.append(LINE_SEPARATOR).toString();
	}

	public void printMultiReceipt(String socustomercode, String socustomername, String sosno, String sodate, ArrayList<Product> sngleArray, int noofcopies, boolean sngle,double calculate) {
		helper.showProgressDialog(R.string.print,
				R.string.creating_file_for_printing);
		try{

			Log.d("sngleArry",""+sngleArray.size());
			createMultiReceiptFile(socustomercode, socustomername, sosno,
					sodate, sngleArray,noofcopies,sngle,calculate);

			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();

			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}

		}catch (Exception e){
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}




	public void printReceipt(String customercode, String customername,
			String receiptno, String receiptdate, List<ProductDetails> product,
			List<String> sort, String gnrlStngs, int nofcopies,
			boolean isSingleCustomer, List<ProductDetails> footerValue,ArrayList<HashMap<String, String>> salesReturnArr,ArrayList<ProductDetails> mSRHeaderDetailArr)
			throws IOException {
		helper.showProgressDialog(R.string.print,
				R.string.creating_file_for_printing);
		try {

			createReceiptFile(customercode, customername, receiptno,
					receiptdate, product, sort, gnrlStngs, nofcopies,
					isSingleCustomer, footerValue,salesReturnArr,mSRHeaderDetailArr);

			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();

			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}

	public void printConsignmentOrder(String dono, String dodate,
									  String customercode, String customername,
									  List<ProductDetails> product, List<ProductDetails> productdet,
									  int nofcopies,String trantype,String duration) {
		helper.showProgressDialog(context.getString(R.string.print),
				context.getString(R.string.creating_file_for_printing));

		Log.d("TranTypeCheck","-->"+trantype);
		try {
				createConsignmentFile(dono, dodate, customercode, customername,
						product, productdet, nofcopies,trantype,duration);


			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();
			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (IOException e) {
			e.printStackTrace();
			helper.dismissProgressDialog();
			helper.showErrorDialog(R.string.error_creating_file_for_printing);
		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}

	/*private void createConsignmentFile(String dono, String dodate,
									   String customercode, String customername,
									   List<ProductDetails> product, List<ProductDetails> productdet,
									   int nofcopies) throws IOException {

		// Used the calculate the y axis printing position dynamically
		logoStr = "logoprint";
		int totalQuantity = 0;
		Log.d("Consignment logoStr", "deliord" + logoStr);

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		for (int n = 0; n < nofcopies; n++) {
			int y = 0;
			StringBuilder temp = new StringBuilder();
			y = printTitle(180, y, "CONSIGNMENT", temp);
			y = printCompanyDetails(y, temp);

			String cpclConfigLabel = temp.toString();

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "CG No")
					+ text(150, y, " : ") + text(180, y, dono);
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "CG Date")
					+ text(150, y, " : ") + text(180, y, dodate);

			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"Cust Code"));
			cpclConfigLabel += (text(150, y, " : "));
			cpclConfigLabel += (text(180, y, customercode));
			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"Cust Name"));
			cpclConfigLabel += (text(150, y, " : "));

			cpclConfigLabel += (text(
					180,
					y,
					(customername.length() > 25) ? customername
							.substring(0, 24) : customername));

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
				cpclConfigLabel += text(140, y, "Description");
				cpclConfigLabel += text(310, y, "Qty");
				cpclConfigLabel += text(395, y, "Price");
				cpclConfigLabel += text(500, y, "Total");
			}else{
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
				cpclConfigLabel += text(150, y, "Description");
				cpclConfigLabel += text(486, y, "Qty");
			}

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			for (ProductDetails prods : product) {
				if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
							.getItemcode().toString());
					// cpclConfigLabel += text(140, y,
					// prods.getDescription().toString());
					cpclConfigLabel += text(140, y, (prods.getDescription()
							.length() > 10) ? prods.getDescription()
							.substring(0, 9) : prods.getDescription());
					cpclConfigLabel += text(310, y, prods.getQty().toString());
					cpclConfigLabel += text(395, y, prods.getPrice().toString());
					cpclConfigLabel += text(486, y, prods.getTotal().toString());
				}else{
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
							.getItemcode().toString());
					// cpclConfigLabel += text(140, y,
					// prods.getDescription().toString());
					cpclConfigLabel += text(150, y, (prods.getDescription()
							.length() > 10) ? prods.getDescription()
							.substring(0, 9) : prods.getDescription());
					cpclConfigLabel += text(486, y, prods.getQty().toString());

					totalQuantity += prods.getQty().toString().equals("") ? 0 : Integer
							.valueOf(prods.getQty().toString());
				}


			}

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){

				for (ProductDetails prd : productdet) {

					if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Item Disc")
								+ text(140, y, " : ")
								+ text(180, y, prd.getItemdisc().toString());
					}

					if (Double.parseDouble(prd.getTax().toString()) > 0) {

						// cpclConfigLabel += text(310, y, "Sub Total")
						// + text(450, y, " : ")
						// + text(486, y, prd.getSubtotal().toString());
						if (Double.parseDouble(prd.getItemdisc().toString()) == 0) {

							if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
								cpclConfigLabel += text(LEFT_MARGIN,
										y += LINE_SPACING, "Bill Disc")
										+ text(140, y, " : ")
										+ text(180, y, prd.getBilldisc().toString());
							}
							if (Double.parseDouble(prd.getBilldisc().toString()) == 0
									&& Double.parseDouble(prd.getItemdisc()
									.toString()) == 0) {
								cpclConfigLabel += text(310, y += LINE_SPACING,
										"Sub Total")
										+ text(450, y, " : ")
										+ text(486, y, prd.getSubtotal().toString());
							} else {

								cpclConfigLabel += text(310, y, "Sub Total")
										+ text(450, y, " : ")
										+ text(486, y, prd.getSubtotal().toString());
							}
						} else {

							cpclConfigLabel += text(310, y, "Sub Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getSubtotal().toString());
						}
					}

					if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
						// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						// "Bill Disc")
						// + text(140, y, " : ")
						// + text(180, y, prd.getBilldisc().toString());
						if (Double.parseDouble(prd.getItemdisc().toString()) != 0) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Bill Disc")
									+ text(140, y, " : ")
									+ text(180, y, prd.getBilldisc().toString());
						} else {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"") + text(140, y, "") + text(180, y, "");
						}
					}

					if (Double.parseDouble(prd.getTax().toString()) > 0) {

						if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"") + text(140, y, "") + text(180, y, "");
						}

						cpclConfigLabel += text(310, y, "Tax")
								+ text(450, y, " : ")
								+ text(486, y, prd.getTax().toString());
					}
					if (Double.parseDouble(prd.getTax().toString()) == 0) {

						cpclConfigLabel += text(310, y, "") + text(450, y, "")
								+ text(486, y, "");

						if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
							cpclConfigLabel += text(310, y += LINE_SPACING,
									"Net Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getNettot().toString());

						} else {
							cpclConfigLabel += text(310, y, "Net Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getNettot().toString());
						}
					} else {
						cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
								+ text(450, y, " : ")
								+ text(486, y, prd.getNettot().toString());
					}
				}
			}else{

				cpclConfigLabel += text(310, y += LINE_SPACING, "Total Qty")
						+ text(450, y, " : ")
						+ text(486, y, totalQuantity);

			}
			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			// Just append everything and create a single string
			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
					+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

			*//*
			 * FileOutputStream os = context.openFileOutput(FILE_NAME,
			 * Context.MODE_PRIVATE);
			 *//*
			os.write(cpclConfigLabel.getBytes());
			os.flush();
		}
		os.close();
	}*/



	private void createConsignmentFile(String dono, String dodate,
									   String customercode, String customername,
									   List<ProductDetails> product, List<ProductDetails> productdet,
									   int nofcopies,String trantype,String duration) throws IOException {

		// Used the calculate the y axis printing position dynamically
		logoStr = "logoprint";
		int totalQuantity = 0;
		Log.d("Consignment logoStr", "deliord" + logoStr);
		Log.d("trantypeCheck","-->"+trantype);
		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		String decimal = MobileSettingsSetterGetter.getDecimalPoints();
		for (int n = 0; n < nofcopies; n++) {
			int y = 0;
			StringBuilder temp = new StringBuilder();
			if (trantype.matches("O")) {
				y = printTitle(180, y, "CONSIGNMENT", temp);
			} else {
				y = printTitle(180, y, "CONSIGNMENT RETURN", temp);
			}

			y = printCompanyDetails(y, temp);

			String cpclConfigLabel = temp.toString();

			if (trantype.matches("O")) {

				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "CG No")
						+ text(200, y, " : ") + text(220, y, dono);
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "CG Date")
						+ text(200, y, " : ") + text(220, y, dodate);

				cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
						"Cust Code"));
				cpclConfigLabel += (text(200, y, " : "));
				cpclConfigLabel += (text(220, y, customercode));
				cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
						"Cust Name"));
				cpclConfigLabel += (text(200, y, " : "));

				cpclConfigLabel += (text(
						220,
						y,
						(customername.length() > 25) ? customername
								.substring(0, 24) : customername));
				cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
						"Duration Days"));
				cpclConfigLabel += (text(200, y, " : "));
				cpclConfigLabel += (text(220, y, duration));


			} else {
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "CG-Return No")
						+ text(200, y, " : ") + text(220, y, dono);
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "CG-Return Date")
						+ text(200, y, " : ") + text(220, y, dodate);
				cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
						"Cust Code"));
				cpclConfigLabel += (text(200, y, " : "));
				cpclConfigLabel += (text(220, y, customercode));
				cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
						"Cust Name"));
				cpclConfigLabel += (text(200, y, " : "));

				cpclConfigLabel += (text(
						220,
						y,
						(customername.length() > 25) ? customername
								.substring(0, 24) : customername));
				cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
						"Duration Days"));
				cpclConfigLabel += (text(200, y, " : "));
				cpclConfigLabel += (text(220, y, duration));
			}


			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			if (trantype.matches("O")) {
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
				cpclConfigLabel += text(140, y, "Description");
				cpclConfigLabel += text(310, y, "Qty");
				cpclConfigLabel += text(395, y, "Price");
				cpclConfigLabel += text(500, y, "Total");
			} else {
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
				cpclConfigLabel += text(150, y, "Description");
				cpclConfigLabel += text(486, y, "Qty");
			}


			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			for (ProductDetails prods : product) {
				if (trantype.matches("O")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
							.getItemcode().toString());
					// cpclConfigLabel += text(140, y,
					// prods.getDescription().toString());
					cpclConfigLabel += text(
							110,
							y,
							(prods.getDescription().length() > 33) ? prods
									.getDescription().substring(0,
											32) : prods
									.getDescription());
					cpclConfigLabel += text(300, y += LINE_SPACING,
							prods.getQty().toString());
					double value = Double.parseDouble(prods
							.getPrice().toString());
					String values = String.format("%."+decimal+"f",value);
					cpclConfigLabel += text(395, y, values);
					cpclConfigLabel += text(486, y, prods.getTotal().toString());

//					if(prods.getConsignmentNumber().toString()!=""){
//						cpclConfigLabel += (text(LEFT_MARGIN, y += 395,
//								prods.getConsignmentNumber()));
//					}

				} else {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
							.getItemcode().toString());
					// cpclConfigLabel += text(140, y,
					// prods.getDescription().toString());
					cpclConfigLabel += text(140,
							y,
							(prods.getDescription().length() > 35) ? prods
									.getDescription().substring(0,
											34) : prods
									.getDescription());

					if (prods.getDescription().length() > 35) {

						String custlastname = prods.getDescription().substring(34);

						Log.d("Custnameeee", custlastname);

						cpclConfigLabel += (text(
								140,
								y += LINE_SPACING,
								(custlastname.length() > 35) ? custlastname
										.substring(0, 34) : custlastname));
					}

					cpclConfigLabel += text(486, y += LINE_SPACING,
							prods.getQty().toString());

					totalQuantity += prods.getQty().toString().equals("") ? 0 : Integer
							.valueOf(prods.getQty().toString());

					if(trantype.matches("COR")) {
						Log.d("getConsignmentNumber", "-->" + prods.getConsignmentNumber());
						if (prods.getConsignmentNumber().toString().matches("")) {
						} else {
							cpclConfigLabel += (text(350, y += LINE_SPACING,
									prods.getConsignmentNumber()));
						}
					}

				}
			}

				cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);


				for (ProductDetails prd : productdet) {

					if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Item Disc")
								+ text(140, y, " : ")
								+ text(180, y, prd.getItemdisc().toString());
					}

					if (Double.parseDouble(prd.getTax().toString()) > 0) {

						// cpclConfigLabel += text(310, y, "Sub Total")
						// + text(450, y, " : ")
						// + text(486, y, prd.getSubtotal().toString());
						if (Double.parseDouble(prd.getItemdisc().toString()) == 0) {

							if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
								cpclConfigLabel += text(LEFT_MARGIN,
										y += LINE_SPACING, "Bill Disc")
										+ text(140, y, " : ")
										+ text(180, y, prd.getBilldisc().toString());
							}
							if (Double.parseDouble(prd.getBilldisc().toString()) == 0
									&& Double.parseDouble(prd.getItemdisc()
									.toString()) == 0) {
								cpclConfigLabel += text(310, y += LINE_SPACING,
										"Sub Total")
										+ text(450, y, " : ")
										+ text(486, y, prd.getSubtotal().toString());
							} else {

								cpclConfigLabel += text(310, y, "Sub Total")
										+ text(450, y, " : ")
										+ text(486, y, prd.getSubtotal().toString());
							}
						} else {

							cpclConfigLabel += text(310, y, "Sub Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getSubtotal().toString());
						}
					}

					if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
						// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						// "Bill Disc")
						// + text(140, y, " : ")
						// + text(180, y, prd.getBilldisc().toString());
						if (Double.parseDouble(prd.getItemdisc().toString()) != 0) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Bill Disc")
									+ text(140, y, " : ")
									+ text(180, y, prd.getBilldisc().toString());
						} else {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"") + text(140, y, "") + text(180, y, "");
						}
					}

					if (Double.parseDouble(prd.getTax().toString()) > 0) {

						if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"") + text(140, y, "") + text(180, y, "");
						}

						cpclConfigLabel += text(310, y, "Tax")
								+ text(450, y, " : ")
								+ text(486, y, prd.getTax().toString());
					}
					if (Double.parseDouble(prd.getTax().toString()) == 0) {

						cpclConfigLabel += text(310, y, "") + text(450, y, "")
								+ text(486, y, "");

						if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
							cpclConfigLabel += text(310, y += LINE_SPACING,
									"Net Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getNettot().toString());

						} else {
							cpclConfigLabel += text(310, y, "Net Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getNettot().toString());
						}
					} else {
						cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
								+ text(450, y, " : ")
								+ text(486, y, prd.getNettot().toString());
					}
				}

				cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

				// Just append everything and create a single string
				cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
						+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

			/*
			 * FileOutputStream os = context.openFileOutput(FILE_NAME,
			 * Context.MODE_PRIVATE);
			 */
				os.write(cpclConfigLabel.getBytes());
				os.flush();
			}
			os.close();
		}

	private void createMultiReceiptFile(String customercode, String customername, String receiptno, String receiptDte, ArrayList<Product> sngleArray, int noofcopies, boolean isSingleCustomer, double calculate) throws IOException {
		String invoiceprintdetail = SalesOrderSetGet.getInvoiceprintdetail();
		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);

		logoStr = "logoprint";
		String  lpayment = "", totalOutStanding = "", bank_Code = "", bank_Name = "", check_No = "", check_Date = "";
		String showCustomerCode = MobileSettingsSetterGetter
				.getShowCustomerCode();
		String showCustomerName = MobileSettingsSetterGetter
				.getShowCustomerName();
		String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
		String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
		String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
		String showCustomerPhone = MobileSettingsSetterGetter
				.getShowCustomerPhone();
		String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
		String showCustomerEmail = MobileSettingsSetterGetter
				.getShowCustomerEmail();
		String showCustomerTerms = MobileSettingsSetterGetter
				.getShowCustomerTerms();
		String showTotalOutstanding = MobileSettingsSetterGetter
				.getShowTotalOutstanding();
		String showProductFullName = MobileSettingsSetterGetter
				.getShowProductFullName();
		String showFooter = MobileSettingsSetterGetter.getShowFooter();

		String printSalesReturnDetailOnReceipt  =  MobileSettingsSetterGetter.getPrintSalesReturnDetailOnReceipt();
		String printSalesReturnSummaryOnReceipt  =  MobileSettingsSetterGetter.getPrintSalesReturnSummaryOnReceipt();
		String decimal = MobileSettingsSetterGetter.getDecimalPoints();

		Log.d("showProductFullName", "mm"+showProductFullName+"");

		for (int n = 0; n < noofcopies; n++) {
			int y = 0;
			int s = 1;
			String cpclConfigLabel = "";
			if (isSingleCustomer) {

				StringBuilder temp = new StringBuilder();
				if (Company.getShortCode().matches("JUBIcc")) {
					y = printTitle(250, y, "RECEIPT", temp);
				} else {
					y = printTitle(228, y, "RECEIPT", temp);
				}
				y = printCompanyDetails(y, temp);
				cpclConfigLabel = temp.toString();


				if(Company.getShortCode().matches("JUBIcc")){
					if (showCustomerName.matches("True")) {

						if (!customername.matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Cust Name"));
							cpclConfigLabel += (text(150, y, " : "));

							cpclConfigLabel += (text(
									180,
									y,
									(customername.length() > 25) ? customername
											.substring(0, 24) : customername));

							if (customername.length() > 25) {

								String custlastname = customername.substring(24);

								Log.d("Custnameeee", custlastname);

								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(custlastname.length() > 25) ? custlastname
												.substring(0, 24) : custlastname));
							}
						}

					}


				}else {

					Log.d("showaddress1check",""+showAddress1);


					if (showCustomerCode.matches("True")) {

						if (!customercode.matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Cust Code"));
							cpclConfigLabel += (text(150, y, " : "));
							cpclConfigLabel += (text(180, y, customercode));
						}
					}

					if (showCustomerName.matches("True")) {

//					cpclConfigLabel += (text(LEFT_MARGIN,
//							y += LINE_SPACING, "^XA^CI28^A@N,50,50,E:SIMSUN.FNT^FO50,110^FDChinese 中文字符^FS^XZ\n"));

						if (!customername.matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Cust Name"));
							cpclConfigLabel += (text(150, y, " : "));

							cpclConfigLabel += (text(
									180,
									y,
									(customername.length() > 25) ? customername
											.substring(0, 24) : customername));

							if (customername.length() > 25) {

								String custlastname = customername.substring(24);

								Log.d("CustnameeeeChck", custlastname);

								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(custlastname.length() > 25) ? custlastname
												.substring(0, 24) : custlastname));
							}
						}

					}

					if (showAddress1.matches("True")) {

						if (!CustomerSetterGetter.getCustomerAddress1().matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Address 1"));
							cpclConfigLabel += (text(150, y, " : "));

							customername = CustomerSetterGetter
									.getCustomerAddress1();

							Log.d("CustnameeeeAddress1", customername);

							cpclConfigLabel += (text(
									180,
									y,
									(customername.length() > 25) ? customername
											.substring(0, 24) : customername));

							if (customername.length() > 25) {

								String custlastname = customername.substring(24);


								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(custlastname.length() > 25) ? custlastname
												.substring(0, 24) : custlastname));
							}
						}
					}

					if (showAddress2.matches("True")) {
						if (!CustomerSetterGetter.getCustomerAddress2().matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Address 2"));
							cpclConfigLabel += (text(150, y, " : "));

							customername = CustomerSetterGetter
									.getCustomerAddress2();

							Log.d("CustnameeeeAddres2", customername);

							cpclConfigLabel += (text(
									180,
									y,
									(customername.length() > 25) ? customername
											.substring(0, 24) : customername));

							if (customername.length() > 25) {

								String custlastname = customername.substring(24);

								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(custlastname.length() > 25) ? custlastname
												.substring(0, 24) : custlastname));
							}
						}
					}

					if (showAddress3.matches("True")) {
						if (!CustomerSetterGetter.getCustomerAddress3().matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Address 3"));
							cpclConfigLabel += (text(150, y, " : "));

							customername = CustomerSetterGetter
									.getCustomerAddress3();

							Log.d("CustnameeeeAddres3", customername);

							cpclConfigLabel += (text(
									180,
									y,
									(customername.length() > 25) ? customername
											.substring(0, 24) : customername));

							if (customername.length() > 25) {

								String custlastname = customername.substring(24);

								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(custlastname.length() > 25) ? custlastname
												.substring(0, 24) : custlastname));
							}
						}
					}

					if (showCustomerPhone.matches("True")) {
						if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Phone No")
									+ text(150, y, " : ")
									+ text(180, y,
									CustomerSetterGetter.getCustomerPhone());
						}
					}

					if (showCustomerHP.matches("True")) {
						if (!CustomerSetterGetter.getCustomerHP().matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Head Phone")
									+ text(150, y, " : ")
									+ text(180, y,
									CustomerSetterGetter.getCustomerHP());
						}
					}

					if (showCustomerEmail.matches("True")) {
						if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Email")
									+ text(150, y, " : ")
									+ text(180, y,
									CustomerSetterGetter.getCustomerEmail());
						}
					}

					if (showCustomerTerms.matches("True")) {
						if (!CustomerSetterGetter.getCustomerTerms().matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Terms")
									+ text(150, y, " : ")
									+ text(180, y,
									CustomerSetterGetter.getCustomerTerms());
						}
					}
				}
			}

			if(sngleArray.size()>0) {

				if (!isSingleCustomer) {
					StringBuilder temp = new StringBuilder();
					if (Company.getShortCode().matches("JUBIcc")) {
						y = printTitle(250, y, "RECEIPT", temp);
					} else {
						y = printTitle(228, y, "RECEIPT", temp);
					}
					y = printCompanyDetails(y, temp);
					cpclConfigLabel = temp.toString();

					if (Company.getShortCode().matches("JUBIcc")) {

						cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
								"Cust Name"));
						cpclConfigLabel += (text(150, y, " : "));

						cpclConfigLabel += (text(
								180,
								y,
								(customername.length() > 25) ? customername
										.substring(0, 24) : customername));

					} else {

						cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
								"Cust Code"));
						cpclConfigLabel += (text(150, y, " : "));
						cpclConfigLabel += (text(180, y, customercode));
						cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
								"Cust Name"));
						cpclConfigLabel += (text(150, y, " : "));

						cpclConfigLabel += (text(
								180,
								y,
								(customername.length() > 25) ? customername
										.substring(0, 24) : customername));
					}
				}

				if (!receiptno.matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Receipt No")
							+ text(150, y, " : ")
							+ text(180, y, receiptno);
				}
				if (!receiptDte.matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN,
							y += LINE_SPACING, "Receipt Date")
							+ text(150, y, " : ")
							+ text(180, y, receiptDte);
				}

				cpclConfigLabel += horizontalLine(y += LINE_SPACING,
						LINE_THICKNESS);
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SlNo");
				cpclConfigLabel += text(100, y, "Invoice No");
				cpclConfigLabel += text(290, y, "Invoice Date");
				cpclConfigLabel += text(460, y, "Amount");

				cpclConfigLabel += horizontalLine(y += LINE_SPACING,
						LINE_THICKNESS);
				for (int i = 0; i < sngleArray.size(); i++) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, sngleArray.get(i).getNo().toString());
					cpclConfigLabel += text(100, y, sngleArray.get(i).getCode().toString());
					cpclConfigLabel += text(300, y , sngleArray.get(i).getTranDate().toString());
					cpclConfigLabel += text(460, y , sngleArray.get(i).getDescription().toString());

				}

				cpclConfigLabel += horizontalLine(y += LINE_SPACING,
						LINE_THICKNESS);
				if (!In_Cash.getPay_Mode().matches("")
						|| !In_Cash.getPay_Mode().matches("null")
						|| !In_Cash.getPay_Mode().matches(null)) {
					String pay_Mode = In_Cash.getPay_Mode();
					cpclConfigLabel += (text(LEFT_MARGIN,
							y += LINE_SPACING, "Pay Mode"));
					cpclConfigLabel += (text(150, y, " : "));
					cpclConfigLabel += (text(180, y, pay_Mode));
				}
				if (calculate > 0) {
					cpclConfigLabel += text(310, y, "Paid Amount")
							+ text(450, y, " : ")
							+ text(470, y, calculate);
				}

				cpclConfigLabel += horizontalLine(y += LINE_SPACING,
						LINE_THICKNESS);
				if (!In_Cash.getPay_Mode().matches("")
						|| !In_Cash.getPay_Mode().matches("null")
						|| !In_Cash.getPay_Mode().matches(null)) {
					String pay_Mode = In_Cash.getPay_Mode();

					if (pay_Mode.matches("Cheque")
							|| pay_Mode.matches("cheque")
							|| pay_Mode.matches("CHEQUE")) {

						bank_Code = In_Cash.getBank_code();
						// bank_Name = In_Cash.getBank_Name();
						check_No = In_Cash.getCheck_No();
						check_Date = In_Cash.getCheck_Date();

						StringTokenizer tk = new StringTokenizer(check_Date);

						String date = tk.nextToken();

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Bank Code");
						cpclConfigLabel += text(200, y, "Cheque No");
						cpclConfigLabel += text(420, y, "Cheque Date");
						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
								LINE_THICKNESS);

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								bank_Code);
						cpclConfigLabel += text(200, y, check_No);
						cpclConfigLabel += text(420, y, date);

						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
								LINE_THICKNESS);

					}

				}


			}

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			// Just append everything and create a single string
			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
					+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

			/*
			 * FileOutputStream os = context.openFileOutput(FILE_NAME,
			 * Context.MODE_PRIVATE);
			 */
			os.write(cpclConfigLabel.getBytes());
			os.flush();
		}
		os.close();
	}

	private void createReceiptFile(String customercode, String customername,
			String receiptno, String receiptdate,
			List<ProductDetails> receipts, List<String> sort, String gnrlStngs,
			int nofcopies, boolean isSingleCustomer,
			List<ProductDetails> footerValue,ArrayList<HashMap<String, String>> salesReturnArr,ArrayList<ProductDetails> mSRHeaderDetailArr) throws IOException {
		String invoiceprintdetail = SalesOrderSetGet.getInvoiceprintdetail();
		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		String  lpayment = "", totalOutStanding = "", bank_Code = "", bank_Name = "", check_No = "", check_Date = "";

		logoStr = "logoprint";
		String taxtype ="";
		footerArr.clear();
		footerArr = footerValue;
		String taxType="";

		Log.d("rec logoStr", "receipt" + logoStr);

		String showCustomerCode = MobileSettingsSetterGetter
				.getShowCustomerCode();
		String showCustomerName = MobileSettingsSetterGetter
				.getShowCustomerName();
		String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
		String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
		String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
		String showCustomerPhone = MobileSettingsSetterGetter
				.getShowCustomerPhone();
		String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
		String showCustomerEmail = MobileSettingsSetterGetter
				.getShowCustomerEmail();
		String showCustomerTerms = MobileSettingsSetterGetter
				.getShowCustomerTerms();
		String showTotalOutstanding = MobileSettingsSetterGetter
				.getShowTotalOutstanding();
		String showProductFullName = MobileSettingsSetterGetter
				.getShowProductFullName();
		String showFooter = MobileSettingsSetterGetter.getShowFooter();

		String printSalesReturnDetailOnReceipt  =  MobileSettingsSetterGetter.getPrintSalesReturnDetailOnReceipt();
		String printSalesReturnSummaryOnReceipt  =  MobileSettingsSetterGetter.getPrintSalesReturnSummaryOnReceipt();
		String decimal = MobileSettingsSetterGetter.getDecimalPoints();
		/*String showuserphoneno = MobileSettingsSetterGetter.getShowUserPhoneNo();
		  
		  String loginphoneno  = SalesOrderSetGet.getLoginPhoneNo();
		  
		  String username = SupplierSetterGetter.getUsername();*/
		Log.d("showProductFullName", "mm"+showProductFullName+"");

		for (int n = 0; n < nofcopies; n++) {
			int y = 0;
			int s = 1;
			String cpclConfigLabel = "";
			if (isSingleCustomer) {

				StringBuilder temp = new StringBuilder();
				if(Company.getShortCode().matches("JUBIcc")){
					y = printTitle(250, y, "RECEIPT", temp);
				}else{
					y = printTitle(228, y, "RECEIPT", temp);
				}
				y = printCompanyDetails(y, temp);
				cpclConfigLabel = temp.toString();
			/*	if (showuserphoneno != null && !showuserphoneno.isEmpty() && loginphoneno != null && !loginphoneno.isEmpty()) {   
				     if (showuserphoneno.matches("True") && !loginphoneno.matches("null")) {
				      cpclConfigLabel+= text(LEFT_MARGIN, y += LINE_SPACING,
				        "Contact ")
				        + text(150, y, " : ")
				        + text(180, y, username +" "+loginphoneno);
				        
				     }   
				    }*/

				if(Company.getShortCode().matches("JUBIcc")){
					if (showCustomerName.matches("True")) {

						if (!customername.matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Cust Name"));
							cpclConfigLabel += (text(150, y, " : "));

							cpclConfigLabel += (text(
									180,
									y,
									(customername.length() > 25) ? customername
											.substring(0, 24) : customername));

							if (customername.length() > 25) {

								String custlastname = customername.substring(24);

								Log.d("Custnameeee", custlastname);

								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(custlastname.length() > 25) ? custlastname
												.substring(0, 24) : custlastname));
							}
						}

					}


				}else {

					Log.d("showaddress1check",""+showAddress1);


					if (showCustomerCode.matches("True")) {

						if (!customercode.matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Cust Code"));
							cpclConfigLabel += (text(150, y, " : "));
							cpclConfigLabel += (text(180, y, customercode));
						}
					}

					if (showCustomerName.matches("True")) {

//					cpclConfigLabel += (text(LEFT_MARGIN,
//							y += LINE_SPACING, "^XA^CI28^A@N,50,50,E:SIMSUN.FNT^FO50,110^FDChinese 中文字符^FS^XZ\n"));

						if (!customername.matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Cust Name"));
							cpclConfigLabel += (text(150, y, " : "));

							cpclConfigLabel += (text(
									180,
									y,
									(customername.length() > 25) ? customername
											.substring(0, 24) : customername));

							if (customername.length() > 25) {

								String custlastname = customername.substring(24);

								Log.d("CustnameeeeChck", custlastname);

								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(custlastname.length() > 25) ? custlastname
												.substring(0, 24) : custlastname));
							}
						}

					}

					if (showAddress1.matches("True")) {

						if (!CustomerSetterGetter.getCustomerAddress1().matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Address 1"));
							cpclConfigLabel += (text(150, y, " : "));

							customername = CustomerSetterGetter
									.getCustomerAddress1();

							Log.d("CustnameeeeAddress1", customername);

							cpclConfigLabel += (text(
									180,
									y,
									(customername.length() > 25) ? customername
											.substring(0, 24) : customername));

							if (customername.length() > 25) {

								String custlastname = customername.substring(24);


								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(custlastname.length() > 25) ? custlastname
												.substring(0, 24) : custlastname));
							}
						}
					}

					if (showAddress2.matches("True")) {
						if (!CustomerSetterGetter.getCustomerAddress2().matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Address 2"));
							cpclConfigLabel += (text(150, y, " : "));

							customername = CustomerSetterGetter
									.getCustomerAddress2();

							Log.d("CustnameeeeAddres2", customername);

							cpclConfigLabel += (text(
									180,
									y,
									(customername.length() > 25) ? customername
											.substring(0, 24) : customername));

							if (customername.length() > 25) {

								String custlastname = customername.substring(24);

								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(custlastname.length() > 25) ? custlastname
												.substring(0, 24) : custlastname));
							}
						}
					}

					if (showAddress3.matches("True")) {
						if (!CustomerSetterGetter.getCustomerAddress3().matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Address 3"));
							cpclConfigLabel += (text(150, y, " : "));

							customername = CustomerSetterGetter
									.getCustomerAddress3();

							Log.d("CustnameeeeAddres3", customername);

							cpclConfigLabel += (text(
									180,
									y,
									(customername.length() > 25) ? customername
											.substring(0, 24) : customername));

							if (customername.length() > 25) {

								String custlastname = customername.substring(24);

								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(custlastname.length() > 25) ? custlastname
												.substring(0, 24) : custlastname));
							}
						}
					}

					if (showCustomerPhone.matches("True")) {
						if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Phone No")
									+ text(150, y, " : ")
									+ text(180, y,
									CustomerSetterGetter.getCustomerPhone());
						}
					}

					if (showCustomerHP.matches("True")) {
						if (!CustomerSetterGetter.getCustomerHP().matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Head Phone")
									+ text(150, y, " : ")
									+ text(180, y,
									CustomerSetterGetter.getCustomerHP());
						}
					}

					if (showCustomerEmail.matches("True")) {
						if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Email")
									+ text(150, y, " : ")
									+ text(180, y,
									CustomerSetterGetter.getCustomerEmail());
						}
					}

					if (showCustomerTerms.matches("True")) {
						if (!CustomerSetterGetter.getCustomerTerms().matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Terms")
									+ text(150, y, " : ")
									+ text(180, y,
									CustomerSetterGetter.getCustomerTerms());
						}
					}
				}
			}

			for (ProductDetails receipt : receipts) {
				if (!isSingleCustomer) {
					StringBuilder temp = new StringBuilder();
					if (Company.getShortCode().matches("JUBIcc")) {
						y = printTitle(250, y, "RECEIPT", temp);
					} else {
						y = printTitle(228, y, "RECEIPT", temp);
					}
					y = printCompanyDetails(y, temp);
					cpclConfigLabel = temp.toString();
					/*if (showuserphoneno != null && !showuserphoneno.isEmpty() && loginphoneno != null && !loginphoneno.isEmpty()) {   
					      if (showuserphoneno.matches("True") && !loginphoneno.matches("null")) {
					       cpclConfigLabel+= text(LEFT_MARGIN, y += LINE_SPACING,
					         "Contact ")
					         + text(150, y, " : ")
					         + text(180, y, username +" "+loginphoneno);
					         
					      }   
					     }*/

					if (Company.getShortCode().matches("JUBIcc")) {

						cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
								"Cust Name"));
						cpclConfigLabel += (text(150, y, " : "));

						cpclConfigLabel += (text(
								180,
								y,
								(customername.length() > 25) ? customername
										.substring(0, 24) : customername));

					} else {

						cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
								"Cust Code"));
						cpclConfigLabel += (text(150, y, " : "));
						cpclConfigLabel += (text(180, y, customercode));
						cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
								"Cust Name"));
						cpclConfigLabel += (text(150, y, " : "));

						cpclConfigLabel += (text(
								180,
								y,
								(customername.length() > 25) ? customername
										.substring(0, 24) : customername));
					}
				}

				if (receiptno.matches(receipt.getItemno())) {
					Log.d("receipt.getItemno()", receipt.getItemno());
					if (!receiptno.matches("")) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Receipt No")
								+ text(150, y, " : ")
								+ text(180, y, receiptno);

					}
					if (receiptdate.matches(receipt.getItemdate())) {
						if (!receiptdate.matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, "Receipt Date")
									+ text(150, y, " : ")
									+ text(180, y, receiptdate);

							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Invoice No")
									+ text(150, y, " : ")
									+ text(180, y, receipt.getItemno());
						}
					} else {
						if (!receiptdate.matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, "Receipt Date")
									+ text(150, y, " : ")
									+ text(180, y, receiptdate);
						}
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Invoice Date")
								+ text(150, y, " : ")
								+ text(180, y, receipt.getItemdate());
					}
				} else {
					Log.d("receiptno", receiptno);
					if (!receiptno.matches("")) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Receipt No")
								+ text(150, y, " : ")
								+ text(180, y, receiptno);

					}
					if (receiptdate.matches(receipt.getItemdate())) {
						if (!receiptdate.matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, "Receipt Date")
									+ text(150, y, " : ")
									+ text(180, y, receiptdate);
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Invoice No")
									+ text(150, y, " : ")
									+ text(180, y, receipt.getItemno());
						}
					} else {
						if (!receiptdate.matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, "Receipt Date")
									+ text(150, y, " : ")
									+ text(180, y, receiptdate);
						}
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Invoice No")
								+ text(150, y, " : ")
								+ text(180, y, receipt.getItemno());
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Invoice Date")
								+ text(150, y, " : ")
								+ text(180, y, receipt.getItemdate());
					}

				}

				cpclConfigLabel += horizontalLine(y += LINE_SPACING,
						LINE_THICKNESS);
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
				cpclConfigLabel += text(70, y, "Description");
				cpclConfigLabel += text(280, y, "Qty");
				cpclConfigLabel += text(365, y, "Price");
				cpclConfigLabel += text(500, y, "Total");

				cpclConfigLabel += horizontalLine(y += LINE_SPACING,
						LINE_THICKNESS);

				if (gnrlStngs.matches("C") || gnrlStngs.matches("S")) {

					for (ProdDetails products : receipt.getProductsDetails()) {

						if ((products.getSortproduct().matches(""))
								|| (products.getSortproduct().matches("0"))) {
							int i = 1;

							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, String.valueOf(i));
							if (showProductFullName.matches("True")) {
								if(Company.getShortCode().matches("JUBI")){
									cpclConfigLabel += text(
											70,
											y,
											(products.getDescription().length() > 18) ? products
													.getDescription().substring(0, 17)
													: products.getDescription());
									cpclConfigLabel += text(280, y, products.getQty()
											.toString());
								}else{
									cpclConfigLabel += text(
											70,
											y,
											(products.getDescription().length() > 31) ? products
													.getDescription().substring(0,
															30) : products
													.getDescription());
									cpclConfigLabel += text(280, y += LINE_SPACING,
											products.getQty().toString());
								}

							} else {

								if(Company.getShortCode().matches("JUBI")){
									cpclConfigLabel += text(
											70,
											y,
											(products.getDescription().length() > 18) ? products
													.getDescription().substring(0, 17)
													: products.getDescription());
									cpclConfigLabel += text(280, y, products.getQty()
											.toString());
								}else{
									cpclConfigLabel += text(
											70,
											y,
											(products.getDescription().length() > 10) ? products
													.getDescription().substring(0,
															9) : products
													.getDescription());
									cpclConfigLabel += text(280, y, products
											.getQty().toString());
								}

							}
							double value = Double.parseDouble(products
									.getPrice().toString());
							String values = String.format("%." + decimal + "f", value);
							cpclConfigLabel += text(365, y, values);
							cpclConfigLabel += text(486, y, products.getTotal()
									.toString());

							// Log.d("products.getFocqty() 1", ""+(int)
							// products.getFocqty());
							Log.d("products", "-->" + products.getFocqty());
							if (products.getFocqty() > 0) {
								cpclConfigLabel += text(70, y += LINE_SPACING,
										"Foc")
										+ text(210, y, " : ")
										+ text(280, y,
										(int) products.getFocqty() + "(FOC)");
							}

							// Log.d("products.getExchangeqty() 1", ""+(int)
							// products.getExchangeqty());

							if (products.getExchangeqty() > 0) {
								cpclConfigLabel += text(70, y += LINE_SPACING,
										"Exchange")
										+ text(210, y, " : ")
										+ text(280, y,
										(int) products.getExchangeqty());
							}

							s += i;
							i++;
						}
					}
					for (int i = 0; i < sort.size(); i++) {
						String catorsub = sort.get(i).toString();
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								catorsub);
						for (ProdDetails prods : receipt.getProductsDetails()) {
							if (catorsub.equalsIgnoreCase(prods.getSortproduct()
									.toString())) {
								cpclConfigLabel += text(LEFT_MARGIN,
										y += LINE_SPACING, String.valueOf(s));

								if (showProductFullName.matches("True")) {
									if(Company.getShortCode().matches("JUBI")){
										cpclConfigLabel += text(
												70,
												y,
												(prods.getDescription().length() > 18) ? prods
														.getDescription().substring(0, 17)
														: prods.getDescription());
										cpclConfigLabel += text(280, y, prods.getQty()
												.toString());
									}else{
										cpclConfigLabel += text(
												70,
												y,
												(prods.getDescription().length() > 31) ? prods
														.getDescription().substring(0,
																30) : prods
														.getDescription());
										cpclConfigLabel += text(280, y += LINE_SPACING,
												prods.getQty().toString());
									}

								} else {
									if(Company.getShortCode().matches("JUBI")){
										cpclConfigLabel += text(
												70,
												y,
												(prods.getDescription().length() > 18) ? prods
														.getDescription().substring(0, 17)
														: prods.getDescription());
										cpclConfigLabel += text(280, y, prods.getQty()
												.toString());
									}else{
										cpclConfigLabel += text(
												70,
												y,
												(prods.getDescription().length() > 10) ? prods
														.getDescription()
														.substring(0, 9) : prods
														.getDescription());
										cpclConfigLabel += text(280, y, prods
												.getQty().toString());
									}

								}
								double value = Double.parseDouble(prods
										.getPrice().toString());
								String values = String.format("%." + decimal + "f", value);
								cpclConfigLabel += text(365, y, values);
								cpclConfigLabel += text(486, y, prods
										.getTotal().toString());

								// Log.d("products.getFocqty() 2", ""+(int)
								// prods.getFocqty());

								if (prods.getFocqty() > 0) {
									cpclConfigLabel += text(70,
											y += LINE_SPACING, "Foc")
											+ text(210, y, " : ")
											+ text(280, y,
											(int) prods.getFocqty());
								}

								// Log.d("products.getExchangeqty() 2", ""+(int)
								// prods.getExchangeqty());

								if (prods.getExchangeqty() > 0) {
									cpclConfigLabel += text(70,
											y += LINE_SPACING, "Exchange")
											+ text(210, y, " : ")
											+ text(280, y,
											(int) prods
													.getExchangeqty());
								}

								s++;
							}
						}
					}

				} else {
					for (ProdDetails product : receipt.getProductsDetails()) {
						String invoicenum = product.getItemnum().toString();
						taxType = product.getTaxType().toString();
						Log.d("getTaxTypeCheck", "-->" + taxType);


						Log.d("InvoiceNumber", "-->" + invoicenum + "  " + receipt.getItemno().toString());

						if (invoicenum.matches(receipt.getItemno().toString())) {

							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, product.getSno()
											.toString());
							if (showProductFullName.matches("True")) {
								if(Company.getShortCode().matches("JUBI")){
									cpclConfigLabel += text(
											70,
											y,
											(product.getDescription().length() > 18) ? product
													.getDescription().substring(0, 17)
													: product.getDescription());
									cpclConfigLabel += text(280, y, product.getQty()
											.toString());
								}else{
									cpclConfigLabel += text(
											70,
											y,
											(product.getDescription().length() > 31) ? product
													.getDescription().substring(0,
															30) : product
													.getDescription());
									cpclConfigLabel += text(280, y += LINE_SPACING,
											product.getQty().toString());
								}
							} else {
								if(Company.getShortCode().matches("JUBI")){
									cpclConfigLabel += text(
											70,
											y,
											(product.getDescription().length() > 18) ? product
													.getDescription().substring(0, 17)
													: product.getDescription());
									cpclConfigLabel += text(280, y, product.getQty()
											.toString());
								}else{
									cpclConfigLabel += text(
											70,
											y,
											(product.getDescription().length() > 31) ? product
													.getDescription().substring(0,
															30) : product
													.getDescription());
									cpclConfigLabel += text(280, y += LINE_SPACING,
											product.getQty().toString());
								}

							}
							double value = Double.parseDouble(product.getPrice()
									.toString());
							String values = String.format("%." + decimal + "f", value);
							cpclConfigLabel += text(365, y, values);
							cpclConfigLabel += text(486, y, product.getTotal()
									.toString());

							Log.d("products", "-->" + product.getFocqty());

							if (product.getFocqty() > 0) {
								cpclConfigLabel += text(70, y += LINE_SPACING,
										"Foc")
										+ text(210, y, " : ")
										+ text(280, y,
										"" + (int) product.getFocqty());
							}


							if (product.getExchangeqty() > 0) {
								cpclConfigLabel += text(70, y += LINE_SPACING,
										"Exchange")
										+ text(210, y, " : ")
										+ text(280,
										y,
										""
												+ (int) product
												.getExchangeqty());
							}
						}
					}
				}
				cpclConfigLabel += horizontalLine(y += LINE_SPACING,
						LINE_THICKNESS);

				if (Double.parseDouble(receipt.getItemdisc().toString()) > 0) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Item Disc")
							+ text(140, y, " : ")
							+ text(180, y, receipt.getItemdisc().toString());
				}

				if (Company.getShortCode().matches("JUBI")) {
					cpclConfigLabel += text(310, y, "Sub Total")
							+ text(450, y, " : ")
							+ text(486, y, receipt.getSubtotal()
							.toString());
				} else {

				if (Double.parseDouble(receipt.getTax().toString()) > 0) {

					if (Double.parseDouble(receipt.getItemdisc().toString()) == 0) {

						if (Double.parseDouble(receipt.getBilldisc().toString()) > 0) {
							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, "Bill Disc")
									+ text(140, y, " : ")
									+ text(180, y, receipt.getBilldisc()
									.toString());
						}
						if (Double.parseDouble(receipt.getBilldisc().toString()) == 0
								&& Double.parseDouble(receipt.getItemdisc()
								.toString()) == 0) {
//							taxtype = receipt.getTaxType().toString();
							taxtype = taxType;
							Log.d("taxtype", "taxtype :" + taxtype + "Nettot :" + receipt.getNettot() + "getTeax  :" + receipt.getTax());
							double nt = Double.parseDouble(receipt.getNettot());
							double tx = Double.parseDouble(receipt.getTax());
							double st = nt - tx;
							Log.d("ResultValue", "net :" + nt + "tax :" + tx + "subtotal :" + st);
//							String sub = String.valueOf(st);
							String sub = String.format("%.2f", st);
							Log.d("checkSub", "-->" + sub);
							if (taxtype != null && !taxtype.isEmpty()) {
								if (taxtype.matches("I")) {

									cpclConfigLabel += text(310, y += LINE_SPACING,
											"Sub Total")
											+ text(450, y, " : ")
											+ text(486, y, sub);
								} else {
									cpclConfigLabel += text(310, y, "Sub Total")
											+ text(450, y, " : ")
											+ text(486, y, receipt.getSubtotal()
											.toString());
								}
							} else {
								cpclConfigLabel += text(310, y, "Sub Total")
										+ text(450, y, " : ")
										+ text(486, y, receipt.getSubtotal()
										.toString());
							}
						} else {

							cpclConfigLabel += text(310, y, "Sub Total")
									+ text(450, y, " : ")
									+ text(486, y, receipt.getSubtotal()
									.toString());
						}
					} else {

						cpclConfigLabel += text(310, y, "Sub Total")
								+ text(450, y, " : ")
								+ text(486, y, receipt.getSubtotal().toString());
					}
				}
			}
				if (Double.parseDouble(receipt.getBilldisc().toString()) > 0) {

					if (Double.parseDouble(receipt.getItemdisc().toString()) != 0) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Bill Disc")
								+ text(140, y, " : ")
								+ text(180, y, receipt.getBilldisc().toString());
					} else {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"") + text(140, y, "") + text(180, y, "");
					}
				}

				if (Double.parseDouble(receipt.getTax().toString()) > 0) {

					if (Double.parseDouble(receipt.getBilldisc().toString()) == 0) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"") + text(140, y, "") + text(180, y, "");
					}

//					taxtype =receipt.getTaxType().toString();
//					String taxTypes =SalesOrderSetGet.getCompanytax();
					taxtype = taxType;
					Log.d("getTaxtype","taxtype :"+taxtype);
					if(taxtype!=null && !taxtype.isEmpty()) {
						if (taxtype.matches("I")) {
							cpclConfigLabel += text(310, y, "Incl-Tax")
									+ text(450, y, " : ")
									+ text(486, y, receipt.getTax().toString());
						}else{
							cpclConfigLabel += text(310, y, "Excl-Tax")
									+ text(450, y, " : ")
									+ text(486, y, receipt.getTax().toString());
						}
					}else{
						cpclConfigLabel += text(310, y, "Tax")
								+ text(450, y, " : ")
								+ text(486, y, receipt.getTax().toString());
					}

				}
				if(Company.getShortCode().matches("JUBIcc")){
					cpclConfigLabel += text(310, y += LINE_SPACING, "----------------");

				}

				if (Double.parseDouble(receipt.getTax().toString()) == 0) {

					cpclConfigLabel += text(310, y, "") + text(450, y, "")
							+ text(486, y, "");

					if(Company.getShortCode().matches("JUBIcc")){
						cpclConfigLabel += text(310, y += LINE_SPACING,
								"Net Total")
								+ text(450, y, " : ")
								+ text(486, y, receipt.getNettot().toString());

						cpclConfigLabel += text(250, y += LINE_SPACING,
								"Prev - Oustanding")
								+ text(450, y, " : ")
								+ text(486, y, receipt.getNettot().toString());

						cpclConfigLabel += text(310, y += LINE_SPACING, "----------------");

						cpclConfigLabel += text(320, y += LINE_SPACING,
								"Total")
								+ text(450, y, " : ")
								+ text(486, y, receipt.getNettot().toString());

						if (Double.parseDouble(receipt.getPaidamount()) > 0) {
							cpclConfigLabel += text(310, y+= LINE_SPACING, "Paid Amount")
									+ text(450, y, " : ")
									+ text(486, y, receipt.getPaidamount()
									.toString());
						}

						cpclConfigLabel += text(310, y += LINE_SPACING, "----------------");



					}else {


						if (Double.parseDouble(receipt.getBilldisc().toString()) == 0) {
							cpclConfigLabel += text(310, y += LINE_SPACING,
									"Net Total")
									+ text(450, y, " : ")
									+ text(486, y, receipt.getNettot().toString());

							//added new 31 aug 2016
							String creditamount = receipt.getCreditAmount();
							double creditVal = 0;

							if (creditamount != null && !creditamount.isEmpty()) {
								creditVal = Double.valueOf(receipt.getCreditAmount());
							}

							if (creditVal > 0) {
								cpclConfigLabel += text(310, y += LINE_SPACING,
										"Credit Amt")
										+ text(450, y, " : ")
										+ text(486, y, creditVal);
							}
							//end

							if (!In_Cash.getPay_Mode().matches("")
									|| !In_Cash.getPay_Mode().matches("null")
									|| !In_Cash.getPay_Mode().matches(null)) {
								String pay_Mode = In_Cash.getPay_Mode();
								cpclConfigLabel += (text(LEFT_MARGIN,
										y += LINE_SPACING, "Pay Mode"));
								cpclConfigLabel += (text(150, y, " : "));
								cpclConfigLabel += (text(180, y, pay_Mode));
							}
							if (Double.parseDouble(receipt.getPaidamount()) > 0) {
								cpclConfigLabel += text(310, y, "Paid Amount")
										+ text(450, y, " : ")
										+ text(486, y, receipt.getPaidamount()
										.toString());
							}

						} else {

							if (Double
									.parseDouble(receipt.getBilldisc().toString()) > 0) {
								cpclConfigLabel += text(LEFT_MARGIN,
										y, "Bill Disc")
										+ text(140, y, " : ")
										+ text(180, y, receipt.getBilldisc()
										.toString());
							}

							cpclConfigLabel += text(310, y, "Net Total")
									+ text(450, y, " : ")
									+ text(486, y, receipt.getNettot().toString());

							//added new 31 aug 2016
							String creditamount = receipt.getCreditAmount();
							double creditVal = 0;

							if (creditamount != null && !creditamount.isEmpty()) {
								creditVal = Double.valueOf(receipt.getCreditAmount());
							}

							if (creditVal > 0) {
								cpclConfigLabel += text(310, y += LINE_SPACING,
										"Credit Amt")
										+ text(450, y, " : ")
										+ text(486, y, creditVal);
							}
							// end

							if (!In_Cash.getPay_Mode().matches("")
									|| !In_Cash.getPay_Mode().matches("null")
									|| !In_Cash.getPay_Mode().matches(null)) {
								String pay_Mode = In_Cash.getPay_Mode();
								cpclConfigLabel += (text(LEFT_MARGIN,
										y += LINE_SPACING, "Pay Mode"));
								cpclConfigLabel += (text(150, y, " : "));
								cpclConfigLabel += (text(180, y, pay_Mode));
							}
							if (Double.parseDouble(receipt.getPaidamount()) > 0) {
								cpclConfigLabel += text(310, y, "Paid Amount")
										+ text(450, y, " : ")
										+ text(486, y, receipt.getPaidamount()
										.toString());
							}
						}
					}
				} else {

					if(Company.getShortCode().matches("JUBIcc")){
						cpclConfigLabel += text(310, y += LINE_SPACING,
								"Net Total")
								+ text(450, y, " : ")
								+ text(486, y, receipt.getNettot().toString());

						cpclConfigLabel += text(250, y += LINE_SPACING,
								"Prev - Oustanding")
								+ text(450, y, " : ")
								+ text(486, y, receipt.getNettot().toString());

						cpclConfigLabel += text(310, y += LINE_SPACING, "----------------");


						cpclConfigLabel += text(320, y += LINE_SPACING,
								"Total")
								+ text(450, y, " : ")
								+ text(486, y, receipt.getNettot().toString());

						if (Double.parseDouble(receipt.getPaidamount()) > 0) {
							cpclConfigLabel += text(310, y+= LINE_SPACING, "Paid Amount")
									+ text(450, y, " : ")
									+ text(486, y, receipt.getPaidamount()
									.toString());
						}
						cpclConfigLabel += text(310, y += LINE_SPACING, "----------------");


					}else {

						cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
								+ text(450, y, " : ")
								+ text(486, y, receipt.getNettot().toString());

						//added new 31 aug 2016
						String creditamount = receipt.getCreditAmount();
						double creditVal = 0;

						if (creditamount != null && !creditamount.isEmpty()) {
							creditVal = Double.valueOf(receipt.getCreditAmount());
						}

						if (creditVal > 0) {
							cpclConfigLabel += text(310, y += LINE_SPACING,
									"Credit Amt")
									+ text(450, y, " : ")
									+ text(486, y, creditVal);
						}
						// end
						if (!In_Cash.getPay_Mode().matches("")
								|| !In_Cash.getPay_Mode().matches("null")
								|| !In_Cash.getPay_Mode().matches(null)) {
							String pay_Mode = In_Cash.getPay_Mode();
							cpclConfigLabel += (text(LEFT_MARGIN,
									y += LINE_SPACING, "Pay Mode"));
							cpclConfigLabel += (text(150, y, " : "));
							cpclConfigLabel += (text(180, y, pay_Mode));
						}
						if (Double.parseDouble(receipt.getPaidamount()) > 0) {
							cpclConfigLabel += text(310, y, "Paid Amount")
									+ text(450, y, " : ")
									+ text(486, y, receipt.getPaidamount()
									.toString());
						}
					}
				}


				cpclConfigLabel += horizontalLine(y += LINE_SPACING,
						LINE_THICKNESS);
				if (!In_Cash.getPay_Mode().matches("")
						|| !In_Cash.getPay_Mode().matches("null")
						|| !In_Cash.getPay_Mode().matches(null)) {
					String pay_Mode = In_Cash.getPay_Mode();

					if (pay_Mode.matches("Cheque")
							|| pay_Mode.matches("cheque")
							|| pay_Mode.matches("CHEQUE")) {

						bank_Code = In_Cash.getBank_code();
						// bank_Name = In_Cash.getBank_Name();
						check_No = In_Cash.getCheck_No();
						check_Date = In_Cash.getCheck_Date();

						StringTokenizer tk = new StringTokenizer(check_Date);

						String date = tk.nextToken();

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Bank Code");
						cpclConfigLabel += text(200, y, "Cheque No");
						cpclConfigLabel += text(420, y, "Cheque Date");
						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
								LINE_THICKNESS);

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								bank_Code);
						cpclConfigLabel += text(200, y, check_No);
						cpclConfigLabel += text(420, y, date);

						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
								LINE_THICKNESS);

					}

				}

				if (!receipt.getRemarks().equals("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Remarks")
							+ text(140, y, " : ")
							+ text(180,
									y,
									(receipt.getRemarks().length() > 25) ? receipt
											.getRemarks().substring(0, 24)
											: receipt.getRemarks());

					cpclConfigLabel += horizontalLine(y += LINE_SPACING,
							LINE_THICKNESS);
				}

				if (receipt.getPaidamount() != null
						&& !receipt.getPaidamount().isEmpty()
						&& receipt.getNettot() != null
						&& !receipt.getNettot().isEmpty()) {

					double netTotal = Double.valueOf(receipt.getNettot());
					double amountPaid = Double.valueOf(receipt.getPaidamount());

					String creditamt =receipt.getCreditAmount();
					double creditValue=0;

					if(creditamt!=null && !creditamt.isEmpty()){
						creditValue = Double.valueOf(receipt.getCreditAmount());
					}


					if (netTotal > 0 && amountPaid > 0
							&& netTotal != amountPaid) {

						Log.d("netTotal", ""+netTotal);
						Log.d("amountPaid", ""+amountPaid);
						Log.d("creditValue", ""+creditValue);


//						double invoiceOutstanding = netTotal - (amountPaid + creditValue);

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Invoice Outstanding")
								+ text(310, y, " : ")
								+ text(450, y, receipt.getBalanceAmount());

						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
								LINE_THICKNESS);

					}
				}

				totalOutStanding = receipt.getTotaloutstanding();

			}

			if(!Company.getShortCode().matches("HEZOM")) {
				if (showTotalOutstanding.matches("True")) {

					if (totalOutStanding != null && !totalOutStanding.isEmpty()
							&& !totalOutStanding.matches("null")) {

						if (Double.valueOf(totalOutStanding) > 0) {

							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Total Outstanding")
									+ text(310, y, " : ")
									+ text(450, y, totalOutStanding);

							cpclConfigLabel += horizontalLine(y += LINE_SPACING,
									LINE_THICKNESS);

						}
					}
				}
			}
			// if (showTotalOutstanding.matches("True")) {
			// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
			// }

			if(printSalesReturnSummaryOnReceipt!=null && !printSalesReturnSummaryOnReceipt.isEmpty()) {
				if (printSalesReturnSummaryOnReceipt.equalsIgnoreCase("True") && printSalesReturnDetailOnReceipt.equalsIgnoreCase("True") || printSalesReturnDetailOnReceipt.equalsIgnoreCase("False")) {
					if (salesReturnArr.size() > 0) {
						double dTotal = 0.00;
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

						cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SR No");
						cpclConfigLabel += text(250, y, "SR Date");
						cpclConfigLabel += text(475, y, "Amount");
						cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

						for (int i = 0; i < salesReturnArr.size(); i++) {
							String salesReturnNo = salesReturnArr.get(i).get("SalesReturnNo");
							String salesReturnDate = salesReturnArr.get(i).get("SalesReturnDate");
							String creditAmount = salesReturnArr.get(i).get("CreditAmount");
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, salesReturnNo);
							cpclConfigLabel += text(250, y, salesReturnDate);
							cpclConfigLabel += text(490, y, creditAmount);

							if (creditAmount != null && !creditAmount.isEmpty()) {
								dTotal += Double.valueOf(creditAmount);
							}

						}

						cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
						cpclConfigLabel += text(310, y += LINE_SPACING, "Total")
								+ text(450, y, " : ")
								+ text(486, y, twoDecimalPoint(dTotal));
						cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

					}
				}
			}

			//PrintSalesReturnDetailOnReceipt
			if(printSalesReturnDetailOnReceipt!=null && !printSalesReturnDetailOnReceipt.isEmpty()){
				if(printSalesReturnSummaryOnReceipt.equalsIgnoreCase("False") && printSalesReturnDetailOnReceipt.equalsIgnoreCase("True") ){

					if(mSRHeaderDetailArr.size()>0){
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
						cpclConfigLabel += text(221, y, "SALES RETURN");
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
						for (ProductDetails mSRHeader : mSRHeaderDetailArr) {

							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Sales Return No")
									+ text(200, y, " : ")
									+ text(230, y, mSRHeader.getItemno());

							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Sales Return Date")
									+ text(200, y, " : ")
									+ text(230, y, mSRHeader.getItemdate());

							cpclConfigLabel += horizontalLine(y += LINE_SPACING,
									LINE_THICKNESS);

							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
							cpclConfigLabel += text(70, y, "Description");
							cpclConfigLabel += text(280, y, "Qty");
							cpclConfigLabel += text(365, y, "Price");
							cpclConfigLabel += text(500, y, "Total");

							cpclConfigLabel += horizontalLine(y += LINE_SPACING,
									LINE_THICKNESS);
							for (ProdDetails mSRDetail : mSRHeader.getProductsDetails()) {
								String salesReturnNo = mSRDetail.getItemnum().toString();
								if (salesReturnNo.matches(mSRHeader.getItemno().toString())) {
									cpclConfigLabel += text(LEFT_MARGIN,y += LINE_SPACING, mSRDetail.getSno().toString());

									if (showProductFullName.matches("True")) {
										cpclConfigLabel += text(70,y,(mSRDetail.getDescription().length() > 31) ? mSRDetail
												.getDescription().substring(0,30) : mSRDetail
												.getDescription());
										cpclConfigLabel += text(280, y += LINE_SPACING,mSRDetail.getQty().toString());
									} else {
										cpclConfigLabel += text(70,y,
												(mSRDetail.getDescription().length() > 10) ? mSRDetail
														.getDescription().substring(0,9) : mSRDetail.getDescription());
										cpclConfigLabel += text(280, y, mSRDetail.getQty().toString());

									}
									double value =Double.parseDouble(mSRDetail.getPrice().toString());
									//  Log.d("ParseString",""+value);
									String values=String.format("%."+decimal+"f",value);
									cpclConfigLabel += text(365, y, values);
									cpclConfigLabel += text(486, y, mSRDetail.getTotal()
											.toString());
									if (mSRDetail.getFocqty() > 0) {
										cpclConfigLabel += text(70, y += LINE_SPACING,"Foc")+ text(210, y, " : ")
												+ text(280, y,"" + (int) mSRDetail.getFocqty());
									}

									if (mSRDetail.getExchangeqty() > 0) {
										cpclConfigLabel += text(70, y += LINE_SPACING,"Exchange")
												+ text(210, y, " : ")
												+ text(280,y,""+ (int) mSRDetail.getExchangeqty());
									}

								}
							}
							cpclConfigLabel += horizontalLine(y += LINE_SPACING,
									LINE_THICKNESS);
							if (Double.parseDouble(mSRHeader.getItemdisc().toString()) > 0) {
								cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
										"Item Disc")+ text(140, y, " : ")
										+ text(180, y, mSRHeader.getItemdisc().toString());
							}
							if (Double.parseDouble(mSRHeader.getTax().toString()) > 0) {

								if (Double.parseDouble(mSRHeader.getItemdisc().toString()) == 0) {

									if (Double
											.parseDouble(mSRHeader.getBilldisc().toString()) > 0) {
										cpclConfigLabel += text(LEFT_MARGIN,
												y += LINE_SPACING, "Bill Disc")
												+ text(140, y, " : ")
												+ text(180, y, mSRHeader.getBilldisc()
												.toString());
									}
									if (Double
											.parseDouble(mSRHeader.getBilldisc().toString()) == 0
											&& Double.parseDouble(mSRHeader.getItemdisc()
											.toString()) == 0) {
										cpclConfigLabel += text(310, y += LINE_SPACING,
												"Sub Total")
												+ text(450, y, " : ")
												+ text(486, y, mSRHeader.getSubtotal()
												.toString());
									} else {

										cpclConfigLabel += text(310, y, "Sub Total")
												+ text(450, y, " : ")
												+ text(486, y, mSRHeader.getSubtotal()
												.toString());
									}
								} else {

									cpclConfigLabel += text(310, y, "Sub Total")
											+ text(450, y, " : ")
											+ text(486, y, mSRHeader.getSubtotal().toString());
								}
							}
							if (Double.parseDouble(mSRHeader.getBilldisc().toString()) > 0) {

								if (Double.parseDouble(mSRHeader.getItemdisc().toString()) != 0) {
									cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
											"Bill Disc")
											+ text(140, y, " : ")
											+ text(180, y, mSRHeader.getBilldisc().toString());
								} else {
									cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
											"") + text(140, y, "") + text(180, y, "");
								}
							}

							if (Double.parseDouble(mSRHeader.getTax().toString()) > 0) {

								if (Double.parseDouble(mSRHeader.getBilldisc().toString()) == 0) {
									cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
											"") + text(140, y, "") + text(180, y, "");
								}

								cpclConfigLabel += text(310, y, "Tax")
										+ text(450, y, " : ")
										+ text(486, y, mSRHeader.getTax().toString());
							}

							cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
									+ text(450, y, " : ")
									+ text(486, y, mSRHeader.getNettot().toString());

							cpclConfigLabel += horizontalLine(y += LINE_SPACING,
									LINE_THICKNESS);
							cpclConfigLabel += text(250, y += LINE_SPACING, "SalesReturn Used")
									+ text(450, y, " : ")
									+ text(486, y, mSRHeader.getCreditAmount());


     /*cpclConfigLabel += text(310, y += LINE_SPACING, "Balance Amt")
       + text(450, y, " : ")
       + text(486, y, mSRHeader.getBalanceAmount());
*/
							cpclConfigLabel += horizontalLine(y += LINE_SPACING,
									LINE_THICKNESS);
						}
					}

				}
			}

			SOTDatabase.init(context);
			String img = SOTDatabase.getSignatureImage();
			Log.d("PrintedImage",""+img);
		/*	if (!img.matches("")) {
				Log.d("Do Nothing", "Do Nothing");
				Log.d("Signature image", img);
			} else {
				Log.d("footerArr", "" + footerArr.size());

				if (footerArr.size() > 0) {
					for (ProductDetails footer : footerArr) {

						Log.d("footer value",
								"val " + footer.getReceiptMessage());

						if (footer.getReceiptMessage() != null
								&& !footer.getReceiptMessage().isEmpty()) {
							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING,
									footer.getReceiptMessage());
						}
					}
				}
			}*/

			if (showFooter.matches("True")) {

				if (img!=null && !img.isEmpty()) {

					Log.d("Do Nothing", "Do Nothing");

				}else{ //without signature

					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
					// cpclConfigLabel += horizontalLine(y += LINE_SPACING,
					// LINE_THICKNESS);
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"-----------------   ------------------");

					cpclConfigLabel += text(75, y += LINE_SPACING,
							"Received By");

					cpclConfigLabel += text(350, y, "Authorized By");

					Log.d("footerArr", "" + footerArr.size());

					if (footerArr.size() > 0) {


						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
								LINE_THICKNESS);

						for (ProductDetails footer : footerArr) {

							Log.d("footer value",
									"val " + footer.getReceiptMessage());

							if (footer.getReceiptMessage() != null
									&& !footer.getReceiptMessage().isEmpty()) {
								cpclConfigLabel += text(LEFT_MARGIN,
										y += LINE_SPACING,
										footer.getReceiptMessage());
							}
						}
					}

				}
			}

			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 2)) + " 1"
					+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

			/*
			 * FileOutputStream os = context.openFileOutput(FILE_NAME,
			 * Context.MODE_PRIVATE);
			 */
			os.write(cpclConfigLabel.getBytes());
			os.flush();
		}
		os.close();

	}

	public void printDeliveryOnInvoice(String invoiceno, String invoicedate,
			String customercode, String customername,
			List<ProductDetails> product, List<ProductDetails> productdet,
			List<String> printsortHeader, String gnrlStngs, int nofcopies,
			List<ProductDetails> product_batch, List<ProductDetails> footerArr)
			throws IOException {

		helper.showProgressDialog(context.getString(R.string.print),
				context.getString(R.string.creating_file_for_printing));
		try {
			deliveryOrderOnInvoice(invoiceno, invoicedate, customercode,
					customername, product, productdet, printsortHeader,
					gnrlStngs, nofcopies, product_batch, footerArr);
			if (isBluetoothEnabled()) {
				print();

			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}

		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}

	public void printInvoice(String invoiceno, String invoicedate,
			String customercode, String customername,
			List<ProductDetails> product, List<ProductDetails> productdet,
			List<String> printsortHeader, String gnrlStngs, int nofcopies,
			List<ProductDetails> product_batch, List<ProductDetails> footerArr,String webmethodname,String trantype,String durationDays,String doNo)
			throws IOException {

		helper.showProgressDialog(context.getString(R.string.print),
				context.getString(R.string.creating_file_for_printing));
		try {
			createInvoiceFile(invoiceno, invoicedate, customercode,
					customername, product, productdet, printsortHeader,
					gnrlStngs, nofcopies, product_batch, footerArr,webmethodname,trantype,durationDays,doNo);

			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				Log.d("isBluetoothEnabled",""+"isBluetoothEnabled");
				print();

			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}

		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}

	private void createInvoiceFile(String invoiceno, String invoicedate,
			String customercode, String customername,
			List<ProductDetails> product, List<ProductDetails> productdet,
			List<String> printsortHeader, String gnrlStngs, int nofcopies,
			List<ProductDetails> product_batch, List<ProductDetails> footerValue,
								   String webmethodname,String tranType,String duration,String doNo)
			throws IOException, ZebraPrinterConnectionException {
		// Used the calculate the y axis printing position dynamically

		// String invoiceprintdetail = SalesOrderSetGet.getInvoiceprintdetail();

		footerArr.clear();
		footerArr = footerValue;

		logoStr = "logoprint";
		String taxtype ="";

		Log.d("tranTypeValues","-->"+tranType +Company.getShortCode());
		Log.d("invlogoStr", "invoice" + logoStr);
		String taxPerc ="";
		String showCustomerCode = MobileSettingsSetterGetter
				.getShowCustomerCode();
		String showCustomerName = MobileSettingsSetterGetter
				.getShowCustomerName();
		String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
		String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
		String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
		String showCustomerPhone = MobileSettingsSetterGetter
				.getShowCustomerPhone();
		String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
		String showCustomerEmail = MobileSettingsSetterGetter
				.getShowCustomerEmail();
		String showCustomerTerms = MobileSettingsSetterGetter
				.getShowCustomerTerms();
		String showTotalOutstanding = MobileSettingsSetterGetter
				.getShowTotalOutstanding();
		String showProductFullName = MobileSettingsSetterGetter.getShowProductFullName();
		String showFooter = MobileSettingsSetterGetter.getShowFooter();
		String DecimalPoints = MobileSettingsSetterGetter.getDecimalPoints();

		/*String showuserphoneno = MobileSettingsSetterGetter.getShowUserPhoneNo();
		String loginphoneno  = SalesOrderSetGet.getLoginPhoneNo();
		String username = SupplierSetterGetter.getUsername();*/
//		String taxType = SalesOrderSetGet.getCompanytax();
		String showGST = MobileSettingsSetterGetter.getShowGST();
		if(showGST!=null && !showGST.isEmpty()){

		}else{
			showGST="";
		}

		String InvoiceHeaderCaption = MobileSettingsSetterGetter.getInvoiceHeaderCaption();
		String InvoiceSubTotalCaption = MobileSettingsSetterGetter.getInvoiceSubTotalCaption();
		String InvoiceTaxCaption = MobileSettingsSetterGetter.getInvoiceTaxCaption();
		String InvoiceNetTotalCaption = MobileSettingsSetterGetter.getInvoiceNetTotalCaption();
		String decimal = MobileSettingsSetterGetter.getDecimalPoints();

		String subtotalCaption = "",nettotalCaption="";
		if(!InvoiceSubTotalCaption.equals("")){
			subtotalCaption = InvoiceSubTotalCaption;
		}else{
			subtotalCaption = "Sub Total";
		}
		taxPerc = SalesOrderSetGet.getCustomerTaxPerc();
		if(taxPerc==null || taxPerc.trim().equals("")){
			taxPerc = "0.00";
		}

		if(!InvoiceNetTotalCaption.equals("")){
			nettotalCaption = InvoiceNetTotalCaption;
		}else{
			nettotalCaption = "Net Total";
		}

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		for (int n = 0; n < nofcopies; n++) {
			int y = 0;
			int s = 1;


			StringBuilder temp = new StringBuilder();
//			y = printTitle(228, y, "INVOICE", temp);

			if(webmethodname.matches("fncGetCashInvoiceHeader")){
//				if(!InvoiceHeaderCaption.equals("")){
//					y = printTitle(228, y, InvoiceHeaderCaption , temp);
//				}else{
					y = printTitle(228, y, "CASH BILL", temp);
					y = printTitle(228, y, "", temp);
//				}

			}else if (Company.getShortCode().matches("JUBI")){
				y = printTitle(250, y, "INVOICE", temp);

				y = printCompanyDetails(y, temp);
			}
			else{
				if(!InvoiceHeaderCaption.equals("")){
					if(tranType.matches("COI")){

						y = printTitle(180, y, "CONSIGNMENT INVOICE", temp);
					}else{
						y = printTitle(200, y, InvoiceHeaderCaption , temp);
					}
				}else{
					if(tranType.matches("COI")){
						y = printTitle(180, y, "CONSIGNMENT INVOICE", temp);
					}else {
						y = printTitle(250, y, "INVOICE", temp);
					}

				}

				y = printCompanyDetails(y, temp);
//				jubiPrint();
			}



			String cpclConfigLabel = temp.toString();
		/*	 if (showuserphoneno != null && !showuserphoneno.isEmpty() && loginphoneno != null && !loginphoneno.isEmpty()) {   
				    if (showuserphoneno.matches("True") && !loginphoneno.matches("null")) {
				     cpclConfigLabel+= text(LEFT_MARGIN, y += LINE_SPACING,
				       "Contact ")
				       + text(150, y, " : ")
				       + text(180, y, username +" "+loginphoneno);
				       
				    }   
				   }*/

		if(tranType.matches("COI")){

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
					"CG-Invoice No")
					+ text(200, y, " : ")
					+ text(220, y, invoiceno);
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
					"CG-Invoice Date")
					+ text(200, y, " : ")
					+ text(220, y, invoicedate);

			if (showCustomerCode.matches("True")) {

				if (!customercode.matches("")) {
					cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
							"Cust Code"));
					cpclConfigLabel += (text(200, y, " : "));
					cpclConfigLabel += (text(220, y, customercode));
				}
			}

			if (showCustomerName.matches("True")) {

				if (!customername.matches("")) {
					cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
							"Cust Name"));
					cpclConfigLabel += (text(200, y, " : "));

//					cpclConfigLabel +="COUNTRY CHINA\r\n"+ "T 55 0 88 58 \r\n";


					cpclConfigLabel += (text(
							220,
							y,
							(customername.length() > 30) ? customername
									.substring(0, 29) : customername));

					if (customername.length() > 30) {

						String custlastname = customername.substring(29);

						Log.d("Custnameeee", custlastname);

						cpclConfigLabel += (text(
								220,
								y += LINE_SPACING,
								(custlastname.length() > 30) ? custlastname
										.substring(0, 29) : custlastname));
					}
				}
			}

			String address1 = CustomerSetterGetter.getCustomerAddress1();
			String address2 = CustomerSetterGetter.getCustomerAddress2();
			String address3 = CustomerSetterGetter.getCustomerAddress3();
			if (showAddress1.matches("True")) {
				if (!address1.matches("")) {
					cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
							"Address 1"));
					cpclConfigLabel += (text(200, y, " : "));

					cpclConfigLabel += (text(
							220,
							y,
							(address1.length() > 23) ? address1
									.substring(0, 22) : address1));

					if (address1.length() > 23) {

						String addr1 = address1.substring(22);

						Log.d("addr1", addr1);

						cpclConfigLabel += (text(
								220,
								y += LINE_SPACING,
								(addr1.length() > 23) ? addr1
										.substring(0, 22) : addr1));
					}
				}
			}

			if (showAddress2.matches("True")) {
				if (!address2.matches("")) {
					cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
							"Address 2"));
					cpclConfigLabel += (text(200, y, " : "));

					cpclConfigLabel += (text(
							220,
							y,
							(address2.length() > 23) ? address2
									.substring(0, 22) : address2));

					if (address2.length() > 23) {

						String addr2 = address2.substring(22);

						Log.d("addr2", addr2);

						cpclConfigLabel += (text(
								220,
								y += LINE_SPACING,
								(addr2.length() > 23) ? addr2
										.substring(0, 22) : addr2));
					}
				}
			}

			if (showAddress3.matches("True")) {
				if (!address3.matches("")) {
					cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
							"Address 3"));
					cpclConfigLabel += (text(200, y, " : "));

					cpclConfigLabel += (text(
							220,
							y,
							(address3.length() > 23) ? address3
									.substring(0, 22) : address3));

					if (address3.length() > 23) {

						String addr3 = address3.substring(22);

						Log.d("addr3", addr3);

						cpclConfigLabel += (text(
								220,
								y += LINE_SPACING,
								(addr3.length() > 23) ? addr3
										.substring(0, 22) : addr3));
					}
				}
			}

			if (showCustomerPhone.matches("True")) {
				if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Phone No")
							+ text(200, y, " : ")
							+ text(220, y,
							CustomerSetterGetter.getCustomerPhone());
				}
			}

			if (showCustomerHP.matches("True")) {
				if (!CustomerSetterGetter.getCustomerHP().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Head Phone")
							+ text(200, y, " : ")
							+ text(220, y, CustomerSetterGetter.getCustomerHP());
				}
			}

			if (showCustomerEmail.matches("True")) {
				if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Email")
							+ text(200, y, " : ")
							+ text(220, y,
							CustomerSetterGetter.getCustomerEmail());
				}
			}

			if (showCustomerTerms.matches("True")) {
				if (!CustomerSetterGetter.getCustomerTerms().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Terms")
							+ text(200, y, " : ")
							+ text(220, y,
							CustomerSetterGetter.getCustomerTerms());

				}
			}

			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"Duration Days"));
			cpclConfigLabel += (text(200, y, " : "));
			cpclConfigLabel += (text(220, y, duration));

		}else {

			Log.d("Company.getShortCode()",Company.getShortCode());
			if (Company.getShortCode().matches("SUPERSTAR")) {
				Log.d("Executed!!!!","Executedd");
				if (showCustomerName.matches("True")) {

					if (!customername.matches("")) {
						cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
								"Cust Name"));
						cpclConfigLabel += (text(150, y, " : "));


						cpclConfigLabel += (text(
								180,
								y,
								(customername.length() > 30) ? customername
										.substring(0, 29) : customername));

						if (customername.length() > 30) {

							String custlastname = customername.substring(29);

							Log.d("Custnameeee", custlastname);

							cpclConfigLabel += (text(
									220,
									y += LINE_SPACING,
									(custlastname.length() > 30) ? custlastname
											.substring(0, 29) : custlastname));
						}
					}
				}

				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						"Invoice No")
						+ text(150, y, " : ")
						+ text(180, y, invoiceno);

				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						"Invoice Date")
						+ text(150, y, " : ")
						+ text(180, y, invoicedate);

				if (showCustomerCode.matches("True")) {

					if (!customercode.matches("")) {
						cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
								"Cust Code"));
						cpclConfigLabel += (text(150, y, " : "));
						cpclConfigLabel += (text(180, y, customercode));
					}
				}


				String address1 = CustomerSetterGetter.getCustomerAddress1();
				String address2 = CustomerSetterGetter.getCustomerAddress2();
				String address3 = CustomerSetterGetter.getCustomerAddress3();
				if (showAddress1.matches("True")) {
					if (!address1.matches("")) {
						cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
								"Address 1"));
						cpclConfigLabel += (text(150, y, " : "));

						cpclConfigLabel += (text(
								180,
								y,
								(address1.length() > 23) ? address1
										.substring(0, 22) : address1));

						if (address1.length() > 23) {

							String addr1 = address1.substring(22);

							Log.d("addr1", addr1);

							cpclConfigLabel += (text(
									180,
									y += LINE_SPACING,
									(addr1.length() > 23) ? addr1
											.substring(0, 22) : addr1));
						}
					}
				}

				if (showAddress2.matches("True")) {
					if (!address2.matches("")) {
						cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
								"Address 2"));
						cpclConfigLabel += (text(150, y, " : "));

						cpclConfigLabel += (text(
								180,
								y,
								(address2.length() > 23) ? address2
										.substring(0, 22) : address2));

						if (address2.length() > 23) {

							String addr2 = address2.substring(22);

							Log.d("addr2", addr2);

							cpclConfigLabel += (text(
									180,
									y += LINE_SPACING,
									(addr2.length() > 23) ? addr2
											.substring(0, 22) : addr2));
						}
					}
				}

				if (showAddress3.matches("True")) {
					if (!address3.matches("")) {
						cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
								"Address 3"));
						cpclConfigLabel += (text(150, y, " : "));

						cpclConfigLabel += (text(
								180,
								y,
								(address3.length() > 23) ? address3
										.substring(0, 22) : address3));

						if (address3.length() > 23) {

							String addr3 = address3.substring(22);

							Log.d("addr3", addr3);

							cpclConfigLabel += (text(
									180,
									y += LINE_SPACING,
									(addr3.length() > 23) ? addr3
											.substring(0, 22) : addr3));
						}
					}
				}

				if (showCustomerPhone.matches("True")) {
					if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Phone No")
								+ text(150, y, " : ")
								+ text(180, y,
								CustomerSetterGetter.getCustomerPhone());
					}
				}

				if (showCustomerHP.matches("True")) {
					if (!CustomerSetterGetter.getCustomerHP().matches("")) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Head Phone")
								+ text(150, y, " : ")
								+ text(180, y, CustomerSetterGetter.getCustomerHP());
					}
				}

				if (showCustomerEmail.matches("True")) {
					if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Email")
								+ text(150, y, " : ")
								+ text(180, y,
								CustomerSetterGetter.getCustomerEmail());
					}
				}

				if (showCustomerTerms.matches("True")) {
					if (!CustomerSetterGetter.getCustomerTerms().matches("")) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Terms")
								+ text(150, y, " : ")
								+ text(180, y,
								CustomerSetterGetter.getCustomerTerms());

					}
				}

				if(!doNo.matches("")){
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"DO No")
							+ text(150, y, " : ")
							+ text(180, y,
							doNo
					);
				}



			} else {

				Log.d("WithoutShortCode","ShortCode");

				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						"Invoice No")
						+ text(150, y, " : ")
						+ text(180, y, invoiceno);
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						"Invoice Date")
						+ text(150, y, " : ")
						+ text(180, y, invoicedate);

				if(Company.getShortCode().matches("JUBI")){
					if (showCustomerName.matches("True")) {
						if (!customername.matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
									"Cust Name"));
							cpclConfigLabel += (text(150, y, " : "));

//					cpclConfigLabel +="COUNTRY CHINA\r\n"+ "T 55 0 88 58 \r\n";


							cpclConfigLabel += (text(
									180,
									y,
									(customername.length() > 30) ? customername
											.substring(0, 29) : customername));

							if (customername.length() > 30) {

								String custlastname = customername.substring(29);

								Log.d("Custnameeee", custlastname);

								cpclConfigLabel += (text(
										220,
										y += LINE_SPACING,
										(custlastname.length() > 30) ? custlastname
												.substring(0, 29) : custlastname));
							}
						}
					}

				}else {
					if (showCustomerCode.matches("True")) {

						if (!customercode.matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
									"Cust Code"));
							cpclConfigLabel += (text(150, y, " : "));
							cpclConfigLabel += (text(180, y, customercode));
						}
					}

					if (showCustomerName.matches("True")) {

						if (!customername.matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
									"Cust Name"));
							cpclConfigLabel += (text(150, y, " : "));

//					cpclConfigLabel +="COUNTRY CHINA\r\n"+ "T 55 0 88 58 \r\n";


							cpclConfigLabel += (text(
									180,
									y,
									(customername.length() > 25) ? customername
											.substring(0, 24) : customername));

							if (customername.length() > 25) {

								String custlastname = customername.substring(24);

								Log.d("Custnameeee", custlastname);

								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(custlastname.length() > 25) ? custlastname
												.substring(0, 24) : custlastname));
							}
						}
					}

					String address1 = CustomerSetterGetter.getCustomerAddress1();
					String address2 = CustomerSetterGetter.getCustomerAddress2();
					String address3 = CustomerSetterGetter.getCustomerAddress3();
					if (showAddress1.matches("True")) {
						if (!address1.matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
									"Address 1"));
							cpclConfigLabel += (text(150, y, " : "));

							cpclConfigLabel += (text(
									180,
									y,
									(address1.length() > 23) ? address1
											.substring(0, 22) : address1));

							if (address1.length() > 23) {

								String addr1 = address1.substring(22);

								Log.d("addr1", addr1);

								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(addr1.length() > 23) ? addr1
												.substring(0, 22) : addr1));
							}
						}
					}

					if (showAddress2.matches("True")) {
						if (!address2.matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
									"Address 2"));
							cpclConfigLabel += (text(150, y, " : "));

							cpclConfigLabel += (text(
									180,
									y,
									(address2.length() > 23) ? address2
											.substring(0, 22) : address2));

							if (address2.length() > 23) {

								String addr2 = address2.substring(22);

								Log.d("addr2", addr2);

								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(addr2.length() > 23) ? addr2
												.substring(0, 22) : addr2));
							}
						}
					}

					if (showAddress3.matches("True")) {
						if (!address3.matches("")) {
							cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
									"Address 3"));
							cpclConfigLabel += (text(150, y, " : "));

							cpclConfigLabel += (text(
									180,
									y,
									(address3.length() > 23) ? address3
											.substring(0, 22) : address3));

							if (address3.length() > 23) {

								String addr3 = address3.substring(22);

								Log.d("addr3", addr3);

								cpclConfigLabel += (text(
										180,
										y += LINE_SPACING,
										(addr3.length() > 23) ? addr3
												.substring(0, 22) : addr3));
							}
						}
					}

					if (showCustomerPhone.matches("True")) {
						if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Phone No")
									+ text(150, y, " : ")
									+ text(180, y,
									CustomerSetterGetter.getCustomerPhone());
						}
					}

					if (showCustomerHP.matches("True")) {
						if (!CustomerSetterGetter.getCustomerHP().matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Head Phone")
									+ text(150, y, " : ")
									+ text(180, y, CustomerSetterGetter.getCustomerHP());
						}
					}

					if (showCustomerEmail.matches("True")) {
						if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Email")
									+ text(150, y, " : ")
									+ text(180, y,
									CustomerSetterGetter.getCustomerEmail());
						}
					}

					if (showCustomerTerms.matches("True")) {
						if (!CustomerSetterGetter.getCustomerTerms().matches("")) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Terms")
									+ text(150, y, " : ")
									+ text(180, y,
									CustomerSetterGetter.getCustomerTerms());

						}
					}
				}
				}


		}
			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			if(product.size()>0){
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
			cpclConfigLabel += text(70, y, "Description");
			cpclConfigLabel += text(280, y, "Qty");
			cpclConfigLabel += text(365, y, "Price");
			if(Company.getShortCode().matches("RAJAGRO")){
				cpclConfigLabel += text(460, y, "SubTotal");
			}else{
				cpclConfigLabel += text(500, y, "Total");
			}


			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			Log.d("gnrlStngs","-->"+gnrlStngs);
			if (gnrlStngs.matches("C") || gnrlStngs.matches("S")) {
				for (ProductDetails products : product) {

					if ((products.getSortproduct().matches(""))
							|| (products.getSortproduct().matches("0"))) {
						int i = 1;
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								String.valueOf(i));


						if (showProductFullName.matches("True")) {
							cpclConfigLabel += text(70, y, (products
									.getDescription().length() > 10) ? products
									.getDescription().substring(0, 9)
									: products.getDescription());

							cpclConfigLabel += text(280, y += LINE_SPACING,
									products.getQty().toString());

//							qty1 = products.getQty()
//									.toString();
//							Log.d("showProductFullName","-->"+showProductFullName+" "+qty1);
//							String numberD = qty1.substring ( qty1.indexOf ( "." ) );
//							double value =Double.parseDouble(numberD);
//							double tot_qty = Double.parseDouble(qty1);

//							String exchangeQty = qty1.split("\\.")[0];
//							Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty));
//							if(value>0){
//								cpclConfigLabel += text(280, y,twoDecimalPoint(tot_qty));
//							}else{
//								cpclConfigLabel += text(280, y,exchangeQty);
//							}
//							cpclConfigLabel += text(70, y, (products
//									.getDescription().length() > 31) ? products
//									.getDescription().substring(0, 30)
//									: products.getDescription());


						} else {

							cpclConfigLabel += text(70, y, (products
									.getDescription().length() > 10) ? products
									.getDescription().substring(0, 9)
									: products.getDescription());

							cpclConfigLabel += text(280, y += LINE_SPACING,
									products.getQty().toString());

//							qty1 = products.getQty().toString();
//							String numberD = qty1.substring ( qty1.indexOf ( "." ) );
//							double value =Double.parseDouble(numberD);
//							double tot_qty = Double.parseDouble(qty1);
//							Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty));
//							String exchangeQty = products.getQty().split("\\.")[0];
//							if(value>0){
//								cpclConfigLabel += text(280, y,twoDecimalPoint(tot_qty));
//							}else{
//								cpclConfigLabel += text(280, y,exchangeQty);
//							}


						}

						/*cpclConfigLabel += text(365, y, products.getPrice()
								.toString());*/
						double value =Double.parseDouble(products.getPrice().toString());
						//  Log.d("ParseString",""+value);
						String values=String.format("%."+decimal+"f",value);
						cpclConfigLabel += text(365, y, values);
						if(Company.getShortCode().matches("RAJAGRO")){
							cpclConfigLabel += text(486, y, products.getSubtotal()
									.toString());
						}else{
							cpclConfigLabel += text(486, y, products.getTotal()
									.toString());
						}

						if(tranType.matches("COI")) {
							Log.d("getConsignmentNumber", "-->" + products.getConsignmentNumber());
							if (products.getConsignmentNumber().toString().matches("")) {
							} else {
								Log.d("getConsignmentNumber1", "-->" + products.getConsignmentNumber());
								cpclConfigLabel += (text(350, y += LINE_SPACING,
										products.getConsignmentNumber()));
							}
						}

						// Log.d("products.getFocqty() 1", ""+(int)
						// products.getFocqty());

						if (products.getFocqty() > 0) {
							cpclConfigLabel += text(70, y += LINE_SPACING,
									"Foc")
									+ text(210, y, " : ")
									+ text(280, y, (int) products.getFocqty() + "(FOC)");
						}

						// Log.d("products.getExchangeqty() 1", ""+(int)
						// products.getExchangeqty());

						if (products.getExchangeqty() > 0) {
							cpclConfigLabel += text(70, y += LINE_SPACING,
									"Exchange")
									+ text(210, y, " : ")
									+ text(280, y,
											(int) products.getExchangeqty());
						}
						/*
						 * if (!products.getIssueQty().matches("0")) {
						 * cpclConfigLabel += text(70, y += LINE_SPACING,
						 * "Issue") + text(210, y, " : ") + text(280, y,
						 * products.getIssueQty()); } if
						 * (!products.getReturnQty().matches("0")) {
						 * cpclConfigLabel += text(375, y, "Return") + text(450,
						 * y, " : ") + text(486, y, products.getReturnQty()); }
						 */
						if ((products.getIssueQty() != null && !products
								.getIssueQty().isEmpty())
								&& (products.getReturnQty() != null && !products
										.getReturnQty().isEmpty())) {

							if ((Double.valueOf(products.getIssueQty()) > 0)
									&& (Double.valueOf(products.getReturnQty()) > 0)) {

								cpclConfigLabel += text(70, y += LINE_SPACING,
										"Issue")
										+ text(210, y, " : ")
										+ text(280, y, products.getIssueQty());

								cpclConfigLabel += text(375, y, "Return")
										+ text(450, y, " : ")
										+ text(486, y, products.getReturnQty());

							}
						}

						if (product_batch.size() > 0) {
							for (ProductDetails prodBatch : product_batch) {

								if (prodBatch.getBatch_productcode().matches(
										products.getItemcode())) {

									cpclConfigLabel += text(70,
											y += LINE_SPACING, prodBatch
													.getProduct_batchno()
													.toString());
								}

							}
						}

						s += i;
						i++;
					}
				}
				for (int i = 0; i < printsortHeader.size(); i++) {
					String catorsub = printsortHeader.get(i).toString();
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							catorsub + " :");
					for (ProductDetails prods : product) {
						if (catorsub.equalsIgnoreCase(prods.getSortproduct().toString())) {

							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, String.valueOf(s));

							/*
							 * cpclConfigLabel += text(280, y += LINE_SPACING,
							 * prods.getQty() .toString());
							 */
							if (showProductFullName.matches("True")) {
								if(Company.getShortCode().matches("JUBI")){
									cpclConfigLabel += text(
											70,
											y,
											(prods.getDescription().length() > 18) ? prods
													.getDescription().substring(0, 17)
													: prods.getDescription());
									cpclConfigLabel += text(280, y, prods.getQty()
											.toString());
								}else {
									cpclConfigLabel += text(
											70,
											y,
											(prods.getDescription().length() > 31) ? prods
													.getDescription().substring(0,
															30) : prods
													.getDescription());
									cpclConfigLabel += text(280, y += LINE_SPACING,
											prods.getQty().toString());
								}
							} else {
								cpclConfigLabel += text(
										70,
										y,
										(prods.getDescription().length() > 10) ? prods
												.getDescription().substring(0, 9) : prods
												.getDescription());
								cpclConfigLabel += text(280, y, prods.getQty()
										.toString());
							}
							double value =Double.parseDouble(prods.getPrice().toString());
							//  Log.d("ParseString",""+value);
							String values=String.format("%."+decimal+"f",value);
							cpclConfigLabel += text(365, y, values);
							if(Company.getShortCode().matches("RAJAGRO")){
								cpclConfigLabel += text(486, y, prods.getSubtotal()
										.toString());
							}else{
								cpclConfigLabel += text(486, y, prods.getTotal()
										.toString());
							}

							Log.d("getConsignmentNumber","-->"+prods.getConsignmentNumber());
							if(prods.getConsignmentNumber().toString().matches("")){
							}else{
								cpclConfigLabel += (text(350, y += LINE_SPACING,
										prods.getConsignmentNumber()));
							}



							// Log.d("products.getFocqty() 2", ""+(int)
							// prods.getFocqty());

							if (prods.getFocqty() > 0) {
								cpclConfigLabel += text(70, y += LINE_SPACING,
										"Foc")
										+ text(210, y, " : ")
										+ text(280, y, (int) prods.getFocqty());
							}

							// Log.d("products.getExchangeqty() 2", ""+(int)
							// prods.getExchangeqty());

							if (prods.getExchangeqty() > 0) {
								cpclConfigLabel += text(70, y += LINE_SPACING,
										"Exchange")
										+ text(210, y, " : ")
										+ text(280, y,
												(int) prods.getExchangeqty());
							}
							/*
							 * if (!prods.getIssueQty().matches("0")) {
							 * cpclConfigLabel += text(70, y += LINE_SPACING,
							 * "Issue") + text(210, y, " : ") + text(280, y,
							 * prods.getIssueQty()); } if
							 * (!prods.getReturnQty().matches("0")) {
							 * cpclConfigLabel += text(375, y, "Return") +
							 * text(450, y, " : ") + text(486, y,
							 * prods.getReturnQty()); }
							 */

							if ((prods.getIssueQty() != null && !prods
									.getIssueQty().isEmpty())
									&& (prods.getReturnQty() != null && !prods
											.getReturnQty().isEmpty())) {

								if ((Double.valueOf(prods.getIssueQty()) > 0)
										&& (Double
												.valueOf(prods.getReturnQty()) > 0)) {

									cpclConfigLabel += text(70,
											y += LINE_SPACING, "Issue")
											+ text(210, y, " : ")
											+ text(280, y, prods.getIssueQty());

									cpclConfigLabel += text(375, y, "Return")
											+ text(450, y, " : ")
											+ text(486, y, prods.getReturnQty());

								}
							}
							if (product_batch.size() > 0) {
								for (ProductDetails prodBatch : product_batch) {

									if (prodBatch.getBatch_productcode()
											.matches(prods.getItemcode())) {

										cpclConfigLabel += text(70,
												y += LINE_SPACING, prodBatch
														.getProduct_batchno()
														.toString());
									}

								}
							}
							s++;

						}

					}
				}
			} else {

				for (ProductDetails products : product) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							products.getSno().toString());
					taxtype = products.getTaxType().toString();

					Log.d("checkTaxType","-->"+taxtype);

					Log.d("showProductFullName","-->"+showProductFullName+" "+ products.getQty());
					if (showProductFullName.matches("True")) {

						if(Company.getShortCode().matches("JUBI")){
							if(products.getDescription().length() > 30){
								cpclConfigLabel += text(
										70,
										y,
										(products.getDescription().length() > 31) ? products
												.getDescription().substring(0, 30)
												: products.getDescription());

							}else{
								cpclConfigLabel += text(
										70,
										y,
										(products.getDescription().length() > 18) ? products
												.getDescription().substring(0, 17)
												: products.getDescription());
							}

							cpclConfigLabel += text(280, y, products.getQty()
									.toString());
						}else {

							cpclConfigLabel += text(
									70,
									y,
									(products.getDescription().length() > 31) ? products
											.getDescription().substring(0, 30)
											: products.getDescription());

//						qty1 = products.getQty().toString();
//						String numberD = qty1.substring ( qty1.indexOf ( "." ) );
//						double value =Double.parseDouble(numberD);
//						double tot_qty = Double.parseDouble(qty1);
//
//						String exchangeQty = qty1.split("\\.")[0];
//						Log.d("DecimalValue1","-->"+ twoDecimalPoint(tot_qty)+value);
//						if(value>0){
//							cpclConfigLabel += text(280, y,twoDecimalPoint(tot_qty));
//						}else{
//							cpclConfigLabel += text(280, y, exchangeQty);
//						}

							cpclConfigLabel += text(280, y += LINE_SPACING,
									products.getQty().toString());
						}
					} else {
						cpclConfigLabel += text(
								100,
								y,
								(products.getDescription().length() > 10) ? products
										.getDescription().substring(0, 9)
										: products.getDescription());
						cpclConfigLabel += text(280, y, products.getQty()
								.toString());


//						qty1 = products.getQty().toString();
//						String numberD = qty1.substring ( qty1.indexOf ( "." ) );
//						double value =Double.parseDouble(numberD);
//						double tot_qty = Double.parseDouble(qty1);
//
//						String exchangeQty = qty1.split("\\.")[0];
//						if(value>0){
//							Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty));
//							cpclConfigLabel += text(280, y,twoDecimalPoint(tot_qty));
//						}else{
//						cpclConfigLabel += text(280, y, exchangeQty);
//						}
					}
					/*cpclConfigLabel += text(365, y, products.getPrice()
							.toString());*/
					double value =Double.parseDouble(products.getPrice().toString());
					//  Log.d("ParseString",""+value);
					String values=String.format("%."+decimal+"f",value);
					cpclConfigLabel += text(365, y, values);
					Log.d("getConsignmentNumber","-->"+products.getConsignmentNumber());
					if(Company.getShortCode().matches("RAJAGRO")){
						cpclConfigLabel += text(486, y, products.getSubtotal()
								.toString());
					}else{
						cpclConfigLabel += text(486, y, products.getTotal()
								.toString());
					}
					if(products.getConsignmentNumber().toString().matches("")){
					}else{
						cpclConfigLabel += (text(350, y += LINE_SPACING,
								products.getConsignmentNumber()));
					}

					Log.d("products.getFocqty() 3",
							"" + (int) products.getFocqty());

					if (products.getFocqty() > 0) {
						cpclConfigLabel += text(70, y += LINE_SPACING, "Foc")
								+ text(210, y, " : ")
								+ text(280, y, "" + (int) products.getFocqty());
					}

					Log.d("getExchangeqty() 3",
							"" + (int) products.getExchangeqty());

					if (products.getExchangeqty() > 0) {
						cpclConfigLabel += text(70, y += LINE_SPACING,
								"Exchange")
								+ text(210, y, " : ")
								+ text(280, y,
										"" + (int) products.getExchangeqty());
					}
					/*
					 * if (!products.getIssueQty().matches("0")) {
					 * cpclConfigLabel += text(70, y += LINE_SPACING, "Issue") +
					 * text(210, y, " : ") + text(280, y,
					 * products.getIssueQty()); } if
					 * (!products.getReturnQty().matches("0")) { cpclConfigLabel
					 * += text(375, y, "Return") + text(450, y, " : ") +
					 * text(486, y, products.getReturnQty()); }
					 */
					if ((products.getIssueQty() != null && !products
							.getIssueQty().isEmpty())
							&& (products.getReturnQty() != null && !products
									.getReturnQty().isEmpty())) {

						if ((Double.valueOf(products.getIssueQty()) > 0)
								&& (Double.valueOf(products.getReturnQty()) > 0)) {

							cpclConfigLabel += text(70, y += LINE_SPACING,
									"Issue")
									+ text(210, y, " : ")
									+ text(280, y, products.getIssueQty());

							cpclConfigLabel += text(375, y, "Return")
									+ text(450, y, " : ")
									+ text(486, y, products.getReturnQty());

						}
					}

					if (product_batch.size() > 0) {
						for (ProductDetails prodBatch : product_batch) {

							if (prodBatch.getBatch_productcode().matches(
									products.getItemcode())) {

								cpclConfigLabel += text(70, y += LINE_SPACING,
										prodBatch.getProduct_batchno()
												.toString());
							}

						}
					}

				}
			}
				Log.d("checkTaxType","-->"+taxtype);
			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			for (ProductDetails prd : productdet) {

				if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Item Disc")
							+ text(140, y, " : ")
							+ text(180, y, prd.getItemdisc().toString());
				}

				if (Double.parseDouble(prd.getTax().toString()) > 0) {

					if (Double.parseDouble(prd.getItemdisc().toString()) == 0) {

						if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, "Bill Disc")
									+ text(140, y, " : ")
									+ text(180, y, prd.getBilldisc().toString());
						}
						if (Double.parseDouble(prd.getBilldisc().toString()) == 0
								&& Double.parseDouble(prd.getItemdisc()
										.toString()) == 0) {
							if(showGST.equalsIgnoreCase("True")) {
								Log.d("taxtype","taxtype :"+taxtype+"Nettot :"+prd.getNettot()+"getTeax  :"+prd.getTax());
								double nt =Double.parseDouble(prd.getNettot());
								double tx =Double.parseDouble(prd.getTax());
								double st =nt-tx;

								Log.d("ResultValue","net :"+nt+"tax :"+tx+"subtotal :"+st+"StringFormat:"+String.format("%.2f",st));
//								String sub = String.valueOf(st);
								String sub =String.format("%.2f",st);

								if(taxtype!=null && !taxtype.isEmpty()) {
									if (taxtype.matches("I")) {
										Log.d("subtotalCaption","-->"+sub);
										cpclConfigLabel += text(310, y += LINE_SPACING,
												subtotalCaption)
												+ text(450, y, " : ")
												+ text(486, y, sub);
									}else{
										cpclConfigLabel += text(310, y += LINE_SPACING,
												subtotalCaption)
												+ text(450, y, " : ")
												+ text(486, y, prd.getSubtotal().toString());
									}
								}else{
									cpclConfigLabel += text(310, y += LINE_SPACING,
											subtotalCaption)
											+ text(450, y, " : ")
											+ text(486, y, prd.getSubtotal().toString());
								}


							}
						} else {
							if(showGST.equalsIgnoreCase("True")) {
								Log.d("taxtype","taxtype :"+taxtype+"Nettot :"+prd.getNettot()+"getTeax  :"+prd.getTax());
								double nt =Double.parseDouble(prd.getNettot());
								double tx =Double.parseDouble(prd.getTax());
								double st =nt-tx;
								Log.d("ResultValue","net :"+nt+"tax :"+tx+"subtotal :"+st);
//								String sub = String.valueOf(st);
								String sub =String.format("%.2f",st);
								if(taxtype!=null && !taxtype.isEmpty()) {
									if (taxtype.matches("I")) {
										cpclConfigLabel += text(310, y += LINE_SPACING,
												subtotalCaption)
												+ text(450, y, " : ")
												+ text(486, y, sub);
									}else{
										cpclConfigLabel += text(310, y += LINE_SPACING,
												subtotalCaption)
												+ text(450, y, " : ")
												+ text(486, y, prd.getSubtotal().toString());
									}
								}
							}
						}
					} else {
						if(showGST.equalsIgnoreCase("True")) {
//							taxtype =prd.getTaxType().toString();
							Log.d("taxtype","taxtype :"+taxtype+"Nettot :"+prd.getNettot()+"getTeax  :"+prd.getTax());
							double nt =Double.parseDouble(prd.getNettot());
							double tx =Double.parseDouble(prd.getTax());
							double st =nt-tx;
							Log.d("ResultValue","net :"+nt+"tax :"+tx+"subtotal :"+st);
//							String sub = String.valueOf(st);
							String sub =String.format("%.2f",st);
							if(taxtype!=null && !taxtype.isEmpty()) {
								if (taxtype.matches("I")) {
									cpclConfigLabel += text(310, y += LINE_SPACING,
											subtotalCaption)
											+ text(450, y, " : ")
											+ text(486, y, sub);
								}else{
									cpclConfigLabel += text(310, y += LINE_SPACING,
											subtotalCaption)
											+ text(450, y, " : ")
											+ text(486, y, prd.getSubtotal().toString());
								}
							}
						}
					}
				}

				if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
					if (Double.parseDouble(prd.getItemdisc().toString()) != 0) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Bill Disc")
								+ text(140, y, " : ")
								+ text(180, y, prd.getBilldisc().toString());
					} else {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"") + text(140, y, "") + text(180, y, "");
					}
				}

				if (Double.parseDouble(prd.getTax().toString()) > 0) {

					if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"") + text(140, y, "") + text(180, y, "");
					}

					if(showGST.equalsIgnoreCase("True")) {
						if (!InvoiceTaxCaption.equals("")) {
//							cpclConfigLabel += text(310, y, InvoiceTaxCaption)
//									+ text(450, y, " : ")
//									+ text(486, y, prd.getTax().toString());

							cpclConfigLabel += text(310, y, InvoiceTaxCaption +" ("+taxPerc.split("\\.")[0] +")"+" % ")
										+ text(450, y, " : ")
										+ text(486, y, prd.getTax().toString());


						} else {
							String txType = taxtype;
							Log.d("txType","-->"+txType);
							if(txType!=null && !txType.isEmpty()) {
								if (txType.matches("I")) {
									cpclConfigLabel += text(240, y, "GST Incl "+"("+taxPerc.split("\\.")[0] +")"+" % ")
											+ text(450, y, " : ")
											+ text(486, y, prd.getTax().toString());
								}else{
									cpclConfigLabel += text(240, y, "GST Excl "+"("+taxPerc.split("\\.")[0] +")"+" % ")
											+ text(450, y, " : ")
											+ text(486, y, prd.getTax().toString());
								}
							}else{
								cpclConfigLabel += text(310, y, "Tax")
										+ text(450, y, " : ")
										+ text(486, y, prd.getTax().toString());
							}

						}
					}
//					if(showGST.equalsIgnoreCase("True")) {
//							cpclConfigLabel += text(310, y, "Tax")
//									+ text(450, y, " : ")
//									+ text(486, y, prd.getTax().toString());
//					}
				}
				if (Double.parseDouble(prd.getTax().toString()) == 0) {

					cpclConfigLabel += text(310, y, "") + text(450, y, "")
							+ text(486, y, "");

					if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
//						cpclConfigLabel += horizontalLine(y += LINE_SPACING,LINE_THICKNESS);
						cpclConfigLabel += text(310, y += LINE_SPACING,
								nettotalCaption)
								+ text(450, y, " : ")
								+ text(486, y, prd.getNettot().toString());

					} else {

						cpclConfigLabel += text(LEFT_MARGIN,
								y += LINE_SPACING, "Bill Disc")
								+ text(140, y, " : ")
								+ text(180, y, prd.getBilldisc().toString());

						cpclConfigLabel += horizontalLine(y += LINE_SPACING,LINE_THICKNESS);
						cpclConfigLabel += text(310, y += LINE_SPACING, nettotalCaption)
								+ text(450, y, " : ")
								+ text(486, y, prd.getNettot().toString());
					}
				} else {
					cpclConfigLabel += horizontalLine(y += LINE_SPACING,LINE_THICKNESS);
					cpclConfigLabel += text(310, y += LINE_SPACING, nettotalCaption)
							+ text(450, y, " : ")
							+ text(486, y, prd.getNettot().toString());
				}

				cpclConfigLabel += horizontalLine(y += LINE_SPACING,
						LINE_THICKNESS);

				if (!prd.getRemarks().matches("")) {
					custlastname =prd.getRemarks();
					if (custlastname.length()>25){
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Remarks")
								+ text(210, y, " : ")
								+ text(230, y,
								(custlastname.length() > 25) ? custlastname.substring(0, 24)
										: custlastname);
						int count =0;
						int len =prd.getRemarks().length();
						int get_len = prd.getRemarks().substring(25,len).length();
						String remark =prd.getRemarks().substring(25,len);
						Log.d("RemarkLength","-->"+remark+" "+get_len);
						StringBuilder cpclConfig =new StringBuilder();
						String names;

						for(int i=0;i<get_len;i=i+24){
							count=count+24;
							if(count>get_len){
								names = remark.substring(i,get_len);
								System.out.println(names);
								cpclConfigLabel += (text(
										230, y += LINE_SPACING,names));
							}else{
								names = remark.substring(i,i+24);
								System.out.println(names);
								cpclConfigLabel += (text(
										230, y += LINE_SPACING,names));
							}
						}
					}else{
						Log.d("RemarksLength","-->"+custlastname.length());
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Remarks")
								+ text(140, y, " : ")
								+ text(180,
								y,
								(custlastname.length() > 25) ? custlastname.substring(0, 24)
										: custlastname);
					}
					Log.d("RemarksStatus1", "-->" + cpclConfigLabel);
				}


//					Log.d("custlastname","-->"+custlastname);
//
//					for (int i = 24; i<get_len; i=i+24) {
//						Log.d("RemarksStatus", "--->"  + custlastname.length() +"   "+plus);
//						if (custlastname.length() > plus) {
//							 custlastname = prd.getRemarks().substring(plus);
//
//							cpclConfigLabel += (text(
//									230, y += LINE_SPACING,
//									(custlastname.length() > 25) ?custlastname
//											.substring(i+0,i+24) :custlastname));
//						}
//						plus=plus+24;
//					}


//



//					if (prd.getRemarks().length() > 25) {
//						String custlastname = prd.getRemarks().substring(24);
//
//						Log.d("RemarksStatus",""+prd.getRemarks()+"  "+custlastname);
//
//							cpclConfigLabel += (text(
//									230, y += LINE_SPACING,
//									(custlastname.length() > 25) ?custlastname
//											.substring(0,24) :custlastname));
//
//						Log.d("cpclConfigLabel1","-->"+cpclConfigLabel);
//					}
//
//					if (prd.getRemarks().length() > 48) {
//						String custlastname = prd.getRemarks().substring(47);
//
//						Log.d("RemarksStatus",""+prd.getRemarks()+"  "+custlastname);
//
//						cpclConfigLabel += (text(
//								230, y += LINE_SPACING,
//								(custlastname.length() > 25) ?custlastname
//										.substring(0,24) :custlastname));
//
//						Log.d("cpclConfigLabel1","-->"+cpclConfigLabel);
//					}
//
//					cpclConfigLabel += horizontalLine(y += LINE_SPACING,
//							LINE_THICKNESS);
//				}
				if(!Company.getShortCode().matches("HEZOM")) {
						if (showTotalOutstanding.matches("True")) {
							Log.d("getTotaloutstanding", "-->" + prd.getTotaloutstanding());
							if (prd.getTotaloutstanding() != null
									&& !prd.getTotaloutstanding().isEmpty()
									&& !prd.getTotaloutstanding().matches("null")) {

								cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
										"Total Outstanding")
										+ text(210, y, " : ")
										+ text(230, y, prd.getTotaloutstanding());

//						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
//								LINE_THICKNESS);

							}
						}
					}
			}

			}
			// if(showTotalOutstanding.matches("True")){
			// cpclConfigLabel += horizontalLine(y += LINE_SPACING,
			// LINE_THICKNESS);
			// }

			SOTDatabase.init(context);
			String img = SOTDatabase.getSignatureImage();
			Log.d("Do_Nothing", "---->"+img +"  "+showFooter);
			if (showFooter.matches("True")) {

				if (img!= null && !img.isEmpty()) {

					Log.d("Do_Nothing1", "Do Nothing");

				}else{

					if(SalesOrderSetGet.getShortCode().matches("HEZOM")){
						String username=SupplierSetterGetter.getUsername();

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"--------------------------------------------");

						cpclConfigLabel += text(75, y += 140,
								"Received By");


						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"--------------------------------------------");

						cpclConfigLabel += text(75, y += LINE_SPACING,
								"Issued  By")+ text(210, y, " : ")
								+ text(240, y,username.toUpperCase());

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"--------------------------------------------");


						String caps ="<b>" + SupplierSetterGetter.getCompanyName().toUpperCase() + "</b> " ;

						Log.d("BoldText","-->"+Html.fromHtml(caps));

						String extra_added ="* ALL CHEQUES SHOULD BE CROSSED AND MADE PAYABLE TO "+ Html.fromHtml(caps)+".";

						String added = "* GOODS RECEIVED IN GOOD ORDER & CONDITIONS.";

						String goods ="* GOODS SOLD ARE NOT RETURNABLE.";

						int count =0;
						int count1=0;

						int gen_len =extra_added.length();

						for(int i=0;i<extra_added.length();i=i+36){
							count=count+36;
							if(count>gen_len){
								String names =extra_added.substring(i,gen_len);
								System.out.println(names);
								cpclConfigLabel +=text(LEFT_MARGIN, y += LINE_SPACING,
										names);
							}else{
								String names =extra_added.substring(i,i+36);
								System.out.println(names);
								cpclConfigLabel +=text(LEFT_MARGIN, y += LINE_SPACING,
										names);
							}

						}


						int gen_len1 =added.length();
						for(int i=0;i<added.length();i=i+33){
							count1=count1+33;
							if(count1>gen_len1){
								String names =added.substring(i,gen_len1);
								System.out.println(names);
								cpclConfigLabel +=text(LEFT_MARGIN, y += LINE_SPACING,
										names);
							}else{
								String names =added.substring(i,i+33);
								System.out.println(names);
								cpclConfigLabel +=text(LEFT_MARGIN, y += LINE_SPACING,
										names);
							}

						}

						cpclConfigLabel +=text(LEFT_MARGIN, y += LINE_SPACING,
								(goods.length() > 40) ? goods.substring(0, 39)
										: goods);
					}else {


						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
						// cpclConfigLabel += horizontalLine(y += LINE_SPACING,
						// LINE_THICKNESS);
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"-----------------   ------------------");

						cpclConfigLabel += text(75, y += LINE_SPACING, "Received By");

						cpclConfigLabel += text(350, y, "Authorized By");

						Log.d("footerArr", "" + footerArr.size());

						if (footerArr.size() > 0) {

							// cpclConfigLabel += text(LEFT_MARGIN, y +=
							// LINE_SPACING,
							// "--------------*********---------------");
							// cpclConfigLabel += text(230, y += LINE_SPACING, "");
							// cpclConfigLabel += text(230, y += LINE_SPACING,
							// "*********");
							cpclConfigLabel += horizontalLine(y += LINE_SPACING,
									LINE_THICKNESS);

							for (ProductDetails footer : footerArr) {

								Log.d("footer value",
										"val " + footer.getReceiptMessage());

								if (footer.getReceiptMessage() != null
										&& !footer.getReceiptMessage().isEmpty()) {
									cpclConfigLabel += text(LEFT_MARGIN,
											y += LINE_SPACING,
											footer.getReceiptMessage());
								}
							}
						}
					}

				}

			}
			Log.d("invoiceprint",cpclConfigLabel);
			// Just append everything and create a single string
			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 2)) + " 1" +"\r\nCOUNTRY CHINA"
					+ LINE_SEPARATOR + HEADER  +cpclConfigLabel + CMD_PRINT;

			// start
//			String valueUTF8 = URLEncoder.encode(cpclConfigLabel, "UTF-8");
//			String res = valueUTF8.replaceAll("\\%", "\\_");
//			String result = res.replaceAll("\\+", "\\ ");
//
//			Log.d("valueUTF8",""+result);
//			String finalResult = "^XA^POI^LL150^CI28^A@N,25,25,E:MSUNG.TTF^FO0,0^FH^FD" + result + "^FS^XZ";
//
//			Log.d("finalResult",""+finalResult);

//			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 2)) + " 1" +"\r\nCOUNTRY CHINA"
//					+ LINE_SEPARATOR + HEADER  + result + CMD_PRINT;
			// end

//			byte[] arrayOfByte1 = { 27, 33, 0 };
//			byte[] format = { 27, 33, 0 };
//			format[2] = ((byte)(0x8 | arrayOfByte1[2]));
//			os.write(format);

			Log.d("cpclConfigLabel",cpclConfigLabel);
//			os.write(cpclConfigLabel.getBytes(),0,cpclConfigLabel.getBytes().length);
			os.write(cpclConfigLabel.getBytes());
			os.flush();
		}
		// ///////////////////////////////////////////////////////////////////////////////////

		os.close();

	}

	public void deliveryOrderOnInvoice(String invoiceno, String invoicedate,
			String customercode, String customername,
			List<ProductDetails> product, List<ProductDetails> productdet,
			List<String> printsortHeader, String gnrlStngs, int nofcopies,
			List<ProductDetails> product_batch, List<ProductDetails> footerValue)
			throws IOException, ZebraPrinterConnectionException {
		// Used the calculate the y axis printing position dynamically

		// String invoiceprintdetail = SalesOrderSetGet.getInvoiceprintdetail();

		logoStr = "logoprint";
		footerArr.clear();
		footerArr = footerValue;

		String showCustomerCode = MobileSettingsSetterGetter
				.getShowCustomerCode();
		String showCustomerName = MobileSettingsSetterGetter
				.getShowCustomerName();
		String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
		String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
		String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
		String showCustomerPhone = MobileSettingsSetterGetter
				.getShowCustomerPhone();
		String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
		String showCustomerEmail = MobileSettingsSetterGetter
				.getShowCustomerEmail();
		String showCustomerTerms = MobileSettingsSetterGetter
				.getShowCustomerTerms();
		String showFooter = MobileSettingsSetterGetter.getShowFooter();
		String showcartonloose = SalesOrderSetGet
			    .getCartonpriceflag();
		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		for (int n = 0; n < nofcopies; n++) {
			double totalqty = 0;
			int y = 0;
			int s = 1;


			StringBuilder temp = new StringBuilder();
			y = printTitle(180, y, "DELIVERY ORDER", temp);
			y = printCompanyDetails(y, temp);

			String cpclConfigLabel = temp.toString();

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
					"Invoice No")
					+ text(150, y, " : ")
					+ text(180, y, invoiceno);
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
					"Invoice Date")
					+ text(150, y, " : ")
					+ text(180, y, invoicedate);

			if (showCustomerCode.matches("True")) {

				if (!customercode.matches("")) {
					cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
							"Cust Code"));
					cpclConfigLabel += (text(150, y, " : "));
					cpclConfigLabel += (text(180, y, customercode));
				}
			}

			if (showCustomerName.matches("True")) {

				if (!customername.matches("")) {
					cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
							"Cust Name"));
					cpclConfigLabel += (text(150, y, " : "));

					cpclConfigLabel += (text(
							180,
							y,
							(customername.length() > 25) ? customername
									.substring(0, 24) : customername));

					if (customername.length() > 25) {

						String custlastname = customername.substring(24);

						Log.d("Custnameeee", custlastname);

						cpclConfigLabel += (text(
								180,
								y += LINE_SPACING,
								(custlastname.length() > 25) ? custlastname
										.substring(0, 24) : custlastname));
					}
				}
			}

			if (showAddress1.matches("True")) {
				if (!CustomerSetterGetter.getCustomerAddress1().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Address 1")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerAddress1());
				}
			}

			if (showAddress2.matches("True")) {
				if (!CustomerSetterGetter.getCustomerAddress2().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Address 2")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerAddress2());
				}
			}

			if (showAddress3.matches("True")) {
				if (!CustomerSetterGetter.getCustomerAddress3().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Address 3")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerAddress3());
				}
			}

			if (showCustomerPhone.matches("True")) {
				if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Phone No")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerPhone());
				}
			}

			if (showCustomerHP.matches("True")) {
				if (!CustomerSetterGetter.getCustomerHP().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Head Phone")
							+ text(150, y, " : ")
							+ text(180, y, CustomerSetterGetter.getCustomerHP());
				}
			}

			if (showCustomerEmail.matches("True")) {
				if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Email")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerEmail());
				}
			}

			if (showCustomerTerms.matches("True")) {
				if (!CustomerSetterGetter.getCustomerTerms().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Terms")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerTerms());

				}
			}

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			if(product.size()>0){
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
			cpclConfigLabel += text(70, y, "Description");
			cpclConfigLabel += text(490, y, "Qty");

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			if (gnrlStngs.matches("C") || gnrlStngs.matches("S")) {
				for (ProductDetails products : product) {

					if ((products.getSortproduct().matches(""))
							|| (products.getSortproduct().matches("0"))) {
						int i = 1;
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								String.valueOf(i));

						cpclConfigLabel += text(
								70,
								y,
								(products.getDescription().length() > 26) ? products
										.getDescription().substring(0, 25)
										: products.getDescription());

					       //showing qty
						cpclConfigLabel += text(486, y, products.getQty()
								.toString());
						if (products.getQty() != null
								&& !products.getQty().isEmpty()) {

							String getqty = products.getQty().split("\\ ")[0];
							totalqty += Double.valueOf(products.getQty());
//							String getqty = products.getQty().split("\\ ")[0];
//							String quan= products.getQty();
//							String numberD = quan.substring ( quan.indexOf ( "." ) );
//							double value =Double.parseDouble(numberD);
//							double tot_qty = Double.parseDouble(quan);
//							Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty));
//							if(value>0){
//								totalqty += Double.valueOf(twoDecimalPoint(tot_qty));
//							}else{
//								totalqty += Double.valueOf(getqty);
//
//							}
//							Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty));
//
						}
					    //  }
						if ((products.getIssueQty() != null && !products
								.getIssueQty().isEmpty())
								&& (products.getReturnQty() != null && !products
										.getReturnQty().isEmpty())) {

							if ((Double.valueOf(products.getIssueQty()) > 0)
									&& (Double.valueOf(products.getReturnQty()) > 0)) {

								cpclConfigLabel += text(70, y += LINE_SPACING,
										"Issue")
										+ text(210, y, " : ")
										+ text(280, y, products.getIssueQty());

								cpclConfigLabel += text(375, y, "Return")
										+ text(450, y, " : ")
										+ text(486, y, products.getReturnQty());

							}
						}

						s += i;
						i++;
					}
				}
				for (int i = 0; i < printsortHeader.size(); i++) {
					String catorsub = printsortHeader.get(i).toString();
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							catorsub + " :");
					for (ProductDetails prods : product) {
						if (catorsub.equalsIgnoreCase(prods.getSortproduct().toString())) {

							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, String.valueOf(s));

							cpclConfigLabel += text(
									70,
									y,
									(prods.getDescription().length() > 26) ? prods
											.getDescription().substring(0, 25)
											: prods.getDescription());

						       //showing qty
							cpclConfigLabel += text(486, y, prods.getQty()
									.toString());
//							String getqty = prods.getQty().split("\\ ")[0];
//							totalqty += Double.valueOf(prods.getQty());
							if (prods.getQty() != null
									&& !prods.getQty().isEmpty()) {
//								String quantity =prods.getQty();
								String getqty = prods.getQty().split("\\ ")[0];
//								String numberD = quantity.substring ( quantity.indexOf ( "." ) );
//								double value =Double.parseDouble(numberD);
//								double tot_qty = Double.parseDouble(quantity);
//								Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty));
//								String qty = twoDecimalPoint(tot_qty);
//								if(value>0){
//									totalqty += Double.valueOf(qty);
//								}else{
//									totalqty += Double.valueOf(prods.getQty());
//								}

								totalqty += Double.valueOf(prods.getQty());

							}
						     // }
							if ((prods.getIssueQty() != null && !prods
									.getIssueQty().isEmpty())
									&& (prods.getReturnQty() != null && !prods
											.getReturnQty().isEmpty())) {

								if ((Double.valueOf(prods.getIssueQty()) > 0)
										&& (Double
												.valueOf(prods.getReturnQty()) > 0)) {

									cpclConfigLabel += text(70,
											y += LINE_SPACING, "Issue")
											+ text(210, y, " : ")
											+ text(280, y, prods.getIssueQty());

									cpclConfigLabel += text(375, y, "Return")
											+ text(450, y, " : ")
											+ text(486, y, prods.getReturnQty());

								}
							}

							s++;

						}

					}
				}
			} else {
				for (ProductDetails products : product) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							products.getSno().toString());

					cpclConfigLabel += text(70, y, (products.getDescription()
							.length() > 26) ? products.getDescription()
							.substring(0, 25) : products.getDescription());

				       //showing qty
					cpclConfigLabel += text(486, y, products.getQty()
							.toString());
					if (products.getQty() != null
							&& !products.getQty().isEmpty()) {

						String getqty = products.getQty().split("\\ ")[0];

						totalqty += Double.valueOf(getqty);
					}
				     // }
					if ((products.getIssueQty() != null && !products
							.getIssueQty().isEmpty())
							&& (products.getReturnQty() != null && !products
									.getReturnQty().isEmpty())) {

						if ((Double.valueOf(products.getIssueQty()) > 0)
								&& (Double.valueOf(products.getReturnQty()) > 0)) {

							cpclConfigLabel += text(70, y += LINE_SPACING,
									"Issue")
									+ text(210, y, " : ")
									+ text(280, y, products.getIssueQty());

							cpclConfigLabel += text(375, y, "Return")
									+ text(450, y, " : ")
									+ text(486, y, products.getReturnQty());

						}
					}

				}
			}
			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			cpclConfigLabel += text(310, y += LINE_SPACING, "Total Qty")
					+ text(450, y, " : ")
					+ text(486, y, String.valueOf(totalqty).split("\\.")[0]);

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			}

			SOTDatabase.init(context);
			String img = SOTDatabase.getSignatureImage();
			Log.d("Signature_image", img);
			if (showFooter.matches("True")) {

				if (!img.matches("")) {
					Log.d("Do_Nothing", "Do Nothing");
					Log.d("SignatureImage", img);
				} else {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
					// cpclConfigLabel += horizontalLine(y += LINE_SPACING,
					// LINE_THICKNESS);
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"-----------------   ------------------");

					cpclConfigLabel += text(75, y += LINE_SPACING,
							"Received By");

					cpclConfigLabel += text(350, y, "Authorized By");

					Log.d("footerArr", "" + footerArr.size());

					if (footerArr.size() > 0) {

						// cpclConfigLabel += text(LEFT_MARGIN, y +=
						// LINE_SPACING,
						// "--------------*********---------------");
						// cpclConfigLabel += text(230, y += LINE_SPACING, "");
						// cpclConfigLabel += text(230, y += LINE_SPACING,
						// "*********");
						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
								LINE_THICKNESS);

						for (ProductDetails footer : footerArr) {

							Log.d("footer value",
									"val " + footer.getReceiptMessage());

							if (footer.getReceiptMessage() != null
									&& !footer.getReceiptMessage().isEmpty()) {
								cpclConfigLabel += text(LEFT_MARGIN,
										y += LINE_SPACING,
										footer.getReceiptMessage());
							}
						}
					}

				}
			}

			// cpclConfigLabel += horizontalLine(y += LINE_SPACING,
			// LINE_THICKNESS);
			// Just append everything and create a single string
			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 2)) + " 1"
					+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

			/*
			 * FileOutputStream os = context.openFileOutput(FILE_NAME,
			 * Context.MODE_PRIVATE);
			 */
			os.write(cpclConfigLabel.getBytes());
			os.flush();
		}
		// ///////////////////////////////////////////////////////////////////////////////////

		os.close();

	}

	public void bottomPrint() throws IOException, ConnectionException {

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);

		File filepath = context.getFileStreamPath(FILE_NAME);
		try {
			printer.sendFileContents(filepath.getAbsolutePath());
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		SOTDatabase.init(context);

		if(SalesOrderSetGet.getShortCode().matches("HEZOM")){


			Log.d("SalesOrderSetGet","--->"+SalesOrderSetGet.getShortCode());

			int y = 0;

			StringBuilder temp = new StringBuilder();

			String cpclConfig = temp.toString();

			String username=SupplierSetterGetter.getUsername();

			cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
					"--------------------------------------------");


			cpclConfig += text(75, y += LINE_SPACING,
					"Received By");


			cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
					"--------------------------------------------");

			cpclConfig += text(75, y += LINE_SPACING,
					"Issued  By")+ text(210, y, " : ")
					+ text(240, y,username.toUpperCase());

			cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
					"--------------------------------------------");


			String caps ="<b>" + SupplierSetterGetter.getCompanyName().toUpperCase() + "</b> " ;

			Log.d("BoldText","-->"+Html.fromHtml(caps));

			String extra_added ="* ALL CHEQUES SHOULD BE CROSSED AND MADE PAYABLE TO "+ Html.fromHtml(caps)+".";

			String added = "* GOODS RECEIVED IN GOOD ORDER & CONDITIONS.";

			String goods ="* GOODS SOLD ARE NOT RETURNABLE.";

			int count =0;
			int count1=0;

			int gen_len =extra_added.length();

			for(int i=0;i<extra_added.length();i=i+36){
				count=count+36;
				if(count>gen_len){
					String names =extra_added.substring(i,gen_len);
					System.out.println(names);
					cpclConfig +=text(LEFT_MARGIN, y += LINE_SPACING,
							names);
				}else{
					String names =extra_added.substring(i,i+36);
					System.out.println(names);
					cpclConfig +=text(LEFT_MARGIN, y += LINE_SPACING,
							names);
				}

			}


			int gen_len1 =added.length();
			for(int i=0;i<added.length();i=i+33){
				count1=count1+33;
				if(count1>gen_len1){
					String names =added.substring(i,gen_len1);
					System.out.println(names);
					cpclConfig +=text(LEFT_MARGIN, y += LINE_SPACING,
							names);
				}else{
					String names =added.substring(i,i+33);
					System.out.println(names);
					cpclConfig +=text(LEFT_MARGIN, y += LINE_SPACING,
							names);
				}

			}

			cpclConfig +=text(LEFT_MARGIN, y += LINE_SPACING,
					(goods.length() > 40) ? goods.substring(0, 39)
							: goods);


//			String img = SOTDatabase.getSignatureImage();
//			Log.d("Print_img", "" + img);
//
//			byte[] encodeByte;
//
//			byte[] encodeByte1 = Base64.decode(img, Base64.DEFAULT);
//
//			String s;
//			try {
//				s = new String(encodeByte1, "UTF-8");
//				encodeByte = Base64.decode(s, Base64.DEFAULT);
//			} catch (Exception e) {
//				encodeByte = encodeByte1;
//			}
//
//			Log.d("encodeByte","-->"+encodeByte);
//
//			Bitmap photo = BitmapFactory.decodeByteArray(
//					encodeByte, 0, encodeByte.length);
//
//
//			Bitmap image = Bitmap.createBitmap(photo, 0, 0,
//					photo.getWidth(), photo.getHeight(), matrix,
//					true);
//			Log.d("Print_imgPhoto", "---->" + photo + "  "+image);
//
//			printer.printImage(new ZebraImageAndroid(image),
//					-300, 0, 300, 80, false);

			Log.d("footerArr bottom", "" + footerArr.size());

			if (footerArr.size() > 0) {
				cpclConfig += verticalLine(y += LINE_SPACING, LINE_THICKNESS);

				for (ProductDetails footer : footerArr) {

					Log.d("footer value", "val " + footer.getReceiptMessage());

					if (footer.getReceiptMessage() != null
							&& !footer.getReceiptMessage().isEmpty()) {
						cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
								footer.getReceiptMessage());
					}
				}
			}

			cpclConfig = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
					+ LINE_SEPARATOR + HEADER + cpclConfig + CMD_PRINT;

			os.write(cpclConfig.getBytes());
			os.flush();
			os.close();

		}else {

			if(Company.getShortCode().matches("JUBI")){
				{
					int y = 0;

					StringBuilder temp = new StringBuilder();

					String cpclConfig = temp.toString();


					cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
							"-----------------   ------------------");

					cpclConfig += text(75, y += LINE_SPACING, "Received By");

					cpclConfig += text(350, y, "Authorized By");

					Log.d("footerArr bottom", "" + footerArr.size());

					if (footerArr.size() > 0) {

						// cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
						// "--------------*********---------------");
						// cpclConfig += text(230, y += LINE_SPACING, "");
						// cpclConfig += text(230, y += LINE_SPACING, "*********");
						cpclConfig += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

						for (ProductDetails footer : footerArr) {

							Log.d("footer value", "val " + footer.getReceiptMessage());

							if (footer.getReceiptMessage() != null
									&& !footer.getReceiptMessage().isEmpty()) {
								cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
										footer.getReceiptMessage());
							}
						}
					}

					cpclConfig = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
							+ LINE_SEPARATOR + HEADER + cpclConfig + CMD_PRINT;

					os.write(cpclConfig.getBytes());
					os.flush();
					os.close();

				}

			}else {

				int y = 0;

				StringBuilder temp = new StringBuilder();

				String cpclConfig = temp.toString();


				cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
						"-----------------   ------------------");

				cpclConfig += text(75, y += LINE_SPACING, "Received By");

				cpclConfig += text(350, y, "Authorized By");

				Log.d("footerArr bottom", "" + footerArr.size());

				if (footerArr.size() > 0) {

					// cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
					// "--------------*********---------------");
					// cpclConfig += text(230, y += LINE_SPACING, "");
					// cpclConfig += text(230, y += LINE_SPACING, "*********");
					cpclConfig += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

					for (ProductDetails footer : footerArr) {

						Log.d("footer value", "val " + footer.getReceiptMessage());

						if (footer.getReceiptMessage() != null
								&& !footer.getReceiptMessage().isEmpty()) {
							cpclConfig += text(LEFT_MARGIN, y += LINE_SPACING,
									footer.getReceiptMessage());
						}
					}
				}

				cpclConfig = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
						+ LINE_SEPARATOR + HEADER + cpclConfig + CMD_PRINT;

				os.write(cpclConfig.getBytes());
				os.flush();
				os.close();

			}
		}
	}


	public void printSalesReturn(String srno, String srdate,
			String customercode, String customername,
			List<ProductDetails> product, List<ProductDetails> productdet,List<String> printSortHeader, String appPrintGroup,
								 int nofcopies) throws IOException {
		helper.showProgressDialog(context.getString(R.string.print),
				context.getString(R.string.creating_file_for_printing));
		try {
			createSalesReturn(srno, srdate, customercode, customername,
					product, productdet,printSortHeader,appPrintGroup, nofcopies);
			// test();
			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();
			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}

	// private void createSalesReturn(String srno, String srdate,
	// String customercode, String customername,
	// List<ProductDetails> product, List<ProductDetails> productdet)
	// throws IOException {
	// // Used the calculate the y axis printing position dynamically
	//
	// int y = 0;
	//
	// StringBuilder temp = new StringBuilder();
	// y = printTitle(200, y, "SALES RETURN", temp);
	// y = printCompanyDetails(y, temp);
	// // y = printCustomerDetail(y, temp);
	//
	// String cpclConfigLabel = temp.toString();
	//
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SR No")
	// + text(150, y, " : ") + text(180, y, srno);
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SR Date")
	// + text(150, y, " : ") + text(180, y, srdate);
	//
	// cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING, "Cust Code"));
	// cpclConfigLabel += (text(150, y, " : "));
	// cpclConfigLabel += (text(180, y, customercode));
	// cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING, "Cust Name"));
	// cpclConfigLabel += (text(150, y, " : "));
	// cpclConfigLabel += (text(180, y, customername));
	//
	// cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
	// cpclConfigLabel += text(140, y, "Description");
	// cpclConfigLabel += text(310, y, "Qty");
	// cpclConfigLabel += text(395, y, "Price");
	// cpclConfigLabel += text(500, y, "Total");
	//
	// cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
	//
	// for (ProductDetails prods : product) {
	//
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
	// .getItemcode().toString());
	// //cpclConfigLabel += text(140, y, prods.getDescription().toString());
	//
	// cpclConfigLabel += text(140, y, (prods.getDescription().length() > 10) ?
	// prods
	// .getDescription().substring(0, 10) : prods
	// .getDescription());
	// cpclConfigLabel += text(310, y, prods.getQty().toString());
	// cpclConfigLabel += text(395, y, prods.getPrice().toString());
	// cpclConfigLabel += text(486, y, prods.getTotal().toString());
	// }
	//
	// cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
	// for (ProductDetails prd : productdet) {
	//
	// if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
	// "Item Disc")
	// + text(140, y, " : ")
	// + text(180, y, prd.getItemdisc().toString());
	// }
	//
	// if (Double.parseDouble(prd.getTax().toString()) > 0) {
	//
	// cpclConfigLabel += text(310, y, "Sub Total")
	// + text(450, y, " : ")
	// + text(486, y, prd.getSubtotal().toString());
	// }
	//
	// if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
	// "Bill Disc")
	// + text(140, y, " : ")
	// + text(180, y, prd.getBilldisc().toString());
	// }
	//
	// if (Double.parseDouble(prd.getTax().toString()) > 0) {
	//
	// if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
	// "")
	// + text(140, y, "")
	// + text(180, y, "");
	// }
	//
	// cpclConfigLabel += text(310, y, "Tax") + text(450, y, " : ")
	// + text(486, y, prd.getTax().toString());
	// }
	// if (Double.parseDouble(prd.getTax().toString()) == 0) {
	//
	// cpclConfigLabel += text(310, y, "")
	// + text(450, y, "")
	// + text(486, y, "");
	//
	// if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
	// cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
	// + text(450, y, " : ")
	// + text(486, y, prd.getNettot().toString());
	//
	// }else{
	// cpclConfigLabel += text(310, y, "Net Total")
	// + text(450, y, " : ")
	// + text(486, y, prd.getNettot().toString());
	// }
	// }else{
	// cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
	// + text(450, y, " : ")
	// + text(486, y, prd.getNettot().toString());
	// }
	//
	// }
	// cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
	//
	// // Just append everything and create a single string
	// cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
	// + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;
	//
	// FileOutputStream os = context.openFileOutput(FILE_NAME,
	// Context.MODE_PRIVATE);
	// os.write(cpclConfigLabel.getBytes());
	// os.flush();
	// os.close();
	//
	// }

	private void createSalesReturn(String srno, String srdate,
			String customercode, String customername,
			List<ProductDetails> product, List<ProductDetails> productdet,List<String> printSortHeader, String appPrintGroup,
								   int nofcopies) throws IOException {

		logoStr = "logoprint";

		Log.d("salesreturn logoStr", "salret" + logoStr);
		String showProductFullName = MobileSettingsSetterGetter.getShowProductFullName();
		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		String decimal =MobileSettingsSetterGetter.getDecimalPoints();
		for (int n = 0; n < nofcopies; n++) {
			int y = 0,s=0;

			StringBuilder temp = new StringBuilder();
			y = printTitle(200, y, "SALES RETURN", temp);
			y = printCompanyDetails(y, temp);

			String cpclConfigLabel = temp.toString();

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SR No")
					+ text(150, y, " : ") + text(180, y, srno);
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SR Date")
					+ text(150, y, " : ") + text(180, y, srdate);

			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"Cust Code"));
			cpclConfigLabel += (text(150, y, " : "));
			cpclConfigLabel += (text(180, y, customercode));
			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"Cust Name"));
			cpclConfigLabel += (text(150, y, " : "));
			cpclConfigLabel += (text(
					180,
					y,
					(customername.length() > 25) ? customername
							.substring(0, 24) : customername));

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
			cpclConfigLabel += text(70, y, "Description");
			cpclConfigLabel += text(280, y, "Qty");
			cpclConfigLabel += text(365, y, "Price");
			cpclConfigLabel += text(500, y, "Total");

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			int no = 1;
			if (appPrintGroup.matches("C") || appPrintGroup.matches("S")) {
				for (ProductDetails products : product) {

					if ((products.getSortproduct().matches(""))
							|| (products.getSortproduct().matches("0"))) {

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								String.valueOf(no));

						if (showProductFullName.matches("True")) {
							cpclConfigLabel += text(70, y, (products
									.getDescription().length() > 31) ? products
									.getDescription().substring(0, 30)
									: products.getDescription());
							cpclConfigLabel += text(280, y += LINE_SPACING,
									products.getQty().toString());
						} else {
							cpclConfigLabel += text(70, y, (products
									.getDescription().length() > 10) ? products
									.getDescription().substring(0, 9)
									: products.getDescription());
							cpclConfigLabel += text(280, y, products.getQty()
									.toString());
						}
						double value =Double.parseDouble(products.getPrice().toString());
						//  Log.d("ParseString",""+value);
						String values=String.format("%."+decimal+"f",value);
						cpclConfigLabel += text(365, y, values);
						cpclConfigLabel += text(500, y, products.getTotal()
								.toString());



						if (products.getFocqty() > 0) {
							cpclConfigLabel += text(70, y += LINE_SPACING,
									"Foc")
									+ text(210, y, " : ")
									+ text(280, y, (int) products.getFocqty());
						}



						s += no;
						no++;
					}
				}
				for (int i = 0; i < printSortHeader.size(); i++) {
					String catorsub = printSortHeader.get(i).toString();
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							catorsub + " :");
					for (ProductDetails prods : product) {
						if (catorsub.equalsIgnoreCase(prods.getSortproduct().toString())) {

							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, String.valueOf(s));


							if (showProductFullName.matches("True")) {
								cpclConfigLabel += text(
										70,
										y,
										(prods.getDescription().length() > 31) ? prods
												.getDescription().substring(0,
														30) : prods
												.getDescription());
								cpclConfigLabel += text(280, y += LINE_SPACING,
										prods.getQty().toString());
							} else {
								cpclConfigLabel += text(
										70,
										y,
										(prods.getDescription().length() > 10) ? prods
												.getDescription().substring(0,
														9) : prods
												.getDescription());
								cpclConfigLabel += text(280, y, prods.getQty()
										.toString());
							}
							double value =Double.parseDouble(prods.getPrice().toString());
							//  Log.d("ParseString",""+value);
							String values=String.format("%."+decimal+"f",value);
							cpclConfigLabel += text(365, y, values);
							cpclConfigLabel += text(486, y, prods.getTotal()
									.toString());


							if (prods.getFocqty() > 0) {
								cpclConfigLabel += text(70, y += LINE_SPACING,
										"Foc")
										+ text(210, y, " : ")
										+ text(280, y, (int) prods.getFocqty());
							}


							s++;

						}
					}
				}


			}
			else{
				for (ProductDetails products : product) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							products.getSno().toString());


					if (showProductFullName.matches("True")) {
						cpclConfigLabel += text(
								70,
								y,
								(products.getDescription().length() > 31) ? products
										.getDescription().substring(0, 30)
										: products.getDescription());

						cpclConfigLabel += text(280, y += LINE_SPACING,
								products.getQty().toString());
					} else {
						cpclConfigLabel += text(
								70,
								y,
								(products.getDescription().length() > 10) ? products
										.getDescription().substring(0, 9)
										: products.getDescription());
						cpclConfigLabel += text(280, y, products.getQty()
								.toString());
					}
					double value =Double.parseDouble(products.getPrice().toString());
					//  Log.d("ParseString",""+value);
					String values=String.format("%."+decimal+"f",value);
					cpclConfigLabel += text(365, y, values);
					cpclConfigLabel += text(500, y, products.getTotal()
							.toString());

					Log.d("products.getFocqty() 3",
							"" + (int) products.getFocqty());

					if (products.getFocqty() > 0) {
						cpclConfigLabel += text(70, y += LINE_SPACING, "Foc")
								+ text(210, y, " : ")
								+ text(280, y, "" + (int) products.getFocqty());
					}

					Log.d("getExchangeqty() 3",
							"" + (int) products.getExchangeqty());


				}

			}
		/*	for (ProductDetails prods : product) {

				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
						.getItemcode().toString());
				// cpclConfigLabel += text(140, y,
				// prods.getDescription().toString());

				cpclConfigLabel += text(140, y, (prods.getDescription()
						.length() > 10) ? prods.getDescription()
						.substring(0, 9) : prods.getDescription());
				cpclConfigLabel += text(310, y, prods.getQty().toString());
				cpclConfigLabel += text(395, y, prods.getPrice().toString());
				cpclConfigLabel += text(486, y, prods.getTotal().toString());
			}*/

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			for (ProductDetails prd : productdet) {

				if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Item Disc")
							+ text(140, y, " : ")
							+ text(180, y, prd.getItemdisc().toString());
				}

				if (Double.parseDouble(prd.getTax().toString()) > 0) {

					if (Double.parseDouble(prd.getItemdisc().toString()) == 0) {

						if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, "Bill Disc")
									+ text(140, y, " : ")
									+ text(180, y, prd.getBilldisc().toString());
						}
						if (Double.parseDouble(prd.getBilldisc().toString()) == 0
								&& Double.parseDouble(prd.getItemdisc()
										.toString()) == 0) {
							cpclConfigLabel += text(310, y += LINE_SPACING,
									"Sub Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getSubtotal().toString());
						} else {

							cpclConfigLabel += text(310, y, "Sub Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getSubtotal().toString());
						}
					} else {

						cpclConfigLabel += text(310, y, "Sub Total")
								+ text(450, y, " : ")
								+ text(486, y, prd.getSubtotal().toString());
					}
				}

				if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {

					if (Double.parseDouble(prd.getItemdisc().toString()) != 0) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Bill Disc")
								+ text(140, y, " : ")
								+ text(180, y, prd.getBilldisc().toString());
					} else {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"") + text(140, y, "") + text(180, y, "");
					}
				}

				if (Double.parseDouble(prd.getTax().toString()) > 0) {

					if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"") + text(140, y, "") + text(180, y, "");
					}

					cpclConfigLabel += text(310, y, "Tax")
							+ text(450, y, " : ")
							+ text(486, y, prd.getTax().toString());
				}
				if (Double.parseDouble(prd.getTax().toString()) == 0) {

					cpclConfigLabel += text(310, y, "") + text(450, y, "")
							+ text(486, y, "");

					if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
						cpclConfigLabel += text(310, y += LINE_SPACING,
								"Net Total")
								+ text(450, y, " : ")
								+ text(486, y, prd.getNettot().toString());

					} else {
						cpclConfigLabel += text(310, y, "Net Total")
								+ text(450, y, " : ")
								+ text(486, y, prd.getNettot().toString());
					}
				} else {
					cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
							+ text(450, y, " : ")
							+ text(486, y, prd.getNettot().toString());
				}

			}
			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			// Just append everything and create a single string
			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
					+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

			/*
			 * FileOutputStream os = context.openFileOutput(FILE_NAME,
			 * Context.MODE_PRIVATE);
			 */
			os.write(cpclConfigLabel.getBytes());
			os.flush();
		}
		os.close();

	}

	public void printSalesOrder(String sono, String sodate,
			String customercode, String customername,
			List<ProductDetails> product, List<ProductDetails> productdet,
			int nofcopies) {
		helper.showProgressDialog(context.getString(R.string.print),
				context.getString(R.string.creating_file_for_printing));
		try {
			// for(int i=0;i<nofcopies.size();i++){
			createSalesOrderFile(sono, sodate, customercode, customername,
					product, productdet, nofcopies);
			// }
			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();

			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (IOException e) {
			e.printStackTrace();
			helper.dismissProgressDialog();
			helper.showErrorDialog(R.string.error_creating_file_for_printing);
		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}

	public void printSettlement(String settlementNo, String sdate, String sTotal, String lctn,String user,ArrayList<CurrencyDeno> product, ArrayList<CurrencyDeno> productdet, int nofcopies) {
		helper.showProgressDialog(context.getString(R.string.print),
				context.getString(R.string.creating_file_for_printing));
		try {
			// for(int i=0;i<nofcopies.size();i++){
			createSettlementFile(settlementNo, sdate, sTotal, lctn, user,
					product, productdet, nofcopies);
			// }
			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();

			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (Exception e) {
			e.printStackTrace();
			helper.dismissProgressDialog();
			helper.showErrorDialog(R.string.error_creating_file_for_printing);
		}
	}

	private void createSettlementFile(String settlementNo, String sdate, String sTotal, String lctn,String user, ArrayList<CurrencyDeno> product, ArrayList<CurrencyDeno> productdet, int nofcopies) throws IOException {

		// Used the calculate the y axis printing position dynamically
		double totalQuantity = 0;
		logoStr = "logoprint";

		Log.d("salesorder logoStr", "salord" + logoStr);

		String showCustomerCode = MobileSettingsSetterGetter
				.getShowCustomerCode();
		String showCustomerName = MobileSettingsSetterGetter
				.getShowCustomerName();
		String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
		String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
		String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
		String showCustomerPhone = MobileSettingsSetterGetter
				.getShowCustomerPhone();
		String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
		String showCustomerEmail = MobileSettingsSetterGetter
				.getShowCustomerEmail();
		String showCustomerTerms = MobileSettingsSetterGetter
				.getShowCustomerTerms();
		String showTotalOutstanding = MobileSettingsSetterGetter
				.getShowTotalOutstanding();
		String showProductFullName = MobileSettingsSetterGetter
				.getShowProductFullName();

		String decimal = MobileSettingsSetterGetter.getDecimalPoints();

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);

		for (int n = 0; n < nofcopies; n++) {
			int y = 0;
			StringBuilder temp = new StringBuilder();

			y = printTitle(200, y, "SETTLEMENT", temp);
			y = printCompanyDetails(y, temp);
			String cpclConfigLabel = temp.toString();

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Settlement No")
					+ text(200, y, " : ") + text(230, y, settlementNo);
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Settlement Date")
					+ text(200, y, " : ") + text(230, y, sdate);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Location code")
					+ text(200, y, " : ") + text(230, y, lctn);
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Settlement By")
					+ text(200, y, " : ") + text(230, y, user);


			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);


				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
				cpclConfigLabel += text(70, y, "Denomination");
				cpclConfigLabel += text(300, y, "Count");
				cpclConfigLabel += text(480, y, "Total");

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			int i = 0;

			for (CurrencyDeno prods : product) {
					++i;
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							String.valueOf(i));

						cpclConfigLabel += text(90, y, (prods
								.getCurency().length() > 10) ? prods
								.getCurency().substring(0, 9)
								: prods.getCurency());
						cpclConfigLabel += text(310, y, prods.getDenomination()
								.toString());

					cpclConfigLabel += text(485, y, prods.getTotal().toString());
				}


			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);


				for (CurrencyDeno prd : productdet) {

						cpclConfigLabel += text(310, y += LINE_SPACING, "Total Amt")
								+ text(450, y, " : ")
								+ text(486, y, prd.getTotlAmt().toString());

			}



			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			// Just append everything and create a single string
			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 2)) + " 1"
					+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;
			os.write(cpclConfigLabel.getBytes());
			os.flush();
		}
		// FileOutputStream os = context.openFileOutput(FILE_NAME,
		// Context.MODE_PRIVATE);
		// os.write(cpclConfigLabel.getBytes());
		os.close();
	}

	// private void createSalesOrderFile(String sono, String sodate,
	// String customercode, String customername,
	// List<ProductDetails> product, List<ProductDetails> productdet)
	// throws IOException {
	// // Used the calculate the y axis printing position dynamically
	//
	// int y = 0;
	//
	// StringBuilder temp = new StringBuilder();
	// y = printTitle(200, y, "SALES ORDER", temp);
	// y = printCompanyDetails(y, temp);
	// // y = printCustomerDetail(y, temp);
	//
	// String cpclConfigLabel = temp.toString();
	//
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SO No")
	// + text(150, y, " : ") + text(180, y, sono);
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SO Date")
	// + text(150, y, " : ") + text(180, y, sodate);
	//
	// cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING, "Cust Code"));
	// cpclConfigLabel += (text(150, y, " : "));
	// cpclConfigLabel += (text(180, y, customercode));
	// cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING, "Cust Name"));
	// cpclConfigLabel += (text(150, y, " : "));
	// cpclConfigLabel += (text(180, y, customername));
	//
	// cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
	// cpclConfigLabel += text(140, y, "Description");
	// cpclConfigLabel += text(310, y, "Qty");
	// cpclConfigLabel += text(395, y, "Price");
	// cpclConfigLabel += text(500, y, "Total");
	//
	// cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
	//
	// for (ProductDetails prods : product) {
	//
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
	// .getItemcode().toString());
	// //cpclConfigLabel += text(140, y, prods.getDescription().toString());
	// cpclConfigLabel += text(140, y, (prods.getDescription().length() > 10) ?
	// prods
	// .getDescription().substring(0, 9) : prods
	// .getDescription());
	// cpclConfigLabel += text(310, y, prods.getQty().toString());
	// cpclConfigLabel += text(395, y, prods.getPrice().toString());
	// cpclConfigLabel += text(486, y, prods.getTotal().toString());
	// }
	//
	// cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
	// for (ProductDetails prd : productdet) {
	//
	// // if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
	// // cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
	// // "Item Disc")
	// // + text(140, y, " : ")
	// // + text(180, y, prd.getItemdisc().toString());
	// // }
	// //
	// // if (Double.parseDouble(prd.getTax().toString()) > 0) {
	// //
	// // cpclConfigLabel += text(310, y, "Sub Total")
	// // + text(450, y, " : ")
	// // + text(486, y, prd.getSubtotal().toString());
	// // }
	// //
	// // if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
	// // cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
	// // "Bill Disc")
	// // + text(140, y, " : ")
	// // + text(180, y, prd.getBilldisc().toString());
	// // }
	// //
	// // if (Double.parseDouble(prd.getTax().toString()) > 0) {
	// // cpclConfigLabel += text(310, y, "Tax") + text(450, y, " : ")
	// // + text(486, y, prd.getTax().toString());
	// // }
	// //
	// // cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
	// // + text(450, y, " : ")
	// // + text(486, y, prd.getNettot().toString());
	//
	//
	//
	// if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
	// "Item Disc")
	// + text(140, y, " : ")
	// + text(180, y, prd.getItemdisc().toString());
	// }
	//
	// if (Double.parseDouble(prd.getTax().toString()) > 0) {
	//
	// cpclConfigLabel += text(310, y, "Sub Total")
	// + text(450, y, " : ")
	// + text(486, y, prd.getSubtotal().toString());
	// }
	//
	// if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
	// "Bill Disc")
	// + text(140, y, " : ")
	// + text(180, y, prd.getBilldisc().toString());
	// }
	//
	// if (Double.parseDouble(prd.getTax().toString()) > 0) {
	//
	// if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
	// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
	// "")
	// + text(140, y, "")
	// + text(180, y, "");
	// }
	//
	// cpclConfigLabel += text(310, y, "Tax") + text(450, y, " : ")
	// + text(486, y, prd.getTax().toString());
	// }
	// if (Double.parseDouble(prd.getTax().toString()) == 0) {
	//
	// cpclConfigLabel += text(310, y, "")
	// + text(450, y, "")
	// + text(486, y, "");
	//
	// if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
	// cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
	// + text(450, y, " : ")
	// + text(486, y, prd.getNettot().toString());
	//
	// }else{
	// cpclConfigLabel += text(310, y, "Net Total")
	// + text(450, y, " : ")
	// + text(486, y, prd.getNettot().toString());
	// }
	// }else{
	// cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
	// + text(450, y, " : ")
	// + text(486, y, prd.getNettot().toString());
	// }
	//
	//
	//
	// }
	// cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
	//
	// // Just append everything and create a single string
	// cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
	// + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;
	//
	// FileOutputStream os = context.openFileOutput(FILE_NAME,
	// Context.MODE_PRIVATE);
	// os.write(cpclConfigLabel.getBytes());
	// os.flush();
	// os.close();
	// }

	/*private void createSalesOrderFile(String sono, String sodate,
			String customercode, String customername,
			List<ProductDetails> product, List<ProductDetails> productdet,
			int nofcopies) throws IOException {
		// Used the calculate the y axis printing position dynamically

		logoStr = "logoprint";

		Log.d("salesorder logoStr", "salord" + logoStr);
		
		String showCustomerCode = MobileSettingsSetterGetter
				.getShowCustomerCode();
		String showCustomerName = MobileSettingsSetterGetter
				.getShowCustomerName();
		String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
		String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
		String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
		String showCustomerPhone = MobileSettingsSetterGetter
				.getShowCustomerPhone();
		String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
		String showCustomerEmail = MobileSettingsSetterGetter
				.getShowCustomerEmail();
		String showCustomerTerms = MobileSettingsSetterGetter
				.getShowCustomerTerms();
		String showTotalOutstanding = MobileSettingsSetterGetter
				.getShowTotalOutstanding();
		String showProductFullName = MobileSettingsSetterGetter
				.getShowProductFullName();
		
		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);

		for (int n = 0; n < nofcopies; n++) {
			int y = 0;
			StringBuilder temp = new StringBuilder();

			y = printTitle(200, y, "SALES ORDER", temp);
			y = printCompanyDetails(y, temp);
			String cpclConfigLabel = temp.toString();

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SO No")
					+ text(150, y, " : ") + text(180, y, sono);
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SO Date")
					+ text(150, y, " : ") + text(180, y, sodate);
			
			//Based on mobile setting show customer code
			if (showCustomerCode.matches("True")) {
				if (!customercode.matches("")) {
			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"Cust Code"));
			cpclConfigLabel += (text(150, y, " : "));
			cpclConfigLabel += (text(180, y, customercode));
				}
			}
			//Based on mobile setting show customer name
			if (showCustomerName.matches("True")) {
				if (!customername.matches("")) {
			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"Cust Name"));
			cpclConfigLabel += (text(150, y, " : "));
			cpclConfigLabel += (text(
					180,
					y,
					(customername.length() > 25) ? customername
							.substring(0, 24) : customername));
			//Printing customer last name
			if (customername.length() > 25) {

				String custlastname = customername.substring(24);

				Log.d("Custnameeee", custlastname);

				cpclConfigLabel += (text(
						180,
						y += LINE_SPACING,
						(custlastname.length() > 25) ? custlastname
								.substring(0, 24) : custlastname));
			}
				}
			
			}
			if (showAddress1.matches("True")) {
				if (!CustomerSetterGetter.getCustomerAddress1().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Address 1")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerAddress1());
				}
			}

			if (showAddress2.matches("True")) {
				if (!CustomerSetterGetter.getCustomerAddress2().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Address 2")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerAddress2());
				}
			}

			if (showAddress3.matches("True")) {
				if (!CustomerSetterGetter.getCustomerAddress3().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Address 3")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerAddress3());
				}
			}

			if (showCustomerPhone.matches("True")) {
				if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Phone No")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerPhone());
				}
			}

			if (showCustomerHP.matches("True")) {
				if (!CustomerSetterGetter.getCustomerHP().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Head Phone")
							+ text(150, y, " : ")
							+ text(180, y, CustomerSetterGetter.getCustomerHP());
				}
			}

			if (showCustomerEmail.matches("True")) {
				if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Email")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerEmail());
				}
			}

			if (showCustomerTerms.matches("True")) {
				if (!CustomerSetterGetter.getCustomerTerms().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Terms")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerTerms());

				}
			}
			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
			cpclConfigLabel += text(70, y, "Description");
			cpclConfigLabel += text(280, y, "Qty");
			cpclConfigLabel += text(365, y, "Price");
			cpclConfigLabel += text(500, y, "Total");

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			int i = 0;
			for (ProductDetails prods : product) {

//				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
//						.getItemcode().toString());
				// cpclConfigLabel += text(140, y,
				// prods.getDescription().toString());
				++i;
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						String.valueOf(i));
				if (showProductFullName.matches("True")) {
					cpclConfigLabel += text(70, y, (prods
							.getDescription().length() > 31) ? prods
							.getDescription().substring(0, 30)
							: prods.getDescription());
					cpclConfigLabel += text(280, y += LINE_SPACING,
							prods.getQty().toString());
				}
				 else {
						cpclConfigLabel += text(70, y, (prods
								.getDescription().length() > 10) ? prods
								.getDescription().substring(0, 9)
								: prods.getDescription());
						cpclConfigLabel += text(280, y, prods.getQty()
								.toString());
					}
				cpclConfigLabel += text(140, y, (prods.getDescription()
						.length() > 10) ? prods.getDescription()
						.substring(0, 9) : prods.getDescription());
				//cpclConfigLabel += text(310, y, prods.getQty().toString());
				cpclConfigLabel += text(365, y, prods.getPrice().toString());
				cpclConfigLabel += text(486, y, prods.getTotal().toString());
			}

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			for (ProductDetails prd : productdet) {

				if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Item Disc")
							+ text(140, y, " : ")
							+ text(180, y, prd.getItemdisc().toString());
				}

				if (Double.parseDouble(prd.getTax().toString()) > 0) {

					// cpclConfigLabel += text(310, y, "Sub Total")
					// + text(450, y, " : ")
					// + text(486, y, prd.getSubtotal().toString());
					if (Double.parseDouble(prd.getItemdisc().toString()) == 0) {

						if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
							cpclConfigLabel += text(LEFT_MARGIN,
									y += LINE_SPACING, "Bill Disc")
									+ text(140, y, " : ")
									+ text(180, y, prd.getBilldisc().toString());
						}
						if (Double.parseDouble(prd.getBilldisc().toString()) == 0
								&& Double.parseDouble(prd.getItemdisc()
										.toString()) == 0) {
							cpclConfigLabel += text(310, y += LINE_SPACING,
									"Sub Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getSubtotal().toString());
						} else {

							cpclConfigLabel += text(310, y, "Sub Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getSubtotal().toString());
						}
					} else {

						cpclConfigLabel += text(310, y, "Sub Total")
								+ text(450, y, " : ")
								+ text(486, y, prd.getSubtotal().toString());
					}
				}

				if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
					// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
					// "Bill Disc")
					// + text(140, y, " : ")
					// + text(180, y, prd.getBilldisc().toString());
					if (Double.parseDouble(prd.getItemdisc().toString()) != 0) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Bill Disc")
								+ text(140, y, " : ")
								+ text(180, y, prd.getBilldisc().toString());
					} else {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"") + text(140, y, "") + text(180, y, "");
					}
				}

				if (Double.parseDouble(prd.getTax().toString()) > 0) {

					if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"") + text(140, y, "") + text(180, y, "");
					}

					cpclConfigLabel += text(310, y, "Tax")
							+ text(450, y, " : ")
							+ text(486, y, prd.getTax().toString());
				}
				if (Double.parseDouble(prd.getTax().toString()) == 0) {

					cpclConfigLabel += text(310, y, "") + text(450, y, "")
							+ text(486, y, "");

					if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
						cpclConfigLabel += text(310, y += LINE_SPACING,
								"Net Total")
								+ text(450, y, " : ")
								+ text(486, y, prd.getNettot().toString());

					} else {
						cpclConfigLabel += text(310, y, "Net Total")
								+ text(450, y, " : ")
								+ text(486, y, prd.getNettot().toString());
					}
				} else {
					cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
							+ text(450, y, " : ")
							+ text(486, y, prd.getNettot().toString());
				}

			}
			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			// Just append everything and create a single string
			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 2)) + " 1"
					+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;
			os.write(cpclConfigLabel.getBytes());
			os.flush();
		}
		// FileOutputStream os = context.openFileOutput(FILE_NAME,
		// Context.MODE_PRIVATE);
		// os.write(cpclConfigLabel.getBytes());
		os.close();
	}
*/
	private void createSalesOrderFile(String sono, String sodate,
			String customercode, String customername,
			List<ProductDetails> product, List<ProductDetails> productdet,
			int nofcopies) throws IOException {
		// Used the calculate the y axis printing position dynamically
		double totalQuantity = 0;
		logoStr = "logoprint";

		Log.d("salesorder logoStr", "salord" + logoStr);

		String showCustomerCode = MobileSettingsSetterGetter
				.getShowCustomerCode();
		String showCustomerName = MobileSettingsSetterGetter
				.getShowCustomerName();
		String showAddress1 = MobileSettingsSetterGetter.getShowAddress1();
		String showAddress2 = MobileSettingsSetterGetter.getShowAddress2();
		String showAddress3 = MobileSettingsSetterGetter.getShowAddress3();
		String showCustomerPhone = MobileSettingsSetterGetter
				.getShowCustomerPhone();
		String showCustomerHP = MobileSettingsSetterGetter.getShowCustomerHP();
		String showCustomerEmail = MobileSettingsSetterGetter
				.getShowCustomerEmail();
		String showCustomerTerms = MobileSettingsSetterGetter
				.getShowCustomerTerms();
		String showTotalOutstanding = MobileSettingsSetterGetter
				.getShowTotalOutstanding();
		String showProductFullName = MobileSettingsSetterGetter
				.getShowProductFullName();

		String decimal = MobileSettingsSetterGetter.getDecimalPoints();

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);

		for (int n = 0; n < nofcopies; n++) {
			int y = 0;
			StringBuilder temp = new StringBuilder();

			y = printTitle(200, y, "SALES ORDER", temp);
			y = printCompanyDetails(y, temp);
			String cpclConfigLabel = temp.toString();

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SO No")
					+ text(150, y, " : ") + text(180, y, sono);
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SO Date")
					+ text(150, y, " : ") + text(180, y, sodate);

			//Based on mobile setting show customer code
			if (showCustomerCode.matches("True")) {
				if (!customercode.matches("")) {
			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"Cust Code"));
			cpclConfigLabel += (text(150, y, " : "));
			cpclConfigLabel += (text(180, y, customercode));
				}
			}
			//Based on mobile setting show customer name
			if (showCustomerName.matches("True")) {
				if (!customername.matches("")) {
			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"Cust Name"));
			cpclConfigLabel += (text(150, y, " : "));
			cpclConfigLabel += (text(
					180,
					y,
					(customername.length() > 25) ? customername
							.substring(0, 24) : customername));
			//Printing customer last name
			if (customername.length() > 25) {

				String custlastname = customername.substring(24);

				Log.d("Custnameeee", custlastname);

				cpclConfigLabel += (text(
						180,
						y += LINE_SPACING,
						(custlastname.length() > 25) ? custlastname
								.substring(0, 24) : custlastname));
			}
				}

			}
			String address1 = CustomerSetterGetter.getCustomerAddress1();
			String address2 = CustomerSetterGetter.getCustomerAddress2();
			String address3 = CustomerSetterGetter.getCustomerAddress3();
			if (showAddress1.matches("True")) {
				if (!address1.matches("")) {
					cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
							"Address 1"));
					cpclConfigLabel += (text(150, y, " : "));

					cpclConfigLabel += (text(
							180,
							y,
							(address1.length() > 23) ? address1
									.substring(0, 22) : address1));

					if (address1.length() > 23) {

						String addr1 = address1.substring(22);

						Log.d("addr1", addr1);

						cpclConfigLabel += (text(
								180,
								y += LINE_SPACING,
								(addr1.length() > 23) ? addr1
										.substring(0, 22) : addr1));
					}
				}
			}

			if (showAddress2.matches("True")) {
				if (!address2.matches("")) {
					cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
							"Address 2"));
					cpclConfigLabel += (text(150, y, " : "));

					cpclConfigLabel += (text(
							180,
							y,
							(address2.length() > 23) ? address2
									.substring(0, 22) : address2));

					if (address2.length() > 23) {

						String addr2 = address2.substring(22);

						Log.d("addr2", addr2);

						cpclConfigLabel += (text(
								180,
								y += LINE_SPACING,
								(addr2.length() > 23) ? addr2
										.substring(0, 22) : addr2));
					}
				}
			}

			if (showAddress3.matches("True")) {
				if (!address3.matches("")) {
					cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
							"Address 3"));
					cpclConfigLabel += (text(150, y, " : "));

					cpclConfigLabel += (text(
							180,
							y,
							(address3.length() > 23) ? address3
									.substring(0, 22) : address3));

					if (address3.length() > 23) {

						String addr3 = address3.substring(22);

						Log.d("addr3", addr3);

						cpclConfigLabel += (text(
								180,
								y += LINE_SPACING,
								(addr3.length() > 23) ? addr3
										.substring(0, 22) : addr3));
					}
				}
			}

			if (showCustomerPhone.matches("True")) {
				if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Phone No")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerPhone());
				}
			}

			if (showCustomerHP.matches("True")) {
				if (!CustomerSetterGetter.getCustomerHP().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Head Phone")
							+ text(150, y, " : ")
							+ text(180, y, CustomerSetterGetter.getCustomerHP());
				}
			}

			if (showCustomerEmail.matches("True")) {
				if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Email")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerEmail());
				}
			}

			if (showCustomerTerms.matches("True")) {
				if (!CustomerSetterGetter.getCustomerTerms().matches("")) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							"Terms")
							+ text(150, y, " : ")
							+ text(180, y,
									CustomerSetterGetter.getCustomerTerms());

				}
			}
			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			 if(FormSetterGetter.getHidePrice()!=null && !FormSetterGetter.getHidePrice().isEmpty()){
					if(FormSetterGetter.getHidePrice().matches("Hide Price")){
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
						cpclConfigLabel += text(70, y, "Description");
						cpclConfigLabel += text(486, y, "Qty");
					}else{
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
						cpclConfigLabel += text(70, y, "Description");
						cpclConfigLabel += text(280, y, "Qty");
						cpclConfigLabel += text(365, y, "Price");
						cpclConfigLabel += text(500, y, "Total");
           					}
			 }else{
				 cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
					cpclConfigLabel += text(70, y, "Description");
					cpclConfigLabel += text(280, y, "Qty");
					cpclConfigLabel += text(365, y, "Price");
					cpclConfigLabel += text(500, y, "Total");
			 }
			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			int i = 0;

			for (ProductDetails prods : product) {

				 if(FormSetterGetter.getHidePrice()!=null && !FormSetterGetter.getHidePrice().isEmpty()){
						if(FormSetterGetter.getHidePrice().matches("Hide Price")){
							++i;
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									String.valueOf(i));
							double quantity = prods.getQty().toString().equals("") ? 0 : Double.valueOf(prods.getQty().toString());
							if (showProductFullName.matches("True")) {
								cpclConfigLabel += text(70, y, (prods
										.getDescription().length() > 31) ? prods
										.getDescription().substring(0, 30)
										: prods.getDescription());

								cpclConfigLabel += text(486, y += LINE_SPACING,
										decimalPoint(quantity));
							}
							 else {
									cpclConfigLabel += text(70, y, (prods
											.getDescription().length() > 10) ? prods
											.getDescription().substring(0, 9)
											: prods.getDescription());
									cpclConfigLabel += text(486, y, decimalPoint(quantity));
								}

							totalQuantity += prods.getQty().toString().equals("") ? 0 : Double.valueOf(prods.getQty().toString());

						}else{
							++i;
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									String.valueOf(i));
							if (showProductFullName.matches("True")) {
								cpclConfigLabel += text(70, y, (prods
										.getDescription().length() > 31) ? prods
										.getDescription().substring(0, 30)
										: prods.getDescription());
								cpclConfigLabel += text(280, y += LINE_SPACING,
										prods.getQty().toString());
							}
							 else {
									cpclConfigLabel += text(70, y, (prods
											.getDescription().length() > 10) ? prods
											.getDescription().substring(0, 9)
											: prods.getDescription());
									cpclConfigLabel += text(280, y, prods.getQty()
											.toString());
								}
							double value =Double.parseDouble(prods.getPrice().toString());
							//  Log.d("ParseString",""+value);
							String values=String.format("%."+decimal+"f",value);
							cpclConfigLabel += text(365, y, values);
							cpclConfigLabel += text(486, y, prods.getTotal().toString());
						}
				 }else{
						++i;
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								String.valueOf(i));
						if (showProductFullName.matches("True")) {
							cpclConfigLabel += text(70, y, (prods
									.getDescription().length() > 31) ? prods
									.getDescription().substring(0, 30)
									: prods.getDescription());
							cpclConfigLabel += text(280, y += LINE_SPACING,
									prods.getQty().toString());
						}
						 else {
								cpclConfigLabel += text(70, y, (prods
										.getDescription().length() > 10) ? prods
										.getDescription().substring(0, 9)
										: prods.getDescription());
								cpclConfigLabel += text(280, y, prods.getQty()
										.toString());
							}
					 double value = Double.parseDouble(prods
							 .getPrice().toString());
					 String values = String.format("%."+decimal+"f",value);
						cpclConfigLabel += text(365, y, values);
						cpclConfigLabel += text(486, y, prods.getTotal().toString());
				 }
			}

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);



			 if(FormSetterGetter.getHidePrice()!=null && !FormSetterGetter.getHidePrice().isEmpty()){
					if(FormSetterGetter.getHidePrice().matches("Hide Price")){
						cpclConfigLabel += text(310, y += LINE_SPACING, "Total Qty")
								+ text(450, y, " : ")
								+ text(486, y, decimalPoint(totalQuantity));


					}else{
						for (ProductDetails prd : productdet) {

							if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
								cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
										"Item Disc")
										+ text(140, y, " : ")
										+ text(180, y, prd.getItemdisc().toString());
							}

							if (Double.parseDouble(prd.getTax().toString()) > 0) {

								// cpclConfigLabel += text(310, y, "Sub Total")
								// + text(450, y, " : ")
								// + text(486, y, prd.getSubtotal().toString());
								if (Double.parseDouble(prd.getItemdisc().toString()) == 0) {

									if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
										cpclConfigLabel += text(LEFT_MARGIN,
												y += LINE_SPACING, "Bill Disc")
												+ text(140, y, " : ")
												+ text(180, y, prd.getBilldisc().toString());
									}
									if (Double.parseDouble(prd.getBilldisc().toString()) == 0
											&& Double.parseDouble(prd.getItemdisc()
													.toString()) == 0) {
										cpclConfigLabel += text(310, y += LINE_SPACING,
												"Sub Total")
												+ text(450, y, " : ")
												+ text(486, y, prd.getSubtotal().toString());
									} else {

										cpclConfigLabel += text(310, y, "Sub Total")
												+ text(450, y, " : ")
												+ text(486, y, prd.getSubtotal().toString());
									}
								} else {

									cpclConfigLabel += text(310, y, "Sub Total")
											+ text(450, y, " : ")
											+ text(486, y, prd.getSubtotal().toString());
								}
							}

							if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
								// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								// "Bill Disc")
								// + text(140, y, " : ")
								// + text(180, y, prd.getBilldisc().toString());
								if (Double.parseDouble(prd.getItemdisc().toString()) != 0) {
									cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
											"Bill Disc")
											+ text(140, y, " : ")
											+ text(180, y, prd.getBilldisc().toString());
								} else {
									cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
											"") + text(140, y, "") + text(180, y, "");
								}
							}

							if (Double.parseDouble(prd.getTax().toString()) > 0) {

								if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
									cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
											"") + text(140, y, "") + text(180, y, "");
								}

								cpclConfigLabel += text(310, y, "Tax")
										+ text(450, y, " : ")
										+ text(486, y, prd.getTax().toString());
							}
							if (Double.parseDouble(prd.getTax().toString()) == 0) {

								cpclConfigLabel += text(310, y, "") + text(450, y, "")
										+ text(486, y, "");

								if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
									cpclConfigLabel += text(310, y += LINE_SPACING,
											"Net Total")
											+ text(450, y, " : ")
											+ text(486, y, prd.getNettot().toString());

								} else {
									cpclConfigLabel += text(310, y, "Net Total")
											+ text(450, y, " : ")
											+ text(486, y, prd.getNettot().toString());
								}
							} else {
								cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
										+ text(450, y, " : ")
										+ text(486, y, prd.getNettot().toString());
							}

						}
					}
			 }
			 else{
					for (ProductDetails prd : productdet) {

						if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Item Disc")
									+ text(140, y, " : ")
									+ text(180, y, prd.getItemdisc().toString());
						}

						if (Double.parseDouble(prd.getTax().toString()) > 0) {

							// cpclConfigLabel += text(310, y, "Sub Total")
							// + text(450, y, " : ")
							// + text(486, y, prd.getSubtotal().toString());
							if (Double.parseDouble(prd.getItemdisc().toString()) == 0) {

								if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
									cpclConfigLabel += text(LEFT_MARGIN,
											y += LINE_SPACING, "Bill Disc")
											+ text(140, y, " : ")
											+ text(180, y, prd.getBilldisc().toString());
								}
								if (Double.parseDouble(prd.getBilldisc().toString()) == 0
										&& Double.parseDouble(prd.getItemdisc()
												.toString()) == 0) {
									cpclConfigLabel += text(310, y += LINE_SPACING,
											"Sub Total")
											+ text(450, y, " : ")
											+ text(486, y, prd.getSubtotal().toString());
								} else {

									cpclConfigLabel += text(310, y, "Sub Total")
											+ text(450, y, " : ")
											+ text(486, y, prd.getSubtotal().toString());
								}
							} else {

								cpclConfigLabel += text(310, y, "Sub Total")
										+ text(450, y, " : ")
										+ text(486, y, prd.getSubtotal().toString());
							}
						}

						if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
							// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							// "Bill Disc")
							// + text(140, y, " : ")
							// + text(180, y, prd.getBilldisc().toString());
							if (Double.parseDouble(prd.getItemdisc().toString()) != 0) {
								cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
										"Bill Disc")
										+ text(140, y, " : ")
										+ text(180, y, prd.getBilldisc().toString());
							} else {
								cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
										"") + text(140, y, "") + text(180, y, "");
							}
						}

						if (Double.parseDouble(prd.getTax().toString()) > 0) {

							if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
								cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
										"") + text(140, y, "") + text(180, y, "");
							}

							cpclConfigLabel += text(310, y, "Tax")
									+ text(450, y, " : ")
									+ text(486, y, prd.getTax().toString());
						}
						if (Double.parseDouble(prd.getTax().toString()) == 0) {

							cpclConfigLabel += text(310, y, "") + text(450, y, "")
									+ text(486, y, "");

							if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
								cpclConfigLabel += text(310, y += LINE_SPACING,
										"Net Total")
										+ text(450, y, " : ")
										+ text(486, y, prd.getNettot().toString());

							} else {
								cpclConfigLabel += text(310, y, "Net Total")
										+ text(450, y, " : ")
										+ text(486, y, prd.getNettot().toString());
							}
						} else {
							cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getNettot().toString());
						}

					}
			 }



			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			// Just append everything and create a single string
			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 2)) + " 1"
					+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;
			os.write(cpclConfigLabel.getBytes());
			os.flush();
		}
		// FileOutputStream os = context.openFileOutput(FILE_NAME,
		// Context.MODE_PRIVATE);
		// os.write(cpclConfigLabel.getBytes());
		os.close();
	}

	public void printDeliveryOrder(String dono, String dodate,
			String customercode, String customername,
			List<ProductDetails> product, List<ProductDetails> productdet,
			int nofcopies) {
		helper.showProgressDialog(context.getString(R.string.print),
				context.getString(R.string.creating_file_for_printing));
		try {
			createDeliveryOrderFile(dono, dodate, customercode, customername,
					product, productdet, nofcopies);
			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();
			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (IOException e) {
			e.printStackTrace();
			helper.dismissProgressDialog();
			helper.showErrorDialog(R.string.error_creating_file_for_printing);
		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}

	private void createDeliveryOrderFile(String dono, String dodate,
			String customercode, String customername,
			List<ProductDetails> product, List<ProductDetails> productdet,
			int nofcopies) throws IOException {
		// Used the calculate the y axis printing position dynamically
		logoStr = "logoprint";
		int totalQuantity = 0;
		Log.d("delivery logoStr", "deliord" + logoStr);

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		String decimal = MobileSettingsSetterGetter.getDecimalPoints();
		for (int n = 0; n < nofcopies; n++) {
			int y = 0;
			StringBuilder temp = new StringBuilder();
			y = printTitle(180, y, "DELIVERY ORDER", temp);
			y = printCompanyDetails(y, temp);

			String cpclConfigLabel = temp.toString();

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "DO No")
					+ text(150, y, " : ") + text(180, y, dono);
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "DO Date")
					+ text(150, y, " : ") + text(180, y, dodate);

			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"Cust Code"));
			cpclConfigLabel += (text(150, y, " : "));
			cpclConfigLabel += (text(180, y, customercode));
			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"Cust Name"));
			cpclConfigLabel += (text(150, y, " : "));

			cpclConfigLabel += (text(
					180,
					y,
					(customername.length() > 25) ? customername
							.substring(0, 24) : customername));

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
				cpclConfigLabel += text(140, y, "Description");
				cpclConfigLabel += text(310, y, "Qty");
				cpclConfigLabel += text(395, y, "Price");
				cpclConfigLabel += text(500, y, "Total");
			}else{
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
				cpclConfigLabel += text(150, y, "Description");
				cpclConfigLabel += text(486, y, "Qty");
			}

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			for (ProductDetails prods : product) {
				if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
							.getItemcode().toString());
					// cpclConfigLabel += text(140, y,
					// prods.getDescription().toString());
					cpclConfigLabel += text(140, y, (prods.getDescription()
							.length() > 10) ? prods.getDescription()
							.substring(0, 9) : prods.getDescription());
					cpclConfigLabel += text(310, y, prods.getQty().toString());
					double value = Double.parseDouble(prods
							.getPrice().toString());
					String values = String.format("%."+decimal+"f",value);
					cpclConfigLabel += text(395, y, values);
					cpclConfigLabel += text(486, y, prods.getTotal().toString());
				}else{
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
							.getItemcode().toString());
					// cpclConfigLabel += text(140, y,
					// prods.getDescription().toString());
					cpclConfigLabel += text(150, y, (prods.getDescription()
							.length() > 10) ? prods.getDescription()
							.substring(0, 9) : prods.getDescription());
					cpclConfigLabel += text(486, y, prods.getQty().toString());

					totalQuantity += prods.getQty().toString().equals("") ? 0 : Integer
							.valueOf(prods.getQty().toString());
				}


			}

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){

				for (ProductDetails prd : productdet) {

					if (Double.parseDouble(prd.getItemdisc().toString()) > 0) {
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Item Disc")
								+ text(140, y, " : ")
								+ text(180, y, prd.getItemdisc().toString());
					}

					if (Double.parseDouble(prd.getTax().toString()) > 0) {

						// cpclConfigLabel += text(310, y, "Sub Total")
						// + text(450, y, " : ")
						// + text(486, y, prd.getSubtotal().toString());
						if (Double.parseDouble(prd.getItemdisc().toString()) == 0) {

							if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
								cpclConfigLabel += text(LEFT_MARGIN,
										y += LINE_SPACING, "Bill Disc")
										+ text(140, y, " : ")
										+ text(180, y, prd.getBilldisc().toString());
							}
							if (Double.parseDouble(prd.getBilldisc().toString()) == 0
									&& Double.parseDouble(prd.getItemdisc()
											.toString()) == 0) {
								cpclConfigLabel += text(310, y += LINE_SPACING,
										"Sub Total")
										+ text(450, y, " : ")
										+ text(486, y, prd.getSubtotal().toString());
							} else {

								cpclConfigLabel += text(310, y, "Sub Total")
										+ text(450, y, " : ")
										+ text(486, y, prd.getSubtotal().toString());
							}
						} else {

							cpclConfigLabel += text(310, y, "Sub Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getSubtotal().toString());
						}
					}

					if (Double.parseDouble(prd.getBilldisc().toString()) > 0) {
						// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						// "Bill Disc")
						// + text(140, y, " : ")
						// + text(180, y, prd.getBilldisc().toString());
						if (Double.parseDouble(prd.getItemdisc().toString()) != 0) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Bill Disc")
									+ text(140, y, " : ")
									+ text(180, y, prd.getBilldisc().toString());
						} else {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"") + text(140, y, "") + text(180, y, "");
						}
					}

					if (Double.parseDouble(prd.getTax().toString()) > 0) {

						if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"") + text(140, y, "") + text(180, y, "");
						}

						cpclConfigLabel += text(310, y, "Tax")
								+ text(450, y, " : ")
								+ text(486, y, prd.getTax().toString());
					}
					if (Double.parseDouble(prd.getTax().toString()) == 0) {

						cpclConfigLabel += text(310, y, "") + text(450, y, "")
								+ text(486, y, "");

						if (Double.parseDouble(prd.getBilldisc().toString()) == 0) {
							cpclConfigLabel += text(310, y += LINE_SPACING,
									"Net Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getNettot().toString());

						} else {
							cpclConfigLabel += text(310, y, "Net Total")
									+ text(450, y, " : ")
									+ text(486, y, prd.getNettot().toString());
						}
					} else {
						cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
								+ text(450, y, " : ")
								+ text(486, y, prd.getNettot().toString());
					}
				}
				}else{

					 cpclConfigLabel += text(310, y += LINE_SPACING, "Total Qty")
								+ text(450, y, " : ")
								+ text(486, y, totalQuantity);

				}
			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			// Just append everything and create a single string
			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
					+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

			/*
			 * FileOutputStream os = context.openFileOutput(FILE_NAME,
			 * Context.MODE_PRIVATE);
			 */
			Log.d("cpclConfigLabel",cpclConfigLabel);
			os.write(cpclConfigLabel.getBytes());
			os.flush();
		}
		os.close();
	}

	public void printStockRequest(String sono, String sodate,
								  String fromlocation, String tolocation,
								  List<ProductDetails> product, String title, int nofcopies, String so_remarks) {
		helper.showProgressDialog(context.getString(R.string.print),
				context.getString(R.string.creating_file_for_printing));
		try {
			createStockRequestFile(sono, sodate, fromlocation, tolocation,
					product, title, nofcopies,so_remarks);
			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();
			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (IOException e) {
			e.printStackTrace();
			helper.dismissProgressDialog();
			helper.showErrorDialog(R.string.error_creating_file_for_printing);
		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}

	private void createStockRequestFile(String sono, String sodate,
										String fromlocation, String tolocation,
										List<ProductDetails> product, String title, int nofcopies, String so_remarks)
			throws IOException {
		// Used the calculate the y axis printing position dynamically

		logoStr = "logoprint";

		Log.d("stockreq logoStr", "stocreq" + logoStr);

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		for (int n = 0; n < nofcopies; n++) {
			int y = 0;

			StringBuilder temp = new StringBuilder();
			y = printTitle(200, y, title, temp);
			y = printCompanyDetails(y, temp);
			// y = printCustomerDetail(y, temp);

			String cpclConfigLabel = temp.toString();

			// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SR No")
			// + text(170, y, " : ") + text(190, y, sono);
			// cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
			// "SR Date")
			// + text(170, y, " : ") + text(190, y, sodate);

			if (title.matches("STOCK REQUEST")) {
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SR No")
						+ text(170, y, " : ") + text(190, y, sono);
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						"SR Date") + text(170, y, " : ") + text(190, y, sodate);
			} else if (title.matches("TRANSFER")) {
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						"Transfer No")
						+ text(170, y, " : ")
						+ text(190, y, sono);
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						"Transfer Date")
						+ text(170, y, " : ")
						+ text(190, y, sodate);
			}

			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"FromLocation"));
			cpclConfigLabel += (text(170, y, " : "));
			cpclConfigLabel += (text(190, y, fromlocation));
			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"ToLocation"));
			cpclConfigLabel += (text(170, y, " : "));
			cpclConfigLabel += (text(190, y, tolocation));
			cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING,
					"Remarks"));
			cpclConfigLabel += (text(170, y, " : "));
			cpclConfigLabel += (text(190, y, (so_remarks.length() > 29) ?so_remarks.substring(0, 28) :
					so_remarks));
			if (so_remarks.length() > 29) {

				String custlastname = so_remarks.substring(28);

				Log.d("getCustomernames",""+so_remarks);

				cpclConfigLabel += (text(
						190,
						y += LINE_SPACING,
						(custlastname.length() > 29) ? custlastname
								.substring(0,28) : custlastname));
			}

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
//			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
//			cpclConfigLabel += text(210, y, "Description");
//			cpclConfigLabel += text(500, y, "Qty");
//			// cpclConfigLabel += text(395, y, "TransferQty");
//			// cpclConfigLabel += text(500, y, "Total");
//
//			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
//			double totalqty = 0d;
//
//			for (ProductDetails prods : product) {
//
//				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
//						.getItemcode().toString());
//				cpclConfigLabel += text(210, y, (prods.getDescription()
//						.length() > 10) ? prods.getDescription()
//						.substring(0, 9) : prods.getDescription());
//				cpclConfigLabel += text(500, y, prods.getQty());
//				// cpclConfigLabel += text(395, y, prods.getPrice().toString());
//				// cpclConfigLabel += text(486, y, prods.getTotal().toString());
//				totalqty += prods.getTotalqty();
//			}

			double totalqty = 0.00;
			   if (title.matches("STOCK REQUEST")) {

			    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
			    cpclConfigLabel += text(210, y, "Description");

			    cpclConfigLabel += text(500, y, "Qty");
			    // cpclConfigLabel += text(395, y, "TransferQty");
			    // cpclConfigLabel += text(500, y, "Total");

			    cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			    totalqty = 0.00;
			    for (ProductDetails prods : product) {

			     cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
			       .getItemcode().toString());
			     cpclConfigLabel += text(210, y, (prods.getDescription()
			       .length() > 10) ? prods.getDescription()
			       .substring(0, 9) : prods.getDescription());
			     cpclConfigLabel += text(500, y, prods.getQty());
			     // cpclConfigLabel += text(395, y, prods.getPrice().toString());
			     // cpclConfigLabel += text(486, y, prods.getTotal().toString());
			     totalqty += prods.getTotalqty();

			    }

			   } else if (title.matches("TRANSFER")) {



//			    cpclConfigLabel += text(70, y, "Description");

			    String showcartonorloose = SalesOrderSetGet.getCartonpriceflag();

			    if(showcartonorloose.matches("1")){
			    	 cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
			    	cpclConfigLabel += text(70, y, "Description");
			    	  cpclConfigLabel += text(280, y, "CQty");
					    cpclConfigLabel += text(375, y, "LQty");
			    }else{
			    	cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
			    	cpclConfigLabel += text(210, y, "Description");
			    }

			    cpclConfigLabel += text(500, y, "Qty");

			    cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			    totalqty = 0.00;
			    int i = 1;
			    for (ProductDetails prods : product) {

			     String showProductFullName = MobileSettingsSetterGetter.getShowProductFullName();

			     if(showcartonorloose.matches("1")){

			    	 cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(i));

			    	  if (showProductFullName.matches("True")) {
					      cpclConfigLabel += text(70, y, (prods
					        .getDescription().length() > 31) ? prods
					        .getDescription().substring(0, 30)
					        : prods.getDescription());
					      cpclConfigLabel += text(280, y += LINE_SPACING,
					        prods.getCqty().toString());
					     } else {
					      cpclConfigLabel += text(70, y, (prods
					        .getDescription().length() > 10) ? prods
					        .getDescription().substring(0, 9)
					        : prods.getDescription());
					      cpclConfigLabel += text(280, y, prods.getCqty()
					        .toString());
					     }

					     cpclConfigLabel += text(375, y, prods.getLqty());
					     cpclConfigLabel += text(500, y, prods.getQty());
			    }else{
				     cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, prods
						       .getItemcode().toString());
			    	cpclConfigLabel += text(210, y, (prods.getDescription()
						       .length() > 10) ? prods.getDescription()
						       .substring(0, 9) : prods.getDescription());
						     cpclConfigLabel += text(500, y, prods.getQty());
			    }

			     totalqty += prods.getTotalqty();

			    i++;
			   }

			   }

//			   cpclConfigLabel += text(300, y += LINE_SPACING, "Total Amount")
//						+ text(450, y, " : ")
//						+ text(486, y, String.valueOf(tot_amt));

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			cpclConfigLabel += text(300, y += LINE_SPACING,
					"Total Quantity")
					+ text(470, y, " : ")
					+ text(500, y, String.valueOf((int) totalqty));

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);



			// Just append everything and create a single string
			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
					+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

			/*
			 * FileOutputStream os = context.openFileOutput(FILE_NAME,
			 * Context.MODE_PRIVATE);
			 */
			os.write(cpclConfigLabel.getBytes());
			os.flush();
		}
		os.close();
	}


	public void printCurrentDateReceipt(String receiptdate,
			ArrayList<Receipt> receiptlist, List<ProductDetails> footerArr,ArrayList<Receipt> receiptinvoicelist,String custCode,String custName) {
		helper.showProgressDialog(context.getString(R.string.print),
				context.getString(R.string.creating_file_for_printing));
		try {
			createCurrentReceiptFile(receiptdate, receiptlist, footerArr, receiptinvoicelist,custCode,custName);
			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();
			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (IOException e) {
			e.printStackTrace();
			helper.dismissProgressDialog();
			helper.showErrorDialog(R.string.error_creating_file_for_printing);
		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}


	private void createCurrentReceiptFile(String receiptdate,
			ArrayList<Receipt> receiptlist, List<ProductDetails> footerValue,ArrayList<Receipt> receiptinvoicelist,String custCode,String custName)
			throws IOException {
		// TODO Auto-generated method stub
		String printinvoicedetail =  MobileSettingsSetterGetter.getPrintReceiptSummary_PrintInvoiceDetail();
		logoStr = "logoprint";
		footerArr.clear();
		footerArr = footerValue;

		Log.d("receipt logoStr", "rece" + logoStr);

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);

		int y = 0;
		String paymode_cash = "", paymode_cheque = "", paymode_other = "";
		double cashamount = 0.00, chequeamount = 0.00, total = 0.00, otheramount = 0.00;
		StringBuilder temp = new StringBuilder();
		y = printTitle(170, y, "RECEIPT SUMMARY", temp);
		y = printCompanyDetails(y, temp);

		String cpclConfigLabel = temp.toString();

		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt Date")
				+ text(170, y, " : ") + text(190, y, receiptdate);
		if(!custCode.matches("")){
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Customer Code")
					+ text(170, y, " : ") + text(190, y, custCode);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "CustomerName")
					+ text(170, y, " : ") + text(190, y, custName);
		}

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt No");
		cpclConfigLabel += text(150, y, "Customer Name");
		cpclConfigLabel += text(500, y, "Total");

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		for (Receipt receipt : receiptlist) {

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, receipt
					.getReceiptno().toString());
			// cpclConfigLabel += text(140, y,
			// prods.getDescription().toString());
			cpclConfigLabel += text(
					160,
					y,
					(receipt.getCustomername().length() > 21) ? receipt
							.getCustomername().substring(0, 20) : receipt
							.getCustomername());
			cpclConfigLabel += text(480, y, receipt.getPaidamount());

			// receipt with invoice details 06.11.2017
			if(printinvoicedetail.equalsIgnoreCase("True")){
				cpclConfigLabel += LINE_SEPARATOR;
				cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
				cpclConfigLabel += LINE_SEPARATOR;
				for (Receipt receiptinvdetail : receiptinvoicelist) {

					if(receiptinvdetail.getInv_receiptno().toString().matches(receipt.getReceiptno().toString())) {
						cpclConfigLabel += LINE_SEPARATOR;

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, receiptinvdetail.getInvoiceno().toString());
						cpclConfigLabel += text(250, y, "");
						cpclConfigLabel += text(400, y, receiptinvdetail.getInv_paidamount());

					}
				}

				cpclConfigLabel += LINE_SEPARATOR;
				cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
				cpclConfigLabel += LINE_SEPARATOR;
			}

			if (receipt.getPaymode().toLowerCase().matches("cash")) {
				cashamount += receipt.getPaidamount();
				paymode_cash = "Cash";
				Log.d("cashamount", "-->" + cashamount);
			} else if (receipt.getPaymode().toLowerCase().matches("cheque")) {
				chequeamount += receipt.getPaidamount();
				paymode_cheque = "Cheque";
				Log.d("chequeamount", "-->" + chequeamount);
			} else {
				otheramount += receipt.getPaidamount();
				paymode_other = "Others";
				Log.d("otheramount", "-->" + otheramount);
			}
			total += receipt.getPaidamount();
		}

		if(printinvoicedetail.equalsIgnoreCase("True")) {
		}else{
			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		}

//		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

		if (paymode_cash.toLowerCase().matches("cash")
				&& paymode_cheque.toLowerCase().matches("cheque")
				&& paymode_other.toLowerCase().matches("others")) {

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
					+ text(140, y, " : ") + text(180, y, cashamount);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
					+ text(140, y, " : ") + text(180, y, chequeamount);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Others")
					+ text(140, y, " : ") + text(180, y, otheramount);

			cpclConfigLabel += text(310, y, "Total") + text(450, y, " : ")
					+ text(486, y, total);

		} else if (paymode_cash.toLowerCase().matches("cash")
				&& paymode_cheque.toLowerCase().matches("cheque")) {

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
					+ text(140, y, " : ") + text(180, y, cashamount);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
					+ text(140, y, " : ") + text(180, y, chequeamount);
			cpclConfigLabel += text(310, y, "Total") + text(450, y, " : ")
					+ text(486, y, total);
		} else if (paymode_cash.toLowerCase().matches("cash")
				&& paymode_other.toLowerCase().matches("others")) {

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
					+ text(140, y, " : ") + text(180, y, cashamount);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Others")
					+ text(140, y, " : ") + text(180, y, otheramount);
			cpclConfigLabel += text(310, y, "Total") + text(450, y, " : ")
					+ text(486, y, total);
		} else if (paymode_cheque.toLowerCase().matches("cheque")
				&& paymode_other.toLowerCase().matches("others")) {

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
					+ text(140, y, " : ") + text(180, y, cashamount);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Others")
					+ text(140, y, " : ") + text(180, y, chequeamount);
			cpclConfigLabel += text(310, y, "Total") + text(450, y, " : ")
					+ text(486, y, total);
		} else if (paymode_cash.toLowerCase().matches("cash")) {
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
					+ text(140, y, " : ") + text(180, y, cashamount);

			cpclConfigLabel += text(310, y, "Total") + text(450, y, " : ")
					+ text(486, y, total);
		} else if (paymode_cheque.toLowerCase().matches("cheque")) {
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
					+ text(140, y, " : ") + text(180, y, chequeamount);

			cpclConfigLabel += text(310, y, "Total") + text(450, y, " : ")
					+ text(486, y, total);
		} else {
			cpclConfigLabel += text(310, y, "Total") + text(450, y, " : ")
					+ text(486, y, total);
		}
		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

		Log.d("footerArr", "" + footerArr.size());

		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque No");
		cpclConfigLabel += text(150, y, "Bank Code");
		cpclConfigLabel += text(470, y, "Amount");

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		for (Receipt receipt : receiptlist) {

			if (receipt.getPaymode().toLowerCase().matches("cheque")) {

				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, receipt
						.getChequeno().toString());
				// cpclConfigLabel += text(140, y,
				// prods.getDescription().toString());
				cpclConfigLabel += text(
						160,
						y,
						(receipt.getBankcode().length() > 21) ? receipt
								.getBankcode().substring(0, 20) : receipt
								.getBankcode());
				cpclConfigLabel += text(480, y, receipt.getPaidamount());
			}
		}

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

		if (footerArr.size() > 0) {

			// cpclConfigLabel += text(230, y += LINE_SPACING, "*********");

			for (ProductDetails footer : footerArr) {

				Log.d("footer value", "val " + footer.getReceiptMessage());

				if (footer.getReceiptMessage() != null
						&& !footer.getReceiptMessage().isEmpty()) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							footer.getReceiptMessage());
				}
			}
		}

		cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
				+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;
		os.write(cpclConfigLabel.getBytes());
		os.flush();
		os.close();

	}

	public void printReceiptSummary(String receiptno,String receiptdate,
									List<ProductDetails> receiptlist, List<ProductDetails> footerArr
			,ArrayList<HashMap<String, String>> salesReturnArr,ArrayList<ProductDetails> mSRHeaderDetailArr) {
		helper.showProgressDialog(context.getString(R.string.print),
				context.getString(R.string.creating_file_for_printing));
		try {
			createReceiptSummary(receiptno, receiptdate, receiptlist, footerArr, salesReturnArr, mSRHeaderDetailArr);
			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();
			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (IOException e) {
			e.printStackTrace();
			helper.dismissProgressDialog();
			helper.showErrorDialog(R.string.error_creating_file_for_printing);
		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}


	private void createReceiptSummary(String receiptno,String receiptdate,
									  List<ProductDetails> receiptlist, List<ProductDetails> footerValue
			,ArrayList<HashMap<String, String>> salesReturnArr,ArrayList<ProductDetails> mSRHeaderDetailArr)
			throws IOException {
		// TODO Auto-generated method stub

		logoStr = "logoprint";
		footerArr.clear();
		footerArr = footerValue;
		String showProductFullName = MobileSettingsSetterGetter
				.getShowProductFullName();
		String showFooter = MobileSettingsSetterGetter.getShowFooter();

		String printSalesReturnDetailOnReceipt  =  MobileSettingsSetterGetter.getPrintSalesReturnDetailOnReceipt();
		String printSalesReturnSummaryOnReceipt  =  MobileSettingsSetterGetter.getPrintSalesReturnSummaryOnReceipt();
		String decimal = MobileSettingsSetterGetter.getDecimalPoints();
		Log.d("receipt logoStr", "rece" + logoStr);

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);

		int y = 0;
		String paymode_cash = "", paymode_cheque = "", paymode_other = "",paymode_nets="";
		double cashamount = 0.00, chequeamount = 0.00, total = 0.00, otheramount = 0.00,netsamount=0.00;
		StringBuilder temp = new StringBuilder();
		y = printTitle(200, y, "RECEIPT SUMMARY", temp);
		y = printCompanyDetails(y, temp);

		String cpclConfigLabel = temp.toString();

		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt No")
				+ text(170, y, " : ") + text(190, y, receiptno);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt Date")
				+ text(170, y, " : ") + text(190, y, receiptdate);

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Invoice No");
		cpclConfigLabel += text(240, y, "Date");
		cpclConfigLabel += text(330, y, "Net Total");
		cpclConfigLabel += text(445, y, "Paid Amt");

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		for (ProductDetails receipt : receiptlist) {

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, receipt.getItemno().toString());
//			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,"IN2019-0033462");
			cpclConfigLabel += text(210, y, receipt.getItemdate());
			cpclConfigLabel += text(350, y, receipt.getNettot());
			cpclConfigLabel += text(470, y, receipt.getPaidamount());

			if (!In_Cash.getPay_Mode().matches("")
					|| !In_Cash.getPay_Mode().matches("null")
					|| !In_Cash.getPay_Mode().matches(null)) {
				String pay_Mode = In_Cash.getPay_Mode();
				if (pay_Mode.matches("cash")) {
					cashamount += Double.parseDouble(receipt.getPaidamount());
					paymode_cash = "Cash";
					Log.d("cashamount", "-->" + cashamount);
				} else if (pay_Mode.toLowerCase().matches("cheque")) {
					chequeamount += Double.parseDouble(receipt.getPaidamount());
					paymode_cheque = "Cheque";
					Log.d("chequeamount", "-->" + chequeamount);
				} else if (pay_Mode.toLowerCase().equalsIgnoreCase("nets ")) {
					netsamount += Double.parseDouble(receipt.getPaidamount());
					paymode_nets = "NETS";
					Log.d("netsamount", "-->" + netsamount);
				} else {
					otheramount += Double.parseDouble(receipt.getPaidamount());
					paymode_other = "Others";
					Log.d("otheramount", "-->" + otheramount);
				}
				total += Double.parseDouble(receipt.getPaidamount());
			}
		}

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

		if (paymode_cash.toLowerCase().matches("cash")
				&& paymode_cheque.toLowerCase().matches("cheque")
				&& paymode_other.toLowerCase().matches("others")) {

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
					+ text(140, y, " : ") + text(180, y, cashamount);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
					+ text(140, y, " : ") + text(180, y, chequeamount);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Others")
					+ text(140, y, " : ") + text(180, y, otheramount);

			cpclConfigLabel += text(310, y, "Total Paid") + text(450, y, " : ")
					+ text(486, y, total);

		} else if (paymode_cash.toLowerCase().matches("cash")
				&& paymode_cheque.toLowerCase().matches("cheque")) {

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
					+ text(140, y, " : ") + text(180, y, cashamount);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
					+ text(140, y, " : ") + text(180, y, chequeamount);
			cpclConfigLabel += text(310, y, "Total Paid") + text(450, y, " : ")
					+ text(486, y, total);
		} else if (paymode_cash.toLowerCase().matches("cash")
				&& paymode_other.toLowerCase().matches("others")) {

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
					+ text(140, y, " : ") + text(180, y, cashamount);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Others")
					+ text(140, y, " : ") + text(180, y, otheramount);
			cpclConfigLabel += text(310, y, "Total Paid") + text(450, y, " : ")
					+ text(486, y, total);
		} else if (paymode_cheque.toLowerCase().matches("cheque")
				&& paymode_other.toLowerCase().matches("others")) {

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
					+ text(140, y, " : ") + text(180, y, cashamount);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Others")
					+ text(140, y, " : ") + text(180, y, chequeamount);
			cpclConfigLabel += text(310, y, "Total Paid") + text(450, y, " : ")
					+ text(486, y, total);
		} else if (paymode_cash.toLowerCase().matches("cash")) {
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cash")
					+ text(140, y, " : ") + text(180, y, cashamount);

			cpclConfigLabel += text(310, y, "Total Paid") + text(450, y, " : ")
					+ text(486, y, total);
		} else if (paymode_cheque.toLowerCase().matches("cheque")) {
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Cheque")
					+ text(140, y, " : ") + text(180, y, chequeamount);

			cpclConfigLabel += text(310, y, "Total Paid") + text(450, y, " : ")
					+ text(486, y, total);
		} else if (paymode_nets.toLowerCase().matches("nets")) {
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "NETS")
					+ text(140, y, " : ") + text(180, y, netsamount);

			cpclConfigLabel += text(310, y, "Total Paid") + text(450, y, " : ")
					+ text(486, y, total);
		}else {
			cpclConfigLabel += text(310,  y += LINE_SPACING, "Total Paid") + text(450, y, " : ")
					+ text(486, y, total);
		}
		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

		if(printSalesReturnSummaryOnReceipt!=null && !printSalesReturnSummaryOnReceipt.isEmpty()) {
			if (printSalesReturnSummaryOnReceipt.equalsIgnoreCase("True") && printSalesReturnDetailOnReceipt.equalsIgnoreCase("True") || printSalesReturnDetailOnReceipt.equalsIgnoreCase("False")) {
				if (salesReturnArr.size() > 0) {
					double dTotal = 0.00;
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

					cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SR No");
					cpclConfigLabel += text(250, y, "SR Date");
					cpclConfigLabel += text(475, y, "Amount");
					cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

					for (int i = 0; i < salesReturnArr.size(); i++) {
						String salesReturnNo = salesReturnArr.get(i).get("SalesReturnNo");
						String salesReturnDate = salesReturnArr.get(i).get("SalesReturnDate");
						String creditAmount = salesReturnArr.get(i).get("CreditAmount");
						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, salesReturnNo);
						cpclConfigLabel += text(250, y, salesReturnDate);
						cpclConfigLabel += text(490, y, creditAmount);

						if (creditAmount != null && !creditAmount.isEmpty()) {
							dTotal += Double.valueOf(creditAmount);
						}

					}

					cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
					cpclConfigLabel += text(310, y += LINE_SPACING, "Total")
							+ text(450, y, " : ")
							+ text(486, y, twoDecimalPoint(dTotal));
					cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

				}
			}
		}


		//PrintSalesReturnDetailOnReceipt
		if(printSalesReturnDetailOnReceipt!=null && !printSalesReturnDetailOnReceipt.isEmpty()){
			if(printSalesReturnSummaryOnReceipt.equalsIgnoreCase("False") && printSalesReturnDetailOnReceipt.equalsIgnoreCase("True") ){

				if(mSRHeaderDetailArr.size()>0){
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
					cpclConfigLabel += text(221, y, "SALES RETURN");
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");
					for (ProductDetails mSRHeader : mSRHeaderDetailArr) {

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Sales Return No")
								+ text(200, y, " : ")
								+ text(230, y, mSRHeader.getItemno());

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
								"Sales Return Date")
								+ text(200, y, " : ")
								+ text(230, y, mSRHeader.getItemdate());

						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
								LINE_THICKNESS);

						cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
						cpclConfigLabel += text(70, y, "Description");
						cpclConfigLabel += text(280, y, "Qty");
						cpclConfigLabel += text(365, y, "Price");
						cpclConfigLabel += text(500, y, "Total");

						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
								LINE_THICKNESS);
						for (ProdDetails mSRDetail : mSRHeader.getProductsDetails()) {
							String salesReturnNo = mSRDetail.getItemnum().toString();
							if (salesReturnNo.matches(mSRHeader.getItemno().toString())) {
								cpclConfigLabel += text(LEFT_MARGIN,y += LINE_SPACING, mSRDetail.getSno().toString());

								if (showProductFullName.matches("True")) {
									cpclConfigLabel += text(70,y,(mSRDetail.getDescription().length() > 31) ? mSRDetail
											.getDescription().substring(0,30) : mSRDetail
											.getDescription());
									cpclConfigLabel += text(280, y += LINE_SPACING,mSRDetail.getQty().toString());
								} else {
									cpclConfigLabel += text(70,y,
											(mSRDetail.getDescription().length() > 10) ? mSRDetail
													.getDescription().substring(0,9) : mSRDetail.getDescription());
									cpclConfigLabel += text(280, y, mSRDetail.getQty().toString());

								}
								double value = Double.parseDouble(mSRDetail
										.getPrice().toString());
								String values = String.format("%."+decimal+"f",value);
								cpclConfigLabel += text(365, y, values);
								cpclConfigLabel += text(486, y, mSRDetail.getTotal()
										.toString());
								if (mSRDetail.getFocqty() > 0) {
									cpclConfigLabel += text(70, y += LINE_SPACING,"Foc")+ text(210, y, " : ")
											+ text(280, y,"" + (int) mSRDetail.getFocqty());
								}

								if (mSRDetail.getExchangeqty() > 0) {
									cpclConfigLabel += text(70, y += LINE_SPACING,"Exchange")
											+ text(210, y, " : ")
											+ text(280,y,""+ (int) mSRDetail.getExchangeqty());
								}

							}
						}
						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
								LINE_THICKNESS);
						if (Double.parseDouble(mSRHeader.getItemdisc().toString()) > 0) {
							cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
									"Item Disc")+ text(140, y, " : ")
									+ text(180, y, mSRHeader.getItemdisc().toString());
						}
						if (Double.parseDouble(mSRHeader.getTax().toString()) > 0) {

							if (Double.parseDouble(mSRHeader.getItemdisc().toString()) == 0) {

								if (Double
										.parseDouble(mSRHeader.getBilldisc().toString()) > 0) {
									cpclConfigLabel += text(LEFT_MARGIN,
											y += LINE_SPACING, "Bill Disc")
											+ text(140, y, " : ")
											+ text(180, y, mSRHeader.getBilldisc()
											.toString());
								}
								if (Double
										.parseDouble(mSRHeader.getBilldisc().toString()) == 0
										&& Double.parseDouble(mSRHeader.getItemdisc()
										.toString()) == 0) {
									cpclConfigLabel += text(310, y += LINE_SPACING,
											"Sub Total")
											+ text(450, y, " : ")
											+ text(486, y, mSRHeader.getSubtotal()
											.toString());
								} else {

									cpclConfigLabel += text(310, y, "Sub Total")
											+ text(450, y, " : ")
											+ text(486, y, mSRHeader.getSubtotal()
											.toString());
								}
							} else {

								cpclConfigLabel += text(310, y, "Sub Total")
										+ text(450, y, " : ")
										+ text(486, y, mSRHeader.getSubtotal().toString());
							}
						}
						if (Double.parseDouble(mSRHeader.getBilldisc().toString()) > 0) {

							if (Double.parseDouble(mSRHeader.getItemdisc().toString()) != 0) {
								cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
										"Bill Disc")
										+ text(140, y, " : ")
										+ text(180, y, mSRHeader.getBilldisc().toString());
							} else {
								cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
										"") + text(140, y, "") + text(180, y, "");
							}
						}

						if (Double.parseDouble(mSRHeader.getTax().toString()) > 0) {

							if (Double.parseDouble(mSRHeader.getBilldisc().toString()) == 0) {
								cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
										"") + text(140, y, "") + text(180, y, "");
							}

							cpclConfigLabel += text(310, y, "Tax")
									+ text(450, y, " : ")
									+ text(486, y, mSRHeader.getTax().toString());
						}

						cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
								+ text(450, y, " : ")
								+ text(486, y, mSRHeader.getNettot().toString());

						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
								LINE_THICKNESS);
						cpclConfigLabel += text(250, y += LINE_SPACING, "SalesReturn Used")
								+ text(450, y, " : ")
								+ text(486, y, mSRHeader.getCreditAmount());


     /*cpclConfigLabel += text(310, y += LINE_SPACING, "Balance Amt")
       + text(450, y, " : ")
       + text(486, y, mSRHeader.getBalanceAmount());
*/
						cpclConfigLabel += horizontalLine(y += LINE_SPACING,
								LINE_THICKNESS);
					}
				}

			}
		}

		Log.d("footerArr", "" + footerArr.size());

		if (footerArr.size() > 0) {

			// cpclConfigLabel += text(230, y += LINE_SPACING, "*********");

			for (ProductDetails footer : footerArr) {

				Log.d("footer value", "val " + footer.getReceiptMessage());

				if (footer.getReceiptMessage() != null
						&& !footer.getReceiptMessage().isEmpty()) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							footer.getReceiptMessage());
				}
			}
		}

		cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
				+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;
		os.write(cpclConfigLabel.getBytes());
		os.flush();
		os.close();

	}

	private int printCompanyDetails(int y, StringBuilder cpclConfigLabel) {

		String showaddress1 = MobileSettingsSetterGetter.getShowAddress1();
		String showaddress2 = MobileSettingsSetterGetter.getShowAddress2();
		String showaddress3 = MobileSettingsSetterGetter.getShowAddress3();
		String showcountrypostal = MobileSettingsSetterGetter
				.getShowCountryPostal();
		String showphone = MobileSettingsSetterGetter.getShowPhone();

		String showfax = MobileSettingsSetterGetter.getShowFax();
		String showemail = MobileSettingsSetterGetter.getShowEmail();
		String showtaxregno = MobileSettingsSetterGetter.getShowTaxRegNo();
		String showbusregno = MobileSettingsSetterGetter.getShowBizRegNo();
		String CompanyNameAlias = MobileSettingsSetterGetter.getCompanyNameAlias();

		String country = Company.getCountry();
		String phoneno = Company.getPhoneNo();
		String zipcode = Company.getZipCode();
		String countryZipcode = country + " " + zipcode;

		String fax = Company.getFax();
		String email = Company.getEmail();
		String taxregno = Company.getTaxRegNo();
		String busregno = Company.getBusinessRegNo();

		String showuserphoneno = MobileSettingsSetterGetter.getShowUserPhoneNo();
		String loginphoneno  = SalesOrderSetGet.getLoginPhoneNo();
		String username = SupplierSetterGetter.getUsername();

		String InvoiceTelCaption = MobileSettingsSetterGetter.getInvoiceTelCaption();
		String InvoiceFaxCaption = MobileSettingsSetterGetter.getInvoiceFaxCaption();
		String InvoiceEmailCaption = MobileSettingsSetterGetter.getInvoiceEmailCaption();
		String InvoiceBizRegNoCaption = MobileSettingsSetterGetter.getInvoiceBizRegNoCaption();
		String InvoiceTaxRegNoCaption = MobileSettingsSetterGetter.getInvoiceTaxRegNoCaption();

		String CenterAlignCompanyName = MobileSettingsSetterGetter.getCenterAlignCompanyName();

		if(CenterAlignCompanyName!=null && !CenterAlignCompanyName.isEmpty()){

		}else{
			CenterAlignCompanyName="";
		}

		Log.d("showtaxregno","-->"+showtaxregno +"-->"+InvoiceTaxRegNoCaption+"taxregno  : "+taxregno+"shortcode:"+Company.getShortCode());

		if(Company.getShortCode().matches("JUBI")){

			Log.d("JUBIShortcode","Executing!!!");


			if(!CompanyNameAlias.equals("")){
				cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
						CompanyNameAlias));
			}

			if (!Company.getCompanyName().equals("")) {
				cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
						Company.getCompanyName()));
			}

			if(Company.getShortCode().matches("REGG")||Company.getShortCode().matches("SUPERSTAR")){
				if (!Company.getAddress1().equals("")) {
					if (!zipcode.equals("")) {
						cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
								Company.getAddress1() +","+countryZipcode));
					} else {
						cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
								Company.getAddress1() +","+country));
					}
				}

				if (showphone.matches("True")) {
					if (!phoneno.equals("")) {

						if(!InvoiceTelCaption.equals("")){

							if (showfax != null && !showfax.isEmpty()) {
								if (showfax.matches("True")) {
									if (!fax.equals("")) {

										if(!InvoiceFaxCaption.equals("")){
											cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
													InvoiceTelCaption+phoneno+","+InvoiceFaxCaption+fax));
										}else {
											cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
													InvoiceTelCaption+phoneno+","+fax));
										}
									}
								}
							}

//							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
//									InvoiceTelCaption+phoneno));
						}else{
							if (showfax != null && !showfax.isEmpty()) {
								if (showfax.matches("True")) {
									if (!fax.equals("")) {

										if(!InvoiceFaxCaption.equals("")){
											cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
													"TEL" + " : " +phoneno+","+InvoiceFaxCaption+fax));
										}else {
											cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
													"TEL" + " : " +phoneno+","+fax));
										}
									}
								}
							}
//							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
//									"TEL" + " : " +phoneno));
						}

					}
				}

				if (showbusregno != null && !showbusregno.isEmpty()) {

					if (showbusregno.matches("True")) {
						if (!busregno.equals("")) {
							if (!InvoiceBizRegNoCaption.equals("")) {

								if (showtaxregno != null && !showtaxregno.isEmpty()) {
									if (showtaxregno.matches("True")) {
										if (!taxregno.equals("")) {
											if (!InvoiceTaxRegNoCaption.equals("")) {
												cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
														InvoiceBizRegNoCaption + busregno+","+InvoiceTaxRegNoCaption + taxregno));
											}else{
												cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
														InvoiceBizRegNoCaption + busregno+","+"TAX REG NO" + " : " +taxregno));
											}

										}
									}
								}

//								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
//										InvoiceBizRegNoCaption + busregno));
							}else{

								if (showtaxregno != null && !showtaxregno.isEmpty()) {
									if (showtaxregno.matches("True")) {
										if (!taxregno.equals("")) {
											if (!InvoiceTaxRegNoCaption.equals("")) {
												cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
														"GST REG NO" + " : " +busregno+","+InvoiceTaxRegNoCaption + taxregno));
											}else{
												cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
														"GST REG NO" + " : " +busregno+","+"TAX REG NO" + " : " +taxregno));
											}

										}
									}
								}

//								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
//										"CO REG NO" + " : " +busregno));
							}

						}
					}
				}
			}else {

				Log.d("otherShortcode","Execute!!");

				if (showaddress1.matches("True")) {
					if (!Company.getAddress1().equals("")) {
						cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
								Company.getAddress1()));
					}
				}

				if (showaddress2.matches("True")) {
					if (!Company.getAddress2().equals("")) {
						cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
								Company.getAddress2()));
					}
				}

				if (showaddress3.matches("True")) {
					if (!Company.getAddress3().equals("")) {
						cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
								Company.getAddress3()));
					}
				}

				if (showcountrypostal.matches("True")) {
					if (!country.equals("")) {

						if (!zipcode.equals("")) {
							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
									countryZipcode));
						} else {
							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
									country));
						}
					}
				}

				if (showphone.matches("True")) {
					if (!phoneno.equals("")) {

						if (!InvoiceTelCaption.equals("")) {

							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
									InvoiceTelCaption + phoneno));
						} else {
							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
									"TEL" + " : " + phoneno));
						}

					}
				}

				if (showfax != null && !showfax.isEmpty()) {
					if (showfax.matches("True")) {
						if (!fax.equals("")) {

							if (!InvoiceFaxCaption.equals("")) {
								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
										InvoiceFaxCaption + fax));
							} else {
								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
										fax));
							}
						}
					}
				}

				if (showemail != null && !showemail.isEmpty()) {
					if (showemail.matches("True")) {
						if (!email.equals("")) {

							if (!InvoiceEmailCaption.equals("")) {
								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
										"EMAIL" + " : " + InvoiceEmailCaption + email));
							} else {
								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
										"EMAIL" + " : " + email));
							}


						}
					}
				}

				if (showtaxregno != null && !showtaxregno.isEmpty()) {
					if (showtaxregno.matches("True")) {
						if (!taxregno.equals("")) {
							if (!InvoiceTaxRegNoCaption.equals("")) {
								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
										InvoiceTaxRegNoCaption + taxregno));
							} else {
								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
										"TAX REG NO" + " : " + taxregno));
							}

						}
					}
				}

				if (showbusregno != null && !showbusregno.isEmpty()) {
					if (showbusregno.matches("True")) {
						if (!busregno.equals("")) {
							if (!InvoiceBizRegNoCaption.equals("")) {
								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
										InvoiceBizRegNoCaption + busregno));
							} else {
								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
										"GST REG NO" + " : " + busregno));
							}

						}
					}
				}

				if (showuserphoneno != null && !showuserphoneno.isEmpty() && loginphoneno != null && !loginphoneno.isEmpty()) {
					if (showuserphoneno.matches("True") && !loginphoneno.matches("null")) {

						cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
								"Contact " + " : " + username + " " + loginphoneno));

					}
				}

				if (mUser != null && !mUser.isEmpty()) {
					cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING, "User")
							+ text(170, y, " : ") + text(190, y, mUser));
				}
			}



		}else {

			// company detial in center alignment
			if (CenterAlignCompanyName.equalsIgnoreCase("True")) {


				if (!CompanyNameAlias.equals("")) {
					cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
							CompanyNameAlias));
				}

				if (!Company.getCompanyName().equals("")) {
					cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
							Company.getCompanyName()));
				}

				if (Company.getShortCode().matches("REGG") || Company.getShortCode().matches("SUPERSTAR")) {
					if (!Company.getAddress1().equals("")) {
						if (!zipcode.equals("")) {
							cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
									Company.getAddress1() + "," + countryZipcode));
						} else {
							cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
									Company.getAddress1() + "," + country));
						}
					}

					if (showphone.matches("True")) {
						if (!phoneno.equals("")) {

							if (!InvoiceTelCaption.equals("")) {

								if (showfax != null && !showfax.isEmpty()) {
									if (showfax.matches("True")) {
										if (!fax.equals("")) {

											if (!InvoiceFaxCaption.equals("")) {
												cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
														InvoiceTelCaption + phoneno + "," + InvoiceFaxCaption + fax));
											} else {
												cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
														InvoiceTelCaption + phoneno + "," + fax));
											}
										}
									}
								}

//							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
//									InvoiceTelCaption+phoneno));
							} else {
								if (showfax != null && !showfax.isEmpty()) {
									if (showfax.matches("True")) {
										if (!fax.equals("")) {

											if (!InvoiceFaxCaption.equals("")) {
												cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
														"TEL" + " : " + phoneno + "," + InvoiceFaxCaption + fax));
											} else {
												cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
														"TEL" + " : " + phoneno + "," + fax));
											}
										}
									}
								}
//							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
//									"TEL" + " : " +phoneno));
							}

						}
					}

					if (showbusregno != null && !showbusregno.isEmpty()) {

						if (showbusregno.matches("True")) {
							if (!busregno.equals("")) {
								if (!InvoiceBizRegNoCaption.equals("")) {

									if (showtaxregno != null && !showtaxregno.isEmpty()) {
										if (showtaxregno.matches("True")) {
											if (!taxregno.equals("")) {
												if (!InvoiceTaxRegNoCaption.equals("")) {
													cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
															InvoiceBizRegNoCaption + busregno + "," + InvoiceTaxRegNoCaption + taxregno));
												} else {
													cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
															InvoiceBizRegNoCaption + busregno + "," + "TAX REG NO" + " : " + taxregno));
												}

											}
										}
									}

//								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
//										InvoiceBizRegNoCaption + busregno));
								} else {

									if (showtaxregno != null && !showtaxregno.isEmpty()) {
										if (showtaxregno.matches("True")) {
											if (!taxregno.equals("")) {
												if (!InvoiceTaxRegNoCaption.equals("")) {
													cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
															"CO REG NO" + " : " + busregno + "," + InvoiceTaxRegNoCaption + taxregno));
												} else {
													cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
															"CO REG NO" + " : " + busregno + "," + "TAX REG NO" + " : " + taxregno));
												}

											}
										}
									}

//								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
//										"CO REG NO" + " : " +busregno));
								}

							}
						}
					}
				} else {

					if (showaddress1.matches("True")) {
						if (!Company.getAddress1().equals("")) {
							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
									Company.getAddress1()));
						}
					}

					if (showaddress2.matches("True")) {
						if (!Company.getAddress2().equals("")) {
							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
									Company.getAddress2()));
						}
					}

					if (showaddress3.matches("True")) {
						if (!Company.getAddress3().equals("")) {
							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
									Company.getAddress3()));
						}
					}

					if (showcountrypostal.matches("True")) {
						if (!country.equals("")) {

							if (!zipcode.equals("")) {
								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
										countryZipcode));
							} else {
								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
										country));
							}
						}
					}

					if (showphone.matches("True")) {
						if (!phoneno.equals("")) {

							if (!InvoiceTelCaption.equals("")) {

								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
										InvoiceTelCaption + phoneno));
							} else {
								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
										"TEL" + " : " + phoneno));
							}

						}
					}

					if (showfax != null && !showfax.isEmpty()) {
						if (showfax.matches("True")) {
							if (!fax.equals("")) {

								if (!InvoiceFaxCaption.equals("")) {
									cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
											InvoiceFaxCaption + fax));
								} else {
									cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
											fax));
								}
							}
						}
					}

					if (showemail != null && !showemail.isEmpty()) {
						if (showemail.matches("True")) {
							if (!email.equals("")) {

								if (!InvoiceEmailCaption.equals("")) {
									cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
											"EMAIL" + " : " + InvoiceEmailCaption + email));
								} else {
									cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
											"EMAIL" + " : " + email));
								}


							}
						}
					}

					if (showtaxregno != null && !showtaxregno.isEmpty()) {
						if (showtaxregno.matches("True")) {
							if (!taxregno.equals("")) {
								if (!InvoiceTaxRegNoCaption.equals("")) {
									cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
											InvoiceTaxRegNoCaption + taxregno));
								} else {
									cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
											"TAX REG NO" + " : " + taxregno));
								}

							}
						}
					}

					if (showbusregno != null && !showbusregno.isEmpty()) {
						if (showbusregno.matches("True")) {
							if (!busregno.equals("")) {
								if (!InvoiceBizRegNoCaption.equals("")) {
									cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
											InvoiceBizRegNoCaption + busregno));
								} else {
									cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
											"CO REG NO" + " : " + busregno));
								}

							}
						}
					}

					if (showuserphoneno != null && !showuserphoneno.isEmpty() && loginphoneno != null && !loginphoneno.isEmpty()) {
						if (showuserphoneno.matches("True") && !loginphoneno.matches("null")) {

							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
									"Contact " + " : " + username + " " + loginphoneno));

						}
					}

					if (mUser != null && !mUser.isEmpty()) {
						cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING, "User")
								+ text(170, y, " : ") + text(190, y, mUser));
					}
				}
			}
			// company detial in left alignment
			else {
				if (!CompanyNameAlias.equals("")) {
					cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
							CompanyNameAlias));
				}

				if (!Company.getCompanyName().equals("")) {
					cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
							Company.getCompanyName()));
				}

				Log.d("alignment", "-->" + showtaxregno);

				if (Company.getShortCode().matches("REGG")) {
					if (!Company.getAddress1().equals("")) {
						if (!zipcode.equals("")) {
							cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
									Company.getAddress1() + "," + Company.getAddress2() + "," + Company.getAddress3() + "," + countryZipcode));
						} else {
							cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
									Company.getAddress1() + "," + Company.getAddress2() + "," + Company.getAddress3() + "," + country));
						}
					}

					if (showphone.matches("True")) {
						if (!phoneno.equals("")) {

							if (!InvoiceTelCaption.equals("")) {

								if (showfax != null && !showfax.isEmpty()) {
									if (showfax.matches("True")) {
										if (!fax.equals("")) {

											if (!InvoiceFaxCaption.equals("")) {
												cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
														InvoiceTelCaption + phoneno + "," + InvoiceFaxCaption + fax));
											} else {
												cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
														InvoiceTelCaption + phoneno + "," + fax));
											}
										}
									}
								}

//							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
//									InvoiceTelCaption+phoneno));
							} else {
								if (showfax != null && !showfax.isEmpty()) {
									if (showfax.matches("True")) {
										if (!fax.equals("")) {

											if (!InvoiceFaxCaption.equals("")) {
												cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
														"TEL" + " : " + phoneno + "," + InvoiceFaxCaption + fax));
											} else {
												cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
														"TEL" + " : " + phoneno + "," + fax));
											}
										}
									}
								}
//							cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
//									"TEL" + " : " +phoneno));
							}

						}
					}

					if (showbusregno != null && !showbusregno.isEmpty()) {

						if (showbusregno.matches("True")) {
							if (!busregno.equals("")) {
								if (!InvoiceBizRegNoCaption.equals("")) {

									if (showtaxregno != null && !showtaxregno.isEmpty()) {
										if (showtaxregno.matches("True")) {
											if (!taxregno.equals("")) {
												if (!InvoiceTaxRegNoCaption.equals("")) {
													cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
															InvoiceBizRegNoCaption + busregno + "," + InvoiceTaxRegNoCaption + taxregno));
												} else {
													cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
															InvoiceBizRegNoCaption + busregno + "," + "TAX REG NO" + " : " + taxregno));
												}

											}
										}
									}

//								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
//										InvoiceBizRegNoCaption + busregno));
								} else {

									if (showtaxregno != null && !showtaxregno.isEmpty()) {
										if (showtaxregno.matches("True")) {
											if (!taxregno.equals("")) {
												if (!InvoiceTaxRegNoCaption.equals("")) {
													cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
															"CO REG NO" + " : " + busregno + "," + InvoiceTaxRegNoCaption + taxregno));
												} else {
													cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
															"CO REG NO" + " : " + busregno + "," + "TAX REG NO" + " : " + taxregno));
												}

											}
										}
									}

//								cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING,
//										"CO REG NO" + " : " +busregno));
								}

							}
						}
					}
				} else {

					Log.d("Executing!!!", "-->" + "Executing!!" + Company.getShortCode());

					if (showaddress1.matches("True")) {
						if (!Company.getAddress1().equals("")) {
							cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
									Company.getAddress1()));
						}
					}

					if (showaddress2.matches("True")) {
						if (!Company.getAddress2().equals("")) {
							cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
									Company.getAddress2()));
						}
					}

					if (showaddress3.matches("True")) {
						if (!Company.getAddress3().equals("")) {
							cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
									Company.getAddress3()));
						}
					}

					if (showcountrypostal.matches("True")) {
						if (!country.equals("")) {

							if (!zipcode.equals("")) {
								cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
										countryZipcode));
							} else {
								cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
										country));
							}
						}
					}

					if (showphone.matches("True")) {
						if (!phoneno.equals("")) {

							if (!InvoiceTelCaption.equals("")) {

								cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
										InvoiceTelCaption + phoneno));
							} else {
								cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
										"TEL" + " : " + phoneno));
							}

						}
					}

					if (showfax != null && !showfax.isEmpty()) {
						if (showfax.matches("True")) {
							if (!fax.equals("")) {

								if (!InvoiceFaxCaption.equals("")) {
									cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
											InvoiceFaxCaption + fax));
								} else {
									cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
											fax));
								}
							}
						}
					}

					if (showemail != null && !showemail.isEmpty()) {
						if (showemail.matches("True")) {
							if (!email.equals("")) {

								if (!InvoiceEmailCaption.equals("")) {
									cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
											"EMAIL" + " : " + InvoiceEmailCaption + email));
								} else {
									cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
											"EMAIL" + " : " + email));
								}


							}
						}
					}
					Log.d("taxregno", "" + taxregno + "InvoiceTaxRegNoCaption:" + InvoiceTaxRegNoCaption);

					if (Company.getShortCode().matches("SUPERSTAR")) {
						if (showtaxregno.matches("True")) {
							if (!taxregno.equals("")) {
								if (!InvoiceTaxRegNoCaption.equals("")) {
									cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
											"TAX REG NO" + " : " + InvoiceTaxRegNoCaption + taxregno));
								} else {
									cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
											"TAX REG NO" + " : " + taxregno));
								}

							}
						}
					} else {
						if (showtaxregno.matches("True")) {
							if (!taxregno.equals("")) {
								if (!InvoiceTaxRegNoCaption.equals("")) {
									cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
											InvoiceTaxRegNoCaption + taxregno));
								} else {
									cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
											"TAX REG NO" + " : " + taxregno));
								}

							}
						}
					}

//				if (showtaxregno != null && !showtaxregno.isEmpty()) {
//				}

					if (Company.getShortCode().matches("SUPERSTAR")) {
						if (showbusregno != null && !showbusregno.isEmpty()) {
							if (showbusregno.matches("True")) {
								if (!busregno.equals("")) {
									if (!InvoiceBizRegNoCaption.equals("")) {
										cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
												"CO REG NO" + " : " + InvoiceBizRegNoCaption + busregno));
									} else {
										cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
												"CO REG NO" + " : " + busregno));
									}

								}
							}
						}
					} else {
						if (showbusregno != null && !showbusregno.isEmpty()) {
							if (showbusregno.matches("True")) {
								if (!busregno.equals("")) {
									if (!InvoiceBizRegNoCaption.equals("")) {
										cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
												InvoiceBizRegNoCaption + busregno));
									} else {
										cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
												"CO REG NO" + " : " + busregno));
									}

								}
							}
						}
					}

					if (showuserphoneno != null && !showuserphoneno.isEmpty() && loginphoneno != null && !loginphoneno.isEmpty()) {
						if (showuserphoneno.matches("True") && !loginphoneno.matches("null")) {

							cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING,
									"Contact " + " : " + username + " " + loginphoneno));

			 /*    cpclConfigLabel+= text(LEFT_MARGIN, y += LINE_SPACING,
			       "Contact ")
			       + text(150, y, " : ")
			       + text(180, y, username +" "+loginphoneno);*/

						}
					}

					if (mUser != null && !mUser.isEmpty()) {
						cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, "User")
								+ text(170, y, " : ") + text(190, y, mUser));
					}
				}
			}
		}
		cpclConfigLabel
				.append(horizontalLine(y += LINE_SPACING, LINE_THICKNESS));
		return y;
	}

/*	private  String subAlignCenter(String data){
		int length = (PAPER_WIDTH-data.length())/2;
		//	System.out.println(length);
		// Create a new StringBuilder.
		StringBuilder builder = new StringBuilder();
		for(int i=0; i < length; i++)
		{
			builder.append(" ");
		}
		// Convert to string.
		String result = builder.toString()+data;
		return result;
	}*/

	private String textCenter(int x, int y, String text) {

		int length = (PAPER_WIDTH-text.length())/2;
		//	System.out.println(length);
		// Create a new StringBuilder.
		StringBuilder builder = new StringBuilder();
		for(int i=0; i < length; i++)
		{
			builder.append(" ");
		}
		// Convert to string.
		String result = builder.toString()+text;


		return new StringBuilder(CMD_TEXT).append(SPACE).append(FONT)
				.append(SPACE).append(FONT_SIZE).append(SPACE).append(x)
				.append(SPACE).append(y).append(SPACE).append(result)
				.append(LINE_SEPARATOR).toString();
	}

	public void printProductStock(String locationCode, String locationName,
			String datetime, ArrayList<ProductStockGetSet> ProductListArray,
			ArrayList<ProductStockGetSet> productstockArr) throws IOException {
		helper.showProgressDialog(R.string.print,
				R.string.creating_file_for_printing);
		try {

			Log.d("ProductListArrayPrint","-->"+ProductListArray.size());
			createProductStock(locationCode, locationName, datetime,
					ProductListArray, productstockArr);

			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();
			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}

	private void createProductStock(String locationCode, String locationName,
			String datetime, ArrayList<ProductStockGetSet> ProductListArray,
			ArrayList<ProductStockGetSet> productstockArr) throws IOException {

		logoStr = "logoprint";

		Log.d("prodstoc logoStr", "prodst" + logoStr);

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		int y = 0;
		StringBuilder temp = new StringBuilder();
		y = printTitle(200, y, "Stock Report", temp);
		y = printCompanyDetails(y, temp);

		String cpclConfigLabel = temp.toString();
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Date & Time")
				+ text(170, y, " : ") + text(190, y, datetime);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Location Code")
				+ text(170, y, " : ") + text(190, y, locationCode);
		System.out.println("locationName==>" + locationName);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Location Name")
				+ text(170, y, " : ") + text(190, y, locationName);
		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

		String mobileproductstockprint = SalesOrderSetGet
				.getMobileproductstockprint();

//		mobileproductstockprint="0";

		if (!mobileproductstockprint.matches("")) {
			if (mobileproductstockprint.matches("1")) {
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						"Product Name");
				cpclConfigLabel += text(400, y, "Issue");
				cpclConfigLabel += text(500, y, "Qty");

				cpclConfigLabel += horizontalLine(y += LINE_SPACING,
						LINE_THICKNESS);

				for (ProductStockGetSet productstock : productstockArr) {
					cpclConfigLabel += text(
							LEFT_MARGIN,
							y += LINE_SPACING,
							(productstock.getProduct_Name().length() > 25) ? productstock
									.getProduct_Name().substring(0, 24)
									: productstock.getProduct_Name());

					if (productstock.getIssueqty().matches(".00")
							|| productstock.getIssueqty().matches("0.00")
							|| productstock.getIssueqty().matches("0.0")) {
						cpclConfigLabel += text(400, y, "0");
					} else {
						String issueqty = String.valueOf(
								productstock.getIssueqty()).split("\\.")[0];
						cpclConfigLabel += text(400, y, issueqty.toString());
					}
					if (productstock.getQty().matches(".00")
							|| productstock.getQty().matches("0.00")
							|| productstock.getQty().matches("0.0")) {
						cpclConfigLabel += text(500, y, "0");
					} else {
						// StringTokenizer tokens = new StringTokenizer(
						// productstock.getQty(), ".");
						System.out.println("productstock.getQty()==>"
								+ productstock.getQty());
						String qty = String.valueOf(productstock.getQty())
								.split("\\.")[0];
						// String qty = tokens.nextToken();
						System.out.println("numWihoutDecimal==>" + qty);
						cpclConfigLabel += text(500, y, qty);
					}
				}

			} else if (mobileproductstockprint.matches("0")) {

				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						"Product Name");
				cpclConfigLabel += text(310, y, "Carton");
				cpclConfigLabel += text(410, y, "Loose");
				cpclConfigLabel += text(500, y, "Qty");

				cpclConfigLabel += horizontalLine(y += LINE_SPACING,
						LINE_THICKNESS);

				for (ProductStockGetSet productstock : ProductListArray) {

					cpclConfigLabel += text(
							LEFT_MARGIN,
							y += LINE_SPACING,
							(productstock.getProduct_Name().length() > 20) ? productstock
									.getProduct_Name().substring(0, 19)
									: productstock.getProduct_Name());
					cpclConfigLabel += text(310, y,
							String.valueOf(productstock.getCartonqty()));

					if (productstock.getLooseqty().matches(".00")
							|| productstock.getLooseqty().matches("0.00")
							|| productstock.getLooseqty().matches("0.0")) {
						cpclConfigLabel += text(410, y, "0");
					} else {
						String looseqty = String.valueOf(
								productstock.getLooseqty()).split("\\.")[0];
						System.out.println("numWihoutDecimal=looseqty=>"
								+ looseqty);
						cpclConfigLabel += text(410, y, looseqty.toString());
					}

					if (productstock.getProduct_Quantity().matches(".00")
							|| productstock.getProduct_Quantity().matches(
									"0.00")
							|| productstock.getProduct_Quantity()
									.matches("0.0")) {
						cpclConfigLabel += text(500, y, "0");
					} else {

						System.out
								.println("productstock.getProduct_Quantity()==>"
										+ productstock.getProduct_Quantity());
						String qty = String.valueOf(
								productstock.getProduct_Quantity())
								.split("\\.")[0];
						System.out.println("numWihoutDecimal==>" + qty);
						cpclConfigLabel += text(500, y, qty);
					}

				}

			}
		}
		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
				+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

		os.write(cpclConfigLabel.getBytes());
		os.flush();

		os.close();

	}
	public void printOnlineInvoiceInOffline(HashMap<String,String> hashmap,
											ArrayList<In_Cash> OnlineInvoiceInOfflineArr, List<ProductDetails> footerArr) {
		helper.showProgressDialog(context.getString(R.string.print),
				context.getString(R.string.creating_file_for_printing));
		try {
			createOnlineInvoiceInOfflinePrint(hashmap,OnlineInvoiceInOfflineArr,footerArr);
			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();
			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (IOException e) {
			e.printStackTrace();
			helper.dismissProgressDialog();
			helper.showErrorDialog(R.string.error_creating_file_for_printing);
		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}




	private void createOnlineInvoiceInOfflinePrint(HashMap<String,String> hashmap,
												   ArrayList<In_Cash> OnlineInvoiceInOfflineArr, List<ProductDetails> footerValue)
			throws IOException {
		// TODO Auto-generated method stub

		String receiptNo = hashmap.get("ReceiptNo");
		String receiptDate = hashmap.get("ReceiptDate");
		String customerName  = hashmap.get("CustomerName");
		String paymode  = hashmap.get("PayMode");

		logoStr = "logoprint";
		footerArr.clear();
		footerArr = footerValue;

		Log.d("receipt logoStr", "rece" + logoStr);

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);

		int y = 0;
		double total = 0.00;
		StringBuilder temp = new StringBuilder();
		y = printTitle(200, y, "RECEIPT", temp);
		y = printCompanyDetails(y, temp);

		String cpclConfigLabel = temp.toString();

		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt No")
				+ text(190, y, " : ") + text(220, y, receiptNo);

		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt Date")
				+ text(190, y, " : ") + text(220, y, receiptDate);

		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Customer Name")
				+ text(190, y, " : ") + text(220, y, (customerName.length() > 10) ? customerName.substring(0, 9) : customerName);

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Invoice No");
		cpclConfigLabel += text(250, y, "Invoice Date");
		cpclConfigLabel += text(500, y, "Total");

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		for (In_Cash mIn_Cash : OnlineInvoiceInOfflineArr) {
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, mIn_Cash.getIn_InvNo().toString());
			cpclConfigLabel += text(250,y,mIn_Cash.getIn_Date());
			cpclConfigLabel += text(480, y, mIn_Cash.getAdd_paid());

			if(mIn_Cash.getAdd_paid()!=null && !mIn_Cash.getAdd_paid().isEmpty()){
				total += Double.valueOf(mIn_Cash.getAdd_paid());
			}
		}

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "PayMode")
				+ text(140, y, " : ") + text(180, y, paymode);

		cpclConfigLabel += text(310, y, "Total") + text(450, y, " : ")
				+ text(486, y, total);

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");

		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "");


		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
				"-----------------   ------------------");

		cpclConfigLabel += text(75, y += LINE_SPACING,
				"Received By");

		cpclConfigLabel += text(350, y, "Authorized By");

		Log.d("footerArr", "" + footerArr.size());

		if (footerArr.size() > 0) {

			// cpclConfigLabel += text(230, y += LINE_SPACING, "*********");

			for (ProductDetails footer : footerArr) {

				Log.d("footer value", "val " + footer.getReceiptMessage());

				if (footer.getReceiptMessage() != null
						&& !footer.getReceiptMessage().isEmpty()) {
					cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
							footer.getReceiptMessage());
				}
			}
		}

		cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
				+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;
		os.write(cpclConfigLabel.getBytes());
		os.flush();
		os.close();


	}
	/** Expense Print Start **/

	public void printExpense(String expenseno, String expensedate,
			List<ProductDetails> product, String title, int nofcopies) {
		helper.showProgressDialog(context.getString(R.string.print),
				context.getString(R.string.creating_file_for_printing));
		try {
			createExpenseFile(expenseno, expensedate, product, title, nofcopies);
			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();
			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (IOException e) {
			e.printStackTrace();
			helper.dismissProgressDialog();
			helper.showErrorDialog(R.string.error_creating_file_for_printing);
		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}

	private void createExpenseFile(String expenseno, String expensedate,
			List<ProductDetails> product, String title, int nofcopies)
			throws IOException, ZebraPrinterConnectionException {
		// Used the calculate the y axis printing position dynamically

		logoStr = "logoprint";

		Log.d("expnse logoStr", "exp" + logoStr);

		double totalqty = 0.00;
		String user = SupplierSetterGetter.getUsername();

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		for (int n = 0; n < nofcopies; n++) {
			int y = 0;
			int s = 1;

			StringBuilder temp = new StringBuilder();
			y = printTitle(228, y, "EXPENSE", temp);
			// y = printCompanyDetails(y, temp);

			if (!Company.getCompanyName().equals("")) {
				temp.append(text(LEFT_MARGIN, y += LINE_SPACING,
						Company.getCompanyName()));
			}

			String cpclConfigLabel = temp.toString();

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			if (!expenseno.matches("")) {
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						"Expense No")
						+ text(150, y, " : ")
						+ text(180, y, expenseno);
			}

			if (!expensedate.matches("")) {
				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						"Expense Date")
						+ text(150, y, " : ")
						+ text(180, y, expensedate);
			}

			if (!user.matches("")) {
				cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING, "User"));
				cpclConfigLabel += (text(150, y, " : "));
				cpclConfigLabel += (text(180, y, user));
			}

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
			cpclConfigLabel += text(70, y, "Description");
			cpclConfigLabel += text(476, y, "Amount");

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			int i = 1;
			for (ProductDetails products : product) {

				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
						String.valueOf(i));

				cpclConfigLabel += text(
						70,
						y,
						(products.getDescription().length() > 31) ? products
								.getDescription().substring(0, 30) : products
								.getDescription());

				 double dAmt = Double.valueOf(products.getNettot());

				cpclConfigLabel += text(486, y, twoDecimalPoint(dAmt));
				if (products.getNettot() != null
						&& !products.getNettot().isEmpty()) {
					totalqty += Double.valueOf(products.getNettot());
				}

				s += i;
				i++;

			}
			String tot_amt = twoDecimalPoint(totalqty);

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			cpclConfigLabel += text(300, y += LINE_SPACING, "Total Amount")
					+ text(450, y, " : ")
					+ text(486, y, String.valueOf(tot_amt));

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			// if(showTotalOutstanding.matches("True")){
			// cpclConfigLabel += horizontalLine(y += LINE_SPACING,
			// LINE_THICKNESS);
			// }

			// Just append everything and create a single string
			cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 2)) + " 1"
					+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

			os.write(cpclConfigLabel.getBytes());
			os.flush();
		}

		os.close();

	}

	public void printInvoiceDate(String flag,String user,String fromDate,String toDate,String locationCode,
			String locationName,
			ArrayList<Product> ProductListArray,String customerCode , String customerName) throws IOException {
		helper.showProgressDialog(R.string.print,
				R.string.creating_file_for_printing);
		try {

			createInvoiceDate(flag,user,fromDate,toDate,locationCode, locationName,
					ProductListArray,customerCode,customerName);

			if (isBluetoothEnabled()) {
				logoStr = "logoprint";
				print();
			} else {
				context.registerReceiver(bluetoothReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				enableBluetooth();
			}
		} catch (Exception e) {
			helper.dismissProgressDialog();
			e.printStackTrace();
		}
	}

	private void createInvoiceDate(String flag,String user,String fromDate,String toDate,String locationCode,
			String locationName,
			ArrayList<Product> InvoiceArray,String custCode,String custName) throws IOException

	/*{

		int totalqty = 0;
		double nettotal = 0.00;
		double value = 0.00,tot_qty =0.00;
		String foc = "",exchange="";


		foc =Product.getFoc();
		exchange=Product.getExchange();
		Log.d("foc&Exchange","-->"+Product.getFoc()+"  "+Product.getExchange());
//		for (Product product : InvoiceArray) {
//			try{
//				if(!foc.matches(null)||!exchange.matches(null)){
//					foc =product.getFoc().toString();
//					exchange=product.getExchange().toString();
//				}else{
//
//				}
//			}catch (NullPointerException e){
//				e.printStackTrace();
//			}
//
//		}

		mUser = user;		
		logoStr = "logoprint";
		
		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		int y = 0;
		StringBuilder temp = new StringBuilder();
		y = printTitle(LEFT_MARGIN, y, flag, temp);

		if(flag.matches("CashBill By Product") || flag.matches("CashBill Summary")){
			y = printTitle(LEFT_MARGIN, y, "", temp);
		}else {
			y = printCompanyDetails(y, temp);
		}

		String cpclConfigLabel = temp.toString();		
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "From Date")
				+ text(170, y, " : ") + text(190, y, fromDate);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "To Date")
				+ text(170, y, " : ") + text(190, y, toDate);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Location Code")
				+ text(170, y, " : ") + text(190, y, locationCode);
		System.out.println("locationName==>" + locationName);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Location Name")
				+ text(170, y, " : ") + text(190, y, locationName);
		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		
		
		if(flag.matches("Invoice By Product") || flag.matches("CashBill By Product")){
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Product Name");


			if((!foc.matches("0"))&&(!exchange.matches("0"))){
				Log.d("Nonzero","-->"+"Nonzero");
				cpclConfigLabel += text(230, y, "Carton");
				cpclConfigLabel += text(320, y, "Loose");
				cpclConfigLabel += text(390, y, "Qty");
				cpclConfigLabel += text(440, y, "Foc");
				cpclConfigLabel += text(490, y, "ExQty");

			}else if(!foc.matches("0")){
				Log.d("FocNonZero","-->"+"FocNonZero");
				cpclConfigLabel += text(290, y, "Carton");
				cpclConfigLabel += text(380, y, "Loose");
				cpclConfigLabel += text(460, y, "Qty");
				cpclConfigLabel += text(510, y, "Foc");
			}else if(!exchange.matches("0")){
				Log.d("ExchangeNonZero","-->"+"ExchangeNonZero");
				cpclConfigLabel += text(280, y, "Carton");
				cpclConfigLabel += text(370, y, "Loose");
				cpclConfigLabel += text(440, y, "Qty");
				cpclConfigLabel += text(490, y, "ExQty");
			}else{
				Log.d("Equaltozero","-->"+"Equaltozero");
				cpclConfigLabel += text(320, y, "Carton");
				cpclConfigLabel += text(410, y, "Loose");
				cpclConfigLabel += text(500, y, "Qty");
			}



			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			for (Product product : InvoiceArray) {
				cpclConfigLabel += text(
						LEFT_MARGIN,
						y+= LINE_SPACING ,
						(product.getProductName().length() > 11) ? product
								.getProductName().substring(0, 10) : product
								.getProductName());
				int count=0;
				String name =product.getProductName();
				int len =name.length();
				if(len>11) {
					int get_len = name.substring(10, len).length();
					String remark = name.substring(10, len);
					Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
					String names;

					for (int i = 0; i < get_len; i = i + 10) {
						count = count + 10;
						if (count > get_len) {
							names = remark.substring(i, get_len);
							cpclConfigLabel += text(
									LEFT_MARGIN,
									y += LINE_SPACING, names);
							Log.d("Balances", "-->" + names);

						} else {
							names = remark.substring(i, i + 10);
							cpclConfigLabel += text(
									LEFT_MARGIN,
									y += LINE_SPACING, names);
							Log.d("BalancesValues", "-->" + names);

						}
					}
				}


				if((!foc.matches("0"))&&(!exchange.matches("0"))){
					Log.d("Nonzero","-->"+"Nonzero");
					cpclConfigLabel += text(240, y, String.valueOf(product.getCqty()));
					cpclConfigLabel += text(330, y, product.getLqty().toString());
					cpclConfigLabel += text(400, y, product.getQty());
					cpclConfigLabel += text(450, y, product.getFoc().toString());
					cpclConfigLabel += text(500, y, product.getExchange().toString());

				}else if(!foc.matches("0")){
					Log.d("FocNonZero","-->"+"FocNonZero");
					cpclConfigLabel += text(300, y, String.valueOf(product.getCqty()));
					cpclConfigLabel += text(390, y, product.getLqty().toString());
					cpclConfigLabel += text(470, y,  product.getQty());
					cpclConfigLabel += text(520, y, product.getFoc().toString());
				}else if(!exchange.matches("0")){
					Log.d("ExchangeNonZero","-->"+"ExchangeNonZero");
					cpclConfigLabel += text(290, y, String.valueOf(product.getCqty()));
					cpclConfigLabel += text(380, y, product.getLqty().toString());
					cpclConfigLabel += text(450, y,  product.getQty());
					cpclConfigLabel += text(500, y, product.getExchange().toString());
				}else{
					Log.d("Equaltozero","-->"+"Equaltozero");
					cpclConfigLabel += text(330, y, String.valueOf(product.getCqty()));
					cpclConfigLabel += text(430, y, product.getLqty().toString());
					cpclConfigLabel += text(510, y,  product.getQty());
				}

				if (product.getQty() != null&& !product.getQty().isEmpty()) {
					String numberD = product.getQty().substring ( product.getQty().indexOf ( "." ) );
					value =Double.parseDouble(numberD);
					 tot_qty = Double.parseDouble(product.getQty());
					Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty) +" "+value);
			    }
			  }
			  if (value>0){
				  cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
				  String totalQty = String.valueOf(totalqty);
				  cpclConfigLabel += text(300, y += LINE_SPACING, "Total Quantity ")
						  + text(450, y, " : ")
						  + text(486, y, twoDecimalPoint(tot_qty));
			  }else{
				  cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
				  String totalQty = String.valueOf(totalqty);
				  cpclConfigLabel += text(300, y += LINE_SPACING, "Total Quantity ")
						  + text(450, y, " : ")
						  + text(486, y, String.valueOf(totalQty));
			  }

		}else if(flag.matches("Invoice Summary") || flag.matches("CashBill Summary")){
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Invoice No");
			cpclConfigLabel += text(250, y, "Customer Name");
			cpclConfigLabel += text(480, y, "Total");

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			for (Product product : InvoiceArray) {

				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, product
						.getNo().toString());

				cpclConfigLabel += text(
						250,
						y,
						(product.getName().length() > 10) ? product
								.getName().substring(0, 9) : product
								.getName());

				int count=0;
				String name =product.getName();
				int len =name.length();
				if(len>10) {
					int get_len = name.substring(9, len).length();
					String remark = name.substring(9, len);
					Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
					String names;

					for (int i = 0; i < get_len; i = i + 10) {
						count = count + 10;
						if (count > get_len) {
							names = remark.substring(i, get_len);
							cpclConfigLabel += text(
									250,
									y += LINE_SPACING, names);
							Log.d("Balances", "-->" + names);

						} else {
							names = remark.substring(i, i + 10);
							cpclConfigLabel += text(
									250,
									y += LINE_SPACING, names);
							Log.d("BalancesValues", "-->" + names);

						}
					}
				}

				cpclConfigLabel += text(460, y, product.getNetTotal());

			if (product.getNetTotal() != null&& !product.getNetTotal().isEmpty()) {
					nettotal += Double.valueOf(product.getNetTotal());
				    }
			  }

			
			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);		
			cpclConfigLabel += text(270, y += LINE_SPACING, "Total")
					+ text(420, y, " : ")
					+ text(456, y, twoDecimalPoint(nettotal));
		}			

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
				+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

		os.write(cpclConfigLabel.getBytes());
		os.flush();
		os.close();
	}*/

	{

		int totalqty = 0;
		double nettotal = 0.00;
		double value = 0.00,tot_qty =0.00;
		double cqty = 0.00;
		double lqty = 0.00;
		double focqty = 0.00;
		double eqty = 0.00;
		String foc = "",exchange="";


		foc =Product.getFoc();
		exchange=Product.getExchange();
		Log.d("foc&Exchange","-->"+Product.getFoc()+"  "+Product.getExchange());
//		for (Product product : InvoiceArray) {
//			try{
//				if(!foc.matches(null)||!exchange.matches(null)){
//					foc =product.getFoc().toString();
//					exchange=product.getExchange().toString();
//				}else{
//
//				}
//			}catch (NullPointerException e){
//				e.printStackTrace();
//			}
//
//		}

		mUser = user;
		logoStr = "logoprint";

		FileOutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		int y = 0;
		StringBuilder temp = new StringBuilder();
		y = printTitle(LEFT_MARGIN, y, flag, temp);

		if(flag.matches("CashBill By Product") || flag.matches("CashBill Summary")){
			y = printTitle(LEFT_MARGIN, y, "", temp);
		}else {
			y = printCompanyDetails(y, temp);
		}

		String cpclConfigLabel = temp.toString();
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "From Date")
				+ text(170, y, " : ") + text(190, y, fromDate);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "To Date")
				+ text(170, y, " : ") + text(190, y, toDate);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Location Code")
				+ text(170, y, " : ") + text(190, y, locationCode);
		System.out.println("locationName==>" + locationName);
		cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Location Name")
				+ text(170, y, " : ") + text(190, y, locationName);

		Log.d("customerCodeCheck",custCode);

		if(!custCode.matches("")){
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Customer Code")
					+ text(170, y, " : ") + text(190, y, custCode);

			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "CustomerName")
					+ text(170, y, " : ") + text(190, y, custName);
		}

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);


		if(flag.matches("Invoice By Product") || flag.matches("CashBill By Product")){
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Product Name");


			if((!foc.matches("0"))&&(!exchange.matches("0"))){
				Log.d("Nonzero","-->"+"Nonzero");
				cpclConfigLabel += text(330, y, "Ctn");
				cpclConfigLabel += text(430, y, "LQty");
				cpclConfigLabel += text(510, y, "Qty");
				/*cpclConfigLabel += text(430, y, "Foc");
				cpclConfigLabel += text(500, y, "Ex");*/

			}else if(!foc.matches("0")){
				Log.d("FocNonZero","-->"+"FocNonZero");
				/*cpclConfigLabel += text(290, y, "Ctn");
				cpclConfigLabel += text(380, y, "LQty");
				cpclConfigLabel += text(460, y, "Qty");
				cpclConfigLabel += text(510, y, "Foc");*/

				cpclConfigLabel += text(330, y, "Ctn");
				cpclConfigLabel += text(430, y, "LQty");
				cpclConfigLabel += text(510, y, "Qty");
				/*cpclConfigLabel += text(430, y, "Foc");
				cpclConfigLabel += text(500, y, "Ex");*/

			}else if(!exchange.matches("0")){
				Log.d("ExchangeNonZero","-->"+"ExchangeNonZero");
				/*cpclConfigLabel += text(280, y, "Ctn");
				cpclConfigLabel += text(370, y, "LQty");
				cpclConfigLabel += text(440, y, "Qty");
				cpclConfigLabel += text(490, y, "Ex");*/

				cpclConfigLabel += text(330, y, "Ctn");
				cpclConfigLabel += text(430, y, "LQty");
				cpclConfigLabel += text(510, y, "Qty");
				/*cpclConfigLabel += text(430, y, "Foc");
				cpclConfigLabel += text(500, y, "Ex");*/

			}else{
				Log.d("Equaltozero","-->"+"Equaltozero");
				/*cpclConfigLabel += text(320, y, "Ctn");
				cpclConfigLabel += text(410, y, "LQty");
				cpclConfigLabel += text(500, y, "Qty");*/

				cpclConfigLabel += text(330, y, "Ctn");
				cpclConfigLabel += text(430, y, "LQty");
				cpclConfigLabel += text(510, y, "Qty");
				/*cpclConfigLabel += text(430, y, "Foc");
				cpclConfigLabel += text(500, y, "Ex");*/
			}

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);

			for (Product product : InvoiceArray) {
				cpclConfigLabel += text(
						LEFT_MARGIN,
						y+= LINE_SPACING ,
						(product.getProductName().length() > 15) ? product
								.getProductName().substring(0, 14) : product
								.getProductName());
				int count=0;
				String name = product.getProductName();
				int len =name.length();
				if(len>15) {
					int get_len = name.substring(14, len).length();
					String remark = name.substring(14, len);
					Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
					String names;

					for (int i = 0; i < get_len; i = i + 14) {
						count = count + 14;
						if (count > get_len) {
							names = remark.substring(i, get_len);
							cpclConfigLabel += text(
									LEFT_MARGIN,
									y += LINE_SPACING, names);
							Log.d("Balances", "-->" + names);

						} else {
							names = remark.substring(i, i + 14);
							cpclConfigLabel += text(
									LEFT_MARGIN,
									y += LINE_SPACING, names);
							Log.d("BalancesValues", "-->" + names);

						}
					}
				}


				if((!foc.matches("0"))&&(!exchange.matches("0"))){
					Log.d("Nonzero","-->"+"Nonzero");
					cpclConfigLabel += text(330, y, String.valueOf(product.getCqty()));
					cpclConfigLabel += text(430, y, product.getLqty().toString());
					cpclConfigLabel += text(510, y, product.getQty());
					/*cpclConfigLabel += text(430, y, product.getFoc().toString());
					cpclConfigLabel += text(500, y, product.getExchange().toString());*/

				}else if(!foc.matches("0")){
					Log.d("FocNonZero","-->"+"FocNonZero");
					/*cpclConfigLabel += text(300, y, String.valueOf(product.getCqty()));
					cpclConfigLabel += text(390, y, product.getLqty().toString());
					cpclConfigLabel += text(470, y,  product.getQty());
					cpclConfigLabel += text(520, y, product.getFoc().toString());*/

					cpclConfigLabel += text(330, y, String.valueOf(product.getCqty()));
					cpclConfigLabel += text(430, y, product.getLqty().toString());
					cpclConfigLabel += text(510, y, product.getQty());
					/*cpclConfigLabel += text(430, y, product.getFoc().toString());
					cpclConfigLabel += text(500, y, product.getExchange().toString());*/

				}else if(!exchange.matches("0")){
					Log.d("ExchangeNonZero","-->"+"ExchangeNonZero");
					/*cpclConfigLabel += text(290, y, String.valueOf(product.getCqty()));
					cpclConfigLabel += text(380, y, product.getLqty().toString());
					cpclConfigLabel += text(450, y,  product.getQty());
					cpclConfigLabel += text(500, y, product.getExchange().toString());*/

					cpclConfigLabel += text(330, y, String.valueOf(product.getCqty()));
					cpclConfigLabel += text(430, y, product.getLqty().toString());
					cpclConfigLabel += text(510, y, product.getQty());
					/*cpclConfigLabel += text(430, y, product.getFoc().toString());
					cpclConfigLabel += text(500, y, product.getExchange().toString());*/

				}else{
					Log.d("Equaltozero","-->"+"Equaltozero");
					/*cpclConfigLabel += text(330, y, String.valueOf(product.getCqty()));
					cpclConfigLabel += text(430, y, product.getLqty().toString());
					cpclConfigLabel += text(510, y,  product.getQty());*/

					cpclConfigLabel += text(330, y, String.valueOf(product.getCqty()));
					cpclConfigLabel += text(430, y, product.getLqty().toString());
					cpclConfigLabel += text(510, y, product.getQty());/*cpclConfigLabel += text(430, y, product.getFoc().toString());
					cpclConfigLabel += text(500, y, product.getExchange().toString());*/

				}

				if (product.getQty() != null&& !product.getQty().isEmpty()) {
					//String numberD = product.getQty().substring (product.getQty().indexOf( "." ));
					String numberD = product.getQty();
					value =Double.parseDouble(numberD);

					/*double cqty = 0.00;
					double lqty = 0.00;
					double focqty = 0.00;
					double eqty = 0.00;*/

					tot_qty+= Double.parseDouble(product.getQty());
					cqty+= Double.parseDouble(product.getCqty());
					lqty+= Double.parseDouble(product.getLqty());
					focqty+= Double.parseDouble(product.getFoc().toString());
					eqty+= Double.parseDouble(product.getExchange().toString());

					Log.e("Quantities", tot_qty +", "+ cqty +", "+ lqty +", "+ focqty +", "+ eqty);

					Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty) +" "+value);
				}
			}
			if (value>0){
				  /*cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
				  String totalQty = String.valueOf(totalqty);
				  cpclConfigLabel += text(300, y += LINE_SPACING, "Total Quantity ")
						  + text(450, y, " : ")
						  + text(486, y, twoDecimalPoint(tot_qty));*/

				cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
				String totalQty = String.valueOf(totalqty);
				cpclConfigLabel += text(10, y += LINE_SPACING, "Total ")
						+ text(200, y, " : ")
						+ text(330, y, DecimalPoint(cqty))
						+ text(430, y, DecimalPoint(lqty))
						+ text(510, y, DecimalPoint(tot_qty));
						  /*+ text(430, y, DecimalPoint(focqty))
						  + text(500, y, DecimalPoint(eqty));*/
			}else{
				  /*cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
				  String totalQty = String.valueOf(totalqty);
				  cpclConfigLabel += text(300, y += LINE_SPACING, "Total Quantity ")
						  + text(450, y, " : ")
						  + text(486, y, String.valueOf(totalQty));*/

				cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
				String totalQty = String.valueOf(totalqty);
				cpclConfigLabel += text(10, y += LINE_SPACING, "Total ")
						+ text(200, y, " : ")
						+ text(330, y, DecimalPoint(cqty))
						+ text(430, y, DecimalPoint(lqty))
						+ text(510, y, DecimalPoint(tot_qty));
						  /*+ text(430, y, DecimalPoint(focqty))
						  + text(500, y, DecimalPoint(eqty));*/
			}

		}else if(flag.matches("Invoice Summary") || flag.matches("CashBill Summary")){
			cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Invoice No");
			cpclConfigLabel += text(220, y, "Customer Name");
			cpclConfigLabel += text(480, y, "Total");

			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			for (Product product : InvoiceArray) {

				cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, product
						.getNo().toString());

				cpclConfigLabel += text(
						220,
						y,
						(product.getName().length() > 15) ? product
								.getName().substring(0, 14) : product
								.getName());

				int count=0;
				String name =product.getName();
				int len =name.length();
				if(len>15) {
					int get_len = name.substring(14, len).length();
					String remark = name.substring(14, len);
					Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
					String names;

					for (int i = 0; i < get_len; i = i + 15) {
						count = count + 15;
						if (count > get_len) {
							names = remark.substring(i, get_len);
							cpclConfigLabel += text(
									250,
									y += LINE_SPACING, names);
							Log.d("Balances", "-->" + names);

						} else {
							names = remark.substring(i, i + 15);
							cpclConfigLabel += text(
									250,
									y += LINE_SPACING, names);
							Log.d("BalancesValues", "-->" + names);

						}
					}
				}

				cpclConfigLabel += text(460, y, product.getNetTotal());

				if (product.getNetTotal() != null&& !product.getNetTotal().isEmpty()) {
					nettotal += Double.valueOf(product.getNetTotal());
				}
			}


			cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
			cpclConfigLabel += text(270, y += LINE_SPACING, "Total")
					+ text(420, y, " : ")
					+ text(456, y, twoDecimalPoint(nettotal));
		}

		cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
		cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
				+ LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

		os.write(cpclConfigLabel.getBytes());
		os.flush();
		os.close();
	}


	public static String DecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}


	public static String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}
	public static String decimalPoint(double d) {
		  DecimalFormat df = new DecimalFormat("###.#");  
		  String tot = df.format(d);

		  return tot;
		 }
	/** Expense Print End **/

}
