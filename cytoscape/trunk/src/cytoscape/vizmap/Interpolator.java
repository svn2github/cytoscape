//Interpolator.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
/**
 * This interface defines an interpolation function that takes two pairs
 * of (domain,range) values plus a target domain value, and calculates an
 * associated range value via some kind of interpolation.
 *
 * The behavior of this function is undefined if the target domain value
 * is not equal to one of the boundaries or between them.
 */
public interface Interpolator {

    public Object getRangeValue(Object lowerDomain, Object lowerRange,
				Object upperDomain, Object upperRange,
				Object domainValue);
}
