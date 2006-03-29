
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



public class NewColorCodingPathSearchTest extends TestCase {

    public void testsearchGraph() {
   	Graph<String,Double> graph = new BasicGraph<String,Double>();
	graph.addNode("a");
	graph.addNode("b");
	graph.addNode("c");
	graph.addNode("d");
	graph.addNode("e");
	graph.addNode("f");
	graph.addNode("g");
	graph.addNode("h");
	graph.addNode("i");
	graph.addNode("j");
	graph.addNode("k");

	graph.addEdge("a","b",1.0);

	ScoreModel<String,Double> model = new SimpleEdgeScoreModel<String>();
	SearchGraph<String,Double> search = new ColorCodingPathSearch<String>(4,5);
	List<Graph<String,Double>> results = search.searchGraph(graph,model); 

	

    }
    public static Test suite() { return new TestSuite( NewColorCodingPathSearchTest.class ); }
}
