
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

import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.RowSetMicroListener;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnDeletedEvent;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;

import java.awt.Color;

import java.lang.RuntimeException;

import java.util.*;


/**
 * DOCUMENT ME!
 */
public abstract class AbstractCyTableTest extends TestCase {

	protected CyTable mgr;
	protected CyRow attrs;
	protected DummyCyEventHelper eventHelper; 


	/**
	 *  DOCUMENT ME!
	 */
	public void testAddStringAttr() {
		mgr.createColumn("someString", String.class);
		mgr.createColumn("someStringElse", String.class);

		attrs.set("someString", "apple");
		attrs.set("someStringElse", "orange");

		assertTrue(attrs.isSet("someString", String.class));
		assertTrue(attrs.isSet("someStringElse", String.class));
		assertFalse(attrs.isSet("yetAnotherString", String.class));

		assertEquals("apple", attrs.get("someString", String.class));
		assertEquals("orange", attrs.get("someStringElse", String.class));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAddIntAttr() {
		mgr.createColumn("someInt", Integer.class);
		mgr.createColumn("someOtherInt", Integer.class);

		attrs.set("someInt", 50);
		attrs.set("someOtherInt", 100);

		assertTrue(attrs.isSet("someInt", Integer.class));
		assertTrue(attrs.isSet("someOtherInt", Integer.class));
		assertFalse(attrs.isSet("yetAnotherInteger", Integer.class));

		assertEquals(50, attrs.get("someInt", Integer.class).intValue());
		assertEquals(100, attrs.get("someOtherInt", Integer.class).intValue());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAddDoubleAttr() {
		mgr.createColumn("someDouble", Double.class);
		mgr.createColumn("someOtherDouble", Double.class);

		attrs.set("someDouble", 3.14);
		attrs.set("someOtherDouble", 2.76);

		assertTrue(attrs.isSet("someDouble", Double.class));
		assertTrue(attrs.isSet("someOtherDouble", Double.class));
		assertFalse(attrs.isSet("yetAnotherDouble", Double.class));

		assertEquals(3.14, attrs.get("someDouble", Double.class).doubleValue());
		assertEquals(2.76, attrs.get("someOtherDouble", Double.class).doubleValue());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAddBooleanAttr() {
		mgr.createColumn("someBoolean", Boolean.class);
		mgr.createColumn("someOtherBoolean", Boolean.class);

		attrs.set("someBoolean", true);
		attrs.set("someOtherBoolean", false);

		assertTrue(attrs.isSet("someBoolean", Boolean.class));
		assertTrue(attrs.isSet("someOtherBoolean", Boolean.class));
		assertFalse(attrs.isSet("yetAnotherBoolean", Boolean.class));

		assertTrue(attrs.get("someBoolean", Boolean.class));
		assertFalse(attrs.get("someOtherBoolean", Boolean.class));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAddListAttr() {
		mgr.createColumn("someList", List.class);

		List<String> l = new LinkedList<String>();
		l.add("orange");
		l.add("banana");

		attrs.set("someList", l);

		assertTrue(attrs.isSet("someList", List.class));

		assertEquals(2, attrs.get("someList", List.class).size());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAddMapAttr() {
		mgr.createColumn("someMap", Map.class);

		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("orange", 1);
		m.put("banana", 2);

		attrs.set("someMap", m);

		assertTrue(attrs.isSet("someMap", Map.class));

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

	// You can't have an attribute with the same name, but
	// a different type.
	public void testAddDuplicateNameAttr() {
		mgr.createColumn("something", String.class);
		try {
			mgr.createColumn("something", Integer.class);
		} catch (Exception e) {
			return;
		}
		fail();
	}

	public void testRowSetMicroListener() {
		mgr.createColumn("someString", String.class);
		attrs.set("someString", "apple");

		Object last = eventHelper.getLastMicroListener();
		assertNotNull( last );
		assertTrue( last instanceof RowSetMicroListener );
	}

	public void testColumnCreatedEvent() {
		mgr.createColumn("someInt", Integer.class);

		Object last = eventHelper.getLastAsynchronousEvent();
		assertNotNull( last );
		assertTrue( last instanceof ColumnCreatedEvent );
	}

	public void testColumnDeletedEvent() {
		mgr.createColumn("someInt", Integer.class);
		mgr.deleteColumn("someInt");

		Object last = eventHelper.getLastSynchronousEvent();
		assertNotNull( last );
		assertTrue( last instanceof ColumnDeletedEvent );
	}

	public void testColumnCreate() {
		mgr.createColumn("someInt", Integer.class);
		assertTrue( mgr.getColumnTypeMap().containsKey("someInt") );
		assertEquals( mgr.getColumnTypeMap().get("someInt"), Integer.class );
	}

	public void testColumnDelete() {
		mgr.createColumn("someInt", Integer.class);
		assertTrue( mgr.getColumnTypeMap().containsKey("someInt") );
		
		mgr.deleteColumn("someInt");
		assertFalse( mgr.getColumnTypeMap().containsKey("someInt") );
	}

	public void testPrimaryKey() {
		String pk = mgr.getPrimaryKey();
		assertEquals( mgr.getPrimaryKeyType(), mgr.getColumnTypeMap().get(pk) );
	}

	// lots more needed
}
