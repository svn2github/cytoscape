
package org.cytoscape.model.attrs;

import org.cytoscape.model.attrs.CyAttributes;
import org.cytoscape.model.attrs.impl.CyAttributesImpl;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;
import java.util.*;

import java.awt.Color;

public class CyAttributesTest extends TestCase {

	private CyAttributes attrs;

	public static Test suite() {
		return new TestSuite(CyAttributesTest.class);
	}

	public void setUp() {
		attrs = new CyAttributesImpl();
	}

	public void tearDown() {
	}

	public void testAddStringAttr() {
		attrs.set("someString","apple");	
		attrs.set("someStringElse","orange");	

		assertTrue( attrs.contains("someString",String.class) );
		assertTrue( attrs.contains("someStringElse",String.class) );
		assertFalse( attrs.contains("yetAnotherString",String.class) );

		assertEquals( "apple", attrs.get("someString",String.class) );
		assertEquals( "orange", attrs.get("someStringElse",String.class) );
	}

	public void testAddIntAttr() {
		attrs.set("someInt",50);	
		attrs.set("someOtherInt",100);	

		assertTrue( attrs.contains("someInt",Integer.class) );
		assertTrue( attrs.contains("someOtherInt",Integer.class) );
		assertFalse( attrs.contains("yetAnotherInteger",Integer.class) );

		assertEquals( 50, attrs.get("someInt",Integer.class).intValue() );
		assertEquals( 100, attrs.get("someOtherInt",Integer.class).intValue() );
	}

	public void testAddDoubleAttr() {
		attrs.set("someDouble",3.14);	
		attrs.set("someOtherDouble",2.76);	

		assertTrue( attrs.contains("someDouble",Double.class) );
		assertTrue( attrs.contains("someOtherDouble",Double.class) );
		assertFalse( attrs.contains("yetAnotherDouble",Double.class) );

		assertEquals( 3.14, attrs.get("someDouble", Double.class).doubleValue() ); 
		assertEquals( 2.76, attrs.get("someOtherDouble", Double.class).doubleValue() ); 
	}

	public void testAddBooleanAttr() {
		attrs.set("someBoolean",true);	
		attrs.set("someOtherBoolean",false);	

		assertTrue( attrs.contains("someBoolean",Boolean.class) );
		assertTrue( attrs.contains("someOtherBoolean",Boolean.class) );
		assertFalse( attrs.contains("yetAnotherBoolean",Boolean.class) );

		assertTrue( attrs.get("someBoolean",Boolean.class) );
		assertFalse( attrs.get("someOtherBoolean",Boolean.class) );
	}

	public void testAddListAttr() {
		List<String> l = new LinkedList<String>();
		l.add("orange");
		l.add("banana");

		attrs.set("someList",l);	

		assertTrue( attrs.contains("someList",List.class) );

		assertEquals( 2, attrs.get("someList",List.class).size() );
	}

	public void testAddMapAttr() {
		Map<String,Integer> m = new HashMap<String,Integer>();
		m.put("orange",1);
		m.put("banana",2);

		attrs.set("someList",m);	

		assertTrue( attrs.contains("someList",Map.class) );

		assertEquals( 2, attrs.get("someList",Map.class).size() );
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
