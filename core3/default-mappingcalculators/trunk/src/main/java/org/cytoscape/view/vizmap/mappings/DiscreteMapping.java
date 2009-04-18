/*
  File: DiscreteMapping.java

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

//----------------------------------------------------------------------------
// $Revision: 13022 $
// $Date: 2008-02-11 13:59:26 -0800 (Mon, 11 Feb 2008) $
// $Author: mes $
//----------------------------------------------------------------------------
package org.cytoscape.view.vizmap.mappings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.GraphObject;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewColumn;
import org.cytoscape.view.model.VisualProperty;

/**
 * Implements a lookup table mapping data to values of a particular class. The
 * data value is extracted from a bundle of attributes by using a specified data
 * attribute name.
 */
public class DiscreteMapping<K, V> extends AbstractMappingCalculator<K, V> {
	
	// contains the actual map elements (sorted)
	private SortedMap<K, V> treeMap; 
	
	private K lastKey;

	/**
	 * Constructor.
	 * 
	 * @param defObj
	 *            Default Object.
	 */
	public DiscreteMapping(String attrName, Class<K> attrType, VisualProperty<V> vp) {
		super(attrName, attrType, vp);
		treeMap = new TreeMap<K, V>();
	}
	
	@Override public String toString() {
		return "Discrete Mapping";
	}

	public <G extends GraphObject> void apply(ViewColumn<V> column,
			List<? extends View<G>> views) {
		if (views == null || views.size() < 1)
			return; // empty list, nothing to do

		//final CyRow row;// = views.get(0).getSource().attrs(); // to check types,
		// have to peek at
		// first view
		// instance
		// check types:
		
//		try {
//			attrType = (Class<K>) row.getDataTable().getColumnTypeMap().get(
//					attrName);
//		} catch (ClassCastException ce) {
//			throw new IllegalArgumentException("Mapping " + toString()
//					+ " can't map from attribute type " + attrType
//					+ " to VisualProperty " + vp + " of type " + vp.getType());
//		}
		// Class<V> vpType = vp.getType();
		// if (vpType.isAssignableFrom(rangeClass)){
		// // FIXME: should check here? or does that not matter?
		// // if (keyClass.isAssignableFrom(attrType))
		doMap(views, column);
		// } else {
		// throw new
		// IllegalArgumentException("Mapping "+toString()+" can't map from attribute type "+attrType+" to VisualProperty "+vp+" of type "+vp.getType());
		// }
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
	private <G extends GraphObject> void doMap(
			final List<? extends View<G>> views, ViewColumn<V> column) {
		// aggregate changes to be made in these:
		final Map<View<G>, V> valuesToSet = new HashMap<View<G>, V>();
		final List<View<G>> valuesToClear = new ArrayList<View<G>>();

		CyRow row;
		for (View<G> v : views) {
			row = v.getSource().attrs();
			if (row.contains(attrName, attrType)) {
				// skip Views where source attribute is not defined;
				// ViewColumn will automatically substitute the per-VS or global
				// default, as appropriate

				final K key = v.getSource().attrs().get(attrName, attrType);
				if (treeMap.containsKey(key)) {
					final V value = treeMap.get(key);
					valuesToSet.put(v, value);
				} else { // remove value so that default value will be used:
					valuesToClear.add(v);
				}
			} else { // remove value so that default value will be used:
				valuesToClear.add(v);
			}
		}
		column.setValues(valuesToSet, valuesToClear);
	}

	/**
	 * Gets Value for Specified Key.
	 * 
	 * @param key
	 *            String Key.
	 * @return Object.
	 */
	public V getMapValue(K key) {
		return treeMap.get(key);
	}

	/**
	 * Puts New Key/Value in Map.
	 * 
	 * @param key
	 *            Key Object.
	 * @param value
	 *            Value Object.
	 */
	public void putMapValue(K key, V value) {
		lastKey = key;
		treeMap.put(key, value);
		// fireStateChanged();
	}

	/**
	 * Gets the Last Modified Key.
	 * 
	 * @return Key Object.
	 */
	public K getLastKeyModified() {
		return lastKey;
	}

	/**
	 * Adds All Members of Specified Map.
	 * 
	 * @param map
	 *            Map.
	 */
	public void putAll(Map<K, V> map) {
		treeMap.putAll(map);
	}

	/**
	 * gets all map values
	 * 
	 */
	public Map<K, V> getAll() {
		return treeMap;
	}
}
