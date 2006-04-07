
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
	protected void setUp() {
		NetworkBlast.setUpLogging(Level.WARNING);
	}



	public void testWrite() {
		try {
			Graph<String,Double> g1 = new BasicGraph<String,Double>();
			g1.addNode("a");
			g1.addNode("b");
			g1.addNode("c");
			g1.addEdge("a","b",0.1);
			g1.addEdge("c","b",0.2);
			g1.addEdge("c","a",0.3);

			Graph<String,Double> g2 = new BasicGraph<String,Double>();
			g2.addNode("a");
			g2.addNode("b");
			g2.addNode("c");
			g2.addEdge("a","b",0.1);
			g2.addEdge("c","b",0.2);
			g2.addEdge("c","a",0.3);

			assertNotNull("expect compat graph not null", g1 );
			assertNotNull("expect compat graph not null", g2 );
			
			// test a non-empty zip file
			String nonEmpty = "/tmp/not-empty-out";
			String nonEmptyZ = nonEmpty + ".zip"; 
			ZIPSIFWriter<String,Double> zipWriter = new ZIPSIFWriter<String,Double>(nonEmpty);		
			zipWriter.add(g1,"g1");
			zipWriter.add(g2,"g2");
			zipWriter.write();
			File nef = new File(nonEmptyZ);
			assertTrue(nef.exists());
			assertTrue(nef.length() > 0);
			nef.delete();

		} catch (IOException e1) {
			System.out.println(e1.getMessage());
			e1.printStackTrace();
			fail("caught exception: " + e1.getMessage());
		}
	}

	public static Test suite() { return new TestSuite( ZIPSIFWriterTest.class ); }

}
