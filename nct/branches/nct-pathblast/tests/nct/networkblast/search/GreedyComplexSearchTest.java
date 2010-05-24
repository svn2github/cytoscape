
//============================================================================
// 
//  file: GreedyComplexSearchTest.java
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
    SearchGraph<String,Double> sg;
    GreedyComplexSearch<String> cg;
    InteractionGraph h, k ;
    ScoreModel<String,Double> s;
    List<Graph<String,Double>> solns;
    protected void setUp() {
    	NetworkBlast.setUpLogging(Level.WARNING);
	sg = new ColorCodingPathSearch<String>(4);
	try {	    
	    h = new InteractionGraph("examples/test.input.sif");
	    k = new InteractionGraph("examples/testNet.input.sif");
	    s = new LogLikelihoodScoreModel<String>(2.5, .8, 1e-10);
	    solns = sg.searchGraph(h, s);
	    cg = new GreedyComplexSearch<String>(4, 15);
	    cg.setSeeds(solns);
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
	List<Graph<String,Double>> cmplx = cg.searchGraph(k,s);
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
	cmplx = cg.searchGraph(k, s);
	assertTrue("expected >0 complexes, got: " + cmplx.size(), cmplx.size() >0 );
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
	assertTrue("expected 2 complexes, got: " + cmplx.size(), cmplx.size() == 2 );
	for (int i = 0; i < cmplx.size(); i++) {
	    assertTrue("expected 15 complex nodes, got: " + cmplx.get(i).numberOfNodes(),
	               cmplx.get(i).numberOfNodes() == 15);
                Set<String> nds = cmplx.get(i).getNodes();
                System.out.println();
                for ( String nd : nds )
                        System.out.print(nd + " ");
                System.out.println();
	}
	
	// ADD MORE TESTS

    }
    public static Test suite() { return new TestSuite( GreedyComplexSearchTest.class ); }
}
