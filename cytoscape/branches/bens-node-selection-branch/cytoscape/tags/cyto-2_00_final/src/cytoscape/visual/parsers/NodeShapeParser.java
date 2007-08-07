//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.parsers;
//----------------------------------------------------------------------------
import cytoscape.visual.ShapeNodeRealizer;
//----------------------------------------------------------------------------
/**
 * Parses a String into a yFiles shape, which is represented by a byte
 * identifier. The return value here is a Byte object wrapping the
 * primitive byte identifier.
 */
public class NodeShapeParser implements ValueParser {

    public Object parseStringValue(String value) {
        return parseNodeShape(value);
    }
    public Byte parseNodeShape(String value) {
        return ShapeNodeRealizer.parseNodeShapeTextIntoByte(value);
    }
}

