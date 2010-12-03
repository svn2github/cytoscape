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
package org.cytoscape.model.internal;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.cytoscape.equations.BooleanList;
import org.cytoscape.equations.DoubleList;
import org.cytoscape.equations.Equation;
import org.cytoscape.equations.IdentDescriptor;
import org.cytoscape.equations.Interpreter;
import org.cytoscape.equations.LongList;
import org.cytoscape.equations.StringList;

import org.cytoscape.event.CyEventHelper;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.RowSetMicroListener;

import org.cytoscape.model.internal.tsort.TopoGraphNode;
import org.cytoscape.model.internal.tsort.TopologicalSort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CyTableImpl implements CyTable {
	private static int counter = 0;
	private static final Logger logger = LoggerFactory.getLogger(CyTableImpl.class);

	private final Set<String> currentlyActiveAttributes;
	private final Map<String, Map<Object, Object>> attributes;
	private final Map<String, Map<Object, Set<Object>>> reverse;
	private final Map<Object, CyRow> rows;

	private final Map<String, Class<?>> types;
	private final Map<String, Class<?>> listElementTypes;

	// This is not unique and might be changed by user.
	private String title;

	// Visibility value is immutable.
	private final boolean pub;

	// Unique ID.
	private final long suid;

	// name of the primary key column
	private final String primaryKey;

	// type of the primary key column
	private final Class<?> primaryKeyType;

	private final CyEventHelper eventHelper;
	private final Interpreter interpreter;

	private String lastInternalError = null;

	/**
	 * Creates a new CyTableImpl object.
	 *
	 * @param typeMap
	 *            DOCUMENT ME!
	 * @param name
	 *            DOCUMENT ME!
	 * @param pub
	 *            DOCUMENT ME!
	 */
	public CyTableImpl(final String title, final String primaryKey, Class<?> pkType,
			   final boolean pub, final CyEventHelper eventHelper,
			   final Interpreter interpreter)
	{
		this.title = title;
		this.primaryKey = primaryKey;
		this.primaryKeyType = getClass(pkType);
		this.pub = pub;
		this.suid = SUIDFactory.getNextSUID();
		this.eventHelper = eventHelper;
		this.interpreter = interpreter;

		currentlyActiveAttributes = new HashSet<String>();
		attributes = new HashMap<String, Map<Object, Object>>();
		reverse =  new HashMap<String, Map<Object, Set<Object>>>();
		rows = new HashMap<Object, CyRow>();
		types = new HashMap<String, Class<?>>();
		listElementTypes = new HashMap<String, Class<?>>();

		// Create the primary key column.  Do this explicitly
		// so that we don't fire an event.
		types.put(primaryKey, primaryKeyType);
		attributes.put(primaryKey, new HashMap<Object, Object>());
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public long getSUID() {
		return suid;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean isPublic() {
		return pub;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param title
	 *            DOCUMENT ME!
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Map<String, Class<?>> getColumnTypeMap() {
		return Collections.unmodifiableMap(types);
	}

	public Class<?> getListElementType(final String columnName) {
		final Class<?> listElementType = listElementTypes.get(columnName);
		if (listElementType == null)
			throw new IllegalArgumentException("can't get list element type for nonexistent or non-List column '"
							   + columnName + "'!");
		return listElementType;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public Class<?> getPrimaryKeyType() {
		return primaryKeyType;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param columnName
	 *            DOCUMENT ME!
	 */
	synchronized public void deleteColumn(String columnName) {
		if (attributes.containsKey(columnName)) {
			attributes.remove(columnName);
			reverse.remove(columnName);
			types.remove(columnName);
			listElementTypes.remove(columnName);

			// This event must be synchronous!
			eventHelper.fireSynchronousEvent(new ColumnDeletedEvent(this, columnName));
		}
	}

	/**
	 * DOCUMENT ME!
	 * @param type
	 *            DOCUMENT ME!
	 * @param columnName
	 *            DOCUMENT ME!
	 *
	 * @param <T>
	 *            DOCUMENT ME!
	 */
	public <T> void createColumn(String columnName, Class<? extends T> type) {
		if (columnName == null)
			throw new NullPointerException("attribute name is null");

		if (type == null)
			throw new NullPointerException("type is null");

		if (types.get(columnName) != null)
			throw new IllegalArgumentException("attribute already exists for name: '"
							   + columnName + "' with type: "
							   + types.get(columnName).getName());

		if (type == List.class)
			throw new IllegalArgumentException(
				"use createListColumn() to create List columns instead of createColumn for attribute '"
				+ columnName + "'!");

		types.put(columnName, type);
		attributes.put(columnName, new HashMap<Object, Object>());
		reverse.put(columnName, new HashMap<Object, Set<Object>>());

		eventHelper.fireAsynchronousEvent(new ColumnCreatedEvent(this, columnName));
	}

	public <T> void createListColumn(final String columnName, final Class<T> listElementType)
	{
		if (columnName == null)
			throw new NullPointerException("attribute name is null");

		if (listElementType == null)
			throw new NullPointerException("listElementType is null");

		if (types.get(columnName) != null)
			throw new IllegalArgumentException("attribute already exists for name: '"
							   + columnName + "' with type: "
							   + types.get(columnName).getName());
		types.put(columnName, List.class);
		listElementTypes.put(columnName, listElementType);
		attributes.put(columnName, new HashMap<Object, Object>());
		reverse.put(columnName, new HashMap<Object, Set<Object>>());

		eventHelper.fireAsynchronousEvent(new ColumnCreatedEvent(this, columnName));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param <T>
	 *            DOCUMENT ME!
	 * @param columnName
	 *            DOCUMENT ME!
	 * @param type
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public <T> List<T> getColumnValues(String columnName, Class<? extends T> type) {
		if (columnName == null)
			throw new NullPointerException("column name is null!");

		if (type == null)
			throw new NullPointerException("column type is null!");

		Map<Object, Object> vals = attributes.get(columnName);
		if (vals == null)
			throw new IllegalArgumentException("attribute does not exist");

		List<T> l = new ArrayList<T>(vals.size());
		for (final Object suid : vals.keySet()) {
			final Object value = vals.get(suid);
			if (value instanceof Equation) {
				final Object eqnValue = evalEquation((Equation)value, suid, columnName);
				if (eqnValue == null)
					throw new IllegalStateException("can't convert an equation to a value!");
				l.add(type.cast(eqnValue));
			} else
				l.add(type.cast(value));
		}

		return l;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param suid
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public CyRow getRow(final Object key) {
		checkKey(key);
		CyRow row = rows.get(key);
		if (row != null)
			return row;

		row = new InternalRow(key, this);
		rows.put(key, row);
		return row;
	}

	@Override
	public String getLastInternalError() {
		return lastInternalError;
	}

	private void checkKey(final Object suid) {
		if (suid == null)
			throw new NullPointerException("key is null");

		if (!primaryKeyType.isAssignableFrom(suid.getClass()))
			throw new IllegalArgumentException("key of type " + suid.getClass()
							   + " and not the expected: " + primaryKeyType);

	}

	public List<CyRow> getAllRows() {
		return new ArrayList<CyRow>(rows.values());
	}

	public Set<CyRow> getMatchingRows(final String columnName, final Object value) {
		final Map<Object, Set<Object>> valueToKeysMap = reverse.get(columnName);

		final Set<Object> keys = valueToKeysMap.get(value);
		if (keys == null)
			return new HashSet<CyRow>();

		final Set<CyRow> matchingRows = new HashSet<CyRow>(keys.size());
		for (final Object key : keys)
			matchingRows.add(rows.get(key));

		return matchingRows;
	}

	private void setX(final Object key, final String columnName, final Object value) {
		++counter;
		if (columnName == null)
			throw new NullPointerException("columnName must not be null!");
		if (value == null)
			throw new NullPointerException("value must not be null!");

		final Class<?> columnType = types.get(columnName);
		if (columnType == null || !attributes.containsKey(columnName))
			throw new IllegalArgumentException("attribute: '" + columnName
					+ "' does not yet exist!");

		if (types.get(columnName) == List.class) {
			setListX(key, columnName, value);
			return;
		}

		if (!(value instanceof Equation))
			checkType(value);

		Map<Object, Object> keyToValueMap = attributes.get(columnName);

		final Class targetType = types.get(columnName);
		if (targetType.isAssignableFrom(value.getClass())
		    || scalarEquationIsCompatible(value, targetType))
		{
			if (value instanceof Equation) {
				final Equation equation = (Equation)value;
				// TODO this is an implicit addRow - not sure if we want to refactor this or not
				keyToValueMap.put(key, equation);
				final Object eqnValue = evalEquation(equation, key, columnName);
				if (eqnValue == null)
					logger.warn("attempted premature evaluation evaluation for " + equation);
				else
				eventHelper.getMicroListener(
						RowSetMicroListener.class,
						getRow(key)).handleRowSet(columnName, eqnValue);
			} else {
				// TODO this is an implicit addRow - not sure if we want to refactor this or not
				final Object newValue = columnType.cast(value);
				keyToValueMap.put(key, newValue);
				addToReverseMap(columnName, key, newValue);
				eventHelper.getMicroListener(RowSetMicroListener.class,
							     getRow(key)).handleRowSet(columnName, newValue);
			}
		} else
			throw new IllegalArgumentException("value is not of type: "
					+ types.get(columnName));
	}

	private void addToReverseMap(final String columnName, final Object key, final Object value) {
		final Map<Object, Set<Object>> valueTokeysMap = reverse.get(columnName);
		Set<Object> keys = valueTokeysMap.get(value);
		if (keys == null) {
			keys = new HashSet<Object>();
			valueTokeysMap.put(value, keys);
		}

		keys.add(key);
	}

	private static boolean scalarEquationIsCompatible(final Object equationCandidate,
							  final Class targetType)
	{
		if (!(equationCandidate instanceof Equation))
			return false;

		final Equation equation = (Equation)equationCandidate;
		final Class<?> eqnReturnType = equation.getType();

		if (targetType == Double.class || targetType == Boolean.class
		    || targetType == Integer.class || targetType == Long.class)
			return eqnReturnType == Double.class || eqnReturnType == Long.class
			       || eqnReturnType == Boolean.class;
		else if (targetType == String.class)
			return true; // Everything can be turned into a String!
		else
			return false;
	}

	private void setListX(final Object key, final String columnName, final Object value) {
		if (value instanceof List) {
			final List list = (List)value;
			if (!list.isEmpty())
				checkType(list.get(0));
		} else if (!(value instanceof Equation))
			throw new IllegalArgumentException("value is a " + value.getClass().getName()
							   + " and not a List for column '"
							   + columnName + "'!");
		else if (!listEquationIsCompatible((Equation)value, listElementTypes.get(columnName)))
			throw new IllegalArgumentException(
				"value is not a List equation of a compatible type for column '"
				+ columnName + "'!");

		Map<Object, Object> keyToValueMap = attributes.get(columnName);

		// TODO this is an implicit addRow - not sure if we want to refactor this or not
		keyToValueMap.put(key, value);
		if (value instanceof List)
			addToReverseMap(columnName, key, value);
		eventHelper.getMicroListener(RowSetMicroListener.class,
					     getRow(key)).handleRowSet(columnName, value);
	}

	private static boolean listEquationIsCompatible(final Equation equation,
							final Class listElementType)
	{
		final Class<?> eqnReturnType = equation.getType();
		if (eqnReturnType == BooleanList.class)
			return listElementType == Boolean.class;
		if (eqnReturnType == DoubleList.class)
			return listElementType == Double.class;
		if (eqnReturnType == StringList.class)
			return listElementType == String.class;
		if (eqnReturnType == LongList.class)
			return listElementType == Long.class;
		// TODO: Add support for a hypothetical IntegerList type.

		return false;
	}

	private void unSetX(final Object key, final String columnName) {
		if (!types.containsKey(columnName) || !attributes.containsKey(columnName))
			throw new IllegalArgumentException("attribute: '" + columnName
							   + "' does not yet exist!");

		final Map<Object, Object> keyToValueMap = attributes.get(columnName);
		if (!keyToValueMap.containsKey(key))
			return;

		final Object value = keyToValueMap.get(key);
		if (!(value instanceof Equation))
			removeFromReverseMap(columnName, key, value);
		keyToValueMap.remove(key);
		eventHelper.getMicroListener(RowSetMicroListener.class, getRow(key)).handleRowSet(columnName, null);
	}

	private static boolean isScalarColumnType(final Class type) {
		return type != List.class && type != Map.class;
	}

	private void removeFromReverseMap(final String columnName, final Object key, final Object value) {
		final Map<Object, Set<Object>> valueTokeysMap = reverse.get(columnName);
		Set<Object> keys = valueTokeysMap.get(value);
		keys.remove(key);
		if (keys.isEmpty())
			valueTokeysMap.remove(value);
	}

	private Object getRawX(final Object key, final String columnName) {
		Map<Object, Object> keyToValueMap = attributes.get(columnName);
		if (keyToValueMap == null)
			return null;

		return keyToValueMap.get(key);
	}

	private <T> T getX(final Object key, final String columnName, final Class<? extends T> type) {
		if (type.isAssignableFrom(List.class))
			throw new IllegalArgumentException("use getList() to retrieve lists!");

		final Object vl = getRawX(key, columnName);
		if (vl == null)
			return null;

		if (vl instanceof Equation) {
			final Object result = evalEquation((Equation)vl, key, columnName);
			return type.cast(result);
		} else
			return type.cast(vl);
	}

	private Object getValue(Object key, String columnName) {
		final Object vl = getRawX(key, columnName);
		if (vl == null)
			return null;

		if (vl instanceof Equation)
			return evalEquation((Equation)vl, key, columnName);
		else
			return vl;
	}

	private <T> List<?extends T> getListX(final Object key, final String columnName,
					      final Class<? extends T> listElementType)
	{
		final Class<?> expectedListElementType = listElementTypes.get(columnName);
		if (expectedListElementType == null)
			throw new IllegalArgumentException("'" + columnName
							   + "' is either not a List or does not exist!");
		if (expectedListElementType != listElementType)
			throw new IllegalArgumentException("invalid list element type for column '"
							   + columnName + ", found: " + listElementType.getName()
							   + ", expected: " + expectedListElementType.getName()
							   + "!");

		final Object vl = getRawX(key, columnName);
		if (vl == null)
			return null;

		if (vl instanceof Equation) {
			final Object result = evalEquation((Equation)vl, key, columnName);
			return (List)result;
		} else
			return (List)vl;
	}

	private <T> boolean isSetX(final Object key, final String columnName,
				   final Class<? extends T> type)
	{
		final Map<Object, Object> keyToValueMap = attributes.get(columnName);
		if (keyToValueMap == null)
			return false;

		Object value = keyToValueMap.get(key);
		if (value == null)
			return false;

		if (types.get(columnName).isAssignableFrom(type))
			return true;
		else

			return false;
	}

	private Class<?> getClass(Class<?> c) {
		if (c == Integer.class || c == Long.class || c == Double.class || c == String.class
		    || c == Boolean.class)
			return c;

		if (Integer.class.isAssignableFrom(c))
			return Integer.class;
		else if (Long.class.isAssignableFrom(c))
			return Long.class;
		else if (Double.class.isAssignableFrom(c))
			return Double.class;
		else if (Boolean.class.isAssignableFrom(c))
			return Boolean.class;
		else if (String.class.isAssignableFrom(c))
			return String.class;
		else if (List.class.isAssignableFrom(c))
			return List.class;
		else if (Map.class.isAssignableFrom(c))
			return Map.class;
		else
			throw new IllegalArgumentException("invalid class: " + c.getName());
	}

	private void checkType(Object o) {
		if (o instanceof String)
			return;
		else if (o instanceof Integer)
			return;
		else if (o instanceof Boolean)
			return;
		else if (o instanceof Double)
			return;
		else if (o instanceof Long)
			return;
		else if (o instanceof Map) {
			Map m = (Map) o;
			Object[] keys = m.keySet().toArray();
			if (keys.length > 0) {
				checkType(m.get(keys[0]));
				checkType(keys[0]);
			}
		} else
			throw new RuntimeException("invalid type: " + o.getClass().toString());
	}

	private class InternalRow implements CyRow {
		private final Object key;
		private final CyTable table;

		InternalRow(Object key, CyTable table) {
			this.key = key;
			this.table = table;
		}

		public void set(String attributeName, Object value) {
			if (value == null)
				unSetX(key, attributeName);
			else
				setX(key, attributeName, value);
		}

		public <T> void setList(String attributeName, List<?extends T> list) {
			if (list == null)
				unSetX(key, attributeName);
			else
				setListX(key, attributeName, list);
		}

		public <T> T get(String attributeName, Class<? extends T> c) {
			return getX(key, attributeName, c);
		}

		public <T> List<?extends T> getList(String attributeName, Class<T> c) {
			return getListX(key, attributeName, c);
		}

		public Object getRaw(String attributeName) {
			return getRawX(key, attributeName);
		}

		public <T> boolean isSet(String attributeName, Class<? extends T> c) {
			return isSetX(key, attributeName, c);
		}

		public Map<String, Object> getAllValues() {
			Map<String, Object> m = new HashMap<String, Object>(attributes
					.size());

			for (String attr : attributes.keySet())
				m.put(attr, attributes.get(attr).get(key));

			return m;
		}

		public CyTable getDataTable() {
			return table;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			final Map<String, Object> allVal = getAllValues();
			for(String key: getAllValues().keySet())
				builder.append(key + " = " + allVal.get(key) + ", ");
			return builder.toString();
		}
	}

	private Object evalEquation(final Equation equation, final Object key,
				    final String columnName)
	{
		if (currentlyActiveAttributes.contains(columnName)) {
			currentlyActiveAttributes.clear();
			lastInternalError = "Recursive equation evaluation of \"" + columnName + "\"!";
			return null;
		} else
			currentlyActiveAttributes.add(columnName);

		final Collection<String> attribReferences = equation.getVariableReferences();

		final Map<String, IdentDescriptor> nameToDescriptorMap = new TreeMap<String, IdentDescriptor>();
		for (final String attribRef : attribReferences) {
			if (attribRef.equals("ID")) {
				nameToDescriptorMap.put("ID", new IdentDescriptor(key));
				continue;
			}

			final Object attribValue = getValue(key, attribRef);
			if (attribValue == null) {
				currentlyActiveAttributes.clear();
				lastInternalError = "Missing value for referenced attribute \"" + attribRef + "\"!";
				logger.warn("Missing value for \"" + attribRef
				            + "\" while evaluating an equation (ID:" + key
				            + ", attribute name:" + columnName + ")");
				return null;
			}

			try {
				nameToDescriptorMap.put(attribRef, new IdentDescriptor(attribValue));
			} catch (final Exception e) {
				currentlyActiveAttributes.clear();
				lastInternalError = "Bad attribute reference to \"" + attribRef + "\"!";
				logger.warn("Bad attribute reference to \"" + attribRef
				            + "\" while evaluating an equation (ID:" + key
				            + ", attribute name:" + columnName + ")");
				return null;
			}
		}

		try {
			final Object result = interpreter.execute(equation, nameToDescriptorMap);
			currentlyActiveAttributes.remove(columnName);
			return result;
		} catch (final Exception e) {
			currentlyActiveAttributes.clear();
			lastInternalError = e.getMessage();
			logger.warn("Error while evaluating an equation: " + e.getMessage() + " (ID:"
			            + key + ", attribute name:" + columnName + ")");
			return null;
		}
	}

	/**
	 *  @return an in-order list of attribute names that will have to be evaluated before "columnName" can be evaluated
	 */
	private List<String> topoSortAttribReferences(final Object key, final String columnName) {
		final Object equationCandidate = getRawX(key, columnName);
		if (!(equationCandidate instanceof Equation))
			return new ArrayList<String>();

		final Equation equation = (Equation)equationCandidate;
		final Set<String> attribReferences = equation.getVariableReferences();
		if (attribReferences.size() == 0)
			return new ArrayList<String>();

		final Set<String> alreadyProcessed = new TreeSet<String>();
		alreadyProcessed.add(columnName);
		final List<TopoGraphNode> dependencies = new ArrayList<TopoGraphNode>();
		for (final String attribReference : attribReferences)
                        followReferences(key, attribReference, alreadyProcessed, dependencies);


		final List<TopoGraphNode> topoOrder = TopologicalSort.sort(dependencies);
		final List<String> retVal = new ArrayList<String>();
		for (final TopoGraphNode node : topoOrder) {
			final AttribTopoGraphNode attribTopoGraphNode = (AttribTopoGraphNode)node;
			final String nodeName = attribTopoGraphNode.getNodeName();
			if (nodeName.equals(columnName))
				return retVal;
			else
				retVal.add(nodeName);
		}

		// We should never get here because "columnName" should have been found in the for-loop above!
		throw new IllegalStateException("\"" + columnName
		                                + "\" was not found in the toplogical order, which should be impossible!");
	}

	/**
	 *  Helper function for topoSortAttribReferences() performing a depth-first search of equation evaluation dependencies.
	 */
	private void followReferences(final Object key, final String columnName, final Collection<String> alreadyProcessed,
	                              final Collection<TopoGraphNode> dependencies)
	{
		// Already visited this attribute?
		if (alreadyProcessed.contains(columnName))
			return;

		alreadyProcessed.add(columnName);
		final Object equationCandidate = getRawX(key, columnName);
		if (!(equationCandidate instanceof Equation))
			return;

		final Equation equation = (Equation)equationCandidate;
		final Set<String> attribReferences = equation.getVariableReferences();
		for (final String attribReference : attribReferences)
			followReferences(key, attribReference, alreadyProcessed, dependencies);
	}

	/**
	 *  @return "x" truncated using Excel's notion of truncation.
	 */
	private static double excelTrunc(final double x) {
		final boolean isNegative = x < 0.0;
		return Math.round(x + (isNegative ? +0.5 : -0.5));
	}

	/**
	 *  @return "d" converted to an Integer using Excel rules, should the number be outside the range of an int, null will be returned
	 */
	private static Integer doubleToInteger(final double d) {
		if (d > Integer.MAX_VALUE || d < Integer.MIN_VALUE)
			return null;

		double x = ((Double)d).intValue();
		if (x != d && x < 0.0)
			--x;

		return (Integer)(int)x;
	}

	/**
	 *  @return "l" converted to an Integer using Excel rules, should the number be outside the range of an int, null will be returned
	 */
	private static Integer longToInteger(final double l) {
		if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE)
			return (Integer)(int)l;

		return null;
	}

	/**
	 *  @return "equationValue" interpreted according to Excel rules as an integer or null if that is not possible
	 */
	private Integer convertEqnRetValToInteger(final String id, final String columnName, final Object equationValue) {
		if (equationValue.getClass() == Double.class) {
			final Integer retVal = doubleToInteger((Double)equationValue);
			if (retVal == null)
				logger.warn("Cannot convert a floating point value ("
					    + equationValue + ") to an integer!  (ID:" + id
					    + ", attribute name:" + columnName + ")");
			return retVal;
		}
		else if (equationValue.getClass() == Long.class) {
			final Integer retVal = longToInteger((Long)equationValue);
			if (retVal == null)
				logger.warn("Cannot convert a large integer (long) value ("
					    + equationValue + ") to an integer! (ID:" + id
					    + ", attribute name:" + columnName + ")");
			return retVal;
		}
		else if (equationValue.getClass() == Boolean.class) {
			final Boolean boolValue = (Boolean)equationValue;
			return (Integer)(boolValue ? 1 : 0);
		}
		else
			throw new IllegalStateException("we should never get here!");
	}

	/**
	 *  @return "equationValue" interpreted according to Excel rules as a double or null if that is not possible
	 */
	private Double convertEqnRetValToDouble(final String id, final String columnName, final Object equationValue) {
		if (equationValue.getClass() == Double.class)
			return (Double)equationValue;
		else if (equationValue.getClass() == Long.class)
			return (double)(Long)(equationValue);
		else if (equationValue.getClass() == Boolean.class) {
			final Boolean boolValue = (Boolean)equationValue;
			return boolValue ? 1.0 : 0.0;
		}
		else if (equationValue.getClass() == String.class) {
			final String valueAsString = (String)equationValue;
			try {
				return Double.parseDouble(valueAsString);
			} catch (final NumberFormatException e) {
				logger.warn("Cannot convert a string (\"" + valueAsString
				            + "\") to a floating point value! (ID:" + id
                                            + ", attribute name:" + columnName + ")");
				return null;
			}
		}
		else
			throw new IllegalStateException("we should never get here!");
	}

	/**
	 *  @return "equationValue" interpreted according to Excel rules as a boolean
	 */
	private Boolean convertEqnRetValToBoolean(final String id, final String columnName, final Object equationValue) {
		if (equationValue.getClass() == Double.class)
			return (Double)equationValue != 0.0;
		else if (equationValue.getClass() == Long.class)
			return (Long)(equationValue) != 0L;
		else if (equationValue.getClass() == Boolean.class) {
			return (Boolean)equationValue;
		}
		else if (equationValue.getClass() == String.class) {
			final String stringValue = (String)equationValue;
			if (stringValue.compareToIgnoreCase("true") == 0)
				return true;
			else if (stringValue.compareToIgnoreCase("false") == 0)
				return false;
			else {
				logger.warn("Cannot convert a string (\"" + stringValue
				            + "\") to a boolean value! (ID:" + id
                                            + ", attribute name:" + columnName + ")");
				return null;
			}
		}
		else
			throw new IllegalStateException("we should never get here!");
	}
}
