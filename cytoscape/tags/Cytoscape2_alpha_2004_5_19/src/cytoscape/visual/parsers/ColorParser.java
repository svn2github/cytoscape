//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.parsers;
//----------------------------------------------------------------------------
import java.awt.Color;
import cytoscape.util.Misc;
//----------------------------------------------------------------------------
/**
 * Parses a String into a Color object.
 */
public class ColorParser implements ValueParser {

    public Object parseStringValue(String value) {
        return parseColor(value);
    }
    public Color parseColor(String value) {
        return Misc.parseRGBText(value);
    }
}

