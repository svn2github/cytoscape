
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

package edu.ucsd.bioeng.coreplugin.tableImport.tests;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.data.ontology.GeneOntology;

import cytoscape.data.servers.OntologyServer.OntologyType;

import edu.ucsd.bioeng.coreplugin.tableImport.reader.GeneAssociationReader;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;

import java.util.Iterator;


/**
 * Test cases for GeneAssociationReader.java.
 *
 * @since Cytoscape 2.4
 * @version 0.8
 *
 * @author Keiichiro Ono
 *
 */
public class GeneAssociationReaderTest extends TestCase {
	private static final String GO_SLIM = "testData/annotation/goslim_generic.obo";
	private static final String GAL_NETWORK = "testData/galFiltered.sif";
	private static final String GENE_ASSOCIATION = "testData/annotation/gene_association.sgd";
	private CyNetwork gal;
	private CyAttributes nodeAttr;

	protected void setUp() throws Exception {
		super.setUp();
		gal = Cytoscape.createNetworkFromFile(GAL_NETWORK);
		Cytoscape.buildOntologyServer();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		Cytoscape.destroyNetwork(gal);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 * @throws URISyntaxException DOCUMENT ME!
	 */
	public void testBuildMap() throws IOException, URISyntaxException {
		assertNotNull(Cytoscape.getOntologyServer().getOntologyNames());

		String ontologyName = null;
		Iterator it = Cytoscape.getOntologyServer().getOntologyNames().iterator();

		while (it.hasNext()) {
			ontologyName = (String) it.next();
			System.out.println("Ontology Name used for GA reader = " + ontologyName);
		}

		File sampleSourceFile = new File(GENE_ASSOCIATION);
		assertTrue(sampleSourceFile.canRead());

		GeneOntology go = new GeneOntology("go1", "testCur", "testDesc", null);
		Cytoscape.getOntologyServer().addOntology(go);

		File goSlim = new File(GO_SLIM);
		Cytoscape.getOntologyServer()
		         .addOntology(goSlim.toURL(), OntologyType.GO, "GO Slim Test", "Test");

		GeneAssociationReader gar = new GeneAssociationReader("GO Slim Test",
		                                                      sampleSourceFile.toURL(), "ID");

		gar.readTable();
		nodeAttr = Cytoscape.getNodeAttributes();

		for (String attrName : nodeAttr.getAttributeNames()) {
			System.out.println("Deleting: " + attrName);
			nodeAttr.deleteAttribute(attrName);
		}

		/*
		 * Delete all attributes
		 */
	}
}
