/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package edu.ucsd.bioeng.idekerlab.biomartclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;


/**
 *
 */
public class BiomartStub {
	private String baseURL = "http://www.biomart.org/biomart/martservice?";
	private URL url;
	private URLConnection uc;
	private Map<String, Map<String, String>> databases = null;

	// Key is datasource, value is database name.
	private Map<String, String> datasourceMap = new HashMap<String, String>();

	
	private Map<String, Map<String, String>> filterConversionMap;
	/**
	 * Creates a new BiomartStub object.
	 * @throws IOException 
	 */
	public BiomartStub() throws IOException {
		this(null);
	}

	/**
	 * Creates a new BiomartStub object from given URL.
	 *
	 * @param baseURL  DOCUMENT ME!
	 * @throws IOException 
	 */
	public BiomartStub(String baseURL) throws IOException {
		if(baseURL != null) {
			this.baseURL = baseURL + "?";
		}
		loadConversionFile();
	}

	private void loadConversionFile() throws IOException {
		filterConversionMap = new HashMap<String, Map<String, String>>();
		InputStreamReader inFile;

		inFile = new InputStreamReader(this.getClass().getResource("/resource/filterconversion.txt")
		                                   .openStream());

		BufferedReader inBuffer = new BufferedReader(inFile);

		String line;
		String trimed;
		String oldName = null;
		Map<String, String> oneEntry = new HashMap<String, String>();
		while ((line = inBuffer.readLine()) != null) {
			trimed = line.trim();
			System.out.println("Filter Conversion-------------> " + trimed);
			String[] dbparts = trimed.split("\\t");
			if(dbparts[0].equals(oldName) == false) {
				oneEntry = new HashMap<String, String>();
				oldName = dbparts[0];
				filterConversionMap.put(oldName, oneEntry);
			}
			oneEntry.put(dbparts[1], dbparts[2]);
			
		}
	}
	
