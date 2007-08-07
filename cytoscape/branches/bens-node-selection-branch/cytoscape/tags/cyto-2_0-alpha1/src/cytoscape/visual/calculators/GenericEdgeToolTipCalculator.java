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

import cytoscape.data.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.StringParser;
//----------------------------------------------------------------------------
public class GenericEdgeToolTipCalculator extends EdgeCalculator implements EdgeToolTipCalculator {
    
    public GenericEdgeToolTipCalculator(String name, ObjectMapping m) {
	super(name, m);

        String sc = new String();
        Class c = sc.getClass();
        if (!c.isAssignableFrom(m.getRangeClass()) ) {
            String s = "Invalid Calculator: Expected class " + c.toString()
		+ ", got " + m.getRangeClass().toString();
            throw new ClassCastException(s);
        }
    }
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericEdgeToolTipCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new StringParser(), new String());
    }
    
    public String calculateEdgeToolTip(Edge edge, CyNetwork network) {
        String canonicalName = network.getEdgeAttributes().getCanonicalName(edge);
        Map attrBundle = network.getEdgeAttributes().getAttributes(canonicalName);
        return (String) super.getMapping().calculateRangeValue(attrBundle);
    }
}

