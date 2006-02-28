
//============================================================================
// 
//  file: DIPInteractionNetworkTest.java
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



package nct.service.interactions;

import junit.framework.*;

import java.util.*;
import java.util.logging.Level;
import java.io.FileReader;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;

import nct.graph.*;
import nct.graph.basic.*;

// A JUnit test class for DIPInteractionNetworkTest.java
public class DIPInteractionNetworkTest extends TestCase {
	Graph<String,Double> chicken;
	DIPInteractionNetwork chickenNet;
	protected void setUp() {

		chicken = new BasicGraph<String,Double>();
		chickenNet = new DIPInteractionNetwork("Gallus gallus");
		chickenNet.updateGraph(chicken);

		try {
		XMLReader xr = XMLReaderFactory.createXMLReader();
		xr.setContentHandler(chickenNet);
		xr.parse(new InputSource(new FileReader("examples/Gallus_gallus.xin")));
		} catch (Exception e) { e.printStackTrace(); }
	}

	public void testParse() {
		assertTrue("expect 8 nodes, got: " + chicken.numberOfNodes(),chicken.numberOfNodes() == 8);
		assertTrue("expect 3 edge, got: " + chicken.numberOfEdges(),chicken.numberOfEdges() == 3);
	}
   
	public static Test suite() {
		return new TestSuite(DIPInteractionNetworkTest.class);
	}
}
