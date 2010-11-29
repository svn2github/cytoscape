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

import org.cytoscape.util.tsort.TopoGraphNode;
import org.cytoscape.util.tsort.TopologicalSort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CyTableImpl implements CyTable {
	private static final Logger logger = LoggerFactory.getLogger(CyTableImpl.class);

	private final Set<String> currentlyActiveAttributes;
	private final Map<String, Map<Object, Object>> attributes;
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
	public void deleteColumn(String columnName) {
		if (attributes.containsKey(columnName)) {
			attributes.remove(columnName);
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
	public <T> List<T> getColumnValues(String columnName,
			Class<? extends T> type)
	{
		if (columnName == null)
			throw new NullPointerException("column name is null");

		Map<Object, Object> vals = attributes.get(columnName);

		if (vals == null)
			throw new NullPointerException("attribute does not exist");

		List<T> l = new ArrayList<T>(vals.size());

		for (Object o : vals.values())
			l.add(type.cast(o)); // TODO: add equation support

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
	public CyRow getRow(final Object suid) {
		checkKey(suid);
		CyRow row = rows.get(suid);
		if (row != null)
			return row;

		row = new InternalRow(suid, this);
		rows.put(suid, row);
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

	// internal methods
	private void removeX(Object suid, String attributeName) {
		if (attributes.containsKey(attributeName)) {
			Map<Object, Object> map = attributes.get(attributeName);

			if (map.containsKey(suid))
				map.remove(suid);
		}
	}

	private void setX(Object suid, String attrName, Object value) {
		assert(value != null);

		if (!types.containsKey(attrName) || !attributes.containsKey(attrName))
			throw new IllegalArgumentException("attribute: '" + attrName
					+ "' does not yet exist!");

		if (types.get(attrName) == List.class) {
			setListX(suid, attrName, value);
			return;
		}

		checkType(value);

		Map<Object, Object> vls = attributes.get(attrName);

		final Class targetType = types.get(attrName);
		if (targetType.isAssignableFrom(value.getClass())
		    || scalarEquationIsCompatible(value, targetType))
		{
			// TODO this is an implicit addRow - not sure if we want to refactor this or not
			vls.put(suid, value);
			eventHelper.getMicroListener(RowSetMicroListener.class, getRow(suid)).handleRowSet(attrName,value);
		} else
			throw new IllegalArgumentException("value is not of type: "
					+ types.get(attrName));
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

	private void setListX(final Object suid, final String columnName, final Object value) {
		if (value instanceof List) {
			final List list = (List)value;
			if (!list.isEmpty())
				checkType(list.get(0));
		} else if (!(value instanceof Equation))
			throw new IllegalArgumentException("value is not a List for column '"
							   + columnName + "'!");
		else if (!listEquationIsCompatible((Equation)value, listElementTypes.get(columnName)))
			throw new IllegalArgumentException(
				"value is not a List equation of a compatible type for column '"
				+ columnName + "'!");

		Map<Object, Object> vls = attributes.get(columnName);

		// TODO this is an implicit addRow - not sure if we want to refactor this or not
		vls.put(suid, value);
		eventHelper.getMicroListener(RowSetMicroListener.class,
					     getRow(suid)).handleRowSet(columnName, value);
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

	private void unSetX(Object suid, String columnName) {

		if (!types.containsKey(columnName) || !attributes.containsKey(columnName))
			throw new IllegalArgumentException("attribute: '" + columnName
					+ "' does not yet exist!");

		Map<Object, Object> vls = attributes.get(columnName);

		vls.remove(suid);
		eventHelper.getMicroListener(RowSetMicroListener.class, getRow(suid)).handleRowSet(columnName,null);
	}

	private Object getRawX(Object suid, String columnName) {
		Map<Object, Object> vls = attributes.get(columnName);

		if (vls == null)
			return null;

		return vls.get(suid);
	}

	private <T> T getX(Object suid, String columnName, Class<? extends T> type) {
		if (type.isAssignableFrom(List.class))
			throw new IllegalArgumentException("use getList() to retrieve lists!");

		final Object vl = getRawX(suid, columnName);
		if (vl == null)
			return null;

		if (vl instanceof Equation) {
			final Object result = evalEquation((Equation)vl, suid, columnName);
			return type.cast(result);
		} else
			return type.cast(vl);
	}

	private Object getX(Object suid, String columnName) {
		final Object vl = getRawX(suid, columnName);
		if (vl == null)
			return null;

		if (vl instanceof Equation)
			return evalEquation((Equation)vl, suid, columnName);
		else
			return vl;
	}

	private <T> List<?extends T> getListX(final Object suid, final String columnName,
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

		final Object vl = getRawX(suid, columnName);
		if (vl == null)
			return null;

		if (vl instanceof Equation) {
			final Object result = evalEquation((Equation)vl, suid, columnName);
			return (List)result;
		} else
			return (List)vl;
	}

	private <T> boolean isSetX(final Object suid, final String columnName,
				   final Class<? extends T> type)
	{
		final Map<Object, Object> vls = attributes.get(columnName);
		if (vls == null)
			return false;

		Object vl = vls.get(suid);
		if (vl == null)
			return false;

		if (types.get(columnName).isAssignableFrom(type))
			return true;
		else

			return false;
	}

	private Class<?> getClass(Class<?> c) {
		if (Integer.class.isAssignableFrom(c))
			return Integer.class;
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
		else if (Long.class.isAssignableFrom(c))
			return Long.class;
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
		else if (o instanceof List) {
			List l = (List) o;

			if (l.size() <= 0)
				throw new RuntimeException("empty list");
			else
				checkType(l.get(0));
		} else if (o instanceof Map) {
			Map m = (Map) o;
			Object[] keys = m.keySet().toArray();

			if (keys.length <= 0) {
				throw new RuntimeException("empty map");
			} else {
				checkType(m.get(keys[0]));
				checkType(keys[0]);
			}
		} else
			throw new RuntimeException("invalid type: "
					+ o.getClass().toString());
	}

	private class InternalRow implements CyRow {
		private final Object suid;
		private final CyTable table;

		InternalRow(Object suid, CyTable table) {
			this.suid = suid;
			this.table = table;
		}

		public void set(String attributeName, Object value) {
			if (value == null)
				unSetX(suid, attributeName);
			else
				setX(suid, attributeName, value);
		}

		public <T> void setList(String attributeName, List<?extends T> list) {
			if (list == null)
				unSetX(suid, attributeName);
			else
				setListX(suid, attributeName, list);
		}

		public <T> T get(String attributeName, Class<? extends T> c) {
			return getX(suid, attributeName, c);
		}

		public <T> List<?extends T> getList(String attributeName, Class<T> c) {
			return getListX(suid, attributeName, c);
		}

		public Object getRaw(String attributeName) {
			return getRawX(suid, attributeName);
		}

		public <T> boolean isSet(String attributeName, Class<? extends T> c) {
			return isSetX(suid, attributeName, c);
		}

		public void remove(String attributeName) {
			removeX(suid, attributeName);
		}

		public Map<String, Object> getAllValues() {
			Map<String, Object> m = new HashMap<String, Object>(attributes
					.size());

			for (String attr : attributes.keySet())
				m.put(attr, attributes.get(attr).get(suid));

			return m;
		}

		public @Override
		boolean equals(Object o) {
			if (!(o instanceof InternalRow))
				return false;

			InternalRow ir = (InternalRow) o;

			// TODO is this sufficent since we're not using long any more?
			if (ir.suid == this.suid)
				return true;
			else
				return false;
		}

		public @Override
		int hashCode() {
			// TODO is this right?
			return suid.hashCode();
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

	private Object evalEquation(final Equation equation, final Object suid,
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
				nameToDescriptorMap.put("ID", new IdentDescriptor(suid));
				continue;
			}

			final Object attribValue = getX(suid, attribRef);
			if (attribValue == null) {
				currentlyActiveAttributes.clear();
				lastInternalError = "Missing value for referenced attribute \"" + attribRef + "\"!";
				logger.warn("Missing value for \"" + attribRef
				            + "\" while evaluating an equation (ID:" + suid
				            + ", attribute name:" + columnName + ")");
				return null;
			}

			try {
				nameToDescriptorMap.put(attribRef, new IdentDescriptor(attribValue));
			} catch (final Exception e) {
				currentlyActiveAttributes.clear();
				lastInternalError = "Bad attribute reference to \"" + attribRef + "\"!";
				logger.warn("Bad attribute reference to \"" + attribRef
				            + "\" while evaluating an equation (ID:" + suid
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
			            + suid + ", attribute name:" + columnName + ")");
			return null;
		}
	}

	/**
	 *  @return an in-order list of attribute names that will have to be evaluated before "columnName" can be evaluated
	 */
	private List<String> topoSortAttribReferences(final Object suid, final String columnName) {
		final Object equationCandidate = getRawX(suid, columnName);
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
                        followReferences(suid, attribReference, alreadyProcessed, dependencies);


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
	private void followReferences(final Object suid, final String columnName, final Collection<String> alreadyProcessed,
	                              final Collection<TopoGraphNode> dependencies)
	{
		// Already visited this attribute?
		if (alreadyProcessed.contains(columnName))
			return;

		alreadyProcessed.add(columnName);
		final Object equationCandidate = getRawX(suid, columnName);
		if (!(equationCandidate instanceof Equation))
			return;

		final Equation equation = (Equation)equationCandidate;
		final Set<String> attribReferences = equation.getVariableReferences();
		for (final String attribReference : attribReferences)
			followReferences(suid, attribReference, alreadyProcessed, dependencies);
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
