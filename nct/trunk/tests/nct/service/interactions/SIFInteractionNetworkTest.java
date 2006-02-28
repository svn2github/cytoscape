
//============================================================================
// 
//  file: SIFInteractionNetworkTest.java
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



package nct.service.interactions;

import junit.framework.*;

import java.util.*;
import java.util.logging.Level;
import java.io.FileReader;

import nct.graph.Graph;
import nct.graph.basic.BasicGraph;
import nct.parsers.SIFParser;


// A JUnit test class for SIFInteractionNetworkTest.java
public class SIFInteractionNetworkTest extends TestCase {
	Graph<String,Double> g;
	SIFInteractionNetwork s;
	protected void setUp() {

		g = new BasicGraph<String,Double>();
		s = new SIFInteractionNetwork("examples/junit.inputB.sif");
		s.updateGraph(g);
	}

	public void testGraph() {
		assertTrue("Expected 5 nodes: got " + g.numberOfNodes(), g.numberOfNodes() == 5 );
		assertTrue("Expected 5 edges: got " + g.numberOfEdges(), g.numberOfEdges() == 5 );
	}
   
	public static Test suite() {
		return new TestSuite(SIFInteractionNetworkTest.class);
	}
}
