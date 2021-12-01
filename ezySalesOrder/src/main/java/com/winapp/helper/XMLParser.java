package com.winapp.helper;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.StrictMode;
import android.util.Log;

public class XMLParser {

	// constructor
	public XMLParser() {

	}

	/**
	 * Getting XML from URL making HTTP request
	 * 
	 * @param url
	 *            string
	 * 
	 * */
/*	public static String getXmlFromUrl(String url) {
		
		  if (android.os.Build.VERSION.SDK_INT > 9) {
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		    }		
		
		String xml = null;
		HttpGet request = new HttpGet(url);
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");
		try {
			HttpResponse httpResponse = httpClient.execute(request);
			HttpEntity httpEntity = httpResponse.getEntity();
//			System.out.println("entity:" + httpEntity);
			if (httpResponse.getEntity() != null) {
			      xml = EntityUtils.toString(httpEntity);
			    }else{
			      xml ="";
			    }
			
//			System.out.println("limt" + xml);

		} catch (UnsupportedEncodingException e) {
			xml = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
		} catch (MalformedURLException e) {
			xml = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
		} catch (IOException e) {
			xml = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
		} catch (OutOfMemoryError e) {
			xml = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
		} finally {
			httpClient.close();
		}
		// return XML
		return xml;
	}*/


	public static Document getDomElement(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}

		return doc;
	}

	/**
	 * Getting node value
	 * 
	 * @param elem
	 *            element
	 */
	public final static String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}


	public static String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return XMLParser.getElementValue(n.item(0));
	}
}
