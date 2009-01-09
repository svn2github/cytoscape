//LinearNumberToNumberInterpolator.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
/**
 * The class assumes that the supplied range objects are Numbers, and returns a
 * linearly interplated value according to the value of frac.
 *
 * If either object argument is not a Number, null is returned.
 */
public class LinearNumberToNumberInterpolator extends LinearNumberInterpolator {

    public LinearNumberToNumberInterpolator() {}

    public Object getRangeValue(double frac, Object lowerRange,
				Object upperRange) {
	if ( !(lowerRange instanceof Number) ) {return null;}
	if ( !(upperRange instanceof Number) ) {return null;}

	double lowerVal = ((Number)lowerRange).doubleValue();
        double upperVal = ((Number)upperRange).doubleValue();

        double returnVal = frac*upperVal + (1.0-frac)*lowerVal;
        return new Double(returnVal);
    }
}
