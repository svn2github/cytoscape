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

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.vizmap.NodeShape;
import org.cytoscape.vizmap.SubjectBase;
import org.cytoscape.vizmap.ValueParser;
import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.mappings.discrete.DiscreteLegend;
import org.cytoscape.vizmap.mappings.discrete.DiscreteMappingReader;
import org.cytoscape.vizmap.mappings.discrete.DiscreteMappingWriter;
import org.cytoscape.vizmap.mappings.discrete.DiscreteRangeCalculator;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Implements a lookup table mapping data to values of a particular class.
 * The data value is extracted from a bundle of attributes by using a
 * specified data attribute name.
 */
public class DiscreteMapping extends SubjectBase implements ObjectMapping {
	Object defaultObj; // the default value held by this mapping
	Class rangeClass; // the valid range class for this mapping
	String attrName; // the name of the controlling data attribute
	protected byte mapType; //  node or edge; specifies which attributes
	                        //  to use.
	private SortedMap<Object,Object> treeMap; //  contains the actual map elements (sorted)
	private Object lastKey;

	/**
	 * Constructor.
	 * @param defObj Default Object.
	 * @param mapType Map Type, ObjectMapping.EDGE_MAPPING or
	 * ObjectMapping.NODE_MAPPING.
	 */
	public DiscreteMapping(Object defObj, byte mapType) {
		this(defObj, null, mapType);
	}

	/**
	 * Constructor.
	 * @param defObj Default Object.
	 * @param attrName Controlling Attribute Name.
	 * @param mapType Map Type, ObjectMapping.EDGE_MAPPING or
	 * ObjectMapping.NODE_MAPPING.
	*/
	public DiscreteMapping(Object defObj, String attrName, byte mapType) {
		treeMap = new TreeMap<Object,Object>();

		this.defaultObj = defObj;
		this.rangeClass = defObj.getClass();

		if ((mapType != ObjectMapping.EDGE_MAPPING) && (mapType != ObjectMapping.NODE_MAPPING))
			throw new IllegalArgumentException("Unknown mapping type " + mapType);

		this.mapType = mapType;

		if (attrName != null)
			setControllingAttributeName(attrName, null, false);
	}

	/**
	 * Clones the Object.
	 * @return DiscreteMapping Object.
	 */
	public Object clone() {
		final DiscreteMapping clone = new DiscreteMapping(defaultObj, attrName, mapType);

		//  Copy over all listeners...
		for (ChangeListener listener : observers) {
			clone.addChangeListener(listener);
		}

		clone.putAll(treeMap);  // TODO we used to clone treeMap, but I don't think it's necessary

		return clone;
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
		fireStateChanged();
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

	// AJK: 05/05/06 END

	/**
	 * Gets the Range Class.
	 * Required by the ObjectMapping interface.
	 * @return Class object.
	 */
	public Class getRangeClass() {
		return rangeClass;
	}

	/**
	 * Gets Accepted Data Classes.
	 * Required by the ObjectMapping interface.
	 * @return ArrayList of Class objects.
	 */
	public Class[] getAcceptedDataClasses() {
		Class[] ret = {
		                  String.class, Number.class, Integer.class, Double.class, Float.class,
		                  Long.class, Short.class, NodeShape.class, List.class
		              };

		return ret;
	}

	/**
	 * Gets the Name of the Controlling Attribute.
	 * Required by the ObjectMapping interface.
	 * @return Attribue Name.
	 */
	public String getControllingAttributeName() {
		return attrName;
	}

	/**
	 * Call whenever the controlling attribute changes. If preserveMapping
	 * is true, all the currently stored mappings are unchanged; otherwise
	 * all the mappings are cleared. In either case, this method calls
	 * {@link #getUI} to rebuild the UI for this mapping, which in turn calls
	 * loadKeys to load the current data values for the new attribute.
	 * <p>
	 * Called by event handler from AbstractCalculator
	 * {@link cytoscape.visual.calculators.AbstractCalculator}.
	 *
	 * @param    attrName    The name of the new attribute to map to
	 */
	public void setControllingAttributeName(String attrName, CyNetwork n, boolean preserveMapping) {
		this.attrName = attrName;

		if (preserveMapping == false) {
			treeMap = new TreeMap<Object,Object>();
		}
	}

	/**
	 * Customizes this object by applying mapping defintions described by the
	 * supplied Properties argument.
	 * Required by the ObjectMapping interface.
	 * @param props Properties Object.
	 * @param baseKey Base Key for finding properties.
	 * @param parser ValueParser Object.
	 */
	public void applyProperties(Properties props, String baseKey, ValueParser parser) {
		DiscreteMappingReader reader = new DiscreteMappingReader(props, baseKey, parser);
		String contValue = reader.getControllingAttributeName();

		if (contValue != null)
			setControllingAttributeName(contValue, null, false);

		this.treeMap = reader.getMap();
	}

	/**
	 * Returns a Properties object with entries suitable for customizing this
	 * object via the applyProperties method.
	 * Required by the ObjectMapping interface.
	 * @param baseKey Base Key for creating properties.
	 * @return Properties Object.
	 */
	public Properties getProperties(String baseKey) {
		DiscreteMappingWriter writer = new DiscreteMappingWriter(attrName, baseKey, treeMap);

		return writer.getProperties();
	}

	/**
	 * Calculates the Range Value.
	 * Required by the ObjectMapping interface.
	 * @param attrBundle A Bundle of Attributes.
	 * @return Mapping object.
	 */
	public Object calculateRangeValue(CyRow attrBundle) {
		final DiscreteRangeCalculator calculator = new DiscreteRangeCalculator(treeMap, attrName);

		return calculator.calculateRangeValue(attrBundle);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param vpt DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public JPanel getLegend(VisualPropertyType vpt) {
		return new DiscreteLegend(treeMap, attrName, vpt);
	}
}
