//============================================================================
// 
//  file: EdgeWeightShuffleTest.java
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
 * A JUnit test class for EdgeWeightShuffle
 */
public class EdgeWeightShuffleTest extends TestCase {
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
		g.addEdge("a", "b", 2.0);
		g.addEdge("b", "c", 3.0);
		g.addEdge("b", "d", 4.0);
		g.addEdge("c", "f", 5.0);
		g.addEdge("c", "d", 6.0);
		g.addEdge("d", "e", 7.0);
		g.addEdge("e", "f", 8.0);

		deg = new EdgeWeightShuffle<String, Double>(new Random(10));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testRandomize() {
		System.out.println("before shuffle");
		System.out.println(g.toString());

		assertEquals("edge a f ", 1.0, g.getEdgeWeight("a", "f"));
		assertEquals("edge a b ", 2.0, g.getEdgeWeight("a", "b"));
		assertEquals("edge b c ", 3.0, g.getEdgeWeight("b", "c"));
		assertEquals("edge b d ", 4.0, g.getEdgeWeight("b", "d"));
		assertEquals("edge c f ", 5.0, g.getEdgeWeight("c", "f"));
		assertEquals("edge c d ", 6.0, g.getEdgeWeight("c", "d"));
		assertEquals("edge d e ", 7.0, g.getEdgeWeight("d", "e"));
		assertEquals("edge e f ", 8.0, g.getEdgeWeight("e", "f"));

		deg.randomize(g);

		System.out.println("after shuffle");
		System.out.println(g.toString());

		assertEquals("edge a f ", 5.0, g.getEdgeWeight("a", "f"));
		assertEquals("edge a b ", 7.0, g.getEdgeWeight("a", "b"));
		assertEquals("edge b c ", 8.0, g.getEdgeWeight("b", "c"));
		assertEquals("edge b d ", 2.0, g.getEdgeWeight("b", "d"));
		assertEquals("edge c f ", 6.0, g.getEdgeWeight("c", "f"));
		assertEquals("edge c d ", 1.0, g.getEdgeWeight("c", "d"));
		assertEquals("edge d e ", 4.0, g.getEdgeWeight("d", "e"));
		assertEquals("edge e f ", 3.0, g.getEdgeWeight("e", "f"));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Test suite() {
		return new TestSuite(EdgeWeightShuffleTest.class);
	}
}
