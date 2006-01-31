
//============================================================================
// 
//  file: SIFInteractionNetworkTest.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
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
