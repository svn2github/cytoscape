//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.parsers;
//----------------------------------------------------------------------------
import y.view.LineType;
import cytoscape.util.Misc;
//----------------------------------------------------------------------------
/**
 * Parses a String into a yFiles LineType object.
 */
public class LineTypeParser implements ValueParser {

    public Object parseStringValue(String value) {
        return parseLineType(value);
    }
    public LineType parseLineType(String value) {
        return Misc.parseLineTypeText(value);
    }
}