	public String toAttributeName(String dbName, String filterID) {
		if(filterConversionMap.get(dbName) == null) {
			return null;
		} else {
			return filterConversionMap.get(dbName).get(filterID);
		}
	}
	/**
	 *  DOCUMENT ME!
	 *
	 * @param baseURL DOCUMENT ME!
	 */
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL + "?";
	}

	/**
	 *  Get the registry information from the base URL.
	 *
	 * @return  Map of registry information.  Key value is "name" field.
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public Map<String, Map<String, String>> getRegistry() throws Exception {
		// If already loaded, just return it.
		if (databases != null)
			return databases;

		// Initialize database map.
		databases = new HashMap<String, Map<String, String>>();

		// Prepare URL for the registry status
		final String reg = "type=registry";
		final URL targetURL = new URL(baseURL + reg);

		// Get the result as XML document.
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream is = targetURL.openStream();
		final Document registry = builder.parse(is);

		// Extract each datasource
		NodeList locations = registry.getElementsByTagName("MartURLLocation");
		int locSize = locations.getLength();
		NamedNodeMap attrList;
		int attrLen;
		String dbID;

		for (int i = 0; i < locSize; i++) {
			attrList = locations.item(i).getAttributes();
			attrLen = attrList.getLength();

			// First, get the key value
			dbID = attrList.getNamedItem("name").getNodeValue();

			Map<String, String> entry = new HashMap<String, String>();

			for (int j = 0; j < attrLen; j++) {
				entry.put(attrList.item(j).getNodeName(), attrList.item(j).getNodeValue());
			}

			databases.put(dbID, entry);
		}

		is.close();
		is = null;

		return databases;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param martName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public Map<String, String> getAvailableDatasets(final String martName)
	    throws Exception {
		final Map<String, String> datasources = new HashMap<String, String>();

		Map<String, String> detail = databases.get(martName);

		String urlStr = "http://" + detail.get("host") + ":" + detail.get("port")
		                + detail.get("path") + "?type=datasets&mart=" + detail.get("name");
		System.out.println("DB name = " + martName + ", Target URL = " + urlStr + "\n");

		URL url = new URL(urlStr);
		URLConnection uc = url.openConnection();
		InputStream is = uc.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;

		while ((s = reader.readLine()) != null) {
			String[] parts = s.split("\\t");

			if ((parts.length > 4) && parts[3].equals("1")) {
				datasources.put(parts[1], parts[2]);
				datasourceMap.put(parts[1], martName);
			}
		}

		is.close();
		reader.close();
		reader = null;
		is = null;

		return datasources;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param datasetName DOCUMENT ME!
	 * @param getAll DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public Map<String, String> getFilters(String datasetName, boolean getAll)
	    throws IOException {
		Map<String, String> filters = new HashMap<String, String>();

		String martName = datasourceMap.get(datasetName);
		Map<String, String> detail = databases.get(martName);

		String urlStr = "http://" + detail.get("host") + ":" + detail.get("port")
		                + detail.get("path") + "?virtualschema="
		                + detail.get("serverVirtualSchema") + "&type=filters&dataset="
		                + datasetName;
		System.out.println("Dataset name = " + datasetName + ", Target URL = " + urlStr + "\n");

		URL url = new URL(urlStr);
		URLConnection uc = url.openConnection();
		InputStream is = uc.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;

		while ((s = reader.readLine()) != null) {
			String[] parts = s.split("\\t");

			if ((parts.length > 1)) {
				if (getAll) {
					filters.put(parts[1], parts[0]);
				} else if ((parts[1].contains("ID(s)") || parts[1].contains("Accession(s)")
				           || parts[1].contains("IDs")) && (parts[0].startsWith("with_") == false)) {
					filters.put(parts[1], parts[0]);
					System.out.println("### Filter Entry = " + parts[1] + " = " + parts[0]);
				}
			}
		}

		is.close();
		reader.close();
		reader = null;
		is = null;

		return filters;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param datasetName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public Map<String, String[]> getAttributes(String datasetName) throws Exception {
		Map<String, String[]> attributes = new HashMap<String, String[]>();

		String martName = datasourceMap.get(datasetName);
		Map<String, String> detail = databases.get(martName);

		String urlStr = "http://" + detail.get("host") + ":" + detail.get("port")
		                + detail.get("path") + "?virtualschema="
		                + detail.get("serverVirtualSchema") + "&type=attributes&dataset="
		                + datasetName;
		System.out.println("Dataset name = " + datasetName + ", Target URL = " + urlStr + "\n");

		URL url = new URL(urlStr);
		URLConnection uc = url.openConnection();
		InputStream is = uc.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;

		String[] attrInfo;

		while ((s = reader.readLine()) != null) {
			String[] parts = s.split("\\t");
			attrInfo = new String[3];

			if (parts.length == 0)
				continue;

			if (parts.length == 4) {
				// Display name of this attribute.
				attrInfo[0] = parts[1];
				attrInfo[1] = parts[2];
				attrInfo[2] = parts[3];
			} else if (parts.length > 1) {
				attrInfo[0] = parts[1];
			}

			attributes.put(parts[0], attrInfo);

			for (String e : parts) {
				System.out.print("(" + e + "), ");
			}

			for (String e : attrInfo) {
				System.out.print("[" + e + "], ");
			}

			System.out.println("");
		}

		is.close();
		reader.close();
		reader = null;
		is = null;

		return attributes;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param datasetName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public String getAvailableAttributes(final String datasetName) throws Exception {
		List<String> databases = new ArrayList<String>();

		final String reg = "type=attributes&dataset=" + datasetName;
		final URL targetURL = new URL(baseURL + reg);

		InputStream is = targetURL.openStream();

		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;

		while ((s = reader.readLine()) != null) {
			System.out.println("--------------------------------");

			String[] parts = s.split("\\t");

			for (String p : parts) {
				System.out.println("Attr: " + p);
			}

			builder.append(s);
			System.out.println("--------------------------------");
		}

		reader.close();

		return builder.toString();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param xmlQuery DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public List<String[]> sendQuery(String xmlQuery) throws Exception {
		try {
			url = new URL(baseURL);

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
