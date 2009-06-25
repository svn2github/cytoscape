
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


/**
 * The purpose of this class is to convert a 'gene_ontology.obo' file to a
 * crufty old 'go.onto' file that Cytoscape's BioDataServer understands. The
 * parameter passed to the constructor is the 'gene_ontology.obo' file and the
 * content read from this reader is the crufty old 'go.onto' file.
 * <p>
 * Please note that this code was not written with performance in mind.
 * Therefore, evaluating this code will not give a good indication of the
 * author's programming ability.
 */
public final class OboOntologyReader2 extends Reader {
	private final String NL = System.getProperty("line.separator");
	private BufferedReader m_obo;
	private String m_readString;
	private int m_readInx; // Index into a m_readString character.

	/**
	 * Creates a new OboOntologyReader2 object.
	 *
	 * @param oboFile  DOCUMENT ME!
	 */
	public OboOntologyReader2(final Reader oboFile) {
		if (oboFile == null)
			throw new NullPointerException("oboFile is null");

		m_obo = new BufferedReader(oboFile);
		m_readString = "(curator=GO) (type=all)" + NL;
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
		if (m_obo == null)
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

	/*
	 * The job of this method is to set m_readString and m_readInx.
	 */
	private final void readMore() throws IOException {
		while (true) // Read until line after '[Term]'.
		 {
			final String line = m_obo.readLine();

			if (line == null) { // End of underlying stream.
				m_readString = null;

				return;
			}

			final String trimmedLine = line.trim();

			if (trimmedLine.equals("[Term]")) {
				break;
			}
		}

		String id = "";
		String namespace = "";

		while (true) // Parse until blank line.
		 {
			final String line = m_obo.readLine().trim();

			if (line.length() == 0)
				break;

			final int colonInx = line.indexOf(':');
			final String key = line.substring(0, colonInx).trim();
			final String val = line.substring(colonInx + 1).trim();

			if (key.equals("id")) {
				// There's only one id.
				id = val;
			} else if (key.equals("namespace")) {
				// There's only one description.
				namespace = val;
			}
		}

		m_readString = id + "=" + namespace;

		m_readString = m_readString + NL;
		m_readInx = 0;
	}

	/**
	 * Closes the underlying obo file stream as well.
	 */
	public final void close() throws IOException {
		try {
			m_obo.close();
		} finally {
			m_obo = null;
		}
	}
}
