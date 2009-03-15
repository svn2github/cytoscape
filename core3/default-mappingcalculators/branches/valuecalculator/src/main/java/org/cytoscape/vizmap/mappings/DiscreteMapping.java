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
package org.cytoscape.vizmap.mappings;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.GraphObject;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewColumn;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.vizmap.MappingCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Implements a lookup table mapping data to values of a particular class.
 * The data value is extracted from a bundle of attributes by using a
 * specified data attribute name.
 */
public class DiscreteMapping implements MappingCalculator {
	Object defaultObj; // the default value held by this mapping
	Class<?> rangeClass; // the valid range class for this mapping
	String attrName; // the name of the controlling data attribute
	private VisualProperty<?> vp;
	private SortedMap<Object,Object> treeMap; //  contains the actual map elements (sorted)
	private Object lastKey;

	/**
	 * Constructor.
	 * @param defObj Default Object.
	 */
	public DiscreteMapping(Object defObj) {
		this(defObj, null);
	}

	/**
	 * Constructor.
	 * @param defObj Default Object.
	 * @param attrName Controlling Attribute Name.
	*/
	public DiscreteMapping(Object defObj, String attrName) {
		treeMap = new TreeMap<Object,Object>();

		this.defaultObj = defObj;
		this.rangeClass = defObj.getClass();

		if (attrName != null)
			setControllingAttributeName(attrName);
	}
	
	public void setMappingAttributeName(String name){
		attrName = name;
	}

	public String getMappingAttributeName(){
		return attrName;
	}

	public void setVisualProperty(VisualProperty<?> vp){
		this.vp = vp;
	}

	public VisualProperty<?> getVisualProperty(){
		return vp;
	}

	public <T, V> V valueFor(T attributeValue, Class<V> type){
		if (treeMap.containsKey(attributeValue)){
			return  (V) treeMap.get(attributeValue);
		} else {
			return null;
		}
	}
	
	/**
	 * Gets Value for Specified Key.
	 * @param key String Key.
	 * @return Object.
	 */
	public Object getMapValue(Object key) {
		return treeMap.get(key);
	}

	/**
	 * Puts New Key/Value in Map.
	 * @param key Key Object.
	 * @param value Value Object.
	 */
	public void putMapValue(Object key, Object value) {
		lastKey = key;
		treeMap.put(key, value);
		//fireStateChanged();
	}

	/**
	 * Gets the Last Modified Key.
	 * @return Key Object.
	 */
	public Object getLastKeyModified() {
		return lastKey;
	}

	/**
	 * Adds All Members of Specified Map.
	 * @param map Map.
	 */
	public void putAll(Map<Object, Object> map) {
		treeMap.putAll(map);
	}

	// AJK: 05/05/06 BEGIN
	/**
	 * gets all map values
	 *
	 */
	public Map<Object,Object> getAll() {
		return treeMap;
	}

	/**
	 * Gets the Name of the Controlling Attribute.
	 * @return Attribute Name.
	 */
	public String getControllingAttributeName() {
		return attrName;
	}

	public void setControllingAttributeName(String attrName) {
		this.attrName = attrName;
	}
}
