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

package org.cytoscape.model.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyRowListener;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.internal.events.ColumnCreatedEventImpl;
import org.cytoscape.model.internal.events.ColumnDeletedEventImpl;

/**
 * 
 */
public class CyDataTableImpl implements CyDataTable {

	private final Map<String, Map<Long, Object>> attributes;
	private final Map<Long, CyRow> rows;
	private final Map<Long, List<CyRowListener>> rowListeners;

	private final Map<String, Class<?>> types;
	private final Map<String, Boolean> unique;
	
	// This is not unique and might be changed by user.
	private String title;
	
	// Visibility value is immutable.
	private final boolean pub;
	
	// Unique ID.
	private final long suid;

	private CyEventHelper eventHelper;

	private long rowSUID = -1;

	/**
	 * Creates a new CyDataTableImpl object.
	 * 
	 * @param typeMap
	 *            DOCUMENT ME!
	 * @param name
	 *            DOCUMENT ME!
	 * @param pub
	 *            DOCUMENT ME!
	 */
	public CyDataTableImpl(Map<String, Class<?>> typeMap, String name,
			boolean pub, final CyEventHelper eventHelper) {
		this.title = name;
		this.pub = pub;
		this.suid = SUIDFactory.getNextSUID();
		this.eventHelper = eventHelper;
		attributes = new HashMap<String, Map<Long, Object>>();
		rows = new HashMap<Long, CyRow>();
		rowListeners = new HashMap<Long, List<CyRowListener>>();

		if (typeMap == null) {
			types = new HashMap<String, Class<?>>();
			unique = new HashMap<String, Boolean>();
		} else {
			types = new HashMap<String, Class<?>>(typeMap);
			// TODO!
			unique = new HashMap<String, Boolean>();
		}
		
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
		return this.title;
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
		return new HashMap<String, Class<?>>(types);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param attributeName
	 *            DOCUMENT ME!
	 */
	public void deleteColumn(String attributeName) {
		if (attributes.containsKey(attributeName)) {
			attributes.remove(attributeName);
			types.remove(attributeName);
			
			// This event should be Synchronous
			eventHelper.fireSynchronousEvent(new ColumnDeletedEventImpl(this,
					attributeName), ColumnDeletedListener.class);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param <T>
	 *            DOCUMENT ME!
	 * @param attributeName
	 *            DOCUMENT ME!
	 * @param type
	 *            DOCUMENT ME!
	 * @param u
	 *            DOCUMENT ME!
	 */
	public <T> void createColumn(String attributeName, Class<? extends T> type,
			boolean u) {
		if (attributeName == null)
			throw new NullPointerException("attribute name is null");

		if (type == null)
			throw new NullPointerException("type is null");

		Class<?> cls = getClass(type);
		Class<?> curr = types.get(attributeName);

		if (curr == null) {
			types.put(attributeName, cls);
			attributes.put(attributeName, new HashMap<Long, Object>());
			unique.put(attributeName, u);
			
			// Fire event
			//System.out.println("\n\n\n\n%%%%%%%%%%%%%%%%%%% Created: New Attr = " + attributeName + " %%%%%%%%%%%%%%%%%%%\n\n\n\n");
			eventHelper.fireAsynchronousEvent(new ColumnCreatedEventImpl(this,
					attributeName), ColumnCreatedListener.class);
		} else {
			if (!curr.equals(cls))
				throw new IllegalArgumentException(
						"attribute already exists for name: '" + attributeName
								+ "' with type: " + cls.getName());
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<String> getUniqueColumns() {
		List<String> l = new ArrayList<String>();

		for (String s : unique.keySet()) {
			if (unique.get(s))
				l.add(s);
		}

		return l;
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
			Class<? extends T> type) {
		if (columnName == null)
			throw new NullPointerException("column name is null");

		Map<Long, Object> vals = attributes.get(columnName);

		if (vals == null)
			throw new NullPointerException("attribute does not exist");

		List<T> l = new ArrayList<T>(vals.size());

		for (Object o : vals.values())
			l.add(type.cast(o));

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
	public CyRow getRow(final long suid) {
		CyRow row = rows.get(suid);
		if (row != null)
			return row;

		row = new InternalRow(suid, this);
		rows.put(suid, row);
		return row;
	}

	public List<CyRow> getAllRows()
	{
		return new ArrayList<CyRow>(rows.values());
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public CyRow addRow() {
		long r;
		synchronized (this) {
			r = rowSUID--;
		}

		CyRow row = new InternalRow(r, this);
		rows.put(r, row);

		return row;
	}

	// internal methods
	private void removeX(long suid, String attributeName) {
		if (attributes.containsKey(attributeName)) {
			Map<Long, Object> map = attributes.get(attributeName);

			if (map.containsKey(suid))
				map.remove(suid);
		}
	}

	private void setX(long suid, String attrName, Object value) {
		if (value == null)
			throw new NullPointerException("value is null");

		if (!types.containsKey(attrName) || !attributes.containsKey(attrName))
			throw new IllegalArgumentException("attribute: '" + attrName
					+ "' does not yet exist!");

		checkType(value);

		Map<Long, Object> vls = attributes.get(attrName);

		if (types.get(attrName).isAssignableFrom(value.getClass())) {
			// TODO this is an implicit addRow - not sure if we want to refactor
			// this or not
			vls.put(suid, value);
			final List<CyRowListener> lrl = rowListeners.get(suid);
			if (lrl != null)
				for (CyRowListener rl : lrl)
					rl.rowSet(attrName, value);
		} else
			throw new IllegalArgumentException("value is not of type: "
					+ types.get(attrName));
	}

	private Object getRawX(long suid, String attrName) {
		Map<Long, Object> vls = attributes.get(attrName);

		if (vls == null)
			return null;

		return vls.get(suid);
	}

	private <T> T getX(long suid, String attrName, Class<? extends T> type) {
		Object vl = getRawX(suid, attrName);

		if (vl == null)
			return null;

		return type.cast(vl);
	}

	private <T> boolean containsX(long suid, String attrName,
			Class<? extends T> type) {
		Map<Long, Object> vls = attributes.get(attrName);

		if (vls == null)
			return false;

		Object vl = vls.get(suid);

		if (vl == null)
			return false;

		if (types.get(attrName).isAssignableFrom(type))
			return true;
		else

			return false;
	}

	private Class<?> containsX(long suid, String attrName) {
		Map<Long, Object> vls = attributes.get(attrName);

		if (vls == null)
			return null;

		Object vl = vls.get(suid);

		if (vl == null)
			return null;
		else

			return types.get(attrName);
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
		private final long suid;
		private final CyDataTable table;

		InternalRow(long suid, CyDataTable table) {
			this.suid = suid;
			this.table = table;
		}

		public void set(String attributeName, Object value) {
			setX(suid, attributeName, value);
		}

		public <T> T get(String attributeName, Class<? extends T> c) {
			return getX(suid, attributeName, c);
		}

		public Object getRaw(String attributeName) {
			return getRawX(suid, attributeName);
		}

		public <T> boolean contains(String attributeName, Class<? extends T> c) {
			return containsX(suid, attributeName, c);
		}

		public Class<?> contains(String attributeName) {
			return containsX(suid, attributeName);
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

			if (ir.suid == this.suid)
				return true;
			else

				return false;
		}

		public @Override
		int hashCode() {
			return (int) (suid ^ (suid >>> 32));
		}

		public CyDataTable getDataTable() {
			return table;
		}

		public void addRowListener(CyRowListener rl) {
			List<CyRowListener> list = rowListeners.get(suid);
			if (list == null) {
				list = new ArrayList<CyRowListener>();
				rowListeners.put(suid, list);
			}
			list.add(rl);
		}

		public void removeRowListener(CyRowListener rl) {
			List<CyRowListener> list = rowListeners.get(suid);
			if (list != null)
				list.remove(rl);
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
}
