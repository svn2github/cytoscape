package edu.ucsd.bioeng.idekerlab.biomartclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class BiomartStub {
	
	private static final String BASE_URL = "http://www.biomart.org/biomart/martservice?";
	private URL url;
	private URLConnection uc;
	
	public BiomartStub() {
		
		
	}
	
	public List<String> getDatabaseList() throws Exception {
		
		List<String> databases = new ArrayList<String>();
		
		final String reg = "type=registry";
		final URL targetURL = new URL(BASE_URL+reg);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		InputStream is = targetURL.openStream();

		Document doc = builder.parse(is);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;
		
		Element root = doc.getDocumentElement();

		
		for(Node nd = root.getFirstChild(); nd != null; nd = nd.getNextSibling()) {
			
			if(nd.getNodeType() == Node.ELEMENT_NODE) {
				
			
				System.out.println("--------------------------------");
				System.out.println("Reply from Biomart: " + nd.getNodeName());
				NamedNodeMap attrs = nd.getAttributes();
				for(int i=0; i<attrs.getLength(); i++) {
					System.out.println("---> " + attrs.item(i).getNodeName() + " = " + attrs.item(i).getNodeValue());
				}
			System.out.println("--------------------------------");
			}
		}

		reader.close();
		
		return databases;
	}
	
	public String getAvailableDatasets(final String martName) throws Exception {
		List<String> databases = new ArrayList<String>();
		
		final String reg = "type=datasets&mart=" + martName;
		final URL targetURL = new URL(BASE_URL+reg);
		
		
		InputStream is = targetURL.openStream();

		
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;
		
		while ((s = reader.readLine()) != null) {
			System.out.println("--------------------------------");
			String[] parts = s.split("\\t");
			for(String p: parts) {
				System.out.println("Attr: " + p);
			}
			builder.append(s);
			System.out.println("--------------------------------");
		}

		reader.close();
		
		
		return builder.toString();
	}
	
	private List buildList(BufferedReader reader) throws IOException {
		List result = new ArrayList();
		
		String s;
		
		while ((s = reader.readLine()) != null) {
			String[] parts = s.split("\\t");
			for(String p: parts) {
				System.out.println("Reply: " + p);
				result.add(p);
			}
		}

		reader.close();
		return result;
		
	}
	
	
	public String getAvailableAttributes(final String datasetName) throws Exception {
		List<String> databases = new ArrayList<String>();
		
		final String reg = "type=attributes&dataset=" + datasetName;
		final URL targetURL = new URL(BASE_URL+reg);
		
		
		InputStream is = targetURL.openStream();

		
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;
		
		while ((s = reader.readLine()) != null) {
			System.out.println("--------------------------------");
			String[] parts = s.split("\\t");
			for(String p: parts) {
				System.out.println("Attr: " + p);
			}
			builder.append(s);
			System.out.println("--------------------------------");
		}

		reader.close();
		
		
		return builder.toString();
	}
	
	

	public List<String[]> sendQuery(String xmlQuery) throws Exception {
		
		try {
			url = new URL(BASE_URL);
		
			uc = url.openConnection();
			uc.setDoOutput(true);

			uc.setRequestProperty("User-Agent", "Java URLConnection");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 // ヘッダを設定

		OutputStream os = uc.getOutputStream(); //POST用のOutputStreamを取得

		String postStr = "query=" + xmlQuery; //POSTするデータ
		PrintStream ps = new PrintStream(os);
		ps.print(postStr); //データをPOSTする
		ps.close();

		InputStream is = uc.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;
		
		StringBuilder builder = new StringBuilder();

		
		s = reader.readLine();
		System.out.println("Header: " + s);
		String[] parts = s.split("\\t");
		final List<String[]> result = new ArrayList<String[]>();
		result.add(parts);
		
		while ((s = reader.readLine()) != null) {
			//System.out.println("--------------------------------");
			parts = s.split("\\t");
//			for(String p: parts) {
//				//System.out.println("Reply from Biomart: " + p);
//			}
			
			result.add(parts);
			//System.out.println("--------------------------------");
		}

		reader.close();
	
		return result;
	}
}
