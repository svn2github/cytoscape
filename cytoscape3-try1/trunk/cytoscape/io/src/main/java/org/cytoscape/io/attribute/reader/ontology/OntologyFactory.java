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
package org.cytoscape.io.attribute.reader.ontology;

import org.cytoscape.io.attribute.reader.obo.OBOFlatFileReader;
import org.cytoscape.io.attribute.reader.go.GeneOntology;
import org.cytoscape.io.attribute.reader.obo.OBOOntology;
import org.cytoscape.io.attribute.reader.MetadataEntries;
import org.cytoscape.io.attribute.reader.MetadataParser;

import static org.cytoscape.io.attribute.reader.obo.OBOHeaderTags.*;

import java.io.IOException;

import java.net.URISyntaxException;
import java.net.URL;

import java.util.Map;

/**
 *
 */
public class OntologyFactory {
	private MetadataParser mdp;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param dataSource DOCUMENT ME!
	 * @param name DOCUMENT ME!
	 * @param description DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 * @throws URISyntaxException DOCUMENT ME!
	 */
	public OBOOntology createBasicOntology(URL dataSource, String name, String description)
	    throws IOException, URISyntaxException {
		OBOFlatFileReader reader = new OBOFlatFileReader(dataSource, name);
		reader.readOntology();

		OBOOntology onto = new OBOOntology(name, "General Ontology", description, reader.getDag());

		Map header = reader.getHeader();

		if ((header != null) && (header.get(DATE.toString()) != null)) {
			mdp = new MetadataParser(reader.getDag());

			mdp.setMetadata(MetadataEntries.DATE, header.get(DATE.toString()).toString());
		}

		return onto;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param dataSource DOCUMENT ME!
	 * @param name DOCUMENT ME!
	 * @param description DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 * @throws URISyntaxException DOCUMENT ME!
	 */
	public GeneOntology createGeneOntology(URL dataSource, String name, String description)
	    throws IOException, URISyntaxException {
		OBOFlatFileReader reader = new OBOFlatFileReader(dataSource, name);
		reader.readOntology();

		GeneOntology go = new GeneOntology(name, "GO", description, reader.getDag());
		Map header = reader.getHeader();

		mdp = new MetadataParser(reader.getDag());
		mdp.setMetadata(MetadataEntries.DATE, header.get(DATE.toString()).toString());

		return go;
	}

	// public KEGGOntology createKEGGOntology(URL dataSource, String name) {
	// KEGGOntology kegg = null;
	// return kegg;
	// }
}
