
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package csplugins.test.quickfind.test;

import csplugins.quickfind.util.CyAttributesUtil;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesImpl;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Tests the CyAttributes Util class.
 *
 * @author Ethan Cerami.
 */
public class TestCyAttributesUtil extends TestCase {
	private static final String ID = "id1";
	private static final String BOOLEAN_TYPE = "boolean_type";
	private static final String INTEGER_TYPE = "integer_type";
	private static final String FLOATING_TYPE = "floating_type";
	private static final String STRING_TYPE = "string_type";
	private static final String LIST_TYPE = "list_type";
	private static final String MAP_TYPE = "map_type";

	/**
	 * Tests the getAttributeValues() method.
	 */
	public void testGetAttributeValues() {
		CyAttributes cyAttributes = new CyAttributesImpl();

		//  Test with Boolean value
		cyAttributes.setAttribute(ID, BOOLEAN_TYPE, Boolean.TRUE);

		String[] values = CyAttributesUtil.getAttributeValues(cyAttributes, ID, BOOLEAN_TYPE);
		assertEquals(1, values.length);
		assertEquals("true", values[0]);

		//  Test with Integer value
		cyAttributes.setAttribute(ID, INTEGER_TYPE, new Integer(25));
		values = CyAttributesUtil.getAttributeValues(cyAttributes, ID, INTEGER_TYPE);
		assertEquals(1, values.length);
		assertEquals("25", values[0]);

		//  Test with Floating value
		cyAttributes.setAttribute(ID, FLOATING_TYPE, new Double(25.0));
		values = CyAttributesUtil.getAttributeValues(cyAttributes, ID, FLOATING_TYPE);
		assertEquals(1, values.length);
		assertEquals("25.0", values[0]);

		//  Test with Simple List
		ArrayList list = new ArrayList();
		list.add("apple");
		list.add("banana");
		cyAttributes.setListAttribute(ID, LIST_TYPE, list);
		values = CyAttributesUtil.getAttributeValues(cyAttributes, ID, LIST_TYPE);
		assertEquals(2, values.length);
		assertEquals("apple", values[0]);
		assertEquals("banana", values[1]);

		//  Test with Simple Map
		HashMap map = new HashMap();
		map.put("first_name", "Ethan");
		map.put("last_name", "Cerami");
		cyAttributes.setMapAttribute(ID, MAP_TYPE, map);
		values = CyAttributesUtil.getAttributeValues(cyAttributes, ID, MAP_TYPE);
		assertEquals(2, values.length);

		boolean firstNameCheck = false;
		boolean lastNameCheck = false;

		for (int i = 0; i < values.length; i++) {
			if (values[i].equals("Ethan")) {
				firstNameCheck = true;
			} else if (values[i].equals("Cerami")) {
				lastNameCheck = true;
			}
		}

		assertTrue(firstNameCheck);
		assertTrue(lastNameCheck);

		//  Test with invalid attribute key
		values = CyAttributesUtil.getAttributeValues(cyAttributes, ID, "CELLULAR_LOCATION");
		assertEquals(null, values);
	}

	/**
	 * Tests the getDistinctAttributeValues method, Take 1.
	 */
	public static void testGetDistinctAttributeValues1() {
		CyAttributes cyAttributes = new CyAttributesImpl();
		cyAttributes.setAttribute("ID1", STRING_TYPE, "A");
		cyAttributes.setAttribute("ID2", STRING_TYPE, "A");
		cyAttributes.setAttribute("ID3", STRING_TYPE, "B");
		cyAttributes.setAttribute("ID4", STRING_TYPE, "B");
		cyAttributes.setAttribute("ID5", STRING_TYPE, "C");
		cyAttributes.setAttribute("ID6", STRING_TYPE, "C");

		CyNetwork network = Cytoscape.createNetwork("csplugins.test");

		createNode("ID1", network);
		createNode("ID2", network);
		createNode("ID3", network);
		createNode("ID4", network);
		createNode("ID5", network);
		createNode("ID6", network);

		String[] values = CyAttributesUtil.getDistinctAttributeValues(network.nodesIterator(),
		                                                              cyAttributes, STRING_TYPE, 5);
		assertEquals(3, values.length);

		boolean aCheck = false;
		boolean bCheck = false;
		boolean cCheck = false;

		for (int i = 0; i < values.length; i++) {
			if (values[i].equals("A")) {
				aCheck = true;
			} else if (values[i].equals("B")) {
				bCheck = true;
			} else if (values[i].equals("C")) {
				cCheck = true;
			}
		}

		assertTrue(aCheck);
		assertTrue(bCheck);
		assertTrue(cCheck);
	}

	/**
	 * Tests the getDistinctAttributeValues method, Take 2.
	 */
	public static void testGetDistinctAttributeValues2() {
		CyAttributes cyAttributes = new CyAttributesImpl();

		//  Test with Simple List
		ArrayList list = new ArrayList();
		list.add("apple");
		list.add("banana");

		cyAttributes.setListAttribute("ID1", LIST_TYPE, list);

		CyNetwork network = Cytoscape.createNetwork("csplugins.test");
		createNode("ID1", network);

		String[] values = CyAttributesUtil.getDistinctAttributeValues(network.nodesIterator(),
		                                                              cyAttributes, LIST_TYPE, 5);
		assertEquals(1, values.length);
		assertEquals("apple, banana", values[0]);
	}

	private static void createNode(String id, CyNetwork network) {
		CyNode node = Cytoscape.getCyNode(id, true);
		network.addNode(node);
	}
}
