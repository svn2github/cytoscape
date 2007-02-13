/*
 File: GMLReaderTest.java

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

// GMLReaderTest.java

//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.readers;

import cytoscape.AllTests;
import cytoscape.Cytoscape;

import cytoscape.data.readers.GMLReader;

//--------------------------------------------------------------------------------------
import giny.model.RootGraph;

import junit.framework.TestCase;
import junit.framework.TestSuite;


//-----------------------------------------------------------------------------------------
/**
 *
 */
public class GMLReaderTest extends TestCase {
	private static String testDataDir;

	// ------------------------------------------------------------------------------
	/**
	 * Creates a new GMLReaderTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public GMLReaderTest(String name) {
		super(name);

		if (AllTests.runAllTests()) {
			testDataDir = "testData";
		}
	}

	// ------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
	}

	// ------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	// ------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testSmallGraphRead() throws Exception {
		AllTests.standardOut("testSmallGraphRead");

		GMLReader reader = new GMLReader("testData/gal.gml");
		reader.read();

		int[] nodeArray = reader.getNodeIndicesArray();
		int[] edgeArray = reader.getEdgeIndicesArray();

		assertEquals("node count", 11, nodeArray.length);
		assertEquals("edge count", 10, edgeArray.length);
	} // testSmallGraphRead

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testMediumGraphRead() throws Exception {
		AllTests.standardOut("testMediumGraphRead");

		GMLReader reader = new GMLReader("testData/noLabels.gml");
		reader.read();

		int[] nodeArray = reader.getNodeIndicesArray();
		int[] edgeArray = reader.getEdgeIndicesArray();

		assertEquals("node count", 332, nodeArray.length);
		assertEquals("edge count", 362, edgeArray.length);
	} // testMediumGraphRead

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testIllFormattedGML() throws Exception {
		AllTests.standardOut("testIllFormattedGML");

		// this file comes from http://darwin.uvigo.es/software/tcs.html
		// their style of gml has caused problems in the past,
		// particularly newlines within quoted words
		GMLReader reader = new GMLReader("testData/broken_t.gml");
		reader.read();

		int[] nodeArray = reader.getNodeIndicesArray();
		int[] edgeArray = reader.getEdgeIndicesArray();

		assertEquals("node count", 30, nodeArray.length);
		assertEquals("edge count", 19, edgeArray.length);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Error!  must supply path to test data directory on command line");
			Cytoscape.exit(0);
		}

		testDataDir = args[0];

		junit.textui.TestRunner.run(new TestSuite(GMLReaderTest.class));
	}

	// ------------------------------------------------------------------------------
} // GMLReaderTest
