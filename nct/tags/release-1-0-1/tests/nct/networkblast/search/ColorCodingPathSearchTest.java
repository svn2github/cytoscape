
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
    SearchGraph<String,Double> sg;
    InteractionGraph h, i ;
    CompatibilityGraph g;
    HomologyGraph homologyGraph;
    ScoreModel<String,Double> s;
    protected void setUp() {
        NetworkBlast.setUpLogging(Level.WARNING);
	sg = new ColorCodingPathSearch<String>(4);
	try {	    
		h = new InteractionGraph("examples/junit.inputA.sif");
		i = new InteractionGraph("examples/junit.inputB.sif");
		s = new LogLikelihoodScoreModel<String>(2.5, .8, 1e-10);
		List<SequenceGraph<String,Double>> inputSpecies = new ArrayList<SequenceGraph<String,Double>>();
		inputSpecies.add(i);
		inputSpecies.add(h);
		SIFHomologyReader sr = new SIFHomologyReader("examples/junit.compat.sif");
		homologyGraph = new HomologyGraph(sr,1e-5,inputSpecies);
		CompatibilityCalculator compatCalc = new AdditiveCompatibilityCalculator(0.01,s,true);
		g = new CompatibilityGraph(homologyGraph, inputSpecies, s, compatCalc );

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void testsearchInit() {
	assertNull(sg.searchGraph(null, s)); // test for null
       	assertNull(sg.searchGraph(g, null));
	assertNull(sg.searchGraph(null, null));
   }

    public void testsearchGraph() {
    	System.out.println("compat n:" + g.numberOfNodes() + " e:" + g.numberOfEdges() );
	System.out.println("interaction i n:" + i.numberOfNodes() + " e:" + i.numberOfEdges() );
	System.out.println("eractionh n:" + h.numberOfNodes() + " e:" + h.numberOfEdges() );
	System.out.println("homologyGraph n:" + homologyGraph.numberOfNodes() + " e:" + homologyGraph.numberOfEdges() );

	List<Graph<String,Double>> solns = sg.searchGraph(g, s);
	assertEquals("expect 4 paths, got ", 4, solns.size()); // search 0 size
    }
    public static Test suite() { return new TestSuite( ColorCodingPathSearchTest.class ); }
}
