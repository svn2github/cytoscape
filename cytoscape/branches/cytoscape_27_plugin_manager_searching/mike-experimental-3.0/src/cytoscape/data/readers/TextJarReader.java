/*
  File: TextJarReader.java

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
package cytoscape.data.readers;

import org.cytoscape.*;
import cytoscape.util.URLUtil;

import java.io.*;

import java.net.*;

import java.util.*;
import java.util.jar.*;


/**
 *
 */
public class TextJarReader {
	String filename;
	InputStreamReader reader;
	StringBuffer sb;

	/**
	 * Creates a new TextJarReader object.
	 *
	 * @param urlString  DOCUMENT ME!
	 *
	 * @throws IOException  DOCUMENT ME!
	 */
	public TextJarReader(String urlString) throws IOException {
		if (!urlString.startsWith("jar"))
			throw new IOException("Ok, so this isn't an IOException, but it's still a problem: "
			                      + urlString + "  This class only accepts JAR urls!!! "
			                      + "  See java.net.JarURLConnection for syntax");

		sb = new StringBuffer();

		InputStream is = null;

		if (urlString.matches("jar\\:\\/\\/.+")) {
			// to support the old way of doing things
			filename = urlString.substring(5);
			is = getClass().getResourceAsStream(filename);
		} else {
			// assume that we match proper jar url syntax 
			// jar:<url>!/{file}  
			// see JarURLConnection api for more details
			filename = urlString;

			URL url = new URL(urlString);
			// is = url.openStream();
            // Use URLUtil to get the InputStream since we might be using a proxy server 
			// and because pages may be cached:
			is = URLUtil.getBasicInputStream(url);
		}

		reader = new InputStreamReader(is);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public int read() throws IOException {
		System.out.println("-- reading " + filename);

		char[] cBuffer = new char[1024];
		int bytesRead;

		while ((bytesRead = reader.read(cBuffer, 0, 1024)) != -1)
			sb.append(new String(cBuffer, 0, bytesRead));

		return sb.length();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getText() {
		return sb.toString();
	}
}
