
//============================================================================
// 
//  file: NewComplexSearchTest.java
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


public class NewComplexSearchTest extends TestCase {


	public void testSimpleSearch() {
		System.out.println("===================================================");

        	Graph<WeightedNode<Integer,Double>,Double> weightedNodeGraph = 
			new BasicGraph<WeightedNode<Integer,Double>,Double>();

		WeightedNode<Integer,Double> a = new BasicWeightedNode<Integer,Double>(1,0.0);
		WeightedNode<Integer,Double> b = new BasicWeightedNode<Integer,Double>(2,0.0);
		WeightedNode<Integer,Double> c = new BasicWeightedNode<Integer,Double>(3,0.0);

		weightedNodeGraph.addNode(a);
		weightedNodeGraph.addNode(b);
		weightedNodeGraph.addNode(c);

		weightedNodeGraph.addEdge(a,b,0.5,"a to b");
		weightedNodeGraph.addEdge(b,c,1.0,"b to c");
		weightedNodeGraph.addEdge(c,a,1.5,"c to a");

		System.out.println(weightedNodeGraph);

        	ScoreModel<WeightedNode<Integer,Double>,Double> model = 
			new SimpleScoreModel<WeightedNode<Integer,Double>>();


       		NewComplexSearch<WeightedNode<Integer,Double>> greedyComplexes = 
			new NewComplexSearch<WeightedNode<Integer,Double>>(1,3);
	       	List<Graph<WeightedNode<Integer,Double>,Double>> results;

        	results = greedyComplexes.searchGraph(weightedNodeGraph,model);
		Collections.sort(results);
        	System.out.println("Printing out results");
		System.out.println(results);
		assertEquals(3, results.size());
		assertEquals(3.0, results.get(0).getScore());
		assertEquals(3.0, results.get(1).getScore());
		assertEquals(3.0, results.get(2).getScore());
		assertEquals(3, results.get(0).numberOfNodes());
		assertEquals(3, results.get(0).numberOfEdges());

		greedyComplexes = new NewComplexSearch<WeightedNode<Integer,Double>>(2,3);
        	results = greedyComplexes.searchGraph(weightedNodeGraph,model);
		Collections.sort(results);
        	System.out.println("Printing out results");
		System.out.println(results);
		assertEquals(3, results.size());
		assertEquals(3.0, results.get(0).getScore());
		assertEquals(3.0, results.get(1).getScore());
		assertEquals(3.0, results.get(2).getScore());
		assertEquals(3, results.get(0).numberOfNodes());
		assertEquals(3, results.get(0).numberOfEdges());

		greedyComplexes = new NewComplexSearch<WeightedNode<Integer,Double>>(1,2);
        	results = greedyComplexes.searchGraph(weightedNodeGraph,model);
		Collections.sort(results);
        	System.out.println("Printing out results");
		System.out.println(results);
		assertEquals(3, results.size());
		assertEquals(1.0, results.get(0).getScore());
		assertEquals(1.5, results.get(1).getScore());
		assertEquals(1.5, results.get(2).getScore());
		assertEquals(2, results.get(0).numberOfNodes());
		assertEquals(1, results.get(0).numberOfEdges());
	}

