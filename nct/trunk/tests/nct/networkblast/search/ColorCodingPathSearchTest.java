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
    SearchGraph sg;
    InteractionGraph h, i ;
    CompatibilityGraph g;
    HomologyGraph homologyGraph;
    ScoreModel s;
    protected void setUp() {
        NetworkBlast.setUpLogging(Level.WARNING);
	sg = new ColorCodingPathSearch(4);
	try {	    
		h = new InteractionGraph("examples/junit.inputA.sif");
		i = new InteractionGraph("examples/junit.inputB.sif");
		s = new LogLikelihoodScoreModel(2.5, .8, 1e-10);
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
	assertTrue(sg.searchGraph(null, s) == null); // test for null
       	assertTrue(sg.searchGraph(g, null) == null);
	assertTrue(sg.searchGraph(null, null) == null);
   }

    public void testsearchGraph() {
    	System.out.println("compat n:" + g.numberOfNodes() + " e:" + g.numberOfEdges() );
	System.out.println("interaction i n:" + i.numberOfNodes() + " e:" + i.numberOfEdges() );
	System.out.println("eractionh n:" + h.numberOfNodes() + " e:" + h.numberOfEdges() );
	System.out.println("homologyGraph n:" + homologyGraph.numberOfNodes() + " e:" + homologyGraph.numberOfEdges() );

	List<Graph> solns = sg.searchGraph(g, s);
	assertTrue("expect 4 paths, got " + solns.size(), solns.size() == 4); // search 0 size
    }
    public static Test suite() { return new TestSuite( ColorCodingPathSearchTest.class ); }
}
