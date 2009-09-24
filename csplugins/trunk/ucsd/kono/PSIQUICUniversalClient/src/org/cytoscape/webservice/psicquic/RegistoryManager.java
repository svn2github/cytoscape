package org.cytoscape.webservice.psicquic;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Simple REST client for PSICQUIC registory service.
 * 
 * @author kono
 * 
 */
public class RegistoryManager {

	private static final String SERVICE_URL = "http://www.ebi.ac.uk/intact/psicquic-registry/registry";

	public RegistoryManager() {

	}

	public void invoke() throws IOException, Exception {
		String command = "?action=STATUS&format=xml";
		URL url = new URL(SERVICE_URL + command);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoInput(true);
		connection.setDoOutput(true);

		connection.setRequestProperty("accept", "text/xml");
		connection.connect();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		StringBuilder builder = new StringBuilder();

		String next;
		while ((next = reader.readLine()) != null) {
			builder.append(next);
		}

		parse(builder.toString());
		System.out.println("==== Registory get success!! ===============\n\n"
				+ builder.toString());

	}

	private void parse(String result) throws Exception {

		System.out.println("@@@@@ " + result);

		byte[] bytes = result.getBytes();

		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
		Document doc = docbuilder.parse(new ByteArrayInputStream(result
				.getBytes("UTF-8")));
		
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xp = xpf.newXPath();
		NodeList l2 = (NodeList) xp.evaluate("//service", doc, XPathConstants.NODESET);
		for(int i=0; i<l2.getLength(); i++) {
			System.out.println("############# Service Provider: " + l2.item(i).getFirstChild().getFirstChild());
			
		}
		
		System.out.println("#L2 = " + l2);

		NodeList list = doc.getElementsByTagName("service");

		for(int i=0; i<list.getLength(); i++) {
			System.out.println("############# Service Provider: " + (i+1));
			walk(list.item(i));
		}
		
		

	}
	
	private void walk(Node item) {
		
	
			
			for(Node n = item.getFirstChild(); n!=null; n = n.getNextSibling()) {
				System.out.println("Entry = "
						+ item.getFirstChild().getNodeName() + ", "
						+ item.getFirstChild().getNodeType() + ", "
						+ item.getFirstChild().getNodeValue());
				
				walk(n);
			}
		
		
	}
}
