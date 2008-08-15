
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

package cytoscape.io.table.reader;

import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.util.URLUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Network text table reader. This implemets GraphReader just like other network
 * file readers.<br>
 *
 * @since Cytoscape 2.4
 * @version 0.8
 * @author Keiichiro Ono
 *
 */
public class NetworkTableReader extends AbstractGraphReader implements TextTableReader {
	protected static final String COMMENT_CHAR = "!";
	protected final NetworkTableMappingParameters nmp;
	protected final URL sourceURL;
	protected final NetworkLineParser parser;
	protected final List<Integer> nodeList;
	protected final List<Integer> edgeList;
	protected final int startLineNumber;
	protected final String commentChar;

	/**
	 * Creates a new NetworkTableReader object.
	 *
	 * @param networkName  DOCUMENT ME!
	 * @param sourceURL  DOCUMENT ME!
	 * @param nmp  DOCUMENT ME!
	 * @param startLineNumber  DOCUMENT ME!
	 * @param commentChar  DOCUMENT ME!
	 */
	public NetworkTableReader(final String networkName, final URL sourceURL,
	                          final NetworkTableMappingParameters nmp, final int startLineNumber,
	                          final String commentChar) {
		this(networkName, sourceURL, nmp, startLineNumber, commentChar, true);
	}
	/**
	 * Creates a new NetworkTableReader object.
	 *
	 * @param networkName  DOCUMENT ME!
	 * @param sourceURL  DOCUMENT ME!
	 * @param nmp  DOCUMENT ME!
	 * @param startLineNumber  DOCUMENT ME!
	 * @param commentChar  DOCUMENT ME!
	 * @param directed_edges  if true, create directed edges, otherwise create undirected ones
	 */
	public NetworkTableReader(final String networkName, final URL sourceURL,
                final NetworkTableMappingParameters nmp, final int startLineNumber,
                final String commentChar, final boolean directed_edges) {
		super(networkName);
		this.sourceURL = sourceURL;
		this.nmp = nmp;
		this.startLineNumber = startLineNumber;
		this.nodeList = new ArrayList<Integer>();
		this.edgeList = new ArrayList<Integer>();
		this.commentChar = commentChar;

		parser = new NetworkLineParser(nodeList, edgeList, nmp, directed_edges);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List getColumnNames() {
		List<String> colNames = new ArrayList<String>();

		for (String name : nmp.getAttributeNames()) {
			colNames.add(name);
		}

		return colNames;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public void readTable() throws IOException {
		InputStream is = URLUtil.getInputStream(sourceURL);
		final BufferedReader bufRd = new BufferedReader(new InputStreamReader(is));
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
			if ((commentChar != null) && (commentChar.trim().length() != 0)
			    && line.startsWith(commentChar)) {
				skipped++;
			} else if ((line.trim().length() > 0) && ((startLineNumber + skipped) <= lineCount)) {
				String[] parts = line.split(nmp.getDelimiterRegEx());
				try {
					parser.parseEntry(parts);
				} catch (Exception ex) {
					System.out.println("Couldn't parse row: " + lineCount);
				}
			}

			lineCount++;
		}

		is.close();
		bufRd.close();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	@Override
	public int[] getNodeIndicesArray() {
		final int[] nodeArray = new int[nodeList.size()];

		for (int i = 0; i < nodeArray.length; i++) {
			nodeArray[i] = nodeList.get(i);
		}

		return nodeArray;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	@Override
	public int[] getEdgeIndicesArray() {
		final int[] edgeArray = new int[edgeList.size()];

		for (int i = 0; i < edgeArray.length; i++) {
			edgeArray[i] = edgeList.get(i);
		}

		return edgeArray;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	@Override
	public void read() throws IOException {
		readTable();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getReport() {
		final StringBuffer sb = new StringBuffer();
		final Set<Integer> uniqueNodes = new TreeSet<Integer>(nodeList);
		final Set<Integer> uniqueEdges = new TreeSet<Integer>(edgeList);

		sb.append(uniqueNodes.size() + " nodes and " + uniqueEdges.size() + " edges are loaded.\n");
		sb.append("New network name is " + super.getNetworkName() + "\n\n");

		return sb.toString();
	}
}
