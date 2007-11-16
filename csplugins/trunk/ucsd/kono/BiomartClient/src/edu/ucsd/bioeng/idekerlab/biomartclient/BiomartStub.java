
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 *
 */
public class BiomartStub {
	private String baseURL = "http://www.biomart.org/biomart/martservice?";
	private URL url;
	private URLConnection uc;

	/**
	 * Creates a new BiomartStub object.
	 */
	public BiomartStub() {
	}

	/**
	 * Creates a new BiomartStub object from given URL.
	 *
	 * @param baseURL  DOCUMENT ME!
	 */
	public BiomartStub(String baseURL) {
		this.baseURL = baseURL;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public List<String> getDatabaseList() throws Exception {
		List<String> databases = new ArrayList<String>();

		final String reg = "type=registry";
		final URL targetURL = new URL(baseURL + reg);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		InputStream is = targetURL.openStream();

		Document doc = builder.parse(is);

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;

		Element root = doc.getDocumentElement();

		for (Node nd = root.getFirstChild(); nd != null; nd = nd.getNextSibling()) {
			if (nd.getNodeType() == Node.ELEMENT_NODE) {
				System.out.println("--------------------------------");
				System.out.println("############# New Reply from Biomart: " + nd.getNodeName());

				NamedNodeMap attrs = nd.getAttributes();

				for (int i = 0; i < attrs.getLength(); i++) {
					System.out.println("---> " + attrs.item(i).getNodeName() + " = "
					                   + attrs.item(i).getNodeValue());
				}

				System.out.println("--------------------------------");
			}
		}

		reader.close();

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
	public List<String> getAvailableDatasets(final String martName) throws Exception {
		List<String> databases = new ArrayList<String>();

		final String reg = "type=datasets&mart=" + martName;
		final URL targetURL = new URL(baseURL + reg);

		InputStream is = targetURL.openStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;

		String[] parts;

		while ((s = reader.readLine()) != null) {
			parts = s.split("\\t");

			if (parts.length > 1) {
				databases.add(parts[1]);
			}
		}

		reader.close();

		return databases;
	}

	private List buildList(BufferedReader reader) throws IOException {
		List result = new ArrayList();

		String s;

		while ((s = reader.readLine()) != null) {
			String[] parts = s.split("\\t");

			for (String p : parts) {
				System.out.println("Reply: " + p);
				result.add(p);
			}
		}

		reader.close();

		return result;
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
