//ContinuousMapper.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.Iterator;
//----------------------------------------------------------------------------
/**
 * This class implements a mapping from a continuous domain value to
 * a range value via interpolation. A SortedMap should be provided
 * where the keys are the domain values (must be instances of Comparable)
 * and the values are BoundaryRangeValues objects. Additionally, an
 * appropriate Interpolator object must be provided which knows how
 * to operate on the specific type of range value.
 */
public class ContinuousMapper implements ValueMapper {

    private SortedMap boundaryValueMap;
    private Interpolator fInt;

    public ContinuousMapper() {
	this.setBoundaryRangeValuesMap( new TreeMap() );
	this.setInterpolator(null);
    }

    public ContinuousMapper(SortedMap boundaryValueMap,
			    Interpolator fInt) {
	this.setBoundaryRangeValuesMap(boundaryValueMap);
	this.setInterpolator(fInt);
    }

    public Map getValueMap() {return this.getBoundaryRangeValuesMap();}
    public SortedMap getBoundaryRangeValuesMap() {return boundaryValueMap;}
    public void setBoundaryRangeValuesMap(SortedMap boundaryValueMap) {
	/* we should check that the SortedMap argument contains
	 * Comparables as keys and BoundaryRangeValues objects as values */
	this.boundaryValueMap = boundaryValueMap;
    }

    public Interpolator getInterpolator() {return fInt;}
    public void setInterpolator(Interpolator fInt) {
	/* null argument could be a problem */
	this.fInt = fInt;
    }

    //--------------------------------------------------------------------

    /**
     * Key function. Given the domain value, search through the Map of
     * boundary values to find the bracketing boundary values. If the
     * provided domain value is exactly equal to one of the boundary
     * values, then we can immediately return the matching range value.
     * If the supplied values is smaller or larger than any of the
     * boundary values, then the matching value is returned without
     * interpolation. Otherwise, the interpolator function is called
     * with the boundary (domain,range) pairs and the target domain value.
     */
    public Object getRangeValue(Object domainValue) {
	if (domainValue == null || boundaryValueMap == null) {return null;}
	int numPoints = boundaryValueMap.size();
	if (numPoints == 0) {
	    return null;
	}

	if ( !(domainValue instanceof Comparable) ) {return null;}
	Comparable inValue = (Comparable)domainValue;
        
	Comparable minDomain = (Comparable)boundaryValueMap.firstKey();
	/* if given domain value is smaller than any in our Vector,
	   return the range value for the smallest domain value we have */
        /* since this is our first compare, we'd better check that the supplied
         * domain value is of the right type by trapping ClassCastExceptions.
         * We're assuming that all the keys of the boundaryValueMap are of the
         * same type, so we only have to check this first time. */
        int firstCmp = 0;
        try {
            firstCmp = compareValues(inValue, minDomain);
        } catch (ClassCastException cce) {//oops, domainValue parameter is wrong type
            return null;
        }
	if (firstCmp <= 0) {
	    BoundaryRangeValues bv =
		(BoundaryRangeValues)boundaryValueMap.get(minDomain);
	    if (firstCmp < 0) {return bv.lesserValue;} else {return bv.equalValue;}
	}

	/* if given domain value is larger than any in our Vector,
	   return the range value for the largest domain value we have */
	Comparable maxDomain = (Comparable)boundaryValueMap.lastKey();
	if (compareValues(inValue, maxDomain) > 0) {
	    BoundaryRangeValues bv =
		(BoundaryRangeValues)boundaryValueMap.get(maxDomain);
	    return bv.greaterValue;
	}

	/* OK, it's somewhere in the middle, so find the boundaries and
	 * pass to our interpolator function. First check for a null
	 * interpolator function */
	if (this.fInt == null) {return null;}

	/* Note that the following set should be sorted since it comes from
	 * a SortedMap. Also, the case of the inValue equalling the smallest
	 * key was checked above */
	Set domainValues = boundaryValueMap.keySet();
	Iterator i = domainValues.iterator();
	Comparable lowerDomain = (Comparable)i.next();
	Comparable upperDomain = null;
	for ( ; i.hasNext(); ) {
	    upperDomain = (Comparable)i.next();
            int cmpValue = compareValues(inValue, upperDomain);
	    if (cmpValue == 0) {
		BoundaryRangeValues bv =
		    (BoundaryRangeValues)boundaryValueMap.get(upperDomain);
		return bv.equalValue;
	    } else if (cmpValue < 0) {
		break;
	    } else {
		lowerDomain = upperDomain;
	    }
	}

	/* this is tricky. The desired domain value is greater than
	 * lowerDomain and less than upperDomain. Therefore, we want
	 * the "greater" field of the lower boundary value (because the
	 * desired domain value is greater) and the "lesser" field of
	 * the upper boundary value (semantic difficulties).
	 */
	BoundaryRangeValues lv =
	    (BoundaryRangeValues)boundaryValueMap.get(lowerDomain);
	Object lowerRange = lv.greaterValue;
	BoundaryRangeValues gv =
	    (BoundaryRangeValues)boundaryValueMap.get(upperDomain);
	Object upperRange = gv.lesserValue;

	return this.fInt.getRangeValue(lowerDomain, lowerRange,
				       upperDomain, upperRange,
				       domainValue);
    }
    
    private int compareValues(Comparable probe, Comparable target) {
        /* If these are numbers, we have to extract double values because Java
         * doesn't allow comparing, for example, Integer objects to Double objects */
        if (probe instanceof Number && target instanceof Number) {
            double d1 = ((Number)probe).doubleValue();
            double d2 = ((Number)target).doubleValue();
            if (d1 < d2) {return -1;} else if (d1 > d2) {return 1;} else {return 0;}
        } else {//assume objects have the same class and compare them
            return probe.compareTo(target);
        }
    }
}


