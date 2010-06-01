/*
  File: ContinuousMapping.java

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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.mappings.continuous.ContinuousLegend;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;
import cytoscape.visual.mappings.continuous.ContinuousMappingReader;
import cytoscape.visual.mappings.continuous.ContinuousMappingWriter;
import cytoscape.visual.mappings.continuous.ContinuousRangeCalculator;
import cytoscape.visual.parsers.ValueParser;

/**
 * Implements an interpolation table mapping data to values of a particular
 * class. The data value is extracted from a bundle of attributes by using a
 * specified data attribute name.
 * 
 * For refactoring changes in this class, please refer to:
 * cytoscape.visual.mappings.continuous.README.txt.
 * 
 */
public class ContinuousMapping<K extends Number, V> extends
		AbstractMapping<V> {

	final Class<?>[] ACCEPTED_CLASS = { Number.class };

	// used to interpolate between boundaries
	private Interpolator<K, V> interpolator;

	// Contains List of Data Points
	private List<ContinuousMappingPoint<K, V>> points;

	/**
	 * Constructor.
	 * 
	 * @param defaultObj
	 *            default object to map to
	 * @param mapType
	 *            Type of mapping, one of {@link ObjectMapping#EDGE_MAPPING} or
	 *            {@link ObjectMapping#NODE_MAPPING}
	 */
	@Deprecated
	public ContinuousMapping(Object defaultObj, byte mapType)
			throws IllegalArgumentException {
		this((Class<V>) defaultObj.getClass(), null);
	}

	public ContinuousMapping(final Class<V> rangeClass,
			final String controllingAttrName) {
		super(rangeClass, controllingAttrName);
		this.acceptedClasses = ACCEPTED_CLASS;
		points = new ArrayList<ContinuousMappingPoint<K, V>>();

		// Interpolater setting
		if (Color.class.isAssignableFrom(this.rangeClass))
			interpolator = new LinearNumberToColorInterpolator();
		else if (Number.class.isAssignableFrom(this.rangeClass))
			interpolator = new LinearNumberToNumberInterpolator();
		else
			interpolator = new FlatInterpolator();
	}

	/**
	 * Create deep copy of the object.
	 * 
	 * @return Cloned Mapping Object.
	 */
	public Object clone() {
		final ContinuousMapping<K, V> clone = new ContinuousMapping<K, V>(
				rangeClass, controllingAttrName);

		// Copy over all listeners...
		for (ChangeListener listener : observers)
			clone.addChangeListener(listener);

		for (ContinuousMappingPoint<K, V> cmp : points) {
			final ContinuousMappingPoint<K, V> cmpClone = (ContinuousMappingPoint<K, V>) cmp.clone();
			clone.addPoint(cmpClone.getValue(), cmpClone.getRange());
		}

		return clone;
	}

	/**
	 * Gets all Data Points.
	 * 
	 * @return ArrayList of ContinuousMappingPoint objects.
	 */
	public List<ContinuousMappingPoint<K, V>> getAllPoints() {
		return points;
	}

	/**
	 * Adds a New Data Point.
	 */
	public void addPoint(K value, BoundaryRangeValues<V> brv) {
		ContinuousMappingPoint<K, V> cmp = new ContinuousMappingPoint<K, V>(value,
				brv);
		points.add(cmp);
	}

	/**
	 * Removes a Point from the List.
	 */
	public void removePoint(int index) {
		points.remove(index);
	}

	/**
	 * Gets Total Point Count.
	 */
	public int getPointCount() {
		return points.size();
	}

	/**
	 * Gets Specified Point.
	 * 
	 * @param index
	 *            Index Value.
	 * @return ContinuousMappingPoint.
	 */
	public ContinuousMappingPoint<K, V> getPoint(int index) {
		return points.get(index);
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
		final ContinuousMappingReader<K, V> reader = new ContinuousMappingReader<K, V>(props,baseKey, parser);
		this.points = reader.getPoints();
		this.controllingAttrName = reader.getControllingAttributeName();
		this.interpolator = reader.getInterpolator();
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
		final ContinuousMappingWriter writer = new ContinuousMappingWriter(
				points, baseKey, controllingAttrName, interpolator);

		return writer.getProperties();
	}

	/**
	 * Gets the Mapping Interpolator. Required by the ObjectMapping interface.
	 * 
	 * @return Interpolator Object.
	 */
	public Interpolator getInterpolator() {
		return interpolator;
	}

	/**
	 * Sets the Mapping Interpolator. Required by the ObjectMapping interface.
	 * 
	 * @param interpolator
	 *            Interpolator Object.
	 */
	public void setInterpolator(Interpolator interpolator) {
		this.interpolator = interpolator;
	}

	/**
	 *
	 */
	public JPanel getLegend(VisualPropertyType vpt) {
		return new ContinuousLegend(points, vpt);
	}

	/**
	 * Calculates the Range Value. Required by the ObjectMapping interface.
	 * 
	 * @param attrBundle
	 *            A Bundle of Attributes.
	 * @return Mapping object.
	 */
	@Override
	public V calculateRangeValue(final Map<String, Object> attrBundle) {
		final ContinuousRangeCalculator<K, V> calc = new ContinuousRangeCalculator<K, V>(
				points, interpolator, attrBundle);

		return calc.calculateRangeValue(controllingAttrName);
	}
}
