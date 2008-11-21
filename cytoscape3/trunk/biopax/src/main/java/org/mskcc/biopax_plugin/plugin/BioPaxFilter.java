// $Id: TestExternalLinkUtil.java,v 1.11 2006/06/15 22:07:49 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross.
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.plugin;

import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.util.CyFileFilter;

import java.io.File;
import java.io.IOException;


/**
 * BioPax Filer class.  Extends CyFileFilter for integration into the Cytoscape ImportHandler
 * framework.
 *
 * @author Ethan Cerami.
 */
public class BioPaxFilter extends CyFileFilter {
	/**
	 * XGMML Files are Graphs.
	 */
	private static String fileNature = ImportHandler.GRAPH_NATURE;

	/**
	 * File Extensions.
	 */
	private static String[] fileExtensions = { "xml", "owl", "rdf" };

	/**
	 * Filter Description.
	 */
	private static String description = "BioPAX files";

	/**
	 * Constructor.
	 */
	public BioPaxFilter() {
		super(fileExtensions, description, fileNature);
	}

	/**
	 * Indicates which files the BioPaxFilter accepts.
	 * <p/>
	 * This method will return true only if:
	 * <UL>
	 * <LI>File ends in .xml or .owl;  and
	 * <LI>File headers includes the www.biopax.org namespace declaration.
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
			//  Second test:  file header must contain the biopax declaration
			try {
				String header = getHeader(file);

				if (header.indexOf("www.biopax.org") > 0) {
					return true;
				}
			} catch (IOException e) {
			}
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
		return new BioPaxGraphReader(fileName);
	}
}
