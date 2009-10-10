/* Copyright 2008 - The Cytoscape Consortium (www.cytoscape.org)
 *
 * The Cytoscape Consortium is:
 * - Institute for Systems Biology
 * - University of California San Diego
 * - Memorial Sloan-Kettering Cancer Center
 * - Institut Pasteur
 * - Agilent Technologies
 *
 * Authors: B. Arman Aksoy, Thomas Kelder, Emek Demir
 * 
 * This file is part of PaxtoolsPlugin.
 *
 *  PaxtoolsPlugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PaxtoolsPlugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

/* Reproduction of org.mskcc.biopax_plugin.plugin.BioPaxFilter */

package cytoscape.coreplugins.biopax;

import cytoscape.util.CyFileFilter;
import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;

import java.io.File;

public class PaxtoolsFileFilter extends CyFileFilter {
	private static String[] fileExtensions = { "xml", "owl", "rdf" };
	private static String description = "BioPAX files",
                          //fullDescription = description,
                          fileNature = ImportHandler.GRAPH_NATURE;

    public PaxtoolsFileFilter() {
        super(fileExtensions, description, fileNature);
    }

    public boolean accept(File file) {
        for(String extension: fileExtensions)
            if( file.getName().toUpperCase().endsWith(extension.toUpperCase()) )
                return true;

        return false;
    }

    public GraphReader getReader(String fileName) {
		return new PaxtoolsReader(fileName);
	}

}
