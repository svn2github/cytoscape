
//============================================================================
// 
//  file: ColorCodingPathSearchTest.java
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

import nct.graph.*;
import nct.graph.basic.*;
import nct.networkblast.score.*;
import nct.networkblast.graph.*;
import nct.networkblast.graph.compatibility.*;
import nct.networkblast.NetworkBlast;
import nct.service.homology.sif.*;



public class ColorCodingPathSearchTest extends TestCase {

	Graph<String,Double> graph; 
	public void setUp() {
		graph = new BasicGraph<String,Double>();
		graph.addNode("a");
		graph.addNode("b");
		graph.addNode("c");
		graph.addNode("d");
		graph.addNode("e");
		graph.addNode("f");
		graph.addNode("g");
		graph.addNode("h");
		graph.addNode("i");

		graph.addEdge("a","b",0.5);
		graph.addEdge("a","g",0.86);
		graph.addEdge("a","h",0.5);


		graph.addEdge("b","c",0.5);
		graph.addEdge("b","d",0.5);
		graph.addEdge("b","e",0.5);
		graph.addEdge("b","f",0.85);
		graph.addEdge("b","g",0.5);
		graph.addEdge("b","h",0.74);

		graph.addEdge("c","d",1.0);
		graph.addEdge("c","e",0.5);
		graph.addEdge("c","f",0.5);
		graph.addEdge("c","g",0.93);

		graph.addEdge("d","e",0.5);
		graph.addEdge("d","f",0.5);
		graph.addEdge("d","i",0.5);
		graph.addEdge("d","h",0.5);

		graph.addEdge("e","f",0.92);
		graph.addEdge("e","g",0.5);

		graph.addEdge("f","h",0.5);
		graph.addEdge("f","i",0.5);

		graph.addEdge("g","h",0.5);
		graph.addEdge("g","i",0.5);

		graph.addEdge("h","i",0.61);
	}

	public void testBasic() {

		System.out.println();
		System.out.println("testBasic");

		ScoreModel<String,Double> edgeScore = new SimpleEdgeScoreModel<String>();
		SearchGraph<String,Double> colorCoding = new ColorCodingPathSearch<String>(4);
		List<Graph<String,Double>> res = colorCoding.searchGraph(graph,edgeScore);
		for( Graph<String,Double> gg : res ) { 
			System.out.println("-------------------------------------------");
			System.out.println(gg.toString());
		}

		Collections.reverse(res);

		assertEquals("num solutions ", 9 ,res.size());

		// verify the scores
		assertEquals(2.79,res.get(0).getScore(),0.0001);
		assertEquals(2.51,res.get(1).getScore(),0.0001);
		assertEquals(2.43,res.get(2).getScore(),0.0001);
		assertEquals(2.42,res.get(3).getScore(),0.0001);
		assertEquals(2.35,res.get(4).getScore(),0.0001);
		assertEquals(2.29,res.get(5).getScore(),0.0001);
		assertEquals(2.28,res.get(6).getScore(),0.0001);
		assertEquals(2.28,res.get(7).getScore(),0.0001);
		assertEquals(2.27,res.get(8).getScore(),0.0001);

		// verify the best path
		assertTrue("best path contains node a ",res.get(0).isNode("a"));
		assertTrue("best path contains node c ",res.get(0).isNode("c"));
		assertTrue("best path contains node d ",res.get(0).isNode("d"));
		assertTrue("best path contains node g ",res.get(0).isNode("g"));

		assertTrue("best path contains edge a g ",res.get(0).isEdge("a","g"));
		assertTrue("best path contains edge c d ",res.get(0).isEdge("c","d"));
		assertTrue("best path contains edge c g ",res.get(0).isEdge("c","g"));

   	}

