//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.parsers;
//----------------------------------------------------------------------------
import cytoscape.visual.Arrow;
//import cytoscape.util.Misc;
//----------------------------------------------------------------------------
/**
 * Parses a String into a yFiles Arrow object.
 */
public class ArrowParser implements ValueParser {

    public Object parseStringValue(String value) {
        return parseArrow(value);
    }
    public Arrow parseArrow(String value) {
        //return Misc.parseArrowText(value);
        return Arrow.parseArrowText(value);
    }
}

