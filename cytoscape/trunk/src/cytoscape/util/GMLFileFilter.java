
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

import cytoscape.data.readers.GMLReader;
import cytoscape.data.readers.GraphReader;


/**
 * FileFilter for Reading in GML Files.
 *
 * @author Cytoscape Development Team.
 */
public class GMLFileFilter extends CyFileFilter {
	/**
	 * XGMML Files are Graphs.
	 */
	private static String fileNature = ImportHandler.GRAPH_NATURE;

	/**
	 * File Extensions.
	 */
	private static String fileExtension = "gml";

	/**
	 * Filter Description.
	 */
	private static String description = "GML files";

	/**
	 * Constructor.
	 */
	public GMLFileFilter() {
		super(fileExtension, description, fileNature);
	}

	/**
	 * Gets GraphReader.
	 * @param fileName File Name.
	 * @return GraphReader Object.
	 */
	public GraphReader getReader(String fileName) {
		reader = new GMLReader(fileName);

		return reader;
	}
}
