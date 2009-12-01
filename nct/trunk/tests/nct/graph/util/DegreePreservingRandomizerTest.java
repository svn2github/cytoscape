//============================================================================
// 
//  file: DegreePreservingRandomizerTest.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================
package nct.graph.util;

import junit.framework.*;

import nct.graph.*;

import nct.networkblast.NetworkBlast;

import java.util.*;
import java.util.logging.Level;


/**
 * A JUnit test class DegreePreservingRandomizer 
 */
public class DegreePreservingRandomizerTest extends TestCase {
	BasicGraph<String, Double> g;
	GraphRandomizer<String, Double> deg;

	protected void setUp() {
		NetworkBlast.setUpLogging(Level.WARNING);
		g = new BasicGraph<String, Double>();

		g.addNode("a");
		g.addNode("b");
		g.addNode("c");
		g.addNode("d");
		g.addNode("e");
		g.addNode("f");

		g.addEdge("a", "f", 1.0);
		g.addEdge("a", "b", 1.0);
		g.addEdge("b", "c", 1.0);
		g.addEdge("b", "d", 1.0);
		g.addEdge("c", "f", 1.0);
		g.addEdge("c", "d", 1.0);
		g.addEdge("d", "e", 1.0);
		g.addEdge("e", "f", 1.0);

		deg = new DegreePreservingRandomizer<String, Double>(new Random(20), false);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testRandomize() {
		System.out.println(g.toString());

		// initial state of graph
		assertEquals(6, g.numberOfNodes());
		assertEquals(8, g.numberOfEdges());

		assertTrue("edge a f", g.isEdge("a", "f"));
		assertTrue("edge a b", g.isEdge("a", "b"));
		assertTrue("edge b c", g.isEdge("b", "c"));
		assertTrue("edge b d", g.isEdge("b", "d"));
		assertTrue("edge c f", g.isEdge("c", "f"));
		assertTrue("edge c d", g.isEdge("c", "d"));
		assertTrue("edge d e", g.isEdge("d", "e"));
		assertTrue("edge e f", g.isEdge("e", "f"));

		assertEquals(2, g.degreeOfNode("a"));
		assertEquals(3, g.degreeOfNode("b"));
		assertEquals(3, g.degreeOfNode("c"));
		assertEquals(3, g.degreeOfNode("d"));
		assertEquals(2, g.degreeOfNode("e"));
		assertEquals(3, g.degreeOfNode("f"));

		deg.randomize(g);

		System.out.println("after randomizing...");
		System.out.println(g.toString());

		// verify degrees are the same
		assertEquals(2, g.degreeOfNode("a"));
		assertEquals(3, g.degreeOfNode("b"));
		assertEquals(3, g.degreeOfNode("c"));
		assertEquals(3, g.degreeOfNode("d"));
		assertEquals(2, g.degreeOfNode("e"));
		assertEquals(3, g.degreeOfNode("f"));

		// check new edges
		assertTrue("edge a c", g.isEdge("a", "c"));
		assertTrue("edge a f", g.isEdge("a", "f"));
		assertTrue("edge b d", g.isEdge("b", "d"));
		assertTrue("edge b e", g.isEdge("b", "e"));
		assertTrue("edge b f", g.isEdge("b", "f"));
		assertTrue("edge c d", g.isEdge("c", "d"));
		assertTrue("edge c e", g.isEdge("c", "e"));
		assertTrue("edge d f", g.isEdge("d", "f"));

		// check old edges
		assertFalse("edge a b", g.isEdge("a", "b"));
		assertFalse("edge b c", g.isEdge("b", "c"));
		assertFalse("edge c f", g.isEdge("c", "f"));
		assertFalse("edge d e", g.isEdge("d", "e"));
		assertFalse("edge e f", g.isEdge("e", "f"));

	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Test suite() {
		return new TestSuite(DegreePreservingRandomizerTest.class);
	}
}
