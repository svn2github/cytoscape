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

import giny.model.Edge;
import cytoscape.visual.Arrow;

import cytoscape.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.ArrowParser;
//----------------------------------------------------------------------------
public class GenericEdgeArrowCalculator extends EdgeCalculator implements EdgeArrowCalculator {
    
    public GenericEdgeArrowCalculator(String name, ObjectMapping m) {
	super(name, m);

        Class c = null;
	//c = Arrow.class;  // this line won't obfuscate; the one below does.
	c = Arrow.NONE.getClass();
	if (!c.isAssignableFrom(m.getRangeClass()) ) {
            String s = "Invalid Calculator: Expected class " + c.toString()
		+ ", got " + m.getRangeClass().toString();
            throw new ClassCastException(s);
        }
    }
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericEdgeArrowCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new ArrowParser(), Arrow.NONE);
    }
    
    public Arrow calculateEdgeArrow(Edge edge, CyNetwork network) {
        String canonicalName = edge.getIdentifier();
        Map attrBundle = getAttrBundle(canonicalName); 
        return (Arrow) super.getMapping().calculateRangeValue(attrBundle);
    }
}

