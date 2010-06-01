/*
  File: ContinuousRangeCalculator.java

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
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.continuous;

import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.Interpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Calculates the Range for Continuous Mappers.
 */
public class ContinuousRangeCalculator<K extends Number, V> {
    private List<ContinuousMappingPoint<K, V>> points;
    private Interpolator<K, V> interpolator;
    private Map<String, Object> attrBundle;

    /**
     * Constructor.
     * @param points ArrayList of ContinuousMappingPoints.
     * @param interpolator Interpolator Object.
     * @param attrBundle Attribute Bundle.
     */
    public ContinuousRangeCalculator(List<ContinuousMappingPoint<K, V>> points,
        Interpolator<K, V> interpolator, Map attrBundle) {
        this.points = points;
        this.interpolator = interpolator;
        this.attrBundle = attrBundle;
    }

    /**
     * Calculates Range Value.
     * @param attrName Attribute Name.
     * @return Object.
     */
    public V calculateRangeValue(String attrName) {
        if ((attrBundle == null) || (attrName == null))
            return null;

        if (points.size() == 0)
            return null;

        Object attrValue = attrBundle.get(attrName);

        if (!(attrValue instanceof Number))
            return null;

        return getRangeValue((Number) attrValue);
    }

    private V getRangeValue(Number domainValue) {
        ContinuousMappingPoint<K, V> firstPoint = points.get(0);
        Number minDomain = firstPoint.getValue().doubleValue();

        //  if given domain value is smaller than any in our list,
        //  return the range value for the smallest domain value we have.
        int firstCmp = compareValues(domainValue, minDomain);

        if (firstCmp <= 0) {
            BoundaryRangeValues<V> bv = firstPoint.getRange();

            if (firstCmp < 0)
                return bv.lesserValue;
            else
                return bv.equalValue;
        }

        //  if given domain value is larger than any in our Vector,
        //  return the range value for the largest domain value we have.
        ContinuousMappingPoint<K, V> lastPoint = points.get(points.size() - 1);
        Number maxDomain = lastPoint.getValue().doubleValue();

        if (compareValues(domainValue, maxDomain) > 0) {
            BoundaryRangeValues<V> bv = lastPoint.getRange();

            return bv.greaterValue;
        }

        //  OK, it's somewhere in the middle, so find the boundaries and
        //  pass to our interpolator function. First check for a null
        //  interpolator function
        if (this.interpolator == null)
            return null;

        // Note that the list of Points is sorted.
        // Also, the case of the inValue equalling the smallest key was
        // checked above.
        ContinuousMappingPoint<K, V> currentPoint;
        int index = 0;

        for (index = 0; index < points.size(); index++) {
            currentPoint = points.get(index);

            Double currentValue = currentPoint.getValue().doubleValue();
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
     *  This is tricky. The desired domain value is greater than
     *  lowerDomain and less than upperDomain. Therefore, we want
     *  the "greater" field of the lower boundary value (because the
     *  desired domain value is greater) and the "lesser" field of
     *  the upper boundary value (semantic difficulties).
     */
    private V getRangeValue(int index, Number domainValue) {
        //  Get Lower Domain and Range
        ContinuousMappingPoint<K, V> lowerBound = points.get(index - 1);
        Number lowerDomain = lowerBound.getValue().byteValue();
        BoundaryRangeValues<V> lv = lowerBound.getRange();
        V lowerRange = lv.greaterValue;

        //  Get Upper Domain and Range
        ContinuousMappingPoint<K, V> upperBound = points.get(index);
        Number upperDomain = upperBound.getValue().doubleValue();
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