	public void testSearch() {
		System.out.println("===================================================");
		WeightedNode<String,Double> a = new BasicWeightedNode<String,Double>("a",0.0);
		WeightedNode<String,Double> b = new BasicWeightedNode<String,Double>("b",0.0);
		WeightedNode<String,Double> c = new BasicWeightedNode<String,Double>("c",0.0);
		WeightedNode<String,Double> d = new BasicWeightedNode<String,Double>("d",0.0);
		WeightedNode<String,Double> e = new BasicWeightedNode<String,Double>("e",0.0);
		WeightedNode<String,Double> f = new BasicWeightedNode<String,Double>("f",0.0);
		WeightedNode<String,Double> g = new BasicWeightedNode<String,Double>("g",0.0);
		WeightedNode<String,Double> h = new BasicWeightedNode<String,Double>("h",0.0);

        	Graph<WeightedNode<String,Double>,Double> simpleGraph = 
			new BasicGraph<WeightedNode<String,Double>,Double>();
		simpleGraph.addNode(a); // seed
		simpleGraph.addNode(b); // seed
		simpleGraph.addNode(c); // seed
		simpleGraph.addNode(d); // seed
		simpleGraph.addNode(e);
		simpleGraph.addNode(f);
		simpleGraph.addNode(g);
		simpleGraph.addNode(h);

		// a is min node - can't remove because it's a seed
		// b is next min node - can't remove because it's connected 
		simpleGraph.addEdge(a,b, 1.0); // seed
		simpleGraph.addEdge(b,c, 1.0); // seed
		simpleGraph.addEdge(c,d, 3.0); // seed

		// e is max potential node
		simpleGraph.addEdge(d,e, 3.0); // e is max node
		simpleGraph.addEdge(c,e, 3.0);

		simpleGraph.addEdge(c,f, 8.0);

		simpleGraph.addEdge(b,h, 1.0);

		// g is new neighbor
		simpleGraph.addEdge(g,e, 10.0);

        	Graph<WeightedNode<String,Double>,Double> seedGraph = 
			new BasicGraph<WeightedNode<String,Double>,Double>();
		seedGraph.addNode(a); // seed
		seedGraph.addNode(b); // seed
		seedGraph.addNode(c); // seed
		seedGraph.addNode(d); // seed
		seedGraph.addEdge(a,b, 1.0); // seed
		seedGraph.addEdge(b,c, 1.0); // seed
		seedGraph.addEdge(c,d, 3.0); // seed


		SimpleScoreModel<WeightedNode<String,Double>> model = 
			new SimpleScoreModel<WeightedNode<String,Double>>();

		List<Graph<WeightedNode<String,Double>,Double>> seeds = 
			new ArrayList<Graph<WeightedNode<String,Double>,Double>>();
		seeds.add(seedGraph);

       		NewComplexSearch<WeightedNode<String,Double>> greedyComplexes = 
			new NewComplexSearch<WeightedNode<String,Double>>(3,6,false,seeds);

		// expected order:
		// add f
		// add e 
		// remove f 
		// add g 
	       	List<Graph<WeightedNode<String,Double>,Double>> results = 
			greedyComplexes.searchGraph(simpleGraph,model);

		System.out.println("Printing results");
		System.out.println(results);

		assertEquals( 1, results.size() );

        	Graph<WeightedNode<String,Double>,Double> resultGraph = results.get(0);

		// make sure min and max sizes are respected
		assertTrue( resultGraph.numberOfNodes() >= 3 );
		assertTrue( resultGraph.numberOfNodes() <= 6 );
		assertEquals( resultGraph.numberOfNodes(), 6 );

		assertEquals( 21.0, resultGraph.getScore());

		// make sure seed nodes are included
		assertTrue( resultGraph.isNode(a) );
		assertTrue( resultGraph.isNode(b) );
		assertTrue( resultGraph.isNode(c) );
		assertTrue( resultGraph.isNode(d) );

		// make sure best potential node is included
		assertTrue( resultGraph.isNode(e) );

		// make sure next best potential node is included
		// also tests updating of potentials 
		// g is not initally a potential node
		assertTrue( resultGraph.isNode(g) );

	}

	// This search tests to make sure we don't add the maximum
	// number of nodes. This should only happen if some of the
	// scores are negative.
	public void testNormalizedSearch() {

		System.out.println("===================================================");
		WeightedNode<String,Double> a = new BasicWeightedNode<String,Double>("a",0.0);
		WeightedNode<String,Double> b = new BasicWeightedNode<String,Double>("b",0.0);
		WeightedNode<String,Double> c = new BasicWeightedNode<String,Double>("c",0.0);
		WeightedNode<String,Double> d = new BasicWeightedNode<String,Double>("d",0.0);
		WeightedNode<String,Double> e = new BasicWeightedNode<String,Double>("e",0.0);
		WeightedNode<String,Double> f = new BasicWeightedNode<String,Double>("f",0.0);
		WeightedNode<String,Double> g = new BasicWeightedNode<String,Double>("g",0.0);

        	Graph<WeightedNode<String,Double>,Double> simpleGraph = 
			new BasicGraph<WeightedNode<String,Double>,Double>();
		simpleGraph.addNode(a); // seed
		simpleGraph.addNode(b); // seed
		simpleGraph.addNode(c); // seed
		simpleGraph.addNode(d); 
		simpleGraph.addNode(e);
		simpleGraph.addNode(f);
		simpleGraph.addNode(g);

		simpleGraph.addEdge(a,b, 1.0); // seed
		simpleGraph.addEdge(b,c, 1.0); // seed
		simpleGraph.addEdge(c,f, 1.0); // should be added to solution 
		simpleGraph.addEdge(b,e, -1.0); // should NOT be added
		simpleGraph.addEdge(a,d, 0.0);  // should NOT be added
		simpleGraph.addEdge(a,g, -1.0);  // should NOT be added


        	Graph<WeightedNode<String,Double>,Double> seedGraph = 
			new BasicGraph<WeightedNode<String,Double>,Double>();
		seedGraph.addNode(a); // seed
		seedGraph.addNode(b); // seed
		seedGraph.addNode(c); // seed
		seedGraph.addEdge(a,b, 1.0); 
		seedGraph.addEdge(b,c, 1.0); 

		SimpleScoreModel<WeightedNode<String,Double>> model = 
			new SimpleScoreModel<WeightedNode<String,Double>>();

		List<Graph<WeightedNode<String,Double>,Double>> seeds = 
			new ArrayList<Graph<WeightedNode<String,Double>,Double>>();
		seeds.add(seedGraph);

       		NewComplexSearch<WeightedNode<String,Double>> greedyComplexes = 
			new NewComplexSearch<WeightedNode<String,Double>>(3,6,false,seeds);

		// expected order:
		// add f
	       	List<Graph<WeightedNode<String,Double>,Double>> results = 
			greedyComplexes.searchGraph(simpleGraph,model);

		System.out.println("Printing results");
		System.out.println(results);

		assertEquals( 1, results.size() );

        	Graph<WeightedNode<String,Double>,Double> resultGraph = results.get(0);

		assertEquals( 3.0, resultGraph.getScore() );
		assertTrue( resultGraph.isNode(a) );
		assertTrue( resultGraph.isNode(b) );
		assertTrue( resultGraph.isNode(c) );
		assertTrue( resultGraph.isNode(f) );
		assertTrue( !resultGraph.isNode(e) );
		assertTrue( !resultGraph.isNode(d) );
		assertTrue( !resultGraph.isNode(g) );
	}

