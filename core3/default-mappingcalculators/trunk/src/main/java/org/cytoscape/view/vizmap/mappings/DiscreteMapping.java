/*
  File: DiscreteMapping.java

  Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.view.vizmap.mappings;


import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a lookup table mapping data to values of a particular class. The
 * data value is extracted from a bundle of attributes by using a specified data
 * attribute name.
 */
public class DiscreteMapping<K, V> extends AbstractVisualMappingFunction<K, V> {
	
	private static final Logger logger = LoggerFactory.getLogger(DiscreteMapping.class);
	
	// Name of mapping.  This will be used by toString() method.
	protected static final String DISCRETE = "Discrete Mapping";
	
	// contains the actual map elements (sorted)
	private final SortedMap<K, V> attribute2visualMap;

	/**
	 * Constructor.
	 * 
	 * @param defObj
	 *            Default Object.
	 */
	public DiscreteMapping(final String attrName, final Class<K> attrType,
			final VisualProperty<V> vp) {
		super(attrName, attrType, vp);
		attribute2visualMap = new TreeMap<K, V>();
	}

	@Override
	public String toString() {
		return DISCRETE;
	}

	@Override
	public void apply(View<? extends CyTableEntry> view) {
		if (view == null)
			return; // empty view, nothing to do

		applyDiscreteMapping(view);
	}

	/**
	 * Read attribute from row, map it and apply it.
	 * 
	 * types are guaranteed to be correct (? FIXME: check this)
	 * 
	 * Putting this in a separate method makes it possible to make it
	 * type-parametric.
	 * 
	 * @param <V>
	 *            the type-parameter of the ViewColumn column
	 * @param <K>
	 *            the type-parameter of the key stored in the mapping (the
	 *            object read as an attribute value has to be is-a K)
	 * @param <V>
	 *            the type-parameter of the View
	 */
	private void applyDiscreteMapping(final View<? extends CyTableEntry> view) {

		final CyRow row = view.getModel().getCyRow();
//		
//		logger.debug("Target View = " + view.getModel().getSUID());
//		logger.debug("AttrName = " + attrName);
//		logger.debug("AttrType = " + attrType);
//		logger.debug("Row keys = " + row.getAllValues().keySet());
//		logger.debug("Row vals = " + row.getAllValues().values());
		
		
		if (row.isSet(attrName)) {
			// skip Views where source attribute is not defined;
			// ViewColumn will automatically substitute the per-VS or global
			// default, as appropriate
			final CyColumn column = row.getTable().getColumn(attrName);
			final Class<?> attrClass = column.getType();
			Object key = null;
			
			if (attrClass.isAssignableFrom(List.class)) {
				List<?> list = row.getList(attrName, column.getListElementType());
				key = list != null ? list.toString() : "";
			} else {
				key = row.get(attrName, attrType);
			}
						
			if (key != null && attribute2visualMap.containsKey(key)) {
				final V value = attribute2visualMap.get(key);
				// Assign value to view
				view.setVisualProperty(vp, value);
			} else { // remove value so that default value will be used:
				// Set default value
				view.setVisualProperty(vp, null);				
			}
		} else { // remove value so that default value will be used:
			view.setVisualProperty(vp, null);
		}
	}

	/**
	 * Gets Value for Specified Key.
	 * 
	 * @param key
	 *            String Key.
	 * @return Object.
	 */
	public V getMapValue(K key) {
		return attribute2visualMap.get(key);
	}

	/**
	 * Puts New Key/Value in Map.
	 * 
	 * @param key
	 *            Key Object.
	 * @param value
	 *            Value Object.
	 */
	public <T extends V> void putMapValue(final K key, final T value) {
		attribute2visualMap.put(key, value);
		// TODO: fire event here.
	}

	/**
	 * Adds All Members of Specified Map.
	 * 
	 * @param map
	 *            Map.
	 */
	public <T extends V> void putAll(Map<K, T> map) {
		attribute2visualMap.putAll(map);
	}

	/**
	 * gets all map values
	 * 
	 */
	public Map<K, V> getAll() {
		return attribute2visualMap;
	}
}
