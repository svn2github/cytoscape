//LinearNumberInterpolator.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
/**
 * This subclass of NumberInterpolator further assumes a linear interpolation,
 * and calculates the fractional distance of the target domain value from
 * the lower boundary value for the convenience of subclasses.
 */
abstract public class LinearNumberInterpolator extends NumberInterpolator {

    public Object getRangeValue(double lowerDomain, Object lowerRange,
				double upperDomain, Object upperRange,
				double domainValue) {
	if (lowerDomain == upperDomain) {return lowerRange;}
	double frac = (domainValue - lowerDomain) / (upperDomain-lowerDomain);
	return getRangeValue(frac, lowerRange, upperRange);
    }

    abstract public Object getRangeValue(double frac, Object lowerRange,
					 Object upperRange);
}
