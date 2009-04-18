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
package org.cytoscape.view.vizmap.mappings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.GraphObject;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewColumn;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.mappings.interpolators.FlatInterpolator;
import org.cytoscape.view.vizmap.mappings.interpolators.Interpolator;
import org.cytoscape.view.vizmap.mappings.interpolators.LinearNumberToColorInterpolator;
import org.cytoscape.view.vizmap.mappings.interpolators.LinearNumberToNumberInterpolator;

/**
 * Implements an interpolation table mapping data to values of a particular
 * class. The data value is extracted from a bundle of attributes by using a
 * specified data attribute name.
 * 
 * @param <V>
 *            Type of object Visual Property holds
 * 
 *            For refactoring changes in this class, please refer to:
 *            cytoscape.visual.mappings.continuous.README.txt.
 * 
 */
public class ContinuousMapping<V> extends AbstractMappingCalculator<Number, V> {

	private Interpolator<Number, V> interpolator; // used to interpolate between
													// boundaries

	// Contains List of Data Points
	private List<ContinuousMappingPoint<V>> points;

	/**
	 * Constructor.
	 * 
	 * @param defaultObj
	 *            default object to map to
	 */
	public ContinuousMapping(String attrName, VisualProperty<V> vp) {
		super(attrName, Number.class, vp);
		this.points = new ArrayList<ContinuousMappingPoint<V>>();

		// Create Interpolator
		if (Color.class.isAssignableFrom(vp.getType()))
			interpolator = (Interpolator<Number, V>) new LinearNumberToColorInterpolator();
		else if (Number.class.isAssignableFrom(vp.getType()))
			interpolator = (Interpolator<Number, V>) new LinearNumberToNumberInterpolator();
		else
			interpolator = (Interpolator<Number, V>) new FlatInterpolator();
	}
	
	@Override public String toString() {
		return "Continuous Mapping";
	}

	/**
	 * Gets all Data Points.
	 * 
	 * @return List of ContinuousMappingPoint objects.
	 */
	public List<ContinuousMappingPoint<V>> getAllPoints() {
		return points;
	}

	/**
	 * Adds a New Data Point.
	 */
	public void addPoint(double value, BoundaryRangeValues<V> brv) {
		points.add(new ContinuousMappingPoint<V>(value, brv));
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
	public ContinuousMappingPoint<V> getPoint(int index) {
		return points.get(index);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param <V>
	 *            DOCUMENT ME!
	 * @param <V>
	 *            DOCUMENT ME!
	 * @param column
	 *            DOCUMENT ME!
	 * @param views
	 *            DOCUMENT ME!
	 */
	public <G extends GraphObject> void apply(ViewColumn<V> column,
			List<? extends View<G>> views) {
		if (views == null || views.size() < 1)
			return; // empty list, nothing to do
				
			doMap(views, column); // due to check in
															// previous line,
															// this is a safe
															// cast
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
	 *            the type-parameter of the domain of the mapping (the object
	 *            read as an attribute value has to be is-a K)
	 * @param <V>
	 *            the type-parameter of the View
	 */
	private <G extends GraphObject> void doMap(
			final List<? extends View<G>> views, ViewColumn<V> column) {
		// aggregate changes to be made in these:
		Map<View<G>, V> valuesToSet = new HashMap<View<G>, V>();
		List<View<G>> valuesToClear = new ArrayList<View<G>>();

		CyRow row;
		for (View<G> v : views) {
			row = v.getSource().attrs();

			if (row.contains(attrName, attrType)) {
				// skip Views where source attribute is not defined;
				// ViewColumn will automatically substitute the per-VS or global
				// default, as appropriate
				
				// In all cases, attribute value should be a number for continuous mapping.
				final Number attrValue = v.getSource().attrs().get(attrName,attrType);
				final V value = getRangeValue(attrValue); // FIXME: make
															// getRangeValue
															// type-parametric,
															// so this shouldn't
															// be needed (??)
				valuesToSet.put(v, value);
			} else { // remove value so that default value will be used:
				valuesToClear.add(v);
			}
		}

		column.setValues(valuesToSet, valuesToClear);
	}

	private V getRangeValue(Number domainValue) {
		ContinuousMappingPoint<V> firstPoint = points.get(0);
		Number minDomain = new Double(firstPoint.getValue());

		// if given domain value is smaller than any in our list,
		// return the range value for the smallest domain value we have.
		int firstCmp = compareValues(domainValue, minDomain);

		if (firstCmp <= 0) {
			BoundaryRangeValues<V> bv = firstPoint.getRange();

			if (firstCmp < 0)
				return bv.lesserValue;
			else

				return bv.equalValue;
		}

		// if given domain value is larger than any in our Vector,
		// return the range value for the largest domain value we have.
		ContinuousMappingPoint<V> lastPoint = points.get(points.size() - 1);
		Number maxDomain = new Double(lastPoint.getValue());

		if (compareValues(domainValue, maxDomain) > 0) {
			BoundaryRangeValues<V> bv = lastPoint.getRange();

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
		ContinuousMappingPoint<V> currentPoint;
		int index = 0;

		for (index = 0; index < points.size(); index++) {
			currentPoint = points.get(index);

			Double currentValue = new Double(currentPoint.getValue());
			int cmpValue = compareValues(domainValue, currentValue);

			if (cmpValue == 0) {
				BoundaryRangeValues<V> bv = currentPoint.getRange();

				return bv.equalValue;
			} else if (cmpValue < 0)
				break;
		}

		return getRangeValue(index, domainValue);
	}

	/**
	 * This is tricky. The desired domain value is greater than lowerDomain and
	 * less than upperDomain. Therefore, we want the "greater" field of the
	 * lower boundary value (because the desired domain value is greater) and
	 * the "lesser" field of the upper boundary value (semantic difficulties).
	 */
	private V getRangeValue(int index, Number domainValue) {
		// Get Lower Domain and Range
		ContinuousMappingPoint<V> lowerBound = points.get(index - 1);
		Number lowerDomain = new Double(lowerBound.getValue());
		BoundaryRangeValues<V> lv = lowerBound.getRange();
		V lowerRange = lv.greaterValue;

		// Get Upper Domain and Range
		ContinuousMappingPoint<V> upperBound = points.get(index);
		Number upperDomain = new Double(upperBound.getValue());
		BoundaryRangeValues<V> gv = upperBound.getRange();
		V upperRange = gv.lesserValue;

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
}
