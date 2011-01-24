/*
 Copyright (c) 2008, 2010, The Cytoscape Consortium (www.cytoscape.org)

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


import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.RowSetMicroListener;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnDeletedEvent;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;

import static org.junit.Assert.*;
import org.junit.Test;


import java.awt.Color;
import java.lang.RuntimeException;
import java.util.*;


public abstract class AbstractCyTableTest {
	protected CyTable table;
	protected CyTable table2;
	protected CyRow attrs;
	protected DummyCyEventHelper eventHelper; 
	protected boolean rowSetMicroListenerWasCalled;
	protected boolean rowCreatedMicroListenerWasCalled;
	protected boolean rowAboutToBeDeletedMicroListenerWasCalled;

	@Test
	public void testAddStringAttr() {
		table.createColumn("someString", String.class);
		table.createColumn("someStringElse", String.class);

		attrs.set("someString", "apple");
		attrs.set("someStringElse", "orange");

		assertTrue(attrs.isSet("someString", String.class));
		assertTrue(attrs.isSet("someStringElse", String.class));
		assertFalse(attrs.isSet("yetAnotherString", String.class));

		assertEquals("apple", attrs.get("someString", String.class));
		assertEquals("orange", attrs.get("someStringElse", String.class));
	}

	@Test
	public void testAddIntAttr() {
		table.createColumn("someInt", Integer.class);
		table.createColumn("someOtherInt", Integer.class);

		attrs.set("someInt", 50);
		attrs.set("someOtherInt", 100);

		assertTrue(attrs.isSet("someInt", Integer.class));
		assertTrue(attrs.isSet("someOtherInt", Integer.class));
		assertFalse(attrs.isSet("yetAnotherInteger", Integer.class));

		assertEquals(50, attrs.get("someInt", Integer.class).intValue());
		assertEquals(100, attrs.get("someOtherInt", Integer.class).intValue());
	}

	@Test
	public void testAddLongAttr() {
		table.createColumn("someLong", Long.class);
		table.createColumn("someOtherLong", Long.class);

		attrs.set("someLong", 50L);
		attrs.set("someOtherLong", 100L);

		assertTrue(attrs.isSet("someLong", Long.class));
		assertTrue(attrs.isSet("someOtherLong", Long.class));
		assertFalse(attrs.isSet("yetAnotherLong", Long.class));

		assertEquals(50, attrs.get("someLong", Long.class).intValue());
		assertEquals(100, attrs.get("someOtherLong", Long.class).intValue());
	}

	@Test
	public void testAddDoubleAttr() {
		table.createColumn("someDouble", Double.class);
		table.createColumn("someOtherDouble", Double.class);

		attrs.set("someDouble", 3.14);
		attrs.set("someOtherDouble", 2.76);

		assertTrue(attrs.isSet("someDouble", Double.class));
		assertTrue(attrs.isSet("someOtherDouble", Double.class));
		assertFalse(attrs.isSet("yetAnotherDouble", Double.class));

		assertEquals(3.14, attrs.get("someDouble", Double.class).doubleValue(), 0.000001);
		assertEquals(2.76, attrs.get("someOtherDouble", Double.class).doubleValue(), 0.000001);
	}

	@Test
	public void testAddBooleanAttr() {
		table.createColumn("someBoolean", Boolean.class);
		table.createColumn("someOtherBoolean", Boolean.class);

		attrs.set("someBoolean", true);
		attrs.set("someOtherBoolean", false);

		assertTrue(attrs.isSet("someBoolean", Boolean.class));
		assertTrue(attrs.isSet("someOtherBoolean", Boolean.class));
		assertFalse(attrs.isSet("yetAnotherBoolean", Boolean.class));

		assertTrue(attrs.get("someBoolean", Boolean.class));
		assertFalse(attrs.get("someOtherBoolean", Boolean.class));
	}

	@Test
	public void testAddListAttr() {
		table.createListColumn("someList", String.class);

		List<String> l = new LinkedList<String>();
		l.add("orange");
		l.add("banana");

		attrs.set("someList", l);

		assertTrue(attrs.isSet("someList", List.class));

		assertEquals(2, attrs.getList("someList", String.class).size());
	}

	@Test(expected=NullPointerException.class)
	public void testCreateListColumnWithFirstArgNull() {
		table.createListColumn(null, String.class);
	}

	@Test(expected=NullPointerException.class)
	public void testCreateListColumnWithSecondArgNull() {
		table.createListColumn("someList", null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testCreateListColumnWithAlreadyExistingCoulmnName() {
		table.createListColumn("someList", String.class);
		table.createListColumn("someList", String.class);
	}

	@Test
	public void testAddMapAttr() {
		table.createColumn("someMap", Map.class);

		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("orange", 1);
		m.put("banana", 2);

		attrs.set("someMap", m);

		assertTrue(attrs.isSet("someMap", Map.class));

		assertEquals(2, attrs.get("someMap", Map.class).size());
	}

	@Test
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

	@Test
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
	@Test
	public void testAddDuplicateNameAttr() {
		table.createColumn("something", String.class);
		try {
			table.createColumn("something", Integer.class);
		} catch (Exception e) {
			return;
		}
		fail();
	}

	@Test
	public void testRowSetMicroListener() {
		table.createColumn("someString", String.class);
		attrs.set("someString", "apple");

		assertTrue(eventHelper.getCalledMicroListeners().contains("handleRowSet"));
	}

	@Test
	public void testColumnCreatedEvent() {
		table.createColumn("someInt", Integer.class);

		Object last = eventHelper.getLastSynchronousEvent();
		assertNotNull( last );
		assertTrue( last instanceof ColumnCreatedEvent );
	}

	@Test
	public void testColumnDeletedEvent() {
		table.createColumn("someInt", Integer.class);
		table.deleteColumn("someInt");

		Object last = eventHelper.getLastSynchronousEvent();
		assertNotNull( last );
		assertTrue( last instanceof ColumnDeletedEvent );
	}

	@Test
	public void testColumnCreate() {
		table.createColumn("someInt", Integer.class);
		assertTrue( table.getColumnTypeMap().containsKey("someInt") );
		assertEquals( table.getColumnTypeMap().get("someInt"), Integer.class );
	}

	@Test
	public void testColumnDelete() {
		table.createColumn("someInt", Integer.class);
		assertTrue( table.getColumnTypeMap().containsKey("someInt") );
		
		table.deleteColumn("someInt");
		assertFalse( table.getColumnTypeMap().containsKey("someInt") );
	}

	@Test
	public void testPrimaryKey() {
		String pk = table.getPrimaryKey();
		assertEquals( table.getPrimaryKeyType(), table.getColumnTypeMap().get(pk) );
	}

	@Test
	public void testUnsetRowBoolean() {
		table.createColumn("someBoolean", Boolean.class);
		attrs.set("someBoolean", true);
		assertTrue(attrs.isSet("someBoolean", Boolean.class));
		attrs.set("someBoolean", null);
		assertFalse(attrs.isSet("someBoolean", Boolean.class));
		attrs.set("someBoolean", false);
		assertTrue(attrs.isSet("someBoolean", Boolean.class));
		attrs.set("someBoolean", null);
		assertFalse(attrs.isSet("someBoolean", Boolean.class));
	}

	@Test
	public void testUnsetRowString() {
		table.createColumn("someString", String.class);
		attrs.set("someString", "homer");
		assertTrue(attrs.isSet("someString", String.class));
		attrs.set("someString", null);
		assertFalse(attrs.isSet("someString", String.class));
	}

	@Test
	public void testUnsetRowInt() {
		table.createColumn("someInt", Integer.class);
		attrs.set("someInt", 5);
		assertTrue(attrs.isSet("someInt", Integer.class));
		attrs.set("someInt", null);
		assertFalse(attrs.isSet("someInt", Integer.class));
	}

	@Test
	public void testUnsetRowDouble() {
		table.createColumn("someDouble", Double.class);
		attrs.set("someDouble", 5.0);
		assertTrue(attrs.isSet("someDouble", Double.class));
		attrs.set("someDouble", null);
		assertFalse(attrs.isSet("someDouble", Double.class));
	}

	@Test
	public void testUnsetRowList() {
		List<String> ls = new ArrayList<String>();
		ls.add("asdf");
		table.createListColumn("someList", String.class);
		attrs.set("someList", ls);
		assertTrue(attrs.isSet("someList", List.class));
		attrs.set("someList", null);
		assertFalse(attrs.isSet("someList", List.class));
	}

	@Test
	public void testUnsetRowMap() {
		Map<Integer,String> mis = new HashMap<Integer,String>();
		mis.put(1,"two");
		table.createColumn("someMap", Map.class);
		attrs.set("someMap", mis);
		assertTrue(attrs.isSet("someMap", Map.class));
		attrs.set("someMap", null);
		assertFalse(attrs.isSet("someMap", Map.class));
	}

	@Test
	public void testGetListElementType() {
		table.createListColumn("someList2", Boolean.class);
		assertEquals(table.getListElementType("someList2"), Boolean.class);
	}

	@Test
	public void testGetColumnValues() {
		table.createColumn("someLongs", Long.class);
		final CyRow row1 = table.getRow(1L);
		row1.set("someLongs", 15L);
		final CyRow row2 = table.getRow(2L);
		row2.set("someLongs", -27L);
		final List<Long> values = table.getColumnValues("someLongs", Long.class);
		assertTrue(values.size() == 2);
		assertTrue(values.contains(15L));
		assertTrue(values.contains(-27L));
	}

	@Test
	public void testGetColumnValues2() {
		table.createColumn("someLongs", Long.class);
		final CyRow row1 = table.getRow(1L);
		row1.set("someLongs", 15L);
		final CyRow row2 = table.getRow(2L);
		row2.set("someLongs", -27L);
		final List<Long> values = table.getColumnValues(table.getPrimaryKey(), Long.class);
		assertTrue(values.size() == 2);
		assertTrue(values.contains(1L));
		assertTrue(values.contains(2L));
	}

	@Test(expected=NullPointerException.class)
	public void testGetRowWithNullKey() {
		table.createColumn("someLongs", Long.class);
		final CyRow row1 = table.getRow(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetRowWithWrongKeyType() {
		table.createColumn("someLongs", Long.class);
		final CyRow row1 = table.getRow("key");
	}

	@Test(expected=NullPointerException.class)
	public void testCreateColumnWithFirstArgNull() {
		table.createColumn(null, Map.class);
	}

	@Test(expected=NullPointerException.class)
	public void testCreateColumnWithSecondArgNull() {
		table.createColumn("someMap", null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testCreateColumnWithListColmnType() {
		table.createColumn("someList", List.class);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSetForListCoulmnWithInvalidValueType() {
		table.createListColumn("someList", String.class);
		attrs.set("someList", 3.5);
	}

	@Test
	public void testGetMatchingColumns() {
		table.createColumn("someLongs", Long.class);
		final CyRow row1 = table.getRow(1L);
		row1.set("someLongs", 15L);
		final CyRow row2 = table.getRow(2L);
		row2.set("someLongs", -27L);
		Set<CyRow> matchingRows = table.getMatchingRows("someLongs", 15L);
		assertTrue(matchingRows.size() == 1);
		matchingRows = table.getMatchingRows("someLongs", -15L);
		assertTrue(matchingRows.isEmpty());
	}

	@Test
	public void testSetAndGetTitle() {
		table.setTitle("my title");
		assertEquals(table.getTitle(), "my title");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetListElementTypeForANonListColumn() {
		table.createColumn("someList2", Boolean.class);
		table.getListElementType("someList2");
	}

	@Test
	public void testDeleteColumnNoOpWithNonexistingColumn() {
		table.deleteColumn("x");
	}

	@Test(expected=NullPointerException.class)
	public void testGetColumnValuesWithNullFirstArgument() {
		table.getColumnValues(null, String.class);
	}

	@Test(expected=NullPointerException.class)
	public void testGetColumnValuesWithNullSecondArgument() {
		table.createColumn("x", String.class);
		table.getColumnValues("x", null);
	}

	@Test
	public void testGetAllRows() {
		assertTrue(table.getAllRows().size() == 1);
		final CyRow row1 = table.getRow(11L);
		assertTrue(table.getAllRows().size() == 2);
		final CyRow row2 = table.getRow(22L);
		assertTrue(table.getAllRows().size() == 3);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetListWithANonExistantColumn() {
		attrs.getList("x", String.class);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetListWithAnInvalidListElementType() {
		table.createListColumn("x", Long.class);
		attrs.getList("x", String.class);
	}

	@Test
	public void testGetListWithAnMissingRowEntry() {
		table.createListColumn("x", Long.class);
		assertNull(attrs.getList("x", Long.class));
	}

	@Test
	public void testSetList() {
		table.createListColumn("l", String.class);
		final List<String> strings = new ArrayList<String>();
		strings.add("joe");
		attrs.set("l", strings);
		assertEquals(attrs.getList("l", String.class), strings);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetColumnValuesWithNonExistentColumnName() {
		table.getColumnValues("l", String.class);
	}

	@Test(expected=NullPointerException.class)
	public void testSetWithNullColumnName() {
		table.createColumn("l", String.class);
		attrs.set(null, "xyz");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSetWithInvalidValueType() {
		table.createColumn("l", Long.class);
		attrs.set("l", "xyz");
	}

	@Test
	public void testToStringMethodOfCyTable() {
		table.createColumn("l", Long.class);
		attrs.set("l", 13L);
		assertTrue(table.toString().length() > 0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testUnsetWithNonExistentColumnName() {
		attrs.set("l", null);
	}

	@Test(expected=Exception.class)
	public void testGetWhereGetListShouldHaveBeenUsed() {
		table.createListColumn("l", Long.class);
		attrs.set("l", new ArrayList<Long>());
		attrs.get("l", Long.class);
	}

	@Test(expected=Exception.class)
	public void testGetWithAnInvalidType() {
		table.createColumn("l", Long.class);
		attrs.set("l", 15L);
		attrs.get("l", CyTable.class);
	}

	@Test
	public void testGetAllValues() {
		table.createColumn("x", Long.class);
		table.createColumn("y", Double.class);
		attrs.set("x", 15L);
		attrs.set("y", 3.14);
		final Map<String, Object> values = attrs.getAllValues();
		assertTrue(values.keySet().contains("x"));
		assertTrue(values.keySet().contains("y"));
		assertEquals((long)(Long)values.get("x"), 15L);
		assertEquals((double)(Double)values.get("y"), 3.14, 0.00001);
	}

	@Test
	public void testGetType() {
		table.createColumn("someInt", Integer.class);
		assertEquals(table.getType("someInt"), Integer.class);
		assertNull(table.getType("nonExistentColumnName"));
	}

	@Test
	public void testGetRowCount() {
		final CyRow row = table.getRow(2L);
		assertEquals(table.getRowCount(), table.getAllRows().size());
	}

	@Test
	public void testHandleRowCreatedMicroListener() {
		final CyRow row = table.getRow(2L);
		assertTrue(eventHelper.getCalledMicroListeners().contains("handleRowCreated"));
	}

	@Test
	public void testGetWithPrimaryKey() {
		final CyRow row = table.getRow(107L);
		assertEquals(row.get(table.getPrimaryKey(), table.getPrimaryKeyType()), 107L);
	}

	@Test
	public void testVirtualColumnType() {
		table.createColumn("x", Long.class);
		table2.createColumn("x2", Long.class);
		table2.createColumn("s", String.class);
		assertEquals(table.addVirtualColumn("s1", "s", table2, "x2", "x"), "s1");
		assertEquals("Virtual column type should have been String!",
			     String.class, table.getType("s1"));
		assertEquals(table.addVirtualColumn("s1", "s", table2, "x2", "x"), "s1-1");
		assertEquals("Virtual column type should have been String!",
			     String.class, table.getType("s1-1"));
	}

	@Test
	public void testVirtualColumnIsSet() {
		table.createColumn("x", Integer.class);
		CyRow row1 = table.getRow(1L);
		row1.set("x", 33);
		table2.createColumn("x2", Integer.class);
		CyRow row2 =  table2.getRow(1L);
		row2.set("x2", 33);
		table2.createColumn("s", String.class);
		table.addVirtualColumn("s1", "s", table2, "x2", "x");
		assertFalse(row1.isSet("s1", String.class));
		row2.set("s", "abc");
		assertTrue(row1.isSet("s1", String.class));
	}

	@Test
	public void testVirtualColumnGet() {
		table.createColumn("x", Integer.class);
		CyRow row1 = table.getRow(1L);
		row1.set("x", 33);
		table2.createColumn("x2", Integer.class);
		CyRow row2 =  table2.getRow(1L);
		row2.set("x2", 33);
		table2.createColumn("s", String.class);
		table.addVirtualColumn("s1", "s", table2, "x2", "x");
		assertFalse(row1.isSet("s1", String.class));
		row2.set("s", "abc");
		assertEquals(row1.get("s1", String.class), "abc");
	}

	@Test
	public void testVirtualColumnSetWithAReplacementValue() {
		table.createColumn("x", Integer.class);
		CyRow row1 = table.getRow(1L);
		row1.set("x", 33);
		table2.createColumn("x2", Integer.class);
		CyRow row2 =  table2.getRow(1L);
		row2.set("x2", 33);
		table2.createColumn("s", String.class);
		table.addVirtualColumn("s1", "s", table2, "x2", "x");
		assertFalse(row1.isSet("s1", String.class));
		row2.set("s", "abc");
		assertEquals(row1.get("s1", String.class), "abc");
		row1.set("s1", "xyz");
		assertEquals(row1.get("s1", String.class), "xyz");
	}

	@Test
	public void testVirtualColumnUnset() {
		table.createColumn("x", Integer.class);
		CyRow row1 = table.getRow(1L);
		row1.set("x", 33);
		table2.createColumn("x2", Integer.class);
		CyRow row2 =  table2.getRow(1L);
		row2.set("x2", 33);
		table2.createColumn("s", String.class);
		table.addVirtualColumn("s1", "s", table2, "x2", "x");
		row2.set("s", "abc");
		assertTrue(row1.isSet("s1", String.class));
		row1.set("s1", null);
		assertFalse(row1.isSet("s1", String.class));
	}

	@Test
	public void testVirtualColumnGetMatchingRows() {
		table.createColumn("x", Integer.class);
		CyRow row1 = table.getRow(1L);
		row1.set("x", 33);
		table2.createColumn("x2", Integer.class);
		CyRow row2 =  table2.getRow(1L);
		row2.set("x2", 33);
		table2.createColumn("s", String.class);
		table.addVirtualColumn("s1", "s", table2, "x2", "x");
		assertFalse(row1.isSet("s1", String.class));
		row2.set("s", "abc");
		Set<CyRow> matchingRows = table.getMatchingRows("s1", "abc");
		assertEquals(matchingRows.size(), 1);
		CyRow matchingRow = matchingRows.iterator().next();
		assertEquals(matchingRow.get("s1", String.class), "abc");
		assertEquals(matchingRow.get("x", Integer.class), Integer.valueOf(33));
	}

	@Test
	public void testVirtualColumnDelete() {
		table.createColumn("x", Long.class);
		table2.createColumn("x2", Long.class);
		table2.createColumn("s", String.class);
		table.addVirtualColumn("s1", "s", table2, "x2", "x");
		assertNotNull(table.getType("s1"));
		table.deleteColumn("s1");
		assertNull(table.getType("s1"));
	}

	@Test
	public void testVirtualColumnListElementType() {
		table.createColumn("x", Integer.class);
		CyRow row1 = table.getRow(1L);
		row1.set("x", 33);
		table2.createColumn("x2", Integer.class);
		CyRow row2 =  table2.getRow(1L);
		row2.set("x2", 33);
		table2.createColumn("s", String.class);
		table.addVirtualColumn("s1", "s", table2, "x2", "x");
		assertFalse(row1.isSet("s1", String.class));
		row2.set("s", "abc");
		List<String> columnValues = table.getColumnValues("s1", String.class);
		assertEquals(1, columnValues.size());
		assertEquals("abc", columnValues.get(0));
	}

	@Test
	public void testVirtualColumnGetColumnValues() {
		table.createColumn("x", Long.class);
		table2.createColumn("x2", Long.class);
		table2.createListColumn("b", Boolean.class);
		table.addVirtualColumn("b1", "b", table2, "x2", "x");
		assertEquals("Virtual column list element type should have been Boolean!",
			     Boolean.class, table.getListElementType("b1"));
	}
}
