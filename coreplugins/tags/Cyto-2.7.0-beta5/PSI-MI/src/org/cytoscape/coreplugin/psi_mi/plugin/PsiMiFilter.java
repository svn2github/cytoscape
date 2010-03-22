/*
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
package org.cytoscape.coreplugin.psi_mi.plugin;

import cytoscape.data.ImportHandler;

import cytoscape.data.readers.GraphReader;

import cytoscape.util.CyFileFilter;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;


/**
 * PsiMi Filter class.  Extends CyFileFilter for integration into the Cytoscape ImportHandler
 * framework.
 *
 * @author Ethan Cerami.
 */
public class PsiMiFilter extends CyFileFilter {
	/**
	 * PSI-MI Files are Graphs.
	 */
	private static String fileNature = ImportHandler.GRAPH_NATURE;

	/**
	 * File Extensions.
	 */
	private static String[] fileExtensions = { "xml" };

	/**
	 * Filter Description.
	 */
	private static String description = "PSI-MI files";

	/**
	 * Constructor.
	 */
	public PsiMiFilter() {
		super(fileExtensions, description, fileNature);
	}

	/**
	 * Indicates which files the PsiMiFilter accepts.
	 * <p/>
	 * This method will return true only if:
	 * <UL>
	 * <LI>File ends in .xml;  and
	 * <LI>File header includes the root entrySet element.
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
			//  Second test:  file header must contain the root PSI-MI entry set
			try {
				String header = getHeader(file);

				if (header.indexOf("<entrySet") > -1) {
					return true;
				}
			} catch (IOException e) {
				firstPass = false;
			}
		}

		return false;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param url DOCUMENT ME!
	 * @param contentType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean accept(URL url, String contentType) {
		// Check for matching content type
		if ((contentType != null) && (contentTypes != null)
		    && (contentTypes.get(contentType) != null))
			return true;

		try {
			final String header = getHeader(url);

			if (header.indexOf("<entrySet") > -1)
				return true;
		} catch (IOException e) {
			return false;
		}

		return false;
	}

	/**
	 * Gets the appropirate GraphReader object.
	 *
	 * @param fileName File Name.
	 * @return GraphReader Object.
	 */
	public GraphReader getReader(String fileName) {
		return new PsiMiGraphReader(fileName);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param url DOCUMENT ME!
	 * @param conn DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public GraphReader getReader(URL url, URLConnection conn) {
		return new PsiMiGraphReader(url.toString());
	}
}
