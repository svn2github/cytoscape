//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.parsers;
//----------------------------------------------------------------------------
import cytoscape.visual.LineType;
//import cytoscape.util.Misc;
//----------------------------------------------------------------------------
/**
 * Parses a String into a yFiles LineType object.
 */
public class LineTypeParser implements ValueParser {

    public Object parseStringValue(String value) {
        return parseLineType(value);
    }
    public LineType parseLineType(String value) {
        //return Misc.parseLineTypeText(value);
        return LineType.parseLineTypeText(value);
    }
}

