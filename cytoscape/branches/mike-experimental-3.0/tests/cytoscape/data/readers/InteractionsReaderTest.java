/*
  File: InteractionsReaderTest.java

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

// InteractionsReaderTest.java
package cytoscape.data.readers;

import cytoscape.AllTests;
import cytoscape.GraphPerspective;
import cytoscape.Cytoscape;

import cytoscape.data.Interaction;

import cytoscape.data.readers.InteractionsReader;

import cytoscape.RootGraph;

import junit.framework.*;

import java.io.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;


/**
 *
 */
public class InteractionsReaderTest extends TestCase {
	private boolean runAllTests = false;
	private String species = "unknown";

	/**
	 * Creates a new InteractionsReaderTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public InteractionsReaderTest(String name) {
		super(name);
		runAllTests = AllTests.runAllTests();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testReadFromTypicalFile()
	    throws Exception // 'typical' means that all lines have the form "node1 pd node2 [node3 node4 ...]
	 {
		AllTests.standardOut("testFromTypicalFile");

		InteractionsReader reader = this.getReader("testData/sample.sif");
		reader.read();
		assertTrue(reader.getCount() == 25);

		Interaction[] interactions = reader.getAllInteractions();
		assertTrue(interactions[0].getSource().equals("YNL312W"));
		assertTrue(interactions[0].getType().equals("pd"));
		assertTrue(interactions[0].numberOfTargets() == 1);
		assertTrue(interactions[0].getTargets()[0].equals("YPL111W"));

		assertTrue(interactions[11].numberOfTargets() == 3);
	} // testReadFromTypicalFile

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testReadFileWithNoInteractions()
	    throws Exception // all lines have the degenerate form 
	                     //   "node1"
	                     // that is, with no interaction type and no target
	 {
		AllTests.standardOut("testReadFileWithNoInteractions");

		InteractionsReader reader = this.getReader("testData/degenerate.sif");
		reader.read();
		assertTrue(reader.getCount() == 9);

		Interaction[] interactions = reader.getAllInteractions();

		for (int i = 0; i < interactions.length; i++) {
			assertTrue(interactions[i].getSource().startsWith("Y"));
			assertTrue(interactions[i].getType() == null);
			assertTrue(interactions[i].numberOfTargets() == 0);
		}
	} // testReadFileWithNoInteractions

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testGetGraph() throws Exception {
		AllTests.standardOut("testGetGraph");

		InteractionsReader reader = this.getReader("testData/sample.sif");
		reader.read();
		assertTrue(reader.getCount() == 25);

		int[] nodes = reader.getNodeIndicesArray();
		int[] edges = reader.getEdgeIndicesArray();

		assertTrue("node count: expect 31, got " + nodes.length, nodes.length == 31);
		assertTrue("edge count: expect 27, got " + edges.length, edges.length == 27);
	} // testGetGraph

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testGetGraphAndEdgeAttributes()
	    throws Exception // when an interactions file is read, and a graph created, an
	                     // GraphObjAttrib*tes hash is created, and all of the edge types are added
	                     // to it.  make sure that works:  make sure the reader returns an
	                     // GraphObjAttrib*tes object; that it has the right size; that the keys
	                     // look like "node1::node2", and that the values are simple strings
	 {
		AllTests.standardOut("testGetGraphAndEdgeAttributes");

		InteractionsReader reader = this.getReader("testData/sample.sif");
		reader.read();
		assertTrue(reader.getCount() == 25);

		int[] nodes = reader.getNodeIndicesArray();
		int[] edges = reader.getEdgeIndicesArray();

		assertTrue("node count: expect 31, got " + nodes.length, nodes.length == 31);
		assertTrue("edge count: expect 27, got " + edges.length, edges.length == 27);

		//  assertTrue ("attribute count", Cytoscape.getEdgeNetworkData().size () == 2);

		//   HashMap interactions = Cytoscape.getEdgeNetworkData().getAttribute ("interaction");
		//   assertTrue ("non-null interactions", interactions != null);

		//   String [] edgeNames = Cytoscape.getEdgeNetworkData().getObjectNames ("interaction");
		//   assertTrue ("edgeNames count", edgeNames.length == 27);

		//   for (int i=0; i < edgeNames.length; i++) {
		//     assertTrue ("looking for ' (pd) '", edgeNames[i].indexOf (" (pd) ") > 0);
		//     String interactionType = (String) Cytoscape.getEdgeNetworkData().getValue ("interaction", edgeNames [i]);
		//     assertTrue (interactionType.equals ("pd"));
		//     }
	} // testGetGraphAndEdgeAttributes

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testReadMultiWordProteinsFile() throws Exception {
		AllTests.standardOut("testReadMultiWordProteinsFile");

		String filename = "testData/multiWordProteins.sif";
		InteractionsReader reader = this.getReader(filename);
		reader.read();
		assertTrue(reader.getCount() == 29);

		Interaction[] interactions = reader.getAllInteractions();

		// interaction 16:
		//  26S ubiquitin dependent proteasome
		//     interactsWith
		// I-kappa-B-alpha
		assertTrue(interactions[16].getSource().equals("26S ubiquitin dependent proteasome"));
		assertTrue(interactions[16].getType().equals("interactsWith"));
		assertTrue(interactions[16].numberOfTargets() == 1);
		assertTrue(interactions[16].getTargets()[0].equals("I-kappa-B-alpha"));

		assertTrue(interactions[28].getSource().equals("TRAF6"));
		assertTrue(interactions[28].getType().equals("interactsWith"));
		assertTrue(interactions[28].numberOfTargets() == 3);
		assertTrue(interactions[28].getTargets()[0].equals("RIP2"));
		assertTrue(interactions[28].getTargets()[1].equals("ABCDE oopah"));
		assertTrue(interactions[28].getTargets()[2].equals("HJKOL coltrane"));
	} // testReadMultiWordProteinsFile

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testReadMultiWordProteinsFileWithErrantSpaces() throws Exception {
		AllTests.standardOut("testReadMultiWordProteinsFileWithErrantSpaces");

		String filename = "testData/multiWordProteinsFileTrailingSpaces.sif";
		InteractionsReader reader = this.getReader(filename);
		reader.read();
		assertTrue(reader.getCount() == 29);

		Interaction[] interactions = reader.getAllInteractions();

		// interaction 16:
		//  26S ubiquitin dependent proteasome
		//     interactsWith
		// I-kappa-B-alpha
		assertTrue(interactions[16].getSource().equals("26S ubiquitin dependent proteasome"));
		assertTrue(interactions[16].getType().equals("interactsWith"));
		assertTrue(interactions[16].numberOfTargets() == 1);
		assertTrue(interactions[16].getTargets()[0].equals("I-kappa-B-alpha"));

		assertTrue(interactions[28].getSource().equals("TRAF6"));
		assertTrue(interactions[28].getType().equals("interactsWith"));
		assertTrue(interactions[28].numberOfTargets() == 3);
		assertTrue(interactions[28].getTargets()[0].equals("RIP2"));
		assertTrue(interactions[28].getTargets()[1].equals("ABCDE oopah"));
		assertTrue(interactions[28].getTargets()[2].equals("HJKOL coltrane"));
	} // testReadMultiWordProteinsFileWithErrantSpaces

	private InteractionsReader getReader(String file) {
		if (runAllTests) {
			file = new String("testData/" + file);
		}

		InteractionsReader reader = new InteractionsReader(file);

		return reader;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(InteractionsReaderTest.class));
	}
} // InteractionsReaderTest
