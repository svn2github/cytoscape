//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.parsers;
//----------------------------------------------------------------------------
import y.view.Arrow;
import cytoscape.util.Misc;
//----------------------------------------------------------------------------
/**
 * Parses a String into a yFiles Arrow object.
 */
public class ArrowParser implements ValueParser {

    public Object parseStringValue(String value) {
        return parseArrow(value);
    }
    public Arrow parseArrow(String value) {
        return Misc.parseArrowText(value);
    }
}

