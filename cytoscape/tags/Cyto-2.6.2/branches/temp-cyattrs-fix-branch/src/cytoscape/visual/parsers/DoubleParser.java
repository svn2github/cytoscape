//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.parsers;
//----------------------------------------------------------------------------
/**
 * Parses a String into a Double object.
 */
public class DoubleParser implements ValueParser {

    public Object parseStringValue(String value) {
        return parseDouble(value);
    }
    public Double parseDouble(String value) {
        double d = Double.NaN;
        try {
            d = Double.parseDouble(value);
            return new Double(d);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

