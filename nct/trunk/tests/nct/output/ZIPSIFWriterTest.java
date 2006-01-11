package nct.output;

import junit.framework.*;
import java.util.*;
import java.io.*;
import java.util.logging.Level;

import nct.graph.*;
import nct.graph.basic.*;
import nct.networkblast.graph.*;
import nct.networkblast.graph.compatibility.*;
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
	    h = new InteractionGraph("examples/junit.inputA.sif");
	    i = new InteractionGraph("examples/junit.inputB.sif");
	    s = new LogLikelihoodScoreModel(2.5, .8, 1e-10);
            List<SequenceGraph<String,Double>> inputSpecies = new ArrayList<SequenceGraph<String,Double>>();
            inputSpecies.add(i);
            inputSpecies.add(h);
            SIFHomologyReader sr = new SIFHomologyReader("examples/junit.compat.sif");
            HomologyGraph homologyGraph = new HomologyGraph(sr,1e-5,inputSpecies);
	    CompatibilityCalculator compatCalc = new AdditiveCompatibilityCalculator(0.01,s);
            g = new CompatibilityGraph(homologyGraph, inputSpecies, s, compatCalc );

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void testserializeList() {
	try {
	    assertTrue("expect compat graph not null", g != null);
	    assertTrue("expect interaction graph not null", h != null);
	    assertTrue("expect interaction graph not null", i != null);
	    assertTrue("expect scoremodel not null", s != null);
	    
	    Graph<String,Double> p = new BasicGraph<String,Double>();       

	    // test a non-empty zip file
	    String nonEmpty = "/tmp/not-empty-out";
	    String nonEmptyZ = nonEmpty + ".zip"; 
	    f = new ZIPSIFWriter<String,Double>(nonEmpty);	
	    List<Graph<String,Double>> solns = sg.searchGraph(g, s);
	    f.write(solns);
	    File nef = new File(nonEmptyZ);
	    assertTrue(nef.exists());
	    assertTrue(nef.length() > 0);
	    nef.delete();

	} catch (IOException e1) {
	    System.out.println(e1.getMessage());
	    e1.printStackTrace();
	    assertTrue("caught exception: " + e1.getMessage(), 1==0);
	}
    }

    public static Test suite() { return new TestSuite( ZIPSIFWriterTest.class ); }

}
