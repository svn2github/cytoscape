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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URLConnection;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;

public class CyFileFilterImpl implements CyFileFilter {

	/*
	 * Basic information for compatible file type.
	 * Everything will be injected through DI container.
	 */
	private Set<String> extensions;
	private Set<String> contentTypes;
	private String description;
	private DataCategory category;
	
	private Proxy proxy;
	
	/**
	 * Creates a file filter from the given string array and description.
	 * Example: new ExampleFileFilter(String {"gif", "jpg"},
	 * "Gif and JPG Images");
	 * <p/>
	 * Note that the "." before the extension is not needed and will be ignored.
	 * 
	 */
	public CyFileFilterImpl(final Set<String> extensions,
			final Set<String> contentTypes, final String description, final DataCategory category) {

		this.extensions = extensions;
		this.contentTypes = contentTypes;
		this.category = category;

		String d = description == null ? "(" : description + " (";

		for (String ex : extensions)
			d += "*." + ex + ", ";
		d = d.substring(0, d.length() - 2);
		d += ")";

		this.description = d;
	}
	
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
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
	public boolean accept(URI uri, DataCategory category) throws IOException {
		
		// Check data category
		if(category != this.category)
			return false;
		
		final URLConnection connection;
		if(proxy != null)
			connection = uri.toURL().openConnection(proxy);
		else
			connection = uri.toURL().openConnection();
		
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

	/**
	 * Must be overridden by subclasses.
	 */
	public boolean accept(InputStream stream, DataCategory category) throws IOException {
		
		// Check data category
		if(category != this.category)
			return false;

		return true;
		
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

	public DataCategory getDataCategory() {
		return category;
	}
}
