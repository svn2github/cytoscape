
package org.cytoscape.attributes;

import org.cytoscape.attributes.CyAttributes;
import org.cytoscape.attributes.CyAttributesManager;
import org.cytoscape.model.network.internal.CyAttributesManagerImpl;

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

		assertTrue( attrs.contains("someString",String.class) );
		assertTrue( attrs.contains("someStringElse",String.class) );
		assertFalse( attrs.contains("yetAnotherString",String.class) );

		assertEquals( "apple", attrs.get("someString",String.class) );
		assertEquals( "orange", attrs.get("someStringElse",String.class) );
	}

	public void testAddIntAttr() {
		mgr.createAttribute("someInt",Integer.class);
		mgr.createAttribute("someOtherInt",Integer.class);

		attrs.set("someInt",50);	
		attrs.set("someOtherInt",100);	

		assertTrue( attrs.contains("someInt",Integer.class) );
		assertTrue( attrs.contains("someOtherInt",Integer.class) );
		assertFalse( attrs.contains("yetAnotherInteger",Integer.class) );

		assertEquals( 50, attrs.get("someInt",Integer.class).intValue() );
		assertEquals( 100, attrs.get("someOtherInt",Integer.class).intValue() );
	}

	public void testAddDoubleAttr() {
		mgr.createAttribute("someDouble",Double.class);
		mgr.createAttribute("someOtherDouble",Double.class);

		attrs.set("someDouble",3.14);	
		attrs.set("someOtherDouble",2.76);	

		assertTrue( attrs.contains("someDouble",Double.class) );
		assertTrue( attrs.contains("someOtherDouble",Double.class) );
		assertFalse( attrs.contains("yetAnotherDouble",Double.class) );

		assertEquals( 3.14, attrs.get("someDouble", Double.class).doubleValue() ); 
		assertEquals( 2.76, attrs.get("someOtherDouble", Double.class).doubleValue() ); 
	}

	public void testAddBooleanAttr() {
		mgr.createAttribute("someBoolean",Boolean.class);
		mgr.createAttribute("someOtherBoolean",Boolean.class);

		attrs.set("someBoolean",true);	
		attrs.set("someOtherBoolean",false);	

		assertTrue( attrs.contains("someBoolean",Boolean.class) );
		assertTrue( attrs.contains("someOtherBoolean",Boolean.class) );
		assertFalse( attrs.contains("yetAnotherBoolean",Boolean.class) );

		assertTrue( attrs.get("someBoolean",Boolean.class) );
		assertFalse( attrs.get("someOtherBoolean",Boolean.class) );
	}

	public void testAddListAttr() {
		mgr.createAttribute("someList",List.class);

		List<String> l = new LinkedList<String>();
		l.add("orange");
		l.add("banana");

		attrs.set("someList",l);	

		assertTrue( attrs.contains("someList",List.class) );

		assertEquals( 2, attrs.get("someList",List.class).size() );
	}

	public void testAddMapAttr() {
		mgr.createAttribute("someMap",Map.class);

		Map<String,Integer> m = new HashMap<String,Integer>();
		m.put("orange",1);
		m.put("banana",2);

		attrs.set("someMap",m);	

		assertTrue( attrs.contains("someMap",Map.class) );

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
