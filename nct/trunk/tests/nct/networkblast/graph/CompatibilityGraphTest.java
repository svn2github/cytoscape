package nct.networkblast.graph;

import junit.framework.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;

import nct.networkblast.score.*;
import nct.networkblast.graph.compatibility.*;
import nct.graph.basic.*;
import nct.graph.*;
import nct.service.homology.sif.*;
import nct.networkblast.NetworkBlast;

public class CompatibilityGraphTest extends TestCase {
    CompatibilityGraph g;
    InteractionGraph h, i;
    HomologyGraph homologyGraph;
    protected void setUp() {
	try {
	    NetworkBlast.setUpLogging(Level.CONFIG);
	    i = new InteractionGraph("examples/junit.inputA.sif");
	    h = new InteractionGraph("examples/junit.inputB.sif");
	    List<SequenceGraph<String,Double>> inputSpecies = new ArrayList<SequenceGraph<String,Double>>();
	    inputSpecies.add(i);
	    inputSpecies.add(h);
	    SIFHomologyReader sr = new SIFHomologyReader("examples/junit.compat.sif");
	    homologyGraph = new HomologyGraph(sr, 1e-5, inputSpecies);
	    LogLikelihoodScoreModel lls = new LogLikelihoodScoreModel(2.5,0.8,1e-10);
	    CompatibilityCalculator compatCalc = new AdditiveCompatibilityCalculator(0.01,lls);
	    g = new CompatibilityGraph(homologyGraph, inputSpecies, lls, compatCalc );
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
    }
    public void testnumberOfNodes() {
	System.out.println("hom num nodes:  " + homologyGraph.numberOfNodes());
	System.out.println("hom num edges:  " + homologyGraph.numberOfEdges());
	System.out.println("g num nodes:  " + g.numberOfNodes());
	System.out.println("h num nodes:  " + h.numberOfNodes());
	System.out.println("i num nodes:  " + i.numberOfNodes());

	assertTrue("g is null when it shouldn't be",g != null);
	assertTrue("expected 9 nodes, got: " + homologyGraph.numberOfNodes(), 
	           homologyGraph.numberOfNodes() == 9);
	assertTrue("expected 4 nodes, got: " + g.numberOfNodes(), 
	           g.numberOfNodes() == 4);
	assertTrue("expected 5 nodes, got: " + h.numberOfNodes(),
	           h.numberOfNodes() == 5);
	assertTrue("expected 4 nodes, got: " + i.numberOfNodes(),
	           i.numberOfNodes() == 4);
    }

    public void testnumberOfEdges() {
	System.out.println("hom num edges:  " + homologyGraph.numberOfEdges());
	System.out.println("g num edges:  " + g.numberOfEdges());
	System.out.println("h num edges:  " + h.numberOfEdges());
	System.out.println("i num edges:  " + i.numberOfEdges());

	Set<Edge<String,Double>> edges = g.getEdges();
	for (Edge e : edges) {
		System.out.print("src node: " + e.getSourceNode()); 
		System.out.print("  target node: " + e.getTargetNode()); 
		System.out.println("  weight: " + e.getWeight()); 
	}

	assertTrue("expected 5 edges, got: " + g.numberOfEdges(),
	           g.numberOfEdges() == 5);
	assertTrue("expected 5 edges, got: " + g.numberOfEdges(),
	           h.numberOfEdges() == 5);
	assertTrue("expected 3 edges, got: " + g.numberOfEdges(),
	           i.numberOfEdges() == 3);

    }

    public void testgetEdgeWeight() {
	System.out.println("A1|B1 A1|B2: " + g.getEdgeWeight("A1|B1", "A1|B2") );
	System.out.println("A1|B1 A1|B3: " + g.getEdgeWeight("A1|B1", "A1|B3") );
	System.out.println("A1|B1 A2|B1: " + g.getEdgeWeight("A1|B1", "A2|B1") );
	
	//assertTrue(g.getEdgeWeight("a|a", "e|e") == -2.0);
	//assertTrue(g.getEdgeWeight("a|a", "b|b") == .25);
	//assertTrue(g.getEdgeWeight("a|a", "c|c") == .0625);
	//assertTrue(g.addEdge("e|e", "a|a", 0));
	//assertTrue(g.getEdgeWeight("e|e", "b|b") == 0); // best path no matter what the distance?
    }

    public void testgetEdgeDescription() {
	assertTrue("g is null when it shouldn't be", g != null);
	assertTrue("edge desc for two null edges is not null", g.getEdgeDescription(null, null) == null);
	System.out.println("node set " + g.getNodes());
	System.out.println("edge desc A3|B1 A1|B2: " +g.getEdgeDescription("A3|B1", "A1|B2"));
	System.out.println("edge desc A2|B1 A1|B1: " +g.getEdgeDescription("A2|B1", "A1|B1"));
	assertTrue("edge desc expected: 21 while we got: " + g.getEdgeDescription("A3|B1", "A1|B2"),
	            g.getEdgeDescription("A3|B1", "A1|B2").equals("21"));
	assertTrue("edge desc expected: 10 while we got: " + g.getEdgeDescription("A2|B1", "A1|B1"),
	            g.getEdgeDescription("A2|B1", "A1|B1").equals("10"));
    }
}
