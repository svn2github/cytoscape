package nct.networkblast.search;

import junit.framework.*;
import java.util.*;
import java.io.*;
import java.util.logging.Level;

import nct.graph.*;
import nct.graph.basic.*;
import nct.networkblast.score.*;
import nct.networkblast.graph.*;
import nct.networkblast.NetworkBlast;
import nct.service.homology.sif.*;



public class ColorCodingPathSearchTest extends TestCase {
    SearchGraph sg;
    InteractionGraph h, i ;
    CompatibilityGraph g,j;
    ScoreModel s;
    protected void setUp() {
        NetworkBlast.setUpLogging(Level.WARNING);
	sg = new ColorCodingPathSearch(4);
	try {	    
	    h = new InteractionGraph("examples/test.input.sif");
	    i = new InteractionGraph("examples/test.input.sif");
	    s = new LogLikelihoodScoreModel(2.5, .8, 1e-10);
            List<SequenceGraph<String,Double>> inputSpecies = new ArrayList<SequenceGraph<String,Double>>();
	    inputSpecies.add(i);
	    inputSpecies.add(h);
	    SIFHomologyReader sr = new SIFHomologyReader("examples/test.compat.sif");
	    HomologyGraph homologyGraph = new HomologyGraph(sr);
	    for ( SequenceGraph<String,Double> spec : inputSpecies )
	        homologyGraph.addGraph(spec);
												                        g = new CompatibilityGraph(homologyGraph, inputSpecies, 1e-5, s );
												                        j = new CompatibilityGraph(homologyGraph, inputSpecies, 1e-5, s );

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void testsearchGraph() {
	assertTrue(sg.searchGraph(null, s) == null); // test for null
       	assertTrue(sg.searchGraph(g, null) == null);
	assertTrue(sg.searchGraph(null, null) == null);
	assertTrue(sg.searchGraph(j, s).size() == 0); // search 0 size
	BasicGraph<String,Double> p = new BasicGraph<String,Double>();
	p.addNode("a");
	p.addNode("b");
	p.addNode("c");
	List<Graph> solns;
	solns = sg.searchGraph(p, s);
	assertTrue(solns.size() == 0);
	solns = sg.searchGraph(h, s);
	for (Graph x: solns) {
	    System.out.println(x.getId() + ": " + x.getScore());
	    System.out.println(x.getNodes());
	}
       	assertTrue(solns.size() == 1); // TODO
    }
}
