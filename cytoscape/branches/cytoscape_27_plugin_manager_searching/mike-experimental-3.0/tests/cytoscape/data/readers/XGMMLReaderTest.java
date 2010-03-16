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
package cytoscape.data.readers;

import cytoscape.AllTests;
import cytoscape.Cytoscape;

import cytoscape.data.readers.XGMMLReader;

import org.cytoscape.RootGraph;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;


/**
 *
 */
public class XGMMLReaderTest extends TestCase {
	private static String testDataDir;

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

		junit.textui.TestRunner.run(new TestSuite(XGMMLReaderTest.class));
	}

	/**
	 * Creates a new XGMMLReaderTest object.
	 *
	 * @param arg0  DOCUMENT ME!
	 */
	public XGMMLReaderTest(String arg0) {
		super(arg0);

		if (AllTests.runAllTests()) {
			testDataDir = "testData";
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testXGMMLGraphRead() throws Exception {
		AllTests.standardOut("testXGMMLGraphRead");

		XGMMLReader reader = new XGMMLReader("testData/galFiltered2.xgmml");

		File testfile = new File("testData/galFiltered2.xgmml");

		if (testfile.canRead()) {
			System.out.println("Reading XGMML: " + testfile.getAbsolutePath());

			RootGraph network = Cytoscape.getRootGraph();
			network.removeNodes(network.nodesList());
			reader.read();

			if (network == null) {
				System.out.println("Root Graph is null!");

				return;
			}

			System.out.println("XGMMLReader: Node count = " + network.getNodeCount());
			System.out.println("XGMMLReader: Edge count = " + network.getEdgeCount());

			assertTrue("XGMMLReader: Node count, expect 331, got " + network.getNodeCount(),
			           network.getNodeCount() == 331);
			assertTrue("XGMMLReader: Edge count, expect 362, got " + network.getEdgeCount(),
			           network.getEdgeCount() == 362);
		} else {
			System.out.println("No such file");
		}
	} // testGraphRead

	/* Too large?

	public void testXGMMLHugeGraphRead() throws Exception {
	    AllTests.standardOut("testXGMMLHugeGraphRead");
	    XGMMLReader reader = new XGMMLReader("testData/BINDyeast.xgmml");

	    File testfile = new File("testData/BINDyeast.xgmml");
	    if(testfile.canRead()) {
	        System.out.println("Reading XGMML: " + testfile.getAbsolutePath());
	        RootGraph network = Cytoscape.getRootGraph();
	        network.removeNodes(network.nodesList());
	        reader.read();


	        if(network == null) {
	            System.out.println("Root Graph is null!");
	            return;
	        }
	        System.out.println("XGMMLReader: Node count = " + network.getNodeCount());
	        System.out.println("XGMMLReader: Edge count = " + network.getEdgeCount());

	        assertTrue("XGMMLReader: Node count, expect 23505, got " + network.getNodeCount(),
	                network.getNodeCount() == 23505);
	        assertTrue("XGMMLReader: Edge count, expect 60457, got " + network.getEdgeCount(),
	                network.getEdgeCount() == 60457);
	    } else {
	        System.out.println("No such file");
	    }


	} // testXGMMLHugeGraphRead

	*/
}
