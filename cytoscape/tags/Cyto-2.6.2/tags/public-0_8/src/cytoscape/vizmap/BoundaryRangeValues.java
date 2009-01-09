// BoundaryRangeValues.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
/**
 * This class defines a data object representing the range values associated
 * with a particular domain value, called a boundary value. The domain value
 * is not stored here, since objects of this class are intended to be used as
 * the values in a map where the domain value is the key.
 *
 * Three values must be specified for each boundary value. The lesserValue
 * field is used for interpolation upon smaller domain values; the
 * greaterValue field is used for interpolation upon larger domain values;
 * and the equalValue field is used when the domain value is exactly equal
 * to the associated boundary domain value. This distinction is needed to
 * support different ranges of interpolation above and below the same
 * domain value, plus allow a distinctly different value for exact matches.
 */
public class BoundaryRangeValues {

    public Object lesserValue = null;
    public Object equalValue = null;
    public Object greaterValue = null;

    public BoundaryRangeValues() {}

    public BoundaryRangeValues(Object o1, Object o2, Object o3) {
	lesserValue = o1;
	equalValue = o2;
	greaterValue = o3;
    }
}