	public void testConstraint() {
		System.out.println();
		System.out.println("testConstraint");

		ScoreModel<String,Double> edgeScore = new SimpleEdgeScoreModel<String>();
		ColorCodingPathSearch<String> colorCoding = new ColorCodingPathSearch<String>(4);

		Set<String> constraint = new HashSet<String>();
		constraint.add("h");

		colorCoding.setConstraint(constraint,1,1);
		List<Graph<String,Double>> res = colorCoding.searchGraph(graph,edgeScore);

		for( Graph<String,Double> gg : res ) { 
			System.out.println("-------------------------------------------");
			System.out.println(gg.toString());
			assertTrue("graph contains node: " + gg.toString(), gg.isNode("h"));
		}

		Collections.reverse(res);

		assertEquals("num solutions ", 9 ,res.size());

		// verify the scores
		assertEquals(2.51,res.get(0).getScore(),0.0001);
		assertEquals(2.43,res.get(1).getScore(),0.0001);
		assertEquals(2.29,res.get(2).getScore(),0.0001);
		assertEquals(2.24,res.get(3).getScore(),0.0001);
		assertEquals(2.20,res.get(4).getScore(),0.0001);
		assertEquals(2.20,res.get(5).getScore(),0.0001);
		assertEquals(2.17,res.get(6).getScore(),0.0001);
		assertEquals(2.16,res.get(7).getScore(),0.0001);
		assertEquals(2.11,res.get(8).getScore(),0.0001);
	

		// check to see if either/or will work
		constraint.add("i");
		colorCoding.setConstraint(constraint,1,2);
		res = colorCoding.searchGraph(graph,edgeScore);
		for( Graph<String,Double> gg : res ) { 
			System.out.println("-------------------------------------------");
			System.out.println(gg.toString());
			assertTrue("graph contains node: " + gg.toString(), gg.isNode("h") || gg.isNode("i"));
		}
		assertEquals("num solutions ", 9 ,res.size());

		// check to see if both will work
		colorCoding.setConstraint(constraint,2,2);
		res = colorCoding.searchGraph(graph,edgeScore);
		for( Graph<String,Double> gg : res ) { 
			System.out.println("-------------------------------------------");
			System.out.println(gg.toString());
			assertTrue("graph contains node: " + gg.toString(), gg.isNode("h") && gg.isNode("i"));
		}
		assertEquals("num solutions ", 9 ,res.size());

	}

	public void testSegmentation() {
		System.out.println();
		System.out.println("testConstraint");

		ScoreModel<String,Double> edgeScore = new SimpleEdgeScoreModel<String>();
		ColorCodingPathSearch<String> colorCoding = new ColorCodingPathSearch<String>(4);

		Map<String,Integer> minSegmentMap = new HashMap<String,Integer>();
		Map<String,Integer> maxSegmentMap = new HashMap<String,Integer>();

		minSegmentMap.put("a",0);
		minSegmentMap.put("b",0);
		minSegmentMap.put("c",0);
		minSegmentMap.put("d",1);
		minSegmentMap.put("e",1);
		minSegmentMap.put("f",2);
		minSegmentMap.put("g",2);
		minSegmentMap.put("h",3);
		minSegmentMap.put("i",3);
	
		colorCoding.setSegments(minSegmentMap,minSegmentMap);

		List<Graph<String,Double>> res = colorCoding.searchGraph(graph,edgeScore);

		for( Graph<String,Double> gg : res ) { 
			System.out.println("-------------------------------------------");
			System.out.println(gg.toString());
		}

		Collections.reverse(res);

		//assertEquals("num solutions ", 9 ,res.size());

		//fail();
	}


	public void testFullPath() {
		try {			
		SearchGraph<String,Double> sg;
		InteractionGraph h, i ;
		CompatibilityGraph g;
		HomologyGraph homologyGraph;
		ScoreModel<String,Double> edgeScore,logScore;
		NetworkBlast.setUpLogging(Level.WARNING);
		sg = new ColorCodingPathSearch<String>(4);
		h = new InteractionGraph("examples/junit.inputA.sif");
		i = new InteractionGraph("examples/junit.inputB.sif");
		logScore = new LogLikelihoodScoreModel<String>(2.5, .8, 1e-10);
		edgeScore = new SimpleEdgeScoreModel<String>();
		List<SequenceGraph<String,Double>> inputSpecies = new ArrayList<SequenceGraph<String,Double>>();
		inputSpecies.add(i);
		inputSpecies.add(h);
		SIFHomologyReader sr = new SIFHomologyReader("examples/junit.compat.sif");
		homologyGraph = new HomologyGraph(sr,1e-5,inputSpecies);
		CompatibilityCalculator compatCalc = new AdditiveCompatibilityCalculator(0.01,logScore,true);
		g = new CompatibilityGraph(homologyGraph, inputSpecies, compatCalc );


		assertNull(sg.searchGraph(null, edgeScore)); // test for null
		assertNull(sg.searchGraph(g, null));
		assertNull(sg.searchGraph(null, null));
		System.out.println("compat n:" + g.numberOfNodes() + " e:" + g.numberOfEdges() );
		System.out.println("interaction i nodes:" + i.numberOfNodes() + " e:" + i.numberOfEdges() );
		System.out.println("interactionh h nodes:" + h.numberOfNodes() + " e:" + h.numberOfEdges() );
		System.out.println("homologyGraph homol nodes:" + homologyGraph.numberOfNodes() + " e:" + homologyGraph.numberOfEdges() );

		List<Graph<String,Double>> solns = sg.searchGraph(g, edgeScore);
		assertEquals("expect 4 paths, got ", 4, solns.size()); // search 0 size

		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}


	public static Test suite() { return new TestSuite( ColorCodingPathSearchTest.class ); }
}
