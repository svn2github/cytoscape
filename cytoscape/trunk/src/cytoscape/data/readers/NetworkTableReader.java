package cytoscape.data.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.URLUtil;

public class NetworkTableReader implements TextTableReader {
	
	private static final String COMMENT_CHAR = "!";
	
	private NetworkTableMappingParameters nmp;
	private URL sourceURL;
	
	private NetworkLineParser parser;
	
	
	public NetworkTableReader(URL sourceURL, NetworkTableMappingParameters nmp) {
		this.sourceURL = sourceURL;
		this.nmp = nmp;
		String[] urlParts = sourceURL.toString().split("/");
		
		CyNetwork network = Cytoscape.createNetwork(urlParts[urlParts.length-1]);
		parser = new NetworkLineParser(network, nmp);
	}
	

	public List getColumnNames() {
		List<String> colNames = new ArrayList<String>();
		for(String name: nmp.getAttributeNames()) {
			colNames.add(name);
		}
		return colNames;
	}

	public void readTable() throws IOException {
		InputStream is = URLUtil.getInputStream(sourceURL);
		final BufferedReader bufRd = new BufferedReader(new InputStreamReader(
				is));
		String line;

		/*
		 * Read & extract one line at a time. The line can be Tab delimited,
		 */
		while ((line = bufRd.readLine()) != null) {
			/*
			 * Ignore Empty & Commnet lines.
			 */
			if (line.startsWith(COMMENT_CHAR) == false && line.trim().length() > 0) {
				String[] parts = line.split(nmp.getDelimiterRegEx());
				parser.parseEntry(parts);
			}

		}
		is.close();
		bufRd.close();

	}

}
