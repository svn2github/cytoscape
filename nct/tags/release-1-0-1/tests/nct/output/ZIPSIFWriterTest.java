
//============================================================================
// 
//  file: ZIPSIFWriterTest.java
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
    SearchGraph<String,Double> sg;
    InteractionGraph h, i;
    CompatibilityGraph g;
    ScoreModel<String,Double> s;
    ZIPSIFWriter<String,Double> f;
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
            HomologyGraph homologyGraph = new HomologyGraph(sr,1e-5,inputSpecies);
	    CompatibilityCalculator compatCalc = new AdditiveCompatibilityCalculator(0.01,s,true);
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
	    int ct = 0;
	    List<Graph<String,Double>> sols = sg.searchGraph(g, s);
	    for (Graph<String,Double> sol : sols) 
	    	f.add(sol,"sol_" + ct++);
	    f.write();
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
