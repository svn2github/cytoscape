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

//----------------------------------------------------------------------------
// $Revision: 13022 $
// $Date: 2008-02-11 13:59:26 -0800 (Mon, 11 Feb 2008) $
// $Author: mes $
//----------------------------------------------------------------------------
package org.cytoscape.vizmap.mappings;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.GraphObject;
import org.cytoscape.vizmap.MappingCalculator;
import org.cytoscape.viewmodel.View;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.vizmap.mappings.ContinuousMappingPoint;
import org.cytoscape.vizmap.mappings.interpolators.Interpolator;
import org.cytoscape.vizmap.mappings.interpolators.FlatInterpolator;
import org.cytoscape.vizmap.mappings.interpolators.LinearNumberToColorInterpolator;
import org.cytoscape.vizmap.mappings.interpolators.LinearNumberToNumberInterpolator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements an interpolation table mapping data to values of a particular
 * class.  The data value is extracted from a bundle of attributes by using a
 * specified data attribute name.
 *
 * For refactoring changes in this class, please refer to:
 * cytoscape.visual.mappings.continuous.README.txt.
 *
 */
public class ContinuousMapping implements MappingCalculator {
	Object defaultObj; //  the default value held by this mapping
	Class rangeClass; //  the class of values held by this mapping
	String attrName; //  the name of the controlling data attribute
	private VisualProperty<?> vp;
	Interpolator interpolator; //  used to interpolate between boundaries
	//  Contains List of Data Points
	private List<ContinuousMappingPoint> points = new ArrayList<ContinuousMappingPoint>();

	/**
	 *  Constructor.
	 *    @param    defaultObj default object to map to
	 *    @param    mapType    Type of mapping, one of
	 *  {@link ObjectMapping#EDGE_MAPPING} or {@link ObjectMapping#NODE_MAPPING}
	 */
	public ContinuousMapping(Object defaultObj) throws IllegalArgumentException {

		this.rangeClass = defaultObj.getClass();
		this.defaultObj = defaultObj;

		//  Create Interpolator
		if (Color.class.isAssignableFrom(this.rangeClass))
			interpolator = new LinearNumberToColorInterpolator();
		else if (Number.class.isAssignableFrom(this.rangeClass))
			interpolator = new LinearNumberToNumberInterpolator();
		else
			interpolator = new FlatInterpolator();
	}

	/**
	 * Gets all Data Points.
	 * @return List of ContinuousMappingPoint objects.
	 */
	public List<ContinuousMappingPoint> getAllPoints() {
		return points;
	}

