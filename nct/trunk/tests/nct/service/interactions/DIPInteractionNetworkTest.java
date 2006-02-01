
//============================================================================
// 
//  file: DIPInteractionNetworkTest.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
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
