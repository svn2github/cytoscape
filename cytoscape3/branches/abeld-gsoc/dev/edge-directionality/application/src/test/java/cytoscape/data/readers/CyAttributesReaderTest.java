/*
  File: CyAttributesReaderTest.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.data.readers;

import org.cytoscape.attributes.CyAttributes;
import org.cytoscape.attributes.CyAttributesFactory;
import org.cytoscape.attributes.AttributeValueVisitor;
import org.cytoscape.attributes.AttributeFilter;

import cytoscape.data.readers.CyAttributesReader;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;


/**
 * Tests the CyAttributesReader Class.
 */
public class CyAttributesReaderTest extends TestCase {
	
	private java.io.InputStreamReader getReader(String file) {
		java.io.InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("testData/" + file);
		java.io.InputStreamReader reader = new java.io.InputStreamReader(is);
		return reader;
	}

	
	/**
	 * Testing Reading, Take 1.
	 * @throws IOException IO Errors.
	 */
	public void testRead1() throws IOException {
		String attributeName = "TestNodeAttribute1";
		CyAttributes cyAttributes = CyAttributesFactory.getCyAttributes("testRead1"); 

		CyAttributesReader.loadAttributes(cyAttributes, getReader("galFiltered.nodeAttrs1"));

		byte type = cyAttributes.getType(attributeName);
		assertEquals(CyAttributes.TYPE_INTEGER, type);

		//  Test a value
		Integer value = cyAttributes.getIntegerAttribute("YKR026C", attributeName);
		assertEquals(1, value.intValue());

		//  Test a Second value
		value = cyAttributes.getIntegerAttribute("YMR043W", attributeName);
		assertEquals(2, value.intValue());

		//  Test the last value in the file
		value = cyAttributes.getIntegerAttribute("YBR043C", attributeName);
		assertEquals(3, value.intValue());

		//  Test a non-existent value
		value = cyAttributes.getIntegerAttribute("Nerius", attributeName);
		assertTrue(value == null);
	}

	/**
	 * Testing Reading, Take 2.
	 * @throws IOException IO Errors.
	 */
	public void testRead2() throws IOException {
		CyAttributes cyAttributes = CyAttributesFactory.getCyAttributes("testRead2"); 

		CyAttributesReader.loadAttributes(cyAttributes, getReader("galFiltered.edgeAttrs2"));

		byte type = cyAttributes.getType("TestEdgeAttribute2");
		assertEquals(CyAttributes.TYPE_INTEGER, type);

		//  Test a value
		Integer value = cyAttributes.getIntegerAttribute("YKR026C (pp) YGL122C",
		                                                 "TestEdgeAttribute2");
		assertEquals(2, value.intValue());

		//  Test a Second value
		value = cyAttributes.getIntegerAttribute("YDR382W (pp) YFL029C", "TestEdgeAttribute2");
		assertEquals(3, value.intValue());

		//  Test the last value in the file
		value = cyAttributes.getIntegerAttribute("YBL026W (pp) YOR127C", "TestEdgeAttribute2");
		assertEquals(3, value.intValue());

		//  Test a non-existent value
		value = cyAttributes.getIntegerAttribute("Nerius", "TestEdgeAttribute2");
		assertTrue(value == null);
	}

	/**
	 * Testing Read, Take 3
	 * @throws IOException IO Errors.
	 */
	public void testRead3() throws IOException {
		CyAttributes cyAttributes = CyAttributesFactory.getCyAttributes("testRead3"); 

		//  This file contains an explicit class declaration, like so:
		//  Score (class=Java.lang.Double)
		//  All the integer values should therefore be stored as Doubles.

		CyAttributesReader.loadAttributes(cyAttributes, getReader("explicitDouble.attribute"));

		byte type = cyAttributes.getType("Score");
		assertEquals(CyAttributes.TYPE_FLOATING, type);

		//  Test a value
		Double value = cyAttributes.getDoubleAttribute("a", "Score");
		assertEquals(1.0, value.doubleValue(), 0.01);

		//  Test the last value in the file
		value = cyAttributes.getDoubleAttribute("c", "Score");
		assertEquals(3.7, value.doubleValue(), 0.01);
	}

	/**
	 * Testing Reading of Simple Lists.
	 * @throws IOException IO Errors.
	 */
	public void testReadSimpleLists() throws IOException {
		String attributeName = "GO_molecular_function_level_4";
		CyAttributes cyAttributes = CyAttributesFactory.getCyAttributes("testReadSimpleLists"); 

		CyAttributesReader.loadAttributes(cyAttributes, getReader("implicitStringArray.attribute"));

		byte type = cyAttributes.getType(attributeName);
		assertEquals(CyAttributes.TYPE_SIMPLE_LIST, type);

		//  Test the First List
		List list = cyAttributes.getListAttribute("AP1G1", attributeName);
		assertEquals(3, list.size());

		String value = (String) list.get(0);
		assertEquals("intracellular", value);
		value = (String) list.get(1);
		assertEquals("clathrin adaptor", value);
		value = (String) list.get(2);
		assertEquals("intracellular transporter", value);

		//  Test the Last List
		list = cyAttributes.getListAttribute("CDH3", attributeName);
		assertEquals(1, list.size());
		value = (String) list.get(0);
		assertEquals("cell adhesion molecule", value);
	}

	/**
	 * Runs just this one unit test.
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(CyAttributesReaderTest.class);
	}
}
