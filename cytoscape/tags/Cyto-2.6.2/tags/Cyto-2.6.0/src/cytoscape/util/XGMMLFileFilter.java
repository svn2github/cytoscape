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
package cytoscape.util;

import cytoscape.data.ImportHandler;

import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.XGMMLReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLConnection;

/**
 * FileFilter for Reading in XGMML Files.
 *
 * @author Cytoscape Development Team.
 */
public class XGMMLFileFilter extends CyFileFilter {
	/**
	 * XGMML Files are Graphs.
	 */
	private static String fileNature = ImportHandler.GRAPH_NATURE;

	/**
	 * File Extensions.
	 */
	private static String[] fileExtensions = { "xgmml", "xml" };

	/**
	 * Content Types
	 */
	private static String[] contentTypes = { "text/xgmml", "text/xgmml+xml" };

	/**
	 * Filter Description.
	 */
	private static String description = "XGMML files";

	/**
	 * Constructor.
	 */
	public XGMMLFileFilter() {
		super(fileExtensions, description, fileNature);

		// Add our content types
		for (int i = 0; i < contentTypes.length; i++)
			addContentType(contentTypes[i]);
	}

	/**
	 * Gets Graph Reader.
	 * @param fileName File name.
	 * @return GraphReader Object.
	 */
	public GraphReader getReader(String fileName) {
		reader = new XGMMLReader(fileName);

		return reader;
	}

	/**
	 * Gets Graph Reader.
	 * @param fileName File name.
	 * @return GraphReader Object.
	 */
	public GraphReader getReader(URL url, URLConnection conn) {
		try {
			// Get the input stream
			reader = new XGMMLReader(conn.getInputStream(), url.toString());
		} catch (Exception e) {
			e.printStackTrace();
			reader = null;
		}
		return reader;
	}

	/**
	 * Indicates which files the XGMMLFileFilter accepts.
	 *
	 * This method will return true only if:
	 * <UL>
	 * <LI>File ends in .xml or .xgmml;  and
	 * <LI>File headers includes the www.cs.rpi.edu/XGMML namespace declaration.
	 * </UL>
	 *
	 * @param file File
	 * @return true or false.
	 */
	public boolean accept(File file) {
		String fileName = file.getName();
		boolean firstPass = false;

		//  First test:  file must end with one of the registered file extensions.
		for (int i = 0; i < fileExtensions.length; i++) {
			if (fileName.endsWith(fileExtensions[i])) {
				firstPass = true;
			}
		}

		if (firstPass) {
			//  Second test:  file header must contain the xgmml declaration
			try {
				String header = getHeader(file).toLowerCase();

				if (header.indexOf("www.cs.rpi.edu/xgmml") > 0) {
					return true;
				}
			} catch (IOException e) {
			}
		}

		return false;
	}
}
