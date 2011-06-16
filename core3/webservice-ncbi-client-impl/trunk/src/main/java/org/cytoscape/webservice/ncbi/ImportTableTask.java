package org.cytoscape.webservice.ncbi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.webservice.ncbi.rest.EntrezRestClient;
import org.cytoscape.webservice.ncbi.ui.AnnotationCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImportTableTask implements Callable<String> {

	private static final Logger logger = LoggerFactory.getLogger(ImportTableTask.class);

	private String ENTRY_KEY = "Entrezgene";

	private final String[] ids;
	private final CyTable table;

	private Map<String, String> valueMap;

	private static final String GENE_ID_TAG = "Gene-track_geneid";

	final Set<AnnotationCategory> category;

	public ImportTableTask(final String[] ids, final Set<AnnotationCategory> category, final CyTable table) {
		this.ids = ids;
		this.table = table;
		this.category = category;
	}

	@Override
	public String call() throws Exception {

		final URL url = createURL();

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream is = url.openStream();

		final Document result = builder.parse(is);
		final NodeList entries = result.getElementsByTagName(ENTRY_KEY);
		logger.debug("######## 1 Got Result for = " + url.toString());
		logger.debug("######## 2 Got Entries: " + entries.getLength());

		final int dataSize = entries.getLength();
		for (int i = 0; i < dataSize; i++) {
			Node item = entries.item(i);
			logger.debug(i + ": Item = " + item.getNodeName());
			processEntry(item);

			// final String geneIDString = walk(item, GENE_ID_TAG);
			// logger.debug("Gene ID ======== " + geneIDString);
			// if (geneIDString == null)
			// throw new
			// NullPointerException("Could not find NCBI Gene ID for the entry.");
			// final CyRow row = table.getRow(geneIDString);
			// row.set(CyTableEntry.NAME, geneIDString);
			// if (table.getColumn("Entrez Gene ID") == null)
			// table.createColumn("Entrez Gene ID", String.class, false);
			// row.set("Entrez Gene ID", geneIDString);
			//
			// final Set<String> idSet = new HashSet<String>();
			// final NodeList ids =
			// result.getElementsByTagName("Gene-commentary");
		}

		// boolean interactionFound = false;
		// Node interactionNode = null;
		// for (int i = 0; i < dataSize; i++) {
		// // logger.debug("    GC = " +
		// // ids.item(i).getChildNodes().getLength());
		// NodeList children = ids.item(i).getChildNodes();
		// for (int j = 0; j < children.getLength(); j++) {
		// if (children.item(j).getNodeName().equals("Gene-commentary_heading"))
		// {
		//
		// //logger.debug("HEADING = " + children.item(j).getTextContent());
		// if (children.item(j).getTextContent().equals("Interactions")) {
		// logger.debug("FOUND interactions");
		// interactionFound = true;
		// break;
		// }
		// }
		// }
		// if (interactionFound) {
		// interactionNode = ids.item(i);
		// break;
		// }
		// }

		is.close();
		is = null;

		return null;
	}

	private void processEntry(Node entry) {
		valueMap = new HashMap<String, String>();
		walk(entry, GENE_ID_TAG);
		walk(entry, "Gene-ref_locus");
		walk(entry, "Gene-source_src");
		walk(entry, "Prot-ref_desc");
		if (category.contains(AnnotationCategory.SUMMARY)) {
			logger.debug("2 !!!!!! Calling summary");
			for (Node child = entry.getFirstChild(); child != null; child = child.getNextSibling()) {
				logger.debug("node = " + child.getNodeName());
				if (child.getNodeName().equals("Entrezgene_summary")) {
					logger.debug("Summary = " + child.getTextContent());
				}

			}
		}

		final CyRow row = table.getRow(valueMap.get(GENE_ID_TAG));
		row.set(CyTableEntry.NAME, valueMap.get(GENE_ID_TAG));
		for (String key : valueMap.keySet()) {
			logger.debug(key + " = " + valueMap.get(key));
		}
	}

	private String walk(Node node, final String targetTag) {
		String result = null;
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (child.getNodeName().equals(targetTag)) {
					result = child.getTextContent();
					logger.debug("Found: " + result);
					valueMap.put(targetTag, result);
					break;
				} else
					walk(child, targetTag);
			}
		}
		return result;
	}

	private URL createURL() throws IOException {

		final StringBuilder builder = new StringBuilder();

		for (final String id : ids) {
			System.out.println("ID = " + id);
			if (id != null)
				builder.append(id + ",");
		}

		String urlString = builder.toString();
		urlString = urlString.substring(0, urlString.length() - 1);
		final URL url = new URL(EntrezRestClient.FETCH_URL + urlString);
		logger.debug("Table Import Query URL = " + url.toString());
		return url;
	}
}
