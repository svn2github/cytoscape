package org.cytoscape.webservice.psicquic;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Simple REST client for PSICQUIC registory service.
 * 
 * @author kono
 * 
 */
public class RegistryManager {
	
	// Tag definitions
	private static final String SOAP_URL = "soapUrl";
	private static final String SERVICE_URL = "http://www.ebi.ac.uk/intact/psicquic-registry/registry";

	private final Map<String, String> regMap;

	public RegistryManager() throws IOException {
		regMap = new HashMap<String, String>();
		invoke();
	}

	public Map<String, String> getRegistry() {
		return regMap;
	}

	private void invoke() throws IOException {
		String command = "?action=ACTIVE&format=xml";

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

		try {
			parse(builder.toString());
		} catch (ParserConfigurationException e) {
			
			e.printStackTrace();
			throw new IOException("Could not parse message from registry.");
		} catch (XPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException("Could not parse message from registry.");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException("Could not parse message from registry.");
		}
		System.out.println("==== Registory get success!! ===============\n\n"
				+ builder.toString());

	}

	private void parse(String result) throws ParserConfigurationException, IOException, XPathException, SAXException {

		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
		Document doc = docbuilder.parse(new ByteArrayInputStream(result
				.getBytes("UTF-8")));

		XPathFactory xpf = XPathFactory.newInstance();
		XPath xp = xpf.newXPath();

		// Extract all service entries.
		NodeList list = (NodeList) xp.evaluate("//service", doc,
				XPathConstants.NODESET);

		String regName = null;
		for (int i = 0; i < list.getLength(); i++) {
			regName = list.item(i).getFirstChild().getFirstChild()
					.getNodeValue();
			System.out.println("Service Provider " + i  + ": " + regName);
			walk(list.item(i), regName);
		}

	}

	private void walk(Node item, String serviceName) {

			String tag = null;
			for(Node n = item.getFirstChild(); n!=null; n = n.getNextSibling()) {
				tag = item.getNodeName();
				if(tag.equals(SOAP_URL)) {
					System.out.println("SOAP = "
							+ item.getFirstChild().getNodeValue());
					regMap.put(serviceName, item.getFirstChild().getNodeValue());
					return;
				}
				walk(n, serviceName);
			}	
	}
}
