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
package cytoscape.visual.mappings;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.mappings.discrete.DiscreteLegend;
import cytoscape.visual.mappings.discrete.DiscreteMappingReader;
import cytoscape.visual.mappings.discrete.DiscreteMappingWriter;
import cytoscape.visual.mappings.discrete.DiscreteRangeCalculator;
import cytoscape.visual.parsers.ValueParser;

/**
 * Implements a lookup table mapping data to values of a particular class. The
 * data value is extracted from a bundle of attributes by using a specified data
 * attribute name.
 */
public class DiscreteMapping<K, V> extends AbstractMapping<K, V> {

	private static final Class<?>[] ACCEPTED_CLASSES = { String.class,
			Number.class, Integer.class, Double.class, Float.class, Long.class,
			Short.class, NodeShape.class, List.class, Boolean.class };

	private SortedMap<K, V> treeMap; // contains the actual map
												// elements (sorted)
	private K lastKey;

	/**
	 * Constructor.
	 * 
	 * @param defObj
	 *            Default Object.
	 * @param mapType
	 *            Map Type, ObjectMapping.EDGE_MAPPING or
	 *            ObjectMapping.NODE_MAPPING.
	 */
	@Deprecated
	public DiscreteMapping(Object defObj, byte mapType) {
		this(defObj, null, mapType);
	}

	/**
	 * Constructor.
	 * 
	 * @param defObj
	 *            Default Object.
	 * @param attrName
	 *            Controlling Attribute Name.
	 * @param mapType
	 *            Map Type, ObjectMapping.EDGE_MAPPING or
	 *            ObjectMapping.NODE_MAPPING.
	 */
	@Deprecated
	public DiscreteMapping(Object defObj, String attrName, byte mapType) {
		this((Class<V>)defObj.getClass(), attrName);
	}

	public DiscreteMapping(final Class<V> rangeClass,
			final String controllingAttrName) {
		super(rangeClass, controllingAttrName);
		this.treeMap = new TreeMap<K, V>();
		this.acceptedClasses = ACCEPTED_CLASSES;
	}

	/**
	 * Clones the Object.
	 * 
	 * @return DiscreteMapping Object.
	 */
	public Object clone() {
		final DiscreteMapping<K, V> clone = new DiscreteMapping<K, V>(rangeClass,
				controllingAttrName);

		// Copy over all listeners...
		for (ChangeListener listener : observers)
			clone.addChangeListener(listener);

		// Copy key-value pairs
		for (K key : this.treeMap.keySet())
			clone.treeMap.put(key, this.treeMap.get(key));

		return clone;
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
		fireStateChanged();
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

	/**
	 * Customizes this object by applying mapping defintions described by the
	 * supplied Properties argument. Required by the ObjectMapping interface.
	 * 
	 * @param props
	 *            Properties Object.
	 * @param baseKey
	 *            Base Key for finding properties.
	 * @param parser
	 *            ValueParser Object.
	 */
	public void applyProperties(Properties props, String baseKey,
			ValueParser<V> parser) {
		final DiscreteMappingReader reader = new DiscreteMappingReader(props,
				baseKey, parser);
		final String contValue = reader.getControllingAttributeName();

		if (contValue != null)
			setControllingAttributeName(contValue);

		this.treeMap = reader.getMap();
	}

	/**
	 * Returns a Properties object with entries suitable for customizing this
	 * object via the applyProperties method. Required by the ObjectMapping
	 * interface.
	 * 
	 * @param baseKey
	 *            Base Key for creating properties.
	 * @return Properties Object.
	 */
	public Properties getProperties(String baseKey) {
		final DiscreteMappingWriter writer = new DiscreteMappingWriter(
				controllingAttrName, baseKey, treeMap);

		return writer.getProperties();
	}

	/**
	 * Calculates the Range Value. Required by the ObjectMapping interface.
	 * 
	 * @param attrBundle
	 *            A Bundle of Attributes.
	 * @return Mapping object.
	 */
	public V calculateRangeValue(Map<String, Object> attrBundle) {
		final DiscreteRangeCalculator<K, V> calculator = new DiscreteRangeCalculator<K, V>(
				treeMap, controllingAttrName);

		return calculator.calculateRangeValue(attrBundle);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param vpt
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public JPanel getLegend(VisualPropertyType vpt) {
		return new DiscreteLegend(treeMap, controllingAttrName, vpt);
	}

}
