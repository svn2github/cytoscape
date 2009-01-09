//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import javax.swing.JPanel;

import y.base.Node;
import y.view.ShapeNodeRealizer;

import cytoscape.data.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.NodeShapeParser;
//----------------------------------------------------------------------------
public class GenericNodeShapeCalculator extends NodeCalculator implements NodeShapeCalculator {
    
    public GenericNodeShapeCalculator(String name, ObjectMapping m) {
	super(name, m);

        byte b = 0;
        Byte bObject = new Byte(b);
        Class c = bObject.getClass();
        if (!c.isAssignableFrom(m.getRangeClass()) ) {
            String s = "Invalid Calculator: Expected class " + c.toString()
                    + ", got " + m.getRangeClass().toString();
            throw new ClassCastException(s);
        }
    }
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericNodeShapeCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new NodeShapeParser(),
              new Byte(ShapeNodeRealizer.RECT));
    }
    
    /**  It is hoped that the -1 value of a byte will not conflict
     *   with any of the values used by ShapeNodeRealizer.
     */
    public byte calculateNodeShape(Node node, CyNetwork network) {
        String canonicalName = network.getNodeAttributes().getCanonicalName(node);
        Map attrBundle = network.getNodeAttributes().getAttributes(canonicalName);
	Object rangeValue = super.getMapping().calculateRangeValue(attrBundle);
	if(rangeValue!=null)
	    return ((Byte)super.getMapping().calculateRangeValue(attrBundle)).byteValue();
	else
	    return (byte)(-1);
    }
}

