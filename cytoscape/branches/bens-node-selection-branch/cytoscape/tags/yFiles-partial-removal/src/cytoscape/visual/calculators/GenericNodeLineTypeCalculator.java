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

import giny.model.Node;
import cytoscape.visual.LineType;

import cytoscape.data.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.LineTypeParser;
//----------------------------------------------------------------------------
public class GenericNodeLineTypeCalculator extends NodeCalculator implements NodeLineTypeCalculator {
    
    public GenericNodeLineTypeCalculator(String name, ObjectMapping m) {
	super(name, m);

        Class c = null;
	//c = LineType.class;  // this line won't obfuscate; the one below does.
	c = LineType.LINE_1.getClass();
        if (!c.isAssignableFrom(m.getRangeClass()) ) {
            String s = "Invalid Calculator: Expected class " + c.toString()
                    + ", got " + m.getRangeClass().toString();
            throw new ClassCastException(s);
        }
    }
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericNodeLineTypeCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new LineTypeParser(), LineType.LINE_1);
    }
    
    public LineType calculateNodeLineType(Node node, CyNetwork network) {
        String canonicalName = network.getNodeAttributes().getCanonicalName(node);
        Map attrBundle = network.getNodeAttributes().getAttributes(canonicalName);
        return (LineType)super.getMapping().calculateRangeValue(attrBundle);
    }
}

