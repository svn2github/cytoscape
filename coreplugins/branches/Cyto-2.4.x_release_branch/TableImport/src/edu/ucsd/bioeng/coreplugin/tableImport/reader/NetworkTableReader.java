package edu.ucsd.bioeng.coreplugin.tableImport.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cytoscape.data.readers.AbstractGraphReader;
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

	protected final int startLineNumber;

	protected final String commentChar;

	public NetworkTableReader(final String networkName, final URL sourceURL,
			final NetworkTableMappingParameters nmp, final int startLineNumber,
			final String commentChar) {
		super(networkName);
		this.sourceURL = sourceURL;
		this.nmp = nmp;
		this.startLineNumber = startLineNumber;
		this.nodeList = new ArrayList<Integer>();
		this.edgeList = new ArrayList<Integer>();
		this.commentChar = commentChar;

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
		int lineCount = 0;
		int skipped = 0;
		while ((line = bufRd.readLine()) != null) {
			/*
			 * Ignore Empty & Commnet lines.
			 */
			if (commentChar != null && commentChar.trim().length() != 0
					&& line.startsWith(commentChar)) {
				skipped++;
			} else if (line.trim().length() > 0
					&& (startLineNumber + skipped) <= lineCount) {
				String[] parts = line.split(nmp.getDelimiterRegEx());
				parser.parseEntry(parts);

			}
			lineCount++;
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

	public String getReport() {
		final StringBuffer sb = new StringBuffer();
		final Set uniqueNodes = new TreeSet(nodeList);
		final Set uniqueEdges = new TreeSet(edgeList);
		
		sb.append(uniqueNodes.size() + " nodes and " + uniqueEdges.size() + " edges are loaded.\n");
		sb.append("New network name is " + super.getNetworkName() + "\n\n");
		return sb.toString();
	}
}
