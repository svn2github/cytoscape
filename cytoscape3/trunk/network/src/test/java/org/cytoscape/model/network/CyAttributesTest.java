
package org.cytoscape.attributes;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.internal.CyAttributesManagerImpl;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;
import java.util.*;

import java.awt.Color;

public class CyAttributesTest extends TestCase {

	private CyAttributesManagerImpl mgr;
	private CyAttributes attrs;

	public static Test suite() {
		return new TestSuite(CyAttributesTest.class);
	}

	public void setUp() {
		mgr = new CyAttributesManagerImpl(null);
		attrs = mgr.getCyAttributes(1);
	}

	public void tearDown() {
	}

	public void testAddStringAttr() {
		mgr.createAttribute("someString",String.class);
		mgr.createAttribute("someStringElse",String.class);

		attrs.set("someString","apple");	
		attrs.set("someStringElse","orange");	

		assertTrue( String.class == attrs.contains("someString") );
		assertTrue( String.class == attrs.contains("someStringElse") );
		assertTrue( null == attrs.contains("yetAnotherString") );

		assertEquals( "apple", attrs.get("someString",String.class) );
		assertEquals( "orange", attrs.get("someStringElse",String.class) );
	}

	public void testAddIntAttr() {
		mgr.createAttribute("someInt",Integer.class);
		mgr.createAttribute("someOtherInt",Integer.class);

		attrs.set("someInt",50);	
		attrs.set("someOtherInt",100);	

		assertTrue( Integer.class == attrs.contains("someInt") );
		assertTrue( Integer.class == attrs.contains("someOtherInt") );
		assertTrue( null == attrs.contains("yetAnotherInteger") );

		assertEquals( 50, attrs.get("someInt",Integer.class).intValue() );
		assertEquals( 100, attrs.get("someOtherInt",Integer.class).intValue() );
	}

	public void testAddDoubleAttr() {
		mgr.createAttribute("someDouble",Double.class);
		mgr.createAttribute("someOtherDouble",Double.class);

		attrs.set("someDouble",3.14);	
		attrs.set("someOtherDouble",2.76);	

		assertTrue( Double.class == attrs.contains("someDouble") );
		assertTrue( Double.class == attrs.contains("someOtherDouble") );
		assertTrue( null == attrs.contains("yetAnotherDouble") );

		assertEquals( 3.14, attrs.get("someDouble", Double.class).doubleValue() ); 
		assertEquals( 2.76, attrs.get("someOtherDouble", Double.class).doubleValue() ); 
	}

	public void testAddBooleanAttr() {
		mgr.createAttribute("someBoolean",Boolean.class);
		mgr.createAttribute("someOtherBoolean",Boolean.class);

		attrs.set("someBoolean",true);	
		attrs.set("someOtherBoolean",false);	

		assertTrue( Boolean.class == attrs.contains("someBoolean") );
		assertTrue( Boolean.class == attrs.contains("someOtherBoolean") );
		assertTrue( null == attrs.contains("yetAnotherBoolean") );

		assertTrue( attrs.get("someBoolean",Boolean.class) );
		assertFalse( attrs.get("someOtherBoolean",Boolean.class) );
	}

	public void testAddListAttr() {
		mgr.createAttribute("someList",List.class);

		List<String> l = new LinkedList<String>();
		l.add("orange");
		l.add("banana");

		attrs.set("someList",l);	

		assertTrue( List.class == attrs.contains("someList") );

		assertEquals( 2, attrs.get("someList",List.class).size() );
	}

	public void testAddMapAttr() {
		mgr.createAttribute("someMap",Map.class);

		Map<String,Integer> m = new HashMap<String,Integer>();
		m.put("orange",1);
		m.put("banana",2);

		attrs.set("someMap",m);	

		assertTrue( Map.class == attrs.contains("someMap") );

		assertEquals( 2, attrs.get("someMap",Map.class).size() );
	}

	public void testAddBadAttr() {
		try {
			attrs.set("nodeColor",Color.white);
		} catch (IllegalArgumentException e) {
			// successfully caught the exception
			return;	
		}
		// shouldn't get here
		fail();
	}

	public void testAddBadList() {
		List<Color> l = new LinkedList<Color>();
		l.add(Color.white);
		l.add(Color.red);

		try {
			attrs.set("someList",l);	
		} catch (IllegalArgumentException e) {
			// successfully caught the exception
			return;	
		}
		// shouldn't get here
		fail();
	}

	// lots more needed
}
