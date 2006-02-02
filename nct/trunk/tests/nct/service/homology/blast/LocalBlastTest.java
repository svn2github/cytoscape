
//============================================================================
// 
//  file: LocalBlastTest.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.service.homology.blast;

import junit.framework.*;

import java.util.*;
import java.util.logging.Level;
import java.io.*;
import java.lang.*;

import nct.graph.*;
import nct.graph.basic.*;
import nct.service.synonyms.*;
import nct.service.interactions.*;
import nct.xml.*;

import org.xml.sax.helpers.*;
import org.xml.sax.*;
import org.xml.*;


// A JUnit test class for LocalBlastTest.java
public class LocalBlastTest extends TestCase {

	boolean noExceptions;
	DIPSynonyms synonyms;
	LocalBlast smallNorm;
	LocalBlast largeNorm;
	LocalBlast smallNotNorm;
	LocalBlast largeNotNorm;
	String outLoc;

	BlastGraph<String,Double> b1;
	BlastGraph<String,Double> b2;

	protected void setUp() {
		try { 
		noExceptions = true;
		synonyms = new DIPSynonyms();

		XMLReader xr = XMLReaderFactory.createXMLReader();
		xr.setContentHandler(synonyms);
		xr.parse(new InputSource(new FileReader("examples/Gallus_gallus.xin")));

		Properties props = System.getProperties();
		outLoc = props.getProperty("java.io.tmpdir") + props.getProperty("file.separator") + "LocalBlastTest." + System.currentTimeMillis() + ".xml";
		props.load( new FileInputStream("examples/blast.properties"));

		// normalize
		smallNorm = new LocalBlast(props,synonyms, outLoc, 1.0e-10,true ); 
		largeNorm = new LocalBlast(props,synonyms, outLoc, 10.0,true ); 

		// don't normalize
		smallNotNorm = new LocalBlast(props,synonyms, outLoc, 1.0e-10,false ); 
		largeNotNorm = new LocalBlast(props,synonyms, outLoc, 10.0,false ); 

		b1 = new BlastGraph<String,Double>("Gallus_gallus.fa","examples");
		DIPInteractionNetwork dipin1 = new DIPInteractionNetwork("Gallus gallus");
		dipin1.updateGraph(b1);

		b2 = new BlastGraph<String,Double>("Gallus_gallus.fa","examples");
		DIPInteractionNetwork dipin2 = new DIPInteractionNetwork("Gallus gallus");
		dipin2.updateGraph(b2);

		XMLSaxEventDistributor xml = new XMLSaxEventDistributor();
		xml.addHandler(dipin1);
		xml.addHandler(dipin2);
		xml.parse("examples/Gallus_gallus.xin");

		} catch (Exception e) { e.printStackTrace(); noExceptions = false; }

	}

	public void testBlast() {
		// sanity check
		assertTrue( noExceptions );
		assertTrue("expect 8 nodes, got: " + b1.numberOfNodes(), b1.numberOfNodes() == 8);
		assertTrue("expect 8 nodes, got: " + b2.numberOfNodes(), b2.numberOfNodes() == 8);
		assertTrue("expect 3 edge, got: " + b1.numberOfEdges(), b1.numberOfEdges() == 3);
		assertTrue("expect 3 edge, got: " + b2.numberOfEdges(), b2.numberOfEdges() == 3);

		for ( Edge<String,Double> e : b1.getEdges() )
			System.out.println( "edge " + e.toString() ); 


		Map<String,Map<String,Double>> map = smallNorm.expectationValues(b1,b2);
		checkMap(map,false);
		map = largeNorm.expectationValues(b1,b2);
		checkMap(map,true);
	}

	public void testNormalize() {
		Map<String,Map<String,Double>> norm = largeNorm.expectationValues(b1,b2);
		Map<String,Map<String,Double>> notNorm = largeNotNorm.expectationValues(b1,b2);

		assertTrue( norm.size() > 0 );
		assertTrue( notNorm.size() > 0 );
	
		double normVal = norm.get("BMRB_CHICK").get("PIR:S33568").doubleValue();
		double notNormVal = notNorm.get("BMRB_CHICK").get("PIR:S33568").doubleValue();

		assertTrue(notNormVal > normVal);
	}

	private void checkMap(Map<String,Map<String,Double>> map,boolean nonHomologs) {
		System.out.println("map size: " + map.size());
		assertTrue( map.size() > 0 );
		int numChecks = 0;
		for ( String key : map.keySet() ) {
			System.out.println("key " + key);
			for ( String key2 : map.get(key).keySet() ) {
				System.out.println(key + " " + key2 + " " + map.get(key).get(key2));
				if ( key.equals(key2) ) {
					assertTrue( map.get(key).get(key2) == 0 );
					numChecks++;
				}
				else if ( key.equals("BMRB_CHICK") && key2.equals("PIR:S33568") ||
				          key2.equals("BMRB_CHICK") && key.equals("PIR:S33568") ) {
					assertTrue( "expect ~1.3e-19: got " +  map.get(key).get(key2), 
					            map.get(key).get(key2) > 1e-20 && 
					            map.get(key).get(key2) < 1e-18 );
					numChecks++;
				}
				else if ( nonHomologs ) {
					if (key.equals("HSF3_CHICK") && key2.equals("ATNB_CHICK")) {
						assertTrue("expect ~0.4: got " +  map.get(key).get(key2),map.get(key).get(key2) > 0.4 );
						numChecks++;
					}
					if (key2.equals("PRGR_CHICK") && key.equals("A1A1_CHICK")) {
						assertTrue("expect ~0.3: got " +  map.get(key).get(key2),map.get(key).get(key2) > 0.3 );
						numChecks++;
					}
				}
			}
		}
		assertTrue( numChecks > 0 );
	}

	protected void tearDown() {
		File f = new File(outLoc);
		f.delete();
	}
   
	public static Test suite() {
		return new TestSuite(LocalBlastTest.class);
	}
}