	public void testNodeScores() {

		System.out.println("===================================================");
		WeightedNode<String,Double> a = new BasicWeightedNode<String,Double>("a",1.0);
		WeightedNode<String,Double> b = new BasicWeightedNode<String,Double>("b",1.0);
		WeightedNode<String,Double> c = new BasicWeightedNode<String,Double>("c",1.0);
		WeightedNode<String,Double> d = new BasicWeightedNode<String,Double>("d",1.0);
		WeightedNode<String,Double> e = new BasicWeightedNode<String,Double>("e",2.0);
		WeightedNode<String,Double> f = new BasicWeightedNode<String,Double>("f",2.0);
		WeightedNode<String,Double> g = new BasicWeightedNode<String,Double>("g",4.0);

        	Graph<WeightedNode<String,Double>,Double> simpleGraph = 
			new BasicGraph<WeightedNode<String,Double>,Double>();
		simpleGraph.addNode(a); // seed
		simpleGraph.addNode(b); // seed
		simpleGraph.addNode(c); // seed
		simpleGraph.addNode(d); // seed 
		simpleGraph.addNode(e);
		simpleGraph.addNode(f);
		simpleGraph.addNode(g);

		simpleGraph.addEdge(a,b, 1.0); // seed
		simpleGraph.addEdge(b,c, 1.0); // seed
		simpleGraph.addEdge(c,d, 1.0); // seed
		simpleGraph.addEdge(d,e, 1.0);
		simpleGraph.addEdge(c,e, 1.0);
		simpleGraph.addEdge(b,f, 1.0);
		simpleGraph.addEdge(a,g, 1.0);


        	Graph<WeightedNode<String,Double>,Double> seedGraph = 
			new BasicGraph<WeightedNode<String,Double>,Double>();
		seedGraph.addNode(a); // seed
		seedGraph.addNode(b); // seed
		seedGraph.addNode(c); // seed
		seedGraph.addNode(d); // seed
		seedGraph.addEdge(a,b, 1.0); 
		seedGraph.addEdge(b,c, 1.0); 
		seedGraph.addEdge(c,d, 1.0); 

		SimpleScoreModel<WeightedNode<String,Double>> model = 
			new SimpleScoreModel<WeightedNode<String,Double>>();

		List<Graph<WeightedNode<String,Double>,Double>> seeds = 
			new ArrayList<Graph<WeightedNode<String,Double>,Double>>();
		seeds.add(seedGraph);

       		NewComplexSearch<WeightedNode<String,Double>> greedyComplexes = 
			new NewComplexSearch<WeightedNode<String,Double>>(3,6,false,seeds);

		// expected order:
		// add g
		// add e
	       	List<Graph<WeightedNode<String,Double>,Double>> results = 
			greedyComplexes.searchGraph(simpleGraph,model);

		System.out.println("Printing results");
		System.out.println(results);

		assertEquals( 1, results.size() );

        	Graph<WeightedNode<String,Double>,Double> resultGraph = results.get(0);

		assertEquals( 16.0, resultGraph.getScore() );
		assertTrue( resultGraph.isNode(a) );
		assertTrue( resultGraph.isNode(b) );
		assertTrue( resultGraph.isNode(c) );
		assertTrue( resultGraph.isNode(d) );
		assertTrue( resultGraph.isNode(e) );
		assertTrue( resultGraph.isNode(g) );
		assertTrue( !resultGraph.isNode(f) );
	}

	public static Test suite() { return new TestSuite( NewComplexSearchTest.class ); }
}
