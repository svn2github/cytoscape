package nct.output;

import junit.framework.*;
import java.util.*;
import java.io.*;
import java.util.logging.Level;

import nct.graph.*;
import nct.graph.basic.*;
import nct.networkblast.graph.*;
import nct.networkblast.score.*;
import nct.networkblast.search.*;
import nct.networkblast.NetworkBlast;
import nct.service.homology.sif.*;

public class ZIPSIFWriterTest extends TestCase {
    SearchGraph sg;
    InteractionGraph h, i;
    CompatibilityGraph g;
    ScoreModel s;
    ZIPSIFWriter<String,Double> f;
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

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void testserializeList() {
	try {
	    // 	assertTrue(f.write(null) == null);
	    // check for empty file if empty graph
	    Graph<String,Double> p = new BasicGraph<String,Double>();       
	    String empty = "/tmp/empty-out";
	    String nonEmpty = "/tmp/not-empty-out";
	    f = new ZIPSIFWriter<String,Double>(empty);
	    f.write(new ArrayList());
	    f = new ZIPSIFWriter<String,Double>(nonEmpty);	
	    List<Graph<String,Double>> solns = sg.searchGraph(g, s);
	    f.write(solns);
	    File ef = new File(empty);
	    assertTrue(ef.exists());
	    assertTrue(ef.length() == 0);
	    ef.delete();
	    File nef = new File(nonEmpty);
	    assertTrue(nef.exists());
	    assertTrue(nef.length() > 0);
	    nef.delete();

	} catch (IOException e1) {
	    System.out.println(e1.getMessage());
	    e1.printStackTrace();
	}
    }
}
