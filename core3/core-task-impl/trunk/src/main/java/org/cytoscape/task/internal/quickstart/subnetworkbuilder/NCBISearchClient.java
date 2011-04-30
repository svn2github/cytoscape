package org.cytoscape.task.internal.quickstart.subnetworkbuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NCBISearchClient {

    private static final String BASE_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&retmax=100000&term=";

    private static final String DISEASE = "[dis]";
    private static final String GO = "[gene%20ontology]";

    private static final String SEPARATOR = ",";

    Set<String> search(final String disease, final String go) throws IOException {

	final URL url = createURL(disease, go);

	try {
	    final Set<String> result = getIDSet(url);
	    return result;
	} catch (Exception e) {
	    throw new IOException("Could not parse the result.", e);
	}
    }

    private URL createURL(final String disease, final String go) throws IOException {
	final String[] dTerms = disease.split(SEPARATOR);
	final String[] gTerms = go.split(SEPARATOR);

	StringBuilder builder = new StringBuilder();

	if (dTerms.length != 0) {
	    for (String dTerm : dTerms) {
		final String trimed = dTerm.trim();
		builder.append(trimed.replaceAll("\\s", "+"));
		builder.append(DISEASE + "+OR+");
	    }
	}

	String urlString = BASE_URL + builder.toString();

	builder = new StringBuilder();

	if (gTerms.length != 0) {
	    for (String gTerm : gTerms) {
		final String trimed = gTerm.trim();
		builder.append(trimed.replaceAll("\\s", "+"));
		builder.append(GO + "+OR+");
	    }
	}

	urlString = urlString + builder.toString();
	urlString = urlString.substring(0, urlString.length() - 4);
	return new URL(urlString);
    }

    private Set<String> getIDSet(final URL url) throws ParserConfigurationException, IOException, SAXException {

	final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	final DocumentBuilder builder = factory.newDocumentBuilder();
	InputStream is = url.openStream();

	final Document result = builder.parse(is);

	final Set<String> idSet = new HashSet<String>();
	final NodeList ids = result.getElementsByTagName("Id");
	final int dataSize = ids.getLength();

	for (int i = 0; i < dataSize; i++) {
	    Node id = ids.item(i);
	    idSet.add(id.getTextContent());
	}

	is.close();
	is = null;

	return idSet;
    }
}
