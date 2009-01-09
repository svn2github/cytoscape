//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.parsers;
//----------------------------------------------------------------------------
/**
 * Interface to classes that parse a String value into a particular class
 * of object.
 */
public interface ValueParser {

    /**
     * Parse the argument into an onject. Returns null if the String is
     * not parsable.
     */
    Object parseStringValue(String value);
}

