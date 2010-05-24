
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

package cytoscape.cruft.obo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import java.util.Map;


/**
 * The purpose of this class is to convert a 'gene_association.*' file (for
 * example 'gene_association.sgd') into a crufty old 'molfunc.anno' file that
 * Cytoscape's BioDataServer understands. The parameter passed to the
 * constructor is the 'gene_association.*' file and the content read from this
 * reader is the crufty old 'molfunc.anno' file.
 * <p>
 * Please note that this code was not written with performance in mind.
 * Therefore, evaluating this code will not give a good indication of the
 * author's programming ability.
 */
public final class MolecularFunctionAnnotationReader extends Reader {
	private final String NL = System.getProperty("line.separator");
	private BufferedReader m_file;
	private String m_readString;
	private int m_readInx; // Index into a m_readString character.
	private Map ontologyTypeMap;

	/**
	 * Creates a new MolecularFunctionAnnotationReader object.
	 *
	 * @param speciesName  DOCUMENT ME!
	 * @param geneAssociationFile  DOCUMENT ME!
	 */
	public MolecularFunctionAnnotationReader(final String speciesName,
	                                         final Reader geneAssociationFile) {
		if (geneAssociationFile == null)
			throw new NullPointerException("geneAssociationFile is null");

		m_file = new BufferedReader(geneAssociationFile);
		m_readString = "(species=" + speciesName + ") (type=Molecular Function) (curator=GO)" + NL;
		m_readInx = 0;
	}

	/**
	 * Creates a new MolecularFunctionAnnotationReader object.
	 *
	 * @param speciesName  DOCUMENT ME!
	 * @param ontologyTypeMap  DOCUMENT ME!
	 * @param geneAssociationFile  DOCUMENT ME!
	 */
	public MolecularFunctionAnnotationReader(final String speciesName, final Map ontologyTypeMap,
	                                         final Reader geneAssociationFile) {
		if (geneAssociationFile == null)
			throw new NullPointerException("geneAssociationFile is null");

		this.ontologyTypeMap = ontologyTypeMap;

		m_file = new BufferedReader(geneAssociationFile);
		m_readString = "(species=" + speciesName + ") (type=Molecular Function) (curator=GO)" + NL;
		m_readInx = 0;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param cbuf DOCUMENT ME!
	 * @param off DOCUMENT ME!
	 * @param len DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public final int read(final char[] cbuf, final int off, final int len)
	    throws IOException {
		if (m_file == null)
			throw new IOException("this stream is closed");

		if (m_readString == null)
			return -1;

		final int returnThis;

		if ((m_readString.length() - m_readInx) >= len) {
			m_readString.getChars(m_readInx, m_readInx + len, cbuf, off);
			returnThis = len;
			m_readInx += len;
		} else { // len is greater than the number of chars left in
			     // m_readString.
			m_readString.getChars(m_readInx, m_readString.length(), cbuf, off);
			returnThis = m_readString.length() - m_readInx;
			m_readInx = m_readString.length();
		}

		if (m_readInx == m_readString.length())
			readMore();

		return returnThis;
	}

	private final void readMore() throws IOException {
		while (true) {
			String line;

			while (true) // Read comments and possibly blank lines.
			 {
				line = m_file.readLine();

				if (line == null) { // End of underlying stream.
					m_readString = null;

					return;
				}

				line = line.trim();

				if ((line.length() > 0) && !line.startsWith("!")) {
					break;
				}
			}

			// Now line contains a line of data.
			int fromIndex = 0;

			for (int i = 0; i < 2; i++) {
				fromIndex = 1 + line.indexOf('\t', fromIndex);
			}

			final String canon = line.substring(fromIndex, line.indexOf('\t', fromIndex));

			for (int i = 0; i < 2; i++) {
				fromIndex = 1 + line.indexOf('\t', fromIndex);
			}

			final String goid = line.substring(fromIndex + 3, fromIndex + 10);

			for (int i = 0; i < 4; i++) {
				fromIndex = 1 + line.indexOf('\t', fromIndex);
			}

			String type = line.substring(fromIndex, fromIndex + 1);

			if (!type.equals("P") && !type.equals("C") && !type.equals("F")) {
				// Try to find one
				type = (String) ontologyTypeMap.get("GO:" + goid);
			}

			if (type != null) {
				if (type.equals("F")) { // Molecular Function.. We found one.
					m_readString = canon + " = " + goid + NL;
					m_readInx = 0;

					break;
				}
			}
		}
	}

	/**
	 * Closes the underlying gene_association file stream as well.
	 */
	public final void close() throws IOException {
		try {
			m_file.close();
		} finally {
			m_file = null;
		}
	}
}
