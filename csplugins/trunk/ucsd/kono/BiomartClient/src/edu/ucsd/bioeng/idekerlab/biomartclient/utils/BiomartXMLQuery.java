package edu.ucsd.bioeng.idekerlab.biomartclient.utils;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.sun.org.apache.xerces.internal.impl.xs.opti.DefaultDocument;

public class BiomartXMLQuery extends DefaultDocument {

	private enum Format {
		XSL, HTML, TSV;
	}
	
	
	private static final String ENCODING = "UTF-8";
	
	private String virtualSchemaName;
	private String formatter;
	
	
	public String getQueryString() {
		final TransformerFactory tff = TransformerFactory.newInstance();
		final Transformer tf;
		
		String result = null;
		try {
			tf = tff.newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, ENCODING);

			StringWriter strWtr = new StringWriter();
			StreamResult strResult = new StreamResult(strWtr);
			
			
			tf.transform(new DOMSource(this.getDocumentElement()), strResult);
			
			result = strResult.getWriter().toString();
		
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}
