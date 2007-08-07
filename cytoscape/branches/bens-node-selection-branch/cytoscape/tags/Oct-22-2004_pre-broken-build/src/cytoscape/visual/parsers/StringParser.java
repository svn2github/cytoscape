//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.parsers;
//----------------------------------------------------------------------------
/**
 * Implements the ValueParser interface for String return values, by
 * simply returning the supplied argument.
 */
public class StringParser implements ValueParser {

    public Object parseStringValue(String value) {
        return parseString(value);
    }
    public String parseString(String value) {return value;}
}

