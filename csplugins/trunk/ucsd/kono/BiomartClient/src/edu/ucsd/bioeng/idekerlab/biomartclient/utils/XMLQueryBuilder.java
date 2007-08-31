package edu.ucsd.bioeng.idekerlab.biomartclient.utils;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLQueryBuilder {
	private static DocumentBuilderFactory factory;
	private static DocumentBuilder builder;
	
	static {
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getQueryString(Dataset dataset, Attribute[] attrs, Filter[] filters) {
		final Document doc = builder.newDocument();
		Element query = doc.createElement("Query");
		query.setAttribute("virtualSchemaName", "default");
		query.setAttribute("header", "1");
		query.setAttribute("uniqueRows", "0");
		query.setAttribute("count", "");
		query.setAttribute("datasetConfigVersion", "0.6");
		query.setAttribute("formatter", "TSV");
		
		doc.appendChild(query);
		
		Element ds = doc.createElement("Dataset");
		ds.setAttribute("name", dataset.getName());
		query.appendChild(ds);
		
		for(Attribute attr: attrs) {
			Element at = doc.createElement("Attribute");
			at.setAttribute("name", attr.getName());
			ds.appendChild(at);
		}
		
		for(Filter filter: filters) {
			Element ft = doc.createElement("Filter");
			ft.setAttribute("name", filter.getName());
			ft.setAttribute("value", filter.getValue());
			ds.appendChild(ft);
		}
		
		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf;
		String result = null;
		try {
			tf = tff.newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			StringWriter strWtr = new StringWriter();
			StreamResult strResult = new StreamResult(strWtr);
			
			
			tf.transform(new DOMSource(doc.getDocumentElement()), strResult);
			
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
