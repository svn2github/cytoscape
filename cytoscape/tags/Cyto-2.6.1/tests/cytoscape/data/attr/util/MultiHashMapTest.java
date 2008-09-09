/*
  File: TestMultiHashMap.java

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
package cytoscape.data.attr.util;

import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.data.attr.util.MultiHashMapFactory;
import cytoscape.data.attr.util.MultiHashMapHelpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public final class MultiHashMapTest extends TestCase {


    public static Test suite() {
		return new TestSuite(MultiHashMapTest.class);
	}

	MultiHashMapDefinition def; 
	MultiHashMap data;
	RuntimeException exc; 

	public void setUp() {

		Object mm = MultiHashMapFactory.instantiateDataModel();
		def = (MultiHashMapDefinition) mm;
		data = (MultiHashMap) mm;

		def.defineAttribute("p-values", MultiHashMapDefinition.TYPE_FLOATING_POINT,
		                    new byte[] {
		                        MultiHashMapDefinition.TYPE_STRING,
		                        MultiHashMapDefinition.TYPE_INTEGER
		                    });
		data.setAttributeValue("node1", "p-values", new Double(0.5),
		                       new Object[] { "Ideker", new Integer(0) });
		data.setAttributeValue("node1", "p-values", new Double(0.6),
		                       new Object[] { "Ideker", new Integer(1) });
		data.setAttributeValue("node1", "p-values", new Double(0.6),
		                       new Object[] { "Ideker", new Integer(2) });
		data.setAttributeValue("node1", "p-values", new Double(0.7),
		                       new Object[] { "Salk", new Integer(0) });
		data.setAttributeValue("node1", "p-values", new Double(0.6),
		                       new Object[] { "Salk", new Integer(1) });
		data.setAttributeValue("node2", "p-values", new Double(0.4),
		                       new Object[] { "Salk", new Integer(0) });
		data.setAttributeValue("node2", "p-values", new Double(0.2),
		                       new Object[] { "Weirdo", new Integer(0) });
		data.setAttributeValue("node3", "p-values", new Double(0.1),
		                       new Object[] { "Foofoo", new Integer(11) });
		data.setAttributeValue("node4", "p-values", new Double(0.9),
		                       new Object[] { "BarBar", new Integer(9) });

		exc = null;
	}

	public void testSetAttrValue1() {

		try {
			data.setAttributeValue("node4", "p-values", new Double(0.4),
			                       new Object[] { "BarBar", new Long(1) });
		} catch (ClassCastException e) {
			exc = e;
		}

		assertNotNull(exc);
	}
	
	public void testSetAttrValue2() {

		try {
			data.setAttributeValue("node5", "p-values", new Double(0.4),
			                       new Object[] { "BarBar", new Long(1) });
		} catch (ClassCastException e) {
			exc = e;
		}

		assertNotNull(exc);
	}

	public void testDefineAttr() {

		def.defineAttribute("color", MultiHashMapDefinition.TYPE_STRING, null);
		data.setAttributeValue("node1", "color", "red", null);
		data.setAttributeValue("node8", "color", "yellow", null);

		try {
			data.removeAttributeValue("node1", "p-values", new Object[] { "Salk", new Long(1) });
		} catch (ClassCastException e) {
			exc = e;
		}

		assertNotNull(exc);

	}

	public void testRemoveAttr() {
		Object o = data.removeAttributeValue("node4", "p-values", new Object[] { "BarBar", new Integer(9) });
		assertEquals(new Double(0.9), (Double)o);

		def.defineAttribute("color", MultiHashMapDefinition.TYPE_STRING, null);
		data.setAttributeValue("node4", "color", "cyan", null);

		o = data.removeAttributeValue("node4", "color", null);
		assertEquals("cyan",(String)o);

	}
}
