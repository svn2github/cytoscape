//NumberInterpolator.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
/**
 * This partial implementation of Interpolator assumes that the domain
 * values are some kind of number, and extracts the values into ordinary
 * doubles for the convenience of subclasses. If any argument is null, or
 * if any of the domain values is not an instance of Number, null is returned.
 */
abstract public class NumberInterpolator implements Interpolator {

    public Object getRangeValue(Object lowerDomain, Object lowerRange,
				Object upperDomain, Object upperRange,
				Object domainValue) {
	if ( lowerRange == null || upperRange == null ) {return null;}
	if ( lowerDomain == null
	     || !(lowerDomain instanceof Number) ) {return null;}
	if ( upperDomain == null
	     || !(upperDomain instanceof Number) ) {return null;}
	if ( domainValue == null
	     || !(domainValue instanceof Number) ) {return null;}

	return getRangeValue( ((Number)lowerDomain).doubleValue(), lowerRange,
			      ((Number)upperDomain).doubleValue(), upperRange,
			      ((Number)domainValue).doubleValue() );
    }

    abstract public Object getRangeValue(double lowerDomain, Object lowerRange,
					 double upperDomain, Object upperRange,
					 double domainValue);
}
