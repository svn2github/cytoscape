package org.cytoscape.webservice.ncbi.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EntrezRestClient {

	private static final Logger logger = LoggerFactory.getLogger(EntrezRestClient.class);
	
	private static final String BASE_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
	static final String FETCH_URL = BASE_URL + "efetch.fcgi?db=gene&retmode=xml&id=";
	private static final String SEARCH_URL = BASE_URL + "esearch.fcgi?db=gene&retmax=100000&term=";


	
	
	private final String regex = "\\s+";
	
	private static final String ID = "Id";
	
	private final CyNetworkFactory networkFactory;

	
	
	public EntrezRestClient(final CyNetworkFactory networkFactory) {
		this.networkFactory = networkFactory;
	}
	
	
	public Set<String> search(final String queryString) throws IOException, ParserConfigurationException, SAXException {
		final URL url = createURL(SEARCH_URL, queryString);
		
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream is = url.openStream();

		final Document result = builder.parse(is);

		final Set<String> idSet = new HashSet<String>();
		final NodeList ids = result.getElementsByTagName(ID);
		final int dataSize = ids.getLength();

		for (int i = 0; i < dataSize; i++) {
			Node id = ids.item(i);
			idSet.add(id.getTextContent());
		}

		is.close();
		is = null;

		return idSet;
	}
	
	public CyNetwork importNetwork(final Set<String> idList) {
		
		long startTime = System.currentTimeMillis();

		final ExecutorService executer = Executors.newFixedThreadPool(4);

		System.out.println("Thread Pool Initialized.");
		final CyNetwork newNetwork = networkFactory.getInstance();

		int group = 0;
		int buketNum = 10;
		String[] box = new String[buketNum];
		
		
		for (String entrezID : idList) {
			box[group] = entrezID;
			group++;

			if (group == buketNum) {
				executer.submit(new EntryProcessor<String>(new ImportNetworkTask<String>(box, newNetwork)));
				group = 0;
				box = new String[buketNum];
			}
		}
		
		String[] newbox = new String[group];

		for (int i = 0; i < group; i++)
			newbox[i] = box[i];

		executer.submit(new EntryProcessor<String>(new ImportNetworkTask<String>(box, newNetwork)));

		try {
			executer.shutdown();
			executer.awaitTermination(1000, TimeUnit.SECONDS);

			long endTime = System.currentTimeMillis();
			double sec = (endTime - startTime) / (1000.0);
			System.out.println("Finished in " + sec + " sec.");

//			if ((canceled != null) && canceled) {
//				canceled = null;
//
//				return null;
//			}
		} catch( Exception ex) {
			ex.printStackTrace();
		}

		return newNetwork;
	}
	
	private void walk(Node node, final String targetTag, final String targetVal) {
		for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				if(child.getNodeName().equals(targetTag)) {
					logger.debug(targetTag + " Tag = " + child.getTextContent());
					//this.resultMap.put(targetVal, child.getTextContent());
					break;
				} else
					walk(child, targetTag, targetVal);
			}
		}
	}
	
	
	private URL createURL(final String base, final String queryString) throws IOException {
		
		final String[] parts = queryString.split(regex);
		final StringBuilder builder = new StringBuilder();

		if (parts.length != 0) {
			for (String dTerm : parts) {
				final String trimed = dTerm.trim();
				builder.append(trimed + "+");
			}
		}
		
		String urlString = builder.toString();
		urlString = urlString.substring(0, urlString.length() - 1);
		final URL url = new URL(base + urlString);
		logger.debug("Query URL = " + url.toString());
		return url;
	}
}
