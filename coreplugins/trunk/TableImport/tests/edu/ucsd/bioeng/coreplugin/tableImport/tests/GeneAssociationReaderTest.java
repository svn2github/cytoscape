package edu.ucsd.bioeng.coreplugin.tableImport.tests;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import junit.framework.TestCase;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.ontology.GeneOntology;
import cytoscape.data.servers.OntologyServer.OntologyType;
import edu.ucsd.bioeng.coreplugin.tableImport.reader.GeneAssociationReader;

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

	public void testBuildMap() throws IOException, URISyntaxException {
		assertNotNull(Cytoscape.getOntologyServer().getOntologyNames());
		String ontologyName = null;
		Iterator it = Cytoscape.getOntologyServer().getOntologyNames()
				.iterator();

		while (it.hasNext()) {
			ontologyName = (String) it.next();
			System.out.println("Ontology Name used for GA reader = "
					+ ontologyName);
		}
		File sampleSourceFile = new File(GENE_ASSOCIATION);
		assertTrue(sampleSourceFile.canRead());
		GeneOntology go = new GeneOntology("go1", "testCur", "testDesc", null);
		Cytoscape.getOntologyServer().addOntology(go);
		File goSlim = new File(GO_SLIM);
		Cytoscape.getOntologyServer().addOntology(goSlim.toURL(),
				OntologyType.GO, "GO Slim Test", "Test");
		GeneAssociationReader gar = new GeneAssociationReader("GO Slim Test",
				sampleSourceFile.toURL(), "ID");

		gar.readTable();
		nodeAttr = Cytoscape.getNodeAttributes();
		
		for(String attrName: nodeAttr.getAttributeNames()) {
			System.out.println("Deleting: " + attrName);
			nodeAttr.deleteAttribute(attrName);
		}
		
		/*
		 * Delete all attributes
		 */
		
		
		
	}

}
