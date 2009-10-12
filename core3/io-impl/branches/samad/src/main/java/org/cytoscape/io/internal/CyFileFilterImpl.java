/*
  File: CyFileFilter.java

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
package org.cytoscape.io.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.util.StreamUtil;                                                                              

public class CyFileFilterImpl implements CyFileFilter {

	/*
	 * Basic information for compatible file type. Everything will be injected
	 * through DI container.
	 */
	protected Set<String> extensions;
	protected Set<String> contentTypes;
	protected String description;
	protected StreamUtil streamUtil;

	/**
	 * Creates a file filter from the given string array and description.
	 * Example: new ExampleFileFilter(String {"gif", "jpg"},
	 * "Gif and JPG Images");
	 * <p/>
	 * Note that the "." before the extension is not needed and will be ignored.
	 * 
	 */
	public CyFileFilterImpl(final Set<String> extensions,
			final Set<String> contentTypes, final String description,
			StreamUtil streamUtil) {

		this.extensions = extensions;
		this.contentTypes = contentTypes;

		String d = description == null ? "(" : description + " (";

		for (String ex : extensions)
			d += "*." + ex + ", ";
		d = d.substring(0, d.length() - 2);
		d += ")";

		this.description = d;
		this.streamUtil = streamUtil;
	}

	/**
	 * Returns true if this class is capable of processing the specified URL
	 * 
	 * @param url
	 *            the URL
	 * @param contentType
	 *            the content-type of the URL
	 * @throws IOException
	 * @throws MalformedURLException
	 * 
	 */
	public boolean accept(URI uri) throws IOException {

		final URLConnection connection = streamUtil.getURLConnection(uri.toURL());
		final String contentType = connection.getContentType();

		// Check for matching content type
		if ((contentType != null) && contentTypes.contains(contentType))
			return true;

		// No content-type match -- try for an extnsion match
		String extension = getExtension(uri.toString());
		if ((extension != null) && extensions.contains(extension))
			return true;

		return false;
	}

	public Set<String> getExtensions() {
		return extensions;
	}

	public Set<String> getContentTypes() {
		return contentTypes;
	}

	/**
	 * Returns the human readable description of this filter. For example:
	 * "JPEG and GIF Image Files (*.jpg, *.gif)"
	 * 
	 * @see setDescription
	 * @see setExtensionListInDescription
	 * @see isExtensionListInDescription
	 * @see FileFilter#getDescription
	 */
	public String getDescription() {
		return description;
	}

	private String getExtension(String filename) {
		if (filename != null) {
			int i = filename.lastIndexOf('.');

			if ((i > 0) && (i < (filename.length() - 1))) {
				return filename.substring(i + 1).toLowerCase();
			}
		}

		return null;
	}

	protected String getHeader(InputStream stream) throws IOException {
		
		String header = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		
		try {
			header = parseHeader(br);
		} finally {
			if (br != null)
				br.close();
			br = null;
		}
		
		return header;
	}

	private String parseHeader(BufferedReader bufferedReader)
			throws IOException {
		StringBuilder header = new StringBuilder();

		try {
			String line = bufferedReader.readLine();

			int numLines = 0;

			while ((line != null) && (numLines < 20)) {
				header.append(line + "\n");
				line = bufferedReader.readLine();
				numLines++;
			}
		} finally {
			if (bufferedReader != null)
				bufferedReader.close();
		}

		return header.toString();
	}

}
