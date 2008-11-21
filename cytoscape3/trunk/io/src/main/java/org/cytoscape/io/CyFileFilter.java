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
package org.cytoscape.io;

import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;


public class CyFileFilter extends FileFilter implements FilenameFilter {

	private final Set<String> extensions;
	private final Set<String> contentTypes;
	private final String description;

	/**
	 * Creates a file filter from the given string array and description.
	 * Example: new ExampleFileFilter(String {"gif", "jpg"}, "Gif and JPG Images");
	 * <p/>
	 * Note that the "." before the extension is not needed and will be ignored.
	 *
	 */
	public CyFileFilter(String[] ext, String[] cont, String desc) {
		extensions = new HashSet<String>();
		contentTypes = new HashSet<String>();

		for ( String e : ext )
			extensions.add(e.toLowerCase());

		for ( String c : cont )
			contentTypes.add( c );

		String d = desc == null ? "(" : desc + " (";

		for ( String ex : extensions ) 
			d += "*." + ex + ", "; 

		d = d.substring(0,d.length()-2);
		d += ")";

		description = d;
	}

	/**
	 * Returns true if this class is capable of processing the specified file.
	 *
	 * @param f File
	 */
		
	public boolean accept(File f) {
		if ( f == null )
			return false;

		if (f.isDirectory()) 
			return true;

		return accept(f.getName());
	}

	public boolean accept(File dir, String fileName) {
		return accept(fileName);
	}

	public boolean accept(String f) {
		if ( f == null || f.equals("") )
			return false;

		// if there are no extensions, accept everything
		if (extensions.size() == 0)
			return true;

		String extension = getExtension(f);

		if ((extension != null) && extensions.contains(extension)) 
			return true;

		return false;
	}

	/**
 	 * Returns true if this class is capable of processing the specified URL
 	 *
 	 * @param url the URL
 	 * @param contentType the content-type of the URL
 	 *
 	 */
	public boolean accept(URL url, String contentType) {
		// Check for matching content type
		if ((contentType != null) && contentTypes.contains(contentType)) 
			return true;
		
		// No content-type match -- try for an extnsion match
		String extension = getExtension(url.getFile());
		if ((extension != null) && extensions.contains(extension)) 
			return true;

		return false;
	}

	/**
	 * Returns the human readable description of this filter. For
	 * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
	 *
	 * @see setDescription
	 * @see setExtensionListInDescription
	 * @see isExtensionListInDescription
	 * @see FileFilter#getDescription
	 */
	public String getDescription() {
		return description;
	}


	protected String getHeader(File file) throws IOException {
		FileReader reader = null;
		BufferedReader bufferedReader = null;

		try {
			reader = new FileReader(file);
			bufferedReader = new BufferedReader(reader);

			String line = bufferedReader.readLine();
			StringBuffer header = new StringBuffer();
			int numLines = 0;

			while ((line != null) && (numLines < 20)) {
				header.append(line + "\n");
				line = bufferedReader.readLine();
				numLines++;
			}

			return header.toString();
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}

			if (reader != null) {
				reader.close();
			}
		}
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
}
