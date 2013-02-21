/*
  File: TextFileReader.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

// TextFileReader.java
package bingo.internal.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 */
public class TextFileReader {
	String filename;
	StringBuffer strbuf;

	/**
	 * Creates a new TextFileReader object.
	 * 
	 * @param filename
	 *            DOCUMENT ME!
	 */
	public TextFileReader(String filename) {
		this.filename = filename;
		strbuf = new StringBuffer();
	} // ctor

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * @throws IOException 
	 */
	public int read() throws IOException {
		String newLineOfText;
		BufferedReader bufferedReader = null;
		int rv;

		// Start with an empty StringBuffer in case this method is called more
		// than once.
		strbuf = new StringBuffer();
		rv = -1;

		try {
			bufferedReader = new BufferedReader(new FileReader(filename));
			while ((newLineOfText = bufferedReader.readLine()) != null) {
				strbuf.append(newLineOfText + "\n");
			}
			rv = strbuf.length();
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
				bufferedReader = null;
			}
		}

		return rv;
	} // read

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getText() {
		return (new String(strbuf));
	} // read
}
