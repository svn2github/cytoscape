//FlatInterpolator.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
/**
 * This simple Interpolator returns the value at either the lower or upper
 * boundary of the domain. Note that no check is made whether the supplied
 * domainValue is actually within the boundaries.
 */
public class FlatInterpolator implements Interpolator {
    
    public static final Integer LOWER = new Integer(0);
    public static final Integer UPPER = new Integer(1);
    
    private boolean useLower;
    
    /**
     * The default FlatInterpolator returns the range value at the lower boundary.
     */
    public FlatInterpolator() {useLower = true;}
    
    /**
     * Constructs a FlatInterpolator which returns the range value at the lower
     * boundary unless the argument 'mode' is equal to FlatInterpolator.UPPER.
     */
    public FlatInterpolator(Integer mode) {
        if (mode.equals(this.UPPER)) {useLower = false;} else {useLower= true;}
    }
    
    public Object getRangeValue(Object lowerDomain, Object lowerRange,
				Object upperDomain, Object upperRange,
				Object domainValue) {
        return ((useLower) ? lowerRange : upperRange);
    }
}
