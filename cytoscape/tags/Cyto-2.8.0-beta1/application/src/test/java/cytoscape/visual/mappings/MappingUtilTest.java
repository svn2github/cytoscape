/*
  File: MappingUtilTest.java

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
package cytoscape.visual.mappings;

import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.visual.mappings.MappingUtil;

import junit.framework.*;

import java.awt.Color;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class MappingUtilTest extends TestCase {
	/**
	 * Creates a new MappingUtilTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public MappingUtilTest(String name) {
		super(name);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testGetNodeAttributeType() throws Exception {
		assertEquals("non-existent attr ", CyAttributes.TYPE_UNDEFINED,
		             MappingUtil.getAttributeType("node", "mappingUtilTest"));

		// string test
		Cytoscape.getNodeAttributes().setAttribute("id", "mappingUtilTest", "test");
		assertEquals("string attr ", CyAttributes.TYPE_STRING,
		             MappingUtil.getAttributeType("node", "mappingUtilTest"));

		// int test
		Cytoscape.getNodeAttributes().setAttribute("id", "mappingUtilTest2", 20);
		assertEquals("int attr ", CyAttributes.TYPE_INTEGER,
		             MappingUtil.getAttributeType("node", "mappingUtilTest2"));

		// double test
		Cytoscape.getNodeAttributes().setAttribute("id", "mappingUtilTest3", 20.5);
		assertEquals("float attr ", CyAttributes.TYPE_FLOATING,
		             MappingUtil.getAttributeType("node", "mappingUtilTest3"));

		// boolean test
		Cytoscape.getNodeAttributes().setAttribute("id", "mappingUtilTest4", true);
		assertEquals("bool attr ", CyAttributes.TYPE_BOOLEAN,
		             MappingUtil.getAttributeType("node", "mappingUtilTest4"));

		// list test
		List l = new ArrayList();
		l.add("asdf");
		Cytoscape.getNodeAttributes().setListAttribute("id", "mappingUtilTest5", l);
		assertEquals("list attr ", CyAttributes.TYPE_SIMPLE_LIST,
		             MappingUtil.getAttributeType("node", "mappingUtilTest5"));

		// map test
		Map m = new HashMap();
		m.put("a", "b");
		Cytoscape.getNodeAttributes().setMapAttribute("id", "mappingUtilTest6", m);
		assertEquals("map attr ", CyAttributes.TYPE_SIMPLE_MAP,
		             MappingUtil.getAttributeType("node", "mappingUtilTest6"));

		// wrong baseKey 
		assertEquals("wrong base key", CyAttributes.TYPE_UNDEFINED,
		             MappingUtil.getAttributeType("junk", "mappingUtilTest2"));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testGetEdgeAttributeType() throws Exception {
		assertEquals("non-existent attr ", CyAttributes.TYPE_UNDEFINED,
		             MappingUtil.getAttributeType("edge", "mappingUtilTest"));

		// string test
		Cytoscape.getEdgeAttributes().setAttribute("id", "mappingUtilTest", "test");
		assertEquals("string attr ", CyAttributes.TYPE_STRING,
		             MappingUtil.getAttributeType("edge", "mappingUtilTest"));

		// int test
		Cytoscape.getEdgeAttributes().setAttribute("id", "mappingUtilTest2", 20);
		assertEquals("int attr ", CyAttributes.TYPE_INTEGER,
		             MappingUtil.getAttributeType("edge", "mappingUtilTest2"));

		// double test
		Cytoscape.getEdgeAttributes().setAttribute("id", "mappingUtilTest3", 20.5);
		assertEquals("float attr ", CyAttributes.TYPE_FLOATING,
		             MappingUtil.getAttributeType("edge", "mappingUtilTest3"));

		// boolean test
		Cytoscape.getEdgeAttributes().setAttribute("id", "mappingUtilTest4", true);
		assertEquals("bool attr ", CyAttributes.TYPE_BOOLEAN,
		             MappingUtil.getAttributeType("edge", "mappingUtilTest4"));

		// list test
		List l = new ArrayList();
		l.add("asdf");
		Cytoscape.getEdgeAttributes().setListAttribute("id", "mappingUtilTest5", l);
		assertEquals("list attr ", CyAttributes.TYPE_SIMPLE_LIST,
		             MappingUtil.getAttributeType("edge", "mappingUtilTest5"));

		// map test
		Map m = new HashMap();
		m.put("a", "b");
		Cytoscape.getEdgeAttributes().setMapAttribute("id", "mappingUtilTest6", m);
		assertEquals("map attr ", CyAttributes.TYPE_SIMPLE_MAP,
		             MappingUtil.getAttributeType("edge", "mappingUtilTest6"));

		// what the hell is TYPE_COMPLEX????

		// wrong baseKey 
		assertEquals("string attr ", CyAttributes.TYPE_UNDEFINED,
		             MappingUtil.getAttributeType("junk", "mappingUtilTest2"));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testParseObject() {
		Object o = null;

		// test valid types
		o = MappingUtil.parseObjectType("1", CyAttributes.TYPE_INTEGER);
		assertTrue("int ", o instanceof Integer);
		o = null;

		o = MappingUtil.parseObjectType("1.75", CyAttributes.TYPE_FLOATING);
		assertTrue("double ", o instanceof Double);
		o = null;

		o = MappingUtil.parseObjectType("false", CyAttributes.TYPE_BOOLEAN);
		assertTrue("boolean ", o instanceof Boolean);
		o = null;

		o = MappingUtil.parseObjectType("homer", CyAttributes.TYPE_STRING);
		assertTrue("string ", o instanceof String);
		o = null;

		o = MappingUtil.parseObjectType("homer", CyAttributes.TYPE_SIMPLE_MAP);
		assertTrue("map ", o instanceof String);
		o = null;

		o = MappingUtil.parseObjectType("homer", CyAttributes.TYPE_SIMPLE_LIST);
		assertTrue("list ", o instanceof String);
		o = null;

		// test some failures
		o = MappingUtil.parseObjectType("homer", (byte) 75);
		assertTrue("non-existent type 75 ", o instanceof String);
		o = null;

		boolean caught = false;

		try {
			o = MappingUtil.parseObjectType("homer", CyAttributes.TYPE_INTEGER);
		} catch (NumberFormatException n) {
			caught = true;
		}

		assertTrue("caught number format excpetion", caught);
		o = null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(MappingUtilTest.class));
	}
}
