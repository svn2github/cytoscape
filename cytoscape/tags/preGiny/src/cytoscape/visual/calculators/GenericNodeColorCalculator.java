//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import java.awt.Color;
import javax.swing.JPanel;

import y.base.Node;

import cytoscape.data.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.ColorParser;
//----------------------------------------------------------------------------
public class GenericNodeColorCalculator extends NodeCalculator implements NodeColorCalculator {
    
    public GenericNodeColorCalculator(String name, ObjectMapping m) {
	super(name, m);

        Color color = new Color(0,0,0);
        Class c = color.getClass();
        if (!c.isAssignableFrom(m.getRangeClass()) ) {
            String s = "Invalid Calculator: Expected class " + c.toString()
                    + ", got " + m.getRangeClass().toString();
            throw new ClassCastException(s);
        }
    }
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericNodeColorCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new ColorParser(), Color.WHITE);
    }
    
    public Color calculateNodeColor(Node node, CyNetwork network) {
        String canonicalName = network.getNodeAttributes().getCanonicalName(node);
        Map attrBundle = network.getNodeAttributes().getAttributes(canonicalName);
        return (Color)super.getMapping().calculateRangeValue(attrBundle);
    }
}

