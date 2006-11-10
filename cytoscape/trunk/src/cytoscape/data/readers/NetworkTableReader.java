package cytoscape.data.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cytoscape.util.URLUtil;

/**
 * Network text table reader. This implemets GraphReader just like other network
 * file readers.<br>
 * 
 * @since Cytoscape 2.4
 * @version 0.8
 * @author Keiichiro Ono
 * 
 */
public class NetworkTableReader extends AbstractGraphReader implements
		TextTableReader {

	protected static final String COMMENT_CHAR = "!";

	protected final NetworkTableMappingParameters nmp;
	protected final URL sourceURL;

	protected final NetworkLineParser parser;

	protected final List<Integer> nodeList;
	protected final List<Integer> edgeList;

	public NetworkTableReader(final String networkName, final URL sourceURL,
			final NetworkTableMappingParameters nmp) {
		super(networkName);
		this.sourceURL = sourceURL;
		this.nmp = nmp;
		this.nodeList = new ArrayList<Integer>();
		this.edgeList = new ArrayList<Integer>();

		parser = new NetworkLineParser(nodeList, edgeList, nmp);
	}

	public List getColumnNames() {
		List<String> colNames = new ArrayList<String>();
		for (String name : nmp.getAttributeNames()) {
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
			if (line.startsWith(COMMENT_CHAR) == false
					&& line.trim().length() > 0) {
				String[] parts = line.split(nmp.getDelimiterRegEx());
				parser.parseEntry(parts);
			}

		}
		is.close();
		bufRd.close();

	}

	@Override
	public int[] getNodeIndicesArray() {
		
		final int[] nodeArray = new int[nodeList.size()];
		for (int i = 0; i < nodeArray.length; i++) {
			nodeArray[i] = nodeList.get(i);
		}
		return nodeArray;
	}

	@Override
	public int[] getEdgeIndicesArray() {
		final int[] edgeArray = new int[edgeList.size()];
		for (int i = 0; i < edgeArray.length; i++) {
			edgeArray[i] = edgeList.get(i);
		}
		return edgeArray;
	}

	@Override
	public void read() throws IOException {
		readTable();
	}
}
