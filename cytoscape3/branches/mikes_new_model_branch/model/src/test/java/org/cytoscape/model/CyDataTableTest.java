
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.model;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.internal.CyDataTableImpl;

import java.awt.Color;

import java.lang.RuntimeException;

import java.util.*;


/**
 * DOCUMENT ME!
  */
public class CyDataTableTest extends TestCase {
	private CyDataTable mgr;
	private CyRow attrs;

	/**
	 *  DOCUMENT ME!
	 */
	public void setUp() {
		mgr = new CyDataTableImpl(null, "homer", true);
		attrs = mgr.getRow(1);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void tearDown() {
	}


  // addRow was implemented to return null (rather than a row). This is just to insure it continues to work.
  public void testAddRow() {
    CyRow row = mgr.addRow();
    assertNotNull(row);
  }

  /**
	 *  DOCUMENT ME!
	 */
	public void testAddStringAttr() {
		mgr.createColumn("someString", String.class, false);
		mgr.createColumn("someStringElse", String.class, false);

		attrs.set("someString", "apple");
		attrs.set("someStringElse", "orange");

		assertTrue(attrs.contains("someString", String.class));
		assertTrue(attrs.contains("someStringElse", String.class));
		assertFalse(attrs.contains("yetAnotherString", String.class));

		assertEquals("apple", attrs.get("someString", String.class));
		assertEquals("orange", attrs.get("someStringElse", String.class));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAddIntAttr() {
		mgr.createColumn("someInt", Integer.class, false);
		mgr.createColumn("someOtherInt", Integer.class, false);

		attrs.set("someInt", 50);
		attrs.set("someOtherInt", 100);

		assertTrue(attrs.contains("someInt", Integer.class));
		assertTrue(attrs.contains("someOtherInt", Integer.class));
		assertFalse(attrs.contains("yetAnotherInteger", Integer.class));

		assertEquals(50, attrs.get("someInt", Integer.class).intValue());
		assertEquals(100, attrs.get("someOtherInt", Integer.class).intValue());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAddDoubleAttr() {
		mgr.createColumn("someDouble", Double.class, false);
		mgr.createColumn("someOtherDouble", Double.class, false);

		attrs.set("someDouble", 3.14);
		attrs.set("someOtherDouble", 2.76);

		assertTrue(attrs.contains("someDouble", Double.class));
		assertTrue(attrs.contains("someOtherDouble", Double.class));
		assertFalse(attrs.contains("yetAnotherDouble", Double.class));

		assertEquals(3.14, attrs.get("someDouble", Double.class).doubleValue());
		assertEquals(2.76, attrs.get("someOtherDouble", Double.class).doubleValue());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAddBooleanAttr() {
		mgr.createColumn("someBoolean", Boolean.class, false);
		mgr.createColumn("someOtherBoolean", Boolean.class, false);

		attrs.set("someBoolean", true);
		attrs.set("someOtherBoolean", false);

		assertTrue(attrs.contains("someBoolean", Boolean.class));
		assertTrue(attrs.contains("someOtherBoolean", Boolean.class));
		assertFalse(attrs.contains("yetAnotherBoolean", Boolean.class));

		assertTrue(attrs.get("someBoolean", Boolean.class));
		assertFalse(attrs.get("someOtherBoolean", Boolean.class));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAddListAttr() {
		mgr.createColumn("someList", List.class, false);

		List<String> l = new LinkedList<String>();
		l.add("orange");
		l.add("banana");

		attrs.set("someList", l);

		assertTrue(attrs.contains("someList", List.class));

		assertEquals(2, attrs.get("someList", List.class).size());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAddMapAttr() {
		mgr.createColumn("someMap", Map.class, false);

		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("orange", 1);
		m.put("banana", 2);

		attrs.set("someMap", m);

		assertTrue(attrs.contains("someMap", Map.class));

		assertEquals(2, attrs.get("someMap", Map.class).size());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAddBadAttr() {
		try {
			attrs.set("nodeColor", Color.white);
		} catch (IllegalArgumentException e) {
			// successfully caught the exception
			return;
		}

		// shouldn't get here
		fail();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAddBadList() {
		List<Color> l = new LinkedList<Color>();
		l.add(Color.white);
		l.add(Color.red);

		try {
			attrs.set("someList", l);
		} catch (IllegalArgumentException e) {
			// successfully caught the exception
			return;
		}

		// shouldn't get here
		fail();
	}

	// lots more needed
}
