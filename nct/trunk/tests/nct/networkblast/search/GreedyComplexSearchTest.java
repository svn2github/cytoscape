
//============================================================================
// 
//  file: GreedyComplexSearchTest.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.networkblast.search;

import junit.framework.*;
import java.util.*;
import java.io.*;
import java.util.logging.Level;

import nct.networkblast.graph.*;
import nct.networkblast.graph.compatibility.*;
import nct.networkblast.score.*;
import nct.networkblast.NetworkBlast;
import nct.graph.*;
import nct.graph.basic.*;
import nct.service.homology.sif.*;


public class GreedyComplexSearchTest extends TestCase {
    SearchGraph sg, cg;
    InteractionGraph h, k ;
    ScoreModel s;
    List<Graph<String,Double>> solns;
    protected void setUp() {
    	NetworkBlast.setUpLogging(Level.WARNING);
	sg = new ColorCodingPathSearch(4);
	try {	    
	    h = new InteractionGraph("examples/test.input.sif");
	    k = new InteractionGraph("examples/testNet.input.sif");
	    s = new LogLikelihoodScoreModel(2.5, .8, 1e-10);
	    solns = sg.searchGraph(h, s);
	    cg = new GreedyComplexSearch(solns, 4, 15);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    public void testnull() {
	assertTrue(cg.searchGraph(null, s) == null); // null tests
	assertTrue(cg.searchGraph(h, null) == null);
	assertTrue(cg.searchGraph(null, null) == null);

	Graph<String,Double> p = new BasicGraph<String,Double>();
	assertTrue(cg.searchGraph(p, s).size() == 0); // check for 0 size on return
    }

    public void testsearchGraph() {

	assertTrue("expected 14 int graph nodes, got: " + k.numberOfNodes(), k.numberOfNodes() == 14 );
	assertTrue("expected 13 int graph edges, got: " + k.numberOfEdges(), k.numberOfEdges() == 13 );

	// Only 1 complex at this point 
	List<Graph> cmplx = cg.searchGraph(k,s);
	assertTrue("expected 1 complex, got: " + cmplx.size(), cmplx.size() == 1 );
	
	// The node is not connected, so we shouldn't get another complex 
	k.addNode("o");
	cmplx = cg.searchGraph(k,s);
	assertTrue("expected 1 complex, got: " + cmplx.size(), cmplx.size() == 1 );

	// This edge just extends the complex we already get.
	k.addEdge("o", "n", .5);
	cmplx = cg.searchGraph(k,s);
	assertTrue("expected 1 complex, got: " + cmplx.size(), cmplx.size() == 1 );
	for (int i = 0; i < cmplx.size(); i++) {
	    assertTrue("expected 15 complex nodes, got: " + cmplx.get(i).numberOfNodes(),
	               cmplx.get(i).numberOfNodes() == 15);
	}

	// still just extending/improving
	k.addNode("p");
	k.addEdge("p", "k", 1.0);
	assertTrue("expected >0 complexes, got: " + cmplx.size(), cmplx.size() >0 );
	cmplx = cg.searchGraph(k, s);
	for (int i = 0; i < cmplx.size(); i++) {
	    assertTrue("expected 15 complex nodes, got: " + cmplx.get(i).numberOfNodes(),
	               cmplx.get(i).numberOfNodes() == 15);
	}
	
	k.addNode("q");
	k.addNode("r");
	k.addEdge("a", "r", .8);
	k.addEdge("q", "k", .9);
	
	// now we get more complexes
	cmplx = cg.searchGraph(k, s);
	assertTrue("expected >1 complexes, got: " + cmplx.size(), cmplx.size() > 1 );
	for (int i = 0; i < cmplx.size(); i++) {
	    assertTrue("expected 15 complex nodes, got: " + cmplx.get(i).numberOfNodes(),
	               cmplx.get(i).numberOfNodes() == 15);
	}

	// ADD MORE TESTS

    }
    public static Test suite() { return new TestSuite( GreedyComplexSearchTest.class ); }
}
