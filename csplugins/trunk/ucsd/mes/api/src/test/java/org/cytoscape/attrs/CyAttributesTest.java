
package org.cytoscape.attrs;

import org.cytoscape.attrs.CyAttributes;
import org.cytoscape.attrs.impl.CyAttributesImpl;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;
import java.util.List;

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
		attrs.set(1,"someString","apple");	
		attrs.set(2,"someString","orange");	

		assertTrue( attrs.contains(1,"someString",String.class) );
		assertTrue( attrs.contains(2,"someString",String.class) );
		assertFalse( attrs.contains(3,"someString",String.class) );

		assertEquals( "apple", attrs.get(1,"someString",String.class) );
		assertEquals( "orange", attrs.get(2,"someString",String.class) );
	}

	public void testAddIntAttr() {
		attrs.set(1,"someInt",50);	
		attrs.set(2,"someInt",100);	

		assertTrue( attrs.contains(1,"someInt",Integer.class) );
		assertTrue( attrs.contains(2,"someInt",Integer.class) );
		assertFalse( attrs.contains(3,"someInt",Integer.class) );

		assertEquals( 50, attrs.get(1,"someInt",Integer.class).intValue() );
		assertEquals( 100, attrs.get(2,"someInt",Integer.class).intValue() );
	}

	public void testAddDoubleAttr() {
		attrs.set(1,"someDouble",3.14);	
		attrs.set(2,"someDouble",2.76);	

		assertTrue( attrs.contains(1,"someDouble",Double.class) );
		assertTrue( attrs.contains(2,"someDouble",Double.class) );
		assertFalse( attrs.contains(3,"someDouble",Double.class) );

		assertEquals( 3.14, attrs.get(1,"someDouble", Double.class).doubleValue() ); 
		assertEquals( 2.76, attrs.get(2,"someDouble", Double.class).doubleValue() ); 
	}

	public void testAddBooleanAttr() {
		attrs.set(1,"someBoolean",true);	
		attrs.set(2,"someBoolean",false);	

		assertTrue( attrs.contains(1,"someBoolean",Boolean.class) );
		assertTrue( attrs.contains(2,"someBoolean",Boolean.class) );
		assertFalse( attrs.contains(3,"someBoolean",Boolean.class) );

		assertTrue( attrs.get(1,"someBoolean",Boolean.class) );
		assertFalse( attrs.get(2,"someBoolean",Boolean.class) );
	}

	// lots more needed
}
