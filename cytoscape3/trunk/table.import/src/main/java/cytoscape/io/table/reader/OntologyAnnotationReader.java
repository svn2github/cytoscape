
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

import cytoscape.util.URLUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;


/**
 *
 */
public class OntologyAnnotationReader implements TextTableReader {
	private final AttributeAndOntologyMappingParameters mapping;
	private final URL source;
	private final String commentChar;
	private final int startLineNumber;
	private final OntologyAndAnnotationLineParser parser;

	/**
	 * Creates a new OntologyAnnotationReader object.
	 *
	 * @param source  DOCUMENT ME!
	 * @param mapping  DOCUMENT ME!
	 * @param commentChar  DOCUMENT ME!
	 * @param startLineNumber  DOCUMENT ME!
	 */
	public OntologyAnnotationReader(URL source, AttributeAndOntologyMappingParameters mapping,
	                                final String commentChar, final int startLineNumber) {
		this.source = source;
		this.mapping = mapping;
		this.commentChar = commentChar;
		this.startLineNumber = startLineNumber;

		parser = new OntologyAndAnnotationLineParser(mapping);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List getColumnNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public void readTable() throws IOException {
		InputStream is = URLUtil.getInputStream(source);
		final BufferedReader bufRd = new BufferedReader(new InputStreamReader(is));
		String line;
		int lineCount = 0;

		/*
		 * Read & extract one line at a time. The line can be Tab delimited,
		 */
		while ((line = bufRd.readLine()) != null) {
			/*
			 * Ignore Empty & Commnet lines.
			 */
			if ((commentChar != null) && line.startsWith(commentChar)) {
				// Do nothing
			} else if ((lineCount > startLineNumber) && (line.trim().length() > 0)) {
				String[] parts = line.split(mapping.getDelimiterRegEx());
				try {
					parser.parseEntry(parts);
				} catch (Exception ex) {
					System.out.println("Couldn't parse line: " + lineCount);
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
	public String getReport() {
		// TODO Auto-generated method stub
		final StringBuffer sb = new StringBuffer();

		return sb.toString();
	}
}