	/**
	 *  Adds a New Data Point.
	 */
	public void addPoint(double value, BoundaryRangeValues brv) {
		ContinuousMappingPoint cmp = new ContinuousMappingPoint(value, brv);
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
	 * @param index Index Value.
	 * @return ContinuousMappingPoint.
	 */
	public ContinuousMappingPoint getPoint(int index) {
		return points.get(index);
	}

	/**
	 * Gets the Name of the Controlling Attribute.
	 * Required by the ObjectMapping interface.
	 * @return Attribute Name.
	 */
	public String getControllingAttributeName() {
		return attrName;
	}

	/**
	 * Sets the Name of the Controlling Attribte.
	 * @param attrName Attribute Name.
	 * @param network CytoscapeNetwork Object.
	 * @param preserveMapping Flag to preserve mapping.
	 */
	public void setControllingAttributeName(String attrName) {
		this.attrName = attrName;
	}

	public <V extends GraphObject> void apply(View<V> v, Object defaultValue) {
		CyRow row = v.getSource().attrs();
		// check types:
		Class<?> attrType = row.getDataTable().getColumnTypeMap().get(attrName);
		Class<?> vpType = vp.getType();
		if (vpType.isAssignableFrom(rangeClass) &&
				Number.class.isAssignableFrom(attrType)){
			// should check here?
			// if (keyClass.isAssignableFrom(attrType)) 
			doMap(v, row, defaultValue, (Class<Number>)attrType, vpType);
		} else {
			throw new IllegalArgumentException("Mapping "+toString()+" can't map from attribute type "+attrType+" to VisualProperty "+vp+" of type "+vp.getType());
		}
	}

	/** 
	 * Read attribute from row, map it and apply it.
	 * 
	 * types are guaranteed to be correct (? FIXME: check this)
	 * 
	 * Putting this in a separate method makes it possible to make it type-parametric.
	 * 
	 * @param <T> the type-parameter of the VisualProperty vp
	 * @param <K> the type-parameter of the domain of the mapping (the object read as an attribute value has to be is-a K)
	 * @param <V> the type-parameter of the View
	 */
	private <T,K extends Number, V extends GraphObject> void doMap(final View<V> v, CyRow row, Object defaultValue, Class<K> attrType, Class<T>vpType){
		if (row.contains(attrName, attrType) ){
			final K attrValue = (K) v.getSource().attrs().get(attrName, attrType);
			final T value = (T) getRangeValue(attrValue); // FIXME: make getRangeValue type-parametric, so this shouldn't be needed 
			v.setVisualProperty((VisualProperty<T>)vp, value);
		} else { // apply per-VS or global default where attribute is not found
			v.setVisualProperty((VisualProperty<T>)vp, (T)defaultValue);
		}
	}
	private Object getRangeValue(Number domainValue) {
		ContinuousMappingPoint firstPoint = points.get(0);
		Number minDomain = new Double(firstPoint.getValue());

		// if given domain value is smaller than any in our list,
		// return the range value for the smallest domain value we have.
		int firstCmp = compareValues(domainValue, minDomain);

		if (firstCmp <= 0) {
			BoundaryRangeValues bv = firstPoint.getRange();

			if (firstCmp < 0)
				return bv.lesserValue;
			else
				return bv.equalValue;
		}

		// if given domain value is larger than any in our Vector,
		// return the range value for the largest domain value we have.
		ContinuousMappingPoint lastPoint = (ContinuousMappingPoint) points
				.get(points.size() - 1);
		Number maxDomain = new Double(lastPoint.getValue());

		if (compareValues(domainValue, maxDomain) > 0) {
			BoundaryRangeValues bv = lastPoint.getRange();

			return bv.greaterValue;
		}
		// OK, it's somewhere in the middle, so find the boundaries and
		// pass to our interpolator function. First check for a null
		// interpolator function
		if (this.interpolator == null)
			return null;

		// Note that the list of Points is sorted.
		// Also, the case of the inValue equalling the smallest key was
		// checked above.
		ContinuousMappingPoint currentPoint;
		int index = 0;

		for (index = 0; index < points.size(); index++) {
			currentPoint = (ContinuousMappingPoint) points.get(index);

			Double currentValue = new Double(currentPoint.getValue());
			int cmpValue = compareValues(domainValue, currentValue);

			if (cmpValue == 0) {
				BoundaryRangeValues bv = currentPoint.getRange();

				return bv.equalValue;
			} else if (cmpValue < 0)
				break;
		}

		Object object = getRangeValue(index, domainValue);

		return object;
	}

	/**
	 * This is tricky. The desired domain value is greater than lowerDomain and
	 * less than upperDomain. Therefore, we want the "greater" field of the
	 * lower boundary value (because the desired domain value is greater) and
	 * the "lesser" field of the upper boundary value (semantic difficulties).
	 */
	private Object getRangeValue(int index, Number domainValue) {
		// Get Lower Domain and Range
		ContinuousMappingPoint lowerBound = (ContinuousMappingPoint) points
				.get(index - 1);
		Number lowerDomain = new Double(lowerBound.getValue());
		BoundaryRangeValues lv = lowerBound.getRange();
		Object lowerRange = lv.greaterValue;

		// Get Upper Domain and Range
		ContinuousMappingPoint upperBound = (ContinuousMappingPoint) points
				.get(index);
		Number upperDomain = new Double(upperBound.getValue());
		BoundaryRangeValues gv = upperBound.getRange();
		Object upperRange = gv.lesserValue;

		return interpolator.getRangeValue(lowerDomain, lowerRange, upperDomain,
				upperRange, domainValue);
	}

	/**
	 * Helper function to compare Number objects. This is needed because Java
	 * doesn't allow comparing, for example, Integer objects to Double objects.
	 */
	private int compareValues(Number probe, Number target) {
		double d1 = probe.doubleValue();
		double d2 = target.doubleValue();

		if (d1 < d2)
			return -1;
		else if (d1 > d2)
			return 1;
		else
			return 0;
	}
	
	public String getMappingAttributeName() {
		return attrName;
	}

	public VisualProperty<?> getVisualProperty() {
		return vp;
	}

	public void setMappingAttributeName(String name) {
		attrName = name;
	}

	public void setVisualProperty(VisualProperty<?> vp) {
		this.vp = vp;
	}
}
