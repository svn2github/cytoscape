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

import y.base.Edge;

import cytoscape.visual.Network;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.ColorParser;
//----------------------------------------------------------------------------
public class GenericEdgeColorCalculator extends EdgeCalculator implements EdgeColorCalculator {
    
    public GenericEdgeColorCalculator(String name, ObjectMapping m) {
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
    public GenericEdgeColorCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new ColorParser(), Color.WHITE);
    }
    
    public Color calculateEdgeColor(Edge edge, Network network) {
        String canonicalName = network.getEdgeAttributes().getCanonicalName(edge);
        Map attrBundle = network.getEdgeAttributes().getAttributes(canonicalName);
        return (Color) super.getMapping().calculateRangeValue(attrBundle);
    }
}

